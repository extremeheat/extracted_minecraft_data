package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Wolf;

public class WolfCollarLayer extends RenderLayer<Wolf, WolfModel<Wolf>> {
   private static final ResourceLocation WOLF_COLLAR_LOCATION = new ResourceLocation("textures/entity/wolf/wolf_collar.png");

   public WolfCollarLayer(RenderLayerParent<Wolf, WolfModel<Wolf>> var1) {
      super(var1);
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, Wolf var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      if (var4.isTame() && !var4.isInvisible()) {
         float[] var11 = var4.getCollarColor().getTextureDiffuseColors();
         renderColoredCutoutModel(this.getParentModel(), WOLF_COLLAR_LOCATION, var1, var2, var3, var4, var11[0], var11[1], var11[2]);
      }
   }
}
