package io.netty.channel;

import io.netty.buffer.ByteBufUtil;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.MacAddressUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public final class DefaultChannelId implements ChannelId {
   private static final long serialVersionUID = 3884076183504074063L;
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultChannelId.class);
   private static final byte[] MACHINE_ID;
   private static final int PROCESS_ID_LEN = 4;
   private static final int PROCESS_ID;
   private static final int SEQUENCE_LEN = 4;
   private static final int TIMESTAMP_LEN = 8;
   private static final int RANDOM_LEN = 4;
   private static final AtomicInteger nextSequence = new AtomicInteger();
   private final byte[] data;
   private final int hashCode;
   private transient String shortValue;
   private transient String longValue;

   public static DefaultChannelId newInstance() {
      return new DefaultChannelId();
   }

   private static int defaultProcessId() {
      ClassLoader var0 = null;

      String var1;
      Class var3;
      Method var4;
      try {
         var0 = PlatformDependent.getClassLoader(DefaultChannelId.class);
         Class var2 = Class.forName("java.lang.management.ManagementFactory", true, var0);
         var3 = Class.forName("java.lang.management.RuntimeMXBean", true, var0);
         var4 = var2.getMethod("getRuntimeMXBean", EmptyArrays.EMPTY_CLASSES);
         Object var5 = var4.invoke((Object)null, EmptyArrays.EMPTY_OBJECTS);
         Method var6 = var3.getMethod("getName", EmptyArrays.EMPTY_CLASSES);
         var1 = (String)var6.invoke(var5, EmptyArrays.EMPTY_OBJECTS);
      } catch (Throwable var9) {
         logger.debug("Could not invoke ManagementFactory.getRuntimeMXBean().getName(); Android?", var9);

         try {
            var3 = Class.forName("android.os.Process", true, var0);
            var4 = var3.getMethod("myPid", EmptyArrays.EMPTY_CLASSES);
            var1 = var4.invoke((Object)null, EmptyArrays.EMPTY_OBJECTS).toString();
         } catch (Throwable var8) {
            logger.debug("Could not invoke Process.myPid(); not Android?", var8);
            var1 = "";
         }
      }

      int var10 = var1.indexOf(64);
      if (var10 >= 0) {
         var1 = var1.substring(0, var10);
      }

      int var11;
      try {
         var11 = Integer.parseInt(var1);
      } catch (NumberFormatException var7) {
         var11 = -1;
      }

      if (var11 < 0) {
         var11 = PlatformDependent.threadLocalRandom().nextInt();
         logger.warn("Failed to find the current process ID from '{}'; using a random value: {}", var1, var11);
      }

      return var11;
   }

   private DefaultChannelId() {
      super();
      this.data = new byte[MACHINE_ID.length + 4 + 4 + 8 + 4];
      byte var1 = 0;
      System.arraycopy(MACHINE_ID, 0, this.data, var1, MACHINE_ID.length);
      int var3 = var1 + MACHINE_ID.length;
      var3 = this.writeInt(var3, PROCESS_ID);
      var3 = this.writeInt(var3, nextSequence.getAndIncrement());
      var3 = this.writeLong(var3, Long.reverse(System.nanoTime()) ^ System.currentTimeMillis());
      int var2 = PlatformDependent.threadLocalRandom().nextInt();
      var3 = this.writeInt(var3, var2);

      assert var3 == this.data.length;

      this.hashCode = Arrays.hashCode(this.data);
   }

   private int writeInt(int var1, int var2) {
      this.data[var1++] = (byte)(var2 >>> 24);
      this.data[var1++] = (byte)(var2 >>> 16);
      this.data[var1++] = (byte)(var2 >>> 8);
      this.data[var1++] = (byte)var2;
      return var1;
   }

   private int writeLong(int var1, long var2) {
      this.data[var1++] = (byte)((int)(var2 >>> 56));
      this.data[var1++] = (byte)((int)(var2 >>> 48));
      this.data[var1++] = (byte)((int)(var2 >>> 40));
      this.data[var1++] = (byte)((int)(var2 >>> 32));
      this.data[var1++] = (byte)((int)(var2 >>> 24));
      this.data[var1++] = (byte)((int)(var2 >>> 16));
      this.data[var1++] = (byte)((int)(var2 >>> 8));
      this.data[var1++] = (byte)((int)var2);
      return var1;
   }

   public String asShortText() {
      String var1 = this.shortValue;
      if (var1 == null) {
         this.shortValue = var1 = ByteBufUtil.hexDump((byte[])this.data, this.data.length - 4, 4);
      }

      return var1;
   }

   public String asLongText() {
      String var1 = this.longValue;
      if (var1 == null) {
         this.longValue = var1 = this.newLongValue();
      }

      return var1;
   }

   private String newLongValue() {
      StringBuilder var1 = new StringBuilder(2 * this.data.length + 5);
      byte var2 = 0;
      int var3 = this.appendHexDumpField(var1, var2, MACHINE_ID.length);
      var3 = this.appendHexDumpField(var1, var3, 4);
      var3 = this.appendHexDumpField(var1, var3, 4);
      var3 = this.appendHexDumpField(var1, var3, 8);
      var3 = this.appendHexDumpField(var1, var3, 4);

      assert var3 == this.data.length;

      return var1.substring(0, var1.length() - 1);
   }

   private int appendHexDumpField(StringBuilder var1, int var2, int var3) {
      var1.append(ByteBufUtil.hexDump(this.data, var2, var3));
      var1.append('-');
      var2 += var3;
      return var2;
   }

   public int hashCode() {
      return this.hashCode;
   }

   public int compareTo(ChannelId var1) {
      if (this == var1) {
         return 0;
      } else if (var1 instanceof DefaultChannelId) {
         byte[] var2 = ((DefaultChannelId)var1).data;
         int var3 = this.data.length;
         int var4 = var2.length;
         int var5 = Math.min(var3, var4);

         for(int var6 = 0; var6 < var5; ++var6) {
            byte var7 = this.data[var6];
            byte var8 = var2[var6];
            if (var7 != var8) {
               return (var7 & 255) - (var8 & 255);
            }
         }

         return var3 - var4;
      } else {
         return this.asLongText().compareTo(var1.asLongText());
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof DefaultChannelId)) {
         return false;
      } else {
         DefaultChannelId var2 = (DefaultChannelId)var1;
         return this.hashCode == var2.hashCode && Arrays.equals(this.data, var2.data);
      }
   }

   public String toString() {
      return this.asShortText();
   }

   static {
      int var0 = -1;
      String var1 = SystemPropertyUtil.get("io.netty.processId");
      if (var1 != null) {
         try {
            var0 = Integer.parseInt(var1);
         } catch (NumberFormatException var6) {
         }

         if (var0 < 0) {
            var0 = -1;
            logger.warn("-Dio.netty.processId: {} (malformed)", (Object)var1);
         } else if (logger.isDebugEnabled()) {
            logger.debug("-Dio.netty.processId: {} (user-set)", (Object)var0);
         }
      }

      if (var0 < 0) {
         var0 = defaultProcessId();
         if (logger.isDebugEnabled()) {
            logger.debug("-Dio.netty.processId: {} (auto-detected)", (Object)var0);
         }
      }

      PROCESS_ID = var0;
      byte[] var2 = null;
      String var3 = SystemPropertyUtil.get("io.netty.machineId");
      if (var3 != null) {
         try {
            var2 = MacAddressUtil.parseMAC(var3);
         } catch (Exception var5) {
            logger.warn("-Dio.netty.machineId: {} (malformed)", var3, var5);
         }

         if (var2 != null) {
            logger.debug("-Dio.netty.machineId: {} (user-set)", (Object)var3);
         }
      }

      if (var2 == null) {
         var2 = MacAddressUtil.defaultMachineId();
         if (logger.isDebugEnabled()) {
            logger.debug("-Dio.netty.machineId: {} (auto-detected)", (Object)MacAddressUtil.formatAddress(var2));
         }
      }

      MACHINE_ID = var2;
   }
}
