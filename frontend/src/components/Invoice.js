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
            price: 0,
            cardNumber: "",
            cvc: "",
            expireAt: "",
            username : null,
            creditcardRetrived: false
        };

        this.getInvoiceId();

    }

    render() {
        let formated = "";
        if (this.state.invoice) {
            const date = this.state.invoice.orderDate.substr(0, 10);
            const time = this.state.invoice.orderDate.substr(11, 5);
            formated = `${date}       ${time}`;
        }


        return (
            <div>
                <Header/>
                <div className="warning">{this.state.error}</div>
                <div className="invoice">
                    {this.state.invoice
                        ? <div>
                            <h1>Confirm your order</h1>
                            <h3>Total Price:</h3>
                            <h4>{this.state.invoice.totalPrice},-</h4>
                            <h3>Your seats:</h3>
                            {this.state.invoice.tickets.map(ticket => {
                                return <p key={ticket.seat}>{ticket.seat}</p>
                            })}
                            <p>{formated}</p>
                        </div>
                        : <div>
                            <h3>Couldn't not fetch receipt</h3>
                        </div>
                    }
                     <form onSubmit={this.verifyCreditCard}>
                         <label>
                             Card number:
                             <input type="text" name="cardNumber" value={this.state.cardNumber} onChange={this.handleCardChange}/>
                         </label>
                         <label>
                             CVC code:
                             <input type="text" name="cvc" value={this.state.cvc} onChange={this.handleCardChange}/>
                         </label>
                         <label>
                             Expire at (mm/yy):
                             <input type="text" name="expireAt" value={this.state.expiration} onChange={this.handleCardChange}/>
                         </label>
                         <input type="submit" value="Pay"/>
                     </form>
                </div>
            </div>
        )
    }

    getInvoiceId = () => {
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
            )
                .catch(err => {
                this.setState({error: "You need to sign in first"})
            });
        }



    };


    verifyCreditCard = (event) => {
        event.preventDefault();
        const client = axios.create({
            headers: {'X-Requested-With': 'XMLHttpRequest'},
            withCredentials: true
        });
        client.post(urls.creditCard,
                {
                    query: `mutation{createCreditCard(creditCard:{expirationDate:"${this.state.expireAt}",cvc: ${this.state.cvc},username:"${this.state.invoice.username}",cardNumber:"${this.state.cardNumber}"})}`
                }
        ).then(res => {
                if (res.status === 200) {
                    this.props.history.push('/done')
                }
            }
        ).catch(err => {
            this.props.history.push('/done')
        });
    };

    handleCardChange = (event) => {
        this.setState({ [event.target.name]: event.target.value });
    };


    checkAuth = () => {
        const client = axios.create({
            headers: {'X-Requested-With': 'XMLHttpRequest'},
            withCredentials: true
        });

        client.get(urls.authUrls.user).then(
            res => {
                if (res.status === 200) {
                    this.setState({username: res.data.name});

                }
            }
        ).catch(err => {
        });
    };
}


export default Invoice