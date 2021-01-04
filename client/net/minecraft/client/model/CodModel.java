package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class CodModel<T extends Entity> extends EntityModel<T> {
   private final ModelPart body;
   private final ModelPart topFin;
   private final ModelPart head;
   private final ModelPart nose;
   private final ModelPart sideFin0;
   private final ModelPart sideFin1;
   private final ModelPart tailFin;

   public CodModel() {
      super();
      this.texWidth = 32;
      this.texHeight = 32;
      boolean var1 = true;
      this.body = new ModelPart(this, 0, 0);
      this.body.addBox(-1.0F, -2.0F, 0.0F, 2, 4, 7);
      this.body.setPos(0.0F, 22.0F, 0.0F);
      this.head = new ModelPart(this, 11, 0);
      this.head.addBox(-1.0F, -2.0F, -3.0F, 2, 4, 3);
      this.head.setPos(0.0F, 22.0F, 0.0F);
      this.nose = new ModelPart(this, 0, 0);
      this.nose.addBox(-1.0F, -2.0F, -1.0F, 2, 3, 1);
      this.nose.setPos(0.0F, 22.0F, -3.0F);
      this.sideFin0 = new ModelPart(this, 22, 1);
      this.sideFin0.addBox(-2.0F, 0.0F, -1.0F, 2, 0, 2);
      this.sideFin0.setPos(-1.0F, 23.0F, 0.0F);
      this.sideFin0.zRot = -0.7853982F;
      this.sideFin1 = new ModelPart(this, 22, 4);
      this.sideFin1.addBox(0.0F, 0.0F, -1.0F, 2, 0, 2);
      this.sideFin1.setPos(1.0F, 23.0F, 0.0F);
      this.sideFin1.zRot = 0.7853982F;
      this.tailFin = new ModelPart(this, 22, 3);
      this.tailFin.addBox(0.0F, -2.0F, 0.0F, 0, 4, 4);
      this.tailFin.setPos(0.0F, 22.0F, 7.0F);
      this.topFin = new ModelPart(this, 20, -6);
      this.topFin.addBox(0.0F, -1.0F, -1.0F, 0, 1, 6);
      this.topFin.setPos(0.0F, 20.0F, 0.0F);
   }

   public void render(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.setupAnim(var1, var2, var3, var4, var5, var6, var7);
      this.body.render(var7);
      this.head.render(var7);
      this.nose.render(var7);
      this.sideFin0.render(var7);
      this.sideFin1.render(var7);
      this.tailFin.render(var7);
      this.topFin.render(var7);
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      float var8 = 1.0F;
      if (!var1.isInWater()) {
         var8 = 1.5F;
      }

      this.tailFin.yRot = -var8 * 0.45F * Mth.sin(0.6F * var4);
   }
}
