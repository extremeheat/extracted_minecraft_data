package net.minecraft.client.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

public class SlimeModel<T extends Entity> extends EntityModel<T> {
   private final ModelPart cube;
   private final ModelPart eye0;
   private final ModelPart eye1;
   private final ModelPart mouth;

   public SlimeModel(int var1) {
      super();
      if (var1 > 0) {
         this.cube = new ModelPart(this, 0, var1);
         this.cube.addBox(-3.0F, 17.0F, -3.0F, 6, 6, 6);
         this.eye0 = new ModelPart(this, 32, 0);
         this.eye0.addBox(-3.25F, 18.0F, -3.5F, 2, 2, 2);
         this.eye1 = new ModelPart(this, 32, 4);
         this.eye1.addBox(1.25F, 18.0F, -3.5F, 2, 2, 2);
         this.mouth = new ModelPart(this, 32, 8);
         this.mouth.addBox(0.0F, 21.0F, -3.5F, 1, 1, 1);
      } else {
         this.cube = new ModelPart(this, 0, var1);
         this.cube.addBox(-4.0F, 16.0F, -4.0F, 8, 8, 8);
         this.eye0 = null;
         this.eye1 = null;
         this.mouth = null;
      }

   }

   public void render(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.setupAnim(var1, var2, var3, var4, var5, var6, var7);
      GlStateManager.translatef(0.0F, 0.001F, 0.0F);
      this.cube.render(var7);
      if (this.eye0 != null) {
         this.eye0.render(var7);
         this.eye1.render(var7);
         this.mouth.render(var7);
      }

   }
}
