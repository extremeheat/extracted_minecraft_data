package org.apache.logging.log4j.core.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.PropertiesUtil;

public final class UuidUtil {
   public static final String UUID_SEQUENCE = "org.apache.logging.log4j.uuidSequence";
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static final String ASSIGNED_SEQUENCES = "org.apache.logging.log4j.assignedSequences";
   private static final AtomicInteger COUNT = new AtomicInteger(0);
   private static final long TYPE1 = 4096L;
   private static final byte VARIANT = -128;
   private static final int SEQUENCE_MASK = 16383;
   private static final long NUM_100NS_INTERVALS_SINCE_UUID_EPOCH = 122192928000000000L;
   private static final long INITIAL_UUID_SEQNO = PropertiesUtil.getProperties().getLongProperty("org.apache.logging.log4j.uuidSequence", 0L);
   private static final long LEAST;
   private static final long LOW_MASK = 4294967295L;
   private static final long MID_MASK = 281470681743360L;
   private static final long HIGH_MASK = 1152640029630136320L;
   private static final int NODE_SIZE = 8;
   private static final int SHIFT_2 = 16;
   private static final int SHIFT_4 = 32;
   private static final int SHIFT_6 = 48;
   private static final int HUNDRED_NANOS_PER_MILLI = 10000;

   private UuidUtil() {
      super();
   }

   public static UUID getTimeBasedUuid() {
      long var0 = System.currentTimeMillis() * 10000L + 122192928000000000L + (long)(COUNT.incrementAndGet() % 10000);
      long var2 = (var0 & 4294967295L) << 32;
      long var4 = (var0 & 281470681743360L) >> 16;
      long var6 = (var0 & 1152640029630136320L) >> 48;
      long var8 = var2 | var4 | 4096L | var6;
      return new UUID(var8, LEAST);
   }

   private static byte[] getLocalMacAddress() {
      byte[] var0 = null;

      try {
         InetAddress var1 = InetAddress.getLocalHost();

         try {
            NetworkInterface var2 = NetworkInterface.getByInetAddress(var1);
            if (isUpAndNotLoopback(var2)) {
               var0 = var2.getHardwareAddress();
            }

            if (var0 == null) {
               Enumeration var3 = NetworkInterface.getNetworkInterfaces();

               while(var3.hasMoreElements() && var0 == null) {
                  NetworkInterface var4 = (NetworkInterface)var3.nextElement();
                  if (isUpAndNotLoopback(var4)) {
                     var0 = var4.getHardwareAddress();
                  }
               }
            }
         } catch (SocketException var5) {
            LOGGER.catching(var5);
         }

         if (var0 == null || var0.length == 0) {
            var0 = var1.getAddress();
         }
      } catch (UnknownHostException var6) {
      }

      return var0;
   }

   private static boolean isUpAndNotLoopback(NetworkInterface var0) throws SocketException {
      return var0 != null && !var0.isLoopback() && var0.isUp();
   }

   static {
      byte[] var0 = getLocalMacAddress();
      SecureRandom var1 = new SecureRandom();
      if (var0 == null || var0.length == 0) {
         var0 = new byte[6];
         var1.nextBytes(var0);
      }

      int var2 = var0.length >= 6 ? 6 : var0.length;
      int var3 = var0.length >= 6 ? var0.length - 6 : 0;
      byte[] var4 = new byte[8];
      var4[0] = -128;
      var4[1] = 0;

      for(int var5 = 2; var5 < 8; ++var5) {
         var4[var5] = 0;
      }

      System.arraycopy(var0, var3, var4, var3 + 2, var2);
      ByteBuffer var16 = ByteBuffer.wrap(var4);
      long var6 = INITIAL_UUID_SEQNO;
      String var8 = PropertiesUtil.getProperties().getStringProperty("org.apache.logging.log4j.assignedSequences");
      long[] var9;
      int var13;
      if (var8 == null) {
         var9 = new long[0];
      } else {
         String[] var10 = var8.split(Patterns.COMMA_SEPARATOR);
         var9 = new long[var10.length];
         int var11 = 0;
         String[] var12 = var10;
         var13 = var10.length;

         for(int var14 = 0; var14 < var13; ++var14) {
            String var15 = var12[var14];
            var9[var11] = Long.parseLong(var15);
            ++var11;
         }
      }

      if (var6 == 0L) {
         var6 = var1.nextLong();
      }

      var6 &= 16383L;

      boolean var17;
      do {
         var17 = false;
         long[] var18 = var9;
         int var19 = var9.length;

         for(var13 = 0; var13 < var19; ++var13) {
            long var20 = var18[var13];
            if (var20 == var6) {
               var17 = true;
               break;
            }
         }

         if (var17) {
            var6 = var6 + 1L & 16383L;
         }
      } while(var17);

      var8 = var8 == null ? Long.toString(var6) : var8 + ',' + Long.toString(var6);
      System.setProperty("org.apache.logging.log4j.assignedSequences", var8);
      LEAST = var16.getLong() | var6 << 48;
   }
}
