package net.minecraft.client.resources.model;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.world.item.ItemDisplayContext;

public interface UnbakedModel extends ResolvableModel {
   boolean DEFAULT_AMBIENT_OCCLUSION = true;
   GuiLight DEFAULT_GUI_LIGHT = UnbakedModel.GuiLight.SIDE;

   BakedModel bake(TextureSlots var1, ModelBaker var2, ModelState var3, boolean var4, boolean var5, ItemTransforms var6);

   @Nullable
   default Boolean getAmbientOcclusion() {
      return null;
   }

   @Nullable
   default GuiLight getGuiLight() {
      return null;
   }

   @Nullable
   default ItemTransforms getTransforms() {
      return null;
   }

   default TextureSlots.Data getTextureSlots() {
      return TextureSlots.Data.EMPTY;
   }

   @Nullable
   default UnbakedModel getParent() {
      return null;
   }

   static BakedModel bakeWithTopModelValues(UnbakedModel var0, ModelBaker var1, ModelState var2) {
      TextureSlots var3 = getTopTextureSlots(var0, var1.rootName());
      boolean var4 = getTopAmbientOcclusion(var0);
      boolean var5 = getTopGuiLight(var0).lightLikeBlock();
      ItemTransforms var6 = getTopTransforms(var0);
      return var0.bake(var3, var1, var2, var4, var5, var6);
   }

   static TextureSlots getTopTextureSlots(UnbakedModel var0, ModelDebugName var1) {
      TextureSlots.Resolver var2;
      for(var2 = new TextureSlots.Resolver(); var0 != null; var0 = var0.getParent()) {
         var2.addLast(var0.getTextureSlots());
      }

      return var2.resolve(var1);
   }

   static boolean getTopAmbientOcclusion(UnbakedModel var0) {
      while(var0 != null) {
         Boolean var1 = var0.getAmbientOcclusion();
         if (var1 != null) {
            return var1;
         }

         var0 = var0.getParent();
      }

      return true;
   }

   static GuiLight getTopGuiLight(UnbakedModel var0) {
      while(var0 != null) {
         GuiLight var1 = var0.getGuiLight();
         if (var1 != null) {
            return var1;
         }

         var0 = var0.getParent();
      }

      return DEFAULT_GUI_LIGHT;
   }

   static ItemTransform getTopTransform(UnbakedModel var0, ItemDisplayContext var1) {
      for(; var0 != null; var0 = var0.getParent()) {
         ItemTransforms var2 = var0.getTransforms();
         if (var2 != null) {
            ItemTransform var3 = var2.getTransform(var1);
            if (var3 != ItemTransform.NO_TRANSFORM) {
               return var3;
            }
         }
      }

      return ItemTransform.NO_TRANSFORM;
   }

   static ItemTransforms getTopTransforms(UnbakedModel var0) {
      ItemTransform var1 = getTopTransform(var0, ItemDisplayContext.THIRD_PERSON_LEFT_HAND);
      ItemTransform var2 = getTopTransform(var0, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND);
      ItemTransform var3 = getTopTransform(var0, ItemDisplayContext.FIRST_PERSON_LEFT_HAND);
      ItemTransform var4 = getTopTransform(var0, ItemDisplayContext.FIRST_PERSON_RIGHT_HAND);
      ItemTransform var5 = getTopTransform(var0, ItemDisplayContext.HEAD);
      ItemTransform var6 = getTopTransform(var0, ItemDisplayContext.GUI);
      ItemTransform var7 = getTopTransform(var0, ItemDisplayContext.GROUND);
      ItemTransform var8 = getTopTransform(var0, ItemDisplayContext.FIXED);
      return new ItemTransforms(var1, var2, var3, var4, var5, var6, var7, var8);
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
