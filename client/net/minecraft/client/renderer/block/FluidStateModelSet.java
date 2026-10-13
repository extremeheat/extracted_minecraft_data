package net.minecraft.client.renderer.block;

import java.util.Map;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.color.block.BlockTintSources;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.MaterialBaker;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class FluidStateModelSet {
   private static final FluidModel.Unbaked WATER_MODEL = new FluidModel.Unbaked(new Material(Identifier.withDefaultNamespace("block/water_still")), new Material(Identifier.withDefaultNamespace("block/water_flow")), new Material(Identifier.withDefaultNamespace("block/water_overlay")), BlockTintSources.water());
   private static final FluidModel.Unbaked LAVA_MODEL = new FluidModel.Unbaked(new Material(Identifier.withDefaultNamespace("block/lava_still")), new Material(Identifier.withDefaultNamespace("block/lava_flow")), (Material)null, (BlockTintSource)null);
   private final Map<Fluid, FluidModel> modelByFluid;
   private final FluidModel missingModel;

   public FluidStateModelSet(final Map<Fluid, FluidModel> modelByFluid, final FluidModel missingModel) {
      super();
      this.modelByFluid = modelByFluid;
      this.missingModel = missingModel;
   }

   public static Map<Fluid, FluidModel> bake(final MaterialBaker materials) {
      return Map.of(Fluids.WATER, WATER_MODEL.bake(materials, () -> "Water"), Fluids.LAVA, LAVA_MODEL.bake(materials, () -> "Lava"));
   }

   public FluidModel get(final FluidState state) {
      return (FluidModel)this.modelByFluid.getOrDefault(state.getType(), this.missingModel);
   }
}
