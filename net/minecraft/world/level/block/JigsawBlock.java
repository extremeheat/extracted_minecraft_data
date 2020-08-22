package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.BlockHitResult;

public class JigsawBlock extends DirectionalBlock implements EntityBlock {
   protected JigsawBlock(Block.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.UP));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder var1) {
      var1.add(FACING);
   }

   public BlockState rotate(BlockState var1, Rotation var2) {
      return (BlockState)var1.setValue(FACING, var2.rotate((Direction)var1.getValue(FACING)));
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return (BlockState)this.defaultBlockState().setValue(FACING, var1.getClickedFace());
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockGetter var1) {
      return new JigsawBlockEntity();
   }

   public InteractionResult use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      BlockEntity var7 = var2.getBlockEntity(var3);
      if (var7 instanceof JigsawBlockEntity && var4.canUseGameMasterBlocks()) {
         var4.openJigsawBlock((JigsawBlockEntity)var7);
         return InteractionResult.SUCCESS;
      } else {
         return InteractionResult.PASS;
      }
   }

   public static boolean canAttach(StructureTemplate.StructureBlockInfo var0, StructureTemplate.StructureBlockInfo var1) {
      return var0.state.getValue(FACING) == ((Direction)var1.state.getValue(FACING)).getOpposite() && var0.nbt.getString("attachement_type").equals(var1.nbt.getString("attachement_type"));
   }
}
