package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class BlazeModel<T extends Entity> extends EntityModel<T> {
   private final ModelPart[] upperBodyParts = new ModelPart[12];
   private final ModelPart head;

   public BlazeModel() {
      super();

      for(int var1 = 0; var1 < this.upperBodyParts.length; ++var1) {
         this.upperBodyParts[var1] = new ModelPart(this, 0, 16);
         this.upperBodyParts[var1].addBox(0.0F, 0.0F, 0.0F, 2, 8, 2);
      }

      this.head = new ModelPart(this, 0, 0);
      this.head.addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8);
   }

   public void render(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.setupAnim(var1, var2, var3, var4, var5, var6, var7);
      this.head.render(var7);
      ModelPart[] var8 = this.upperBodyParts;
      int var9 = var8.length;

      for(int var10 = 0; var10 < var9; ++var10) {
         ModelPart var11 = var8[var10];
         var11.render(var7);
      }

   }

   public void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      float var8 = var4 * 3.1415927F * -0.1F;

      int var9;
      for(var9 = 0; var9 < 4; ++var9) {
         this.upperBodyParts[var9].y = -2.0F + Mth.cos(((float)(var9 * 2) + var4) * 0.25F);
         this.upperBodyParts[var9].x = Mth.cos(var8) * 9.0F;
         this.upperBodyParts[var9].z = Mth.sin(var8) * 9.0F;
         ++var8;
      }

      var8 = 0.7853982F + var4 * 3.1415927F * 0.03F;

      for(var9 = 4; var9 < 8; ++var9) {
         this.upperBodyParts[var9].y = 2.0F + Mth.cos(((float)(var9 * 2) + var4) * 0.25F);
         this.upperBodyParts[var9].x = Mth.cos(var8) * 7.0F;
         this.upperBodyParts[var9].z = Mth.sin(var8) * 7.0F;
         ++var8;
      }

      var8 = 0.47123894F + var4 * 3.1415927F * -0.05F;

      for(var9 = 8; var9 < 12; ++var9) {
         this.upperBodyParts[var9].y = 11.0F + Mth.cos(((float)var9 * 1.5F + var4) * 0.5F);
         this.upperBodyParts[var9].x = Mth.cos(var8) * 5.0F;
         this.upperBodyParts[var9].z = Mth.sin(var8) * 5.0F;
         ++var8;
      }

      this.head.yRot = var5 * 0.017453292F;
      this.head.xRot = var6 * 0.017453292F;
   }
}
