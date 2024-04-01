package net.minecraft.world.food;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.effect.MobEffectInstance;

public record FoodProperties(int c, float d, boolean e, float f, Holder<SoundEvent> g, List<FoodProperties.PossibleEffect> h) {
   private final int nutrition;
   private final float saturationModifier;
   private final boolean canAlwaysEat;
   private final float eatSeconds;
   private final Holder<SoundEvent> eatSound;
   private final List<FoodProperties.PossibleEffect> effects;
   private static final float DEFAULT_EAT_SECONDS = 1.6F;
   public static final Codec<FoodProperties> DIRECT_CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               ExtraCodecs.NON_NEGATIVE_INT.fieldOf("nutrition").forGetter(FoodProperties::nutrition),
               Codec.FLOAT.fieldOf("saturation_modifier").forGetter(FoodProperties::saturationModifier),
               ExtraCodecs.strictOptionalField(Codec.BOOL, "can_always_eat", false).forGetter(FoodProperties::canAlwaysEat),
               ExtraCodecs.strictOptionalField(ExtraCodecs.POSITIVE_FLOAT, "eat_seconds", 1.6F).forGetter(FoodProperties::eatSeconds),
               SoundEvent.CODEC.fieldOf("eat_sound").forGetter(FoodProperties::eatSound),
               ExtraCodecs.strictOptionalField(FoodProperties.PossibleEffect.CODEC.listOf(), "effects", List.of()).forGetter(FoodProperties::effects)
            )
            .apply(var0, FoodProperties::new)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, FoodProperties> DIRECT_STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.VAR_INT,
      FoodProperties::nutrition,
      ByteBufCodecs.FLOAT,
      FoodProperties::saturationModifier,
      ByteBufCodecs.BOOL,
      FoodProperties::canAlwaysEat,
      ByteBufCodecs.FLOAT,
      FoodProperties::eatSeconds,
      SoundEvent.STREAM_CODEC,
      FoodProperties::eatSound,
      FoodProperties.PossibleEffect.STREAM_CODEC.apply(ByteBufCodecs.list()),
      FoodProperties::effects,
      FoodProperties::new
   );

   public FoodProperties(int var1, float var2, boolean var3, float var4, Holder<SoundEvent> var5, List<FoodProperties.PossibleEffect> var6) {
      super();
      this.nutrition = var1;
      this.saturationModifier = var2;
      this.canAlwaysEat = var3;
      this.eatSeconds = var4;
      this.eatSound = var5;
      this.effects = var6;
   }

   public int eatDurationTicks() {
      return (int)(this.eatSeconds * 20.0F);
   }

   public static class Builder {
      private int nutrition;
      private float saturationModifier;
      private boolean canAlwaysEat;
      private float eatSeconds = 1.6F;
      private Holder<SoundEvent> eatSound = SoundEvents.GENERIC_EAT;
      private final com.google.common.collect.ImmutableList.Builder<FoodProperties.PossibleEffect> effects = ImmutableList.builder();

      public Builder() {
         super();
      }

      public FoodProperties.Builder nutrition(int var1) {
         this.nutrition = var1;
         return this;
      }

      public FoodProperties.Builder saturationModifier(float var1) {
         this.saturationModifier = var1;
         return this;
      }

      public FoodProperties.Builder alwaysEdible() {
         this.canAlwaysEat = true;
         return this;
      }

      public FoodProperties.Builder fast() {
         this.eatSeconds = 0.8F;
         return this;
      }

      public FoodProperties.Builder effect(MobEffectInstance var1, float var2) {
         this.effects.add(new FoodProperties.PossibleEffect(var1, var2));
         return this;
      }

      public FoodProperties.Builder eatSound(Holder<SoundEvent> var1) {
         this.eatSound = var1;
         return this;
      }

      public FoodProperties build() {
         return new FoodProperties(this.nutrition, this.saturationModifier, this.canAlwaysEat, this.eatSeconds, this.eatSound, this.effects.build());
      }
   }

   public static record PossibleEffect(MobEffectInstance c, float d) {
      private final MobEffectInstance effect;
      private final float probability;
      public static final Codec<FoodProperties.PossibleEffect> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  MobEffectInstance.CODEC.fieldOf("effect").forGetter(FoodProperties.PossibleEffect::effect),
                  ExtraCodecs.strictOptionalField(Codec.floatRange(0.0F, 1.0F), "probability", 1.0F).forGetter(FoodProperties.PossibleEffect::probability)
               )
               .apply(var0, FoodProperties.PossibleEffect::new)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, FoodProperties.PossibleEffect> STREAM_CODEC = StreamCodec.composite(
         MobEffectInstance.STREAM_CODEC,
         FoodProperties.PossibleEffect::effect,
         ByteBufCodecs.FLOAT,
         FoodProperties.PossibleEffect::probability,
         FoodProperties.PossibleEffect::new
      );

      public PossibleEffect(MobEffectInstance var1, float var2) {
         super();
         this.effect = var1;
         this.probability = var2;
      }

      public MobEffectInstance effect() {
         return new MobEffectInstance(this.effect);
      }
   }
}
