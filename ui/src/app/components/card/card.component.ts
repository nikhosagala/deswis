import {Component, Input} from "@angular/core";
import {Destination} from "../utils/models";

@Component({
  selector: 'card',
  templateUrl: './card.component.html'
})
export class CardComponent {
  @Input()
  destination: Destination;
}
