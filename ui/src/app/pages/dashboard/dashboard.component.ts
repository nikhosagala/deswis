import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { RecommendationService } from '../../services/recommendation.service';
import {
  Category,
  Recommendation,
  RecommendationDetail,
  Setting
} from '../../components/utils/models';

import { default as swal } from 'sweetalert2';
import { CategoryService } from '../../services/category.service';
import { ApplicationService } from '../../services/application.service';

@Component({
  selector: 'dashboard',
  templateUrl: './dashboard.component.html',
})

export class DashboardComponent implements OnInit {
  recommendation: Recommendation;
  categories: Category[];
  recommendationDetail: RecommendationDetail[];
  _recommendationDetail: RecommendationDetail[];
  _setting = {
    distanceWeight: null,
    durationWeight: null,
    interestWeight: null,
    ratingWeight: null,
    threshold: null,
    name: null,
  };
  @Input()
  post = {
    lat: null,
    lng: null,
    distanceWeight: null,
    durationWeight: null,
    interestWeight: null,
    ratingWeight: null,
    processTime: null,
    name: null,
    threshold: null,
    recommendationDetails: [],
  };
  latest = {
    distanceWeight: null,
    durationWeight: null,
    interestWeight: null,
    ratingWeight: null,
    name: null,
    threshold: null,
    recommendationDetails: [],
  };
  displayPagination = false;
  loading: boolean = false;
  page: number = 1;
  showSetting: boolean;
  processTime: number = 0;
  private totalItems: number;
  private currentPage: number = 1;
  private itemsPerPage: number = 8;
  private totalRecommendation: number;

  constructor(private recommendationService: RecommendationService,
              private categoryService: CategoryService,
              private applicationService: ApplicationService,
              private router: Router) {
  }

  submit(save: any): Promise<any> {
    this.setParam();
    return this.recommendationService.save(this.post, save).then(response => {
      this.totalRecommendation = response.total_record;
      if (save) {
        this.recommendation = response.data;
      } else {
        this.processTime = response.data;
      }
    });
  }

  onSaveSetting(event: any) {
    this.showSetting = event;
  }

  setParam() {
    let _setting = this.applicationService.getSetting();
    let _location = this.applicationService.getLocation();
    if (_location) {
      this.post.lat = _location.lat;
      this.post.lng = _location.lng;
    }
    this.latest.durationWeight = _setting.durationWeight;
    this.post.durationWeight = _setting.durationWeight;
    this.latest.interestWeight = _setting.interestWeight;
    this.post.interestWeight = _setting.interestWeight;
    this.latest.ratingWeight = _setting.ratingWeight;
    this.post.ratingWeight = _setting.ratingWeight;
    this.latest.name = _setting.name;
    this.post.name = _setting.name;
    this.post.threshold = _setting.threshold;
    this.latest.threshold = _setting.threshold;
    if (this.processTime != 0) {
      this.post.processTime = this.processTime;
    }
    this.latest.recommendationDetails = this.recommendationDetail;
    this.post.recommendationDetails = this.applicationService.getRecommendationDetail();
  }

  saveRecommendationDetail() {
    for (let detail of this.validateInput(this.recommendationDetail)) {
      this.applicationService.addRecommendationDetail(detail);
    }
  }

  validateInput(recommendationDetail: RecommendationDetail[]): RecommendationDetail[] {
    let details = [];
    for (let detail of recommendationDetail) {
      if (detail.interestValue != 0) {
        details.push(detail);
      }
    }
    return details;
  }

  reset() {
    this.getCategories();
    this.loading = false;
    for (let detail of this.recommendationDetail) {
      detail.interestValue = 0;
    }
  }

  resetRecommendationDetail() {
    this.setRecommendationDetail();
  }

  getCategories() {
    // if (this.categories == null) {
    this.categoryService.getChildren().then(response => {
      this.categories = response.data;
      this.parseCategories(this.categories);
    })
    // }
  }

  public pageChanged(event: any): void {
    this.loading = true;
    let offset = (event.page - 1) * event.itemsPerPage;
    let limit = offset + this.itemsPerPage;
    this.recommendationDetail = this._recommendationDetail.slice(offset, limit);
    this.loading = false;
  }

  getLatestCategories(submit: any) {
    return this.recommendationService.latest(submit).then(response => {
      this.categories = response.data;
      this.parseCategories(this.categories);
    })
  }

  parseCategories(data: any) {
    this.recommendationDetail = DashboardComponent.initRecommendationDetail(data).slice(0, this.itemsPerPage);
    this._recommendationDetail = DashboardComponent.initRecommendationDetail(data);
    this.totalItems = this._recommendationDetail.length;
    if (this.totalItems > this.itemsPerPage) {
      this.displayPagination = true;
    }
  }

  onClickSubmit() {
    this.saveRecommendationDetail();
    this.loading = true;
    this.submit(+0).then(() => {
        if (this.totalRecommendation == 0) {
          this.loading = false;
          this.reset();
          this.resetRecommendationDetail();
          swal({
            title: 'Informasi',
            text: 'Sistem belum bisa memberikan rekomendasi.',
            type: 'info'
          }).then(function () {
            swal.close();
            this.reset();
            this.resetRecommendationDetail();
            this.getCategories();
          })
        } else if (this.totalRecommendation > 5) {
          this.getLatestCategories(this.latest).then(() => {
            if (this.categories.length == 0) {
              swal({
                title: 'Apakah Anda Yakin?',
                type: 'question',
                text: 'Jumlah destinasi wisata terlalu banyak, yaitu sebanyak ' + this.totalRecommendation + '. Sistem akan menanyakan kategori lain untuk Anda pilih.',
                showCancelButton: true,
                confirmButtonText: 'Tampilkan Destinasi',
                cancelButtonText: 'Kembali ke Awal'
              }).then(function () {
                this.saveSubmit();
              }.bind(this), function (dismiss) {
                if (dismiss === 'cancel') {
                  this.reset();
                  this.resetRecommendationDetail();
                  this.getCategories();
                }
              }.bind(this))
            } else {
              swal({
                title: 'Apakah Anda Yakin?',
                type: 'question',
                text: 'Jumlah destinasi wisata terlalu banyak, yaitu sebanyak ' + this.totalRecommendation + '. Sistem akan menanyakan kategori lain untuk Anda pilih.',
                showCancelButton: true,
                confirmButtonText: 'Pilih Kategori Lain',
                cancelButtonText: 'Tampilkan Destinasi'
              }).then(function () {
                this.getLatestCategories(this.latest).then(() => {
                  this.changeThreshold(1);
                  this.loading = false;
                });
                this.reset();
              }.bind(this), function (dismiss) {
                if (dismiss === 'cancel') {
                  this.saveSubmit();
                }
              }.bind(this))
            }
          });
        } else {
          this.saveSubmit();
        }
      }
    )
  }

  saveSubmit() {
    this.submit(+1).then(() => {
      swal({
        title: 'Informasi',
        text: 'Rekomendasi berhasil.',
        type: 'success'
      }).then(function () {
        this.loading = false;
        this.router.navigateByUrl('recommendation/' + this.recommendation.id).then(() => this.setRecommendationDetail());
      }.bind(this));
    });
  }

  static initRecommendationDetail(data: Category[]): RecommendationDetail[] {
    let recommendationDetail = [];
    if (data) {
      for (let cat of data) {
        let temp: RecommendationDetail = new RecommendationDetail(0, cat);
        recommendationDetail.push(temp);
      }
    }
    return recommendationDetail;
  }

  setRecommendationDetail() {
    let recommendationDetails: RecommendationDetail[] = [];
    this.applicationService.setRecommendationDetail(recommendationDetails)
  }

  checkSetting() {
    let _setting = this.applicationService.getSetting();
    if (!_setting) {
      this.showSetting = true;
    } else {
      this._setting = _setting;
      this.showSetting = false;
    }
  }

  changeThreshold(type: number) {
    let _setting = this.applicationService.getSetting();
    if (!_setting) {
      _setting = new Setting();
    }
    if (type == 0) {
      _setting.threshold = 0.5;
    } else {
      _setting.threshold += 0.05;
    }
    this.applicationService.setSetting(_setting);
  }

  onSaved() {
    if (this._setting.durationWeight == null || this._setting.interestWeight == null || this._setting.ratingWeight == null || !this._setting.name) {
      swal(
        'Informasi',
        'Silahkan isi informasi terlebih dahulu',
        'info'
      )
    } else {
      this._setting.threshold = this.applicationService.getSetting().threshold;
      this.applicationService.setSetting(this._setting);
      this.showSetting = false;
      this.onClickSubmit();
    }
  }

  ngOnInit() {
    this.getCategories();
    this.checkSetting();
    this.changeThreshold(0);
    this.setRecommendationDetail();
  }
}
