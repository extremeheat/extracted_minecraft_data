package net.minecraft.client.particle;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class EntityLargeExplodeFX extends EntityFX {
   private static final ResourceLocation field_110127_a = new ResourceLocation("textures/entity/explosion.png");
   private int field_70581_a;
   private int field_70584_aq;
   private TextureManager field_70583_ar;
   private float field_70582_as;

   public EntityLargeExplodeFX(TextureManager var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13) {
      super(var2, var3, var5, var7, 0.0, 0.0, 0.0);
      this.field_70583_ar = var1;
      this.field_70584_aq = 6 + this.field_70146_Z.nextInt(4);
      this.field_70552_h = this.field_70553_i = this.field_70551_j = this.field_70146_Z.nextFloat() * 0.6F + 0.4F;
      this.field_70582_as = 1.0F - (float)var9 * 0.5F;
   }

   @Override
   public void func_70539_a(Tessellator var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      int var8 = (int)(((float)this.field_70581_a + var2) * 15.0F / (float)this.field_70584_aq);
      if (var8 <= 15) {
         this.field_70583_ar.func_110577_a(field_110127_a);
         float var9 = (float)(var8 % 4) / 4.0F;
         float var10 = var9 + 0.24975F;
         float var11 = (float)(var8 / 4) / 4.0F;
         float var12 = var11 + 0.24975F;
         float var13 = 2.0F * this.field_70582_as;
         float var14 = (float)(this.field_70169_q + (this.field_70165_t - this.field_70169_q) * (double)var2 - field_70556_an);
         float var15 = (float)(this.field_70167_r + (this.field_70163_u - this.field_70167_r) * (double)var2 - field_70554_ao);
         float var16 = (float)(this.field_70166_s + (this.field_70161_v - this.field_70166_s) * (double)var2 - field_70555_ap);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         GL11.glDisable(2896);
         RenderHelper.func_74518_a();
         var1.func_78382_b();
         var1.func_78369_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, 1.0F);
         var1.func_78375_b(0.0F, 1.0F, 0.0F);
         var1.func_78380_c(240);
         var1.func_78374_a(
            (double)(var14 - var3 * var13 - var6 * var13),
            (double)(var15 - var4 * var13),
            (double)(var16 - var5 * var13 - var7 * var13),
            (double)var10,
            (double)var12
         );
         var1.func_78374_a(
            (double)(var14 - var3 * var13 + var6 * var13),
            (double)(var15 + var4 * var13),
            (double)(var16 - var5 * var13 + var7 * var13),
            (double)var10,
            (double)var11
         );
         var1.func_78374_a(
            (double)(var14 + var3 * var13 + var6 * var13),
            (double)(var15 + var4 * var13),
            (double)(var16 + var5 * var13 + var7 * var13),
            (double)var9,
            (double)var11
         );
         var1.func_78374_a(
            (double)(var14 + var3 * var13 - var6 * var13),
            (double)(var15 - var4 * var13),
            (double)(var16 + var5 * var13 - var7 * var13),
            (double)var9,
            (double)var12
         );
         var1.func_78381_a();
         GL11.glPolygonOffset(0.0F, 0.0F);
         GL11.glEnable(2896);
      }
   }

   @Override
   public int func_70070_b(float var1) {
      return 61680;
   }

   @Override
   public void func_70071_h_() {
      this.field_70169_q = this.field_70165_t;
      this.field_70167_r = this.field_70163_u;
      this.field_70166_s = this.field_70161_v;
      ++this.field_70581_a;
      if (this.field_70581_a == this.field_70584_aq) {
         this.func_70106_y();
      }
   }

   @Override
   public int func_70537_b() {
      return 3;
   }
}
