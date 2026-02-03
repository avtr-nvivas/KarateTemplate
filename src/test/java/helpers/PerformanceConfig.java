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
        //Yaml yaml = new Yaml();

        performanceYaml = loadYaml("scenarioPerf/performance.yaml");
        parametricYaml = loadYaml("scenarioPerf/parametricConfigurationValues.yaml");
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

    private FeatureConfig mapToFeatureConfig(Map<String, Object> feature) {
        FeatureConfig config = new FeatureConfig();

        config.scenario = feature.get("scenario").toString();
        config.featurePath = feature.get("feature").toString();

        String concurrencyLevel = feature.get("concurrency").toString();
        String executionLevel = feature.get("execution_time").toString();

        config.concurrentUsers = resolveConcurrency(concurrencyLevel);
        config.executions = resolveExecutionCount(executionLevel);

        return config;
    }

    /* ---------------------------
       Parametric resolution
     --------------------------- */

    @SuppressWarnings("unchecked")
    private int resolveConcurrency(String level) {
        Map<String, Integer> concurrency =
                (Map<String, Integer>) parametricYaml.get("concurrency");
        return concurrency.get(level);
    }

    @SuppressWarnings("unchecked")
    private int resolveExecutionCount(String level) {
        Map<String, Integer> execution =
                (Map<String, Integer>) parametricYaml.get("execution_time");
        return execution.get(level);
    }

    /* ---------------------------
       FeatureConfig model
     --------------------------- */

    public static class FeatureConfig {
        public String scenario;
        public String featurePath;
        public int concurrentUsers;
        public int executions;
    }

}
