package net.minecraft.client.model;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Random;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class GhastModel<T extends Entity> extends EntityModel<T> {
   private final ModelPart body;
   private final ModelPart[] tentacles = new ModelPart[9];

   public GhastModel() {
      super();
      boolean var1 = true;
      this.body = new ModelPart(this, 0, 0);
      this.body.addBox(-8.0F, -8.0F, -8.0F, 16, 16, 16);
      ModelPart var10000 = this.body;
      var10000.y += 8.0F;
      Random var2 = new Random(1660L);

      for(int var3 = 0; var3 < this.tentacles.length; ++var3) {
         this.tentacles[var3] = new ModelPart(this, 0, 0);
         float var4 = (((float)(var3 % 3) - (float)(var3 / 3 % 2) * 0.5F + 0.25F) / 2.0F * 2.0F - 1.0F) * 5.0F;
         float var5 = ((float)(var3 / 3) / 2.0F * 2.0F - 1.0F) * 5.0F;
         int var6 = var2.nextInt(7) + 8;
         this.tentacles[var3].addBox(-1.0F, 0.0F, -1.0F, 2, var6, 2);
         this.tentacles[var3].x = var4;
         this.tentacles[var3].z = var5;
         this.tentacles[var3].y = 15.0F;
      }

   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      for(int var8 = 0; var8 < this.tentacles.length; ++var8) {
         this.tentacles[var8].xRot = 0.2F * Mth.sin(var4 * 0.3F + (float)var8) + 0.4F;
      }

   }

   public void render(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.setupAnim(var1, var2, var3, var4, var5, var6, var7);
      GlStateManager.pushMatrix();
      GlStateManager.translatef(0.0F, 0.6F, 0.0F);
      this.body.render(var7);
      ModelPart[] var8 = this.tentacles;
      int var9 = var8.length;

      for(int var10 = 0; var10 < var9; ++var10) {
         ModelPart var11 = var8[var10];
         var11.render(var7);
      }

      GlStateManager.popMatrix();
   }
}
