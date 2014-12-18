package org.gaia;

import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: Rye
 * Date: 4/5/14
 * Time: 1:11 PM
 */
public interface ConfigProperties extends Lifecycle {


    /**
     * Return specified property value.
     */
    public String getProperty(String name);
    public Properties getProperties();
    public String getComponentHome();
    public void setComponentHome(String name);
}
