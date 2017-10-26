import { Component, OnInit } from '@angular/core';
import { Recommendation, Survey } from 'app/components/utils/models';

import { default as swal } from 'sweetalert2';
import { ApplicationService } from '../../services/application.service';
import { MzBaseModal } from 'ng2-materialize';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RecommendationService } from '../../services/recommendation.service';
import { SurveyService } from '../../services/survey.service';

@Component({
  selector: 'survey',
  templateUrl: './survey-modal.component.html',
})

export class SurveyModalComponent extends MzBaseModal implements OnInit {
  recommendation: Recommendation = {
    id: null,
    name: null,
    rating: null,
    threshold: null,
    age: null,
    processTime: null,
  }
  survey: Survey = {
    id: null,
    recommendationId: null,
    name: null,
    age: null,
    comment: null,
    familiar: null,
    pu1: null,
    pu2: null,
    pu3: null,
    pu4: null,
    pu5: null,
    pu6: null,
    pu7: null,
    eou1: null,
    eou2: null,
    eou3: null,
    eou4: null,
    eou5: null,
    tr1: null,
    tr2: null,
    tr3: null,
    tr4: null,
    pe1: null,
    pe2: null,
    pe3: null,
    pe4: null,
    bi1: null,
    bi2: null,
    bi3: null,
  }
  surveyForm: FormGroup;
  submitted = false;

  constructor(private applicationService: ApplicationService,
              private recommendationService: RecommendationService,
              private surveyService: SurveyService,
              private formBuilder: FormBuilder) {
    super();
  };

  // buildForm() {
  //   this.surveyForm = this.formBuilder.group({
  //     name: [this.recommendation.name, Validators.compose([
  //       Validators.required,
  //       Validators.maxLength(24)
  //     ]),
  //     ],
  //     comment: [this.recommendation.comment],
  //     rating: [this.recommendation.rating],
  //     age: [this.recommendation.age, Validators.required],
  //     familiar: [this.recommendation.familiar, Validators.required],
  //     match: [this.recommendation.match, Validators.required],
  //     speed: [this.recommendation.speed, Validators.required],
  //     interaction: [this.recommendation.interaction, Validators.required],
  //     processTime: [this.recommendation.processTime]
  //   })
  // }

  buildForm() {
    this.surveyForm = this.formBuilder.group({
      name: [this.survey.name, Validators.compose([
        Validators.required,
        Validators.maxLength(24)
      ])],
      age: [this.survey.age, Validators.required],
      comment: [this.survey.comment],
      familiar: [this.survey.familiar],
      pu1: [this.survey.pu1],
      pu2: [this.survey.pu2],
      pu3: [this.survey.pu3],
      pu4: [this.survey.pu4],
      pu5: [this.survey.pu5],
      pu6: [this.survey.pu6],
      pu7: [this.survey.pu7],
      eou1: [this.survey.eou1],
      eou2: [this.survey.eou2],
      eou3: [this.survey.eou3],
      eou4: [this.survey.eou4],
      eou5: [this.survey.eou5],
      tr1: [this.survey.tr1],
      tr2: [this.survey.tr2],
      tr3: [this.survey.tr3],
      tr4: [this.survey.tr4],
      pe1: [this.survey.pe1],
      pe2: [this.survey.pe2],
      pe3: [this.survey.pe3],
      pe4: [this.survey.pe4],
      bi1: [this.survey.bi1],
      bi2: [this.survey.bi2],
      bi3: [this.survey.bi3],
    })
  }

  onSubmit() {
    this.submitted = true;
    let _recommendation = this.applicationService.getRecommendation();
    this.survey = Object.assign({}, this.surveyForm.value);
    this.survey.recommendationId = _recommendation.id;
    this.submit(this.survey);
    swal(
      'Informasi',
      'Terima kasih ' + this.survey.name + '  sudah menggunakan aplikasi ini.',
      'success'
    )
  }

  update(update: any) {
    this.recommendationService.update(update).then(response => {
      this.recommendation = response.data;
    })
  }

  submit(submit: any) {
    this.surveyService.save(submit).then(() => {
      console.log('Thank you so much. By Nikho Sagala (nikhosagala@gmail.com)')
    })
    // this.recommendationService.saveSurvey(submit).then(response => {
    //   // console.log(response)
    // });

  }

  errorMessageResources = {
    name: {
      maxlength: 'Nama tidak boleh lebih dari 24 karakter.',
      required: 'Field nama diperlukan.',
    },
    rating: {
      required: 'Rating is required.',
    },
    age: {
      required: 'Field usia diperlukan.',
    }
  }

  ngOnInit() {
    this.survey.name = this.applicationService.getRecommendation().name;
    this.buildForm();
  }
}
