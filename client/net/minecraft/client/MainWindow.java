package net.minecraft.client;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Optional;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.client.main.GameConfiguration;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.VideoMode;
import net.minecraft.client.renderer.VirtualScreen;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWImage.Buffer;
import org.lwjgl.opengl.GL;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public final class MainWindow implements AutoCloseable {
   private static final Logger field_198114_a = LogManager.getLogger();
   private final GLFWErrorCallback field_198115_b = GLFWErrorCallback.create(this::func_198084_a);
   private final Minecraft field_198116_c;
   private final VirtualScreen field_198117_d;
   private Monitor field_198118_e;
   private final long field_198119_f;
   private int field_198120_g;
   private int field_198121_h;
   private int field_198122_i;
   private int field_198123_j;
   private Optional<VideoMode> field_198124_k;
   private boolean field_198125_l;
   private boolean field_198126_m;
   private int field_198127_n;
   private int field_198128_o;
   private int field_198129_p;
   private int field_198130_q;
   private int field_198131_r;
   private int field_198132_s;
   private int field_198133_t;
   private int field_198134_u;
   private double field_198135_v;
   private String field_198136_w = "";
   private boolean field_198138_y;
   private double field_198139_z = 4.9E-324D;

   public MainWindow(Minecraft var1, VirtualScreen var2, GameConfiguration.DisplayInformation var3, String var4) {
      super();
      this.field_198117_d = var2;
      this.func_198093_u();
      this.func_198076_a("Pre startup");
      this.field_198116_c = var1;
      Optional var5 = VideoMode.func_198061_a(var4);
      if (var5.isPresent()) {
         this.field_198124_k = var5;
      } else if (var3.field_199045_c.isPresent() && var3.field_199046_d.isPresent()) {
         this.field_198124_k = Optional.of(new VideoMode((Integer)var3.field_199045_c.get(), (Integer)var3.field_199046_d.get(), 8, 8, 8, 60));
      } else {
         this.field_198124_k = Optional.empty();
      }

      this.field_198126_m = this.field_198125_l = var3.field_178763_c;
      this.field_198118_e = var2.func_198054_a(GLFW.glfwGetPrimaryMonitor());
      VideoMode var6 = this.field_198118_e.func_197992_a(this.field_198125_l ? this.field_198124_k : Optional.empty());
      this.field_198122_i = this.field_198129_p = var3.field_178764_a > 0 ? var3.field_178764_a : 1;
      this.field_198123_j = this.field_198130_q = var3.field_178762_b > 0 ? var3.field_178762_b : 1;
      this.field_198120_g = this.field_198127_n = this.field_198118_e.func_197989_c() + var6.func_198064_a() / 2 - this.field_198129_p / 2;
      this.field_198121_h = this.field_198128_o = this.field_198118_e.func_197990_d() + var6.func_198065_b() / 2 - this.field_198130_q / 2;
      GLFW.glfwDefaultWindowHints();
      this.field_198119_f = GLFW.glfwCreateWindow(this.field_198129_p, this.field_198130_q, "Minecraft 1.13.2", this.field_198125_l ? this.field_198118_e.func_197995_f() : 0L, 0L);
      var1.field_195555_I = true;
      this.func_198085_v();
      GLFW.glfwMakeContextCurrent(this.field_198119_f);
      GL.createCapabilities();
      this.func_198108_y();
      this.func_198103_w();
      this.func_198110_t();
      GLFW.glfwSetFramebufferSizeCallback(this.field_198119_f, this::func_198102_b);
      GLFW.glfwSetWindowPosCallback(this.field_198119_f, this::func_198080_a);
      GLFW.glfwSetWindowSizeCallback(this.field_198119_f, this::func_198089_c);
      GLFW.glfwSetWindowFocusCallback(this.field_198119_f, this::func_198095_a);
      var1.field_71417_B = new MouseHelper(var1);
      var1.field_71417_B.func_198029_a(this.field_198119_f);
      var1.field_195559_v = new KeyboardListener(var1);
      var1.field_195559_v.func_197968_a(this.field_198119_f);
   }

   public static void func_211162_a(BiConsumer<Integer, String> var0) {
      MemoryStack var1 = MemoryStack.stackPush();
      Throwable var2 = null;

      try {
         PointerBuffer var3 = var1.mallocPointer(1);
         int var4 = GLFW.glfwGetError(var3);
         if (var4 != 0) {
            long var5 = var3.get();
            String var7 = var5 != 0L ? MemoryUtil.memUTF8(var5) : "";
            var0.accept(var4, var7);
         }
      } catch (Throwable var15) {
         var2 = var15;
         throw var15;
      } finally {
         if (var1 != null) {
            if (var2 != null) {
               try {
                  var1.close();
               } catch (Throwable var14) {
                  var2.addSuppressed(var14);
               }
            } else {
               var1.close();
            }
         }

      }

   }

   public void func_198094_a() {
      GlStateManager.func_179086_m(256);
      GlStateManager.func_179128_n(5889);
      GlStateManager.func_179096_D();
      GlStateManager.func_179130_a(0.0D, (double)this.func_198109_k() / this.func_198100_s(), (double)this.func_198091_l() / this.func_198100_s(), 0.0D, 1000.0D, 3000.0D);
      GlStateManager.func_179128_n(5888);
      GlStateManager.func_179096_D();
      GlStateManager.func_179109_b(0.0F, 0.0F, -2000.0F);
   }

   private void func_198110_t() {
      try {
         MemoryStack var1 = MemoryStack.stackPush();
         Throwable var2 = null;

         try {
            InputStream var3 = this.field_198116_c.func_195541_I().func_195746_a().func_195761_a(ResourcePackType.CLIENT_RESOURCES, new ResourceLocation("icons/icon_16x16.png"));
            Throwable var4 = null;

            try {
               InputStream var5 = this.field_198116_c.func_195541_I().func_195746_a().func_195761_a(ResourcePackType.CLIENT_RESOURCES, new ResourceLocation("icons/icon_32x32.png"));
               Throwable var6 = null;

               try {
                  if (var3 == null) {
                     throw new FileNotFoundException("icons/icon_16x16.png");
                  }

                  if (var5 == null) {
                     throw new FileNotFoundException("icons/icon_32x32.png");
                  }

                  IntBuffer var7 = var1.mallocInt(1);
                  IntBuffer var8 = var1.mallocInt(1);
                  IntBuffer var9 = var1.mallocInt(1);
                  Buffer var10 = GLFWImage.mallocStack(2, var1);
                  ByteBuffer var11 = this.func_198111_a(var3, var7, var8, var9);
                  if (var11 == null) {
                     throw new IllegalStateException("Could not load icon: " + STBImage.stbi_failure_reason());
                  }

                  var10.position(0);
                  var10.width(var7.get(0));
                  var10.height(var8.get(0));
                  var10.pixels(var11);
                  ByteBuffer var12 = this.func_198111_a(var5, var7, var8, var9);
                  if (var12 == null) {
                     throw new IllegalStateException("Could not load icon: " + STBImage.stbi_failure_reason());
                  }

                  var10.position(1);
                  var10.width(var7.get(0));
                  var10.height(var8.get(0));
                  var10.pixels(var12);
                  var10.position(0);
                  GLFW.glfwSetWindowIcon(this.field_198119_f, var10);
                  STBImage.stbi_image_free(var11);
                  STBImage.stbi_image_free(var12);
               } catch (Throwable var58) {
                  var6 = var58;
                  throw var58;
               } finally {
                  if (var5 != null) {
                     if (var6 != null) {
                        try {
                           var5.close();
                        } catch (Throwable var57) {
                           var6.addSuppressed(var57);
                        }
                     } else {
                        var5.close();
                     }
                  }

               }
            } catch (Throwable var60) {
               var4 = var60;
               throw var60;
            } finally {
               if (var3 != null) {
                  if (var4 != null) {
                     try {
                        var3.close();
                     } catch (Throwable var56) {
                        var4.addSuppressed(var56);
                     }
                  } else {
                     var3.close();
                  }
               }

            }
         } catch (Throwable var62) {
            var2 = var62;
            throw var62;
         } finally {
            if (var1 != null) {
               if (var2 != null) {
                  try {
                     var1.close();
                  } catch (Throwable var55) {
                     var2.addSuppressed(var55);
                  }
               } else {
                  var1.close();
               }
            }

         }
      } catch (IOException var64) {
         field_198114_a.error("Couldn't set icon", var64);
      }

   }

   @Nullable
   private ByteBuffer func_198111_a(InputStream var1, IntBuffer var2, IntBuffer var3, IntBuffer var4) throws IOException {
      ByteBuffer var5 = null;

      ByteBuffer var6;
      try {
         var5 = TextureUtil.func_195724_a(var1);
         var5.rewind();
         var6 = STBImage.stbi_load_from_memory(var5, var2, var3, var4, 0);
      } finally {
         if (var5 != null) {
            MemoryUtil.memFree(var5);
         }

      }

      return var6;
   }

   void func_198076_a(String var1) {
      this.field_198136_w = var1;
   }

   private void func_198093_u() {
      GLFW.glfwSetErrorCallback(MainWindow::func_208034_b);
   }

   private static void func_208034_b(int var0, long var1) {
      throw new IllegalStateException("GLFW error " + var0 + ": " + MemoryUtil.memUTF8(var1));
   }

   void func_198084_a(int var1, long var2) {
      String var4 = MemoryUtil.memUTF8(var2);
      field_198114_a.error("########## GL ERROR ##########");
      field_198114_a.error("@ {}", this.field_198136_w);
      field_198114_a.error("{}: {}", var1, var4);
   }

   void func_198112_b() {
      GLFW.glfwSetErrorCallback(this.field_198115_b).free();
   }

   public void func_209548_c() {
      GLFW.glfwSwapInterval(this.field_198116_c.field_71474_y.field_74352_v ? 1 : 0);
   }

   public void close() {
      Util.field_211180_a = System::nanoTime;
      Callbacks.glfwFreeCallbacks(this.field_198119_f);
      this.field_198115_b.close();
      GLFW.glfwDestroyWindow(this.field_198119_f);
      GLFW.glfwTerminate();
   }

   private void func_198085_v() {
      this.field_198118_e = this.field_198117_d.func_198055_a(this);
   }

   private void func_198080_a(long var1, int var3, int var4) {
      this.field_198127_n = var3;
      this.field_198128_o = var4;
      this.func_198085_v();
   }

   private void func_198102_b(long var1, int var3, int var4) {
      if (var1 == this.field_198119_f) {
         int var5 = this.func_198109_k();
         int var6 = this.func_198091_l();
         if (var3 != 0 && var4 != 0) {
            this.field_198131_r = var3;
            this.field_198132_s = var4;
            if (this.func_198109_k() != var5 || this.func_198091_l() != var6) {
               this.func_198098_h();
            }

         }
      }
   }

   private void func_198103_w() {
      int[] var1 = new int[1];
      int[] var2 = new int[1];
      GLFW.glfwGetFramebufferSize(this.field_198119_f, var1, var2);
      this.field_198131_r = var1[0];
      this.field_198132_s = var2[0];
   }

   private void func_198089_c(long var1, int var3, int var4) {
      this.field_198129_p = var3;
      this.field_198130_q = var4;
      this.func_198085_v();
   }

   private void func_198095_a(long var1, boolean var3) {
      if (var1 == this.field_198119_f) {
         this.field_198116_c.field_195555_I = var3;
      }

   }

   private int func_198082_x() {
      return this.field_198116_c.field_71441_e == null && this.field_198116_c.field_71462_r != null ? 60 : this.field_198116_c.field_71474_y.field_74350_i;
   }

   public boolean func_198096_c() {
      return (double)this.func_198082_x() < GameSettings.Options.FRAMERATE_LIMIT.func_198009_f();
   }

   public void func_198086_a(boolean var1) {
      this.field_198116_c.field_71424_I.func_76320_a("display_update");
      GLFW.glfwSwapBuffers(this.field_198119_f);
      GLFW.glfwPollEvents();
      if (this.field_198125_l != this.field_198126_m) {
         this.field_198126_m = this.field_198125_l;
         this.func_198081_z();
      }

      this.field_198116_c.field_71424_I.func_76319_b();
      if (var1 && this.func_198096_c()) {
         this.field_198116_c.field_71424_I.func_76320_a("fpslimit_wait");
         double var2 = this.field_198139_z + 1.0D / (double)this.func_198082_x();

         double var4;
         for(var4 = GLFW.glfwGetTime(); var4 < var2; var4 = GLFW.glfwGetTime()) {
            GLFW.glfwWaitEventsTimeout(var2 - var4);
         }

         this.field_198139_z = var4;
         this.field_198116_c.field_71424_I.func_76319_b();
      }

   }

   public Optional<VideoMode> func_198106_d() {
      return this.field_198124_k;
   }

   public int func_198090_e() {
      return this.field_198124_k.isPresent() ? this.field_198118_e.func_197993_b(this.field_198124_k) + 1 : 0;
   }

   public String func_198088_a(int var1) {
      if (this.field_198118_e.func_197994_e() <= var1) {
         var1 = this.field_198118_e.func_197994_e() - 1;
      }

      return this.field_198118_e.func_197991_a(var1).toString();
   }

   public void func_198104_b(int var1) {
      Optional var2 = this.field_198124_k;
      if (var1 == 0) {
         this.field_198124_k = Optional.empty();
      } else {
         this.field_198124_k = Optional.of(this.field_198118_e.func_197991_a(var1 - 1));
      }

      if (!this.field_198124_k.equals(var2)) {
         this.field_198138_y = true;
      }

   }

   public void func_198097_f() {
      if (this.field_198125_l && this.field_198138_y) {
         this.field_198138_y = false;
         this.func_198108_y();
         this.func_198098_h();
      }

   }

   private void func_198108_y() {
      boolean var1 = GLFW.glfwGetWindowMonitor(this.field_198119_f) != 0L;
      VideoMode var2;
      if (this.field_198125_l) {
         var2 = this.field_198118_e.func_197992_a(this.field_198124_k);
         if (!var1) {
            this.field_198120_g = this.field_198127_n;
            this.field_198121_h = this.field_198128_o;
            this.field_198122_i = this.field_198129_p;
            this.field_198123_j = this.field_198130_q;
         }

         this.field_198127_n = 0;
         this.field_198128_o = 0;
         this.field_198129_p = var2.func_198064_a();
         this.field_198130_q = var2.func_198065_b();
         GLFW.glfwSetWindowMonitor(this.field_198119_f, this.field_198118_e.func_197995_f(), this.field_198127_n, this.field_198128_o, this.field_198129_p, this.field_198130_q, var2.func_198067_f());
      } else {
         var2 = this.field_198118_e.func_197987_b();
         this.field_198127_n = this.field_198120_g;
         this.field_198128_o = this.field_198121_h;
         this.field_198129_p = this.field_198122_i;
         this.field_198130_q = this.field_198123_j;
         GLFW.glfwSetWindowMonitor(this.field_198119_f, 0L, this.field_198127_n, this.field_198128_o, this.field_198129_p, this.field_198130_q, -1);
      }

   }

   public void func_198077_g() {
      this.field_198125_l = !this.field_198125_l;
      this.field_198116_c.field_71474_y.field_74353_u = this.field_198125_l;
   }

   private void func_198081_z() {
      try {
         this.func_198108_y();
         this.func_198098_h();
         this.func_209548_c();
         this.func_198086_a(false);
      } catch (Exception var2) {
         field_198114_a.error("Couldn't toggle fullscreen", var2);
      }

   }

   public void func_198098_h() {
      this.field_198135_v = (double)this.func_198078_c(this.field_198116_c.field_71474_y.field_74335_Z);
      this.field_198133_t = MathHelper.func_76143_f((double)this.field_198131_r / this.field_198135_v);
      this.field_198134_u = MathHelper.func_76143_f((double)this.field_198132_s / this.field_198135_v);
      if (this.field_198116_c.field_71462_r != null) {
         this.field_198116_c.field_71462_r.func_175273_b(this.field_198116_c, this.field_198133_t, this.field_198134_u);
      }

      Framebuffer var1 = this.field_198116_c.func_147110_a();
      if (var1 != null) {
         var1.func_147613_a(this.field_198131_r, this.field_198132_s);
      }

      if (this.field_198116_c.field_71460_t != null) {
         this.field_198116_c.field_71460_t.func_147704_a(this.field_198131_r, this.field_198132_s);
      }

      if (this.field_198116_c.field_71417_B != null) {
         this.field_198116_c.field_71417_B.func_198021_g();
      }

   }

   public int func_198078_c(int var1) {
      int var2;
      for(var2 = 1; var2 != var1 && var2 < this.field_198131_r && var2 < this.field_198132_s && this.field_198131_r / (var2 + 1) >= 320 && this.field_198132_s / (var2 + 1) >= 240; ++var2) {
      }

      if (this.field_198116_c.func_211821_e() && var2 % 2 != 0) {
         ++var2;
      }

      return var2;
   }

   public long func_198092_i() {
      return this.field_198119_f;
   }

   public boolean func_198113_j() {
      return this.field_198125_l;
   }

   public int func_198109_k() {
      return this.field_198131_r;
   }

   public int func_198091_l() {
      return this.field_198132_s;
   }

   public int func_198105_m() {
      return this.field_198129_p;
   }

   public int func_198083_n() {
      return this.field_198130_q;
   }

   public int func_198107_o() {
      return this.field_198133_t;
   }

   public int func_198087_p() {
      return this.field_198134_u;
   }

   public int func_198099_q() {
      return this.field_198127_n;
   }

   public int func_198079_r() {
      return this.field_198128_o;
   }

   public double func_198100_s() {
      return this.field_198135_v;
   }
}
