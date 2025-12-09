package net.minecraft.client.particle;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.List;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;

public class ParticleDescription {
   private final List<Identifier> textures;

   private ParticleDescription(List<Identifier> var1) {
      super();
      this.textures = var1;
   }

   public List<Identifier> getTextures() {
      return this.textures;
   }

   public static ParticleDescription fromJson(JsonObject var0) {
      JsonArray var1 = GsonHelper.getAsJsonArray(var0, "textures", (JsonArray)null);
      if (var1 == null) {
         return new ParticleDescription(List.of());
      } else {
         List var2 = (List)Streams.stream(var1).map((var0x) -> GsonHelper.convertToString(var0x, "texture")).map(Identifier::parse).collect(ImmutableList.toImmutableList());
         return new ParticleDescription(var2);
      }
   }
}
