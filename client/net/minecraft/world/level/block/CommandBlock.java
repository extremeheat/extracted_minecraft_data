package net.minecraft.world.level.block;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringUtil;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import org.slf4j.Logger;

public class CommandBlock extends BaseEntityBlock implements GameMasterBlock {
   public static final MapCodec<CommandBlock> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Codec.BOOL.fieldOf("automatic").forGetter((var0x) -> var0x.automatic), propertiesCodec()).apply(var0, CommandBlock::new));
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final EnumProperty<Direction> FACING;
   public static final BooleanProperty CONDITIONAL;
   private final boolean automatic;

   public MapCodec<CommandBlock> codec() {
      return CODEC;
   }

   public CommandBlock(boolean var1, BlockBehaviour.Properties var2) {
      super(var2);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(CONDITIONAL, false));
      this.automatic = var1;
   }

   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      CommandBlockEntity var3 = new CommandBlockEntity(var1, var2);
      var3.setAutomatic(this.automatic);
      return var3;
   }

   protected void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, @Nullable Orientation var5, boolean var6) {
      if (!var2.isClientSide) {
         BlockEntity var7 = var2.getBlockEntity(var3);
         if (var7 instanceof CommandBlockEntity) {
            CommandBlockEntity var8 = (CommandBlockEntity)var7;
            this.setPoweredAndUpdate(var2, var3, var8, var2.hasNeighborSignal(var3));
         }

      }
   }

   private void setPoweredAndUpdate(Level var1, BlockPos var2, CommandBlockEntity var3, boolean var4) {
      boolean var5 = var3.isPowered();
      if (var4 != var5) {
         var3.setPowered(var4);
         if (var4) {
            if (var3.isAutomatic() || var3.getMode() == CommandBlockEntity.Mode.SEQUENCE) {
               return;
            }

            var3.markConditionMet();
            var1.scheduleTick(var2, this, 1);
         }

      }
   }

   protected void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      BlockEntity var5 = var2.getBlockEntity(var3);
      if (var5 instanceof CommandBlockEntity var6) {
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
               var2.scheduleTick(var3, this, 1);
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

   private void execute(BlockState var1, ServerLevel var2, BlockPos var3, BaseCommandBlock var4, boolean var5) {
      if (var5) {
         var4.performCommand(var2);
      } else {
         var4.setSuccessCount(0);
      }

      executeChain(var2, var3, (Direction)var1.getValue(FACING));
   }

   protected InteractionResult useWithoutItem(BlockState var1, Level var2, BlockPos var3, Player var4, BlockHitResult var5) {
      BlockEntity var6 = var2.getBlockEntity(var3);
      if (var6 instanceof CommandBlockEntity && var4.canUseGameMasterBlocks()) {
         var4.openCommandBlock((CommandBlockEntity)var6);
         return InteractionResult.SUCCESS;
      } else {
         return InteractionResult.PASS;
      }
   }

   protected boolean hasAnalogOutputSignal(BlockState var1) {
      return true;
   }

   protected int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3) {
      BlockEntity var4 = var2.getBlockEntity(var3);
      return var4 instanceof CommandBlockEntity ? ((CommandBlockEntity)var4).getCommandBlock().getSuccessCount() : 0;
   }

   public void setPlacedBy(Level var1, BlockPos var2, BlockState var3, LivingEntity var4, ItemStack var5) {
      BlockEntity var6 = var1.getBlockEntity(var2);
      if (var6 instanceof CommandBlockEntity var7) {
         BaseCommandBlock var8 = var7.getCommandBlock();
         if (var1 instanceof ServerLevel var9) {
            if (!var5.has(DataComponents.BLOCK_ENTITY_DATA)) {
               var8.setTrackOutput(var9.getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK));
               var7.setAutomatic(this.automatic);
            }

            boolean var10 = var1.hasNeighborSignal(var2);
            this.setPoweredAndUpdate(var1, var2, var7, var10);
         }

      }
   }

   protected BlockState rotate(BlockState var1, Rotation var2) {
      return (BlockState)var1.setValue(FACING, var2.rotate((Direction)var1.getValue(FACING)));
   }

   protected BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation((Direction)var1.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, CONDITIONAL);
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return (BlockState)this.defaultBlockState().setValue(FACING, var1.getNearestLookingDirection().getOpposite());
   }

   private static void executeChain(ServerLevel var0, BlockPos var1, Direction var2) {
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
