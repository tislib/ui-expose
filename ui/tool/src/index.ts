#!/usr/bin/env node

import * as fs from 'fs';
import { interval, Subject } from 'rxjs';
import { debounce } from 'rxjs/operators';
import { ConfigLoader } from './config-loader';
import { Generator } from './generator';

const configLoader = new ConfigLoader();

interface FsWatchEvent {
    event: string;
    filename: string;
}

const fsEventSubject = new Subject<FsWatchEvent>();
const generator = new Generator();

fs.watch(configLoader.getBuildPath(), {
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
        generator.run(configLoader.getBuildPath(), configLoader.getGeneratedPath());
    });

generator.run(configLoader.getBuildPath(), configLoader.getGeneratedPath())


