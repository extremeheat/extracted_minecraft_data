package net.minecraft.client.renderer.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.rendertype.RenderType;

public class CustomFeatureRenderer {
   public CustomFeatureRenderer() {
      super();
   }

   public void render(SubmitNodeCollection var1, MultiBufferSource.BufferSource var2) {
      Storage var3 = var1.getCustomGeometrySubmits();

      for(Map.Entry var5 : var3.customGeometrySubmits.entrySet()) {
         VertexConsumer var6 = var2.getBuffer((RenderType)var5.getKey());

         for(SubmitNodeStorage.CustomGeometrySubmit var8 : (List)var5.getValue()) {
            var8.customGeometryRenderer().render(var8.pose(), var6);
         }
      }

   }

   public static class Storage {
      final Map<RenderType, List<SubmitNodeStorage.CustomGeometrySubmit>> customGeometrySubmits = new HashMap();
      private final Set<RenderType> customGeometrySubmitsUsage = new ObjectOpenHashSet();

      public Storage() {
         super();
      }

      public void add(PoseStack var1, RenderType var2, SubmitNodeCollector.CustomGeometryRenderer var3) {
         List var4 = (List)this.customGeometrySubmits.computeIfAbsent(var2, (var0) -> new ArrayList());
         var4.add(new SubmitNodeStorage.CustomGeometrySubmit(var1.last().copy(), var3));
      }

      public void clear() {
         for(Map.Entry var2 : this.customGeometrySubmits.entrySet()) {
            if (!((List)var2.getValue()).isEmpty()) {
               this.customGeometrySubmitsUsage.add((RenderType)var2.getKey());
               ((List)var2.getValue()).clear();
            }
         }

      }

      public void endFrame() {
         this.customGeometrySubmits.keySet().removeIf((var1) -> !this.customGeometrySubmitsUsage.contains(var1));
         this.customGeometrySubmitsUsage.clear();
      }
   }
}
