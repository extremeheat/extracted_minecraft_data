package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

public abstract class ListModel extends EntityModel {
   public void renderToBuffer(PoseStack var1, VertexConsumer var2, int var3, int var4, float var5, float var6, float var7, float var8) {
      this.parts().forEach((var8x) -> {
         var8x.render(var1, var2, var3, var4, var5, var6, var7, var8);
      });
   }

   public abstract Iterable parts();
}
