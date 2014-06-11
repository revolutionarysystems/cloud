package uk.co.revsys.cloud.service.rest;

import org.jclouds.aws.ec2.AWSEC2Api;
import org.jclouds.aws.ec2.compute.AWSEC2ComputeService;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.ec2.features.ElasticIPAddressApi;
import org.jclouds.rest.ApiContext;

public class AssociateElasticIPRunnable implements Runnable {
	
	private AWSEC2ComputeService computeService;
    private ElasticIPAddressApi elasticIPAddressApi;
	private String instanceId;
	private String elasticIp;

	private boolean associated = false;
	private int pollFrequency = 3000;
	private int elapsedTime = 0;
	private int threshold = 180000;

	public AssociateElasticIPRunnable(AWSEC2ComputeService computeService, String instanceId, String elasticIp) {
		this.computeService = computeService;
        this.elasticIPAddressApi = (ElasticIPAddressApi) ((ApiContext<AWSEC2Api>)computeService.getContext().unwrap()).getApi().getElasticIPAddressApi().get();
		this.instanceId = instanceId;
		this.elasticIp = elasticIp;
	}

	@Override
	public void run() {
		while (!associated) {
			associated = associateAddress();
			if (!associated) {
				try {
					elapsedTime = elapsedTime + pollFrequency;
					if(elapsedTime > threshold){
						throw new RuntimeException("Unable to associate elastic ip with " + instanceId + ". Instance did not start within the time limit of " + threshold + "ms");
					}
					Thread.sleep(pollFrequency);
				} catch (InterruptedException ex) {
					throw new RuntimeException(ex);
				}
			}
		}
	}

	private boolean associateAddress() {
        System.out.println("associating address");
        NodeMetadata instance = computeService.getNodeMetadata(instanceId);
        NodeMetadata.Status status = instance.getStatus();
        if(status.equals(NodeMetadata.Status.RUNNING)){
            System.out.println("associating address");
            elasticIPAddressApi.associateAddressInRegion(instance.getLocation().getId(), elasticIp, instance.getProviderId());
            System.out.println("associated");
            return true;
        }
        System.out.println("not running yet");
        return false;
	}

}

