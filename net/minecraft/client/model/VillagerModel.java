package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.AbstractVillager;

public class VillagerModel extends ListModel implements HeadedModel, VillagerHeadModel {
   protected ModelPart head;
   protected ModelPart hat;
   protected final ModelPart hatRim;
   protected final ModelPart body;
   protected final ModelPart jacket;
   protected final ModelPart arms;
   protected final ModelPart leg0;
   protected final ModelPart leg1;
   protected final ModelPart nose;

   public VillagerModel(float var1) {
      this(var1, 64, 64);
   }

   public VillagerModel(float var1, int var2, int var3) {
      float var4 = 0.5F;
      this.head = (new ModelPart(this)).setTexSize(var2, var3);
      this.head.setPos(0.0F, 0.0F, 0.0F);
      this.head.texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, var1);
      this.hat = (new ModelPart(this)).setTexSize(var2, var3);
      this.hat.setPos(0.0F, 0.0F, 0.0F);
      this.hat.texOffs(32, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, var1 + 0.5F);
      this.head.addChild(this.hat);
      this.hatRim = (new ModelPart(this)).setTexSize(var2, var3);
      this.hatRim.setPos(0.0F, 0.0F, 0.0F);
      this.hatRim.texOffs(30, 47).addBox(-8.0F, -8.0F, -6.0F, 16.0F, 16.0F, 1.0F, var1);
      this.hatRim.xRot = -1.5707964F;
      this.hat.addChild(this.hatRim);
      this.nose = (new ModelPart(this)).setTexSize(var2, var3);
      this.nose.setPos(0.0F, -2.0F, 0.0F);
      this.nose.texOffs(24, 0).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F, var1);
      this.head.addChild(this.nose);
      this.body = (new ModelPart(this)).setTexSize(var2, var3);
      this.body.setPos(0.0F, 0.0F, 0.0F);
      this.body.texOffs(16, 20).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F, var1);
      this.jacket = (new ModelPart(this)).setTexSize(var2, var3);
      this.jacket.setPos(0.0F, 0.0F, 0.0F);
      this.jacket.texOffs(0, 38).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 18.0F, 6.0F, var1 + 0.5F);
      this.body.addChild(this.jacket);
      this.arms = (new ModelPart(this)).setTexSize(var2, var3);
      this.arms.setPos(0.0F, 2.0F, 0.0F);
      this.arms.texOffs(44, 22).addBox(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, var1);
      this.arms.texOffs(44, 22).addBox(4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, var1, true);
      this.arms.texOffs(40, 38).addBox(-4.0F, 2.0F, -2.0F, 8.0F, 4.0F, 4.0F, var1);
      this.leg0 = (new ModelPart(this, 0, 22)).setTexSize(var2, var3);
      this.leg0.setPos(-2.0F, 12.0F, 0.0F);
      this.leg0.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, var1);
      this.leg1 = (new ModelPart(this, 0, 22)).setTexSize(var2, var3);
      this.leg1.mirror = true;
      this.leg1.setPos(2.0F, 12.0F, 0.0F);
      this.leg1.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, var1);
   }

   public Iterable parts() {
      return ImmutableList.of(this.head, this.body, this.leg0, this.leg1, this.arms);
   }

   public void setupAnim(Entity var1, float var2, float var3, float var4, float var5, float var6) {
      boolean var7 = false;
      if (var1 instanceof AbstractVillager) {
         var7 = ((AbstractVillager)var1).getUnhappyCounter() > 0;
      }

      this.head.yRot = var5 * 0.017453292F;
      this.head.xRot = var6 * 0.017453292F;
      if (var7) {
         this.head.zRot = 0.3F * Mth.sin(0.45F * var4);
         this.head.xRot = 0.4F;
      } else {
         this.head.zRot = 0.0F;
      }

      this.arms.y = 3.0F;
      this.arms.z = -1.0F;
      this.arms.xRot = -0.75F;
      this.leg0.xRot = Mth.cos(var2 * 0.6662F) * 1.4F * var3 * 0.5F;
      this.leg1.xRot = Mth.cos(var2 * 0.6662F + 3.1415927F) * 1.4F * var3 * 0.5F;
      this.leg0.yRot = 0.0F;
      this.leg1.yRot = 0.0F;
   }

   public ModelPart getHead() {
      return this.head;
   }

   public void hatVisible(boolean var1) {
      this.head.visible = var1;
      this.hat.visible = var1;
      this.hatRim.visible = var1;
   }
}
