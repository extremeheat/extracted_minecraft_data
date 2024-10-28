package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import javax.annotation.Nullable;
import net.minecraft.client.model.ArmorStandArmorModel;
import net.minecraft.client.model.ArmorStandModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.decoration.ArmorStand;

public class ArmorStandRenderer extends LivingEntityRenderer<ArmorStand, ArmorStandArmorModel> {
   public static final ResourceLocation DEFAULT_SKIN_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/armorstand/wood.png");

   public ArmorStandRenderer(EntityRendererProvider.Context var1) {
      super(var1, new ArmorStandModel(var1.bakeLayer(ModelLayers.ARMOR_STAND)), 0.0F);
      this.addLayer(new HumanoidArmorLayer(this, new ArmorStandArmorModel(var1.bakeLayer(ModelLayers.ARMOR_STAND_INNER_ARMOR)), new ArmorStandArmorModel(var1.bakeLayer(ModelLayers.ARMOR_STAND_OUTER_ARMOR)), var1.getModelManager()));
      this.addLayer(new ItemInHandLayer(this, var1.getItemInHandRenderer()));
      this.addLayer(new ElytraLayer(this, var1.getModelSet()));
      this.addLayer(new CustomHeadLayer(this, var1.getModelSet(), var1.getItemInHandRenderer()));
   }

   public ResourceLocation getTextureLocation(ArmorStand var1) {
      return DEFAULT_SKIN_LOCATION;
   }

   protected void setupRotations(ArmorStand var1, PoseStack var2, float var3, float var4, float var5, float var6) {
      var2.mulPose(Axis.YP.rotationDegrees(180.0F - var4));
      float var7 = (float)(var1.level().getGameTime() - var1.lastHit) + var5;
      if (var7 < 5.0F) {
         var2.mulPose(Axis.YP.rotationDegrees(Mth.sin(var7 / 1.5F * 3.1415927F) * 3.0F));
      }

   }

   protected boolean shouldShowName(ArmorStand var1) {
      double var2 = this.entityRenderDispatcher.distanceToSqr(var1);
      float var4 = var1.isCrouching() ? 32.0F : 64.0F;
      return var2 >= (double)(var4 * var4) ? false : var1.isCustomNameVisible();
   }

   @Nullable
   protected RenderType getRenderType(ArmorStand var1, boolean var2, boolean var3, boolean var4) {
      if (!var1.isMarker()) {
         return super.getRenderType(var1, var2, var3, var4);
      } else {
         ResourceLocation var5 = this.getTextureLocation(var1);
         if (var3) {
            return RenderType.entityTranslucent(var5, false);
         } else {
            return var2 ? RenderType.entityCutoutNoCull(var5, false) : null;
         }
      }
   }
}
