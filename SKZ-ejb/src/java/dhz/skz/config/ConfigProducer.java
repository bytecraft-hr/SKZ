/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dhz.skz.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

/**
 *
 * @author kraljevic
 */
public class ConfigProducer {

    private static final Logger log = Logger.getLogger(ConfigProducer.class.getName());
    private volatile static Properties configProperties;

    @Produces
    @Config
    public TimeZone injectTimeZone(InjectionPoint ip) {
        return TimeZone.getTimeZone("GMT");
    }

    @Produces
    @Config
    public Logger getLogger(InjectionPoint ip) {
        return Logger.getLogger(ip.getMember().getDeclaringClass().getName());

    }

    public synchronized static Properties getProperties() {
        try (InputStream resourceAsStream = ConfigProducer.class.getClassLoader().getResourceAsStream("/dhz/skz/config/skz.properties")) {
            if (configProperties == null) {
                configProperties = new Properties();
                try {
                    configProperties.load(resourceAsStream);
                } catch (IOException ex) {
                    log.log(Level.SEVERE, null, ex);
                    throw new RuntimeException(ex);
                }
            }
        } catch (IOException ex) {
            log.log(Level.SEVERE, null, ex);
        }
        return configProperties;
    }

    @Produces
    @Config
    public String getConfiguration(InjectionPoint p) {
        Config param = p.getAnnotated().getAnnotation(Config.class);
        Properties config = getProperties();
        if (config == null) {
            return param.vrijednost();
        }
        String configKey = p.getMember().getDeclaringClass().getName() + "." + p.getMember().getName();
        if (config.getProperty(configKey) == null) {
            configKey = p.getMember().getDeclaringClass().getSimpleName() + "." + p.getMember().getName();
            if (config.getProperty(configKey) == null) {
                configKey = p.getMember().getName();
            }

        }
        log.log(Level.INFO, "Config key= {0} value = {1}", new Object[]{configKey, config.getProperty(configKey)});
        return config.getProperty(configKey, param.vrijednost());
    }

    @Produces
    @Config
    public Integer getConfigurationInteger(InjectionPoint p) {
        String val = getConfiguration(p);
        return Integer.parseInt(val);
    }
    
    @Produces
    @Config
    public Date getDefaultTime(InjectionPoint p){
        return new Date(0L);
    }

}
