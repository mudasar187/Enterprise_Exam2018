import React from "react"


export default (props) => {
	return (
		<div>
			{props.movie !== null
				? <div>
						<img className={"posterImages"} src={props.movie.posterUrl} alt={"Poster"}/>
						<h3>{props.movie.title}</h3>
						{props.movie.genre.map( gen => {
							return <p className="genre" key={gen.id}>{gen.name}</p>
						})}
						<p>{props.movie.movieDuration} min</p>
						<p>Age limit: {props.movie.ageLimit}</p>
					</div>
				: <p>No content</p>

			}
		</div>
	)
}