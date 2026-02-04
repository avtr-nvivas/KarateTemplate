package performance

import com.intuit.karate.gatling.PreDef._
import io.gatling.core.Predef._
import scala.concurrent.duration._
import helpers.PerformanceConfig
import scala.jdk.CollectionConverters._

class PerfTest extends Simulation {

  val config = new PerformanceConfig()

  val protocol = karateProtocol()
  protocol.nameResolver = (req, ctx) => req.getHeader("karate-name")

  val features = config.features().asScala

  // -------------------------------
  // Population builders
  // -------------------------------
  val populations =
    if (config.mode() == "sequence") {

      val chainedScenario =
        features.foldLeft(
          scenario("Sequence-Flow")
        ) { (scn, feature) =>
          scn.repeat(feature.executions) {
            exec(karateFeature(feature.featurePath))
          }
        }

      Seq(
        chainedScenario
          .inject(atOnceUsers(features.head.concurrentUsers))
          .protocols(protocol)
      )

    } else {

      features.map { feature =>
        scenario(feature.scenario)
          .repeat(feature.executions) {
            exec(karateFeature(feature.featurePath))
          }
          .inject(atOnceUsers(feature.concurrentUsers))
          .protocols(protocol)
      }.toSeq
    }

  // -------------------------------
  // Assertions
  // -------------------------------
  val globalAssertions = Seq(
    global.responseTime.max.lte(config.globalMaxResponseTimeMs()),
    global.failedRequests.percent.lte(config.globalMaxErrorRatePercent()),
    global.requestsPerSec.gte(config.globalMinThroughputRps()),
    global.responseTime.percentile(95).lte(config.globalP95Ms()),
    global.responseTime.percentile(99).lte(config.globalP99Ms())
  )

  val featureAssertions =
    features.flatMap { feature =>
      Option(feature.requests)
        .map(_.asScala)
        .getOrElse(Seq.empty)
        .map { reqName =>
          details(reqName)
            .failedRequests.percent
            .lte(feature.maxErrorRatePercent)
        }
    }

  // -------------------------------
  // Setup
  // -------------------------------
  setUp(populations: _*)
    .assertions((globalAssertions ++ featureAssertions).toSeq: _*)
}