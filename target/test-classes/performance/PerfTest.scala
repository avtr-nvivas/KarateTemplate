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

  setUp(populations: _*)
}
