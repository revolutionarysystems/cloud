package uk.co.revsys.cloud.service;

import org.jclouds.compute.ComputeService;

public class ComputeServiceFactory {

    private static ComputeService computeService;

    public static ComputeService getComputeService() {
        return computeService;
    }

    public static void setComputeService(ComputeService computeService) {
        ComputeServiceFactory.computeService = computeService;
    }
    
}
