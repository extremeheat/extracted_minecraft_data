package net.minecraft.world.level.levelgen.densityfunction;

import com.mojang.serialization.MapCodec;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import net.minecraft.util.Interval;
import net.minecraft.world.level.levelgen.densityfunction.op.CacheFunction;

public class DensityFunctionCompiler {
   private final DensityFunction.CompileContext context;
   private final Map<DensityFunction, DensitySampler> samplers = new ConcurrentHashMap();
   private final Function<DensityFunction, DensitySampler> optimizeAndCompile = this::optimizeAndCompile;
   private final DfRewriteRule optimizerRule;
   private final ReentrantLock compileLock;
   private final Map<DensityFunction, PreparedCache> preparedCaches;
   private int nextCacheId;

   public DensityFunctionCompiler(final DensityFunction.CompileContext context) {
      super();
      this.optimizerRule = DfRewriteRule.sequence(new DfRewriteRule() {
         {
            Objects.requireNonNull(DensityFunctionCompiler.this);
         }

         public DensityFunction rewrite(DensityFunction function) {
            function = DfRewriteRule.INLINE_REFERENCE.rewrite(function);
            if (function instanceof CacheFunction cache) {
               return DensityFunctionCompiler.this.reuseOrPrepareCache(cache);
            } else {
               return function.rewriteChildren(this);
            }
         }
      }, DfRewriteRule.SLICE_UNIFORM_AXES);
      this.compileLock = new ReentrantLock();
      this.preparedCaches = new HashMap();
      this.context = context;
   }

   public DensitySampler getSampler(final DensityFunction function) {
      return (DensitySampler)this.samplers.computeIfAbsent(function, this.optimizeAndCompile);
   }

   private DensitySampler optimizeAndCompile(final DensityFunction function) {
      this.compileLock.lock();

      DensitySampler var3;
      try {
         DensityFunction optimizedFunction = this.optimizerRule.rewrite(function);
         var3 = optimizedFunction.compileSampler(this.context);
      } finally {
         this.compileLock.unlock();
      }

      return var3;
   }

   private DensityFunction reuseOrPrepareCache(final CacheFunction cache) {
      PreparedCache prepared = (PreparedCache)this.preparedCaches.get(cache.input());
      if (prepared == null) {
         prepared = this.prepareCache(cache);
         this.preparedCaches.put(cache.input(), prepared);
      }

      return prepared;
   }

   private PreparedCache prepareCache(final CacheFunction cache) {
      int id = this.nextCacheId++;
      DensityFunction input = this.optimizerRule.rewrite(cache.input());
      CachingDensitySampler cachingSampler = new CachingDensitySampler(id, input.compileSampler(this.context));
      return new PreparedCache(id, input.range(), input.domainAxes(), cachingSampler);
   }

   private static record PreparedCache(int id, Interval range, @DensityFunction.Axes int domainAxes, DensitySampler cachingSampler) implements DensityFunction {
      private PreparedCache {
         super();
      }

      public DensitySampler compileSampler(final DensityFunction.CompileContext context) {
         return this.cachingSampler;
      }

      public DensityFunction rewriteChildren(final DfRewriteRule rule) {
         return this;
      }

      public MapCodec<PreparedCache> codec() {
         throw new UnsupportedOperationException("PreparedCache should never be encoded");
      }
   }
}
