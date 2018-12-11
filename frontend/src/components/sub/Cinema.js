import React from "react";
import { Link } from "react-router-dom";



export default (props) => {
	return (
		<div>
			{props.cinema !== null
				? <div>
					<h3>{props.cinema.name}</h3>
					<Link to={{ pathname: '/nowPlayings', state: { cinemaId: props.cinema.id} }}>My route</Link>
				</div>
				: <p>No content</p>

			}
		</div>
	)
}