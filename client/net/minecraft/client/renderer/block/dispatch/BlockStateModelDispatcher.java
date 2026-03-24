package net.minecraft.client.renderer.block.dispatch;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.client.renderer.block.dispatch.multipart.MultiPartModel;
import net.minecraft.client.renderer.block.dispatch.multipart.Selector;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import org.slf4j.Logger;

public record BlockStateModelDispatcher(Optional<SimpleModelSelectors> simpleModels, Optional<MultiPartDefinition> multiPart) {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final Codec<BlockStateModelDispatcher> CODEC = RecordCodecBuilder.create((i) -> i.group(BlockStateModelDispatcher.SimpleModelSelectors.CODEC.optionalFieldOf("variants").forGetter(BlockStateModelDispatcher::simpleModels), BlockStateModelDispatcher.MultiPartDefinition.CODEC.optionalFieldOf("multipart").forGetter(BlockStateModelDispatcher::multiPart)).apply(i, BlockStateModelDispatcher::new)).validate((o) -> o.simpleModels().isEmpty() && o.multiPart().isEmpty() ? DataResult.error(() -> "Neither 'variants' nor 'multipart' found") : DataResult.success(o));

   public BlockStateModelDispatcher {
      super();
   }

   public Map<BlockState, BlockStateModel.UnbakedRoot> instantiate(final StateDefinition<Block, BlockState> stateDefinition, final Supplier<String> source) {
      Map<BlockState, BlockStateModel.UnbakedRoot> matchedStates = new IdentityHashMap();
      this.simpleModels.ifPresent((s) -> s.instantiate(stateDefinition, source, (state, model) -> {
            BlockStateModel.UnbakedRoot previousValue = (BlockStateModel.UnbakedRoot)matchedStates.put(state, model);
            if (previousValue != null) {
               throw new IllegalArgumentException("Overlapping definition on state: " + String.valueOf(state));
            }
         }));
      this.multiPart.ifPresent((m) -> {
         List<BlockState> possibleStates = stateDefinition.getPossibleStates();
         BlockStateModel.UnbakedRoot model = m.instantiate(stateDefinition);

         for(BlockState state : possibleStates) {
            matchedStates.putIfAbsent(state, model);
         }

      });
      return matchedStates;
   }

   public static record SimpleModelSelectors(Map<String, BlockStateModel.Unbaked> models) {
      public static final Codec<SimpleModelSelectors> CODEC;

      public SimpleModelSelectors {
         super();
      }

      public void instantiate(final StateDefinition<Block, BlockState> stateDefinition, final Supplier<String> source, final BiConsumer<BlockState, BlockStateModel.UnbakedRoot> output) {
         this.models.forEach((selectorString, model) -> {
            try {
               Predicate<StateHolder<Block, BlockState>> selector = VariantSelector.predicate(stateDefinition, selectorString);
               BlockStateModel.UnbakedRoot wrapper = model.asRoot();
               Iterator i$ = stateDefinition.getPossibleStates().iterator();

               while(i$.hasNext()) {
                  BlockState state = (BlockState)i$.next();
                  if (selector.test(state)) {
                     output.accept(state, wrapper);
                  }
               }
            } catch (Exception e) {
               BlockStateModelDispatcher.LOGGER.warn("Exception loading blockstate definition: '{}' for variant: '{}': {}", new Object[]{source.get(), selectorString, e.getMessage()});
            }

         });
      }

      static {
         CODEC = ExtraCodecs.nonEmptyMap(Codec.unboundedMap(Codec.STRING, BlockStateModel.Unbaked.CODEC)).xmap(SimpleModelSelectors::new, SimpleModelSelectors::models);
      }
   }

   public static record MultiPartDefinition(List<Selector> selectors) {
      public static final Codec<MultiPartDefinition> CODEC;

      public MultiPartDefinition {
         super();
      }

      public MultiPartModel.Unbaked instantiate(final StateDefinition<Block, BlockState> stateDefinition) {
         ImmutableList.Builder<MultiPartModel.Selector<BlockStateModel.Unbaked>> instantiatedSelectors = ImmutableList.builderWithExpectedSize(this.selectors.size());

         for(Selector selector : this.selectors) {
            instantiatedSelectors.add(new MultiPartModel.Selector(selector.instantiate(stateDefinition), selector.variant()));
         }

         return new MultiPartModel.Unbaked(instantiatedSelectors.build());
      }

      static {
         CODEC = ExtraCodecs.nonEmptyList(Selector.CODEC.listOf()).xmap(MultiPartDefinition::new, MultiPartDefinition::selectors);
      }
   }
}
