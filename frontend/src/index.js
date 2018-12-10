import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './App';
import * as serviceWorker from './serviceWorker';
import {BrowserRouter, Route} from "react-router-dom";
import Welcome from "./components/Welcome";
import NowPlayings from "./components/NowPlayings";

ReactDOM.render(
	<BrowserRouter>
		<App>
			<Route path="/" exact component={Welcome} />
			<Route path="/nowPlayings" component={NowPlayings} />
		</App>
	</BrowserRouter>
	, document.getElementById('root'));

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: http://bit.ly/CRA-PWA
serviceWorker.unregister();


