package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import java.util.function.Function;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

public class LivingEntityEmissiveLayer<S extends LivingEntityRenderState, M extends EntityModel<S>> extends RenderLayer<S, M> {
   private final ResourceLocation texture;
   private final LivingEntityEmissiveLayer.AlphaFunction<S> alphaFunction;
   private final LivingEntityEmissiveLayer.DrawSelector<S, M> drawSelector;
   private final Function<ResourceLocation, RenderType> bufferProvider;

   public LivingEntityEmissiveLayer(
      RenderLayerParent<S, M> var1,
      ResourceLocation var2,
      LivingEntityEmissiveLayer.AlphaFunction<S> var3,
      LivingEntityEmissiveLayer.DrawSelector<S, M> var4,
      Function<ResourceLocation, RenderType> var5
   ) {
      super(var1);
      this.texture = var2;
      this.alphaFunction = var3;
      this.drawSelector = var4;
      this.bufferProvider = var5;
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, S var4, float var5, float var6) {
      if (!var4.isInvisible) {
         if (this.onlyDrawSelectedParts(var4)) {
            VertexConsumer var7 = var2.getBuffer(this.bufferProvider.apply(this.texture));
            float var8 = this.alphaFunction.apply((S)var4, var4.ageInTicks);
            int var9 = ARGB.color(Mth.floor(var8 * 255.0F), 255, 255, 255);
            this.getParentModel().renderToBuffer(var1, var7, var3, LivingEntityRenderer.getOverlayCoords(var4, 0.0F), var9);
            this.resetDrawForAllParts();
         }
      }
   }

   private boolean onlyDrawSelectedParts(S var1) {
      List var2 = this.drawSelector.getPartsToDraw(this.getParentModel(), (S)var1);
      if (var2.isEmpty()) {
         return false;
      } else {
         this.getParentModel().allParts().forEach(var0 -> var0.skipDraw = true);
         var2.forEach(var0 -> var0.skipDraw = false);
         return true;
      }
   }

   private void resetDrawForAllParts() {
      this.getParentModel().allParts().forEach(var0 -> var0.skipDraw = false);
   }

   public interface AlphaFunction<S extends LivingEntityRenderState> {
      float apply(S var1, float var2);
   }

   public interface DrawSelector<S extends LivingEntityRenderState, M extends EntityModel<S>> {
      List<ModelPart> getPartsToDraw(M var1, S var2);
   }
}
