import React from 'react';
import Avatar from '@material-ui/core/Avatar';
import Button from '@material-ui/core/Button';
import CssBaseline from '@material-ui/core/CssBaseline';
import TextField from '@material-ui/core/TextField';
import Link from '@material-ui/core/Link';
import Grid from '@material-ui/core/Grid';
import LockOutlinedIcon from '@material-ui/icons/LockOutlined';
import Typography from '@material-ui/core/Typography';
import Container from '@material-ui/core/Container';
import { UserConsumer } from '../../context/UserContext';
import UserBoundary from '../../models/UserBoundary';

class SignUp extends React.Component {
  constructor() {
    super();
    this.state = { emailAddress: '', userName: '', avatar: '' };
  }

  handleUserName(name) {
    this.setState({ userName: name });
  }

  handleEmailAddress(email) {
    this.setState({ emailAddress: email });
  }

  handleAvatar(avatar) {
    this.setState({ avatar: avatar });
  }

  render() {
    return (
      <UserConsumer>
        {(context) => {
          const { createUser } = context;
          return (
            <React.Fragment>
              <Container component="main" maxWidth="xs">
                <CssBaseline />
                <Grid container alignItems="center">
                  <Grid item xs={12}>
                    <Avatar className="m-auto my-5">
                      <LockOutlinedIcon />
                    </Avatar>
                  </Grid>
                  <Grid item xs={12}>
                    <Typography component="h1" variant="h5">
                      Sign Up
                    </Typography>
                  </Grid>
                </Grid>
                <form noValidate>
                  <TextField
                    autoComplete="fname"
                    name="firstName"
                    variant="outlined"
                    required
                    fullWidth
                    id="firstName"
                    label="User Name"
                    autoFocus
                    value={this.state.userName}
                    onChange={(event) =>
                      this.handleUserName(event.target.value)
                    }
                  />
                  <TextField
                    variant="outlined"
                    margin="normal"
                    required
                    fullWidth
                    id="email"
                    label="Email Address"
                    name="email"
                    autoComplete="email"
                    value={this.state.emailAddress}
                    onChange={(event) =>
                      this.handleEmailAddress(event.target.value)
                    }
                  />
                  <TextField
                    variant="outlined"
                    margin="normal"
                    required
                    fullWidth
                    id="avatar"
                    label="Avatar"
                    name="Avatar"
                    autoComplete="avatar"
                    value={this.state.avatar}
                    onChange={(event) => this.handleAvatar(event.target.value)}
                  />
                  <Button
                    type="submit"
                    fullWidth
                    variant="contained"
                    color="primary"
                    onClick={async (event) => {
                      event.preventDefault();

                      // Organize the data:
                      let userBoundary = new UserBoundary();
                      userBoundary.avatar = this.state.avatar;
                      userBoundary.email = this.state.emailAddress;
                      userBoundary.username = this.state.userName;

                      // Preform the API request:
                      const user = await createUser(userBoundary);
                      if (user) this.props.history.push('/');
                    }}
                  >
                    Sign Up
                  </Button>
                  <Grid container justify="flex-end">
                    <Grid item>
                      <Link href="/signin" variant="body2">
                        Already have an account? Sign in
                      </Link>
                    </Grid>
                  </Grid>
                </form>
              </Container>
            </React.Fragment>
          );
        }}
      </UserConsumer>
    );
  }
}

export default SignUp;
