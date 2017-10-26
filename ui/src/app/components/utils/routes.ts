export const API_ROUTES = {

  APPLICATION: {
    UPLOAD: 'upload',
    RESET: 'resetData',
    INSERT: 'insertData',
    LATEST: 'latestUpdate'
  },

  DESTINATION: {
    GET: 'api/destinations.json',
    DELETE: 'api/destination/', // WITH_ID
    GET_ID: 'api/destination/', // WITH_ID
    SAVE: 'api/destination/submit',
    UPDATE: 'api/destination/update',
    GET_BY_CATEGORY: 'api/destination/category/', // WITH_ID
  },

  CATEGORIES: {
    GET: 'api/categories.json',
    DELETE: 'api/category/', // WITH_ID
    GET_ID: 'api/category/', // WITH_ID
    SAVE: 'api/category/submit',
    UPDATE: 'api/category/update',
    GET_CHILD: 'api/category/child/node'
  },

  GOOGLE_PLACE: {
    GET: 'api/google_places.json',
    DELETE: 'api/googlePlace/', // WITH_ID
    GET_ID: 'api/googlePlace/', // WITH_ID
    SAVE: 'api/googlePlace/submit',
    UPDATE: 'api/googlePlace/update'
  },

  RECOMMENDATION: {
    DELETE: 'api/recommendation/', // WITH_ID
    GET_ID: 'api/recommendation/', // WITH_ID
    SAVE: 'api/recommendation/submit',
    UPDATE: 'api/recommendation/update',
    LATEST: 'api/recommendation/latest',
    GET_DEST: 'api/recommendation/destination/' // WITH_ID
  },

  SURVEY: {
    SAVE: 'api/survey/submit',
    UPDATE: 'api/survey/update',
    GET_ID: 'api/survey/', // WITH_ID
  },

};
