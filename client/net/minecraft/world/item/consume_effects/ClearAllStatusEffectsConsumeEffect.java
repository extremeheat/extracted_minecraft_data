package net.minecraft.world.item.consume_effects;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public record ClearAllStatusEffectsConsumeEffect() implements ConsumeEffect {
   public static final ClearAllStatusEffectsConsumeEffect INSTANCE = new ClearAllStatusEffectsConsumeEffect();
   public static final MapCodec<ClearAllStatusEffectsConsumeEffect> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, ClearAllStatusEffectsConsumeEffect> STREAM_CODEC;

   public ClearAllStatusEffectsConsumeEffect() {
      super();
   }

   public ConsumeEffect.Type<ClearAllStatusEffectsConsumeEffect> getType() {
      return ConsumeEffect.Type.CLEAR_ALL_EFFECTS;
   }

   public boolean apply(Level var1, ItemStack var2, LivingEntity var3) {
      return var3.removeAllEffects();
   }

   static {
      CODEC = MapCodec.unit(INSTANCE);
      STREAM_CODEC = StreamCodec.<RegistryFriendlyByteBuf, ClearAllStatusEffectsConsumeEffect>unit(INSTANCE);
   }
}
