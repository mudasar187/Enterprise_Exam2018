import React, { Component } from 'react';
import axios from "axios";
import urls from "../utils/Urls"
import Cinema from "./sub/Cinema";
import Header from "./Header";

class Welcome extends Component {

	constructor(props){
		super(props);

		this.state = {
			cinemas: null,
			error: null
		};

		this.getCinemas();

	}

	getCinemas = () => {
		axios.get(urls.cinemaUrls.cinema).then(
			res => {
				this.setState({cinemas : res.data.data.list});
			}
		).catch(err => {
			this.setState({error : err})
		});


	};

	render() {
		return <div>
			<Header/>
			<div className="grid">
				{this.state.cinemas !== null
					? this.state.cinemas.map( item => {
						return <Cinema key={item.id} cinema={item}/>
					})
					: <p>No elemts in nowplaying</p>
				}
			</div>
		</div>
	}

}

export default Welcome