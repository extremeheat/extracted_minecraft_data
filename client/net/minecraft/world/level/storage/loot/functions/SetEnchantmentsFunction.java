package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public class SetEnchantmentsFunction extends LootItemConditionalFunction {
   public static final MapCodec<SetEnchantmentsFunction> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> commonFields(var0)
            .and(
               var0.group(
                  Codec.unboundedMap(BuiltInRegistries.ENCHANTMENT.holderByNameCodec(), NumberProviders.CODEC)
                     .optionalFieldOf("enchantments", Map.of())
                     .forGetter(var0x -> var0x.enchantments),
                  Codec.BOOL.fieldOf("add").orElse(false).forGetter(var0x -> var0x.add)
               )
            )
            .apply(var0, SetEnchantmentsFunction::new)
   );
   private final Map<Holder<Enchantment>, NumberProvider> enchantments;
   private final boolean add;

   SetEnchantmentsFunction(List<LootItemCondition> var1, Map<Holder<Enchantment>, NumberProvider> var2, boolean var3) {
      super(var1);
      this.enchantments = Map.copyOf(var2);
      this.add = var3;
   }

   @Override
   public LootItemFunctionType getType() {
      return LootItemFunctions.SET_ENCHANTMENTS;
   }

   @Override
   public Set<LootContextParam<?>> getReferencedContextParams() {
      return this.enchantments.values().stream().flatMap(var0 -> var0.getReferencedContextParams().stream()).collect(ImmutableSet.toImmutableSet());
   }

   @Override
   public ItemStack run(ItemStack var1, LootContext var2) {
      Object2IntOpenHashMap var3 = new Object2IntOpenHashMap();
      this.enchantments.forEach((var2x, var3x) -> var3.put(var2x.value(), Mth.clamp(var3x.getInt(var2), 0, 255)));
      if (var1.is(Items.BOOK)) {
         var1 = var1.transmuteCopy(Items.ENCHANTED_BOOK, var1.getCount());
         var1.set(DataComponents.STORED_ENCHANTMENTS, var1.remove(DataComponents.ENCHANTMENTS));
      }

      EnchantmentHelper.updateEnchantments(var1, var2x -> {
         if (this.add) {
            var3.forEach((var1xx, var2xx) -> var2x.set(var1xx, var2x.getLevel(var1xx) + var2xx));
         } else {
            var3.forEach(var2x::set);
         }
      });
      return var1;
   }

   public static class Builder extends LootItemConditionalFunction.Builder<SetEnchantmentsFunction.Builder> {
      private final com.google.common.collect.ImmutableMap.Builder<Holder<Enchantment>, NumberProvider> enchantments = ImmutableMap.builder();
      private final boolean add;

      public Builder() {
         this(false);
      }

      public Builder(boolean var1) {
         super();
         this.add = var1;
      }

      protected SetEnchantmentsFunction.Builder getThis() {
         return this;
      }

      public SetEnchantmentsFunction.Builder withEnchantment(Enchantment var1, NumberProvider var2) {
         this.enchantments.put(var1.builtInRegistryHolder(), var2);
         return this;
      }

      @Override
      public LootItemFunction build() {
         return new SetEnchantmentsFunction(this.getConditions(), this.enchantments.build(), this.add);
      }
   }
}
