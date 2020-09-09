import React, {Component} from 'react';
import './Users.css';
import {Link, Redirect} from 'react-router-dom'
import Alert from 'react-s-alert';
import {createUser} from '../util/APIUtils';

class CreateUser extends Component {
    render() {
        return (
            <div className="signup-container">
                <div className="signup-content">
                    <h1 className="signup-title">Add User To Account</h1>
                    <CreateUserForm {...this.props} />
                </div>
            </div>
        );
    }
}

class CreateUserForm extends Component {
    constructor(props) {
        super(props);
        this.state = {
            role: "ACCOUNT_ADMIN"
        }
    }
    componentDidMount() {
        if (!this.props.hasRole(["SUPER_ADMIN", "ACCOUNT_ADMIN"])) {
            this.props.history.push("/agents");
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

        createUser(signUpRequest)
            .then(response => {
                console.log(response);
                Alert.success(`You have successfully created new User`);
                this.props.history.push("/users");
            }).catch(error => {
            Alert.error((error && error.message) || 'Oops! Something went wrong. Please try again!');
        });
    }

    render() {
        return (
            <form onSubmit={this.handleSubmit}>
                <div className="form-item">
                    <input type="text" name="userId"
                           className="form-control" placeholder="User Email Id"
                           value={this.state.userId} onChange={this.handleInputChange} required/>
                </div>
                <div className="form-item">
                    <select name="role"
                           className="form-control"
                            value={this.state.role} onChange={this.handleInputChange} required>
                        <option value="ACCOUNT_ADMIN">ADMIN</option>
                        <option value="ACCOUNT_SECURITY_ENGINEER">SECURITY ENGINEER</option>
                        <option value="ACCOUNT_DEVELOPER">DEVELOPER</option>
                    </select>
                </div>
                <div className="form-item">
                    <button type="submit" className="btn btn-block btn-primary">Register User</button>
                </div>
            </form>

        );
    }
}

export default CreateUser
