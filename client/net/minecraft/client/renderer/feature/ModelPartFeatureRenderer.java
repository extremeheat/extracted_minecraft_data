package net.minecraft.client.renderer.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.entity.ItemRenderer;

public class ModelPartFeatureRenderer {
   private final PoseStack poseStack = new PoseStack();

   public ModelPartFeatureRenderer() {
      super();
   }

   public void render(SubmitNodeCollection var1, MultiBufferSource.BufferSource var2) {
      for(Map.Entry var4 : var1.getModelPartSubmits().entrySet()) {
         RenderType var5 = (RenderType)var4.getKey();
         List var6 = (List)var4.getValue();
         VertexConsumer var7 = var2.getBuffer(var5);

         for(SubmitNodeStorage.ModelPartSubmit var9 : var6) {
            VertexConsumer var10;
            if (var9.sprite() != null) {
               if (var9.hasFoil()) {
                  var10 = var9.sprite().wrap(ItemRenderer.getFoilBuffer(var2, var5, var9.sheeted(), var9.hasFoil()));
               } else {
                  var10 = var9.sprite().wrap(var7);
               }
            } else {
               var10 = var7;
            }

            this.poseStack.last().set(var9.pose());
            var9.modelPart().render(this.poseStack, var10, var9.lightCoords(), var9.overlayCoords(), var9.tintedColor());
         }
      }

   }
}
