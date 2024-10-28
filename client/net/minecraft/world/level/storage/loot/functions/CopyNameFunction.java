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
   public static final MapCodec<CopyNameFunction> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return commonFields(var0).and(CopyNameFunction.NameSource.CODEC.fieldOf("source").forGetter((var0x) -> {
         return var0x.source;
      })).apply(var0, CopyNameFunction::new);
   });
   private final NameSource source;

   private CopyNameFunction(List<LootItemCondition> var1, NameSource var2) {
      super(var1);
      this.source = var2;
   }

   public LootItemFunctionType getType() {
      return LootItemFunctions.COPY_NAME;
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return ImmutableSet.of(this.source.param);
   }

   public ItemStack run(ItemStack var1, LootContext var2) {
      Object var3 = var2.getParamOrNull(this.source.param);
      if (var3 instanceof Nameable var4) {
         var1.set(DataComponents.CUSTOM_NAME, var4.getCustomName());
      }

      return var1;
   }

   public static LootItemConditionalFunction.Builder<?> copyName(NameSource var0) {
      return simpleBuilder((var1) -> {
         return new CopyNameFunction(var1, var0);
      });
   }

   public static enum NameSource implements StringRepresentable {
      THIS("this", LootContextParams.THIS_ENTITY),
      KILLER("killer", LootContextParams.KILLER_ENTITY),
      KILLER_PLAYER("killer_player", LootContextParams.LAST_DAMAGE_PLAYER),
      BLOCK_ENTITY("block_entity", LootContextParams.BLOCK_ENTITY);

      public static final Codec<NameSource> CODEC = StringRepresentable.fromEnum(NameSource::values);
      private final String name;
      final LootContextParam<?> param;

      private NameSource(String var3, LootContextParam var4) {
         this.name = var3;
         this.param = var4;
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static NameSource[] $values() {
         return new NameSource[]{THIS, KILLER, KILLER_PLAYER, BLOCK_ENTITY};
      }
   }
}
