"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var rxjs_1 = require("rxjs");
var generator_1 = require("./generator");
var buildPath = '/Volumes/TisDirectory/TisFiles/Tislib/UI-Expose/spring-lib/out/production/classes';
var generatedPath = '/Volumes/TisDirectory/TisFiles/Tislib/UI-Expose/ui/angular-test/src/generated';
var fsEventSubject = new rxjs_1.Subject();
var generator = new generator_1.Generator();
// fs.watch(buildPath, {
//     recursive: true,
// }, (event: string, filename: string) => {
//     fsEventSubject.next({
//         event: event,
//         filename: filename
//     });
// });
//
// fsEventSubject
//     .pipe(debounce(() => interval(1000)))
//     .subscribe(event => {
//         generator.run(buildPath, generatedPath);
//     });
generator.run(buildPath, generatedPath);
