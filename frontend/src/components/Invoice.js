import React, {Component} from "react";
import axios from "axios";
import urls from "../utils/Urls";
import Header from "./Header";

class Invoice extends Component {

    constructor(props) {
        super(props);

        console.log(props.match.params.id);
        const {invoiceId} = props.match.params.id;

        this.state = {
            invoiceId: props.match.params.id,
            error: null,
            invoice: null,
            price: 0
        };



        this.getInvoiceId();

    }
    render() {
        return (
            <div>
                <Header/>
                <div className="warning">{this.state.error}</div>
            </div>
        )
    }

    getInvoiceId = () => {
        console.log("bla");
        console.log(this.state);
        if (this.state.invoiceId) {
            const client = axios.create({
                headers: {'X-Requested-With': 'XMLHttpRequest'},
                withCredentials: true
            });
            client.get(`${urls.invoiceUrls.getById}/${this.state.invoiceId}`).then(
                res => {
                    let invoiceObj = res.data.data.list[0];
                    this.setState({invoice: invoiceObj});
                    console.log(this.state.invoice)
                }
            ).catch(err => {
                this.setState({error: "You need to sign in first"})
            });
        }
    };


}


export default Invoice