package net.minecraft.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record Brightness(int block, int sky) {
   public static final Codec<Integer> LIGHT_VALUE_CODEC = ExtraCodecs.intRange(0, 15);
   public static final Codec<Brightness> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(LIGHT_VALUE_CODEC.fieldOf("block").forGetter(Brightness::block), LIGHT_VALUE_CODEC.fieldOf("sky").forGetter(Brightness::sky)).apply(var0, Brightness::new);
   });
   public static Brightness FULL_BRIGHT = new Brightness(15, 15);

   public Brightness(int block, int sky) {
      super();
      this.block = block;
      this.sky = sky;
   }

   public int pack() {
      return this.block << 4 | this.sky << 20;
   }

   public static Brightness unpack(int var0) {
      int var1 = var0 >> 4 & '\uffff';
      int var2 = var0 >> 20 & '\uffff';
      return new Brightness(var1, var2);
   }

   public int block() {
      return this.block;
   }

   public int sky() {
      return this.sky;
   }
}
