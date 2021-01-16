package io.netty.channel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.UncheckedBooleanSupplier;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

public class DefaultMaxBytesRecvByteBufAllocator implements MaxBytesRecvByteBufAllocator {
   private volatile int maxBytesPerRead;
   private volatile int maxBytesPerIndividualRead;

   public DefaultMaxBytesRecvByteBufAllocator() {
      this(65536, 65536);
   }

   public DefaultMaxBytesRecvByteBufAllocator(int var1, int var2) {
      super();
      checkMaxBytesPerReadPair(var1, var2);
      this.maxBytesPerRead = var1;
      this.maxBytesPerIndividualRead = var2;
   }

   public RecvByteBufAllocator.Handle newHandle() {
      return new DefaultMaxBytesRecvByteBufAllocator.HandleImpl();
   }

   public int maxBytesPerRead() {
      return this.maxBytesPerRead;
   }

   public DefaultMaxBytesRecvByteBufAllocator maxBytesPerRead(int var1) {
      if (var1 <= 0) {
         throw new IllegalArgumentException("maxBytesPerRead: " + var1 + " (expected: > 0)");
      } else {
         synchronized(this) {
            int var3 = this.maxBytesPerIndividualRead();
            if (var1 < var3) {
               throw new IllegalArgumentException("maxBytesPerRead cannot be less than maxBytesPerIndividualRead (" + var3 + "): " + var1);
            } else {
               this.maxBytesPerRead = var1;
               return this;
            }
         }
      }
   }

   public int maxBytesPerIndividualRead() {
      return this.maxBytesPerIndividualRead;
   }

   public DefaultMaxBytesRecvByteBufAllocator maxBytesPerIndividualRead(int var1) {
      if (var1 <= 0) {
         throw new IllegalArgumentException("maxBytesPerIndividualRead: " + var1 + " (expected: > 0)");
      } else {
         synchronized(this) {
            int var3 = this.maxBytesPerRead();
            if (var1 > var3) {
               throw new IllegalArgumentException("maxBytesPerIndividualRead cannot be greater than maxBytesPerRead (" + var3 + "): " + var1);
            } else {
               this.maxBytesPerIndividualRead = var1;
               return this;
            }
         }
      }
   }

   public synchronized Entry<Integer, Integer> maxBytesPerReadPair() {
      return new SimpleEntry(this.maxBytesPerRead, this.maxBytesPerIndividualRead);
   }

   private static void checkMaxBytesPerReadPair(int var0, int var1) {
      if (var0 <= 0) {
         throw new IllegalArgumentException("maxBytesPerRead: " + var0 + " (expected: > 0)");
      } else if (var1 <= 0) {
         throw new IllegalArgumentException("maxBytesPerIndividualRead: " + var1 + " (expected: > 0)");
      } else if (var0 < var1) {
         throw new IllegalArgumentException("maxBytesPerRead cannot be less than maxBytesPerIndividualRead (" + var1 + "): " + var0);
      }
   }

   public DefaultMaxBytesRecvByteBufAllocator maxBytesPerReadPair(int var1, int var2) {
      checkMaxBytesPerReadPair(var1, var2);
      synchronized(this) {
         this.maxBytesPerRead = var1;
         this.maxBytesPerIndividualRead = var2;
         return this;
      }
   }

   private final class HandleImpl implements RecvByteBufAllocator.ExtendedHandle {
      private int individualReadMax;
      private int bytesToRead;
      private int lastBytesRead;
      private int attemptBytesRead;
      private final UncheckedBooleanSupplier defaultMaybeMoreSupplier;

      private HandleImpl() {
         super();
         this.defaultMaybeMoreSupplier = new UncheckedBooleanSupplier() {
            public boolean get() {
               return HandleImpl.this.attemptBytesRead == HandleImpl.this.lastBytesRead;
            }
         };
      }

      public ByteBuf allocate(ByteBufAllocator var1) {
         return var1.ioBuffer(this.guess());
      }

      public int guess() {
         return Math.min(this.individualReadMax, this.bytesToRead);
      }

      public void reset(ChannelConfig var1) {
         this.bytesToRead = DefaultMaxBytesRecvByteBufAllocator.this.maxBytesPerRead();
         this.individualReadMax = DefaultMaxBytesRecvByteBufAllocator.this.maxBytesPerIndividualRead();
      }

      public void incMessagesRead(int var1) {
      }

      public void lastBytesRead(int var1) {
         this.lastBytesRead = var1;
         this.bytesToRead -= var1;
      }

      public int lastBytesRead() {
         return this.lastBytesRead;
      }

      public boolean continueReading() {
         return this.continueReading(this.defaultMaybeMoreSupplier);
      }

      public boolean continueReading(UncheckedBooleanSupplier var1) {
         return this.bytesToRead > 0 && var1.get();
      }

      public void readComplete() {
      }

      public void attemptedBytesRead(int var1) {
         this.attemptBytesRead = var1;
      }

      public int attemptedBytesRead() {
         return this.attemptBytesRead;
      }

      // $FF: synthetic method
      HandleImpl(Object var2) {
         this();
      }
   }
}
