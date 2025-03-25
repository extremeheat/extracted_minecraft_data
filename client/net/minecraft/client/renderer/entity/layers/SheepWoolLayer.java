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

public class SheepWoolLayer extends RenderLayer<SheepRenderState, SheepModel> {
   private static final ResourceLocation SHEEP_WOOL_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/sheep/sheep_wool.png");
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
               VertexConsumer var8 = var2.getBuffer(RenderType.outline(SHEEP_WOOL_LOCATION));
               var7.renderToBuffer(var1, var8, var3, LivingEntityRenderer.getOverlayCoords(var4, 0.0F), -16777216);
            }

         } else {
            coloredCutoutModelCopyLayerRender(var7, SHEEP_WOOL_LOCATION, var1, var2, var3, var4, var4.getWoolColor());
         }
      }
   }
}
