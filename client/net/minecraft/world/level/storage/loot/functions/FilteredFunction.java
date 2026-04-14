package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class FilteredFunction extends LootItemConditionalFunction {
   public static final MapCodec<FilteredFunction> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> commonFields(i).and(i.group(ItemPredicate.CODEC.fieldOf("item_filter").forGetter((f) -> f.filter), LootItemFunctions.ROOT_CODEC.optionalFieldOf("on_pass").forGetter((f) -> f.onPass), LootItemFunctions.ROOT_CODEC.optionalFieldOf("on_fail").forGetter((f) -> f.onFail))).apply(i, FilteredFunction::new));
   private final ItemPredicate filter;
   private final Optional<LootItemFunction> onPass;
   private final Optional<LootItemFunction> onFail;

   private FilteredFunction(final List<LootItemCondition> predicates, final ItemPredicate filter, final Optional<LootItemFunction> onPass, final Optional<LootItemFunction> onFail) {
      super(predicates);
      this.filter = filter;
      this.onPass = onPass;
      this.onFail = onFail;
   }

   public MapCodec<FilteredFunction> codec() {
      return MAP_CODEC;
   }

   public ItemStack run(final ItemStack itemStack, final LootContext context) {
      Optional<LootItemFunction> function = this.filter.test((ItemInstance)itemStack) ? this.onPass : this.onFail;
      return function.isPresent() ? (ItemStack)((LootItemFunction)function.get()).apply(itemStack, context) : itemStack;
   }

   public void validate(final ValidationContext context) {
      super.validate(context);
      Validatable.validate(context, "on_pass", this.onPass);
      Validatable.validate(context, "on_fail", this.onFail);
   }

   public static Builder filtered(final ItemPredicate predicate) {
      return new Builder(predicate);
   }

   public static class Builder extends LootItemConditionalFunction.Builder<Builder> {
      private final ItemPredicate itemPredicate;
      private Optional<LootItemFunction> onPass = Optional.empty();
      private Optional<LootItemFunction> onFail = Optional.empty();

      private Builder(final ItemPredicate itemPredicate) {
         super();
         this.itemPredicate = itemPredicate;
      }

      protected Builder getThis() {
         return this;
      }

      public Builder onPass(final Optional<LootItemFunction> onPass) {
         this.onPass = onPass;
         return this;
      }

      public Builder onFail(final Optional<LootItemFunction> onFail) {
         this.onFail = onFail;
         return this;
      }

      public LootItemFunction build() {
         return new FilteredFunction(this.getConditions(), this.itemPredicate, this.onPass, this.onFail);
      }
   }
}
