import { Component, Input, OnInit } from '@angular/core';
import { Setting } from 'app/components/utils/models';

import { default as swal } from 'sweetalert2';
import { ApplicationService } from '../../services/application.service';
import { MzBaseModal } from 'ng2-materialize';

@Component({
  selector: 'setting-admin',
  templateUrl: './setting-admin.component.html',
})

export class SettingAdminComponent extends MzBaseModal implements OnInit {
  setting: Setting;
  @Input()
  _setting = {
    distanceWeight: null,
    durationWeight: null,
    interestWeight: null,
    ratingWeight: null,
    threshold: null,
    name: null,
    age: null,
    status: null
  };

  constructor(private applicationService: ApplicationService) {
    super();
  };

  onInput0(event: any) {
    this._setting.distanceWeight = event.value;
  }

  onInput1(event: any) {
    this._setting.interestWeight = event.value;
  }

  onInput2(event: any) {
    this._setting.ratingWeight = event.value;
  }

  onSaved() {
    this.applicationService.setSetting(this._setting);
    swal(
      'Informasi',
      'Pengaturan Berhasil Disimpan',
      'success'
    )
  }

  ngOnInit() {
    this.setting = this.applicationService.getSetting();
    if (this.setting) {
      this._setting.distanceWeight = this.setting.distanceWeight;
      this._setting.interestWeight = this.setting.interestWeight;
      this._setting.ratingWeight = this.setting.ratingWeight;
    }
  }
}
