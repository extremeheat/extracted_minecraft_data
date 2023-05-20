package net.minecraft.client.renderer.texture.atlas;

import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

public interface SpriteSource {
   FileToIdConverter TEXTURE_ID_CONVERTER = new FileToIdConverter("textures", ".png");

   void run(ResourceManager var1, SpriteSource.Output var2);

   SpriteSourceType type();

   public interface Output {
      default void add(ResourceLocation var1, Resource var2) {
         this.add(var1, () -> SpriteLoader.loadSprite(var1, var2));
      }

      void add(ResourceLocation var1, SpriteSource.SpriteSupplier var2);

      void removeAll(Predicate<ResourceLocation> var1);
   }

   public interface SpriteSupplier extends Supplier<SpriteContents> {
      default void discard() {
      }
   }
}
