package net.minecraft.world.item.trading;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.functions.FunctionUserBuilder;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import org.jspecify.annotations.Nullable;

public class VillagerTrade implements Validatable {
   public static final Codec<VillagerTrade> CODEC;
   private final TradeCost wants;
   private final Optional<TradeCost> additionalWants;
   private final ItemStackTemplate gives;
   private final Optional<Holder<LootItemCondition>> merchantPredicate;
   private final Optional<Holder<LootItemFunction>> givenItemModifier;
   private final Holder<NumberProvider> maxUses;
   private final Holder<NumberProvider> xp;
   private final Holder<NumberProvider> reputationDiscount;
   private final Optional<HolderSet<Enchantment>> doubleTradePriceEnchantments;

   private VillagerTrade(final TradeCost wants, final Optional<TradeCost> additionalWants, final ItemStackTemplate gives, final Holder<NumberProvider> maxUses, final Holder<NumberProvider> xp, final Holder<NumberProvider> reputationDiscount, final Optional<Holder<LootItemCondition>> merchantPredicate, final Optional<Holder<LootItemFunction>> givenItemModifier, final Optional<HolderSet<Enchantment>> doubleTradePriceEnchantments) {
      super();
      this.wants = wants;
      this.additionalWants = additionalWants;
      this.gives = gives;
      this.maxUses = maxUses;
      this.xp = xp;
      this.reputationDiscount = reputationDiscount;
      this.merchantPredicate = merchantPredicate;
      this.givenItemModifier = givenItemModifier;
      this.doubleTradePriceEnchantments = doubleTradePriceEnchantments;
   }

   public void validate(final ValidationContext context) {
      Validatable.validate(context, "wants", this.wants);
      Validatable.validate(context, "additional_wants", this.additionalWants);
      Validatable.validateHolder(context, "max_uses", this.maxUses);
      Validatable.validateHolder(context, "reputation_discount", this.reputationDiscount);
      Validatable.validateHolder(context, "xp", this.xp);
      Validatable.validateHolder(context, "merchant_predicate", this.merchantPredicate);
      Validatable.validateHolder(context, "given_item_modifier", this.givenItemModifier);
   }

   public @Nullable MerchantOffer getOffer(final LootContext lootContext) {
      if (this.merchantPredicate.isPresent() && !((LootItemCondition)((Holder)this.merchantPredicate.get()).value()).test(lootContext)) {
         return null;
      } else {
         ItemStack result = this.gives.create();
         int additionalCost = 0;
         if (this.givenItemModifier.isPresent()) {
            result = (ItemStack)((LootItemFunction)((Holder)this.givenItemModifier.get()).value()).apply(result, lootContext);
            if (result.isEmpty()) {
               return null;
            }
         }

         Integer additionalTradeCost = (Integer)result.remove(DataComponents.ADDITIONAL_TRADE_COST);
         if (additionalTradeCost != null) {
            additionalCost += additionalTradeCost;
         }

         if (this.doubleTradePriceEnchantments.isPresent()) {
            HolderSet<Enchantment> enchantments = (HolderSet)this.doubleTradePriceEnchantments.get();
            ItemEnchantments itemEnchantments = (ItemEnchantments)result.get(DataComponents.STORED_ENCHANTMENTS);
            if (itemEnchantments != null) {
               Stream var10000 = itemEnchantments.keySet().stream();
               Objects.requireNonNull(enchantments);
               if (var10000.anyMatch(enchantments::contains)) {
                  additionalCost *= 2;
               }
            }
         }

         ItemCost itemCost = this.wants.toItemCost(lootContext, additionalCost);
         if (itemCost.count() < 1) {
            return null;
         } else {
            Optional<ItemCost> additionalItemCost = this.additionalWants.map((tradeCost) -> tradeCost.toItemCost(lootContext, 0));
            return additionalItemCost.isPresent() && ((ItemCost)additionalItemCost.get()).count() < 1 ? null : new MerchantOffer(itemCost, additionalItemCost, result, Math.max(((NumberProvider)this.maxUses.value()).getInt(lootContext), 1), Math.max(((NumberProvider)this.xp.value()).getInt(lootContext), 0), Math.max(((NumberProvider)this.reputationDiscount.value()).getFloat(lootContext), 0.0F));
         }
      }
   }

   public static Builder builder(final TradeCost wants, final ItemStackTemplate gives, final int maxUses, final int xp, final float reputationDiscount) {
      return new Builder(wants, gives, ConstantValue.exactly((float)maxUses), ConstantValue.exactly((float)xp), ConstantValue.exactly(reputationDiscount));
   }

   public static Builder builder(final TradeCost wants, final TradeCost additionalWants, final ItemStackTemplate gives, final int maxUses, final int xp, final float reputationDiscount) {
      return (new Builder(wants, gives, ConstantValue.exactly((float)maxUses), ConstantValue.exactly((float)xp), ConstantValue.exactly(reputationDiscount))).additionalWants(additionalWants);
   }

   static {
      CODEC = RecordCodecBuilder.create((i) -> i.group(TradeCost.CODEC.fieldOf("wants").forGetter((villagerTrade) -> villagerTrade.wants), TradeCost.CODEC.optionalFieldOf("additional_wants").forGetter((villagerTrade) -> villagerTrade.additionalWants), ItemStackTemplate.CODEC.fieldOf("gives").forGetter((villagerTrade) -> villagerTrade.gives), NumberProviders.CODEC.lenientOptionalFieldOf("max_uses", ConstantValue.exactly(4.0F)).forGetter((villagerTrade) -> villagerTrade.maxUses), NumberProviders.CODEC.lenientOptionalFieldOf("xp", ConstantValue.exactly(1.0F)).forGetter((villagerTrade) -> villagerTrade.xp), NumberProviders.CODEC.lenientOptionalFieldOf("reputation_discount", ConstantValue.exactly(0.0F)).forGetter((villagerTrade) -> villagerTrade.reputationDiscount), LootItemCondition.CODEC.optionalFieldOf("merchant_predicate").forGetter((villagerTrade) -> villagerTrade.merchantPredicate), LootItemFunctions.CODEC.optionalFieldOf("given_item_modifier").forGetter((villagerTrade) -> villagerTrade.givenItemModifier), RegistryCodecs.holderSet(Registries.ENCHANTMENT).optionalFieldOf("double_trade_price_enchantments").forGetter((villagerTrade) -> villagerTrade.doubleTradePriceEnchantments)).apply(i, VillagerTrade::new)).validate(Validatable.validatorForContext(LootContextParamSets.VILLAGER_TRADE));
   }

   public static class Builder {
      private final TradeCost wants;
      private final ItemStackTemplate gives;
      private final Holder<NumberProvider> maxUses;
      private final Holder<NumberProvider> xp;
      private final Holder<NumberProvider> reputationDiscount;
      private Optional<TradeCost> additionalWants = Optional.empty();
      private Optional<Holder<LootItemCondition>> merchantPredicate = Optional.empty();
      private Optional<HolderSet<Enchantment>> doubleTradePriceEnchantments = Optional.empty();
      private final ImmutableList.Builder<Holder<LootItemFunction>> givenItemModifiers = ImmutableList.builder();

      public Builder(final TradeCost wants, final ItemStackTemplate gives, final Holder<NumberProvider> maxUses, final Holder<NumberProvider> xp, final Holder<NumberProvider> reputationDiscount) {
         super();
         this.wants = wants;
         this.gives = gives;
         this.maxUses = maxUses;
         this.xp = xp;
         this.reputationDiscount = reputationDiscount;
      }

      public Builder additionalWants(final TradeCost additionalWants) {
         this.additionalWants = Optional.of(additionalWants);
         return this;
      }

      public Builder merchantPredicate(final Holder<LootItemCondition> merchantPredicate) {
         this.merchantPredicate = Optional.of(merchantPredicate);
         return this;
      }

      public Builder addModifier(final LootItemFunction.Builder function) {
         this.givenItemModifiers.add(Holder.direct(function.build()));
         return this;
      }

      @SafeVarargs
      public final Builder addModifiers(final Holder<LootItemFunction>... functions) {
         return this.addModifiers(Arrays.asList(functions));
      }

      public Builder addModifiers(final Collection<Holder<LootItemFunction>> function) {
         this.givenItemModifiers.addAll(function);
         return this;
      }

      public Builder doubleTradePriceEnchantments(final HolderSet<Enchantment> doubleTradePriceEnchantments) {
         this.doubleTradePriceEnchantments = Optional.of(doubleTradePriceEnchantments);
         return this;
      }

      public VillagerTrade build() {
         return new VillagerTrade(this.wants, this.additionalWants, this.gives, this.maxUses, this.xp, this.reputationDiscount, this.merchantPredicate, FunctionUserBuilder.buildFunction(this.givenItemModifiers.build()), this.doubleTradePriceEnchantments);
      }
   }
}
