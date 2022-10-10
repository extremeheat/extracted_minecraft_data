package net.minecraft.client.renderer.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class RenderFish extends Render<EntityFishHook> {
   private static final ResourceLocation field_110792_a = new ResourceLocation("textures/particle/particles.png");

   public RenderFish(RenderManager var1) {
      super(var1);
   }

   public void func_76986_a(EntityFishHook var1, double var2, double var4, double var6, float var8, float var9) {
      EntityPlayer var10 = var1.func_190619_l();
      if (var10 != null && !this.field_188301_f) {
         GlStateManager.func_179094_E();
         GlStateManager.func_179109_b((float)var2, (float)var4, (float)var6);
         GlStateManager.func_179091_B();
         GlStateManager.func_179152_a(0.5F, 0.5F, 0.5F);
         this.func_180548_c(var1);
         Tessellator var11 = Tessellator.func_178181_a();
         BufferBuilder var12 = var11.func_178180_c();
         boolean var13 = true;
         boolean var14 = true;
         float var15 = 0.03125F;
         float var16 = 0.0625F;
         float var17 = 0.0625F;
         float var18 = 0.09375F;
         float var19 = 1.0F;
         float var20 = 0.5F;
         float var21 = 0.5F;
         GlStateManager.func_179114_b(180.0F - this.field_76990_c.field_78735_i, 0.0F, 1.0F, 0.0F);
         GlStateManager.func_179114_b((float)(this.field_76990_c.field_78733_k.field_74320_O == 2 ? -1 : 1) * -this.field_76990_c.field_78732_j, 1.0F, 0.0F, 0.0F);
         if (this.field_188301_f) {
            GlStateManager.func_179142_g();
            GlStateManager.func_187431_e(this.func_188298_c(var1));
         }

         var12.func_181668_a(7, DefaultVertexFormats.field_181710_j);
         var12.func_181662_b(-0.5D, -0.5D, 0.0D).func_187315_a(0.03125D, 0.09375D).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
         var12.func_181662_b(0.5D, -0.5D, 0.0D).func_187315_a(0.0625D, 0.09375D).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
         var12.func_181662_b(0.5D, 0.5D, 0.0D).func_187315_a(0.0625D, 0.0625D).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
         var12.func_181662_b(-0.5D, 0.5D, 0.0D).func_187315_a(0.03125D, 0.0625D).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
         var11.func_78381_a();
         if (this.field_188301_f) {
            GlStateManager.func_187417_n();
            GlStateManager.func_179119_h();
         }

         GlStateManager.func_179101_C();
         GlStateManager.func_179121_F();
         int var22 = var10.func_184591_cq() == EnumHandSide.RIGHT ? 1 : -1;
         ItemStack var23 = var10.func_184614_ca();
         if (var23.func_77973_b() != Items.field_151112_aM) {
            var22 = -var22;
         }

         float var24 = var10.func_70678_g(var9);
         float var25 = MathHelper.func_76126_a(MathHelper.func_76129_c(var24) * 3.1415927F);
         float var26 = (var10.field_70760_ar + (var10.field_70761_aq - var10.field_70760_ar) * var9) * 0.017453292F;
         double var27 = (double)MathHelper.func_76126_a(var26);
         double var29 = (double)MathHelper.func_76134_b(var26);
         double var31 = (double)var22 * 0.35D;
         double var33 = 0.8D;
         double var35;
         double var37;
         double var39;
         double var41;
         double var43;
         if ((this.field_76990_c.field_78733_k == null || this.field_76990_c.field_78733_k.field_74320_O <= 0) && var10 == Minecraft.func_71410_x().field_71439_g) {
            var43 = this.field_76990_c.field_78733_k.field_74334_X;
            var43 /= 100.0D;
            Vec3d var45 = new Vec3d((double)var22 * -0.36D * var43, -0.045D * var43, 0.4D);
            var45 = var45.func_178789_a(-(var10.field_70127_C + (var10.field_70125_A - var10.field_70127_C) * var9) * 0.017453292F);
            var45 = var45.func_178785_b(-(var10.field_70126_B + (var10.field_70177_z - var10.field_70126_B) * var9) * 0.017453292F);
            var45 = var45.func_178785_b(var25 * 0.5F);
            var45 = var45.func_178789_a(-var25 * 0.7F);
            var35 = var10.field_70169_q + (var10.field_70165_t - var10.field_70169_q) * (double)var9 + var45.field_72450_a;
            var37 = var10.field_70167_r + (var10.field_70163_u - var10.field_70167_r) * (double)var9 + var45.field_72448_b;
            var39 = var10.field_70166_s + (var10.field_70161_v - var10.field_70166_s) * (double)var9 + var45.field_72449_c;
            var41 = (double)var10.func_70047_e();
         } else {
            var35 = var10.field_70169_q + (var10.field_70165_t - var10.field_70169_q) * (double)var9 - var29 * var31 - var27 * 0.8D;
            var37 = var10.field_70167_r + (double)var10.func_70047_e() + (var10.field_70163_u - var10.field_70167_r) * (double)var9 - 0.45D;
            var39 = var10.field_70166_s + (var10.field_70161_v - var10.field_70166_s) * (double)var9 - var27 * var31 + var29 * 0.8D;
            var41 = var10.func_70093_af() ? -0.1875D : 0.0D;
         }

         var43 = var1.field_70169_q + (var1.field_70165_t - var1.field_70169_q) * (double)var9;
         double var58 = var1.field_70167_r + (var1.field_70163_u - var1.field_70167_r) * (double)var9 + 0.25D;
         double var47 = var1.field_70166_s + (var1.field_70161_v - var1.field_70166_s) * (double)var9;
         double var49 = (double)((float)(var35 - var43));
         double var51 = (double)((float)(var37 - var58)) + var41;
         double var53 = (double)((float)(var39 - var47));
         GlStateManager.func_179090_x();
         GlStateManager.func_179140_f();
         var12.func_181668_a(3, DefaultVertexFormats.field_181706_f);
         boolean var55 = true;

         for(int var56 = 0; var56 <= 16; ++var56) {
            float var57 = (float)var56 / 16.0F;
            var12.func_181662_b(var2 + var49 * (double)var57, var4 + var51 * (double)(var57 * var57 + var57) * 0.5D + 0.25D, var6 + var53 * (double)var57).func_181669_b(0, 0, 0, 255).func_181675_d();
         }

         var11.func_78381_a();
         GlStateManager.func_179145_e();
         GlStateManager.func_179098_w();
         super.func_76986_a(var1, var2, var4, var6, var8, var9);
      }
   }

   protected ResourceLocation func_110775_a(EntityFishHook var1) {
      return field_110792_a;
   }
}
