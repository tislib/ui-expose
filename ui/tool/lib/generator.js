"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var rxjs_1 = require("rxjs");
var ts_morph_1 = require("ts-morph");
var exec = require('child_process').exec;
var Generator = /** @class */ (function () {
    function Generator() {
    }
    Generator.prototype.runUiExposeScanner = function (buildPath) {
        var subject = new rxjs_1.AsyncSubject();
        exec('java -jar /Volumes/TisDirectory/TisFiles/Tislib/UI-Expose/core/build/libs/core-1.0-SNAPSHOT.jar ' + buildPath, function (err, stdout) {
            if (err) {
                subject.error(err);
            }
            subject.next(JSON.parse(stdout));
            subject.complete();
        });
        return subject;
    };
    Generator.prototype.runTsGenerator = function (data, generatedPath) {
        var _this = this;
        var project = new ts_morph_1.Project({});
        project.addSourceFilesAtPaths(generatedPath + '*.ts');
        data.forEach(function (item) {
            _this.storeServiceClass(generatedPath, project, item);
        });
        project.save()
            .then(function () { return console.log('Saved!'); });
    };
    Generator.prototype.storeServiceClass = function (generatedPath, project, serviceInfo) {
        var _this = this;
        project.createSourceFile(generatedPath + '/service/' + Generator.prepareServiceClassFileName(serviceInfo.name) + '.ts', {
            statements: [
                "import { Injectable } from '@angular/core';\nimport { Observable } from 'rxjs';\nimport { UiExposeBackend } from '../../lib/ui-expose-backend.service';\n\n@Injectable()",
                {
                    kind: ts_morph_1.StructureKind.Class,
                    name: serviceInfo.name,
                    isExported: true,
                    properties: [
                        {
                            name: 'SERVICE_NAME',
                            isStatic: true,
                            isReadonly: true,
                            initializer: '\'' + serviceInfo.name + '\''
                        }
                    ],
                    ctors: [
                        {
                            parameters: [
                                {
                                    name: 'backend',
                                    type: 'UiExposeBackend'
                                }
                            ],
                        }
                    ],
                    methods: serviceInfo.methods.map(function (method) { return _this.makeMethodDeclaration(serviceInfo.name, method); })
                }
            ]
        }, {
            overwrite: true
        }).formatText({
            ensureNewLineAtEndOfFile: true
        });
    };
    Generator.prototype.run = function (buildPath, generatedPath) {
        var _this = this;
        var serviceInfo$ = this.runUiExposeScanner(buildPath);
        serviceInfo$.subscribe(function (res) {
            _this.runTsGenerator(res, generatedPath);
            console.log('Done');
        });
    };
    Generator.prototype.makeMethodDeclaration = function (serviceInfo, method) {
        var parameters = [];
        var argumentsDef = [];
        for (var key in method.arguments) {
            parameters.push({
                name: key,
                type: method.arguments[key]
            });
            argumentsDef.push({
                value: key,
                type: method.arguments[key]
            });
        }
        var argumentsText = JSON.stringify(argumentsDef);
        return {
            name: method.name,
            returnType: 'Observable<string>',
            parameters: parameters,
            statements: function (writer) {
                writer.write("this.backend.invoke({\n      serviceName: " + serviceInfo + ".SERVICE_NAME,\n      methodName: '" + method.name + "',\n      arguments: " + argumentsText + ",\n      returnType: '" + method.returnType + "'\n    });");
            }
        };
    };
    Generator.prepareServiceClassFileName = function (name) {
        var nameFormatter = function (str) { return str.replace(/\B[A-Z]/g, function (letter) { return "-" + letter.toLowerCase(); }) + '.service'; };
        return nameFormatter(name).toLowerCase();
    };
    return Generator;
}());
exports.Generator = Generator;
