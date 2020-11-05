package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import java.util.Random;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

public class PlayerModel<T extends LivingEntity> extends HumanoidModel<T> {
   private List<ModelPart> cubes = Lists.newArrayList();
   public final ModelPart leftSleeve;
   public final ModelPart rightSleeve;
   public final ModelPart leftPants;
   public final ModelPart rightPants;
   public final ModelPart jacket;
   private final ModelPart cloak;
   private final ModelPart ear;
   private final boolean slim;

   public PlayerModel(float var1, boolean var2) {
      super(RenderType::entityTranslucent, var1, 0.0F, 64, 64);
      this.slim = var2;
      this.ear = new ModelPart(this, 24, 0);
      this.ear.addBox(-3.0F, -6.0F, -1.0F, 6.0F, 6.0F, 1.0F, var1);
      this.cloak = new ModelPart(this, 0, 0);
      this.cloak.setTexSize(64, 32);
      this.cloak.addBox(-5.0F, 0.0F, -1.0F, 10.0F, 16.0F, 1.0F, var1);
      if (var2) {
         this.leftArm = new ModelPart(this, 32, 48);
         this.leftArm.addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, var1);
         this.leftArm.setPos(5.0F, 2.5F, 0.0F);
         this.rightArm = new ModelPart(this, 40, 16);
         this.rightArm.addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, var1);
         this.rightArm.setPos(-5.0F, 2.5F, 0.0F);
         this.leftSleeve = new ModelPart(this, 48, 48);
         this.leftSleeve.addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, var1 + 0.25F);
         this.leftSleeve.setPos(5.0F, 2.5F, 0.0F);
         this.rightSleeve = new ModelPart(this, 40, 32);
         this.rightSleeve.addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, var1 + 0.25F);
         this.rightSleeve.setPos(-5.0F, 2.5F, 10.0F);
      } else {
         this.leftArm = new ModelPart(this, 32, 48);
         this.leftArm.addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, var1);
         this.leftArm.setPos(5.0F, 2.0F, 0.0F);
         this.leftSleeve = new ModelPart(this, 48, 48);
         this.leftSleeve.addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, var1 + 0.25F);
         this.leftSleeve.setPos(5.0F, 2.0F, 0.0F);
         this.rightSleeve = new ModelPart(this, 40, 32);
         this.rightSleeve.addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, var1 + 0.25F);
         this.rightSleeve.setPos(-5.0F, 2.0F, 10.0F);
      }

      this.leftLeg = new ModelPart(this, 16, 48);
      this.leftLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, var1);
      this.leftLeg.setPos(1.9F, 12.0F, 0.0F);
      this.leftPants = new ModelPart(this, 0, 48);
      this.leftPants.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, var1 + 0.25F);
      this.leftPants.setPos(1.9F, 12.0F, 0.0F);
      this.rightPants = new ModelPart(this, 0, 32);
      this.rightPants.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, var1 + 0.25F);
      this.rightPants.setPos(-1.9F, 12.0F, 0.0F);
      this.jacket = new ModelPart(this, 16, 32);
      this.jacket.addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, var1 + 0.25F);
      this.jacket.setPos(0.0F, 0.0F, 0.0F);
   }

   protected Iterable<ModelPart> bodyParts() {
      return Iterables.concat(super.bodyParts(), ImmutableList.of(this.leftPants, this.rightPants, this.leftSleeve, this.rightSleeve, this.jacket));
   }

   public void renderEars(PoseStack var1, VertexConsumer var2, int var3, int var4) {
      this.ear.copyFrom(this.head);
      this.ear.x = 0.0F;
      this.ear.y = 0.0F;
      this.ear.render(var1, var2, var3, var4);
   }

   public void renderCloak(PoseStack var1, VertexConsumer var2, int var3, int var4) {
      this.cloak.render(var1, var2, var3, var4);
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6) {
      super.setupAnim(var1, var2, var3, var4, var5, var6);
      this.leftPants.copyFrom(this.leftLeg);
      this.rightPants.copyFrom(this.rightLeg);
      this.leftSleeve.copyFrom(this.leftArm);
      this.rightSleeve.copyFrom(this.rightArm);
      this.jacket.copyFrom(this.body);
      if (var1.getItemBySlot(EquipmentSlot.CHEST).isEmpty()) {
         if (var1.isCrouching()) {
            this.cloak.z = 1.4F;
            this.cloak.y = 1.85F;
         } else {
            this.cloak.z = 0.0F;
            this.cloak.y = 0.0F;
         }
      } else if (var1.isCrouching()) {
         this.cloak.z = 0.3F;
         this.cloak.y = 0.8F;
      } else {
         this.cloak.z = -1.1F;
         this.cloak.y = -0.85F;
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

   public void translateToHand(HumanoidArm var1, PoseStack var2) {
      ModelPart var3 = this.getArm(var1);
      if (this.slim) {
         float var4 = 0.5F * (float)(var1 == HumanoidArm.RIGHT ? 1 : -1);
         var3.x += var4;
         var3.translateAndRotate(var2);
         var3.x -= var4;
      } else {
         var3.translateAndRotate(var2);
      }

   }

   public ModelPart getRandomModelPart(Random var1) {
      return (ModelPart)this.cubes.get(var1.nextInt(this.cubes.size()));
   }

   public void accept(ModelPart var1) {
      if (this.cubes == null) {
         this.cubes = Lists.newArrayList();
      }

      this.cubes.add(var1);
   }

   // $FF: synthetic method
   public void accept(Object var1) {
      this.accept((ModelPart)var1);
   }
}
