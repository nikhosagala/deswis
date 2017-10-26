import {Inject, Injectable} from "@angular/core";
import {Http, RequestMethod, RequestOptions, URLSearchParams} from "@angular/http";

import "rxjs/add/operator/toPromise";

import {API_ROUTES} from "../components/utils/routes";
import {CrudService} from "./crud.service";
import {GlobalErrorHandler} from "./error.handler";

@Injectable()
export class RecommendationService extends CrudService {

  constructor(@Inject(Http) http?: Http, @Inject(GlobalErrorHandler) handleError?: GlobalErrorHandler) {
    super(http, handleError);
    this.urlAPI = API_ROUTES.RECOMMENDATION;
  }

  public getDestinations(id: number, filter?, sort?, limit?, offset?): Promise<any> {
    let params: URLSearchParams = new URLSearchParams();

    if (filter) {
      filter = JSON.stringify(filter);
      params.set('filter', filter);
    }

    if (limit && offset) {
      params.set('limit', limit);
      params.set('offset', offset);
    }

    if (sort) {
      params.set('sort', JSON.stringify(sort));
    }

    let options: RequestOptions = new RequestOptions({
      search: params,
      headers: super.getHeaders(RequestMethod.Get)
    });

    return this.http.get(this.urlAPI.GET_DEST + id, options)
    .toPromise()
    .then(response => response.json())
    .catch(this.handleError.bind(this));
  }

  public latest(object): Promise<any> {
    return this.http.post(this.urlAPI.LATEST, JSON.stringify(object), {
      headers: this.getHeaders(RequestMethod.Post)
    })
      .toPromise()
      .then(response => response.json())
      .catch(this.handleError.bind(this));
  }

  public saveSurvey(object): Promise<any> {

    return this.http.post(this.urlAPI.SAVE_SURVEY, JSON.stringify(object), {
      headers: this.getHeaders(RequestMethod.Post)
    })
      .toPromise()
      .then(response => response.json())
      .catch(this.handleError.bind(this));
  }
}
