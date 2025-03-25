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
   public MultiVariant(WeightedList<Variant> var1) {
      super();
      if (var1.isEmpty()) {
         throw new IllegalArgumentException("Variant list must contain at least one element");
      } else {
         this.variants = var1;
      }
   }

   public MultiVariant with(VariantMutator var1) {
      return new MultiVariant(this.variants.map(var1));
   }

   public BlockStateModel.Unbaked toUnbaked() {
      List var1 = this.variants.unwrap();
      return (BlockStateModel.Unbaked)(var1.size() == 1 ? new SingleVariant.Unbaked((Variant)((Weighted)var1.getFirst()).value()) : new WeightedVariants.Unbaked(this.variants.map(SingleVariant.Unbaked::new)));
   }
}
