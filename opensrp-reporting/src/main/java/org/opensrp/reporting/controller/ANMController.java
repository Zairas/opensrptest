package org.opensrp.reporting.controller;

import ch.lambdaj.function.convert.Converter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.opensrp.common.domain.UserDetail;
import org.opensrp.common.util.HttpAgent;
import org.opensrp.common.util.HttpResponse;
import org.opensrp.common.util.VerhouffUtil;
import org.opensrp.dto.ANMDTO;
import org.opensrp.dto.LastIdDTO;
import org.opensrp.dto.LocationDTO;
import org.opensrp.dto.UniqueIdDTO;
import org.opensrp.reporting.domain.LastId;
import org.opensrp.reporting.domain.Location;
import org.opensrp.reporting.domain.SP_ANM;
import org.opensrp.reporting.domain.UniqueId;
import org.opensrp.reporting.factory.DetailsFetcherFactory;
import org.opensrp.reporting.service.ANMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.opensrp.reporting.HttpHeaderFactory.allowOrigin;
import static ch.lambdaj.collection.LambdaCollections.with;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@Controller
public class ANMController {
    private String opensrpSiteUrl;
    private String opensrpUserDetailsUrl;
    private ANMService anmService;
    private HttpAgent httpAgent;
    private DetailsFetcherFactory detailsFetcherFactory;

    @Autowired
    public ANMController(@Value("#{opensrp['opensrp.site.url']}") String opensrpSiteUrl,
                         @Value("#{opensrp['opensrp.user.details.url']}") String opensrpUserDetailsUrl,
                         ANMService anmService, HttpAgent httpAgent, DetailsFetcherFactory detailsFetcherFactory) {
        this.opensrpSiteUrl = opensrpSiteUrl;
        this.opensrpUserDetailsUrl = opensrpUserDetailsUrl;
        this.anmService = anmService;
        this.httpAgent = httpAgent;
        this.detailsFetcherFactory = detailsFetcherFactory;
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.OPTIONS}, value = "/anm-details")
    @ResponseBody
    public ResponseEntity<List<ANMDTO>> all(@RequestParam("anm-id") String anmIdentifier) {
        HttpResponse response;
        try {
            response = httpAgent.get(opensrpUserDetailsUrl + "?anm-id=" + anmIdentifier);
            UserDetail userDetail = new Gson().fromJson(response.body(),
                    new TypeToken<UserDetail>() {
                    }.getType()
            );
            ANMDetailsFetcher anmDetailsFetcher = detailsFetcherFactory.detailsFetcher(userDetail.roles());

            List<ANMDTO> anmDTOList = convertToDTO(anmDetailsFetcher.fetchDetails(anmIdentifier));
            return new ResponseEntity<>(anmDTOList, allowOrigin(opensrpSiteUrl), OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(null, allowOrigin(opensrpSiteUrl), OK);
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.OPTIONS}, value = "/unique-id")
    @ResponseBody
    public ResponseEntity<UniqueIdDTO> getUniqueId(@RequestParam("anm-id") String anmIdentifier){
        List uniqueIdsforAnm = anmService.getUniqueIdsforAnm(anmIdentifier);
        List<UniqueIdDTO> dtos = convertToUniqueIdDTo(uniqueIdsforAnm);
        UniqueIdDTO dto = null;
        if(dtos != null && !dtos.isEmpty()){
            dto = dtos.get(dtos.size()-1);
        }
        return new ResponseEntity<UniqueIdDTO>(dto,allowOrigin("*"),OK);
    }


    @RequestMapping(method = {RequestMethod.GET, RequestMethod.OPTIONS}, value = "/last-used-id")
    @ResponseBody
    public ResponseEntity<LastIdDTO> getLastUsedId(@RequestParam("anm-id") String anmIdentifier){
        List lastUsedIdForAnm = anmService.getLastUsedIdForAnm(anmIdentifier);
        List<LastIdDTO> dtos = convertToLastIdDTO(lastUsedIdForAnm);
        LastIdDTO dto = null;
        if(dtos != null && !dtos.isEmpty()){
            if(dtos.size()>1) {
                dto = Collections.max(dtos, new Comparator<LastIdDTO>() {
                    @Override
                    public int compare(LastIdDTO o1, LastIdDTO o2) {
                        return o1.getLastUsedId() > o2.getLastUsedId() ? 1 : 0;
                    }
                });
            } else {
                dto = dtos.get(0);
            }
            dto.setStatus(1);
        }
        return new ResponseEntity<>(dto,allowOrigin("*"),OK);
    }

    @RequestMapping(method = {RequestMethod.POST, RequestMethod.OPTIONS}, value ="/last-id")
    @ResponseBody
    public ResponseEntity<LastIdDTO> setLastId(@RequestParam("anm-id") String anmIdentifier, @RequestParam("last-id") String lastId){
        int status = anmService.insertlastIdforAnm(anmIdentifier,Long.parseLong(lastId)) ? 1:0;
        LastIdDTO lastIdDTO = new LastIdDTO();
        lastIdDTO.setStatus(status);
        lastIdDTO.setLastUsedId(Long.parseLong(lastId));
        return  new ResponseEntity<>(lastIdDTO,allowOrigin("*"),OK);
    }

    @RequestMapping(method = {RequestMethod.POST, RequestMethod.OPTIONS}, value="/refill-unique-id")
    @ResponseBody
    public ResponseEntity<UniqueIdDTO> refillUniqueId(@RequestParam("anm-id") String anmIdentifier) {
        boolean status = anmService.refillUniqueId(anmIdentifier);
        if (status) {
            return getUniqueId(anmIdentifier);
        }
        UniqueIdDTO dto = null;
        return new ResponseEntity<>(dto, OK);
    }

    private List<UniqueIdDTO> convertToUniqueIdDTo(List<UniqueId> uniqueIds) {
        return with(uniqueIds).convert(new Converter<UniqueId, UniqueIdDTO>() {
            @Override
            public UniqueIdDTO convert(UniqueId uniqueId){
                UniqueIdDTO dto = new UniqueIdDTO();
                for (long i = uniqueId.getLastValue() - UniqueId.INCREMENT + 1; i<= uniqueId.getLastValue();i++){
                    String verhouffId = VerhouffUtil.generateVerhoeff(Long.toString(i));
                    String withVerhouff = i + "" + verhouffId;
                    dto.getIds().add(Long.parseLong(withVerhouff));
                }
                return dto;
            }
        });
    }

    private List<LastIdDTO> convertToLastIdDTO(List<LastId> lastIds) {
        return with(lastIds).convert(new Converter<LastId, LastIdDTO>() {
            @Override
            public LastIdDTO convert(LastId from) {
                return new LastIdDTO(from.getLastId());
            }
        });
    }

    private List<ANMDTO> convertToDTO(List<SP_ANM> anms) {
        return with(anms).convert(new Converter<SP_ANM, ANMDTO>() {
            @Override
            public ANMDTO convert(SP_ANM anm) {
                LocationDTO location = convertToDTO(anmService.getLocation(anm.identifier()));
                return new ANMDTO(anm.identifier(), anm.name(), location);
            }

            private LocationDTO convertToDTO(Location location) {
                return new LocationDTO(location.subCenter(), location.phcName(), location.taluka(), location.district(), location.state());
            }
        });
    }
}
