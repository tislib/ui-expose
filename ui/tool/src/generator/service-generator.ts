import { Project, StatementStructures, StructureKind } from 'ts-morph';
import { ServiceModel } from '../data/service-model';
import { BeanGenerator } from './bean-generator';
import { MethodGenerator } from './method-generator';

export class ServiceGenerator {
    private methodGenerator: MethodGenerator = new MethodGenerator();
    private beanGenerator: BeanGenerator = new BeanGenerator();
    
    storeServiceClass(generatedPath: string, project: Project, serviceInfo: ServiceModel) {
        const fileLoc = generatedPath + '/service/' + this.prepareServiceClassFileName(serviceInfo.name) + '.ts';
        project.createSourceFile(fileLoc, {
            statements: [
                ...this.prepareImports(serviceInfo),
                '@Injectable()',
                {
                    kind: StructureKind.Class,
                    name: serviceInfo.name,
                    isExported: true,
                    properties: [
                        {
                            name: 'SERVICE_NAME',
                            isStatic: true,
                            isReadonly: true,
                            initializer: '\'' + serviceInfo.name + '\''
                        },
                        {
                            name: 'backend',
                            type: 'UiExposeBackend'
                        }
                    ],
                    ctors: [
                        {
                            parameters: [
                                {
                                    name: 'backend',
                                    type: 'UiExposeBackend'
                                }
                            ],
                            statements: [
                                'this.backend = backend;'
                            ]
                        }
                    ],
                    methods: serviceInfo.methods.map(method => this.methodGenerator.makeMethodDeclaration(serviceInfo.name, method))
                }]
        }, {
            overwrite: true
        }).formatText({
            ensureNewLineAtEndOfFile: true
        });
    }
    
    private prepareServiceClassFileName(name: string): string {
        const nameFormatter = (str: string) => str.replace(/\B[A-Z]/g, letter => `-${letter.toLowerCase()}`) + '.service';
        return nameFormatter(name).toLowerCase();
    }
    
    private prepareImports(serviceModel: ServiceModel): StatementStructures[] {
        const imports: StatementStructures[] = [
            {
                kind: StructureKind.ImportDeclaration,
                namedImports: [{
                    name: 'Injectable',
                }],
                moduleSpecifier: '@angular/core'
            },
            
            {
                kind: StructureKind.ImportDeclaration,
                namedImports: [{
                    name: 'Observable',
                }],
                moduleSpecifier: 'rxjs'
            },
            
            {
                kind: StructureKind.ImportDeclaration,
                namedImports: [{
                    name: 'UiExposeBackend',
                }],
                moduleSpecifier: '@tislib/ui-expose-angular-lib'
            }
        ];
        
        const allTypes: string[] = [];
        const addTypeIfNotExists = (type: string) => {
            if (allTypes.indexOf(type) === -1) {
                allTypes.push(type);
            }
        };
        serviceModel.methods.forEach(method => {
            addTypeIfNotExists(method.returnType);
            for (let index in method.arguments) {
                addTypeIfNotExists(method.arguments[index].type);
            }
        });
        
        allTypes.filter(item => item.startsWith('#')).forEach(item => {
            const type = item.substr(1);
            imports.push({
                kind: StructureKind.ImportDeclaration,
                namedImports: [{
                    name: type,
                }],
                moduleSpecifier: '../model/' + this.beanGenerator.prepareBeanClassFileName(type, false)
            });
        });
        
        return imports;
    }
}