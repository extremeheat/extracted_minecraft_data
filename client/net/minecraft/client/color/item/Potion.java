package net.minecraft.client.color.item;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.ARGB;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;

public record Potion(int defaultColor) implements ItemTintSource {
   public static final MapCodec<Potion> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(ExtraCodecs.RGB_COLOR_CODEC.fieldOf("default").forGetter(Potion::defaultColor)).apply(var0, Potion::new);
   });

   public Potion() {
      this(-13083194);
   }

   public Potion(int var1) {
      super();
      this.defaultColor = var1;
   }

   public int calculate(ItemStack var1) {
      PotionContents var2 = (PotionContents)var1.get(DataComponents.POTION_CONTENTS);
      return var2 != null ? ARGB.opaque(var2.getColorOr(this.defaultColor)) : ARGB.opaque(this.defaultColor);
   }

   public MapCodec<Potion> type() {
      return MAP_CODEC;
   }

   public int defaultColor() {
      return this.defaultColor;
   }
}
