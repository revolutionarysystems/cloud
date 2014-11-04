package uk.co.revsys.cloud.service;

import java.util.List;

public class SpotImage {

    private String name;
    private String type;
    private String ipAddress;
    private float spotPrice;
    private boolean stopOvernight;
    private List<String> securityGroups;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public float getSpotPrice() {
        return spotPrice;
    }

    public void setSpotPrice(float spotPrice) {
        this.spotPrice = spotPrice;
    }

    public boolean getStopOvernight() {
        return stopOvernight;
    }

    public void setStopOvernight(boolean stopOvernight) {
        this.stopOvernight = stopOvernight;
    }

    public List<String> getSecurityGroups() {
        return securityGroups;
    }

    public void setSecurityGroups(List<String> securityGroups) {
        this.securityGroups = securityGroups;
    }
    
}
