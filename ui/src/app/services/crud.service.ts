import {Inject, Injectable} from "@angular/core";
import {Headers, Http, RequestMethod, RequestOptions, URLSearchParams} from "@angular/http";
import {API_ROUTES} from "../components/utils/routes";
import {GlobalErrorHandler} from "./error.handler";

@Injectable()
export abstract class CrudService {

  protected urlAPI: any;

  constructor(@Inject(Http) protected http?: Http, @Inject(GlobalErrorHandler) protected handlingError?: GlobalErrorHandler) {
  }

  public save(object, save?): Promise<any> {
    let params: URLSearchParams = new URLSearchParams();

    if (save) {
      params.set('save', save);
    }

    return this.http.post(this.urlAPI.SAVE, JSON.stringify(object), {
      search: params,
      headers: this.getHeaders(RequestMethod.Post)
    })
      .toPromise()
      .then(response => response.json())
      .catch(this.handleError.bind(this));
  }

  public update(object): Promise<any> {
    return this.http.put(this.urlAPI.UPDATE, JSON.stringify(object), {
      headers: this.getHeaders(RequestMethod.Put)
    })
      .toPromise()
      .then(response => response.json())
      .catch(this.handleError.bind(this));
  }

  public get(filter?, sort?, limit?, offset?): Promise<any> {
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
      headers: this.getHeaders(RequestMethod.Get)
    });

    return this.http.get(this.urlAPI.GET, options)
      .toPromise()
      .then(response => response.json())
      .catch(this.handleError.bind(this));
  }

  public find(id: number): Promise<any> {
    return this.http.get(this.urlAPI.GET_ID + id, {
      headers: this.getHeaders(RequestMethod.Get)
    })
      .toPromise()
      .then(response => response.json())
      .catch(this.handleError.bind(this));
  }

  public remove(id: number): Promise<any> {
    return this.http.delete(this.urlAPI.DELETE + id, {
      headers: this.getHeaders(RequestMethod.Delete)
    })
      .toPromise()
      .then(response => response.json())
      .catch(this.handleError.bind(this));
  }

  public handleError(error: any): Promise<any> {
    this.handlingError.checkAndHandling(error);
    return Promise.reject(error.message || error);
  }

  protected getHeaders(method): Headers {
    let headers = new Headers();
    if (method === RequestMethod.Post || method === RequestMethod.Put) {
      headers.append('Content-Type', 'application/json');
    }
    return headers;
  }
}
