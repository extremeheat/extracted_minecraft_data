package net.minecraft.client.renderer.block.model.multipart;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
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

   MultiPartModel(SharedBakedState var1, BlockState var2) {
      super();
      this.shared = var1;
      this.blockState = var2;
   }

   public TextureAtlasSprite particleIcon() {
      return this.shared.particleIcon;
   }

   public void collectParts(RandomSource var1, List<BlockModelPart> var2) {
      if (this.models == null) {
         this.models = this.shared.selectModels(this.blockState);
      }

      long var3 = var1.nextLong();

      for(BlockStateModel var6 : this.models) {
         var1.setSeed(var3);
         var6.collectParts(var1, var2);
      }

   }

   public static record Selector<T>(Predicate<BlockState> condition, T model) {
      final Predicate<BlockState> condition;
      final T model;

      public Selector(Predicate<BlockState> var1, T var2) {
         super();
         this.condition = var1;
         this.model = var2;
      }

      public <S> Selector<S> with(S var1) {
         return new Selector<S>(this.condition, var1);
      }
   }

   static final class SharedBakedState {
      private final List<Selector<BlockStateModel>> selectors;
      final TextureAtlasSprite particleIcon;
      private final Map<BitSet, List<BlockStateModel>> subsets = new ConcurrentHashMap();

      private static BlockStateModel getFirstModel(List<Selector<BlockStateModel>> var0) {
         if (var0.isEmpty()) {
            throw new IllegalArgumentException("Model must have at least one selector");
         } else {
            return (BlockStateModel)((Selector)var0.getFirst()).model();
         }
      }

      public SharedBakedState(List<Selector<BlockStateModel>> var1) {
         super();
         this.selectors = var1;
         BlockStateModel var2 = getFirstModel(var1);
         this.particleIcon = var2.particleIcon();
      }

      public List<BlockStateModel> selectModels(BlockState var1) {
         BitSet var2 = new BitSet();

         for(int var3 = 0; var3 < this.selectors.size(); ++var3) {
            if (((Selector)this.selectors.get(var3)).condition.test(var1)) {
               var2.set(var3);
            }
         }

         return (List)this.subsets.computeIfAbsent(var2, (var1x) -> {
            ImmutableList.Builder var2 = ImmutableList.builder();

            for(int var3 = 0; var3 < this.selectors.size(); ++var3) {
               if (var1x.get(var3)) {
                  var2.add((BlockStateModel)((Selector)this.selectors.get(var3)).model);
               }
            }

            return var2.build();
         });
      }
   }

   public static class Unbaked implements BlockStateModel.UnbakedRoot {
      final List<Selector<BlockStateModel.Unbaked>> selectors;
      private final ModelBaker.SharedOperationKey<SharedBakedState> sharedStateKey = new ModelBaker.SharedOperationKey<SharedBakedState>() {
         public SharedBakedState compute(ModelBaker var1) {
            ImmutableList.Builder var2 = ImmutableList.builderWithExpectedSize(Unbaked.this.selectors.size());

            for(Selector var4 : Unbaked.this.selectors) {
               var2.add(var4.with(((BlockStateModel.Unbaked)var4.model).bake(var1)));
            }

            return new SharedBakedState(var2.build());
         }

         // $FF: synthetic method
         public Object compute(final ModelBaker var1) {
            return this.compute(var1);
         }
      };

      public Unbaked(List<Selector<BlockStateModel.Unbaked>> var1) {
         super();
         this.selectors = var1;
      }

      public Object visualEqualityGroup(BlockState var1) {
         IntArrayList var2 = new IntArrayList();

         for(int var3 = 0; var3 < this.selectors.size(); ++var3) {
            if (((Selector)this.selectors.get(var3)).condition.test(var1)) {
               var2.add(var3);
            }
         }

         record 1Key(Unbaked model, IntList selectors) {
            _Key/* $FF was: 1Key*/(Unbaked var1, IntList var2) {
               super();
               this.model = var1;
               this.selectors = var2;
            }
         }

         return new 1Key(this, var2);
      }

      public void resolveDependencies(ResolvableModel.Resolver var1) {
         this.selectors.forEach((var1x) -> ((BlockStateModel.Unbaked)var1x.model).resolveDependencies(var1));
      }

      public BlockStateModel bake(BlockState var1, ModelBaker var2) {
         SharedBakedState var3 = (SharedBakedState)var2.compute(this.sharedStateKey);
         return new MultiPartModel(var3, var1);
      }
   }
}
