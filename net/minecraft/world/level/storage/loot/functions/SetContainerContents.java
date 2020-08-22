package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Arrays;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetContainerContents extends LootItemConditionalFunction {
   private final List entries;

   private SetContainerContents(LootItemCondition[] var1, List var2) {
      super(var1);
      this.entries = ImmutableList.copyOf(var2);
   }

   public ItemStack run(ItemStack var1, LootContext var2) {
      if (var1.isEmpty()) {
         return var1;
      } else {
         NonNullList var3 = NonNullList.create();
         this.entries.forEach((var2x) -> {
            var2x.expand(var2, (var2xx) -> {
               var3.getClass();
               var2xx.createItemStack(LootTable.createStackSplitter(var3::add), var2);
            });
         });
         CompoundTag var4 = new CompoundTag();
         ContainerHelper.saveAllItems(var4, var3);
         CompoundTag var5 = var1.getOrCreateTag();
         var5.put("BlockEntityTag", var4.merge(var5.getCompound("BlockEntityTag")));
         return var1;
      }
   }

   public void validate(ValidationContext var1) {
      super.validate(var1);

      for(int var2 = 0; var2 < this.entries.size(); ++var2) {
         ((LootPoolEntryContainer)this.entries.get(var2)).validate(var1.forChild(".entry[" + var2 + "]"));
      }

   }

   public static SetContainerContents.Builder setContents() {
      return new SetContainerContents.Builder();
   }

   // $FF: synthetic method
   SetContainerContents(LootItemCondition[] var1, List var2, Object var3) {
      this(var1, var2);
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer {
      protected Serializer() {
         super(new ResourceLocation("set_contents"), SetContainerContents.class);
      }

      public void serialize(JsonObject var1, SetContainerContents var2, JsonSerializationContext var3) {
         super.serialize(var1, (LootItemConditionalFunction)var2, var3);
         var1.add("entries", var3.serialize(var2.entries));
      }

      public SetContainerContents deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         LootPoolEntryContainer[] var4 = (LootPoolEntryContainer[])GsonHelper.getAsObject(var1, "entries", var2, LootPoolEntryContainer[].class);
         return new SetContainerContents(var3, Arrays.asList(var4));
      }

      // $FF: synthetic method
      public LootItemConditionalFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         return this.deserialize(var1, var2, var3);
      }
   }

   public static class Builder extends LootItemConditionalFunction.Builder {
      private final List entries = Lists.newArrayList();

      protected SetContainerContents.Builder getThis() {
         return this;
      }

      public SetContainerContents.Builder withEntry(LootPoolEntryContainer.Builder var1) {
         this.entries.add(var1.build());
         return this;
      }

      public LootItemFunction build() {
         return new SetContainerContents(this.getConditions(), this.entries);
      }

      // $FF: synthetic method
      protected LootItemConditionalFunction.Builder getThis() {
         return this.getThis();
      }
   }
}
