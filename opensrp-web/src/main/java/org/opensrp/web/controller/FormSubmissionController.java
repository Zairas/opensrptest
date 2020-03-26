package org.opensrp.web.controller;

import static ch.lambdaj.collection.LambdaCollections.with;
import static java.text.MessageFormat.format;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.net.URLConnection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.common.AllConstants;
import org.opensrp.connector.openmrs.constants.OpenmrsHouseHold;
import org.opensrp.connector.openmrs.service.EncounterService;
import org.opensrp.connector.openmrs.service.HouseholdService;
import org.opensrp.connector.openmrs.service.PatientService;
import org.opensrp.domain.Client;
import org.opensrp.domain.ErrorTrace;
import org.opensrp.domain.Event;
import org.opensrp.domain.Multimedia;
import org.opensrp.dto.form.FormSubmissionDTO;
import org.opensrp.dto.form.MultimediaDTO;
import org.opensrp.form.domain.FormSubmission;
import org.opensrp.form.service.FormSubmissionConverter;
import org.opensrp.form.service.FormSubmissionService;
import org.opensrp.scheduler.SystemEvent;
import org.opensrp.scheduler.TaskSchedulerService;
import org.opensrp.service.ErrorTraceService;
import org.opensrp.service.MultimediaService;
import org.opensrp.service.formSubmission.FormEntityConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import ch.lambdaj.function.convert.Converter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Controller
public class FormSubmissionController {
    private static Logger logger = LoggerFactory.getLogger(FormSubmissionController.class.toString());
    private FormSubmissionService formSubmissionService;
    private TaskSchedulerService scheduler;
    private EncounterService encounterService;
    private FormEntityConverter formEntityConverter;
    private PatientService patientService;
    private HouseholdService householdService;
    private ErrorTraceService errorTraceService;
    
    @Autowired //TODO: Julkar Confirm this
    private MultimediaService multimediaService;
    
    @Autowired
    public FormSubmissionController(FormSubmissionService formSubmissionService, TaskSchedulerService scheduler,
    		EncounterService encounterService, FormEntityConverter formEntityConverter, PatientService patientService, 
    		HouseholdService householdService, ErrorTraceService errorTraceService) {
        this.formSubmissionService = formSubmissionService;
        this.scheduler = scheduler;
        this.errorTraceService=errorTraceService;
        this.encounterService = encounterService;
        this.formEntityConverter = formEntityConverter;
        this.patientService = patientService;
        this.householdService = householdService;
    }

    @RequestMapping(method = GET, value = "/form-submissions")
    @ResponseBody
    private List<FormSubmissionDTO> getNewSubmissionsForANM(@RequestParam("anm-id") String anmIdentifier,
                                                            @RequestParam("timestamp") Long timeStamp,
                                                            @RequestParam(value = "batch-size", required = false)
                                                            Integer batchSize) {
        List<FormSubmission> newSubmissionsForANM = formSubmissionService
                .getNewSubmissionsForANM(anmIdentifier, timeStamp, batchSize);
        return with(newSubmissionsForANM).convert(new Converter<FormSubmission, FormSubmissionDTO>() {
            @Override
            public FormSubmissionDTO convert(FormSubmission submission) {
                return FormSubmissionConverter.from(submission);
            }
        });
    }

    @RequestMapping(method = GET, value = "/form-submissions-by-loc")
    @ResponseBody
    private List<FormSubmissionDTO> getNewSubmissionsForLoc(@RequestParam("locationId") String locationId,
                                                            @RequestParam("timestamp") Long timeStamp,
                                                            @RequestParam(value = "batch-size", required = false)
                                                            Integer batchSize) {
        List<FormSubmission> newSubmissionsForANM = formSubmissionService
                .getNewSubmissionsForLoc(locationId, timeStamp, batchSize);
        return with(newSubmissionsForANM).convert(new Converter<FormSubmission, FormSubmissionDTO>() {
            @Override
            public FormSubmissionDTO convert(FormSubmission submission) {
                return FormSubmissionConverter.from(submission);
            }
        });
    }

    @RequestMapping(method = GET, value="/all-form-submissions")
    @ResponseBody
    private List<FormSubmissionDTO> getAllFormSubmissions(@RequestParam("timestamp") Long timeStamp,
                                                          @RequestParam(value = "batch-size", required = false)
                                                          Integer batchSize) {
        List<FormSubmission> allSubmissions = formSubmissionService
                .getAllSubmissions(timeStamp, batchSize);
        return with(allSubmissions).convert(new Converter<FormSubmission, FormSubmissionDTO>() {
            @Override
            public FormSubmissionDTO convert(FormSubmission submission) {
                return FormSubmissionConverter.from(submission);
            }
        });
    }
    
 

    @RequestMapping(headers = {"Accept=application/json"}, method = POST, value = "/form-submissions")
    public ResponseEntity<HttpStatus> submitForms(@RequestBody List<FormSubmissionDTO> formSubmissionsDTO) {
        try {
            if (formSubmissionsDTO.isEmpty()) {
                return new ResponseEntity<>(BAD_REQUEST);
            }

            scheduler.notifyEvent(new SystemEvent<>(AllConstants.OpenSRPEvent.FORM_SUBMISSION, formSubmissionsDTO));
            
            logger.debug(format("Added Form submissions to queue.\nSubmissions: {0}", formSubmissionsDTO));
        } catch (Exception e) {
            logger.error(format("Form submissions processing failed with exception {0}.\nSubmissions: {1}", e, formSubmissionsDTO));
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(CREATED);
    }

    private void addFormToOpenMRS(FormSubmission formSubmission) throws ParseException, IllegalStateException, JSONException{
//    	if(formEntityConverter.isOpenmrsForm(formSubmission)){
    		Client c = formEntityConverter.getClientFromFormSubmission(formSubmission);
			Event e = formEntityConverter.getEventFromFormSubmission(formSubmission);
			Map<String, Map<String, Object>> dep = formEntityConverter.getDependentClientsFromFormSubmission(formSubmission);

    		// TODO temporary because not necessarily we register inner entity for Household only
    		if(formSubmission.formName().toLowerCase().contains("household")
    				|| formSubmission.formName().toLowerCase().contains("census") ){
    			OpenmrsHouseHold hh = new OpenmrsHouseHold(c, e);
    			for (Map<String, Object> cm : dep.values()) {
    				hh.addHHMember((Client)cm.get("client"), (Event)cm.get("event"));
    			}

    			householdService.saveHH(hh, true);
    		}
    		else {
    			JSONObject p = patientService.getPatientByIdentifier(c.getBaseEntityId());
    			if(p == null){
    				System.out.println(patientService.createPatient(c));
    			}

    			System.out.println(encounterService.createEncounter(e));

    			for (Map<String, Object> cm : dep.values()) {
    				Client cin = (Client)cm.get("client");
    				Event evin = (Event)cm.get("event");
    				JSONObject pin = patientService.getPatientByIdentifier(cin.getBaseEntityId());
        			if(pin == null){
        				System.out.println(patientService.createPatient(cin));
        			}

        			System.out.println(encounterService.createEncounter(evin));
    			}
    		}
    	//}
    }
    @RequestMapping(headers = {"Accept=application/json"}, method = GET, value = "/multimedia-file")
    @ResponseBody
    public List<MultimediaDTO> getFiles(@RequestParam(value ="anm-id", required = false) String providerId,@RequestParam(value ="locationid", required = false) String locationId) {
        List<Multimedia> allMultimedias = new ArrayList<Multimedia>();
        if(locationId!=null){
            allMultimedias = multimediaService.getMultimediaFilesByLocId(locationId);
        }else if(providerId!=null){
            allMultimedias = multimediaService.getMultimediaFiles(providerId);
        }
    	
    	return with(allMultimedias).convert(new Converter<Multimedia, MultimediaDTO>() {
			@Override
			public MultimediaDTO convert(Multimedia md) {
				return new MultimediaDTO(md.getCaseId(), md.getProviderId(), md.getContentType(), md.getFilePath(),md.getLocationId(), md.getFileCategory(),md.getAttributes());
			}
		});
    }
    
}
