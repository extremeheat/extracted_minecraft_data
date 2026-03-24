package net.minecraft.client.data.models.blockstates;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelDispatcher;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;

public class MultiVariantGenerator implements BlockModelDefinitionGenerator {
   private final Block block;
   private final List<Entry> entries;
   private final Set<Property<?>> seenProperties;

   private MultiVariantGenerator(final Block block, final List<Entry> entries, final Set<Property<?>> seenProperties) {
      super();
      this.block = block;
      this.entries = entries;
      this.seenProperties = seenProperties;
   }

   private static Set<Property<?>> validateAndExpandProperties(final Set<Property<?>> seenProperties, final Block block, final PropertyDispatch<?> generator) {
      List<Property<?>> addedProperties = generator.getDefinedProperties();
      addedProperties.forEach((property) -> {
         if (block.getStateDefinition().getProperty(property.getName()) != property) {
            String var3 = String.valueOf(property);
            throw new IllegalStateException("Property " + var3 + " is not defined for block " + String.valueOf(block));
         } else if (seenProperties.contains(property)) {
            String var10002 = String.valueOf(property);
            throw new IllegalStateException("Values of property " + var10002 + " already defined for block " + String.valueOf(block));
         }
      });
      Set<Property<?>> newSeenProperties = new HashSet(seenProperties);
      newSeenProperties.addAll(addedProperties);
      return newSeenProperties;
   }

   public MultiVariantGenerator with(final PropertyDispatch<VariantMutator> newStage) {
      Set<Property<?>> newSeenProperties = validateAndExpandProperties(this.seenProperties, this.block, newStage);
      List<Entry> newEntries = this.entries.stream().flatMap((entry) -> entry.apply(newStage)).toList();
      return new MultiVariantGenerator(this.block, newEntries, newSeenProperties);
   }

   public MultiVariantGenerator with(final VariantMutator singleMutator) {
      List<Entry> newEntries = this.entries.stream().flatMap((entry) -> entry.apply(singleMutator)).toList();
      return new MultiVariantGenerator(this.block, newEntries, this.seenProperties);
   }

   public BlockStateModelDispatcher create() {
      Map<String, BlockStateModel.Unbaked> variants = new HashMap();

      for(Entry entry : this.entries) {
         variants.put(entry.properties.getKey(), entry.variant.toUnbaked());
      }

      return new BlockStateModelDispatcher(Optional.of(new BlockStateModelDispatcher.SimpleModelSelectors(variants)), Optional.empty());
   }

   public Block block() {
      return this.block;
   }

   public static Empty dispatch(final Block block) {
      return new Empty(block);
   }

   public static MultiVariantGenerator dispatch(final Block block, final MultiVariant initialModel) {
      return new MultiVariantGenerator(block, List.of(new Entry(PropertyValueList.EMPTY, initialModel)), Set.of());
   }

   public static class Empty {
      private final Block block;

      public Empty(final Block block) {
         super();
         this.block = block;
      }

      public MultiVariantGenerator with(final PropertyDispatch<MultiVariant> newStage) {
         Set<Property<?>> newSeenProperties = MultiVariantGenerator.validateAndExpandProperties(Set.of(), this.block, newStage);
         List<Entry> newEntries = newStage.getEntries().entrySet().stream().map((e) -> new Entry((PropertyValueList)e.getKey(), (MultiVariant)e.getValue())).toList();
         return new MultiVariantGenerator(this.block, newEntries, newSeenProperties);
      }
   }

   private static record Entry(PropertyValueList properties, MultiVariant variant) {
      private Entry {
         super();
      }

      public Stream<Entry> apply(final PropertyDispatch<VariantMutator> stage) {
         return stage.getEntries().entrySet().stream().map((property) -> {
            PropertyValueList newSelector = this.properties.extend((PropertyValueList)property.getKey());
            MultiVariant newVariants = this.variant.with((VariantMutator)property.getValue());
            return new Entry(newSelector, newVariants);
         });
      }

      public Stream<Entry> apply(final VariantMutator mutator) {
         return Stream.of(new Entry(this.properties, this.variant.with(mutator)));
      }
   }
}
