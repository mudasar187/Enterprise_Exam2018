import React from "react"
import Movie from "./Movie";
import {Link} from "react-router-dom";

export default (props) => {

	var formated = "";
	if (props.nowPlaying !== null) {
		const date = props.nowPlaying.time.substr(0, 10);
		const time = props.nowPlaying.time.substr(11, 5);

		formated = `${date}       ${time}`
		//parsedTime = moment(time).tz("Europe/Berlin").format("dddd MMM Mo kk:mm")

	}
	return (
		<div className="grid-item">
			{props.nowPlaying !== null
				? <Link to={{ pathname: '/booking', state: { nowPlaying: props.nowPlaying} }}>
						<div>
							<p>{formated}</p>
							<Movie key={props.nowPlaying.id} movie={props.nowPlaying.movieDto}/>
						</div>
				</Link>
				: <p>No content</p>

			}
		</div>
	)

}