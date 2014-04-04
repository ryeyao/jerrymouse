package cn.iie.gateway.abstracthandler;

import wshare.dc.resource.DataHandler;
import wshare.dc.resource.Resource;

/**
 * Created with IntelliJ IDEA.
 * User: Rye
 * Date: 3/26/14
 * Time: 4:08 PM
 */
public abstract class CommandHandler implements DataHandler{
    private Resource res;

    public void setRes(Resource res) {
        this.res = res;
    }

    public Resource getRes() {
        return this.res;
    }

    public abstract void init();
}
