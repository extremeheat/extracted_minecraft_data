package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class SnowGolemModel<T extends Entity> extends EntityModel<T> {
   private final ModelPart piece1;
   private final ModelPart piece2;
   private final ModelPart head;
   private final ModelPart arm1;
   private final ModelPart arm2;

   public SnowGolemModel() {
      super();
      float var1 = 4.0F;
      float var2 = 0.0F;
      this.head = (new ModelPart(this, 0, 0)).setTexSize(64, 64);
      this.head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, -0.5F);
      this.head.setPos(0.0F, 4.0F, 0.0F);
      this.arm1 = (new ModelPart(this, 32, 0)).setTexSize(64, 64);
      this.arm1.addBox(-1.0F, 0.0F, -1.0F, 12, 2, 2, -0.5F);
      this.arm1.setPos(0.0F, 6.0F, 0.0F);
      this.arm2 = (new ModelPart(this, 32, 0)).setTexSize(64, 64);
      this.arm2.addBox(-1.0F, 0.0F, -1.0F, 12, 2, 2, -0.5F);
      this.arm2.setPos(0.0F, 6.0F, 0.0F);
      this.piece1 = (new ModelPart(this, 0, 16)).setTexSize(64, 64);
      this.piece1.addBox(-5.0F, -10.0F, -5.0F, 10, 10, 10, -0.5F);
      this.piece1.setPos(0.0F, 13.0F, 0.0F);
      this.piece2 = (new ModelPart(this, 0, 36)).setTexSize(64, 64);
      this.piece2.addBox(-6.0F, -12.0F, -6.0F, 12, 12, 12, -0.5F);
      this.piece2.setPos(0.0F, 24.0F, 0.0F);
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      super.setupAnim(var1, var2, var3, var4, var5, var6, var7);
      this.head.yRot = var5 * 0.017453292F;
      this.head.xRot = var6 * 0.017453292F;
      this.piece1.yRot = var5 * 0.017453292F * 0.25F;
      float var8 = Mth.sin(this.piece1.yRot);
      float var9 = Mth.cos(this.piece1.yRot);
      this.arm1.zRot = 1.0F;
      this.arm2.zRot = -1.0F;
      this.arm1.yRot = 0.0F + this.piece1.yRot;
      this.arm2.yRot = 3.1415927F + this.piece1.yRot;
      this.arm1.x = var9 * 5.0F;
      this.arm1.z = -var8 * 5.0F;
      this.arm2.x = -var9 * 5.0F;
      this.arm2.z = var8 * 5.0F;
   }

   public void render(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.setupAnim(var1, var2, var3, var4, var5, var6, var7);
      this.piece1.render(var7);
      this.piece2.render(var7);
      this.head.render(var7);
      this.arm1.render(var7);
      this.arm2.render(var7);
   }

   public ModelPart getHead() {
      return this.head;
   }
}
