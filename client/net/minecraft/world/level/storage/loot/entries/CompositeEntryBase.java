package net.minecraft.world.level.storage.loot.entries;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public abstract class CompositeEntryBase extends LootPoolEntryContainer {
   protected final List<LootPoolEntryContainer> children;
   private final ComposableEntryContainer composedChildren;

   protected CompositeEntryBase(List<LootPoolEntryContainer> var1, List<LootItemCondition> var2) {
      super(var2);
      this.children = var1;
      this.composedChildren = this.compose(var1);
   }

   @Override
   public void validate(ValidationContext var1) {
      super.validate(var1);
      if (this.children.isEmpty()) {
         var1.reportProblem("Empty children list");
      }

      for(int var2 = 0; var2 < this.children.size(); ++var2) {
         this.children.get(var2).validate(var1.forChild(".entry[" + var2 + "]"));
      }
   }

   protected abstract ComposableEntryContainer compose(List<? extends ComposableEntryContainer> var1);

   @Override
   public final boolean expand(LootContext var1, Consumer<LootPoolEntry> var2) {
      return !this.canRun(var1) ? false : this.composedChildren.expand(var1, var2);
   }

   public static <T extends CompositeEntryBase> MapCodec<T> createCodec(CompositeEntryBase.CompositeEntryConstructor<T> var0) {
      return RecordCodecBuilder.mapCodec(
         var1 -> var1.group(LootPoolEntries.CODEC.listOf().optionalFieldOf("children", List.of()).forGetter(var0xx -> var0xx.children))
               .and(commonFields(var1).t1())
               .apply(var1, var0::create)
      );
   }

   @FunctionalInterface
   public interface CompositeEntryConstructor<T extends CompositeEntryBase> {
      T create(List<LootPoolEntryContainer> var1, List<LootItemCondition> var2);
   }
}
