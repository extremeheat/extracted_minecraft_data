package net.minecraft.world.level.levelgen.structure.pools;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.nbt.CompoundTag;
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
      int var7,
      PoolAliasLookup var8
   ) {
      RegistryAccess var9 = var0.registryAccess();
      ChunkGenerator var10 = var0.chunkGenerator();
      StructureTemplateManager var11 = var0.structureTemplateManager();
      LevelHeightAccessor var12 = var0.heightAccessor();
      WorldgenRandom var13 = var0.random();
      Registry var14 = var9.registryOrThrow(Registries.TEMPLATE_POOL);
      Rotation var15 = Rotation.getRandom(var13);
      StructureTemplatePool var16 = var1.unwrapKey()
         .flatMap(var2x -> var14.getOptional(var8.lookup((ResourceKey<StructureTemplatePool>)var2x)))
         .orElse((StructureTemplatePool)var1.value());
      StructurePoolElement var17 = var16.getRandomTemplate(var13);
      if (var17 == EmptyPoolElement.INSTANCE) {
         return Optional.empty();
      } else {
         BlockPos var18;
         if (var2.isPresent()) {
            ResourceLocation var19 = (ResourceLocation)var2.get();
            Optional var20 = getRandomNamedJigsaw(var17, var19, var4, var15, var11, var13);
            if (var20.isEmpty()) {
               LOGGER.error(
                  "No starting jigsaw {} found in start pool {}", var19, var1.unwrapKey().map(var0x -> var0x.location().toString()).orElse("<unregistered>")
               );
               return Optional.empty();
            }

            var18 = (BlockPos)var20.get();
         } else {
            var18 = var4;
         }

         BlockPos var28 = var18.subtract(var4);
         BlockPos var29 = var4.subtract(var28);
         PoolElementStructurePiece var21 = new PoolElementStructurePiece(
            var11, var17, var29, var17.getGroundLevelDelta(), var15, var17.getBoundingBox(var11, var29, var15)
         );
         BoundingBox var22 = var21.getBoundingBox();
         int var23 = (var22.maxX() + var22.minX()) / 2;
         int var24 = (var22.maxZ() + var22.minZ()) / 2;
         int var25;
         if (var6.isPresent()) {
            var25 = var4.getY() + var10.getFirstFreeHeight(var23, var24, (Heightmap.Types)var6.get(), var12, var0.randomState());
         } else {
            var25 = var29.getY();
         }

         int var26 = var22.minY() + var21.getGroundLevelDelta();
         var21.move(0, var25 - var26, 0);
         int var27 = var25 + var28.getY();
         return Optional.of(
            new Structure.GenerationStub(
               new BlockPos(var23, var27, var24),
               (Consumer<StructurePiecesBuilder>)(var15x -> {
                  ArrayList var16x = Lists.newArrayList();
                  var16x.add(var21);
                  if (var3 > 0) {
                     AABB var17x = new AABB(
                        (double)(var23 - var7),
                        (double)(var27 - var7),
                        (double)(var24 - var7),
                        (double)(var23 + var7 + 1),
                        (double)(var27 + var7 + 1),
                        (double)(var24 + var7 + 1)
                     );
                     VoxelShape var18x = Shapes.join(Shapes.create(var17x), Shapes.create(AABB.of(var22)), BooleanOp.ONLY_FIRST);
                     addPieces(var0.randomState(), var3, var5, var10, var11, var12, var13, var14, var21, var16x, var18x, var8);
                     var16x.forEach(var15x::addPiece);
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

      for (StructureTemplate.StructureBlockInfo var9 : var6) {
         ResourceLocation var10 = ResourceLocation.tryParse(Objects.requireNonNull(var9.nbt(), () -> var9 + " nbt was null").getString("name"));
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
      VoxelShape var10,
      PoolAliasLookup var11
   ) {
      JigsawPlacement.Placer var12 = new JigsawPlacement.Placer(var7, var1, var3, var4, var9, var6);
      var12.tryPlacingChildren(var8, new MutableObject(var10), 0, var2, var5, var0, var11);

      while (var12.placing.hasNext()) {
         JigsawPlacement.PieceState var13 = (JigsawPlacement.PieceState)var12.placing.next();
         var12.tryPlacingChildren(var13.piece, var13.free, var13.depth, var2, var5, var0, var11);
      }
   }

   public static boolean generateJigsaw(ServerLevel var0, Holder<StructureTemplatePool> var1, ResourceLocation var2, int var3, BlockPos var4, boolean var5) {
      ChunkGenerator var6 = var0.getChunkSource().getGenerator();
      StructureTemplateManager var7 = var0.getStructureManager();
      StructureManager var8 = var0.structureManager();
      RandomSource var9 = var0.getRandom();
      Structure.GenerationContext var10 = new Structure.GenerationContext(
         var0.registryAccess(), var6, var6.getBiomeSource(), var0.getChunkSource().randomState(), var7, var0.getSeed(), new ChunkPos(var4), var0, var0x -> true
      );
      Optional var11 = addPieces(var10, var1, Optional.of(var2), var3, var4, false, Optional.empty(), 128, PoolAliasLookup.EMPTY);
      if (var11.isPresent()) {
         StructurePiecesBuilder var12 = ((Structure.GenerationStub)var11.get()).getPiecesBuilder();

         for (StructurePiece var14 : var12.build().pieces()) {
            if (var14 instanceof PoolElementStructurePiece var15) {
               var15.place(var0, var8, var6, var9, BoundingBox.infinite(), var4, var5);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   static record PieceState(PoolElementStructurePiece piece, MutableObject<VoxelShape> free, int depth) {

      PieceState(PoolElementStructurePiece piece, MutableObject<VoxelShape> free, int depth) {
         super();
         this.piece = piece;
         this.free = free;
         this.depth = depth;
      }
   }

   static final class Placer {
      private final Registry<StructureTemplatePool> pools;
      private final int maxDepth;
      private final ChunkGenerator chunkGenerator;
      private final StructureTemplateManager structureTemplateManager;
      private final List<? super PoolElementStructurePiece> pieces;
      private final RandomSource random;
      final SequencedPriorityIterator<JigsawPlacement.PieceState> placing = new SequencedPriorityIterator<>();

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
         PoolElementStructurePiece var1,
         MutableObject<VoxelShape> var2,
         int var3,
         boolean var4,
         LevelHeightAccessor var5,
         RandomState var6,
         PoolAliasLookup var7
      ) {
         StructurePoolElement var8 = var1.getElement();
         BlockPos var9 = var1.getPosition();
         Rotation var10 = var1.getRotation();
         StructureTemplatePool.Projection var11 = var8.getProjection();
         boolean var12 = var11 == StructureTemplatePool.Projection.RIGID;
         MutableObject var13 = new MutableObject();
         BoundingBox var14 = var1.getBoundingBox();
         int var15 = var14.minY();

         label134:
         for (StructureTemplate.StructureBlockInfo var17 : var8.getShuffledJigsawBlocks(this.structureTemplateManager, var9, var10, this.random)) {
            Direction var18 = JigsawBlock.getFrontFacing(var17.state());
            BlockPos var19 = var17.pos();
            BlockPos var20 = var19.relative(var18);
            int var21 = var19.getY() - var15;
            int var22 = -1;
            ResourceKey var23 = readPoolKey(var17, var7);
            Optional var24 = this.pools.getHolder(var23);
            if (var24.isEmpty()) {
               JigsawPlacement.LOGGER.warn("Empty or non-existent pool: {}", var23.location());
            } else {
               Holder var25 = (Holder)var24.get();
               if (((StructureTemplatePool)var25.value()).size() == 0 && !var25.is(Pools.EMPTY)) {
                  JigsawPlacement.LOGGER.warn("Empty or non-existent pool: {}", var23.location());
               } else {
                  Holder var26 = ((StructureTemplatePool)var25.value()).getFallback();
                  if (((StructureTemplatePool)var26.value()).size() == 0 && !var26.is(Pools.EMPTY)) {
                     JigsawPlacement.LOGGER
                        .warn("Empty or non-existent fallback pool: {}", var26.unwrapKey().map(var0 -> var0.location().toString()).orElse("<unregistered>"));
                  } else {
                     boolean var28 = var14.isInside(var20);
                     MutableObject var27;
                     if (var28) {
                        var27 = var13;
                        if (var13.getValue() == null) {
                           var13.setValue(Shapes.create(AABB.of(var14)));
                        }
                     } else {
                        var27 = var2;
                     }

                     ArrayList var29 = Lists.newArrayList();
                     if (var3 != this.maxDepth) {
                        var29.addAll(((StructureTemplatePool)var25.value()).getShuffledTemplates(this.random));
                     }

                     var29.addAll(((StructureTemplatePool)var26.value()).getShuffledTemplates(this.random));
                     int var30 = var17.nbt() != null ? var17.nbt().getInt("placement_priority") : 0;

                     for (StructurePoolElement var32 : var29) {
                        if (var32 == EmptyPoolElement.INSTANCE) {
                           break;
                        }

                        for (Rotation var34 : Rotation.getShuffled(this.random)) {
                           List var35 = var32.getShuffledJigsawBlocks(this.structureTemplateManager, BlockPos.ZERO, var34, this.random);
                           BoundingBox var36 = var32.getBoundingBox(this.structureTemplateManager, BlockPos.ZERO, var34);
                           int var37;
                           if (var4 && var36.getYSpan() <= 16) {
                              var37 = var35.stream()
                                 .mapToInt(
                                    var3x -> {
                                       if (!var36.isInside(var3x.pos().relative(JigsawBlock.getFrontFacing(var3x.state())))) {
                                          return 0;
                                       } else {
                                          ResourceKey var4x = readPoolKey(var3x, var7);
                                          Optional var5x = this.pools.getHolder(var4x);
                                          Optional var6x = var5x.map(var0 -> ((StructureTemplatePool)var0.value()).getFallback());
                                          int var7x = var5x.<Integer>map(
                                                var1xx -> ((StructureTemplatePool)var1xx.value()).getMaxSize(this.structureTemplateManager)
                                             )
                                             .orElse(0);
                                          int var8x = var6x.<Integer>map(
                                                var1xx -> ((StructureTemplatePool)var1xx.value()).getMaxSize(this.structureTemplateManager)
                                             )
                                             .orElse(0);
                                          return Math.max(var7x, var8x);
                                       }
                                    }
                                 )
                                 .max()
                                 .orElse(0);
                           } else {
                              var37 = 0;
                           }

                           for (StructureTemplate.StructureBlockInfo var39 : var35) {
                              if (JigsawBlock.canAttach(var17, var39)) {
                                 BlockPos var40 = var39.pos();
                                 BlockPos var41 = var20.subtract(var40);
                                 BoundingBox var42 = var32.getBoundingBox(this.structureTemplateManager, var41, var34);
                                 int var43 = var42.minY();
                                 StructureTemplatePool.Projection var44 = var32.getProjection();
                                 boolean var45 = var44 == StructureTemplatePool.Projection.RIGID;
                                 int var46 = var40.getY();
                                 int var47 = var21 - var46 + JigsawBlock.getFrontFacing(var17.state()).getStepY();
                                 int var48;
                                 if (var12 && var45) {
                                    var48 = var15 + var47;
                                 } else {
                                    if (var22 == -1) {
                                       var22 = this.chunkGenerator.getFirstFreeHeight(var19.getX(), var19.getZ(), Heightmap.Types.WORLD_SURFACE_WG, var5, var6);
                                    }

                                    var48 = var22 - var46;
                                 }

                                 int var49 = var48 - var43;
                                 BoundingBox var50 = var42.moved(0, var49, 0);
                                 BlockPos var51 = var41.offset(0, var49, 0);
                                 if (var37 > 0) {
                                    int var52 = Math.max(var37 + 1, var50.maxY() - var50.minY());
                                    var50.encapsulate(new BlockPos(var50.minX(), var50.minY() + var52, var50.minZ()));
                                 }

                                 if (!Shapes.joinIsNotEmpty((VoxelShape)var27.getValue(), Shapes.create(AABB.of(var50).deflate(0.25)), BooleanOp.ONLY_SECOND)) {
                                    var27.setValue(Shapes.joinUnoptimized((VoxelShape)var27.getValue(), Shapes.create(AABB.of(var50)), BooleanOp.ONLY_FIRST));
                                    int var57 = var1.getGroundLevelDelta();
                                    int var53;
                                    if (var45) {
                                       var53 = var57 - var47;
                                    } else {
                                       var53 = var32.getGroundLevelDelta();
                                    }

                                    PoolElementStructurePiece var54 = new PoolElementStructurePiece(
                                       this.structureTemplateManager, var32, var51, var53, var34, var50
                                    );
                                    int var55;
                                    if (var12) {
                                       var55 = var15 + var21;
                                    } else if (var45) {
                                       var55 = var48 + var46;
                                    } else {
                                       if (var22 == -1) {
                                          var22 = this.chunkGenerator
                                             .getFirstFreeHeight(var19.getX(), var19.getZ(), Heightmap.Types.WORLD_SURFACE_WG, var5, var6);
                                       }

                                       var55 = var22 + var47 / 2;
                                    }

                                    var1.addJunction(new JigsawJunction(var20.getX(), var55 - var21 + var57, var20.getZ(), var47, var44));
                                    var54.addJunction(new JigsawJunction(var19.getX(), var55 - var46 + var53, var19.getZ(), -var47, var11));
                                    this.pieces.add(var54);
                                    if (var3 + 1 <= this.maxDepth) {
                                       JigsawPlacement.PieceState var56 = new JigsawPlacement.PieceState(var54, var27, var3 + 1);
                                       this.placing.add(var56, var30);
                                    }
                                    continue label134;
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

      private static ResourceKey<StructureTemplatePool> readPoolKey(StructureTemplate.StructureBlockInfo var0, PoolAliasLookup var1) {
         CompoundTag var2 = Objects.requireNonNull(var0.nbt(), () -> var0 + " nbt was null");
         ResourceKey var3 = Pools.createKey(var2.getString("pool"));
         return var1.lookup(var3);
      }
   }
}
