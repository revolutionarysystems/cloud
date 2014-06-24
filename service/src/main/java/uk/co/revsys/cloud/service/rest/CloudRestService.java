package uk.co.revsys.cloud.service.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.jclouds.ContextBuilder;
import org.jclouds.aws.ec2.compute.AWSEC2ComputeService;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.sshj.config.SshjSshClientModule;
import uk.co.revsys.cloud.service.ComputeServiceFactory;

@Path("/")
public class CloudRestService {

    private ExecutorService executorService = Executors.newCachedThreadPool();
    
    private final ComputeService computeService;
    private final ObjectMapper objectMapper;

    public CloudRestService() {
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
        if (computeService instanceof AWSEC2ComputeService) {
            NodeMetadata instance = computeService.getNodeMetadata(id);
            if (instance.getUserMetadata().containsKey("ElasticIP")) {
                String elasticIP = instance.getUserMetadata().get("ElasticIP");
                AWSEC2ComputeService awsComputeService = (AWSEC2ComputeService) computeService;
                executorService.submit(new AssociateElasticIPRunnable(awsComputeService, id, elasticIP));
            }
        }
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

    public static void main(String[] args) {
        ComputeServiceContext context = ContextBuilder.newBuilder("aws-ec2")
                .credentials("", "")
                .modules(ImmutableSet.<Module>of(new Log4JLoggingModule(),
                                new SshjSshClientModule()))
                .buildView(ComputeServiceContext.class);

        ComputeService computeService = context.getComputeService();
        ComputeServiceFactory.setComputeService(computeService);
        CloudRestService cloudRestService = new CloudRestService();
        System.out.println(cloudRestService.getInstances().getEntity());
        //System.out.println(cloudRestService.stopInstance("eu-west-1/i-7fb36f3c").getEntity());
        //System.out.println(cloudRestService.startInstance("eu-west-1/i-7fb36f3c").getEntity());
    }

}
