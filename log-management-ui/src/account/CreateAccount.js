import React, {Component} from 'react';
import './Accounts.css';
import {Link, Redirect} from 'react-router-dom'
import Alert from 'react-s-alert';
import {createAccount} from '../util/APIUtils';

class CreateAccount extends Component {

    componentDidMount() {
        if(!this.props.hasRole(["SUPER_ADMIN"])){
            this.props.history.push("/accounts");
        }
    }

    render() {
        return (
            <div className="signup-container">
                <div className="signup-content">
                    <h1 className="signup-title">Create Account</h1>
                    <CreateAccountForm {...this.props} />
                </div>
            </div>
        );
    }
}

class CreateAccountForm extends Component {
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

        const signUpRequest = Object.assign({}, this.state,{accountAdmin: this.props.currentUser.userId});

        createAccount(signUpRequest)
            .then(response => {
                console.log(response);
                Alert.success(`You have successfully created new Account`);
                this.props.history.push("/accounts");
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
                    <button type="submit" className="btn btn-block btn-primary">Create Account</button>
                </div>
            </form>

        );
    }
}

export default CreateAccount
