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
      Storage var4 = var1.getModelPartSubmits();

      for(Map.Entry var6 : var4.modelPartSubmits.entrySet()) {
         RenderType var7 = (RenderType)var6.getKey();
         List var8 = (List)var6.getValue();
         VertexConsumer var9 = var2.getBuffer(var7);

         for(SubmitNodeStorage.ModelPartSubmit var11 : var8) {
            VertexConsumer var12;
            if (var11.sprite() != null) {
               if (var11.hasFoil()) {
                  var12 = var11.sprite().wrap(ItemRenderer.getFoilBuffer(var2, var7, var11.sheeted(), true));
               } else {
                  var12 = var11.sprite().wrap(var9);
               }
            } else if (var11.hasFoil()) {
               var12 = ItemRenderer.getFoilBuffer(var2, var7, var11.sheeted(), true);
            } else {
               var12 = var9;
            }

            this.poseStack.last().set(var11.pose());
            var11.modelPart().render(this.poseStack, var12, var11.lightCoords(), var11.overlayCoords(), var11.tintedColor());
            if (var11.crumblingOverlay() != null) {
               SheetedDecalTextureGenerator var13 = new SheetedDecalTextureGenerator(var3.getBuffer((RenderType)ModelBakery.DESTROY_TYPES.get(var11.crumblingOverlay().progress())), var11.crumblingOverlay().cameraPose(), 1.0F);
               var11.modelPart().render(this.poseStack, var13, var11.lightCoords(), var11.overlayCoords(), var11.tintedColor());
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
