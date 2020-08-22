package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

public class HorseModel extends AgeableListModel {
   protected final ModelPart body;
   protected final ModelPart headParts;
   private final ModelPart leg1;
   private final ModelPart leg2;
   private final ModelPart leg3;
   private final ModelPart leg4;
   private final ModelPart babyLeg1;
   private final ModelPart babyLeg2;
   private final ModelPart babyLeg3;
   private final ModelPart babyLeg4;
   private final ModelPart tail;
   private final ModelPart[] saddleParts;
   private final ModelPart[] ridingParts;

   public HorseModel(float var1) {
      super(true, 16.2F, 1.36F, 2.7272F, 2.0F, 20.0F);
      this.texWidth = 64;
      this.texHeight = 64;
      this.body = new ModelPart(this, 0, 32);
      this.body.addBox(-5.0F, -8.0F, -17.0F, 10.0F, 10.0F, 22.0F, 0.05F);
      this.body.setPos(0.0F, 11.0F, 5.0F);
      this.headParts = new ModelPart(this, 0, 35);
      this.headParts.addBox(-2.05F, -6.0F, -2.0F, 4.0F, 12.0F, 7.0F);
      this.headParts.xRot = 0.5235988F;
      ModelPart var2 = new ModelPart(this, 0, 13);
      var2.addBox(-3.0F, -11.0F, -2.0F, 6.0F, 5.0F, 7.0F, var1);
      ModelPart var3 = new ModelPart(this, 56, 36);
      var3.addBox(-1.0F, -11.0F, 5.01F, 2.0F, 16.0F, 2.0F, var1);
      ModelPart var4 = new ModelPart(this, 0, 25);
      var4.addBox(-2.0F, -11.0F, -7.0F, 4.0F, 5.0F, 5.0F, var1);
      this.headParts.addChild(var2);
      this.headParts.addChild(var3);
      this.headParts.addChild(var4);
      this.addEarModels(this.headParts);
      this.leg1 = new ModelPart(this, 48, 21);
      this.leg1.mirror = true;
      this.leg1.addBox(-3.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, var1);
      this.leg1.setPos(4.0F, 14.0F, 7.0F);
      this.leg2 = new ModelPart(this, 48, 21);
      this.leg2.addBox(-1.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, var1);
      this.leg2.setPos(-4.0F, 14.0F, 7.0F);
      this.leg3 = new ModelPart(this, 48, 21);
      this.leg3.mirror = true;
      this.leg3.addBox(-3.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, var1);
      this.leg3.setPos(4.0F, 6.0F, -12.0F);
      this.leg4 = new ModelPart(this, 48, 21);
      this.leg4.addBox(-1.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, var1);
      this.leg4.setPos(-4.0F, 6.0F, -12.0F);
      float var5 = 5.5F;
      this.babyLeg1 = new ModelPart(this, 48, 21);
      this.babyLeg1.mirror = true;
      this.babyLeg1.addBox(-3.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, var1, var1 + 5.5F, var1);
      this.babyLeg1.setPos(4.0F, 14.0F, 7.0F);
      this.babyLeg2 = new ModelPart(this, 48, 21);
      this.babyLeg2.addBox(-1.0F, -1.01F, -1.0F, 4.0F, 11.0F, 4.0F, var1, var1 + 5.5F, var1);
      this.babyLeg2.setPos(-4.0F, 14.0F, 7.0F);
      this.babyLeg3 = new ModelPart(this, 48, 21);
      this.babyLeg3.mirror = true;
      this.babyLeg3.addBox(-3.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, var1, var1 + 5.5F, var1);
      this.babyLeg3.setPos(4.0F, 6.0F, -12.0F);
      this.babyLeg4 = new ModelPart(this, 48, 21);
      this.babyLeg4.addBox(-1.0F, -1.01F, -1.9F, 4.0F, 11.0F, 4.0F, var1, var1 + 5.5F, var1);
      this.babyLeg4.setPos(-4.0F, 6.0F, -12.0F);
      this.tail = new ModelPart(this, 42, 36);
      this.tail.addBox(-1.5F, 0.0F, 0.0F, 3.0F, 14.0F, 4.0F, var1);
      this.tail.setPos(0.0F, -5.0F, 2.0F);
      this.tail.xRot = 0.5235988F;
      this.body.addChild(this.tail);
      ModelPart var6 = new ModelPart(this, 26, 0);
      var6.addBox(-5.0F, -8.0F, -9.0F, 10.0F, 9.0F, 9.0F, 0.5F);
      this.body.addChild(var6);
      ModelPart var7 = new ModelPart(this, 29, 5);
      var7.addBox(2.0F, -9.0F, -6.0F, 1.0F, 2.0F, 2.0F, var1);
      this.headParts.addChild(var7);
      ModelPart var8 = new ModelPart(this, 29, 5);
      var8.addBox(-3.0F, -9.0F, -6.0F, 1.0F, 2.0F, 2.0F, var1);
      this.headParts.addChild(var8);
      ModelPart var9 = new ModelPart(this, 32, 2);
      var9.addBox(3.1F, -6.0F, -8.0F, 0.0F, 3.0F, 16.0F, var1);
      var9.xRot = -0.5235988F;
      this.headParts.addChild(var9);
      ModelPart var10 = new ModelPart(this, 32, 2);
      var10.addBox(-3.1F, -6.0F, -8.0F, 0.0F, 3.0F, 16.0F, var1);
      var10.xRot = -0.5235988F;
      this.headParts.addChild(var10);
      ModelPart var11 = new ModelPart(this, 1, 1);
      var11.addBox(-3.0F, -11.0F, -1.9F, 6.0F, 5.0F, 6.0F, 0.2F);
      this.headParts.addChild(var11);
      ModelPart var12 = new ModelPart(this, 19, 0);
      var12.addBox(-2.0F, -11.0F, -4.0F, 4.0F, 5.0F, 2.0F, 0.2F);
      this.headParts.addChild(var12);
      this.saddleParts = new ModelPart[]{var6, var7, var8, var11, var12};
      this.ridingParts = new ModelPart[]{var9, var10};
   }

   protected void addEarModels(ModelPart var1) {
      ModelPart var2 = new ModelPart(this, 19, 16);
      var2.addBox(0.55F, -13.0F, 4.0F, 2.0F, 3.0F, 1.0F, -0.001F);
      ModelPart var3 = new ModelPart(this, 19, 16);
      var3.addBox(-2.55F, -13.0F, 4.0F, 2.0F, 3.0F, 1.0F, -0.001F);
      var1.addChild(var2);
      var1.addChild(var3);
   }

   public void setupAnim(AbstractHorse var1, float var2, float var3, float var4, float var5, float var6) {
      boolean var7 = var1.isSaddled();
      boolean var8 = var1.isVehicle();
      ModelPart[] var9 = this.saddleParts;
      int var10 = var9.length;

      int var11;
      ModelPart var12;
      for(var11 = 0; var11 < var10; ++var11) {
         var12 = var9[var11];
         var12.visible = var7;
      }

      var9 = this.ridingParts;
      var10 = var9.length;

      for(var11 = 0; var11 < var10; ++var11) {
         var12 = var9[var11];
         var12.visible = var8 && var7;
      }

      this.body.y = 11.0F;
   }

   public Iterable headParts() {
      return ImmutableList.of(this.headParts);
   }

   protected Iterable bodyParts() {
      return ImmutableList.of(this.body, this.leg1, this.leg2, this.leg3, this.leg4, this.babyLeg1, this.babyLeg2, this.babyLeg3, this.babyLeg4);
   }

   public void prepareMobModel(AbstractHorse var1, float var2, float var3, float var4) {
      super.prepareMobModel(var1, var2, var3, var4);
      float var5 = Mth.rotlerp(var1.yBodyRotO, var1.yBodyRot, var4);
      float var6 = Mth.rotlerp(var1.yHeadRotO, var1.yHeadRot, var4);
      float var7 = Mth.lerp(var4, var1.xRotO, var1.xRot);
      float var8 = var6 - var5;
      float var9 = var7 * 0.017453292F;
      if (var8 > 20.0F) {
         var8 = 20.0F;
      }

      if (var8 < -20.0F) {
         var8 = -20.0F;
      }

      if (var3 > 0.2F) {
         var9 += Mth.cos(var2 * 0.4F) * 0.15F * var3;
      }

      float var10 = var1.getEatAnim(var4);
      float var11 = var1.getStandAnim(var4);
      float var12 = 1.0F - var11;
      float var13 = var1.getMouthAnim(var4);
      boolean var14 = var1.tailCounter != 0;
      float var15 = (float)var1.tickCount + var4;
      this.headParts.y = 4.0F;
      this.headParts.z = -12.0F;
      this.body.xRot = 0.0F;
      this.headParts.xRot = 0.5235988F + var9;
      this.headParts.yRot = var8 * 0.017453292F;
      float var16 = var1.isInWater() ? 0.2F : 1.0F;
      float var17 = Mth.cos(var16 * var2 * 0.6662F + 3.1415927F);
      float var18 = var17 * 0.8F * var3;
      float var19 = (1.0F - Math.max(var11, var10)) * (0.5235988F + var9 + var13 * Mth.sin(var15) * 0.05F);
      this.headParts.xRot = var11 * (0.2617994F + var9) + var10 * (2.1816616F + Mth.sin(var15) * 0.05F) + var19;
      this.headParts.yRot = var11 * var8 * 0.017453292F + (1.0F - Math.max(var11, var10)) * this.headParts.yRot;
      this.headParts.y = var11 * -4.0F + var10 * 11.0F + (1.0F - Math.max(var11, var10)) * this.headParts.y;
      this.headParts.z = var11 * -4.0F + var10 * -12.0F + (1.0F - Math.max(var11, var10)) * this.headParts.z;
      this.body.xRot = var11 * -0.7853982F + var12 * this.body.xRot;
      float var20 = 0.2617994F * var11;
      float var21 = Mth.cos(var15 * 0.6F + 3.1415927F);
      this.leg3.y = 2.0F * var11 + 14.0F * var12;
      this.leg3.z = -6.0F * var11 - 10.0F * var12;
      this.leg4.y = this.leg3.y;
      this.leg4.z = this.leg3.z;
      float var22 = (-1.0471976F + var21) * var11 + var18 * var12;
      float var23 = (-1.0471976F - var21) * var11 - var18 * var12;
      this.leg1.xRot = var20 - var17 * 0.5F * var3 * var12;
      this.leg2.xRot = var20 + var17 * 0.5F * var3 * var12;
      this.leg3.xRot = var22;
      this.leg4.xRot = var23;
      this.tail.xRot = 0.5235988F + var3 * 0.75F;
      this.tail.y = -5.0F + var3;
      this.tail.z = 2.0F + var3 * 2.0F;
      if (var14) {
         this.tail.yRot = Mth.cos(var15 * 0.7F);
      } else {
         this.tail.yRot = 0.0F;
      }

      this.babyLeg1.y = this.leg1.y;
      this.babyLeg1.z = this.leg1.z;
      this.babyLeg1.xRot = this.leg1.xRot;
      this.babyLeg2.y = this.leg2.y;
      this.babyLeg2.z = this.leg2.z;
      this.babyLeg2.xRot = this.leg2.xRot;
      this.babyLeg3.y = this.leg3.y;
      this.babyLeg3.z = this.leg3.z;
      this.babyLeg3.xRot = this.leg3.xRot;
      this.babyLeg4.y = this.leg4.y;
      this.babyLeg4.z = this.leg4.z;
      this.babyLeg4.xRot = this.leg4.xRot;
      boolean var24 = var1.isBaby();
      this.leg1.visible = !var24;
      this.leg2.visible = !var24;
      this.leg3.visible = !var24;
      this.leg4.visible = !var24;
      this.babyLeg1.visible = var24;
      this.babyLeg2.visible = var24;
      this.babyLeg3.visible = var24;
      this.babyLeg4.visible = var24;
      this.body.y = var24 ? 10.8F : 0.0F;
   }
}
