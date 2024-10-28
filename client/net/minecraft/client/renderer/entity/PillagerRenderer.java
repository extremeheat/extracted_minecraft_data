package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.IllagerRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Pillager;

public class PillagerRenderer extends IllagerRenderer<Pillager, IllagerRenderState> {
   private static final ResourceLocation PILLAGER = ResourceLocation.withDefaultNamespace("textures/entity/illager/pillager.png");

   public PillagerRenderer(EntityRendererProvider.Context var1) {
      super(var1, new IllagerModel(var1.bakeLayer(ModelLayers.PILLAGER)), 0.5F);
      this.addLayer(new ItemInHandLayer(this, var1.getItemRenderer()));
   }

   public ResourceLocation getTextureLocation(IllagerRenderState var1) {
      return PILLAGER;
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
