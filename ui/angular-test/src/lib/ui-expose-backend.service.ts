import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApiInvokeDescription } from './api-invoke-description';

@Injectable({
  providedIn: 'root'
})
export class UiExposeBackend {

  constructor(private httpClient: HttpClient) {
  }

  public invoke<T>(description: ApiInvokeDescription<T>): Observable<T> {
    const url = this.generateUrl(description);

    const result = this.httpClient.post(url, {
      'values': description.arguments.map(item => {
        return {
          type: item.type,
          value: item.value,
        };
      })
    });

    return this.transformResult(result, description);
  }

  private generateUrl<T>(description: ApiInvokeDescription<T>) {
    return `http://localhost:8080/api/${description.serviceName}/${description.methodName}`;
  }

  private transformResult<T>(result: Observable<Object>, description: ApiInvokeDescription<T>) {
    return result.pipe(map(item => item as T));
  }
}
