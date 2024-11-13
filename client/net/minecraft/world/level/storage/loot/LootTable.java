package net.minecraft.world.level.storage.loot;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.functions.FunctionUserBuilder;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.slf4j.Logger;

public class LootTable {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final LootTable EMPTY;
   public static final ContextKeySet DEFAULT_PARAM_SET;
   public static final long RANDOMIZE_SEED = 0L;
   public static final Codec<LootTable> DIRECT_CODEC;
   public static final Codec<Holder<LootTable>> CODEC;
   private final ContextKeySet paramSet;
   private final Optional<ResourceLocation> randomSequence;
   private final List<LootPool> pools;
   private final List<LootItemFunction> functions;
   private final BiFunction<ItemStack, LootContext, ItemStack> compositeFunction;

   LootTable(ContextKeySet var1, Optional<ResourceLocation> var2, List<LootPool> var3, List<LootItemFunction> var4) {
      super();
      this.paramSet = var1;
      this.randomSequence = var2;
      this.pools = var3;
      this.functions = var4;
      this.compositeFunction = LootItemFunctions.compose(var4);
   }

   public static Consumer<ItemStack> createStackSplitter(ServerLevel var0, Consumer<ItemStack> var1) {
      return (var2) -> {
         if (var2.isItemEnabled(var0.enabledFeatures())) {
            if (var2.getCount() < var2.getMaxStackSize()) {
               var1.accept(var2);
            } else {
               int var3 = var2.getCount();

               while(var3 > 0) {
                  ItemStack var4 = var2.copyWithCount(Math.min(var2.getMaxStackSize(), var3));
                  var3 -= var4.getCount();
                  var1.accept(var4);
               }
            }

         }
      };
   }

   public void getRandomItemsRaw(LootParams var1, Consumer<ItemStack> var2) {
      this.getRandomItemsRaw((new LootContext.Builder(var1)).create(this.randomSequence), var2);
   }

   public void getRandomItemsRaw(LootContext var1, Consumer<ItemStack> var2) {
      LootContext.VisitedEntry var3 = LootContext.createVisitedEntry(this);
      if (var1.pushVisitedElement(var3)) {
         Consumer var4 = LootItemFunction.decorate(this.compositeFunction, var2, var1);

         for(LootPool var6 : this.pools) {
            var6.addRandomItems(var4, var1);
         }

         var1.popVisitedElement(var3);
      } else {
         LOGGER.warn("Detected infinite loop in loot tables");
      }

   }

   public void getRandomItems(LootParams var1, long var2, Consumer<ItemStack> var4) {
      this.getRandomItemsRaw((new LootContext.Builder(var1)).withOptionalRandomSeed(var2).create(this.randomSequence), createStackSplitter(var1.getLevel(), var4));
   }

   public void getRandomItems(LootParams var1, Consumer<ItemStack> var2) {
      this.getRandomItemsRaw(var1, createStackSplitter(var1.getLevel(), var2));
   }

   public void getRandomItems(LootContext var1, Consumer<ItemStack> var2) {
      this.getRandomItemsRaw(var1, createStackSplitter(var1.getLevel(), var2));
   }

   public ObjectArrayList<ItemStack> getRandomItems(LootParams var1, RandomSource var2) {
      return this.getRandomItems((new LootContext.Builder(var1)).withOptionalRandomSource(var2).create(this.randomSequence));
   }

   public ObjectArrayList<ItemStack> getRandomItems(LootParams var1, long var2) {
      return this.getRandomItems((new LootContext.Builder(var1)).withOptionalRandomSeed(var2).create(this.randomSequence));
   }

   public ObjectArrayList<ItemStack> getRandomItems(LootParams var1) {
      return this.getRandomItems((new LootContext.Builder(var1)).create(this.randomSequence));
   }

   private ObjectArrayList<ItemStack> getRandomItems(LootContext var1) {
      ObjectArrayList var2 = new ObjectArrayList();
      Objects.requireNonNull(var2);
      this.getRandomItems(var1, var2::add);
      return var2;
   }

   public ContextKeySet getParamSet() {
      return this.paramSet;
   }

   public void validate(ValidationContext var1) {
      for(int var2 = 0; var2 < this.pools.size(); ++var2) {
         ((LootPool)this.pools.get(var2)).validate(var1.forChild(".pools[" + var2 + "]"));
      }

      for(int var3 = 0; var3 < this.functions.size(); ++var3) {
         ((LootItemFunction)this.functions.get(var3)).validate(var1.forChild(".functions[" + var3 + "]"));
      }

   }

   public void fill(Container var1, LootParams var2, long var3) {
      LootContext var5 = (new LootContext.Builder(var2)).withOptionalRandomSeed(var3).create(this.randomSequence);
      ObjectArrayList var6 = this.getRandomItems(var5);
      RandomSource var7 = var5.getRandom();
      List var8 = this.getAvailableSlots(var1, var7);
      this.shuffleAndSplitItems(var6, var8.size(), var7);
      ObjectListIterator var9 = var6.iterator();

      while(var9.hasNext()) {
         ItemStack var10 = (ItemStack)var9.next();
         if (var8.isEmpty()) {
            LOGGER.warn("Tried to over-fill a container");
            return;
         }

         if (var10.isEmpty()) {
            var1.setItem((Integer)var8.remove(var8.size() - 1), ItemStack.EMPTY);
         } else {
            var1.setItem((Integer)var8.remove(var8.size() - 1), var10);
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

   public static Builder lootTable() {
      return new Builder();
   }

   static {
      EMPTY = new LootTable(LootContextParamSets.EMPTY, Optional.empty(), List.of(), List.of());
      DEFAULT_PARAM_SET = LootContextParamSets.ALL_PARAMS;
      DIRECT_CODEC = RecordCodecBuilder.create((var0) -> var0.group(LootContextParamSets.CODEC.lenientOptionalFieldOf("type", DEFAULT_PARAM_SET).forGetter((var0x) -> var0x.paramSet), ResourceLocation.CODEC.optionalFieldOf("random_sequence").forGetter((var0x) -> var0x.randomSequence), LootPool.CODEC.listOf().optionalFieldOf("pools", List.of()).forGetter((var0x) -> var0x.pools), LootItemFunctions.ROOT_CODEC.listOf().optionalFieldOf("functions", List.of()).forGetter((var0x) -> var0x.functions)).apply(var0, LootTable::new));
      CODEC = RegistryFileCodec.<Holder<LootTable>>create(Registries.LOOT_TABLE, DIRECT_CODEC);
   }

   public static class Builder implements FunctionUserBuilder<Builder> {
      private final ImmutableList.Builder<LootPool> pools = ImmutableList.builder();
      private final ImmutableList.Builder<LootItemFunction> functions = ImmutableList.builder();
      private ContextKeySet paramSet;
      private Optional<ResourceLocation> randomSequence;

      public Builder() {
         super();
         this.paramSet = LootTable.DEFAULT_PARAM_SET;
         this.randomSequence = Optional.empty();
      }

      public Builder withPool(LootPool.Builder var1) {
         this.pools.add(var1.build());
         return this;
      }

      public Builder setParamSet(ContextKeySet var1) {
         this.paramSet = var1;
         return this;
      }

      public Builder setRandomSequence(ResourceLocation var1) {
         this.randomSequence = Optional.of(var1);
         return this;
      }

      public Builder apply(LootItemFunction.Builder var1) {
         this.functions.add(var1.build());
         return this;
      }

      public Builder unwrap() {
         return this;
      }

      public LootTable build() {
         return new LootTable(this.paramSet, this.randomSequence, this.pools.build(), this.functions.build());
      }

      // $FF: synthetic method
      public FunctionUserBuilder unwrap() {
         return this.unwrap();
      }

      // $FF: synthetic method
      public FunctionUserBuilder apply(final LootItemFunction.Builder var1) {
         return this.apply(var1);
      }
   }
}
