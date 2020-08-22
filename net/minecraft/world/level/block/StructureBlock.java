package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.phys.BlockHitResult;

public class StructureBlock extends BaseEntityBlock {
   public static final EnumProperty MODE;

   protected StructureBlock(Block.Properties var1) {
      super(var1);
   }

   public BlockEntity newBlockEntity(BlockGetter var1) {
      return new StructureBlockEntity();
   }

   public InteractionResult use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      BlockEntity var7 = var2.getBlockEntity(var3);
      if (var7 instanceof StructureBlockEntity) {
         return ((StructureBlockEntity)var7).usedBy(var4) ? InteractionResult.SUCCESS : InteractionResult.PASS;
      } else {
         return InteractionResult.PASS;
      }
   }

   public void setPlacedBy(Level var1, BlockPos var2, BlockState var3, @Nullable LivingEntity var4, ItemStack var5) {
      if (!var1.isClientSide) {
         if (var4 != null) {
            BlockEntity var6 = var1.getBlockEntity(var2);
            if (var6 instanceof StructureBlockEntity) {
               ((StructureBlockEntity)var6).createdBy(var4);
            }
         }

      }
   }

   public RenderShape getRenderShape(BlockState var1) {
      return RenderShape.MODEL;
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return (BlockState)this.defaultBlockState().setValue(MODE, StructureMode.DATA);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder var1) {
      var1.add(MODE);
   }

   public void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, BlockPos var5, boolean var6) {
      if (!var2.isClientSide) {
         BlockEntity var7 = var2.getBlockEntity(var3);
         if (var7 instanceof StructureBlockEntity) {
            StructureBlockEntity var8 = (StructureBlockEntity)var7;
            boolean var9 = var2.hasNeighborSignal(var3);
            boolean var10 = var8.isPowered();
            if (var9 && !var10) {
               var8.setPowered(true);
               this.trigger(var8);
            } else if (!var9 && var10) {
               var8.setPowered(false);
            }

         }
      }
   }

   private void trigger(StructureBlockEntity var1) {
      switch(var1.getMode()) {
      case SAVE:
         var1.saveStructure(false);
         break;
      case LOAD:
         var1.loadStructure(false);
         break;
      case CORNER:
         var1.unloadStructure();
      case DATA:
      }

   }

   static {
      MODE = BlockStateProperties.STRUCTUREBLOCK_MODE;
   }
}
