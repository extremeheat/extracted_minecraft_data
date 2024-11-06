package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.WitchModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.WitchItemLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HoldingEntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.WitchRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class WitchRenderer extends MobRenderer<Witch, WitchRenderState, WitchModel> {
   private static final ResourceLocation WITCH_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/witch.png");

   public WitchRenderer(EntityRendererProvider.Context var1) {
      super(var1, new WitchModel(var1.bakeLayer(ModelLayers.WITCH)), 0.5F);
      this.addLayer(new WitchItemLayer(this));
   }

   public ResourceLocation getTextureLocation(WitchRenderState var1) {
      return WITCH_LOCATION;
   }

   public WitchRenderState createRenderState() {
      return new WitchRenderState();
   }

   public void extractRenderState(Witch var1, WitchRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      HoldingEntityRenderState.extractHoldingEntityRenderState(var1, var2, this.itemModelResolver);
      var2.entityId = var1.getId();
      ItemStack var4 = var1.getMainHandItem();
      var2.isHoldingItem = !var4.isEmpty();
      var2.isHoldingPotion = var4.is(Items.POTION);
   }

   // $FF: synthetic method
   public ResourceLocation getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((WitchRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
