import React, { Component } from 'react';
import axios from "axios";
import NowPlaying from "./NowPlaying";
import Flexbox from 'flexbox-react';


class NowPlayings extends Component{

	constructor(props) {
		super(props);

		this.state = {
			nowPlayings: null,
			error: null
		};

		this.getNowPlayings();
	}

	getNowPlayings = () => {

		const url = "http://localhost:7083/now-playings";

		axios.get(url).then(
			res => {
				this.setState({nowPlayings : res.data.data.list});
			}
		).catch(err => {
			this.setState({error : err})
		});
	};



	render() {
		return (
			<div>
				<div className="grid">
					{this.state.nowPlayings !== null
						? this.state.nowPlayings.map( item => {
							return <NowPlaying key={item.id} nowPlaying={item}/>
							})
						: <p>No elemts in nowplaying</p>
					}
				</div>

			</div>
		);
	}
}

export default NowPlayings