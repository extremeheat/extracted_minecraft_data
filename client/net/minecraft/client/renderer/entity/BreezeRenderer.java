package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.BreezeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.BreezeEyesLayer;
import net.minecraft.client.renderer.entity.layers.BreezeWindLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.breeze.Breeze;

public class BreezeRenderer extends MobRenderer<Breeze, BreezeModel<Breeze>> {
   private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/breeze/breeze.png");

   public BreezeRenderer(EntityRendererProvider.Context var1) {
      super(var1, new BreezeModel(var1.bakeLayer(ModelLayers.BREEZE)), 0.5F);
      this.addLayer(new BreezeWindLayer(var1, this));
      this.addLayer(new BreezeEyesLayer(this));
   }

   public void render(Breeze var1, float var2, float var3, PoseStack var4, MultiBufferSource var5, int var6) {
      BreezeModel var7 = (BreezeModel)this.getModel();
      enable(var7, var7.head(), var7.rods());
      super.render(var1, var2, var3, var4, var5, var6);
   }

   public ResourceLocation getTextureLocation(Breeze var1) {
      return TEXTURE_LOCATION;
   }

   public static BreezeModel<Breeze> enable(BreezeModel<Breeze> var0, ModelPart... var1) {
      var0.head().visible = false;
      var0.eyes().visible = false;
      var0.rods().visible = false;
      var0.wind().visible = false;
      ModelPart[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ModelPart var5 = var2[var4];
         var5.visible = true;
      }

      return var0;
   }
}
