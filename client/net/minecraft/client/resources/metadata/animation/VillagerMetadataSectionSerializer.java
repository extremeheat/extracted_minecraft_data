package net.minecraft.client.resources.metadata.animation;

import com.google.gson.JsonObject;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.util.GsonHelper;

public class VillagerMetadataSectionSerializer implements MetadataSectionSerializer<VillagerMetaDataSection> {
   public VillagerMetadataSectionSerializer() {
      super();
   }

   public VillagerMetaDataSection fromJson(JsonObject var1) {
      return new VillagerMetaDataSection(VillagerMetaDataSection.Hat.getByName(GsonHelper.getAsString(var1, "hat", "none")));
   }

   @Override
   public String getMetadataSectionName() {
      return "villager";
   }
}
