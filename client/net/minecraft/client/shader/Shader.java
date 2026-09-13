package net.minecraft.client.shader;

import com.google.common.collect.Lists;
import java.util.List;
import javax.vecmath.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.IResourceManager;
import org.lwjgl.opengl.GL11;

public class Shader {
   private final ShaderManager field_148051_c;
   public final Framebuffer field_148052_a;
   public final Framebuffer field_148050_b;
   private final List field_148048_d = Lists.newArrayList();
   private final List field_148049_e = Lists.newArrayList();
   private final List field_148046_f = Lists.newArrayList();
   private final List field_148047_g = Lists.newArrayList();
   private Matrix4f field_148053_h;

   public Shader(IResourceManager var1, String var2, Framebuffer var3, Framebuffer var4) {
      super();
      this.field_148051_c = new ShaderManager(var1, var2);
      this.field_148052_a = var3;
      this.field_148050_b = var4;
   }

   public void func_148044_b() {
      this.field_148051_c.func_147988_a();
   }

   public void func_148041_a(String var1, Object var2, int var3, int var4) {
      this.field_148049_e.add(this.field_148049_e.size(), var1);
      this.field_148048_d.add(this.field_148048_d.size(), var2);
      this.field_148046_f.add(this.field_148046_f.size(), var3);
      this.field_148047_g.add(this.field_148047_g.size(), var4);
   }

   private void func_148040_d() {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glDisable(3042);
      GL11.glDisable(2929);
      GL11.glDisable(3008);
      GL11.glDisable(2912);
      GL11.glDisable(2896);
      GL11.glDisable(2903);
      GL11.glEnable(3553);
      GL11.glBindTexture(3553, 0);
   }

   public void func_148045_a(Matrix4f var1) {
      this.field_148053_h = var1;
   }

   public void func_148042_a(float var1) {
      this.func_148040_d();
      this.field_148052_a.func_147609_e();
      float var2 = (float)this.field_148050_b.field_147622_a;
      float var3 = (float)this.field_148050_b.field_147620_b;
      GL11.glViewport(0, 0, (int)var2, (int)var3);
      this.field_148051_c.func_147992_a("DiffuseSampler", this.field_148052_a);

      for(int var4 = 0; var4 < this.field_148048_d.size(); ++var4) {
         this.field_148051_c.func_147992_a((String)this.field_148049_e.get(var4), this.field_148048_d.get(var4));
         this.field_148051_c
            .func_147984_b("AuxSize" + var4)
            .func_148087_a((float)((Integer)this.field_148046_f.get(var4)).intValue(), (float)((Integer)this.field_148047_g.get(var4)).intValue());
      }

      this.field_148051_c.func_147984_b("ProjMat").func_148088_a(this.field_148053_h);
      this.field_148051_c.func_147984_b("InSize").func_148087_a((float)this.field_148052_a.field_147622_a, (float)this.field_148052_a.field_147620_b);
      this.field_148051_c.func_147984_b("OutSize").func_148087_a(var2, var3);
      this.field_148051_c.func_147984_b("Time").func_148090_a(var1);
      Minecraft var8 = Minecraft.func_71410_x();
      this.field_148051_c.func_147984_b("ScreenSize").func_148087_a((float)var8.field_71443_c, (float)var8.field_71440_d);
      this.field_148051_c.func_147995_c();
      this.field_148050_b.func_147614_f();
      this.field_148050_b.func_147610_a(false);
      GL11.glDepthMask(false);
      GL11.glColorMask(true, true, true, false);
      Tessellator var5 = Tessellator.field_78398_a;
      var5.func_78382_b();
      var5.func_78378_d(-1);
      var5.func_78377_a(0.0, (double)var3, 500.0);
      var5.func_78377_a((double)var2, (double)var3, 500.0);
      var5.func_78377_a((double)var2, 0.0, 500.0);
      var5.func_78377_a(0.0, 0.0, 500.0);
      var5.func_78381_a();
      GL11.glDepthMask(true);
      GL11.glColorMask(true, true, true, true);
      this.field_148051_c.func_147993_b();
      this.field_148050_b.func_147609_e();
      this.field_148052_a.func_147606_d();

      for(Object var7 : this.field_148048_d) {
         if (var7 instanceof Framebuffer) {
            ((Framebuffer)var7).func_147606_d();
         }
      }
   }

   public ShaderManager func_148043_c() {
      return this.field_148051_c;
   }
}
