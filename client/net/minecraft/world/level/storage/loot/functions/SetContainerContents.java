package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetContainerContents extends LootItemConditionalFunction {
   public static final MapCodec<SetContainerContents> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> commonFields(var0)
            .and(
               var0.group(
                  BuiltInRegistries.BLOCK_ENTITY_TYPE.holderByNameCodec().fieldOf("type").forGetter(var0x -> var0x.type),
                  LootPoolEntries.CODEC.listOf().fieldOf("entries").forGetter(var0x -> var0x.entries)
               )
            )
            .apply(var0, SetContainerContents::new)
   );
   private final Holder<BlockEntityType<?>> type;
   private final List<LootPoolEntryContainer> entries;

   SetContainerContents(List<LootItemCondition> var1, Holder<BlockEntityType<?>> var2, List<LootPoolEntryContainer> var3) {
      super(var1);
      this.type = var2;
      this.entries = List.copyOf(var3);
   }

   @Override
   public LootItemFunctionType getType() {
      return LootItemFunctions.SET_CONTENTS;
   }

   @Override
   public ItemStack run(ItemStack var1, LootContext var2) {
      if (var1.isEmpty()) {
         return var1;
      } else {
         NonNullList var3 = NonNullList.create();
         this.entries.forEach(var2x -> var2x.expand(var2, var2xx -> var2xx.createItemStack(LootTable.createStackSplitter(var2.getLevel(), var3::add), var2)));
         var1.set(DataComponents.CONTAINER, ItemContainerContents.copyOf(var3));
         return var1;
      }
   }

   @Override
   public void validate(ValidationContext var1) {
      super.validate(var1);

      for(int var2 = 0; var2 < this.entries.size(); ++var2) {
         this.entries.get(var2).validate(var1.forChild(".entry[" + var2 + "]"));
      }
   }

   public static SetContainerContents.Builder setContents(BlockEntityType<?> var0) {
      return new SetContainerContents.Builder(var0);
   }

   public static class Builder extends LootItemConditionalFunction.Builder<SetContainerContents.Builder> {
      private final com.google.common.collect.ImmutableList.Builder<LootPoolEntryContainer> entries = ImmutableList.builder();
      private final BlockEntityType<?> type;

      public Builder(BlockEntityType<?> var1) {
         super();
         this.type = var1;
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
         return new SetContainerContents(this.getConditions(), this.type.builtInRegistryHolder(), this.entries.build());
      }
   }
}
