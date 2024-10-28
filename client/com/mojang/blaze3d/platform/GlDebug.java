package com.mojang.blaze3d.platform;

import com.google.common.collect.EvictingQueue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import javax.annotation.Nullable;
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
   private static final Queue<LogEntry> MESSAGE_BUFFER = EvictingQueue.create(10);
   @Nullable
   private static volatile LogEntry lastEntry;
   private static final List<Integer> DEBUG_LEVELS = ImmutableList.of(37190, 37191, 37192, 33387);
   private static final List<Integer> DEBUG_LEVELS_ARB = ImmutableList.of(37190, 37191, 37192);
   private static boolean debugEnabled;

   public GlDebug() {
      super();
   }

   private static String printUnknownToken(int var0) {
      return "Unknown (0x" + Integer.toHexString(var0).toUpperCase() + ")";
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

   private static void printDebugLog(int var0, int var1, int var2, int var3, int var4, long var5, long var7) {
      String var9 = GLDebugMessageCallback.getMessage(var4, var5);
      LogEntry var10;
      synchronized(MESSAGE_BUFFER) {
         var10 = lastEntry;
         if (var10 != null && var10.isSame(var0, var1, var2, var3, var9)) {
            ++var10.count;
         } else {
            var10 = new LogEntry(var0, var1, var2, var3, var9);
            MESSAGE_BUFFER.add(var10);
            lastEntry = var10;
         }
      }

      LOGGER.info("OpenGL debug message: {}", var10);
   }

   public static List<String> getLastOpenGlDebugMessages() {
      synchronized(MESSAGE_BUFFER) {
         ArrayList var1 = Lists.newArrayListWithCapacity(MESSAGE_BUFFER.size());
         Iterator var2 = MESSAGE_BUFFER.iterator();

         while(var2.hasNext()) {
            LogEntry var3 = (LogEntry)var2.next();
            String var10001 = String.valueOf(var3);
            var1.add(var10001 + " x " + var3.count);
         }

         return var1;
      }
   }

   public static boolean isDebugEnabled() {
      return debugEnabled;
   }

   public static void enableDebugCallback(int var0, boolean var1) {
      RenderSystem.assertInInitPhase();
      if (var0 > 0) {
         GLCapabilities var2 = GL.getCapabilities();
         int var3;
         boolean var4;
         if (var2.GL_KHR_debug) {
            debugEnabled = true;
            GL11.glEnable(37600);
            if (var1) {
               GL11.glEnable(33346);
            }

            for(var3 = 0; var3 < DEBUG_LEVELS.size(); ++var3) {
               var4 = var3 < var0;
               KHRDebug.glDebugMessageControl(4352, 4352, (Integer)DEBUG_LEVELS.get(var3), (int[])null, var4);
            }

            KHRDebug.glDebugMessageCallback((GLDebugMessageCallbackI)GLX.make(GLDebugMessageCallback.create(GlDebug::printDebugLog), DebugMemoryUntracker::untrack), 0L);
         } else if (var2.GL_ARB_debug_output) {
            debugEnabled = true;
            if (var1) {
               GL11.glEnable(33346);
            }

            for(var3 = 0; var3 < DEBUG_LEVELS_ARB.size(); ++var3) {
               var4 = var3 < var0;
               ARBDebugOutput.glDebugMessageControlARB(4352, 4352, (Integer)DEBUG_LEVELS_ARB.get(var3), (int[])null, var4);
            }

            ARBDebugOutput.glDebugMessageCallbackARB((GLDebugMessageARBCallbackI)GLX.make(GLDebugMessageARBCallback.create(GlDebug::printDebugLog), DebugMemoryUntracker::untrack), 0L);
         }

      }
   }

   private static class LogEntry {
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
