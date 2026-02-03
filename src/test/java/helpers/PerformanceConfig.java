package helpers;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PerformanceConfig {

    private final Map<String, Object> performanceYaml;
    private final Map<String, Object> parametricYaml;

    public PerformanceConfig() {
        Yaml yaml = new Yaml();

        //performanceYaml = loadYaml("scenarioPerf/performance.yaml");
        //parametricYaml  = loadYaml("scenarioPerf/parametricConfigurationValues.yaml");

        this.performanceYaml = loadYaml("scenarioPerf/performance.yaml");
        this.parametricYaml = loadYaml("scenarioPerf/parametricConfigurationValues.yaml");

        PerformanceYamlValidator.validatePerformanceYaml(
                performanceYaml,
                parametricYaml
        );
    }

    private Map<String, Object> loadYaml(String fileName) {
        try (InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream(fileName)) {

            if (input == null) {
                throw new RuntimeException("YAML file not found in classpath: " + fileName);
            }

            Yaml yaml = new Yaml();
            return yaml.load(input);

        } catch (Exception e) {
            throw new RuntimeException("Error loading YAML file: " + fileName, e);
        }
    }
                    
    /* ---------------------------
       General configuration
     --------------------------- */

    public String loadType() {
        return performanceYaml.get("loadType").toString();
    }

    public String mode() {
        return performanceYaml.get("mode").toString();
    }

    /* ---------------------------
       Feature definitions
     --------------------------- */

    @SuppressWarnings("unchecked")
    public List<FeatureConfig> features() {
        List<Map<String, Object>> features =
                (List<Map<String, Object>>) performanceYaml.get("features");

        return features.stream()
                .filter(f -> Boolean.TRUE.equals(f.get("enabled")))
                .map(this::mapToFeatureConfig)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private int resolveResponseTime(String level) {
        Map<String, Object> responseTimes =
                (Map<String, Object>) parametricYaml.get("response_time");

        Number value = (Number) responseTimes.get(level);
        return value.intValue();
    }

    @SuppressWarnings("unchecked")
    private double resolveErrorRate(String level) {
        Map<String, Object> errorRates =
                (Map<String, Object>) parametricYaml.get("error_rate");

        Number value = (Number) errorRates.get(level);
        return value.doubleValue();
    }

    @SuppressWarnings("unchecked")
    private int resolveThroughput(String level) {
        Map<String, Object> throughput =
                (Map<String, Object>) parametricYaml.get("throughput");
    
        Number value = (Number) throughput.get(level);
        return value.intValue();
    }

    private FeatureConfig mapToFeatureConfig(Map<String, Object> feature) {

        String responseLevel = feature.get("response_time").toString();
        String errorRateLevel = feature.get("error_rate").toString();
        String throughputLevel = feature.get("throughput").toString();
        
        FeatureConfig config = new FeatureConfig();

        config.scenario = feature.get("scenario").toString();
        config.featurePath = feature.get("feature").toString();

        String concurrencyLevel = feature.get("concurrency").toString();
        String executionLevel = feature.get("execution_time").toString();

        config.concurrentUsers = resolveConcurrency(concurrencyLevel);
        config.executions = resolveExecutionCount(executionLevel);
        
        config.maxResponseTimeMs = resolveResponseTime(responseLevel);
        config.maxErrorRatePercent = resolveErrorRate(errorRateLevel);
        config.minThroughputRps = resolveThroughput(throughputLevel);

        return config;
    }

    /* ---------------------------
       Parametric resolution
     --------------------------- */

    private int resolveConcurrency(String level) {
        Map<String, Integer> concurrency =
                (Map<String, Integer>) parametricYaml.get("concurrency");
        return concurrency.get(level);
    }

    private int resolveExecutionCount(String level) {
        Map<String, Integer> execution =
                (Map<String, Integer>) parametricYaml.get("execution_time");
        return execution.get(level);
    }

    /* ---------------------------
       FeatureConfig model
     --------------------------- */
    public static class FeatureConfig {

        // Identidad
        public String scenario;
        public String featurePath;

        // Carga
        public int concurrentUsers;
        public int executions;

        // SLA / Quality Gates
        public int maxResponseTimeMs;
        public double maxErrorRatePercent;
        public int minThroughputRps;
    }

    public int maxResponseTimeMs;
    
    
}
