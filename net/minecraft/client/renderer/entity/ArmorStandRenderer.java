package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
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

public class ArmorStandRenderer extends LivingEntityRenderer {
   public static final ResourceLocation DEFAULT_SKIN_LOCATION = new ResourceLocation("textures/entity/armorstand/wood.png");

   public ArmorStandRenderer(EntityRenderDispatcher var1) {
      super(var1, new ArmorStandModel(), 0.0F);
      this.addLayer(new HumanoidArmorLayer(this, new ArmorStandArmorModel(0.5F), new ArmorStandArmorModel(1.0F)));
      this.addLayer(new ItemInHandLayer(this));
      this.addLayer(new ElytraLayer(this));
      this.addLayer(new CustomHeadLayer(this));
   }

   public ResourceLocation getTextureLocation(ArmorStand var1) {
      return DEFAULT_SKIN_LOCATION;
   }

   protected void setupRotations(ArmorStand var1, PoseStack var2, float var3, float var4, float var5) {
      var2.mulPose(Vector3f.YP.rotationDegrees(180.0F - var4));
      float var6 = (float)(var1.level.getGameTime() - var1.lastHit) + var5;
      if (var6 < 5.0F) {
         var2.mulPose(Vector3f.YP.rotationDegrees(Mth.sin(var6 / 1.5F * 3.1415927F) * 3.0F));
      }

   }

   protected boolean shouldShowName(ArmorStand var1) {
      double var2 = this.entityRenderDispatcher.distanceToSqr(var1);
      float var4 = var1.isCrouching() ? 32.0F : 64.0F;
      return var2 >= (double)(var4 * var4) ? false : var1.isCustomNameVisible();
   }

   protected boolean isVisible(ArmorStand var1, boolean var2) {
      return !var1.isInvisible();
   }

   // $FF: synthetic method
   protected boolean shouldShowName(LivingEntity var1) {
      return this.shouldShowName((ArmorStand)var1);
   }
}
