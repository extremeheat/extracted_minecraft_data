package com.mojang.blaze3d.platform;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWVidMode.Buffer;

public final class VideoMode {
   private final int width;
   private final int height;
   private final int redBits;
   private final int greenBits;
   private final int blueBits;
   private final int refreshRate;
   private static final Pattern PATTERN = Pattern.compile("(\\d+)x(\\d+)(?:@(\\d+)(?::(\\d+))?)?");

   public VideoMode(int var1, int var2, int var3, int var4, int var5, int var6) {
      super();
      this.width = var1;
      this.height = var2;
      this.redBits = var3;
      this.greenBits = var4;
      this.blueBits = var5;
      this.refreshRate = var6;
   }

   public VideoMode(Buffer var1) {
      super();
      this.width = var1.width();
      this.height = var1.height();
      this.redBits = var1.redBits();
      this.greenBits = var1.greenBits();
      this.blueBits = var1.blueBits();
      this.refreshRate = var1.refreshRate();
   }

   public VideoMode(GLFWVidMode var1) {
      super();
      this.width = var1.width();
      this.height = var1.height();
      this.redBits = var1.redBits();
      this.greenBits = var1.greenBits();
      this.blueBits = var1.blueBits();
      this.refreshRate = var1.refreshRate();
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public int getRedBits() {
      return this.redBits;
   }

   public int getGreenBits() {
      return this.greenBits;
   }

   public int getBlueBits() {
      return this.blueBits;
   }

   public int getRefreshRate() {
      return this.refreshRate;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         VideoMode var2 = (VideoMode)var1;
         return this.width == var2.width
            && this.height == var2.height
            && this.redBits == var2.redBits
            && this.greenBits == var2.greenBits
            && this.blueBits == var2.blueBits
            && this.refreshRate == var2.refreshRate;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.width, this.height, this.redBits, this.greenBits, this.blueBits, this.refreshRate);
   }

   @Override
   public String toString() {
      return String.format("%sx%s@%s (%sbit)", this.width, this.height, this.refreshRate, this.redBits + this.greenBits + this.blueBits);
   }

   public static Optional<VideoMode> read(@Nullable String var0) {
      if (var0 == null) {
         return Optional.empty();
      } else {
         try {
            Matcher var1 = PATTERN.matcher(var0);
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
   }

   public String write() {
      return String.format("%sx%s@%s:%s", this.width, this.height, this.refreshRate, this.redBits + this.greenBits + this.blueBits);
   }
}
