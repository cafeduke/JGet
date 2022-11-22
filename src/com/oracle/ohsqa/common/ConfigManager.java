package com.oracle.ohsqa.common;

import java.util.logging.Logger;

/**
 * This class manages the configuration for the given HttpServer.
 * 
 * @TODO
 *       - Add methods like backupConfig, restoreConfig, copyConfigFile, removeConfigFile
 * 
 * @author raghunandan.seshadri
 *
 */
public class ConfigManager
{
    private HttpServer httpServer = null;

    private Logger logger = null;

    /**
     * The configuration manager for the ohs instance.
     *
     * @param trafficDirector Instance of ohs.
     */
    public ConfigManager(HttpServer httpServer)
    {
        this.httpServer = httpServer;
        this.logger = httpServer.getLogger();
    }

    /**
     * ConfigManager initialization.
     *
     * <b>NOTE:<b> Objects managed by ConfigManager are created during initialization
     */
    public void init()
    {
    }
}
