import React, {Component} from 'react';
import {Link, NavLink} from 'react-router-dom';
import {setCurrentAccount} from '../util/APIUtils'
import './AppHeader.css';

class AppHeader extends Component {
    constructor(props) {
        super(props);
        this.state =
            {
                agents: [],
                loading: true,
                account: {}
            };
    }

    componentDidMount() {
        if (this.props.authenticated && this.props.currentUser.accounts && this.props.currentUser.accounts.length > 0) {
            setCurrentAccount(this.props.currentUser.accounts[0]);
            this.setState({account: this.props.currentUser.accounts[0]});
        }
    }

    userAccountSelected = (a,e) => {
        setCurrentAccount(a);
        this.setState({account: a})
    }

    render() {
        return (
            <header className="app-header">
                <div className="container">
                    <div className="app-branding">
                        <Link to="/" className="app-title">Logs Manager</Link>
                    </div>
                    <div className="app-options">
                        <nav className="app-nav">
                            {this.props.authenticated ? (
                                <ul>
                                    <li>
                                        <select className="form-control" disabled={!this.props.authenticated} id="userAccountSelection"
                                                onChange={(e) => this.userAccountSelected(this.props.currentUser.accounts[parseInt(e.target.value)],e)}>
                                            {
                                                this.props.currentUser.accounts.map(
                                                    (account,idx) => <option key={account.id} value={idx}>{account.name}</option>
                                                )
                                            }
                                        </select>
                                    </li>
                                    {
                                        this.props.hasRole(["SUPER_ADMIN"]) ?
                                            <li>
                                                <NavLink to="/accounts">Accounts</NavLink>
                                            </li>
                                            : ""

                                    }
                                    {
                                        this.props.hasRole(["SUPER_ADMIN","ACCOUNT_ADMIN","ACCOUNT_DEVELOPER"]) ?
                                            <li>
                                                <NavLink to="/access-tokens">Access Tokens</NavLink>
                                            </li>
                                            : ""

                                    }
                                    {
                                        this.props.hasRole(["SUPER_ADMIN","ACCOUNT_ADMIN","ACCOUNT_DEVELOPER","ACCOUNT_SECURITY_ENGINEER"]) ?
                                            <li>
                                                <NavLink to="/users">Users</NavLink>
                                            </li>
                                            : ""
                                    }
                                    {
                                        this.props.hasRole(["SUPER_ADMIN","ACCOUNT_ADMIN","ACCOUNT_SECURITY_ENGINEER"]) ?
                                            <li>
                                                <NavLink to="/events">Audit Events</NavLink>
                                            </li>
                                            : ""
                                    }
                                    <li>
                                        <NavLink to="/profile">Profile</NavLink>
                                    </li>
                                    <li>
                                        <a onClick={this.props.onLogout}>Logout</a>
                                    </li>
                                </ul>
                            ) : (
                                <ul>
                                    <li>
                                        <NavLink to="/login">Login</NavLink>
                                    </li>
                                    <li>
                                        {
                                            //<NavLink to="/signup">Signup</NavLink>
                                        }
                                    </li>
                                </ul>
                            )}
                        </nav>
                    </div>
                </div>
            </header>
        )
    }
}

export default AppHeader;
