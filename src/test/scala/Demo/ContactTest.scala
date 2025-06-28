package Demo

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import Demo.Data._

class ContactTest extends Simulation{

  // 1 Http Conf
  val httpConf = http.baseUrl(url)
    .acceptHeader("application/json")

  // 2 Scenario Definition
  val scn = scenario("Create Contact").
    exec(http("Login")
        .post(s"users/login")
        .body(StringBody(
        s"""
        {
            "email": "$email",
            "password": "$password"
        }
        """)).asJson
      .check(jsonPath("$.token").saveAs("authToken"))
    )
    .exec(
      http("Create Contact")
        .post(s"contacts")
        .header("Authorization", s"Bearer ${authToken}")
        .body(StringBody(
        s"""
        {
            "firstName": "$firstName",
            "lastName": "$lastName",
            "birthdate": "$birthdate",
            "email": "$contactEmail",
            "phone": "$phone",
            "street1": "$street1",
            "street2": "$street2",
            "city": "$city",
            "stateProvince": "$stateProvince",
            "postalCode": "$postalCode",
            "country": "$country"
        }
        """)).asJson
        .check(status.is(201))
        .check(jsonPath("$._id").exists)
    )

  // 3 Load Scenario
  setUp(
    scn.inject(rampUsers(10).during(50))
  ).protocols(httpConf);
}