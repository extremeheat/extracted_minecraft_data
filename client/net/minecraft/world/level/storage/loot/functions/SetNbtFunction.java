package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetNbtFunction extends LootItemConditionalFunction {
   public static final Codec<SetNbtFunction> CODEC = RecordCodecBuilder.create(
      var0 -> commonFields(var0).and(TagParser.AS_CODEC.fieldOf("tag").forGetter(var0x -> var0x.tag)).apply(var0, SetNbtFunction::new)
   );
   private final CompoundTag tag;

   private SetNbtFunction(List<LootItemCondition> var1, CompoundTag var2) {
      super(var1);
      this.tag = var2;
   }

   @Override
   public LootItemFunctionType getType() {
      return LootItemFunctions.SET_NBT;
   }

   @Override
   public ItemStack run(ItemStack var1, LootContext var2) {
      var1.getOrCreateTag().merge(this.tag);
      return var1;
   }

   @Deprecated
   public static LootItemConditionalFunction.Builder<?> setTag(CompoundTag var0) {
      return simpleBuilder(var1 -> new SetNbtFunction(var1, var0));
   }
}
