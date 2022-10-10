package net.minecraft.client.renderer;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWVidMode.Buffer;

public final class VideoMode {
   private final int field_198069_a;
   private final int field_198070_b;
   private final int field_198071_c;
   private final int field_198072_d;
   private final int field_198073_e;
   private final int field_198074_f;
   private static final Pattern field_198075_g = Pattern.compile("(\\d+)x(\\d+)(?:@(\\d+)(?::(\\d+))?)?");

   public VideoMode(int var1, int var2, int var3, int var4, int var5, int var6) {
      super();
      this.field_198069_a = var1;
      this.field_198070_b = var2;
      this.field_198071_c = var3;
      this.field_198072_d = var4;
      this.field_198073_e = var5;
      this.field_198074_f = var6;
   }

   public VideoMode(Buffer var1) {
      super();
      this.field_198069_a = var1.width();
      this.field_198070_b = var1.height();
      this.field_198071_c = var1.redBits();
      this.field_198072_d = var1.greenBits();
      this.field_198073_e = var1.blueBits();
      this.field_198074_f = var1.refreshRate();
   }

   public VideoMode(GLFWVidMode var1) {
      super();
      this.field_198069_a = var1.width();
      this.field_198070_b = var1.height();
      this.field_198071_c = var1.redBits();
      this.field_198072_d = var1.greenBits();
      this.field_198073_e = var1.blueBits();
      this.field_198074_f = var1.refreshRate();
   }

   public int func_198064_a() {
      return this.field_198069_a;
   }

   public int func_198065_b() {
      return this.field_198070_b;
   }

   public int func_198062_c() {
      return this.field_198071_c;
   }

   public int func_198063_d() {
      return this.field_198072_d;
   }

   public int func_198068_e() {
      return this.field_198073_e;
   }

   public int func_198067_f() {
      return this.field_198074_f;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         VideoMode var2 = (VideoMode)var1;
         return this.field_198069_a == var2.field_198069_a && this.field_198070_b == var2.field_198070_b && this.field_198071_c == var2.field_198071_c && this.field_198072_d == var2.field_198072_d && this.field_198073_e == var2.field_198073_e && this.field_198074_f == var2.field_198074_f;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.field_198069_a, this.field_198070_b, this.field_198071_c, this.field_198072_d, this.field_198073_e, this.field_198074_f});
   }

   public String toString() {
      return String.format("%sx%s@%s (%sbit)", this.field_198069_a, this.field_198070_b, this.field_198074_f, this.field_198071_c + this.field_198072_d + this.field_198073_e);
   }

   public static Optional<VideoMode> func_198061_a(String var0) {
      try {
         Matcher var1 = field_198075_g.matcher(var0);
         if (var1.matches()) {
            int var2 = Integer.parseInt(var1.group(1));
            int var3 = Integer.parseInt(var1.group(2));
            String var4 = var1.group(3);
            int var5;
            if (var4 == null) {
               var5 = 60;
            } else {
               var5 = Integer.parseInt(var4);
            }

            String var6 = var1.group(4);
            int var7;
            if (var6 == null) {
               var7 = 24;
            } else {
               var7 = Integer.parseInt(var6);
            }

            int var8 = var7 / 3;
            return Optional.of(new VideoMode(var2, var3, var8, var8, var8, var5));
         }
      } catch (Exception var9) {
      }

      return Optional.empty();
   }

   public String func_198066_g() {
      return String.format("%sx%s@%s:%s", this.field_198069_a, this.field_198070_b, this.field_198074_f, this.field_198071_c + this.field_198072_d + this.field_198073_e);
   }
}
