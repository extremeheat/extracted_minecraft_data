package net.minecraft.client.util;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IntHashMap;
import org.lwjgl.glfw.GLFW;

public class InputMappings {
   public static final InputMappings.Input field_197958_a;

   public static boolean func_197956_a(int var0) {
      return GLFW.glfwGetKey(Minecraft.func_71410_x().field_195558_d.func_198092_i(), var0) == 1;
   }

   public static InputMappings.Input func_197954_a(int var0, int var1) {
      return var0 == -1 ? InputMappings.Type.SCANCODE.func_197944_a(var1) : InputMappings.Type.KEYSYM.func_197944_a(var0);
   }

   public static InputMappings.Input func_197955_a(String var0) {
      if (InputMappings.Input.field_199875_d.containsKey(var0)) {
         return (InputMappings.Input)InputMappings.Input.field_199875_d.get(var0);
      } else {
         InputMappings.Type[] var1 = InputMappings.Type.values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            InputMappings.Type var4 = var1[var3];
            if (var0.startsWith(var4.field_197952_f)) {
               String var5 = var0.substring(var4.field_197952_f.length() + 1);
               return var4.func_197944_a(Integer.parseInt(var5));
            }
         }

         throw new IllegalArgumentException("Unknown key name: " + var0);
      }
   }

   static {
      field_197958_a = InputMappings.Type.KEYSYM.func_197944_a(-1);
   }

   public static final class Input {
      private final String field_197939_a;
      private final InputMappings.Type field_197940_b;
      private final int field_197941_c;
      private static final Map<String, InputMappings.Input> field_199875_d = Maps.newHashMap();

      private Input(String var1, InputMappings.Type var2, int var3) {
         super();
         this.field_197939_a = var1;
         this.field_197940_b = var2;
         this.field_197941_c = var3;
         field_199875_d.put(var1, this);
      }

      public String func_197936_a() {
         String var1 = null;
         switch(this.field_197940_b) {
         case KEYSYM:
            var1 = GLFW.glfwGetKeyName(this.field_197941_c, -1);
            break;
         case SCANCODE:
            var1 = GLFW.glfwGetKeyName(-1, this.field_197941_c);
            break;
         case MOUSE:
            String var2 = I18n.func_135052_a(this.field_197939_a);
            var1 = Objects.equals(var2, this.field_197939_a) ? I18n.func_135052_a(InputMappings.Type.MOUSE.field_197952_f, this.field_197941_c + 1) : var2;
         }

         return var1 == null ? I18n.func_135052_a(this.field_197939_a) : var1;
      }

      public InputMappings.Type func_197938_b() {
         return this.field_197940_b;
      }

      public int func_197937_c() {
         return this.field_197941_c;
      }

      public String func_197935_d() {
         return this.field_197939_a;
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            InputMappings.Input var2 = (InputMappings.Input)var1;
            return this.field_197941_c == var2.field_197941_c && this.field_197940_b == var2.field_197940_b;
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.field_197940_b, this.field_197941_c});
      }

      public String toString() {
         return this.field_197939_a;
      }

      // $FF: synthetic method
      Input(String var1, InputMappings.Type var2, int var3, Object var4) {
         this(var1, var2, var3);
      }
   }

   public static enum Type {
      KEYSYM("key.keyboard"),
      SCANCODE("scancode"),
      MOUSE("key.mouse");

      private static final String[] field_197950_d;
      private final IntHashMap<InputMappings.Input> field_197951_e = new IntHashMap();
      private final String field_197952_f;

      private static void func_197943_a(InputMappings.Type var0, String var1, int var2) {
         InputMappings.Input var3 = new InputMappings.Input(var1, var0, var2);
         var0.field_197951_e.func_76038_a(var2, var3);
      }

      private Type(String var3) {
         this.field_197952_f = var3;
      }

      public InputMappings.Input func_197944_a(int var1) {
         if (this.field_197951_e.func_76037_b(var1)) {
            return (InputMappings.Input)this.field_197951_e.func_76041_a(var1);
         } else {
            String var2;
            if (this == MOUSE) {
               if (var1 <= 2) {
                  var2 = "." + field_197950_d[var1];
               } else {
                  var2 = "." + (var1 + 1);
               }
            } else {
               var2 = "." + var1;
            }

            InputMappings.Input var3 = new InputMappings.Input(this.field_197952_f + var2, this, var1);
            this.field_197951_e.func_76038_a(var1, var3);
            return var3;
         }
      }

      static {
         func_197943_a(KEYSYM, "key.keyboard.unknown", -1);
         func_197943_a(MOUSE, "key.mouse.left", 0);
         func_197943_a(MOUSE, "key.mouse.right", 1);
         func_197943_a(MOUSE, "key.mouse.middle", 2);
         func_197943_a(MOUSE, "key.mouse.4", 3);
         func_197943_a(MOUSE, "key.mouse.5", 4);
         func_197943_a(MOUSE, "key.mouse.6", 5);
         func_197943_a(MOUSE, "key.mouse.7", 6);
         func_197943_a(MOUSE, "key.mouse.8", 7);
         func_197943_a(KEYSYM, "key.keyboard.0", 48);
         func_197943_a(KEYSYM, "key.keyboard.1", 49);
         func_197943_a(KEYSYM, "key.keyboard.2", 50);
         func_197943_a(KEYSYM, "key.keyboard.3", 51);
         func_197943_a(KEYSYM, "key.keyboard.4", 52);
         func_197943_a(KEYSYM, "key.keyboard.5", 53);
         func_197943_a(KEYSYM, "key.keyboard.6", 54);
         func_197943_a(KEYSYM, "key.keyboard.7", 55);
         func_197943_a(KEYSYM, "key.keyboard.8", 56);
         func_197943_a(KEYSYM, "key.keyboard.9", 57);
         func_197943_a(KEYSYM, "key.keyboard.a", 65);
         func_197943_a(KEYSYM, "key.keyboard.b", 66);
         func_197943_a(KEYSYM, "key.keyboard.c", 67);
         func_197943_a(KEYSYM, "key.keyboard.d", 68);
         func_197943_a(KEYSYM, "key.keyboard.e", 69);
         func_197943_a(KEYSYM, "key.keyboard.f", 70);
         func_197943_a(KEYSYM, "key.keyboard.g", 71);
         func_197943_a(KEYSYM, "key.keyboard.h", 72);
         func_197943_a(KEYSYM, "key.keyboard.i", 73);
         func_197943_a(KEYSYM, "key.keyboard.j", 74);
         func_197943_a(KEYSYM, "key.keyboard.k", 75);
         func_197943_a(KEYSYM, "key.keyboard.l", 76);
         func_197943_a(KEYSYM, "key.keyboard.m", 77);
         func_197943_a(KEYSYM, "key.keyboard.n", 78);
         func_197943_a(KEYSYM, "key.keyboard.o", 79);
         func_197943_a(KEYSYM, "key.keyboard.p", 80);
         func_197943_a(KEYSYM, "key.keyboard.q", 81);
         func_197943_a(KEYSYM, "key.keyboard.r", 82);
         func_197943_a(KEYSYM, "key.keyboard.s", 83);
         func_197943_a(KEYSYM, "key.keyboard.t", 84);
         func_197943_a(KEYSYM, "key.keyboard.u", 85);
         func_197943_a(KEYSYM, "key.keyboard.v", 86);
         func_197943_a(KEYSYM, "key.keyboard.w", 87);
         func_197943_a(KEYSYM, "key.keyboard.x", 88);
         func_197943_a(KEYSYM, "key.keyboard.y", 89);
         func_197943_a(KEYSYM, "key.keyboard.z", 90);
         func_197943_a(KEYSYM, "key.keyboard.f1", 290);
         func_197943_a(KEYSYM, "key.keyboard.f2", 291);
         func_197943_a(KEYSYM, "key.keyboard.f3", 292);
         func_197943_a(KEYSYM, "key.keyboard.f4", 293);
         func_197943_a(KEYSYM, "key.keyboard.f5", 294);
         func_197943_a(KEYSYM, "key.keyboard.f6", 295);
         func_197943_a(KEYSYM, "key.keyboard.f7", 296);
         func_197943_a(KEYSYM, "key.keyboard.f8", 297);
         func_197943_a(KEYSYM, "key.keyboard.f9", 298);
         func_197943_a(KEYSYM, "key.keyboard.f10", 299);
         func_197943_a(KEYSYM, "key.keyboard.f11", 300);
         func_197943_a(KEYSYM, "key.keyboard.f12", 301);
         func_197943_a(KEYSYM, "key.keyboard.f13", 302);
         func_197943_a(KEYSYM, "key.keyboard.f14", 303);
         func_197943_a(KEYSYM, "key.keyboard.f15", 304);
         func_197943_a(KEYSYM, "key.keyboard.f16", 305);
         func_197943_a(KEYSYM, "key.keyboard.f17", 306);
         func_197943_a(KEYSYM, "key.keyboard.f18", 307);
         func_197943_a(KEYSYM, "key.keyboard.f19", 308);
         func_197943_a(KEYSYM, "key.keyboard.f20", 309);
         func_197943_a(KEYSYM, "key.keyboard.f21", 310);
         func_197943_a(KEYSYM, "key.keyboard.f22", 311);
         func_197943_a(KEYSYM, "key.keyboard.f23", 312);
         func_197943_a(KEYSYM, "key.keyboard.f24", 313);
         func_197943_a(KEYSYM, "key.keyboard.f25", 314);
         func_197943_a(KEYSYM, "key.keyboard.num.lock", 282);
         func_197943_a(KEYSYM, "key.keyboard.keypad.0", 320);
         func_197943_a(KEYSYM, "key.keyboard.keypad.1", 321);
         func_197943_a(KEYSYM, "key.keyboard.keypad.2", 322);
         func_197943_a(KEYSYM, "key.keyboard.keypad.3", 323);
         func_197943_a(KEYSYM, "key.keyboard.keypad.4", 324);
         func_197943_a(KEYSYM, "key.keyboard.keypad.5", 325);
         func_197943_a(KEYSYM, "key.keyboard.keypad.6", 326);
         func_197943_a(KEYSYM, "key.keyboard.keypad.7", 327);
         func_197943_a(KEYSYM, "key.keyboard.keypad.8", 328);
         func_197943_a(KEYSYM, "key.keyboard.keypad.9", 329);
         func_197943_a(KEYSYM, "key.keyboard.keypad.add", 334);
         func_197943_a(KEYSYM, "key.keyboard.keypad.decimal", 330);
         func_197943_a(KEYSYM, "key.keyboard.keypad.enter", 335);
         func_197943_a(KEYSYM, "key.keyboard.keypad.equal", 336);
         func_197943_a(KEYSYM, "key.keyboard.keypad.multiply", 332);
         func_197943_a(KEYSYM, "key.keyboard.keypad.divide", 331);
         func_197943_a(KEYSYM, "key.keyboard.keypad.subtract", 333);
         func_197943_a(KEYSYM, "key.keyboard.down", 264);
         func_197943_a(KEYSYM, "key.keyboard.left", 263);
         func_197943_a(KEYSYM, "key.keyboard.right", 262);
         func_197943_a(KEYSYM, "key.keyboard.up", 265);
         func_197943_a(KEYSYM, "key.keyboard.apostrophe", 39);
         func_197943_a(KEYSYM, "key.keyboard.backslash", 92);
         func_197943_a(KEYSYM, "key.keyboard.comma", 44);
         func_197943_a(KEYSYM, "key.keyboard.equal", 61);
         func_197943_a(KEYSYM, "key.keyboard.grave.accent", 96);
         func_197943_a(KEYSYM, "key.keyboard.left.bracket", 91);
         func_197943_a(KEYSYM, "key.keyboard.minus", 45);
         func_197943_a(KEYSYM, "key.keyboard.period", 46);
         func_197943_a(KEYSYM, "key.keyboard.right.bracket", 93);
         func_197943_a(KEYSYM, "key.keyboard.semicolon", 59);
         func_197943_a(KEYSYM, "key.keyboard.slash", 47);
         func_197943_a(KEYSYM, "key.keyboard.space", 32);
         func_197943_a(KEYSYM, "key.keyboard.tab", 258);
         func_197943_a(KEYSYM, "key.keyboard.left.alt", 342);
         func_197943_a(KEYSYM, "key.keyboard.left.control", 341);
         func_197943_a(KEYSYM, "key.keyboard.left.shift", 340);
         func_197943_a(KEYSYM, "key.keyboard.left.win", 343);
         func_197943_a(KEYSYM, "key.keyboard.right.alt", 346);
         func_197943_a(KEYSYM, "key.keyboard.right.control", 345);
         func_197943_a(KEYSYM, "key.keyboard.right.shift", 344);
         func_197943_a(KEYSYM, "key.keyboard.right.win", 347);
         func_197943_a(KEYSYM, "key.keyboard.enter", 257);
         func_197943_a(KEYSYM, "key.keyboard.escape", 256);
         func_197943_a(KEYSYM, "key.keyboard.backspace", 259);
         func_197943_a(KEYSYM, "key.keyboard.delete", 261);
         func_197943_a(KEYSYM, "key.keyboard.end", 269);
         func_197943_a(KEYSYM, "key.keyboard.home", 268);
         func_197943_a(KEYSYM, "key.keyboard.insert", 260);
         func_197943_a(KEYSYM, "key.keyboard.page.down", 267);
         func_197943_a(KEYSYM, "key.keyboard.page.up", 266);
         func_197943_a(KEYSYM, "key.keyboard.caps.lock", 280);
         func_197943_a(KEYSYM, "key.keyboard.pause", 284);
         func_197943_a(KEYSYM, "key.keyboard.scroll.lock", 281);
         func_197943_a(KEYSYM, "key.keyboard.menu", 348);
         func_197943_a(KEYSYM, "key.keyboard.print.screen", 283);
         func_197943_a(KEYSYM, "key.keyboard.world.1", 161);
         func_197943_a(KEYSYM, "key.keyboard.world.2", 162);
         field_197950_d = new String[]{"left", "middle", "right"};
      }
   }
}
