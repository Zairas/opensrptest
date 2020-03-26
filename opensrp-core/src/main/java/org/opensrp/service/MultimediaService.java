package org.opensrp.service;

import java.io.File;
import java.util.List;

import org.opensrp.domain.Multimedia;
import org.opensrp.dto.form.MultimediaDTO;
import org.opensrp.repository.MultimediaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MultimediaService {
    private static Logger logger = LoggerFactory.getLogger(MultimediaService.class.toString());

	private final MultimediaRepository multimediaRepository;
	private String multimediaDirPath;
	@Value("#{opensrp['multimedia.directory.name']}")
	String baseMultimediaDirPath;

	@Autowired
	public MultimediaService(MultimediaRepository multimediaRepository) {
		this.multimediaRepository = multimediaRepository;
	}

	public String saveMultimediaFile(MultimediaDTO multimediaDTO, MultipartFile file) {
		
		boolean uploadStatus = uploadFile(multimediaDTO, file);

		if (uploadStatus) {
			try {
				Multimedia multimediaFile = new Multimedia()
						.withCaseId(multimediaDTO.caseId())
						.withProviderId(multimediaDTO.providerId())
						.withContentType(multimediaDTO.contentType())
						.withFilePath(multimediaDTO.filePath())
						.withFileCategory(multimediaDTO.fileCategory())
						.withLocationId(multimediaDTO.locationId())
						.withAttributes(multimediaDTO.attributes());

				multimediaRepository.add(multimediaFile);

				return "success";

			} catch (Exception e) {
				e.getMessage();
			}
		}

		return "fail";

	}

	public boolean uploadFile(MultimediaDTO multimediaDTO,
			MultipartFile multimediaFile) {
		
		// String baseMultimediaDirPath = System.getProperty("user.home");

		if (!multimediaFile.isEmpty()) {
			try {

				 multimediaDirPath = baseMultimediaDirPath + File.separator;
				String fileExt=".jpg";
				switch (multimediaDTO.contentType()) {
				
				case "application/octet-stream":
					multimediaDirPath += "videos";
					fileExt=".mp4";
					break;

				case "image/jpeg":
					multimediaDirPath += "images";
					fileExt=".jpg";
					break;

				case "image/gif":
					multimediaDirPath += "images";
					fileExt=".gif";
					break;

				case "image/png":
					multimediaDirPath += "images"; 
					fileExt=".png";
					break;

				}
				new File(multimediaDirPath).mkdir();
				String fileName=multimediaDirPath+File.separator+multimediaDTO.caseId()+"_"+multimediaDTO.fileCategory() + fileExt;
				multimediaDTO.withFilePath(fileName);
				File multimediaDir = new File(fileName);

				multimediaFile.transferTo(multimediaDir);

			/*
			 byte[] bytes = multimediaFile.getBytes();
			 	
			 BufferedOutputStream stream = new BufferedOutputStream(
						new FileOutputStream(multimediaDirPath));
				stream.write(bytes);
				stream.close();*/
				 
				return true;
				
			} catch (Exception e) {
				logger.error("",e);
				return false;
			}
		} else {
			return false;
		}
	}

	public List<Multimedia> getMultimediaFiles(String providerId) {
		return multimediaRepository.all(providerId);
	}
	public List<Multimedia> getMultimediaFilesByLocId(String locationId) {
		return multimediaRepository.byLocationId(locationId);
	}
	public Multimedia findByCaseId(String entityId){
		return multimediaRepository.findByCaseId(entityId);
	}
}
