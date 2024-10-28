package net.minecraft.client.particle;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class ParticleDescription {
   private final List<ResourceLocation> textures;

   private ParticleDescription(List<ResourceLocation> var1) {
      super();
      this.textures = var1;
   }

   public List<ResourceLocation> getTextures() {
      return this.textures;
   }

   public static ParticleDescription fromJson(JsonObject var0) {
      JsonArray var1 = GsonHelper.getAsJsonArray(var0, "textures", (JsonArray)null);
      if (var1 == null) {
         return new ParticleDescription(List.of());
      } else {
         List var2 = (List)Streams.stream(var1).map((var0x) -> {
            return GsonHelper.convertToString(var0x, "texture");
         }).map(ResourceLocation::parse).collect(ImmutableList.toImmutableList());
         return new ParticleDescription(var2);
      }
   }
}
