package net.minecraft.world.item;

import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;

public class StandingAndWallBlockItem extends BlockItem {
   protected final Block wallBlock;
   private final Direction attachmentDirection;

   public StandingAndWallBlockItem(Block var1, Block var2, Item.Properties var3, Direction var4) {
      super(var1, var3);
      this.wallBlock = var2;
      this.attachmentDirection = var4;
   }

   protected boolean canPlace(LevelReader var1, BlockState var2, BlockPos var3) {
      return var2.canSurvive(var1, var3);
   }

   @Nullable
   protected BlockState getPlacementState(BlockPlaceContext var1) {
      BlockState var2 = this.wallBlock.getStateForPlacement(var1);
      BlockState var3 = null;
      Level var4 = var1.getLevel();
      BlockPos var5 = var1.getClickedPos();
      Direction[] var6 = var1.getNearestLookingDirections();
      int var7 = var6.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         Direction var9 = var6[var8];
         if (var9 != this.attachmentDirection.getOpposite()) {
            BlockState var10 = var9 == this.attachmentDirection ? this.getBlock().getStateForPlacement(var1) : var2;
            if (var10 != null && this.canPlace(var4, var10, var5)) {
               var3 = var10;
               break;
            }
         }
      }

      return var3 != null && var4.isUnobstructed(var3, var5, CollisionContext.empty()) ? var3 : null;
   }

   public void registerBlocks(Map<Block, Item> var1, Item var2) {
      super.registerBlocks(var1, var2);
      var1.put(this.wallBlock, var2);
   }
}
