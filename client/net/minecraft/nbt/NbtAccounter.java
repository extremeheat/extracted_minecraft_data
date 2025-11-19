package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;

public class NbtAccounter {
   public static final int DEFAULT_NBT_QUOTA = 2097152;
   public static final int UNCOMPRESSED_NBT_QUOTA = 104857600;
   private static final int MAX_STACK_DEPTH = 512;
   private final long quota;
   private long usage;
   private final int maxDepth;
   private int depth;

   public NbtAccounter(long var1, int var3) {
      super();
      this.quota = var1;
      this.maxDepth = var3;
   }

   public static NbtAccounter create(long var0) {
      return new NbtAccounter(var0, 512);
   }

   public static NbtAccounter defaultQuota() {
      return new NbtAccounter(2097152L, 512);
   }

   public static NbtAccounter uncompressedQuota() {
      return new NbtAccounter(104857600L, 512);
   }

   public static NbtAccounter unlimitedHeap() {
      return new NbtAccounter(9223372036854775807L, 512);
   }

   public void accountBytes(long var1, long var3) {
      this.accountBytes(var1 * var3);
   }

   public void accountBytes(long var1) {
      if (var1 < 0L) {
         throw new IllegalArgumentException("Tried to account NBT tag with negative size: " + var1);
      } else if (this.usage + var1 > this.quota) {
         throw new NbtAccounterException("Tried to read NBT tag that was too big; tried to allocate: " + this.usage + " + " + var1 + " bytes where max allowed: " + this.quota);
      } else {
         this.usage += var1;
      }
   }

   public void pushDepth() {
      if (this.depth >= this.maxDepth) {
         throw new NbtAccounterException("Tried to read NBT tag with too high complexity, depth > " + this.maxDepth);
      } else {
         ++this.depth;
      }
   }

   public void popDepth() {
      if (this.depth <= 0) {
         throw new NbtAccounterException("NBT-Accounter tried to pop stack-depth at top-level");
      } else {
         --this.depth;
      }
   }

   @VisibleForTesting
   public long getUsage() {
      return this.usage;
   }

   @VisibleForTesting
   public int getDepth() {
      return this.depth;
   }
}
