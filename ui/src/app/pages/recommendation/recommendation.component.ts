import { Component, OnInit } from '@angular/core';
import { Destination, Recommendation } from '../../components/utils/models';
import { RecommendationService } from '../../services/recommendation.service';
import { ActivatedRoute, Params } from '@angular/router';
import { SlimLoadingBarService } from 'ng2-slim-loading-bar';
import { MzModalService } from 'ng2-materialize';
import { SurveyModalComponent } from '../../components/modal/survey-modal.component';
import { ApplicationService } from '../../services/application.service';

@Component({
  selector: 'recommendation',
  templateUrl: './recommendation.component.html'
})

export class RecommendationComponent implements OnInit {
  destinations: Destination[] = [];
  destClick: Destination[] = [];
  noData: boolean = false;
  check: boolean = false;
  disabled: boolean = false;
  showMap: boolean = false;
  rating: number = 0;
  recommendation: Recommendation;
  displayPagination = true;
  page: number = 1;
  private totalItems: number;
  private currentPage: number = 1;
  private itemsPerPage: number = 6;

  constructor(private recommendationService: RecommendationService,
              private applicationService: ApplicationService,
              private router: ActivatedRoute,
              private slimLoader: SlimLoadingBarService,
              private modalService: MzModalService) {
  }

  getRecommendation(id) {
    this.noData = false;
    return this.recommendationService.getDestinations(id).then(response => {
      this.destinations = response.data.slice(0, this.itemsPerPage);
      this.totalItems = response.total_record;
      if (this.totalItems == 0) {
        this.noData = true;
      }
      if (this.totalItems < this.itemsPerPage) {
        this.displayPagination = false;
      } else {
        this.displayPagination = true;
      }
    })
  }

  public pageChanged(event: any): void {
    this.router.params.subscribe((params: Params) => {
      this.recommendationService.getDestinations(+params['id'], null, null, event.itemsPerPage, event.page).then(response => {
        this.destinations = response.data;
        this.incrementProgress();
      });
    });
  }

  incrementProgress() {
    let progress = (100 / Math.ceil(this.totalItems / this.itemsPerPage)) * this.currentPage;
    this.slimLoader.progress = progress;
  }

  onClicked(destination: Destination) {
    if (this.check) {
      this.destClick.push(destination);
    } else {
      let index: number = this.destClick.indexOf(destination);
      if (index !== -1) {
        this.destClick.splice(index, 1);
      }
    }
  }

  isChecked(checked: boolean) {
    this.check = checked;
  }

  showMaps() {
    if (this.showMap) {
      this.showMap = false;
    } else {
      this.showMap = true;
    }
  }

  update(update: any) {
    this.recommendationService.update(update).then(response => {
      this.recommendation = response.data;
    })
  }

  survey() {
    this.setParam();
  }

  setParam() {
    let _recommendation = new Recommendation();
    _recommendation.id = this.recommendation.id;
    _recommendation.rating = this.rating;
    _recommendation.processTime = this.recommendation.processTime;
    _recommendation.name = this.recommendation.name;
    this.update(_recommendation);
    this.applicationService.setRecommendation(this.recommendation);
    this.modalService.open(SurveyModalComponent);
  }

  ngOnInit() {
    this.router.params.subscribe((params: Params) => {
      this.destClick = [];
      this.recommendationService.find(+params['id']).then(response => {
        this.recommendation = response.data;
        this.rating = this.recommendation.rating;
        if (this.rating != null) {
          this.disabled = true;
        }
      });
      this.getRecommendation(+params['id']).catch();
    })
  }

}
