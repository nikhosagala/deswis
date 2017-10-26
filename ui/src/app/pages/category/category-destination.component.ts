import {Component, OnInit} from "@angular/core";
import {ActivatedRoute, Params} from "@angular/router";

import {CategoryService} from "../../services/category.service";
import {DestinationService} from "../../services/destination.service";
import {Destination} from "../../components/utils/models";
import {SlimLoadingBarService} from "ng2-slim-loading-bar";

@Component({
  selector: 'category',
  templateUrl: './category-destination.component.html'
})

export class CategoryDestinationComponent implements OnInit {
  destinations: Destination[];
  displayPagination: boolean = true;
  noData: boolean = false;
  loading: boolean = false;
  title: string;
  private totalItems: number;
  private currentPage: number = 1;
  private itemsPerPage: number = 6;

  constructor(private categoryService: CategoryService,
              private destinationService: DestinationService,
              private router: ActivatedRoute,
              private slimLoader: SlimLoadingBarService) {
  }

  getDestinationByCategory(id) {
    this.noData = false;
    return this.destinationService.getDestinationByCategory(id).then(response => {
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
    });
  }

  getCategory(id) {
    let filter = {
      filters: [
        {
          property: 'id',
          operator: 'equals',
          values: [{value: id}]
        }]
    };
    this.categoryService.get(filter, null, null, null).then(response => {
      this.title = this.parseJson(response.data);
    });
  }

  public pageChanged(event: any): void {
    this.loading = true;
    this.router.params.subscribe((params: Params) => {
      this.destinationService.getDestinationByCategory(+params['id'], null, null, event.itemsPerPage, event.page).then(response => {
        this.destinations = response.data;
        this.incrementProgress();
        this.loading = false;
      })
    });
  }

  public parseJson(datas: any): string {
    for (let data of datas) {
      return data.name;
    }
  }

  incrementProgress() {
    let progress = (100 / Math.ceil(this.totalItems / this.itemsPerPage)) * this.currentPage;
    this.slimLoader.progress = progress;
  }

  ngOnInit() {
    this.router.params.subscribe((params: Params) => {
      this.getDestinationByCategory(+params['id']).then(response => {
        this.slimLoader.progress = (100 / Math.ceil(this.totalItems / this.itemsPerPage)) * this.currentPage;
      });
      this.getCategory(+params['id']);
    });
  }
}
