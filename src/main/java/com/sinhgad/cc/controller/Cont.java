package com.sinhgad.cc.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DownloadErrorException;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.UploadErrorException;
import com.sinhgad.cc.main.Candidate;

@Controller
public class Cont {
	
	@GetMapping("")
	public String home() {
		return "home";
	}
	
	@GetMapping("/form")
	public String getForm() {
		return "form";
	}
	
	private final Path root = Paths.get("uploads");
	private ArrayList<Candidate> candidates= new ArrayList<Candidate>();
	
	static String ACCESS_TOKEN = "r_-2tJC6saQAAAAAAAAAAesxKNqYv239dySedH3KhtziMYFgi4a5W5UlsvZ2nRgv";
	
	@PostMapping("/form")
	public String putForm(@RequestParam("resume") MultipartFile file, @RequestParam("first-name") String firstName, @RequestParam("last-name") String lastName, @RequestParam("email") String email, @RequestParam("phone") String phone, @RequestParam("skills") String skills) throws FileNotFoundException, IOException, UploadErrorException, DbxException {
		 try {
		      Files.copy(file.getInputStream(), this.root.resolve(file.getOriginalFilename()));
		    } catch (Exception e) {
		      throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
		    }
		 DbxRequestConfig config = new DbxRequestConfig("Temp-12345", Locale.getDefault().toString());
			DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);
			
			// Upload "test.txt" to Dropbox
	        try (InputStream in = new FileInputStream("uploads/" + file.getOriginalFilename())) {
	            FileMetadata metadata = client.files().uploadBuilder("/Apps/Temp-12345/" + file.getOriginalFilename())
	                .uploadAndFinish(in);
	        }
	        
		 candidates.add(new Candidate(firstName, lastName, email, phone, skills, "../download/" + file.getOriginalFilename()));
		 File file1 = new File("uploads/" + file.getOriginalFilename());
		 file1.delete();
		return "redirect:table";
	}
	
	@GetMapping("/table")
	public String getTable(Model model) {
		model.addAttribute("users", candidates);
		return "table";
	}
	
	@GetMapping("/download/{fileName}")
	public void downloadFile(HttpServletRequest request, HttpServletResponse response, @PathVariable("fileName") String fileName) throws IOException, DownloadErrorException, DbxException {
		
		DbxRequestConfig config = new DbxRequestConfig("Temp-12345", Locale.getDefault().toString());
		DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);
		DbxDownloader<FileMetadata> downloader = client.files().download("/Apps/Temp-12345/" + fileName);
		try {
            FileOutputStream out = new FileOutputStream("uploads/" + fileName);
            downloader.download(out);
            out.close();
        } catch (DbxException ex) {
            System.out.println(ex.getMessage());
        }
		
		File file = new File("uploads/" + fileName);
		
		//get the mimetype
		String mimeType = URLConnection.guessContentTypeFromName(file.getName());
		if (mimeType == null) {
			//unknown mimetype so set the mimetype to application/octet-stream
			mimeType = "application/octet-stream";
		}

		response.setContentType(mimeType);
		response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));
		response.setContentLength((int) file.length());

		InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

		FileCopyUtils.copy(inputStream, response.getOutputStream());
		inputStream.close();
		file.delete();
	}
}
