import React, { Component } from 'react';
import { Link } from 'react-router-dom';
import axios from "axios";
import urls from "../utils/Urls"


class Header extends Component {

	constructor(props){
		super(props);

		this.checkAuth();

		this.state = {
			authenticated : false
		}
	}

	componentDidMount() {
		this.checkAuth()
	}

	checkAuth = () => {
		const client = axios.create({
			headers: {'X-Requested-With': 'XMLHttpRequest'},
			withCredentials: true
		});

		client.get(urls.authUrls.user).then(
			res => {
				if (res.status === 200) {
					this.setState({authenticated: true});
				}
			}
		).catch(err => {
			this.setState({error: err})
		});
	};

	renderLinks() {
		if (this.state.authenticated) {
			return (
				<div>
					<Link to="/signout">Sign Out</Link>
					<Link to="/feature">My Page</Link>
				</div>
			);
		} else {
			return (
				<div>
					<Link to="/signup">Sign Up</Link>
					<Link to="/signin">Sign In</Link>
				</div>
			);
		}
	}

	render() {
		return (
			<div className="header">
				<Link to="/">Home</Link>
				{this.renderLinks()}
			</div>
		);
	}
}

export default Header;