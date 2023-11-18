package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class PressurePlateBlock extends BasePressurePlateBlock {
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   private final PressurePlateBlock.Sensitivity sensitivity;

   protected PressurePlateBlock(PressurePlateBlock.Sensitivity var1, BlockBehaviour.Properties var2, BlockSetType var3) {
      super(var2, var3);
      this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, Boolean.valueOf(false)));
      this.sensitivity = var1;
   }

   @Override
   protected int getSignalForState(BlockState var1) {
      return var1.getValue(POWERED) ? 15 : 0;
   }

   @Override
   protected BlockState setSignalForState(BlockState var1, int var2) {
      return var1.setValue(POWERED, Boolean.valueOf(var2 > 0));
   }

   @Override
   protected int getSignalStrength(Level var1, BlockPos var2) {
      Class<Entity> var3 = switch(this.sensitivity) {
         case EVERYTHING -> Entity.class;
         case MOBS -> LivingEntity.class;
      };
      return getEntityCount(var1, TOUCH_AABB.move(var2), var3) > 0 ? 15 : 0;
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(POWERED);
   }

   public static enum Sensitivity {
      EVERYTHING,
      MOBS;

      private Sensitivity() {
      }
   }
}
