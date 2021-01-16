package io.netty.handler.codec.compression;

import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public final class ZlibCodecFactory {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ZlibCodecFactory.class);
   private static final int DEFAULT_JDK_WINDOW_SIZE = 15;
   private static final int DEFAULT_JDK_MEM_LEVEL = 8;
   private static final boolean noJdkZlibDecoder = SystemPropertyUtil.getBoolean("io.netty.noJdkZlibDecoder", PlatformDependent.javaVersion() < 7);
   private static final boolean noJdkZlibEncoder;
   private static final boolean supportsWindowSizeAndMemLevel;

   public static boolean isSupportingWindowSizeAndMemLevel() {
      return supportsWindowSizeAndMemLevel;
   }

   public static ZlibEncoder newZlibEncoder(int var0) {
      return (ZlibEncoder)(PlatformDependent.javaVersion() >= 7 && !noJdkZlibEncoder ? new JdkZlibEncoder(var0) : new JZlibEncoder(var0));
   }

   public static ZlibEncoder newZlibEncoder(ZlibWrapper var0) {
      return (ZlibEncoder)(PlatformDependent.javaVersion() >= 7 && !noJdkZlibEncoder ? new JdkZlibEncoder(var0) : new JZlibEncoder(var0));
   }

   public static ZlibEncoder newZlibEncoder(ZlibWrapper var0, int var1) {
      return (ZlibEncoder)(PlatformDependent.javaVersion() >= 7 && !noJdkZlibEncoder ? new JdkZlibEncoder(var0, var1) : new JZlibEncoder(var0, var1));
   }

   public static ZlibEncoder newZlibEncoder(ZlibWrapper var0, int var1, int var2, int var3) {
      return (ZlibEncoder)(PlatformDependent.javaVersion() >= 7 && !noJdkZlibEncoder && var2 == 15 && var3 == 8 ? new JdkZlibEncoder(var0, var1) : new JZlibEncoder(var0, var1, var2, var3));
   }

   public static ZlibEncoder newZlibEncoder(byte[] var0) {
      return (ZlibEncoder)(PlatformDependent.javaVersion() >= 7 && !noJdkZlibEncoder ? new JdkZlibEncoder(var0) : new JZlibEncoder(var0));
   }

   public static ZlibEncoder newZlibEncoder(int var0, byte[] var1) {
      return (ZlibEncoder)(PlatformDependent.javaVersion() >= 7 && !noJdkZlibEncoder ? new JdkZlibEncoder(var0, var1) : new JZlibEncoder(var0, var1));
   }

   public static ZlibEncoder newZlibEncoder(int var0, int var1, int var2, byte[] var3) {
      return (ZlibEncoder)(PlatformDependent.javaVersion() >= 7 && !noJdkZlibEncoder && var1 == 15 && var2 == 8 ? new JdkZlibEncoder(var0, var3) : new JZlibEncoder(var0, var1, var2, var3));
   }

   public static ZlibDecoder newZlibDecoder() {
      return (ZlibDecoder)(PlatformDependent.javaVersion() >= 7 && !noJdkZlibDecoder ? new JdkZlibDecoder(true) : new JZlibDecoder());
   }

   public static ZlibDecoder newZlibDecoder(ZlibWrapper var0) {
      return (ZlibDecoder)(PlatformDependent.javaVersion() >= 7 && !noJdkZlibDecoder ? new JdkZlibDecoder(var0, true) : new JZlibDecoder(var0));
   }

   public static ZlibDecoder newZlibDecoder(byte[] var0) {
      return (ZlibDecoder)(PlatformDependent.javaVersion() >= 7 && !noJdkZlibDecoder ? new JdkZlibDecoder(var0) : new JZlibDecoder(var0));
   }

   private ZlibCodecFactory() {
      super();
   }

   static {
      logger.debug("-Dio.netty.noJdkZlibDecoder: {}", (Object)noJdkZlibDecoder);
      noJdkZlibEncoder = SystemPropertyUtil.getBoolean("io.netty.noJdkZlibEncoder", false);
      logger.debug("-Dio.netty.noJdkZlibEncoder: {}", (Object)noJdkZlibEncoder);
      supportsWindowSizeAndMemLevel = noJdkZlibDecoder || PlatformDependent.javaVersion() >= 7;
   }
}
