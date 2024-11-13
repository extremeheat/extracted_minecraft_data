package net.minecraft.world.level.levelgen.structure.pools;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.SequencedPriorityIterator;
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
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
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
   private static final int UNSET_HEIGHT = -2147483648;

   public JigsawPlacement() {
      super();
   }

   public static Optional<Structure.GenerationStub> addPieces(Structure.GenerationContext var0, Holder<StructureTemplatePool> var1, Optional<ResourceLocation> var2, int var3, BlockPos var4, boolean var5, Optional<Heightmap.Types> var6, int var7, PoolAliasLookup var8, DimensionPadding var9, LiquidSettings var10) {
      RegistryAccess var11 = var0.registryAccess();
      ChunkGenerator var12 = var0.chunkGenerator();
      StructureTemplateManager var13 = var0.structureTemplateManager();
      LevelHeightAccessor var14 = var0.heightAccessor();
      WorldgenRandom var15 = var0.random();
      Registry var16 = var11.lookupOrThrow(Registries.TEMPLATE_POOL);
      Rotation var17 = Rotation.getRandom(var15);
      StructureTemplatePool var18 = (StructureTemplatePool)var1.unwrapKey().flatMap((var2x) -> var16.getOptional(var8.lookup(var2x))).orElse((StructureTemplatePool)var1.value());
      StructurePoolElement var19 = var18.getRandomTemplate(var15);
      if (var19 == EmptyPoolElement.INSTANCE) {
         return Optional.empty();
      } else {
         BlockPos var20;
         if (var2.isPresent()) {
            ResourceLocation var21 = (ResourceLocation)var2.get();
            Optional var22 = getRandomNamedJigsaw(var19, var21, var4, var17, var13, var15);
            if (var22.isEmpty()) {
               LOGGER.error("No starting jigsaw {} found in start pool {}", var21, var1.unwrapKey().map((var0x) -> var0x.location().toString()).orElse("<unregistered>"));
               return Optional.empty();
            }

            var20 = (BlockPos)var22.get();
         } else {
            var20 = var4;
         }

         BlockPos var30 = var20.subtract(var4);
         BlockPos var31 = var4.subtract(var30);
         PoolElementStructurePiece var23 = new PoolElementStructurePiece(var13, var19, var31, var19.getGroundLevelDelta(), var17, var19.getBoundingBox(var13, var31, var17), var10);
         BoundingBox var24 = var23.getBoundingBox();
         int var25 = (var24.maxX() + var24.minX()) / 2;
         int var26 = (var24.maxZ() + var24.minZ()) / 2;
         int var27 = var6.isEmpty() ? var31.getY() : var4.getY() + var12.getFirstFreeHeight(var25, var26, (Heightmap.Types)var6.get(), var14, var0.randomState());
         int var28 = var24.minY() + var23.getGroundLevelDelta();
         var23.move(0, var27 - var28, 0);
         if (isStartTooCloseToWorldHeightLimits(var14, var9, var23.getBoundingBox())) {
            LOGGER.debug("Center piece {} with bounding box {} does not fit dimension padding {}", new Object[]{var19, var23.getBoundingBox(), var9});
            return Optional.empty();
         } else {
            int var29 = var27 + ((Vec3i)var30).getY();
            return Optional.of(new Structure.GenerationStub(new BlockPos(var25, var29, var26), (var17x) -> {
               ArrayList var18 = Lists.newArrayList();
               var18.add(var23);
               if (var3 > 0) {
                  AABB var19 = new AABB((double)(var25 - var7), (double)Math.max(var29 - var7, var14.getMinY() + var9.bottom()), (double)(var26 - var7), (double)(var25 + var7 + 1), (double)Math.min(var29 + var7 + 1, var14.getMaxY() + 1 - var9.top()), (double)(var26 + var7 + 1));
                  VoxelShape var20 = Shapes.join(Shapes.create(var19), Shapes.create(AABB.of(var24)), BooleanOp.ONLY_FIRST);
                  addPieces(var0.randomState(), var3, var5, var12, var13, var14, var15, var16, var23, var18, var20, var8, var10);
                  Objects.requireNonNull(var17x);
                  var18.forEach(var17x::addPiece);
               }
            }));
         }
      }
   }

   private static boolean isStartTooCloseToWorldHeightLimits(LevelHeightAccessor var0, DimensionPadding var1, BoundingBox var2) {
      if (var1 == DimensionPadding.ZERO) {
         return false;
      } else {
         int var3 = var0.getMinY() + var1.bottom();
         int var4 = var0.getMaxY() - var1.top();
         return var2.minY() < var3 || var2.maxY() > var4;
      }
   }

   private static Optional<BlockPos> getRandomNamedJigsaw(StructurePoolElement var0, ResourceLocation var1, BlockPos var2, Rotation var3, StructureTemplateManager var4, WorldgenRandom var5) {
      for(StructureTemplate.JigsawBlockInfo var8 : var0.getShuffledJigsawBlocks(var4, var2, var3, var5)) {
         if (var1.equals(var8.name())) {
            return Optional.of(var8.info().pos());
         }
      }

      return Optional.empty();
   }

   private static void addPieces(RandomState var0, int var1, boolean var2, ChunkGenerator var3, StructureTemplateManager var4, LevelHeightAccessor var5, RandomSource var6, Registry<StructureTemplatePool> var7, PoolElementStructurePiece var8, List<PoolElementStructurePiece> var9, VoxelShape var10, PoolAliasLookup var11, LiquidSettings var12) {
      Placer var13 = new Placer(var7, var1, var3, var4, var9, var6);
      var13.tryPlacingChildren(var8, new MutableObject(var10), 0, var2, var5, var0, var11, var12);

      while(var13.placing.hasNext()) {
         PieceState var14 = (PieceState)var13.placing.next();
         var13.tryPlacingChildren(var14.piece, var14.free, var14.depth, var2, var5, var0, var11, var12);
      }

   }

   public static boolean generateJigsaw(ServerLevel var0, Holder<StructureTemplatePool> var1, ResourceLocation var2, int var3, BlockPos var4, boolean var5) {
      ChunkGenerator var6 = var0.getChunkSource().getGenerator();
      StructureTemplateManager var7 = var0.getStructureManager();
      StructureManager var8 = var0.structureManager();
      RandomSource var9 = var0.getRandom();
      Structure.GenerationContext var10 = new Structure.GenerationContext(var0.registryAccess(), var6, var6.getBiomeSource(), var0.getChunkSource().randomState(), var7, var0.getSeed(), new ChunkPos(var4), var0, (var0x) -> true);
      Optional var11 = addPieces(var10, var1, Optional.of(var2), var3, var4, false, Optional.empty(), 128, PoolAliasLookup.EMPTY, JigsawStructure.DEFAULT_DIMENSION_PADDING, JigsawStructure.DEFAULT_LIQUID_SETTINGS);
      if (var11.isPresent()) {
         StructurePiecesBuilder var12 = ((Structure.GenerationStub)var11.get()).getPiecesBuilder();

         for(StructurePiece var14 : var12.build().pieces()) {
            if (var14 instanceof PoolElementStructurePiece) {
               PoolElementStructurePiece var15 = (PoolElementStructurePiece)var14;
               var15.place(var0, var8, var6, var9, BoundingBox.infinite(), var4, var5);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   static record PieceState(PoolElementStructurePiece piece, MutableObject<VoxelShape> free, int depth) {
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
      final SequencedPriorityIterator<PieceState> placing = new SequencedPriorityIterator<PieceState>();

      Placer(Registry<StructureTemplatePool> var1, int var2, ChunkGenerator var3, StructureTemplateManager var4, List<? super PoolElementStructurePiece> var5, RandomSource var6) {
         super();
         this.pools = var1;
         this.maxDepth = var2;
         this.chunkGenerator = var3;
         this.structureTemplateManager = var4;
         this.pieces = var5;
         this.random = var6;
      }

      void tryPlacingChildren(PoolElementStructurePiece var1, MutableObject<VoxelShape> var2, int var3, boolean var4, LevelHeightAccessor var5, RandomState var6, PoolAliasLookup var7, LiquidSettings var8) {
         StructurePoolElement var9 = var1.getElement();
         BlockPos var10 = var1.getPosition();
         Rotation var11 = var1.getRotation();
         StructureTemplatePool.Projection var12 = var9.getProjection();
         boolean var13 = var12 == StructureTemplatePool.Projection.RIGID;
         MutableObject var14 = new MutableObject();
         BoundingBox var15 = var1.getBoundingBox();
         int var16 = var15.minY();

         label129:
         for(StructureTemplate.JigsawBlockInfo var18 : var9.getShuffledJigsawBlocks(this.structureTemplateManager, var10, var11, this.random)) {
            StructureTemplate.StructureBlockInfo var19 = var18.info();
            Direction var20 = JigsawBlock.getFrontFacing(var19.state());
            BlockPos var21 = var19.pos();
            BlockPos var22 = var21.relative(var20);
            int var23 = var21.getY() - var16;
            int var24 = -2147483648;
            ResourceKey var25 = readPoolKey(var18, var7);
            Optional var26 = this.pools.get(var25);
            if (var26.isEmpty()) {
               JigsawPlacement.LOGGER.warn("Empty or non-existent pool: {}", var25.location());
            } else {
               Holder var27 = (Holder)var26.get();
               if (((StructureTemplatePool)var27.value()).size() == 0 && !var27.is(Pools.EMPTY)) {
                  JigsawPlacement.LOGGER.warn("Empty or non-existent pool: {}", var25.location());
               } else {
                  Holder var28 = ((StructureTemplatePool)var27.value()).getFallback();
                  if (((StructureTemplatePool)var28.value()).size() == 0 && !var28.is(Pools.EMPTY)) {
                     JigsawPlacement.LOGGER.warn("Empty or non-existent fallback pool: {}", var28.unwrapKey().map((var0) -> var0.location().toString()).orElse("<unregistered>"));
                  } else {
                     boolean var30 = var15.isInside(var22);
                     MutableObject var29;
                     if (var30) {
                        var29 = var14;
                        if (var14.getValue() == null) {
                           var14.setValue(Shapes.create(AABB.of(var15)));
                        }
                     } else {
                        var29 = var2;
                     }

                     ArrayList var31 = Lists.newArrayList();
                     if (var3 != this.maxDepth) {
                        var31.addAll(((StructureTemplatePool)var27.value()).getShuffledTemplates(this.random));
                     }

                     var31.addAll(((StructureTemplatePool)var28.value()).getShuffledTemplates(this.random));
                     int var32 = var18.placementPriority();

                     for(StructurePoolElement var34 : var31) {
                        if (var34 == EmptyPoolElement.INSTANCE) {
                           break;
                        }

                        for(Rotation var36 : Rotation.getShuffled(this.random)) {
                           List var37 = var34.getShuffledJigsawBlocks(this.structureTemplateManager, BlockPos.ZERO, var36, this.random);
                           BoundingBox var38 = var34.getBoundingBox(this.structureTemplateManager, BlockPos.ZERO, var36);
                           int var39;
                           if (var4 && var38.getYSpan() <= 16) {
                              var39 = var37.stream().mapToInt((var3x) -> {
                                 StructureTemplate.StructureBlockInfo var4 = var3x.info();
                                 if (!var38.isInside(var4.pos().relative(JigsawBlock.getFrontFacing(var4.state())))) {
                                    return 0;
                                 } else {
                                    ResourceKey var5 = readPoolKey(var3x, var7);
                                    Optional var6 = this.pools.get(var5);
                                    Optional var7x = var6.map((var0) -> ((StructureTemplatePool)var0.value()).getFallback());
                                    int var8 = (Integer)var6.map((var1) -> ((StructureTemplatePool)var1.value()).getMaxSize(this.structureTemplateManager)).orElse(0);
                                    int var9 = (Integer)var7x.map((var1) -> ((StructureTemplatePool)var1.value()).getMaxSize(this.structureTemplateManager)).orElse(0);
                                    return Math.max(var8, var9);
                                 }
                              }).max().orElse(0);
                           } else {
                              var39 = 0;
                           }

                           for(StructureTemplate.JigsawBlockInfo var41 : var37) {
                              if (JigsawBlock.canAttach(var18, var41)) {
                                 BlockPos var42 = var41.info().pos();
                                 BlockPos var43 = var22.subtract(var42);
                                 BoundingBox var44 = var34.getBoundingBox(this.structureTemplateManager, var43, var36);
                                 int var45 = var44.minY();
                                 StructureTemplatePool.Projection var46 = var34.getProjection();
                                 boolean var47 = var46 == StructureTemplatePool.Projection.RIGID;
                                 int var48 = var42.getY();
                                 int var49 = var23 - var48 + JigsawBlock.getFrontFacing(var19.state()).getStepY();
                                 int var50;
                                 if (var13 && var47) {
                                    var50 = var16 + var49;
                                 } else {
                                    if (var24 == -2147483648) {
                                       var24 = this.chunkGenerator.getFirstFreeHeight(var21.getX(), var21.getZ(), Heightmap.Types.WORLD_SURFACE_WG, var5, var6);
                                    }

                                    var50 = var24 - var48;
                                 }

                                 int var51 = var50 - var45;
                                 BoundingBox var52 = var44.moved(0, var51, 0);
                                 BlockPos var53 = var43.offset(0, var51, 0);
                                 if (var39 > 0) {
                                    int var54 = Math.max(var39 + 1, var52.maxY() - var52.minY());
                                    var52.encapsulate(new BlockPos(var52.minX(), var52.minY() + var54, var52.minZ()));
                                 }

                                 if (!Shapes.joinIsNotEmpty((VoxelShape)var29.getValue(), Shapes.create(AABB.of(var52).deflate(0.25)), BooleanOp.ONLY_SECOND)) {
                                    var29.setValue(Shapes.joinUnoptimized((VoxelShape)var29.getValue(), Shapes.create(AABB.of(var52)), BooleanOp.ONLY_FIRST));
                                    int var59 = var1.getGroundLevelDelta();
                                    int var55;
                                    if (var47) {
                                       var55 = var59 - var49;
                                    } else {
                                       var55 = var34.getGroundLevelDelta();
                                    }

                                    PoolElementStructurePiece var56 = new PoolElementStructurePiece(this.structureTemplateManager, var34, var53, var55, var36, var52, var8);
                                    int var57;
                                    if (var13) {
                                       var57 = var16 + var23;
                                    } else if (var47) {
                                       var57 = var50 + var48;
                                    } else {
                                       if (var24 == -2147483648) {
                                          var24 = this.chunkGenerator.getFirstFreeHeight(var21.getX(), var21.getZ(), Heightmap.Types.WORLD_SURFACE_WG, var5, var6);
                                       }

                                       var57 = var24 + var49 / 2;
                                    }

                                    var1.addJunction(new JigsawJunction(var22.getX(), var57 - var23 + var59, var22.getZ(), var49, var46));
                                    var56.addJunction(new JigsawJunction(var21.getX(), var57 - var48 + var55, var21.getZ(), -var49, var12));
                                    this.pieces.add(var56);
                                    if (var3 + 1 <= this.maxDepth) {
                                       PieceState var58 = new PieceState(var56, var29, var3 + 1);
                                       this.placing.add(var58, var32);
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

      private static ResourceKey<StructureTemplatePool> readPoolKey(StructureTemplate.JigsawBlockInfo var0, PoolAliasLookup var1) {
         return var1.lookup(Pools.createKey(var0.pool()));
      }
   }
}
