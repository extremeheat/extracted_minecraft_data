package net.minecraft.client.color.item;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.ARGB;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.MapItemColor;

public record MapColor(int defaultColor) implements ItemTintSource {
   public static final MapCodec<MapColor> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(ExtraCodecs.RGB_COLOR_CODEC.fieldOf("default").forGetter(MapColor::defaultColor)).apply(var0, MapColor::new);
   });

   public MapColor() {
      this(MapItemColor.DEFAULT.rgb());
   }

   public MapColor(int var1) {
      super();
      this.defaultColor = var1;
   }

   public int calculate(ItemStack var1) {
      MapItemColor var2 = (MapItemColor)var1.get(DataComponents.MAP_COLOR);
      return var2 != null ? ARGB.opaque(var2.rgb()) : ARGB.opaque(this.defaultColor);
   }

   public MapCodec<MapColor> type() {
      return MAP_CODEC;
   }

   public int defaultColor() {
      return this.defaultColor;
   }
}
