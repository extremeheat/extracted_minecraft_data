package net.minecraft.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record Brightness(int block, int sky) {
   public static final Codec<Integer> LIGHT_VALUE_CODEC = ExtraCodecs.intRange(0, 15);
   public static final Codec<Brightness> CODEC = RecordCodecBuilder.create((var0) -> var0.group(LIGHT_VALUE_CODEC.fieldOf("block").forGetter(Brightness::block), LIGHT_VALUE_CODEC.fieldOf("sky").forGetter(Brightness::sky)).apply(var0, Brightness::new));
   public static final Brightness FULL_BRIGHT = new Brightness(15, 15);

   public Brightness(int var1, int var2) {
      super();
      this.block = var1;
      this.sky = var2;
   }

   public static int pack(int var0, int var1) {
      return var0 << 4 | var1 << 20;
   }

   public int pack() {
      return pack(this.block, this.sky);
   }

   public static int block(int var0) {
      return var0 >> 4 & '\uffff';
   }

   public static int sky(int var0) {
      return var0 >> 20 & '\uffff';
   }

   public static Brightness unpack(int var0) {
      return new Brightness(block(var0), sky(var0));
   }
}
