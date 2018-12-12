import React from "react"
import Movie from "./Movie";
import moment from "moment-timezone"
import {Link} from "react-router-dom";

export default (props) => {

	var parsedTime = "";
	if (props.nowPlaying !== null) {
		const time = `${props.nowPlaying.time.substr(0, 19)}Z`;
		parsedTime = moment(time).tz("Europe/Berlin").format("dddd MMM Mo kk:mm")
	}
	return (
		<div className="grid-item">
			{props.nowPlaying !== null
				? <Link to={{ pathname: '/booking', state: { nowPlaying: props.nowPlaying} }}>
						<div>
							<p>{parsedTime}</p>
							<Movie key={props.nowPlaying.id} movie={props.nowPlaying.movieDto}/>
						</div>
				</Link>
				: <p>No content</p>

			}
		</div>
	)

}