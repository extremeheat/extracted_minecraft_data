package net.minecraft.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;

public class RandomSequence {
   public static final Codec<RandomSequence> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(XoroshiroRandomSource.CODEC.fieldOf("source").forGetter(var0x -> var0x.source)).apply(var0, RandomSequence::new)
   );
   private final XoroshiroRandomSource source;

   public RandomSequence(XoroshiroRandomSource var1) {
      super();
      this.source = var1;
   }

   public RandomSequence(long var1, ResourceLocation var3) {
      this(createSequence(var1, var3));
   }

   private static XoroshiroRandomSource createSequence(long var0, ResourceLocation var2) {
      return new XoroshiroRandomSource(RandomSupport.upgradeSeedTo128bitUnmixed(var0).xor(seedForKey(var2)).mixed());
   }

   public static RandomSupport.Seed128bit seedForKey(ResourceLocation var0) {
      return RandomSupport.seedFromHashOf(var0.toString());
   }

   public RandomSource random() {
      return this.source;
   }
}
