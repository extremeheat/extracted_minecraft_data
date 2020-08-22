package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.BatModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ambient.Bat;

public class BatRenderer extends MobRenderer {
   private static final ResourceLocation BAT_LOCATION = new ResourceLocation("textures/entity/bat.png");

   public BatRenderer(EntityRenderDispatcher var1) {
      super(var1, new BatModel(), 0.25F);
   }

   public ResourceLocation getTextureLocation(Bat var1) {
      return BAT_LOCATION;
   }

   protected void scale(Bat var1, PoseStack var2, float var3) {
      var2.scale(0.35F, 0.35F, 0.35F);
   }

   protected void setupRotations(Bat var1, PoseStack var2, float var3, float var4, float var5) {
      if (var1.isResting()) {
         var2.translate(0.0D, -0.10000000149011612D, 0.0D);
      } else {
         var2.translate(0.0D, (double)(Mth.cos(var3 * 0.3F) * 0.1F), 0.0D);
      }

      super.setupRotations(var1, var2, var3, var4, var5);
   }
}
