/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.api.impl;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.api.RestHelperService;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.CustomRepresentation;
import org.openmrs.module.webservices.rest.web.representation.NamedRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.Resource;
import org.openmrs.module.webservices.rest.web.resource.api.SearchConfig;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.response.InvalidSearchException;
import org.openmrs.test.BaseContextMockTest;
import org.openmrs.util.OpenmrsConstants;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests {@link RestServiceImpl}.
 */
public class RestServiceImplTest extends BaseContextMockTest {
	
	@Mock
	RestHelperService restHelperService;
	
	@InjectMocks
	RestService restService = new RestServiceImpl();
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	/**
	 * @verifies return default representation if given null
	 * @see RestServiceImpl#getRepresentation(String)
	 */
	@Test
	public void getRepresentation_shouldReturnDefaultRepresentationIfGivenNull() throws Exception {
		
		assertThat(restService.getRepresentation(null), is(Representation.DEFAULT));
	}
	
	/**
	 * @verifies return default representation if given string is empty
	 * @see RestServiceImpl#getRepresentation(String)
	 */
	@Test
	public void getRepresentation_shouldReturnDefaultRepresentationIfGivenStringIsEmpty() throws Exception {
		
		assertThat(restService.getRepresentation(""), is(Representation.DEFAULT));
	}
	
	/**
	 * @verifies return reference representation if given string matches the ref representation
	 *           constant
	 * @see RestServiceImpl#getRepresentation(String)
	 */
	@Test
	public void getRepresentation_shouldReturnReferenceRepresentationIfGivenStringMatchesTheRefRepresentationConstant()
	        throws Exception {
		
		RestUtil.disableContext(); //to avoid a Context call
		assertThat(restService.getRepresentation("ref"), is(Representation.REF));
	}
	
	/**
	 * @verifies return default representation if given string matches the default representation
	 *           constant
	 * @see RestServiceImpl#getRepresentation(String)
	 */
	@Test
	public void getRepresentation_shouldReturnDefaultRepresentationIfGivenStringMatchesTheDefaultRepresentationConstant()
	        throws Exception {
		
		RestUtil.disableContext(); //to avoid a Context call
		assertThat(restService.getRepresentation("default"), is(Representation.DEFAULT));
	}
	
	/**
	 * @verifies return full representation if given string matches the full representation constant
	 * @see RestServiceImpl#getRepresentation(String)
	 */
	@Test
	public void getRepresentation_shouldReturnFullRepresentationIfGivenStringMatchesTheFullRepresentationConstant()
	        throws Exception {
		
		RestUtil.disableContext(); //to avoid a Context call
		assertThat(restService.getRepresentation("full"), is(Representation.FULL));
	}
	
	/**
	 * @verifies return an instance of custom representation if given string starts with the custom
	 *           representation prefix
	 * @see RestServiceImpl#getRepresentation(String)
	 */
	@Test
	public void getRepresentation_shouldReturnAnInstanceOfCustomRepresentationIfGivenStringStartsWithTheCustomRepresentationPrefix()
	        throws Exception {
		
		RestUtil.disableContext(); //to avoid a Context call
		Representation representation = restService.getRepresentation("custom:datatableslist");
		assertThat(representation, instanceOf(CustomRepresentation.class));
		assertThat(representation.getRepresentation(), is("datatableslist"));
	}
	
	/**
	 * @verifies return an instance of named representation for given string if it is not empty and
	 *           does not match any other case
	 * @see RestServiceImpl#getRepresentation(String)
	 */
	@Test
	public void getRepresentation_shouldReturnAnInstanceOfNamedRepresentationForGivenStringIfItIsNotEmptyAndDoesNotMatchAnyOtherCase()
	        throws Exception {
		
		RestUtil.disableContext(); //to avoid a Context call
		Representation representation = restService.getRepresentation("UNKNOWNREPRESENTATION");
		assertThat(representation, instanceOf(NamedRepresentation.class));
		assertThat(representation.getRepresentation(), is("UNKNOWNREPRESENTATION"));
	}
	
	/**
	 * @see RestServiceImpl#getSearchHandler(String,Map)
	 * @verifies throw exception if no handler with id
	 */
	@Test
	public void getSearchHandler_shouldThrowExceptionIfNoHandlerWithId() throws Exception {
		
		SearchHandler searchHandler = mock(SearchHandler.class);
		SearchConfig searchConfig = new SearchConfig("none", "concept", "1.8.*", new SearchQuery.Builder("Fuzzy search")
		        .withRequiredParameters("q").build());
		when(searchHandler.getSearchConfig()).thenReturn(searchConfig);
		when(restHelperService.getRegisteredSearchHandlers()).thenReturn(Arrays.asList(searchHandler));
		
		Map<String, String[]> parameters = new HashMap<String, String[]>();
		parameters.put("s", new String[] { "conceptByMapping" });
		
		expectedException.expect(InvalidSearchException.class);
		expectedException.expectMessage("The search with id 'conceptByMapping' for '" + searchConfig.getSupportedResource()
		        + "' resource is not recognized");
		restService.getSearchHandler("concept", parameters);
	}
	
	/**
	 * @see RestServiceImpl#getSearchHandler(String,Map)
	 * @verifies return handler by id if exists
	 */
	@Test
	public void getSearchHandler_shouldReturnHandlerByIdIfExists() throws Exception {
		
		SearchHandler searchHandler = mock(SearchHandler.class);
		SearchConfig searchConfig = new SearchConfig("conceptByMapping", "concept", OpenmrsConstants.OPENMRS_VERSION_SHORT,
		        new SearchQuery.Builder("Fuzzy search").withRequiredParameters("q").build());
		when(searchHandler.getSearchConfig()).thenReturn(searchConfig);
		when(restHelperService.getRegisteredSearchHandlers()).thenReturn(Arrays.asList(searchHandler));
		
		Map<String, String[]> parameters = new HashMap<String, String[]>();
		parameters.put("s", new String[] { "conceptByMapping" });
		SearchHandler searchHandler2 = restService.getSearchHandler("concept", parameters);
		assertThat(searchHandler2, is(searchHandler));
	}
	
	/**
	 * @see RestServiceImpl#getSearchHandler(String,Map)
	 * @verifies throw ambiguous exception if case 1
	 */
	@Test
	public void getSearchHandler_shouldThrowAmbiguousExceptionIfCase1() throws Exception {
		
		SearchHandler searchHandler = mock(SearchHandler.class);
		SearchConfig searchConfig = new SearchConfig("conceptByMapping", "concept", OpenmrsConstants.OPENMRS_VERSION_SHORT,
		        new SearchQuery.Builder("description").withRequiredParameters("sourceName", "code").build());
		when(searchHandler.getSearchConfig()).thenReturn(searchConfig);
		
		SearchHandler searchHandler2 = mock(SearchHandler.class);
		SearchConfig searchConfig2 = new SearchConfig("conceptByMapping2", "concept",
		        OpenmrsConstants.OPENMRS_VERSION_SHORT, new SearchQuery.Builder("description").withRequiredParameters(
		            "sourceName", "code").build());
		when(searchHandler2.getSearchConfig()).thenReturn(searchConfig2);
		
		when(restHelperService.getRegisteredSearchHandlers()).thenReturn(Arrays.asList(searchHandler, searchHandler2));
		
		RestUtil.disableContext(); //to avoid a Context call
		
		Map<String, String[]> parameters = new HashMap<String, String[]>();
		parameters.put("sourceName", new String[] { "some name" });
		parameters.put("code", new String[] { "some code" });
		
		expectedException.expect(InvalidSearchException.class);
		expectedException.expectMessage("The search is ambiguous. Please specify s=");
		restService.getSearchHandler("concept", parameters);
	}
	
	public Set<SearchQuery> newParameters(SearchQuery... parameters) {
		return new HashSet<SearchQuery>(Arrays.asList(parameters));
	}
	
	/**
	 * @see RestServiceImpl#getSearchHandler(String,Map)
	 * @verifies return handler if case 2
	 */
	@Test
	public void getSearchHandler_shouldReturnHandlerIfCase2() throws Exception {
		
		SearchHandler searchHandler = mock(SearchHandler.class);
		SearchConfig searchConfig = new SearchConfig("conceptByMapping", "concept", OpenmrsConstants.OPENMRS_VERSION_SHORT,
		        new SearchQuery.Builder("description").withRequiredParameters("sourceName").withOptionalParameters("code")
		                .build());
		when(searchHandler.getSearchConfig()).thenReturn(searchConfig);
		
		SearchHandler searchHandler2 = mock(SearchHandler.class);
		SearchConfig searchConfig2 = new SearchConfig("conceptByMapping2", "concept",
		        OpenmrsConstants.OPENMRS_VERSION_SHORT, new SearchQuery.Builder("description").withRequiredParameters(
		            "sourceName").build());
		when(searchHandler2.getSearchConfig()).thenReturn(searchConfig2);
		
		when(restHelperService.getRegisteredSearchHandlers()).thenReturn(Arrays.asList(searchHandler, searchHandler2));
		
		RestUtil.disableContext(); //to avoid a Context call
		
		Map<String, String[]> parameters = new HashMap<String, String[]>();
		parameters.put("sourceName", new String[] { "some name" });
		parameters.put("code", new String[] { "some code" });
		SearchHandler searchHandler3 = restService.getSearchHandler("concept", parameters);
		
		assertThat(searchHandler3, is(searchHandler));
	}
	
	/**
	 * @see RestServiceImpl#getSearchHandler(String,Map)
	 * @verifies throw ambiguous exception if case 3
	 */
	@Test
	public void getSearchHandler_shouldThrowAmbiguousExceptionIfCase3() throws Exception {
		
		SearchHandler searchHandler = mock(SearchHandler.class);
		SearchConfig searchConfig = new SearchConfig("conceptByMapping", "concept", OpenmrsConstants.OPENMRS_VERSION_SHORT,
		        new SearchQuery.Builder("description").withRequiredParameters("sourceName").withOptionalParameters("code")
		                .build());
		when(searchHandler.getSearchConfig()).thenReturn(searchConfig);
		
		SearchHandler searchHandler2 = mock(SearchHandler.class);
		SearchConfig searchConfig2 = new SearchConfig("conceptByMapping2", "concept",
		        OpenmrsConstants.OPENMRS_VERSION_SHORT, new SearchQuery.Builder("description").withRequiredParameters(
		            "sourceName").build());
		when(searchHandler2.getSearchConfig()).thenReturn(searchConfig2);
		
		when(restHelperService.getRegisteredSearchHandlers()).thenReturn(Arrays.asList(searchHandler, searchHandler2));
		
		RestUtil.disableContext(); //to avoid a Context call
		
		Map<String, String[]> parameters = new HashMap<String, String[]>();
		parameters.put("sourceName", new String[] { "some name" });
		
		expectedException.expect(InvalidSearchException.class);
		expectedException.expectMessage("The search is ambiguous. Please specify s=");
		restService.getSearchHandler("concept", parameters);
	}
	
	/**
	 * @see RestServiceImpl#getSearchHandler(String,Map)
	 * @verifies return null if too few parameters
	 */
	@Test
	public void getSearchHandler_shouldReturnNullIfTooFewParameters() throws Exception {
		
		SearchHandler searchHandler = mock(SearchHandler.class);
		SearchConfig searchConfig = new SearchConfig("conceptByMapping", "concept", OpenmrsConstants.OPENMRS_VERSION_SHORT,
		        new SearchQuery.Builder("description").withRequiredParameters("sourceName").withOptionalParameters("code")
		                .build());
		when(searchHandler.getSearchConfig()).thenReturn(searchConfig);
		
		SearchHandler searchHandler2 = mock(SearchHandler.class);
		SearchConfig searchConfig2 = new SearchConfig("conceptByMapping2", "concept",
		        OpenmrsConstants.OPENMRS_VERSION_SHORT, new SearchQuery.Builder("description").withRequiredParameters(
		            "sourceName").build());
		when(searchHandler2.getSearchConfig()).thenReturn(searchConfig2);
		
		when(restHelperService.getRegisteredSearchHandlers()).thenReturn(Arrays.asList(searchHandler, searchHandler2));
		
		RestUtil.disableContext(); //to avoid a Context call
		
		Map<String, String[]> parameters = new HashMap<String, String[]>();
		parameters.put("code", new String[] { "some name" });
		
		SearchHandler searchHandler3 = restService.getSearchHandler("concept", parameters);
		assertThat(searchHandler3, nullValue());
	}
	
	/**
	 * @see RestServiceImpl#getSearchHandler(String,Map)
	 * @verifies return null if resource does not match
	 */
	@Test
	public void getSearchHandler_shouldReturnNullIfResourceDoesNotMatch() throws Exception {
		
		SearchHandler searchHandler = mock(SearchHandler.class);
		SearchConfig searchConfig = new SearchConfig("conceptByMapping", "concept", OpenmrsConstants.OPENMRS_VERSION_SHORT,
		        new SearchQuery.Builder("description").withRequiredParameters("sourceName").build());
		when(searchHandler.getSearchConfig()).thenReturn(searchConfig);
		
		when(restHelperService.getRegisteredSearchHandlers()).thenReturn(Arrays.asList(searchHandler));
		
		RestUtil.disableContext(); //to avoid a Context call
		
		Map<String, String[]> parameters = new HashMap<String, String[]>();
		parameters.put("sourceName", new String[] { "some name" });
		
		SearchHandler searchHandler2 = restService.getSearchHandler("nonexistingresource", parameters);
		assertThat(searchHandler2, nullValue());
	}
	
	@Test
	public void getResourceBySupportedClass_shouldReturnTheMostExactMatch() throws Exception {
		//Given
		RestServiceImpl service = new RestServiceImpl();
		
		Resource personResource = mock(Resource.class);
		Resource patientResource = mock(Resource.class);
		
		//Mocked for deterministic order
		service.resourceDefinitionsByNames = new LinkedHashMap<String, RestServiceImpl.ResourceDefinition>();
		service.resourcesBySupportedClasses = new LinkedHashMap<Class<?>, Resource>();
		
		service.resourcesBySupportedClasses.put(Person.class, personResource);
		service.resourcesBySupportedClasses.put(Patient.class, patientResource);
		
		//When
		Resource resource = service.getResourceBySupportedClass(ChildPatient.class);
		
		//Then
		assertThat(resource, is(patientResource));
	}
	
	public static class ChildPatient extends Patient {};
}
