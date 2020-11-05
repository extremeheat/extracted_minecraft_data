package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ambient.Bat;

public class BatModel extends ListModel<Bat> {
   private final ModelPart head;
   private final ModelPart body;
   private final ModelPart rightWing;
   private final ModelPart leftWing;
   private final ModelPart rightWingTip;
   private final ModelPart leftWingTip;

   public BatModel() {
      super();
      this.texWidth = 64;
      this.texHeight = 64;
      this.head = new ModelPart(this, 0, 0);
      this.head.addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F);
      ModelPart var1 = new ModelPart(this, 24, 0);
      var1.addBox(-4.0F, -6.0F, -2.0F, 3.0F, 4.0F, 1.0F);
      this.head.addChild(var1);
      ModelPart var2 = new ModelPart(this, 24, 0);
      var2.mirror = true;
      var2.addBox(1.0F, -6.0F, -2.0F, 3.0F, 4.0F, 1.0F);
      this.head.addChild(var2);
      this.body = new ModelPart(this, 0, 16);
      this.body.addBox(-3.0F, 4.0F, -3.0F, 6.0F, 12.0F, 6.0F);
      this.body.texOffs(0, 34).addBox(-5.0F, 16.0F, 0.0F, 10.0F, 6.0F, 1.0F);
      this.rightWing = new ModelPart(this, 42, 0);
      this.rightWing.addBox(-12.0F, 1.0F, 1.5F, 10.0F, 16.0F, 1.0F);
      this.rightWingTip = new ModelPart(this, 24, 16);
      this.rightWingTip.setPos(-12.0F, 1.0F, 1.5F);
      this.rightWingTip.addBox(-8.0F, 1.0F, 0.0F, 8.0F, 12.0F, 1.0F);
      this.leftWing = new ModelPart(this, 42, 0);
      this.leftWing.mirror = true;
      this.leftWing.addBox(2.0F, 1.0F, 1.5F, 10.0F, 16.0F, 1.0F);
      this.leftWingTip = new ModelPart(this, 24, 16);
      this.leftWingTip.mirror = true;
      this.leftWingTip.setPos(12.0F, 1.0F, 1.5F);
      this.leftWingTip.addBox(0.0F, 1.0F, 0.0F, 8.0F, 12.0F, 1.0F);
      this.body.addChild(this.rightWing);
      this.body.addChild(this.leftWing);
      this.rightWing.addChild(this.rightWingTip);
      this.leftWing.addChild(this.leftWingTip);
   }

   public Iterable<ModelPart> parts() {
      return ImmutableList.of(this.head, this.body);
   }

   public void setupAnim(Bat var1, float var2, float var3, float var4, float var5, float var6) {
      if (var1.isResting()) {
         this.head.xRot = var6 * 0.017453292F;
         this.head.yRot = 3.1415927F - var5 * 0.017453292F;
         this.head.zRot = 3.1415927F;
         this.head.setPos(0.0F, -2.0F, 0.0F);
         this.rightWing.setPos(-3.0F, 0.0F, 3.0F);
         this.leftWing.setPos(3.0F, 0.0F, 3.0F);
         this.body.xRot = 3.1415927F;
         this.rightWing.xRot = -0.15707964F;
         this.rightWing.yRot = -1.2566371F;
         this.rightWingTip.yRot = -1.7278761F;
         this.leftWing.xRot = this.rightWing.xRot;
         this.leftWing.yRot = -this.rightWing.yRot;
         this.leftWingTip.yRot = -this.rightWingTip.yRot;
      } else {
         this.head.xRot = var6 * 0.017453292F;
         this.head.yRot = var5 * 0.017453292F;
         this.head.zRot = 0.0F;
         this.head.setPos(0.0F, 0.0F, 0.0F);
         this.rightWing.setPos(0.0F, 0.0F, 0.0F);
         this.leftWing.setPos(0.0F, 0.0F, 0.0F);
         this.body.xRot = 0.7853982F + Mth.cos(var4 * 0.1F) * 0.15F;
         this.body.yRot = 0.0F;
         this.rightWing.yRot = Mth.cos(var4 * 1.3F) * 3.1415927F * 0.25F;
         this.leftWing.yRot = -this.rightWing.yRot;
         this.rightWingTip.yRot = this.rightWing.yRot * 0.5F;
         this.leftWingTip.yRot = -this.rightWing.yRot * 0.5F;
      }

   }
}
