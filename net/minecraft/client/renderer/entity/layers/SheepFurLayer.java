package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.SheepFurModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;

public class SheepFurLayer extends RenderLayer {
   private static final ResourceLocation SHEEP_FUR_LOCATION = new ResourceLocation("textures/entity/sheep/sheep_fur.png");
   private final SheepFurModel model = new SheepFurModel();

   public SheepFurLayer(RenderLayerParent var1) {
      super(var1);
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, Sheep var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      if (!var4.isSheared() && !var4.isInvisible()) {
         float var11;
         float var12;
         float var13;
         if (var4.hasCustomName() && "jeb_".equals(var4.getName().getContents())) {
            boolean var22 = true;
            int var15 = var4.tickCount / 25 + var4.getId();
            int var16 = DyeColor.values().length;
            int var17 = var15 % var16;
            int var18 = (var15 + 1) % var16;
            float var19 = ((float)(var4.tickCount % 25) + var7) / 25.0F;
            float[] var20 = Sheep.getColorArray(DyeColor.byId(var17));
            float[] var21 = Sheep.getColorArray(DyeColor.byId(var18));
            var11 = var20[0] * (1.0F - var19) + var21[0] * var19;
            var12 = var20[1] * (1.0F - var19) + var21[1] * var19;
            var13 = var20[2] * (1.0F - var19) + var21[2] * var19;
         } else {
            float[] var14 = Sheep.getColorArray(var4.getColor());
            var11 = var14[0];
            var12 = var14[1];
            var13 = var14[2];
         }

         coloredCutoutModelCopyLayerRender(this.getParentModel(), this.model, SHEEP_FUR_LOCATION, var1, var2, var3, var4, var5, var6, var8, var9, var10, var7, var11, var12, var13);
      }
   }
}
