import React, {Component} from 'react';
import './Users.css';
import {listUsers,createUser,onCurrentAccountChange} from '../util/APIUtils';
import LoadingIndicator from '../common/LoadingIndicator';

class Users extends Component {
    constructor(props) {
        super(props);
        this.state =
            {
                users: [],
                loading: true
            };
    }

    componentDidMount() {
        this.getUsers();
        onCurrentAccountChange(acc => {
            this.getUsers();
        })
    }

    getUsers = () => {
        this.setState({
            loading: true
        });

        listUsers()
            .then(response => {
                this.setState({
                    users: response,
                    loading: false
                });
            }).catch(error => {
            this.setState({
                loading: false
            });
        });
    }

    createUser = () => {
        this.props.history.push("/user-create");
    }

    removeUser = (agent) => {
        console.log(agent)
    }

    disableUser = (agent) => {
        console.log(agent)
    }

    render() {
        if (this.state.loading) {
            return <LoadingIndicator/>
        }
        return (
            <div className="users-container">
                <div className="container">
                    {
                        this.props.hasRole(["SUPER_ADMIN","ACCOUNT_ADMIN"]) ?
                            <div id={"userCreateActions"}>
                                <button type="submit" className="btn btn-small btn-primary" onClick={this.createUser}>Register User</button>
                            </div>
                            : ""
                    }
                    <div  id={"users-listings-container"}>
                        <table  id={"users-listing"}>
                            <thead>
                                <tr>
                                    <th width={'200px'}>User Id</th>
                                    <th width={'100px'}>User Name</th>
                                    <th width={'100px'}>Created On</th>
                                    <th width={'100px'}>Status</th>
                                    <th width={'100px'}>Login Type</th>
                                    <th width={'100px'}>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                            {this.state.users.map((item, index) => (
                                <tr className="ul li" key={index}>
                                    <td>{item.userId}</td>
                                    <td>{item.name}</td>
                                    <td>{item.createdOn}</td>
                                    <td>{item.status}</td>
                                    <td>{item.authProvider}</td>
                                    <td>
                                        <button onClick={(e) => this.removeUser(item)} className="btn btn-small">Delete</button>
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

export default Users
