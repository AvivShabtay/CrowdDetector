# Crowd Detector

## Architecture

This application is split into a server (Java Spring) and a client (React).

## Deployments

Deploying is handled automatically on every push to Master.
The server is deployed at: https://crowd-detector.herokuapp.com/
The client is deployed at: https://crowd-detector.netlify.app/

## Testing

Unit tests on the server are run by Maven.

## Error Tracking

We use Sentry.io for error tracking:
https://sentry.io/organizations/crowd-detector/issues/?project=5251243

## Generate Mock Users + Locations

### Generate Users

To generate users you can use the node script `mock:users`
It can receive an arg for AMOUNT: `yarn mock:users 100`

### Generate Locations

To generate locations you can use the node script `mock:locations`
It can receive args for LAT, LNG, RADIUS, SPEED, AMOUNT: `yarn mock:locations 32.4675101 34.9547147 300 200 100`
