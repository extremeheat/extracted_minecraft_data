package net.minecraft.client.shader;

import java.nio.IntBuffer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class Framebuffer {
   public int field_147622_a;
   public int field_147620_b;
   public int field_147621_c;
   public int field_147618_d;
   public boolean field_147619_e;
   public int field_147616_f;
   public int field_147617_g;
   public int field_147624_h;
   public float[] field_147625_i;
   public int field_147623_j;

   public Framebuffer(int var1, int var2, boolean var3) {
      super();
      this.field_147619_e = var3;
      this.field_147616_f = -1;
      this.field_147617_g = -1;
      this.field_147624_h = -1;
      this.field_147625_i = new float[4];
      this.field_147625_i[0] = 1.0F;
      this.field_147625_i[1] = 1.0F;
      this.field_147625_i[2] = 1.0F;
      this.field_147625_i[3] = 0.0F;
      this.func_147613_a(var1, var2);
   }

   public void func_147613_a(int var1, int var2) {
      if (!OpenGlHelper.func_148822_b()) {
         this.field_147621_c = var1;
         this.field_147618_d = var2;
      } else {
         GlStateManager.func_179126_j();
         if (this.field_147616_f >= 0) {
            this.func_147608_a();
         }

         this.func_147605_b(var1, var2);
         OpenGlHelper.func_153171_g(OpenGlHelper.field_153198_e, 0);
      }
   }

   public void func_147608_a() {
      if (OpenGlHelper.func_148822_b()) {
         this.func_147606_d();
         this.func_147609_e();
         if (this.field_147624_h > -1) {
            OpenGlHelper.func_153184_g(this.field_147624_h);
            this.field_147624_h = -1;
         }

         if (this.field_147617_g > -1) {
            TextureUtil.func_147942_a(this.field_147617_g);
            this.field_147617_g = -1;
         }

         if (this.field_147616_f > -1) {
            OpenGlHelper.func_153171_g(OpenGlHelper.field_153198_e, 0);
            OpenGlHelper.func_153174_h(this.field_147616_f);
            this.field_147616_f = -1;
         }

      }
   }

   public void func_147605_b(int var1, int var2) {
      this.field_147621_c = var1;
      this.field_147618_d = var2;
      this.field_147622_a = var1;
      this.field_147620_b = var2;
      if (!OpenGlHelper.func_148822_b()) {
         this.func_147614_f();
      } else {
         this.field_147616_f = OpenGlHelper.func_153165_e();
         this.field_147617_g = TextureUtil.func_110996_a();
         if (this.field_147619_e) {
            this.field_147624_h = OpenGlHelper.func_153185_f();
         }

         this.func_147607_a(9728);
         GlStateManager.func_179144_i(this.field_147617_g);
         GlStateManager.func_187419_a(3553, 0, 32856, this.field_147622_a, this.field_147620_b, 0, 6408, 5121, (IntBuffer)null);
         OpenGlHelper.func_153171_g(OpenGlHelper.field_153198_e, this.field_147616_f);
         OpenGlHelper.func_153188_a(OpenGlHelper.field_153198_e, OpenGlHelper.field_153200_g, 3553, this.field_147617_g, 0);
         if (this.field_147619_e) {
            OpenGlHelper.func_153176_h(OpenGlHelper.field_153199_f, this.field_147624_h);
            OpenGlHelper.func_153186_a(OpenGlHelper.field_153199_f, 33190, this.field_147622_a, this.field_147620_b);
            OpenGlHelper.func_153190_b(OpenGlHelper.field_153198_e, OpenGlHelper.field_153201_h, OpenGlHelper.field_153199_f, this.field_147624_h);
         }

         this.func_147611_b();
         this.func_147614_f();
         this.func_147606_d();
      }
   }

   public void func_147607_a(int var1) {
      if (OpenGlHelper.func_148822_b()) {
         this.field_147623_j = var1;
         GlStateManager.func_179144_i(this.field_147617_g);
         GlStateManager.func_187421_b(3553, 10241, var1);
         GlStateManager.func_187421_b(3553, 10240, var1);
         GlStateManager.func_187421_b(3553, 10242, 10496);
         GlStateManager.func_187421_b(3553, 10243, 10496);
         GlStateManager.func_179144_i(0);
      }

   }

   public void func_147611_b() {
      int var1 = OpenGlHelper.func_153167_i(OpenGlHelper.field_153198_e);
      if (var1 != OpenGlHelper.field_153202_i) {
         if (var1 == OpenGlHelper.field_153203_j) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT");
         } else if (var1 == OpenGlHelper.field_153204_k) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT");
         } else if (var1 == OpenGlHelper.field_153205_l) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER");
         } else if (var1 == OpenGlHelper.field_153206_m) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER");
         } else {
            throw new RuntimeException("glCheckFramebufferStatus returned unknown status:" + var1);
         }
      }
   }

   public void func_147612_c() {
      if (OpenGlHelper.func_148822_b()) {
         GlStateManager.func_179144_i(this.field_147617_g);
      }

   }

   public void func_147606_d() {
      if (OpenGlHelper.func_148822_b()) {
         GlStateManager.func_179144_i(0);
      }

   }

   public void func_147610_a(boolean var1) {
      if (OpenGlHelper.func_148822_b()) {
         OpenGlHelper.func_153171_g(OpenGlHelper.field_153198_e, this.field_147616_f);
         if (var1) {
            GlStateManager.func_179083_b(0, 0, this.field_147621_c, this.field_147618_d);
         }
      }

   }

   public void func_147609_e() {
      if (OpenGlHelper.func_148822_b()) {
         OpenGlHelper.func_153171_g(OpenGlHelper.field_153198_e, 0);
      }

   }

   public void func_147604_a(float var1, float var2, float var3, float var4) {
      this.field_147625_i[0] = var1;
      this.field_147625_i[1] = var2;
      this.field_147625_i[2] = var3;
      this.field_147625_i[3] = var4;
   }

   public void func_147615_c(int var1, int var2) {
      this.func_178038_a(var1, var2, true);
   }

   public void func_178038_a(int var1, int var2, boolean var3) {
      if (OpenGlHelper.func_148822_b()) {
         GlStateManager.func_179135_a(true, true, true, false);
         GlStateManager.func_179097_i();
         GlStateManager.func_179132_a(false);
         GlStateManager.func_179128_n(5889);
         GlStateManager.func_179096_D();
         GlStateManager.func_179130_a(0.0D, (double)var1, (double)var2, 0.0D, 1000.0D, 3000.0D);
         GlStateManager.func_179128_n(5888);
         GlStateManager.func_179096_D();
         GlStateManager.func_179109_b(0.0F, 0.0F, -2000.0F);
         GlStateManager.func_179083_b(0, 0, var1, var2);
         GlStateManager.func_179098_w();
         GlStateManager.func_179140_f();
         GlStateManager.func_179118_c();
         if (var3) {
            GlStateManager.func_179084_k();
            GlStateManager.func_179142_g();
         }

         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         this.func_147612_c();
         float var4 = (float)var1;
         float var5 = (float)var2;
         float var6 = (float)this.field_147621_c / (float)this.field_147622_a;
         float var7 = (float)this.field_147618_d / (float)this.field_147620_b;
         Tessellator var8 = Tessellator.func_178181_a();
         BufferBuilder var9 = var8.func_178180_c();
         var9.func_181668_a(7, DefaultVertexFormats.field_181709_i);
         var9.func_181662_b(0.0D, (double)var5, 0.0D).func_187315_a(0.0D, 0.0D).func_181669_b(255, 255, 255, 255).func_181675_d();
         var9.func_181662_b((double)var4, (double)var5, 0.0D).func_187315_a((double)var6, 0.0D).func_181669_b(255, 255, 255, 255).func_181675_d();
         var9.func_181662_b((double)var4, 0.0D, 0.0D).func_187315_a((double)var6, (double)var7).func_181669_b(255, 255, 255, 255).func_181675_d();
         var9.func_181662_b(0.0D, 0.0D, 0.0D).func_187315_a(0.0D, (double)var7).func_181669_b(255, 255, 255, 255).func_181675_d();
         var8.func_78381_a();
         this.func_147606_d();
         GlStateManager.func_179132_a(true);
         GlStateManager.func_179135_a(true, true, true, true);
      }
   }

   public void func_147614_f() {
      this.func_147610_a(true);
      GlStateManager.func_179082_a(this.field_147625_i[0], this.field_147625_i[1], this.field_147625_i[2], this.field_147625_i[3]);
      int var1 = 16384;
      if (this.field_147619_e) {
         GlStateManager.func_179151_a(1.0D);
         var1 |= 256;
      }

      GlStateManager.func_179086_m(var1);
      this.func_147609_e();
   }
}
