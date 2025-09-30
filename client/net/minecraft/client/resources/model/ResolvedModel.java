package net.minecraft.client.resources.model;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.item.ItemDisplayContext;

public interface ResolvedModel extends ModelDebugName {
   boolean DEFAULT_AMBIENT_OCCLUSION = true;
   UnbakedModel.GuiLight DEFAULT_GUI_LIGHT = UnbakedModel.GuiLight.SIDE;

   UnbakedModel wrapped();

   @Nullable
   ResolvedModel parent();

   static TextureSlots findTopTextureSlots(ResolvedModel var0) {
      ResolvedModel var1 = var0;

      TextureSlots.Resolver var2;
      for(var2 = new TextureSlots.Resolver(); var1 != null; var1 = var1.parent()) {
         var2.addLast(var1.wrapped().textureSlots());
      }

      return var2.resolve(var0);
   }

   default TextureSlots getTopTextureSlots() {
      return findTopTextureSlots(this);
   }

   static boolean findTopAmbientOcclusion(ResolvedModel var0) {
      while(var0 != null) {
         Boolean var1 = var0.wrapped().ambientOcclusion();
         if (var1 != null) {
            return var1;
         }

         var0 = var0.parent();
      }

      return true;
   }

   default boolean getTopAmbientOcclusion() {
      return findTopAmbientOcclusion(this);
   }

   static UnbakedModel.GuiLight findTopGuiLight(ResolvedModel var0) {
      while(var0 != null) {
         UnbakedModel.GuiLight var1 = var0.wrapped().guiLight();
         if (var1 != null) {
            return var1;
         }

         var0 = var0.parent();
      }

      return DEFAULT_GUI_LIGHT;
   }

   default UnbakedModel.GuiLight getTopGuiLight() {
      return findTopGuiLight(this);
   }

   static UnbakedGeometry findTopGeometry(ResolvedModel var0) {
      while(var0 != null) {
         UnbakedGeometry var1 = var0.wrapped().geometry();
         if (var1 != null) {
            return var1;
         }

         var0 = var0.parent();
      }

      return UnbakedGeometry.EMPTY;
   }

   default UnbakedGeometry getTopGeometry() {
      return findTopGeometry(this);
   }

   default QuadCollection bakeTopGeometry(TextureSlots var1, ModelBaker var2, ModelState var3) {
      return this.getTopGeometry().bake(var1, var2, var3, this);
   }

   static TextureAtlasSprite resolveParticleSprite(TextureSlots var0, ModelBaker var1, ModelDebugName var2) {
      return var1.sprites().resolveSlot(var0, "particle", var2);
   }

   default TextureAtlasSprite resolveParticleSprite(TextureSlots var1, ModelBaker var2) {
      return resolveParticleSprite(var1, var2, this);
   }

   static ItemTransform findTopTransform(ResolvedModel var0, ItemDisplayContext var1) {
      for(; var0 != null; var0 = var0.parent()) {
         ItemTransforms var2 = var0.wrapped().transforms();
         if (var2 != null) {
            ItemTransform var3 = var2.getTransform(var1);
            if (var3 != ItemTransform.NO_TRANSFORM) {
               return var3;
            }
         }
      }

      return ItemTransform.NO_TRANSFORM;
   }

   static ItemTransforms findTopTransforms(ResolvedModel var0) {
      ItemTransform var1 = findTopTransform(var0, ItemDisplayContext.THIRD_PERSON_LEFT_HAND);
      ItemTransform var2 = findTopTransform(var0, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND);
      ItemTransform var3 = findTopTransform(var0, ItemDisplayContext.FIRST_PERSON_LEFT_HAND);
      ItemTransform var4 = findTopTransform(var0, ItemDisplayContext.FIRST_PERSON_RIGHT_HAND);
      ItemTransform var5 = findTopTransform(var0, ItemDisplayContext.HEAD);
      ItemTransform var6 = findTopTransform(var0, ItemDisplayContext.GUI);
      ItemTransform var7 = findTopTransform(var0, ItemDisplayContext.GROUND);
      ItemTransform var8 = findTopTransform(var0, ItemDisplayContext.FIXED);
      ItemTransform var9 = findTopTransform(var0, ItemDisplayContext.ON_SHELF);
      return new ItemTransforms(var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   default ItemTransforms getTopTransforms() {
      return findTopTransforms(this);
   }
}
