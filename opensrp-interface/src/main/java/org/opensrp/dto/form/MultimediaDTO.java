package org.opensrp.dto.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.HashMap;
import java.util.Map;

public class MultimediaDTO {

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
	
	public MultimediaDTO()
	{
		
	}
	public MultimediaDTO(String caseId, String providerId, String contentType, String filePath,String fileCategory, String locationId) {
		this.caseId = caseId;
		this.providerId = providerId;
		this.contentType = contentType;
		this.filePath = filePath;
		this.fileCategory = fileCategory;
		this.locationId = locationId;
	}
	public MultimediaDTO(String caseId, String providerId, String contentType, String filePath,String fileCategory, String locationId, Map<String, String> attributes) {
		this.caseId = caseId;
		this.providerId = providerId;
		this.contentType = contentType;
		this.filePath = filePath;
		this.fileCategory = fileCategory;
		this.locationId = locationId;
		this.attributes = attributes;
	}

	public String caseId() {
		return this.caseId;
	}

	public String providerId() {
		return this.providerId;
	}

	public String contentType() {
		return this.contentType;
	}

	public String filePath() {
		return this.filePath;
	}

	public String fileCategory() {
		return this.fileCategory;
	}

	public String locationId() { return this.locationId; }

	public Map<String, String> attributes() {
		return this.attributes;
	}

	public MultimediaDTO withAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
		return this;
	}

	public MultimediaDTO withAttributes(String name, String value) {
		if (attributes == null) {
			attributes = new HashMap<>();
		}
		attributes.put(name, value);
		return this;
	}

	public Map<String, String> getAttributes() {
		if (attributes == null) {
			attributes = new HashMap<>();
		}
		return attributes;
	}
	public String getAttributes(String name) {
		return attributes.get(name);
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
		return EqualsBuilder.reflectionEquals(this, o);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	public MultimediaDTO withFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }

}
