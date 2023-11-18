package net.minecraft.world.level.levelgen.structure.pools;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.mutable.MutableObject;
import org.slf4j.Logger;

public class JigsawPlacement {
   static final Logger LOGGER = LogUtils.getLogger();

   public JigsawPlacement() {
      super();
   }

   public static Optional<Structure.GenerationStub> addPieces(
      Structure.GenerationContext var0,
      Holder<StructureTemplatePool> var1,
      Optional<ResourceLocation> var2,
      int var3,
      BlockPos var4,
      boolean var5,
      Optional<Heightmap.Types> var6,
      int var7
   ) {
      RegistryAccess var8 = var0.registryAccess();
      ChunkGenerator var9 = var0.chunkGenerator();
      StructureTemplateManager var10 = var0.structureTemplateManager();
      LevelHeightAccessor var11 = var0.heightAccessor();
      WorldgenRandom var12 = var0.random();
      Registry var13 = var8.registryOrThrow(Registries.TEMPLATE_POOL);
      Rotation var14 = Rotation.getRandom(var12);
      StructureTemplatePool var15 = (StructureTemplatePool)var1.value();
      StructurePoolElement var16 = var15.getRandomTemplate(var12);
      if (var16 == EmptyPoolElement.INSTANCE) {
         return Optional.empty();
      } else {
         BlockPos var17;
         if (var2.isPresent()) {
            ResourceLocation var18 = (ResourceLocation)var2.get();
            Optional var19 = getRandomNamedJigsaw(var16, var18, var4, var14, var10, var12);
            if (var19.isEmpty()) {
               LOGGER.error(
                  "No starting jigsaw {} found in start pool {}", var18, var1.unwrapKey().map(var0x -> var0x.location().toString()).orElse("<unregistered>")
               );
               return Optional.empty();
            }

            var17 = (BlockPos)var19.get();
         } else {
            var17 = var4;
         }

         BlockPos var27 = var17.subtract(var4);
         BlockPos var28 = var4.subtract(var27);
         PoolElementStructurePiece var20 = new PoolElementStructurePiece(
            var10, var16, var28, var16.getGroundLevelDelta(), var14, var16.getBoundingBox(var10, var28, var14)
         );
         BoundingBox var21 = var20.getBoundingBox();
         int var22 = (var21.maxX() + var21.minX()) / 2;
         int var23 = (var21.maxZ() + var21.minZ()) / 2;
         int var24;
         if (var6.isPresent()) {
            var24 = var4.getY() + var9.getFirstFreeHeight(var22, var23, (Heightmap.Types)var6.get(), var11, var0.randomState());
         } else {
            var24 = var28.getY();
         }

         int var25 = var21.minY() + var20.getGroundLevelDelta();
         var20.move(0, var24 - var25, 0);
         int var26 = var24 + var27.getY();
         return Optional.of(
            new Structure.GenerationStub(
               new BlockPos(var22, var26, var23),
               (Consumer<StructurePiecesBuilder>)(var14x -> {
                  ArrayList var15x = Lists.newArrayList();
                  var15x.add(var20);
                  if (var3 > 0) {
                     AABB var16x = new AABB(
                        (double)(var22 - var7),
                        (double)(var26 - var7),
                        (double)(var23 - var7),
                        (double)(var22 + var7 + 1),
                        (double)(var26 + var7 + 1),
                        (double)(var23 + var7 + 1)
                     );
                     VoxelShape var17x = Shapes.join(Shapes.create(var16x), Shapes.create(AABB.of(var21)), BooleanOp.ONLY_FIRST);
                     addPieces(var0.randomState(), var3, var5, var9, var10, var11, var12, var13, var20, var15x, var17x);
                     var15x.forEach(var14x::addPiece);
                  }
               })
            )
         );
      }
   }

   private static Optional<BlockPos> getRandomNamedJigsaw(
      StructurePoolElement var0, ResourceLocation var1, BlockPos var2, Rotation var3, StructureTemplateManager var4, WorldgenRandom var5
   ) {
      List var6 = var0.getShuffledJigsawBlocks(var4, var2, var3, var5);
      Optional var7 = Optional.empty();

      for(StructureTemplate.StructureBlockInfo var9 : var6) {
         ResourceLocation var10 = ResourceLocation.tryParse(var9.nbt().getString("name"));
         if (var1.equals(var10)) {
            var7 = Optional.of(var9.pos());
            break;
         }
      }

      return var7;
   }

   private static void addPieces(
      RandomState var0,
      int var1,
      boolean var2,
      ChunkGenerator var3,
      StructureTemplateManager var4,
      LevelHeightAccessor var5,
      RandomSource var6,
      Registry<StructureTemplatePool> var7,
      PoolElementStructurePiece var8,
      List<PoolElementStructurePiece> var9,
      VoxelShape var10
   ) {
      JigsawPlacement.Placer var11 = new JigsawPlacement.Placer(var7, var1, var3, var4, var9, var6);
      var11.placing.addLast(new JigsawPlacement.PieceState(var8, new MutableObject(var10), 0));

      while(!var11.placing.isEmpty()) {
         JigsawPlacement.PieceState var12 = var11.placing.removeFirst();
         var11.tryPlacingChildren(var12.piece, var12.free, var12.depth, var2, var5, var0);
      }
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static boolean generateJigsaw(ServerLevel var0, Holder<StructureTemplatePool> var1, ResourceLocation var2, int var3, BlockPos var4, boolean var5) {
      ChunkGenerator var6 = var0.getChunkSource().getGenerator();
      StructureTemplateManager var7 = var0.getStructureManager();
      StructureManager var8 = var0.structureManager();
      RandomSource var9 = var0.getRandom();
      Structure.GenerationContext var10 = new Structure.GenerationContext(
         var0.registryAccess(),
         var6,
         var6.getBiomeSource(),
         var0.getChunkSource().randomState(),
         var7,
         var0.getSeed(),
         new ChunkPos(var4),
         var0,
         var0x -> true
      );
      Optional var11 = addPieces(var10, var1, Optional.of(var2), var3, var4, false, Optional.empty(), 128);
      if (var11.isPresent()) {
         StructurePiecesBuilder var12 = ((Structure.GenerationStub)var11.get()).getPiecesBuilder();

         for(StructurePiece var14 : var12.build().pieces()) {
            if (var14 instanceof PoolElementStructurePiece var15) {
               var15.place(var0, var8, var6, var9, BoundingBox.infinite(), var4, var5);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   static final class PieceState {
      final PoolElementStructurePiece piece;
      final MutableObject<VoxelShape> free;
      final int depth;

      PieceState(PoolElementStructurePiece var1, MutableObject<VoxelShape> var2, int var3) {
         super();
         this.piece = var1;
         this.free = var2;
         this.depth = var3;
      }
   }

   static final class Placer {
      private final Registry<StructureTemplatePool> pools;
      private final int maxDepth;
      private final ChunkGenerator chunkGenerator;
      private final StructureTemplateManager structureTemplateManager;
      private final List<? super PoolElementStructurePiece> pieces;
      private final RandomSource random;
      final Deque<JigsawPlacement.PieceState> placing = Queues.newArrayDeque();

      Placer(
         Registry<StructureTemplatePool> var1,
         int var2,
         ChunkGenerator var3,
         StructureTemplateManager var4,
         List<? super PoolElementStructurePiece> var5,
         RandomSource var6
      ) {
         super();
         this.pools = var1;
         this.maxDepth = var2;
         this.chunkGenerator = var3;
         this.structureTemplateManager = var4;
         this.pieces = var5;
         this.random = var6;
      }

      void tryPlacingChildren(
         PoolElementStructurePiece var1, MutableObject<VoxelShape> var2, int var3, boolean var4, LevelHeightAccessor var5, RandomState var6
      ) {
         StructurePoolElement var7 = var1.getElement();
         BlockPos var8 = var1.getPosition();
         Rotation var9 = var1.getRotation();
         StructureTemplatePool.Projection var10 = var7.getProjection();
         boolean var11 = var10 == StructureTemplatePool.Projection.RIGID;
         MutableObject var12 = new MutableObject();
         BoundingBox var13 = var1.getBoundingBox();
         int var14 = var13.minY();

         label129:
         for(StructureTemplate.StructureBlockInfo var16 : var7.getShuffledJigsawBlocks(this.structureTemplateManager, var8, var9, this.random)) {
            Direction var17 = JigsawBlock.getFrontFacing(var16.state());
            BlockPos var18 = var16.pos();
            BlockPos var19 = var18.relative(var17);
            int var20 = var18.getY() - var14;
            int var21 = -1;
            ResourceKey var22 = readPoolName(var16);
            Optional var23 = this.pools.getHolder(var22);
            if (var23.isEmpty()) {
               JigsawPlacement.LOGGER.warn("Empty or non-existent pool: {}", var22.location());
            } else {
               Holder var24 = (Holder)var23.get();
               if (((StructureTemplatePool)var24.value()).size() == 0 && !var24.is(Pools.EMPTY)) {
                  JigsawPlacement.LOGGER.warn("Empty or non-existent pool: {}", var22.location());
               } else {
                  Holder var25 = ((StructureTemplatePool)var24.value()).getFallback();
                  if (((StructureTemplatePool)var25.value()).size() == 0 && !var25.is(Pools.EMPTY)) {
                     JigsawPlacement.LOGGER
                        .warn("Empty or non-existent fallback pool: {}", var25.unwrapKey().map(var0 -> var0.location().toString()).orElse("<unregistered>"));
                  } else {
                     boolean var27 = var13.isInside(var19);
                     MutableObject var26;
                     if (var27) {
                        var26 = var12;
                        if (var12.getValue() == null) {
                           var12.setValue(Shapes.create(AABB.of(var13)));
                        }
                     } else {
                        var26 = var2;
                     }

                     ArrayList var28 = Lists.newArrayList();
                     if (var3 != this.maxDepth) {
                        var28.addAll(((StructureTemplatePool)var24.value()).getShuffledTemplates(this.random));
                     }

                     var28.addAll(((StructureTemplatePool)var25.value()).getShuffledTemplates(this.random));

                     for(StructurePoolElement var30 : var28) {
                        if (var30 == EmptyPoolElement.INSTANCE) {
                           break;
                        }

                        for(Rotation var32 : Rotation.getShuffled(this.random)) {
                           List var33 = var30.getShuffledJigsawBlocks(this.structureTemplateManager, BlockPos.ZERO, var32, this.random);
                           BoundingBox var34 = var30.getBoundingBox(this.structureTemplateManager, BlockPos.ZERO, var32);
                           int var35;
                           if (var4 && var34.getYSpan() <= 16) {
                              var35 = var33.stream()
                                 .mapToInt(
                                    var2x -> {
                                       if (!var34.isInside(var2x.pos().relative(JigsawBlock.getFrontFacing(var2x.state())))) {
                                          return 0;
                                       } else {
                                          ResourceKey var3x = readPoolName(var2x);
                                          Optional var4x = this.pools.getHolder(var3x);
                                          Optional var5x = var4x.map(var0 -> ((StructureTemplatePool)var0.value()).getFallback());
                                          int var6x = var4x.<Integer>map(
                                                var1xx -> ((StructureTemplatePool)var1xx.value()).getMaxSize(this.structureTemplateManager)
                                             )
                                             .orElse(0);
                                          int var7x = var5x.<Integer>map(
                                                var1xx -> ((StructureTemplatePool)var1xx.value()).getMaxSize(this.structureTemplateManager)
                                             )
                                             .orElse(0);
                                          return Math.max(var6x, var7x);
                                       }
                                    }
                                 )
                                 .max()
                                 .orElse(0);
                           } else {
                              var35 = 0;
                           }

                           for(StructureTemplate.StructureBlockInfo var37 : var33) {
                              if (JigsawBlock.canAttach(var16, var37)) {
                                 BlockPos var38 = var37.pos();
                                 BlockPos var39 = var19.subtract(var38);
                                 BoundingBox var40 = var30.getBoundingBox(this.structureTemplateManager, var39, var32);
                                 int var41 = var40.minY();
                                 StructureTemplatePool.Projection var42 = var30.getProjection();
                                 boolean var43 = var42 == StructureTemplatePool.Projection.RIGID;
                                 int var44 = var38.getY();
                                 int var45 = var20 - var44 + JigsawBlock.getFrontFacing(var16.state()).getStepY();
                                 int var46;
                                 if (var11 && var43) {
                                    var46 = var14 + var45;
                                 } else {
                                    if (var21 == -1) {
                                       var21 = this.chunkGenerator
                                          .getFirstFreeHeight(var18.getX(), var18.getZ(), Heightmap.Types.WORLD_SURFACE_WG, var5, var6);
                                    }

                                    var46 = var21 - var44;
                                 }

                                 int var47 = var46 - var41;
                                 BoundingBox var48 = var40.moved(0, var47, 0);
                                 BlockPos var49 = var39.offset(0, var47, 0);
                                 if (var35 > 0) {
                                    int var50 = Math.max(var35 + 1, var48.maxY() - var48.minY());
                                    var48.encapsulate(new BlockPos(var48.minX(), var48.minY() + var50, var48.minZ()));
                                 }

                                 if (!Shapes.joinIsNotEmpty((VoxelShape)var26.getValue(), Shapes.create(AABB.of(var48).deflate(0.25)), BooleanOp.ONLY_SECOND)) {
                                    var26.setValue(Shapes.joinUnoptimized((VoxelShape)var26.getValue(), Shapes.create(AABB.of(var48)), BooleanOp.ONLY_FIRST));
                                    int var54 = var1.getGroundLevelDelta();
                                    int var51;
                                    if (var43) {
                                       var51 = var54 - var45;
                                    } else {
                                       var51 = var30.getGroundLevelDelta();
                                    }

                                    PoolElementStructurePiece var52 = new PoolElementStructurePiece(
                                       this.structureTemplateManager, var30, var49, var51, var32, var48
                                    );
                                    int var53;
                                    if (var11) {
                                       var53 = var14 + var20;
                                    } else if (var43) {
                                       var53 = var46 + var44;
                                    } else {
                                       if (var21 == -1) {
                                          var21 = this.chunkGenerator
                                             .getFirstFreeHeight(var18.getX(), var18.getZ(), Heightmap.Types.WORLD_SURFACE_WG, var5, var6);
                                       }

                                       var53 = var21 + var45 / 2;
                                    }

                                    var1.addJunction(new JigsawJunction(var19.getX(), var53 - var20 + var54, var19.getZ(), var45, var42));
                                    var52.addJunction(new JigsawJunction(var18.getX(), var53 - var44 + var51, var18.getZ(), -var45, var10));
                                    this.pieces.add(var52);
                                    if (var3 + 1 <= this.maxDepth) {
                                       this.placing.addLast(new JigsawPlacement.PieceState(var52, var26, var3 + 1));
                                    }
                                    continue label129;
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      private static ResourceKey<StructureTemplatePool> readPoolName(StructureTemplate.StructureBlockInfo var0) {
         return ResourceKey.create(Registries.TEMPLATE_POOL, new ResourceLocation(var0.nbt().getString("pool")));
      }
   }
}
