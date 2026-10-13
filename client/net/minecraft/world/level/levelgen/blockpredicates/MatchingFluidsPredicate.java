package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.predicates.StatePropertiesPredicate;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

public class MatchingFluidsPredicate extends StateTestingPredicate {
   public static final MapCodec<MatchingFluidsPredicate> CODEC = RecordCodecBuilder.mapCodec((i) -> stateTestingCodec(i).and(i.group(RegistryCodecs.holderSet(Registries.FLUID).fieldOf("fluids").forGetter((c) -> c.fluids), StatePropertiesPredicate.CODEC.optionalFieldOf("state", StatePropertiesPredicate.EMPTY).forGetter((c) -> c.statePredicate))).apply(i, MatchingFluidsPredicate::new));
   private final HolderSet<Fluid> fluids;
   private final StatePropertiesPredicate statePredicate;

   public MatchingFluidsPredicate(final Vec3i offset, final HolderSet<Fluid> fluids, final StatePropertiesPredicate statePredicate) {
      super(offset);
      this.fluids = fluids;
      this.statePredicate = statePredicate;
   }

   protected boolean test(final BlockState state) {
      FluidState fluidState = state.getFluidState();
      return !fluidState.is(this.fluids) ? false : this.statePredicate.matches(fluidState);
   }

   public BlockPredicateType<?> type() {
      return BlockPredicateType.MATCHING_FLUIDS;
   }
}
