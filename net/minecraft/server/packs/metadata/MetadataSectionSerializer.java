package net.minecraft.server.packs.metadata;

import com.google.gson.JsonObject;

public interface MetadataSectionSerializer {
   String getMetadataSectionName();

   Object fromJson(JsonObject var1);
}
