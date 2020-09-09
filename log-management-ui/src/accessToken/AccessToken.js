import React, {Component} from 'react';
import './AccessToken.css';
import {listAccessTokens, createAccessTokens,deleteAccessTokens, onCurrentAccountChange} from '../util/APIUtils';
import LoadingIndicator from '../common/LoadingIndicator';

class AccessToken extends Component {
    constructor(props) {
        super(props);
        this.state =
            {
                agents: [],
                loading: true
            };
    }

    componentDidMount() {
        this.getAgents();
        onCurrentAccountChange(acc => {
            this.getAgents();
        })
    }

    getAgents = () => {
        this.setState({
            loading: true
        });

        listAccessTokens()
            .then(response => {
                this.setState({
                    agents: response,
                    loading: false
                });
            }).catch(error => {
            this.setState({
                loading: false
            });
        });
    }

    createAgent = () => {
        this.props.history.push("/access-token-create");
    }

    removeAgent = (agent) => {
        deleteAccessTokens(agent.id).then((r)=>{
            this.getAgents();
        })
    }

    disableAgent = (agent) => {
        console.log(agent)
    }

    render() {
        if (this.state.loading) {
            return <LoadingIndicator/>
        }
        return (
            <div className="agent-container">
                <div className="container">
                    {
                        this.props.hasRole(["SUPER_ADMIN","ACCOUNT_ADMIN"]) ?
                            <div id={"agentCreateActions"}>
                                <button type="submit" className="btn btn-small btn-primary" onClick={this.createAgent}>Create Access Token</button>
                            </div>
                             : ""

                    }
                    <div id={"agent-listings-container"}>
                        <table id={"agent-listing"}>
                            <thead>
                            <tr>
                                <th width={'200px'}>Token Alias</th>
                                <th width={'200px'}>Access Key</th>
                                <th width={'100px'}>Created On</th>
                                <th width={'100px'}>Status</th>
                                <th width={'100px'}>Action</th>
                            </tr>
                            </thead>
                            <tbody>
                            {this.state.agents.map((item, index) => (
                                <tr className="ul li" key={index}>
                                    <td>{item.name}</td>
                                    <td>{item.userId}</td>
                                    <td>{item.createdOn}</td>
                                    <td>{item.status}</td>
                                    <td>
                                        <button onClick={(e) => this.removeAgent(item)}
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

export default AccessToken
