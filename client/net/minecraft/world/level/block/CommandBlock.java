package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandBlock extends BaseEntityBlock implements GameMasterBlock {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final DirectionProperty FACING;
   public static final BooleanProperty CONDITIONAL;
   private final boolean automatic;

   public CommandBlock(BlockBehaviour.Properties var1, boolean var2) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(CONDITIONAL, false));
      this.automatic = var2;
   }

   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      CommandBlockEntity var3 = new CommandBlockEntity(var1, var2);
      var3.setAutomatic(this.automatic);
      return var3;
   }

   public void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, BlockPos var5, boolean var6) {
      if (!var2.isClientSide) {
         BlockEntity var7 = var2.getBlockEntity(var3);
         if (var7 instanceof CommandBlockEntity) {
            CommandBlockEntity var8 = (CommandBlockEntity)var7;
            boolean var9 = var2.hasNeighborSignal(var3);
            boolean var10 = var8.isPowered();
            var8.setPowered(var9);
            if (!var10 && !var8.isAutomatic() && var8.getMode() != CommandBlockEntity.Mode.SEQUENCE) {
               if (var9) {
                  var8.markConditionMet();
                  var2.getBlockTicks().scheduleTick(var3, this, 1);
               }

            }
         }
      }
   }

   public void tick(BlockState var1, ServerLevel var2, BlockPos var3, Random var4) {
      BlockEntity var5 = var2.getBlockEntity(var3);
      if (var5 instanceof CommandBlockEntity) {
         CommandBlockEntity var6 = (CommandBlockEntity)var5;
         BaseCommandBlock var7 = var6.getCommandBlock();
         boolean var8 = !StringUtil.isNullOrEmpty(var7.getCommand());
         CommandBlockEntity.Mode var9 = var6.getMode();
         boolean var10 = var6.wasConditionMet();
         if (var9 == CommandBlockEntity.Mode.AUTO) {
            var6.markConditionMet();
            if (var10) {
               this.execute(var1, var2, var3, var7, var8);
            } else if (var6.isConditional()) {
               var7.setSuccessCount(0);
            }

            if (var6.isPowered() || var6.isAutomatic()) {
               var2.getBlockTicks().scheduleTick(var3, this, 1);
            }
         } else if (var9 == CommandBlockEntity.Mode.REDSTONE) {
            if (var10) {
               this.execute(var1, var2, var3, var7, var8);
            } else if (var6.isConditional()) {
               var7.setSuccessCount(0);
            }
         }

         var2.updateNeighbourForOutputSignal(var3, this);
      }

   }

   private void execute(BlockState var1, Level var2, BlockPos var3, BaseCommandBlock var4, boolean var5) {
      if (var5) {
         var4.performCommand(var2);
      } else {
         var4.setSuccessCount(0);
      }

      executeChain(var2, var3, (Direction)var1.getValue(FACING));
   }

   public InteractionResult use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      BlockEntity var7 = var2.getBlockEntity(var3);
      if (var7 instanceof CommandBlockEntity && var4.canUseGameMasterBlocks()) {
         var4.openCommandBlock((CommandBlockEntity)var7);
         return InteractionResult.sidedSuccess(var2.isClientSide);
      } else {
         return InteractionResult.PASS;
      }
   }

   public boolean hasAnalogOutputSignal(BlockState var1) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3) {
      BlockEntity var4 = var2.getBlockEntity(var3);
      return var4 instanceof CommandBlockEntity ? ((CommandBlockEntity)var4).getCommandBlock().getSuccessCount() : 0;
   }

   public void setPlacedBy(Level var1, BlockPos var2, BlockState var3, LivingEntity var4, ItemStack var5) {
      BlockEntity var6 = var1.getBlockEntity(var2);
      if (var6 instanceof CommandBlockEntity) {
         CommandBlockEntity var7 = (CommandBlockEntity)var6;
         BaseCommandBlock var8 = var7.getCommandBlock();
         if (var5.hasCustomHoverName()) {
            var8.setName(var5.getHoverName());
         }

         if (!var1.isClientSide) {
            if (var5.getTagElement("BlockEntityTag") == null) {
               var8.setTrackOutput(var1.getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK));
               var7.setAutomatic(this.automatic);
            }

            if (var7.getMode() == CommandBlockEntity.Mode.SEQUENCE) {
               boolean var9 = var1.hasNeighborSignal(var2);
               var7.setPowered(var9);
            }
         }

      }
   }

   public RenderShape getRenderShape(BlockState var1) {
      return RenderShape.MODEL;
   }

   public BlockState rotate(BlockState var1, Rotation var2) {
      return (BlockState)var1.setValue(FACING, var2.rotate((Direction)var1.getValue(FACING)));
   }

   public BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation((Direction)var1.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, CONDITIONAL);
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return (BlockState)this.defaultBlockState().setValue(FACING, var1.getNearestLookingDirection().getOpposite());
   }

   private static void executeChain(Level var0, BlockPos var1, Direction var2) {
      BlockPos.MutableBlockPos var3 = var1.mutable();
      GameRules var4 = var0.getGameRules();

      int var5;
      BlockState var6;
      for(var5 = var4.getInt(GameRules.RULE_MAX_COMMAND_CHAIN_LENGTH); var5-- > 0; var2 = (Direction)var6.getValue(FACING)) {
         var3.move(var2);
         var6 = var0.getBlockState(var3);
         Block var7 = var6.getBlock();
         if (!var6.is(Blocks.CHAIN_COMMAND_BLOCK)) {
            break;
         }

         BlockEntity var8 = var0.getBlockEntity(var3);
         if (!(var8 instanceof CommandBlockEntity)) {
            break;
         }

         CommandBlockEntity var9 = (CommandBlockEntity)var8;
         if (var9.getMode() != CommandBlockEntity.Mode.SEQUENCE) {
            break;
         }

         if (var9.isPowered() || var9.isAutomatic()) {
            BaseCommandBlock var10 = var9.getCommandBlock();
            if (var9.markConditionMet()) {
               if (!var10.performCommand(var0)) {
                  break;
               }

               var0.updateNeighbourForOutputSignal(var3, var7);
            } else if (var9.isConditional()) {
               var10.setSuccessCount(0);
            }
         }
      }

      if (var5 <= 0) {
         int var11 = Math.max(var4.getInt(GameRules.RULE_MAX_COMMAND_CHAIN_LENGTH), 0);
         LOGGER.warn("Command Block chain tried to execute more than {} steps!", var11);
      }

   }

   static {
      FACING = DirectionalBlock.FACING;
      CONDITIONAL = BlockStateProperties.CONDITIONAL;
   }
}
