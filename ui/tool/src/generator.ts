import { AsyncSubject, Observable } from 'rxjs';
import { OptionalKind, ParameterDeclarationStructure, Project, StructureKind } from 'ts-morph';
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
    
    storeServiceClass(generatedPath: string, project: Project, item: ServiceInfo) {
        project.createSourceFile(generatedPath + '/' + item.name + '.ts', {
            statements: [{
                kind: StructureKind.Interface,
                name: item.name,
                isExported: true,
                methods: item.methods.map(method => this.makeMethodDecleration(method))
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
    
    private makeMethodDecleration(method: MethodInfo) {
        console.log(method);
        
        const parameters: OptionalKind<ParameterDeclarationStructure>[] = [];
        
        for (let key in method.arguments) {
            parameters.push({
                name: key,
                type: 'string'
            });
        }
        
        return {
            name: method.name,
            returnType: 'string',
            parameters: parameters
        }
    }
}
