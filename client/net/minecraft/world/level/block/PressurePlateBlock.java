package net.minecraft.world.level.block;

import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;

public class PressurePlateBlock extends BasePressurePlateBlock {
   public static final BooleanProperty POWERED;
   private final PressurePlateBlock.Sensitivity sensitivity;

   protected PressurePlateBlock(PressurePlateBlock.Sensitivity var1, Block.Properties var2) {
      super(var2);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(POWERED, false));
      this.sensitivity = var1;
   }

   protected int getSignalForState(BlockState var1) {
      return (Boolean)var1.getValue(POWERED) ? 15 : 0;
   }

   protected BlockState setSignalForState(BlockState var1, int var2) {
      return (BlockState)var1.setValue(POWERED, var2 > 0);
   }

   protected void playOnSound(LevelAccessor var1, BlockPos var2) {
      if (this.material == Material.WOOD) {
         var1.playSound((Player)null, var2, SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_ON, SoundSource.BLOCKS, 0.3F, 0.8F);
      } else {
         var1.playSound((Player)null, var2, SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON, SoundSource.BLOCKS, 0.3F, 0.6F);
      }

   }

   protected void playOffSound(LevelAccessor var1, BlockPos var2) {
      if (this.material == Material.WOOD) {
         var1.playSound((Player)null, var2, SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_OFF, SoundSource.BLOCKS, 0.3F, 0.7F);
      } else {
         var1.playSound((Player)null, var2, SoundEvents.STONE_PRESSURE_PLATE_CLICK_OFF, SoundSource.BLOCKS, 0.3F, 0.5F);
      }

   }

   protected int getSignalStrength(Level var1, BlockPos var2) {
      AABB var3 = TOUCH_AABB.move(var2);
      List var4;
      switch(this.sensitivity) {
      case EVERYTHING:
         var4 = var1.getEntities((Entity)null, var3);
         break;
      case MOBS:
         var4 = var1.getEntitiesOfClass(LivingEntity.class, var3);
         break;
      default:
         return 0;
      }

      if (!var4.isEmpty()) {
         Iterator var5 = var4.iterator();

         while(var5.hasNext()) {
            Entity var6 = (Entity)var5.next();
            if (!var6.isIgnoringBlockTriggers()) {
               return 15;
            }
         }
      }

      return 0;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(POWERED);
   }

   static {
      POWERED = BlockStateProperties.POWERED;
   }

   public static enum Sensitivity {
      EVERYTHING,
      MOBS;

      private Sensitivity() {
      }
   }
}
