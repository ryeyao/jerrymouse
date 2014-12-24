package org.omg.gaia.loader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.omg.gaia.*;
import org.omg.gaia.util.StringManager;

import javax.naming.directory.DirContext;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.security.AccessControlException;
import java.security.Policy;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created with IntelliJ IDEA.
 * User: Rye
 * Date: 4/3/14
 * Time: 4:04 PM
 */
public class ComponentClassLoader extends URLClassLoader implements Lifecycle {

    private static final Logger log=
            LogManager.getLogger(ComponentClassLoader.class);

    private static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");
    protected static final StringManager sm = StringManager.getManager(Constants.Package);
    protected final ClassLoader j2seClassLoader;
    protected SecurityManager securityManager;
    protected String[] repositories = new String[0];
    protected URL[] repositoryURLs = null;
    protected File[] files = new File[0];
    protected JarFile[] jarFiles = new JarFile[0];
    protected File[] jarRealFiles = new File[0];
    protected String jarPath = null;
    protected String[] jarNames = new String[0];
    protected String[] paths = new String[0];
    protected File loaderDir = null;
    protected String canonicalLoaderDir = null;
    protected ClassLoader parent = null;
    protected ClassLoader system = null;
    protected DirContext resources = null;
    protected Map<String, Class<?>> loadedClasses = new HashMap<String, Class<?>>();
    protected static final String[] triggers = null;
    protected boolean started = false;


    public ComponentClassLoader(URL[] urls) {
        super(urls);
        ClassLoader p = getParent();

        if (p == null) {
            p = getSystemClassLoader();
        }
        this.parent = p;

        ClassLoader j = String.class.getClassLoader();
        if (j == null) {
            j = getSystemClassLoader();
            while (j.getParent() != null) {
                j = j.getParent();
            }
        }
        this.j2seClassLoader = j;

        securityManager = System.getSecurityManager();
        if (securityManager != null) {
            refreshPolicy();
        }
    }

    public ComponentClassLoader() {
        super(new URL[0]);

        ClassLoader p = getParent();

        if (p == null) {
            p = getSystemClassLoader();
        }
        this.parent = p;

        ClassLoader j = String.class.getClassLoader();
        if (j == null) {
            j = getSystemClassLoader();
            while (j.getParent() != null) {
                j = j.getParent();
            }
        }
        this.j2seClassLoader = j;

        securityManager = System.getSecurityManager();
        if (securityManager != null) {
            refreshPolicy();
        }
    }

    /**
     * Construct a new ClassLoader with no defined repositories and the given
     * parent ClassLoader.
     * <p>
     * Method is used via reflection -
     * see {@link ComponentLoader#createClassLoader()}
     *
     * @param parent Our parent class loader
     */
    public ComponentClassLoader(ClassLoader parent) {

        super(new URL[0], parent);

        ClassLoader p = getParent();
        if (p == null) {
            p = getSystemClassLoader();
        }
        this.parent = p;

        ClassLoader j = String.class.getClassLoader();
        if (j == null) {
            j = getSystemClassLoader();
            while (j.getParent() != null) {
                j = j.getParent();
            }
        }
        this.j2seClassLoader = j;

        securityManager = System.getSecurityManager();
        if (securityManager != null) {
            refreshPolicy();
        }
    }

    public void addRepository(URL repositoryURL) {
        super.addURL(repositoryURL);
        repositoryURLs = null;
    }

    public void addRepository(String repository) {
        log.debug("Add repository: {}", repository);
        if(repository.startsWith(File.separatorChar + Component.LIB_DIR_NAME)
                || repository.startsWith(File.separatorChar + Component.CLASS_DIR_NAME)) {
            return;
        }

        try {
            URL url = new URL(repository);
            addRepository(url);

        } catch (MalformedURLException e) {
            IllegalArgumentException iae = new IllegalArgumentException("Invalid repository: " + repository);
            iae.initCause(e);
            throw iae;
        }
    }

    synchronized void addRepository(String repository, File file) {
        if(repository == null) {
            return;
        }

        int i;

        String[] result = new String[repositories.length + 1];
        for(i = 0; i < repositories.length; i++) {
            result[i] = repositories[i];
        }
        result[repositories.length] = repository;
        repositories = result;

        File[] result2 = new File[files.length + 1];
        for(i = 0; i < files.length; i++) {
            result2[i] = files[i];
        }
        result2[files.length] = file;
        files = result2;
    }

    synchronized void addJar(String jar, JarFile jarFile, File file)
            throws IOException {

        if (jar == null)
            return;
        if (jarFile == null)
            return;
        if (file == null)
            return;

        if (log.isDebugEnabled())
            log.debug("addJar(" + jar + ")");

        int i;

        if ((jarPath != null) && (jar.startsWith(jarPath))) {

            String jarName = jar.substring(jarPath.length());
            while (jarName.startsWith("/"))
                jarName = jarName.substring(1);

            String[] result = new String[jarNames.length + 1];
            for (i = 0; i < jarNames.length; i++) {
                result[i] = jarNames[i];
            }
            result[jarNames.length] = jarName;
            jarNames = result;

        }

        // If the JAR currently contains invalid classes, don't actually use it
        // for classloading
        if (!validateJarFile(file))
            return;

        JarFile[] result2 = new JarFile[jarFiles.length + 1];
        for (i = 0; i < jarFiles.length; i++) {
            result2[i] = jarFiles[i];
        }
        result2[jarFiles.length] = jarFile;
        jarFiles = result2;

        // Add the file to the list
        File[] result4 = new File[jarRealFiles.length + 1];
        for (i = 0; i < jarRealFiles.length; i++) {
            result4[i] = jarRealFiles[i];
        }
        result4[jarRealFiles.length] = file;
        jarRealFiles = result4;
    }


    /**
     * Return a String array of the current repositories for this class
     * loader.  If there are no repositories, a zero-length array is
     * returned.For security reason, returns a clone of the Array (since
     * String are immutable).
     */
    public String[] findRepositories() {

        return (repositories.clone());

    }

    /**
     * Check the specified JAR file, and return <code>true</code> if it does
     * not contain any of the trigger classes.
     *
     * @param file  The JAR file to be checked
     *
     * @exception IOException if an input/output error occurs
     */
    protected boolean validateJarFile(File file)
            throws IOException {

        if (triggers == null)
            return (true);

        try (JarFile jarFile = new JarFile(file);) {
            for (int i = 0; i < triggers.length; i++) {
                Class<?> clazz = null;
                try {
                    if (parent != null) {
                        clazz = parent.loadClass(triggers[i]);
                    } else {
                        clazz = Class.forName(triggers[i]);
                    }
                } catch (Exception e) {
                    clazz = null;
                }
                if (clazz == null)
                    continue;
                String name = triggers[i].replace('.', '/') + ".class";
                if (log.isDebugEnabled())
                    log.debug(" Checking for " + name);
                JarEntry jarEntry = jarFile.getJarEntry(name);
                if (jarEntry != null) {
                    log.info("validateJarFile(" + file +
                            ") - jar not loaded. See Servlet Spec 3.0, "
                            + "section 10.7.2. Offending class: " + name);
                    return false;
                }
            }
            return true;
        }
    }


    /**
     * Get the URI for the given file.
     */
    protected URL getURL(File file)
            throws MalformedURLException {


        File realFile = file;
        try {
            realFile = realFile.getCanonicalFile();
        } catch (IOException e) {
            // Ignore
        }
        return realFile.toURI().toURL();

    }


    /**
     * Delete the specified directory, including all of its contents and
     * subdirectories recursively.
     *
     * @param dir File object representing the directory to be deleted
     */
    protected static void deleteDir(File dir) {

        String files[] = dir.list();
        if (files == null) {
            files = new String[0];
        }
        for (int i = 0; i < files.length; i++) {
            File file = new File(dir, files[i]);
            if (file.isDirectory()) {
                deleteDir(file);
            } else {
                file.delete();
            }
        }
        dir.delete();

    }
    @Override
    public void addLifecycleListener(LifecycleListener listener) {

    }

    @Override
    public LifecycleListener[] findLifecycleListeners() {
        return new LifecycleListener[0];
    }

    @Override
    public void removeLifecycleListener(LifecycleListener listener) {

    }

    @Override
    public void init() throws LifecycleException {

    }

    @Override
    public void start() throws LifecycleException {
        started = true;
    }

    public boolean isStarted() {
        return started;
    }

    @Override
    public void stop() throws LifecycleException {
        started = false;
        int length = files.length;
        for(int i = 0; i < length; i++) {
            files[i] = null;
        }

        length = jarFiles.length;
        for(int i = 0; i < length; i++) {
            try {
                if(jarFiles[i] != null) {
                    jarFiles[i].close();
                }
            } catch (IOException e) {

            }
            jarFiles[i] = null;
        }

        resources = null;
        repositories = null;
        repositoryURLs = null;
        files = null;
        jarFiles = null;
        jarRealFiles = null;
        jarPath = null;
        jarNames = null;
        paths = null;
        parent = null;

        if(loaderDir != null) {
            deleteDir(loaderDir);
        }
    }

    /**
     * Load the class with the specified name.  This method searches for
     * classes in the same manner as <code>loadClass(String, boolean)</code>
     * with <code>false</code> as the second argument.
     *
     * @param name Name of the class to be loaded
     *
     * @exception ClassNotFoundException if the class was not found
     */
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {

        return (loadClass(name, false));

    }

    public Class<?> findLoadedClass0(String name) {

        if(loadedClasses.containsKey(name)) {
            return loadedClasses.get(name);
        }
        return (null);
    }

    /**
     * Load the class with the specified name, searching using the following
     * algorithm until it finds and returns the class.  If the class cannot
     * be found, returns <code>ClassNotFoundException</code>.
     * <ul>
     * <li>Call <code>findLoadedClass(String)</code> to check if the
     *     class has already been loaded.  If it has, the same
     *     <code>Class</code> object is returned.</li>
     * <li>If the <code>delegate</code> property is set to <code>true</code>,
     *     call the <code>loadClass()</code> method of the parent class
     *     loader, if any.</li>
     * <li>Call <code>findClass()</code> to find this class in our locally
     *     defined repositories.</li>
     * <li>Call the <code>loadClass()</code> method of our parent
     *     class loader, if any.</li>
     * </ul>
     * If the class was found using the above steps, and the
     * <code>resolve</code> flag is <code>true</code>, this method will then
     * call <code>resolveClass(Class)</code> on the resulting Class object.
     *
     * @param name Name of the class to be loaded
     * @param resolve If <code>true</code> then resolve the class
     *
     * @exception ClassNotFoundException if the class was not found
     */
    @Override
    public synchronized Class<?> loadClass(String name, boolean resolve)
            throws ClassNotFoundException {

        log.debug("loadClass(" + name + ", " + resolve + ")");
        Class<?> clazz = null;

        // Log access to stopped classloader
        if (!started) {
            try {
                throw new IllegalStateException();
            } catch (IllegalStateException e) {
                log.info(sm.getString("componentClassLoader.stopped", name), e);
            }
        }

        // (0) Check our previously loaded local class cache
        clazz = findLoadedClass0(name);
        if (clazz != null) {
            if (log.isDebugEnabled())
                log.debug("  Returning class from local cache");
            if (resolve)
                resolveClass(clazz);
            return (clazz);
        }

        // (0.1) Check our previously loaded class cache
        clazz = findLoadedClass(name);
        if (clazz != null) {
            if (log.isDebugEnabled())
                log.debug("  Returning class from cache");
            if (resolve)
                resolveClass(clazz);
            return (clazz);
        }

        // (0.2) Try loading the class with the system class loader, to prevent
        //       the component from overriding J2SE classes
        try {
            clazz = j2seClassLoader.loadClass(name);
            if (clazz != null) {
                if (resolve)
                    resolveClass(clazz);
                loadedClasses.put(name, clazz);
                return (clazz);
            }
        } catch (ClassNotFoundException e) {
            // Ignore
        }


        // (2) Search local repositories
        if (log.isDebugEnabled())
            log.debug("  Searching local repositories for class {}", name);
        try {
            clazz = findClass(name);
            if (clazz != null) {
                if (log.isDebugEnabled())
                    log.debug("  Loading class from local repository");
                if (resolve)
                    resolveClass(clazz);
                loadedClasses.put(name, clazz);
                return (clazz);
            }
        } catch (ClassNotFoundException e) {
            // Ignore
        }

        // (1) Delegate to our parent if requested
        if (log.isDebugEnabled())
            log.debug("  Delegating to parent classloader1 " + parent);
        try {
            clazz = Class.forName(name, false, parent);
            if (clazz != null) {
                log.debug("  Loading class from parent");
                if (resolve)
                    resolveClass(clazz);
                loadedClasses.put(name, clazz);
                return (clazz);
            }
        } catch (ClassNotFoundException e) {
            // Ignore
        }

        throw new ClassNotFoundException(name);

    }

    @Override
    public void destroy() throws LifecycleException {

    }

    /**
     * Change the Jar path.
     */
    public void setJarPath(String jarPath) {

        this.jarPath = jarPath;

    }

    @Override
    public LifecycleState getState() {
        return null;
    }

    @Override
    public String getStateName() {
        return null;
    }
    /**
     * Refresh the system policy file, to pick up eventual changes.
     */
    protected void refreshPolicy() {

        try {
            // The policy file may have been modified to adjust
            // permissions, so we're reloading it when loading or
            // reloading a Context
            Policy policy = Policy.getPolicy();
            policy.refresh();
        } catch (AccessControlException e) {
            // Some policy files may restrict this, even for the core,
            // so this exception is ignored
        }

    }

    public String toString() {

        StringBuilder sb = new StringBuilder("ComponentClassLoader\r\n");
        sb.append("  repositories:\r\n");
        if (repositories != null) {
            for (int i = 0; i < repositories.length; i++) {
                sb.append("    ");
                sb.append(repositories[i]);
                sb.append("\r\n");
            }
        }
        if (this.parent != null) {
            sb.append("----------> Parent Classloader:\r\n");
            sb.append(this.parent.toString());
            sb.append("\r\n");
        }
        return (sb.toString());

    }
}
