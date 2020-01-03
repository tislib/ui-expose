import { Project } from 'ts-morph';
import { CoreLibHelper } from '../core-lib-helper';
import { Model } from '../data/model';
import { BeanGenerator } from './bean-generator';
import { ServiceGenerator } from './service-generator';

export class ModelGenerator {
    private coreLibHelper: CoreLibHelper = new CoreLibHelper();
    private serviceGenerator: ServiceGenerator = new ServiceGenerator();
    private beanGenerator: BeanGenerator = new BeanGenerator();
    
    runTsGenerator(data: Model, generatedPath: string) {
        const project = new Project({});
        
        project.addSourceFilesAtPaths(generatedPath + '*.ts');
        data.services.forEach(item => {
            this.serviceGenerator.storeServiceClass(generatedPath, project, item);
        });
        data.beans.forEach(item => {
            this.beanGenerator.storeBeanClass(generatedPath, project, item);
        });
        // data.enums.forEach(item => {
        //     this.storeEnumClass(generatedPath, project, item);
        // });
        
        project.save()
            .then(() => console.log('Updated'));
    }
    
    run(buildPath: string, generatedPath: string) {
        const model$ = this.coreLibHelper.runScanner(buildPath);
        
        model$.subscribe(res => {
            this.runTsGenerator(res, generatedPath);
        });
        
    }
    
}