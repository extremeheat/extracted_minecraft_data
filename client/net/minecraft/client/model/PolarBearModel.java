package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.animal.PolarBear;

public class PolarBearModel<T extends PolarBear> extends QuadrupedModel<T> {
   public PolarBearModel() {
      super(12, 0.0F, true, 16.0F, 4.0F, 2.25F, 2.0F, 24);
      this.texWidth = 128;
      this.texHeight = 64;
      this.head = new ModelPart(this, 0, 0);
      this.head.addBox(-3.5F, -3.0F, -3.0F, 7.0F, 7.0F, 7.0F, 0.0F);
      this.head.setPos(0.0F, 10.0F, -16.0F);
      this.head.texOffs(0, 44).addBox(-2.5F, 1.0F, -6.0F, 5.0F, 3.0F, 3.0F, 0.0F);
      this.head.texOffs(26, 0).addBox(-4.5F, -4.0F, -1.0F, 2.0F, 2.0F, 1.0F, 0.0F);
      ModelPart var1 = this.head.texOffs(26, 0);
      var1.mirror = true;
      var1.addBox(2.5F, -4.0F, -1.0F, 2.0F, 2.0F, 1.0F, 0.0F);
      this.body = new ModelPart(this);
      this.body.texOffs(0, 19).addBox(-5.0F, -13.0F, -7.0F, 14.0F, 14.0F, 11.0F, 0.0F);
      this.body.texOffs(39, 0).addBox(-4.0F, -25.0F, -7.0F, 12.0F, 12.0F, 10.0F, 0.0F);
      this.body.setPos(-2.0F, 9.0F, 12.0F);
      boolean var2 = true;
      this.leg0 = new ModelPart(this, 50, 22);
      this.leg0.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 8.0F, 0.0F);
      this.leg0.setPos(-3.5F, 14.0F, 6.0F);
      this.leg1 = new ModelPart(this, 50, 22);
      this.leg1.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 8.0F, 0.0F);
      this.leg1.setPos(3.5F, 14.0F, 6.0F);
      this.leg2 = new ModelPart(this, 50, 40);
      this.leg2.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 6.0F, 0.0F);
      this.leg2.setPos(-2.5F, 14.0F, -7.0F);
      this.leg3 = new ModelPart(this, 50, 40);
      this.leg3.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 6.0F, 0.0F);
      this.leg3.setPos(2.5F, 14.0F, -7.0F);
      --this.leg0.x;
      ++this.leg1.x;
      ModelPart var10000 = this.leg0;
      var10000.z += 0.0F;
      var10000 = this.leg1;
      var10000.z += 0.0F;
      --this.leg2.x;
      ++this.leg3.x;
      --this.leg2.z;
      --this.leg3.z;
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6) {
      super.setupAnim(var1, var2, var3, var4, var5, var6);
      float var7 = var4 - (float)var1.tickCount;
      float var8 = var1.getStandingAnimationScale(var7);
      var8 *= var8;
      float var9 = 1.0F - var8;
      this.body.xRot = 1.5707964F - var8 * 3.1415927F * 0.35F;
      this.body.y = 9.0F * var9 + 11.0F * var8;
      this.leg2.y = 14.0F * var9 - 6.0F * var8;
      this.leg2.z = -8.0F * var9 - 4.0F * var8;
      ModelPart var10000 = this.leg2;
      var10000.xRot -= var8 * 3.1415927F * 0.45F;
      this.leg3.y = this.leg2.y;
      this.leg3.z = this.leg2.z;
      var10000 = this.leg3;
      var10000.xRot -= var8 * 3.1415927F * 0.45F;
      if (this.young) {
         this.head.y = 10.0F * var9 - 9.0F * var8;
         this.head.z = -16.0F * var9 - 7.0F * var8;
      } else {
         this.head.y = 10.0F * var9 - 14.0F * var8;
         this.head.z = -16.0F * var9 - 3.0F * var8;
      }

      var10000 = this.head;
      var10000.xRot += var8 * 3.1415927F * 0.15F;
   }
}
