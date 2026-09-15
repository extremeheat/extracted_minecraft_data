package net.minecraft.world.level.storage.loot.entries;

import com.mojang.serialization.MapCodec;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public abstract class SingleEntryContainerBase extends UniformContainerBase {
   private final LootPoolEntry entry = new UniformContainerBase.EntryBase() {
      {
         Objects.requireNonNull(SingleEntryContainerBase.this);
      }

      public void createItemStack(final Consumer<ItemStack> output, final LootContext context) {
         SingleEntryContainerBase.this.createItemStack(output, context);
      }
   };

   protected SingleEntryContainerBase(final int weight, final int quality, final Optional<Holder<LootItemCondition>> condition, final Optional<Holder<LootItemFunction>> modifier) {
      super(weight, quality, condition, modifier);
   }

   public abstract MapCodec<? extends SingleEntryContainerBase> codec();

   protected abstract void createItemStack(Consumer<ItemStack> output, LootContext context);

   public final boolean expandRaw(final LootContext context, final Consumer<LootPoolEntry> output) {
      output.accept(this.entry);
      return true;
   }
}
