package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;

class MatchingFluidsPredicate extends StateTestingPredicate {
   private final HolderSet<Fluid> fluids;
   public static final MapCodec<MatchingFluidsPredicate> CODEC = RecordCodecBuilder.mapCodec((i) -> stateTestingCodec(i).and(RegistryCodecs.homogeneousList(Registries.FLUID).fieldOf("fluids").forGetter((c) -> c.fluids)).apply(i, MatchingFluidsPredicate::new));

   public MatchingFluidsPredicate(final Vec3i offset, final HolderSet<Fluid> fluids) {
      super(offset);
      this.fluids = fluids;
   }

   protected boolean test(final BlockState state) {
      return state.getFluidState().is(this.fluids);
   }

   public BlockPredicateType<?> type() {
      return BlockPredicateType.MATCHING_FLUIDS;
   }
}
