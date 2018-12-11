import React, { Component } from 'react';
import axios from "axios";
import NowPlaying from "./sub/NowPlaying";
import urls from "../utils/Urls"


class NowPlayings extends Component{

	constructor(props) {
		super(props);

		const {cinemaId} = props.location.state;
		console.log(cinemaId);

		this.state = {
			nowPlayings: null,
			error: null,
			cinemaId: cinemaId
		};

		this.getNowPlayings();
	}

	getNowPlayings = () => {

		if (this.state.cinemaId !== null) {
			const url = `${urls.movieUrls.nowPlayings}?cinemaId=${this.state.cinemaId}`;

			axios.get(url).then(
				res => {
					this.setState({nowPlayings: res.data.data.list});
				}
			).catch(err => {
				this.setState({error: err})
			});
		}
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