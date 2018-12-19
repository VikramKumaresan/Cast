package com.example.vikram.cast.clientCode.ServerDiscoveryFragment;

public class DiscoveredServer {
    private String serverName;
    private String endpointId;

    public DiscoveredServer(String serverName, String endpointId) {
        this.serverName = serverName;
        this.endpointId = endpointId;
    }

    public String getEndpointId() {
        return endpointId;
    }

    public String getServerName() {
        return serverName;
    }
}
