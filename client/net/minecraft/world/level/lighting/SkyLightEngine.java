package net.minecraft.world.level.lighting;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.mutable.MutableInt;

public final class SkyLightEngine extends LayerLightEngine<SkyLightSectionStorage.SkyDataLayerStorageMap, SkyLightSectionStorage> {
   private static final Direction[] DIRECTIONS = Direction.values();
   private static final Direction[] HORIZONTALS;

   public SkyLightEngine(LightChunkGetter var1) {
      super(var1, LightLayer.SKY, new SkyLightSectionStorage(var1));
   }

   protected int computeLevelFromNeighbor(long var1, long var3, int var5) {
      if (var3 != 9223372036854775807L && var1 != 9223372036854775807L) {
         if (var5 >= 15) {
            return var5;
         } else {
            MutableInt var6 = new MutableInt();
            BlockState var7 = this.getStateAndOpacity(var3, var6);
            if (var6.getValue() >= 15) {
               return 15;
            } else {
               int var8 = BlockPos.getX(var1);
               int var9 = BlockPos.getY(var1);
               int var10 = BlockPos.getZ(var1);
               int var11 = BlockPos.getX(var3);
               int var12 = BlockPos.getY(var3);
               int var13 = BlockPos.getZ(var3);
               int var14 = Integer.signum(var11 - var8);
               int var15 = Integer.signum(var12 - var9);
               int var16 = Integer.signum(var13 - var10);
               Direction var17 = Direction.fromNormal(var14, var15, var16);
               if (var17 == null) {
                  throw new IllegalStateException(String.format("Light was spread in illegal direction %d, %d, %d", var14, var15, var16));
               } else {
                  BlockState var18 = this.getStateAndOpacity(var1, (MutableInt)null);
                  VoxelShape var19 = this.getShape(var18, var1, var17);
                  VoxelShape var20 = this.getShape(var7, var3, var17.getOpposite());
                  if (Shapes.faceShapeOccludes(var19, var20)) {
                     return 15;
                  } else {
                     boolean var21 = var8 == var11 && var10 == var13;
                     boolean var22 = var21 && var9 > var12;
                     return var22 && var5 == 0 && var6.getValue() == 0 ? 0 : var5 + Math.max(1, var6.getValue());
                  }
               }
            }
         }
      } else {
         return 15;
      }
   }

   protected void checkNeighborsAfterUpdate(long var1, int var3, boolean var4) {
      long var5 = SectionPos.blockToSection(var1);
      int var7 = BlockPos.getY(var1);
      int var8 = SectionPos.sectionRelative(var7);
      int var9 = SectionPos.blockToSectionCoord(var7);
      int var10;
      if (var8 != 0) {
         var10 = 0;
      } else {
         int var11;
         for(var11 = 0; !((SkyLightSectionStorage)this.storage).storingLightForSection(SectionPos.offset(var5, 0, -var11 - 1, 0)) && ((SkyLightSectionStorage)this.storage).hasSectionsBelow(var9 - var11 - 1); ++var11) {
         }

         var10 = var11;
      }

      long var30 = BlockPos.offset(var1, 0, -1 - var10 * 16, 0);
      long var13 = SectionPos.blockToSection(var30);
      if (var5 == var13 || ((SkyLightSectionStorage)this.storage).storingLightForSection(var13)) {
         this.checkNeighbor(var1, var30, var3, var4);
      }

      long var15 = BlockPos.offset(var1, Direction.UP);
      long var17 = SectionPos.blockToSection(var15);
      if (var5 == var17 || ((SkyLightSectionStorage)this.storage).storingLightForSection(var17)) {
         this.checkNeighbor(var1, var15, var3, var4);
      }

      Direction[] var19 = HORIZONTALS;
      int var20 = var19.length;

      for(int var21 = 0; var21 < var20; ++var21) {
         Direction var22 = var19[var21];
         int var23 = 0;

         while(true) {
            long var24 = BlockPos.offset(var1, var22.getStepX(), -var23, var22.getStepZ());
            long var26 = SectionPos.blockToSection(var24);
            if (var5 == var26) {
               this.checkNeighbor(var1, var24, var3, var4);
               break;
            }

            if (((SkyLightSectionStorage)this.storage).storingLightForSection(var26)) {
               long var28 = BlockPos.offset(var1, 0, -var23, 0);
               this.checkNeighbor(var28, var24, var3, var4);
            }

            ++var23;
            if (var23 > var10 * 16) {
               break;
            }
         }
      }

   }

   protected int getComputedLevel(long var1, long var3, int var5) {
      int var6 = var5;
      long var7 = SectionPos.blockToSection(var1);
      DataLayer var9 = ((SkyLightSectionStorage)this.storage).getDataLayer(var7, true);
      Direction[] var10 = DIRECTIONS;
      int var11 = var10.length;

      for(int var12 = 0; var12 < var11; ++var12) {
         Direction var13 = var10[var12];
         long var14 = BlockPos.offset(var1, var13);
         if (var14 != var3) {
            long var16 = SectionPos.blockToSection(var14);
            DataLayer var18;
            if (var7 == var16) {
               var18 = var9;
            } else {
               var18 = ((SkyLightSectionStorage)this.storage).getDataLayer(var16, true);
            }

            int var19;
            if (var18 != null) {
               var19 = this.getLevel(var18, var14);
            } else {
               if (var13 == Direction.DOWN) {
                  continue;
               }

               var19 = 15 - ((SkyLightSectionStorage)this.storage).getLightValue(var14, true);
            }

            int var20 = this.computeLevelFromNeighbor(var14, var1, var19);
            if (var6 > var20) {
               var6 = var20;
            }

            if (var6 == 0) {
               return var6;
            }
         }
      }

      return var6;
   }

   protected void checkNode(long var1) {
      ((SkyLightSectionStorage)this.storage).runAllUpdates();
      long var3 = SectionPos.blockToSection(var1);
      if (((SkyLightSectionStorage)this.storage).storingLightForSection(var3)) {
         super.checkNode(var1);
      } else {
         for(var1 = BlockPos.getFlatIndex(var1); !((SkyLightSectionStorage)this.storage).storingLightForSection(var3) && !((SkyLightSectionStorage)this.storage).isAboveData(var3); var1 = BlockPos.offset(var1, 0, 16, 0)) {
            var3 = SectionPos.offset(var3, Direction.UP);
         }

         if (((SkyLightSectionStorage)this.storage).storingLightForSection(var3)) {
            super.checkNode(var1);
         }
      }

   }

   public String getDebugData(long var1) {
      return super.getDebugData(var1) + (((SkyLightSectionStorage)this.storage).isAboveData(var1) ? "*" : "");
   }

   static {
      HORIZONTALS = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
   }
}
