package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.InsideBlockEffectType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LavaCauldronBlock extends AbstractCauldronBlock {
   public static final MapCodec<LavaCauldronBlock> CODEC = simpleCodec(LavaCauldronBlock::new);
   private static final VoxelShape SHAPE_INSIDE = Block.column(12.0, 4.0, 15.0);
   private static final VoxelShape FILLED_SHAPE;

   public MapCodec<LavaCauldronBlock> codec() {
      return CODEC;
   }

   public LavaCauldronBlock(BlockBehaviour.Properties var1) {
      super(var1, CauldronInteraction.LAVA);
   }

   protected double getContentHeight(BlockState var1) {
      return 0.9375;
   }

   public boolean isFull(BlockState var1) {
      return true;
   }

   protected VoxelShape getEntityInsideCollisionShape(BlockState var1, BlockGetter var2, BlockPos var3, Entity var4) {
      return FILLED_SHAPE;
   }

   protected void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4, InsideBlockEffectApplier var5) {
      var5.apply(InsideBlockEffectType.LAVA_IGNITE);
      var5.runAfter(InsideBlockEffectType.LAVA_IGNITE, Entity::lavaHurt);
   }

   protected int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3, Direction var4) {
      return 3;
   }

   static {
      FILLED_SHAPE = Shapes.or(AbstractCauldronBlock.SHAPE, SHAPE_INSIDE);
   }
}
