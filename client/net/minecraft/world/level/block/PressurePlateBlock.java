package net.minecraft.world.level.block;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;

public class PressurePlateBlock extends BasePressurePlateBlock {
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   private final PressurePlateBlock.Sensitivity sensitivity;
   private final SoundEvent soundOff;
   private final SoundEvent soundOn;

   protected PressurePlateBlock(PressurePlateBlock.Sensitivity var1, BlockBehaviour.Properties var2, SoundEvent var3, SoundEvent var4) {
      super(var2);
      this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, Boolean.valueOf(false)));
      this.sensitivity = var1;
      this.soundOff = var3;
      this.soundOn = var4;
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
   protected void playOnSound(LevelAccessor var1, BlockPos var2) {
      var1.playSound(null, var2, this.soundOn, SoundSource.BLOCKS);
   }

   @Override
   protected void playOffSound(LevelAccessor var1, BlockPos var2) {
      var1.playSound(null, var2, this.soundOff, SoundSource.BLOCKS);
   }

   @Override
   protected int getSignalStrength(Level var1, BlockPos var2) {
      AABB var3 = TOUCH_AABB.move(var2);
      List var4;
      switch(this.sensitivity) {
         case EVERYTHING:
            var4 = var1.getEntities(null, var3);
            break;
         case MOBS:
            var4 = var1.getEntitiesOfClass(LivingEntity.class, var3);
            break;
         default:
            return 0;
      }

      if (!var4.isEmpty()) {
         for(Entity var6 : var4) {
            if (!var6.isIgnoringBlockTriggers()) {
               return 15;
            }
         }
      }

      return 0;
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
