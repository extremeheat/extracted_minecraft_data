package net.minecraft.world.entity.ai.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;

public class CatSitOnBlockGoal extends MoveToBlockGoal {
   private final Cat cat;

   public CatSitOnBlockGoal(Cat var1, double var2) {
      super(var1, var2, 8);
      this.cat = var1;
   }

   public boolean canUse() {
      return this.cat.isTame() && !this.cat.isOrderedToSit() && super.canUse();
   }

   public void start() {
      super.start();
      this.cat.setInSittingPose(false);
   }

   public void stop() {
      super.stop();
      this.cat.setInSittingPose(false);
   }

   public void tick() {
      super.tick();
      this.cat.setInSittingPose(this.isReachedTarget());
   }

   protected boolean isValidTarget(LevelReader var1, BlockPos var2) {
      if (!var1.isEmptyBlock(var2.above())) {
         return false;
      } else {
         BlockState var3 = var1.getBlockState(var2);
         if (var3.is(Blocks.CHEST)) {
            return ChestBlockEntity.getOpenCount(var1, var2) < 1;
         } else {
            return var3.is(Blocks.FURNACE) && (Boolean)var3.getValue(FurnaceBlock.LIT) ? true : var3.is(BlockTags.BEDS, (var0) -> {
               return (Boolean)var0.getOptionalValue(BedBlock.PART).map((var0x) -> {
                  return var0x != BedPart.HEAD;
               }).orElse(true);
            });
         }
      }
   }
}
