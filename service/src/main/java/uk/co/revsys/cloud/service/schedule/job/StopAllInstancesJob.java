package uk.co.revsys.cloud.service.schedule.job;

import java.util.Set;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.ComputeMetadata;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import uk.co.revsys.cloud.service.ComputeServiceFactory;

public class StopAllInstancesJob implements Job {

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        ComputeService computeService = ComputeServiceFactory.getComputeService();
        Set<? extends ComputeMetadata> instances = computeService.listNodes();
        for (ComputeMetadata instance : instances) {
            if ("true".equalsIgnoreCase(instance.getUserMetadata().get("StopOvernight"))) {
                if ("true".equalsIgnoreCase(instance.getUserMetadata().get("SpotInstance"))) {
                    computeService.destroyNode(instance.getId());
                } else {
                    computeService.suspendNode(instance.getId());
                }
            }
        }
    }

}
