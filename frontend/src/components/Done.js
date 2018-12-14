import React, {Component} from "react";
import Header from "./Header";
import Link from "react-router-dom/es/Link";

class Done extends Component {

    constructor(props) {
        super(props);

    }

    render() {
        return (
            <div className="done">
                <Header/>
                <h2>Thank you for buying tickets, enjoy the movie!</h2>
                <Link to="/">Go to home</Link>
            </div>
        );
    }
}

export default Done