package cn.iie.gaia.loader;

import cn.iie.gaia.Container;
import cn.iie.gaia.Lifecycle;
import cn.iie.gaia.LifecycleException;
import cn.iie.gaia.LifecycleState;
import cn.iie.gaia.util.*;
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
    private String repositories[] = new String[0];
    private String classpath = null;
    private String loaderClass = "cn.iie.gaia.loader.ComponentClassLoader";

    public ComponentClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ComponentClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    public String[] getRepositories() {
        return repositories.clone();
    }

    private void setRepositories() throws IOException {
        // load classes
        String userDirBase = System.getProperty("user.dir");

        String classesPath = "/COMP-INF/classes";

        File classRepository = null;
        String absoluteClassesPath = userDirBase + "/" + container.getPath() + "/" + classesPath;

        classRepository = new File(absoluteClassesPath);

        if(!classRepository.exists()) {
            if(!classRepository.mkdirs() &&
                    !classRepository.isDirectory()) {
                throw new IOException(sm.getString("webappLoader.mkdirFailure"));
            }
        } else if(!classRepository.isDirectory()) {
            throw new IOException(sm.getString("webappLoader.notDirFailure"));
        }

        classLoader.addRepository(absoluteClassesPath + "/", classRepository);

        // load libs
        String libPath = "COMP-INF/lib";
        classLoader.setJarPath(libPath);

        String absoluteLibPath = userDirBase + "/" + container.getPath() + "/" + libPath;

        File libDir = new File(absoluteLibPath);

        if(!libDir.exists()) {
            if(!libDir.mkdirs() && !libDir.isDirectory()) {
                throw new IOException(sm.getString("webappLoader.mkdirFailure"));
            }
        } else if(!libDir.isDirectory()) {
            throw new IOException(sm.getString("webappLoader.mkdirFailure"));
        }

        for(File file : libDir.listFiles()) {
            if(file.isDirectory()) {
                throw new IOException(sm.getString("weappLoader.readlibFailure"));
            } else {
                JarFile jarFile = new JarFile(file);
                classLoader.addJar(file.getName(), jarFile, file);
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
            if(!buildClassPath(classpath, loader));
        }

        this.classpath = classpath.toString();
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

    @Override
    protected void startInternal() throws LifecycleException {

        try {
            classLoader = createClassLoader();
            for(int i = 0; i < repositories.length; i++) {
                classLoader.addRepository(repositories[i]);
            }

            setRepositories();
            setClassPath();
            ((Lifecycle) classLoader).start();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Throwable t) {
            t = ExceptionUtils.unwrapInvocationTargetException(t);
            ExceptionUtils.handleThrowable(t);
            log.error("LifecycleException ", t);
            throw new LifecycleException("start: ", t);
        }

        setState(LifecycleState.STARTING);

    }

    @Override
    protected void stopInternal() throws LifecycleException {

        setState(LifecycleState.STOPPING);

        if(classLoader != null) {
            ((Lifecycle) classLoader).stop();
        }

        classLoader = null;
    }

    private ComponentClassLoader createClassLoader() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, ClassNotFoundException {
        Class<?> clazz = Class.forName(loaderClass);
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
}
