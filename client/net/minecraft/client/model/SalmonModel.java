package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class SalmonModel<T extends Entity> extends ListModel<T> {
   private final ModelPart bodyFront;
   private final ModelPart bodyBack;
   private final ModelPart head;
   private final ModelPart sideFin0;
   private final ModelPart sideFin1;

   public SalmonModel() {
      super();
      this.texWidth = 32;
      this.texHeight = 32;
      boolean var1 = true;
      this.bodyFront = new ModelPart(this, 0, 0);
      this.bodyFront.addBox(-1.5F, -2.5F, 0.0F, 3.0F, 5.0F, 8.0F);
      this.bodyFront.setPos(0.0F, 20.0F, 0.0F);
      this.bodyBack = new ModelPart(this, 0, 13);
      this.bodyBack.addBox(-1.5F, -2.5F, 0.0F, 3.0F, 5.0F, 8.0F);
      this.bodyBack.setPos(0.0F, 20.0F, 8.0F);
      this.head = new ModelPart(this, 22, 0);
      this.head.addBox(-1.0F, -2.0F, -3.0F, 2.0F, 4.0F, 3.0F);
      this.head.setPos(0.0F, 20.0F, 0.0F);
      ModelPart var2 = new ModelPart(this, 20, 10);
      var2.addBox(0.0F, -2.5F, 0.0F, 0.0F, 5.0F, 6.0F);
      var2.setPos(0.0F, 0.0F, 8.0F);
      this.bodyBack.addChild(var2);
      ModelPart var3 = new ModelPart(this, 2, 1);
      var3.addBox(0.0F, 0.0F, 0.0F, 0.0F, 2.0F, 3.0F);
      var3.setPos(0.0F, -4.5F, 5.0F);
      this.bodyFront.addChild(var3);
      ModelPart var4 = new ModelPart(this, 0, 2);
      var4.addBox(0.0F, 0.0F, 0.0F, 0.0F, 2.0F, 4.0F);
      var4.setPos(0.0F, -4.5F, -1.0F);
      this.bodyBack.addChild(var4);
      this.sideFin0 = new ModelPart(this, -4, 0);
      this.sideFin0.addBox(-2.0F, 0.0F, 0.0F, 2.0F, 0.0F, 2.0F);
      this.sideFin0.setPos(-1.5F, 21.5F, 0.0F);
      this.sideFin0.zRot = -0.7853982F;
      this.sideFin1 = new ModelPart(this, 0, 0);
      this.sideFin1.addBox(0.0F, 0.0F, 0.0F, 2.0F, 0.0F, 2.0F);
      this.sideFin1.setPos(1.5F, 21.5F, 0.0F);
      this.sideFin1.zRot = 0.7853982F;
   }

   public Iterable<ModelPart> parts() {
      return ImmutableList.of(this.bodyFront, this.bodyBack, this.head, this.sideFin0, this.sideFin1);
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6) {
      float var7 = 1.0F;
      float var8 = 1.0F;
      if (!var1.isInWater()) {
         var7 = 1.3F;
         var8 = 1.7F;
      }

      this.bodyBack.yRot = -var7 * 0.25F * Mth.sin(var8 * 0.6F * var4);
   }
}
