package com.mojang.blaze3d.opengl;

import com.google.common.collect.EvictingQueue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
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
import org.lwjgl.opengl.GLDebugMessageCallback;
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

   private static String printUnknownToken(final int token) {
      return "Unknown (0x" + HexFormat.of().withUpperCase().toHexDigits(token) + ")";
   }

   public static String sourceToString(final int source) {
      switch (source) {
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
            return printUnknownToken(source);
         }
      }
   }

   public static String typeToString(final int type) {
      switch (type) {
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
            return printUnknownToken(type);
         }
      }
   }

   public static String severityToString(final int severity) {
      switch (severity) {
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
            return printUnknownToken(severity);
         }
      }
   }

   private void printDebugLog(final int source, final int type, final int id, final int severity, final int length, final long message, final long userParam) {
      String msg = GLDebugMessageCallback.getMessage(length, message);
      LogEntry entry;
      synchronized(this.MESSAGE_BUFFER) {
         entry = this.lastEntry;
         if (entry != null && entry.isSame(source, type, id, severity, msg)) {
            ++entry.count;
         } else {
            entry = new LogEntry(source, type, id, severity, msg);
            this.MESSAGE_BUFFER.add(entry);
            this.lastEntry = entry;
         }
      }

      LOGGER.info("OpenGL debug message: {}", entry);
   }

   public List<String> getLastOpenGlDebugMessages() {
      synchronized(this.MESSAGE_BUFFER) {
         List<String> result = Lists.newArrayListWithCapacity(this.MESSAGE_BUFFER.size());

         for(LogEntry e : this.MESSAGE_BUFFER) {
            String var10001 = String.valueOf(e);
            result.add(var10001 + " x " + e.count);
         }

         return result;
      }
   }

   public static @Nullable GlDebug enableDebugCallback(final int verbosity, final boolean debugSynchronousGlLogs, final Set<String> enabledExtensions) {
      if (verbosity <= 0) {
         return null;
      } else {
         GLCapabilities caps = GL.getCapabilities();
         if (caps.GL_KHR_debug && GlDevice.USE_GL_KHR_debug) {
            GlDebug debug = new GlDebug();
            enabledExtensions.add("GL_KHR_debug");
            GL11.glEnable(37600);
            if (debugSynchronousGlLogs) {
               GL11.glEnable(33346);
            }

            for(int i = 0; i < DEBUG_LEVELS.size(); ++i) {
               boolean isEnabled = i < verbosity;
               KHRDebug.glDebugMessageControl(4352, 4352, (Integer)DEBUG_LEVELS.get(i), (int[])null, isEnabled);
            }

            Objects.requireNonNull(debug);
            KHRDebug.glDebugMessageCallback(GLDebugMessageCallback.create(debug::printDebugLog), 0L);
            return debug;
         } else if (caps.GL_ARB_debug_output && GlDevice.USE_GL_ARB_debug_output) {
            GlDebug debug = new GlDebug();
            enabledExtensions.add("GL_ARB_debug_output");
            if (debugSynchronousGlLogs) {
               GL11.glEnable(33346);
            }

            for(int i = 0; i < DEBUG_LEVELS_ARB.size(); ++i) {
               boolean isEnabled = i < verbosity;
               ARBDebugOutput.glDebugMessageControlARB(4352, 4352, (Integer)DEBUG_LEVELS_ARB.get(i), (int[])null, isEnabled);
            }

            Objects.requireNonNull(debug);
            ARBDebugOutput.glDebugMessageCallbackARB(GLDebugMessageARBCallback.create(debug::printDebugLog), 0L);
            return debug;
         } else {
            return null;
         }
      }
   }

   private static class LogEntry {
      private final int id;
      private final int source;
      private final int type;
      private final int severity;
      private final String message;
      private int count = 1;

      private LogEntry(final int source, final int type, final int id, final int severity, final String message) {
         super();
         this.id = id;
         this.source = source;
         this.type = type;
         this.severity = severity;
         this.message = message;
      }

      private boolean isSame(final int source, final int type, final int id, final int severity, final String message) {
         return type == this.type && source == this.source && id == this.id && severity == this.severity && message.equals(this.message);
      }

      public String toString() {
         int var10000 = this.id;
         return "id=" + var10000 + ", source=" + GlDebug.sourceToString(this.source) + ", type=" + GlDebug.typeToString(this.type) + ", severity=" + GlDebug.severityToString(this.severity) + ", message='" + this.message + "'";
      }
   }
}
