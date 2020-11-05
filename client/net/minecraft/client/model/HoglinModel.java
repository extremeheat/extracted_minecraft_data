package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.hoglin.HoglinBase;

public class HoglinModel<T extends Mob & HoglinBase> extends AgeableListModel<T> {
   private final ModelPart head;
   private final ModelPart rightEar;
   private final ModelPart leftEar;
   private final ModelPart body;
   private final ModelPart frontRightLeg;
   private final ModelPart frontLeftLeg;
   private final ModelPart backRightLeg;
   private final ModelPart backLeftLeg;
   private final ModelPart mane;

   public HoglinModel() {
      super(true, 8.0F, 6.0F, 1.9F, 2.0F, 24.0F);
      this.texWidth = 128;
      this.texHeight = 64;
      this.body = new ModelPart(this);
      this.body.setPos(0.0F, 7.0F, 0.0F);
      this.body.texOffs(1, 1).addBox(-8.0F, -7.0F, -13.0F, 16.0F, 14.0F, 26.0F);
      this.mane = new ModelPart(this);
      this.mane.setPos(0.0F, -14.0F, -5.0F);
      this.mane.texOffs(90, 33).addBox(0.0F, 0.0F, -9.0F, 0.0F, 10.0F, 19.0F, 0.001F);
      this.body.addChild(this.mane);
      this.head = new ModelPart(this);
      this.head.setPos(0.0F, 2.0F, -12.0F);
      this.head.texOffs(61, 1).addBox(-7.0F, -3.0F, -19.0F, 14.0F, 6.0F, 19.0F);
      this.rightEar = new ModelPart(this);
      this.rightEar.setPos(-6.0F, -2.0F, -3.0F);
      this.rightEar.texOffs(1, 1).addBox(-6.0F, -1.0F, -2.0F, 6.0F, 1.0F, 4.0F);
      this.rightEar.zRot = -0.6981317F;
      this.head.addChild(this.rightEar);
      this.leftEar = new ModelPart(this);
      this.leftEar.setPos(6.0F, -2.0F, -3.0F);
      this.leftEar.texOffs(1, 6).addBox(0.0F, -1.0F, -2.0F, 6.0F, 1.0F, 4.0F);
      this.leftEar.zRot = 0.6981317F;
      this.head.addChild(this.leftEar);
      ModelPart var1 = new ModelPart(this);
      var1.setPos(-7.0F, 2.0F, -12.0F);
      var1.texOffs(10, 13).addBox(-1.0F, -11.0F, -1.0F, 2.0F, 11.0F, 2.0F);
      this.head.addChild(var1);
      ModelPart var2 = new ModelPart(this);
      var2.setPos(7.0F, 2.0F, -12.0F);
      var2.texOffs(1, 13).addBox(-1.0F, -11.0F, -1.0F, 2.0F, 11.0F, 2.0F);
      this.head.addChild(var2);
      this.head.xRot = 0.87266463F;
      boolean var3 = true;
      boolean var4 = true;
      this.frontRightLeg = new ModelPart(this);
      this.frontRightLeg.setPos(-4.0F, 10.0F, -8.5F);
      this.frontRightLeg.texOffs(66, 42).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 14.0F, 6.0F);
      this.frontLeftLeg = new ModelPart(this);
      this.frontLeftLeg.setPos(4.0F, 10.0F, -8.5F);
      this.frontLeftLeg.texOffs(41, 42).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 14.0F, 6.0F);
      this.backRightLeg = new ModelPart(this);
      this.backRightLeg.setPos(-5.0F, 13.0F, 10.0F);
      this.backRightLeg.texOffs(21, 45).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 11.0F, 5.0F);
      this.backLeftLeg = new ModelPart(this);
      this.backLeftLeg.setPos(5.0F, 13.0F, 10.0F);
      this.backLeftLeg.texOffs(0, 45).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 11.0F, 5.0F);
   }

   protected Iterable<ModelPart> headParts() {
      return ImmutableList.of(this.head);
   }

   protected Iterable<ModelPart> bodyParts() {
      return ImmutableList.of(this.body, this.frontRightLeg, this.frontLeftLeg, this.backRightLeg, this.backLeftLeg);
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6) {
      this.rightEar.zRot = -0.6981317F - var3 * Mth.sin(var2);
      this.leftEar.zRot = 0.6981317F + var3 * Mth.sin(var2);
      this.head.yRot = var5 * 0.017453292F;
      int var7 = ((HoglinBase)var1).getAttackAnimationRemainingTicks();
      float var8 = 1.0F - (float)Mth.abs(10 - 2 * var7) / 10.0F;
      this.head.xRot = Mth.lerp(var8, 0.87266463F, -0.34906584F);
      if (var1.isBaby()) {
         this.head.y = Mth.lerp(var8, 2.0F, 5.0F);
         this.mane.z = -3.0F;
      } else {
         this.head.y = 2.0F;
         this.mane.z = -7.0F;
      }

      float var9 = 1.2F;
      this.frontRightLeg.xRot = Mth.cos(var2) * 1.2F * var3;
      this.frontLeftLeg.xRot = Mth.cos(var2 + 3.1415927F) * 1.2F * var3;
      this.backRightLeg.xRot = this.frontLeftLeg.xRot;
      this.backLeftLeg.xRot = this.frontRightLeg.xRot;
   }
}
