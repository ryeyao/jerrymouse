package org.omg.gaia.loader;

import org.omg.gaia.*;
import org.omg.gaia.entity.ConfigPropertiesBase;
import org.omg.gaia.util.ExceptionUtils;
import org.omg.gaia.util.LifecycleMBeanBase;
import org.omg.gaia.util.StringManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.jar.JarFile;

/**
 * Created with IntelliJ IDEA.
 * User: Rye
 * Date: 4/2/14
 * Time: 3:33 PM
 */
public class ComponentLoader extends LifecycleMBeanBase {

    protected static final StringManager sm = StringManager.getManager(Constants.Package);
    private static final Logger log = LogManager.getLogger(ComponentLoader.class);
    private ComponentClassLoader classLoader = null;
    private ClassLoader parentClassLoader = null;
    private Container container = null;
    private String componentName = null;

    private ConfigProperties properties = null;
    private String repositories[] = new String[0];
    private String classpath = null;
    private String loaderClass = "org.omg.gaia.loader.ComponentClassLoader";

    public ComponentClassLoader getClassLoader() {
        return classLoader;
    }

    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public String[] getRepositories() {
        return repositories.clone();
    }

    public ConfigProperties getProperties() {
        return properties;
    }

    private void setRepositories() throws IOException {
        // load classes
        String classesPath = "COMP-INF" + File.separatorChar + "classes";

        File classRepository = null;
        String absoluteClassesPath = container.getPath() + File.separatorChar + componentName + File.separatorChar + classesPath;

        classRepository = new File(absoluteClassesPath);

        if(!classRepository.exists()) {
            if(!classRepository.mkdirs() &&
                    !classRepository.isDirectory()) {
                throw new IOException(sm.getString("componentLoader.mkdirFailure"));
            }
        } else if(!classRepository.isDirectory()) {
            throw new IOException(sm.getString("componentLoader.notDirFailure"));
        }

//        classLoader.addRepository(absoluteClassesPath + File.separatorChar, classRepository);
        classLoader.addRepository(classRepository.toURI().toString());

        // load libs
        String libPath = "COMP-INF" + File.separatorChar + "lib";
        classLoader.setJarPath(libPath);

        String absoluteLibPath = container.getPath() + File.separatorChar + componentName + File.separatorChar + libPath;

        File libDir = new File(absoluteLibPath);

        if(!libDir.exists()) {
            if(!libDir.mkdirs() && !libDir.isDirectory()) {
                throw new IOException(sm.getString("componentLoader.mkdirFailure"));
            }
        } else if(!libDir.isDirectory()) {
            throw new IOException(sm.getString("componentLoader.mkdirFailure"));
        }

        // TODO: add recursively read directory support
        for(File file : libDir.listFiles()) {
            if(file.isDirectory()) {
                throw new IOException(sm.getString("componentLoader.readlibFailure"));
            } else if (file.getName().endsWith(".jar")){
                JarFile jarFile = new JarFile(file);
                classLoader.addJar(file.getName(), jarFile, file);
                classLoader.addRepository(file.toURI().toString());
//                classLoader.addRepository(absoluteLibPath + File.separatorChar, file);
            }
        }


    }

    public String getClasspath() {
        return classpath;
    }

    // try to extract the classpath from a loader that is not URLClassLoader
    private String getClasspath( ClassLoader loader ) {
        try {
            Method m=loader.getClass().getMethod("getClasspath", new Class[] {});
            if( log.isTraceEnabled())
                log.trace("getClasspath " + m );
            if( m==null ) return null;
            Object o=m.invoke( loader, new Object[] {} );
            if( log.isDebugEnabled() )
                log.debug("gotClasspath " + o);
            if( o instanceof String )
                return (String)o;
            return null;
        } catch( Exception ex ) {
            Throwable t = ExceptionUtils.unwrapInvocationTargetException(ex);
            ExceptionUtils.handleThrowable(t);
            if (log.isDebugEnabled())
                log.debug("getClasspath ", ex);
        }
        return null;
    }

    public void setClassPath() {
        StringBuilder classpath = new StringBuilder();
        ClassLoader loader = getClassLoader();

        while(loader != null) {
            if(!buildClassPath(classpath, loader)) {
                break;
            }
            loader = loader.getParent();
        }

        this.classpath = classpath.toString();
        log.debug("classpath: {}", classpath);
    }

    private boolean buildClassPath(StringBuilder classpath, ClassLoader loader) {
        if (loader instanceof URLClassLoader) {
            URL repositories[] =
                    ((URLClassLoader) loader).getURLs();
            for (int i = 0; i < repositories.length; i++) {
                String repository = repositories[i].toString();
                if (repository.startsWith("file://"))
                    repository = utf8Decode(repository.substring(7));
                else if (repository.startsWith("file:"))
                    repository = utf8Decode(repository.substring(5));
                else
                    continue;
                if (repository == null)
                    continue;
                if (classpath.length() > 0)
                    classpath.append(File.pathSeparator);
                classpath.append(repository);
            }
        } else {
            String cp = getClasspath(loader);
            if (cp == null) {
                log.info( "Unknown loader " + loader + " " + loader.getClass());
            } else {
                if (classpath.length() > 0)
                    classpath.append(File.pathSeparator);
                classpath.append(cp);
            }
            return false;
        }
        return true;
    }

    private String utf8Decode(String input) {
        String result = null;
        try {
            result = URLDecoder.decode(input, "UTF-8");
        } catch (UnsupportedEncodingException uee) {
            // Impossible. All JVMs are required to support UTF-8.
        }
        return result;
    }

    /**
     * Add a new repository to the set of repositories for this class loader.
     *
     * @param repository Repository to be added
     */
    public void addRepository(String repository) {

        if (log.isDebugEnabled())
            log.debug(sm.getString("componentLoader.addRepository", repository));

        for (int i = 0; i < repositories.length; i++) {
            if (repository.equals(repositories[i]))
                return;
        }
        String results[] = new String[repositories.length + 1];
        for (int i = 0; i < repositories.length; i++)
            results[i] = repositories[i];
        results[repositories.length] = repository;
        repositories = results;

        if (getState().isAvailable() && (classLoader != null)) {
            classLoader.addRepository(repository);
            setClassPath();
        }

    }



    @Override
    protected void startInternal() throws LifecycleException {

        try {
            classLoader = createClassLoader();


            for(int i = 0; i < repositories.length; i++) {
                classLoader.addRepository(repositories[i]);
            }


            setRepositories();
            setClassPath();
            loadConfig();
            properties.start();
            ((Lifecycle) classLoader).start();
            loadComponent();

        } catch (IOException e) {
        } catch (Throwable t) {
            t = ExceptionUtils.unwrapInvocationTargetException(t);
            ExceptionUtils.handleThrowable(t);
            log.error("LifecycleException ", t);
            throw new LifecycleException("start: ", t);
        }

        super.startInternal();

    }

    @Override
    protected void stopInternal() throws LifecycleException {


        super.stopInternal();
        if(classLoader != null) {
            ((Lifecycle) classLoader).stop();
        }

        classLoader = null;
    }

    private ComponentClassLoader createClassLoader() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, ClassNotFoundException {
        Class<?> clazz = Class.forName(loaderClass);
//        Class<?> clazz = ComponentClassLoader.class;
        ComponentClassLoader cl = null;

        if(parentClassLoader == null) {
            parentClassLoader = container.getParentClassLoader();
        }

        Class<?>[] argTypes = { ClassLoader.class };
        Object[] args = { parentClassLoader };
        Constructor<?> constr = clazz.getConstructor(argTypes);
        cl = (ComponentClassLoader) constr.newInstance(args);

        return cl;
    }

    private void loadConfig() throws LifecycleException {
        properties = new ConfigPropertiesBase();
        properties.setComponentHome(container.getPath() + File.separatorChar + componentName);
    }

    private void loadComponent() {
//            System.out.println("CompClassLoader:\n " + ccl);
        log.debug(sm.getString("componentLoader.loading", componentName));
        Component comp = null;
        try {
            String cName = getProperties().getProperty("myComponent");
            if(cName == null || cName.isEmpty()) {
                log.error(sm.getString("componentLoader.loadFailure", componentName));
                return;
            }
            Class<?> c =  classLoader.loadClass(cName);

            Object o = c.newInstance();
            if(o instanceof Component) {
                comp = (Component) o;
                comp.setName(componentName);
                container.addComponent(comp);
                comp.setContainer(container);
                comp.setConfigProperties(properties);
//                System.setProperty(comp.getName() + ".path", comp.getComponentHome());
                container.addComponent(comp);
            } else {
                log.error(sm.getString("componentLoader.loadFailure", componentName));
                return;
            }
            log.debug(sm.getString("componentLoader.loaded", componentName));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }
}
