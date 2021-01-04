package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.minecraft.client.model.CodModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Cod;

public class CodRenderer extends MobRenderer<Cod, CodModel<Cod>> {
   private static final ResourceLocation COD_LOCATION = new ResourceLocation("textures/entity/fish/cod.png");

   public CodRenderer(EntityRenderDispatcher var1) {
      super(var1, new CodModel(), 0.3F);
   }

   @Nullable
   protected ResourceLocation getTextureLocation(Cod var1) {
      return COD_LOCATION;
   }

   protected void setupRotations(Cod var1, float var2, float var3, float var4) {
      super.setupRotations(var1, var2, var3, var4);
      float var5 = 4.3F * Mth.sin(0.6F * var2);
      GlStateManager.rotatef(var5, 0.0F, 1.0F, 0.0F);
      if (!var1.isInWater()) {
         GlStateManager.translatef(0.1F, 0.1F, -0.1F);
         GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
      }

   }
}
