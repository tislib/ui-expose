import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { UiExposeBackend } from "@tislib/ui-expose-angular-lib";
import { Person } from "../model/person.model";
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

    createDummyPerson(name: string, surname: string): Observable<Person> {
        return this.backend.invoke({
            serviceName: TestService1.SERVICE_NAME,
            methodName: 'createDummyPerson',
            arguments: [{ value: name, type: 'string' }, { value: surname, type: 'string' }],
            returnType: '#Person'
        });
    }
}
