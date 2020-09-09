import React, {Component} from 'react';
import './AccessToken.css';
import {Link, Redirect} from 'react-router-dom'
import Alert from 'react-s-alert';
import {createAccessTokens} from '../util/APIUtils';

class CreateAccessToken extends Component {
    componentDidMount() {
        if (!this.props.hasRole(["SUPER_ADMIN", "ACCOUNT_ADMIN"])) {
            this.props.history.push("/access-tokens");
        }
    }

    render() {
        return (
            <div className="signup-container">
                <div className="signup-content">
                    <h1 className="signup-title">Create Access Token</h1>
                    <CreateAgentForm {...this.props} />
                </div>
            </div>
        );
    }
}

class CreateAgentForm extends Component {
    constructor(props) {
        super(props);
        this.state = {
            name: ''
        }
    }

    handleInputChange = (event) => {
        const target = event.target;
        const inputName = target.name;
        const inputValue = target.value;

        this.setState({
            [inputName]: inputValue
        });
    }

    handleSubmit = (event) => {
        event.preventDefault();

        const signUpRequest = Object.assign({}, this.state);

        createAccessTokens(signUpRequest)
            .then(response => {
                console.log(response);
                Alert.success(`You're successfully registered with accessKey - "${response.clientId}" and secret - "${response.clientSecret}"`);
                this.props.history.push("/access-tokens");
            }).catch(error => {
            Alert.error((error && error.message) || 'Oops! Something went wrong. Please try again!');
        });
    }

    render() {
        return (
            <form onSubmit={this.handleSubmit}>
                <div className="form-item">
                    <input type="text" name="name"
                           className="form-control" placeholder="Name"
                           value={this.state.name} onChange={this.handleInputChange} required/>
                </div>
                <div className="form-item">
                    <button type="submit" className="btn btn-block btn-primary">Create</button>
                </div>
            </form>

        );
    }
}

export default CreateAccessToken
