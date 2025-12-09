package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.Nameable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextArg;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class CopyNameFunction extends LootItemConditionalFunction {
   public static final MapCodec<CopyNameFunction> CODEC = RecordCodecBuilder.mapCodec((var0) -> commonFields(var0).and(LootContextArg.ENTITY_OR_BLOCK.fieldOf("source").forGetter((var0x) -> var0x.source)).apply(var0, CopyNameFunction::new));
   private final LootContextArg<Object> source;

   private CopyNameFunction(List<LootItemCondition> var1, LootContextArg<?> var2) {
      super(var1);
      this.source = LootContextArg.<Object>cast(var2);
   }

   public LootItemFunctionType<CopyNameFunction> getType() {
      return LootItemFunctions.COPY_NAME;
   }

   public Set<ContextKey<?>> getReferencedContextParams() {
      return Set.of(this.source.contextParam());
   }

   public ItemStack run(ItemStack var1, LootContext var2) {
      Object var3 = this.source.get(var2);
      if (var3 instanceof Nameable var4) {
         var1.set(DataComponents.CUSTOM_NAME, var4.getCustomName());
      }

      return var1;
   }

   public static LootItemConditionalFunction.Builder<?> copyName(LootContextArg<?> var0) {
      return simpleBuilder((var1) -> new CopyNameFunction(var1, var0));
   }
}
