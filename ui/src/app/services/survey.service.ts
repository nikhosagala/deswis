import { Inject, Injectable } from '@angular/core';
import { Http } from '@angular/http';

import 'rxjs/add/operator/toPromise';

import { API_ROUTES } from '../components/utils/routes';
import { CrudService } from './crud.service';
import { GlobalErrorHandler } from './error.handler';

@Injectable()
export class SurveyService extends CrudService {
  constructor(@Inject(Http) http?: Http, @Inject(GlobalErrorHandler) handleError?: GlobalErrorHandler) {
    super(http, handleError);
    this.urlAPI = API_ROUTES.SURVEY;
  }
}
