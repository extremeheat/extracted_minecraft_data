package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;

class MatchingFluidsPredicate extends StateTestingPredicate {
   private final HolderSet<Fluid> fluids;
   public static final Codec<MatchingFluidsPredicate> CODEC = RecordCodecBuilder.create((var0) -> {
      return stateTestingCodec(var0).and(RegistryCodecs.homogeneousList(Registry.FLUID_REGISTRY).fieldOf("fluids").forGetter((var0x) -> {
         return var0x.fluids;
      })).apply(var0, MatchingFluidsPredicate::new);
   });

   public MatchingFluidsPredicate(Vec3i var1, HolderSet<Fluid> var2) {
      super(var1);
      this.fluids = var2;
   }

   protected boolean test(BlockState var1) {
      return var1.getFluidState().is(this.fluids);
   }

   public BlockPredicateType<?> type() {
      return BlockPredicateType.MATCHING_FLUIDS;
   }
}
