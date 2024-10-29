package net.minecraft.world.level.portal;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
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
      Stream var10000 = var4.getInSquare((var0) -> {
         return var0.is(PoiTypes.NETHER_PORTAL);
      }, var1, var5, PoiManager.Occupancy.ANY).map(PoiRecord::getPos);
      Objects.requireNonNull(var3);
      return var10000.filter(var3::isWithinBounds).filter((var1x) -> {
         return this.level.getBlockState(var1x).hasProperty(BlockStateProperties.HORIZONTAL_AXIS);
      }).min(Comparator.comparingDouble((var1x) -> {
         return var1x.distSqr(var1);
      }).thenComparingInt(Vec3i::getY));
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
      Iterator var14 = BlockPos.spiralAround(var1, 16, Direction.EAST, Direction.SOUTH).iterator();

      while(true) {
         BlockPos.MutableBlockPos var15;
         int var16;
         int var17;
         int var18;
         int var19;
         do {
            do {
               if (!var14.hasNext()) {
                  if (var4 == -1.0 && var7 != -1.0) {
                     var6 = var9;
                     var4 = var7;
                  }

                  int var22;
                  int var23;
                  if (var4 == -1.0) {
                     var22 = Math.max(this.level.getMinY() - -1, 70);
                     var23 = var11 - 9;
                     if (var23 < var22) {
                        return Optional.empty();
                     }

                     var6 = (new BlockPos(var1.getX() - var3.getStepX() * 1, Mth.clamp(var1.getY(), var22, var23), var1.getZ() - var3.getStepZ() * 1)).immutable();
                     var6 = var10.clampToBounds(var6);
                     Direction var25 = var3.getClockWise();

                     for(var17 = -1; var17 < 2; ++var17) {
                        for(var18 = 0; var18 < 2; ++var18) {
                           for(var19 = -1; var19 < 3; ++var19) {
                              BlockState var26 = var19 < 0 ? Blocks.OBSIDIAN.defaultBlockState() : Blocks.AIR.defaultBlockState();
                              var13.setWithOffset(var6, var18 * var3.getStepX() + var17 * var25.getStepX(), var19, var18 * var3.getStepZ() + var17 * var25.getStepZ());
                              this.level.setBlockAndUpdate(var13, var26);
                           }
                        }
                     }
                  }

                  for(var22 = -1; var22 < 3; ++var22) {
                     for(var23 = -1; var23 < 4; ++var23) {
                        if (var22 == -1 || var22 == 2 || var23 == -1 || var23 == 3) {
                           var13.setWithOffset(var6, var22 * var3.getStepX(), var23, var22 * var3.getStepZ());
                           this.level.setBlock(var13, Blocks.OBSIDIAN.defaultBlockState(), 3);
                        }
                     }
                  }

                  BlockState var24 = (BlockState)Blocks.NETHER_PORTAL.defaultBlockState().setValue(NetherPortalBlock.AXIS, var2);

                  for(var23 = 0; var23 < 2; ++var23) {
                     for(var16 = 0; var16 < 3; ++var16) {
                        var13.setWithOffset(var6, var23 * var3.getStepX(), var16, var23 * var3.getStepZ());
                        this.level.setBlock(var13, var24, 18);
                     }
                  }

                  return Optional.of(new BlockUtil.FoundRectangle(var6.immutable(), 2, 3));
               }

               var15 = (BlockPos.MutableBlockPos)var14.next();
               var16 = Math.min(var11, this.level.getHeight(Heightmap.Types.MOTION_BLOCKING, var15.getX(), var15.getZ()));
            } while(!var10.isWithinBounds((BlockPos)var15));
         } while(!var10.isWithinBounds((BlockPos)var15.move(var3, 1)));

         var15.move(var3.getOpposite(), 1);

         for(var17 = var16; var17 >= this.level.getMinY(); --var17) {
            var15.setY(var17);
            if (this.canPortalReplaceBlock(var15)) {
               for(var18 = var17; var17 > this.level.getMinY() && this.canPortalReplaceBlock(var15.move(Direction.DOWN)); --var17) {
               }

               if (var17 + 4 <= var11) {
                  var19 = var18 - var17;
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

   private boolean canPortalReplaceBlock(BlockPos.MutableBlockPos var1) {
      BlockState var2 = this.level.getBlockState(var1);
      return var2.canBeReplaced() && var2.getFluidState().isEmpty();
   }

   private boolean canHostFrame(BlockPos var1, BlockPos.MutableBlockPos var2, Direction var3, int var4) {
      Direction var5 = var3.getClockWise();

      for(int var6 = -1; var6 < 3; ++var6) {
         for(int var7 = -1; var7 < 4; ++var7) {
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
