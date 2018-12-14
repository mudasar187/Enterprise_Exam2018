import React, { Component } from 'react';
import axios from "axios";
import urls from "../utils/Urls"
import Header from "./Header";


class LogOut extends Component {

    constructor(props) {
        super(props);

        this.state = {
            message: null,
            error: null
        }
    }

    componentDidMount() {
        this.signOut()
    }

    render() {
        return(
            <div>
                <Header/>
                <h1 className="success">{this.state.message}</h1>
                <div className="warning">{this.state.error}</div>
            </div>
        )
    }

    signOut = () => {
        const client = axios.create({
            headers: {'X-Requested-With': 'XMLHttpRequest'},
            withCredentials: true
        });
        client.post(`${urls.authUrls.logOut}`
        ).then(res => {
            this.setState({message: "Good bye!"})
        }
        ).catch(err => {
            this.setState({error: "Failed to sign out"});
        });

    };
}

export default LogOut