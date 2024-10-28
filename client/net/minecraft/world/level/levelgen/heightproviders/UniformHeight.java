package net.minecraft.world.level.levelgen.heightproviders;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import org.slf4j.Logger;

public class UniformHeight extends HeightProvider {
   public static final MapCodec<UniformHeight> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(VerticalAnchor.CODEC.fieldOf("min_inclusive").forGetter((var0x) -> {
         return var0x.minInclusive;
      }), VerticalAnchor.CODEC.fieldOf("max_inclusive").forGetter((var0x) -> {
         return var0x.maxInclusive;
      })).apply(var0, UniformHeight::new);
   });
   private static final Logger LOGGER = LogUtils.getLogger();
   private final VerticalAnchor minInclusive;
   private final VerticalAnchor maxInclusive;
   private final LongSet warnedFor = new LongOpenHashSet();

   private UniformHeight(VerticalAnchor var1, VerticalAnchor var2) {
      super();
      this.minInclusive = var1;
      this.maxInclusive = var2;
   }

   public static UniformHeight of(VerticalAnchor var0, VerticalAnchor var1) {
      return new UniformHeight(var0, var1);
   }

   public int sample(RandomSource var1, WorldGenerationContext var2) {
      int var3 = this.minInclusive.resolveY(var2);
      int var4 = this.maxInclusive.resolveY(var2);
      if (var3 > var4) {
         if (this.warnedFor.add((long)var3 << 32 | (long)var4)) {
            LOGGER.warn("Empty height range: {}", this);
         }

         return var3;
      } else {
         return Mth.randomBetweenInclusive(var1, var3, var4);
      }
   }

   public HeightProviderType<?> getType() {
      return HeightProviderType.UNIFORM;
   }

   public String toString() {
      String var10000 = String.valueOf(this.minInclusive);
      return "[" + var10000 + "-" + String.valueOf(this.maxInclusive) + "]";
   }
}
