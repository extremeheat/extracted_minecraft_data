package net.minecraft.client.renderer.block.model.multipart;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.block.model.MultiVariant;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.MultiPartBakedModel;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class MultiPart implements BlockStateModel.Unbaked {
   private final List<InstantiatedSelector> selectors;

   MultiPart(List<InstantiatedSelector> var1) {
      super();
      this.selectors = var1;
   }

   public Object visualEqualityGroup(BlockState var1) {
      IntArrayList var2 = new IntArrayList();

      for(int var3 = 0; var3 < this.selectors.size(); ++var3) {
         if (((InstantiatedSelector)this.selectors.get(var3)).predicate.test(var1)) {
            var2.add(var3);
         }
      }

      record 1Key(MultiPart model, IntList selectors) {
         _Key/* $FF was: 1Key*/(MultiPart var1, IntList var2) {
            super();
            this.model = var1;
            this.selectors = var2;
         }
      }

      return new 1Key(this, var2);
   }

   public void resolveDependencies(ResolvableModel.Resolver var1) {
      this.selectors.forEach((var1x) -> var1x.variant.resolveDependencies(var1));
   }

   public BlockStateModel bake(ModelBaker var1) {
      ArrayList var2 = new ArrayList(this.selectors.size());

      for(InstantiatedSelector var4 : this.selectors) {
         BlockStateModel var5 = var4.variant.bake(var1);
         var2.add(new MultiPartBakedModel.Selector(var4.predicate, var5));
      }

      return new MultiPartBakedModel(var2);
   }

   static record InstantiatedSelector(Predicate<BlockState> predicate, MultiVariant variant) {
      final Predicate<BlockState> predicate;
      final MultiVariant variant;

      InstantiatedSelector(Predicate<BlockState> var1, MultiVariant var2) {
         super();
         this.predicate = var1;
         this.variant = var2;
      }
   }

   public static record Definition(List<Selector> selectors) {
      public static final Codec<Definition> CODEC;

      public Definition(List<Selector> var1) {
         super();
         this.selectors = var1;
      }

      public MultiPart instantiate(StateDefinition<Block, BlockState> var1) {
         ArrayList var2 = new ArrayList(this.selectors.size());

         for(Selector var4 : this.selectors) {
            var2.add(new InstantiatedSelector(var4.instantiate(var1), var4.variant()));
         }

         return new MultiPart(var2);
      }

      static {
         CODEC = ExtraCodecs.nonEmptyList(Selector.CODEC.listOf()).xmap(Definition::new, Definition::selectors);
      }
   }
}
