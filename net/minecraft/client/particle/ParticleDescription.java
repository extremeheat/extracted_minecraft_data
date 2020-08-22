package net.minecraft.client.particle;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class ParticleDescription {
   @Nullable
   private final List textures;

   private ParticleDescription(@Nullable List var1) {
      this.textures = var1;
   }

   @Nullable
   public List getTextures() {
      return this.textures;
   }

   public static ParticleDescription fromJson(JsonObject var0) {
      JsonArray var1 = GsonHelper.getAsJsonArray(var0, "textures", (JsonArray)null);
      List var2;
      if (var1 != null) {
         var2 = (List)Streams.stream(var1).map((var0x) -> {
            return GsonHelper.convertToString(var0x, "texture");
         }).map(ResourceLocation::new).collect(ImmutableList.toImmutableList());
      } else {
         var2 = null;
      }

      return new ParticleDescription(var2);
   }
}
