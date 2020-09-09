import React, {Component} from 'react';
import './Accounts.css';
import {listAccounts, createAccount, deleteAccount} from '../util/APIUtils';
import LoadingIndicator from '../common/LoadingIndicator';

class Accounts extends Component {
    constructor(props) {
        super(props);
        this.state =
            {
                agents: [],
                loading: true
            };
    }

    componentDidMount() {
        console.log("currentUser", this.props.currentUser)
        this.getAccounts();
    }

    getAccounts = () => {
        this.setState({
            loading: true
        });

        listAccounts()
            .then(response => {
                this.setState({
                    accounts: response,
                    loading: false
                });
            }).catch(error => {
            this.setState({
                loading: false
            });
        });
    }

    createAccount = () => {
        this.props.history.push("/account-create");
    }

    removeAccount = (account) => {
        deleteAccount(account.id).then((r) => {
            this.getAccounts();
        })
    }

    disableAccount = (account) => {
        console.log("not implemented")
    }

    render() {
        if (this.state.loading) {
            return <LoadingIndicator/>
        }
        return (
            <div className="account-container">
                <div className="container">
                    {
                        this.props.hasRole(["SUPER_ADMIN","ACCOUNT_ADMIN"]) ?
                            <div id={"accountCreateActions"}>
                                <button type="submit" className="btn btn-small btn-primary" onClick={this.createAccount}>Create</button>
                            </div> : ""

                    }
                    <div id={"account-listings-container"}>
                        <table id={"account-listing"}>
                            <thead>
                            <tr>
                                <th width={'200px'}>Account Name</th>
                                <th width={'100px'}>Created On</th>
                                <th width={'100px'}>Status</th>
                                <th width={'100px'}>Action</th>
                            </tr>
                            </thead>
                            <tbody>
                            {this.state.accounts.map((item, index) => (
                                <tr className="ul li" key={index}>
                                    <td>{item.name}</td>
                                    <td>{item.createdOn}</td>
                                    <td>Active</td>
                                    <td>
                                        <button onClick={(e) => this.removeAccount(item)}
                                                className="btn btn-small">Delete
                                        </button>
                                    </td>
                                </tr>))
                            }
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        );
    }
}

export default Accounts
