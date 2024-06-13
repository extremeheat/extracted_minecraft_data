package net.minecraft.world.item.alchemy;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.FastColor;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public record PotionContents(Optional<Holder<Potion>> potion, Optional<Integer> customColor, List<MobEffectInstance> customEffects) {
   public static final PotionContents EMPTY = new PotionContents(Optional.empty(), Optional.empty(), List.of());
   private static final Component NO_EFFECT = Component.translatable("effect.none").withStyle(ChatFormatting.GRAY);
   private static final int BASE_POTION_COLOR = -13083194;
   private static final Codec<PotionContents> FULL_CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               Potion.CODEC.optionalFieldOf("potion").forGetter(PotionContents::potion),
               Codec.INT.optionalFieldOf("custom_color").forGetter(PotionContents::customColor),
               MobEffectInstance.CODEC.listOf().optionalFieldOf("custom_effects", List.of()).forGetter(PotionContents::customEffects)
            )
            .apply(var0, PotionContents::new)
   );
   public static final Codec<PotionContents> CODEC = Codec.withAlternative(FULL_CODEC, Potion.CODEC, PotionContents::new);
   public static final StreamCodec<RegistryFriendlyByteBuf, PotionContents> STREAM_CODEC = StreamCodec.composite(
      Potion.STREAM_CODEC.apply(ByteBufCodecs::optional),
      PotionContents::potion,
      ByteBufCodecs.INT.apply(ByteBufCodecs::optional),
      PotionContents::customColor,
      MobEffectInstance.STREAM_CODEC.apply(ByteBufCodecs.list()),
      PotionContents::customEffects,
      PotionContents::new
   );

   public PotionContents(Holder<Potion> var1) {
      this(Optional.of(var1), Optional.empty(), List.of());
   }

   public PotionContents(Optional<Holder<Potion>> potion, Optional<Integer> customColor, List<MobEffectInstance> customEffects) {
      super();
      this.potion = potion;
      this.customColor = customColor;
      this.customEffects = customEffects;
   }

   public static ItemStack createItemStack(Item var0, Holder<Potion> var1) {
      ItemStack var2 = new ItemStack(var0);
      var2.set(DataComponents.POTION_CONTENTS, new PotionContents(var1));
      return var2;
   }

   public boolean is(Holder<Potion> var1) {
      return this.potion.isPresent() && this.potion.get().is(var1) && this.customEffects.isEmpty();
   }

   public Iterable<MobEffectInstance> getAllEffects() {
      if (this.potion.isEmpty()) {
         return this.customEffects;
      } else {
         return (Iterable<MobEffectInstance>)(this.customEffects.isEmpty()
            ? this.potion.get().value().getEffects()
            : Iterables.concat(this.potion.get().value().getEffects(), this.customEffects));
      }
   }

   public void forEachEffect(Consumer<MobEffectInstance> var1) {
      if (this.potion.isPresent()) {
         for (MobEffectInstance var3 : this.potion.get().value().getEffects()) {
            var1.accept(new MobEffectInstance(var3));
         }
      }

      for (MobEffectInstance var5 : this.customEffects) {
         var1.accept(new MobEffectInstance(var5));
      }
   }

   public PotionContents withPotion(Holder<Potion> var1) {
      return new PotionContents(Optional.of(var1), this.customColor, this.customEffects);
   }

   public PotionContents withEffectAdded(MobEffectInstance var1) {
      return new PotionContents(this.potion, this.customColor, Util.copyAndAdd(this.customEffects, var1));
   }

   public int getColor() {
      return this.customColor.isPresent() ? this.customColor.get() : getColor(this.getAllEffects());
   }

   public static int getColor(Holder<Potion> var0) {
      return getColor(((Potion)var0.value()).getEffects());
   }

   public static int getColor(Iterable<MobEffectInstance> var0) {
      return getColorOptional(var0).orElse(-13083194);
   }

   public static OptionalInt getColorOptional(Iterable<MobEffectInstance> var0) {
      int var1 = 0;
      int var2 = 0;
      int var3 = 0;
      int var4 = 0;

      for (MobEffectInstance var6 : var0) {
         if (var6.isVisible()) {
            int var7 = var6.getEffect().value().getColor();
            int var8 = var6.getAmplifier() + 1;
            var1 += var8 * FastColor.ARGB32.red(var7);
            var2 += var8 * FastColor.ARGB32.green(var7);
            var3 += var8 * FastColor.ARGB32.blue(var7);
            var4 += var8;
         }
      }

      return var4 == 0 ? OptionalInt.empty() : OptionalInt.of(FastColor.ARGB32.color(var1 / var4, var2 / var4, var3 / var4));
   }

   public boolean hasEffects() {
      return !this.customEffects.isEmpty() ? true : this.potion.isPresent() && !this.potion.get().value().getEffects().isEmpty();
   }

   public List<MobEffectInstance> customEffects() {
      return Lists.transform(this.customEffects, MobEffectInstance::new);
   }

   public void addPotionTooltip(Consumer<Component> var1, float var2, float var3) {
      addPotionTooltip(this.getAllEffects(), var1, var2, var3);
   }

   public static void addPotionTooltip(Iterable<MobEffectInstance> var0, Consumer<Component> var1, float var2, float var3) {
      ArrayList var4 = Lists.newArrayList();
      boolean var5 = true;

      for (MobEffectInstance var7 : var0) {
         var5 = false;
         MutableComponent var8 = Component.translatable(var7.getDescriptionId());
         Holder var9 = var7.getEffect();
         ((MobEffect)var9.value()).createModifiers(var7.getAmplifier(), (var1x, var2x) -> var4.add(new Pair(var1x, var2x)));
         if (var7.getAmplifier() > 0) {
            var8 = Component.translatable("potion.withAmplifier", var8, Component.translatable("potion.potency." + var7.getAmplifier()));
         }

         if (!var7.endsWithin(20)) {
            var8 = Component.translatable("potion.withDuration", var8, MobEffectUtil.formatDuration(var7, var2, var3));
         }

         var1.accept(var8.withStyle(((MobEffect)var9.value()).getCategory().getTooltipFormatting()));
      }

      if (var5) {
         var1.accept(NO_EFFECT);
      }

      if (!var4.isEmpty()) {
         var1.accept(CommonComponents.EMPTY);
         var1.accept(Component.translatable("potion.whenDrank").withStyle(ChatFormatting.DARK_PURPLE));

         for (Pair var14 : var4) {
            AttributeModifier var15 = (AttributeModifier)var14.getSecond();
            double var16 = var15.amount();
            double var11;
            if (var15.operation() != AttributeModifier.Operation.ADD_MULTIPLIED_BASE && var15.operation() != AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL) {
               var11 = var15.amount();
            } else {
               var11 = var15.amount() * 100.0;
            }

            if (var16 > 0.0) {
               var1.accept(
                  Component.translatable(
                        "attribute.modifier.plus." + var15.operation().id(),
                        ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(var11),
                        Component.translatable(((Attribute)((Holder)var14.getFirst()).value()).getDescriptionId())
                     )
                     .withStyle(ChatFormatting.BLUE)
               );
            } else if (var16 < 0.0) {
               var11 *= -1.0;
               var1.accept(
                  Component.translatable(
                        "attribute.modifier.take." + var15.operation().id(),
                        ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(var11),
                        Component.translatable(((Attribute)((Holder)var14.getFirst()).value()).getDescriptionId())
                     )
                     .withStyle(ChatFormatting.RED)
               );
            }
         }
      }
   }
}
