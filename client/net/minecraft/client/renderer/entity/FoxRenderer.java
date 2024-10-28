package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.FoxModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.FoxHeldItemLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Fox;

public class FoxRenderer extends MobRenderer<Fox, FoxModel<Fox>> {
   private static final ResourceLocation RED_FOX_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/fox/fox.png");
   private static final ResourceLocation RED_FOX_SLEEP_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/fox/fox_sleep.png");
   private static final ResourceLocation SNOW_FOX_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/fox/snow_fox.png");
   private static final ResourceLocation SNOW_FOX_SLEEP_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/fox/snow_fox_sleep.png");

   public FoxRenderer(EntityRendererProvider.Context var1) {
      super(var1, new FoxModel(var1.bakeLayer(ModelLayers.FOX)), 0.4F);
      this.addLayer(new FoxHeldItemLayer(this, var1.getItemInHandRenderer()));
   }

   protected void setupRotations(Fox var1, PoseStack var2, float var3, float var4, float var5, float var6) {
      super.setupRotations(var1, var2, var3, var4, var5, var6);
      if (var1.isPouncing() || var1.isFaceplanted()) {
         float var7 = -Mth.lerp(var5, var1.xRotO, var1.getXRot());
         var2.mulPose(Axis.XP.rotationDegrees(var7));
      }

   }

   public ResourceLocation getTextureLocation(Fox var1) {
      if (var1.getVariant() == Fox.Type.RED) {
         return var1.isSleeping() ? RED_FOX_SLEEP_TEXTURE : RED_FOX_TEXTURE;
      } else {
         return var1.isSleeping() ? SNOW_FOX_SLEEP_TEXTURE : SNOW_FOX_TEXTURE;
      }
   }
}
