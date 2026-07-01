package com.tui.qa.driver;

import com.tui.qa.constants.FrameworkConstants;
import com.tui.qa.utils.LoggerUtil;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.SessionNotCreatedException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.time.Duration;

public final class DriverInitializer {

    private static final Logger logger =
            LoggerUtil.getLogger(DriverInitializer.class);

    private DriverInitializer() {
    }

    public static void initializeAndroidDriver() {

        try {

            logger.info(
                    "Starting Android Driver initialization for device: {}",
                    FrameworkConstants.DEVICE_NAME
            );

            UiAutomator2Options options = new UiAutomator2Options();

            options.setPlatformName(FrameworkConstants.PLATFORM_NAME);
            options.setAutomationName(FrameworkConstants.AUTOMATION_NAME);
            options.setApp(resolveAppPath(FrameworkConstants.APP));
            options.setAppPackage(FrameworkConstants.APP_PACKAGE);
            options.setAppActivity(FrameworkConstants.APP_ACTIVITY);

            if (!FrameworkConstants.PLATFORM_VERSION.isBlank()) {
                options.setPlatformVersion(FrameworkConstants.PLATFORM_VERSION);
            }

            if (!FrameworkConstants.DEVICE_NAME.isBlank()) {
                options.setDeviceName(FrameworkConstants.DEVICE_NAME);
            }

            if (!FrameworkConstants.UDID.isBlank()) {
                options.setUdid(FrameworkConstants.UDID);
            }

            options.autoGrantPermissions();
            options.setNoReset(false);
            options.setFullReset(false);
            options.setNewCommandTimeout(Duration.ofSeconds(60));
            options.setUiautomator2ServerLaunchTimeout(Duration.ofSeconds(120));
            options.setAdbExecTimeout(Duration.ofSeconds(120));
            options.setAndroidInstallTimeout(Duration.ofSeconds(180));

            URI appiumServerUri = resolveAppiumServerUri(FrameworkConstants.APPIUM_SERVER);

            AndroidDriver driver = null;

            for (int attempt = 1; attempt <= 2; attempt++) {

                try {

                    driver = new AndroidDriver(
                            appiumServerUri.toURL(),
                            options
                    );

                    break;

                } catch (SessionNotCreatedException e) {

                    String errorMessage = e.getMessage() == null ? "" : e.getMessage();

                    if (attempt < 2 && errorMessage.contains("socket hang up")) {

                        logger.warn(
                                "Session creation failed with transient UiAutomator2 socket hang up. Retrying once..."
                        );

                        pauseBeforeRetry();

                        continue;
                    }

                    throw e;
                }
            }

            DriverManager.setDriver(driver);

            logger.info(
                    "Android Driver initialized successfully | Platform: {} | Device: {} | UDID: {}",
                    FrameworkConstants.PLATFORM_NAME,
                    FrameworkConstants.DEVICE_NAME,
                    FrameworkConstants.UDID
            );

        } catch (MalformedURLException e) {

            logger.error("Failed to initialize Android Driver.", e);

            throw new RuntimeException("Unable to initialize Android Driver", e);
        }
    }

    private static String resolveAppPath(String configuredAppPath) {

        Path appPath = Paths.get(configuredAppPath);

        if (!appPath.isAbsolute()) {
            appPath = Paths.get(System.getProperty("user.dir"), configuredAppPath);
        }

        appPath = appPath.normalize().toAbsolutePath();

        if (!Files.exists(appPath)) {
            logger.warn("Configured app path does not exist: {}", appPath);
        }

        return appPath.toString();
    }

    private static URI resolveAppiumServerUri(String serverUrl) {

        URI uri = URI.create(serverUrl);
        String path = uri.getPath();

        if (path == null || path.isBlank() || "/".equals(path)) {

            String rootStatusEndpoint = serverUrl.replaceAll("/+$", "") + "/status";

            if (isStatusEndpointHealthy(rootStatusEndpoint)) {

                logger.info("Detected Appium endpoint at root path: {}", serverUrl);

                return uri;
            }

            String normalizedServerUrl = serverUrl.replaceAll("/+$", "") + "/wd/hub";
            URI wdHubUri = URI.create(normalizedServerUrl);
            String wdHubStatusEndpoint = normalizedServerUrl + "/status";

            if (isStatusEndpointHealthy(wdHubStatusEndpoint)) {

                logger.info(
                        "Detected Appium 1.x endpoint. Using base path /wd/hub: {}",
                        normalizedServerUrl
                );

                return wdHubUri;
            }

            logger.warn(
                    "Unable to auto-detect Appium base path from {}. Proceeding with provided root URL.",
                    serverUrl
            );

            return uri;
        }

        return uri;
    }

    private static boolean isStatusEndpointHealthy(String endpoint) {

        HttpURLConnection connection = null;

        try {

            connection = (HttpURLConnection) URI.create(endpoint).toURL().openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);

            return connection.getResponseCode() == 200;

        } catch (Exception ignored) {

            return false;

        } finally {

            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static void pauseBeforeRetry() {

        try {
            Thread.sleep(2000);
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
        }
    }
}