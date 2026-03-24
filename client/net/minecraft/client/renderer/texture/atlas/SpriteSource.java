package net.minecraft.client.renderer.texture.atlas;

import com.mojang.serialization.MapCodec;
import java.util.function.Predicate;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jspecify.annotations.Nullable;

public interface SpriteSource {
   FileToIdConverter TEXTURE_ID_CONVERTER = new FileToIdConverter("textures", ".png");

   void run(ResourceManager resourceManager, Output output);

   MapCodec<? extends SpriteSource> codec();

   public interface Output {
      default void add(final Identifier id, final Resource resource) {
         this.add(id, (DiscardableLoader)((loader) -> loader.loadSprite(id, resource)));
      }

      void add(Identifier id, DiscardableLoader sprite);

      void removeAll(Predicate<Identifier> predicate);
   }

   public interface DiscardableLoader extends Loader {
      default void discard() {
      }
   }

   @FunctionalInterface
   public interface Loader {
      @Nullable SpriteContents get(SpriteResourceLoader loader);
   }
}
