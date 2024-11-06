package net.minecraft.client.renderer.item.properties.numeric;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public record Count(boolean normalize) implements RangeSelectItemModelProperty {
   public static final MapCodec<Count> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(Codec.BOOL.optionalFieldOf("normalize", true).forGetter(Count::normalize)).apply(var0, Count::new);
   });

   public Count(boolean var1) {
      super();
      this.normalize = var1;
   }

   public float get(ItemStack var1, @Nullable ClientLevel var2, @Nullable LivingEntity var3, int var4) {
      float var5 = (float)var1.getCount();
      float var6 = (float)var1.getMaxStackSize();
      return this.normalize ? Mth.clamp(var5 / var6, 0.0F, 1.0F) : Mth.clamp(var5, 0.0F, var6);
   }

   public MapCodec<Count> type() {
      return MAP_CODEC;
   }

   public boolean normalize() {
      return this.normalize;
   }
}
