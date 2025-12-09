package net.minecraft.network.chat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.component.ResolvableProfile;

public interface FontDescription {
   Codec<FontDescription> CODEC = Identifier.CODEC.flatComapMap(Resource::new, (var0) -> {
      if (var0 instanceof Resource var1) {
         return DataResult.success(var1.id());
      } else {
         return DataResult.error(() -> "Unsupported font description type: " + String.valueOf(var0));
      }
   });
   Resource DEFAULT = new Resource(Identifier.withDefaultNamespace("default"));

   public static record Resource(Identifier id) implements FontDescription {
      public Resource(Identifier var1) {
         super();
         this.id = var1;
      }
   }

   public static record AtlasSprite(Identifier atlasId, Identifier spriteId) implements FontDescription {
      public AtlasSprite(Identifier var1, Identifier var2) {
         super();
         this.atlasId = var1;
         this.spriteId = var2;
      }
   }

   public static record PlayerSprite(ResolvableProfile profile, boolean hat) implements FontDescription {
      public PlayerSprite(ResolvableProfile var1, boolean var2) {
         super();
         this.profile = var1;
         this.hat = var2;
      }
   }
}
