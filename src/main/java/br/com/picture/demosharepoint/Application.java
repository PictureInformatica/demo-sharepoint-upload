/*
 * Copyright (c) 2023 Picture Soluções em TI - All Rights Reserved
 */

package br.com.picture.demosharepoint;

import com.microsoft.graph.models.DriveItem;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.tasks.LargeFileUploadResult;
import okhttp3.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    public static Settings settings = new Settings();

    public static void main(String[] args) throws IOException {
        Path path = null;
        if (args != null && args.length == 1) {
            path = Paths.get(args[0]);
        } else {
            System.out.println("Usage:\n\tjava -jar application.jar file-to-upload");
            System.exit(1);
        }

        String destination = "/test/" + path.getFileName();

        GraphServiceClient<Request> graphClient = GraphClientBuilder.newBuilder()
                .clientId(settings.getClientId())
                .clientSecret(settings.getClientSecret())
                .tenantId(settings.getTenant())
                .scope(settings.getOauthScope())
                .debug(settings.isGraphDebug())
                .build();

        SharepointUploader sharepointUploader = new SharepointUploader(graphClient,
                settings.getSharepointSite(),
                destination,
                path);

        if (!sharepointUploader.fileExists()) {
            sharepointUploader.setProgressCallback(
                    (current, max) -> System.out.printf("Progress: %2.1f %%\n", ((current * 1f) / (max)) * 100));
            LargeFileUploadResult<DriveItem> upload = sharepointUploader.upload();

            if (upload != null && upload.responseBody != null && upload.responseBody.file != null && upload.responseBody.size != null) {
                System.out.printf("File uploaded: file-name:%s; size: %d KB; id:%s; mime-type: %s\n",
                        upload.responseBody.name,
                        upload.responseBody.size / 1024,
                        upload.responseBody.id,
                        upload.responseBody.file.mimeType
                );
            } else {
                throw new IOException("Error uploading file");
            }
        } else {
            logger.error("File '{}' already exists", destination);
        }
    }
}
