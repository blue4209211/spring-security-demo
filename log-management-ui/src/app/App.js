import React, {Component} from 'react';
import {
    Route,
    Switch,
    Redirect
} from 'react-router-dom';
import AppHeader from '../common/AppHeader';
import Home from '../home/Home';
import Login from '../user/login/Login';
import Signup from '../user/signup/Signup';
import Profile from '../user/profile/Profile';
import AccessToken from '../accessToken/AccessToken';
import CreateAccessToken from '../accessToken/CreateAccessToken';
import Accounts from '../account/Accounts';
import CreateAccount from '../account/CreateAccount';
import Users from '../user/Users';
import CreateUser from '../user/CreateUser';
import OAuth2RedirectHandler from '../user/oauth2/OAuth2RedirectHandler';
import NotFound from '../common/NotFound';
import LoadingIndicator from '../common/LoadingIndicator';
import {getCurrentUser,getCurrentAccount} from '../util/APIUtils';
import {ACCESS_TOKEN} from '../constants';
import PrivateRoute from '../common/PrivateRoute';
import Alert from 'react-s-alert';
import 'react-s-alert/dist/s-alert-default.css';
import 'react-s-alert/dist/s-alert-css-effects/slide.css';
import './App.css';
import AccountAuditLogs from "../events/AccountAuditLogs";

class App extends Component {
    constructor(props) {
        super(props);
        this.state = {
            authenticated: false,
            currentUser: null,
            loading: false
        }
    }

    hasRole = (roles) => {
        if (this.state.authenticated) {
            let accountRoles = []
            for (let r of this.state.currentUser.roles) {
                if (r.name == "SUPER_ADMIN") {
                    return true
                }
            }
            for (let r of this.state.currentUser.roles) {
                if (r.accountId == getCurrentAccount().id) {
                    accountRoles.push(r.name);
                }
            }
            for (let r of roles) {
                if (accountRoles.indexOf(r) >= 0) {
                    return true
                }
            }
        }
        return false
    }


    loadCurrentlyLoggedInUser = () => {
        this.setState({
            loading: true
        });

        getCurrentUser()
            .then(response => {
                this.setState({
                    currentUser: response,
                    authenticated: true,
                    loading: false
                });
            }).catch(error => {
            this.setState({
                loading: false
            });
        });
    }

    handleLogout = () => {
        localStorage.removeItem(ACCESS_TOKEN);
        this.setState({
            authenticated: false,
            currentUser: null
        });
        Alert.success("You're safely logged out!");
    }

    componentDidMount() {
        this.loadCurrentlyLoggedInUser();
    }

    render() {
        if (this.state.loading) {
            return <LoadingIndicator/>
        }

        return (
            <div className="app">
                <div className="app-top-box">
                    <AppHeader hasRole={this.hasRole} authenticated={this.state.authenticated} currentUser={this.state.currentUser}
                               onLogout={this.handleLogout}/>
                </div>
                <div className="app-body">
                    <Switch>
                        <Route exact path="/">
                            <Redirect to="/profile"/>
                        </Route>
                        <Route exact path="/home" component={Home} hasRole={this.hasRole}
                               authenticated={this.state.authenticated} currentUser={this.state.currentUser}></Route>
                        <PrivateRoute path="/profile" hasRole={this.hasRole} authenticated={this.state.authenticated}
                                      currentUser={this.state.currentUser}
                                      component={Profile}></PrivateRoute>
                        <PrivateRoute path="/access-tokens" hasRole={this.hasRole} authenticated={this.state.authenticated}
                                      currentUser={this.state.currentUser}
                                      component={AccessToken}></PrivateRoute>
                        <PrivateRoute path="/access-token-create" hasRole={this.hasRole}
                                      authenticated={this.state.authenticated} currentUser={this.state.currentUser}
                                      component={CreateAccessToken}></PrivateRoute>
                        <PrivateRoute path="/accounts" hasRole={this.hasRole} authenticated={this.state.authenticated}
                                      currentUser={this.state.currentUser}
                                      component={Accounts}></PrivateRoute>
                        <PrivateRoute path="/account-create" hasRole={this.hasRole}
                                      authenticated={this.state.authenticated} currentUser={this.state.currentUser}
                                      component={CreateAccount}></PrivateRoute>
                        <PrivateRoute path="/users" hasRole={this.hasRole} authenticated={this.state.authenticated}
                                      currentUser={this.state.currentUser}
                                      component={Users}></PrivateRoute>
                        <PrivateRoute path="/events" hasRole={this.hasRole} authenticated={this.state.authenticated}
                                      currentUser={this.state.currentUser}
                                      component={AccountAuditLogs}></PrivateRoute>
                        <PrivateRoute path="/user-create" hasRole={this.hasRole}
                                      authenticated={this.state.authenticated} currentUser={this.state.currentUser}
                                      component={CreateUser}></PrivateRoute>
                        <Route path="/login"
                               render={(props) => <Login
                                   authenticated={this.state.authenticated} {...props} />}></Route>
                        <Route path="/signup"
                               render={(props) => <Signup
                                   authenticated={this.state.authenticated} {...props} />}></Route>
                        <Route path="/oauth2/redirect" component={OAuth2RedirectHandler}></Route>
                        <Route component={NotFound}></Route>
                    </Switch>
                </div>
                <Alert stack={{limit: 3}}
                       timeout={3000}
                       position='top-right' effect='slide' offset={65}/>
            </div>
        );
    }
}

export default App;
