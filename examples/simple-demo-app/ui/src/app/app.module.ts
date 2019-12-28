import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { UiExposeModule } from '@tislib/ui-expose-angular-lib';
import { environment } from '../environments/environment';
import { TestService1 } from '../generated/service/test-service1.service';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    UiExposeModule.forRoot(environment.uiExposeConf)
  ],
  providers: [TestService1],
  bootstrap: [AppComponent]
})
export class AppModule { }
