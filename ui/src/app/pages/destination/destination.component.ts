import { Component, OnInit } from '@angular/core';

import { SlimLoadingBarService } from 'ng2-slim-loading-bar';
import { DestinationService } from '../../services/destination.service';
import { Destination } from '../../components/utils/models';

@Component({
  selector: 'destination',
  templateUrl: './destination.component.html'
})

export class DestinationComponent implements OnInit {
  destinations: Destination[];
  displayPagination = true;
  loading: boolean = false;
  page: number = 1;
  private totalItems: number;
  private currentPage: number = 1;
  private itemsPerPage: number = 6;

  constructor(private destinationService: DestinationService,
              private slimLoader: SlimLoadingBarService) {
  }

  getDestinations() {
    return this.destinationService.get(null, null, this.itemsPerPage, this.page).then(response => {
      this.destinations = response.data.slice(0, this.itemsPerPage);
      this.totalItems = response.total_record;
    });
  }

  public pageChanged(event: any): void {
    this.loading = true;
    this.slimLoader.start();
    this.destinationService.get(null, null, event.itemsPerPage, event.page).then(response => {
      this.destinations = response.data;
      this.slimLoader.stop();
      this.incrementProgress();
      this.loading = false;
    });
  }

  incrementProgress() {
    this.slimLoader.progress = (100 / Math.ceil(this.totalItems / this.itemsPerPage)) * this.currentPage;
  }

  ngOnInit() {
    this.getDestinations().then(() => {
      this.slimLoader.progress = (100 / Math.ceil(this.totalItems / this.itemsPerPage)) * this.currentPage;
    });
  }
}
