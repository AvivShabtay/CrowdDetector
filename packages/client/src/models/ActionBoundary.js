const ActionTypes = {
  USER_LOCATION: 'userLocation',
  USER_ENVIRONMENT: 'userEnvironment',
  USER_PREFERENCES: 'userPreferences',
};

class ActionBoundary {
  constructor() {
    this.actionId = '';
    this.type = '';
    this.element = {
      elementId: '',
    };
    this.createdTimestamp = '';
    this.invokedBy = '';
    this.actionAttributes = {};
  }
}

class ActionBoundaryWithoutId {
  constructor() {
    this.type = '';
    this.invokedBy = '';
    this.actionAttributes = {};
  }
}

export { ActionBoundary, ActionBoundaryWithoutId, ActionTypes };
