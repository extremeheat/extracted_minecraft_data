package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Turtle;

public class TurtleModel<T extends Turtle> extends QuadrupedModel<T> {
   private final ModelPart eggBelly;

   public TurtleModel(float var1) {
      super(12, var1, true, 120.0F, 0.0F, 9.0F, 6.0F, 120);
      this.texWidth = 128;
      this.texHeight = 64;
      this.head = new ModelPart(this, 3, 0);
      this.head.addBox(-3.0F, -1.0F, -3.0F, 6.0F, 5.0F, 6.0F, 0.0F);
      this.head.setPos(0.0F, 19.0F, -10.0F);
      this.body = new ModelPart(this);
      this.body.texOffs(7, 37).addBox(-9.5F, 3.0F, -10.0F, 19.0F, 20.0F, 6.0F, 0.0F);
      this.body.texOffs(31, 1).addBox(-5.5F, 3.0F, -13.0F, 11.0F, 18.0F, 3.0F, 0.0F);
      this.body.setPos(0.0F, 11.0F, -10.0F);
      this.eggBelly = new ModelPart(this);
      this.eggBelly.texOffs(70, 33).addBox(-4.5F, 3.0F, -14.0F, 9.0F, 18.0F, 1.0F, 0.0F);
      this.eggBelly.setPos(0.0F, 11.0F, -10.0F);
      boolean var2 = true;
      this.leg0 = new ModelPart(this, 1, 23);
      this.leg0.addBox(-2.0F, 0.0F, 0.0F, 4.0F, 1.0F, 10.0F, 0.0F);
      this.leg0.setPos(-3.5F, 22.0F, 11.0F);
      this.leg1 = new ModelPart(this, 1, 12);
      this.leg1.addBox(-2.0F, 0.0F, 0.0F, 4.0F, 1.0F, 10.0F, 0.0F);
      this.leg1.setPos(3.5F, 22.0F, 11.0F);
      this.leg2 = new ModelPart(this, 27, 30);
      this.leg2.addBox(-13.0F, 0.0F, -2.0F, 13.0F, 1.0F, 5.0F, 0.0F);
      this.leg2.setPos(-5.0F, 21.0F, -4.0F);
      this.leg3 = new ModelPart(this, 27, 24);
      this.leg3.addBox(0.0F, 0.0F, -2.0F, 13.0F, 1.0F, 5.0F, 0.0F);
      this.leg3.setPos(5.0F, 21.0F, -4.0F);
   }

   protected Iterable<ModelPart> bodyParts() {
      return Iterables.concat(super.bodyParts(), ImmutableList.of(this.eggBelly));
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6) {
      super.setupAnim(var1, var2, var3, var4, var5, var6);
      this.leg0.xRot = Mth.cos(var2 * 0.6662F * 0.6F) * 0.5F * var3;
      this.leg1.xRot = Mth.cos(var2 * 0.6662F * 0.6F + 3.1415927F) * 0.5F * var3;
      this.leg2.zRot = Mth.cos(var2 * 0.6662F * 0.6F + 3.1415927F) * 0.5F * var3;
      this.leg3.zRot = Mth.cos(var2 * 0.6662F * 0.6F) * 0.5F * var3;
      this.leg2.xRot = 0.0F;
      this.leg3.xRot = 0.0F;
      this.leg2.yRot = 0.0F;
      this.leg3.yRot = 0.0F;
      this.leg0.yRot = 0.0F;
      this.leg1.yRot = 0.0F;
      this.eggBelly.xRot = 1.5707964F;
      if (!var1.isInWater() && var1.isOnGround()) {
         float var7 = var1.isLayingEgg() ? 4.0F : 1.0F;
         float var8 = var1.isLayingEgg() ? 2.0F : 1.0F;
         float var9 = 5.0F;
         this.leg2.yRot = Mth.cos(var7 * var2 * 5.0F + 3.1415927F) * 8.0F * var3 * var8;
         this.leg2.zRot = 0.0F;
         this.leg3.yRot = Mth.cos(var7 * var2 * 5.0F) * 8.0F * var3 * var8;
         this.leg3.zRot = 0.0F;
         this.leg0.yRot = Mth.cos(var2 * 5.0F + 3.1415927F) * 3.0F * var3;
         this.leg0.xRot = 0.0F;
         this.leg1.yRot = Mth.cos(var2 * 5.0F) * 3.0F * var3;
         this.leg1.xRot = 0.0F;
      }

      this.eggBelly.visible = !this.young && var1.hasEgg();
   }

   public void renderToBuffer(PoseStack var1, VertexConsumer var2, int var3, int var4, float var5, float var6, float var7, float var8) {
      boolean var9 = this.eggBelly.visible;
      if (var9) {
         var1.pushPose();
         var1.translate(0.0D, -0.07999999821186066D, 0.0D);
      }

      super.renderToBuffer(var1, var2, var3, var4, var5, var6, var7, var8);
      if (var9) {
         var1.popPose();
      }

   }
}
