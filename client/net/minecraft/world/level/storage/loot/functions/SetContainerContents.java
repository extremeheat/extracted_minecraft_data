package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.ContainerComponentManipulator;
import net.minecraft.world.level.storage.loot.ContainerComponentManipulators;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetContainerContents extends LootItemConditionalFunction {
   public static final MapCodec<SetContainerContents> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> commonFields(var0)
            .and(
               var0.group(
                  ContainerComponentManipulators.CODEC.fieldOf("component").forGetter(var0x -> var0x.component),
                  LootPoolEntries.CODEC.listOf().fieldOf("entries").forGetter(var0x -> var0x.entries)
               )
            )
            .apply(var0, SetContainerContents::new)
   );
   private final ContainerComponentManipulator<?> component;
   private final List<LootPoolEntryContainer> entries;

   SetContainerContents(List<LootItemCondition> var1, ContainerComponentManipulator<?> var2, List<LootPoolEntryContainer> var3) {
      super(var1);
      this.component = var2;
      this.entries = List.copyOf(var3);
   }

   @Override
   public LootItemFunctionType<SetContainerContents> getType() {
      return LootItemFunctions.SET_CONTENTS;
   }

   @Override
   public ItemStack run(ItemStack var1, LootContext var2) {
      if (var1.isEmpty()) {
         return var1;
      } else {
         Stream.Builder var3 = Stream.builder();
         this.entries.forEach(var2x -> var2x.expand(var2, var2xx -> var2xx.createItemStack(LootTable.createStackSplitter(var2.getLevel(), var3::add), var2)));
         this.component.setContents(var1, var3.build());
         return var1;
      }
   }

   @Override
   public void validate(ValidationContext var1) {
      super.validate(var1);

      for (int var2 = 0; var2 < this.entries.size(); var2++) {
         this.entries.get(var2).validate(var1.forChild(".entry[" + var2 + "]"));
      }
   }

   public static SetContainerContents.Builder setContents(ContainerComponentManipulator<?> var0) {
      return new SetContainerContents.Builder(var0);
   }

   public static class Builder extends LootItemConditionalFunction.Builder<SetContainerContents.Builder> {
      private final com.google.common.collect.ImmutableList.Builder<LootPoolEntryContainer> entries = ImmutableList.builder();
      private final ContainerComponentManipulator<?> component;

      public Builder(ContainerComponentManipulator<?> var1) {
         super();
         this.component = var1;
      }

      protected SetContainerContents.Builder getThis() {
         return this;
      }

      public SetContainerContents.Builder withEntry(LootPoolEntryContainer.Builder<?> var1) {
         this.entries.add(var1.build());
         return this;
      }

      @Override
      public LootItemFunction build() {
         return new SetContainerContents(this.getConditions(), this.component, this.entries.build());
      }
   }
}