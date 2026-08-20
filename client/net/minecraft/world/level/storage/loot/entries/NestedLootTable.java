package net.minecraft.world.level.storage.loot.entries;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class NestedLootTable extends ExpandableContainerBase {
   public static final MapCodec<NestedLootTable> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(LootTable.LIST_CODEC.fieldOf("value").forGetter((e) -> e.value)).and(expandableFields(i)).apply(i, NestedLootTable::new));
   private final HolderSet<LootTable> value;

   private NestedLootTable(final HolderSet<LootTable> value, final boolean expand, final int weight, final int quality, final Optional<Holder<LootItemCondition>> condition, final Optional<Holder<LootItemFunction>> modifier) {
      super(expand, weight, quality, condition, modifier);
      this.value = value;
   }

   public MapCodec<NestedLootTable> codec() {
      return MAP_CODEC;
   }

   protected boolean addExpandedEntries(final Consumer<LootPoolEntry> output) {
      for(final Holder<LootTable> table : this.value) {
         output.accept(new UniformContainerBase.EntryBase() {
            {
               Objects.requireNonNull(NestedLootTable.this);
            }

            public void createItemStack(final Consumer<ItemStack> output, final LootContext context) {
               ((LootTable)table.value()).getRandomItemsRaw(context, output);
            }
         });
      }

      return true;
   }

   protected boolean addUnexpandedEntry(final Consumer<LootPoolEntry> output) {
      output.accept(new UniformContainerBase.EntryBase() {
         {
            Objects.requireNonNull(NestedLootTable.this);
         }

         public void createItemStack(final Consumer<ItemStack> output, final LootContext context) {
            NestedLootTable.this.value.forEach((t) -> ((LootTable)t.value()).getRandomItemsRaw(context, output));
         }
      });
      return true;
   }

   public void validate(final ValidationContext context) {
      super.validate(context);
      Validatable.validateHolderSet(context, "value", this.value);
   }

   public static UniformContainerBase.Builder<?> lootTableReference(final Holder<LootTable> table) {
      return simpleBuilder((weight, quality, conditions, functions) -> new NestedLootTable(HolderSet.direct(table), false, weight, quality, conditions, functions));
   }

   public static UniformContainerBase.Builder<?> inlineLootTable(final LootTable table) {
      return lootTableReference(Holder.direct(table));
   }
}
