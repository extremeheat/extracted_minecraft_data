package net.minecraft.client.renderer;

import java.nio.FloatBuffer;
import org.lwjgl.opengl.GL11;

public class GlStateManager {
   private static GlStateManager.AlphaState field_179160_a = new GlStateManager.AlphaState();
   private static GlStateManager.BooleanState field_179158_b = new GlStateManager.BooleanState(2896);
   private static GlStateManager.BooleanState[] field_179159_c = new GlStateManager.BooleanState[8];
   private static GlStateManager.ColorMaterialState field_179156_d = new GlStateManager.ColorMaterialState();
   private static GlStateManager.BlendState field_179157_e = new GlStateManager.BlendState();
   private static GlStateManager.DepthState field_179154_f = new GlStateManager.DepthState();
   private static GlStateManager.FogState field_179155_g = new GlStateManager.FogState();
   private static GlStateManager.CullState field_179167_h = new GlStateManager.CullState();
   private static GlStateManager.PolygonOffsetState field_179168_i = new GlStateManager.PolygonOffsetState();
   private static GlStateManager.ColorLogicState field_179165_j = new GlStateManager.ColorLogicState();
   private static GlStateManager.TexGenState field_179166_k = new GlStateManager.TexGenState();
   private static GlStateManager.ClearState field_179163_l = new GlStateManager.ClearState();
   private static GlStateManager.StencilState field_179164_m = new GlStateManager.StencilState();
   private static GlStateManager.BooleanState field_179161_n = new GlStateManager.BooleanState(2977);
   private static int field_179162_o = 0;
   private static GlStateManager.TextureState[] field_179174_p = new GlStateManager.TextureState[8];
   private static int field_179173_q = 7425;
   private static GlStateManager.BooleanState field_179172_r = new GlStateManager.BooleanState(32826);
   private static GlStateManager.ColorMask field_179171_s = new GlStateManager.ColorMask();
   private static GlStateManager.Color field_179170_t = new GlStateManager.Color();

   public static void func_179123_a() {
      GL11.glPushAttrib(8256);
   }

   public static void func_179099_b() {
      GL11.glPopAttrib();
   }

   public static void func_179118_c() {
      field_179160_a.field_179208_a.func_179198_a();
   }

   public static void func_179141_d() {
      field_179160_a.field_179208_a.func_179200_b();
   }

   public static void func_179092_a(int var0, float var1) {
      if (var0 != field_179160_a.field_179206_b || var1 != field_179160_a.field_179207_c) {
         field_179160_a.field_179206_b = var0;
         field_179160_a.field_179207_c = var1;
         GL11.glAlphaFunc(var0, var1);
      }

   }

   public static void func_179145_e() {
      field_179158_b.func_179200_b();
   }

   public static void func_179140_f() {
      field_179158_b.func_179198_a();
   }

   public static void func_179085_a(int var0) {
      field_179159_c[var0].func_179200_b();
   }

   public static void func_179122_b(int var0) {
      field_179159_c[var0].func_179198_a();
   }

   public static void func_179142_g() {
      field_179156_d.field_179191_a.func_179200_b();
   }

   public static void func_179119_h() {
      field_179156_d.field_179191_a.func_179198_a();
   }

   public static void func_179104_a(int var0, int var1) {
      if (var0 != field_179156_d.field_179189_b || var1 != field_179156_d.field_179190_c) {
         field_179156_d.field_179189_b = var0;
         field_179156_d.field_179190_c = var1;
         GL11.glColorMaterial(var0, var1);
      }

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

   public static void func_179112_b(int var0, int var1) {
      if (var0 != field_179157_e.field_179211_b || var1 != field_179157_e.field_179212_c) {
         field_179157_e.field_179211_b = var0;
         field_179157_e.field_179212_c = var1;
         GL11.glBlendFunc(var0, var1);
      }

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

   public static void func_179127_m() {
      field_179155_g.field_179049_a.func_179200_b();
   }

   public static void func_179106_n() {
      field_179155_g.field_179049_a.func_179198_a();
   }

   public static void func_179093_d(int var0) {
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

   public static void func_179089_o() {
      field_179167_h.field_179054_a.func_179200_b();
   }

   public static void func_179129_p() {
      field_179167_h.field_179054_a.func_179198_a();
   }

   public static void func_179107_e(int var0) {
      if (var0 != field_179167_h.field_179053_b) {
         field_179167_h.field_179053_b = var0;
         GL11.glCullFace(var0);
      }

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
      GL11.glTexGen(func_179125_c(var0).field_179065_b, var1, var2);
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
      field_179174_p[field_179162_o].field_179060_a.func_179200_b();
   }

   public static void func_179090_x() {
      field_179174_p[field_179162_o].field_179060_a.func_179198_a();
   }

   public static int func_179146_y() {
      return GL11.glGenTextures();
   }

   public static void func_179150_h(int var0) {
      GL11.glDeleteTextures(var0);
      GlStateManager.TextureState[] var1 = field_179174_p;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         GlStateManager.TextureState var4 = var1[var3];
         if (var4.field_179059_b == var0) {
            var4.field_179059_b = -1;
         }
      }

   }

   public static void func_179144_i(int var0) {
      if (var0 != field_179174_p[field_179162_o].field_179059_b) {
         field_179174_p[field_179162_o].field_179059_b = var0;
         GL11.glBindTexture(3553, var0);
      }

   }

   public static void func_179108_z() {
      field_179161_n.func_179200_b();
   }

   public static void func_179133_A() {
      field_179161_n.func_179198_a();
   }

   public static void func_179103_j(int var0) {
      if (var0 != field_179173_q) {
         field_179173_q = var0;
         GL11.glShadeModel(var0);
      }

   }

   public static void func_179091_B() {
      field_179172_r.func_179200_b();
   }

   public static void func_179101_C() {
      field_179172_r.func_179198_a();
   }

   public static void func_179083_b(int var0, int var1, int var2, int var3) {
      GL11.glViewport(var0, var1, var2, var3);
   }

   public static void func_179135_a(boolean var0, boolean var1, boolean var2, boolean var3) {
      if (var0 != field_179171_s.field_179188_a || var1 != field_179171_s.field_179186_b || var2 != field_179171_s.field_179187_c || var3 != field_179171_s.field_179185_d) {
         field_179171_s.field_179188_a = var0;
         field_179171_s.field_179186_b = var1;
         field_179171_s.field_179187_c = var2;
         field_179171_s.field_179185_d = var3;
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
      GL11.glGetFloat(var0, var1);
   }

   public static void func_179130_a(double var0, double var2, double var4, double var6, double var8, double var10) {
      GL11.glOrtho(var0, var2, var4, var6, var8, var10);
   }

   public static void func_179114_b(float var0, float var1, float var2, float var3) {
      GL11.glRotatef(var0, var1, var2, var3);
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
      GL11.glMultMatrix(var0);
   }

   public static void func_179131_c(float var0, float var1, float var2, float var3) {
      if (var0 != field_179170_t.field_179195_a || var1 != field_179170_t.field_179193_b || var2 != field_179170_t.field_179194_c || var3 != field_179170_t.field_179192_d) {
         field_179170_t.field_179195_a = var0;
         field_179170_t.field_179193_b = var1;
         field_179170_t.field_179194_c = var2;
         field_179170_t.field_179192_d = var3;
         GL11.glColor4f(var0, var1, var2, var3);
      }

   }

   public static void func_179124_c(float var0, float var1, float var2) {
      func_179131_c(var0, var1, var2, 1.0F);
   }

   public static void func_179117_G() {
      field_179170_t.field_179195_a = field_179170_t.field_179193_b = field_179170_t.field_179194_c = field_179170_t.field_179192_d = -1.0F;
   }

   public static void func_179148_o(int var0) {
      GL11.glCallList(var0);
   }

   static {
      int var0;
      for(var0 = 0; var0 < 8; ++var0) {
         field_179159_c[var0] = new GlStateManager.BooleanState(16384 + var0);
      }

      for(var0 = 0; var0 < 8; ++var0) {
         field_179174_p[var0] = new GlStateManager.TextureState();
      }

   }

   static class BooleanState {
      private final int field_179202_a;
      private boolean field_179201_b = false;

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
      public float field_179195_a = 1.0F;
      public float field_179193_b = 1.0F;
      public float field_179194_c = 1.0F;
      public float field_179192_d = 1.0F;

      public Color() {
         super();
      }

      public Color(float var1, float var2, float var3, float var4) {
         super();
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
      public int field_179079_b;
      public int field_179080_c;

      private StencilFunc() {
         super();
         this.field_179081_a = 519;
         this.field_179079_b = 0;
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
      public int field_179204_c;

      private ClearState() {
         super();
         this.field_179205_a = 1.0D;
         this.field_179203_b = new GlStateManager.Color(0.0F, 0.0F, 0.0F, 0.0F);
         this.field_179204_c = 0;
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
         this.field_179043_c = 0.0F;
         this.field_179041_d = 0.0F;
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
         this.field_179045_d = 0.0F;
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
         this.field_179059_b = 0;
      }

      // $FF: synthetic method
      TextureState(Object var1) {
         this();
      }
   }
}
