"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var ts_morph_1 = require("ts-morph");
var core_lib_helper_1 = require("./core-lib-helper");
var Generator = /** @class */ (function () {
    function Generator() {
        this.coreLibHelper = new core_lib_helper_1.CoreLibHelper();
    }
    Generator.prototype.runTsGenerator = function (data, generatedPath) {
        var _this = this;
        var project = new ts_morph_1.Project({});
        project.addSourceFilesAtPaths(generatedPath + '*.ts');
        data.forEach(function (item) {
            _this.storeServiceClass(generatedPath, project, item);
        });
        project.save()
            .then(function () { return console.log('Updated'); });
    };
    Generator.prototype.storeServiceClass = function (generatedPath, project, serviceInfo) {
        var _this = this;
        project.createSourceFile(generatedPath + '/service/' + Generator.prepareServiceClassFileName(serviceInfo.name) + '.ts', {
            statements: [
                "import { Injectable } from '@angular/core';\nimport { Observable } from 'rxjs';\nimport { UiExposeBackend } from '@tislib/ui-expose-angular-lib';\n\n@Injectable()",
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
                        },
                        {
                            name: 'backend',
                            type: 'UiExposeBackend'
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
                            statements: [
                                'this.backend = backend;'
                            ]
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
        var serviceInfo$ = this.coreLibHelper.runScanner(buildPath);
        serviceInfo$.subscribe(function (res) {
            _this.runTsGenerator(res, generatedPath);
        });
    };
    Generator.prototype.makeMethodDeclaration = function (serviceInfo, method) {
        var parameters = [];
        var argumentsText = '';
        for (var key in method.arguments) {
            parameters.push({
                name: key,
                type: method.arguments[key]
            });
            if (argumentsText) {
                argumentsText += ',';
            }
            argumentsText = argumentsText + "{value: " + key + ", type: '" + method.arguments[key] + "'}";
        }
        return {
            name: method.name,
            returnType: 'Observable<string>',
            parameters: parameters,
            statements: function (writer) {
                writer.write("return this.backend.invoke({\n                  serviceName: " + serviceInfo + ".SERVICE_NAME,\n                  methodName: '" + method.name + "',\n                  arguments: [" + argumentsText + "],\n                  returnType: '" + method.returnType + "'\n                });");
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
