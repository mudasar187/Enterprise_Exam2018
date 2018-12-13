import React, { Component } from 'react';
import axios from "axios";
import urls from "../utils/Urls"
import "react-datepicker/dist/react-datepicker.css";
import Header from "./Header";

class SignIn extends Component {

	constructor(props) {
		super(props);

		this.state = {
			error: null,
			username: "",
			password: ""
		};
	}

	signUp = () => {

		const headers = {
			'Content-Type': 'application/json'};

		if (!this.checkFormData){
			return;
		}

		axios.post(
			urls.authUrls.signIn,
			{
				password: this.state.password,
				username: this.state.username,
			},
			{
				headers: headers
			}

		).then(
			res => {
				if (res.status === 204) {
					this.props.history.push('/');
				}
			}
		).catch(err => {
			this.setState({error : err})
		});
	};




	render() {
		return (
			<div>
				<Header/>
				<form onSubmit={this.handleSubmit} className="form">
					<label>
						<p>Username:</p>
						<input type="text" name="username" value={this.state.username} onChange={this.handleChange} />
					</label>
					<br/>
					<br/>

					<label>
						<p>Password:</p>
						<input type="password" name="password" value={this.state.password} onChange={this.handleChange} />
					</label>
					<br/>
					<br/>

					<input type="submit" value="Sign In" />
				</form>
				<p className="warning">{this.state.error}</p>
			</div>
		);
	}

	handleSubmit =(event) => {
		console.log(this.state);
		event.preventDefault();
		this.signUp();
	};

	checkFormData = () => {
		if (!this.state.username || this.state.username.length === 0){
			this.setState({error: "Missing username"});
			return false;
		}
		if (!this.state.password || this.state.password.length === 0){
			this.setState({error: "Missing password"});
			return false;
		}

		return true;
	};

	handleChange = (evt) => {
		// check it out: we get the evt.target.name (which will be either "email" or "password")
		// and use it to target the key on our `state` object with the same name, using bracket syntax
		this.setState({ [evt.target.name]: evt.target.value });
	};
}

export default SignIn