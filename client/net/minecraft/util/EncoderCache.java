package net.minecraft.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.nbt.Tag;

public class EncoderCache {
   final LoadingCache<EncoderCache.Key<?, ?>, DataResult<?>> cache;

   public EncoderCache(int var1) {
      super();
      this.cache = CacheBuilder.newBuilder()
         .maximumSize((long)var1)
         .concurrencyLevel(1)
         .softValues()
         .build(new CacheLoader<EncoderCache.Key<?, ?>, DataResult<?>>() {
            public DataResult<?> load(EncoderCache.Key<?, ?> var1) {
               return var1.resolve();
            }
         });
   }

   public <A> Codec<A> wrap(final Codec<A> var1) {
      return new Codec<A>() {
         public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> var1x, T var2) {
            return var1.decode(var1x, var2);
         }

         public <T> DataResult<T> encode(A var1x, DynamicOps<T> var2, T var3) {
            return ((DataResult)EncoderCache.this.cache.getUnchecked(new EncoderCache.Key<>(var1, var1x, var2)))
               .map(var0 -> var0 instanceof Tag var1xxx ? var1xxx.copy() : var0);
         }
      };
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
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
