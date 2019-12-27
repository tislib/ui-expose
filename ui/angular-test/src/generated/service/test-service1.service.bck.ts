import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { UiExposeBackend } from '../../lib/ui-expose-backend.service';

@Injectable()
export class TestService1 {
  private static readonly SERVICE_NAME = 'TestService1';

  constructor(private backend: UiExposeBackend) {
  }

  public test1(param1: string, param2: string): Observable<string> {
    return this.backend.invoke({
      serviceName: TestService1.SERVICE_NAME,
      methodName: 'test1',
      arguments: [
        {
          type: 'string',
          value: param1,
        },
        {
          type: 'string',
          value: param2,
        }
      ],
      returnType: 'string'
    });
  }

}
