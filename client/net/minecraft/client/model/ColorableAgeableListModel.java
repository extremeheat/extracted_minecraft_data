package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.Entity;

public abstract class ColorableAgeableListModel<E extends Entity> extends AgeableListModel<E> {
   private int color = -1;

   public ColorableAgeableListModel() {
      super();
   }

   public void setColor(int var1) {
      this.color = var1;
   }

   @Override
   public void renderToBuffer(PoseStack var1, VertexConsumer var2, int var3, int var4, int var5) {
      super.renderToBuffer(var1, var2, var3, var4, FastColor.ARGB32.multiply(var5, this.color));
   }
}
