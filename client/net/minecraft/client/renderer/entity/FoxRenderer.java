package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.minecraft.client.model.FoxModel;
import net.minecraft.client.renderer.entity.layers.FoxHeldItemLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Fox;

public class FoxRenderer extends MobRenderer<Fox, FoxModel<Fox>> {
   private static final ResourceLocation RED_FOX_TEXTURE = new ResourceLocation("textures/entity/fox/fox.png");
   private static final ResourceLocation RED_FOX_SLEEP_TEXTURE = new ResourceLocation("textures/entity/fox/fox_sleep.png");
   private static final ResourceLocation SNOW_FOX_TEXTURE = new ResourceLocation("textures/entity/fox/snow_fox.png");
   private static final ResourceLocation SNOW_FOX_SLEEP_TEXTURE = new ResourceLocation("textures/entity/fox/snow_fox_sleep.png");

   public FoxRenderer(EntityRenderDispatcher var1) {
      super(var1, new FoxModel(), 0.4F);
      this.addLayer(new FoxHeldItemLayer(this));
   }

   protected void setupRotations(Fox var1, float var2, float var3, float var4) {
      super.setupRotations(var1, var2, var3, var4);
      if (var1.isPouncing() || var1.isFaceplanted()) {
         GlStateManager.rotatef(-Mth.lerp(var4, var1.xRotO, var1.xRot), 1.0F, 0.0F, 0.0F);
      }

   }

   @Nullable
   protected ResourceLocation getTextureLocation(Fox var1) {
      if (var1.getFoxType() == Fox.Type.RED) {
         return var1.isSleeping() ? RED_FOX_SLEEP_TEXTURE : RED_FOX_TEXTURE;
      } else {
         return var1.isSleeping() ? SNOW_FOX_SLEEP_TEXTURE : SNOW_FOX_TEXTURE;
      }
   }
}
