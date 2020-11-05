package net.minecraft.world.level.portal;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.levelgen.Heightmap;

public class PortalForcer {
   private final ServerLevel level;

   public PortalForcer(ServerLevel var1) {
      super();
      this.level = var1;
   }

   public Optional<BlockUtil.FoundRectangle> findPortalAround(BlockPos var1, boolean var2) {
      PoiManager var3 = this.level.getPoiManager();
      int var4 = var2 ? 16 : 128;
      var3.ensureLoadedAndValid(this.level, var1, var4);
      Optional var5 = var3.getInSquare((var0) -> {
         return var0 == PoiType.NETHER_PORTAL;
      }, var1, var4, PoiManager.Occupancy.ANY).sorted(Comparator.comparingDouble((var1x) -> {
         return var1x.getPos().distSqr(var1);
      }).thenComparingInt((var0) -> {
         return var0.getPos().getY();
      })).filter((var1x) -> {
         return this.level.getBlockState(var1x.getPos()).hasProperty(BlockStateProperties.HORIZONTAL_AXIS);
      }).findFirst();
      return var5.map((var1x) -> {
         BlockPos var2 = var1x.getPos();
         this.level.getChunkSource().addRegionTicket(TicketType.PORTAL, new ChunkPos(var2), 3, var2);
         BlockState var3 = this.level.getBlockState(var2);
         return BlockUtil.getLargestRectangleAround(var2, (Direction.Axis)var3.getValue(BlockStateProperties.HORIZONTAL_AXIS), 21, Direction.Axis.Y, 21, (var2x) -> {
            return this.level.getBlockState(var2x) == var3;
         });
      });
   }

   public Optional<BlockUtil.FoundRectangle> createPortal(BlockPos var1, Direction.Axis var2) {
      Direction var3 = Direction.get(Direction.AxisDirection.POSITIVE, var2);
      double var4 = -1.0D;
      BlockPos var6 = null;
      double var7 = -1.0D;
      BlockPos var9 = null;
      WorldBorder var10 = this.level.getWorldBorder();
      int var11 = this.level.getHeight() - 1;
      BlockPos.MutableBlockPos var12 = var1.mutable();
      Iterator var13 = BlockPos.spiralAround(var1, 16, Direction.EAST, Direction.SOUTH).iterator();

      while(true) {
         BlockPos.MutableBlockPos var14;
         int var15;
         do {
            do {
               if (!var13.hasNext()) {
                  if (var4 == -1.0D && var7 != -1.0D) {
                     var6 = var9;
                     var4 = var7;
                  }

                  int var24;
                  if (var4 == -1.0D) {
                     var6 = (new BlockPos(var1.getX(), Mth.clamp(var1.getY(), 70, this.level.getHeight() - 10), var1.getZ())).immutable();
                     Direction var22 = var3.getClockWise();
                     if (!var10.isWithinBounds(var6)) {
                        return Optional.empty();
                     }

                     for(var24 = -1; var24 < 2; ++var24) {
                        for(var15 = 0; var15 < 2; ++var15) {
                           for(int var26 = -1; var26 < 3; ++var26) {
                              BlockState var27 = var26 < 0 ? Blocks.OBSIDIAN.defaultBlockState() : Blocks.AIR.defaultBlockState();
                              var12.setWithOffset(var6, var15 * var3.getStepX() + var24 * var22.getStepX(), var26, var15 * var3.getStepZ() + var24 * var22.getStepZ());
                              this.level.setBlockAndUpdate(var12, var27);
                           }
                        }
                     }
                  }

                  for(int var23 = -1; var23 < 3; ++var23) {
                     for(var24 = -1; var24 < 4; ++var24) {
                        if (var23 == -1 || var23 == 2 || var24 == -1 || var24 == 3) {
                           var12.setWithOffset(var6, var23 * var3.getStepX(), var24, var23 * var3.getStepZ());
                           this.level.setBlock(var12, Blocks.OBSIDIAN.defaultBlockState(), 3);
                        }
                     }
                  }

                  BlockState var25 = (BlockState)Blocks.NETHER_PORTAL.defaultBlockState().setValue(NetherPortalBlock.AXIS, var2);

                  for(var24 = 0; var24 < 2; ++var24) {
                     for(var15 = 0; var15 < 3; ++var15) {
                        var12.setWithOffset(var6, var24 * var3.getStepX(), var15, var24 * var3.getStepZ());
                        this.level.setBlock(var12, var25, 18);
                     }
                  }

                  return Optional.of(new BlockUtil.FoundRectangle(var6.immutable(), 2, 3));
               }

               var14 = (BlockPos.MutableBlockPos)var13.next();
               var15 = Math.min(var11, this.level.getHeight(Heightmap.Types.MOTION_BLOCKING, var14.getX(), var14.getZ()));
               boolean var16 = true;
            } while(!var10.isWithinBounds((BlockPos)var14));
         } while(!var10.isWithinBounds((BlockPos)var14.move(var3, 1)));

         var14.move(var3.getOpposite(), 1);

         for(int var17 = var15; var17 >= 0; --var17) {
            var14.setY(var17);
            if (this.level.isEmptyBlock(var14)) {
               int var18;
               for(var18 = var17; var17 > 0 && this.level.isEmptyBlock(var14.move(Direction.DOWN)); --var17) {
               }

               if (var17 + 4 <= var11) {
                  int var19 = var18 - var17;
                  if (var19 <= 0 || var19 >= 3) {
                     var14.setY(var17);
                     if (this.canHostFrame(var14, var12, var3, 0)) {
                        double var20 = var1.distSqr(var14);
                        if (this.canHostFrame(var14, var12, var3, -1) && this.canHostFrame(var14, var12, var3, 1) && (var4 == -1.0D || var4 > var20)) {
                           var4 = var20;
                           var6 = var14.immutable();
                        }

                        if (var4 == -1.0D && (var7 == -1.0D || var7 > var20)) {
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

   private boolean canHostFrame(BlockPos var1, BlockPos.MutableBlockPos var2, Direction var3, int var4) {
      Direction var5 = var3.getClockWise();

      for(int var6 = -1; var6 < 3; ++var6) {
         for(int var7 = -1; var7 < 4; ++var7) {
            var2.setWithOffset(var1, var3.getStepX() * var6 + var5.getStepX() * var4, var7, var3.getStepZ() * var6 + var5.getStepZ() * var4);
            if (var7 < 0 && !this.level.getBlockState(var2).getMaterial().isSolid()) {
               return false;
            }

            if (var7 >= 0 && !this.level.isEmptyBlock(var2)) {
               return false;
            }
         }
      }

      return true;
   }
}
