import * as fs from 'fs';
import * as cmd from 'yargs';

export class ConfigLoader {
    private cmdArgv: any;
    private buildPath: string = '';
    private generatedPath: string = '';
    
    constructor() {
        this.cmdArgv = cmd.options({
            buildPath: { type: 'string' },
            generatedPath: { type: 'string' },
            config: { type: 'string', default: 'ui-expose.conf.json' },
        }).argv;
        
        if (this.cmdArgv.buildPath && this.cmdArgv.generatedPath) {
            this.buildPath = this.cmdArgv.buildPath;
            this.generatedPath = this.cmdArgv.generatedPath;
        } else if (this.cmdArgv.config) {
            // loadConfig
            this.loadConfig(this.cmdArgv.config);
            
        } else {
            this.loadConfig('ui-expose.conf.json');
        }
    }
    
    getBuildPath() {
        return this.buildPath;
    }
    
    getGeneratedPath() {
        return this.generatedPath;
    }
    
    private loadConfig(configFileLocation: string) {
        const config = JSON.parse(fs.readFileSync(configFileLocation).toString());
        
        this.buildPath = config.buildPath;
        this.generatedPath = config.generatedPath;
    }
}