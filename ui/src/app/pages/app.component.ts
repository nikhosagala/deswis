import { Component, OnInit } from '@angular/core';
import {
  NavigationCancel,
  NavigationEnd,
  NavigationError,
  NavigationStart,
  Router
} from '@angular/router';
import { SlimLoadingBarService } from 'ng2-slim-loading-bar';
import { MzModalService } from 'ng2-materialize';
import { CategoryService } from '../services/category.service';
import { Category, Location, Setting } from '../components/utils/models';
import { SettingAdminComponent } from '../components/setting/setting-admin.component';

import { default as swal } from 'sweetalert2';
import { ApplicationService } from '../services/application.service';

@Component({
  selector: 'app-deswis',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})

export class AppComponent implements OnInit {
  private sub: any;
  categories: Category[];
  setting: Setting;
  latestUpdateBackend: string;
  latestUpdateFrontend: string;

  constructor(private applicationService: ApplicationService,
              private categoryService: CategoryService,
              private router: Router,
              private slimLoader: SlimLoadingBarService,
              private modalService: MzModalService) {
    this.sub = this.router.events.subscribe(event => {
      if (event instanceof NavigationStart) {
        this.slimLoader.start();
      } else if (event instanceof NavigationEnd ||
        event instanceof NavigationCancel ||
        event instanceof NavigationError) {
        this.slimLoader.complete();
      }
    }, (error: any) => {
      console.log(error);
      this.slimLoader.complete();
    });
  }

  version() {
    swal({
      title: 'Last Update',
      html: this.latestUpdateBackend + '<br>' + this.latestUpdateFrontend
    });
  }

  getCategorySide() {
    if (this.categories == null) {
      this.categoryService.getChildren().then(response => {
        this.categories = response.data;
      })
    }
  }

  public openServiceModal() {
    this.modalService.open(SettingAdminComponent);
  }

  getCurrentPosition() {
    navigator.geolocation.getCurrentPosition(
      (position: Position) => {
        let _location = new Location;
        _location.lat = position.coords.latitude;
        _location.lng = position.coords.longitude;
        this.applicationService.setLocation(_location);
      },
      (error: PositionError) => {
        console.log('Geolocation service: ' + error.message);
      }
    );
  };

  ngOnInit() {
    this.getCategorySide();
    this.getCurrentPosition();
  }
}
