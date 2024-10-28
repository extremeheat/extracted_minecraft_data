package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.WolfArmorLayer;
import net.minecraft.client.renderer.entity.layers.WolfCollarLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.animal.Wolf;

public class WolfRenderer extends MobRenderer<Wolf, WolfModel<Wolf>> {
   public WolfRenderer(EntityRendererProvider.Context var1) {
      super(var1, new WolfModel(var1.bakeLayer(ModelLayers.WOLF)), 0.5F);
      this.addLayer(new WolfArmorLayer(this, var1.getModelSet()));
      this.addLayer(new WolfCollarLayer(this));
   }

   protected float getBob(Wolf var1, float var2) {
      return var1.getTailAngle();
   }

   public void render(Wolf var1, float var2, float var3, PoseStack var4, MultiBufferSource var5, int var6) {
      if (var1.isWet()) {
         float var7 = var1.getWetShade(var3);
         ((WolfModel)this.model).setColor(FastColor.ARGB32.colorFromFloat(1.0F, var7, var7, var7));
      }

      super.render(var1, var2, var3, var4, var5, var6);
      if (var1.isWet()) {
         ((WolfModel)this.model).setColor(-1);
      }

   }

   public ResourceLocation getTextureLocation(Wolf var1) {
      return var1.getTexture();
   }
}
