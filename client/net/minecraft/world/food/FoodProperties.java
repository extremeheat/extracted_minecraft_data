package net.minecraft.world.food;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.effect.MobEffectInstance;

public record FoodProperties(int c, float d, boolean e, float f, List<FoodProperties.PossibleEffect> g) {
   private final int nutrition;
   private final float saturationModifier;
   private final boolean canAlwaysEat;
   private final float eatSeconds;
   private final List<FoodProperties.PossibleEffect> effects;
   private static final float DEFAULT_EAT_SECONDS = 1.6F;
   public static final Codec<FoodProperties> DIRECT_CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               ExtraCodecs.NON_NEGATIVE_INT.fieldOf("nutrition").forGetter(FoodProperties::nutrition),
               Codec.FLOAT.fieldOf("saturation_modifier").forGetter(FoodProperties::saturationModifier),
               Codec.BOOL.optionalFieldOf("can_always_eat", false).forGetter(FoodProperties::canAlwaysEat),
               ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("eat_seconds", 1.6F).forGetter(FoodProperties::eatSeconds),
               FoodProperties.PossibleEffect.CODEC.listOf().optionalFieldOf("effects", List.of()).forGetter(FoodProperties::effects)
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
      FoodProperties.PossibleEffect.STREAM_CODEC.apply(ByteBufCodecs.list()),
      FoodProperties::effects,
      FoodProperties::new
   );

   public FoodProperties(int var1, float var2, boolean var3, float var4, List<FoodProperties.PossibleEffect> var5) {
      super();
      this.nutrition = var1;
      this.saturationModifier = var2;
      this.canAlwaysEat = var3;
      this.eatSeconds = var4;
      this.effects = var5;
   }

   public int eatDurationTicks() {
      return (int)(this.eatSeconds * 20.0F);
   }

   public static class Builder {
      private int nutrition;
      private float saturationModifier;
      private boolean canAlwaysEat;
      private float eatSeconds = 1.6F;
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

      public FoodProperties build() {
         return new FoodProperties(this.nutrition, this.saturationModifier, this.canAlwaysEat, this.eatSeconds, this.effects.build());
      }
   }

   public static record PossibleEffect(MobEffectInstance c, float d) {
      private final MobEffectInstance effect;
      private final float probability;
      public static final Codec<FoodProperties.PossibleEffect> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  MobEffectInstance.CODEC.fieldOf("effect").forGetter(FoodProperties.PossibleEffect::effect),
                  Codec.floatRange(0.0F, 1.0F).optionalFieldOf("probability", 1.0F).forGetter(FoodProperties.PossibleEffect::probability)
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
