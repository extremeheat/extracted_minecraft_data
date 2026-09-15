package net.minecraft.world.level.storage.loot.entries;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public abstract class ExpandableContainerBase extends UniformContainerBase {
   protected final boolean expand;

   protected static <T extends ExpandableContainerBase> Products.P5<RecordCodecBuilder.Mu<T>, Boolean, Integer, Integer, Optional<Holder<LootItemCondition>>, Optional<Holder<LootItemFunction>>> expandableFields(final RecordCodecBuilder.Instance<T> i) {
      return i.group(Codec.BOOL.optionalFieldOf("expand", false).forGetter((e) -> e.expand)).and(uniformFields(i));
   }

   protected ExpandableContainerBase(final boolean expand, final int weight, final int quality, final Optional<Holder<LootItemCondition>> condition, final Optional<Holder<LootItemFunction>> modifier) {
      super(weight, quality, condition, modifier);
      this.expand = expand;
   }

   public abstract MapCodec<? extends ExpandableContainerBase> codec();

   public final boolean expandRaw(final LootContext context, final Consumer<LootPoolEntry> output) {
      return this.expand ? this.addExpandedEntries(output) : this.addUnexpandedEntry(output);
   }

   protected abstract boolean addExpandedEntries(Consumer<LootPoolEntry> output);

   protected abstract boolean addUnexpandedEntry(Consumer<LootPoolEntry> output);
}
