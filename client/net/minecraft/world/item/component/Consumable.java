package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.item.consume_effects.PlaySoundConsumeEffect;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public record Consumable(float consumeSeconds, ItemUseAnimation animation, Holder<SoundEvent> sound, boolean hasConsumeParticles, List<ConsumeEffect> onConsumeEffects) {
   public static final float DEFAULT_CONSUME_SECONDS = 1.6F;
   private static final int CONSUME_EFFECTS_INTERVAL = 4;
   private static final float CONSUME_EFFECTS_START_FRACTION = 0.21875F;
   public static final Codec<Consumable> CODEC = RecordCodecBuilder.create((var0) -> var0.group(ExtraCodecs.NON_NEGATIVE_FLOAT.optionalFieldOf("consume_seconds", 1.6F).forGetter(Consumable::consumeSeconds), ItemUseAnimation.CODEC.optionalFieldOf("animation", ItemUseAnimation.EAT).forGetter(Consumable::animation), SoundEvent.CODEC.optionalFieldOf("sound", SoundEvents.GENERIC_EAT).forGetter(Consumable::sound), Codec.BOOL.optionalFieldOf("has_consume_particles", true).forGetter(Consumable::hasConsumeParticles), ConsumeEffect.CODEC.listOf().optionalFieldOf("on_consume_effects", List.of()).forGetter(Consumable::onConsumeEffects)).apply(var0, Consumable::new));
   public static final StreamCodec<RegistryFriendlyByteBuf, Consumable> STREAM_CODEC;

   public Consumable(float var1, ItemUseAnimation var2, Holder<SoundEvent> var3, boolean var4, List<ConsumeEffect> var5) {
      super();
      this.consumeSeconds = var1;
      this.animation = var2;
      this.sound = var3;
      this.hasConsumeParticles = var4;
      this.onConsumeEffects = var5;
   }

   public InteractionResult startConsuming(LivingEntity var1, ItemStack var2, InteractionHand var3) {
      if (!this.canConsume(var1, var2)) {
         return InteractionResult.FAIL;
      } else {
         boolean var4 = this.consumeTicks() > 0;
         if (var4) {
            var1.startUsingItem(var3);
            return InteractionResult.CONSUME;
         } else {
            ItemStack var5 = this.onConsume(var1.level(), var1, var2);
            return InteractionResult.CONSUME.heldItemTransformedTo(var5);
         }
      }
   }

   public ItemStack onConsume(Level var1, LivingEntity var2, ItemStack var3) {
      RandomSource var4 = var2.getRandom();
      this.emitParticlesAndSounds(var4, var2, var3, 16);
      if (var2 instanceof ServerPlayer var5) {
         var5.awardStat(Stats.ITEM_USED.get(var3.getItem()));
         CriteriaTriggers.CONSUME_ITEM.trigger(var5, var3);
      }

      var3.getAllOfType(ConsumableListener.class).forEach((var4x) -> var4x.onConsume(var1, var2, var3, this));
      if (!var1.isClientSide) {
         this.onConsumeEffects.forEach((var3x) -> var3x.apply(var1, var3, var2));
      }

      var2.gameEvent(this.animation == ItemUseAnimation.DRINK ? GameEvent.DRINK : GameEvent.EAT);
      var3.consume(1, var2);
      return var3;
   }

   public boolean canConsume(LivingEntity var1, ItemStack var2) {
      FoodProperties var3 = (FoodProperties)var2.get(DataComponents.FOOD);
      if (var3 != null && var1 instanceof Player var4) {
         return var4.canEat(var3.canAlwaysEat());
      } else {
         return true;
      }
   }

   public int consumeTicks() {
      return (int)(this.consumeSeconds * 20.0F);
   }

   public void emitParticlesAndSounds(RandomSource var1, LivingEntity var2, ItemStack var3, int var4) {
      float var5 = var1.nextBoolean() ? 0.5F : 1.0F;
      float var6 = var1.triangle(1.0F, 0.2F);
      float var7 = 0.5F;
      float var8 = Mth.randomBetween(var1, 0.9F, 1.0F);
      float var9 = this.animation == ItemUseAnimation.DRINK ? 0.5F : var5;
      float var10 = this.animation == ItemUseAnimation.DRINK ? var8 : var6;
      if (this.hasConsumeParticles) {
         var2.spawnItemParticles(var3, var4);
      }

      SoundEvent var10000;
      if (var2 instanceof OverrideConsumeSound var12) {
         var10000 = var12.getConsumeSound(var3);
      } else {
         var10000 = this.sound.value();
      }

      SoundEvent var11 = var10000;
      var2.playSound(var11, var9, var10);
   }

   public boolean shouldEmitParticlesAndSounds(int var1) {
      int var2 = this.consumeTicks() - var1;
      int var3 = (int)((float)this.consumeTicks() * 0.21875F);
      boolean var4 = var2 > var3;
      return var4 && var1 % 4 == 0;
   }

   public static Builder builder() {
      return new Builder();
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.FLOAT, Consumable::consumeSeconds, ItemUseAnimation.STREAM_CODEC, Consumable::animation, SoundEvent.STREAM_CODEC, Consumable::sound, ByteBufCodecs.BOOL, Consumable::hasConsumeParticles, ConsumeEffect.STREAM_CODEC.apply(ByteBufCodecs.list()), Consumable::onConsumeEffects, Consumable::new);
   }

   public static class Builder {
      private float consumeSeconds = 1.6F;
      private ItemUseAnimation animation;
      private Holder<SoundEvent> sound;
      private boolean hasConsumeParticles;
      private final List<ConsumeEffect> onConsumeEffects;

      Builder() {
         super();
         this.animation = ItemUseAnimation.EAT;
         this.sound = SoundEvents.GENERIC_EAT;
         this.hasConsumeParticles = true;
         this.onConsumeEffects = new ArrayList();
      }

      public Builder consumeSeconds(float var1) {
         this.consumeSeconds = var1;
         return this;
      }

      public Builder animation(ItemUseAnimation var1) {
         this.animation = var1;
         return this;
      }

      public Builder sound(Holder<SoundEvent> var1) {
         this.sound = var1;
         return this;
      }

      public Builder soundAfterConsume(Holder<SoundEvent> var1) {
         return this.onConsume(new PlaySoundConsumeEffect(var1));
      }

      public Builder hasConsumeParticles(boolean var1) {
         this.hasConsumeParticles = var1;
         return this;
      }

      public Builder onConsume(ConsumeEffect var1) {
         this.onConsumeEffects.add(var1);
         return this;
      }

      public Consumable build() {
         return new Consumable(this.consumeSeconds, this.animation, this.sound, this.hasConsumeParticles, this.onConsumeEffects);
      }
   }

   public interface OverrideConsumeSound {
      SoundEvent getConsumeSound(ItemStack var1);
   }
}
