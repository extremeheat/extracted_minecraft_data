package net.minecraft.world.food;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public record FoodProperties(int nutrition, float saturation, boolean canAlwaysEat, float eatSeconds, Optional<ItemStack> usingConvertsTo, List<PossibleEffect> effects) {
   private static final float DEFAULT_EAT_SECONDS = 1.6F;
   public static final Codec<FoodProperties> DIRECT_CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(ExtraCodecs.NON_NEGATIVE_INT.fieldOf("nutrition").forGetter(FoodProperties::nutrition), Codec.FLOAT.fieldOf("saturation").forGetter(FoodProperties::saturation), Codec.BOOL.optionalFieldOf("can_always_eat", false).forGetter(FoodProperties::canAlwaysEat), ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("eat_seconds", 1.6F).forGetter(FoodProperties::eatSeconds), ItemStack.SINGLE_ITEM_CODEC.optionalFieldOf("using_converts_to").forGetter(FoodProperties::usingConvertsTo), FoodProperties.PossibleEffect.CODEC.listOf().optionalFieldOf("effects", List.of()).forGetter(FoodProperties::effects)).apply(var0, FoodProperties::new);
   });
   public static final StreamCodec<RegistryFriendlyByteBuf, FoodProperties> DIRECT_STREAM_CODEC;

   public FoodProperties(int var1, float var2, boolean var3, float var4, Optional<ItemStack> var5, List<PossibleEffect> var6) {
      super();
      this.nutrition = var1;
      this.saturation = var2;
      this.canAlwaysEat = var3;
      this.eatSeconds = var4;
      this.usingConvertsTo = var5;
      this.effects = var6;
   }

   public int eatDurationTicks() {
      return (int)(this.eatSeconds * 20.0F);
   }

   public int nutrition() {
      return this.nutrition;
   }

   public float saturation() {
      return this.saturation;
   }

   public boolean canAlwaysEat() {
      return this.canAlwaysEat;
   }

   public float eatSeconds() {
      return this.eatSeconds;
   }

   public Optional<ItemStack> usingConvertsTo() {
      return this.usingConvertsTo;
   }

   public List<PossibleEffect> effects() {
      return this.effects;
   }

   static {
      DIRECT_STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, FoodProperties::nutrition, ByteBufCodecs.FLOAT, FoodProperties::saturation, ByteBufCodecs.BOOL, FoodProperties::canAlwaysEat, ByteBufCodecs.FLOAT, FoodProperties::eatSeconds, ItemStack.STREAM_CODEC.apply(ByteBufCodecs::optional), FoodProperties::usingConvertsTo, FoodProperties.PossibleEffect.STREAM_CODEC.apply(ByteBufCodecs.list()), FoodProperties::effects, FoodProperties::new);
   }

   public static record PossibleEffect(MobEffectInstance effect, float probability) {
      public static final Codec<PossibleEffect> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(MobEffectInstance.CODEC.fieldOf("effect").forGetter(PossibleEffect::effect), Codec.floatRange(0.0F, 1.0F).optionalFieldOf("probability", 1.0F).forGetter(PossibleEffect::probability)).apply(var0, PossibleEffect::new);
      });
      public static final StreamCodec<RegistryFriendlyByteBuf, PossibleEffect> STREAM_CODEC;

      public PossibleEffect(MobEffectInstance var1, float var2) {
         super();
         this.effect = var1;
         this.probability = var2;
      }

      public MobEffectInstance effect() {
         return new MobEffectInstance(this.effect);
      }

      public float probability() {
         return this.probability;
      }

      static {
         STREAM_CODEC = StreamCodec.composite(MobEffectInstance.STREAM_CODEC, PossibleEffect::effect, ByteBufCodecs.FLOAT, PossibleEffect::probability, PossibleEffect::new);
      }
   }

   public static class Builder {
      private int nutrition;
      private float saturationModifier;
      private boolean canAlwaysEat;
      private float eatSeconds = 1.6F;
      private Optional<ItemStack> usingConvertsTo = Optional.empty();
      private final ImmutableList.Builder<PossibleEffect> effects = ImmutableList.builder();

      public Builder() {
         super();
      }

      public Builder nutrition(int var1) {
         this.nutrition = var1;
         return this;
      }

      public Builder saturationModifier(float var1) {
         this.saturationModifier = var1;
         return this;
      }

      public Builder alwaysEdible() {
         this.canAlwaysEat = true;
         return this;
      }

      public Builder fast() {
         this.eatSeconds = 0.8F;
         return this;
      }

      public Builder effect(MobEffectInstance var1, float var2) {
         this.effects.add(new PossibleEffect(var1, var2));
         return this;
      }

      public Builder usingConvertsTo(ItemLike var1) {
         this.usingConvertsTo = Optional.of(new ItemStack(var1));
         return this;
      }

      public FoodProperties build() {
         float var1 = FoodConstants.saturationByModifier(this.nutrition, this.saturationModifier);
         return new FoodProperties(this.nutrition, var1, this.canAlwaysEat, this.eatSeconds, this.usingConvertsTo, this.effects.build());
      }
   }
}
