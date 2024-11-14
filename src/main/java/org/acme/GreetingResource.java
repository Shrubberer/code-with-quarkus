package org.acme;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import org.jboss.logging.Logger;

import org.jboss.resteasy.client.jaxrs.ResteasyClient; 
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

mport org.eclipse.microprofile.config.inject.ConfigProperty;


@Path("/hello")
public class GreetingResource {

    private static final Logger LOG = Logger.getLogger(GreetingResource.class);
    
    @ConfigProperty(name = "sample.env.var") 
    String sampleEvnVar;
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        LOG.info("Aloha from version 1.0.8!"); 
        return "Aloha from version 1.0.8!" + sampleEvnVar;
        
    }
    
    @GET
    @Path("/ping")
    @Produces(MediaType.TEXT_PLAIN)
    public String ping() {
      return "pong";
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


