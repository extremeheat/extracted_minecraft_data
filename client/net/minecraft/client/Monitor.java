package net.minecraft.client;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.renderer.VideoMode;
import net.minecraft.client.renderer.VirtualScreen;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWVidMode.Buffer;

public final class Monitor {
   private final VirtualScreen field_197996_a;
   private final long field_197997_b;
   private final List<VideoMode> field_197998_c;
   private VideoMode field_197999_d;
   private int field_198000_e;
   private int field_198001_f;

   public Monitor(VirtualScreen var1, long var2) {
      super();
      this.field_197996_a = var1;
      this.field_197997_b = var2;
      this.field_197998_c = Lists.newArrayList();
      this.func_197988_a();
   }

   public void func_197988_a() {
      this.field_197998_c.clear();
      Buffer var1 = GLFW.glfwGetVideoModes(this.field_197997_b);

      for(int var2 = 0; var2 < var1.limit(); ++var2) {
         var1.position(var2);
         VideoMode var3 = new VideoMode(var1);
         if (var3.func_198062_c() >= 8 && var3.func_198063_d() >= 8 && var3.func_198068_e() >= 8) {
            this.field_197998_c.add(var3);
         }
      }

      int[] var5 = new int[1];
      int[] var6 = new int[1];
      GLFW.glfwGetMonitorPos(this.field_197997_b, var5, var6);
      this.field_198000_e = var5[0];
      this.field_198001_f = var6[0];
      GLFWVidMode var4 = GLFW.glfwGetVideoMode(this.field_197997_b);
      this.field_197999_d = new VideoMode(var4);
   }

   VideoMode func_197992_a(Optional<VideoMode> var1) {
      if (var1.isPresent()) {
         VideoMode var2 = (VideoMode)var1.get();
         Iterator var3 = Lists.reverse(this.field_197998_c).iterator();

         while(var3.hasNext()) {
            VideoMode var4 = (VideoMode)var3.next();
            if (var4.equals(var2)) {
               return var4;
            }
         }
      }

      return this.func_197987_b();
   }

   int func_197993_b(Optional<VideoMode> var1) {
      if (var1.isPresent()) {
         VideoMode var2 = (VideoMode)var1.get();

         for(int var3 = this.field_197998_c.size() - 1; var3 >= 0; --var3) {
            if (var2.equals(this.field_197998_c.get(var3))) {
               return var3;
            }
         }
      }

      return this.field_197998_c.indexOf(this.func_197987_b());
   }

   public VideoMode func_197987_b() {
      return this.field_197999_d;
   }

   public int func_197989_c() {
      return this.field_198000_e;
   }

   public int func_197990_d() {
      return this.field_198001_f;
   }

   public VideoMode func_197991_a(int var1) {
      return (VideoMode)this.field_197998_c.get(var1);
   }

   public int func_197994_e() {
      return this.field_197998_c.size();
   }

   public long func_197995_f() {
      return this.field_197997_b;
   }

   public String toString() {
      return String.format("Monitor[%s %sx%s %s]", this.field_197997_b, this.field_198000_e, this.field_198001_f, this.field_197999_d);
   }
}
