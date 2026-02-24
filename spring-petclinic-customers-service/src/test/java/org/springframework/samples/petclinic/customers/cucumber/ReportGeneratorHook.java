package org.springframework.samples.petclinic.customers.cucumber;

import io.cucumber.java.AfterAll;
import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;

import java.io.File;
import java.util.Collections;

/**
 * Cucumber hook that generates a rich, business-readable HTML report
 * after all scenarios have completed.
 *
 * <p>The report is created by the <a href="https://github.com/damianszczepanik/cucumber-reporting">
 * cucumber-reporting</a> library and written to
 * {@code target/cucumber-reports/advanced-reports/}.</p>
 */
public class ReportGeneratorHook {

    private static final String JSON_REPORT = "target/cucumber-reports/cucumber-report.json";
    private static final String REPORT_OUTPUT = "target/cucumber-reports/advanced-reports";

    @AfterAll
    public static void generateReport() {
        File jsonReport = new File(JSON_REPORT);
        if (!jsonReport.exists() || jsonReport.length() == 0) {
            System.out.println("[Cucumber Report] JSON report not found or empty at " + JSON_REPORT
                + " -- skipping rich report generation. Run 'mvn verify' to produce reports.");
            return;
        }

        File reportOutputDir = new File(REPORT_OUTPUT);
        if (!reportOutputDir.exists()) {
            reportOutputDir.mkdirs();
        }

        try {
            Configuration configuration = new Configuration(reportOutputDir, "PetClinic Customers Service");
            configuration.addClassifications("Environment", "Test");
            configuration.addClassifications("Platform", System.getProperty("os.name"));
            configuration.addClassifications("Java Version", System.getProperty("java.version"));
            configuration.addClassifications("Spring Boot", "4.0.1");

            ReportBuilder reportBuilder = new ReportBuilder(
                Collections.singletonList(jsonReport.getAbsolutePath()),
                configuration
            );
            reportBuilder.generateReports();

            System.out.println("[Cucumber Report] Rich HTML report generated at: " + reportOutputDir.getAbsolutePath());
        } catch (Exception e) {
            System.out.println("[Cucumber Report] Could not generate rich report (JSON may not be fully written yet): " + e.getMessage());
            System.out.println("[Cucumber Report] Standard reports are still available at: " + jsonReport.getParent());
        }
    }
}
