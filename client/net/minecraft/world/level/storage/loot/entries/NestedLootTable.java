package net.minecraft.world.level.storage.loot.entries;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class NestedLootTable extends LootPoolSingletonContainer {
   public static final MapCodec<NestedLootTable> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Codec.either(LootTable.KEY_CODEC, LootTable.DIRECT_CODEC).fieldOf("value").forGetter((var0x) -> var0x.contents)).and(singletonFields(var0)).apply(var0, NestedLootTable::new));
   public static final ProblemReporter.PathElement INLINE_LOOT_TABLE_PATH_ELEMENT = new ProblemReporter.PathElement() {
      public String get() {
         return "->{inline}";
      }
   };
   private final Either<ResourceKey<LootTable>, LootTable> contents;

   private NestedLootTable(Either<ResourceKey<LootTable>, LootTable> var1, int var2, int var3, List<LootItemCondition> var4, List<LootItemFunction> var5) {
      super(var2, var3, var4, var5);
      this.contents = var1;
   }

   public LootPoolEntryType getType() {
      return LootPoolEntries.LOOT_TABLE;
   }

   public void createItemStack(Consumer<ItemStack> var1, LootContext var2) {
      ((LootTable)this.contents.map((var1x) -> (LootTable)var2.getResolver().get(var1x).map(Holder::value).orElse(LootTable.EMPTY), (var0) -> var0)).getRandomItemsRaw(var2, var1);
   }

   public void validate(ValidationContext var1) {
      Optional var2 = this.contents.left();
      if (var2.isPresent()) {
         ResourceKey var3 = (ResourceKey)var2.get();
         if (!var1.allowsReferences()) {
            var1.reportProblem(new ValidationContext.ReferenceNotAllowedProblem(var3));
            return;
         }

         if (var1.hasVisitedElement(var3)) {
            var1.reportProblem(new ValidationContext.RecursiveReferenceProblem(var3));
            return;
         }
      }

      super.validate(var1);
      this.contents.ifLeft((var1x) -> var1.resolver().get(var1x).ifPresentOrElse((var2) -> ((LootTable)var2.value()).validate(var1.enterElement(new ProblemReporter.ElementReferencePathElement(var1x), var1x)), () -> var1.reportProblem(new ValidationContext.MissingReferenceProblem(var1x)))).ifRight((var1x) -> var1x.validate(var1.forChild(INLINE_LOOT_TABLE_PATH_ELEMENT)));
   }

   public static LootPoolSingletonContainer.Builder<?> lootTableReference(ResourceKey<LootTable> var0) {
      return simpleBuilder((var1, var2, var3, var4) -> new NestedLootTable(Either.left(var0), var1, var2, var3, var4));
   }

   public static LootPoolSingletonContainer.Builder<?> inlineLootTable(LootTable var0) {
      return simpleBuilder((var1, var2, var3, var4) -> new NestedLootTable(Either.right(var0), var1, var2, var3, var4));
   }
}
