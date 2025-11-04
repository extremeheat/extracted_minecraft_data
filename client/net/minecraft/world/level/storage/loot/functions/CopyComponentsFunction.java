package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Util;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextArg;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class CopyComponentsFunction extends LootItemConditionalFunction {
   private static final Codec<LootContextArg<DataComponentGetter>> GETTER_CODEC = LootContextArg.createArgCodec((var0) -> var0.anyEntity(DirectSource::new).anyBlockEntity(BlockEntitySource::new).anyItemStack(DirectSource::new));
   public static final MapCodec<CopyComponentsFunction> CODEC = RecordCodecBuilder.mapCodec((var0) -> commonFields(var0).and(var0.group(GETTER_CODEC.fieldOf("source").forGetter((var0x) -> var0x.source), DataComponentType.CODEC.listOf().optionalFieldOf("include").forGetter((var0x) -> var0x.include), DataComponentType.CODEC.listOf().optionalFieldOf("exclude").forGetter((var0x) -> var0x.exclude))).apply(var0, CopyComponentsFunction::new));
   private final LootContextArg<DataComponentGetter> source;
   private final Optional<List<DataComponentType<?>>> include;
   private final Optional<List<DataComponentType<?>>> exclude;
   private final Predicate<DataComponentType<?>> bakedPredicate;

   CopyComponentsFunction(List<LootItemCondition> var1, LootContextArg<DataComponentGetter> var2, Optional<List<DataComponentType<?>>> var3, Optional<List<DataComponentType<?>>> var4) {
      super(var1);
      this.source = var2;
      this.include = var3.map(List::copyOf);
      this.exclude = var4.map(List::copyOf);
      ArrayList var5 = new ArrayList(2);
      var4.ifPresent((var1x) -> var5.add((Predicate)(var1) -> !var1x.contains(var1)));
      var3.ifPresent((var1x) -> {
         Objects.requireNonNull(var1x);
         var5.add(var1x::contains);
      });
      this.bakedPredicate = Util.allOf(var5);
   }

   public LootItemFunctionType<CopyComponentsFunction> getType() {
      return LootItemFunctions.COPY_COMPONENTS;
   }

   public Set<ContextKey<?>> getReferencedContextParams() {
      return Set.of(this.source.contextParam());
   }

   public ItemStack run(ItemStack var1, LootContext var2) {
      DataComponentGetter var3 = this.source.get(var2);
      if (var3 != null) {
         if (var3 instanceof DataComponentMap) {
            DataComponentMap var4 = (DataComponentMap)var3;
            var1.applyComponents(var4.filter(this.bakedPredicate));
         } else {
            Collection var5 = (Collection)this.exclude.orElse(List.of());
            ((Stream)this.include.map(Collection::stream).orElse(BuiltInRegistries.DATA_COMPONENT_TYPE.listElements().map(Holder::value))).forEach((var3x) -> {
               if (!var5.contains(var3x)) {
                  TypedDataComponent var4 = var3.getTyped(var3x);
                  if (var4 != null) {
                     var1.set(var4);
                  }

               }
            });
         }
      }

      return var1;
   }

   public static Builder copyComponentsFromEntity(ContextKey<? extends Entity> var0) {
      return new Builder(new DirectSource(var0));
   }

   public static Builder copyComponentsFromBlockEntity(ContextKey<? extends BlockEntity> var0) {
      return new Builder(new BlockEntitySource(var0));
   }

   public static class Builder extends LootItemConditionalFunction.Builder<Builder> {
      private final LootContextArg<DataComponentGetter> source;
      private Optional<ImmutableList.Builder<DataComponentType<?>>> include = Optional.empty();
      private Optional<ImmutableList.Builder<DataComponentType<?>>> exclude = Optional.empty();

      Builder(LootContextArg<DataComponentGetter> var1) {
         super();
         this.source = var1;
      }

      public Builder include(DataComponentType<?> var1) {
         if (this.include.isEmpty()) {
            this.include = Optional.of(ImmutableList.builder());
         }

         ((ImmutableList.Builder)this.include.get()).add(var1);
         return this;
      }

      public Builder exclude(DataComponentType<?> var1) {
         if (this.exclude.isEmpty()) {
            this.exclude = Optional.of(ImmutableList.builder());
         }

         ((ImmutableList.Builder)this.exclude.get()).add(var1);
         return this;
      }

      protected Builder getThis() {
         return this;
      }

      public LootItemFunction build() {
         return new CopyComponentsFunction(this.getConditions(), this.source, this.include.map(ImmutableList.Builder::build), this.exclude.map(ImmutableList.Builder::build));
      }

      // $FF: synthetic method
      protected LootItemConditionalFunction.Builder getThis() {
         return this.getThis();
      }
   }

   static record DirectSource<T extends DataComponentGetter>(ContextKey<? extends T> contextParam) implements LootContextArg.Getter<T, DataComponentGetter> {
      DirectSource(ContextKey<? extends T> var1) {
         super();
         this.contextParam = var1;
      }

      public DataComponentGetter get(T var1) {
         return var1;
      }
   }

   static record BlockEntitySource(ContextKey<? extends BlockEntity> contextParam) implements LootContextArg.Getter<BlockEntity, DataComponentGetter> {
      BlockEntitySource(ContextKey<? extends BlockEntity> var1) {
         super();
         this.contextParam = var1;
      }

      public DataComponentGetter get(BlockEntity var1) {
         return var1.collectComponents();
      }
   }
}
