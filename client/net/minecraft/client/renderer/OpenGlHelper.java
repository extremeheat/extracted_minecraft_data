package net.minecraft.client.renderer;

import com.google.common.collect.Maps;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Util;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.ARBMultitexture;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.EXTBlendFuncSeparate;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLCapabilities;
import oshi.SystemInfo;
import oshi.hardware.Processor;

public class OpenGlHelper {
   public static boolean field_153197_d;
   public static boolean field_181063_b;
   public static int field_153198_e;
   public static int field_153199_f;
   public static int field_153200_g;
   public static int field_153201_h;
   public static int field_153202_i;
   public static int field_153203_j;
   public static int field_153204_k;
   public static int field_153205_l;
   public static int field_153206_m;
   private static OpenGlHelper.FboMode field_153212_w;
   public static boolean field_148823_f;
   private static boolean field_153213_x;
   private static boolean field_153214_y;
   public static int field_153207_o;
   public static int field_153208_p;
   public static int field_153209_q;
   public static int field_153210_r;
   private static boolean field_153215_z;
   public static int field_77478_a;
   public static int field_77476_b;
   public static int field_176096_r;
   private static boolean field_176088_V;
   public static int field_176095_s;
   public static int field_176094_t;
   public static int field_176093_u;
   public static int field_176092_v;
   public static int field_176091_w;
   public static int field_176099_x;
   public static int field_176098_y;
   public static int field_176097_z;
   public static int field_176080_A;
   public static int field_176081_B;
   public static int field_176082_C;
   public static int field_176076_D;
   public static int field_176077_E;
   public static int field_176078_F;
   public static int field_176079_G;
   public static int field_176084_H;
   public static int field_176085_I;
   public static int field_176086_J;
   public static int field_176087_K;
   private static boolean field_148828_i;
   public static boolean field_153211_u;
   public static boolean field_148827_a;
   public static boolean field_148824_g;
   private static String field_153196_B = "";
   private static String field_183030_aa;
   public static boolean field_176083_O;
   public static boolean field_181062_Q;
   private static boolean field_176090_Y;
   public static int field_176089_P;
   public static int field_148826_e;
   private static final Map<Integer, String> field_195919_ac = (Map)Util.func_200696_a(Maps.newHashMap(), (var0) -> {
      var0.put(0, "No error");
      var0.put(1280, "Enum parameter is invalid for this function");
      var0.put(1281, "Parameter is invalid for this function");
      var0.put(1282, "Current state is invalid for this function");
      var0.put(1283, "Stack overflow");
      var0.put(1284, "Stack underflow");
      var0.put(1285, "Out of memory");
      var0.put(1286, "Operation on incomplete framebuffer");
      var0.put(1286, "Operation on incomplete framebuffer");
   });

   public static void func_77474_a() {
      GLCapabilities var0 = GL.getCapabilities();
      field_153215_z = var0.GL_ARB_multitexture && !var0.OpenGL13;
      field_176088_V = var0.GL_ARB_texture_env_combine && !var0.OpenGL13;
      if (field_153215_z) {
         field_153196_B = field_153196_B + "Using ARB_multitexture.\n";
         field_77478_a = 33984;
         field_77476_b = 33985;
         field_176096_r = 33986;
      } else {
         field_153196_B = field_153196_B + "Using GL 1.3 multitexturing.\n";
         field_77478_a = 33984;
         field_77476_b = 33985;
         field_176096_r = 33986;
      }

      if (field_176088_V) {
         field_153196_B = field_153196_B + "Using ARB_texture_env_combine.\n";
         field_176095_s = 34160;
         field_176094_t = 34165;
         field_176093_u = 34167;
         field_176092_v = 34166;
         field_176091_w = 34168;
         field_176099_x = 34161;
         field_176098_y = 34176;
         field_176097_z = 34177;
         field_176080_A = 34178;
         field_176081_B = 34192;
         field_176082_C = 34193;
         field_176076_D = 34194;
         field_176077_E = 34162;
         field_176078_F = 34184;
         field_176079_G = 34185;
         field_176084_H = 34186;
         field_176085_I = 34200;
         field_176086_J = 34201;
         field_176087_K = 34202;
      } else {
         field_153196_B = field_153196_B + "Using GL 1.3 texture combiners.\n";
         field_176095_s = 34160;
         field_176094_t = 34165;
         field_176093_u = 34167;
         field_176092_v = 34166;
         field_176091_w = 34168;
         field_176099_x = 34161;
         field_176098_y = 34176;
         field_176097_z = 34177;
         field_176080_A = 34178;
         field_176081_B = 34192;
         field_176082_C = 34193;
         field_176076_D = 34194;
         field_176077_E = 34162;
         field_176078_F = 34184;
         field_176079_G = 34185;
         field_176084_H = 34186;
         field_176085_I = 34200;
         field_176086_J = 34201;
         field_176087_K = 34202;
      }

      field_153211_u = var0.GL_EXT_blend_func_separate && !var0.OpenGL14;
      field_148828_i = var0.OpenGL14 || var0.GL_EXT_blend_func_separate;
      field_148823_f = field_148828_i && (var0.GL_ARB_framebuffer_object || var0.GL_EXT_framebuffer_object || var0.OpenGL30);
      if (field_148823_f) {
         field_153196_B = field_153196_B + "Using framebuffer objects because ";
         if (var0.OpenGL30) {
            field_153196_B = field_153196_B + "OpenGL 3.0 is supported and separate blending is supported.\n";
            field_153212_w = OpenGlHelper.FboMode.BASE;
            field_153198_e = 36160;
            field_153199_f = 36161;
            field_153200_g = 36064;
            field_153201_h = 36096;
            field_153202_i = 36053;
            field_153203_j = 36054;
            field_153204_k = 36055;
            field_153205_l = 36059;
            field_153206_m = 36060;
         } else if (var0.GL_ARB_framebuffer_object) {
            field_153196_B = field_153196_B + "ARB_framebuffer_object is supported and separate blending is supported.\n";
            field_153212_w = OpenGlHelper.FboMode.ARB;
            field_153198_e = 36160;
            field_153199_f = 36161;
            field_153200_g = 36064;
            field_153201_h = 36096;
            field_153202_i = 36053;
            field_153204_k = 36055;
            field_153203_j = 36054;
            field_153205_l = 36059;
            field_153206_m = 36060;
         } else if (var0.GL_EXT_framebuffer_object) {
            field_153196_B = field_153196_B + "EXT_framebuffer_object is supported.\n";
            field_153212_w = OpenGlHelper.FboMode.EXT;
            field_153198_e = 36160;
            field_153199_f = 36161;
            field_153200_g = 36064;
            field_153201_h = 36096;
            field_153202_i = 36053;
            field_153204_k = 36055;
            field_153203_j = 36054;
            field_153205_l = 36059;
            field_153206_m = 36060;
         }
      } else {
         field_153196_B = field_153196_B + "Not using framebuffer objects because ";
         field_153196_B = field_153196_B + "OpenGL 1.4 is " + (var0.OpenGL14 ? "" : "not ") + "supported, ";
         field_153196_B = field_153196_B + "EXT_blend_func_separate is " + (var0.GL_EXT_blend_func_separate ? "" : "not ") + "supported, ";
         field_153196_B = field_153196_B + "OpenGL 3.0 is " + (var0.OpenGL30 ? "" : "not ") + "supported, ";
         field_153196_B = field_153196_B + "ARB_framebuffer_object is " + (var0.GL_ARB_framebuffer_object ? "" : "not ") + "supported, and ";
         field_153196_B = field_153196_B + "EXT_framebuffer_object is " + (var0.GL_EXT_framebuffer_object ? "" : "not ") + "supported.\n";
      }

      field_148827_a = var0.OpenGL21;
      field_153213_x = field_148827_a || var0.GL_ARB_vertex_shader && var0.GL_ARB_fragment_shader && var0.GL_ARB_shader_objects;
      field_153196_B = field_153196_B + "Shaders are " + (field_153213_x ? "" : "not ") + "available because ";
      if (field_153213_x) {
         if (var0.OpenGL21) {
            field_153196_B = field_153196_B + "OpenGL 2.1 is supported.\n";
            field_153214_y = false;
            field_153207_o = 35714;
            field_153208_p = 35713;
            field_153209_q = 35633;
            field_153210_r = 35632;
         } else {
            field_153196_B = field_153196_B + "ARB_shader_objects, ARB_vertex_shader, and ARB_fragment_shader are supported.\n";
            field_153214_y = true;
            field_153207_o = 35714;
            field_153208_p = 35713;
            field_153209_q = 35633;
            field_153210_r = 35632;
         }
      } else {
         field_153196_B = field_153196_B + "OpenGL 2.1 is " + (var0.OpenGL21 ? "" : "not ") + "supported, ";
         field_153196_B = field_153196_B + "ARB_shader_objects is " + (var0.GL_ARB_shader_objects ? "" : "not ") + "supported, ";
         field_153196_B = field_153196_B + "ARB_vertex_shader is " + (var0.GL_ARB_vertex_shader ? "" : "not ") + "supported, and ";
         field_153196_B = field_153196_B + "ARB_fragment_shader is " + (var0.GL_ARB_fragment_shader ? "" : "not ") + "supported.\n";
      }

      field_148824_g = field_148823_f && field_153213_x;
      String var1 = GL11.glGetString(7936).toLowerCase(Locale.ROOT);
      field_153197_d = var1.contains("nvidia");
      field_176090_Y = !var0.OpenGL15 && var0.GL_ARB_vertex_buffer_object;
      field_176083_O = var0.OpenGL15 || field_176090_Y;
      field_153196_B = field_153196_B + "VBOs are " + (field_176083_O ? "" : "not ") + "available because ";
      if (field_176083_O) {
         if (field_176090_Y) {
            field_153196_B = field_153196_B + "ARB_vertex_buffer_object is supported.\n";
            field_148826_e = 35044;
            field_176089_P = 34962;
         } else {
            field_153196_B = field_153196_B + "OpenGL 1.5 is supported.\n";
            field_148826_e = 35044;
            field_176089_P = 34962;
         }
      }

      field_181063_b = var1.contains("ati");
      if (field_181063_b) {
         if (field_176083_O) {
            field_181062_Q = true;
         } else {
            GameSettings.Options.RENDER_DISTANCE.func_148263_a(16.0F);
         }
      }

      try {
         Processor[] var2 = (new SystemInfo()).getHardware().getProcessors();
         field_183030_aa = String.format("%dx %s", var2.length, var2[0]).replaceAll("\\s+", " ");
      } catch (Throwable var3) {
      }

   }

   public static boolean func_153193_b() {
      return field_148824_g;
   }

   public static String func_153172_c() {
      return field_153196_B;
   }

   public static int func_153175_a(int var0, int var1) {
      return field_153214_y ? ARBShaderObjects.glGetObjectParameteriARB(var0, var1) : GL20.glGetProgrami(var0, var1);
   }

   public static void func_153178_b(int var0, int var1) {
      if (field_153214_y) {
         ARBShaderObjects.glAttachObjectARB(var0, var1);
      } else {
         GL20.glAttachShader(var0, var1);
      }

   }

   public static void func_153180_a(int var0) {
      if (field_153214_y) {
         ARBShaderObjects.glDeleteObjectARB(var0);
      } else {
         GL20.glDeleteShader(var0);
      }

   }

   public static int func_153195_b(int var0) {
      return field_153214_y ? ARBShaderObjects.glCreateShaderObjectARB(var0) : GL20.glCreateShader(var0);
   }

   public static void func_195918_a(int var0, CharSequence var1) {
      if (field_153214_y) {
         ARBShaderObjects.glShaderSourceARB(var0, var1);
      } else {
         GL20.glShaderSource(var0, var1);
      }

   }

   public static void func_153170_c(int var0) {
      if (field_153214_y) {
         ARBShaderObjects.glCompileShaderARB(var0);
      } else {
         GL20.glCompileShader(var0);
      }

   }

   public static int func_153157_c(int var0, int var1) {
      return field_153214_y ? ARBShaderObjects.glGetObjectParameteriARB(var0, var1) : GL20.glGetShaderi(var0, var1);
   }

   public static String func_153158_d(int var0, int var1) {
      return field_153214_y ? ARBShaderObjects.glGetInfoLogARB(var0, var1) : GL20.glGetShaderInfoLog(var0, var1);
   }

   public static String func_153166_e(int var0, int var1) {
      return field_153214_y ? ARBShaderObjects.glGetInfoLogARB(var0, var1) : GL20.glGetProgramInfoLog(var0, var1);
   }

   public static void func_153161_d(int var0) {
      if (field_153214_y) {
         ARBShaderObjects.glUseProgramObjectARB(var0);
      } else {
         GL20.glUseProgram(var0);
      }

   }

   public static int func_153183_d() {
      return field_153214_y ? ARBShaderObjects.glCreateProgramObjectARB() : GL20.glCreateProgram();
   }

   public static void func_153187_e(int var0) {
      if (field_153214_y) {
         ARBShaderObjects.glDeleteObjectARB(var0);
      } else {
         GL20.glDeleteProgram(var0);
      }

   }

   public static void func_153179_f(int var0) {
      if (field_153214_y) {
         ARBShaderObjects.glLinkProgramARB(var0);
      } else {
         GL20.glLinkProgram(var0);
      }

   }

   public static int func_153194_a(int var0, CharSequence var1) {
      return field_153214_y ? ARBShaderObjects.glGetUniformLocationARB(var0, var1) : GL20.glGetUniformLocation(var0, var1);
   }

   public static void func_153181_a(int var0, IntBuffer var1) {
      if (field_153214_y) {
         ARBShaderObjects.glUniform1ivARB(var0, var1);
      } else {
         GL20.glUniform1iv(var0, var1);
      }

   }

   public static void func_153163_f(int var0, int var1) {
      if (field_153214_y) {
         ARBShaderObjects.glUniform1iARB(var0, var1);
      } else {
         GL20.glUniform1i(var0, var1);
      }

   }

   public static void func_153168_a(int var0, FloatBuffer var1) {
      if (field_153214_y) {
         ARBShaderObjects.glUniform1fvARB(var0, var1);
      } else {
         GL20.glUniform1fv(var0, var1);
      }

   }

   public static void func_153182_b(int var0, IntBuffer var1) {
      if (field_153214_y) {
         ARBShaderObjects.glUniform2ivARB(var0, var1);
      } else {
         GL20.glUniform2iv(var0, var1);
      }

   }

   public static void func_153177_b(int var0, FloatBuffer var1) {
      if (field_153214_y) {
         ARBShaderObjects.glUniform2fvARB(var0, var1);
      } else {
         GL20.glUniform2fv(var0, var1);
      }

   }

   public static void func_153192_c(int var0, IntBuffer var1) {
      if (field_153214_y) {
         ARBShaderObjects.glUniform3ivARB(var0, var1);
      } else {
         GL20.glUniform3iv(var0, var1);
      }

   }

   public static void func_153191_c(int var0, FloatBuffer var1) {
      if (field_153214_y) {
         ARBShaderObjects.glUniform3fvARB(var0, var1);
      } else {
         GL20.glUniform3fv(var0, var1);
      }

   }

   public static void func_153162_d(int var0, IntBuffer var1) {
      if (field_153214_y) {
         ARBShaderObjects.glUniform4ivARB(var0, var1);
      } else {
         GL20.glUniform4iv(var0, var1);
      }

   }

   public static void func_153159_d(int var0, FloatBuffer var1) {
      if (field_153214_y) {
         ARBShaderObjects.glUniform4fvARB(var0, var1);
      } else {
         GL20.glUniform4fv(var0, var1);
      }

   }

   public static void func_153173_a(int var0, boolean var1, FloatBuffer var2) {
      if (field_153214_y) {
         ARBShaderObjects.glUniformMatrix2fvARB(var0, var1, var2);
      } else {
         GL20.glUniformMatrix2fv(var0, var1, var2);
      }

   }

   public static void func_153189_b(int var0, boolean var1, FloatBuffer var2) {
      if (field_153214_y) {
         ARBShaderObjects.glUniformMatrix3fvARB(var0, var1, var2);
      } else {
         GL20.glUniformMatrix3fv(var0, var1, var2);
      }

   }

   public static void func_153160_c(int var0, boolean var1, FloatBuffer var2) {
      if (field_153214_y) {
         ARBShaderObjects.glUniformMatrix4fvARB(var0, var1, var2);
      } else {
         GL20.glUniformMatrix4fv(var0, var1, var2);
      }

   }

   public static int func_153164_b(int var0, CharSequence var1) {
      return field_153214_y ? ARBVertexShader.glGetAttribLocationARB(var0, var1) : GL20.glGetAttribLocation(var0, var1);
   }

   public static int func_176073_e() {
      return field_176090_Y ? ARBVertexBufferObject.glGenBuffersARB() : GL15.glGenBuffers();
   }

   public static void func_176072_g(int var0, int var1) {
      if (field_176090_Y) {
         ARBVertexBufferObject.glBindBufferARB(var0, var1);
      } else {
         GL15.glBindBuffer(var0, var1);
      }

   }

   public static void func_176071_a(int var0, ByteBuffer var1, int var2) {
      if (field_176090_Y) {
         ARBVertexBufferObject.glBufferDataARB(var0, var1, var2);
      } else {
         GL15.glBufferData(var0, var1, var2);
      }

   }

   public static void func_176074_g(int var0) {
      if (field_176090_Y) {
         ARBVertexBufferObject.glDeleteBuffersARB(var0);
      } else {
         GL15.glDeleteBuffers(var0);
      }

   }

   public static boolean func_176075_f() {
      return field_176083_O && Minecraft.func_71410_x().field_71474_y.field_178881_t;
   }

   public static void func_153171_g(int var0, int var1) {
      if (field_148823_f) {
         switch(field_153212_w) {
         case BASE:
            GL30.glBindFramebuffer(var0, var1);
            break;
         case ARB:
            ARBFramebufferObject.glBindFramebuffer(var0, var1);
            break;
         case EXT:
            EXTFramebufferObject.glBindFramebufferEXT(var0, var1);
         }

      }
   }

   public static void func_153176_h(int var0, int var1) {
      if (field_148823_f) {
         switch(field_153212_w) {
         case BASE:
            GL30.glBindRenderbuffer(var0, var1);
            break;
         case ARB:
            ARBFramebufferObject.glBindRenderbuffer(var0, var1);
            break;
         case EXT:
            EXTFramebufferObject.glBindRenderbufferEXT(var0, var1);
         }

      }
   }

   public static void func_153184_g(int var0) {
      if (field_148823_f) {
         switch(field_153212_w) {
         case BASE:
            GL30.glDeleteRenderbuffers(var0);
            break;
         case ARB:
            ARBFramebufferObject.glDeleteRenderbuffers(var0);
            break;
         case EXT:
            EXTFramebufferObject.glDeleteRenderbuffersEXT(var0);
         }

      }
   }

   public static void func_153174_h(int var0) {
      if (field_148823_f) {
         switch(field_153212_w) {
         case BASE:
            GL30.glDeleteFramebuffers(var0);
            break;
         case ARB:
            ARBFramebufferObject.glDeleteFramebuffers(var0);
            break;
         case EXT:
            EXTFramebufferObject.glDeleteFramebuffersEXT(var0);
         }

      }
   }

   public static int func_153165_e() {
      if (!field_148823_f) {
         return -1;
      } else {
         switch(field_153212_w) {
         case BASE:
            return GL30.glGenFramebuffers();
         case ARB:
            return ARBFramebufferObject.glGenFramebuffers();
         case EXT:
            return EXTFramebufferObject.glGenFramebuffersEXT();
         default:
            return -1;
         }
      }
   }

   public static int func_153185_f() {
      if (!field_148823_f) {
         return -1;
      } else {
         switch(field_153212_w) {
         case BASE:
            return GL30.glGenRenderbuffers();
         case ARB:
            return ARBFramebufferObject.glGenRenderbuffers();
         case EXT:
            return EXTFramebufferObject.glGenRenderbuffersEXT();
         default:
            return -1;
         }
      }
   }

   public static void func_153186_a(int var0, int var1, int var2, int var3) {
      if (field_148823_f) {
         switch(field_153212_w) {
         case BASE:
            GL30.glRenderbufferStorage(var0, var1, var2, var3);
            break;
         case ARB:
            ARBFramebufferObject.glRenderbufferStorage(var0, var1, var2, var3);
            break;
         case EXT:
            EXTFramebufferObject.glRenderbufferStorageEXT(var0, var1, var2, var3);
         }

      }
   }

   public static void func_153190_b(int var0, int var1, int var2, int var3) {
      if (field_148823_f) {
         switch(field_153212_w) {
         case BASE:
            GL30.glFramebufferRenderbuffer(var0, var1, var2, var3);
            break;
         case ARB:
            ARBFramebufferObject.glFramebufferRenderbuffer(var0, var1, var2, var3);
            break;
         case EXT:
            EXTFramebufferObject.glFramebufferRenderbufferEXT(var0, var1, var2, var3);
         }

      }
   }

   public static int func_153167_i(int var0) {
      if (!field_148823_f) {
         return -1;
      } else {
         switch(field_153212_w) {
         case BASE:
            return GL30.glCheckFramebufferStatus(var0);
         case ARB:
            return ARBFramebufferObject.glCheckFramebufferStatus(var0);
         case EXT:
            return EXTFramebufferObject.glCheckFramebufferStatusEXT(var0);
         default:
            return -1;
         }
      }
   }

   public static void func_153188_a(int var0, int var1, int var2, int var3, int var4) {
      if (field_148823_f) {
         switch(field_153212_w) {
         case BASE:
            GL30.glFramebufferTexture2D(var0, var1, var2, var3, var4);
            break;
         case ARB:
            ARBFramebufferObject.glFramebufferTexture2D(var0, var1, var2, var3, var4);
            break;
         case EXT:
            EXTFramebufferObject.glFramebufferTexture2DEXT(var0, var1, var2, var3, var4);
         }

      }
   }

   public static void func_77473_a(int var0) {
      if (field_153215_z) {
         ARBMultitexture.glActiveTextureARB(var0);
      } else {
         GL13.glActiveTexture(var0);
      }

   }

   public static void func_77472_b(int var0) {
      if (field_153215_z) {
         ARBMultitexture.glClientActiveTextureARB(var0);
      } else {
         GL13.glClientActiveTexture(var0);
      }

   }

   public static void func_77475_a(int var0, float var1, float var2) {
      if (field_153215_z) {
         ARBMultitexture.glMultiTexCoord2fARB(var0, var1, var2);
      } else {
         GL13.glMultiTexCoord2f(var0, var1, var2);
      }

   }

   public static void func_148821_a(int var0, int var1, int var2, int var3) {
      if (field_148828_i) {
         if (field_153211_u) {
            EXTBlendFuncSeparate.glBlendFuncSeparateEXT(var0, var1, var2, var3);
         } else {
            GL14.glBlendFuncSeparate(var0, var1, var2, var3);
         }
      } else {
         GL11.glBlendFunc(var0, var1);
      }

   }

   public static boolean func_148822_b() {
      return field_148823_f && Minecraft.func_71410_x().field_71474_y.field_151448_g;
   }

   public static String func_183029_j() {
      return field_183030_aa == null ? "<unknown>" : field_183030_aa;
   }

   public static void func_188785_m(int var0) {
      func_203094_a(var0, true, true, true);
   }

   public static void func_203094_a(int var0, boolean var1, boolean var2, boolean var3) {
      GlStateManager.func_179090_x();
      GlStateManager.func_179132_a(false);
      Tessellator var4 = Tessellator.func_178181_a();
      BufferBuilder var5 = var4.func_178180_c();
      GL11.glLineWidth(4.0F);
      var5.func_181668_a(1, DefaultVertexFormats.field_181706_f);
      if (var1) {
         var5.func_181662_b(0.0D, 0.0D, 0.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
         var5.func_181662_b((double)var0, 0.0D, 0.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
      }

      if (var2) {
         var5.func_181662_b(0.0D, 0.0D, 0.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
         var5.func_181662_b(0.0D, (double)var0, 0.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
      }

      if (var3) {
         var5.func_181662_b(0.0D, 0.0D, 0.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
         var5.func_181662_b(0.0D, 0.0D, (double)var0).func_181669_b(0, 0, 0, 255).func_181675_d();
      }

      var4.func_78381_a();
      GL11.glLineWidth(2.0F);
      var5.func_181668_a(1, DefaultVertexFormats.field_181706_f);
      if (var1) {
         var5.func_181662_b(0.0D, 0.0D, 0.0D).func_181669_b(255, 0, 0, 255).func_181675_d();
         var5.func_181662_b((double)var0, 0.0D, 0.0D).func_181669_b(255, 0, 0, 255).func_181675_d();
      }

      if (var2) {
         var5.func_181662_b(0.0D, 0.0D, 0.0D).func_181669_b(0, 255, 0, 255).func_181675_d();
         var5.func_181662_b(0.0D, (double)var0, 0.0D).func_181669_b(0, 255, 0, 255).func_181675_d();
      }

      if (var3) {
         var5.func_181662_b(0.0D, 0.0D, 0.0D).func_181669_b(127, 127, 255, 255).func_181675_d();
         var5.func_181662_b(0.0D, 0.0D, (double)var0).func_181669_b(127, 127, 255, 255).func_181675_d();
      }

      var4.func_78381_a();
      GL11.glLineWidth(1.0F);
      GlStateManager.func_179132_a(true);
      GlStateManager.func_179098_w();
   }

   public static String func_195917_n(int var0) {
      return (String)field_195919_ac.get(var0);
   }

   static enum FboMode {
      BASE,
      ARB,
      EXT;

      private FboMode() {
      }
   }
}
