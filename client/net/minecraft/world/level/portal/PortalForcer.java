package net.minecraft.world.level.portal;

import java.util.Comparator;
import java.util.Optional;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.levelgen.Heightmap;

public class PortalForcer {
   public static final int TICKET_RADIUS = 3;
   private static final int NETHER_PORTAL_RADIUS = 16;
   private static final int OVERWORLD_PORTAL_RADIUS = 128;
   private static final int FRAME_HEIGHT = 5;
   private static final int FRAME_WIDTH = 4;
   private static final int FRAME_BOX = 3;
   private static final int FRAME_HEIGHT_START = -1;
   private static final int FRAME_HEIGHT_END = 4;
   private static final int FRAME_WIDTH_START = -1;
   private static final int FRAME_WIDTH_END = 3;
   private static final int FRAME_BOX_START = -1;
   private static final int FRAME_BOX_END = 2;
   private static final int NOTHING_FOUND = -1;
   private final ServerLevel level;

   public PortalForcer(ServerLevel var1) {
      super();
      this.level = var1;
   }

   public Optional<BlockPos> findClosestPortalPosition(BlockPos var1, boolean var2, WorldBorder var3) {
      PoiManager var4 = this.level.getPoiManager();
      int var5 = var2 ? 16 : 128;
      var4.ensureLoadedAndValid(this.level, var1, var5);
      return var4.getInSquare(var0 -> var0.is(PoiTypes.NETHER_PORTAL), var1, var5, PoiManager.Occupancy.ANY)
         .map(PoiRecord::getPos)
         .filter(var3::isWithinBounds)
         .filter(var1x -> this.level.getBlockState(var1x).hasProperty(BlockStateProperties.HORIZONTAL_AXIS))
         .min(Comparator.<BlockPos>comparingDouble(var1x -> var1x.distSqr(var1)).thenComparingInt(Vec3i::getY));
   }

   public Optional<BlockUtil.FoundRectangle> createPortal(BlockPos var1, Direction.Axis var2) {
      Direction var3 = Direction.get(Direction.AxisDirection.POSITIVE, var2);
      double var4 = -1.0;
      BlockPos var6 = null;
      double var7 = -1.0;
      BlockPos var9 = null;
      WorldBorder var10 = this.level.getWorldBorder();
      int var11 = Math.min(this.level.getMaxY(), this.level.getMinY() + this.level.getLogicalHeight() - 1);
      boolean var12 = true;
      BlockPos.MutableBlockPos var13 = var1.mutable();

      for (BlockPos.MutableBlockPos var15 : BlockPos.spiralAround(var1, 16, Direction.EAST, Direction.SOUTH)) {
         int var16 = Math.min(var11, this.level.getHeight(Heightmap.Types.MOTION_BLOCKING, var15.getX(), var15.getZ()));
         if (var10.isWithinBounds(var15) && var10.isWithinBounds(var15.move(var3, 1))) {
            var15.move(var3.getOpposite(), 1);

            for (int var17 = var16; var17 >= this.level.getMinY(); var17--) {
               var15.setY(var17);
               if (this.canPortalReplaceBlock(var15)) {
                  int var18 = var17;

                  while (var17 > this.level.getMinY() && this.canPortalReplaceBlock(var15.move(Direction.DOWN))) {
                     var17--;
                  }

                  if (var17 + 4 <= var11) {
                     int var19 = var18 - var17;
                     if (var19 <= 0 || var19 >= 3) {
                        var15.setY(var17);
                        if (this.canHostFrame(var15, var13, var3, 0)) {
                           double var20 = var1.distSqr(var15);
                           if (this.canHostFrame(var15, var13, var3, -1) && this.canHostFrame(var15, var13, var3, 1) && (var4 == -1.0 || var4 > var20)) {
                              var4 = var20;
                              var6 = var15.immutable();
                           }

                           if (var4 == -1.0 && (var7 == -1.0 || var7 > var20)) {
                              var7 = var20;
                              var9 = var15.immutable();
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      if (var4 == -1.0 && var7 != -1.0) {
         var6 = var9;
         var4 = var7;
      }

      if (var4 == -1.0) {
         int var23 = Math.max(this.level.getMinY() - -1, 70);
         int var26 = var11 - 9;
         if (var26 < var23) {
            return Optional.empty();
         }

         var6 = new BlockPos(var1.getX() - var3.getStepX() * 1, Mth.clamp(var1.getY(), var23, var26), var1.getZ() - var3.getStepZ() * 1).immutable();
         var6 = var10.clampToBounds(var6);
         Direction var29 = var3.getClockWise();

         for (int var31 = -1; var31 < 2; var31++) {
            for (int var32 = 0; var32 < 2; var32++) {
               for (int var33 = -1; var33 < 3; var33++) {
                  BlockState var34 = var33 < 0 ? Blocks.OBSIDIAN.defaultBlockState() : Blocks.AIR.defaultBlockState();
                  var13.setWithOffset(var6, var32 * var3.getStepX() + var31 * var29.getStepX(), var33, var32 * var3.getStepZ() + var31 * var29.getStepZ());
                  this.level.setBlockAndUpdate(var13, var34);
               }
            }
         }
      }

      for (int var24 = -1; var24 < 3; var24++) {
         for (int var27 = -1; var27 < 4; var27++) {
            if (var24 == -1 || var24 == 2 || var27 == -1 || var27 == 3) {
               var13.setWithOffset(var6, var24 * var3.getStepX(), var27, var24 * var3.getStepZ());
               this.level.setBlock(var13, Blocks.OBSIDIAN.defaultBlockState(), 3);
            }
         }
      }

      BlockState var25 = Blocks.NETHER_PORTAL.defaultBlockState().setValue(NetherPortalBlock.AXIS, var2);

      for (int var28 = 0; var28 < 2; var28++) {
         for (int var30 = 0; var30 < 3; var30++) {
            var13.setWithOffset(var6, var28 * var3.getStepX(), var30, var28 * var3.getStepZ());
            this.level.setBlock(var13, var25, 18);
         }
      }

      return Optional.of(new BlockUtil.FoundRectangle(var6.immutable(), 2, 3));
   }

   private boolean canPortalReplaceBlock(BlockPos.MutableBlockPos var1) {
      BlockState var2 = this.level.getBlockState(var1);
      return var2.canBeReplaced() && var2.getFluidState().isEmpty();
   }

   private boolean canHostFrame(BlockPos var1, BlockPos.MutableBlockPos var2, Direction var3, int var4) {
      Direction var5 = var3.getClockWise();

      for (int var6 = -1; var6 < 3; var6++) {
         for (int var7 = -1; var7 < 4; var7++) {
            var2.setWithOffset(var1, var3.getStepX() * var6 + var5.getStepX() * var4, var7, var3.getStepZ() * var6 + var5.getStepZ() * var4);
            if (var7 < 0 && !this.level.getBlockState(var2).isSolid()) {
               return false;
            }

            if (var7 >= 0 && !this.canPortalReplaceBlock(var2)) {
               return false;
            }
         }
      }

      return true;
   }
}
