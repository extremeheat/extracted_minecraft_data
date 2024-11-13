package net.minecraft.client.color.item;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;

public record Dye(int defaultColor) implements ItemTintSource {
   public static final MapCodec<Dye> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ExtraCodecs.RGB_COLOR_CODEC.fieldOf("default").forGetter(Dye::defaultColor)).apply(var0, Dye::new));

   public Dye(int var1) {
      super();
      this.defaultColor = var1;
   }

   public int calculate(ItemStack var1) {
      return DyedItemColor.getOrDefault(var1, this.defaultColor);
   }

   public MapCodec<Dye> type() {
      return MAP_CODEC;
   }
}
