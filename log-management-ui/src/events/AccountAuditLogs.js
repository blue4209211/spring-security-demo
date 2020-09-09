import React, {Component} from 'react';
import {listAccessTokens, createAccessTokens, deleteAccessTokens, onCurrentAccountChange, getCurrentAccountEvents} from '../util/APIUtils';
import LoadingIndicator from '../common/LoadingIndicator';
import AuditLog from '../common/AuditLog';

class AccountAuditLogs extends Component {
    constructor(props) {
        super(props);
        this.state =
            {
                events: [],
                loading: true
            };
    }

    componentDidMount() {
        this.getEvents();
        onCurrentAccountChange(acc => {
            this.getEvents();
        })
    }

    getEvents = () =>{
        getCurrentAccountEvents().then((e) => {
            this.setState({events: e, loading: false})
        })
    }

    render() {
        if (this.state.loading) {
            return <LoadingIndicator/>
        }
        return (
            <AuditLog auditlogs={this.state.events} showUser={true}></AuditLog>
        );
    }
}

export default AccountAuditLogs
