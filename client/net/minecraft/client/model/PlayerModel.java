package net.minecraft.client.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

public class PlayerModel<T extends LivingEntity> extends HumanoidModel<T> {
   public final ModelPart leftSleeve;
   public final ModelPart rightSleeve;
   public final ModelPart leftPants;
   public final ModelPart rightPants;
   public final ModelPart jacket;
   private final ModelPart cloak;
   private final ModelPart ear;
   private final boolean slim;

   public PlayerModel(float var1, boolean var2) {
      super(var1, 0.0F, 64, 64);
      this.slim = var2;
      this.ear = new ModelPart(this, 24, 0);
      this.ear.addBox(-3.0F, -6.0F, -1.0F, 6, 6, 1, var1);
      this.cloak = new ModelPart(this, 0, 0);
      this.cloak.setTexSize(64, 32);
      this.cloak.addBox(-5.0F, 0.0F, -1.0F, 10, 16, 1, var1);
      if (var2) {
         this.leftArm = new ModelPart(this, 32, 48);
         this.leftArm.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, var1);
         this.leftArm.setPos(5.0F, 2.5F, 0.0F);
         this.rightArm = new ModelPart(this, 40, 16);
         this.rightArm.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, var1);
         this.rightArm.setPos(-5.0F, 2.5F, 0.0F);
         this.leftSleeve = new ModelPart(this, 48, 48);
         this.leftSleeve.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, var1 + 0.25F);
         this.leftSleeve.setPos(5.0F, 2.5F, 0.0F);
         this.rightSleeve = new ModelPart(this, 40, 32);
         this.rightSleeve.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, var1 + 0.25F);
         this.rightSleeve.setPos(-5.0F, 2.5F, 10.0F);
      } else {
         this.leftArm = new ModelPart(this, 32, 48);
         this.leftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, var1);
         this.leftArm.setPos(5.0F, 2.0F, 0.0F);
         this.leftSleeve = new ModelPart(this, 48, 48);
         this.leftSleeve.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, var1 + 0.25F);
         this.leftSleeve.setPos(5.0F, 2.0F, 0.0F);
         this.rightSleeve = new ModelPart(this, 40, 32);
         this.rightSleeve.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, var1 + 0.25F);
         this.rightSleeve.setPos(-5.0F, 2.0F, 10.0F);
      }

      this.leftLeg = new ModelPart(this, 16, 48);
      this.leftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, var1);
      this.leftLeg.setPos(1.9F, 12.0F, 0.0F);
      this.leftPants = new ModelPart(this, 0, 48);
      this.leftPants.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, var1 + 0.25F);
      this.leftPants.setPos(1.9F, 12.0F, 0.0F);
      this.rightPants = new ModelPart(this, 0, 32);
      this.rightPants.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, var1 + 0.25F);
      this.rightPants.setPos(-1.9F, 12.0F, 0.0F);
      this.jacket = new ModelPart(this, 16, 32);
      this.jacket.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, var1 + 0.25F);
      this.jacket.setPos(0.0F, 0.0F, 0.0F);
   }

   public void render(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      super.render(var1, var2, var3, var4, var5, var6, var7);
      GlStateManager.pushMatrix();
      if (this.young) {
         float var8 = 2.0F;
         GlStateManager.scalef(0.5F, 0.5F, 0.5F);
         GlStateManager.translatef(0.0F, 24.0F * var7, 0.0F);
         this.leftPants.render(var7);
         this.rightPants.render(var7);
         this.leftSleeve.render(var7);
         this.rightSleeve.render(var7);
         this.jacket.render(var7);
      } else {
         if (var1.isVisuallySneaking()) {
            GlStateManager.translatef(0.0F, 0.2F, 0.0F);
         }

         this.leftPants.render(var7);
         this.rightPants.render(var7);
         this.leftSleeve.render(var7);
         this.rightSleeve.render(var7);
         this.jacket.render(var7);
      }

      GlStateManager.popMatrix();
   }

   public void renderEars(float var1) {
      this.ear.copyFrom(this.head);
      this.ear.x = 0.0F;
      this.ear.y = 0.0F;
      this.ear.render(var1);
   }

   public void renderCloak(float var1) {
      this.cloak.render(var1);
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      super.setupAnim(var1, var2, var3, var4, var5, var6, var7);
      this.leftPants.copyFrom(this.leftLeg);
      this.rightPants.copyFrom(this.rightLeg);
      this.leftSleeve.copyFrom(this.leftArm);
      this.rightSleeve.copyFrom(this.rightArm);
      this.jacket.copyFrom(this.body);
      if (var1.isVisuallySneaking()) {
         this.cloak.y = 2.0F;
      } else {
         this.cloak.y = 0.0F;
      }

   }

   public void setAllVisible(boolean var1) {
      super.setAllVisible(var1);
      this.leftSleeve.visible = var1;
      this.rightSleeve.visible = var1;
      this.leftPants.visible = var1;
      this.rightPants.visible = var1;
      this.jacket.visible = var1;
      this.cloak.visible = var1;
      this.ear.visible = var1;
   }

   public void translateToHand(float var1, HumanoidArm var2) {
      ModelPart var3 = this.getArm(var2);
      if (this.slim) {
         float var4 = 0.5F * (float)(var2 == HumanoidArm.RIGHT ? 1 : -1);
         var3.x += var4;
         var3.translateTo(var1);
         var3.x -= var4;
      } else {
         var3.translateTo(var1);
      }

   }
}
