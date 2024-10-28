package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;

public abstract class AbstractSkullBlock extends BaseEntityBlock implements Equipable {
   public static final BooleanProperty POWERED;
   private final SkullBlock.Type type;

   public AbstractSkullBlock(SkullBlock.Type var1, BlockBehaviour.Properties var2) {
      super(var2);
      this.type = var1;
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(POWERED, false));
   }

   protected abstract MapCodec<? extends AbstractSkullBlock> codec();

   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new SkullBlockEntity(var1, var2);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level var1, BlockState var2, BlockEntityType<T> var3) {
      if (var1.isClientSide) {
         boolean var4 = var2.is(Blocks.DRAGON_HEAD) || var2.is(Blocks.DRAGON_WALL_HEAD) || var2.is(Blocks.PIGLIN_HEAD) || var2.is(Blocks.PIGLIN_WALL_HEAD);
         if (var4) {
            return createTickerHelper(var3, BlockEntityType.SKULL, SkullBlockEntity::animation);
         }
      }

      return null;
   }

   public SkullBlock.Type getType() {
      return this.type;
   }

   protected boolean isPathfindable(BlockState var1, PathComputationType var2) {
      return false;
   }

   public EquipmentSlot getEquipmentSlot() {
      return EquipmentSlot.HEAD;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(POWERED);
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return (BlockState)this.defaultBlockState().setValue(POWERED, var1.getLevel().hasNeighborSignal(var1.getClickedPos()));
   }

   protected void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, BlockPos var5, boolean var6) {
      if (!var2.isClientSide) {
         boolean var7 = var2.hasNeighborSignal(var3);
         if (var7 != (Boolean)var1.getValue(POWERED)) {
            var2.setBlock(var3, (BlockState)var1.setValue(POWERED, var7), 2);
         }

      }
   }

   static {
      POWERED = BlockStateProperties.POWERED;
   }
}
