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

  val populations =
    if (config.mode() == "sequence") {

      // 🔹 SEQUENCE MODE: one scenario, chained features
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

      // 🔹 PARALLEL MODE: one scenario per feature
      features.map { feature =>
        scenario(feature.scenario)
          .repeat(feature.executions) {
            exec(karateFeature(feature.featurePath))
          }
          .inject(atOnceUsers(feature.concurrentUsers))
          .protocols(protocol)
      }.toSeq
    }

  val populationBuilders = populations

  val assertions = config.features().asScala.flatMap { feature =>
    Seq(
      global.responseTime.max.lte(feature.maxResponseTimeMs),
      global.failedRequests.percent.lte(feature.maxErrorRatePercent),
      global.requestsPerSec.gte(feature.minThroughputRps)
    ) ++
    Option(feature.p95ResponseTimeMs).toSeq.map { p95 =>
      global.responseTime.percentile(95).lte(p95)
    } ++
    Option(feature.p99ResponseTimeMs).toSeq.map { p99 =>
      global.responseTime.percentile(99).lte(p99)
    }
  }

  setUp(populationBuilders: _*)
    .assertions(assertions.toSeq: _*)

}
