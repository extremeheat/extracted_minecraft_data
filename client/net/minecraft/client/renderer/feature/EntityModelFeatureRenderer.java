package net.minecraft.client.renderer.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeStorage;

public class EntityModelFeatureRenderer {
   private final PoseStack poseStack = new PoseStack();

   public EntityModelFeatureRenderer() {
      super();
   }

   public void render(SubmitNodeStorage var1, MultiBufferSource.BufferSource var2, OutlineBufferSource var3) {
      this.renderBatch(var2, var3, var1.getOpaqueModelSubmits());
      var1.getTranslucentModelSubmits().sort(Comparator.comparingDouble((var0) -> (double)(-var0.position().lengthSquared())).thenComparingInt(SubmitNodeStorage.TranslucentModelSubmit::order));
      this.renderTranslucents(var2, var3, var1.getTranslucentModelSubmits());
   }

   private void renderTranslucents(MultiBufferSource.BufferSource var1, OutlineBufferSource var2, List<SubmitNodeStorage.TranslucentModelSubmit<?>> var3) {
      for(SubmitNodeStorage.TranslucentModelSubmit var5 : var3) {
         this.renderModel(var5.modelSubmit(), var5.renderType(), var1.getBuffer(var5.renderType()), var2);
      }

   }

   private void renderBatch(MultiBufferSource.BufferSource var1, OutlineBufferSource var2, Int2ObjectAVLTreeMap<Map<RenderType, List<SubmitNodeStorage.ModelSubmit<?>>>> var3) {
      ObjectIterator var4 = var3.values().iterator();

      while(var4.hasNext()) {
         Map var5 = (Map)var4.next();

         for(Map.Entry var8 : var5.entrySet()) {
            VertexConsumer var9 = var1.getBuffer((RenderType)var8.getKey());

            for(SubmitNodeStorage.ModelSubmit var11 : (List)var8.getValue()) {
               this.renderModel(var11, (RenderType)var8.getKey(), var9, var2);
            }
         }
      }

   }

   private <S> void renderModel(SubmitNodeStorage.ModelSubmit<S> var1, RenderType var2, VertexConsumer var3, OutlineBufferSource var4) {
      this.poseStack.pushPose();
      this.poseStack.last().set(var1.pose());
      Model var5 = var1.model();
      VertexConsumer var6 = var1.sprite() == null ? var3 : var1.sprite().wrap(var3);
      var5.setupAnim(var1.state());
      var5.renderToBuffer(this.poseStack, var6, var1.lightCoords(), var1.overlayCoords(), var1.tintedColor());
      if (var1.outlineColor() != 0 && var2.outline().isPresent()) {
         var4.setColor(var1.outlineColor());
         VertexConsumer var7 = var4.getBuffer(var2);
         var5.renderToBuffer(this.poseStack, var1.sprite() == null ? var7 : var1.sprite().wrap(var7), var1.lightCoords(), var1.overlayCoords(), var1.tintedColor());
      }

      this.poseStack.popPose();
   }
}
