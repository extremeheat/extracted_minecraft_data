package net.minecraft.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record Brightness(int d, int e) {
   private final int block;
   private final int sky;
   public static final Codec<Integer> LIGHT_VALUE_CODEC = ExtraCodecs.intRange(0, 15);
   public static final Codec<Brightness> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(LIGHT_VALUE_CODEC.fieldOf("block").forGetter(Brightness::block), LIGHT_VALUE_CODEC.fieldOf("sky").forGetter(Brightness::sky))
            .apply(var0, Brightness::new)
   );
   public static Brightness FULL_BRIGHT = new Brightness(15, 15);

   public Brightness(int var1, int var2) {
      super();
      this.block = var1;
      this.sky = var2;
   }

   public int pack() {
      return this.block << 4 | this.sky << 20;
   }

   public static Brightness unpack(int var0) {
      int var1 = var0 >> 4 & 65535;
      int var2 = var0 >> 20 & 65535;
      return new Brightness(var1, var2);
   }
}
