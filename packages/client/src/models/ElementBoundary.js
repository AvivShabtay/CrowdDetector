export default class ElementBoundary {
  constructor() {
    this.elementId = '';
    this.type = '';
    this.name = '';
    this.active = false;
    this.createdTimestamp = '';
    this.createdBy = {
      email: '',
    };
    this.location = {
      lat: 0,
      lng: 0,
    };
    this.elementAttributes = {};
  }
}

class ElementBoundaryWithoutId {
  constructor() {
    //this.elementId = '';
    this.type = '';
    this.name = '';
    this.active = false;
    this.createdTimestamp = '';
    this.createdBy = {
      email: '',
    };
    this.location = {
      lat: 0,
      lng: 0,
    };
    this.elementAttributes = {};
  }
}

export { ElementBoundary, ElementBoundaryWithoutId };
