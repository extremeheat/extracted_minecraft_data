package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;

public record OminousBottleAmplifier(int value) implements ConsumableListener, TooltipProvider {
   public static final int EFFECT_DURATION = 120000;
   public static final int MIN_AMPLIFIER = 0;
   public static final int MAX_AMPLIFIER = 4;
   public static final Codec<OminousBottleAmplifier> CODEC = ExtraCodecs.intRange(0, 4).xmap(OminousBottleAmplifier::new, OminousBottleAmplifier::value);
   public static final StreamCodec<RegistryFriendlyByteBuf, OminousBottleAmplifier> STREAM_CODEC;

   public OminousBottleAmplifier(int var1) {
      super();
      this.value = var1;
   }

   public void onConsume(Level var1, LivingEntity var2, ItemStack var3, Consumable var4) {
      var2.addEffect(new MobEffectInstance(MobEffects.BAD_OMEN, 120000, this.value, false, false, true));
   }

   public void addToTooltip(Item.TooltipContext var1, Consumer<Component> var2, TooltipFlag var3) {
      List var4 = List.of(new MobEffectInstance(MobEffects.BAD_OMEN, 120000, this.value, false, false, true));
      PotionContents.addPotionTooltip(var4, var2, 1.0F, var1.tickRate());
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, OminousBottleAmplifier::value, OminousBottleAmplifier::new);
   }
}
