package ws.gw.util;

import com.google.gson.*;
import wshare.dc.resource.Resource;

import java.io.*;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Rye
 * Date: 3/25/14
 * Time: 4:34 PM
 */
public class ResourceDef2Json {

    public static JsonObject createJson(Resource res) {
        JsonObject jo = new JsonObject();
        jo.addProperty("rid", res.getId());
//        jo.add("check", JsonNull.INSTANCE);
        jo.addProperty("check", "");
        jo.addProperty("name", res.getName());

        jo.add("tags", new GsonBuilder().create().toJsonTree(res.getTags()).getAsJsonArray());
        jo.add("description", new GsonBuilder().create().toJsonTree(res.getDefinition().description));

        jo.add("properties", new GsonBuilder().create().toJsonTree(res.getDefinition().properties));
        for(Map.Entry<String, JsonElement> e: jo.getAsJsonObject("properties").entrySet()) {

            JsonObject pe = e.getValue().getAsJsonObject();
            String direction = pe.get("direction").getAsString();
            if(direction == "RES_2_USER") {
                direction = "RESOURCE_2_USER";
            } else if(direction == "USER_2_RES") {
                direction = "USER_2_RESOURCE";
            }

            pe.remove("direction");
            pe.addProperty("flow", direction);
        }

        jo.add("relationship", new GsonBuilder().create().toJsonTree(res.getDefinition().relationship));

        return jo;
    }

    public static void writeJson(String path, JsonObject jo) throws IOException {

        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(path), "utf-8");
        Gson gson = new Gson();
        writer.write(gson.toJson(jo));
        writer.flush();
        writer.close();
    }
}
