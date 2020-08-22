package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class EvokerFangsModel extends ListModel {
   private final ModelPart base = new ModelPart(this, 0, 0);
   private final ModelPart upperJaw;
   private final ModelPart lowerJaw;

   public EvokerFangsModel() {
      this.base.setPos(-5.0F, 22.0F, -5.0F);
      this.base.addBox(0.0F, 0.0F, 0.0F, 10.0F, 12.0F, 10.0F);
      this.upperJaw = new ModelPart(this, 40, 0);
      this.upperJaw.setPos(1.5F, 22.0F, -4.0F);
      this.upperJaw.addBox(0.0F, 0.0F, 0.0F, 4.0F, 14.0F, 8.0F);
      this.lowerJaw = new ModelPart(this, 40, 0);
      this.lowerJaw.setPos(-1.5F, 22.0F, 4.0F);
      this.lowerJaw.addBox(0.0F, 0.0F, 0.0F, 4.0F, 14.0F, 8.0F);
   }

   public void setupAnim(Entity var1, float var2, float var3, float var4, float var5, float var6) {
      float var7 = var2 * 2.0F;
      if (var7 > 1.0F) {
         var7 = 1.0F;
      }

      var7 = 1.0F - var7 * var7 * var7;
      this.upperJaw.zRot = 3.1415927F - var7 * 0.35F * 3.1415927F;
      this.lowerJaw.zRot = 3.1415927F + var7 * 0.35F * 3.1415927F;
      this.lowerJaw.yRot = 3.1415927F;
      float var8 = (var2 + Mth.sin(var2 * 2.7F)) * 0.6F * 12.0F;
      this.upperJaw.y = 24.0F - var8;
      this.lowerJaw.y = this.upperJaw.y;
      this.base.y = this.upperJaw.y;
   }

   public Iterable parts() {
      return ImmutableList.of(this.base, this.upperJaw, this.lowerJaw);
   }
}
