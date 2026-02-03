package helpers;

import java.util.List;
import java.util.Map;

public class PerformanceYamlValidator {

    public static void validatePerformanceYaml(
            Map<String, Object> performanceYaml,
            Map<String, Object> parametricYaml
    ) {

        requireKey(performanceYaml, "mode");
        requireKey(performanceYaml, "features");

        validateMode(performanceYaml.get("mode").toString());

        List<Map<String, Object>> features =
                (List<Map<String, Object>>) performanceYaml.get("features");

        if (features.isEmpty()) {
            throw new IllegalArgumentException(
                    "performance.yaml: 'features' list cannot be empty"
            );
        }

        for (int i = 0; i < features.size(); i++) {
            validateFeature(features.get(i), i, parametricYaml);
        }
    }

    /* -------------------------
       Feature validation
     ------------------------- */

    private static void validateFeature(
            Map<String, Object> feature,
            int index,
            Map<String, Object> parametricYaml
    ) {

        String prefix = "performance.yaml -> features[" + index + "]";

        requireKey(feature, "scenario", prefix);
        requireKey(feature, "feature", prefix);
        requireKey(feature, "enabled", prefix);
        requireKey(feature, "concurrency", prefix);
        requireKey(feature, "execution_time", prefix);
        requireKey(feature, "response_time", prefix);
        requireKey(feature, "error_rate", prefix);
        requireKey(feature, "throughput", prefix);

        if (!(feature.get("enabled") instanceof Boolean)) {
            throw new IllegalArgumentException(
                    prefix + ": 'enabled' must be true or false"
            );
        }

        validateParametricValue(
                "concurrency",
                feature.get("concurrency").toString(),
                parametricYaml,
                prefix
        );

        validateParametricValue(
                "execution_time",
                feature.get("execution_time").toString(),
                parametricYaml,
                prefix
        );

        validateParametricValue(
                "response_time",
                feature.get("response_time").toString(),
                parametricYaml,
                prefix
        );

        validateParametricValue(
                "error_rate",
                feature.get("error_rate").toString(),
                parametricYaml,
                prefix
        );

        validateParametricValue(
                "throughput",
                feature.get("throughput").toString(),
                parametricYaml,
                prefix
        );

        if (feature.containsKey("percentiles")) {
        
            Map<String, Object> percentiles =
                    (Map<String, Object>) feature.get("percentiles");
        
            if (percentiles.containsKey("p95")) {
                validateParametricValue(
                    "p95",
                    percentiles.get("p95").toString(),
                    (Map<String, Object>) parametricYaml.get("percentiles"),
                    prefix + ".percentiles"
                );
            }
        
            if (percentiles.containsKey("p99")) {
                validateParametricValue(
                    "p99",
                    percentiles.get("p99").toString(),
                    (Map<String, Object>) parametricYaml.get("percentiles"),
                    prefix + ".percentiles"
                );
            }
        }

    }

    /* -------------------------
       Helpers
     ------------------------- */

    private static void requireKey(Map<String, Object> map, String key) {
        requireKey(map, key, "performance.yaml");
    }

    private static void requireKey(
            Map<String, Object> map,
            String key,
            String context
    ) {
        if (!map.containsKey(key)) {
            throw new IllegalArgumentException(
                    context + ": missing required key '" + key + "'"
            );
        }
    }

    private static void validateMode(String mode) {
        if (!mode.equals("sequence") && !mode.equals("parallel")) {
            throw new IllegalArgumentException(
                    "performance.yaml: invalid mode '" + mode +
                    "'. Allowed values: sequence | parallel"
            );
        }
    }

    private static void validateParametricValue(
            String section,
            String value,
            Map<String, Object> parametricYaml,
            String context
    ) {

        Map<String, Object> allowedValues =
                (Map<String, Object>) parametricYaml.get(section);

        if (!allowedValues.containsKey(value)) {
            throw new IllegalArgumentException(
                    context + ": invalid '" + section + "' value '" + value +
                    "'. Allowed values: " + allowedValues.keySet()
            );
        }
    }
}