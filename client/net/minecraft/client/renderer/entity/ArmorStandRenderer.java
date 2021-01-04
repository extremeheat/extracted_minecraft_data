package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.ArmorStandArmorModel;
import net.minecraft.client.model.ArmorStandModel;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;

public class ArmorStandRenderer extends LivingEntityRenderer<ArmorStand, ArmorStandArmorModel> {
   public static final ResourceLocation DEFAULT_SKIN_LOCATION = new ResourceLocation("textures/entity/armorstand/wood.png");

   public ArmorStandRenderer(EntityRenderDispatcher var1) {
      super(var1, new ArmorStandModel(), 0.0F);
      this.addLayer(new HumanoidArmorLayer(this, new ArmorStandArmorModel(0.5F), new ArmorStandArmorModel(1.0F)));
      this.addLayer(new ItemInHandLayer(this));
      this.addLayer(new ElytraLayer(this));
      this.addLayer(new CustomHeadLayer(this));
   }

   protected ResourceLocation getTextureLocation(ArmorStand var1) {
      return DEFAULT_SKIN_LOCATION;
   }

   protected void setupRotations(ArmorStand var1, float var2, float var3, float var4) {
      GlStateManager.rotatef(180.0F - var3, 0.0F, 1.0F, 0.0F);
      float var5 = (float)(var1.level.getGameTime() - var1.lastHit) + var4;
      if (var5 < 5.0F) {
         GlStateManager.rotatef(Mth.sin(var5 / 1.5F * 3.1415927F) * 3.0F, 0.0F, 1.0F, 0.0F);
      }

   }

   protected boolean shouldShowName(ArmorStand var1) {
      return var1.isCustomNameVisible();
   }

   public void render(ArmorStand var1, double var2, double var4, double var6, float var8, float var9) {
      if (var1.isMarker()) {
         this.onlySolidLayers = true;
      }

      super.render((LivingEntity)var1, var2, var4, var6, var8, var9);
      if (var1.isMarker()) {
         this.onlySolidLayers = false;
      }

   }

   // $FF: synthetic method
   protected boolean shouldShowName(LivingEntity var1) {
      return this.shouldShowName((ArmorStand)var1);
   }
}
