#!/usr/bin/env node
"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var fs = require("fs");
var rxjs_1 = require("rxjs");
var operators_1 = require("rxjs/operators");
var config_loader_1 = require("./config-loader");
var generator_1 = require("./generator");
var configLoader = new config_loader_1.ConfigLoader();
var fsEventSubject = new rxjs_1.Subject();
var generator = new generator_1.Generator();
fs.watch(configLoader.getBuildPath(), {
    recursive: true,
}, function (event, filename) {
    fsEventSubject.next({
        event: event,
        filename: filename
    });
});
fsEventSubject
    .pipe(operators_1.debounce(function () { return rxjs_1.interval(1000); }))
    .subscribe(function (event) {
    generator.run(configLoader.getBuildPath(), configLoader.getGeneratedPath());
});
generator.run(configLoader.getBuildPath(), configLoader.getGeneratedPath());
