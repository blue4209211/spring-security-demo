import React, { Component } from 'react';
import './AuditLog.css';

class AuditLog extends Component {
    render() {
        return (
            <div>
                <div id={"auditlog-listings-container"}>
                    <table id={"auditlog-listing"}>
                        <thead>
                        <tr>
                            <th width={'200px'}>Account Id</th>
                            {this.props.showUser?<th width={'200px'}>User Id</th>:null}
                            <th width={'200px'}>Event Name</th>
                            <th width={'100px'}>Created On</th>
                            <th width={'100px'}>Extra Details</th>
                        </tr>
                        </thead>
                        <tbody>
                        {this.props.auditlogs.map((item, index) => (
                            <tr className="ul li" key={index}>
                                <td>{item.accountId}</td>
                                {this.props.showUser?<td>{item.userId}</td>:null}
                                <td>{item.eventName}</td>
                                <td>{new Date(item.timestamp).toISOString()}</td>
                                <td>{JSON.stringify(item.details)}</td>
                            </tr>))
                        }
                        </tbody>
                    </table>
                </div>
            </div>
        );
    }
}

export default AuditLog;
