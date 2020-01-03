import { Component } from '@angular/core';
import { TestService1 } from '../generated/service/test-service1.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'angular-test';

  constructor(private testService1: TestService1) {
  }

  test1() {
    this.testService1.test1('aaa', 'bbb')
      .subscribe(resp => {
        console.log(resp);
      });

    this.testService1.createDummyPerson('surname-1', 'test-name-1')
      .subscribe(resp => {
        console.log(resp);
      })
  }
}
