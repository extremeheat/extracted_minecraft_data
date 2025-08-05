package net.minecraft.network.chat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.resources.ResourceLocation;

public interface FontDescription {
   Codec<FontDescription> CODEC = ResourceLocation.CODEC.flatComapMap(Resource::new, (var0) -> {
      if (var0 instanceof Resource var1) {
         return DataResult.success(var1.id());
      } else {
         return DataResult.error(() -> "Unsupported font description type: " + String.valueOf(var0));
      }
   });
   Resource DEFAULT = new Resource(ResourceLocation.withDefaultNamespace("default"));

   public static record Resource(ResourceLocation id) implements FontDescription {
      public Resource(ResourceLocation var1) {
         super();
         this.id = var1;
      }
   }

   public static record AtlasSprite(ResourceLocation atlasId, ResourceLocation spriteId) implements FontDescription {
      public AtlasSprite(ResourceLocation var1, ResourceLocation var2) {
         super();
         this.atlasId = var1;
         this.spriteId = var2;
      }
   }
}
