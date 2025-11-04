package net.minecraft.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;

public class RandomSequence {
   public static final Codec<RandomSequence> CODEC = RecordCodecBuilder.create((var0) -> var0.group(XoroshiroRandomSource.CODEC.fieldOf("source").forGetter((var0x) -> var0x.source)).apply(var0, RandomSequence::new));
   private final XoroshiroRandomSource source;

   public RandomSequence(XoroshiroRandomSource var1) {
      super();
      this.source = var1;
   }

   public RandomSequence(long var1, Identifier var3) {
      this(createSequence(var1, Optional.of(var3)));
   }

   public RandomSequence(long var1, Optional<Identifier> var3) {
      this(createSequence(var1, var3));
   }

   private static XoroshiroRandomSource createSequence(long var0, Optional<Identifier> var2) {
      RandomSupport.Seed128bit var3 = RandomSupport.upgradeSeedTo128bitUnmixed(var0);
      if (var2.isPresent()) {
         var3 = var3.xor(seedForKey((Identifier)var2.get()));
      }

      return new XoroshiroRandomSource(var3.mixed());
   }

   public static RandomSupport.Seed128bit seedForKey(Identifier var0) {
      return RandomSupport.seedFromHashOf(var0.toString());
   }

   public RandomSource random() {
      return this.source;
   }
}
