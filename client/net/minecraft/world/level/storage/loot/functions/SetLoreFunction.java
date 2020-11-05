package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetLoreFunction extends LootItemConditionalFunction {
   private final boolean replace;
   private final List<Component> lore;
   @Nullable
   private final LootContext.EntityTarget resolutionContext;

   public SetLoreFunction(LootItemCondition[] var1, boolean var2, List<Component> var3, @Nullable LootContext.EntityTarget var4) {
      super(var1);
      this.replace = var2;
      this.lore = ImmutableList.copyOf(var3);
      this.resolutionContext = var4;
   }

   public LootItemFunctionType getType() {
      return LootItemFunctions.SET_LORE;
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return this.resolutionContext != null ? ImmutableSet.of(this.resolutionContext.getParam()) : ImmutableSet.of();
   }

   public ItemStack run(ItemStack var1, LootContext var2) {
      ListTag var3 = this.getLoreTag(var1, !this.lore.isEmpty());
      if (var3 != null) {
         if (this.replace) {
            var3.clear();
         }

         UnaryOperator var4 = SetNameFunction.createResolver(var2, this.resolutionContext);
         this.lore.stream().map(var4).map(Component.Serializer::toJson).map(StringTag::valueOf).forEach(var3::add);
      }

      return var1;
   }

   @Nullable
   private ListTag getLoreTag(ItemStack var1, boolean var2) {
      CompoundTag var3;
      if (var1.hasTag()) {
         var3 = var1.getTag();
      } else {
         if (!var2) {
            return null;
         }

         var3 = new CompoundTag();
         var1.setTag(var3);
      }

      CompoundTag var4;
      if (var3.contains("display", 10)) {
         var4 = var3.getCompound("display");
      } else {
         if (!var2) {
            return null;
         }

         var4 = new CompoundTag();
         var3.put("display", var4);
      }

      if (var4.contains("Lore", 9)) {
         return var4.getList("Lore", 8);
      } else if (var2) {
         ListTag var5 = new ListTag();
         var4.put("Lore", var5);
         return var5;
      } else {
         return null;
      }
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<SetLoreFunction> {
      public Serializer() {
         super();
      }

      public void serialize(JsonObject var1, SetLoreFunction var2, JsonSerializationContext var3) {
         super.serialize(var1, (LootItemConditionalFunction)var2, var3);
         var1.addProperty("replace", var2.replace);
         JsonArray var4 = new JsonArray();
         Iterator var5 = var2.lore.iterator();

         while(var5.hasNext()) {
            Component var6 = (Component)var5.next();
            var4.add(Component.Serializer.toJsonTree(var6));
         }

         var1.add("lore", var4);
         if (var2.resolutionContext != null) {
            var1.add("entity", var3.serialize(var2.resolutionContext));
         }

      }

      public SetLoreFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         boolean var4 = GsonHelper.getAsBoolean(var1, "replace", false);
         List var5 = (List)Streams.stream(GsonHelper.getAsJsonArray(var1, "lore")).map(Component.Serializer::fromJson).collect(ImmutableList.toImmutableList());
         LootContext.EntityTarget var6 = (LootContext.EntityTarget)GsonHelper.getAsObject(var1, "entity", (Object)null, var2, LootContext.EntityTarget.class);
         return new SetLoreFunction(var3, var4, var5, var6);
      }

      // $FF: synthetic method
      public LootItemConditionalFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         return this.deserialize(var1, var2, var3);
      }
   }
}
