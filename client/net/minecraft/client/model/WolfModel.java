package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Wolf;

public class WolfModel<T extends Wolf> extends ColorableAgeableListModel<T> {
   private final ModelPart head;
   private final ModelPart realHead;
   private final ModelPart body;
   private final ModelPart leg0;
   private final ModelPart leg1;
   private final ModelPart leg2;
   private final ModelPart leg3;
   private final ModelPart tail;
   private final ModelPart realTail;
   private final ModelPart upperBody;

   public WolfModel() {
      super();
      float var1 = 0.0F;
      float var2 = 13.5F;
      this.head = new ModelPart(this, 0, 0);
      this.head.setPos(-1.0F, 13.5F, -7.0F);
      this.realHead = new ModelPart(this, 0, 0);
      this.realHead.addBox(-2.0F, -3.0F, -2.0F, 6.0F, 6.0F, 4.0F, 0.0F);
      this.head.addChild(this.realHead);
      this.body = new ModelPart(this, 18, 14);
      this.body.addBox(-3.0F, -2.0F, -3.0F, 6.0F, 9.0F, 6.0F, 0.0F);
      this.body.setPos(0.0F, 14.0F, 2.0F);
      this.upperBody = new ModelPart(this, 21, 0);
      this.upperBody.addBox(-3.0F, -3.0F, -3.0F, 8.0F, 6.0F, 7.0F, 0.0F);
      this.upperBody.setPos(-1.0F, 14.0F, 2.0F);
      this.leg0 = new ModelPart(this, 0, 18);
      this.leg0.addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.0F);
      this.leg0.setPos(-2.5F, 16.0F, 7.0F);
      this.leg1 = new ModelPart(this, 0, 18);
      this.leg1.addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.0F);
      this.leg1.setPos(0.5F, 16.0F, 7.0F);
      this.leg2 = new ModelPart(this, 0, 18);
      this.leg2.addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.0F);
      this.leg2.setPos(-2.5F, 16.0F, -4.0F);
      this.leg3 = new ModelPart(this, 0, 18);
      this.leg3.addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.0F);
      this.leg3.setPos(0.5F, 16.0F, -4.0F);
      this.tail = new ModelPart(this, 9, 18);
      this.tail.setPos(-1.0F, 12.0F, 8.0F);
      this.realTail = new ModelPart(this, 9, 18);
      this.realTail.addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.0F);
      this.tail.addChild(this.realTail);
      this.realHead.texOffs(16, 14).addBox(-2.0F, -5.0F, 0.0F, 2.0F, 2.0F, 1.0F, 0.0F);
      this.realHead.texOffs(16, 14).addBox(2.0F, -5.0F, 0.0F, 2.0F, 2.0F, 1.0F, 0.0F);
      this.realHead.texOffs(0, 10).addBox(-0.5F, 0.0F, -5.0F, 3.0F, 3.0F, 4.0F, 0.0F);
   }

   protected Iterable<ModelPart> headParts() {
      return ImmutableList.of(this.head);
   }

   protected Iterable<ModelPart> bodyParts() {
      return ImmutableList.of(this.body, this.leg0, this.leg1, this.leg2, this.leg3, this.tail, this.upperBody);
   }

   public void prepareMobModel(T var1, float var2, float var3, float var4) {
      if (var1.isAngry()) {
         this.tail.yRot = 0.0F;
      } else {
         this.tail.yRot = Mth.cos(var2 * 0.6662F) * 1.4F * var3;
      }

      if (var1.isInSittingPose()) {
         this.upperBody.setPos(-1.0F, 16.0F, -3.0F);
         this.upperBody.xRot = 1.2566371F;
         this.upperBody.yRot = 0.0F;
         this.body.setPos(0.0F, 18.0F, 0.0F);
         this.body.xRot = 0.7853982F;
         this.tail.setPos(-1.0F, 21.0F, 6.0F);
         this.leg0.setPos(-2.5F, 22.7F, 2.0F);
         this.leg0.xRot = 4.712389F;
         this.leg1.setPos(0.5F, 22.7F, 2.0F);
         this.leg1.xRot = 4.712389F;
         this.leg2.xRot = 5.811947F;
         this.leg2.setPos(-2.49F, 17.0F, -4.0F);
         this.leg3.xRot = 5.811947F;
         this.leg3.setPos(0.51F, 17.0F, -4.0F);
      } else {
         this.body.setPos(0.0F, 14.0F, 2.0F);
         this.body.xRot = 1.5707964F;
         this.upperBody.setPos(-1.0F, 14.0F, -3.0F);
         this.upperBody.xRot = this.body.xRot;
         this.tail.setPos(-1.0F, 12.0F, 8.0F);
         this.leg0.setPos(-2.5F, 16.0F, 7.0F);
         this.leg1.setPos(0.5F, 16.0F, 7.0F);
         this.leg2.setPos(-2.5F, 16.0F, -4.0F);
         this.leg3.setPos(0.5F, 16.0F, -4.0F);
         this.leg0.xRot = Mth.cos(var2 * 0.6662F) * 1.4F * var3;
         this.leg1.xRot = Mth.cos(var2 * 0.6662F + 3.1415927F) * 1.4F * var3;
         this.leg2.xRot = Mth.cos(var2 * 0.6662F + 3.1415927F) * 1.4F * var3;
         this.leg3.xRot = Mth.cos(var2 * 0.6662F) * 1.4F * var3;
      }

      this.realHead.zRot = var1.getHeadRollAngle(var4) + var1.getBodyRollAngle(var4, 0.0F);
      this.upperBody.zRot = var1.getBodyRollAngle(var4, -0.08F);
      this.body.zRot = var1.getBodyRollAngle(var4, -0.16F);
      this.realTail.zRot = var1.getBodyRollAngle(var4, -0.2F);
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6) {
      this.head.xRot = var6 * 0.017453292F;
      this.head.yRot = var5 * 0.017453292F;
      this.tail.xRot = var4;
   }
}
