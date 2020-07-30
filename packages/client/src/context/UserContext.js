import React, { Component } from 'react';
import axios from 'axios';
import {
  ActionBoundaryWithoutId,
  ActionTypes,
} from './../models/ActionBoundary';

const UserContext = React.createContext();

const SERVER_URL = process.env.REACT_APP_SERVER_URL.trim();

class UserProvider extends Component {
  constructor(props) {
    super(props);
    this.state = {
      email: JSON.parse(window.localStorage.getItem('userEmail')) || '',
      role: JSON.parse(window.localStorage.getItem('userRole')) || '',
      username: JSON.parse(window.localStorage.getItem('username')) || '',
      avatar: JSON.parse(window.localStorage.getItem('userAvatar')) || '',
      isAuthenticated:
        JSON.parse(window.localStorage.getItem('isAuthenticated')) || '',
      userLocationElementId:
        JSON.parse(window.localStorage.getItem('userLocationElementId')) || '',
      userEnvironment:
        JSON.parse(window.localStorage.getItem('userEnvironment')) || [],
    };
  }

  setEmail = (email) => {
    this.setState({ email });
    window.localStorage.setItem('userEmail', JSON.stringify(email));
  };

  setRole = (role) => {
    this.setState({ role });
    window.localStorage.setItem('userRole', JSON.stringify(role));
  };

  setUserName = (username) => {
    this.setState({ username });
    window.localStorage.setItem('username', JSON.stringify(username));
  };

  setAvatar = (avatar) => {
    this.setState({ avatar });
    window.localStorage.setItem('userAvatar', JSON.stringify(avatar));
  };

  setIsAuthenticated = (value) => {
    this.setState({ isAuthenticated: value });
    window.localStorage.setItem('isAuthenticated', JSON.stringify(value));
  };

  setUserLocationElementId = (userLocationElementId) => {
    this.setState({ userLocationElementId });
    window.localStorage.setItem(
      'userLocationElementId',
      JSON.stringify(userLocationElementId)
    );
  };

  setUserEnvironment = (users) => {
    // Check if there are no users available:
    if (!Array.isArray(users) && !users.length) {
      // TODO: replace with application logger.
      console.log(`[-] Empty users for this client.`);
      this.setState({ userEnvironment: [] });
      return;
    }

    let userEnvironment = [];
    users.forEach((user) => {
      // TODO: Add user model.
      const id = user.elementId;
      const lat = user.location.lat;
      const lng = user.location.lng;
      const email = user.elementAttributes.userProperties.email;
      const role = user.elementAttributes.userProperties.role;
      const username = user.elementAttributes.userProperties.username;
      const avatar = user.elementAttributes.userProperties.avatar;
      userEnvironment.push({ id, lat, lng, email, role, username, avatar });
    });
    this.setState({ userEnvironment });
    window.localStorage.setItem(
      'userEnvironment',
      JSON.stringify(userEnvironment)
    );
  };

  loginUser = async (email) => {
    try {
      const url = `${SERVER_URL}/acs/users/login/${email}`;

      // Preform API request to the server:
      const response = await axios.get(url);
      const userBoundaryResponse = response.data;

      // Set the context data:
      this.setAvatar(userBoundaryResponse.avatar);
      this.setEmail(userBoundaryResponse.email);
      this.setRole(userBoundaryResponse.role);
      this.setUserName(userBoundaryResponse.username);
      this.setIsAuthenticated(true);

      return userBoundaryResponse;
    } catch (error) {
      if (error.response && error.response.status === 404) return false;
      console.log(error);
    }
  };

  logoutUser = () => {
    //Set the context data:
    this.setAvatar('');
    this.setEmail('');
    this.setRole('');
    this.setUserName('');
    this.setIsAuthenticated(false);
    this.setUserLocationElementId('');
    this.setUserEnvironment([]);
  };

  createUser = async (userBoundary) => {
    try {
      const url = `${SERVER_URL}/acs/users`;

      userBoundary.role = 'PLAYER';

      // Preform API request to the server:
      const response = await axios.post(url, userBoundary);
      const userBoundaryResponse = response.data;

      // Set the context data:
      this.setAvatar(userBoundaryResponse.avatar);
      this.setEmail(userBoundaryResponse.email);
      this.setRole(userBoundaryResponse.role);
      this.setUserName(userBoundaryResponse.username);
      this.setIsAuthenticated(true);

      return userBoundaryResponse;
    } catch (error) {
      if (error.response && error.response.status === 404) return false;
      console.log(error);
    }
  };

  notifyLocation = async (location) => {
    try {
      const url = `${SERVER_URL}/acs/actions`;

      // Setup the data to be send:
      let actionToInvoke = new ActionBoundaryWithoutId();
      actionToInvoke.type = ActionTypes.USER_LOCATION;
      actionToInvoke.invokedBy = this.state.email;
      actionToInvoke.actionAttributes = {
        lat: location.lat,
        lng: location.lng,
      };

      // Preform API request to the server:
      const response = await axios.post(url, actionToInvoke);

      // Set the context data:
      const userLocationActionResponse = response.data;
      const userLocationElementId =
        userLocationActionResponse.element.elementId;
      this.setUserLocationElementId(userLocationElementId);

      return true;
    } catch (error) {
      if (error.response && error.response.status === 404) return false;
      console.log(error);
    }
  };

  fetchUserEnvironment = async (location, radius) => {
    try {
      const url = `${SERVER_URL}/acs/actions`;

      // Setup the data to be send:
      let actionToInvoke = new ActionBoundaryWithoutId();
      actionToInvoke.type = ActionTypes.USER_ENVIRONMENT;
      actionToInvoke.invokedBy = this.state.email;
      actionToInvoke.actionAttributes = {
        lat: location.lat,
        lng: location.lng,
        radius,
      };

      // Preform API request to the server:
      const response = await axios.post(url, actionToInvoke);

      // Set the context data:
      const responseData = response.data;
      const users = responseData.actionAttributes.users;
      this.setUserEnvironment(users);

      return true;
    } catch (error) {
      if (error.response && error.response.status === 404) return false;
      console.log(error);
    }
  };

  render() {
    return (
      <UserContext.Provider
        value={{
          ...this.state,
          setEmail: this.setEmail,
          setRole: this.setRole,
          setUserName: this.setUserName,
          setAvatar: this.setAvatar,
          loginUser: this.loginUser,
          createUser: this.createUser,
          logoutUser: this.logoutUser,
          notifyLocation: this.notifyLocation,
          fetchUserEnvironment: this.fetchUserEnvironment,
        }}
      >
        {this.props.children}
      </UserContext.Provider>
    );
  }
}

const UserConsumer = UserContext.Consumer;
export { UserProvider, UserConsumer };
