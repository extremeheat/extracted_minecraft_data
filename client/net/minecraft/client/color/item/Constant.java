package net.minecraft.client.color.item;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ARGB;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;

public record Constant(int value) implements ItemTintSource {
   public static final MapCodec<Constant> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ExtraCodecs.RGB_COLOR_CODEC.fieldOf("value").forGetter(Constant::value)).apply(var0, Constant::new));

   public Constant(int var1) {
      super();
      var1 = ARGB.opaque(var1);
      this.value = var1;
   }

   public int calculate(ItemStack var1) {
      return this.value;
   }

   public MapCodec<Constant> type() {
      return MAP_CODEC;
   }
}
