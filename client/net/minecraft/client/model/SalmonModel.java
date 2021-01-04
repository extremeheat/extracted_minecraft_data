package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class SalmonModel<T extends Entity> extends EntityModel<T> {
   private final ModelPart bodyFront;
   private final ModelPart bodyBack;
   private final ModelPart head;
   private final ModelPart topFin0;
   private final ModelPart topFin1;
   private final ModelPart backFin;
   private final ModelPart sideFin0;
   private final ModelPart sideFin1;

   public SalmonModel() {
      super();
      this.texWidth = 32;
      this.texHeight = 32;
      boolean var1 = true;
      this.bodyFront = new ModelPart(this, 0, 0);
      this.bodyFront.addBox(-1.5F, -2.5F, 0.0F, 3, 5, 8);
      this.bodyFront.setPos(0.0F, 20.0F, 0.0F);
      this.bodyBack = new ModelPart(this, 0, 13);
      this.bodyBack.addBox(-1.5F, -2.5F, 0.0F, 3, 5, 8);
      this.bodyBack.setPos(0.0F, 20.0F, 8.0F);
      this.head = new ModelPart(this, 22, 0);
      this.head.addBox(-1.0F, -2.0F, -3.0F, 2, 4, 3);
      this.head.setPos(0.0F, 20.0F, 0.0F);
      this.backFin = new ModelPart(this, 20, 10);
      this.backFin.addBox(0.0F, -2.5F, 0.0F, 0, 5, 6);
      this.backFin.setPos(0.0F, 0.0F, 8.0F);
      this.bodyBack.addChild(this.backFin);
      this.topFin0 = new ModelPart(this, 2, 1);
      this.topFin0.addBox(0.0F, 0.0F, 0.0F, 0, 2, 3);
      this.topFin0.setPos(0.0F, -4.5F, 5.0F);
      this.bodyFront.addChild(this.topFin0);
      this.topFin1 = new ModelPart(this, 0, 2);
      this.topFin1.addBox(0.0F, 0.0F, 0.0F, 0, 2, 4);
      this.topFin1.setPos(0.0F, -4.5F, -1.0F);
      this.bodyBack.addChild(this.topFin1);
      this.sideFin0 = new ModelPart(this, -4, 0);
      this.sideFin0.addBox(-2.0F, 0.0F, 0.0F, 2, 0, 2);
      this.sideFin0.setPos(-1.5F, 21.5F, 0.0F);
      this.sideFin0.zRot = -0.7853982F;
      this.sideFin1 = new ModelPart(this, 0, 0);
      this.sideFin1.addBox(0.0F, 0.0F, 0.0F, 2, 0, 2);
      this.sideFin1.setPos(1.5F, 21.5F, 0.0F);
      this.sideFin1.zRot = 0.7853982F;
   }

   public void render(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.setupAnim(var1, var2, var3, var4, var5, var6, var7);
      this.bodyFront.render(var7);
      this.bodyBack.render(var7);
      this.head.render(var7);
      this.sideFin0.render(var7);
      this.sideFin1.render(var7);
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      float var8 = 1.0F;
      float var9 = 1.0F;
      if (!var1.isInWater()) {
         var8 = 1.3F;
         var9 = 1.7F;
      }

      this.bodyBack.yRot = -var8 * 0.25F * Mth.sin(var9 * 0.6F * var4);
   }
}
