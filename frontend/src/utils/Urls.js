export default {
	cinemaUrls: {
		cinema: "http://localhost:10000/cinema-service/cinemas"
	},
	movieUrls: {
		movies: "http://localhost:10000/movie-service/movies",
		nowPlayings : "http://localhost:10000/movie-service/now-playings",
	},
	authUrls: {
		signIn:"http://localhost:10000/auth-service/login",
		signUp:"http://localhost:10000/auth-service/signup",
		logOut:"http://localhost:10000/auth-service/logout",
		user: "http://localhost:10000/auth-service/user",
		isAuthenticated: "http://localhost:10000/auth-service/is-authenticated"
	},
	invoiceUrls : {
		create: "http://localhost:10000/order-service/invoices",
		getById: "http://localhost:10000/order-service/invoices",
		getCoupon: "http://localhost:10000/order-service/coupons?code="
	}

};