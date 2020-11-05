package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class NoiseSlideSettings {
   public static final Codec<NoiseSlideSettings> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Codec.INT.fieldOf("target").forGetter(NoiseSlideSettings::target), Codec.intRange(0, 256).fieldOf("size").forGetter(NoiseSlideSettings::size), Codec.INT.fieldOf("offset").forGetter(NoiseSlideSettings::offset)).apply(var0, NoiseSlideSettings::new);
   });
   private final int target;
   private final int size;
   private final int offset;

   public NoiseSlideSettings(int var1, int var2, int var3) {
      super();
      this.target = var1;
      this.size = var2;
      this.offset = var3;
   }

   public int target() {
      return this.target;
   }

   public int size() {
      return this.size;
   }

   public int offset() {
      return this.offset;
   }
}
