package net.minecraft.client.renderer.texture.atlas;

import com.mojang.serialization.MapCodec;
import java.util.function.Predicate;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jspecify.annotations.Nullable;

public interface SpriteSource {
   FileToIdConverter TEXTURE_ID_CONVERTER = new FileToIdConverter("textures", ".png");

   void run(ResourceManager var1, Output var2);

   MapCodec<? extends SpriteSource> codec();

   public interface Output {
      default void add(ResourceLocation var1, Resource var2) {
         this.add(var1, (DiscardableLoader)((var2x) -> var2x.loadSprite(var1, var2)));
      }

      void add(ResourceLocation var1, DiscardableLoader var2);

      void removeAll(Predicate<ResourceLocation> var1);
   }

   public interface DiscardableLoader extends Loader {
      default void discard() {
      }
   }

   @FunctionalInterface
   public interface Loader {
      @Nullable SpriteContents get(SpriteResourceLoader var1);
   }
}
