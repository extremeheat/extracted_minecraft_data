package com.mojang.blaze3d.opengl;

import com.google.common.collect.EvictingQueue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.DebugMemoryUntracker;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import org.jspecify.annotations.Nullable;
import org.lwjgl.opengl.ARBDebugOutput;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.GLDebugMessageARBCallback;
import org.lwjgl.opengl.GLDebugMessageARBCallbackI;
import org.lwjgl.opengl.GLDebugMessageCallback;
import org.lwjgl.opengl.GLDebugMessageCallbackI;
import org.lwjgl.opengl.KHRDebug;
import org.slf4j.Logger;

public class GlDebug {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int CIRCULAR_LOG_SIZE = 10;
   private final Queue<LogEntry> MESSAGE_BUFFER = EvictingQueue.create(10);
   private volatile @Nullable LogEntry lastEntry;
   private static final List<Integer> DEBUG_LEVELS = ImmutableList.of(37190, 37191, 37192, 33387);
   private static final List<Integer> DEBUG_LEVELS_ARB = ImmutableList.of(37190, 37191, 37192);

   public GlDebug() {
      super();
   }

   private static String printUnknownToken(int var0) {
      return "Unknown (0x" + HexFormat.of().withUpperCase().toHexDigits(var0) + ")";
   }

   public static String sourceToString(int var0) {
      switch (var0) {
         case 33350 -> {
            return "API";
         }
         case 33351 -> {
            return "WINDOW SYSTEM";
         }
         case 33352 -> {
            return "SHADER COMPILER";
         }
         case 33353 -> {
            return "THIRD PARTY";
         }
         case 33354 -> {
            return "APPLICATION";
         }
         case 33355 -> {
            return "OTHER";
         }
         default -> {
            return printUnknownToken(var0);
         }
      }
   }

   public static String typeToString(int var0) {
      switch (var0) {
         case 33356 -> {
            return "ERROR";
         }
         case 33357 -> {
            return "DEPRECATED BEHAVIOR";
         }
         case 33358 -> {
            return "UNDEFINED BEHAVIOR";
         }
         case 33359 -> {
            return "PORTABILITY";
         }
         case 33360 -> {
            return "PERFORMANCE";
         }
         case 33361 -> {
            return "OTHER";
         }
         case 33384 -> {
            return "MARKER";
         }
         default -> {
            return printUnknownToken(var0);
         }
      }
   }

   public static String severityToString(int var0) {
      switch (var0) {
         case 33387 -> {
            return "NOTIFICATION";
         }
         case 37190 -> {
            return "HIGH";
         }
         case 37191 -> {
            return "MEDIUM";
         }
         case 37192 -> {
            return "LOW";
         }
         default -> {
            return printUnknownToken(var0);
         }
      }
   }

   private void printDebugLog(int var1, int var2, int var3, int var4, int var5, long var6, long var8) {
      String var10 = GLDebugMessageCallback.getMessage(var5, var6);
      LogEntry var11;
      synchronized(this.MESSAGE_BUFFER) {
         var11 = this.lastEntry;
         if (var11 != null && var11.isSame(var1, var2, var3, var4, var10)) {
            ++var11.count;
         } else {
            var11 = new LogEntry(var1, var2, var3, var4, var10);
            this.MESSAGE_BUFFER.add(var11);
            this.lastEntry = var11;
         }
      }

      LOGGER.info("OpenGL debug message: {}", var11);
   }

   public List<String> getLastOpenGlDebugMessages() {
      synchronized(this.MESSAGE_BUFFER) {
         ArrayList var2 = Lists.newArrayListWithCapacity(this.MESSAGE_BUFFER.size());

         for(LogEntry var4 : this.MESSAGE_BUFFER) {
            String var10001 = String.valueOf(var4);
            var2.add(var10001 + " x " + var4.count);
         }

         return var2;
      }
   }

   public static @Nullable GlDebug enableDebugCallback(int var0, boolean var1, Set<String> var2) {
      if (var0 <= 0) {
         return null;
      } else {
         GLCapabilities var3 = GL.getCapabilities();
         if (var3.GL_KHR_debug && GlDevice.USE_GL_KHR_debug) {
            GlDebug var7 = new GlDebug();
            var2.add("GL_KHR_debug");
            GL11.glEnable(37600);
            if (var1) {
               GL11.glEnable(33346);
            }

            for(int var8 = 0; var8 < DEBUG_LEVELS.size(); ++var8) {
               boolean var9 = var8 < var0;
               KHRDebug.glDebugMessageControl(4352, 4352, (Integer)DEBUG_LEVELS.get(var8), (int[])null, var9);
            }

            Objects.requireNonNull(var7);
            KHRDebug.glDebugMessageCallback((GLDebugMessageCallbackI)GLX.make(GLDebugMessageCallback.create(var7::printDebugLog), DebugMemoryUntracker::untrack), 0L);
            return var7;
         } else if (var3.GL_ARB_debug_output && GlDevice.USE_GL_ARB_debug_output) {
            GlDebug var4 = new GlDebug();
            var2.add("GL_ARB_debug_output");
            if (var1) {
               GL11.glEnable(33346);
            }

            for(int var5 = 0; var5 < DEBUG_LEVELS_ARB.size(); ++var5) {
               boolean var6 = var5 < var0;
               ARBDebugOutput.glDebugMessageControlARB(4352, 4352, (Integer)DEBUG_LEVELS_ARB.get(var5), (int[])null, var6);
            }

            Objects.requireNonNull(var4);
            ARBDebugOutput.glDebugMessageCallbackARB((GLDebugMessageARBCallbackI)GLX.make(GLDebugMessageARBCallback.create(var4::printDebugLog), DebugMemoryUntracker::untrack), 0L);
            return var4;
         } else {
            return null;
         }
      }
   }

   static class LogEntry {
      private final int id;
      private final int source;
      private final int type;
      private final int severity;
      private final String message;
      int count = 1;

      LogEntry(int var1, int var2, int var3, int var4, String var5) {
         super();
         this.id = var3;
         this.source = var1;
         this.type = var2;
         this.severity = var4;
         this.message = var5;
      }

      boolean isSame(int var1, int var2, int var3, int var4, String var5) {
         return var2 == this.type && var1 == this.source && var3 == this.id && var4 == this.severity && var5.equals(this.message);
      }

      public String toString() {
         int var10000 = this.id;
         return "id=" + var10000 + ", source=" + GlDebug.sourceToString(this.source) + ", type=" + GlDebug.typeToString(this.type) + ", severity=" + GlDebug.severityToString(this.severity) + ", message='" + this.message + "'";
      }
   }
}
