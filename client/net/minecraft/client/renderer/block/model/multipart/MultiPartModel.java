package net.minecraft.client.renderer.block.model.multipart;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class MultiPartModel implements BlockStateModel {
   private final SharedBakedState shared;
   private final BlockState blockState;
   private @Nullable List<BlockStateModel> models;

   private MultiPartModel(final SharedBakedState shared, final BlockState blockState) {
      super();
      this.shared = shared;
      this.blockState = blockState;
   }

   public TextureAtlasSprite particleIcon() {
      return this.shared.particleIcon;
   }

   public void collectParts(final RandomSource random, final List<BlockModelPart> output) {
      if (this.models == null) {
         this.models = this.shared.selectModels(this.blockState);
      }

      long seed = random.nextLong();

      for(BlockStateModel model : this.models) {
         random.setSeed(seed);
         model.collectParts(random, output);
      }

   }

   public static record Selector<T>(Predicate<BlockState> condition, T model) {
      public Selector {
         super();
      }

      public <S> Selector<S> with(final S newModel) {
         return new Selector<S>(this.condition, newModel);
      }
   }

   private static final class SharedBakedState {
      private final List<Selector<BlockStateModel>> selectors;
      private final TextureAtlasSprite particleIcon;
      private final Map<BitSet, List<BlockStateModel>> subsets = new ConcurrentHashMap();

      private static BlockStateModel getFirstModel(final List<Selector<BlockStateModel>> selectors) {
         if (selectors.isEmpty()) {
            throw new IllegalArgumentException("Model must have at least one selector");
         } else {
            return (BlockStateModel)((Selector)selectors.getFirst()).model();
         }
      }

      public SharedBakedState(final List<Selector<BlockStateModel>> selectors) {
         super();
         this.selectors = selectors;
         BlockStateModel firstModel = getFirstModel(selectors);
         this.particleIcon = firstModel.particleIcon();
      }

      public List<BlockStateModel> selectModels(final BlockState state) {
         BitSet selectedModels = new BitSet();

         for(int i = 0; i < this.selectors.size(); ++i) {
            if (((Selector)this.selectors.get(i)).condition.test(state)) {
               selectedModels.set(i);
            }
         }

         return (List)this.subsets.computeIfAbsent(selectedModels, (selected) -> {
            ImmutableList.Builder<BlockStateModel> result = ImmutableList.builder();

            for(int i = 0; i < this.selectors.size(); ++i) {
               if (selected.get(i)) {
                  result.add((BlockStateModel)((Selector)this.selectors.get(i)).model);
               }
            }

            return result.build();
         });
      }
   }

   public static class Unbaked implements BlockStateModel.UnbakedRoot {
      private final List<Selector<BlockStateModel.Unbaked>> selectors;
      private final ModelBaker.SharedOperationKey<SharedBakedState> sharedStateKey = new ModelBaker.SharedOperationKey<SharedBakedState>() {
         {
            Objects.requireNonNull(Unbaked.this);
         }

         public SharedBakedState compute(final ModelBaker modelBakery) {
            ImmutableList.Builder<Selector<BlockStateModel>> selectors = ImmutableList.builderWithExpectedSize(Unbaked.this.selectors.size());

            for(Selector<BlockStateModel.Unbaked> selector : Unbaked.this.selectors) {
               selectors.add(selector.with(((BlockStateModel.Unbaked)selector.model).bake(modelBakery)));
            }

            return new SharedBakedState(selectors.build());
         }
      };

      public Unbaked(final List<Selector<BlockStateModel.Unbaked>> selectors) {
         super();
         this.selectors = selectors;
      }

      public Object visualEqualityGroup(final BlockState blockState) {
         IntList triggeredSelectors = new IntArrayList();

         for(int i = 0; i < this.selectors.size(); ++i) {
            if (((Selector)this.selectors.get(i)).condition.test(blockState)) {
               triggeredSelectors.add(i);
            }
         }

         record Key(Unbaked model, IntList selectors) {
            Key {
               super();
            }
         }

         return new Key(this, triggeredSelectors);
      }

      public void resolveDependencies(final ResolvableModel.Resolver resolver) {
         this.selectors.forEach((s) -> ((BlockStateModel.Unbaked)s.model).resolveDependencies(resolver));
      }

      public BlockStateModel bake(final BlockState blockState, final ModelBaker modelBakery) {
         SharedBakedState shared = (SharedBakedState)modelBakery.compute(this.sharedStateKey);
         return new MultiPartModel(shared, blockState);
      }
   }
}
