package net.minecraft.world.level.levelgen.feature.structures;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JigsawPlacement {
   static final Logger LOGGER = LogManager.getLogger();

   public JigsawPlacement() {
      super();
   }

   public static Optional<PieceGenerator<JigsawConfiguration>> addPieces(PieceGeneratorSupplier.Context<JigsawConfiguration> var0, JigsawPlacement.PieceFactory var1, BlockPos var2, boolean var3, boolean var4) {
      WorldgenRandom var5 = new WorldgenRandom(new LegacyRandomSource(0L));
      var5.setLargeFeatureSeed(var0.seed(), var0.chunkPos().field_504, var0.chunkPos().field_505);
      RegistryAccess var6 = var0.registryAccess();
      JigsawConfiguration var7 = (JigsawConfiguration)var0.config();
      ChunkGenerator var8 = var0.chunkGenerator();
      StructureManager var9 = var0.structureManager();
      LevelHeightAccessor var10 = var0.heightAccessor();
      Predicate var11 = var0.validBiome();
      StructureFeature.bootstrap();
      Registry var12 = var6.registryOrThrow(Registry.TEMPLATE_POOL_REGISTRY);
      Rotation var13 = Rotation.getRandom(var5);
      StructureTemplatePool var14 = (StructureTemplatePool)var7.startPool().get();
      StructurePoolElement var15 = var14.getRandomTemplate(var5);
      if (var15 == EmptyPoolElement.INSTANCE) {
         return Optional.empty();
      } else {
         PoolElementStructurePiece var16 = var1.create(var9, var15, var2, var15.getGroundLevelDelta(), var13, var15.getBoundingBox(var9, var2, var13));
         BoundingBox var17 = var16.getBoundingBox();
         int var18 = (var17.maxX() + var17.minX()) / 2;
         int var19 = (var17.maxZ() + var17.minZ()) / 2;
         int var20;
         if (var4) {
            var20 = var2.getY() + var8.getFirstFreeHeight(var18, var19, Heightmap.Types.WORLD_SURFACE_WG, var10);
         } else {
            var20 = var2.getY();
         }

         if (!var11.test(var8.getNoiseBiome(QuartPos.fromBlock(var18), QuartPos.fromBlock(var20), QuartPos.fromBlock(var19)))) {
            return Optional.empty();
         } else {
            int var21 = var17.minY() + var16.getGroundLevelDelta();
            var16.move(0, var20 - var21, 0);
            return Optional.of((var13x, var14x) -> {
               ArrayList var15 = Lists.newArrayList();
               var15.add(var16);
               if (var7.maxDepth() > 0) {
                  boolean var16x = true;
                  AABB var17x = new AABB((double)(var18 - 80), (double)(var20 - 80), (double)(var19 - 80), (double)(var18 + 80 + 1), (double)(var20 + 80 + 1), (double)(var19 + 80 + 1));
                  JigsawPlacement.Placer var18x = new JigsawPlacement.Placer(var12, var7.maxDepth(), var1, var8, var9, var15, var5);
                  var18x.placing.addLast(new JigsawPlacement.PieceState(var16, new MutableObject(Shapes.join(Shapes.create(var17x), Shapes.create(AABB.method_92(var17)), BooleanOp.ONLY_FIRST)), 0));

                  while(!var18x.placing.isEmpty()) {
                     JigsawPlacement.PieceState var19x = (JigsawPlacement.PieceState)var18x.placing.removeFirst();
                     var18x.tryPlacingChildren(var19x.piece, var19x.free, var19x.depth, var3, var10);
                  }

                  Objects.requireNonNull(var13x);
                  var15.forEach(var13x::addPiece);
               }
            });
         }
      }
   }

   public static void addPieces(RegistryAccess var0, PoolElementStructurePiece var1, int var2, JigsawPlacement.PieceFactory var3, ChunkGenerator var4, StructureManager var5, List<? super PoolElementStructurePiece> var6, Random var7, LevelHeightAccessor var8) {
      Registry var9 = var0.registryOrThrow(Registry.TEMPLATE_POOL_REGISTRY);
      JigsawPlacement.Placer var10 = new JigsawPlacement.Placer(var9, var2, var3, var4, var5, var6, var7);
      var10.placing.addLast(new JigsawPlacement.PieceState(var1, new MutableObject(Shapes.INFINITY), 0));

      while(!var10.placing.isEmpty()) {
         JigsawPlacement.PieceState var11 = (JigsawPlacement.PieceState)var10.placing.removeFirst();
         var10.tryPlacingChildren(var11.piece, var11.free, var11.depth, false, var8);
      }

   }

   public interface PieceFactory {
      PoolElementStructurePiece create(StructureManager var1, StructurePoolElement var2, BlockPos var3, int var4, Rotation var5, BoundingBox var6);
   }

   static final class Placer {
      private final Registry<StructureTemplatePool> pools;
      private final int maxDepth;
      private final JigsawPlacement.PieceFactory factory;
      private final ChunkGenerator chunkGenerator;
      private final StructureManager structureManager;
      private final List<? super PoolElementStructurePiece> pieces;
      private final Random random;
      final Deque<JigsawPlacement.PieceState> placing = Queues.newArrayDeque();

      Placer(Registry<StructureTemplatePool> var1, int var2, JigsawPlacement.PieceFactory var3, ChunkGenerator var4, StructureManager var5, List<? super PoolElementStructurePiece> var6, Random var7) {
         super();
         this.pools = var1;
         this.maxDepth = var2;
         this.factory = var3;
         this.chunkGenerator = var4;
         this.structureManager = var5;
         this.pieces = var6;
         this.random = var7;
      }

      void tryPlacingChildren(PoolElementStructurePiece var1, MutableObject<VoxelShape> var2, int var3, boolean var4, LevelHeightAccessor var5) {
         StructurePoolElement var6 = var1.getElement();
         BlockPos var7 = var1.getPosition();
         Rotation var8 = var1.getRotation();
         StructureTemplatePool.Projection var9 = var6.getProjection();
         boolean var10 = var9 == StructureTemplatePool.Projection.RIGID;
         MutableObject var11 = new MutableObject();
         BoundingBox var12 = var1.getBoundingBox();
         int var13 = var12.minY();
         Iterator var14 = var6.getShuffledJigsawBlocks(this.structureManager, var7, var8, this.random).iterator();

         while(true) {
            while(true) {
               while(true) {
                  label93:
                  while(var14.hasNext()) {
                     StructureTemplate.StructureBlockInfo var15 = (StructureTemplate.StructureBlockInfo)var14.next();
                     Direction var16 = JigsawBlock.getFrontFacing(var15.state);
                     BlockPos var17 = var15.pos;
                     BlockPos var18 = var17.relative(var16);
                     int var19 = var17.getY() - var13;
                     int var20 = -1;
                     ResourceLocation var21 = new ResourceLocation(var15.nbt.getString("pool"));
                     Optional var22 = this.pools.getOptional(var21);
                     if (var22.isPresent() && (((StructureTemplatePool)var22.get()).size() != 0 || Objects.equals(var21, Pools.EMPTY.location()))) {
                        ResourceLocation var23 = ((StructureTemplatePool)var22.get()).getFallback();
                        Optional var24 = this.pools.getOptional(var23);
                        if (var24.isPresent() && (((StructureTemplatePool)var24.get()).size() != 0 || Objects.equals(var23, Pools.EMPTY.location()))) {
                           boolean var26 = var12.isInside(var18);
                           MutableObject var25;
                           if (var26) {
                              var25 = var11;
                              if (var11.getValue() == null) {
                                 var11.setValue(Shapes.create(AABB.method_92(var12)));
                              }
                           } else {
                              var25 = var2;
                           }

                           ArrayList var27 = Lists.newArrayList();
                           if (var3 != this.maxDepth) {
                              var27.addAll(((StructureTemplatePool)var22.get()).getShuffledTemplates(this.random));
                           }

                           var27.addAll(((StructureTemplatePool)var24.get()).getShuffledTemplates(this.random));
                           Iterator var28 = var27.iterator();

                           while(var28.hasNext()) {
                              StructurePoolElement var29 = (StructurePoolElement)var28.next();
                              if (var29 == EmptyPoolElement.INSTANCE) {
                                 break;
                              }

                              Iterator var30 = Rotation.getShuffled(this.random).iterator();

                              label133:
                              while(var30.hasNext()) {
                                 Rotation var31 = (Rotation)var30.next();
                                 List var32 = var29.getShuffledJigsawBlocks(this.structureManager, BlockPos.ZERO, var31, this.random);
                                 BoundingBox var33 = var29.getBoundingBox(this.structureManager, BlockPos.ZERO, var31);
                                 int var34;
                                 if (var4 && var33.getYSpan() <= 16) {
                                    var34 = var32.stream().mapToInt((var2x) -> {
                                       if (!var33.isInside(var2x.pos.relative(JigsawBlock.getFrontFacing(var2x.state)))) {
                                          return 0;
                                       } else {
                                          ResourceLocation var3 = new ResourceLocation(var2x.nbt.getString("pool"));
                                          Optional var4 = this.pools.getOptional(var3);
                                          Optional var5 = var4.flatMap((var1) -> {
                                             return this.pools.getOptional(var1.getFallback());
                                          });
                                          int var6 = (Integer)var4.map((var1) -> {
                                             return var1.getMaxSize(this.structureManager);
                                          }).orElse(0);
                                          int var7 = (Integer)var5.map((var1) -> {
                                             return var1.getMaxSize(this.structureManager);
                                          }).orElse(0);
                                          return Math.max(var6, var7);
                                       }
                                    }).max().orElse(0);
                                 } else {
                                    var34 = 0;
                                 }

                                 Iterator var35 = var32.iterator();

                                 StructureTemplatePool.Projection var41;
                                 boolean var42;
                                 int var43;
                                 int var44;
                                 int var45;
                                 BoundingBox var47;
                                 BlockPos var48;
                                 int var49;
                                 do {
                                    StructureTemplate.StructureBlockInfo var36;
                                    do {
                                       if (!var35.hasNext()) {
                                          continue label133;
                                       }

                                       var36 = (StructureTemplate.StructureBlockInfo)var35.next();
                                    } while(!JigsawBlock.canAttach(var15, var36));

                                    BlockPos var37 = var36.pos;
                                    BlockPos var38 = var18.subtract(var37);
                                    BoundingBox var39 = var29.getBoundingBox(this.structureManager, var38, var31);
                                    int var40 = var39.minY();
                                    var41 = var29.getProjection();
                                    var42 = var41 == StructureTemplatePool.Projection.RIGID;
                                    var43 = var37.getY();
                                    var44 = var19 - var43 + JigsawBlock.getFrontFacing(var15.state).getStepY();
                                    if (var10 && var42) {
                                       var45 = var13 + var44;
                                    } else {
                                       if (var20 == -1) {
                                          var20 = this.chunkGenerator.getFirstFreeHeight(var17.getX(), var17.getZ(), Heightmap.Types.WORLD_SURFACE_WG, var5);
                                       }

                                       var45 = var20 - var43;
                                    }

                                    int var46 = var45 - var40;
                                    var47 = var39.moved(0, var46, 0);
                                    var48 = var38.offset(0, var46, 0);
                                    if (var34 > 0) {
                                       var49 = Math.max(var34 + 1, var47.maxY() - var47.minY());
                                       var47.encapsulate(new BlockPos(var47.minX(), var47.minY() + var49, var47.minZ()));
                                    }
                                 } while(Shapes.joinIsNotEmpty((VoxelShape)var25.getValue(), Shapes.create(AABB.method_92(var47).deflate(0.25D)), BooleanOp.ONLY_SECOND));

                                 var25.setValue(Shapes.joinUnoptimized((VoxelShape)var25.getValue(), Shapes.create(AABB.method_92(var47)), BooleanOp.ONLY_FIRST));
                                 var49 = var1.getGroundLevelDelta();
                                 int var50;
                                 if (var42) {
                                    var50 = var49 - var44;
                                 } else {
                                    var50 = var29.getGroundLevelDelta();
                                 }

                                 PoolElementStructurePiece var51 = this.factory.create(this.structureManager, var29, var48, var50, var31, var47);
                                 int var52;
                                 if (var10) {
                                    var52 = var13 + var19;
                                 } else if (var42) {
                                    var52 = var45 + var43;
                                 } else {
                                    if (var20 == -1) {
                                       var20 = this.chunkGenerator.getFirstFreeHeight(var17.getX(), var17.getZ(), Heightmap.Types.WORLD_SURFACE_WG, var5);
                                    }

                                    var52 = var20 + var44 / 2;
                                 }

                                 var1.addJunction(new JigsawJunction(var18.getX(), var52 - var19 + var49, var18.getZ(), var44, var41));
                                 var51.addJunction(new JigsawJunction(var17.getX(), var52 - var43 + var50, var17.getZ(), -var44, var9));
                                 this.pieces.add(var51);
                                 if (var3 + 1 <= this.maxDepth) {
                                    this.placing.addLast(new JigsawPlacement.PieceState(var51, var25, var3 + 1));
                                 }
                                 continue label93;
                              }
                           }
                        } else {
                           JigsawPlacement.LOGGER.warn("Empty or non-existent fallback pool: {}", var23);
                        }
                     } else {
                        JigsawPlacement.LOGGER.warn("Empty or non-existent pool: {}", var21);
                     }
                  }

                  return;
               }
            }
         }
      }
   }

   private static final class PieceState {
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
}
