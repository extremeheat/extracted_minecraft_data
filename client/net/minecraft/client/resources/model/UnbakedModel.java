package net.minecraft.client.resources.model;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.resources.ResourceLocation;

public interface UnbakedModel {
   String PARTICLE_TEXTURE_REFERENCE = "particle";

   @Nullable
   default Boolean ambientOcclusion() {
      return null;
   }

   @Nullable
   default GuiLight guiLight() {
      return null;
   }

   @Nullable
   default ItemTransforms transforms() {
      return null;
   }

   default TextureSlots.Data textureSlots() {
      return TextureSlots.Data.EMPTY;
   }

   @Nullable
   default UnbakedGeometry geometry() {
      return null;
   }

   @Nullable
   default ResourceLocation parent() {
      return null;
   }

   public static enum GuiLight {
      FRONT("front"),
      SIDE("side");

      private final String name;

      private GuiLight(final String var3) {
         this.name = var3;
      }

      public static GuiLight getByName(String var0) {
         for(GuiLight var4 : values()) {
            if (var4.name.equals(var0)) {
               return var4;
            }
         }

         throw new IllegalArgumentException("Invalid gui light: " + var0);
      }

      public boolean lightLikeBlock() {
         return this == SIDE;
      }

      // $FF: synthetic method
      private static GuiLight[] $values() {
         return new GuiLight[]{FRONT, SIDE};
      }
   }
}
