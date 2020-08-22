package net.minecraft.world.level;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.phys.Vec3;

public class PortalForcer {
   private final ServerLevel level;
   private final Random random;

   public PortalForcer(ServerLevel var1) {
      this.level = var1;
      this.random = new Random(var1.getSeed());
   }

   public boolean findAndMoveToPortal(Entity var1, float var2) {
      Vec3 var3 = var1.getPortalEntranceOffset();
      Direction var4 = var1.getPortalEntranceForwards();
      BlockPattern.PortalInfo var5 = this.findPortal(new BlockPos(var1), var1.getDeltaMovement(), var4, var3.x, var3.y, var1 instanceof Player);
      if (var5 == null) {
         return false;
      } else {
         Vec3 var6 = var5.pos;
         Vec3 var7 = var5.speed;
         var1.setDeltaMovement(var7);
         var1.yRot = var2 + (float)var5.angle;
         var1.forceMove(var6.x, var6.y, var6.z);
         return true;
      }
   }

   @Nullable
   public BlockPattern.PortalInfo findPortal(BlockPos var1, Vec3 var2, Direction var3, double var4, double var6, boolean var8) {
      PoiManager var9 = this.level.getPoiManager();
      var9.ensureLoadedAndValid(this.level, var1, 128);
      List var10 = (List)var9.getInSquare((var0) -> {
         return var0 == PoiType.NETHER_PORTAL;
      }, var1, 128, PoiManager.Occupancy.ANY).collect(Collectors.toList());
      Optional var11 = var10.stream().min(Comparator.comparingDouble((var1x) -> {
         return var1x.getPos().distSqr(var1);
      }).thenComparingInt((var0) -> {
         return var0.getPos().getY();
      }));
      return (BlockPattern.PortalInfo)var11.map((var7) -> {
         BlockPos var8 = var7.getPos();
         this.level.getChunkSource().addRegionTicket(TicketType.PORTAL, new ChunkPos(var8), 3, var8);
         BlockPattern.BlockPatternMatch var9 = NetherPortalBlock.getPortalShape(this.level, var8);
         return var9.getPortalOutput(var3, var8, var6, var2, var4);
      }).orElse((Object)null);
   }

   public boolean createPortal(Entity var1) {
      boolean var2 = true;
      double var3 = -1.0D;
      int var5 = Mth.floor(var1.getX());
      int var6 = Mth.floor(var1.getY());
      int var7 = Mth.floor(var1.getZ());
      int var8 = var5;
      int var9 = var6;
      int var10 = var7;
      int var11 = 0;
      int var12 = this.random.nextInt(4);
      BlockPos.MutableBlockPos var13 = new BlockPos.MutableBlockPos();

      int var14;
      double var15;
      int var17;
      double var18;
      int var20;
      int var21;
      int var22;
      int var23;
      int var24;
      int var25;
      int var26;
      int var27;
      int var28;
      double var33;
      double var34;
      for(var14 = var5 - 16; var14 <= var5 + 16; ++var14) {
         var15 = (double)var14 + 0.5D - var1.getX();

         for(var17 = var7 - 16; var17 <= var7 + 16; ++var17) {
            var18 = (double)var17 + 0.5D - var1.getZ();

            label276:
            for(var20 = this.level.getHeight() - 1; var20 >= 0; --var20) {
               if (this.level.isEmptyBlock(var13.set(var14, var20, var17))) {
                  while(var20 > 0 && this.level.isEmptyBlock(var13.set(var14, var20 - 1, var17))) {
                     --var20;
                  }

                  for(var21 = var12; var21 < var12 + 4; ++var21) {
                     var22 = var21 % 2;
                     var23 = 1 - var22;
                     if (var21 % 4 >= 2) {
                        var22 = -var22;
                        var23 = -var23;
                     }

                     for(var24 = 0; var24 < 3; ++var24) {
                        for(var25 = 0; var25 < 4; ++var25) {
                           for(var26 = -1; var26 < 4; ++var26) {
                              var27 = var14 + (var25 - 1) * var22 + var24 * var23;
                              var28 = var20 + var26;
                              int var29 = var17 + (var25 - 1) * var23 - var24 * var22;
                              var13.set(var27, var28, var29);
                              if (var26 < 0 && !this.level.getBlockState(var13).getMaterial().isSolid() || var26 >= 0 && !this.level.isEmptyBlock(var13)) {
                                 continue label276;
                              }
                           }
                        }
                     }

                     var33 = (double)var20 + 0.5D - var1.getY();
                     var34 = var15 * var15 + var33 * var33 + var18 * var18;
                     if (var3 < 0.0D || var34 < var3) {
                        var3 = var34;
                        var8 = var14;
                        var9 = var20;
                        var10 = var17;
                        var11 = var21 % 4;
                     }
                  }
               }
            }
         }
      }

      if (var3 < 0.0D) {
         for(var14 = var5 - 16; var14 <= var5 + 16; ++var14) {
            var15 = (double)var14 + 0.5D - var1.getX();

            for(var17 = var7 - 16; var17 <= var7 + 16; ++var17) {
               var18 = (double)var17 + 0.5D - var1.getZ();

               label214:
               for(var20 = this.level.getHeight() - 1; var20 >= 0; --var20) {
                  if (this.level.isEmptyBlock(var13.set(var14, var20, var17))) {
                     while(var20 > 0 && this.level.isEmptyBlock(var13.set(var14, var20 - 1, var17))) {
                        --var20;
                     }

                     for(var21 = var12; var21 < var12 + 2; ++var21) {
                        var22 = var21 % 2;
                        var23 = 1 - var22;

                        for(var24 = 0; var24 < 4; ++var24) {
                           for(var25 = -1; var25 < 4; ++var25) {
                              var26 = var14 + (var24 - 1) * var22;
                              var27 = var20 + var25;
                              var28 = var17 + (var24 - 1) * var23;
                              var13.set(var26, var27, var28);
                              if (var25 < 0 && !this.level.getBlockState(var13).getMaterial().isSolid() || var25 >= 0 && !this.level.isEmptyBlock(var13)) {
                                 continue label214;
                              }
                           }
                        }

                        var33 = (double)var20 + 0.5D - var1.getY();
                        var34 = var15 * var15 + var33 * var33 + var18 * var18;
                        if (var3 < 0.0D || var34 < var3) {
                           var3 = var34;
                           var8 = var14;
                           var9 = var20;
                           var10 = var17;
                           var11 = var21 % 2;
                        }
                     }
                  }
               }
            }
         }
      }

      int var30 = var8;
      int var16 = var9;
      var17 = var10;
      int var31 = var11 % 2;
      int var19 = 1 - var31;
      if (var11 % 4 >= 2) {
         var31 = -var31;
         var19 = -var19;
      }

      if (var3 < 0.0D) {
         var9 = Mth.clamp(var9, 70, this.level.getHeight() - 10);
         var16 = var9;

         for(var20 = -1; var20 <= 1; ++var20) {
            for(var21 = 1; var21 < 3; ++var21) {
               for(var22 = -1; var22 < 3; ++var22) {
                  var23 = var30 + (var21 - 1) * var31 + var20 * var19;
                  var24 = var16 + var22;
                  var25 = var17 + (var21 - 1) * var19 - var20 * var31;
                  boolean var35 = var22 < 0;
                  var13.set(var23, var24, var25);
                  this.level.setBlockAndUpdate(var13, var35 ? Blocks.OBSIDIAN.defaultBlockState() : Blocks.AIR.defaultBlockState());
               }
            }
         }
      }

      for(var20 = -1; var20 < 3; ++var20) {
         for(var21 = -1; var21 < 4; ++var21) {
            if (var20 == -1 || var20 == 2 || var21 == -1 || var21 == 3) {
               var13.set(var30 + var20 * var31, var16 + var21, var17 + var20 * var19);
               this.level.setBlock(var13, Blocks.OBSIDIAN.defaultBlockState(), 3);
            }
         }
      }

      BlockState var32 = (BlockState)Blocks.NETHER_PORTAL.defaultBlockState().setValue(NetherPortalBlock.AXIS, var31 == 0 ? Direction.Axis.Z : Direction.Axis.X);

      for(var21 = 0; var21 < 2; ++var21) {
         for(var22 = 0; var22 < 3; ++var22) {
            var13.set(var30 + var21 * var31, var16 + var22, var17 + var21 * var19);
            this.level.setBlock(var13, var32, 18);
         }
      }

      return true;
   }
}
