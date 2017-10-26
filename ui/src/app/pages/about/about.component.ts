import {Component, Input, OnInit} from "@angular/core";
import {Router} from "@angular/router";

import {RecommendationService} from "../../services/recommendation.service";
import {Recommendation} from "../../components/utils/models";

import {default as swal} from "sweetalert2";

@Component({
  selector: 'dashboard',
  templateUrl: './about.component.html',
})

export class AboutComponent implements OnInit {

  constructor(private recommendationService: RecommendationService,
              private router: Router) {
  }

  onClickSubmit() {
    swal({
      title: 'Are you sure?',
      type: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Submit',
      cancelButtonText: 'Cancel'
    }).then(function () {
      this.submit().then(response => {
        this.reset();
        swal({
          title: 'Success!',
          text: 'This function is not implemented yet.',
          type: 'success'
        }).then(function () {
          this.router.navigateByUrl('recommendation/' + this.recommendation.id);
        }.bind(this));
      });
    }.bind(this), function (dismiss) {
      if (dismiss === 'cancel') {

      }
    });
  }

  ngOnInit() {
  }
}
