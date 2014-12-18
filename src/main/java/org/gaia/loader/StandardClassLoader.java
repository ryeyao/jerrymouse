package org.gaia.loader;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created with IntelliJ IDEA.
 * User: Rye
 * Date: 4/5/14
 * Time: 9:15 PM
 */
public class StandardClassLoader extends URLClassLoader implements StandardClassLoaderMBean {
    public StandardClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public StandardClassLoader(URL[] urls) {
        super(urls);
    }
}
