import { AsyncSubject, Observable } from 'rxjs';
import {
    MethodDeclarationStructure,
    OptionalKind,
    ParameterDeclarationStructure,
    Project,
    StructureKind
} from 'ts-morph';
import { MethodInfo } from './data/method-info';
import { ServiceInfo } from './data/service-info';

const { exec } = require('child_process');

export class Generator {
    
    runUiExposeScanner(buildPath: string): Observable<ServiceInfo[]> {
        const subject = new AsyncSubject<ServiceInfo[]>();
        exec('java -jar /Volumes/TisDirectory/TisFiles/Tislib/UI-Expose/core/build/libs/core-1.0-SNAPSHOT.jar ' + buildPath, (err: object, stdout: string) => {
            if (err) {
                subject.error(err);
            }
            
            subject.next(JSON.parse(stdout));
            subject.complete();
        });
        
        return subject;
    }
    
    runTsGenerator(data: ServiceInfo[], generatedPath: string) {
        const project = new Project({});
        
        project.addSourceFilesAtPaths(generatedPath + '*.ts');
        data.forEach(item => {
            this.storeServiceClass(generatedPath, project, item);
        });
        
        project.save()
            .then(() => console.log('Saved!'));
    }
    
    storeServiceClass(generatedPath: string, project: Project, serviceInfo: ServiceInfo) {
        project.createSourceFile(generatedPath + '/service/' + Generator.prepareServiceClassFileName(serviceInfo.name) + '.ts', {
            statements: [
                `import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { UiExposeBackend } from '../../lib/ui-expose-backend.service';

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
        const serviceInfo$ = this.runUiExposeScanner(buildPath);
        
        serviceInfo$.subscribe(res => {
            this.runTsGenerator(res, generatedPath);
            console.log('Done')
        });
        
    }
    
    private makeMethodDeclaration(serviceInfo: string, method: MethodInfo): OptionalKind<MethodDeclarationStructure> {
        const parameters: OptionalKind<ParameterDeclarationStructure>[] = [];
        const argumentsDef = [];
        
        for (let key in method.arguments) {
            parameters.push({
                name: key,
                type: method.arguments[key]
            });
            argumentsDef.push({
                value: key,
                type: method.arguments[key]
            });
        }
        
        const argumentsText = JSON.stringify(argumentsDef);
        
        return {
            name: method.name,
            returnType: 'Observable<string>',
            parameters: parameters,
            statements: writer => {
                writer.write(`this.backend.invoke({
      serviceName: ${serviceInfo}.SERVICE_NAME,
      methodName: '${method.name}',
      arguments: ${argumentsText},
      returnType: '${method.returnType}'
    });`)
            }
        }
    }
    
    private static prepareServiceClassFileName(name: string): string {
        const nameFormatter = (str: string) => str.replace(/\B[A-Z]/g, letter => `-${letter.toLowerCase()}`) + '.service';
        return nameFormatter(name).toLowerCase();
    }
}
