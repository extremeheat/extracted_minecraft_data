package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class StrongholdConfiguration {
   public static final Codec<StrongholdConfiguration> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Codec.intRange(0, 1023).fieldOf("distance").forGetter(StrongholdConfiguration::distance), Codec.intRange(0, 1023).fieldOf("spread").forGetter(StrongholdConfiguration::spread), Codec.intRange(1, 4095).fieldOf("count").forGetter(StrongholdConfiguration::count)).apply(var0, StrongholdConfiguration::new);
   });
   private final int distance;
   private final int spread;
   private final int count;

   public StrongholdConfiguration(int var1, int var2, int var3) {
      super();
      this.distance = var1;
      this.spread = var2;
      this.count = var3;
   }

   public int distance() {
      return this.distance;
   }

   public int spread() {
      return this.spread;
   }

   public int count() {
      return this.count;
   }
}
