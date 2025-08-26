package net.minecraft.client.renderer.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.ModelBakery;

public class ModelPartFeatureRenderer {
   private final PoseStack poseStack = new PoseStack();

   public ModelPartFeatureRenderer() {
      super();
   }

   public void render(SubmitNodeCollection var1, MultiBufferSource.BufferSource var2, MultiBufferSource.BufferSource var3) {
      for(Map.Entry var5 : var1.getModelPartSubmits().entrySet()) {
         RenderType var6 = (RenderType)var5.getKey();
         List var7 = (List)var5.getValue();
         VertexConsumer var8 = var2.getBuffer(var6);

         for(SubmitNodeStorage.ModelPartSubmit var10 : var7) {
            VertexConsumer var11;
            if (var10.sprite() != null) {
               if (var10.hasFoil()) {
                  var11 = var10.sprite().wrap(ItemRenderer.getFoilBuffer(var2, var6, var10.sheeted(), true));
               } else {
                  var11 = var10.sprite().wrap(var8);
               }
            } else if (var10.hasFoil()) {
               var11 = ItemRenderer.getFoilBuffer(var2, var6, var10.sheeted(), true);
            } else {
               var11 = var8;
            }

            this.poseStack.last().set(var10.pose());
            var10.modelPart().render(this.poseStack, var11, var10.lightCoords(), var10.overlayCoords(), var10.tintedColor());
            if (var10.crumblingOverlay() != null) {
               SheetedDecalTextureGenerator var12 = new SheetedDecalTextureGenerator(var3.getBuffer((RenderType)ModelBakery.DESTROY_TYPES.get(var10.crumblingOverlay().progress())), var10.crumblingOverlay().cameraPose(), 1.0F);
               var10.modelPart().render(this.poseStack, var12, var10.lightCoords(), var10.overlayCoords(), var10.tintedColor());
            }
         }
      }

   }
}
