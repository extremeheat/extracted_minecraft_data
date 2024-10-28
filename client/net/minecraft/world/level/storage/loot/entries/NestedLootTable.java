package net.minecraft.world.level.storage.loot.entries;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class NestedLootTable extends LootPoolSingletonContainer {
   public static final MapCodec<NestedLootTable> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(Codec.either(ResourceKey.codec(Registries.LOOT_TABLE), LootTable.DIRECT_CODEC).fieldOf("value").forGetter((var0x) -> {
         return var0x.contents;
      })).and(singletonFields(var0)).apply(var0, NestedLootTable::new);
   });
   private final Either<ResourceKey<LootTable>, LootTable> contents;

   private NestedLootTable(Either<ResourceKey<LootTable>, LootTable> var1, int var2, int var3, List<LootItemCondition> var4, List<LootItemFunction> var5) {
      super(var2, var3, var4, var5);
      this.contents = var1;
   }

   public LootPoolEntryType getType() {
      return LootPoolEntries.LOOT_TABLE;
   }

   public void createItemStack(Consumer<ItemStack> var1, LootContext var2) {
      ((LootTable)this.contents.map((var1x) -> {
         return (LootTable)var2.getResolver().get(Registries.LOOT_TABLE, var1x).map(Holder::value).orElse(LootTable.EMPTY);
      }, (var0) -> {
         return var0;
      })).getRandomItemsRaw(var2, var1);
   }

   public void validate(ValidationContext var1) {
      Optional var2 = this.contents.left();
      if (var2.isPresent()) {
         ResourceKey var3 = (ResourceKey)var2.get();
         if (!var1.allowsReferences()) {
            var1.reportProblem("Uses reference to " + String.valueOf(var3.location()) + ", but references are not allowed");
            return;
         }

         if (var1.hasVisitedElement(var3)) {
            var1.reportProblem("Table " + String.valueOf(var3.location()) + " is recursively called");
            return;
         }
      }

      super.validate(var1);
      this.contents.ifLeft((var1x) -> {
         var1.resolver().get(Registries.LOOT_TABLE, var1x).ifPresentOrElse((var2) -> {
            ((LootTable)var2.value()).validate(var1.enterElement("->{" + String.valueOf(var1x.location()) + "}", var1x));
         }, () -> {
            var1.reportProblem("Unknown loot table called " + String.valueOf(var1x.location()));
         });
      }).ifRight((var1x) -> {
         var1x.validate(var1.forChild("->{inline}"));
      });
   }

   public static LootPoolSingletonContainer.Builder<?> lootTableReference(ResourceKey<LootTable> var0) {
      return simpleBuilder((var1, var2, var3, var4) -> {
         return new NestedLootTable(Either.left(var0), var1, var2, var3, var4);
      });
   }

   public static LootPoolSingletonContainer.Builder<?> inlineLootTable(LootTable var0) {
      return simpleBuilder((var1, var2, var3, var4) -> {
         return new NestedLootTable(Either.right(var0), var1, var2, var3, var4);
      });
   }
}
