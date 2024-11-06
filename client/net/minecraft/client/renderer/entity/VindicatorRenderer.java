package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.IllagerRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Vindicator;

public class VindicatorRenderer extends IllagerRenderer<Vindicator, IllagerRenderState> {
   private static final ResourceLocation VINDICATOR = ResourceLocation.withDefaultNamespace("textures/entity/illager/vindicator.png");

   public VindicatorRenderer(EntityRendererProvider.Context var1) {
      super(var1, new IllagerModel(var1.bakeLayer(ModelLayers.VINDICATOR)), 0.5F);
      this.addLayer(new ItemInHandLayer<IllagerRenderState, IllagerModel<IllagerRenderState>>(this, this) {
         public void render(PoseStack var1, MultiBufferSource var2, int var3, IllagerRenderState var4, float var5, float var6) {
            if (var4.isAggressive) {
               super.render(var1, var2, var3, (ArmedEntityRenderState)var4, var5, var6);
            }

         }
      });
   }

   public ResourceLocation getTextureLocation(IllagerRenderState var1) {
      return VINDICATOR;
   }

   public IllagerRenderState createRenderState() {
      return new IllagerRenderState();
   }

   // $FF: synthetic method
   public ResourceLocation getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((IllagerRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
