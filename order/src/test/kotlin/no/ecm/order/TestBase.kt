package no.ecm.order

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import io.restassured.RestAssured
import io.restassured.http.ContentType
import no.ecm.utils.response.CouponResponseDto
import no.ecm.utils.response.InvoiceResponse
import no.ecm.utils.response.TicketResponseDto
import org.junit.*
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [(OrderApplication::class)],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
abstract class TestBase {

    @LocalServerPort
    protected var port = 0

    val nowPlayingURL = "/now-playings"
    val ticketURL = "/tickets"
    val couponURL = "/coupons"
    val invoiceUrl = "/invoices"
    
    companion object {
    	private lateinit var wireMockServer: WireMockServer
        
        @BeforeClass
        @JvmStatic
        fun initWireMock() {
            wireMockServer = WireMockServer(wireMockConfig().port(8083))
            wireMockServer.start()
        }
        
        @AfterClass
        @JvmStatic
        fun tearDown() {
            wireMockServer.stop()
        }
    }

    @Before
    @After
    fun clean() {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
        RestAssured.basePath = "/"
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()

        /*
           Here, we read each resource (GET), and then delete them
           one by one (DELETE)
         */

        cleanInvoices()
        //cleanTickets()
        cleanCoupons()
    }

    private fun cleanTickets(){
        
        println(RestAssured.baseURI + " " + RestAssured.port + " " + RestAssured.basePath)
        
        println(getDbCount("/tickets"))
        
        val response = RestAssured.given().accept(ContentType.JSON)
                .param("limit", 100)
                .get("/tickets")
                .then()
                .statusCode(200)
                .extract()
                .`as`(TicketResponseDto::class.java)

        response.data!!.list.forEach { RestAssured.given()
                .delete("$ticketURL/${ it.id }")
                .then()
                .statusCode(200) }
        Assert.assertEquals(0, getDbCount(ticketURL))
    }

    private fun cleanCoupons(){
        val response = RestAssured.given().accept(ContentType.JSON)
                .param("limit", 100)
                .get(couponURL)
                .then()
                .statusCode(200)
                .extract()
                .`as`(CouponResponseDto::class.java)

        response.data!!.list.forEach { RestAssured.given()
                .delete("$couponURL/${ it.id }")
                .then()
                .statusCode(200) }
        Assert.assertEquals(0, getDbCount(couponURL))
    }

    private fun cleanInvoices(){
        val response = RestAssured.given().accept(ContentType.JSON)
                .param("limit", 100)
                .get(invoiceUrl)
                .then()
                .statusCode(200)
                .extract()
                .`as`(InvoiceResponse::class.java)

        response.data!!.list.forEach { RestAssured.given()
                .delete("$invoiceUrl/${ it.id }")
                .then()
                .statusCode(200) }
        Assert.assertEquals(0, getDbCount(invoiceUrl))
    }

    protected fun getDbCount(url: String) : Int{
        return RestAssured.given().accept(ContentType.JSON)
                .get(url)
                .then()
                .statusCode(200)
                .extract()
                .jsonPath().getInt("data.totalSize")
    }

    private fun cleanDb(url: String) {
        val response = RestAssured.given().accept(ContentType.JSON)
                .param("limit", 100)
                .get(url)
                .then()
                .statusCode(200)
                .extract()
                .`as`(TicketResponseDto::class.java)

        response.data!!.list.forEach { RestAssured.given()
                .delete("$url/${ it.id }")
                .then()
                .statusCode(200) }
    }

    protected fun getAMockNowPlayingResponse(nowPlayingId: String, seat: String) : String {

        return """
        {
		    "code": 200,
            "message": null,
            "status": null,
            "data": {
                "list": [
                    {
                        "id": "$nowPlayingId",
                        "movieDto": {
                            "id": "4",
                            "title": "Inception",
                            "posterUrl": "https://m.media-amazon.com/images/M/MV5BMjAxMzY3NjcxNF5BMl5BanBnXkFtZTcwNTI5OTM0Mw@@._V1_SX300.jpg",
                            "genre": [
                                {
                                    "id": "1",
                                    "name": "Action",
                                    "movies": null
                                }
                            ],
                            "movieDuration": 120,
                            "ageLimit": 13,
                            "nowPlaying": null
                        },
                        "roomId": "4",
                        "cinemaId": "1",
                        "cinemaName": null,
                        "time": "2018-12-08T16:56:41.230+01:00[Europe/Berlin]",
                        "seats": [
                            "$seat",
                            "A3",
                            "A5",
                            "A7",
                            "D2",
                            "B1",
                            "C2"
                        ]
                    }
                ],
                "rangeMin": 0,
                "rangeMax": 0,
                "totalSize": 0,
                "_links": { }
                }
            }
        """.trimIndent()
    }
    
    protected fun stubNowPlayingResponse(responseBody: String) {
        
        wireMockServer.stubFor(get(urlMatching("/now-playings.*")).willReturn(
            aResponse()
                .withHeader("Content-Type", "application/json; charset=utf-8")
                .withHeader("Content-Length", "" + responseBody.toByteArray(charset("utf-8")).size)
                .withHeader("ETag", responseBody.hashCode().toString())
                .withBody(responseBody)
        ))
        
        wireMockServer.stubFor(patch(urlMatching("/now-playings.*"))
            .withRequestBody(matchingJsonPath("$.seats"))
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json; charset=utf-8")
                    .withStatus(204)
        ))
        
    }
}