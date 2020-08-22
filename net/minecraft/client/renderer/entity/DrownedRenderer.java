package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.DrownedModel;
import net.minecraft.client.renderer.entity.layers.DrownedOuterLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Zombie;

public class DrownedRenderer extends AbstractZombieRenderer {
   private static final ResourceLocation DROWNED_LOCATION = new ResourceLocation("textures/entity/zombie/drowned.png");

   public DrownedRenderer(EntityRenderDispatcher var1) {
      super(var1, new DrownedModel(0.0F, 0.0F, 64, 64), new DrownedModel(0.5F, true), new DrownedModel(1.0F, true));
      this.addLayer(new DrownedOuterLayer(this));
   }

   public ResourceLocation getTextureLocation(Zombie var1) {
      return DROWNED_LOCATION;
   }

   protected void setupRotations(Drowned var1, PoseStack var2, float var3, float var4, float var5) {
      super.setupRotations((Zombie)var1, var2, var3, var4, var5);
      float var6 = var1.getSwimAmount(var5);
      if (var6 > 0.0F) {
         var2.mulPose(Vector3f.XP.rotationDegrees(Mth.lerp(var6, var1.xRot, -10.0F - var1.xRot)));
      }

   }
}
