package net.minecraft.client.renderer.feature;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeStorage;

public class CustomFeatureRenderer {
   public CustomFeatureRenderer() {
      super();
   }

   public void render(SubmitNodeStorage var1, MultiBufferSource.BufferSource var2) {
      for(Map.Entry var4 : var1.getCustomGeometrySubmits().entrySet()) {
         VertexConsumer var5 = var2.getBuffer((RenderType)var4.getKey());

         for(SubmitNodeStorage.CustomGeometrySubmit var7 : (List)var4.getValue()) {
            var7.customGeometryRenderer().render(var7.pose(), var5);
         }
      }

   }
}
