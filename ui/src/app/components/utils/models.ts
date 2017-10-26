export class Category {
  name: string;
  hint: string;
}

export class Destination {
  id: number;
  name: string;
  description: string;
  rating: number;
  address: string;
  phone: string;
  monday: string;
  tuesday: string;
  wednesday: string;
  thursday: string;
  friday: string;
  saturday: string;
  sunday: string;
  lengthOfVisit: number;
  googlePlace: GooglePlace;
}

export class GooglePlace {
  id: number;
  lat: number;
  lng: number;
  imageData: string;
  contentType: string;
}

export class Recommendation {
  id: number;
  rating: number;
  name: string;
  age: number;
  processTime: number;
  threshold: number;
}

export class Survey {
  id: number;
  recommendationId: number;
  name: string;
  age: number;
  comment: string;
  familiar: string;
  pu1: string;
  pu2: string;
  pu3: string;
  pu4: string;
  pu5: string;
  pu6: string;
  pu7: string;
  eou1: string;
  eou2: string;
  eou3: string;
  eou4: string;
  eou5: string;
  tr1: string;
  tr2: string;
  tr3: string;
  tr4: string;
  pe1: string;
  pe2: string;
  pe3: string;
  pe4: string;
  bi1: string;
  bi2: string;
  bi3: string;
}

export class RecommendationDetail {
  interestValue: number;
  category: Category;

  constructor(interestValue: number, category: Category) {
    this.interestValue = interestValue;
    this.category = category;
  }
}

export class Setting {
  distanceWeight: number;
  durationWeight: number;
  interestWeight: number;
  ratingWeight: number;
  name: string;
  threshold: number;
}

export class Location {
  lat: number;
  lng: number;
}
