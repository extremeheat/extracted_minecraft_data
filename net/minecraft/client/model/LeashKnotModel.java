package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

public class LeashKnotModel extends ListModel {
   private final ModelPart knot;

   public LeashKnotModel() {
      this.texWidth = 32;
      this.texHeight = 32;
      this.knot = new ModelPart(this, 0, 0);
      this.knot.addBox(-3.0F, -6.0F, -3.0F, 6.0F, 8.0F, 6.0F, 0.0F);
      this.knot.setPos(0.0F, 0.0F, 0.0F);
   }

   public Iterable parts() {
      return ImmutableList.of(this.knot);
   }

   public void setupAnim(Entity var1, float var2, float var3, float var4, float var5, float var6) {
      this.knot.yRot = var5 * 0.017453292F;
      this.knot.xRot = var6 * 0.017453292F;
   }
}
