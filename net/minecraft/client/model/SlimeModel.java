package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

public class SlimeModel extends ListModel {
   private final ModelPart cube;
   private final ModelPart eye0;
   private final ModelPart eye1;
   private final ModelPart mouth;

   public SlimeModel(int var1) {
      this.cube = new ModelPart(this, 0, var1);
      this.eye0 = new ModelPart(this, 32, 0);
      this.eye1 = new ModelPart(this, 32, 4);
      this.mouth = new ModelPart(this, 32, 8);
      if (var1 > 0) {
         this.cube.addBox(-3.0F, 17.0F, -3.0F, 6.0F, 6.0F, 6.0F);
         this.eye0.addBox(-3.25F, 18.0F, -3.5F, 2.0F, 2.0F, 2.0F);
         this.eye1.addBox(1.25F, 18.0F, -3.5F, 2.0F, 2.0F, 2.0F);
         this.mouth.addBox(0.0F, 21.0F, -3.5F, 1.0F, 1.0F, 1.0F);
      } else {
         this.cube.addBox(-4.0F, 16.0F, -4.0F, 8.0F, 8.0F, 8.0F);
      }

   }

   public void setupAnim(Entity var1, float var2, float var3, float var4, float var5, float var6) {
   }

   public Iterable parts() {
      return ImmutableList.of(this.cube, this.eye0, this.eye1, this.mouth);
   }
}
