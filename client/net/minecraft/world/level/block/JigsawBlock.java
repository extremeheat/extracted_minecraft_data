package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.BlockHitResult;

public class JigsawBlock extends Block implements EntityBlock, GameMasterBlock {
   public static final MapCodec<JigsawBlock> CODEC = simpleCodec(JigsawBlock::new);
   public static final EnumProperty<FrontAndTop> ORIENTATION = BlockStateProperties.ORIENTATION;

   @Override
   public MapCodec<JigsawBlock> codec() {
      return CODEC;
   }

   protected JigsawBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(this.stateDefinition.any().setValue(ORIENTATION, FrontAndTop.NORTH_UP));
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(ORIENTATION);
   }

   @Override
   protected BlockState rotate(BlockState var1, Rotation var2) {
      return var1.setValue(ORIENTATION, var2.rotation().rotate(var1.getValue(ORIENTATION)));
   }

   @Override
   protected BlockState mirror(BlockState var1, Mirror var2) {
      return var1.setValue(ORIENTATION, var2.rotation().rotate(var1.getValue(ORIENTATION)));
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      Direction var2 = var1.getClickedFace();
      Direction var3;
      if (var2.getAxis() == Direction.Axis.Y) {
         var3 = var1.getHorizontalDirection().getOpposite();
      } else {
         var3 = Direction.UP;
      }

      return this.defaultBlockState().setValue(ORIENTATION, FrontAndTop.fromFrontAndTop(var2, var3));
   }

   @Override
   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new JigsawBlockEntity(var1, var2);
   }

   @Override
   protected InteractionResult useWithoutItem(BlockState var1, Level var2, BlockPos var3, Player var4, BlockHitResult var5) {
      BlockEntity var6 = var2.getBlockEntity(var3);
      if (var6 instanceof JigsawBlockEntity && var4.canUseGameMasterBlocks()) {
         var4.openJigsawBlock((JigsawBlockEntity)var6);
         return InteractionResult.SUCCESS;
      } else {
         return InteractionResult.PASS;
      }
   }

   public static boolean canAttach(StructureTemplate.JigsawBlockInfo var0, StructureTemplate.JigsawBlockInfo var1) {
      Direction var2 = getFrontFacing(var0.info().state());
      Direction var3 = getFrontFacing(var1.info().state());
      Direction var4 = getTopFacing(var0.info().state());
      Direction var5 = getTopFacing(var1.info().state());
      JigsawBlockEntity.JointType var6 = var0.jointType();
      boolean var7 = var6 == JigsawBlockEntity.JointType.ROLLABLE;
      return var2 == var3.getOpposite() && (var7 || var4 == var5) && var0.target().equals(var1.name());
   }

   public static Direction getFrontFacing(BlockState var0) {
      return var0.getValue(ORIENTATION).front();
   }

   public static Direction getTopFacing(BlockState var0) {
      return var0.getValue(ORIENTATION).top();
   }
}
