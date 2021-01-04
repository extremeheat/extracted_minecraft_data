package net.minecraft.world.level.newbiome.context;

import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import java.util.Random;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.area.LazyArea;
import net.minecraft.world.level.newbiome.layer.traits.PixelTransformer;

public class LazyAreaContext implements BigContext<LazyArea> {
   private final Long2IntLinkedOpenHashMap cache;
   private final int maxCache;
   protected long seedMixup;
   protected ImprovedNoise biomeNoise;
   private long seed;
   private long rval;

   public LazyAreaContext(int var1, long var2, long var4) {
      super();
      this.seedMixup = var4;
      this.seedMixup *= this.seedMixup * 6364136223846793005L + 1442695040888963407L;
      this.seedMixup += var4;
      this.seedMixup *= this.seedMixup * 6364136223846793005L + 1442695040888963407L;
      this.seedMixup += var4;
      this.seedMixup *= this.seedMixup * 6364136223846793005L + 1442695040888963407L;
      this.seedMixup += var4;
      this.cache = new Long2IntLinkedOpenHashMap(16, 0.25F);
      this.cache.defaultReturnValue(-2147483648);
      this.maxCache = var1;
      this.init(var2);
   }

   public LazyArea createResult(PixelTransformer var1) {
      return new LazyArea(this.cache, this.maxCache, var1);
   }

   public LazyArea createResult(PixelTransformer var1, LazyArea var2) {
      return new LazyArea(this.cache, Math.min(1024, var2.getMaxCache() * 4), var1);
   }

   public LazyArea createResult(PixelTransformer var1, LazyArea var2, LazyArea var3) {
      return new LazyArea(this.cache, Math.min(1024, Math.max(var2.getMaxCache(), var3.getMaxCache()) * 4), var1);
   }

   public void init(long var1) {
      this.seed = var1;
      this.seed *= this.seed * 6364136223846793005L + 1442695040888963407L;
      this.seed += this.seedMixup;
      this.seed *= this.seed * 6364136223846793005L + 1442695040888963407L;
      this.seed += this.seedMixup;
      this.seed *= this.seed * 6364136223846793005L + 1442695040888963407L;
      this.seed += this.seedMixup;
      this.biomeNoise = new ImprovedNoise(new Random(var1));
   }

   public void initRandom(long var1, long var3) {
      this.rval = this.seed;
      this.rval *= this.rval * 6364136223846793005L + 1442695040888963407L;
      this.rval += var1;
      this.rval *= this.rval * 6364136223846793005L + 1442695040888963407L;
      this.rval += var3;
      this.rval *= this.rval * 6364136223846793005L + 1442695040888963407L;
      this.rval += var1;
      this.rval *= this.rval * 6364136223846793005L + 1442695040888963407L;
      this.rval += var3;
   }

   public int nextRandom(int var1) {
      int var2 = (int)((this.rval >> 24) % (long)var1);
      if (var2 < 0) {
         var2 += var1;
      }

      this.rval *= this.rval * 6364136223846793005L + 1442695040888963407L;
      this.rval += this.seed;
      return var2;
   }

   public ImprovedNoise getBiomeNoise() {
      return this.biomeNoise;
   }

   // $FF: synthetic method
   public Area createResult(PixelTransformer var1) {
      return this.createResult(var1);
   }
}
