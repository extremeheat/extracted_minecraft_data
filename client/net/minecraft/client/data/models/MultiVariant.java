package net.minecraft.client.data.models;

import java.util.List;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.block.model.SingleVariant;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.renderer.block.model.VariantMutator;
import net.minecraft.client.resources.model.WeightedVariants;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;

public record MultiVariant(WeightedList<Variant> variants) {
   public MultiVariant {
      super();
      if (variants.isEmpty()) {
         throw new IllegalArgumentException("Variant list must contain at least one element");
      }
   }

   public MultiVariant with(final VariantMutator mutator) {
      return new MultiVariant(this.variants.map(mutator));
   }

   public BlockStateModel.Unbaked toUnbaked() {
      List<Weighted<Variant>> entries = this.variants.unwrap();
      return (BlockStateModel.Unbaked)(entries.size() == 1 ? new SingleVariant.Unbaked((Variant)((Weighted)entries.getFirst()).value()) : new WeightedVariants.Unbaked(this.variants.map(SingleVariant.Unbaked::new)));
   }
}
