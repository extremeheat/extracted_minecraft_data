package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Nameable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class CopyNameFunction extends LootItemConditionalFunction {
   public static final MapCodec<CopyNameFunction> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> commonFields(var0).and(CopyNameFunction.NameSource.CODEC.fieldOf("source").forGetter(var0x -> var0x.source)).apply(var0, CopyNameFunction::new)
   );
   private final CopyNameFunction.NameSource source;

   private CopyNameFunction(List<LootItemCondition> var1, CopyNameFunction.NameSource var2) {
      super(var1);
      this.source = var2;
   }

   @Override
   public LootItemFunctionType<CopyNameFunction> getType() {
      return LootItemFunctions.COPY_NAME;
   }

   @Override
   public Set<LootContextParam<?>> getReferencedContextParams() {
      return ImmutableSet.of(this.source.param);
   }

   @Override
   public ItemStack run(ItemStack var1, LootContext var2) {
      if (var2.getParamOrNull(this.source.param) instanceof Nameable var4) {
         var1.set(DataComponents.CUSTOM_NAME, var4.getCustomName());
      }

      return var1;
   }

   public static LootItemConditionalFunction.Builder<?> copyName(CopyNameFunction.NameSource var0) {
      return simpleBuilder(var1 -> new CopyNameFunction(var1, var0));
   }

   public static enum NameSource implements StringRepresentable {
      THIS("this", LootContextParams.THIS_ENTITY),
      KILLER("killer", LootContextParams.KILLER_ENTITY),
      KILLER_PLAYER("killer_player", LootContextParams.LAST_DAMAGE_PLAYER),
      BLOCK_ENTITY("block_entity", LootContextParams.BLOCK_ENTITY);

      public static final Codec<CopyNameFunction.NameSource> CODEC = StringRepresentable.fromEnum(CopyNameFunction.NameSource::values);
      private final String name;
      final LootContextParam<?> param;

      private NameSource(final String param3, final LootContextParam<?> param4) {
         this.name = nullxx;
         this.param = nullxxx;
      }

      @Override
      public String getSerializedName() {
         return this.name;
      }
   }
}
