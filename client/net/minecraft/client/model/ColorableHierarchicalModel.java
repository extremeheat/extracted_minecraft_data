package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.entity.Entity;

public abstract class ColorableHierarchicalModel<E extends Entity> extends HierarchicalModel<E> {
   private float r = 1.0F;
   private float g = 1.0F;
   private float b = 1.0F;

   public ColorableHierarchicalModel() {
      super();
   }

   public void setColor(float var1, float var2, float var3) {
      this.r = var1;
      this.g = var2;
      this.b = var3;
   }

   public void renderToBuffer(PoseStack var1, VertexConsumer var2, int var3, int var4, float var5, float var6, float var7, float var8) {
      super.renderToBuffer(var1, var2, var3, var4, this.r * var5, this.g * var6, this.b * var7, var8);
   }
}
