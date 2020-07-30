const axios = require('axios');
const { generateRandomPoint } = require('./geo');

const SERVER_URL = process.env.REACT_APP_SERVER_URL.trim();

const args = process.argv.slice(2);

const LAT = args[0] ? args[0].trim() : 32.4675101;
const LNG = args[1] ? args[1].trim() : 34.9547147;

const RADIUS = args[2] ? args[2].trim() : 100;
const SPEED = args[3] ? args[3].trim() : 2000;
const AMOUNT = args[4] ? args[4].trim() : 10;

(() => {
  const notifyLocation = async (location, invokedBy) => {
    const url = `${SERVER_URL}/acs/actions`;

    const actionToInvoke = {
      type: 'userLocation',
      invokedBy,
      actionAttributes: {
        lat: location.lat,
        lng: location.lng,
      },
    };

    const data = JSON.stringify(actionToInvoke);

    const config = {
      method: 'post',
      url,
      headers: {
        'Content-Type': 'application/json',
      },
      data,
    };

    try {
      const response = await axios(config);
      console.log(JSON.stringify(response.data));
    } catch (error) {
      console.log(error);
    }
  };

  let i = 0;
  setInterval(() => {
    if (i === Number(AMOUNT)) {
      i = 0;
    }
    const point = generateRandomPoint({ lat: LAT, lng: LNG }, RADIUS);

    notifyLocation(
      { lat: point.lat, lng: point.lng },
      `mockuser${i}@example.com`
    );
    i++;
  }, SPEED);
})();
