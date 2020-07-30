import React from 'react';
import Container from '@material-ui/core/Container';
import Button from '@material-ui/core/Button';
import TextField from '@material-ui/core/TextField';
import CssBaseline from '@material-ui/core/CssBaseline';
import Avatar from '@material-ui/core/Avatar';
import LockOutlinedIcon from '@material-ui/icons/LockOutlined';
import Typography from '@material-ui/core/Typography';
import Grid from '@material-ui/core/Grid';
import { UserConsumer } from '../../context/UserContext';

class SignIn extends React.Component {
  constructor() {
    super();
    this.state = {
      emailAddress: '',
      showPasswordError: false,
    };
  }

  /* Get new email and updates the state */
  handleEmailAddress(email) {
    this.setState({ emailAddress: email });
  }

  handleIncorrectPassword = () => {
    this.setState({ showPasswordError: true });
  };

  render() {
    return (
      <UserConsumer>
        {(context) => {
          const { loginUser } = context;
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
                      Sign in
                    </Typography>
                  </Grid>
                </Grid>
                <form noValidate>
                  <TextField
                    variant="outlined"
                    margin="normal"
                    required
                    fullWidth
                    id="email"
                    label="Email Address"
                    name="email"
                    autoComplete="email"
                    autoFocus
                    helperText={
                      this.state.showPasswordError
                        ? 'Incorrect email address.'
                        : null
                    }
                    value={this.state.emailAddress}
                    onChange={(event) =>
                      this.handleEmailAddress(event.target.value)
                    }
                  />
                  <Button
                    type="submit"
                    fullWidth
                    variant="contained"
                    color="primary"
                    onClick={async (event) => {
                      event.preventDefault();
                      const user = await loginUser(this.state.emailAddress);
                      if (user) this.props.history.push('/');
                      else this.handleIncorrectPassword();
                    }}
                  >
                    Sign In
                  </Button>
                </form>
              </Container>
            </React.Fragment>
          );
        }}
      </UserConsumer>
    );
  }
}

export default SignIn;
