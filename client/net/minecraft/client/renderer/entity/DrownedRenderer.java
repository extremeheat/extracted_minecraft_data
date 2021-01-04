package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.minecraft.client.model.DrownedModel;
import net.minecraft.client.renderer.entity.layers.DrownedOuterLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Zombie;

public class DrownedRenderer extends AbstractZombieRenderer<Drowned, DrownedModel<Drowned>> {
   private static final ResourceLocation DROWNED_LOCATION = new ResourceLocation("textures/entity/zombie/drowned.png");

   public DrownedRenderer(EntityRenderDispatcher var1) {
      super(var1, new DrownedModel(0.0F, 0.0F, 64, 64), new DrownedModel(0.5F, true), new DrownedModel(1.0F, true));
      this.addLayer(new DrownedOuterLayer(this));
   }

   @Nullable
   protected ResourceLocation getTextureLocation(Zombie var1) {
      return DROWNED_LOCATION;
   }

   protected void setupRotations(Drowned var1, float var2, float var3, float var4) {
      float var5 = var1.getSwimAmount(var4);
      super.setupRotations((Zombie)var1, var2, var3, var4);
      if (var5 > 0.0F) {
         GlStateManager.rotatef(Mth.lerp(var5, var1.xRot, -10.0F - var1.xRot), 1.0F, 0.0F, 0.0F);
      }

   }
}
