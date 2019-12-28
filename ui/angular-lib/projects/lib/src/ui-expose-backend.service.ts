import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApiInvokeDescription } from './api-invoke-description';
import { UiExposeConfig } from './ui-expose-config';

@Injectable()
export class UiExposeBackend {

  constructor(private httpClient: HttpClient, private config: UiExposeConfig) {
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
    return `${this.getHostUrl()}${description.serviceName}/${description.methodName}`;
  }

  private transformResult<T>(result: Observable<Object>, description: ApiInvokeDescription<T>) {
    return result.pipe(map(item => item as T));
  }

  private getHostUrl() {
    if (!this.config.hostUrl.endsWith('/')) {
      this.config.hostUrl = this.config.hostUrl + '/';
    }
    return this.config.hostUrl;
  }
}
