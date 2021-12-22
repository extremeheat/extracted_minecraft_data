package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.entity.Entity;

public abstract class ColorableAgeableListModel<E extends Entity> extends AgeableListModel<E> {
   // $FF: renamed from: r float
   private float field_47 = 1.0F;
   // $FF: renamed from: g float
   private float field_48 = 1.0F;
   // $FF: renamed from: b float
   private float field_49 = 1.0F;

   public ColorableAgeableListModel() {
      super();
   }

   public void setColor(float var1, float var2, float var3) {
      this.field_47 = var1;
      this.field_48 = var2;
      this.field_49 = var3;
   }

   public void renderToBuffer(PoseStack var1, VertexConsumer var2, int var3, int var4, float var5, float var6, float var7, float var8) {
      super.renderToBuffer(var1, var2, var3, var4, this.field_47 * var5, this.field_48 * var6, this.field_49 * var7, var8);
   }
}
