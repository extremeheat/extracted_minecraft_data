package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.SnowGolemModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.SnowGolemHeadLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class SnowGolemRenderer extends MobRenderer<SnowGolem, LivingEntityRenderState, SnowGolemModel> {
   private static final ResourceLocation SNOW_GOLEM_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/snow_golem.png");

   public SnowGolemRenderer(EntityRendererProvider.Context var1) {
      super(var1, new SnowGolemModel(var1.bakeLayer(ModelLayers.SNOW_GOLEM)), 0.5F);
      this.addLayer(new SnowGolemHeadLayer(this, var1.getBlockRenderDispatcher(), var1.getItemRenderer()));
   }

   public ResourceLocation getTextureLocation(LivingEntityRenderState var1) {
      return SNOW_GOLEM_LOCATION;
   }

   public LivingEntityRenderState createRenderState() {
      return new LivingEntityRenderState();
   }

   public void extractRenderState(SnowGolem var1, LivingEntityRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.headItem = var1.hasPumpkin() ? new ItemStack(Items.CARVED_PUMPKIN) : ItemStack.EMPTY;
      var2.headItemModel = this.itemRenderer.resolveItemModel(var2.headItem, var1, ItemDisplayContext.HEAD);
   }
}
