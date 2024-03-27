package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.Util;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class CopyComponentsFunction extends LootItemConditionalFunction {
   public static final MapCodec<CopyComponentsFunction> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> commonFields(var0)
            .and(
               var0.group(
                  CopyComponentsFunction.Source.CODEC.fieldOf("source").forGetter(var0x -> var0x.source),
                  DataComponentType.CODEC.listOf().optionalFieldOf("include").forGetter(var0x -> var0x.include),
                  DataComponentType.CODEC.listOf().optionalFieldOf("exclude").forGetter(var0x -> var0x.exclude)
               )
            )
            .apply(var0, CopyComponentsFunction::new)
   );
   private final CopyComponentsFunction.Source source;
   private final Optional<List<DataComponentType<?>>> include;
   private final Optional<List<DataComponentType<?>>> exclude;
   private final Predicate<DataComponentType<?>> bakedPredicate;

   CopyComponentsFunction(
      List<LootItemCondition> var1, CopyComponentsFunction.Source var2, Optional<List<DataComponentType<?>>> var3, Optional<List<DataComponentType<?>>> var4
   ) {
      super(var1);
      this.source = var2;
      this.include = var3.map(List::copyOf);
      this.exclude = var4.map(List::copyOf);
      ArrayList var5 = new ArrayList(2);
      var4.ifPresent(var1x -> var5.add(var1xx -> !var1x.contains(var1xx)));
      var3.ifPresent(var1x -> var5.add(var1x::contains));
      this.bakedPredicate = Util.allOf(var5);
   }

   @Override
   public LootItemFunctionType getType() {
      return LootItemFunctions.COPY_COMPONENTS;
   }

   @Override
   public Set<LootContextParam<?>> getReferencedContextParams() {
      return this.source.getReferencedContextParams();
   }

   @Override
   public ItemStack run(ItemStack var1, LootContext var2) {
      DataComponentMap var3 = this.source.get(var2);
      var1.applyComponents(var3.filter(this.bakedPredicate));
      return var1;
   }

   public static CopyComponentsFunction.Builder copyComponents(CopyComponentsFunction.Source var0) {
      return new CopyComponentsFunction.Builder(var0);
   }

   public static class Builder extends LootItemConditionalFunction.Builder<CopyComponentsFunction.Builder> {
      private final CopyComponentsFunction.Source source;
      private Optional<com.google.common.collect.ImmutableList.Builder<DataComponentType<?>>> include = Optional.empty();
      private Optional<com.google.common.collect.ImmutableList.Builder<DataComponentType<?>>> exclude = Optional.empty();

      Builder(CopyComponentsFunction.Source var1) {
         super();
         this.source = var1;
      }

      public CopyComponentsFunction.Builder include(DataComponentType<?> var1) {
         if (this.include.isEmpty()) {
            this.include = Optional.of(ImmutableList.builder());
         }

         ((com.google.common.collect.ImmutableList.Builder)this.include.get()).add(var1);
         return this;
      }

      public CopyComponentsFunction.Builder exclude(DataComponentType<?> var1) {
         if (this.exclude.isEmpty()) {
            this.exclude = Optional.of(ImmutableList.builder());
         }

         ((com.google.common.collect.ImmutableList.Builder)this.exclude.get()).add(var1);
         return this;
      }

      protected CopyComponentsFunction.Builder getThis() {
         return this;
      }

      @Override
      public LootItemFunction build() {
         return new CopyComponentsFunction(
            this.getConditions(),
            this.source,
            this.include.map(com.google.common.collect.ImmutableList.Builder::build),
            this.exclude.map(com.google.common.collect.ImmutableList.Builder::build)
         );
      }
   }

   public static enum Source implements StringRepresentable {
      BLOCK_ENTITY("block_entity");

      public static final Codec<CopyComponentsFunction.Source> CODEC = StringRepresentable.fromValues(CopyComponentsFunction.Source::values);
      private final String name;

      private Source(String var3) {
         this.name = var3;
      }

      public DataComponentMap get(LootContext var1) {
         switch(this) {
            case BLOCK_ENTITY:
               BlockEntity var2 = var1.getParamOrNull(LootContextParams.BLOCK_ENTITY);
               return var2 != null ? var2.collectComponents() : DataComponentMap.EMPTY;
            default:
               throw new IncompatibleClassChangeError();
         }
      }

      public Set<LootContextParam<?>> getReferencedContextParams() {
         switch(this) {
            case BLOCK_ENTITY:
               return Set.of(LootContextParams.BLOCK_ENTITY);
            default:
               throw new IncompatibleClassChangeError();
         }
      }

      @Override
      public String getSerializedName() {
         return this.name;
      }
   }
}
