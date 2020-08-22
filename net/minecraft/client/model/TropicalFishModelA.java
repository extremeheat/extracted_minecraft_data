package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class TropicalFishModelA extends ColorableListModel {
   private final ModelPart body;
   private final ModelPart tail;
   private final ModelPart leftFin;
   private final ModelPart rightFin;
   private final ModelPart topFin;

   public TropicalFishModelA(float var1) {
      this.texWidth = 32;
      this.texHeight = 32;
      boolean var2 = true;
      this.body = new ModelPart(this, 0, 0);
      this.body.addBox(-1.0F, -1.5F, -3.0F, 2.0F, 3.0F, 6.0F, var1);
      this.body.setPos(0.0F, 22.0F, 0.0F);
      this.tail = new ModelPart(this, 22, -6);
      this.tail.addBox(0.0F, -1.5F, 0.0F, 0.0F, 3.0F, 6.0F, var1);
      this.tail.setPos(0.0F, 22.0F, 3.0F);
      this.leftFin = new ModelPart(this, 2, 16);
      this.leftFin.addBox(-2.0F, -1.0F, 0.0F, 2.0F, 2.0F, 0.0F, var1);
      this.leftFin.setPos(-1.0F, 22.5F, 0.0F);
      this.leftFin.yRot = 0.7853982F;
      this.rightFin = new ModelPart(this, 2, 12);
      this.rightFin.addBox(0.0F, -1.0F, 0.0F, 2.0F, 2.0F, 0.0F, var1);
      this.rightFin.setPos(1.0F, 22.5F, 0.0F);
      this.rightFin.yRot = -0.7853982F;
      this.topFin = new ModelPart(this, 10, -5);
      this.topFin.addBox(0.0F, -3.0F, 0.0F, 0.0F, 3.0F, 6.0F, var1);
      this.topFin.setPos(0.0F, 20.5F, -3.0F);
   }

   public Iterable parts() {
      return ImmutableList.of(this.body, this.tail, this.leftFin, this.rightFin, this.topFin);
   }

   public void setupAnim(Entity var1, float var2, float var3, float var4, float var5, float var6) {
      float var7 = 1.0F;
      if (!var1.isInWater()) {
         var7 = 1.5F;
      }

      this.tail.yRot = -var7 * 0.45F * Mth.sin(0.6F * var4);
   }
}
