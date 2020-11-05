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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
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
   private static final Logger LOGGER = LogManager.getLogger();

   public static void addPieces(RegistryAccess var0, JigsawConfiguration var1, JigsawPlacement.PieceFactory var2, ChunkGenerator var3, StructureManager var4, BlockPos var5, List<? super PoolElementStructurePiece> var6, Random var7, boolean var8, boolean var9) {
      StructureFeature.bootstrap();
      WritableRegistry var10 = var0.registryOrThrow(Registry.TEMPLATE_POOL_REGISTRY);
      Rotation var11 = Rotation.getRandom(var7);
      StructureTemplatePool var12 = (StructureTemplatePool)var1.startPool().get();
      StructurePoolElement var13 = var12.getRandomTemplate(var7);
      PoolElementStructurePiece var14 = var2.create(var4, var13, var5, var13.getGroundLevelDelta(), var11, var13.getBoundingBox(var4, var5, var11));
      BoundingBox var15 = var14.getBoundingBox();
      int var16 = (var15.x1 + var15.x0) / 2;
      int var17 = (var15.z1 + var15.z0) / 2;
      int var18;
      if (var9) {
         var18 = var5.getY() + var3.getFirstFreeHeight(var16, var17, Heightmap.Types.WORLD_SURFACE_WG);
      } else {
         var18 = var5.getY();
      }

      int var19 = var15.y0 + var14.getGroundLevelDelta();
      var14.move(0, var18 - var19, 0);
      var6.add(var14);
      if (var1.maxDepth() > 0) {
         boolean var20 = true;
         AABB var21 = new AABB((double)(var16 - 80), (double)(var18 - 80), (double)(var17 - 80), (double)(var16 + 80 + 1), (double)(var18 + 80 + 1), (double)(var17 + 80 + 1));
         JigsawPlacement.Placer var22 = new JigsawPlacement.Placer(var10, var1.maxDepth(), var2, var3, var4, var6, var7);
         var22.placing.addLast(new JigsawPlacement.PieceState(var14, new MutableObject(Shapes.join(Shapes.create(var21), Shapes.create(AABB.of(var15)), BooleanOp.ONLY_FIRST)), var18 + 80, 0));

         while(!var22.placing.isEmpty()) {
            JigsawPlacement.PieceState var23 = (JigsawPlacement.PieceState)var22.placing.removeFirst();
            var22.tryPlacingChildren(var23.piece, var23.free, var23.boundsTop, var23.depth, var8);
         }

      }
   }

   public static void addPieces(RegistryAccess var0, PoolElementStructurePiece var1, int var2, JigsawPlacement.PieceFactory var3, ChunkGenerator var4, StructureManager var5, List<? super PoolElementStructurePiece> var6, Random var7) {
      WritableRegistry var8 = var0.registryOrThrow(Registry.TEMPLATE_POOL_REGISTRY);
      JigsawPlacement.Placer var9 = new JigsawPlacement.Placer(var8, var2, var3, var4, var5, var6, var7);
      var9.placing.addLast(new JigsawPlacement.PieceState(var1, new MutableObject(Shapes.INFINITY), 0, 0));

      while(!var9.placing.isEmpty()) {
         JigsawPlacement.PieceState var10 = (JigsawPlacement.PieceState)var9.placing.removeFirst();
         var9.tryPlacingChildren(var10.piece, var10.free, var10.boundsTop, var10.depth, false);
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
      private final Deque<JigsawPlacement.PieceState> placing;

      private Placer(Registry<StructureTemplatePool> var1, int var2, JigsawPlacement.PieceFactory var3, ChunkGenerator var4, StructureManager var5, List<? super PoolElementStructurePiece> var6, Random var7) {
         super();
         this.placing = Queues.newArrayDeque();
         this.pools = var1;
         this.maxDepth = var2;
         this.factory = var3;
         this.chunkGenerator = var4;
         this.structureManager = var5;
         this.pieces = var6;
         this.random = var7;
      }

      private void tryPlacingChildren(PoolElementStructurePiece var1, MutableObject<VoxelShape> var2, int var3, int var4, boolean var5) {
         StructurePoolElement var6 = var1.getElement();
         BlockPos var7 = var1.getPosition();
         Rotation var8 = var1.getRotation();
         StructureTemplatePool.Projection var9 = var6.getProjection();
         boolean var10 = var9 == StructureTemplatePool.Projection.RIGID;
         MutableObject var11 = new MutableObject();
         BoundingBox var12 = var1.getBoundingBox();
         int var13 = var12.y0;
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
                           boolean var27 = var12.isInside(var18);
                           MutableObject var25;
                           int var26;
                           if (var27) {
                              var25 = var11;
                              var26 = var13;
                              if (var11.getValue() == null) {
                                 var11.setValue(Shapes.create(AABB.of(var12)));
                              }
                           } else {
                              var25 = var2;
                              var26 = var3;
                           }

                           ArrayList var28 = Lists.newArrayList();
                           if (var4 != this.maxDepth) {
                              var28.addAll(((StructureTemplatePool)var22.get()).getShuffledTemplates(this.random));
                           }

                           var28.addAll(((StructureTemplatePool)var24.get()).getShuffledTemplates(this.random));
                           Iterator var29 = var28.iterator();

                           while(var29.hasNext()) {
                              StructurePoolElement var30 = (StructurePoolElement)var29.next();
                              if (var30 == EmptyPoolElement.INSTANCE) {
                                 break;
                              }

                              Iterator var31 = Rotation.getShuffled(this.random).iterator();

                              label133:
                              while(var31.hasNext()) {
                                 Rotation var32 = (Rotation)var31.next();
                                 List var33 = var30.getShuffledJigsawBlocks(this.structureManager, BlockPos.ZERO, var32, this.random);
                                 BoundingBox var34 = var30.getBoundingBox(this.structureManager, BlockPos.ZERO, var32);
                                 int var35;
                                 if (var5 && var34.getYSpan() <= 16) {
                                    var35 = var33.stream().mapToInt((var2x) -> {
                                       if (!var34.isInside(var2x.pos.relative(JigsawBlock.getFrontFacing(var2x.state)))) {
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
                                    var35 = 0;
                                 }

                                 Iterator var36 = var33.iterator();

                                 StructureTemplatePool.Projection var42;
                                 boolean var43;
                                 int var44;
                                 int var45;
                                 int var46;
                                 BoundingBox var48;
                                 BlockPos var49;
                                 int var50;
                                 do {
                                    StructureTemplate.StructureBlockInfo var37;
                                    do {
                                       if (!var36.hasNext()) {
                                          continue label133;
                                       }

                                       var37 = (StructureTemplate.StructureBlockInfo)var36.next();
                                    } while(!JigsawBlock.canAttach(var15, var37));

                                    BlockPos var38 = var37.pos;
                                    BlockPos var39 = new BlockPos(var18.getX() - var38.getX(), var18.getY() - var38.getY(), var18.getZ() - var38.getZ());
                                    BoundingBox var40 = var30.getBoundingBox(this.structureManager, var39, var32);
                                    int var41 = var40.y0;
                                    var42 = var30.getProjection();
                                    var43 = var42 == StructureTemplatePool.Projection.RIGID;
                                    var44 = var38.getY();
                                    var45 = var19 - var44 + JigsawBlock.getFrontFacing(var15.state).getStepY();
                                    if (var10 && var43) {
                                       var46 = var13 + var45;
                                    } else {
                                       if (var20 == -1) {
                                          var20 = this.chunkGenerator.getFirstFreeHeight(var17.getX(), var17.getZ(), Heightmap.Types.WORLD_SURFACE_WG);
                                       }

                                       var46 = var20 - var44;
                                    }

                                    int var47 = var46 - var41;
                                    var48 = var40.moved(0, var47, 0);
                                    var49 = var39.offset(0, var47, 0);
                                    if (var35 > 0) {
                                       var50 = Math.max(var35 + 1, var48.y1 - var48.y0);
                                       var48.y1 = var48.y0 + var50;
                                    }
                                 } while(Shapes.joinIsNotEmpty((VoxelShape)var25.getValue(), Shapes.create(AABB.of(var48).deflate(0.25D)), BooleanOp.ONLY_SECOND));

                                 var25.setValue(Shapes.joinUnoptimized((VoxelShape)var25.getValue(), Shapes.create(AABB.of(var48)), BooleanOp.ONLY_FIRST));
                                 var50 = var1.getGroundLevelDelta();
                                 int var51;
                                 if (var43) {
                                    var51 = var50 - var45;
                                 } else {
                                    var51 = var30.getGroundLevelDelta();
                                 }

                                 PoolElementStructurePiece var52 = this.factory.create(this.structureManager, var30, var49, var51, var32, var48);
                                 int var53;
                                 if (var10) {
                                    var53 = var13 + var19;
                                 } else if (var43) {
                                    var53 = var46 + var44;
                                 } else {
                                    if (var20 == -1) {
                                       var20 = this.chunkGenerator.getFirstFreeHeight(var17.getX(), var17.getZ(), Heightmap.Types.WORLD_SURFACE_WG);
                                    }

                                    var53 = var20 + var45 / 2;
                                 }

                                 var1.addJunction(new JigsawJunction(var18.getX(), var53 - var19 + var50, var18.getZ(), var45, var42));
                                 var52.addJunction(new JigsawJunction(var17.getX(), var53 - var44 + var51, var17.getZ(), -var45, var9));
                                 this.pieces.add(var52);
                                 if (var4 + 1 <= this.maxDepth) {
                                    this.placing.addLast(new JigsawPlacement.PieceState(var52, var25, var26, var4 + 1));
                                 }
                                 continue label93;
                              }
                           }
                        } else {
                           JigsawPlacement.LOGGER.warn("Empty or none existent fallback pool: {}", var23);
                        }
                     } else {
                        JigsawPlacement.LOGGER.warn("Empty or none existent pool: {}", var21);
                     }
                  }

                  return;
               }
            }
         }
      }

      // $FF: synthetic method
      Placer(Registry var1, int var2, JigsawPlacement.PieceFactory var3, ChunkGenerator var4, StructureManager var5, List var6, Random var7, Object var8) {
         this(var1, var2, var3, var4, var5, var6, var7);
      }
   }

   static final class PieceState {
      private final PoolElementStructurePiece piece;
      private final MutableObject<VoxelShape> free;
      private final int boundsTop;
      private final int depth;

      private PieceState(PoolElementStructurePiece var1, MutableObject<VoxelShape> var2, int var3, int var4) {
         super();
         this.piece = var1;
         this.free = var2;
         this.boundsTop = var3;
         this.depth = var4;
      }

      // $FF: synthetic method
      PieceState(PoolElementStructurePiece var1, MutableObject var2, int var3, int var4, Object var5) {
         this(var1, var2, var3, var4);
      }
   }
}
