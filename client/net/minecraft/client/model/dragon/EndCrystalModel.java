package net.minecraft.client.model.dragon;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

public class EndCrystalModel<T extends Entity> extends EntityModel<T> {
   private final ModelPart cube;
   private final ModelPart glass = new ModelPart(this, "glass");
   private final ModelPart base;

   public EndCrystalModel(float var1, boolean var2) {
      super();
      this.glass.texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8);
      this.cube = new ModelPart(this, "cube");
      this.cube.texOffs(32, 0).addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8);
      if (var2) {
         this.base = new ModelPart(this, "base");
         this.base.texOffs(0, 16).addBox(-6.0F, 0.0F, -6.0F, 12, 4, 12);
      } else {
         this.base = null;
      }

   }

   public void render(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      GlStateManager.pushMatrix();
      GlStateManager.scalef(2.0F, 2.0F, 2.0F);
      GlStateManager.translatef(0.0F, -0.5F, 0.0F);
      if (this.base != null) {
         this.base.render(var7);
      }

      GlStateManager.rotatef(var3, 0.0F, 1.0F, 0.0F);
      GlStateManager.translatef(0.0F, 0.8F + var4, 0.0F);
      GlStateManager.rotatef(60.0F, 0.7071F, 0.0F, 0.7071F);
      this.glass.render(var7);
      float var8 = 0.875F;
      GlStateManager.scalef(0.875F, 0.875F, 0.875F);
      GlStateManager.rotatef(60.0F, 0.7071F, 0.0F, 0.7071F);
      GlStateManager.rotatef(var3, 0.0F, 1.0F, 0.0F);
      this.glass.render(var7);
      GlStateManager.scalef(0.875F, 0.875F, 0.875F);
      GlStateManager.rotatef(60.0F, 0.7071F, 0.0F, 0.7071F);
      GlStateManager.rotatef(var3, 0.0F, 1.0F, 0.0F);
      this.cube.render(var7);
      GlStateManager.popMatrix();
   }
}
