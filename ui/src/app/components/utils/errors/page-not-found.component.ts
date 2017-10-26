import {Component} from "@angular/core";
import {Router} from "@angular/router";
import {Location} from "@angular/common";

@Component({
  selector: 'page-not-found',
  templateUrl: './404.html'
})

export class PageNotFoundComponent {

  constructor(private router: Router,
              private location: Location) {
  }

  onClickHome() {
    this.router.navigateByUrl('dashboard');
  }

  onClickBack() {
    this.location.back();
  }

}
