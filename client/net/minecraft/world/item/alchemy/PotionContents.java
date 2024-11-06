package net.minecraft.world.item.alchemy;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Iterator;
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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ARGB;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.ConsumableListener;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;

public record PotionContents(Optional<Holder<Potion>> potion, Optional<Integer> customColor, List<MobEffectInstance> customEffects, Optional<String> customName) implements ConsumableListener {
   public static final PotionContents EMPTY = new PotionContents(Optional.empty(), Optional.empty(), List.of(), Optional.empty());
   private static final Component NO_EFFECT;
   public static final int BASE_POTION_COLOR = -13083194;
   private static final Codec<PotionContents> FULL_CODEC;
   public static final Codec<PotionContents> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, PotionContents> STREAM_CODEC;

   public PotionContents(Holder<Potion> var1) {
      this(Optional.of(var1), Optional.empty(), List.of(), Optional.empty());
   }

   public PotionContents(Optional<Holder<Potion>> var1, Optional<Integer> var2, List<MobEffectInstance> var3, Optional<String> var4) {
      super();
      this.potion = var1;
      this.customColor = var2;
      this.customEffects = var3;
      this.customName = var4;
   }

   public static ItemStack createItemStack(Item var0, Holder<Potion> var1) {
      ItemStack var2 = new ItemStack(var0);
      var2.set(DataComponents.POTION_CONTENTS, new PotionContents(var1));
      return var2;
   }

   public boolean is(Holder<Potion> var1) {
      return this.potion.isPresent() && ((Holder)this.potion.get()).is(var1) && this.customEffects.isEmpty();
   }

   public Iterable<MobEffectInstance> getAllEffects() {
      if (this.potion.isEmpty()) {
         return this.customEffects;
      } else {
         return (Iterable)(this.customEffects.isEmpty() ? ((Potion)((Holder)this.potion.get()).value()).getEffects() : Iterables.concat(((Potion)((Holder)this.potion.get()).value()).getEffects(), this.customEffects));
      }
   }

   public void forEachEffect(Consumer<MobEffectInstance> var1) {
      Iterator var2;
      MobEffectInstance var3;
      if (this.potion.isPresent()) {
         var2 = ((Potion)((Holder)this.potion.get()).value()).getEffects().iterator();

         while(var2.hasNext()) {
            var3 = (MobEffectInstance)var2.next();
            var1.accept(new MobEffectInstance(var3));
         }
      }

      var2 = this.customEffects.iterator();

      while(var2.hasNext()) {
         var3 = (MobEffectInstance)var2.next();
         var1.accept(new MobEffectInstance(var3));
      }

   }

   public PotionContents withPotion(Holder<Potion> var1) {
      return new PotionContents(Optional.of(var1), this.customColor, this.customEffects, this.customName);
   }

   public PotionContents withEffectAdded(MobEffectInstance var1) {
      return new PotionContents(this.potion, this.customColor, Util.copyAndAdd((List)this.customEffects, (Object)var1), this.customName);
   }

   public int getColor() {
      return this.getColorOr(-13083194);
   }

   public int getColorOr(int var1) {
      return this.customColor.isPresent() ? (Integer)this.customColor.get() : getColorOptional(this.getAllEffects()).orElse(var1);
   }

   public Component getName(String var1) {
      String var2 = (String)this.customName.or(() -> {
         return this.potion.map((var0) -> {
            return ((Potion)var0.value()).name();
         });
      }).orElse("empty");
      return Component.translatable(var1 + var2);
   }

   public static OptionalInt getColorOptional(Iterable<MobEffectInstance> var0) {
      int var1 = 0;
      int var2 = 0;
      int var3 = 0;
      int var4 = 0;
      Iterator var5 = var0.iterator();

      while(var5.hasNext()) {
         MobEffectInstance var6 = (MobEffectInstance)var5.next();
         if (var6.isVisible()) {
            int var7 = ((MobEffect)var6.getEffect().value()).getColor();
            int var8 = var6.getAmplifier() + 1;
            var1 += var8 * ARGB.red(var7);
            var2 += var8 * ARGB.green(var7);
            var3 += var8 * ARGB.blue(var7);
            var4 += var8;
         }
      }

      if (var4 == 0) {
         return OptionalInt.empty();
      } else {
         return OptionalInt.of(ARGB.color(var1 / var4, var2 / var4, var3 / var4));
      }
   }

   public boolean hasEffects() {
      if (!this.customEffects.isEmpty()) {
         return true;
      } else {
         return this.potion.isPresent() && !((Potion)((Holder)this.potion.get()).value()).getEffects().isEmpty();
      }
   }

   public List<MobEffectInstance> customEffects() {
      return Lists.transform(this.customEffects, MobEffectInstance::new);
   }

   public void addPotionTooltip(Consumer<Component> var1, float var2, float var3) {
      addPotionTooltip(this.getAllEffects(), var1, var2, var3);
   }

   public void applyToLivingEntity(LivingEntity var1) {
      Level var3 = var1.level();
      if (var3 instanceof ServerLevel var2) {
         Player var10000;
         if (var1 instanceof Player var4) {
            var10000 = var4;
         } else {
            var10000 = null;
         }

         Player var5 = var10000;
         this.forEachEffect((var3x) -> {
            if (((MobEffect)var3x.getEffect().value()).isInstantenous()) {
               ((MobEffect)var3x.getEffect().value()).applyInstantenousEffect(var2, var5, var5, var1, var3x.getAmplifier(), 1.0);
            } else {
               var1.addEffect(var3x);
            }

         });
      }
   }

   public static void addPotionTooltip(Iterable<MobEffectInstance> var0, Consumer<Component> var1, float var2, float var3) {
      ArrayList var4 = Lists.newArrayList();
      boolean var5 = true;

      Iterator var6;
      MutableComponent var8;
      Holder var9;
      for(var6 = var0.iterator(); var6.hasNext(); var1.accept(var8.withStyle(((MobEffect)var9.value()).getCategory().getTooltipFormatting()))) {
         MobEffectInstance var7 = (MobEffectInstance)var6.next();
         var5 = false;
         var8 = Component.translatable(var7.getDescriptionId());
         var9 = var7.getEffect();
         ((MobEffect)var9.value()).createModifiers(var7.getAmplifier(), (var1x, var2x) -> {
            var4.add(new Pair(var1x, var2x));
         });
         if (var7.getAmplifier() > 0) {
            var8 = Component.translatable("potion.withAmplifier", var8, Component.translatable("potion.potency." + var7.getAmplifier()));
         }

         if (!var7.endsWithin(20)) {
            var8 = Component.translatable("potion.withDuration", var8, MobEffectUtil.formatDuration(var7, var2, var3));
         }
      }

      if (var5) {
         var1.accept(NO_EFFECT);
      }

      if (!var4.isEmpty()) {
         var1.accept(CommonComponents.EMPTY);
         var1.accept(Component.translatable("potion.whenDrank").withStyle(ChatFormatting.DARK_PURPLE));
         var6 = var4.iterator();

         while(var6.hasNext()) {
            Pair var13 = (Pair)var6.next();
            AttributeModifier var15 = (AttributeModifier)var13.getSecond();
            double var14 = var15.amount();
            double var11;
            if (var15.operation() != AttributeModifier.Operation.ADD_MULTIPLIED_BASE && var15.operation() != AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL) {
               var11 = var15.amount();
            } else {
               var11 = var15.amount() * 100.0;
            }

            if (var14 > 0.0) {
               var1.accept(Component.translatable("attribute.modifier.plus." + var15.operation().id(), ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(var11), Component.translatable(((Attribute)((Holder)var13.getFirst()).value()).getDescriptionId())).withStyle(ChatFormatting.BLUE));
            } else if (var14 < 0.0) {
               var11 *= -1.0;
               var1.accept(Component.translatable("attribute.modifier.take." + var15.operation().id(), ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(var11), Component.translatable(((Attribute)((Holder)var13.getFirst()).value()).getDescriptionId())).withStyle(ChatFormatting.RED));
            }
         }
      }

   }

   public void onConsume(Level var1, LivingEntity var2, ItemStack var3, Consumable var4) {
      this.applyToLivingEntity(var2);
   }

   public Optional<Holder<Potion>> potion() {
      return this.potion;
   }

   public Optional<Integer> customColor() {
      return this.customColor;
   }

   public Optional<String> customName() {
      return this.customName;
   }

   static {
      NO_EFFECT = Component.translatable("effect.none").withStyle(ChatFormatting.GRAY);
      FULL_CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(Potion.CODEC.optionalFieldOf("potion").forGetter(PotionContents::potion), Codec.INT.optionalFieldOf("custom_color").forGetter(PotionContents::customColor), MobEffectInstance.CODEC.listOf().optionalFieldOf("custom_effects", List.of()).forGetter(PotionContents::customEffects), Codec.STRING.optionalFieldOf("custom_name").forGetter(PotionContents::customName)).apply(var0, PotionContents::new);
      });
      CODEC = Codec.withAlternative(FULL_CODEC, Potion.CODEC, PotionContents::new);
      STREAM_CODEC = StreamCodec.composite(Potion.STREAM_CODEC.apply(ByteBufCodecs::optional), PotionContents::potion, ByteBufCodecs.INT.apply(ByteBufCodecs::optional), PotionContents::customColor, MobEffectInstance.STREAM_CODEC.apply(ByteBufCodecs.list()), PotionContents::customEffects, ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs::optional), PotionContents::customName, PotionContents::new);
   }
}
