package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class TropicalFishModelA<T extends Entity> extends EntityModel<T> {
   private final ModelPart body;
   private final ModelPart tail;
   private final ModelPart leftFin;
   private final ModelPart rightFin;
   private final ModelPart topFin;

   public TropicalFishModelA() {
      this(0.0F);
   }

   public TropicalFishModelA(float var1) {
      super();
      this.texWidth = 32;
      this.texHeight = 32;
      boolean var2 = true;
      this.body = new ModelPart(this, 0, 0);
      this.body.addBox(-1.0F, -1.5F, -3.0F, 2, 3, 6, var1);
      this.body.setPos(0.0F, 22.0F, 0.0F);
      this.tail = new ModelPart(this, 22, -6);
      this.tail.addBox(0.0F, -1.5F, 0.0F, 0, 3, 6, var1);
      this.tail.setPos(0.0F, 22.0F, 3.0F);
      this.leftFin = new ModelPart(this, 2, 16);
      this.leftFin.addBox(-2.0F, -1.0F, 0.0F, 2, 2, 0, var1);
      this.leftFin.setPos(-1.0F, 22.5F, 0.0F);
      this.leftFin.yRot = 0.7853982F;
      this.rightFin = new ModelPart(this, 2, 12);
      this.rightFin.addBox(0.0F, -1.0F, 0.0F, 2, 2, 0, var1);
      this.rightFin.setPos(1.0F, 22.5F, 0.0F);
      this.rightFin.yRot = -0.7853982F;
      this.topFin = new ModelPart(this, 10, -5);
      this.topFin.addBox(0.0F, -3.0F, 0.0F, 0, 3, 6, var1);
      this.topFin.setPos(0.0F, 20.5F, -3.0F);
   }

   public void render(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.setupAnim(var1, var2, var3, var4, var5, var6, var7);
      this.body.render(var7);
      this.tail.render(var7);
      this.leftFin.render(var7);
      this.rightFin.render(var7);
      this.topFin.render(var7);
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      float var8 = 1.0F;
      if (!var1.isInWater()) {
         var8 = 1.5F;
      }

      this.tail.yRot = -var8 * 0.45F * Mth.sin(0.6F * var4);
   }
}
