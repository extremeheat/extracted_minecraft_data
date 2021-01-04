package net.minecraft.world.level.storage.loot.entries;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTableProblemCollector;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.apache.commons.lang3.ArrayUtils;

public class AlternativesEntry extends CompositeEntryBase {
   AlternativesEntry(LootPoolEntryContainer[] var1, LootItemCondition[] var2) {
      super(var1, var2);
   }

   protected ComposableEntryContainer compose(ComposableEntryContainer[] var1) {
      switch(var1.length) {
      case 0:
         return ALWAYS_FALSE;
      case 1:
         return var1[0];
      case 2:
         return var1[0].or(var1[1]);
      default:
         return (var1x, var2) -> {
            ComposableEntryContainer[] var3 = var1;
            int var4 = var1.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               ComposableEntryContainer var6 = var3[var5];
               if (var6.expand(var1x, var2)) {
                  return true;
               }
            }

            return false;
         };
      }
   }

   public void validate(LootTableProblemCollector var1, Function<ResourceLocation, LootTable> var2, Set<ResourceLocation> var3, LootContextParamSet var4) {
      super.validate(var1, var2, var3, var4);

      for(int var5 = 0; var5 < this.children.length - 1; ++var5) {
         if (ArrayUtils.isEmpty(this.children[var5].conditions)) {
            var1.reportProblem("Unreachable entry!");
         }
      }

   }

   public static AlternativesEntry.Builder alternatives(LootPoolEntryContainer.Builder<?>... var0) {
      return new AlternativesEntry.Builder(var0);
   }

   public static class Builder extends LootPoolEntryContainer.Builder<AlternativesEntry.Builder> {
      private final List<LootPoolEntryContainer> entries = Lists.newArrayList();

      public Builder(LootPoolEntryContainer.Builder<?>... var1) {
         super();
         LootPoolEntryContainer.Builder[] var2 = var1;
         int var3 = var1.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            LootPoolEntryContainer.Builder var5 = var2[var4];
            this.entries.add(var5.build());
         }

      }

      protected AlternativesEntry.Builder getThis() {
         return this;
      }

      public AlternativesEntry.Builder otherwise(LootPoolEntryContainer.Builder<?> var1) {
         this.entries.add(var1.build());
         return this;
      }

      public LootPoolEntryContainer build() {
         return new AlternativesEntry((LootPoolEntryContainer[])this.entries.toArray(new LootPoolEntryContainer[0]), this.getConditions());
      }

      // $FF: synthetic method
      protected LootPoolEntryContainer.Builder getThis() {
         return this.getThis();
      }
   }
}
