package net.minecraft.world.level.storage.loot;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.functions.FunctionUserBuilder;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;

public class LootTable {
   static final Logger LOGGER = LogUtils.getLogger();
   public static final LootTable EMPTY = new LootTable(LootContextParamSets.EMPTY, new LootPool[0], new LootItemFunction[0]);
   public static final LootContextParamSet DEFAULT_PARAM_SET = LootContextParamSets.ALL_PARAMS;
   final LootContextParamSet paramSet;
   final LootPool[] pools;
   final LootItemFunction[] functions;
   private final BiFunction<ItemStack, LootContext, ItemStack> compositeFunction;

   LootTable(LootContextParamSet var1, LootPool[] var2, LootItemFunction[] var3) {
      super();
      this.paramSet = var1;
      this.pools = var2;
      this.functions = var3;
      this.compositeFunction = LootItemFunctions.compose(var3);
   }

   public static Consumer<ItemStack> createStackSplitter(LootContext var0, Consumer<ItemStack> var1) {
      return var2 -> {
         if (var2.isItemEnabled(var0.getLevel().enabledFeatures())) {
            if (var2.getCount() < var2.getMaxStackSize()) {
               var1.accept(var2);
            } else {
               int var3 = var2.getCount();

               while(var3 > 0) {
                  ItemStack var4 = var2.copy();
                  var4.setCount(Math.min(var2.getMaxStackSize(), var3));
                  var3 -= var4.getCount();
                  var1.accept(var4);
               }
            }
         }
      };
   }

   public void getRandomItemsRaw(LootContext var1, Consumer<ItemStack> var2) {
      if (var1.addVisitedTable(this)) {
         Consumer var3 = LootItemFunction.decorate(this.compositeFunction, var2, var1);

         for(LootPool var7 : this.pools) {
            var7.addRandomItems(var3, var1);
         }

         var1.removeVisitedTable(this);
      } else {
         LOGGER.warn("Detected infinite loop in loot tables");
      }
   }

   public void getRandomItems(LootContext var1, Consumer<ItemStack> var2) {
      this.getRandomItemsRaw(var1, createStackSplitter(var1, var2));
   }

   public ObjectArrayList<ItemStack> getRandomItems(LootContext var1) {
      ObjectArrayList var2 = new ObjectArrayList();
      this.getRandomItems(var1, var2::add);
      return var2;
   }

   public LootContextParamSet getParamSet() {
      return this.paramSet;
   }

   public void validate(ValidationContext var1) {
      for(int var2 = 0; var2 < this.pools.length; ++var2) {
         this.pools[var2].validate(var1.forChild(".pools[" + var2 + "]"));
      }

      for(int var3 = 0; var3 < this.functions.length; ++var3) {
         this.functions[var3].validate(var1.forChild(".functions[" + var3 + "]"));
      }
   }

   public void fill(Container var1, LootContext var2) {
      ObjectArrayList var3 = this.getRandomItems(var2);
      RandomSource var4 = var2.getRandom();
      List var5 = this.getAvailableSlots(var1, var4);
      this.shuffleAndSplitItems(var3, var5.size(), var4);
      ObjectListIterator var6 = var3.iterator();

      while(var6.hasNext()) {
         ItemStack var7 = (ItemStack)var6.next();
         if (var5.isEmpty()) {
            LOGGER.warn("Tried to over-fill a container");
            return;
         }

         if (var7.isEmpty()) {
            var1.setItem(var5.remove(var5.size() - 1), ItemStack.EMPTY);
         } else {
            var1.setItem(var5.remove(var5.size() - 1), var7);
         }
      }
   }

   private void shuffleAndSplitItems(ObjectArrayList<ItemStack> var1, int var2, RandomSource var3) {
      ArrayList var4 = Lists.newArrayList();
      ObjectListIterator var5 = var1.iterator();

      while(var5.hasNext()) {
         ItemStack var6 = (ItemStack)var5.next();
         if (var6.isEmpty()) {
            var5.remove();
         } else if (var6.getCount() > 1) {
            var4.add(var6);
            var5.remove();
         }
      }

      while(var2 - var1.size() - var4.size() > 0 && !var4.isEmpty()) {
         ItemStack var8 = (ItemStack)var4.remove(Mth.nextInt(var3, 0, var4.size() - 1));
         int var9 = Mth.nextInt(var3, 1, var8.getCount() / 2);
         ItemStack var7 = var8.split(var9);
         if (var8.getCount() > 1 && var3.nextBoolean()) {
            var4.add(var8);
         } else {
            var1.add(var8);
         }

         if (var7.getCount() > 1 && var3.nextBoolean()) {
            var4.add(var7);
         } else {
            var1.add(var7);
         }
      }

      var1.addAll(var4);
      Util.shuffle(var1, var3);
   }

   private List<Integer> getAvailableSlots(Container var1, RandomSource var2) {
      ObjectArrayList var3 = new ObjectArrayList();

      for(int var4 = 0; var4 < var1.getContainerSize(); ++var4) {
         if (var1.getItem(var4).isEmpty()) {
            var3.add(var4);
         }
      }

      Util.shuffle(var3, var2);
      return var3;
   }

   public static LootTable.Builder lootTable() {
      return new LootTable.Builder();
   }

   public static class Builder implements FunctionUserBuilder<LootTable.Builder> {
      private final List<LootPool> pools = Lists.newArrayList();
      private final List<LootItemFunction> functions = Lists.newArrayList();
      private LootContextParamSet paramSet = LootTable.DEFAULT_PARAM_SET;

      public Builder() {
         super();
      }

      public LootTable.Builder withPool(LootPool.Builder var1) {
         this.pools.add(var1.build());
         return this;
      }

      public LootTable.Builder setParamSet(LootContextParamSet var1) {
         this.paramSet = var1;
         return this;
      }

      public LootTable.Builder apply(LootItemFunction.Builder var1) {
         this.functions.add(var1.build());
         return this;
      }

      public LootTable.Builder unwrap() {
         return this;
      }

      public LootTable build() {
         return new LootTable(this.paramSet, this.pools.toArray(new LootPool[0]), this.functions.toArray(new LootItemFunction[0]));
      }
   }

   public static class Serializer implements JsonDeserializer<LootTable>, JsonSerializer<LootTable> {
      public Serializer() {
         super();
      }

      public LootTable deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var4 = GsonHelper.convertToJsonObject(var1, "loot table");
         LootPool[] var5 = (LootPool[])GsonHelper.getAsObject(var4, "pools", new LootPool[0], var3, LootPool[].class);
         LootContextParamSet var6 = null;
         if (var4.has("type")) {
            String var7 = GsonHelper.getAsString(var4, "type");
            var6 = LootContextParamSets.get(new ResourceLocation(var7));
         }

         LootItemFunction[] var8 = (LootItemFunction[])GsonHelper.getAsObject(var4, "functions", new LootItemFunction[0], var3, LootItemFunction[].class);
         return new LootTable(var6 != null ? var6 : LootContextParamSets.ALL_PARAMS, var5, var8);
      }

      public JsonElement serialize(LootTable var1, Type var2, JsonSerializationContext var3) {
         JsonObject var4 = new JsonObject();
         if (var1.paramSet != LootTable.DEFAULT_PARAM_SET) {
            ResourceLocation var5 = LootContextParamSets.getKey(var1.paramSet);
            if (var5 != null) {
               var4.addProperty("type", var5.toString());
            } else {
               LootTable.LOGGER.warn("Failed to find id for param set {}", var1.paramSet);
            }
         }

         if (var1.pools.length > 0) {
            var4.add("pools", var3.serialize(var1.pools));
         }

         if (!ArrayUtils.isEmpty(var1.functions)) {
            var4.add("functions", var3.serialize(var1.functions));
         }

         return var4;
      }
   }
}
