package cn.iie.gaia;

/**
 * Created with IntelliJ IDEA.
 * User: Rye
 * Date: 4/1/14
 * Time: 9:36 AM
 */

/*
 * Resources in the same Group share one  PreProcessor, CommandHandler and PostProcessor.
 * It's like a local composite resource that technically has nothing to do with the resource defined by platform
 */
public interface Component extends Lifecycle {

    public static final String METAINFO_DIR_NAME = "COM-INF";
    public static final String CLASS_DIR_NAME = "classes";
    public static final String LIB_DIR_NAME = "lib";
    public static final String RESDEF_DIR_NAME = "def";
    public static final String CONF_DIR_NAME = "conf";
    public static final String CONF_FILE_NAME = "group.properties";

    public void setName(String name);
    public String getName();

    public void setClassLoader(ClassLoader classLoader);
    public ClassLoader getClassLoader();

    public void setContainer(Container container);
    public Container getContainer();

//    public void setCommandHandler(CommandHandler commandHandler);
//    public void setPreprocessor(PreProcessor preProcessor);
//    public void setProcessor(PostProcessor postProcessor);

}


