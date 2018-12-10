import React, { Component } from 'react';
import { Link } from 'react-router-dom';
//import './HeaderStyle.css';

class Header extends Component {

	constructor(props){
		super(props)

		this.state = {
			authenticated : false
		}
	}

	componentDidMount() {
		//TODO check auth and set state

	}

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