package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public class SetEnchantmentsFunction extends LootItemConditionalFunction {
   public static final MapCodec<SetEnchantmentsFunction> CODEC = RecordCodecBuilder.mapCodec((var0) -> commonFields(var0).and(var0.group(Codec.unboundedMap(Enchantment.CODEC, NumberProviders.CODEC).optionalFieldOf("enchantments", Map.of()).forGetter((var0x) -> var0x.enchantments), Codec.BOOL.fieldOf("add").orElse(false).forGetter((var0x) -> var0x.add))).apply(var0, SetEnchantmentsFunction::new));
   private final Map<Holder<Enchantment>, NumberProvider> enchantments;
   private final boolean add;

   SetEnchantmentsFunction(List<LootItemCondition> var1, Map<Holder<Enchantment>, NumberProvider> var2, boolean var3) {
      super(var1);
      this.enchantments = Map.copyOf(var2);
      this.add = var3;
   }

   public LootItemFunctionType<SetEnchantmentsFunction> getType() {
      return LootItemFunctions.SET_ENCHANTMENTS;
   }

   public Set<ContextKey<?>> getReferencedContextParams() {
      return (Set)this.enchantments.values().stream().flatMap((var0) -> var0.getReferencedContextParams().stream()).collect(ImmutableSet.toImmutableSet());
   }

   public ItemStack run(ItemStack var1, LootContext var2) {
      if (var1.is(Items.BOOK)) {
         var1 = var1.transmuteCopy(Items.ENCHANTED_BOOK);
      }

      EnchantmentHelper.updateEnchantments(var1, (var2x) -> {
         if (this.add) {
            this.enchantments.forEach((var2xx, var3) -> var2x.set(var2xx, Mth.clamp(var2x.getLevel(var2xx) + var3.getInt(var2), 0, 255)));
         } else {
            this.enchantments.forEach((var2xx, var3) -> var2x.set(var2xx, Mth.clamp(var3.getInt(var2), 0, 255)));
         }

      });
      return var1;
   }

   public static class Builder extends LootItemConditionalFunction.Builder<Builder> {
      private final ImmutableMap.Builder<Holder<Enchantment>, NumberProvider> enchantments;
      private final boolean add;

      public Builder() {
         this(false);
      }

      public Builder(boolean var1) {
         super();
         this.enchantments = ImmutableMap.builder();
         this.add = var1;
      }

      protected Builder getThis() {
         return this;
      }

      public Builder withEnchantment(Holder<Enchantment> var1, NumberProvider var2) {
         this.enchantments.put(var1, var2);
         return this;
      }

      public LootItemFunction build() {
         return new SetEnchantmentsFunction(this.getConditions(), this.enchantments.build(), this.add);
      }

      // $FF: synthetic method
      protected LootItemConditionalFunction.Builder getThis() {
         return this.getThis();
      }
   }
}
