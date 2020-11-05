package com.mojang.blaze3d.platform;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.LazyLoadedValue;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharModsCallbackI;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWDropCallbackI;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.glfw.GLFWScrollCallbackI;

public class InputConstants {
   @Nullable
   private static final MethodHandle glfwRawMouseMotionSupported;
   private static final int GLFW_RAW_MOUSE_MOTION;
   public static final InputConstants.Key UNKNOWN;

   public static InputConstants.Key getKey(int var0, int var1) {
      return var0 == -1 ? InputConstants.Type.SCANCODE.getOrCreate(var1) : InputConstants.Type.KEYSYM.getOrCreate(var0);
   }

   public static InputConstants.Key getKey(String var0) {
      if (InputConstants.Key.NAME_MAP.containsKey(var0)) {
         return (InputConstants.Key)InputConstants.Key.NAME_MAP.get(var0);
      } else {
         InputConstants.Type[] var1 = InputConstants.Type.values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            InputConstants.Type var4 = var1[var3];
            if (var0.startsWith(var4.defaultPrefix)) {
               String var5 = var0.substring(var4.defaultPrefix.length() + 1);
               return var4.getOrCreate(Integer.parseInt(var5));
            }
         }

         throw new IllegalArgumentException("Unknown key name: " + var0);
      }
   }

   public static boolean isKeyDown(long var0, int var2) {
      return GLFW.glfwGetKey(var0, var2) == 1;
   }

   public static void setupKeyboardCallbacks(long var0, GLFWKeyCallbackI var2, GLFWCharModsCallbackI var3) {
      GLFW.glfwSetKeyCallback(var0, var2);
      GLFW.glfwSetCharModsCallback(var0, var3);
   }

   public static void setupMouseCallbacks(long var0, GLFWCursorPosCallbackI var2, GLFWMouseButtonCallbackI var3, GLFWScrollCallbackI var4, GLFWDropCallbackI var5) {
      GLFW.glfwSetCursorPosCallback(var0, var2);
      GLFW.glfwSetMouseButtonCallback(var0, var3);
      GLFW.glfwSetScrollCallback(var0, var4);
      GLFW.glfwSetDropCallback(var0, var5);
   }

   public static void grabOrReleaseMouse(long var0, int var2, double var3, double var5) {
      GLFW.glfwSetCursorPos(var0, var3, var5);
      GLFW.glfwSetInputMode(var0, 208897, var2);
   }

   public static boolean isRawMouseInputSupported() {
      try {
         return glfwRawMouseMotionSupported != null && glfwRawMouseMotionSupported.invokeExact();
      } catch (Throwable var1) {
         throw new RuntimeException(var1);
      }
   }

   public static void updateRawMouseInput(long var0, boolean var2) {
      if (isRawMouseInputSupported()) {
         GLFW.glfwSetInputMode(var0, GLFW_RAW_MOUSE_MOTION, var2 ? 1 : 0);
      }

   }

   static {
      Lookup var0 = MethodHandles.lookup();
      MethodType var1 = MethodType.methodType(Boolean.TYPE);
      MethodHandle var2 = null;
      int var3 = 0;

      try {
         var2 = var0.findStatic(GLFW.class, "glfwRawMouseMotionSupported", var1);
         MethodHandle var4 = var0.findStaticGetter(GLFW.class, "GLFW_RAW_MOUSE_MOTION", Integer.TYPE);
         var3 = var4.invokeExact();
      } catch (NoSuchFieldException | NoSuchMethodException var5) {
      } catch (Throwable var6) {
         throw new RuntimeException(var6);
      }

      glfwRawMouseMotionSupported = var2;
      GLFW_RAW_MOUSE_MOTION = var3;
      UNKNOWN = InputConstants.Type.KEYSYM.getOrCreate(-1);
   }

   public static final class Key {
      private final String name;
      private final InputConstants.Type type;
      private final int value;
      private final LazyLoadedValue<Component> displayName;
      private static final Map<String, InputConstants.Key> NAME_MAP = Maps.newHashMap();

      private Key(String var1, InputConstants.Type var2, int var3) {
         super();
         this.name = var1;
         this.type = var2;
         this.value = var3;
         this.displayName = new LazyLoadedValue(() -> {
            return (Component)var2.displayTextSupplier.apply(var3, var1);
         });
         NAME_MAP.put(var1, this);
      }

      public InputConstants.Type getType() {
         return this.type;
      }

      public int getValue() {
         return this.value;
      }

      public String getName() {
         return this.name;
      }

      public Component getDisplayName() {
         return (Component)this.displayName.get();
      }

      public OptionalInt getNumericKeyValue() {
         if (this.value >= 48 && this.value <= 57) {
            return OptionalInt.of(this.value - 48);
         } else {
            return this.value >= 320 && this.value <= 329 ? OptionalInt.of(this.value - 320) : OptionalInt.empty();
         }
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            InputConstants.Key var2 = (InputConstants.Key)var1;
            return this.value == var2.value && this.type == var2.type;
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.type, this.value});
      }

      public String toString() {
         return this.name;
      }

      // $FF: synthetic method
      Key(String var1, InputConstants.Type var2, int var3, Object var4) {
         this(var1, var2, var3);
      }
   }

   public static enum Type {
      KEYSYM("key.keyboard", (var0, var1) -> {
         String var2 = GLFW.glfwGetKeyName(var0, -1);
         return (Component)(var2 != null ? new TextComponent(var2) : new TranslatableComponent(var1));
      }),
      SCANCODE("scancode", (var0, var1) -> {
         String var2 = GLFW.glfwGetKeyName(-1, var0);
         return (Component)(var2 != null ? new TextComponent(var2) : new TranslatableComponent(var1));
      }),
      MOUSE("key.mouse", (var0, var1) -> {
         return Language.getInstance().has(var1) ? new TranslatableComponent(var1) : new TranslatableComponent("key.mouse", new Object[]{var0 + 1});
      });

      private final Int2ObjectMap<InputConstants.Key> map = new Int2ObjectOpenHashMap();
      private final String defaultPrefix;
      private final BiFunction<Integer, String, Component> displayTextSupplier;

      private static void addKey(InputConstants.Type var0, String var1, int var2) {
         InputConstants.Key var3 = new InputConstants.Key(var1, var0, var2);
         var0.map.put(var2, var3);
      }

      private Type(String var3, BiFunction<Integer, String, Component> var4) {
         this.defaultPrefix = var3;
         this.displayTextSupplier = var4;
      }

      public InputConstants.Key getOrCreate(int var1) {
         return (InputConstants.Key)this.map.computeIfAbsent(var1, (var1x) -> {
            int var2 = var1x;
            if (this == MOUSE) {
               var2 = var1x + 1;
            }

            String var3 = this.defaultPrefix + "." + var2;
            return new InputConstants.Key(var3, this, var1x);
         });
      }

      static {
         addKey(KEYSYM, "key.keyboard.unknown", -1);
         addKey(MOUSE, "key.mouse.left", 0);
         addKey(MOUSE, "key.mouse.right", 1);
         addKey(MOUSE, "key.mouse.middle", 2);
         addKey(MOUSE, "key.mouse.4", 3);
         addKey(MOUSE, "key.mouse.5", 4);
         addKey(MOUSE, "key.mouse.6", 5);
         addKey(MOUSE, "key.mouse.7", 6);
         addKey(MOUSE, "key.mouse.8", 7);
         addKey(KEYSYM, "key.keyboard.0", 48);
         addKey(KEYSYM, "key.keyboard.1", 49);
         addKey(KEYSYM, "key.keyboard.2", 50);
         addKey(KEYSYM, "key.keyboard.3", 51);
         addKey(KEYSYM, "key.keyboard.4", 52);
         addKey(KEYSYM, "key.keyboard.5", 53);
         addKey(KEYSYM, "key.keyboard.6", 54);
         addKey(KEYSYM, "key.keyboard.7", 55);
         addKey(KEYSYM, "key.keyboard.8", 56);
         addKey(KEYSYM, "key.keyboard.9", 57);
         addKey(KEYSYM, "key.keyboard.a", 65);
         addKey(KEYSYM, "key.keyboard.b", 66);
         addKey(KEYSYM, "key.keyboard.c", 67);
         addKey(KEYSYM, "key.keyboard.d", 68);
         addKey(KEYSYM, "key.keyboard.e", 69);
         addKey(KEYSYM, "key.keyboard.f", 70);
         addKey(KEYSYM, "key.keyboard.g", 71);
         addKey(KEYSYM, "key.keyboard.h", 72);
         addKey(KEYSYM, "key.keyboard.i", 73);
         addKey(KEYSYM, "key.keyboard.j", 74);
         addKey(KEYSYM, "key.keyboard.k", 75);
         addKey(KEYSYM, "key.keyboard.l", 76);
         addKey(KEYSYM, "key.keyboard.m", 77);
         addKey(KEYSYM, "key.keyboard.n", 78);
         addKey(KEYSYM, "key.keyboard.o", 79);
         addKey(KEYSYM, "key.keyboard.p", 80);
         addKey(KEYSYM, "key.keyboard.q", 81);
         addKey(KEYSYM, "key.keyboard.r", 82);
         addKey(KEYSYM, "key.keyboard.s", 83);
         addKey(KEYSYM, "key.keyboard.t", 84);
         addKey(KEYSYM, "key.keyboard.u", 85);
         addKey(KEYSYM, "key.keyboard.v", 86);
         addKey(KEYSYM, "key.keyboard.w", 87);
         addKey(KEYSYM, "key.keyboard.x", 88);
         addKey(KEYSYM, "key.keyboard.y", 89);
         addKey(KEYSYM, "key.keyboard.z", 90);
         addKey(KEYSYM, "key.keyboard.f1", 290);
         addKey(KEYSYM, "key.keyboard.f2", 291);
         addKey(KEYSYM, "key.keyboard.f3", 292);
         addKey(KEYSYM, "key.keyboard.f4", 293);
         addKey(KEYSYM, "key.keyboard.f5", 294);
         addKey(KEYSYM, "key.keyboard.f6", 295);
         addKey(KEYSYM, "key.keyboard.f7", 296);
         addKey(KEYSYM, "key.keyboard.f8", 297);
         addKey(KEYSYM, "key.keyboard.f9", 298);
         addKey(KEYSYM, "key.keyboard.f10", 299);
         addKey(KEYSYM, "key.keyboard.f11", 300);
         addKey(KEYSYM, "key.keyboard.f12", 301);
         addKey(KEYSYM, "key.keyboard.f13", 302);
         addKey(KEYSYM, "key.keyboard.f14", 303);
         addKey(KEYSYM, "key.keyboard.f15", 304);
         addKey(KEYSYM, "key.keyboard.f16", 305);
         addKey(KEYSYM, "key.keyboard.f17", 306);
         addKey(KEYSYM, "key.keyboard.f18", 307);
         addKey(KEYSYM, "key.keyboard.f19", 308);
         addKey(KEYSYM, "key.keyboard.f20", 309);
         addKey(KEYSYM, "key.keyboard.f21", 310);
         addKey(KEYSYM, "key.keyboard.f22", 311);
         addKey(KEYSYM, "key.keyboard.f23", 312);
         addKey(KEYSYM, "key.keyboard.f24", 313);
         addKey(KEYSYM, "key.keyboard.f25", 314);
         addKey(KEYSYM, "key.keyboard.num.lock", 282);
         addKey(KEYSYM, "key.keyboard.keypad.0", 320);
         addKey(KEYSYM, "key.keyboard.keypad.1", 321);
         addKey(KEYSYM, "key.keyboard.keypad.2", 322);
         addKey(KEYSYM, "key.keyboard.keypad.3", 323);
         addKey(KEYSYM, "key.keyboard.keypad.4", 324);
         addKey(KEYSYM, "key.keyboard.keypad.5", 325);
         addKey(KEYSYM, "key.keyboard.keypad.6", 326);
         addKey(KEYSYM, "key.keyboard.keypad.7", 327);
         addKey(KEYSYM, "key.keyboard.keypad.8", 328);
         addKey(KEYSYM, "key.keyboard.keypad.9", 329);
         addKey(KEYSYM, "key.keyboard.keypad.add", 334);
         addKey(KEYSYM, "key.keyboard.keypad.decimal", 330);
         addKey(KEYSYM, "key.keyboard.keypad.enter", 335);
         addKey(KEYSYM, "key.keyboard.keypad.equal", 336);
         addKey(KEYSYM, "key.keyboard.keypad.multiply", 332);
         addKey(KEYSYM, "key.keyboard.keypad.divide", 331);
         addKey(KEYSYM, "key.keyboard.keypad.subtract", 333);
         addKey(KEYSYM, "key.keyboard.down", 264);
         addKey(KEYSYM, "key.keyboard.left", 263);
         addKey(KEYSYM, "key.keyboard.right", 262);
         addKey(KEYSYM, "key.keyboard.up", 265);
         addKey(KEYSYM, "key.keyboard.apostrophe", 39);
         addKey(KEYSYM, "key.keyboard.backslash", 92);
         addKey(KEYSYM, "key.keyboard.comma", 44);
         addKey(KEYSYM, "key.keyboard.equal", 61);
         addKey(KEYSYM, "key.keyboard.grave.accent", 96);
         addKey(KEYSYM, "key.keyboard.left.bracket", 91);
         addKey(KEYSYM, "key.keyboard.minus", 45);
         addKey(KEYSYM, "key.keyboard.period", 46);
         addKey(KEYSYM, "key.keyboard.right.bracket", 93);
         addKey(KEYSYM, "key.keyboard.semicolon", 59);
         addKey(KEYSYM, "key.keyboard.slash", 47);
         addKey(KEYSYM, "key.keyboard.space", 32);
         addKey(KEYSYM, "key.keyboard.tab", 258);
         addKey(KEYSYM, "key.keyboard.left.alt", 342);
         addKey(KEYSYM, "key.keyboard.left.control", 341);
         addKey(KEYSYM, "key.keyboard.left.shift", 340);
         addKey(KEYSYM, "key.keyboard.left.win", 343);
         addKey(KEYSYM, "key.keyboard.right.alt", 346);
         addKey(KEYSYM, "key.keyboard.right.control", 345);
         addKey(KEYSYM, "key.keyboard.right.shift", 344);
         addKey(KEYSYM, "key.keyboard.right.win", 347);
         addKey(KEYSYM, "key.keyboard.enter", 257);
         addKey(KEYSYM, "key.keyboard.escape", 256);
         addKey(KEYSYM, "key.keyboard.backspace", 259);
         addKey(KEYSYM, "key.keyboard.delete", 261);
         addKey(KEYSYM, "key.keyboard.end", 269);
         addKey(KEYSYM, "key.keyboard.home", 268);
         addKey(KEYSYM, "key.keyboard.insert", 260);
         addKey(KEYSYM, "key.keyboard.page.down", 267);
         addKey(KEYSYM, "key.keyboard.page.up", 266);
         addKey(KEYSYM, "key.keyboard.caps.lock", 280);
         addKey(KEYSYM, "key.keyboard.pause", 284);
         addKey(KEYSYM, "key.keyboard.scroll.lock", 281);
         addKey(KEYSYM, "key.keyboard.menu", 348);
         addKey(KEYSYM, "key.keyboard.print.screen", 283);
         addKey(KEYSYM, "key.keyboard.world.1", 161);
         addKey(KEYSYM, "key.keyboard.world.2", 162);
      }
   }
}
