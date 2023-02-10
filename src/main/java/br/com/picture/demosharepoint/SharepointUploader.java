/*
 * Copyright (c) 2023 Picture Soluções em TI - All Rights Reserved
 */

package br.com.picture.demosharepoint;

import com.microsoft.graph.core.ClientException;
import com.microsoft.graph.models.DriveItem;
import com.microsoft.graph.models.DriveItemCreateUploadSessionParameterSet;
import com.microsoft.graph.models.DriveItemUploadableProperties;
import com.microsoft.graph.models.UploadSession;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.tasks.IProgressCallback;
import com.microsoft.graph.tasks.LargeFileUploadResult;
import com.microsoft.graph.tasks.LargeFileUploadTask;
import okhttp3.Request;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class SharepointUploader {
    private static final Logger logger = LoggerFactory.getLogger(SharepointUploader.class);
    private final String siteName;
    private final String destinationPath;
    private final Path sourceFile;
    private final GraphServiceClient<Request> graphClient;
    private IProgressCallback progressCallback = null;

    public SharepointUploader(GraphServiceClient<Request> graphClient, String siteName, String destinationPath, Path sourceFile) {
        this.graphClient = graphClient;
        this.siteName = siteName;
        this.destinationPath = destinationPath;
        this.sourceFile = sourceFile;
    }

    public void setProgressCallback(IProgressCallback callback) {
        this.progressCallback = callback;
    }

    public boolean fileExists() {
        DriveItem driveItem = null;
        try {
            driveItem = graphClient.sites(siteName).drive().root().itemWithPath(destinationPath).buildRequest().get();
        } catch (ClientException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }

        return driveItem != null;
    }

    public LargeFileUploadResult<DriveItem> upload() throws IOException {
        DriveItemCreateUploadSessionParameterSet parameterSet = DriveItemCreateUploadSessionParameterSet.newBuilder()
                .withItem(new DriveItemUploadableProperties())
                .build();

        UploadSession uploadSession = graphClient.sites(siteName)
                .drive()
                .root()
                .itemWithPath(destinationPath)
                .createUploadSession(parameterSet)
                .buildRequest()
                .post();

        LargeFileUploadTask<DriveItem> largeFileUploadTask = null;
        if (uploadSession != null) {
            largeFileUploadTask = new LargeFileUploadTask<>(
                    uploadSession,
                    graphClient,
                    Files.newInputStream(sourceFile, StandardOpenOption.READ),
                    Files.size(sourceFile),
                    DriveItem.class
            );
        } else {
            throw new IOException("Error creating session for file upload");
        }

        return largeFileUploadTask.upload(0, null, progressCallback);
    }
}
