package uk.co.revsys.cloud.service.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.ComputeMetadata;
import uk.co.revsys.cloud.service.ComputeServiceFactory;

@Path("/")
public class InstanceRestService {
    
    private final ComputeService computeService;
    private final ObjectMapper objectMapper;

    public InstanceRestService() {
        this.computeService = ComputeServiceFactory.getComputeService();
        this.objectMapper = new ObjectMapper();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInstances() {
        try {
            Set<? extends ComputeMetadata> instances = computeService.listNodes();
            return Response.ok(objectMapper.writeValueAsString(instances)).header("Access-Control-Allow-Origin", "*").build();
        } catch (JsonProcessingException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("/{id}/start")
    @Produces(MediaType.APPLICATION_JSON)
    public Response startInstance(@PathParam("id") String id) {
        id = id.replace("|", "/");
        computeService.resumeNode(id);
        return getInstances();
    }

    @GET
    @Path("/{id}/stop")
    @Produces(MediaType.APPLICATION_JSON)
    public Response stopInstance(@PathParam("id") String id) {
        id = id.replace("|", "/");
        computeService.suspendNode(id);
        return getInstances();
    }
    
    @GET
    @Path("/{id}/terminate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response terminateInstance(@PathParam("id") String id) {
        id = id.replace("|", "/");
        computeService.destroyNode(id);
        return getInstances();
    }

}
