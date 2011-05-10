package org.openmrs.module.webservices.rest.resource;

import org.openmrs.Patient;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.RequestContext;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.annotation.Resource;

/**
 * {@link Resource} for Patients, supporting standard CRUD operations, and listing and adding of the
 * "names" subresource
 */
@Resource("patient")
@Handler(supports=Patient.class, order=0)
public class PatientResource extends DataDelegatingCrudResource<Patient> {
		
	public PatientResource() {
		super(null);
	}
	
	public PatientResource(Patient patient) {
		super(patient);
	}

	/**
	 * @see org.openmrs.module.webservices.rest.resource.DelegatingCrudResource#newDelegate()
	 */
	@Override
    public Patient newDelegate() {
	    return new Patient();
    }
	
	/**
	 * @see org.openmrs.module.webservices.rest.resource.DelegatingCrudResource#saveDelegate()
	 */
	@Override
    public Patient saveDelegate() {
	    return Context.getPatientService().savePatient(delegate);
    }

	/**
	 * @see org.openmrs.module.webservices.rest.resource.DelegateConverter#fromString(java.lang.String)
	 */
	@Override
	public Patient fromString(String uuid) {
	    return Context.getPatientService().getPatientByUuid(uuid);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.resource.DelegatingCrudResource#delete(java.lang.String, org.openmrs.module.webservices.rest.RequestContext)
	 */
	@Override
	public void delete(String reason, RequestContext context) throws ResourceDeletionException {
	    Context.getPatientService().voidPatient(delegate, reason);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.resource.DelegatingCrudResource#purge(org.openmrs.module.webservices.rest.RequestContext)
	 */
	@Override
	public void purge(RequestContext context) throws ResourceDeletionException {
	    try {
	    	Context.getPatientService().purgePatient(delegate);
	    } catch (Exception ex) {
	    	throw new ResourceDeletionException(ex);
	    }
	}

	/**
	 * Lists current values for a given subresource
	 * @param subResourceName
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public Object listSubResource(String subResourceName, RequestContext context) throws Exception {
		return getPropertyWithRepresentation(subResourceName, context.getRepresentation());
    }
	
	/**
	 * Adds a new PersonName to the "names" subresource
	 * @param post
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public PersonNameResource createPersonName(SimpleObject post, RequestContext context) throws Exception {
		post.put("person", delegate);
		return (PersonNameResource) new PersonNameResource().create(post, context);
	}
	
}