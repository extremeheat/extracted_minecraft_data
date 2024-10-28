package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Vindicator;

public class VindicatorRenderer extends IllagerRenderer<Vindicator> {
   private static final ResourceLocation VINDICATOR = ResourceLocation.withDefaultNamespace("textures/entity/illager/vindicator.png");

   public VindicatorRenderer(EntityRendererProvider.Context var1) {
      super(var1, new IllagerModel(var1.bakeLayer(ModelLayers.VINDICATOR)), 0.5F);
      this.addLayer(new ItemInHandLayer<Vindicator, IllagerModel<Vindicator>>(this, this, var1.getItemInHandRenderer()) {
         public void render(PoseStack var1, MultiBufferSource var2, int var3, Vindicator var4, float var5, float var6, float var7, float var8, float var9, float var10) {
            if (var4.isAggressive()) {
               super.render(var1, var2, var3, (LivingEntity)var4, var5, var6, var7, var8, var9, var10);
            }

         }
      });
   }

   public ResourceLocation getTextureLocation(Vindicator var1) {
      return VINDICATOR;
   }
}
