package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;

public class SkullModel extends Model {
   protected final ModelPart head;

   public SkullModel() {
      this(0, 35, 64, 64);
   }

   public SkullModel(int var1, int var2, int var3, int var4) {
      super();
      this.texWidth = var3;
      this.texHeight = var4;
      this.head = new ModelPart(this, var1, var2);
      this.head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
      this.head.setPos(0.0F, 0.0F, 0.0F);
   }

   public void render(float var1, float var2, float var3, float var4, float var5, float var6) {
      this.head.yRot = var4 * 0.017453292F;
      this.head.xRot = var5 * 0.017453292F;
      this.head.render(var6);
   }
}
