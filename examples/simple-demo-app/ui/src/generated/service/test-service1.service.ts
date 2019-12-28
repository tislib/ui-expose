import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { UiExposeBackend } from '@tislib/ui-expose-angular-lib';

@Injectable()

export class TestService1 {
    static readonly SERVICE_NAME = 'TestService1';
    backend: UiExposeBackend;

    constructor(backend: UiExposeBackend) {
        this.backend = backend;
    }

    test1(param1: string, param2: string): Observable<string> {
        return this.backend.invoke({
            serviceName: TestService1.SERVICE_NAME,
            methodName: 'test1',
            arguments: [{ value: param1, type: 'string' }, { value: param2, type: 'string' }],
            returnType: 'string'
        });
    }

    test2(param3: string, param1: string, param2: string): Observable<string> {
        return this.backend.invoke({
            serviceName: TestService1.SERVICE_NAME,
            methodName: 'test2',
            arguments: [{ value: param3, type: 'string' }, { value: param1, type: 'string' }, { value: param2, type: 'string' }],
            returnType: 'string'
        });
    }

    test4(param3: string, param1: string, param2: string): Observable<string> {
        return this.backend.invoke({
            serviceName: TestService1.SERVICE_NAME,
            methodName: 'test4',
            arguments: [{ value: param3, type: 'string' }, { value: param1, type: 'string' }, { value: param2, type: 'string' }],
            returnType: 'string'
        });
    }

    test3(param3: string, param1: string, param2: string): Observable<string> {
        return this.backend.invoke({
            serviceName: TestService1.SERVICE_NAME,
            methodName: 'test3',
            arguments: [{ value: param3, type: 'string' }, { value: param1, type: 'string' }, { value: param2, type: 'string' }],
            returnType: 'string'
        });
    }
}
