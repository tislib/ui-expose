import { HttpClientModule } from '@angular/common/http';
import { ModuleWithProviders, NgModule } from '@angular/core';
import { UiExposeBackend } from './ui-expose-backend.service';
import { UiExposeConfig } from './ui-expose-config';

@NgModule({
  declarations: [],
  imports: [
    HttpClientModule
  ],
  providers: [
    UiExposeBackend
  ],
  exports: []
})
export class UiExposeModule {

  static forRoot(config: UiExposeConfig): ModuleWithProviders {
    return {
      ngModule: UiExposeModule,
      providers: [
        { provide: UiExposeConfig, useValue: config }
      ]
    };
  }

}
