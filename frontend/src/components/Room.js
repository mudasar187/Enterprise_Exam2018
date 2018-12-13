import React, { Component } from 'react';
import axios from "axios";
import urls from "../utils/Urls"
import Seatmap from 'react-seatmap';
import naturalSort from "javascript-natural-sort";
import Header from "./Header";
import { SeatingChart } from 'react-seat-charts';


class Room extends Component {

	constructor(props) {
		super(props);

		const {nowPlaying} = props.location.state;
		//console.log(nowPlaying);

		this.state = {
			nowPlaying: nowPlaying,
			error: null,
			rows: null,
			cols : null,
			free: null,
			allSeats: null,
			seatmap: null
		};

		this.getRoomInfo();
	}

	render() {
		let seatingChart;
		if (this.state.seatmap != null) {
			let naming = {rows: ["A"], columns: ["1"]};
			seatingChart = <SeatingChart seats={this.state.seatmap} naming={naming}/>;
		}

		return <div>
			<Header/>
			{this.state.seatmap != null
			? <div className="seat-table">
					{seatingChart}
				</div>
				: <p>No seats for this movie found</p>
			}

		</div>
	}

	calculateSeats = (allSeats) => {
		const freeSeats = this.state.nowPlaying.seats.sort(naturalSort);

		const firstSeat = allSeats[0].substr(0,1);
		const seatsInARow = allSeats.filter(function(x){ return x.substr(0,1) === firstSeat; }).length;

		let rows = allSeats.length / seatsInARow;


		//this.setState({rows: (allSeats.length / seatsInARow), cols: seatsInARow});
		//console.log(allSeats.length / freeSeats.length)

		let arr = [];
		console.log(rows);

		for (let row = 0; row < rows; row++) {

			console.log(row);

			let seatarray = [];

			for (let j = 0; j < seatsInARow; j++) {
				let isReserved = false;
				let currentSeat = allSeats[seatsInARow * row + j];

				console.log();
				freeSeats.includes(currentSeat) ? isReserved = false : isReserved = true;
				console.log(isReserved);

				let status;

				if (!isReserved) {
					status = "available"
				} else {
					status = "occupied"
				}

				seatarray.push({
					seatType: "regular",
					label: currentSeat,
					status: status
				});
			}
			arr.push(seatarray);
		}

		this.setState({seatmap: arr});
	};

	getRoomInfo = () => {
		if (this.state.nowPlaying) {
			axios.get(`${urls.cinemaUrls.cinema}/${this.state.nowPlaying.cinemaId}/rooms/${this.state.nowPlaying.roomId}`).then(
				res => {
					let sorted = res.data.data.list[0].seats.sort(naturalSort);

					this.setState({allSeats: sorted});
					this.calculateSeats(sorted);

				}
			).catch(err => {
				this.setState({error: err})
			});
		}

	}
}

export default Room