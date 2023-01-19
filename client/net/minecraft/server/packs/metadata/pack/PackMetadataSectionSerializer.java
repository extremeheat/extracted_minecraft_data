package net.minecraft.server.packs.metadata.pack;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.util.GsonHelper;

public class PackMetadataSectionSerializer implements MetadataSectionSerializer<PackMetadataSection> {
   public PackMetadataSectionSerializer() {
      super();
   }

   public PackMetadataSection fromJson(JsonObject var1) {
      MutableComponent var2 = Component.Serializer.fromJson(var1.get("description"));
      if (var2 == null) {
         throw new JsonParseException("Invalid/missing description!");
      } else {
         int var3 = GsonHelper.getAsInt(var1, "pack_format");
         return new PackMetadataSection(var2, var3);
      }
   }

   @Override
   public String getMetadataSectionName() {
      return "pack";
   }
}
