package net.minecraft.client.renderer.item.properties.numeric;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public record UseCycle(float period) implements RangeSelectItemModelProperty {
   public static final MapCodec<UseCycle> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("period", 1.0F).forGetter(UseCycle::period)).apply(var0, UseCycle::new));

   public UseCycle(float var1) {
      super();
      this.period = var1;
   }

   public float get(ItemStack var1, @Nullable ClientLevel var2, @Nullable ItemOwner var3, int var4) {
      LivingEntity var5 = var3 == null ? null : var3.asLivingEntity();
      return var5 != null && var5.getUseItem() == var1 ? (float)var5.getUseItemRemainingTicks() % this.period : 0.0F;
   }

   public MapCodec<UseCycle> type() {
      return MAP_CODEC;
   }
}
