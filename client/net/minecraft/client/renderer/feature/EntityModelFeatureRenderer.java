package net.minecraft.client.renderer.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
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
      ObjectIterator var4 = var1.getModelSubmits().values().iterator();

      while(var4.hasNext()) {
         Map var5 = (Map)var4.next();

         for(Map.Entry var7 : var5.entrySet()) {
            VertexConsumer var8 = var2.getBuffer((RenderType)var7.getKey());

            for(SubmitNodeStorage.ModelSubmit var10 : (List)var7.getValue()) {
               this.renderModel(var10, (RenderType)var7.getKey(), var8, var3);
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
