import React from "react"
import Movie from "./Movie";
import moment from "moment-timezone"

export default (props) => {

	var parsedTime = "";
	if (props.nowPlaying !== null) {
		const time = `${props.nowPlaying.time.substr(0, 19)}Z`;
		parsedTime = moment(time).tz("Europe/Berlin").format("dddd MMM Mo kk:mm")
	}
	return (
		<div className="grid-item">
			{props.nowPlaying !== null
				? <div>
					<p>{parsedTime}</p>
					<Movie key={props.nowPlaying.id} movie={props.nowPlaying.movieDto}/>
				</div>
				: <p>No content</p>

			}
		</div>
	)

}