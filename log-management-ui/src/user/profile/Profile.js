import React, {Component} from 'react';
import AuditLog from "../../common/AuditLog"
import {getCurrentUserEvents} from "../../util/APIUtils"
import './Profile.css';

class Profile extends Component {
    constructor(props) {
        super(props);
        this.state = {
            auditlogs: []
        }
    }

    componentDidMount() {
        getCurrentUserEvents().then((e) => {
            this.setState({auditlogs: e})
        })
    }

    render() {
        return (
            <div className="profile-container">
                <div className="container">
                    <div className="profile-info">
                        <div id={"users-listings-container"}>
                            <table id={"users-listing"}>
                                <thead>
                                <tr>
                                    <th width={'200px'}>Attribute</th>
                                    <th width={'100px'}>Value</th>
                                </tr>
                                </thead>
                                <tbody>
                                <tr>
                                    <td>User Id</td>
                                    <td>{this.props.currentUser.userId}</td>
                                </tr>
                                <tr>
                                    <td>Name</td>
                                    <td>{this.props.currentUser.name}</td>
                                </tr>
                                <tr>
                                    <td>Email</td>
                                    {/* userId and email are same */}
                                    <td>{this.props.currentUser.userId}</td>
                                </tr>
                                <tr>
                                    <td>Accounts</td>
                                    <td>{this.props.currentUser.accounts.map(a => a.name).join(",")}</td>
                                </tr>
                                <tr>
                                    <td>Roles</td>
                                    <td>{JSON.stringify(this.props.currentUser.roles)}</td>
                                </tr>
                                <tr>
                                    <td>Created On</td>
                                    <td>{this.props.currentUser.createdOn}</td>
                                </tr>
                                <tr>
                                    <td>Auth Type</td>
                                    <td>{this.props.currentUser.authProvider}</td>
                                </tr>
                                </tbody>
                            </table>
                        </div>
                        <div><h3>Audit Logs</h3></div>
                        <AuditLog auditlogs={this.state.auditlogs}></AuditLog>
                    </div>
                </div>
            </div>
        );
    }
}

export default Profile
