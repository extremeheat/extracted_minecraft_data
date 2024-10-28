package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.VexModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Vex;

public class VexRenderer extends MobRenderer<Vex, VexModel> {
   private static final ResourceLocation VEX_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/illager/vex.png");
   private static final ResourceLocation VEX_CHARGING_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/illager/vex_charging.png");

   public VexRenderer(EntityRendererProvider.Context var1) {
      super(var1, new VexModel(var1.bakeLayer(ModelLayers.VEX)), 0.3F);
      this.addLayer(new ItemInHandLayer(this, var1.getItemInHandRenderer()));
   }

   protected int getBlockLightLevel(Vex var1, BlockPos var2) {
      return 15;
   }

   public ResourceLocation getTextureLocation(Vex var1) {
      return var1.isCharging() ? VEX_CHARGING_LOCATION : VEX_LOCATION;
   }
}
