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
   private static final Codec<LootContextArg<DataComponentGetter>> GETTER_CODEC = LootContextArg.createArgCodec((builder) -> builder.anyEntity(DirectSource::new).anyBlockEntity(BlockEntitySource::new).anyItemStack(DirectSource::new));
   public static final MapCodec<CopyComponentsFunction> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> commonFields(i).and(i.group(GETTER_CODEC.fieldOf("source").forGetter((f) -> f.source), DataComponentType.CODEC.listOf().optionalFieldOf("include").forGetter((f) -> f.include), DataComponentType.CODEC.listOf().optionalFieldOf("exclude").forGetter((f) -> f.exclude))).apply(i, CopyComponentsFunction::new));
   private final LootContextArg<DataComponentGetter> source;
   private final Optional<List<DataComponentType<?>>> include;
   private final Optional<List<DataComponentType<?>>> exclude;
   private final Predicate<DataComponentType<?>> bakedPredicate;

   private CopyComponentsFunction(final List<LootItemCondition> predicates, final LootContextArg<DataComponentGetter> source, final Optional<List<DataComponentType<?>>> include, final Optional<List<DataComponentType<?>>> exclude) {
      super(predicates);
      this.source = source;
      this.include = include.map(List::copyOf);
      this.exclude = exclude.map(List::copyOf);
      List<Predicate<DataComponentType<?>>> componentPredicates = new ArrayList(2);
      exclude.ifPresent((s) -> componentPredicates.add((Predicate)(e) -> !s.contains(e)));
      include.ifPresent((s) -> {
         Objects.requireNonNull(s);
         componentPredicates.add(s::contains);
      });
      this.bakedPredicate = Util.allOf(componentPredicates);
   }

   public MapCodec<CopyComponentsFunction> codec() {
      return MAP_CODEC;
   }

   public Set<ContextKey<?>> getReferencedContextParams() {
      return Set.of(this.source.contextParam());
   }

   public ItemStack run(final ItemStack itemStack, final LootContext context) {
      DataComponentGetter data = this.source.get(context);
      if (data != null) {
         if (data instanceof DataComponentMap) {
            DataComponentMap sourceComponents = (DataComponentMap)data;
            itemStack.applyComponents(sourceComponents.filter(this.bakedPredicate));
         } else {
            Collection<DataComponentType<?>> exclude = (Collection)this.exclude.orElse(List.of());
            ((Stream)this.include.map(Collection::stream).orElse(BuiltInRegistries.DATA_COMPONENT_TYPE.listElements().map(Holder::value))).forEach((componentType) -> {
               if (!exclude.contains(componentType)) {
                  TypedDataComponent<?> value = data.getTyped(componentType);
                  if (value != null) {
                     itemStack.set(value);
                  }

               }
            });
         }
      }

      return itemStack;
   }

   public static Builder copyComponentsFromEntity(final ContextKey<? extends Entity> source) {
      return new Builder(new DirectSource(source));
   }

   public static Builder copyComponentsFromBlockEntity(final ContextKey<? extends BlockEntity> source) {
      return new Builder(new BlockEntitySource(source));
   }

   public static class Builder extends LootItemConditionalFunction.Builder<Builder> {
      private final LootContextArg<DataComponentGetter> source;
      private Optional<ImmutableList.Builder<DataComponentType<?>>> include = Optional.empty();
      private Optional<ImmutableList.Builder<DataComponentType<?>>> exclude = Optional.empty();

      private Builder(final LootContextArg<DataComponentGetter> source) {
         super();
         this.source = source;
      }

      public Builder include(final DataComponentType<?> type) {
         if (this.include.isEmpty()) {
            this.include = Optional.of(ImmutableList.builder());
         }

         ((ImmutableList.Builder)this.include.get()).add(type);
         return this;
      }

      public Builder exclude(final DataComponentType<?> type) {
         if (this.exclude.isEmpty()) {
            this.exclude = Optional.of(ImmutableList.builder());
         }

         ((ImmutableList.Builder)this.exclude.get()).add(type);
         return this;
      }

      protected Builder getThis() {
         return this;
      }

      public LootItemFunction build() {
         return new CopyComponentsFunction(this.getConditions(), this.source, this.include.map(ImmutableList.Builder::build), this.exclude.map(ImmutableList.Builder::build));
      }
   }

   private static record DirectSource<T extends DataComponentGetter>(ContextKey<? extends T> contextParam) implements LootContextArg.Getter<T, DataComponentGetter> {
      private DirectSource {
         super();
      }

      public DataComponentGetter get(final T value) {
         return value;
      }
   }

   private static record BlockEntitySource(ContextKey<? extends BlockEntity> contextParam) implements LootContextArg.Getter<BlockEntity, DataComponentGetter> {
      private BlockEntitySource {
         super();
      }

      public DataComponentGetter get(final BlockEntity blockEntity) {
         return blockEntity.collectComponents();
      }
   }
}
