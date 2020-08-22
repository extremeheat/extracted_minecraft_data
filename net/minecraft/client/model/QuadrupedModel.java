package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class QuadrupedModel extends AgeableListModel {
   protected ModelPart head = new ModelPart(this, 0, 0);
   protected ModelPart body;
   protected ModelPart leg0;
   protected ModelPart leg1;
   protected ModelPart leg2;
   protected ModelPart leg3;

   public QuadrupedModel(int var1, float var2, boolean var3, float var4, float var5, float var6, float var7, int var8) {
      super(var3, var4, var5, var6, var7, (float)var8);
      this.head.addBox(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 8.0F, var2);
      this.head.setPos(0.0F, (float)(18 - var1), -6.0F);
      this.body = new ModelPart(this, 28, 8);
      this.body.addBox(-5.0F, -10.0F, -7.0F, 10.0F, 16.0F, 8.0F, var2);
      this.body.setPos(0.0F, (float)(17 - var1), 2.0F);
      this.leg0 = new ModelPart(this, 0, 16);
      this.leg0.addBox(-2.0F, 0.0F, -2.0F, 4.0F, (float)var1, 4.0F, var2);
      this.leg0.setPos(-3.0F, (float)(24 - var1), 7.0F);
      this.leg1 = new ModelPart(this, 0, 16);
      this.leg1.addBox(-2.0F, 0.0F, -2.0F, 4.0F, (float)var1, 4.0F, var2);
      this.leg1.setPos(3.0F, (float)(24 - var1), 7.0F);
      this.leg2 = new ModelPart(this, 0, 16);
      this.leg2.addBox(-2.0F, 0.0F, -2.0F, 4.0F, (float)var1, 4.0F, var2);
      this.leg2.setPos(-3.0F, (float)(24 - var1), -5.0F);
      this.leg3 = new ModelPart(this, 0, 16);
      this.leg3.addBox(-2.0F, 0.0F, -2.0F, 4.0F, (float)var1, 4.0F, var2);
      this.leg3.setPos(3.0F, (float)(24 - var1), -5.0F);
   }

   protected Iterable headParts() {
      return ImmutableList.of(this.head);
   }

   protected Iterable bodyParts() {
      return ImmutableList.of(this.body, this.leg0, this.leg1, this.leg2, this.leg3);
   }

   public void setupAnim(Entity var1, float var2, float var3, float var4, float var5, float var6) {
      this.head.xRot = var6 * 0.017453292F;
      this.head.yRot = var5 * 0.017453292F;
      this.body.xRot = 1.5707964F;
      this.leg0.xRot = Mth.cos(var2 * 0.6662F) * 1.4F * var3;
      this.leg1.xRot = Mth.cos(var2 * 0.6662F + 3.1415927F) * 1.4F * var3;
      this.leg2.xRot = Mth.cos(var2 * 0.6662F + 3.1415927F) * 1.4F * var3;
      this.leg3.xRot = Mth.cos(var2 * 0.6662F) * 1.4F * var3;
   }
}
