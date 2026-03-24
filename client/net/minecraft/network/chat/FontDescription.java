package net.minecraft.network.chat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.component.ResolvableProfile;

public interface FontDescription {
   Codec<FontDescription> CODEC = Identifier.CODEC.flatComapMap(Resource::new, (fontDescription) -> {
      if (fontDescription instanceof Resource resource) {
         return DataResult.success(resource.id());
      } else {
         return DataResult.error(() -> "Unsupported font description type: " + String.valueOf(fontDescription));
      }
   });
   Resource DEFAULT = new Resource(Identifier.withDefaultNamespace("default"));

   public static record Resource(Identifier id) implements FontDescription {
      public Resource {
         super();
      }
   }

   public static record AtlasSprite(Identifier atlasId, Identifier spriteId) implements FontDescription {
      public AtlasSprite {
         super();
      }
   }

   public static record PlayerSprite(ResolvableProfile profile, boolean hat) implements FontDescription {
      public PlayerSprite {
         super();
      }
   }
}
