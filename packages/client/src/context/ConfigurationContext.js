import React, { Component } from 'react';
import axios from 'axios';
import {
  ActionBoundaryWithoutId,
  ActionTypes,
} from './../models/ActionBoundary';

const ConfigurationContext = React.createContext();

const SERVER_URL = process.env.REACT_APP_SERVER_URL.trim();

class ConfigurationProvider extends Component {
  state = {
    showMenu: false,
    darkMode: false,
  };

  displayMenu = () => {
    this.setState({ showMenu: true });
  };

  hideMenu = () => {
    this.setState({ showMenu: false });
  };

  isDarkMode = () => {
    return this.state.darkMode;
  };

  getUserTheme = (isDarkMode) => {
    if (isDarkMode) return 'Dark';
    else return 'General';
  };

  isDarkMode = (themeFromServer) => {
    if (themeFromServer === 'Dark') return true;
    else return false;
  };

  getMapTheme = async (userEmail) => {
    const url = `${SERVER_URL}/acs/actions`;

    // Try to get the map-theme-mode from the server:
    try {
      // Setup the data to be send:
      let actionToInvoke = new ActionBoundaryWithoutId();
      actionToInvoke.type = ActionTypes.USER_PREFERENCES;
      actionToInvoke.invokedBy = userEmail;

      // Empty attributes represent request for preferences:
      actionToInvoke.actionAttributes = {};

      // Preform API request to the server:
      const response = await axios.post(url, actionToInvoke);

      // Set the context data:
      const userPreferencesResponse = response.data;
      const preferences = userPreferencesResponse.actionAttributes.Preferences;
      const darkMode = this.isDarkMode(preferences.theme);
      this.setState({ darkMode });
    } catch (error) {
      // If there are no preferences for this user:
      if (error.response && error.response.status === 500)
        return this.changeDarkMode(false, userEmail);
      if (error.response && error.response.status === 404) return false;
      console.log(error);
    }
  };

  changeDarkMode = async (isDarkMode, userEmail) => {
    this.setState({ darkMode: isDarkMode });

    try {
      const url = `${SERVER_URL}/acs/actions`;
      const userTheme = this.getUserTheme(isDarkMode);

      // Setup the data to be send:
      let actionToInvoke = new ActionBoundaryWithoutId();
      actionToInvoke.type = ActionTypes.USER_PREFERENCES;
      actionToInvoke.invokedBy = userEmail;
      actionToInvoke.actionAttributes = {
        theme: userTheme,
      };

      // Preform API request to the server:
      const response = await axios.post(url, actionToInvoke);

      // Set the context data:
      const userPreferencesActionResponse = response.data;
      // TODO: is it necessary to store the action-ID ?
      //const userLocationElementId = userPreferencesActionResponse.element.elementId;
    } catch (error) {
      if (error.response && error.response.status === 404) return false;
      console.log(error);
    }
  };

  render() {
    return (
      <ConfigurationContext.Provider
        value={{
          ...this.state,
          displayMenu: this.displayMenu,
          hideMenu: this.hideMenu,
          changeDarkMode: this.changeDarkMode,
          getMapTheme: this.getMapTheme,
        }}
      >
        {this.props.children}
      </ConfigurationContext.Provider>
    );
  }
}

const ConfigurationConsumer = ConfigurationContext.Consumer;
export { ConfigurationProvider, ConfigurationConsumer };
