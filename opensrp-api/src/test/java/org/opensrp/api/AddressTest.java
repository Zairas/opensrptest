package org.opensrp.api;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;
import org.opensrp.api.domain.Address;
import org.opensrp.common.AddressField;

public class AddressTest {

	@Test
	public void testAddress(){
		Address address = new Address("", null, null, null, null, null, null, null, null);
		address.addAddressField(AddressField.AREA, "areaVal");

		assertNotNull("Address field inserted was returned null", address.getAddressField(AddressField.AREA.name()));
		assertSame("Expected value of Address field not returned", address.getAddressField(AddressField.AREA.name()), "areaVal");

		assertTrue("Address should be ACTIVE when no endDate provided", address.isActive());
		
		address.setEndDate(new Date());
		assertFalse("Address should be IN-ACTIVE when endDate exists", address.isActive());

		assertTrue("Duration of Address should be -1 for no startDate", address.durationInDays()==-1);
		assertTrue("Duration of Address should be -1 for no startDate", address.durationInWeeks()==-1);
		assertTrue("Duration of Address should be -1 for no startDate", address.durationInMonths()==-1);
		assertTrue("Duration of Address should be -1 for no startDate", address.durationInYears()==-1);
	}
	
	@Test
	public void testAddress2(){
		Address address = new Address("Birthplace", new Date(), null, null, "2.222", "3.333", "75210", "Sindh", "Pakistan");
		
		assertEquals("Address type invalid", address.getAddressType(), "Birthplace");
		assertEquals("Lat value invalid", address.getLatitude(), "2.222");
		assertEquals("Long value invalid", address.getLongitute(), "3.333");
		assertEquals("Postcode value invalid", address.getPostalCode(), "75210");
		assertEquals("State value invalid", address.getState(), "Sindh");
		assertEquals("Country value invalid", address.getCountry(), "Pakistan");
	}
	
	@Test
	public void testAddress3(){
		Date sd = new Date();
		Date ed = new Date();
		Address address = new Address();
		address.withAddressType("Deathplace");
		address.withCountry("Pakistan");
		address.withState("Punjab");
		address.withPostalCode("75290");
		address.withStartDate(sd);
		address.withEndDate(ed);
		
		assertEquals("Address type invalid", address.getAddressType(), "Deathplace");
		assertEquals("Postcode value invalid", address.getPostalCode(), "75290");
		assertEquals("State value invalid", address.getState(), "Punjab");
		assertEquals("Country value invalid", address.getCountry(), "Pakistan");
		assertEquals("StartDate invalid", address.getStartDate(), sd);
		assertEquals("EndDate invalid", address.getStartDate(), ed);
	}
}
