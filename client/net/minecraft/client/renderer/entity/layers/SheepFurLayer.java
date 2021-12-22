package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.SheepFurModel;
import net.minecraft.client.model.SheepModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;

public class SheepFurLayer extends RenderLayer<Sheep, SheepModel<Sheep>> {
   private static final ResourceLocation SHEEP_FUR_LOCATION = new ResourceLocation("textures/entity/sheep/sheep_fur.png");
   private final SheepFurModel<Sheep> model;

   public SheepFurLayer(RenderLayerParent<Sheep, SheepModel<Sheep>> var1, EntityModelSet var2) {
      super(var1);
      this.model = new SheepFurModel(var2.bakeLayer(ModelLayers.SHEEP_FUR));
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, Sheep var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      if (!var4.isSheared()) {
         if (var4.isInvisible()) {
            Minecraft var22 = Minecraft.getInstance();
            boolean var23 = var22.shouldEntityAppearGlowing(var4);
            if (var23) {
               ((SheepModel)this.getParentModel()).copyPropertiesTo(this.model);
               this.model.prepareMobModel(var4, var5, var6, var7);
               this.model.setupAnim(var4, var5, var6, var8, var9, var10);
               VertexConsumer var24 = var2.getBuffer(RenderType.outline(SHEEP_FUR_LOCATION));
               this.model.renderToBuffer(var1, var24, var3, LivingEntityRenderer.getOverlayCoords(var4, 0.0F), 0.0F, 0.0F, 0.0F, 1.0F);
            }

         } else {
            float var11;
            float var12;
            float var13;
            if (var4.hasCustomName() && "jeb_".equals(var4.getName().getContents())) {
               boolean var25 = true;
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
}
