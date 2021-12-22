package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.phys.BlockHitResult;

public class StructureBlock extends BaseEntityBlock implements GameMasterBlock {
   public static final EnumProperty<StructureMode> MODE;

   protected StructureBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(MODE, StructureMode.LOAD));
   }

   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new StructureBlockEntity(var1, var2);
   }

   public InteractionResult use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      BlockEntity var7 = var2.getBlockEntity(var3);
      if (var7 instanceof StructureBlockEntity) {
         return ((StructureBlockEntity)var7).usedBy(var4) ? InteractionResult.sidedSuccess(var2.isClientSide) : InteractionResult.PASS;
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

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(MODE);
   }

   public void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, BlockPos var5, boolean var6) {
      if (var2 instanceof ServerLevel) {
         BlockEntity var7 = var2.getBlockEntity(var3);
         if (var7 instanceof StructureBlockEntity) {
            StructureBlockEntity var8 = (StructureBlockEntity)var7;
            boolean var9 = var2.hasNeighborSignal(var3);
            boolean var10 = var8.isPowered();
            if (var9 && !var10) {
               var8.setPowered(true);
               this.trigger((ServerLevel)var2, var8);
            } else if (!var9 && var10) {
               var8.setPowered(false);
            }

         }
      }
   }

   private void trigger(ServerLevel var1, StructureBlockEntity var2) {
      switch(var2.getMode()) {
      case SAVE:
         var2.saveStructure(false);
         break;
      case LOAD:
         var2.loadStructure(var1, false);
         break;
      case CORNER:
         var2.unloadStructure();
      case DATA:
      }

   }

   static {
      MODE = BlockStateProperties.STRUCTUREBLOCK_MODE;
   }
}
