import React from 'react';
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';
import IconButton from '@material-ui/core/IconButton';
import MenuIcon from '@material-ui/icons/Menu';
import AccountCircle from '@material-ui/icons/AccountCircle';
import MenuItem from '@material-ui/core/MenuItem';
import Menu from '@material-ui/core/Menu';
import { ConfigurationConsumer } from '../../context/ConfigurationContext';
import { UserConsumer } from '../../context/UserContext';

class AppNavbar extends React.Component {
  constructor() {
    super();
    this.state = {
      isAuthenticated: true,
      anchorEl: null,
    };
  }

  handleMenu = (event) => {
    this.setState({ anchorEl: event.currentTarget });
  };

  handleClose = () => {
    this.setState({ anchorEl: null });
  };

  render() {
    return (
      <React.Fragment>
        <AppBar className="flex-grow" position="static">
          <Toolbar>
            <ConfigurationConsumer>
              {(context) => {
                const { displayMenu } = context;
                return (
                  <IconButton
                    edge="start"
                    className="mr-2"
                    color="inherit"
                    aria-label="menu"
                    onClick={() => displayMenu()}
                  >
                    <MenuIcon />
                  </IconButton>
                );
              }}
            </ConfigurationConsumer>
            <Typography variant="h6" className="flex-grow">
              Crowd Detector
            </Typography>
            <UserConsumer>
              {(context) => {
                const { isAuthenticated } = context;
                return (
                  <IconButton
                    aria-label="account of current user"
                    aria-controls="menu-appbar"
                    aria-haspopup="true"
                    onClick={(event) => {
                      if (isAuthenticated) this.handleMenu(event);
                    }}
                    color="inherit"
                  >
                    <AccountCircle />
                  </IconButton>
                );
              }}
            </UserConsumer>

            <Menu
              id="menu-appbar"
              anchorEl={this.state.anchorEl}
              anchorOrigin={{
                vertical: 'top',
                horizontal: 'right',
              }}
              keepMounted
              transformOrigin={{
                vertical: 'top',
                horizontal: 'right',
              }}
              open={this.state.anchorEl ? true : false}
              onClose={() => this.handleClose()}
            >
              <MenuItem onClick={this.handleClose}>Profile</MenuItem>
              <MenuItem onClick={this.handleClose}>My account</MenuItem>
            </Menu>
          </Toolbar>
        </AppBar>
      </React.Fragment>
    );
  }
}

export default AppNavbar;
