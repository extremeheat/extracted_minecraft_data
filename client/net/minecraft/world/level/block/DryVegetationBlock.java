package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.sounds.AmbientDesertBlockSoundsPlayer;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DryVegetationBlock extends VegetationBlock {
   public static final MapCodec<DryVegetationBlock> CODEC = simpleCodec(DryVegetationBlock::new);
   private static final VoxelShape SHAPE = Block.column(12.0, 0.0, 13.0);

   public MapCodec<? extends DryVegetationBlock> codec() {
      return CODEC;
   }

   protected DryVegetationBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   protected boolean mayPlaceOn(BlockState var1, BlockGetter var2, BlockPos var3) {
      return var1.is(BlockTags.DRY_VEGETATION_MAY_PLACE_ON);
   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      AmbientDesertBlockSoundsPlayer.playAmbientDeadBushSounds(var2, var3, var4);
   }
}
