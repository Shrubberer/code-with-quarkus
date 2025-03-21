package org.acme;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import org.jboss.logging.Logger;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient; 
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.ws.rs.QueryParam;



@Path("/hello")
public class GreetingResource {

    private static final Logger LOG = Logger.getLogger(GreetingResource.class);
    
    @ConfigProperty(name = "sample.env.var") 
    String sampleEvnVar;
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response hello(@QueryParam("delay") Long delay,  @QueryParam("error") String error) {

        // Handle error parameter - return 503 if present
        if (error != null) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                          .entity("Service unavailable - intentional error")
                          .build();
        }

        if (delay != null && delay > 0) {
            try {
                Thread.sleep(delay);
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error: Interrupted during sleep")
                .build();
            }
        } 
        
        LOG.info(sampleEvnVar); 
        return Response.ok(sampleEvnVar).build();
        
    }
    
    @GET
    @Path("/ping")
    @Produces(MediaType.TEXT_PLAIN)
    public String ping() {
      return "pong";
    }


    @GET
    @Path("/error")
    @Produces(MediaType.TEXT_PLAIN)
    public Response error() {
        LOG.error("Simulating a 503 error for testing purposes");
        return Response.status(Response.Status.SERVICE_UNAVAILABLE) // HTTP 503
                      .entity("Simulated 503 error for testing")
                      .build();
    }
    
    @Path("/send")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String sendToPython() {
        try {
            ResteasyClient client = (ResteasyClient) ClientBuilder.newClient(); // Use javax.ws.rs.client.ClientBuilder
            ResteasyWebTarget target = client.target("http://pythonapp-python.apps.swtd-ocp4.unilab");
            String response = target.request().get(String.class);
            return "Message from Java: " + response;
        } catch (Exception e) {
            LOG.error("Error sending request to Python endpoint: " + e.getMessage());
            return "Error sending request to Python endpoint.";
        }
    }

 
}


