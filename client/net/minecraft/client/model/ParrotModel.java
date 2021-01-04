package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Parrot;

public class ParrotModel extends EntityModel<Parrot> {
   private final ModelPart body;
   private final ModelPart tail;
   private final ModelPart wingLeft;
   private final ModelPart wingRight;
   private final ModelPart head;
   private final ModelPart head2;
   private final ModelPart beak1;
   private final ModelPart beak2;
   private final ModelPart feather;
   private final ModelPart legLeft;
   private final ModelPart legRight;

   public ParrotModel() {
      super();
      this.texWidth = 32;
      this.texHeight = 32;
      this.body = new ModelPart(this, 2, 8);
      this.body.addBox(-1.5F, 0.0F, -1.5F, 3, 6, 3);
      this.body.setPos(0.0F, 16.5F, -3.0F);
      this.tail = new ModelPart(this, 22, 1);
      this.tail.addBox(-1.5F, -1.0F, -1.0F, 3, 4, 1);
      this.tail.setPos(0.0F, 21.07F, 1.16F);
      this.wingLeft = new ModelPart(this, 19, 8);
      this.wingLeft.addBox(-0.5F, 0.0F, -1.5F, 1, 5, 3);
      this.wingLeft.setPos(1.5F, 16.94F, -2.76F);
      this.wingRight = new ModelPart(this, 19, 8);
      this.wingRight.addBox(-0.5F, 0.0F, -1.5F, 1, 5, 3);
      this.wingRight.setPos(-1.5F, 16.94F, -2.76F);
      this.head = new ModelPart(this, 2, 2);
      this.head.addBox(-1.0F, -1.5F, -1.0F, 2, 3, 2);
      this.head.setPos(0.0F, 15.69F, -2.76F);
      this.head2 = new ModelPart(this, 10, 0);
      this.head2.addBox(-1.0F, -0.5F, -2.0F, 2, 1, 4);
      this.head2.setPos(0.0F, -2.0F, -1.0F);
      this.head.addChild(this.head2);
      this.beak1 = new ModelPart(this, 11, 7);
      this.beak1.addBox(-0.5F, -1.0F, -0.5F, 1, 2, 1);
      this.beak1.setPos(0.0F, -0.5F, -1.5F);
      this.head.addChild(this.beak1);
      this.beak2 = new ModelPart(this, 16, 7);
      this.beak2.addBox(-0.5F, 0.0F, -0.5F, 1, 2, 1);
      this.beak2.setPos(0.0F, -1.75F, -2.45F);
      this.head.addChild(this.beak2);
      this.feather = new ModelPart(this, 2, 18);
      this.feather.addBox(0.0F, -4.0F, -2.0F, 0, 5, 4);
      this.feather.setPos(0.0F, -2.15F, 0.15F);
      this.head.addChild(this.feather);
      this.legLeft = new ModelPart(this, 14, 18);
      this.legLeft.addBox(-0.5F, 0.0F, -0.5F, 1, 2, 1);
      this.legLeft.setPos(1.0F, 22.0F, -1.05F);
      this.legRight = new ModelPart(this, 14, 18);
      this.legRight.addBox(-0.5F, 0.0F, -0.5F, 1, 2, 1);
      this.legRight.setPos(-1.0F, 22.0F, -1.05F);
   }

   public void render(Parrot var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.render(var7);
   }

   public void setupAnim(Parrot var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.setupAnim(getState(var1), var1.tickCount, var2, var3, var4, var5, var6);
   }

   public void prepareMobModel(Parrot var1, float var2, float var3, float var4) {
      this.prepare(getState(var1));
   }

   public void renderOnShoulder(float var1, float var2, float var3, float var4, float var5, int var6) {
      this.prepare(ParrotModel.State.ON_SHOULDER);
      this.setupAnim(ParrotModel.State.ON_SHOULDER, var6, var1, var2, 0.0F, var3, var4);
      this.render(var5);
   }

   private void render(float var1) {
      this.body.render(var1);
      this.wingLeft.render(var1);
      this.wingRight.render(var1);
      this.tail.render(var1);
      this.head.render(var1);
      this.legLeft.render(var1);
      this.legRight.render(var1);
   }

   private void setupAnim(ParrotModel.State var1, int var2, float var3, float var4, float var5, float var6, float var7) {
      this.head.xRot = var7 * 0.017453292F;
      this.head.yRot = var6 * 0.017453292F;
      this.head.zRot = 0.0F;
      this.head.x = 0.0F;
      this.body.x = 0.0F;
      this.tail.x = 0.0F;
      this.wingRight.x = -1.5F;
      this.wingLeft.x = 1.5F;
      switch(var1) {
      case SITTING:
         break;
      case PARTY:
         float var8 = Mth.cos((float)var2);
         float var9 = Mth.sin((float)var2);
         this.head.x = var8;
         this.head.y = 15.69F + var9;
         this.head.xRot = 0.0F;
         this.head.yRot = 0.0F;
         this.head.zRot = Mth.sin((float)var2) * 0.4F;
         this.body.x = var8;
         this.body.y = 16.5F + var9;
         this.wingLeft.zRot = -0.0873F - var5;
         this.wingLeft.x = 1.5F + var8;
         this.wingLeft.y = 16.94F + var9;
         this.wingRight.zRot = 0.0873F + var5;
         this.wingRight.x = -1.5F + var8;
         this.wingRight.y = 16.94F + var9;
         this.tail.x = var8;
         this.tail.y = 21.07F + var9;
         break;
      case STANDING:
         ModelPart var10000 = this.legLeft;
         var10000.xRot += Mth.cos(var3 * 0.6662F) * 1.4F * var4;
         var10000 = this.legRight;
         var10000.xRot += Mth.cos(var3 * 0.6662F + 3.1415927F) * 1.4F * var4;
      case FLYING:
      case ON_SHOULDER:
      default:
         float var10 = var5 * 0.3F;
         this.head.y = 15.69F + var10;
         this.tail.xRot = 1.015F + Mth.cos(var3 * 0.6662F) * 0.3F * var4;
         this.tail.y = 21.07F + var10;
         this.body.y = 16.5F + var10;
         this.wingLeft.zRot = -0.0873F - var5;
         this.wingLeft.y = 16.94F + var10;
         this.wingRight.zRot = 0.0873F + var5;
         this.wingRight.y = 16.94F + var10;
         this.legLeft.y = 22.0F + var10;
         this.legRight.y = 22.0F + var10;
      }

   }

   private void prepare(ParrotModel.State var1) {
      this.feather.xRot = -0.2214F;
      this.body.xRot = 0.4937F;
      this.wingLeft.xRot = -0.6981F;
      this.wingLeft.yRot = -3.1415927F;
      this.wingRight.xRot = -0.6981F;
      this.wingRight.yRot = -3.1415927F;
      this.legLeft.xRot = -0.0299F;
      this.legRight.xRot = -0.0299F;
      this.legLeft.y = 22.0F;
      this.legRight.y = 22.0F;
      this.legLeft.zRot = 0.0F;
      this.legRight.zRot = 0.0F;
      switch(var1) {
      case SITTING:
         float var2 = 1.9F;
         this.head.y = 17.59F;
         this.tail.xRot = 1.5388988F;
         this.tail.y = 22.97F;
         this.body.y = 18.4F;
         this.wingLeft.zRot = -0.0873F;
         this.wingLeft.y = 18.84F;
         this.wingRight.zRot = 0.0873F;
         this.wingRight.y = 18.84F;
         ++this.legLeft.y;
         ++this.legRight.y;
         ++this.legLeft.xRot;
         ++this.legRight.xRot;
         break;
      case PARTY:
         this.legLeft.zRot = -0.34906584F;
         this.legRight.zRot = 0.34906584F;
      case STANDING:
      case ON_SHOULDER:
      default:
         break;
      case FLYING:
         ModelPart var10000 = this.legLeft;
         var10000.xRot += 0.6981317F;
         var10000 = this.legRight;
         var10000.xRot += 0.6981317F;
      }

   }

   private static ParrotModel.State getState(Parrot var0) {
      if (var0.isPartyParrot()) {
         return ParrotModel.State.PARTY;
      } else if (var0.isSitting()) {
         return ParrotModel.State.SITTING;
      } else {
         return var0.isFlying() ? ParrotModel.State.FLYING : ParrotModel.State.STANDING;
      }
   }

   // $FF: synthetic method
   public void setupAnim(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.setupAnim((Parrot)var1, var2, var3, var4, var5, var6, var7);
   }

   // $FF: synthetic method
   public void render(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.render((Parrot)var1, var2, var3, var4, var5, var6, var7);
   }

   public static enum State {
      FLYING,
      STANDING,
      SITTING,
      PARTY,
      ON_SHOULDER;

      private State() {
      }
   }
}
