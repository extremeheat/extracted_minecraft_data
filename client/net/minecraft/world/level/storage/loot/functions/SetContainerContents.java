package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetContainerContents extends LootItemConditionalFunction {
   public static final MapCodec<SetContainerContents> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return commonFields(var0).and(var0.group(BuiltInRegistries.BLOCK_ENTITY_TYPE.holderByNameCodec().fieldOf("type").forGetter((var0x) -> {
         return var0x.type;
      }), LootPoolEntries.CODEC.listOf().fieldOf("entries").forGetter((var0x) -> {
         return var0x.entries;
      }))).apply(var0, SetContainerContents::new);
   });
   private final Holder<BlockEntityType<?>> type;
   private final List<LootPoolEntryContainer> entries;

   SetContainerContents(List<LootItemCondition> var1, Holder<BlockEntityType<?>> var2, List<LootPoolEntryContainer> var3) {
      super(var1);
      this.type = var2;
      this.entries = List.copyOf(var3);
   }

   public LootItemFunctionType getType() {
      return LootItemFunctions.SET_CONTENTS;
   }

   public ItemStack run(ItemStack var1, LootContext var2) {
      if (var1.isEmpty()) {
         return var1;
      } else {
         NonNullList var3 = NonNullList.create();
         this.entries.forEach((var2x) -> {
            var2x.expand(var2, (var2xx) -> {
               ServerLevel var10001 = var2.getLevel();
               Objects.requireNonNull(var3);
               var2xx.createItemStack(LootTable.createStackSplitter(var10001, var3::add), var2);
            });
         });
         var1.set(DataComponents.CONTAINER, ItemContainerContents.copyOf(var3));
         return var1;
      }
   }

   public void validate(ValidationContext var1) {
      super.validate(var1);

      for(int var2 = 0; var2 < this.entries.size(); ++var2) {
         ((LootPoolEntryContainer)this.entries.get(var2)).validate(var1.forChild(".entry[" + var2 + "]"));
      }

   }

   public static Builder setContents(BlockEntityType<?> var0) {
      return new Builder(var0);
   }

   public static class Builder extends LootItemConditionalFunction.Builder<Builder> {
      private final ImmutableList.Builder<LootPoolEntryContainer> entries = ImmutableList.builder();
      private final BlockEntityType<?> type;

      public Builder(BlockEntityType<?> var1) {
         super();
         this.type = var1;
      }

      protected Builder getThis() {
         return this;
      }

      public Builder withEntry(LootPoolEntryContainer.Builder<?> var1) {
         this.entries.add(var1.build());
         return this;
      }

      public LootItemFunction build() {
         return new SetContainerContents(this.getConditions(), this.type.builtInRegistryHolder(), this.entries.build());
      }

      // $FF: synthetic method
      protected LootItemConditionalFunction.Builder getThis() {
         return this.getThis();
      }
   }
}
