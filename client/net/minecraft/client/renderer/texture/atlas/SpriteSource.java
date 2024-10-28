package net.minecraft.client.renderer.texture.atlas;

import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

public interface SpriteSource {
   FileToIdConverter TEXTURE_ID_CONVERTER = new FileToIdConverter("textures", ".png");

   void run(ResourceManager var1, Output var2);

   SpriteSourceType type();

   public interface SpriteSupplier extends Function<SpriteResourceLoader, SpriteContents> {
      default void discard() {
      }
   }

   public interface Output {
      default void add(ResourceLocation var1, Resource var2) {
         this.add(var1, (var2x) -> {
            return var2x.loadSprite(var1, var2);
         });
      }

      void add(ResourceLocation var1, SpriteSupplier var2);

      void removeAll(Predicate<ResourceLocation> var1);
   }
}
