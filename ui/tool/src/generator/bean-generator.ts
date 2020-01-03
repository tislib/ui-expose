import { OptionalKind, Project, PropertySignatureStructure, StatementStructures, StructureKind } from 'ts-morph';
import { BeanModel } from '../data/bean-model';
import { PropertyModel } from '../data/property-model';

export class BeanGenerator {
    storeBeanClass(generatedPath: string, project: Project, beanModel: BeanModel) {
        const fileLoc = generatedPath + '/model/' + this.prepareBeanClassFileName(beanModel.name);
        let statements: StatementStructures[] = [];
        statements = [...statements, ...this.prepareImports(beanModel)];
        statements = [...statements, {
            kind: StructureKind.Interface,
            name: beanModel.name,
            isExported: true,
            properties: beanModel.properties.map(item => this.prepareBeanProperty(item))
        }];
        project.createSourceFile(fileLoc, {
            statements: statements
        }, {
            overwrite: true
        }).formatText({
            ensureNewLineAtEndOfFile: true
        });
    }
    
    public prepareBeanClassFileName(name: string, includeFileExtension: boolean = true): string {
        const nameFormatter = (str: string) => str.replace(/\B[A-Z]/g, letter => `-${letter.toLowerCase()}`)
            + '.model'
            + (includeFileExtension ? '.ts' : '');
        return nameFormatter(name).toLowerCase();
    }
    
    private prepareBeanProperty(item: PropertyModel): OptionalKind<PropertySignatureStructure> {
        return {
            kind: StructureKind.PropertySignature,
            name: item.name,
            type: this.resolveType(item.type)
        };
    }
    
    public resolveType(type: string): string {
        if (type.startsWith('#')) {
            return type.substr(1);
        } else {
            return type;
        }
    }
    
    private prepareImports(beanModel: BeanModel): StatementStructures[] {
        return beanModel.properties.filter(item => item.type.startsWith('#')).map(item => {
            const type = item.type.substr(1);
            return {
                kind: StructureKind.ImportDeclaration,
                namedImports: [{
                    name: type,
                }],
                moduleSpecifier: './' + this.prepareBeanClassFileName(type, false)
            };
        });
    }
}