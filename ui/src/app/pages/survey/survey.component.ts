import { Component, OnInit } from '@angular/core';
import { Survey } from 'app/components/utils/models';

import { default as swal } from 'sweetalert2';
import { ApplicationService } from '../../services/application.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { SurveyService } from '../../services/survey.service';
import { ActivatedRoute, Params, Router } from '@angular/router';

@Component({
  selector: 'survey',
  templateUrl: './survey.component.html',
})

export class SurveyComponent implements OnInit {
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
              private router: ActivatedRoute,
              private route: Router,
              private surveyService: SurveyService,
              private formBuilder: FormBuilder) {
  };

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
  };

  onSubmit() {
    this.submitted = true;
    let id = this.survey.id;
    this.survey = Object.assign({}, this.surveyForm.value);
    this.survey.id = id;
    this.update(this.survey);
    swal({
      title: 'Informasi',
      text: 'Terima kasih ' + this.survey.name + '  sudah menggunakan aplikasi ini.',
      type: 'success'
    }).then(function () {
      this.route.navigateByUrl('dashboard').then(() => console.log('Thank you so much. By Nikho Sagala (nikhosagala@gmail.com)'));
    }.bind(this));
  }

  update(update: any) {
    this.surveyService.update(update).then(response => {
      this.survey = response.data;
    })
  }

  submit(submit: any) {
    this.surveyService.save(submit).then(response => {
      this.survey = response.data;
    })
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
  };

  ngOnInit() {
    this.router.params.subscribe((params: Params) => {
      this.surveyService.find(+params['id']).then(response => {
        this.survey = response.data;
        this.applicationService.setSurvey(this.survey);
      }).then(() => this.buildForm());
    });
    this.buildForm();
  }
}
