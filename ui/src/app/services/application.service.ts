import { Inject, Injectable } from '@angular/core';
import { Http, RequestMethod } from '@angular/http';

import 'rxjs/add/operator/toPromise';

import { API_ROUTES } from '../components/utils/routes';
import { CrudService } from './crud.service';
import { GlobalErrorHandler } from './error.handler';
import {
  Location,
  Recommendation,
  RecommendationDetail,
  Setting,
  Survey
} from '../components/utils/models';

@Injectable()
export class ApplicationService extends CrudService {
  private _location: Location;
  private _setting: Setting;
  private _survey: Survey;
  private _recommendation: Recommendation;
  private _recommendationDetails: RecommendationDetail[];

  constructor(@Inject(Http) http?: Http, @Inject(GlobalErrorHandler) handleError?: GlobalErrorHandler) {
    super(http, handleError);
    this.urlAPI = API_ROUTES.APPLICATION;
  }

  public getLatestUpdate(): Promise<any> {
    return this.http.get(this.urlAPI.LATEST, {
      headers: this.getHeaders(RequestMethod.Get)
    })
      .toPromise()
      .then(response => response.json())
      .catch(super.handleError.bind(this))
  }

  public setSetting(data: Setting) {
    this._setting = data;
  }

  public getSetting(): Setting {
    return this._setting;
  }

  public setLocation(data: Location) {
    this._location = data;
  }

  public getLocation(): Location {
    return this._location;
  }

  public setRecommendationDetail(data: RecommendationDetail[]) {
    this._recommendationDetails = data;
  }

  public getRecommendationDetail(): RecommendationDetail[] {
    return this._recommendationDetails;
  }

  public addRecommendationDetail(data: RecommendationDetail) {
    this._recommendationDetails.push(new RecommendationDetail(data.interestValue, data.category));
  }

  public setRecommendation(recommendation: Recommendation) {
    this._recommendation = recommendation;
  }

  public getRecommendation(): Recommendation {
    return this._recommendation;
  }

  public setSurvey(survey: Survey) {
    this._survey = survey;
  }

  public getSurvey(): Survey {
    return this._survey;
  }
}
