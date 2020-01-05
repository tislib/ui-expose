import download from 'mvn-artifact-download';
import { from, Observable, of, throwError } from 'rxjs';
import { catchError, flatMap } from 'rxjs/operators';
import { Model } from './data/model';

const fs = require('fs');

const { exec } = require('child_process');

export class CoreLibHelper {
    private static readonly CORE_LIB_MAVEN_DEPENDENCY = 'net.tislib.ui-expose:core:0.0.1';
    private static CORE_LIB_JAR: string = 'core.jar';
    private static CORE_LIB_PATH: string = require('path').dirname(require.main?.filename);
    
    runScanner(buildPath: string): Observable<Model> {
        const coreLibPath$ = this.getCoreLib();
        return coreLibPath$.pipe(flatMap(coreLibPath => new Observable<Model>(observer => {
            exec(`java -jar ${CoreLibHelper.CORE_LIB_PATH}/${coreLibPath} ${buildPath}`, (err: object, stdout: string) => {
                if (err) {
                    observer.error(err);
                }
                
                observer.next(JSON.parse(stdout));
                observer.complete();
            });
        })));
    }
    
    private getCoreLib(): Observable<string> {
        return this.locateCoreLib().pipe(catchError(error => this.downloadCoreLib()));
    }
    
    private locateCoreLib(): Observable<string> {
        if (fs.existsSync(CoreLibHelper.CORE_LIB_PATH + '/' + CoreLibHelper.CORE_LIB_JAR)) {
            return of(CoreLibHelper.CORE_LIB_JAR);
        }
        return throwError('core lib not found');
    }
    
    private downloadCoreLib(): Observable<string> {
        console.log('downloading core library');
        const downloadPromise = download(
            CoreLibHelper.CORE_LIB_MAVEN_DEPENDENCY,
            CoreLibHelper.CORE_LIB_PATH,
            undefined,
            CoreLibHelper.CORE_LIB_JAR);
        return from(downloadPromise);
    }
}