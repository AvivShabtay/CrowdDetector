import React, { Component } from 'react';
import { Map, Marker, Popup, TileLayer, Circle } from 'react-leaflet';
import {
  Switch,
  IconButton,
  Tooltip,
  MenuItem,
  Select,
} from '@material-ui/core';
import AdjustIcon from '@material-ui/icons/Adjust';
import LocationOn from '@material-ui/icons/LocationOn';
import LocationOff from '@material-ui/icons/LocationOff';
import LocalFlorist from '@material-ui/icons/LocalFlorist';
import { LatLng } from 'leaflet';
import { UserConsumer } from '../../context/UserContext';
import { ConfigurationConsumer } from '../../context/ConfigurationContext';

/**
 * Controller to the MapView component.
 * Organize the BL of the UI components and integrates if with the application state (context).
 */
export default class MapController extends Component {
  constructor() {
    super();
    this.state = {
      userLocation: {
        lat: 0,
        lng: 0,
      },
      isLocationIsOn: false,
      zoom: 16,
      radius: 100,
      searchRadius: 100,
    };
  }

  async componentDidMount() {
    await this.setUserCurrentLocation();
  }

  /* Wrapper function to promisify the getCurrentPosition function.  */
  getPosition = () => {
    return new Promise(function (resolve, reject) {
      navigator.geolocation.getCurrentPosition(resolve, reject);
    });
  };

  /* Get user geolocation based on the device GPS. */
  setUserCurrentLocation = async () => {
    if (navigator.geolocation) {
      const position = await this.getPosition();
      let userCurrentLocation = { ...this.state.userLocation };
      userCurrentLocation.lat = position.coords.latitude;
      userCurrentLocation.lng = position.coords.longitude;

      this.setState({ userLocation: userCurrentLocation });
      this.setState({ isLocationIsOn: true });
    }
  };

  /* Set searchRadius to define how wide to search for players. */
  setSearchRadius = (event, fetchUserEnvironment) => {
    const searchRadius = event.target.value;
    const userLocation = this.state.userLocation;
    this.setState({ searchRadius });
    fetchUserEnvironment(userLocation, searchRadius);
  };

  setUserRadius = (event) => {
    const zoomLevel = event.target.getZoom();
    if (zoomLevel >= 15) {
      return this.setState({ radius: 100 });
    }
    if (zoomLevel >= 12) {
      return this.setState({ radius: 1000 });
    }
    if (zoomLevel >= 8) {
      return this.setState({ radius: 10000 });
    }
    this.setState({ radius: 100000 });
  };

  render() {
    return (
      <ConfigurationConsumer>
        {(configContext) => {
          const { darkMode, changeDarkMode, getMapTheme } = configContext;
          return (
            <UserConsumer>
              {(context) => {
                const {
                  email,
                  setLocationElement,
                  locationElement,
                  isAuthenticated,
                  notifyLocation,
                  fetchUserEnvironment,
                  userEnvironment,
                } = context;
                if (isAuthenticated)
                  return (
                    <MapView
                      setLocationElement={setLocationElement}
                      notifyLocation={notifyLocation}
                      locationElement={locationElement}
                      isAuthenticated={isAuthenticated}
                      fetchUserEnvironment={fetchUserEnvironment}
                      userEnvironment={userEnvironment}
                      userLocation={this.state.userLocation}
                      setUserCurrentLocation={this.setUserCurrentLocation}
                      isLocationIsOn={this.state.isLocationIsOn}
                      darkMode={darkMode}
                      changeDarkMode={changeDarkMode}
                      setSearchRadius={this.setSearchRadius}
                      setUserRadius={this.setUserRadius}
                      zoom={this.state.zoom}
                      radius={this.state.radius}
                      searchRadius={this.state.searchRadius}
                      userEmail={email}
                      getMapTheme={getMapTheme}
                    ></MapView>
                  );
                else
                  return (
                    <React.Fragment>
                      <br />
                      <div className="max-h-full my-10">
                        <LocalFlorist />
                        <p className="flex-1 m-auto">Please sign-in first.</p>
                      </div>
                    </React.Fragment>
                  );
              }}
            </UserConsumer>
          );
        }}
      </ConfigurationConsumer>
    );
  }
}

class MapView extends Component {
  constructor(props) {
    super(props);
    this.state = {
      numOfPeople: 1,
      radiusColor: '',
      environmentUsers: [],
    };
    // Create reference to the Leaflet map element:
    this.mapRef = React.createRef();
  }

  updateUserEnvironment = async (
    userLocation,
    searchRadius,
    userEnvironment
  ) => {
    await this.props.fetchUserEnvironment(userLocation, searchRadius);
    await this.calculateUserEnvironment(userEnvironment);
  };

  async componentDidMount() {
    await this.props.setUserCurrentLocation();
    await this.props.getMapTheme(this.props.userEmail);
    const { userLocation, searchRadius } = this.props;
    await this.props.notifyLocation(userLocation);
    this.updateUserEnvironment(
      userLocation,
      searchRadius,
      this.props.userEnvironment
    );

    this.interval = setInterval(() => {
      const { searchRadius } = this.props;
      this.updateUserEnvironment(
        userLocation,
        searchRadius,
        this.props.userEnvironment
      );
    }, 2000);
  }

  componentWillUnmount() {
    clearInterval(this.interval);
  }

  /* Handle user requests to recenter the map. */
  handleAdjustment = () => {
    const userPosition = [
      this.props.userLocation.lat,
      this.props.userLocation.lng,
    ];
    this.map.leafletElement.setView(userPosition);
  };

  /* Check if location (LatLng object) exist in array */
  isLocationExist(locationArray, location) {
    for (let i = 0; i < locationArray.length; i++) {
      const tempLocation = locationArray[i];
      if (
        tempLocation.lat === location.lat &&
        tempLocation.lng === location.lng
      ) {
        return true;
      }
    }
    return false;
  }

  /* Handle the UI settings for the circle of the radius on the map. */
  calculateUserEnvironment = (userEnvironment) => {
    // Check if the data is empty:
    if (!Array.isArray(userEnvironment) && !userEnvironment.length) {
      return;
    }

    let users = [];
    // Check how much people near the user, in the defined radius:
    userEnvironment.map((user) => {
      const { lat, lng } = user;
      const userLocation = new LatLng(lat, lng);

      // Check if the user in the radius:
      const isInRadius = this.distanceFromCenter(
        userLocation,
        this.props.radius
      );

      if (isInRadius) {
        users.push(userLocation);
      }
      return true;
    });
    const numOfPeople = users.length + 1;
    this.setState({ environmentUsers: users });
    this.setState({ numOfPeople });

    // Setup the radius color:
    const color = this.getRadiusColor(this.state.numOfPeople);
    this.setState({ radiusColor: color });
  };

  /* Return the hex-color according to the number of people */
  getRadiusColor = (numOfPeopleInRadius) => {
    if (numOfPeopleInRadius <= 2) return '#33A5FF'; /*blue*/
    if (numOfPeopleInRadius <= 4) return '#FFBB33'; /*orange*/
    if (numOfPeopleInRadius > 5) return '#FF4233'; /*red*/
  };

  /**
   * Accept two LatLng objects that represents locations (Leaflet object) and
   * check if the distance between them is less or equal to the given radius
   * and update the state with the number of people in the defined radius.
   */
  distanceFromCenter = (location, radius) => {
    const userLocation = new LatLng(
      this.props.userLocation.lat,
      this.props.userLocation.lng
    );
    if (userLocation.distanceTo(location) <= radius) {
      // const numOfPeople = this.state.numOfPeople;
      // this.setState({ numOfPeople: numOfPeople + 1 });
      return true;
    }
  };

  render() {
    const userPosition = [
      this.props.userLocation.lat,
      this.props.userLocation.lng,
    ];
    return (
      <React.Fragment>
        <Tooltip title="Search Radius (m)" arrow>
          <Select
            labelId="radius-label"
            id="radius-id"
            value={this.props.searchRadius}
            onChange={(event) =>
              this.props.setSearchRadius(event, this.props.fetchUserEnvironment)
            }
          >
            <MenuItem value={100}>100m</MenuItem>
            <MenuItem value={1000}>1000m</MenuItem>
            <MenuItem value={10000}>10000m</MenuItem>
            <MenuItem value={1000000}>1000000m</MenuItem>
          </Select>
        </Tooltip>
        <Tooltip title="Dark theme" arrow>
          <Switch
            checked={this.props.darkMode}
            onChange={(event) =>
              this.props.changeDarkMode(
                event.target.checked,
                this.props.userEmail
              )
            }
            name="darkMode"
            color="primary"
            label="Active Dark Mode"
          />
        </Tooltip>
        <Tooltip title="Center location" arrow>
          <IconButton onClick={() => this.handleAdjustment()}>
            <AdjustIcon />
          </IconButton>
        </Tooltip>

        {this.props.isLocationIsOn && (
          <Tooltip title="Update location" arrow>
            <IconButton onClick={() => this.props.setUserCurrentLocation()}>
              <LocationOn />
            </IconButton>
          </Tooltip>
        )}

        {!this.props.isLocationIsOn && (
          <Tooltip title="Location is disabled" arrow>
            <IconButton>
              <LocationOff />
            </IconButton>
          </Tooltip>
        )}

        <Map
          ref={(ref) => {
            // keep reference of the map element:
            this.map = ref;
          }}
          animate={true}
          center={this.props.userLocation}
          zoom={this.props.zoom}
          style={{ width: '80%', height: '550px' }}
          className="m-auto"
          // onzoomend={this.props.setUserRadius}
        >
          <TileLayer
            attribution='&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
            url={
              this.props.darkMode
                ? 'https://cartodb-basemaps-{s}.global.ssl.fastly.net/dark_all/{z}/{x}/{y}.png'
                : 'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png'
            }
          />
          {this.props.userEnvironment.map(
            ({ id, lat, lng, username, email }) => {
              const position = { lat, lng };
              return (
                <Marker key={id} riseOnHover={true} position={position}>
                  <Popup key={id}>{username}</Popup>
                </Marker>
              );
            }
          )}
          <Marker riseOnHover={true} position={userPosition}>
            <Popup>Me</Popup>
          </Marker>
          <Circle
            key={'User'}
            fillOpacity={0.1}
            center={userPosition}
            radius={this.props.radius}
            color={this.state.radiusColor}
          />
        </Map>
      </React.Fragment>
    );
  }
}
