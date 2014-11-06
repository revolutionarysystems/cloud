package uk.co.revsys.cloud.service.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import java.util.UUID;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.jclouds.aws.ec2.AWSEC2Api;
import org.jclouds.aws.ec2.compute.AWSEC2TemplateOptions;
import org.jclouds.aws.ec2.features.SpotInstanceApi;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.ec2.features.ElasticIPAddressApi;
import org.jclouds.rest.ApiContext;
import uk.co.revsys.cloud.service.ComputeServiceFactory;
import uk.co.revsys.cloud.service.DNSSettings;
import uk.co.revsys.cloud.service.DaoException;
import uk.co.revsys.cloud.service.SpotImage;
import uk.co.revsys.cloud.service.SpotImageDao;

@Path("/image")
public class ImageRestService {

    private final ComputeService computeService;
    private final ElasticIPAddressApi elasticIPAddressApi;
    private final SpotInstanceApi spotInstanceApi;
    private final ObjectMapper objectMapper;
    private final SpotImageDao dao;
    private final String controller;
    private final String templateId;
    private final String keyPair;

    public ImageRestService(SpotImageDao dao, String templateId, String controller, String keyPair) {
        computeService = ComputeServiceFactory.getComputeService();
        this.elasticIPAddressApi = (ElasticIPAddressApi) ((ApiContext<AWSEC2Api>) computeService.getContext().unwrap()).getApi().getElasticIPAddressApi().get();
        this.spotInstanceApi = (SpotInstanceApi) ((ApiContext<AWSEC2Api>) computeService.getContext().unwrap()).getApi().getSpotInstanceApi().get();
        this.objectMapper = new ObjectMapper();
        this.dao = dao;
        this.templateId = templateId;
        this.controller = controller;
        this.keyPair = keyPair;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        try {
            return Response.ok(objectMapper.writeValueAsString(dao.findAll())).header("Access-Control-Allow-Origin", "*").build();
        } catch (DaoException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).header("Access-Control-Allow-Origin", "*").build();
        } catch (JsonProcessingException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).header("Access-Control-Allow-Origin", "*").build();
        }
    }

    @GET
    @Path("/{name}/start")
    public Response start(@PathParam("name") String name) {
        try {
            SpotImage image = dao.findByName(name);
            String alias = image.getName() + "-" + UUID.randomUUID().toString();
            Template template = computeService.templateBuilder().imageId(templateId).hardwareId(image.getType()).build();
            AWSEC2TemplateOptions templateOptions = template.getOptions().as(AWSEC2TemplateOptions.class);
            templateOptions.userMetadata("Name", image.getName()).userMetadata("alias", alias).userMetadata("SpotInstance", "true").userMetadata("StopOvernight", String.valueOf(image.getStopOvernight()));
            String userData = alias + "|" + controller;
            DNSSettings dnsSettings = image.getDnsSettings();
            if(dnsSettings!=null){
                userData = userData + "|" + dnsSettings.getZone() + "|" + dnsSettings.getDomain() + "|" + dnsSettings.getIpType();
                templateOptions.userMetadata("Domain", dnsSettings.getDomain());
            }
            templateOptions.userData(userData.getBytes());
            templateOptions.spotPrice(image.getSpotPrice());
            templateOptions.securityGroups(image.getSecurityGroups());
            templateOptions.keyPair(keyPair);
            System.out.println("Starting instance...");
            Set<? extends NodeMetadata> nodes = computeService.createNodesInGroup("group", 1, template);
            System.out.println("nodes = " + nodes);
            if (nodes.isEmpty()) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("Access-Control-Allow-Origin", "*").build();
            }
            NodeMetadata node = nodes.iterator().next();
            System.out.println("node = " + node);
            if (image.getIpAddress() != null) {
                elasticIPAddressApi.associateAddressInRegion(node.getLocation().getParent().getId(), image.getIpAddress(), node.getProviderId());
            }
            return Response.ok(objectMapper.writeValueAsString(node)).header("Access-Control-Allow-Origin", "*").build();
        } catch (RunNodesException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).header("Access-Control-Allow-Origin", "*").build();
        } catch (JsonProcessingException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).header("Access-Control-Allow-Origin", "*").build();
        } catch (DaoException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).header("Access-Control-Allow-Origin", "*").build();
        }
    }

}
