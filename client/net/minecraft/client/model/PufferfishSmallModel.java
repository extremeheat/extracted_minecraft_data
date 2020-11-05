package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class PufferfishSmallModel<T extends Entity> extends ListModel<T> {
   private final ModelPart cube;
   private final ModelPart eye0;
   private final ModelPart eye1;
   private final ModelPart fin0;
   private final ModelPart fin1;
   private final ModelPart finBack;

   public PufferfishSmallModel() {
      super();
      this.texWidth = 32;
      this.texHeight = 32;
      boolean var1 = true;
      this.cube = new ModelPart(this, 0, 27);
      this.cube.addBox(-1.5F, -2.0F, -1.5F, 3.0F, 2.0F, 3.0F);
      this.cube.setPos(0.0F, 23.0F, 0.0F);
      this.eye0 = new ModelPart(this, 24, 6);
      this.eye0.addBox(-1.5F, 0.0F, -1.5F, 1.0F, 1.0F, 1.0F);
      this.eye0.setPos(0.0F, 20.0F, 0.0F);
      this.eye1 = new ModelPart(this, 28, 6);
      this.eye1.addBox(0.5F, 0.0F, -1.5F, 1.0F, 1.0F, 1.0F);
      this.eye1.setPos(0.0F, 20.0F, 0.0F);
      this.finBack = new ModelPart(this, -3, 0);
      this.finBack.addBox(-1.5F, 0.0F, 0.0F, 3.0F, 0.0F, 3.0F);
      this.finBack.setPos(0.0F, 22.0F, 1.5F);
      this.fin0 = new ModelPart(this, 25, 0);
      this.fin0.addBox(-1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 2.0F);
      this.fin0.setPos(-1.5F, 22.0F, -1.5F);
      this.fin1 = new ModelPart(this, 25, 0);
      this.fin1.addBox(0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 2.0F);
      this.fin1.setPos(1.5F, 22.0F, -1.5F);
   }

   public Iterable<ModelPart> parts() {
      return ImmutableList.of(this.cube, this.eye0, this.eye1, this.finBack, this.fin0, this.fin1);
   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6) {
      this.fin0.zRot = -0.2F + 0.4F * Mth.sin(var4 * 0.2F);
      this.fin1.zRot = 0.2F - 0.4F * Mth.sin(var4 * 0.2F);
   }
}
