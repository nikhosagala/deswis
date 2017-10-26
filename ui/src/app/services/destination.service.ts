import {Inject, Injectable} from "@angular/core";
import {Http, RequestMethod, RequestOptions, URLSearchParams} from "@angular/http";

import {API_ROUTES} from "../components/utils/routes";
import {CrudService} from "./crud.service";

import "rxjs/add/operator/catch";
import "rxjs/add/operator/map";
import "rxjs/add/operator/toPromise";
import {GlobalErrorHandler} from "./error.handler";

@Injectable()
export class DestinationService extends CrudService {

  constructor(@Inject(Http) http?: Http, @Inject(GlobalErrorHandler) handleError?: GlobalErrorHandler) {
    super(http, handleError);
    this.urlAPI = API_ROUTES.DESTINATION;
  }

  public getDestinationByCategory(id: number, filter?, sort?, limit?, offset?) {
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

    return this.http.get(this.urlAPI.GET_BY_CATEGORY + id, options)
    .toPromise()
    .then(response => response.json())
    .catch(super.handleError.bind(this));
  }
}
