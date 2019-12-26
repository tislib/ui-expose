import * as fs from 'fs';
import { interval, Subject } from 'rxjs';
import { debounce } from 'rxjs/operators';
import { Generator } from './generator';

const buildPath = '/Volumes/TisDirectory/TisFiles/Tislib/UI-Expose/spring-lib/out/production/classes';
const generatedPath = '/Volumes/TisDirectory/TisFiles/Tislib/UI-Expose/ui/tool/src/generated';


interface FsWatchEvent {
    event: string;
    filename: string;
}

const fsEventSubject = new Subject<FsWatchEvent>();
const generator = new Generator();

fs.watch(buildPath, {
    recursive: true,
}, (event: string, filename: string) => {
    fsEventSubject.next({
        event: event,
        filename: filename
    });
});

fsEventSubject
    .pipe(debounce(() => interval(1000)))
    .subscribe(event => {
        generator.run(buildPath, generatedPath);
    });

generator.run(buildPath, generatedPath);