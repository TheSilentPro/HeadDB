package com.github.thesilentpro.headdb.implementation.model;

import com.github.thesilentpro.headdb.api.model.Head;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HeadMapper implements JsonDeserializer<Head>, JsonSerializer<Head> {

    public static final Type HEADS_LIST_TYPE = new TypeToken<List<Head>>(){}.getType();

    @Override
    public Head deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject main = json.getAsJsonObject();
        List<String> tags = new ArrayList<>();
        for (JsonElement tagEntry : main.get("tags").getAsJsonArray()) {
            tags.add(tagEntry.getAsString());
        }
        return new BaseHead(main.get("id").getAsInt(), main.get("name").getAsString(), main.get("texture").getAsString(), main.get("category").getAsString(), tags);
    }

    @Override
    public JsonElement serialize(Head src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject json = new JsonObject();

        json.addProperty("id", src.getId());
        json.addProperty("name", src.getName());
        json.addProperty("texture", src.getTexture());
        json.addProperty("category", src.getCategory());

        JsonArray tagsArray = new JsonArray();
        for (String tag : src.getTags()) {
            tagsArray.add(new JsonPrimitive(tag));
        }
        json.add("tags", tagsArray);

        return json;
    }

}
