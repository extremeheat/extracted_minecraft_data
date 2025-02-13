package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.WeightedBakedModel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.block.state.BlockState;

public record MultiVariant(List<Variant> variants) implements BlockStateModel.Unbaked {
   public static final Codec<MultiVariant> CODEC;

   public MultiVariant(List<Variant> var1) {
      super();
      if (var1.isEmpty()) {
         throw new IllegalArgumentException("Variant list must contain at least one element");
      } else {
         this.variants = var1;
      }
   }

   public Object visualEqualityGroup(BlockState var1) {
      return this;
   }

   public void resolveDependencies(ResolvableModel.Resolver var1) {
      this.variants.forEach((var1x) -> var1.markDependency(var1x.modelLocation()));
   }

   private static BlockStateModel bakeModel(ModelBaker var0, Variant var1) {
      return SimpleModelWrapper.bake(var0, var1.modelLocation(), var1.modelState().asModelState());
   }

   public BlockStateModel bake(ModelBaker var1) {
      if (this.variants.size() == 1) {
         Variant var5 = (Variant)this.variants.getFirst();
         return bakeModel(var1, var5);
      } else {
         WeightedList.Builder var2 = WeightedList.builder();

         for(Variant var4 : this.variants) {
            var2.add(bakeModel(var1, var4), var4.weight());
         }

         return new WeightedBakedModel(var2.build());
      }
   }

   public MultiVariant with(VariantMutator var1) {
      List var2;
      if (this.variants.size() == 1) {
         var2 = List.of((Variant)var1.apply((Variant)this.variants.getFirst()));
      } else {
         List var10000 = this.variants;
         Objects.requireNonNull(var1);
         var2 = List.copyOf(Lists.transform(var10000, var1::apply));
      }

      return new MultiVariant(var2);
   }

   static {
      CODEC = ExtraCodecs.nonEmptyList(ExtraCodecs.compactListCodec(Variant.CODEC)).xmap(MultiVariant::new, MultiVariant::variants);
   }
}
