package net.minecraft.client.util;

import com.google.gson.JsonObject;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.JsonUtils;
import org.lwjgl.opengl.GL14;

public class JsonBlendingMode {
   private static JsonBlendingMode field_148118_a = null;
   private final int field_148116_b;
   private final int field_148117_c;
   private final int field_148114_d;
   private final int field_148115_e;
   private final int field_148112_f;
   private final boolean field_148113_g;
   private final boolean field_148119_h;

   private JsonBlendingMode(boolean var1, boolean var2, int var3, int var4, int var5, int var6, int var7) {
      super();
      this.field_148113_g = var1;
      this.field_148116_b = var3;
      this.field_148114_d = var4;
      this.field_148117_c = var5;
      this.field_148115_e = var6;
      this.field_148119_h = var2;
      this.field_148112_f = var7;
   }

   public JsonBlendingMode() {
      this(false, true, 1, 0, 1, 0, 32774);
   }

   public JsonBlendingMode(int var1, int var2, int var3) {
      this(false, false, var1, var2, var1, var2, var3);
   }

   public JsonBlendingMode(int var1, int var2, int var3, int var4, int var5) {
      this(true, false, var1, var2, var3, var4, var5);
   }

   public void func_148109_a() {
      if (!this.equals(field_148118_a)) {
         if (field_148118_a == null || this.field_148119_h != field_148118_a.func_148111_b()) {
            field_148118_a = this;
            if (this.field_148119_h) {
               GlStateManager.func_179084_k();
               return;
            }

            GlStateManager.func_179147_l();
         }

         GL14.glBlendEquation(this.field_148112_f);
         if (this.field_148113_g) {
            GlStateManager.func_179120_a(this.field_148116_b, this.field_148114_d, this.field_148117_c, this.field_148115_e);
         } else {
            GlStateManager.func_179112_b(this.field_148116_b, this.field_148114_d);
         }

      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof JsonBlendingMode)) {
         return false;
      } else {
         JsonBlendingMode var2 = (JsonBlendingMode)var1;
         if (this.field_148112_f != var2.field_148112_f) {
            return false;
         } else if (this.field_148115_e != var2.field_148115_e) {
            return false;
         } else if (this.field_148114_d != var2.field_148114_d) {
            return false;
         } else if (this.field_148119_h != var2.field_148119_h) {
            return false;
         } else if (this.field_148113_g != var2.field_148113_g) {
            return false;
         } else if (this.field_148117_c != var2.field_148117_c) {
            return false;
         } else {
            return this.field_148116_b == var2.field_148116_b;
         }
      }
   }

   public int hashCode() {
      int var1 = this.field_148116_b;
      var1 = 31 * var1 + this.field_148117_c;
      var1 = 31 * var1 + this.field_148114_d;
      var1 = 31 * var1 + this.field_148115_e;
      var1 = 31 * var1 + this.field_148112_f;
      var1 = 31 * var1 + (this.field_148113_g ? 1 : 0);
      var1 = 31 * var1 + (this.field_148119_h ? 1 : 0);
      return var1;
   }

   public boolean func_148111_b() {
      return this.field_148119_h;
   }

   public static JsonBlendingMode func_148110_a(JsonObject var0) {
      if (var0 == null) {
         return new JsonBlendingMode();
      } else {
         int var1 = 32774;
         int var2 = 1;
         int var3 = 0;
         int var4 = 1;
         int var5 = 0;
         boolean var6 = true;
         boolean var7 = false;
         if (JsonUtils.func_151205_a(var0, "func")) {
            var1 = func_148108_a(var0.get("func").getAsString());
            if (var1 != 32774) {
               var6 = false;
            }
         }

         if (JsonUtils.func_151205_a(var0, "srcrgb")) {
            var2 = func_148107_b(var0.get("srcrgb").getAsString());
            if (var2 != 1) {
               var6 = false;
            }
         }

         if (JsonUtils.func_151205_a(var0, "dstrgb")) {
            var3 = func_148107_b(var0.get("dstrgb").getAsString());
            if (var3 != 0) {
               var6 = false;
            }
         }

         if (JsonUtils.func_151205_a(var0, "srcalpha")) {
            var4 = func_148107_b(var0.get("srcalpha").getAsString());
            if (var4 != 1) {
               var6 = false;
            }

            var7 = true;
         }

         if (JsonUtils.func_151205_a(var0, "dstalpha")) {
            var5 = func_148107_b(var0.get("dstalpha").getAsString());
            if (var5 != 0) {
               var6 = false;
            }

            var7 = true;
         }

         if (var6) {
            return new JsonBlendingMode();
         } else {
            return var7 ? new JsonBlendingMode(var2, var3, var4, var5, var1) : new JsonBlendingMode(var2, var3, var1);
         }
      }
   }

   private static int func_148108_a(String var0) {
      String var1 = var0.trim().toLowerCase();
      if (var1.equals("add")) {
         return 32774;
      } else if (var1.equals("subtract")) {
         return 32778;
      } else if (var1.equals("reversesubtract")) {
         return 32779;
      } else if (var1.equals("reverse_subtract")) {
         return 32779;
      } else if (var1.equals("min")) {
         return 32775;
      } else {
         return var1.equals("max") ? '\u8008' : '\u8006';
      }
   }

   private static int func_148107_b(String var0) {
      String var1 = var0.trim().toLowerCase();
      var1 = var1.replaceAll("_", "");
      var1 = var1.replaceAll("one", "1");
      var1 = var1.replaceAll("zero", "0");
      var1 = var1.replaceAll("minus", "-");
      if (var1.equals("0")) {
         return 0;
      } else if (var1.equals("1")) {
         return 1;
      } else if (var1.equals("srccolor")) {
         return 768;
      } else if (var1.equals("1-srccolor")) {
         return 769;
      } else if (var1.equals("dstcolor")) {
         return 774;
      } else if (var1.equals("1-dstcolor")) {
         return 775;
      } else if (var1.equals("srcalpha")) {
         return 770;
      } else if (var1.equals("1-srcalpha")) {
         return 771;
      } else if (var1.equals("dstalpha")) {
         return 772;
      } else {
         return var1.equals("1-dstalpha") ? 773 : -1;
      }
   }
}
