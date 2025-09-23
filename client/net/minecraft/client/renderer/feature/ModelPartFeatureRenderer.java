package net.minecraft.client.renderer.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
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

   public void render(SubmitNodeCollection var1, MultiBufferSource.BufferSource var2, OutlineBufferSource var3, MultiBufferSource.BufferSource var4) {
      Storage var5 = var1.getModelPartSubmits();

      for(Map.Entry var7 : var5.modelPartSubmits.entrySet()) {
         RenderType var8 = (RenderType)var7.getKey();
         List var9 = (List)var7.getValue();
         VertexConsumer var10 = var2.getBuffer(var8);

         for(SubmitNodeStorage.ModelPartSubmit var12 : var9) {
            VertexConsumer var13;
            if (var12.sprite() != null) {
               if (var12.hasFoil()) {
                  var13 = var12.sprite().wrap(ItemRenderer.getFoilBuffer(var2, var8, var12.sheeted(), true));
               } else {
                  var13 = var12.sprite().wrap(var10);
               }
            } else if (var12.hasFoil()) {
               var13 = ItemRenderer.getFoilBuffer(var2, var8, var12.sheeted(), true);
            } else {
               var13 = var10;
            }

            this.poseStack.last().set(var12.pose());
            var12.modelPart().render(this.poseStack, var13, var12.lightCoords(), var12.overlayCoords(), var12.tintedColor());
            if (var12.outlineColor() != 0 && (var8.outline().isPresent() || var8.isOutline())) {
               var3.setColor(var12.outlineColor());
               VertexConsumer var14 = var3.getBuffer(var8);
               var12.modelPart().render(this.poseStack, var12.sprite() == null ? var14 : var12.sprite().wrap(var14), var12.lightCoords(), var12.overlayCoords(), var12.tintedColor());
            }

            if (var12.crumblingOverlay() != null) {
               SheetedDecalTextureGenerator var15 = new SheetedDecalTextureGenerator(var4.getBuffer((RenderType)ModelBakery.DESTROY_TYPES.get(var12.crumblingOverlay().progress())), var12.crumblingOverlay().cameraPose(), 1.0F);
               var12.modelPart().render(this.poseStack, var15, var12.lightCoords(), var12.overlayCoords(), var12.tintedColor());
            }
         }
      }

   }

   public static class Storage {
      final Map<RenderType, List<SubmitNodeStorage.ModelPartSubmit>> modelPartSubmits = new HashMap();
      private final Set<RenderType> modelPartSubmitsUsage = new ObjectOpenHashSet();

      public Storage() {
         super();
      }

      public void add(RenderType var1, SubmitNodeStorage.ModelPartSubmit var2) {
         ((List)this.modelPartSubmits.computeIfAbsent(var1, (var0) -> new ArrayList())).add(var2);
      }

      public void clear() {
         for(Map.Entry var2 : this.modelPartSubmits.entrySet()) {
            if (!((List)var2.getValue()).isEmpty()) {
               this.modelPartSubmitsUsage.add((RenderType)var2.getKey());
               ((List)var2.getValue()).clear();
            }
         }

      }

      public void endFrame() {
         this.modelPartSubmits.keySet().removeIf((var1) -> !this.modelPartSubmitsUsage.contains(var1));
         this.modelPartSubmitsUsage.clear();
      }
   }
}
