/*
 * Copyright (c) 2023 Picture Soluções em TI - All Rights Reserved
 */

package br.com.picture.demosharepoint;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

class Settings {
    private static final Logger logger = LoggerFactory.getLogger(Settings.class);
    private final Config config;

    Settings() {
        File configFile = Paths.get(System.getProperty("user.dir"), "application.conf").toFile();
        if (configFile.exists()) {
            logger.info("Loading config file '{}'", configFile);
        } else {
            //try to copy a config template
            try (InputStream templateStream = Settings.class.getResourceAsStream("/application.conf")) {
                if (templateStream != null) {
                    logger.info("Trying to create a new config file at '{}'", configFile.toPath());
                    byte[] templateBytes = templateStream.readAllBytes();
                    Files.write(configFile.toPath(), templateBytes, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
                }
            } catch (IOException ignored) {
            }
        }
        config = ConfigFactory.parseFile(configFile);
    }

    String getClientId() {
        return config.getString("azure.client-id");
    }

    String getClientSecret() {
        return config.getString("azure.client-secret");
    }

    String getTenant() {
        return config.getString("azure.tenant");
    }

    String getOauthScope() {
        return config.getString("azure.oauth-scope");
    }

    String getSharepointSite() {
        return config.getString("sharepoint.site");
    }

    boolean isGraphDebug() {
        return config.getBoolean("graph-client.debug");
    }

}
