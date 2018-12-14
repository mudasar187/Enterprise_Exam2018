import React, { Component } from 'react';
import axios from "axios";
import urls from "../utils/Urls"
import naturalSort from "javascript-natural-sort";
import Header from "./Header";
import {Link} from "react-router-dom";


class Room extends Component {

	constructor(props) {
		super(props);

		const {nowPlaying} = props.location.state;

		this.state = {
			nowPlaying: nowPlaying,
			error: null,
			free: null,
			allSeats: null,
			seatmap: null,
			price: 0,
			username: null,
			codeInput: null,
			correctCode: false,
			correctCodeId: null
		};

		this.getRoomInfo();
		this.checkAuth()

	}

	render() {
		return <div>
			<Header/>
			{this.state.seatmap != null
				? <div>
					<div className="warning">{this.state.error}</div>
					<div className="seat-grid">
						{this.state.seatmap.map(seat => {

							if (!seat.isReserved){
								return (<div key={seat.label} className={seat.isSelected ? "selectedSeat" : "defaultSeat"} onClick={() => this.handleSeatPick(seat.index)}>{seat.label}</div>)
							} else {
								return (<div key={seat.label} className={"reservedSeat"}>{seat.label}</div>)
							}
						})}
					</div>
					<div className="legend">
						<div id="free">Free seat(s)</div>
						<div id="selected">Selected seat(s)</div>
						<div id="reserved">Reserved seats</div>
					</div>
					<div className="checkout">
						<form onSubmit={this.checkCouponCode}>
							<label>Coupon code:
								<input type="text" name="code" onChange={this.handleCodeInput}/>
							</label>
							<input type="submit" value="Submit"/>
						</form>
						<h2>{this.state.price},-</h2>
						<div className="buy-btn" onClick={this.makePurchase}>Purchase tickets</div>
					</div>
					<div className="selectedSeats">
						<h3>Selected seats:</h3>
						{this.state.seatmap.map(seat => {
							if (seat.isSelected) {
								return (<div key={seat.label}>{seat.label}</div>)
							}
						})
						}
					</div>
				</div>
				: <p>No seats for this movie found</p>
			}

		</div>
	}

	calculatePrice = () => {
		let price = 0.0;
		if (this.state.seatmap !== null) {
			this.state.seatmap.forEach(seat => {
				if (seat.isSelected) {
					price += 100
				}
			});
		}
		console.log("clicked");
		this.setState({price})
	};

	handleSeatPick = (seat) => {

		console.log(seat);
		const newSeatMap = this.state.seatmap;
		newSeatMap[seat].isSelected = !newSeatMap[seat].isSelected;

		this.setState(prevState => ({
			seatmap: newSeatMap,
		}));

		this.calculatePrice();

	};

	calculateSeats = () => {

		const freeSeats = this.state.nowPlaying.seats.sort(naturalSort);

		const objSeats = this.state.allSeats.map((seat, index) => {
			return {
				isSelected: false,
				isReserved: !freeSeats.includes(seat),
				label: seat,
				index,
			}
		});

		this.setState({
			seatmap: objSeats
		});
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
				this.setState({error: "Cant retrieve room information"})
			});
		}
	};

	makePurchase = () => {
		const seats = this.state.seatmap.filter(seat => {
			return seat.isSelected
		});

		const ticketArray = seats.map(seat => {
			return {
				seat: seat.label,
				price: 20.5
			}
		});

		if (!this.state.username) {
			this.setState({error: "Missing username, you need to log in first"})
		}else if(!this.state.nowPlaying){
			this.setState({error: "Missing movie, please reload page"})
		} else {
			const client = axios.create({
				headers: {'X-Requested-With': 'XMLHttpRequest'},
				withCredentials: true
			});
			client.post(`${urls.invoiceUrls.create}`,
				{
					nowPlayingId: this.state.nowPlaying.id,
					tickets: ticketArray,
					username: this.state.username,
					orderDate: "2018-12-23 20:00:02",
					couponCode: {
						id: this.state.correctCodeId,
						code: this.state.correctCode ? this.state.codeInput : null
					}
				}
			).then(res => {
					console.log(res);
					const invoiceId = res.data.data.list[0].id;
					console.log(invoiceId);
					this.props.history.push(`/invoices/${invoiceId}`, {invoiceId: invoiceId})
				}
			).catch(err => {
				console.log(err);
				this.setState({error: "Failed to book movie"})

			});
		}
	};

	checkAuth = () => {
		const client = axios.create({
			headers: {'X-Requested-With': 'XMLHttpRequest'},
			withCredentials: true
		});

		client.get(urls.authUrls.user).then(
			res => {
				if (res.status === 200) {
					this.setState({username: res.data.name});
					console.log(this.state.username);
					console.log();
				}
			}
		).catch(err => {
			this.setState({error: "You need to log in first"})
		});
	};

	checkCouponCode = (event) => {
		event.preventDefault();

		if (this.state.codeInput) {

			const client = axios.create({
				headers: {'X-Requested-With': 'XMLHttpRequest'},
				withCredentials: true
			});

			client.get(`${urls.invoiceUrls.getCoupon}${this.state.codeInput}`).then(
				res => {
					if (res.status === 200) {
						this.setState({correctCode: true, correctCodeId: res.data.data.list[0].id});
						console.log("SUCCESS!!!")
					}
				}
			).catch(err => {
				this.setState({error: "Invalid coupon code"})
			});
		}
	};

	handleCodeInput = (event) => {
		this.setState({codeInput: event.target.value})
	};
}

export default Room