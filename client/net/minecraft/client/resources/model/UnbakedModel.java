package net.minecraft.client.resources.model;

import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public interface UnbakedModel {
   String PARTICLE_TEXTURE_REFERENCE = "particle";

   default @Nullable Boolean ambientOcclusion() {
      return null;
   }

   default @Nullable GuiLight guiLight() {
      return null;
   }

   default @Nullable ItemTransforms transforms() {
      return null;
   }

   default TextureSlots.Data textureSlots() {
      return TextureSlots.Data.EMPTY;
   }

   default @Nullable UnbakedGeometry geometry() {
      return null;
   }

   default @Nullable Identifier parent() {
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
