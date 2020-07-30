import React from 'react';
import SwipeableDrawer from '@material-ui/core/SwipeableDrawer';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';
import ExitToApp from '@material-ui/icons/ExitToApp';
import LockOpen from '@material-ui/icons/LockOpen';
import PersonAdd from '@material-ui/icons/PersonAdd';
import PersonPinCircle from '@material-ui/icons/PersonPinCircle';
import { ConfigurationConsumer } from '../../context/ConfigurationContext';
import { UserConsumer } from '../../context/UserContext';
import { Link } from 'react-router-dom';

export default function SwipeableMenu(props) {
  const buttons = { DASHBOARD: 0, SIGN_IN: 1, SIGN_UP: 2, LOG_OUT: 3 };

  return (
    <React.Fragment>
      <ConfigurationConsumer>
        {(context) => {
          const { showMenu, hideMenu, displayMenu } = context;
          return (
            <SwipeableDrawer
              anchor="left"
              open={showMenu}
              onClose={() => hideMenu()}
              onOpen={() => displayMenu()}
            >
              <List className="w-40">
                <Link to="/" onClick={() => hideMenu()}>
                  <ListItem button key={buttons.DASHBOARD}>
                    <PersonPinCircle />
                    <ListItemText primary="Map" className="ml-4" />
                  </ListItem>
                </Link>
                <Link to="/signin" onClick={() => hideMenu()}>
                  <ListItem button key={buttons.SIGN_IN}>
                    <LockOpen />
                    <ListItemText primary="Sign in" className="ml-4" />
                  </ListItem>
                </Link>
                <Link to="/signup" onClick={() => hideMenu()}>
                  <ListItem button key={buttons.SIGN_UP}>
                    <PersonAdd />
                    <ListItemText primary="Sign up" className="ml-4" />
                  </ListItem>
                </Link>
                <UserConsumer>
                  {(context) => {
                    const { logoutUser } = context;
                    return (
                      <Link
                        to="/"
                        onClick={() => {
                          logoutUser();
                          hideMenu();
                        }}
                      >
                        <ListItem button key={buttons.LOG_OUT}>
                          <ExitToApp />
                          <ListItemText primary="Log out" className="ml-4" />
                        </ListItem>
                      </Link>
                    );
                  }}
                </UserConsumer>
              </List>
            </SwipeableDrawer>
          );
        }}
      </ConfigurationConsumer>
    </React.Fragment>
  );
}
