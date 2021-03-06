package org.safehaus.confluence.processors;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.safehaus.confluence.models.Space;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;


/**
 * Created by root on 5/8/15.
 */
public class SpaceProcessor {
    public static List<Space> processJsonArray(JsonArray jsonArray){
        List<Space> result = new ArrayList<Space>();
        for(Iterator<JsonElement> i = jsonArray.iterator(); i.hasNext();){
            Space space = new Space((i.next().getAsJsonObject()));
            result.add(space);
        }

        return result;
    }
}