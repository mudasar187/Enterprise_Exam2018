import React, { Component } from 'react';
import axios from "axios";
import urls from "../utils/Urls"
import DatePicker from "react-datepicker";

import "react-datepicker/dist/react-datepicker.css";
import Header from "./Header";

class SignUp extends Component {

	constructor(props) {
		super(props);

		this.state = {
			error: null,
			username: "",
			password: "",
			name: "",
			email: "",
			dateOfBirth: new Date()
		};
	}

	signUp = () => {

		const headers = {
			'Content-Type': 'application/json'};

		if (!this.checkFormData){
			return;
		}

		axios.post(
			urls.authUrls.signUp,
			{
				password: this.state.password,
				userInfo: {
					username: this.state.username,
					name: this.state.name,
					email: this.state.email,
					dateOfBirth: this.state.dateOfBirth.toISOString().substr(0, 10)
				}
			},
			{
				headers: headers,
				withCredentials: true
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


					<label>
						<p>Email:</p>
						<input type="text" name="email" value={this.state.email} onChange={this.handleChange} />
					</label>
					<br/>
					<br/>


					<label>
						<p>Name:</p>
						<input type="text" name="name" value={this.state.name} onChange={this.handleChange} />
					</label>
					<br/>
					<br/>


					<label>
						<p>Date of birth:</p>
						<DatePicker name="dateOfBirth" selected={this.state.dateOfBirth} onChange={this.handleDateChange}/>
					</label>
					<br/>
					<br/>

					<input type="submit" value="Sign Up" />
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
		console.log("checking formdata");
		if (!this.state.username || this.state.username.length === 0){
			this.setState({error: "Missing username"});
			return false;
		}
		if (!this.state.password || this.state.password.length === 0){
			this.setState({error: "Missing password"});
			return false;
		}
		if (!this.state.name || this.state.email.length === 0){
			this.setState({error: "Missing name"});
			return false;
		}
		if (!this.state.email || this.state.email.length === 0){
			this.setState({error: "Missing email"});
			return false;
		}
		if (this.state.username === null){
			this.setState({error: "Missing birth date"});
			return false;
		}

		return true;
	};

	handleChange = (evt) => {
		// check it out: we get the evt.target.name (which will be either "email" or "password")
		// and use it to target the key on our `state` object with the same name, using bracket syntax
		this.setState({ [evt.target.name]: evt.target.value });
	};

	handleDateChange = (date) => {
		this.setState({
			dateOfBirth: date
		});

		console.log(this.state.dateOfBirth.toISOString().substr(0,10));

	}
}

export default SignUp