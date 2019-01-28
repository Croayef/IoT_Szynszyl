package com.tt.thingworx.client;

import com.thingworx.communications.client.ClientConfigurator;
import com.thingworx.communications.client.ConnectedThingClient;
import com.thingworx.communications.client.things.VirtualThing;
import com.thingworx.relationships.RelationshipTypes.ThingworxEntityTypes;
import com.thingworx.types.collections.ValueCollection;
import com.tt.thingworx.thing.Cage;
import com.tt.thingworx.thing.CageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client extends ConnectedThingClient {


    private static final Logger LOG = LoggerFactory.getLogger(Client.class);

    private static final String THINGWORX_ADDRESS = "192.168.49.132:8080";

    public Client(ClientConfigurator config) throws Exception {
        super(config);
    }

    public static void startConnection() {
        ClientConfigurator config = new ClientConfigurator();
        LOG.info("START");
        config.setUri("ws://" + THINGWORX_ADDRESS + "/Thingworx/WS");
        config.setAppKey("e6393a51-37d6-4472-b0fb-0d9f1a043b1b");
        config.ignoreSSLErrors(true);
        try {
            Client client = new Client(config);
            client.start();

            while (!client.getEndpoint().isConnected()) {
                Thread.sleep(1000);
                LOG.info("WAIT");
            }
            for (Cage cage : Cage.values()) {
                ValueCollection params = new ValueCollection();
                params.SetStringValue("ThingName", cage.name);
                client.invokeService(ThingworxEntityTypes.Things, "CageHelper", "CageHelperService", params, 5000);
            }
            for (Cage cage : Cage.values()) {
                CageTemplate cageTemplate = new CageTemplate(cage.name, "", client);
                client.bindThing(cageTemplate);
            }
            while (!client.isShutdown()) {
                if (client.isConnected()) {
                    LOG.info("SEND");
                    for (VirtualThing thing : client.getThings().values()) {
                        try {
                            thing.processScanRequest();
                        } catch (Exception eProcessing) {
                            System.out.println("Error Processing Scan Request for [" + thing.getName() + "] : " + eProcessing.getMessage());
                        }
                    }
                    LOG.info("SLEEP");
                    Thread.sleep(1000 * 10);
                }
            }
            LOG.info("END");
        } catch (Exception e) {
            LOG.info("ERROR");
            e.printStackTrace();
        }
    }
}
