const axios = require('axios');

const SERVER_URL = process.env.REACT_APP_SERVER_URL.trim();

const args = process.argv.slice(2);

const AMOUNT = args[0] ? args[0].trim() : 10;

const createUser = async (userBoundary) => {
  const url = `${SERVER_URL}/acs/users`;
  userBoundary.role = 'PLAYER';

  const data = JSON.stringify(userBoundary);

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

for (let i = 0; i < AMOUNT; i++) {
  createUser({
    avatar: `${i}`,
    email: `mockuser${i}@example.com`,
    username: `mockuser${i}`,
  });
}
