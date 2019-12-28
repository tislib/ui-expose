import { AsyncSubject, Observable } from 'rxjs';
import {
    MethodDeclarationStructure,
    OptionalKind,
    ParameterDeclarationStructure,
    Project,
    StructureKind
} from 'ts-morph';
import { CoreLibHelper } from './core-lib-helper';
import { MethodInfo } from './data/method-info';
import { ServiceInfo } from './data/service-info';

export class Generator {
    private coreLibHelper: CoreLibHelper = new CoreLibHelper();
    
    runTsGenerator(data: ServiceInfo[], generatedPath: string) {
        const project = new Project({});
        
        project.addSourceFilesAtPaths(generatedPath + '*.ts');
        data.forEach(item => {
            this.storeServiceClass(generatedPath, project, item);
        });
        
        project.save()
            .then(() => console.log('Updated'));
    }
    
    storeServiceClass(generatedPath: string, project: Project, serviceInfo: ServiceInfo) {
        project.createSourceFile(generatedPath + '/service/' + Generator.prepareServiceClassFileName(serviceInfo.name) + '.ts', {
            statements: [
                `import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { UiExposeBackend } from '@tislib/ui-expose-angular-lib';

@Injectable()`,
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
                    methods: serviceInfo.methods.map(method => this.makeMethodDeclaration(serviceInfo.name, method))
                }]
        }, {
            overwrite: true
        }).formatText({
            ensureNewLineAtEndOfFile: true
        });
    }
    
    run(buildPath: string, generatedPath: string) {
        const serviceInfo$ = this.coreLibHelper.runScanner(buildPath);
        
        serviceInfo$.subscribe(res => {
            this.runTsGenerator(res, generatedPath);
        });
        
    }
    
    private makeMethodDeclaration(serviceInfo: string, method: MethodInfo): OptionalKind<MethodDeclarationStructure> {
        const parameters: OptionalKind<ParameterDeclarationStructure>[] = [];
        
        let argumentsText = '';
        for (let key in method.arguments) {
            parameters.push({
                name: key,
                type: method.arguments[key]
            });
            if (argumentsText) {
                argumentsText += ',';
            }
            argumentsText = `${argumentsText}{value: ${key}, type: '${method.arguments[key]}'}`;
        }
        
        return {
            name: method.name,
            returnType: 'Observable<string>',
            parameters: parameters,
            statements: writer => {
                writer.write(`return this.backend.invoke({
                  serviceName: ${serviceInfo}.SERVICE_NAME,
                  methodName: '${method.name}',
                  arguments: [${argumentsText}],
                  returnType: '${method.returnType}'
                });`);
            }
        }
    }
    
    private static prepareServiceClassFileName(name: string): string {
        const nameFormatter = (str: string) => str.replace(/\B[A-Z]/g, letter => `-${letter.toLowerCase()}`) + '.service';
        return nameFormatter(name).toLowerCase();
    }
}
