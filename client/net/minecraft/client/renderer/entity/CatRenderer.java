package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.CatModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.CatCollarLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

public class CatRenderer extends MobRenderer<Cat, CatModel<Cat>> {
   public CatRenderer(EntityRendererProvider.Context var1) {
      super(var1, new CatModel<>(var1.bakeLayer(ModelLayers.CAT)), 0.4F);
      this.addLayer(new CatCollarLayer(this, var1.getModelSet()));
   }

   public ResourceLocation getTextureLocation(Cat var1) {
      return var1.getTextureId();
   }

   protected void scale(Cat var1, PoseStack var2, float var3) {
      super.scale(var1, var2, var3);
      var2.scale(0.8F, 0.8F, 0.8F);
   }

   protected void setupRotations(Cat var1, PoseStack var2, float var3, float var4, float var5, float var6) {
      super.setupRotations(var1, var2, var3, var4, var5, var6);
      float var7 = var1.getLieDownAmount(var5);
      if (var7 > 0.0F) {
         var2.translate(0.4F * var7, 0.15F * var7, 0.1F * var7);
         var2.mulPose(Axis.ZP.rotationDegrees(Mth.rotLerp(var7, 0.0F, 90.0F)));
         BlockPos var8 = var1.blockPosition();

         for(Player var11 : var1.level().getEntitiesOfClass(Player.class, new AABB(var8).inflate(2.0, 2.0, 2.0))) {
            if (var11.isSleeping()) {
               var2.translate(0.15F * var7, 0.0F, 0.0F);
               break;
            }
         }
      }
   }
}
