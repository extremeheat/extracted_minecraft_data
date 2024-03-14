package net.minecraft.world.level.storage.loot.entries;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootDataId;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class NestedLootTable extends LootPoolSingletonContainer {
   public static final Codec<NestedLootTable> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(Codec.either(ResourceLocation.CODEC, LootTable.CODEC).fieldOf("value").forGetter(var0x -> var0x.contents))
            .and(singletonFields(var0))
            .apply(var0, NestedLootTable::new)
   );
   private final Either<ResourceLocation, LootTable> contents;

   private NestedLootTable(Either<ResourceLocation, LootTable> var1, int var2, int var3, List<LootItemCondition> var4, List<LootItemFunction> var5) {
      super(var2, var3, var4, var5);
      this.contents = var1;
   }

   @Override
   public LootPoolEntryType getType() {
      return LootPoolEntries.LOOT_TABLE;
   }

   @Override
   public void createItemStack(Consumer<ItemStack> var1, LootContext var2) {
      ((LootTable)this.contents.map(var1x -> var2.getResolver().getLootTable(var1x), var0 -> var0)).getRandomItemsRaw(var2, var1);
   }

   @Override
   public void validate(ValidationContext var1) {
      Optional var2 = this.contents.left();
      if (var2.isPresent()) {
         LootDataId var3 = new LootDataId<>(LootDataType.TABLE, (ResourceLocation)var2.get());
         if (var1.hasVisitedElement(var3)) {
            var1.reportProblem("Table " + var2.get() + " is recursively called");
            return;
         }
      }

      super.validate(var1);
      this.contents
         .ifLeft(
            var1x -> {
               LootDataId var2xx = new LootDataId<>(LootDataType.TABLE, var1x);
               var1.resolver()
                  .<LootTable>getElementOptional(var2xx)
                  .ifPresentOrElse(
                     var3x -> var3x.validate(var1.enterElement("->{" + var1x + "}", var2x)), () -> var1.reportProblem("Unknown loot table called " + var1x)
                  );
            }
         )
         .ifRight(var1x -> var1x.validate(var1.forChild("->{inline}")));
   }

   public static LootPoolSingletonContainer.Builder<?> lootTableReference(ResourceLocation var0) {
      return simpleBuilder((var1, var2, var3, var4) -> new NestedLootTable(Either.left(var0), var1, var2, var3, var4));
   }

   public static LootPoolSingletonContainer.Builder<?> inlineLootTable(LootTable var0) {
      return simpleBuilder((var1, var2, var3, var4) -> new NestedLootTable(Either.right(var0), var1, var2, var3, var4));
   }
}
