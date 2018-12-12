import React, { Component } from 'react';
import axios from "axios";
import urls from "../utils/Urls"
import Seatmap from 'react-seatmap';
import naturalSort from "javascript-natural-sort";


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
		return <div>
			{this.state.seatmap != null
			? <div className="seat-table">
					<Seatmap rows={this.state.seatmap} maxReservableSeats={3} alpha={true} />


				</div>
				: <p>No seats</p>

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

				seatarray.push({
					number: currentSeat,
					isReserved: isReserved
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