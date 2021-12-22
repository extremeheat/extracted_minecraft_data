package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class NetherBridgePieces {
   private static final int MAX_DEPTH = 30;
   private static final int LOWEST_Y_POSITION = 10;
   public static final int MAGIC_START_Y = 64;
   static final NetherBridgePieces.PieceWeight[] BRIDGE_PIECE_WEIGHTS = new NetherBridgePieces.PieceWeight[]{new NetherBridgePieces.PieceWeight(NetherBridgePieces.BridgeStraight.class, 30, 0, true), new NetherBridgePieces.PieceWeight(NetherBridgePieces.BridgeCrossing.class, 10, 4), new NetherBridgePieces.PieceWeight(NetherBridgePieces.RoomCrossing.class, 10, 4), new NetherBridgePieces.PieceWeight(NetherBridgePieces.StairsRoom.class, 10, 3), new NetherBridgePieces.PieceWeight(NetherBridgePieces.MonsterThrone.class, 5, 2), new NetherBridgePieces.PieceWeight(NetherBridgePieces.CastleEntrance.class, 5, 1)};
   static final NetherBridgePieces.PieceWeight[] CASTLE_PIECE_WEIGHTS = new NetherBridgePieces.PieceWeight[]{new NetherBridgePieces.PieceWeight(NetherBridgePieces.CastleSmallCorridorPiece.class, 25, 0, true), new NetherBridgePieces.PieceWeight(NetherBridgePieces.CastleSmallCorridorCrossingPiece.class, 15, 5), new NetherBridgePieces.PieceWeight(NetherBridgePieces.CastleSmallCorridorRightTurnPiece.class, 5, 10), new NetherBridgePieces.PieceWeight(NetherBridgePieces.CastleSmallCorridorLeftTurnPiece.class, 5, 10), new NetherBridgePieces.PieceWeight(NetherBridgePieces.CastleCorridorStairsPiece.class, 10, 3, true), new NetherBridgePieces.PieceWeight(NetherBridgePieces.CastleCorridorTBalconyPiece.class, 7, 2), new NetherBridgePieces.PieceWeight(NetherBridgePieces.CastleStalkRoom.class, 5, 2)};

   public NetherBridgePieces() {
      super();
   }

   static NetherBridgePieces.NetherBridgePiece findAndCreateBridgePieceFactory(NetherBridgePieces.PieceWeight var0, StructurePieceAccessor var1, Random var2, int var3, int var4, int var5, Direction var6, int var7) {
      Class var8 = var0.pieceClass;
      Object var9 = null;
      if (var8 == NetherBridgePieces.BridgeStraight.class) {
         var9 = NetherBridgePieces.BridgeStraight.createPiece(var1, var2, var3, var4, var5, var6, var7);
      } else if (var8 == NetherBridgePieces.BridgeCrossing.class) {
         var9 = NetherBridgePieces.BridgeCrossing.createPiece(var1, var3, var4, var5, var6, var7);
      } else if (var8 == NetherBridgePieces.RoomCrossing.class) {
         var9 = NetherBridgePieces.RoomCrossing.createPiece(var1, var3, var4, var5, var6, var7);
      } else if (var8 == NetherBridgePieces.StairsRoom.class) {
         var9 = NetherBridgePieces.StairsRoom.createPiece(var1, var3, var4, var5, var7, var6);
      } else if (var8 == NetherBridgePieces.MonsterThrone.class) {
         var9 = NetherBridgePieces.MonsterThrone.createPiece(var1, var3, var4, var5, var7, var6);
      } else if (var8 == NetherBridgePieces.CastleEntrance.class) {
         var9 = NetherBridgePieces.CastleEntrance.createPiece(var1, var2, var3, var4, var5, var6, var7);
      } else if (var8 == NetherBridgePieces.CastleSmallCorridorPiece.class) {
         var9 = NetherBridgePieces.CastleSmallCorridorPiece.createPiece(var1, var3, var4, var5, var6, var7);
      } else if (var8 == NetherBridgePieces.CastleSmallCorridorRightTurnPiece.class) {
         var9 = NetherBridgePieces.CastleSmallCorridorRightTurnPiece.createPiece(var1, var2, var3, var4, var5, var6, var7);
      } else if (var8 == NetherBridgePieces.CastleSmallCorridorLeftTurnPiece.class) {
         var9 = NetherBridgePieces.CastleSmallCorridorLeftTurnPiece.createPiece(var1, var2, var3, var4, var5, var6, var7);
      } else if (var8 == NetherBridgePieces.CastleCorridorStairsPiece.class) {
         var9 = NetherBridgePieces.CastleCorridorStairsPiece.createPiece(var1, var3, var4, var5, var6, var7);
      } else if (var8 == NetherBridgePieces.CastleCorridorTBalconyPiece.class) {
         var9 = NetherBridgePieces.CastleCorridorTBalconyPiece.createPiece(var1, var3, var4, var5, var6, var7);
      } else if (var8 == NetherBridgePieces.CastleSmallCorridorCrossingPiece.class) {
         var9 = NetherBridgePieces.CastleSmallCorridorCrossingPiece.createPiece(var1, var3, var4, var5, var6, var7);
      } else if (var8 == NetherBridgePieces.CastleStalkRoom.class) {
         var9 = NetherBridgePieces.CastleStalkRoom.createPiece(var1, var3, var4, var5, var6, var7);
      }

      return (NetherBridgePieces.NetherBridgePiece)var9;
   }

   private static class PieceWeight {
      public final Class<? extends NetherBridgePieces.NetherBridgePiece> pieceClass;
      public final int weight;
      public int placeCount;
      public final int maxPlaceCount;
      public final boolean allowInRow;

      public PieceWeight(Class<? extends NetherBridgePieces.NetherBridgePiece> var1, int var2, int var3, boolean var4) {
         super();
         this.pieceClass = var1;
         this.weight = var2;
         this.maxPlaceCount = var3;
         this.allowInRow = var4;
      }

      public PieceWeight(Class<? extends NetherBridgePieces.NetherBridgePiece> var1, int var2, int var3) {
         this(var1, var2, var3, false);
      }

      public boolean doPlace(int var1) {
         return this.maxPlaceCount == 0 || this.placeCount < this.maxPlaceCount;
      }

      public boolean isValid() {
         return this.maxPlaceCount == 0 || this.placeCount < this.maxPlaceCount;
      }
   }

   public static class BridgeStraight extends NetherBridgePieces.NetherBridgePiece {
      private static final int WIDTH = 5;
      private static final int HEIGHT = 10;
      private static final int DEPTH = 19;

      public BridgeStraight(int var1, Random var2, BoundingBox var3, Direction var4) {
         super(StructurePieceType.NETHER_FORTRESS_BRIDGE_STRAIGHT, var1, var3);
         this.setOrientation(var4);
      }

      public BridgeStraight(CompoundTag var1) {
         super(StructurePieceType.NETHER_FORTRESS_BRIDGE_STRAIGHT, var1);
      }

      public void addChildren(StructurePiece var1, StructurePieceAccessor var2, Random var3) {
         this.generateChildForward((NetherBridgePieces.StartPiece)var1, var2, var3, 1, 3, false);
      }

      public static NetherBridgePieces.BridgeStraight createPiece(StructurePieceAccessor var0, Random var1, int var2, int var3, int var4, Direction var5, int var6) {
         BoundingBox var7 = BoundingBox.orientBox(var2, var3, var4, -1, -3, 0, 5, 10, 19, var5);
         return isOkBox(var7) && var0.findCollisionPiece(var7) == null ? new NetherBridgePieces.BridgeStraight(var6, var1, var7, var5) : null;
      }

      public void postProcess(WorldGenLevel var1, StructureFeatureManager var2, ChunkGenerator var3, Random var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
         this.generateBox(var1, var5, 0, 3, 0, 4, 4, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 1, 5, 0, 3, 7, 18, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(var1, var5, 0, 5, 0, 0, 5, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 4, 5, 0, 4, 5, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 0, 2, 0, 4, 2, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 0, 2, 13, 4, 2, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 0, 0, 0, 4, 1, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 0, 0, 15, 4, 1, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);

         for(int var8 = 0; var8 <= 4; ++var8) {
            for(int var9 = 0; var9 <= 2; ++var9) {
               this.fillColumnDown(var1, Blocks.NETHER_BRICKS.defaultBlockState(), var8, -1, var9, var5);
               this.fillColumnDown(var1, Blocks.NETHER_BRICKS.defaultBlockState(), var8, -1, 18 - var9, var5);
            }
         }

         BlockState var11 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, true)).setValue(FenceBlock.SOUTH, true);
         BlockState var12 = (BlockState)var11.setValue(FenceBlock.EAST, true);
         BlockState var10 = (BlockState)var11.setValue(FenceBlock.WEST, true);
         this.generateBox(var1, var5, 0, 1, 1, 0, 4, 1, var12, var12, false);
         this.generateBox(var1, var5, 0, 3, 4, 0, 4, 4, var12, var12, false);
         this.generateBox(var1, var5, 0, 3, 14, 0, 4, 14, var12, var12, false);
         this.generateBox(var1, var5, 0, 1, 17, 0, 4, 17, var12, var12, false);
         this.generateBox(var1, var5, 4, 1, 1, 4, 4, 1, var10, var10, false);
         this.generateBox(var1, var5, 4, 3, 4, 4, 4, 4, var10, var10, false);
         this.generateBox(var1, var5, 4, 3, 14, 4, 4, 14, var10, var10, false);
         this.generateBox(var1, var5, 4, 1, 17, 4, 4, 17, var10, var10, false);
      }
   }

   public static class BridgeCrossing extends NetherBridgePieces.NetherBridgePiece {
      private static final int WIDTH = 19;
      private static final int HEIGHT = 10;
      private static final int DEPTH = 19;

      public BridgeCrossing(int var1, BoundingBox var2, Direction var3) {
         super(StructurePieceType.NETHER_FORTRESS_BRIDGE_CROSSING, var1, var2);
         this.setOrientation(var3);
      }

      protected BridgeCrossing(int var1, int var2, Direction var3) {
         super(StructurePieceType.NETHER_FORTRESS_BRIDGE_CROSSING, 0, StructurePiece.makeBoundingBox(var1, 64, var2, var3, 19, 10, 19));
         this.setOrientation(var3);
      }

      protected BridgeCrossing(StructurePieceType var1, CompoundTag var2) {
         super(var1, var2);
      }

      public BridgeCrossing(CompoundTag var1) {
         this(StructurePieceType.NETHER_FORTRESS_BRIDGE_CROSSING, var1);
      }

      public void addChildren(StructurePiece var1, StructurePieceAccessor var2, Random var3) {
         this.generateChildForward((NetherBridgePieces.StartPiece)var1, var2, var3, 8, 3, false);
         this.generateChildLeft((NetherBridgePieces.StartPiece)var1, var2, var3, 3, 8, false);
         this.generateChildRight((NetherBridgePieces.StartPiece)var1, var2, var3, 3, 8, false);
      }

      public static NetherBridgePieces.BridgeCrossing createPiece(StructurePieceAccessor var0, int var1, int var2, int var3, Direction var4, int var5) {
         BoundingBox var6 = BoundingBox.orientBox(var1, var2, var3, -8, -3, 0, 19, 10, 19, var4);
         return isOkBox(var6) && var0.findCollisionPiece(var6) == null ? new NetherBridgePieces.BridgeCrossing(var5, var6, var4) : null;
      }

      public void postProcess(WorldGenLevel var1, StructureFeatureManager var2, ChunkGenerator var3, Random var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
         this.generateBox(var1, var5, 7, 3, 0, 11, 4, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 0, 3, 7, 18, 4, 11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 8, 5, 0, 10, 7, 18, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(var1, var5, 0, 5, 8, 18, 7, 10, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(var1, var5, 7, 5, 0, 7, 5, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 7, 5, 11, 7, 5, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 11, 5, 0, 11, 5, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 11, 5, 11, 11, 5, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 0, 5, 7, 7, 5, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 11, 5, 7, 18, 5, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 0, 5, 11, 7, 5, 11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 11, 5, 11, 18, 5, 11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 7, 2, 0, 11, 2, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 7, 2, 13, 11, 2, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 7, 0, 0, 11, 1, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 7, 0, 15, 11, 1, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);

         int var8;
         int var9;
         for(var8 = 7; var8 <= 11; ++var8) {
            for(var9 = 0; var9 <= 2; ++var9) {
               this.fillColumnDown(var1, Blocks.NETHER_BRICKS.defaultBlockState(), var8, -1, var9, var5);
               this.fillColumnDown(var1, Blocks.NETHER_BRICKS.defaultBlockState(), var8, -1, 18 - var9, var5);
            }
         }

         this.generateBox(var1, var5, 0, 2, 7, 5, 2, 11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 13, 2, 7, 18, 2, 11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 0, 0, 7, 3, 1, 11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 15, 0, 7, 18, 1, 11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);

         for(var8 = 0; var8 <= 2; ++var8) {
            for(var9 = 7; var9 <= 11; ++var9) {
               this.fillColumnDown(var1, Blocks.NETHER_BRICKS.defaultBlockState(), var8, -1, var9, var5);
               this.fillColumnDown(var1, Blocks.NETHER_BRICKS.defaultBlockState(), 18 - var8, -1, var9, var5);
            }
         }

      }
   }

   public static class RoomCrossing extends NetherBridgePieces.NetherBridgePiece {
      private static final int WIDTH = 7;
      private static final int HEIGHT = 9;
      private static final int DEPTH = 7;

      public RoomCrossing(int var1, BoundingBox var2, Direction var3) {
         super(StructurePieceType.NETHER_FORTRESS_ROOM_CROSSING, var1, var2);
         this.setOrientation(var3);
      }

      public RoomCrossing(CompoundTag var1) {
         super(StructurePieceType.NETHER_FORTRESS_ROOM_CROSSING, var1);
      }

      public void addChildren(StructurePiece var1, StructurePieceAccessor var2, Random var3) {
         this.generateChildForward((NetherBridgePieces.StartPiece)var1, var2, var3, 2, 0, false);
         this.generateChildLeft((NetherBridgePieces.StartPiece)var1, var2, var3, 0, 2, false);
         this.generateChildRight((NetherBridgePieces.StartPiece)var1, var2, var3, 0, 2, false);
      }

      public static NetherBridgePieces.RoomCrossing createPiece(StructurePieceAccessor var0, int var1, int var2, int var3, Direction var4, int var5) {
         BoundingBox var6 = BoundingBox.orientBox(var1, var2, var3, -2, 0, 0, 7, 9, 7, var4);
         return isOkBox(var6) && var0.findCollisionPiece(var6) == null ? new NetherBridgePieces.RoomCrossing(var5, var6, var4) : null;
      }

      public void postProcess(WorldGenLevel var1, StructureFeatureManager var2, ChunkGenerator var3, Random var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
         this.generateBox(var1, var5, 0, 0, 0, 6, 1, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 0, 2, 0, 6, 7, 6, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(var1, var5, 0, 2, 0, 1, 6, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 0, 2, 6, 1, 6, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 5, 2, 0, 6, 6, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 5, 2, 6, 6, 6, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 0, 2, 0, 0, 6, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 0, 2, 5, 0, 6, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 6, 2, 0, 6, 6, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 6, 2, 5, 6, 6, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         BlockState var8 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, true)).setValue(FenceBlock.EAST, true);
         BlockState var9 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, true)).setValue(FenceBlock.SOUTH, true);
         this.generateBox(var1, var5, 2, 6, 0, 4, 6, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 2, 5, 0, 4, 5, 0, var8, var8, false);
         this.generateBox(var1, var5, 2, 6, 6, 4, 6, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 2, 5, 6, 4, 5, 6, var8, var8, false);
         this.generateBox(var1, var5, 0, 6, 2, 0, 6, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 0, 5, 2, 0, 5, 4, var9, var9, false);
         this.generateBox(var1, var5, 6, 6, 2, 6, 6, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 6, 5, 2, 6, 5, 4, var9, var9, false);

         for(int var10 = 0; var10 <= 6; ++var10) {
            for(int var11 = 0; var11 <= 6; ++var11) {
               this.fillColumnDown(var1, Blocks.NETHER_BRICKS.defaultBlockState(), var10, -1, var11, var5);
            }
         }

      }
   }

   public static class StairsRoom extends NetherBridgePieces.NetherBridgePiece {
      private static final int WIDTH = 7;
      private static final int HEIGHT = 11;
      private static final int DEPTH = 7;

      public StairsRoom(int var1, BoundingBox var2, Direction var3) {
         super(StructurePieceType.NETHER_FORTRESS_STAIRS_ROOM, var1, var2);
         this.setOrientation(var3);
      }

      public StairsRoom(CompoundTag var1) {
         super(StructurePieceType.NETHER_FORTRESS_STAIRS_ROOM, var1);
      }

      public void addChildren(StructurePiece var1, StructurePieceAccessor var2, Random var3) {
         this.generateChildRight((NetherBridgePieces.StartPiece)var1, var2, var3, 6, 2, false);
      }

      public static NetherBridgePieces.StairsRoom createPiece(StructurePieceAccessor var0, int var1, int var2, int var3, int var4, Direction var5) {
         BoundingBox var6 = BoundingBox.orientBox(var1, var2, var3, -2, 0, 0, 7, 11, 7, var5);
         return isOkBox(var6) && var0.findCollisionPiece(var6) == null ? new NetherBridgePieces.StairsRoom(var4, var6, var5) : null;
      }

      public void postProcess(WorldGenLevel var1, StructureFeatureManager var2, ChunkGenerator var3, Random var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
         this.generateBox(var1, var5, 0, 0, 0, 6, 1, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 0, 2, 0, 6, 10, 6, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(var1, var5, 0, 2, 0, 1, 8, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 5, 2, 0, 6, 8, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 0, 2, 1, 0, 8, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 6, 2, 1, 6, 8, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 1, 2, 6, 5, 8, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         BlockState var8 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, true)).setValue(FenceBlock.EAST, true);
         BlockState var9 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, true)).setValue(FenceBlock.SOUTH, true);
         this.generateBox(var1, var5, 0, 3, 2, 0, 5, 4, var9, var9, false);
         this.generateBox(var1, var5, 6, 3, 2, 6, 5, 2, var9, var9, false);
         this.generateBox(var1, var5, 6, 3, 4, 6, 5, 4, var9, var9, false);
         this.placeBlock(var1, Blocks.NETHER_BRICKS.defaultBlockState(), 5, 2, 5, var5);
         this.generateBox(var1, var5, 4, 2, 5, 4, 3, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 3, 2, 5, 3, 4, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 2, 2, 5, 2, 5, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 1, 2, 5, 1, 6, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 1, 7, 1, 5, 7, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 6, 8, 2, 6, 8, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(var1, var5, 2, 6, 0, 4, 8, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 2, 5, 0, 4, 5, 0, var8, var8, false);

         for(int var10 = 0; var10 <= 6; ++var10) {
            for(int var11 = 0; var11 <= 6; ++var11) {
               this.fillColumnDown(var1, Blocks.NETHER_BRICKS.defaultBlockState(), var10, -1, var11, var5);
            }
         }

      }
   }

   public static class MonsterThrone extends NetherBridgePieces.NetherBridgePiece {
      private static final int WIDTH = 7;
      private static final int HEIGHT = 8;
      private static final int DEPTH = 9;
      private boolean hasPlacedSpawner;

      public MonsterThrone(int var1, BoundingBox var2, Direction var3) {
         super(StructurePieceType.NETHER_FORTRESS_MONSTER_THRONE, var1, var2);
         this.setOrientation(var3);
      }

      public MonsterThrone(CompoundTag var1) {
         super(StructurePieceType.NETHER_FORTRESS_MONSTER_THRONE, var1);
         this.hasPlacedSpawner = var1.getBoolean("Mob");
      }

      protected void addAdditionalSaveData(StructurePieceSerializationContext var1, CompoundTag var2) {
         super.addAdditionalSaveData(var1, var2);
         var2.putBoolean("Mob", this.hasPlacedSpawner);
      }

      public static NetherBridgePieces.MonsterThrone createPiece(StructurePieceAccessor var0, int var1, int var2, int var3, int var4, Direction var5) {
         BoundingBox var6 = BoundingBox.orientBox(var1, var2, var3, -2, 0, 0, 7, 8, 9, var5);
         return isOkBox(var6) && var0.findCollisionPiece(var6) == null ? new NetherBridgePieces.MonsterThrone(var4, var6, var5) : null;
      }

      public void postProcess(WorldGenLevel var1, StructureFeatureManager var2, ChunkGenerator var3, Random var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
         this.generateBox(var1, var5, 0, 2, 0, 6, 7, 7, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(var1, var5, 1, 0, 0, 5, 1, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 1, 2, 1, 5, 2, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 1, 3, 2, 5, 3, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 1, 4, 3, 5, 4, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 1, 2, 0, 1, 4, 2, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 5, 2, 0, 5, 4, 2, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 1, 5, 2, 1, 5, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 5, 5, 2, 5, 5, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 0, 5, 3, 0, 5, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 6, 5, 3, 6, 5, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 1, 5, 8, 5, 5, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         BlockState var8 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, true)).setValue(FenceBlock.EAST, true);
         BlockState var9 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, true)).setValue(FenceBlock.SOUTH, true);
         this.placeBlock(var1, (BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, true), 1, 6, 3, var5);
         this.placeBlock(var1, (BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.EAST, true), 5, 6, 3, var5);
         this.placeBlock(var1, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.EAST, true)).setValue(FenceBlock.NORTH, true), 0, 6, 3, var5);
         this.placeBlock(var1, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, true)).setValue(FenceBlock.NORTH, true), 6, 6, 3, var5);
         this.generateBox(var1, var5, 0, 6, 4, 0, 6, 7, var9, var9, false);
         this.generateBox(var1, var5, 6, 6, 4, 6, 6, 7, var9, var9, false);
         this.placeBlock(var1, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.EAST, true)).setValue(FenceBlock.SOUTH, true), 0, 6, 8, var5);
         this.placeBlock(var1, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, true)).setValue(FenceBlock.SOUTH, true), 6, 6, 8, var5);
         this.generateBox(var1, var5, 1, 6, 8, 5, 6, 8, var8, var8, false);
         this.placeBlock(var1, (BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.EAST, true), 1, 7, 8, var5);
         this.generateBox(var1, var5, 2, 7, 8, 4, 7, 8, var8, var8, false);
         this.placeBlock(var1, (BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, true), 5, 7, 8, var5);
         this.placeBlock(var1, (BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.EAST, true), 2, 8, 8, var5);
         this.placeBlock(var1, var8, 3, 8, 8, var5);
         this.placeBlock(var1, (BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, true), 4, 8, 8, var5);
         if (!this.hasPlacedSpawner) {
            BlockPos.MutableBlockPos var10 = this.getWorldPos(3, 5, 5);
            if (var5.isInside(var10)) {
               this.hasPlacedSpawner = true;
               var1.setBlock(var10, Blocks.SPAWNER.defaultBlockState(), 2);
               BlockEntity var11 = var1.getBlockEntity(var10);
               if (var11 instanceof SpawnerBlockEntity) {
                  ((SpawnerBlockEntity)var11).getSpawner().setEntityId(EntityType.BLAZE);
               }
            }
         }

         for(int var12 = 0; var12 <= 6; ++var12) {
            for(int var13 = 0; var13 <= 6; ++var13) {
               this.fillColumnDown(var1, Blocks.NETHER_BRICKS.defaultBlockState(), var12, -1, var13, var5);
            }
         }

      }
   }

   public static class CastleEntrance extends NetherBridgePieces.NetherBridgePiece {
      private static final int WIDTH = 13;
      private static final int HEIGHT = 14;
      private static final int DEPTH = 13;

      public CastleEntrance(int var1, Random var2, BoundingBox var3, Direction var4) {
         super(StructurePieceType.NETHER_FORTRESS_CASTLE_ENTRANCE, var1, var3);
         this.setOrientation(var4);
      }

      public CastleEntrance(CompoundTag var1) {
         super(StructurePieceType.NETHER_FORTRESS_CASTLE_ENTRANCE, var1);
      }

      public void addChildren(StructurePiece var1, StructurePieceAccessor var2, Random var3) {
         this.generateChildForward((NetherBridgePieces.StartPiece)var1, var2, var3, 5, 3, true);
      }

      public static NetherBridgePieces.CastleEntrance createPiece(StructurePieceAccessor var0, Random var1, int var2, int var3, int var4, Direction var5, int var6) {
         BoundingBox var7 = BoundingBox.orientBox(var2, var3, var4, -5, -3, 0, 13, 14, 13, var5);
         return isOkBox(var7) && var0.findCollisionPiece(var7) == null ? new NetherBridgePieces.CastleEntrance(var6, var1, var7, var5) : null;
      }

      public void postProcess(WorldGenLevel var1, StructureFeatureManager var2, ChunkGenerator var3, Random var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
         this.generateBox(var1, var5, 0, 3, 0, 12, 4, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 0, 5, 0, 12, 13, 12, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(var1, var5, 0, 5, 0, 1, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 11, 5, 0, 12, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 2, 5, 11, 4, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 8, 5, 11, 10, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 5, 9, 11, 7, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 2, 5, 0, 4, 12, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 8, 5, 0, 10, 12, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 5, 9, 0, 7, 12, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 2, 11, 2, 10, 12, 10, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 5, 8, 0, 7, 8, 0, Blocks.NETHER_BRICK_FENCE.defaultBlockState(), Blocks.NETHER_BRICK_FENCE.defaultBlockState(), false);
         BlockState var8 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, true)).setValue(FenceBlock.EAST, true);
         BlockState var9 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, true)).setValue(FenceBlock.SOUTH, true);

         int var10;
         for(var10 = 1; var10 <= 11; var10 += 2) {
            this.generateBox(var1, var5, var10, 10, 0, var10, 11, 0, var8, var8, false);
            this.generateBox(var1, var5, var10, 10, 12, var10, 11, 12, var8, var8, false);
            this.generateBox(var1, var5, 0, 10, var10, 0, 11, var10, var9, var9, false);
            this.generateBox(var1, var5, 12, 10, var10, 12, 11, var10, var9, var9, false);
            this.placeBlock(var1, Blocks.NETHER_BRICKS.defaultBlockState(), var10, 13, 0, var5);
            this.placeBlock(var1, Blocks.NETHER_BRICKS.defaultBlockState(), var10, 13, 12, var5);
            this.placeBlock(var1, Blocks.NETHER_BRICKS.defaultBlockState(), 0, 13, var10, var5);
            this.placeBlock(var1, Blocks.NETHER_BRICKS.defaultBlockState(), 12, 13, var10, var5);
            if (var10 != 11) {
               this.placeBlock(var1, var8, var10 + 1, 13, 0, var5);
               this.placeBlock(var1, var8, var10 + 1, 13, 12, var5);
               this.placeBlock(var1, var9, 0, 13, var10 + 1, var5);
               this.placeBlock(var1, var9, 12, 13, var10 + 1, var5);
            }
         }

         this.placeBlock(var1, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, true)).setValue(FenceBlock.EAST, true), 0, 13, 0, var5);
         this.placeBlock(var1, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.SOUTH, true)).setValue(FenceBlock.EAST, true), 0, 13, 12, var5);
         this.placeBlock(var1, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.SOUTH, true)).setValue(FenceBlock.WEST, true), 12, 13, 12, var5);
         this.placeBlock(var1, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, true)).setValue(FenceBlock.WEST, true), 12, 13, 0, var5);

         for(var10 = 3; var10 <= 9; var10 += 2) {
            this.generateBox(var1, var5, 1, 7, var10, 1, 8, var10, (BlockState)var9.setValue(FenceBlock.WEST, true), (BlockState)var9.setValue(FenceBlock.WEST, true), false);
            this.generateBox(var1, var5, 11, 7, var10, 11, 8, var10, (BlockState)var9.setValue(FenceBlock.EAST, true), (BlockState)var9.setValue(FenceBlock.EAST, true), false);
         }

         this.generateBox(var1, var5, 4, 2, 0, 8, 2, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 0, 2, 4, 12, 2, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 4, 0, 0, 8, 1, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 4, 0, 9, 8, 1, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 0, 0, 4, 3, 1, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 9, 0, 4, 12, 1, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);

         int var11;
         for(var10 = 4; var10 <= 8; ++var10) {
            for(var11 = 0; var11 <= 2; ++var11) {
               this.fillColumnDown(var1, Blocks.NETHER_BRICKS.defaultBlockState(), var10, -1, var11, var5);
               this.fillColumnDown(var1, Blocks.NETHER_BRICKS.defaultBlockState(), var10, -1, 12 - var11, var5);
            }
         }

         for(var10 = 0; var10 <= 2; ++var10) {
            for(var11 = 4; var11 <= 8; ++var11) {
               this.fillColumnDown(var1, Blocks.NETHER_BRICKS.defaultBlockState(), var10, -1, var11, var5);
               this.fillColumnDown(var1, Blocks.NETHER_BRICKS.defaultBlockState(), 12 - var10, -1, var11, var5);
            }
         }

         this.generateBox(var1, var5, 5, 5, 5, 7, 5, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 6, 1, 6, 6, 4, 6, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.placeBlock(var1, Blocks.NETHER_BRICKS.defaultBlockState(), 6, 0, 6, var5);
         this.placeBlock(var1, Blocks.LAVA.defaultBlockState(), 6, 5, 6, var5);
         BlockPos.MutableBlockPos var12 = this.getWorldPos(6, 5, 6);
         if (var5.isInside(var12)) {
            var1.scheduleTick(var12, Fluids.LAVA, 0);
         }

      }
   }

   public static class CastleSmallCorridorPiece extends NetherBridgePieces.NetherBridgePiece {
      private static final int WIDTH = 5;
      private static final int HEIGHT = 7;
      private static final int DEPTH = 5;

      public CastleSmallCorridorPiece(int var1, BoundingBox var2, Direction var3) {
         super(StructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR, var1, var2);
         this.setOrientation(var3);
      }

      public CastleSmallCorridorPiece(CompoundTag var1) {
         super(StructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR, var1);
      }

      public void addChildren(StructurePiece var1, StructurePieceAccessor var2, Random var3) {
         this.generateChildForward((NetherBridgePieces.StartPiece)var1, var2, var3, 1, 0, true);
      }

      public static NetherBridgePieces.CastleSmallCorridorPiece createPiece(StructurePieceAccessor var0, int var1, int var2, int var3, Direction var4, int var5) {
         BoundingBox var6 = BoundingBox.orientBox(var1, var2, var3, -1, 0, 0, 5, 7, 5, var4);
         return isOkBox(var6) && var0.findCollisionPiece(var6) == null ? new NetherBridgePieces.CastleSmallCorridorPiece(var5, var6, var4) : null;
      }

      public void postProcess(WorldGenLevel var1, StructureFeatureManager var2, ChunkGenerator var3, Random var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
         this.generateBox(var1, var5, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 0, 2, 0, 4, 5, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         BlockState var8 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, true)).setValue(FenceBlock.SOUTH, true);
         this.generateBox(var1, var5, 0, 2, 0, 0, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 4, 2, 0, 4, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 0, 3, 1, 0, 4, 1, var8, var8, false);
         this.generateBox(var1, var5, 0, 3, 3, 0, 4, 3, var8, var8, false);
         this.generateBox(var1, var5, 4, 3, 1, 4, 4, 1, var8, var8, false);
         this.generateBox(var1, var5, 4, 3, 3, 4, 4, 3, var8, var8, false);
         this.generateBox(var1, var5, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);

         for(int var9 = 0; var9 <= 4; ++var9) {
            for(int var10 = 0; var10 <= 4; ++var10) {
               this.fillColumnDown(var1, Blocks.NETHER_BRICKS.defaultBlockState(), var9, -1, var10, var5);
            }
         }

      }
   }

   public static class CastleSmallCorridorRightTurnPiece extends NetherBridgePieces.NetherBridgePiece {
      private static final int WIDTH = 5;
      private static final int HEIGHT = 7;
      private static final int DEPTH = 5;
      private boolean isNeedingChest;

      public CastleSmallCorridorRightTurnPiece(int var1, Random var2, BoundingBox var3, Direction var4) {
         super(StructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_RIGHT_TURN, var1, var3);
         this.setOrientation(var4);
         this.isNeedingChest = var2.nextInt(3) == 0;
      }

      public CastleSmallCorridorRightTurnPiece(CompoundTag var1) {
         super(StructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_RIGHT_TURN, var1);
         this.isNeedingChest = var1.getBoolean("Chest");
      }

      protected void addAdditionalSaveData(StructurePieceSerializationContext var1, CompoundTag var2) {
         super.addAdditionalSaveData(var1, var2);
         var2.putBoolean("Chest", this.isNeedingChest);
      }

      public void addChildren(StructurePiece var1, StructurePieceAccessor var2, Random var3) {
         this.generateChildRight((NetherBridgePieces.StartPiece)var1, var2, var3, 0, 1, true);
      }

      public static NetherBridgePieces.CastleSmallCorridorRightTurnPiece createPiece(StructurePieceAccessor var0, Random var1, int var2, int var3, int var4, Direction var5, int var6) {
         BoundingBox var7 = BoundingBox.orientBox(var2, var3, var4, -1, 0, 0, 5, 7, 5, var5);
         return isOkBox(var7) && var0.findCollisionPiece(var7) == null ? new NetherBridgePieces.CastleSmallCorridorRightTurnPiece(var6, var1, var7, var5) : null;
      }

      public void postProcess(WorldGenLevel var1, StructureFeatureManager var2, ChunkGenerator var3, Random var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
         this.generateBox(var1, var5, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 0, 2, 0, 4, 5, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         BlockState var8 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, true)).setValue(FenceBlock.EAST, true);
         BlockState var9 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, true)).setValue(FenceBlock.SOUTH, true);
         this.generateBox(var1, var5, 0, 2, 0, 0, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 0, 3, 1, 0, 4, 1, var9, var9, false);
         this.generateBox(var1, var5, 0, 3, 3, 0, 4, 3, var9, var9, false);
         this.generateBox(var1, var5, 4, 2, 0, 4, 5, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 1, 2, 4, 4, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 1, 3, 4, 1, 4, 4, var8, var8, false);
         this.generateBox(var1, var5, 3, 3, 4, 3, 4, 4, var8, var8, false);
         if (this.isNeedingChest && var5.isInside(this.getWorldPos(1, 2, 3))) {
            this.isNeedingChest = false;
            this.createChest(var1, var5, var4, 1, 2, 3, BuiltInLootTables.NETHER_BRIDGE);
         }

         this.generateBox(var1, var5, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);

         for(int var10 = 0; var10 <= 4; ++var10) {
            for(int var11 = 0; var11 <= 4; ++var11) {
               this.fillColumnDown(var1, Blocks.NETHER_BRICKS.defaultBlockState(), var10, -1, var11, var5);
            }
         }

      }
   }

   public static class CastleSmallCorridorLeftTurnPiece extends NetherBridgePieces.NetherBridgePiece {
      private static final int WIDTH = 5;
      private static final int HEIGHT = 7;
      private static final int DEPTH = 5;
      private boolean isNeedingChest;

      public CastleSmallCorridorLeftTurnPiece(int var1, Random var2, BoundingBox var3, Direction var4) {
         super(StructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_LEFT_TURN, var1, var3);
         this.setOrientation(var4);
         this.isNeedingChest = var2.nextInt(3) == 0;
      }

      public CastleSmallCorridorLeftTurnPiece(CompoundTag var1) {
         super(StructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_LEFT_TURN, var1);
         this.isNeedingChest = var1.getBoolean("Chest");
      }

      protected void addAdditionalSaveData(StructurePieceSerializationContext var1, CompoundTag var2) {
         super.addAdditionalSaveData(var1, var2);
         var2.putBoolean("Chest", this.isNeedingChest);
      }

      public void addChildren(StructurePiece var1, StructurePieceAccessor var2, Random var3) {
         this.generateChildLeft((NetherBridgePieces.StartPiece)var1, var2, var3, 0, 1, true);
      }

      public static NetherBridgePieces.CastleSmallCorridorLeftTurnPiece createPiece(StructurePieceAccessor var0, Random var1, int var2, int var3, int var4, Direction var5, int var6) {
         BoundingBox var7 = BoundingBox.orientBox(var2, var3, var4, -1, 0, 0, 5, 7, 5, var5);
         return isOkBox(var7) && var0.findCollisionPiece(var7) == null ? new NetherBridgePieces.CastleSmallCorridorLeftTurnPiece(var6, var1, var7, var5) : null;
      }

      public void postProcess(WorldGenLevel var1, StructureFeatureManager var2, ChunkGenerator var3, Random var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
         this.generateBox(var1, var5, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 0, 2, 0, 4, 5, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         BlockState var8 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, true)).setValue(FenceBlock.EAST, true);
         BlockState var9 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, true)).setValue(FenceBlock.SOUTH, true);
         this.generateBox(var1, var5, 4, 2, 0, 4, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 4, 3, 1, 4, 4, 1, var9, var9, false);
         this.generateBox(var1, var5, 4, 3, 3, 4, 4, 3, var9, var9, false);
         this.generateBox(var1, var5, 0, 2, 0, 0, 5, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 0, 2, 4, 3, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 1, 3, 4, 1, 4, 4, var8, var8, false);
         this.generateBox(var1, var5, 3, 3, 4, 3, 4, 4, var8, var8, false);
         if (this.isNeedingChest && var5.isInside(this.getWorldPos(3, 2, 3))) {
            this.isNeedingChest = false;
            this.createChest(var1, var5, var4, 3, 2, 3, BuiltInLootTables.NETHER_BRIDGE);
         }

         this.generateBox(var1, var5, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);

         for(int var10 = 0; var10 <= 4; ++var10) {
            for(int var11 = 0; var11 <= 4; ++var11) {
               this.fillColumnDown(var1, Blocks.NETHER_BRICKS.defaultBlockState(), var10, -1, var11, var5);
            }
         }

      }
   }

   public static class CastleCorridorStairsPiece extends NetherBridgePieces.NetherBridgePiece {
      private static final int WIDTH = 5;
      private static final int HEIGHT = 14;
      private static final int DEPTH = 10;

      public CastleCorridorStairsPiece(int var1, BoundingBox var2, Direction var3) {
         super(StructurePieceType.NETHER_FORTRESS_CASTLE_CORRIDOR_STAIRS, var1, var2);
         this.setOrientation(var3);
      }

      public CastleCorridorStairsPiece(CompoundTag var1) {
         super(StructurePieceType.NETHER_FORTRESS_CASTLE_CORRIDOR_STAIRS, var1);
      }

      public void addChildren(StructurePiece var1, StructurePieceAccessor var2, Random var3) {
         this.generateChildForward((NetherBridgePieces.StartPiece)var1, var2, var3, 1, 0, true);
      }

      public static NetherBridgePieces.CastleCorridorStairsPiece createPiece(StructurePieceAccessor var0, int var1, int var2, int var3, Direction var4, int var5) {
         BoundingBox var6 = BoundingBox.orientBox(var1, var2, var3, -1, -7, 0, 5, 14, 10, var4);
         return isOkBox(var6) && var0.findCollisionPiece(var6) == null ? new NetherBridgePieces.CastleCorridorStairsPiece(var5, var6, var4) : null;
      }

      public void postProcess(WorldGenLevel var1, StructureFeatureManager var2, ChunkGenerator var3, Random var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
         BlockState var8 = (BlockState)Blocks.NETHER_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.SOUTH);
         BlockState var9 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, true)).setValue(FenceBlock.SOUTH, true);

         for(int var10 = 0; var10 <= 9; ++var10) {
            int var11 = Math.max(1, 7 - var10);
            int var12 = Math.min(Math.max(var11 + 5, 14 - var10), 13);
            int var13 = var10;
            this.generateBox(var1, var5, 0, 0, var10, 4, var11, var10, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(var1, var5, 1, var11 + 1, var10, 3, var12 - 1, var10, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            if (var10 <= 6) {
               this.placeBlock(var1, var8, 1, var11 + 1, var10, var5);
               this.placeBlock(var1, var8, 2, var11 + 1, var10, var5);
               this.placeBlock(var1, var8, 3, var11 + 1, var10, var5);
            }

            this.generateBox(var1, var5, 0, var12, var10, 4, var12, var10, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(var1, var5, 0, var11 + 1, var10, 0, var12 - 1, var10, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(var1, var5, 4, var11 + 1, var10, 4, var12 - 1, var10, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            if ((var10 & 1) == 0) {
               this.generateBox(var1, var5, 0, var11 + 2, var10, 0, var11 + 3, var10, var9, var9, false);
               this.generateBox(var1, var5, 4, var11 + 2, var10, 4, var11 + 3, var10, var9, var9, false);
            }

            for(int var14 = 0; var14 <= 4; ++var14) {
               this.fillColumnDown(var1, Blocks.NETHER_BRICKS.defaultBlockState(), var14, -1, var13, var5);
            }
         }

      }
   }

   public static class CastleCorridorTBalconyPiece extends NetherBridgePieces.NetherBridgePiece {
      private static final int WIDTH = 9;
      private static final int HEIGHT = 7;
      private static final int DEPTH = 9;

      public CastleCorridorTBalconyPiece(int var1, BoundingBox var2, Direction var3) {
         super(StructurePieceType.NETHER_FORTRESS_CASTLE_CORRIDOR_T_BALCONY, var1, var2);
         this.setOrientation(var3);
      }

      public CastleCorridorTBalconyPiece(CompoundTag var1) {
         super(StructurePieceType.NETHER_FORTRESS_CASTLE_CORRIDOR_T_BALCONY, var1);
      }

      public void addChildren(StructurePiece var1, StructurePieceAccessor var2, Random var3) {
         byte var4 = 1;
         Direction var5 = this.getOrientation();
         if (var5 == Direction.WEST || var5 == Direction.NORTH) {
            var4 = 5;
         }

         this.generateChildLeft((NetherBridgePieces.StartPiece)var1, var2, var3, 0, var4, var3.nextInt(8) > 0);
         this.generateChildRight((NetherBridgePieces.StartPiece)var1, var2, var3, 0, var4, var3.nextInt(8) > 0);
      }

      public static NetherBridgePieces.CastleCorridorTBalconyPiece createPiece(StructurePieceAccessor var0, int var1, int var2, int var3, Direction var4, int var5) {
         BoundingBox var6 = BoundingBox.orientBox(var1, var2, var3, -3, 0, 0, 9, 7, 9, var4);
         return isOkBox(var6) && var0.findCollisionPiece(var6) == null ? new NetherBridgePieces.CastleCorridorTBalconyPiece(var5, var6, var4) : null;
      }

      public void postProcess(WorldGenLevel var1, StructureFeatureManager var2, ChunkGenerator var3, Random var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
         BlockState var8 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, true)).setValue(FenceBlock.SOUTH, true);
         BlockState var9 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, true)).setValue(FenceBlock.EAST, true);
         this.generateBox(var1, var5, 0, 0, 0, 8, 1, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 0, 2, 0, 8, 5, 8, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(var1, var5, 0, 6, 0, 8, 6, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 0, 2, 0, 2, 5, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 6, 2, 0, 8, 5, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 1, 3, 0, 1, 4, 0, var9, var9, false);
         this.generateBox(var1, var5, 7, 3, 0, 7, 4, 0, var9, var9, false);
         this.generateBox(var1, var5, 0, 2, 4, 8, 2, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 1, 1, 4, 2, 2, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(var1, var5, 6, 1, 4, 7, 2, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(var1, var5, 1, 3, 8, 7, 3, 8, var9, var9, false);
         this.placeBlock(var1, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.EAST, true)).setValue(FenceBlock.SOUTH, true), 0, 3, 8, var5);
         this.placeBlock(var1, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, true)).setValue(FenceBlock.SOUTH, true), 8, 3, 8, var5);
         this.generateBox(var1, var5, 0, 3, 6, 0, 3, 7, var8, var8, false);
         this.generateBox(var1, var5, 8, 3, 6, 8, 3, 7, var8, var8, false);
         this.generateBox(var1, var5, 0, 3, 4, 0, 5, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 8, 3, 4, 8, 5, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 1, 3, 5, 2, 5, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 6, 3, 5, 7, 5, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 1, 4, 5, 1, 5, 5, var9, var9, false);
         this.generateBox(var1, var5, 7, 4, 5, 7, 5, 5, var9, var9, false);

         for(int var10 = 0; var10 <= 5; ++var10) {
            for(int var11 = 0; var11 <= 8; ++var11) {
               this.fillColumnDown(var1, Blocks.NETHER_BRICKS.defaultBlockState(), var11, -1, var10, var5);
            }
         }

      }
   }

   public static class CastleSmallCorridorCrossingPiece extends NetherBridgePieces.NetherBridgePiece {
      private static final int WIDTH = 5;
      private static final int HEIGHT = 7;
      private static final int DEPTH = 5;

      public CastleSmallCorridorCrossingPiece(int var1, BoundingBox var2, Direction var3) {
         super(StructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_CROSSING, var1, var2);
         this.setOrientation(var3);
      }

      public CastleSmallCorridorCrossingPiece(CompoundTag var1) {
         super(StructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_CROSSING, var1);
      }

      public void addChildren(StructurePiece var1, StructurePieceAccessor var2, Random var3) {
         this.generateChildForward((NetherBridgePieces.StartPiece)var1, var2, var3, 1, 0, true);
         this.generateChildLeft((NetherBridgePieces.StartPiece)var1, var2, var3, 0, 1, true);
         this.generateChildRight((NetherBridgePieces.StartPiece)var1, var2, var3, 0, 1, true);
      }

      public static NetherBridgePieces.CastleSmallCorridorCrossingPiece createPiece(StructurePieceAccessor var0, int var1, int var2, int var3, Direction var4, int var5) {
         BoundingBox var6 = BoundingBox.orientBox(var1, var2, var3, -1, 0, 0, 5, 7, 5, var4);
         return isOkBox(var6) && var0.findCollisionPiece(var6) == null ? new NetherBridgePieces.CastleSmallCorridorCrossingPiece(var5, var6, var4) : null;
      }

      public void postProcess(WorldGenLevel var1, StructureFeatureManager var2, ChunkGenerator var3, Random var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
         this.generateBox(var1, var5, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 0, 2, 0, 4, 5, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(var1, var5, 0, 2, 0, 0, 5, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 4, 2, 0, 4, 5, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 0, 2, 4, 0, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 4, 2, 4, 4, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);

         for(int var8 = 0; var8 <= 4; ++var8) {
            for(int var9 = 0; var9 <= 4; ++var9) {
               this.fillColumnDown(var1, Blocks.NETHER_BRICKS.defaultBlockState(), var8, -1, var9, var5);
            }
         }

      }
   }

   public static class CastleStalkRoom extends NetherBridgePieces.NetherBridgePiece {
      private static final int WIDTH = 13;
      private static final int HEIGHT = 14;
      private static final int DEPTH = 13;

      public CastleStalkRoom(int var1, BoundingBox var2, Direction var3) {
         super(StructurePieceType.NETHER_FORTRESS_CASTLE_STALK_ROOM, var1, var2);
         this.setOrientation(var3);
      }

      public CastleStalkRoom(CompoundTag var1) {
         super(StructurePieceType.NETHER_FORTRESS_CASTLE_STALK_ROOM, var1);
      }

      public void addChildren(StructurePiece var1, StructurePieceAccessor var2, Random var3) {
         this.generateChildForward((NetherBridgePieces.StartPiece)var1, var2, var3, 5, 3, true);
         this.generateChildForward((NetherBridgePieces.StartPiece)var1, var2, var3, 5, 11, true);
      }

      public static NetherBridgePieces.CastleStalkRoom createPiece(StructurePieceAccessor var0, int var1, int var2, int var3, Direction var4, int var5) {
         BoundingBox var6 = BoundingBox.orientBox(var1, var2, var3, -5, -3, 0, 13, 14, 13, var4);
         return isOkBox(var6) && var0.findCollisionPiece(var6) == null ? new NetherBridgePieces.CastleStalkRoom(var5, var6, var4) : null;
      }

      public void postProcess(WorldGenLevel var1, StructureFeatureManager var2, ChunkGenerator var3, Random var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
         this.generateBox(var1, var5, 0, 3, 0, 12, 4, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 0, 5, 0, 12, 13, 12, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(var1, var5, 0, 5, 0, 1, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 11, 5, 0, 12, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 2, 5, 11, 4, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 8, 5, 11, 10, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 5, 9, 11, 7, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 2, 5, 0, 4, 12, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 8, 5, 0, 10, 12, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 5, 9, 0, 7, 12, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 2, 11, 2, 10, 12, 10, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         BlockState var8 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, true)).setValue(FenceBlock.EAST, true);
         BlockState var9 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, true)).setValue(FenceBlock.SOUTH, true);
         BlockState var10 = (BlockState)var9.setValue(FenceBlock.WEST, true);
         BlockState var11 = (BlockState)var9.setValue(FenceBlock.EAST, true);

         int var12;
         for(var12 = 1; var12 <= 11; var12 += 2) {
            this.generateBox(var1, var5, var12, 10, 0, var12, 11, 0, var8, var8, false);
            this.generateBox(var1, var5, var12, 10, 12, var12, 11, 12, var8, var8, false);
            this.generateBox(var1, var5, 0, 10, var12, 0, 11, var12, var9, var9, false);
            this.generateBox(var1, var5, 12, 10, var12, 12, 11, var12, var9, var9, false);
            this.placeBlock(var1, Blocks.NETHER_BRICKS.defaultBlockState(), var12, 13, 0, var5);
            this.placeBlock(var1, Blocks.NETHER_BRICKS.defaultBlockState(), var12, 13, 12, var5);
            this.placeBlock(var1, Blocks.NETHER_BRICKS.defaultBlockState(), 0, 13, var12, var5);
            this.placeBlock(var1, Blocks.NETHER_BRICKS.defaultBlockState(), 12, 13, var12, var5);
            if (var12 != 11) {
               this.placeBlock(var1, var8, var12 + 1, 13, 0, var5);
               this.placeBlock(var1, var8, var12 + 1, 13, 12, var5);
               this.placeBlock(var1, var9, 0, 13, var12 + 1, var5);
               this.placeBlock(var1, var9, 12, 13, var12 + 1, var5);
            }
         }

         this.placeBlock(var1, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, true)).setValue(FenceBlock.EAST, true), 0, 13, 0, var5);
         this.placeBlock(var1, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.SOUTH, true)).setValue(FenceBlock.EAST, true), 0, 13, 12, var5);
         this.placeBlock(var1, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.SOUTH, true)).setValue(FenceBlock.WEST, true), 12, 13, 12, var5);
         this.placeBlock(var1, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, true)).setValue(FenceBlock.WEST, true), 12, 13, 0, var5);

         for(var12 = 3; var12 <= 9; var12 += 2) {
            this.generateBox(var1, var5, 1, 7, var12, 1, 8, var12, var10, var10, false);
            this.generateBox(var1, var5, 11, 7, var12, 11, 8, var12, var11, var11, false);
         }

         BlockState var17 = (BlockState)Blocks.NETHER_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.NORTH);

         int var13;
         int var15;
         for(var13 = 0; var13 <= 6; ++var13) {
            int var14 = var13 + 4;

            for(var15 = 5; var15 <= 7; ++var15) {
               this.placeBlock(var1, var17, var15, 5 + var13, var14, var5);
            }

            if (var14 >= 5 && var14 <= 8) {
               this.generateBox(var1, var5, 5, 5, var14, 7, var13 + 4, var14, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            } else if (var14 >= 9 && var14 <= 10) {
               this.generateBox(var1, var5, 5, 8, var14, 7, var13 + 4, var14, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            }

            if (var13 >= 1) {
               this.generateBox(var1, var5, 5, 6 + var13, var14, 7, 9 + var13, var14, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            }
         }

         for(var13 = 5; var13 <= 7; ++var13) {
            this.placeBlock(var1, var17, var13, 12, 11, var5);
         }

         this.generateBox(var1, var5, 5, 6, 7, 5, 7, 7, var11, var11, false);
         this.generateBox(var1, var5, 7, 6, 7, 7, 7, 7, var10, var10, false);
         this.generateBox(var1, var5, 5, 13, 12, 7, 13, 12, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(var1, var5, 2, 5, 2, 3, 5, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 2, 5, 9, 3, 5, 10, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 2, 5, 4, 2, 5, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 9, 5, 2, 10, 5, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 9, 5, 9, 10, 5, 10, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 10, 5, 4, 10, 5, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         BlockState var18 = (BlockState)var17.setValue(StairBlock.FACING, Direction.EAST);
         BlockState var19 = (BlockState)var17.setValue(StairBlock.FACING, Direction.WEST);
         this.placeBlock(var1, var19, 4, 5, 2, var5);
         this.placeBlock(var1, var19, 4, 5, 3, var5);
         this.placeBlock(var1, var19, 4, 5, 9, var5);
         this.placeBlock(var1, var19, 4, 5, 10, var5);
         this.placeBlock(var1, var18, 8, 5, 2, var5);
         this.placeBlock(var1, var18, 8, 5, 3, var5);
         this.placeBlock(var1, var18, 8, 5, 9, var5);
         this.placeBlock(var1, var18, 8, 5, 10, var5);
         this.generateBox(var1, var5, 3, 4, 4, 4, 4, 8, Blocks.SOUL_SAND.defaultBlockState(), Blocks.SOUL_SAND.defaultBlockState(), false);
         this.generateBox(var1, var5, 8, 4, 4, 9, 4, 8, Blocks.SOUL_SAND.defaultBlockState(), Blocks.SOUL_SAND.defaultBlockState(), false);
         this.generateBox(var1, var5, 3, 5, 4, 4, 5, 8, Blocks.NETHER_WART.defaultBlockState(), Blocks.NETHER_WART.defaultBlockState(), false);
         this.generateBox(var1, var5, 8, 5, 4, 9, 5, 8, Blocks.NETHER_WART.defaultBlockState(), Blocks.NETHER_WART.defaultBlockState(), false);
         this.generateBox(var1, var5, 4, 2, 0, 8, 2, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 0, 2, 4, 12, 2, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 4, 0, 0, 8, 1, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 4, 0, 9, 8, 1, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 0, 0, 4, 3, 1, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 9, 0, 4, 12, 1, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);

         int var16;
         for(var15 = 4; var15 <= 8; ++var15) {
            for(var16 = 0; var16 <= 2; ++var16) {
               this.fillColumnDown(var1, Blocks.NETHER_BRICKS.defaultBlockState(), var15, -1, var16, var5);
               this.fillColumnDown(var1, Blocks.NETHER_BRICKS.defaultBlockState(), var15, -1, 12 - var16, var5);
            }
         }

         for(var15 = 0; var15 <= 2; ++var15) {
            for(var16 = 4; var16 <= 8; ++var16) {
               this.fillColumnDown(var1, Blocks.NETHER_BRICKS.defaultBlockState(), var15, -1, var16, var5);
               this.fillColumnDown(var1, Blocks.NETHER_BRICKS.defaultBlockState(), 12 - var15, -1, var16, var5);
            }
         }

      }
   }

   public static class BridgeEndFiller extends NetherBridgePieces.NetherBridgePiece {
      private static final int WIDTH = 5;
      private static final int HEIGHT = 10;
      private static final int DEPTH = 8;
      private final int selfSeed;

      public BridgeEndFiller(int var1, Random var2, BoundingBox var3, Direction var4) {
         super(StructurePieceType.NETHER_FORTRESS_BRIDGE_END_FILLER, var1, var3);
         this.setOrientation(var4);
         this.selfSeed = var2.nextInt();
      }

      public BridgeEndFiller(CompoundTag var1) {
         super(StructurePieceType.NETHER_FORTRESS_BRIDGE_END_FILLER, var1);
         this.selfSeed = var1.getInt("Seed");
      }

      public static NetherBridgePieces.BridgeEndFiller createPiece(StructurePieceAccessor var0, Random var1, int var2, int var3, int var4, Direction var5, int var6) {
         BoundingBox var7 = BoundingBox.orientBox(var2, var3, var4, -1, -3, 0, 5, 10, 8, var5);
         return isOkBox(var7) && var0.findCollisionPiece(var7) == null ? new NetherBridgePieces.BridgeEndFiller(var6, var1, var7, var5) : null;
      }

      protected void addAdditionalSaveData(StructurePieceSerializationContext var1, CompoundTag var2) {
         super.addAdditionalSaveData(var1, var2);
         var2.putInt("Seed", this.selfSeed);
      }

      public void postProcess(WorldGenLevel var1, StructureFeatureManager var2, ChunkGenerator var3, Random var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
         Random var8 = new Random((long)this.selfSeed);

         int var9;
         int var10;
         int var11;
         for(var9 = 0; var9 <= 4; ++var9) {
            for(var10 = 3; var10 <= 4; ++var10) {
               var11 = var8.nextInt(8);
               this.generateBox(var1, var5, var9, var10, 0, var9, var10, var11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            }
         }

         var9 = var8.nextInt(8);
         this.generateBox(var1, var5, 0, 5, 0, 0, 5, var9, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         var9 = var8.nextInt(8);
         this.generateBox(var1, var5, 4, 5, 0, 4, 5, var9, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);

         for(var9 = 0; var9 <= 4; ++var9) {
            var10 = var8.nextInt(5);
            this.generateBox(var1, var5, var9, 2, 0, var9, 2, var10, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         }

         for(var9 = 0; var9 <= 4; ++var9) {
            for(var10 = 0; var10 <= 1; ++var10) {
               var11 = var8.nextInt(3);
               this.generateBox(var1, var5, var9, var10, 0, var9, var10, var11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            }
         }

      }
   }

   public static class StartPiece extends NetherBridgePieces.BridgeCrossing {
      public NetherBridgePieces.PieceWeight previousPiece;
      public List<NetherBridgePieces.PieceWeight> availableBridgePieces;
      public List<NetherBridgePieces.PieceWeight> availableCastlePieces;
      public final List<StructurePiece> pendingChildren = Lists.newArrayList();

      public StartPiece(Random var1, int var2, int var3) {
         super(var2, var3, getRandomHorizontalDirection(var1));
         this.availableBridgePieces = Lists.newArrayList();
         NetherBridgePieces.PieceWeight[] var4 = NetherBridgePieces.BRIDGE_PIECE_WEIGHTS;
         int var5 = var4.length;

         int var6;
         NetherBridgePieces.PieceWeight var7;
         for(var6 = 0; var6 < var5; ++var6) {
            var7 = var4[var6];
            var7.placeCount = 0;
            this.availableBridgePieces.add(var7);
         }

         this.availableCastlePieces = Lists.newArrayList();
         var4 = NetherBridgePieces.CASTLE_PIECE_WEIGHTS;
         var5 = var4.length;

         for(var6 = 0; var6 < var5; ++var6) {
            var7 = var4[var6];
            var7.placeCount = 0;
            this.availableCastlePieces.add(var7);
         }

      }

      public StartPiece(CompoundTag var1) {
         super(StructurePieceType.NETHER_FORTRESS_START, var1);
      }
   }

   private abstract static class NetherBridgePiece extends StructurePiece {
      protected NetherBridgePiece(StructurePieceType var1, int var2, BoundingBox var3) {
         super(var1, var2, var3);
      }

      public NetherBridgePiece(StructurePieceType var1, CompoundTag var2) {
         super(var1, var2);
      }

      protected void addAdditionalSaveData(StructurePieceSerializationContext var1, CompoundTag var2) {
      }

      private int updatePieceWeight(List<NetherBridgePieces.PieceWeight> var1) {
         boolean var2 = false;
         int var3 = 0;

         NetherBridgePieces.PieceWeight var5;
         for(Iterator var4 = var1.iterator(); var4.hasNext(); var3 += var5.weight) {
            var5 = (NetherBridgePieces.PieceWeight)var4.next();
            if (var5.maxPlaceCount > 0 && var5.placeCount < var5.maxPlaceCount) {
               var2 = true;
            }
         }

         return var2 ? var3 : -1;
      }

      private NetherBridgePieces.NetherBridgePiece generatePiece(NetherBridgePieces.StartPiece var1, List<NetherBridgePieces.PieceWeight> var2, StructurePieceAccessor var3, Random var4, int var5, int var6, int var7, Direction var8, int var9) {
         int var10 = this.updatePieceWeight(var2);
         boolean var11 = var10 > 0 && var9 <= 30;
         int var12 = 0;

         while(var12 < 5 && var11) {
            ++var12;
            int var13 = var4.nextInt(var10);
            Iterator var14 = var2.iterator();

            while(var14.hasNext()) {
               NetherBridgePieces.PieceWeight var15 = (NetherBridgePieces.PieceWeight)var14.next();
               var13 -= var15.weight;
               if (var13 < 0) {
                  if (!var15.doPlace(var9) || var15 == var1.previousPiece && !var15.allowInRow) {
                     break;
                  }

                  NetherBridgePieces.NetherBridgePiece var16 = NetherBridgePieces.findAndCreateBridgePieceFactory(var15, var3, var4, var5, var6, var7, var8, var9);
                  if (var16 != null) {
                     ++var15.placeCount;
                     var1.previousPiece = var15;
                     if (!var15.isValid()) {
                        var2.remove(var15);
                     }

                     return var16;
                  }
               }
            }
         }

         return NetherBridgePieces.BridgeEndFiller.createPiece(var3, var4, var5, var6, var7, var8, var9);
      }

      private StructurePiece generateAndAddPiece(NetherBridgePieces.StartPiece var1, StructurePieceAccessor var2, Random var3, int var4, int var5, int var6, @Nullable Direction var7, int var8, boolean var9) {
         if (Math.abs(var4 - var1.getBoundingBox().minX()) <= 112 && Math.abs(var6 - var1.getBoundingBox().minZ()) <= 112) {
            List var10 = var1.availableBridgePieces;
            if (var9) {
               var10 = var1.availableCastlePieces;
            }

            NetherBridgePieces.NetherBridgePiece var11 = this.generatePiece(var1, var10, var2, var3, var4, var5, var6, var7, var8 + 1);
            if (var11 != null) {
               var2.addPiece(var11);
               var1.pendingChildren.add(var11);
            }

            return var11;
         } else {
            return NetherBridgePieces.BridgeEndFiller.createPiece(var2, var3, var4, var5, var6, var7, var8);
         }
      }

      @Nullable
      protected StructurePiece generateChildForward(NetherBridgePieces.StartPiece var1, StructurePieceAccessor var2, Random var3, int var4, int var5, boolean var6) {
         Direction var7 = this.getOrientation();
         if (var7 != null) {
            switch(var7) {
            case NORTH:
               return this.generateAndAddPiece(var1, var2, var3, this.boundingBox.minX() + var4, this.boundingBox.minY() + var5, this.boundingBox.minZ() - 1, var7, this.getGenDepth(), var6);
            case SOUTH:
               return this.generateAndAddPiece(var1, var2, var3, this.boundingBox.minX() + var4, this.boundingBox.minY() + var5, this.boundingBox.maxZ() + 1, var7, this.getGenDepth(), var6);
            case WEST:
               return this.generateAndAddPiece(var1, var2, var3, this.boundingBox.minX() - 1, this.boundingBox.minY() + var5, this.boundingBox.minZ() + var4, var7, this.getGenDepth(), var6);
            case EAST:
               return this.generateAndAddPiece(var1, var2, var3, this.boundingBox.maxX() + 1, this.boundingBox.minY() + var5, this.boundingBox.minZ() + var4, var7, this.getGenDepth(), var6);
            }
         }

         return null;
      }

      @Nullable
      protected StructurePiece generateChildLeft(NetherBridgePieces.StartPiece var1, StructurePieceAccessor var2, Random var3, int var4, int var5, boolean var6) {
         Direction var7 = this.getOrientation();
         if (var7 != null) {
            switch(var7) {
            case NORTH:
               return this.generateAndAddPiece(var1, var2, var3, this.boundingBox.minX() - 1, this.boundingBox.minY() + var4, this.boundingBox.minZ() + var5, Direction.WEST, this.getGenDepth(), var6);
            case SOUTH:
               return this.generateAndAddPiece(var1, var2, var3, this.boundingBox.minX() - 1, this.boundingBox.minY() + var4, this.boundingBox.minZ() + var5, Direction.WEST, this.getGenDepth(), var6);
            case WEST:
               return this.generateAndAddPiece(var1, var2, var3, this.boundingBox.minX() + var5, this.boundingBox.minY() + var4, this.boundingBox.minZ() - 1, Direction.NORTH, this.getGenDepth(), var6);
            case EAST:
               return this.generateAndAddPiece(var1, var2, var3, this.boundingBox.minX() + var5, this.boundingBox.minY() + var4, this.boundingBox.minZ() - 1, Direction.NORTH, this.getGenDepth(), var6);
            }
         }

         return null;
      }

      @Nullable
      protected StructurePiece generateChildRight(NetherBridgePieces.StartPiece var1, StructurePieceAccessor var2, Random var3, int var4, int var5, boolean var6) {
         Direction var7 = this.getOrientation();
         if (var7 != null) {
            switch(var7) {
            case NORTH:
               return this.generateAndAddPiece(var1, var2, var3, this.boundingBox.maxX() + 1, this.boundingBox.minY() + var4, this.boundingBox.minZ() + var5, Direction.EAST, this.getGenDepth(), var6);
            case SOUTH:
               return this.generateAndAddPiece(var1, var2, var3, this.boundingBox.maxX() + 1, this.boundingBox.minY() + var4, this.boundingBox.minZ() + var5, Direction.EAST, this.getGenDepth(), var6);
            case WEST:
               return this.generateAndAddPiece(var1, var2, var3, this.boundingBox.minX() + var5, this.boundingBox.minY() + var4, this.boundingBox.maxZ() + 1, Direction.SOUTH, this.getGenDepth(), var6);
            case EAST:
               return this.generateAndAddPiece(var1, var2, var3, this.boundingBox.minX() + var5, this.boundingBox.minY() + var4, this.boundingBox.maxZ() + 1, Direction.SOUTH, this.getGenDepth(), var6);
            }
         }

         return null;
      }

      protected static boolean isOkBox(BoundingBox var0) {
         return var0 != null && var0.minY() > 10;
      }
   }
}
