package serviceApp;

import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;


// importante estas librerías 
import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


import org.apache.commons.io.FileUtils;
class ServiceTest {
    /* Runner Karate */
    @Test
    void testParallel() {
        Results results = Runner.path("classpath:serviceApp")
                    .outputCucumberJson(true)   // ⬅ OBLIGATORIO para reportes
                    .parallel(1);
        generateReport(results.getReportDir());
        assertEquals(0, results.getFailCount(), results.getErrorMessages());
    }
    
                        
   public static void generateReport(String karateOutputPath) {
        Collection<File> jsonFiles = FileUtils.listFiles(
                new File(karateOutputPath),
                new String[]{"json"},
                true
        );


        List<String> jsonPaths = new ArrayList<>(jsonFiles.size());


        jsonFiles.forEach(file -> jsonPaths.add(file.getAbsolutePath()));


        Configuration config = new Configuration(
                new File("target"),   // ⬅ raíz del proyecto, NO cucumber-html-reports
                "serviceApp"
        );


        ReportBuilder reportBuilder = new ReportBuilder(jsonPaths, config);
        reportBuilder.generateReports();
    }
}
