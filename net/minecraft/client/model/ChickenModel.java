package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class ChickenModel extends AgeableListModel {
   private final ModelPart head;
   private final ModelPart body;
   private final ModelPart leg0;
   private final ModelPart leg1;
   private final ModelPart wing0;
   private final ModelPart wing1;
   private final ModelPart beak;
   private final ModelPart redThing;

   public ChickenModel() {
      boolean var1 = true;
      this.head = new ModelPart(this, 0, 0);
      this.head.addBox(-2.0F, -6.0F, -2.0F, 4.0F, 6.0F, 3.0F, 0.0F);
      this.head.setPos(0.0F, 15.0F, -4.0F);
      this.beak = new ModelPart(this, 14, 0);
      this.beak.addBox(-2.0F, -4.0F, -4.0F, 4.0F, 2.0F, 2.0F, 0.0F);
      this.beak.setPos(0.0F, 15.0F, -4.0F);
      this.redThing = new ModelPart(this, 14, 4);
      this.redThing.addBox(-1.0F, -2.0F, -3.0F, 2.0F, 2.0F, 2.0F, 0.0F);
      this.redThing.setPos(0.0F, 15.0F, -4.0F);
      this.body = new ModelPart(this, 0, 9);
      this.body.addBox(-3.0F, -4.0F, -3.0F, 6.0F, 8.0F, 6.0F, 0.0F);
      this.body.setPos(0.0F, 16.0F, 0.0F);
      this.leg0 = new ModelPart(this, 26, 0);
      this.leg0.addBox(-1.0F, 0.0F, -3.0F, 3.0F, 5.0F, 3.0F);
      this.leg0.setPos(-2.0F, 19.0F, 1.0F);
      this.leg1 = new ModelPart(this, 26, 0);
      this.leg1.addBox(-1.0F, 0.0F, -3.0F, 3.0F, 5.0F, 3.0F);
      this.leg1.setPos(1.0F, 19.0F, 1.0F);
      this.wing0 = new ModelPart(this, 24, 13);
      this.wing0.addBox(0.0F, 0.0F, -3.0F, 1.0F, 4.0F, 6.0F);
      this.wing0.setPos(-4.0F, 13.0F, 0.0F);
      this.wing1 = new ModelPart(this, 24, 13);
      this.wing1.addBox(-1.0F, 0.0F, -3.0F, 1.0F, 4.0F, 6.0F);
      this.wing1.setPos(4.0F, 13.0F, 0.0F);
   }

   protected Iterable headParts() {
      return ImmutableList.of(this.head, this.beak, this.redThing);
   }

   protected Iterable bodyParts() {
      return ImmutableList.of(this.body, this.leg0, this.leg1, this.wing0, this.wing1);
   }

   public void setupAnim(Entity var1, float var2, float var3, float var4, float var5, float var6) {
      this.head.xRot = var6 * 0.017453292F;
      this.head.yRot = var5 * 0.017453292F;
      this.beak.xRot = this.head.xRot;
      this.beak.yRot = this.head.yRot;
      this.redThing.xRot = this.head.xRot;
      this.redThing.yRot = this.head.yRot;
      this.body.xRot = 1.5707964F;
      this.leg0.xRot = Mth.cos(var2 * 0.6662F) * 1.4F * var3;
      this.leg1.xRot = Mth.cos(var2 * 0.6662F + 3.1415927F) * 1.4F * var3;
      this.wing0.zRot = var4;
      this.wing1.zRot = -var4;
   }
}
