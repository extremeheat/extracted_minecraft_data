package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class VegetationPatchFeature extends Feature<VegetationPatchConfiguration> {
   public VegetationPatchFeature(Codec<VegetationPatchConfiguration> var1) {
      super(var1);
   }

   public boolean place(FeaturePlaceContext<VegetationPatchConfiguration> var1) {
      WorldGenLevel var2 = var1.level();
      VegetationPatchConfiguration var3 = (VegetationPatchConfiguration)var1.config();
      RandomSource var4 = var1.random();
      BlockPos var5 = var1.origin();
      Predicate var6 = (var1x) -> {
         return var1x.is(var3.replaceable);
      };
      int var7 = var3.xzRadius.sample(var4) + 1;
      int var8 = var3.xzRadius.sample(var4) + 1;
      Set var9 = this.placeGroundPatch(var2, var3, var4, var5, var6, var7, var8);
      this.distributeVegetation(var1, var2, var3, var4, var9, var7, var8);
      return !var9.isEmpty();
   }

   protected Set<BlockPos> placeGroundPatch(WorldGenLevel var1, VegetationPatchConfiguration var2, RandomSource var3, BlockPos var4, Predicate<BlockState> var5, int var6, int var7) {
      BlockPos.MutableBlockPos var8 = var4.mutable();
      BlockPos.MutableBlockPos var9 = var8.mutable();
      Direction var10 = var2.surface.getDirection();
      Direction var11 = var10.getOpposite();
      HashSet var12 = new HashSet();

      for(int var13 = -var6; var13 <= var6; ++var13) {
         boolean var14 = var13 == -var6 || var13 == var6;

         for(int var15 = -var7; var15 <= var7; ++var15) {
            boolean var16 = var15 == -var7 || var15 == var7;
            boolean var17 = var14 || var16;
            boolean var18 = var14 && var16;
            boolean var19 = var17 && !var18;
            if (!var18 && (!var19 || var2.extraEdgeColumnChance != 0.0F && !(var3.nextFloat() > var2.extraEdgeColumnChance))) {
               var8.setWithOffset(var4, var13, 0, var15);

               int var20;
               for(var20 = 0; var1.isStateAtPosition(var8, BlockBehaviour.BlockStateBase::isAir) && var20 < var2.verticalRange; ++var20) {
                  var8.move(var10);
               }

               for(var20 = 0; var1.isStateAtPosition(var8, (var0) -> {
                  return !var0.isAir();
               }) && var20 < var2.verticalRange; ++var20) {
                  var8.move(var11);
               }

               var9.setWithOffset(var8, (Direction)var2.surface.getDirection());
               BlockState var21 = var1.getBlockState(var9);
               if (var1.isEmptyBlock(var8) && var21.isFaceSturdy(var1, var9, var2.surface.getDirection().getOpposite())) {
                  int var22 = var2.depth.sample(var3) + (var2.extraBottomBlockChance > 0.0F && var3.nextFloat() < var2.extraBottomBlockChance ? 1 : 0);
                  BlockPos var23 = var9.immutable();
                  boolean var24 = this.placeGround(var1, var2, var5, var3, var9, var22);
                  if (var24) {
                     var12.add(var23);
                  }
               }
            }
         }
      }

      return var12;
   }

   protected void distributeVegetation(FeaturePlaceContext<VegetationPatchConfiguration> var1, WorldGenLevel var2, VegetationPatchConfiguration var3, RandomSource var4, Set<BlockPos> var5, int var6, int var7) {
      Iterator var8 = var5.iterator();

      while(var8.hasNext()) {
         BlockPos var9 = (BlockPos)var8.next();
         if (var3.vegetationChance > 0.0F && var4.nextFloat() < var3.vegetationChance) {
            this.placeVegetation(var2, var3, var1.chunkGenerator(), var4, var9);
         }
      }

   }

   protected boolean placeVegetation(WorldGenLevel var1, VegetationPatchConfiguration var2, ChunkGenerator var3, RandomSource var4, BlockPos var5) {
      return ((PlacedFeature)var2.vegetationFeature.value()).place(var1, var3, var4, var5.relative(var2.surface.getDirection().getOpposite()));
   }

   protected boolean placeGround(WorldGenLevel var1, VegetationPatchConfiguration var2, Predicate<BlockState> var3, RandomSource var4, BlockPos.MutableBlockPos var5, int var6) {
      for(int var7 = 0; var7 < var6; ++var7) {
         BlockState var8 = var2.groundState.getState(var4, var5);
         BlockState var9 = var1.getBlockState(var5);
         if (!var8.is(var9.getBlock())) {
            if (!var3.test(var9)) {
               return var7 != 0;
            }

            var1.setBlock(var5, var8, 2);
            var5.move(var2.surface.getDirection());
         }
      }

      return true;
   }
}
