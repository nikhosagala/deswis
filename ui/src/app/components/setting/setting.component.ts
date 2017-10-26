import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Setting } from 'app/components/utils/models';

import { default as swal } from 'sweetalert2';
import { ApplicationService } from '../../services/application.service';

@Component({
  selector: 'setting',
  templateUrl: './setting.component.html',
})

export class SettingComponent implements OnInit {
  setting: Setting;
  @Input()
  _setting = {
    distanceWeight: null,
    durationWeight: null,
    interestWeight: null,
    ratingWeight: null,
    name: null,
  };
  @Output() showSetting = new EventEmitter();

  constructor(private applicationService: ApplicationService) {
  };

  onInput0(event: any) {
    this._setting.durationWeight = event.value;
  }

  onInput1(event: any) {
    this._setting.interestWeight = event.value;
  }

  onInput2(event: any) {
    this._setting.ratingWeight = event.value;
  }

  ngOnInit() {
    this.setting = this.applicationService.getSetting();
    if (this.setting) {
      this._setting.distanceWeight = this.setting.distanceWeight;
      this._setting.durationWeight = this.setting.durationWeight;
      this._setting.interestWeight = this.setting.interestWeight;
      this._setting.ratingWeight = this.setting.ratingWeight;
    }
  }
}
