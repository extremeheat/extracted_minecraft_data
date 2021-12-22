package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetContainerContents extends LootItemConditionalFunction {
   final List<LootPoolEntryContainer> entries;
   final BlockEntityType<?> type;

   SetContainerContents(LootItemCondition[] var1, BlockEntityType<?> var2, List<LootPoolEntryContainer> var3) {
      super(var1);
      this.type = var2;
      this.entries = ImmutableList.copyOf(var3);
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
               Objects.requireNonNull(var3);
               var2xx.createItemStack(LootTable.createStackSplitter(var3::add), var2);
            });
         });
         CompoundTag var4 = new CompoundTag();
         ContainerHelper.saveAllItems(var4, var3);
         CompoundTag var5 = BlockItem.getBlockEntityData(var1);
         if (var5 == null) {
            var5 = var4;
         } else {
            var5.merge(var4);
         }

         BlockItem.setBlockEntityData(var1, this.type, var5);
         return var1;
      }
   }

   public void validate(ValidationContext var1) {
      super.validate(var1);

      for(int var2 = 0; var2 < this.entries.size(); ++var2) {
         ((LootPoolEntryContainer)this.entries.get(var2)).validate(var1.forChild(".entry[" + var2 + "]"));
      }

   }

   public static SetContainerContents.Builder setContents(BlockEntityType<?> var0) {
      return new SetContainerContents.Builder(var0);
   }

   public static class Builder extends LootItemConditionalFunction.Builder<SetContainerContents.Builder> {
      private final List<LootPoolEntryContainer> entries = Lists.newArrayList();
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

      public LootItemFunction build() {
         return new SetContainerContents(this.getConditions(), this.type, this.entries);
      }

      // $FF: synthetic method
      protected LootItemConditionalFunction.Builder getThis() {
         return this.getThis();
      }
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<SetContainerContents> {
      public Serializer() {
         super();
      }

      public void serialize(JsonObject var1, SetContainerContents var2, JsonSerializationContext var3) {
         super.serialize(var1, (LootItemConditionalFunction)var2, var3);
         var1.addProperty("type", Registry.BLOCK_ENTITY_TYPE.getKey(var2.type).toString());
         var1.add("entries", var3.serialize(var2.entries));
      }

      public SetContainerContents deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         LootPoolEntryContainer[] var4 = (LootPoolEntryContainer[])GsonHelper.getAsObject(var1, "entries", var2, LootPoolEntryContainer[].class);
         ResourceLocation var5 = new ResourceLocation(GsonHelper.getAsString(var1, "type"));
         BlockEntityType var6 = (BlockEntityType)Registry.BLOCK_ENTITY_TYPE.getOptional(var5).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown block entity type id '" + var5 + "'");
         });
         return new SetContainerContents(var3, var6, Arrays.asList(var4));
      }

      // $FF: synthetic method
      public LootItemConditionalFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         return this.deserialize(var1, var2, var3);
      }
   }
}
