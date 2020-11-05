package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.IronGolem;

public class IronGolemModel<T extends IronGolem> extends ListModel<T> {
   private final ModelPart head;
   private final ModelPart body;
   private final ModelPart arm0;
   private final ModelPart arm1;
   private final ModelPart leg0;
   private final ModelPart leg1;

   public IronGolemModel() {
      super();
      boolean var1 = true;
      boolean var2 = true;
      this.head = (new ModelPart(this)).setTexSize(128, 128);
      this.head.setPos(0.0F, -7.0F, -2.0F);
      this.head.texOffs(0, 0).addBox(-4.0F, -12.0F, -5.5F, 8.0F, 10.0F, 8.0F, 0.0F);
      this.head.texOffs(24, 0).addBox(-1.0F, -5.0F, -7.5F, 2.0F, 4.0F, 2.0F, 0.0F);
      this.body = (new ModelPart(this)).setTexSize(128, 128);
      this.body.setPos(0.0F, -7.0F, 0.0F);
      this.body.texOffs(0, 40).addBox(-9.0F, -2.0F, -6.0F, 18.0F, 12.0F, 11.0F, 0.0F);
      this.body.texOffs(0, 70).addBox(-4.5F, 10.0F, -3.0F, 9.0F, 5.0F, 6.0F, 0.5F);
      this.arm0 = (new ModelPart(this)).setTexSize(128, 128);
      this.arm0.setPos(0.0F, -7.0F, 0.0F);
      this.arm0.texOffs(60, 21).addBox(-13.0F, -2.5F, -3.0F, 4.0F, 30.0F, 6.0F, 0.0F);
      this.arm1 = (new ModelPart(this)).setTexSize(128, 128);
      this.arm1.setPos(0.0F, -7.0F, 0.0F);
      this.arm1.texOffs(60, 58).addBox(9.0F, -2.5F, -3.0F, 4.0F, 30.0F, 6.0F, 0.0F);
      this.leg0 = (new ModelPart(this, 0, 22)).setTexSize(128, 128);
      this.leg0.setPos(-4.0F, 11.0F, 0.0F);
      this.leg0.texOffs(37, 0).addBox(-3.5F, -3.0F, -3.0F, 6.0F, 16.0F, 5.0F, 0.0F);
      this.leg1 = (new ModelPart(this, 0, 22)).setTexSize(128, 128);
      this.leg1.mirror = true;
      this.leg1.texOffs(60, 0).setPos(5.0F, 11.0F, 0.0F);
      this.leg1.addBox(-3.5F, -3.0F, -3.0F, 6.0F, 16.0F, 5.0F, 0.0F);
   }

   public Iterable<ModelPart> parts() {
      return ImmutableList.of(this.head, this.body, this.leg0, this.leg1, this.arm0, this.arm1);
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6) {
      this.head.yRot = var5 * 0.017453292F;
      this.head.xRot = var6 * 0.017453292F;
      this.leg0.xRot = -1.5F * Mth.triangleWave(var2, 13.0F) * var3;
      this.leg1.xRot = 1.5F * Mth.triangleWave(var2, 13.0F) * var3;
      this.leg0.yRot = 0.0F;
      this.leg1.yRot = 0.0F;
   }

   public void prepareMobModel(T var1, float var2, float var3, float var4) {
      int var5 = var1.getAttackAnimationTick();
      if (var5 > 0) {
         this.arm0.xRot = -2.0F + 1.5F * Mth.triangleWave((float)var5 - var4, 10.0F);
         this.arm1.xRot = -2.0F + 1.5F * Mth.triangleWave((float)var5 - var4, 10.0F);
      } else {
         int var6 = var1.getOfferFlowerTick();
         if (var6 > 0) {
            this.arm0.xRot = -0.8F + 0.025F * Mth.triangleWave((float)var6, 70.0F);
            this.arm1.xRot = 0.0F;
         } else {
            this.arm0.xRot = (-0.2F + 1.5F * Mth.triangleWave(var2, 13.0F)) * var3;
            this.arm1.xRot = (-0.2F - 1.5F * Mth.triangleWave(var2, 13.0F)) * var3;
         }
      }

   }

   public ModelPart getFlowerHoldingArm() {
      return this.arm0;
   }
}
