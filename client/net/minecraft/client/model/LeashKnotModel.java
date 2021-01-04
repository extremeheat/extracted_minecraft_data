package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

public class LeashKnotModel<T extends Entity> extends EntityModel<T> {
   private final ModelPart knot;

   public LeashKnotModel() {
      this(0, 0, 32, 32);
   }

   public LeashKnotModel(int var1, int var2, int var3, int var4) {
      super();
      this.texWidth = var3;
      this.texHeight = var4;
      this.knot = new ModelPart(this, var1, var2);
      this.knot.addBox(-3.0F, -6.0F, -3.0F, 6, 8, 6, 0.0F);
      this.knot.setPos(0.0F, 0.0F, 0.0F);
   }

   public void render(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.setupAnim(var1, var2, var3, var4, var5, var6, var7);
      this.knot.render(var7);
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      super.setupAnim(var1, var2, var3, var4, var5, var6, var7);
      this.knot.yRot = var5 * 0.017453292F;
      this.knot.xRot = var6 * 0.017453292F;
   }
}
