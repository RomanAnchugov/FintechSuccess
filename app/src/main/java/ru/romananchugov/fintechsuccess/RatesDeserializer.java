package ru.romananchugov.fintechsuccess;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

import ru.romananchugov.fintechsuccess.Model.RateObject;

/**
 * Created by romananchugov on 08.02.2018.
 */

public class RatesDeserializer implements JsonDeserializer {
    @Override
    public RateObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        RateObject rateObject = null;
        if(json.isJsonObject()){
            Set<Map.Entry<String, JsonElement>> entires =
                    json.getAsJsonObject().entrySet();
            if(entires.size() > 0){
                Map.Entry<String, JsonElement> entry = entires.iterator().next();
                rateObject = new RateObject(entry.getKey(), entry.getValue().getAsDouble());
            }
        }
        return rateObject;
    }
}
