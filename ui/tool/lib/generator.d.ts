import { Observable } from 'rxjs';
import { Project } from 'ts-morph';
import { ServiceInfo } from './data/service-info';
export declare class Generator {
    runUiExposeScanner(buildPath: string): Observable<ServiceInfo[]>;
    runTsGenerator(data: ServiceInfo[], generatedPath: string): void;
    storeServiceClass(generatedPath: string, project: Project, serviceInfo: ServiceInfo): void;
    run(buildPath: string, generatedPath: string): void;
    private makeMethodDeclaration;
    private static prepareServiceClassFileName;
}
