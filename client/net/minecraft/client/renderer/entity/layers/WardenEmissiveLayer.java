package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.WardenModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.warden.Warden;

public class WardenEmissiveLayer<T extends Warden, M extends WardenModel<T>> extends RenderLayer<T, M> {
   private final ResourceLocation texture;
   private final WardenEmissiveLayer.AlphaFunction<T> alphaFunction;
   private final WardenEmissiveLayer.DrawSelector<T, M> drawSelector;

   public WardenEmissiveLayer(
      RenderLayerParent<T, M> var1, ResourceLocation var2, WardenEmissiveLayer.AlphaFunction<T> var3, WardenEmissiveLayer.DrawSelector<T, M> var4
   ) {
      super(var1);
      this.texture = var2;
      this.alphaFunction = var3;
      this.drawSelector = var4;
   }

   public void render(PoseStack var1, MultiBufferSource var2, int var3, T var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      if (!var4.isInvisible()) {
         this.onlyDrawSelectedParts();
         VertexConsumer var11 = var2.getBuffer(RenderType.entityTranslucentEmissive(this.texture));
         float var12 = this.alphaFunction.apply((T)var4, var7, var8);
         int var13 = FastColor.ARGB32.color(Mth.floor(var12 * 255.0F), 255, 255, 255);
         this.getParentModel().renderToBuffer(var1, var11, var3, LivingEntityRenderer.getOverlayCoords(var4, 0.0F), var13);
         this.resetDrawForAllParts();
      }
   }

   private void onlyDrawSelectedParts() {
      List var1 = this.drawSelector.getPartsToDraw(this.getParentModel());
      this.getParentModel().root().getAllParts().forEach(var0 -> var0.skipDraw = true);
      var1.forEach(var0 -> var0.skipDraw = false);
   }

   private void resetDrawForAllParts() {
      this.getParentModel().root().getAllParts().forEach(var0 -> var0.skipDraw = false);
   }

   public interface AlphaFunction<T extends Warden> {
      float apply(T var1, float var2, float var3);
   }

   public interface DrawSelector<T extends Warden, M extends EntityModel<T>> {
      List<ModelPart> getPartsToDraw(M var1);
   }
}
