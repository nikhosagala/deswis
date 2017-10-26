import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Destination, GooglePlace } from '../utils/models';

@Component({
  selector: 'card-rec',
  templateUrl: './card-recommendation.component.html'
})
export class CardRecommendationComponent{
  @Input()
  destination: Destination;
  @Output()
  onClicked = new EventEmitter<Destination>();
  @Output()
  checked = new EventEmitter<boolean>();

  onChange(event: any) {
    this.checked.emit(event.checked);
    this.onClicked.emit(this.destination);
  }

}
