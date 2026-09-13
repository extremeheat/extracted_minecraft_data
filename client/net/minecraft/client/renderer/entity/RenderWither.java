package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelWither;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderWither extends RenderLiving {
   private static final ResourceLocation field_110913_a = new ResourceLocation("textures/entity/wither/wither_invulnerable.png");
   private static final ResourceLocation field_110912_f = new ResourceLocation("textures/entity/wither/wither.png");
   private int field_82419_a = ((ModelWither)this.field_77045_g).func_82903_a();

   public RenderWither() {
      super(new ModelWither(), 1.0F);
   }

   public void func_76986_a(EntityWither var1, double var2, double var4, double var6, float var8, float var9) {
      BossStatus.func_82824_a(var1, true);
      int var10 = ((ModelWither)this.field_77045_g).func_82903_a();
      if (var10 != this.field_82419_a) {
         this.field_82419_a = var10;
         this.field_77045_g = new ModelWither();
      }

      super.func_76986_a((EntityLiving)var1, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation func_110775_a(EntityWither var1) {
      int var2 = var1.func_82212_n();
      return var2 > 0 && (var2 > 80 || var2 / 5 % 2 != 1) ? field_110913_a : field_110912_f;
   }

   protected void func_77041_b(EntityWither var1, float var2) {
      int var3 = var1.func_82212_n();
      if (var3 > 0) {
         float var4 = 2.0F - ((float)var3 - var2) / 220.0F * 0.5F;
         GL11.glScalef(var4, var4, var4);
      } else {
         GL11.glScalef(2.0F, 2.0F, 2.0F);
      }
   }

   protected int func_77032_a(EntityWither var1, int var2, float var3) {
      if (var1.func_82205_o()) {
         if (var1.func_82150_aj()) {
            GL11.glDepthMask(false);
         } else {
            GL11.glDepthMask(true);
         }

         if (var2 == 1) {
            float var4 = (float)var1.field_70173_aa + var3;
            this.func_110776_a(field_110913_a);
            GL11.glMatrixMode(5890);
            GL11.glLoadIdentity();
            float var5 = MathHelper.func_76134_b(var4 * 0.02F) * 3.0F;
            float var6 = var4 * 0.01F;
            GL11.glTranslatef(var5, var6, 0.0F);
            this.func_77042_a(this.field_77045_g);
            GL11.glMatrixMode(5888);
            GL11.glEnable(3042);
            float var7 = 0.5F;
            GL11.glColor4f(var7, var7, var7, 1.0F);
            GL11.glDisable(2896);
            GL11.glBlendFunc(1, 1);
            GL11.glTranslatef(0.0F, -0.01F, 0.0F);
            GL11.glScalef(1.1F, 1.1F, 1.1F);
            return 1;
         }

         if (var2 == 2) {
            GL11.glMatrixMode(5890);
            GL11.glLoadIdentity();
            GL11.glMatrixMode(5888);
            GL11.glEnable(2896);
            GL11.glDisable(3042);
         }
      }

      return -1;
   }

   protected int func_77035_b(EntityWither var1, int var2, float var3) {
      return -1;
   }
}
