import React, { Component } from 'react';

export default ChildComponent => {
	class ComposedComponent extends Component {
		// Our component just got rendered
		componentDidMount() {
			this.shouldNavigateAway();
		}

		// Our component just got updated
		componentDidUpdate() {
			this.shouldNavigateAway();
		}

		shouldNavigateAway() {
			if (!this.props.auth) {
				this.props.history.push('/');
			}
		}

		render() {
			return <ChildComponent {...this.props} />;
		}
	}

	return ComposedComponent;
};