package net.minecraft.client.shader;

import java.nio.ByteBuffer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureUtil;
import org.lwjgl.opengl.GL11;

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
         GL11.glEnable(2929);
         if (this.field_147616_f >= 0) {
            this.func_147608_a();
         }

         this.func_147605_b(var1, var2);
         this.func_147611_b();
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
         GL11.glBindTexture(3553, this.field_147617_g);
         GL11.glTexImage2D(3553, 0, 32856, this.field_147622_a, this.field_147620_b, 0, 6408, 5121, (ByteBuffer)null);
         OpenGlHelper.func_153171_g(OpenGlHelper.field_153198_e, this.field_147616_f);
         OpenGlHelper.func_153188_a(OpenGlHelper.field_153198_e, OpenGlHelper.field_153200_g, 3553, this.field_147617_g, 0);
         if (this.field_147619_e) {
            OpenGlHelper.func_153176_h(OpenGlHelper.field_153199_f, this.field_147624_h);
            OpenGlHelper.func_153186_a(OpenGlHelper.field_153199_f, 33190, this.field_147622_a, this.field_147620_b);
            OpenGlHelper.func_153190_b(OpenGlHelper.field_153198_e, OpenGlHelper.field_153201_h, OpenGlHelper.field_153199_f, this.field_147624_h);
         }

         this.func_147614_f();
         this.func_147606_d();
      }
   }

   public void func_147607_a(int var1) {
      if (OpenGlHelper.func_148822_b()) {
         this.field_147623_j = var1;
         GL11.glBindTexture(3553, this.field_147617_g);
         GL11.glTexParameterf(3553, 10241, (float)var1);
         GL11.glTexParameterf(3553, 10240, (float)var1);
         GL11.glTexParameterf(3553, 10242, 10496.0F);
         GL11.glTexParameterf(3553, 10243, 10496.0F);
         GL11.glBindTexture(3553, 0);
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
         GL11.glBindTexture(3553, this.field_147617_g);
      }
   }

   public void func_147606_d() {
      if (OpenGlHelper.func_148822_b()) {
         GL11.glBindTexture(3553, 0);
      }
   }

   public void func_147610_a(boolean var1) {
      if (OpenGlHelper.func_148822_b()) {
         OpenGlHelper.func_153171_g(OpenGlHelper.field_153198_e, this.field_147616_f);
         if (var1) {
            GL11.glViewport(0, 0, this.field_147621_c, this.field_147618_d);
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
      if (OpenGlHelper.func_148822_b()) {
         GL11.glColorMask(true, true, true, false);
         GL11.glDisable(2929);
         GL11.glDepthMask(false);
         GL11.glMatrixMode(5889);
         GL11.glLoadIdentity();
         GL11.glOrtho(0.0, (double)var1, (double)var2, 0.0, 1000.0, 3000.0);
         GL11.glMatrixMode(5888);
         GL11.glLoadIdentity();
         GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
         GL11.glViewport(0, 0, var1, var2);
         GL11.glEnable(3553);
         GL11.glDisable(2896);
         GL11.glDisable(3008);
         GL11.glDisable(3042);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         GL11.glEnable(2903);
         this.func_147612_c();
         float var3 = (float)var1;
         float var4 = (float)var2;
         float var5 = (float)this.field_147621_c / (float)this.field_147622_a;
         float var6 = (float)this.field_147618_d / (float)this.field_147620_b;
         Tessellator var7 = Tessellator.field_78398_a;
         var7.func_78382_b();
         var7.func_78378_d(-1);
         var7.func_78374_a(0.0, (double)var4, 0.0, 0.0, 0.0);
         var7.func_78374_a((double)var3, (double)var4, 0.0, (double)var5, 0.0);
         var7.func_78374_a((double)var3, 0.0, 0.0, (double)var5, (double)var6);
         var7.func_78374_a(0.0, 0.0, 0.0, 0.0, (double)var6);
         var7.func_78381_a();
         this.func_147606_d();
         GL11.glDepthMask(true);
         GL11.glColorMask(true, true, true, true);
      }
   }

   public void func_147614_f() {
      this.func_147610_a(true);
      GL11.glClearColor(this.field_147625_i[0], this.field_147625_i[1], this.field_147625_i[2], this.field_147625_i[3]);
      int var1 = 16384;
      if (this.field_147619_e) {
         GL11.glClearDepth(1.0);
         var1 |= 256;
      }

      GL11.glClear(var1);
      this.func_147609_e();
   }
}
