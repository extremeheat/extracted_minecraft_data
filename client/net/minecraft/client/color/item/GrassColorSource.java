package net.minecraft.client.color.item;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GrassColor;

public record GrassColorSource(float temperature, float downfall) implements ItemTintSource {
   public static final MapCodec<GrassColorSource> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(ExtraCodecs.floatRange(0.0F, 1.0F).fieldOf("temperature").forGetter(GrassColorSource::temperature), ExtraCodecs.floatRange(0.0F, 1.0F).fieldOf("downfall").forGetter(GrassColorSource::downfall)).apply(var0, GrassColorSource::new);
   });

   public GrassColorSource() {
      this(0.5F, 1.0F);
   }

   public GrassColorSource(float var1, float var2) {
      super();
      this.temperature = var1;
      this.downfall = var2;
   }

   public int calculate(ItemStack var1) {
      return GrassColor.get((double)this.temperature, (double)this.downfall);
   }

   public MapCodec<GrassColorSource> type() {
      return MAP_CODEC;
   }

   public float temperature() {
      return this.temperature;
   }

   public float downfall() {
      return this.downfall;
   }
}
