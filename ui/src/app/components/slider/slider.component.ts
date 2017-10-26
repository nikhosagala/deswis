import {Component, Input} from "@angular/core";
import {RecommendationDetail} from "../utils/models";

@Component({
  selector: 'deswis-slider',
  templateUrl: './slider.component.html'
})
export class SliderComponent {
  @Input()
  recommendationDetail: RecommendationDetail;
  @Input()
  disable: boolean;

  onInput(event: any) {
    this.recommendationDetail.interestValue = event.value;
  }

  onInputButton(type: number) {
    if (type == 0 && this.recommendationDetail.interestValue > 0) {
      this.recommendationDetail.interestValue = this.recommendationDetail.interestValue - 5;
    } else if (type == 1 && this.recommendationDetail.interestValue < 100) {
      this.recommendationDetail.interestValue = this.recommendationDetail.interestValue + 5;
    }
  }
}
