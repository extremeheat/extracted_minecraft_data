package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.VillagerTradeItemLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.WanderingTrader;

public class WanderingTraderRenderer extends MobRenderer<WanderingTrader, VillagerModel<WanderingTrader>> {
   private static final ResourceLocation VILLAGER_BASE_SKIN = new ResourceLocation("textures/entity/wandering_trader.png");

   public WanderingTraderRenderer(EntityRenderDispatcher var1) {
      super(var1, new VillagerModel(0.0F), 0.5F);
      this.addLayer(new CustomHeadLayer(this));
      this.addLayer(new VillagerTradeItemLayer(this));
   }

   protected ResourceLocation getTextureLocation(WanderingTrader var1) {
      return VILLAGER_BASE_SKIN;
   }

   protected void scale(WanderingTrader var1, float var2) {
      float var3 = 0.9375F;
      GlStateManager.scalef(0.9375F, 0.9375F, 0.9375F);
   }
}
