package net.minecraft.client.renderer;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.client.GameSettings;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Monitor;
import net.minecraft.client.main.GameConfiguration;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMonitorCallback;
import org.lwjgl.glfw.GLFWMonitorCallbackI;

public final class VirtualScreen implements AutoCloseable {
   private final Minecraft field_198057_a;
   private final Map<Long, Monitor> field_198058_b = Maps.newHashMap();
   private final Map<Long, MainWindow> field_198059_c = Maps.newHashMap();
   private final Map<MainWindow, Monitor> field_198060_d = Maps.newHashMap();

   public VirtualScreen(Minecraft var1) {
      super();
      this.field_198057_a = var1;
      GLFW.glfwSetMonitorCallback(this::func_198056_a);
      PointerBuffer var2 = GLFW.glfwGetMonitors();

      for(int var3 = 0; var3 < var2.limit(); ++var3) {
         long var4 = var2.get(var3);
         this.field_198058_b.put(var4, new Monitor(this, var4));
      }

   }

   private void func_198056_a(long var1, int var3) {
      if (var3 == 262145) {
         this.field_198058_b.put(var1, new Monitor(this, var1));
      } else if (var3 == 262146) {
         this.field_198058_b.remove(var1);
      }

   }

   public Monitor func_198054_a(long var1) {
      return (Monitor)this.field_198058_b.get(var1);
   }

   public Monitor func_198055_a(MainWindow var1) {
      long var2 = GLFW.glfwGetWindowMonitor(var1.func_198092_i());
      if (var2 != 0L) {
         return (Monitor)this.field_198058_b.get(var2);
      } else {
         Monitor var4 = (Monitor)this.field_198058_b.values().iterator().next();
         int var5 = -1;
         int var6 = var1.func_198099_q();
         int var7 = var6 + var1.func_198105_m();
         int var8 = var1.func_198079_r();
         int var9 = var8 + var1.func_198083_n();
         Iterator var10 = this.field_198058_b.values().iterator();

         while(var10.hasNext()) {
            Monitor var11 = (Monitor)var10.next();
            int var12 = var11.func_197989_c();
            int var13 = var12 + var11.func_197987_b().func_198064_a();
            int var14 = var11.func_197990_d();
            int var15 = var14 + var11.func_197987_b().func_198065_b();
            int var16 = MathHelper.func_76125_a(var6, var12, var13);
            int var17 = MathHelper.func_76125_a(var7, var12, var13);
            int var18 = MathHelper.func_76125_a(var8, var14, var15);
            int var19 = MathHelper.func_76125_a(var9, var14, var15);
            int var20 = Math.max(0, var17 - var16);
            int var21 = Math.max(0, var19 - var18);
            int var22 = var20 * var21;
            if (var22 > var5) {
               var4 = var11;
               var5 = var22;
            }
         }

         if (var4 != this.field_198060_d.get(var1)) {
            this.field_198060_d.put(var1, var4);
            GameSettings.Options.FULLSCREEN_RESOLUTION.func_148263_a((float)var4.func_197994_e());
         }

         return var4;
      }
   }

   public MainWindow func_198053_a(GameConfiguration.DisplayInformation var1, String var2) {
      return new MainWindow(this.field_198057_a, this, var1, var2);
   }

   public void close() {
      GLFWMonitorCallback var1 = GLFW.glfwSetMonitorCallback((GLFWMonitorCallbackI)null);
      if (var1 != null) {
         var1.free();
      }

   }
}
