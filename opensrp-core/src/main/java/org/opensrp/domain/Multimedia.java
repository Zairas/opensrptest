package org.opensrp.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

import java.util.HashMap;
import java.util.Map;

@TypeDiscriminator("doc.type == 'Multimedia'")
public class Multimedia extends MotechBaseDataObject {

	@JsonProperty
	private String caseId;
	@JsonProperty
	private String providerId;
	@JsonProperty
	private String contentType;
	@JsonProperty
	private String filePath;
	@JsonProperty
	private String fileCategory;
	@JsonProperty
	private String locationId;
	@JsonProperty
	private Map<String, String> attributes;
	
	
	public Multimedia() {

	}
	public Multimedia( String caseId, String providerId, String contentType, String filePath, String fileCategory, String locationId) {
		this.caseId = caseId;
		this.providerId  = providerId;
		this.contentType = contentType;
		this.filePath = filePath;
		this.fileCategory = fileCategory;
	}
	public Multimedia( String caseId, String providerId, String contentType, String filePath, String fileCategory, String locationId, Map<String, String> attributes) {
		this.caseId = caseId;
		this.providerId  = providerId; 
		this.contentType = contentType;
		this.filePath = filePath;
		this.fileCategory = fileCategory;
		this.attributes = attributes;
	}

	public Multimedia withCaseId(String caseId) {
		this.caseId = caseId;
		return this;
	}
	public Multimedia withProviderId(String providerId) {
		this.providerId = providerId;
		return this;
	}

	public Multimedia withContentType(String contentType) {
		this.contentType = contentType;
		return this;
	}

	public Multimedia withFilePath(String filePath) {
		this.filePath = filePath;
		return this;
	}

	public Multimedia withFileCategory(String fileCategory) {
		this.fileCategory = fileCategory;
		return this;
	}

	public Multimedia withLocationId(String locationId) {
		this.locationId = locationId;
		return this;
	}

	public Multimedia withAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
		return this;
	}

	public Multimedia withAttributes(String name, String value) {
		if (attributes == null) {
			attributes = new HashMap<>();
		}
		attributes.put(name, value);
		return this;
	}
	
	public String getCaseId() {
		return caseId;
	}
	public String getProviderId() {
		return providerId;
	}
	public String getContentType() {
		return contentType;
	}
	public String getFilePath() {
		return filePath;
	}
	public String getFileCategory() {return fileCategory; }
	public String getLocationId() {return locationId; }
	public Map<String, String> getAttributes() {
		if (attributes == null) {
			attributes = new HashMap<>();
		}
		return attributes;
	}
	public String getAttributes(String name) {
		return attributes.get(name);
	}

	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}
	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public void setFileCategory(String fileCategory) {
		this.fileCategory = fileCategory;
	}
	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}
	public void setAttributes(String name, String value) {
		this.attributes = attributes;
	}
	public void addAttribute(String name, String value) {
		if (attributes == null) {
			attributes = new HashMap<>();
		}

		attributes.put(name, value);
	}
	public void removeAttribute(String name) {
		attributes.remove(name);
	}

	@Override
	public boolean equals(Object o) {
		return EqualsBuilder.reflectionEquals(this, o, "id");
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this, "id");
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
