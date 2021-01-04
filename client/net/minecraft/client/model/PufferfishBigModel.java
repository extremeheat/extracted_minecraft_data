package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class PufferfishBigModel<T extends Entity> extends EntityModel<T> {
   private final ModelPart cube;
   private final ModelPart blueFin0;
   private final ModelPart blueFin1;
   private final ModelPart topFrontFin;
   private final ModelPart topMidFin;
   private final ModelPart topBackFin;
   private final ModelPart sideFrontFin0;
   private final ModelPart sideFrontFin1;
   private final ModelPart bottomFrontFin;
   private final ModelPart bottomBackFin;
   private final ModelPart bottomMidFin;
   private final ModelPart sideBackFin0;
   private final ModelPart sideBackFin1;

   public PufferfishBigModel() {
      super();
      this.texWidth = 32;
      this.texHeight = 32;
      boolean var1 = true;
      this.cube = new ModelPart(this, 0, 0);
      this.cube.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8);
      this.cube.setPos(0.0F, 22.0F, 0.0F);
      this.blueFin0 = new ModelPart(this, 24, 0);
      this.blueFin0.addBox(-2.0F, 0.0F, -1.0F, 2, 1, 2);
      this.blueFin0.setPos(-4.0F, 15.0F, -2.0F);
      this.blueFin1 = new ModelPart(this, 24, 3);
      this.blueFin1.addBox(0.0F, 0.0F, -1.0F, 2, 1, 2);
      this.blueFin1.setPos(4.0F, 15.0F, -2.0F);
      this.topFrontFin = new ModelPart(this, 15, 17);
      this.topFrontFin.addBox(-4.0F, -1.0F, 0.0F, 8, 1, 0);
      this.topFrontFin.setPos(0.0F, 14.0F, -4.0F);
      this.topFrontFin.xRot = 0.7853982F;
      this.topMidFin = new ModelPart(this, 14, 16);
      this.topMidFin.addBox(-4.0F, -1.0F, 0.0F, 8, 1, 1);
      this.topMidFin.setPos(0.0F, 14.0F, 0.0F);
      this.topBackFin = new ModelPart(this, 23, 18);
      this.topBackFin.addBox(-4.0F, -1.0F, 0.0F, 8, 1, 0);
      this.topBackFin.setPos(0.0F, 14.0F, 4.0F);
      this.topBackFin.xRot = -0.7853982F;
      this.sideFrontFin0 = new ModelPart(this, 5, 17);
      this.sideFrontFin0.addBox(-1.0F, -8.0F, 0.0F, 1, 8, 0);
      this.sideFrontFin0.setPos(-4.0F, 22.0F, -4.0F);
      this.sideFrontFin0.yRot = -0.7853982F;
      this.sideFrontFin1 = new ModelPart(this, 1, 17);
      this.sideFrontFin1.addBox(0.0F, -8.0F, 0.0F, 1, 8, 0);
      this.sideFrontFin1.setPos(4.0F, 22.0F, -4.0F);
      this.sideFrontFin1.yRot = 0.7853982F;
      this.bottomFrontFin = new ModelPart(this, 15, 20);
      this.bottomFrontFin.addBox(-4.0F, 0.0F, 0.0F, 8, 1, 0);
      this.bottomFrontFin.setPos(0.0F, 22.0F, -4.0F);
      this.bottomFrontFin.xRot = -0.7853982F;
      this.bottomMidFin = new ModelPart(this, 15, 20);
      this.bottomMidFin.addBox(-4.0F, 0.0F, 0.0F, 8, 1, 0);
      this.bottomMidFin.setPos(0.0F, 22.0F, 0.0F);
      this.bottomBackFin = new ModelPart(this, 15, 20);
      this.bottomBackFin.addBox(-4.0F, 0.0F, 0.0F, 8, 1, 0);
      this.bottomBackFin.setPos(0.0F, 22.0F, 4.0F);
      this.bottomBackFin.xRot = 0.7853982F;
      this.sideBackFin0 = new ModelPart(this, 9, 17);
      this.sideBackFin0.addBox(-1.0F, -8.0F, 0.0F, 1, 8, 0);
      this.sideBackFin0.setPos(-4.0F, 22.0F, 4.0F);
      this.sideBackFin0.yRot = 0.7853982F;
      this.sideBackFin1 = new ModelPart(this, 9, 17);
      this.sideBackFin1.addBox(0.0F, -8.0F, 0.0F, 1, 8, 0);
      this.sideBackFin1.setPos(4.0F, 22.0F, 4.0F);
      this.sideBackFin1.yRot = -0.7853982F;
   }

   public void render(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.setupAnim(var1, var2, var3, var4, var5, var6, var7);
      this.cube.render(var7);
      this.blueFin0.render(var7);
      this.blueFin1.render(var7);
      this.topFrontFin.render(var7);
      this.topMidFin.render(var7);
      this.topBackFin.render(var7);
      this.sideFrontFin0.render(var7);
      this.sideFrontFin1.render(var7);
      this.bottomFrontFin.render(var7);
      this.bottomMidFin.render(var7);
      this.bottomBackFin.render(var7);
      this.sideBackFin0.render(var7);
      this.sideBackFin1.render(var7);
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.blueFin0.zRot = -0.2F + 0.4F * Mth.sin(var4 * 0.2F);
      this.blueFin1.zRot = 0.2F - 0.4F * Mth.sin(var4 * 0.2F);
   }
}
