package net.minecraft.client.renderer;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.LWJGLMemoryUntracker;
import net.minecraft.util.Util;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.system.MemoryUtil;

public class GlStateManager {
   private static final FloatBuffer field_187450_a = (FloatBuffer)Util.func_200696_a(MemoryUtil.memAllocFloat(16), (var0) -> {
      LWJGLMemoryUntracker.func_197933_a(MemoryUtil.memAddress(var0));
   });
   private static final FloatBuffer field_187451_b = (FloatBuffer)Util.func_200696_a(MemoryUtil.memAllocFloat(4), (var0) -> {
      LWJGLMemoryUntracker.func_197933_a(MemoryUtil.memAddress(var0));
   });
   private static final GlStateManager.AlphaState field_199299_c = new GlStateManager.AlphaState();
   private static final GlStateManager.BooleanState field_199300_d = new GlStateManager.BooleanState(2896);
   private static final GlStateManager.BooleanState[] field_199301_e = (GlStateManager.BooleanState[])IntStream.range(0, 8).mapToObj((var0) -> {
      return new GlStateManager.BooleanState(16384 + var0);
   }).toArray((var0) -> {
      return new GlStateManager.BooleanState[var0];
   });
   private static final GlStateManager.ColorMaterialState field_199302_f = new GlStateManager.ColorMaterialState();
   private static final GlStateManager.BlendState field_179157_e = new GlStateManager.BlendState();
   private static final GlStateManager.DepthState field_179154_f = new GlStateManager.DepthState();
   private static final GlStateManager.FogState field_179155_g = new GlStateManager.FogState();
   private static final GlStateManager.CullState field_179167_h = new GlStateManager.CullState();
   private static final GlStateManager.PolygonOffsetState field_179168_i = new GlStateManager.PolygonOffsetState();
   private static final GlStateManager.ColorLogicState field_179165_j = new GlStateManager.ColorLogicState();
   private static final GlStateManager.TexGenState field_179166_k = new GlStateManager.TexGenState();
   private static final GlStateManager.ClearState field_179163_l = new GlStateManager.ClearState();
   private static final GlStateManager.StencilState field_179164_m = new GlStateManager.StencilState();
   private static final GlStateManager.BooleanState field_199303_p = new GlStateManager.BooleanState(2977);
   private static int field_179162_o;
   private static final GlStateManager.TextureState[] field_199304_r = (GlStateManager.TextureState[])IntStream.range(0, 8).mapToObj((var0) -> {
      return new GlStateManager.TextureState();
   }).toArray((var0) -> {
      return new GlStateManager.TextureState[var0];
   });
   private static int field_179173_q = 7425;
   private static final GlStateManager.BooleanState field_199305_t = new GlStateManager.BooleanState(32826);
   private static final GlStateManager.ColorMask field_199306_u = new GlStateManager.ColorMask();
   private static final GlStateManager.Color field_199307_v = new GlStateManager.Color();

   public static void func_179123_a() {
      GL11.glPushAttrib(8256);
   }

   public static void func_179099_b() {
      GL11.glPopAttrib();
   }

   public static void func_179118_c() {
      field_199299_c.field_179208_a.func_179198_a();
   }

   public static void func_179141_d() {
      field_199299_c.field_179208_a.func_179200_b();
   }

   public static void func_179092_a(int var0, float var1) {
      if (var0 != field_199299_c.field_179206_b || var1 != field_199299_c.field_179207_c) {
         field_199299_c.field_179206_b = var0;
         field_199299_c.field_179207_c = var1;
         GL11.glAlphaFunc(var0, var1);
      }

   }

   public static void func_179145_e() {
      field_199300_d.func_179200_b();
   }

   public static void func_179140_f() {
      field_199300_d.func_179198_a();
   }

   public static void func_179085_a(int var0) {
      field_199301_e[var0].func_179200_b();
   }

   public static void func_179122_b(int var0) {
      field_199301_e[var0].func_179198_a();
   }

   public static void func_179142_g() {
      field_199302_f.field_179191_a.func_179200_b();
   }

   public static void func_179119_h() {
      field_199302_f.field_179191_a.func_179198_a();
   }

   public static void func_179104_a(int var0, int var1) {
      if (var0 != field_199302_f.field_179189_b || var1 != field_199302_f.field_179190_c) {
         field_199302_f.field_179189_b = var0;
         field_199302_f.field_179190_c = var1;
         GL11.glColorMaterial(var0, var1);
      }

   }

   public static void func_187438_a(int var0, int var1, FloatBuffer var2) {
      GL11.glLightfv(var0, var1, var2);
   }

   public static void func_187424_a(int var0, FloatBuffer var1) {
      GL11.glLightModelfv(var0, var1);
   }

   public static void func_187432_a(float var0, float var1, float var2) {
      GL11.glNormal3f(var0, var1, var2);
   }

   public static void func_179097_i() {
      field_179154_f.field_179052_a.func_179198_a();
   }

   public static void func_179126_j() {
      field_179154_f.field_179052_a.func_179200_b();
   }

   public static void func_179143_c(int var0) {
      if (var0 != field_179154_f.field_179051_c) {
         field_179154_f.field_179051_c = var0;
         GL11.glDepthFunc(var0);
      }

   }

   public static void func_179132_a(boolean var0) {
      if (var0 != field_179154_f.field_179050_b) {
         field_179154_f.field_179050_b = var0;
         GL11.glDepthMask(var0);
      }

   }

   public static void func_179084_k() {
      field_179157_e.field_179213_a.func_179198_a();
   }

   public static void func_179147_l() {
      field_179157_e.field_179213_a.func_179200_b();
   }

   public static void func_187401_a(GlStateManager.SourceFactor var0, GlStateManager.DestFactor var1) {
      func_179112_b(var0.field_187395_p, var1.field_187345_o);
   }

   public static void func_179112_b(int var0, int var1) {
      if (var0 != field_179157_e.field_179211_b || var1 != field_179157_e.field_179212_c) {
         field_179157_e.field_179211_b = var0;
         field_179157_e.field_179212_c = var1;
         GL11.glBlendFunc(var0, var1);
      }

   }

   public static void func_187428_a(GlStateManager.SourceFactor var0, GlStateManager.DestFactor var1, GlStateManager.SourceFactor var2, GlStateManager.DestFactor var3) {
      func_179120_a(var0.field_187395_p, var1.field_187345_o, var2.field_187395_p, var3.field_187345_o);
   }

   public static void func_179120_a(int var0, int var1, int var2, int var3) {
      if (var0 != field_179157_e.field_179211_b || var1 != field_179157_e.field_179212_c || var2 != field_179157_e.field_179209_d || var3 != field_179157_e.field_179210_e) {
         field_179157_e.field_179211_b = var0;
         field_179157_e.field_179212_c = var1;
         field_179157_e.field_179209_d = var2;
         field_179157_e.field_179210_e = var3;
         OpenGlHelper.func_148821_a(var0, var1, var2, var3);
      }

   }

   public static void func_187398_d(int var0) {
      GL14.glBlendEquation(var0);
   }

   public static void func_187431_e(int var0) {
      field_187451_b.put(0, (float)(var0 >> 16 & 255) / 255.0F);
      field_187451_b.put(1, (float)(var0 >> 8 & 255) / 255.0F);
      field_187451_b.put(2, (float)(var0 >> 0 & 255) / 255.0F);
      field_187451_b.put(3, (float)(var0 >> 24 & 255) / 255.0F);
      func_187448_b(8960, 8705, field_187451_b);
      func_187399_a(8960, 8704, 34160);
      func_187399_a(8960, 34161, 7681);
      func_187399_a(8960, 34176, 34166);
      func_187399_a(8960, 34192, 768);
      func_187399_a(8960, 34162, 7681);
      func_187399_a(8960, 34184, 5890);
      func_187399_a(8960, 34200, 770);
   }

   public static void func_187417_n() {
      func_187399_a(8960, 8704, 8448);
      func_187399_a(8960, 34161, 8448);
      func_187399_a(8960, 34162, 8448);
      func_187399_a(8960, 34176, 5890);
      func_187399_a(8960, 34184, 5890);
      func_187399_a(8960, 34192, 768);
      func_187399_a(8960, 34200, 770);
   }

   public static void func_179127_m() {
      field_179155_g.field_179049_a.func_179200_b();
   }

   public static void func_179106_n() {
      field_179155_g.field_179049_a.func_179198_a();
   }

   public static void func_187430_a(GlStateManager.FogMode var0) {
      func_179093_d(var0.field_187351_d);
   }

   private static void func_179093_d(int var0) {
      if (var0 != field_179155_g.field_179047_b) {
         field_179155_g.field_179047_b = var0;
         GL11.glFogi(2917, var0);
      }

   }

   public static void func_179095_a(float var0) {
      if (var0 != field_179155_g.field_179048_c) {
         field_179155_g.field_179048_c = var0;
         GL11.glFogf(2914, var0);
      }

   }

   public static void func_179102_b(float var0) {
      if (var0 != field_179155_g.field_179045_d) {
         field_179155_g.field_179045_d = var0;
         GL11.glFogf(2915, var0);
      }

   }

   public static void func_179153_c(float var0) {
      if (var0 != field_179155_g.field_179046_e) {
         field_179155_g.field_179046_e = var0;
         GL11.glFogf(2916, var0);
      }

   }

   public static void func_187402_b(int var0, FloatBuffer var1) {
      GL11.glFogfv(var0, var1);
   }

   public static void func_187412_c(int var0, int var1) {
      GL11.glFogi(var0, var1);
   }

   public static void func_179089_o() {
      field_179167_h.field_179054_a.func_179200_b();
   }

   public static void func_179129_p() {
      field_179167_h.field_179054_a.func_179198_a();
   }

   public static void func_187407_a(GlStateManager.CullFace var0) {
      func_179107_e(var0.field_187328_d);
   }

   private static void func_179107_e(int var0) {
      if (var0 != field_179167_h.field_179053_b) {
         field_179167_h.field_179053_b = var0;
         GL11.glCullFace(var0);
      }

   }

   public static void func_187409_d(int var0, int var1) {
      GL11.glPolygonMode(var0, var1);
   }

   public static void func_179088_q() {
      field_179168_i.field_179044_a.func_179200_b();
   }

   public static void func_179113_r() {
      field_179168_i.field_179044_a.func_179198_a();
   }

   public static void func_179136_a(float var0, float var1) {
      if (var0 != field_179168_i.field_179043_c || var1 != field_179168_i.field_179041_d) {
         field_179168_i.field_179043_c = var0;
         field_179168_i.field_179041_d = var1;
         GL11.glPolygonOffset(var0, var1);
      }

   }

   public static void func_179115_u() {
      field_179165_j.field_179197_a.func_179200_b();
   }

   public static void func_179134_v() {
      field_179165_j.field_179197_a.func_179198_a();
   }

   public static void func_187422_a(GlStateManager.LogicOp var0) {
      func_179116_f(var0.field_187370_q);
   }

   public static void func_179116_f(int var0) {
      if (var0 != field_179165_j.field_179196_b) {
         field_179165_j.field_179196_b = var0;
         GL11.glLogicOp(var0);
      }

   }

   public static void func_179087_a(GlStateManager.TexGen var0) {
      func_179125_c(var0).field_179067_a.func_179200_b();
   }

   public static void func_179100_b(GlStateManager.TexGen var0) {
      func_179125_c(var0).field_179067_a.func_179198_a();
   }

   public static void func_179149_a(GlStateManager.TexGen var0, int var1) {
      GlStateManager.TexGenCoord var2 = func_179125_c(var0);
      if (var1 != var2.field_179066_c) {
         var2.field_179066_c = var1;
         GL11.glTexGeni(var2.field_179065_b, 9472, var1);
      }

   }

   public static void func_179105_a(GlStateManager.TexGen var0, int var1, FloatBuffer var2) {
      GL11.glTexGenfv(func_179125_c(var0).field_179065_b, var1, var2);
   }

   private static GlStateManager.TexGenCoord func_179125_c(GlStateManager.TexGen var0) {
      switch(var0) {
      case S:
         return field_179166_k.field_179064_a;
      case T:
         return field_179166_k.field_179062_b;
      case R:
         return field_179166_k.field_179063_c;
      case Q:
         return field_179166_k.field_179061_d;
      default:
         return field_179166_k.field_179064_a;
      }
   }

   public static void func_179138_g(int var0) {
      if (field_179162_o != var0 - OpenGlHelper.field_77478_a) {
         field_179162_o = var0 - OpenGlHelper.field_77478_a;
         OpenGlHelper.func_77473_a(var0);
      }

   }

   public static void func_179098_w() {
      field_199304_r[field_179162_o].field_179060_a.func_179200_b();
   }

   public static void func_179090_x() {
      field_199304_r[field_179162_o].field_179060_a.func_179198_a();
   }

   public static void func_187448_b(int var0, int var1, FloatBuffer var2) {
      GL11.glTexEnvfv(var0, var1, var2);
   }

   public static void func_187399_a(int var0, int var1, int var2) {
      GL11.glTexEnvi(var0, var1, var2);
   }

   public static void func_187436_a(int var0, int var1, float var2) {
      GL11.glTexEnvf(var0, var1, var2);
   }

   public static void func_187403_b(int var0, int var1, float var2) {
      GL11.glTexParameterf(var0, var1, var2);
   }

   public static void func_187421_b(int var0, int var1, int var2) {
      GL11.glTexParameteri(var0, var1, var2);
   }

   public static int func_187411_c(int var0, int var1, int var2) {
      return GL11.glGetTexLevelParameteri(var0, var1, var2);
   }

   public static int func_179146_y() {
      return GL11.glGenTextures();
   }

   public static void func_179150_h(int var0) {
      GL11.glDeleteTextures(var0);
      GlStateManager.TextureState[] var1 = field_199304_r;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         GlStateManager.TextureState var4 = var1[var3];
         if (var4.field_179059_b == var0) {
            var4.field_179059_b = -1;
         }
      }

   }

   public static void func_179144_i(int var0) {
      if (var0 != field_199304_r[field_179162_o].field_179059_b) {
         field_199304_r[field_179162_o].field_179059_b = var0;
         GL11.glBindTexture(3553, var0);
      }

   }

   public static void func_187419_a(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, @Nullable IntBuffer var8) {
      GL11.glTexImage2D(var0, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public static void func_199298_a(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, long var8) {
      GL11.glTexSubImage2D(var0, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public static void func_199295_a(int var0, int var1, int var2, int var3, long var4) {
      GL11.glGetTexImage(var0, var1, var2, var3, var4);
   }

   public static void func_179108_z() {
      field_199303_p.func_179200_b();
   }

   public static void func_179133_A() {
      field_199303_p.func_179198_a();
   }

   public static void func_179103_j(int var0) {
      if (var0 != field_179173_q) {
         field_179173_q = var0;
         GL11.glShadeModel(var0);
      }

   }

   public static void func_179091_B() {
      field_199305_t.func_179200_b();
   }

   public static void func_179101_C() {
      field_199305_t.func_179198_a();
   }

   public static void func_179083_b(int var0, int var1, int var2, int var3) {
      GlStateManager.Viewport.INSTANCE.field_199289_b = var0;
      GlStateManager.Viewport.INSTANCE.field_199290_c = var1;
      GlStateManager.Viewport.INSTANCE.field_199291_d = var2;
      GlStateManager.Viewport.INSTANCE.field_199292_e = var3;
      GL11.glViewport(var0, var1, var2, var3);
   }

   public static void func_179135_a(boolean var0, boolean var1, boolean var2, boolean var3) {
      if (var0 != field_199306_u.field_179188_a || var1 != field_199306_u.field_179186_b || var2 != field_199306_u.field_179187_c || var3 != field_199306_u.field_179185_d) {
         field_199306_u.field_179188_a = var0;
         field_199306_u.field_179186_b = var1;
         field_199306_u.field_179187_c = var2;
         field_199306_u.field_179185_d = var3;
         GL11.glColorMask(var0, var1, var2, var3);
      }

   }

   public static void func_179151_a(double var0) {
      if (var0 != field_179163_l.field_179205_a) {
         field_179163_l.field_179205_a = var0;
         GL11.glClearDepth(var0);
      }

   }

   public static void func_179082_a(float var0, float var1, float var2, float var3) {
      if (var0 != field_179163_l.field_179203_b.field_179195_a || var1 != field_179163_l.field_179203_b.field_179193_b || var2 != field_179163_l.field_179203_b.field_179194_c || var3 != field_179163_l.field_179203_b.field_179192_d) {
         field_179163_l.field_179203_b.field_179195_a = var0;
         field_179163_l.field_179203_b.field_179193_b = var1;
         field_179163_l.field_179203_b.field_179194_c = var2;
         field_179163_l.field_179203_b.field_179192_d = var3;
         GL11.glClearColor(var0, var1, var2, var3);
      }

   }

   public static void func_179086_m(int var0) {
      GL11.glClear(var0);
      if (Minecraft.field_142025_a) {
         func_187434_L();
      }

   }

   public static void func_179128_n(int var0) {
      GL11.glMatrixMode(var0);
   }

   public static void func_179096_D() {
      GL11.glLoadIdentity();
   }

   public static void func_179094_E() {
      GL11.glPushMatrix();
   }

   public static void func_179121_F() {
      GL11.glPopMatrix();
   }

   public static void func_179111_a(int var0, FloatBuffer var1) {
      GL11.glGetFloatv(var0, var1);
   }

   public static void func_179130_a(double var0, double var2, double var4, double var6, double var8, double var10) {
      GL11.glOrtho(var0, var2, var4, var6, var8, var10);
   }

   public static void func_179114_b(float var0, float var1, float var2, float var3) {
      GL11.glRotatef(var0, var1, var2, var3);
   }

   public static void func_212477_a(double var0, double var2, double var4, double var6) {
      GL11.glRotated(var0, var2, var4, var6);
   }

   public static void func_179152_a(float var0, float var1, float var2) {
      GL11.glScalef(var0, var1, var2);
   }

   public static void func_179139_a(double var0, double var2, double var4) {
      GL11.glScaled(var0, var2, var4);
   }

   public static void func_179109_b(float var0, float var1, float var2) {
      GL11.glTranslatef(var0, var1, var2);
   }

   public static void func_179137_b(double var0, double var2, double var4) {
      GL11.glTranslated(var0, var2, var4);
   }

   public static void func_179110_a(FloatBuffer var0) {
      GL11.glMultMatrixf(var0);
   }

   public static void func_199294_a(Matrix4f var0) {
      var0.func_195879_b(field_187450_a);
      field_187450_a.rewind();
      GL11.glMultMatrixf(field_187450_a);
   }

   public static void func_179131_c(float var0, float var1, float var2, float var3) {
      if (var0 != field_199307_v.field_179195_a || var1 != field_199307_v.field_179193_b || var2 != field_199307_v.field_179194_c || var3 != field_199307_v.field_179192_d) {
         field_199307_v.field_179195_a = var0;
         field_199307_v.field_179193_b = var1;
         field_199307_v.field_179194_c = var2;
         field_199307_v.field_179192_d = var3;
         GL11.glColor4f(var0, var1, var2, var3);
      }

   }

   public static void func_179124_c(float var0, float var1, float var2) {
      func_179131_c(var0, var1, var2, 1.0F);
   }

   public static void func_179117_G() {
      field_199307_v.field_179195_a = -1.0F;
      field_199307_v.field_179193_b = -1.0F;
      field_199307_v.field_179194_c = -1.0F;
      field_199307_v.field_179192_d = -1.0F;
   }

   public static void func_204611_f(int var0, int var1, int var2) {
      GL11.glNormalPointer(var0, var1, (long)var2);
   }

   public static void func_187446_a(int var0, int var1, ByteBuffer var2) {
      GL11.glNormalPointer(var0, var1, var2);
   }

   public static void func_187405_c(int var0, int var1, int var2, int var3) {
      GL11.glTexCoordPointer(var0, var1, var2, (long)var3);
   }

   public static void func_187404_a(int var0, int var1, int var2, ByteBuffer var3) {
      GL11.glTexCoordPointer(var0, var1, var2, var3);
   }

   public static void func_187420_d(int var0, int var1, int var2, int var3) {
      GL11.glVertexPointer(var0, var1, var2, (long)var3);
   }

   public static void func_187427_b(int var0, int var1, int var2, ByteBuffer var3) {
      GL11.glVertexPointer(var0, var1, var2, var3);
   }

   public static void func_187406_e(int var0, int var1, int var2, int var3) {
      GL11.glColorPointer(var0, var1, var2, (long)var3);
   }

   public static void func_187400_c(int var0, int var1, int var2, ByteBuffer var3) {
      GL11.glColorPointer(var0, var1, var2, var3);
   }

   public static void func_187429_p(int var0) {
      GL11.glDisableClientState(var0);
   }

   public static void func_187410_q(int var0) {
      GL11.glEnableClientState(var0);
   }

   public static void func_187439_f(int var0, int var1, int var2) {
      GL11.glDrawArrays(var0, var1, var2);
   }

   public static void func_187441_d(float var0) {
      GL11.glLineWidth(var0);
   }

   public static void func_179148_o(int var0) {
      GL11.glCallList(var0);
   }

   public static void func_187449_e(int var0, int var1) {
      GL11.glDeleteLists(var0, var1);
   }

   public static void func_187423_f(int var0, int var1) {
      GL11.glNewList(var0, var1);
   }

   public static void func_187415_K() {
      GL11.glEndList();
   }

   public static int func_187442_t(int var0) {
      return GL11.glGenLists(var0);
   }

   public static void func_187425_g(int var0, int var1) {
      GL11.glPixelStorei(var0, var1);
   }

   public static void func_199297_b(int var0, float var1) {
      GL11.glPixelTransferf(var0, var1);
   }

   public static void func_199296_a(int var0, int var1, int var2, int var3, int var4, int var5, long var6) {
      GL11.glReadPixels(var0, var1, var2, var3, var4, var5, var6);
   }

   public static int func_187434_L() {
      return GL11.glGetError();
   }

   public static String func_187416_u(int var0) {
      return GL11.glGetString(var0);
   }

   public static void func_187408_a(GlStateManager.Profile var0) {
      var0.func_187373_a();
   }

   public static void func_187440_b(GlStateManager.Profile var0) {
      var0.func_187374_b();
   }

   public static enum Profile {
      DEFAULT {
         public void func_187373_a() {
            GlStateManager.func_179118_c();
            GlStateManager.func_179092_a(519, 0.0F);
            GlStateManager.func_179140_f();
            GlStateManager.func_187424_a(2899, RenderHelper.func_74521_a(0.2F, 0.2F, 0.2F, 1.0F));

            for(int var1 = 0; var1 < 8; ++var1) {
               GlStateManager.func_179122_b(var1);
               GlStateManager.func_187438_a(16384 + var1, 4608, RenderHelper.func_74521_a(0.0F, 0.0F, 0.0F, 1.0F));
               GlStateManager.func_187438_a(16384 + var1, 4611, RenderHelper.func_74521_a(0.0F, 0.0F, 1.0F, 0.0F));
               if (var1 == 0) {
                  GlStateManager.func_187438_a(16384 + var1, 4609, RenderHelper.func_74521_a(1.0F, 1.0F, 1.0F, 1.0F));
                  GlStateManager.func_187438_a(16384 + var1, 4610, RenderHelper.func_74521_a(1.0F, 1.0F, 1.0F, 1.0F));
               } else {
                  GlStateManager.func_187438_a(16384 + var1, 4609, RenderHelper.func_74521_a(0.0F, 0.0F, 0.0F, 1.0F));
                  GlStateManager.func_187438_a(16384 + var1, 4610, RenderHelper.func_74521_a(0.0F, 0.0F, 0.0F, 1.0F));
               }
            }

            GlStateManager.func_179119_h();
            GlStateManager.func_179104_a(1032, 5634);
            GlStateManager.func_179097_i();
            GlStateManager.func_179143_c(513);
            GlStateManager.func_179132_a(true);
            GlStateManager.func_179084_k();
            GlStateManager.func_187401_a(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.func_187428_a(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.func_187398_d(32774);
            GlStateManager.func_179106_n();
            GlStateManager.func_187412_c(2917, 2048);
            GlStateManager.func_179095_a(1.0F);
            GlStateManager.func_179102_b(0.0F);
            GlStateManager.func_179153_c(1.0F);
            GlStateManager.func_187402_b(2918, RenderHelper.func_74521_a(0.0F, 0.0F, 0.0F, 0.0F));
            if (GL.getCapabilities().GL_NV_fog_distance) {
               GlStateManager.func_187412_c(2917, 34140);
            }

            GlStateManager.func_179136_a(0.0F, 0.0F);
            GlStateManager.func_179134_v();
            GlStateManager.func_179116_f(5379);
            GlStateManager.func_179100_b(GlStateManager.TexGen.S);
            GlStateManager.func_179149_a(GlStateManager.TexGen.S, 9216);
            GlStateManager.func_179105_a(GlStateManager.TexGen.S, 9474, RenderHelper.func_74521_a(1.0F, 0.0F, 0.0F, 0.0F));
            GlStateManager.func_179105_a(GlStateManager.TexGen.S, 9217, RenderHelper.func_74521_a(1.0F, 0.0F, 0.0F, 0.0F));
            GlStateManager.func_179100_b(GlStateManager.TexGen.T);
            GlStateManager.func_179149_a(GlStateManager.TexGen.T, 9216);
            GlStateManager.func_179105_a(GlStateManager.TexGen.T, 9474, RenderHelper.func_74521_a(0.0F, 1.0F, 0.0F, 0.0F));
            GlStateManager.func_179105_a(GlStateManager.TexGen.T, 9217, RenderHelper.func_74521_a(0.0F, 1.0F, 0.0F, 0.0F));
            GlStateManager.func_179100_b(GlStateManager.TexGen.R);
            GlStateManager.func_179149_a(GlStateManager.TexGen.R, 9216);
            GlStateManager.func_179105_a(GlStateManager.TexGen.R, 9474, RenderHelper.func_74521_a(0.0F, 0.0F, 0.0F, 0.0F));
            GlStateManager.func_179105_a(GlStateManager.TexGen.R, 9217, RenderHelper.func_74521_a(0.0F, 0.0F, 0.0F, 0.0F));
            GlStateManager.func_179100_b(GlStateManager.TexGen.Q);
            GlStateManager.func_179149_a(GlStateManager.TexGen.Q, 9216);
            GlStateManager.func_179105_a(GlStateManager.TexGen.Q, 9474, RenderHelper.func_74521_a(0.0F, 0.0F, 0.0F, 0.0F));
            GlStateManager.func_179105_a(GlStateManager.TexGen.Q, 9217, RenderHelper.func_74521_a(0.0F, 0.0F, 0.0F, 0.0F));
            GlStateManager.func_179138_g(0);
            GlStateManager.func_187421_b(3553, 10240, 9729);
            GlStateManager.func_187421_b(3553, 10241, 9986);
            GlStateManager.func_187421_b(3553, 10242, 10497);
            GlStateManager.func_187421_b(3553, 10243, 10497);
            GlStateManager.func_187421_b(3553, 33085, 1000);
            GlStateManager.func_187421_b(3553, 33083, 1000);
            GlStateManager.func_187421_b(3553, 33082, -1000);
            GlStateManager.func_187403_b(3553, 34049, 0.0F);
            GlStateManager.func_187399_a(8960, 8704, 8448);
            GlStateManager.func_187448_b(8960, 8705, RenderHelper.func_74521_a(0.0F, 0.0F, 0.0F, 0.0F));
            GlStateManager.func_187399_a(8960, 34161, 8448);
            GlStateManager.func_187399_a(8960, 34162, 8448);
            GlStateManager.func_187399_a(8960, 34176, 5890);
            GlStateManager.func_187399_a(8960, 34177, 34168);
            GlStateManager.func_187399_a(8960, 34178, 34166);
            GlStateManager.func_187399_a(8960, 34184, 5890);
            GlStateManager.func_187399_a(8960, 34185, 34168);
            GlStateManager.func_187399_a(8960, 34186, 34166);
            GlStateManager.func_187399_a(8960, 34192, 768);
            GlStateManager.func_187399_a(8960, 34193, 768);
            GlStateManager.func_187399_a(8960, 34194, 770);
            GlStateManager.func_187399_a(8960, 34200, 770);
            GlStateManager.func_187399_a(8960, 34201, 770);
            GlStateManager.func_187399_a(8960, 34202, 770);
            GlStateManager.func_187436_a(8960, 34163, 1.0F);
            GlStateManager.func_187436_a(8960, 3356, 1.0F);
            GlStateManager.func_179133_A();
            GlStateManager.func_179103_j(7425);
            GlStateManager.func_179101_C();
            GlStateManager.func_179135_a(true, true, true, true);
            GlStateManager.func_179151_a(1.0D);
            GlStateManager.func_187441_d(1.0F);
            GlStateManager.func_187432_a(0.0F, 0.0F, 1.0F);
            GlStateManager.func_187409_d(1028, 6914);
            GlStateManager.func_187409_d(1029, 6914);
         }

         public void func_187374_b() {
         }
      },
      PLAYER_SKIN {
         public void func_187373_a() {
            GlStateManager.func_179147_l();
            GlStateManager.func_179120_a(770, 771, 1, 0);
         }

         public void func_187374_b() {
            GlStateManager.func_179084_k();
         }
      },
      TRANSPARENT_MODEL {
         public void func_187373_a() {
            GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 0.15F);
            GlStateManager.func_179132_a(false);
            GlStateManager.func_179147_l();
            GlStateManager.func_187401_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.func_179092_a(516, 0.003921569F);
         }

         public void func_187374_b() {
            GlStateManager.func_179084_k();
            GlStateManager.func_179092_a(516, 0.1F);
            GlStateManager.func_179132_a(true);
         }
      };

      private Profile() {
      }

      public abstract void func_187373_a();

      public abstract void func_187374_b();

      // $FF: synthetic method
      Profile(Object var3) {
         this();
      }
   }

   public static enum DestFactor {
      CONSTANT_ALPHA(32771),
      CONSTANT_COLOR(32769),
      DST_ALPHA(772),
      DST_COLOR(774),
      ONE(1),
      ONE_MINUS_CONSTANT_ALPHA(32772),
      ONE_MINUS_CONSTANT_COLOR(32770),
      ONE_MINUS_DST_ALPHA(773),
      ONE_MINUS_DST_COLOR(775),
      ONE_MINUS_SRC_ALPHA(771),
      ONE_MINUS_SRC_COLOR(769),
      SRC_ALPHA(770),
      SRC_COLOR(768),
      ZERO(0);

      public final int field_187345_o;

      private DestFactor(int var3) {
         this.field_187345_o = var3;
      }
   }

   public static enum SourceFactor {
      CONSTANT_ALPHA(32771),
      CONSTANT_COLOR(32769),
      DST_ALPHA(772),
      DST_COLOR(774),
      ONE(1),
      ONE_MINUS_CONSTANT_ALPHA(32772),
      ONE_MINUS_CONSTANT_COLOR(32770),
      ONE_MINUS_DST_ALPHA(773),
      ONE_MINUS_DST_COLOR(775),
      ONE_MINUS_SRC_ALPHA(771),
      ONE_MINUS_SRC_COLOR(769),
      SRC_ALPHA(770),
      SRC_ALPHA_SATURATE(776),
      SRC_COLOR(768),
      ZERO(0);

      public final int field_187395_p;

      private SourceFactor(int var3) {
         this.field_187395_p = var3;
      }
   }

   static class BooleanState {
      private final int field_179202_a;
      private boolean field_179201_b;

      public BooleanState(int var1) {
         super();
         this.field_179202_a = var1;
      }

      public void func_179198_a() {
         this.func_179199_a(false);
      }

      public void func_179200_b() {
         this.func_179199_a(true);
      }

      public void func_179199_a(boolean var1) {
         if (var1 != this.field_179201_b) {
            this.field_179201_b = var1;
            if (var1) {
               GL11.glEnable(this.field_179202_a);
            } else {
               GL11.glDisable(this.field_179202_a);
            }
         }

      }
   }

   static class Color {
      public float field_179195_a;
      public float field_179193_b;
      public float field_179194_c;
      public float field_179192_d;

      public Color() {
         this(1.0F, 1.0F, 1.0F, 1.0F);
      }

      public Color(float var1, float var2, float var3, float var4) {
         super();
         this.field_179195_a = 1.0F;
         this.field_179193_b = 1.0F;
         this.field_179194_c = 1.0F;
         this.field_179192_d = 1.0F;
         this.field_179195_a = var1;
         this.field_179193_b = var2;
         this.field_179194_c = var3;
         this.field_179192_d = var4;
      }
   }

   static class ColorMask {
      public boolean field_179188_a;
      public boolean field_179186_b;
      public boolean field_179187_c;
      public boolean field_179185_d;

      private ColorMask() {
         super();
         this.field_179188_a = true;
         this.field_179186_b = true;
         this.field_179187_c = true;
         this.field_179185_d = true;
      }

      // $FF: synthetic method
      ColorMask(Object var1) {
         this();
      }
   }

   public static enum TexGen {
      S,
      T,
      R,
      Q;

      private TexGen() {
      }
   }

   static class TexGenCoord {
      public GlStateManager.BooleanState field_179067_a;
      public int field_179065_b;
      public int field_179066_c = -1;

      public TexGenCoord(int var1, int var2) {
         super();
         this.field_179065_b = var1;
         this.field_179067_a = new GlStateManager.BooleanState(var2);
      }
   }

   static class TexGenState {
      public GlStateManager.TexGenCoord field_179064_a;
      public GlStateManager.TexGenCoord field_179062_b;
      public GlStateManager.TexGenCoord field_179063_c;
      public GlStateManager.TexGenCoord field_179061_d;

      private TexGenState() {
         super();
         this.field_179064_a = new GlStateManager.TexGenCoord(8192, 3168);
         this.field_179062_b = new GlStateManager.TexGenCoord(8193, 3169);
         this.field_179063_c = new GlStateManager.TexGenCoord(8194, 3170);
         this.field_179061_d = new GlStateManager.TexGenCoord(8195, 3171);
      }

      // $FF: synthetic method
      TexGenState(Object var1) {
         this();
      }
   }

   static class StencilState {
      public GlStateManager.StencilFunc field_179078_a;
      public int field_179076_b;
      public int field_179077_c;
      public int field_179074_d;
      public int field_179075_e;

      private StencilState() {
         super();
         this.field_179078_a = new GlStateManager.StencilFunc();
         this.field_179076_b = -1;
         this.field_179077_c = 7680;
         this.field_179074_d = 7680;
         this.field_179075_e = 7680;
      }

      // $FF: synthetic method
      StencilState(Object var1) {
         this();
      }
   }

   static class StencilFunc {
      public int field_179081_a;
      public int field_179080_c;

      private StencilFunc() {
         super();
         this.field_179081_a = 519;
         this.field_179080_c = -1;
      }

      // $FF: synthetic method
      StencilFunc(Object var1) {
         this();
      }
   }

   static class ClearState {
      public double field_179205_a;
      public GlStateManager.Color field_179203_b;

      private ClearState() {
         super();
         this.field_179205_a = 1.0D;
         this.field_179203_b = new GlStateManager.Color(0.0F, 0.0F, 0.0F, 0.0F);
      }

      // $FF: synthetic method
      ClearState(Object var1) {
         this();
      }
   }

   static class ColorLogicState {
      public GlStateManager.BooleanState field_179197_a;
      public int field_179196_b;

      private ColorLogicState() {
         super();
         this.field_179197_a = new GlStateManager.BooleanState(3058);
         this.field_179196_b = 5379;
      }

      // $FF: synthetic method
      ColorLogicState(Object var1) {
         this();
      }
   }

   static class PolygonOffsetState {
      public GlStateManager.BooleanState field_179044_a;
      public GlStateManager.BooleanState field_179042_b;
      public float field_179043_c;
      public float field_179041_d;

      private PolygonOffsetState() {
         super();
         this.field_179044_a = new GlStateManager.BooleanState(32823);
         this.field_179042_b = new GlStateManager.BooleanState(10754);
      }

      // $FF: synthetic method
      PolygonOffsetState(Object var1) {
         this();
      }
   }

   static class CullState {
      public GlStateManager.BooleanState field_179054_a;
      public int field_179053_b;

      private CullState() {
         super();
         this.field_179054_a = new GlStateManager.BooleanState(2884);
         this.field_179053_b = 1029;
      }

      // $FF: synthetic method
      CullState(Object var1) {
         this();
      }
   }

   static class FogState {
      public GlStateManager.BooleanState field_179049_a;
      public int field_179047_b;
      public float field_179048_c;
      public float field_179045_d;
      public float field_179046_e;

      private FogState() {
         super();
         this.field_179049_a = new GlStateManager.BooleanState(2912);
         this.field_179047_b = 2048;
         this.field_179048_c = 1.0F;
         this.field_179046_e = 1.0F;
      }

      // $FF: synthetic method
      FogState(Object var1) {
         this();
      }
   }

   static class DepthState {
      public GlStateManager.BooleanState field_179052_a;
      public boolean field_179050_b;
      public int field_179051_c;

      private DepthState() {
         super();
         this.field_179052_a = new GlStateManager.BooleanState(2929);
         this.field_179050_b = true;
         this.field_179051_c = 513;
      }

      // $FF: synthetic method
      DepthState(Object var1) {
         this();
      }
   }

   static class BlendState {
      public GlStateManager.BooleanState field_179213_a;
      public int field_179211_b;
      public int field_179212_c;
      public int field_179209_d;
      public int field_179210_e;

      private BlendState() {
         super();
         this.field_179213_a = new GlStateManager.BooleanState(3042);
         this.field_179211_b = 1;
         this.field_179212_c = 0;
         this.field_179209_d = 1;
         this.field_179210_e = 0;
      }

      // $FF: synthetic method
      BlendState(Object var1) {
         this();
      }
   }

   static class ColorMaterialState {
      public GlStateManager.BooleanState field_179191_a;
      public int field_179189_b;
      public int field_179190_c;

      private ColorMaterialState() {
         super();
         this.field_179191_a = new GlStateManager.BooleanState(2903);
         this.field_179189_b = 1032;
         this.field_179190_c = 5634;
      }

      // $FF: synthetic method
      ColorMaterialState(Object var1) {
         this();
      }
   }

   static class AlphaState {
      public GlStateManager.BooleanState field_179208_a;
      public int field_179206_b;
      public float field_179207_c;

      private AlphaState() {
         super();
         this.field_179208_a = new GlStateManager.BooleanState(3008);
         this.field_179206_b = 519;
         this.field_179207_c = -1.0F;
      }

      // $FF: synthetic method
      AlphaState(Object var1) {
         this();
      }
   }

   static class TextureState {
      public GlStateManager.BooleanState field_179060_a;
      public int field_179059_b;

      private TextureState() {
         super();
         this.field_179060_a = new GlStateManager.BooleanState(3553);
      }

      // $FF: synthetic method
      TextureState(Object var1) {
         this();
      }
   }

   public static enum Viewport {
      INSTANCE;

      protected int field_199289_b;
      protected int field_199290_c;
      protected int field_199291_d;
      protected int field_199292_e;

      private Viewport() {
      }
   }

   public static enum LogicOp {
      AND(5377),
      AND_INVERTED(5380),
      AND_REVERSE(5378),
      CLEAR(5376),
      COPY(5379),
      COPY_INVERTED(5388),
      EQUIV(5385),
      INVERT(5386),
      NAND(5390),
      NOOP(5381),
      NOR(5384),
      OR(5383),
      OR_INVERTED(5389),
      OR_REVERSE(5387),
      SET(5391),
      XOR(5382);

      public final int field_187370_q;

      private LogicOp(int var3) {
         this.field_187370_q = var3;
      }
   }

   public static enum CullFace {
      FRONT(1028),
      BACK(1029),
      FRONT_AND_BACK(1032);

      public final int field_187328_d;

      private CullFace(int var3) {
         this.field_187328_d = var3;
      }
   }

   public static enum FogMode {
      LINEAR(9729),
      EXP(2048),
      EXP2(2049);

      public final int field_187351_d;

      private FogMode(int var3) {
         this.field_187351_d = var3;
      }
   }
}
