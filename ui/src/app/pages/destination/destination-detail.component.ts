import {Component, OnChanges, OnInit} from "@angular/core";
import {ActivatedRoute, Params} from "@angular/router";

import {DestinationService} from "../../services/destination.service";
import {Destination, GooglePlace} from "../../components/utils/models";

@Component({
  selector: 'destination-detail',
  templateUrl: './destination-detail.component.html'
})

export class DestinationDetailComponent implements OnInit, OnChanges {
  showMap: boolean = false;
  destination: Destination;
  googlePlace: GooglePlace;

  constructor(private destinationService: DestinationService,
              private router: ActivatedRoute) {
  }

  getDestination(id: number) {
    return this.destinationService.find(id).then(response => {
      this.destination = response.data;
      this.googlePlace = this.destination.googlePlace;
    });
  }

  showMaps(status: boolean) {
    this.showMap = status;
  }

  ngOnInit() {
    this.router.params.subscribe((params: Params) => {
      this.getDestination(+params['id']).then(response => {
        if (this.googlePlace.id == 1) {
        }
      });
    });
  }

  ngOnChanges() {
    this.googlePlace = null;
  }
}
