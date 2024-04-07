package net.minecraft.world.level.portal;

import java.util.Comparator;
import java.util.Optional;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.levelgen.Heightmap;

public class PortalForcer {
   public static final int TICKET_RADIUS = 3;
   private static final int SEARCH_RADIUS = 128;
   private static final int CREATE_RADIUS = 16;
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

   public Optional<BlockUtil.FoundRectangle> findPortalAround(BlockPos var1, boolean var2, WorldBorder var3) {
      PoiManager var4 = this.level.getPoiManager();
      int var5 = var2 ? 16 : 128;
      var4.ensureLoadedAndValid(this.level, var1, var5);
      Optional var6 = var4.getInSquare(var0 -> var0.is(PoiTypes.NETHER_PORTAL), var1, var5, PoiManager.Occupancy.ANY)
         .filter(var1x -> var3.isWithinBounds(var1x.getPos()))
         .sorted(Comparator.<PoiRecord>comparingDouble(var1x -> var1x.getPos().distSqr(var1)).thenComparingInt(var0 -> var0.getPos().getY()))
         .filter(var1x -> this.level.getBlockState(var1x.getPos()).hasProperty(BlockStateProperties.HORIZONTAL_AXIS))
         .findFirst();
      return var6.map(
         var1x -> {
            BlockPos var2x = var1x.getPos();
            this.level.getChunkSource().addRegionTicket(TicketType.PORTAL, new ChunkPos(var2x), 3, var2x);
            BlockState var3x = this.level.getBlockState(var2x);
            return BlockUtil.getLargestRectangleAround(
               var2x, var3x.getValue(BlockStateProperties.HORIZONTAL_AXIS), 21, Direction.Axis.Y, 21, var2xx -> this.level.getBlockState(var2xx) == var3x
            );
         }
      );
   }

   public Optional<BlockUtil.FoundRectangle> createPortal(BlockPos var1, Direction.Axis var2) {
      Direction var3 = Direction.get(Direction.AxisDirection.POSITIVE, var2);
      double var4 = -1.0;
      BlockPos var6 = null;
      double var7 = -1.0;
      BlockPos var9 = null;
      WorldBorder var10 = this.level.getWorldBorder();
      int var11 = Math.min(this.level.getMaxBuildHeight(), this.level.getMinBuildHeight() + this.level.getLogicalHeight()) - 1;
      BlockPos.MutableBlockPos var12 = var1.mutable();

      for (BlockPos.MutableBlockPos var14 : BlockPos.spiralAround(var1, 16, Direction.EAST, Direction.SOUTH)) {
         int var15 = Math.min(var11, this.level.getHeight(Heightmap.Types.MOTION_BLOCKING, var14.getX(), var14.getZ()));
         boolean var16 = true;
         if (var10.isWithinBounds(var14) && var10.isWithinBounds(var14.move(var3, 1))) {
            var14.move(var3.getOpposite(), 1);

            for (int var17 = var15; var17 >= this.level.getMinBuildHeight(); var17--) {
               var14.setY(var17);
               if (this.canPortalReplaceBlock(var14)) {
                  int var18 = var17;

                  while (var17 > this.level.getMinBuildHeight() && this.canPortalReplaceBlock(var14.move(Direction.DOWN))) {
                     var17--;
                  }

                  if (var17 + 4 <= var11) {
                     int var19 = var18 - var17;
                     if (var19 <= 0 || var19 >= 3) {
                        var14.setY(var17);
                        if (this.canHostFrame(var14, var12, var3, 0)) {
                           double var20 = var1.distSqr(var14);
                           if (this.canHostFrame(var14, var12, var3, -1) && this.canHostFrame(var14, var12, var3, 1) && (var4 == -1.0 || var4 > var20)) {
                              var4 = var20;
                              var6 = var14.immutable();
                           }

                           if (var4 == -1.0 && (var7 == -1.0 || var7 > var20)) {
                              var7 = var20;
                              var9 = var14.immutable();
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
         int var22 = Math.max(this.level.getMinBuildHeight() - -1, 70);
         int var25 = var11 - 9;
         if (var25 < var22) {
            return Optional.empty();
         }

         var6 = new BlockPos(var1.getX(), Mth.clamp(var1.getY(), var22, var25), var1.getZ()).immutable();
         Direction var28 = var3.getClockWise();
         if (!var10.isWithinBounds(var6)) {
            return Optional.empty();
         }

         for (int var30 = -1; var30 < 2; var30++) {
            for (int var31 = 0; var31 < 2; var31++) {
               for (int var32 = -1; var32 < 3; var32++) {
                  BlockState var33 = var32 < 0 ? Blocks.OBSIDIAN.defaultBlockState() : Blocks.AIR.defaultBlockState();
                  var12.setWithOffset(var6, var31 * var3.getStepX() + var30 * var28.getStepX(), var32, var31 * var3.getStepZ() + var30 * var28.getStepZ());
                  this.level.setBlockAndUpdate(var12, var33);
               }
            }
         }
      }

      for (int var23 = -1; var23 < 3; var23++) {
         for (int var26 = -1; var26 < 4; var26++) {
            if (var23 == -1 || var23 == 2 || var26 == -1 || var26 == 3) {
               var12.setWithOffset(var6, var23 * var3.getStepX(), var26, var23 * var3.getStepZ());
               this.level.setBlock(var12, Blocks.OBSIDIAN.defaultBlockState(), 3);
            }
         }
      }

      BlockState var24 = Blocks.NETHER_PORTAL.defaultBlockState().setValue(NetherPortalBlock.AXIS, var2);

      for (int var27 = 0; var27 < 2; var27++) {
         for (int var29 = 0; var29 < 3; var29++) {
            var12.setWithOffset(var6, var27 * var3.getStepX(), var29, var27 * var3.getStepZ());
            this.level.setBlock(var12, var24, 18);
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
