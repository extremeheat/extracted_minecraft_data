package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import net.minecraft.client.model.WardenModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.WardenRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

public class WardenEmissiveLayer extends RenderLayer<WardenRenderState, WardenModel> {
   private final ResourceLocation texture;
   private final WardenEmissiveLayer.AlphaFunction alphaFunction;
   private final WardenEmissiveLayer.DrawSelector drawSelector;

   public WardenEmissiveLayer(
      RenderLayerParent<WardenRenderState, WardenModel> var1,
      ResourceLocation var2,
      WardenEmissiveLayer.AlphaFunction var3,
      WardenEmissiveLayer.DrawSelector var4
   ) {
      super(var1);
      this.texture = var2;
      this.alphaFunction = var3;
      this.drawSelector = var4;
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, WardenRenderState var4, float var5, float var6) {
      if (!var4.isInvisible) {
         this.onlyDrawSelectedParts();
         VertexConsumer var7 = var2.getBuffer(RenderType.entityTranslucentEmissive(this.texture));
         float var8 = this.alphaFunction.apply(var4, var4.ageInTicks);
         int var9 = ARGB.color(Mth.floor(var8 * 255.0F), 255, 255, 255);
         this.getParentModel().renderToBuffer(var1, var7, var3, LivingEntityRenderer.getOverlayCoords(var4, 0.0F), var9);
         this.resetDrawForAllParts();
      }
   }

   private void onlyDrawSelectedParts() {
      List var1 = this.drawSelector.getPartsToDraw(this.getParentModel());
      this.getParentModel().allParts().forEach(var0 -> var0.skipDraw = true);
      var1.forEach(var0 -> var0.skipDraw = false);
   }

   private void resetDrawForAllParts() {
      this.getParentModel().allParts().forEach(var0 -> var0.skipDraw = false);
   }

   public interface AlphaFunction {
      float apply(WardenRenderState var1, float var2);
   }

   public interface DrawSelector {
      List<ModelPart> getPartsToDraw(WardenModel var1);
   }
}
