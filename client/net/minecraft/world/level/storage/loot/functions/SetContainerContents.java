package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.ContainerComponentManipulator;
import net.minecraft.world.level.storage.loot.ContainerComponentManipulators;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetContainerContents extends LootItemConditionalFunction {
   public static final MapCodec<SetContainerContents> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> commonFields(i).and(i.group(ContainerComponentManipulators.CODEC.fieldOf("component").forGetter((f) -> f.component), LootPoolEntries.CODEC.listOf().fieldOf("entries").forGetter((f) -> f.entries))).apply(i, SetContainerContents::new));
   private final ContainerComponentManipulator<?> component;
   private final List<LootPoolEntryContainer> entries;

   private SetContainerContents(final Optional<Holder<LootItemCondition>> condition, final ContainerComponentManipulator<?> component, final List<LootPoolEntryContainer> entries) {
      super(condition);
      this.component = component;
      this.entries = List.copyOf(entries);
   }

   public MapCodec<SetContainerContents> codec() {
      return MAP_CODEC;
   }

   public ItemStack run(final ItemStack itemStack, final LootContext context) {
      if (itemStack.isEmpty()) {
         return itemStack;
      } else {
         Stream.Builder<ItemStack> contents = Stream.builder();
         this.entries.forEach((e) -> e.expand(context, (entry) -> {
               ServerLevel var10001 = context.getLevel();
               Objects.requireNonNull(contents);
               entry.createItemStack(LootTable.createStackSplitter(var10001, contents::add), context);
            }));
         this.component.setContents(itemStack, contents.build());
         return itemStack;
      }
   }

   public void validate(final ValidationContext context) {
      super.validate(context);
      Validatable.validate(context, "entries", this.entries);
   }

   public static Builder setContents(final ContainerComponentManipulator<?> component) {
      return new Builder(component);
   }

   public static class Builder extends LootItemConditionalFunction.Builder<Builder> {
      private final ImmutableList.Builder<LootPoolEntryContainer> entries = ImmutableList.builder();
      private final ContainerComponentManipulator<?> component;

      public Builder(final ContainerComponentManipulator<?> component) {
         super();
         this.component = component;
      }

      protected Builder getThis() {
         return this;
      }

      public Builder withEntry(final LootPoolEntryContainer.Builder<?> entry) {
         this.entries.add(entry.build());
         return this;
      }

      public LootItemFunction build() {
         return new SetContainerContents(this.getCondition(), this.component, this.entries.build());
      }
   }
}
