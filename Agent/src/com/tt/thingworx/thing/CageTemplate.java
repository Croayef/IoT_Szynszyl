package com.tt.thingworx.thing;


import com.thingworx.communications.client.ConnectedThingClient;
import com.thingworx.communications.client.things.VirtualThing;
import com.thingworx.metadata.annotations.ThingworxPropertyDefinition;
import com.thingworx.metadata.annotations.ThingworxPropertyDefinitions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ThingworxPropertyDefinitions(properties = {
        @ThingworxPropertyDefinition(name = "Food", description = "", baseType = "NUMBER",
                aspects = {"dataChangeType:ALWAYS",
                        "dataChangeThreshold:0",
                        "cacheTime:0",
                        "isPersistent:FALSE",
                        "isReadOnly:FALSE",
                        "pushType:ALWAYS",
                        "isFolded:FALSE",
                        "defaultValue:2000"}),
        @ThingworxPropertyDefinition(name = "Water", description = "", baseType = "NUMBER",
                aspects = {"dataChangeType:ALWAYS",
                        "dataChangeThreshold:0",
                        "cacheTime:0",
                        "isPersistent:FALSE",
                        "isReadOnly:FALSE",
                        "pushType:ALWAYS",
                        "isFolded:FALSE",
                        "defaultValue:3000"}),
        @ThingworxPropertyDefinition(name = "Humidity", description = "", baseType = "NUMBER",
                aspects = {"dataChangeType:ALWAYS",
                        "dataChangeThreshold:0",
                        "cacheTime:0",
                        "isPersistent:FALSE",
                        "isReadOnly:FALSE",
                        "pushType:ALWAYS",
                        "isFolded:FALSE",
                        "defaultValue:0"}),
        @ThingworxPropertyDefinition(name = "Temperature", description = "", baseType = "NUMBER",
                aspects = {"dataChangeType:ALWAYS",
                        "dataChangeThreshold:0",
                        "cacheTime:0",
                        "isPersistent:FALSE",
                        "isReadOnly:FALSE",
                        "pushType:ALWAYS",
                        "isFolded:FALSE",
                        "defaultValue:0"}),
        @ThingworxPropertyDefinition(name = "Clean", description = "", baseType = "BOOLEAN",
                aspects = {"dataChangeType:ALWAYS",
                        "dataChangeThreshold:0",
                        "cacheTime:0",
                        "isPersistent:FALSE",
                        "isReadOnly:FALSE",
                        "pushType:ALWAYS",
                        "isFolded:FALSE",
                        "defaultValue:0"})
})


public class CageTemplate extends VirtualThing implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(CageTemplate.class);

    private final static String HUMIDITY_FIELD = "Humidity";
    private final static String TEMPERATURE_FIELD = "Temperature";
    private final static String WATER_FIELD = "Water";
    private final static String FOOD_FIELD = "Food";
    private final static String CLEAN_FIELD = "Clean";

    private Double humidity;
    private Double temperature;
    private Double water;
    private Double food;
    private Boolean clean;
    private String thingName = null;

    public CageTemplate(String name, String description, ConnectedThingClient client) {
        super(name, description, client);
        this.getThingShape();
        this.getBindingName();
        thingName = name;
        try {
            super.initializeFromAnnotations();
        } catch (Exception ex) {
            LOG.error("Not work", ex);
        }
        this.init();
    }

    public void synchronizeState() {
        super.synchronizeState();
        super.syncProperties();
    }


    private void init() {
        humidity = 25d;
        water = 3000d;
        humidity = 25d;
        food = 2000d;
        temperature = 20d;
        clean = true;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(1000);
            this.getClient().shutdown();
        } catch (Exception ex) {
            LOG.error("Error " + thingName, ex);
        }
    }

    @Override
    public void processScanRequest() throws Exception {
        temperature = temperature + randomWithRange(-2, 2);
        if (temperature < 15)
            temperature = 15d;
        else if (temperature > 30)
            temperature = 30d;
        humidity = humidity + randomWithRange(-2, 2);
        if (humidity < 10)
            humidity = 10d;
        else if (humidity > 65)
            humidity = 65d;
        water = water + randomWithRange(-150, 20);
        if (water < 0)
            water = 0d;
        else if (water > 5000)
            water = 5000d;
        food = food + randomWithRange(-100, 25);
        if (food < 0)
            food = 0d;
        else if (food > 5000)
            food = 5000d;
        if (clean)
            clean = randomWithRange(0, 100) < 75;
        setProperties();
        this.updateSubscribedProperties(5000);
    }

    private int randomWithRange(int min, int max) {
        int range = (max - min) + 1;
        return (int) (Math.random() * range) + min;
    }

    private void setProperties() throws Exception {
        setHumidity();
        setTemperature();
        setWater();
        setFood();
        setClean();
    }

    private void setHumidity() throws Exception {
        setProperty(HUMIDITY_FIELD, this.humidity);
    }

    private void setTemperature() throws Exception {
        setProperty(TEMPERATURE_FIELD, this.temperature);
    }

    private void setWater() throws Exception {
        setProperty(WATER_FIELD, this.water);
    }

    private void setFood() throws Exception {
        setProperty(FOOD_FIELD, this.food);
    }

    private void setClean() throws Exception {
        setProperty(CLEAN_FIELD, this.clean);
    }

}

