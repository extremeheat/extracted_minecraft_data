package net.minecraft.client.color.item;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.ARGB;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;

public record CustomModelDataSource(int index, int defaultColor) implements ItemTintSource {
   public static final MapCodec<CustomModelDataSource> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("index", 0).forGetter(CustomModelDataSource::index), ExtraCodecs.RGB_COLOR_CODEC.fieldOf("default").forGetter(CustomModelDataSource::defaultColor)).apply(var0, CustomModelDataSource::new);
   });

   public CustomModelDataSource(int var1, int var2) {
      super();
      this.index = var1;
      this.defaultColor = var2;
   }

   public int calculate(ItemStack var1) {
      CustomModelData var2 = (CustomModelData)var1.get(DataComponents.CUSTOM_MODEL_DATA);
      if (var2 != null) {
         Integer var3 = var2.getColor(this.index);
         if (var3 != null) {
            return ARGB.opaque(var3);
         }
      }

      return ARGB.opaque(this.defaultColor);
   }

   public MapCodec<CustomModelDataSource> type() {
      return MAP_CODEC;
   }

   public int index() {
      return this.index;
   }

   public int defaultColor() {
      return this.defaultColor;
   }
}
