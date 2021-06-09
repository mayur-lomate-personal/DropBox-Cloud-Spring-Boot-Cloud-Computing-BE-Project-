package com.sinhgad.cc.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import com.dropbox.core.DbxApiException;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v1.DbxClientV1;
import com.dropbox.core.v1.DbxEntry;
import com.dropbox.core.v1.DbxWriteMode;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;

public class Services {

	static String ACCESS_TOKEN = "nROcEWw4VQYAAAAAAAAAASSXam_YKU9fZ6jsvJkCjDgH9jwDlDgMBvSqZndTRUMa";

	public void authDropbox() throws DbxApiException, DbxException, FileNotFoundException, IOException {
		
		DbxRequestConfig config = new DbxRequestConfig("Temp-12345", Locale.getDefault().toString());
		DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);
		
		// Upload "test.txt" to Dropbox
        try (InputStream in = new FileInputStream("uploads/download.png")) {
            FileMetadata metadata = client.files().uploadBuilder("/Apps/Temp-12345/download.png")
                .uploadAndFinish(in);
        }
		
		// Get files and folder metadata from Dropbox root directory
        ListFolderResult result = client.files().listFolder("/Apps/Temp-12345");
        while (true) {
            for (Metadata metadata : result.getEntries()) {
                System.out.println(metadata.getPathLower());
            }

            if (!result.getHasMore()) {
                break;
            }

            result = client.files().listFolderContinue(result.getCursor());
        }
	}
}
