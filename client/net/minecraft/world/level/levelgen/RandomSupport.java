package net.minecraft.world.level.levelgen;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.primitives.Longs;
import java.util.concurrent.atomic.AtomicLong;

public final class RandomSupport {
   public static final long GOLDEN_RATIO_64 = -7046029254386353131L;
   public static final long SILVER_RATIO_64 = 7640891576956012809L;
   private static final HashFunction MD5_128 = Hashing.md5();
   private static final AtomicLong SEED_UNIQUIFIER = new AtomicLong(8682522807148012L);

   public RandomSupport() {
      super();
   }

   @VisibleForTesting
   public static long mixStafford13(long var0) {
      var0 = (var0 ^ var0 >>> 30) * -4658895280553007687L;
      var0 = (var0 ^ var0 >>> 27) * -7723592293110705685L;
      return var0 ^ var0 >>> 31;
   }

   public static RandomSupport.Seed128bit upgradeSeedTo128bitUnmixed(long var0) {
      long var2 = var0 ^ 7640891576956012809L;
      long var4 = var2 + -7046029254386353131L;
      return new RandomSupport.Seed128bit(var2, var4);
   }

   public static RandomSupport.Seed128bit upgradeSeedTo128bit(long var0) {
      return upgradeSeedTo128bitUnmixed(var0).mixed();
   }

   public static RandomSupport.Seed128bit seedFromHashOf(String var0) {
      byte[] var1 = MD5_128.hashString(var0, Charsets.UTF_8).asBytes();
      long var2 = Longs.fromBytes(var1[0], var1[1], var1[2], var1[3], var1[4], var1[5], var1[6], var1[7]);
      long var4 = Longs.fromBytes(var1[8], var1[9], var1[10], var1[11], var1[12], var1[13], var1[14], var1[15]);
      return new RandomSupport.Seed128bit(var2, var4);
   }

   public static long generateUniqueSeed() {
      return SEED_UNIQUIFIER.updateAndGet(var0 -> var0 * 1181783497276652981L) ^ System.nanoTime();
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "varName" is null
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
