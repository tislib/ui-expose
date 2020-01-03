import { CoreLibHelper } from './core-lib-helper';
import { ModelGenerator } from './generator/model-generator';

export class Generator {
    private coreLibHelper: CoreLibHelper = new CoreLibHelper();
    private modelGenerator: ModelGenerator = new ModelGenerator();
    
    run(buildPath: string, generatedPath: string) {
        const model$ = this.coreLibHelper.runScanner(buildPath);
        
        model$.subscribe(res => {
            this.modelGenerator.runTsGenerator(res, generatedPath);
        });
        
    }
}
