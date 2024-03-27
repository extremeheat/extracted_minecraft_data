package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.function.BiFunction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;

public class SequenceFunction implements LootItemFunction {
   public static final MapCodec<SequenceFunction> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(LootItemFunctions.TYPED_CODEC.listOf().fieldOf("functions").forGetter(var0x -> var0x.functions)).apply(var0, SequenceFunction::new)
   );
   public static final Codec<SequenceFunction> INLINE_CODEC = LootItemFunctions.TYPED_CODEC.listOf().xmap(SequenceFunction::new, var0 -> var0.functions);
   private final List<LootItemFunction> functions;
   private final BiFunction<ItemStack, LootContext, ItemStack> compositeFunction;

   private SequenceFunction(List<LootItemFunction> var1) {
      super();
      this.functions = var1;
      this.compositeFunction = LootItemFunctions.compose(var1);
   }

   public static SequenceFunction of(List<LootItemFunction> var0) {
      return new SequenceFunction(List.copyOf(var0));
   }

   public ItemStack apply(ItemStack var1, LootContext var2) {
      return this.compositeFunction.apply(var1, var2);
   }

   @Override
   public void validate(ValidationContext var1) {
      LootItemFunction.super.validate(var1);

      for(int var2 = 0; var2 < this.functions.size(); ++var2) {
         this.functions.get(var2).validate(var1.forChild(".function[" + var2 + "]"));
      }
   }

   @Override
   public LootItemFunctionType getType() {
      return LootItemFunctions.SEQUENCE;
   }
}
