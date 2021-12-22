package net.minecraft.world.level.block;

import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class PowderSnowCauldronBlock extends LayeredCauldronBlock {
   public PowderSnowCauldronBlock(BlockBehaviour.Properties var1, Predicate<Biome.Precipitation> var2, Map<Item, CauldronInteraction> var3) {
      super(var1, var2, var3);
   }

   protected void handleEntityOnFireInside(BlockState var1, Level var2, BlockPos var3) {
      lowerFillLevel((BlockState)Blocks.WATER_CAULDRON.defaultBlockState().setValue(LEVEL, (Integer)var1.getValue(LEVEL)), var2, var3);
   }
}
