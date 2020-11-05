package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.WanderingTrader;

public class WanderingTraderRenderer extends MobRenderer<WanderingTrader, VillagerModel<WanderingTrader>> {
   private static final ResourceLocation VILLAGER_BASE_SKIN = new ResourceLocation("textures/entity/wandering_trader.png");

   public WanderingTraderRenderer(EntityRenderDispatcher var1) {
      super(var1, new VillagerModel(0.0F), 0.5F);
      this.addLayer(new CustomHeadLayer(this));
      this.addLayer(new CrossedArmsItemLayer(this));
   }

   public ResourceLocation getTextureLocation(WanderingTrader var1) {
      return VILLAGER_BASE_SKIN;
   }

   protected void scale(WanderingTrader var1, PoseStack var2, float var3) {
      float var4 = 0.9375F;
      var2.scale(0.9375F, 0.9375F, 0.9375F);
   }
}
