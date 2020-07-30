import React from 'react';
import './App.css';
import { Route } from 'react-router-dom';
import Dashboard from './components/Dashboard/Dashboard';
import SignIn from './components/SignIn/SignIn';
import SignUp from './components/SignUp/SignUp';
import AppNavbar from './components/Nevigation/AppNavbar';
import SwipeableMenu from './components/Nevigation/SwipeableMenu';
import './tailwind.generated.css';

const App = () => {
  return (
    <div className="text-center">
      <AppNavbar />
      <SwipeableMenu />
      <Route path="/" exact component={Dashboard} />
      <Route path="/signin" component={SignIn} />
      <Route path="/signup" component={SignUp} />
    </div>
  );
};

export default App;
