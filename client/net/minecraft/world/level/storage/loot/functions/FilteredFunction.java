package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class FilteredFunction extends LootItemConditionalFunction {
   public static final MapCodec<FilteredFunction> CODEC = RecordCodecBuilder.mapCodec((var0) -> commonFields(var0).and(var0.group(ItemPredicate.CODEC.fieldOf("item_filter").forGetter((var0x) -> var0x.filter), LootItemFunctions.ROOT_CODEC.optionalFieldOf("on_pass").forGetter((var0x) -> var0x.onPass), LootItemFunctions.ROOT_CODEC.optionalFieldOf("on_fail").forGetter((var0x) -> var0x.onFail))).apply(var0, FilteredFunction::new));
   private final ItemPredicate filter;
   private final Optional<LootItemFunction> onPass;
   private final Optional<LootItemFunction> onFail;

   FilteredFunction(List<LootItemCondition> var1, ItemPredicate var2, Optional<LootItemFunction> var3, Optional<LootItemFunction> var4) {
      super(var1);
      this.filter = var2;
      this.onPass = var3;
      this.onFail = var4;
   }

   public LootItemFunctionType<FilteredFunction> getType() {
      return LootItemFunctions.FILTERED;
   }

   public ItemStack run(ItemStack var1, LootContext var2) {
      Optional var3 = this.filter.test(var1) ? this.onPass : this.onFail;
      return var3.isPresent() ? (ItemStack)((LootItemFunction)var3.get()).apply(var1, var2) : var1;
   }

   public void validate(ValidationContext var1) {
      super.validate(var1);
      this.onPass.ifPresent((var1x) -> var1x.validate(var1.forChild(new ProblemReporter.FieldPathElement("on_pass"))));
      this.onFail.ifPresent((var1x) -> var1x.validate(var1.forChild(new ProblemReporter.FieldPathElement("on_fail"))));
   }

   public static Builder filtered(ItemPredicate var0) {
      return new Builder(var0);
   }

   public static class Builder extends LootItemConditionalFunction.Builder<Builder> {
      private final ItemPredicate itemPredicate;
      private Optional<LootItemFunction> onPass = Optional.empty();
      private Optional<LootItemFunction> onFail = Optional.empty();

      Builder(ItemPredicate var1) {
         super();
         this.itemPredicate = var1;
      }

      protected Builder getThis() {
         return this;
      }

      public Builder onPass(Optional<LootItemFunction> var1) {
         this.onPass = var1;
         return this;
      }

      public Builder onFail(Optional<LootItemFunction> var1) {
         this.onFail = var1;
         return this;
      }

      public LootItemFunction build() {
         return new FilteredFunction(this.getConditions(), this.itemPredicate, this.onPass, this.onFail);
      }

      // $FF: synthetic method
      protected LootItemConditionalFunction.Builder getThis() {
         return this.getThis();
      }
   }
}
