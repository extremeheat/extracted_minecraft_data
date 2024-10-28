package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.SheepFurModel;
import net.minecraft.client.model.SheepModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.SheepRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;

public class SheepWoolLayer extends RenderLayer<SheepRenderState, SheepModel> {
   private static final ResourceLocation SHEEP_FUR_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/sheep/sheep_fur.png");
   private final EntityModel<SheepRenderState> adultModel;
   private final EntityModel<SheepRenderState> babyModel;

   public SheepWoolLayer(RenderLayerParent<SheepRenderState, SheepModel> var1, EntityModelSet var2) {
      super(var1);
      this.adultModel = new SheepFurModel(var2.bakeLayer(ModelLayers.SHEEP_WOOL));
      this.babyModel = new SheepFurModel(var2.bakeLayer(ModelLayers.SHEEP_BABY_WOOL));
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, SheepRenderState var4, float var5, float var6) {
      if (!var4.isSheared) {
         EntityModel var7 = var4.isBaby ? this.babyModel : this.adultModel;
         if (var4.isInvisible) {
            if (var4.appearsGlowing) {
               var7.setupAnim(var4);
               VertexConsumer var18 = var2.getBuffer(RenderType.outline(SHEEP_FUR_LOCATION));
               var7.renderToBuffer(var1, var18, var3, LivingEntityRenderer.getOverlayCoords(var4, 0.0F), -16777216);
            }

         } else {
            int var8;
            if (var4.customName != null && "jeb_".equals(var4.customName.getString())) {
               boolean var9 = true;
               int var10 = Mth.floor(var4.ageInTicks);
               int var11 = var10 / 25 + var4.id;
               int var12 = DyeColor.values().length;
               int var13 = var11 % var12;
               int var14 = (var11 + 1) % var12;
               float var15 = ((float)(var10 % 25) + Mth.frac(var4.ageInTicks)) / 25.0F;
               int var16 = Sheep.getColor(DyeColor.byId(var13));
               int var17 = Sheep.getColor(DyeColor.byId(var14));
               var8 = ARGB.lerp(var15, var16, var17);
            } else {
               var8 = Sheep.getColor(var4.woolColor);
            }

            coloredCutoutModelCopyLayerRender(var7, SHEEP_FUR_LOCATION, var1, var2, var3, var4, var8);
         }
      }
   }
}
