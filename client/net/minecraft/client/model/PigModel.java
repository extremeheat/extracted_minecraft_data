package net.minecraft.client.model;

import net.minecraft.world.entity.Entity;

public class PigModel<T extends Entity> extends QuadrupedModel<T> {
   public PigModel() {
      this(0.0F);
   }

   public PigModel(float var1) {
      super(6, var1);
      this.head.texOffs(16, 16).addBox(-2.0F, 0.0F, -9.0F, 4, 3, 1, var1);
      this.yHeadOffs = 4.0F;
   }
}
