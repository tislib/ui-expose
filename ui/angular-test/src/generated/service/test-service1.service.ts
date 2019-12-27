import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { UiExposeBackend } from '../../lib/ui-expose-backend.service';

// @ts-ignore
@Injectable()

export class TestService1 {
  static readonly SERVICE_NAME = 'TestService1';

  constructor(backend: UiExposeBackend) {
  }

  test1(arg1: string, arg0: string): Observable<string> {
    this.backend.invoke({
      serviceName: TestService1.SERVICE_NAME,
      methodName: 'test1',
      arguments: [{ 'value': 'arg1', 'type': 'string' }, { 'value': 'arg0', 'type': 'string' }],
      returnType: 'string'
    });
  }

  test3(arg2: string, arg1: string, arg0: string): Observable<string> {
    this.backend.invoke({
      serviceName: TestService1.SERVICE_NAME,
      methodName: 'test3',
      arguments: [{ 'value': 'arg2', 'type': 'string' }, { 'value': 'arg1', 'type': 'string' }, {
        'value': 'arg0',
        'type': 'string'
      }],
      returnType: 'string'
    });
  }

  test2(arg2: string, arg1: string, arg0: string): Observable<string> {
    this.backend.invoke({
      serviceName: TestService1.SERVICE_NAME,
      methodName: 'test2',
      arguments: [{ 'value': 'arg2', 'type': 'string' }, { 'value': 'arg1', 'type': 'string' }, {
        'value': 'arg0',
        'type': 'string'
      }],
      returnType: 'string'
    });
  }
}
