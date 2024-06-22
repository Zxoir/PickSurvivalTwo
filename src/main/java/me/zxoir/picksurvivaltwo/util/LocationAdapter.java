package me.zxoir.picksurvivaltwo.util;

import com.google.gson.*;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import me.zxoir.picksurvivaltwo.PickSurvivalTwo;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

/**
 * MIT License Copyright (c) 2020/2021 Zxoir
 *
 * @author Zxoir
 * @since 10/20/2020
 */
public class LocationAdapter implements JsonSerializer<Location>, JsonDeserializer<Location> {

    @Override
    public JsonElement serialize(@NotNull Location src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        MVWorldManager worldManager = PickSurvivalTwo.getMultiverseCore().getMVWorldManager();
        boolean isMVWorld = worldManager.isMVWorld(src.getWorld());

        if (isMVWorld)
            object.addProperty("world", worldManager.getMVWorld(src.getWorld()) == null ? null : worldManager.getMVWorld(src.getWorld()).getName());
        else
            object.addProperty("world", src.getWorld() == null ? null : src.getWorld().getName());

        object.addProperty("x", src.getX());
        object.addProperty("y", src.getY());
        object.addProperty("z", src.getZ());
        object.addProperty("yaw", src.getYaw());
        object.addProperty("pitch", src.getPitch());
        return object;
    }

    @Override
    public Location deserialize(@NotNull JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        MVWorldManager worldManager = PickSurvivalTwo.getMultiverseCore().getMVWorldManager();

        World world;
        if (!object.get("world").isJsonNull() && worldManager.isMVWorld(object.get("world").getAsString()))
            world = worldManager.getMVWorld(object.get("world").getAsString()) == null ? null : worldManager.getMVWorld(object.get("world").getAsString()).getCBWorld();
        else
            world = object.get("world").isJsonNull() ? null : Bukkit.getWorld(object.get("world").getAsString());

        double x = object.get("x").getAsDouble();
        double y = object.get("y").getAsDouble();
        double z = object.get("z").getAsDouble();
        float yaw = object.get("yaw").getAsFloat();
        float pitch = object.get("pitch").getAsFloat();
        return new Location(world, x, y, z, yaw, pitch);
    }
}
