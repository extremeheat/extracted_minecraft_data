package net.minecraft.world.entity.ai.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
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
      return this.cat.isTame() && !this.cat.isSitting() && super.canUse();
   }

   public void start() {
      super.start();
      this.cat.getSitGoal().wantToSit(false);
   }

   public void stop() {
      super.stop();
      this.cat.setSitting(false);
   }

   public void tick() {
      super.tick();
      this.cat.getSitGoal().wantToSit(false);
      if (!this.isReachedTarget()) {
         this.cat.setSitting(false);
      } else if (!this.cat.isSitting()) {
         this.cat.setSitting(true);
      }

   }

   protected boolean isValidTarget(LevelReader var1, BlockPos var2) {
      if (!var1.isEmptyBlock(var2.above())) {
         return false;
      } else {
         BlockState var3 = var1.getBlockState(var2);
         Block var4 = var3.getBlock();
         if (var4 == Blocks.CHEST) {
            return ChestBlockEntity.getOpenCount(var1, var2) < 1;
         } else if (var4 == Blocks.FURNACE && (Boolean)var3.getValue(FurnaceBlock.LIT)) {
            return true;
         } else {
            return var4.is(BlockTags.BEDS) && var3.getValue(BedBlock.PART) != BedPart.HEAD;
         }
      }
   }
}
