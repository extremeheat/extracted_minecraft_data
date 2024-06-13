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
      PoolAliasLookup var8,
      DimensionPadding var9,
      LiquidSettings var10
   ) {
      RegistryAccess var11 = var0.registryAccess();
      ChunkGenerator var12 = var0.chunkGenerator();
      StructureTemplateManager var13 = var0.structureTemplateManager();
      LevelHeightAccessor var14 = var0.heightAccessor();
      WorldgenRandom var15 = var0.random();
      Registry var16 = var11.registryOrThrow(Registries.TEMPLATE_POOL);
      Rotation var17 = Rotation.getRandom(var15);
      StructureTemplatePool var18 = var1.unwrapKey()
         .flatMap(var2x -> var16.getOptional(var8.lookup((ResourceKey<StructureTemplatePool>)var2x)))
         .orElse((StructureTemplatePool)var1.value());
      StructurePoolElement var19 = var18.getRandomTemplate(var15);
      if (var19 == EmptyPoolElement.INSTANCE) {
         return Optional.empty();
      } else {
         BlockPos var20;
         if (var2.isPresent()) {
            ResourceLocation var21 = (ResourceLocation)var2.get();
            Optional var22 = getRandomNamedJigsaw(var19, var21, var4, var17, var13, var15);
            if (var22.isEmpty()) {
               LOGGER.error(
                  "No starting jigsaw {} found in start pool {}", var21, var1.unwrapKey().map(var0x -> var0x.location().toString()).orElse("<unregistered>")
               );
               return Optional.empty();
            }

            var20 = (BlockPos)var22.get();
         } else {
            var20 = var4;
         }

         BlockPos var30 = var20.subtract(var4);
         BlockPos var31 = var4.subtract(var30);
         PoolElementStructurePiece var23 = new PoolElementStructurePiece(
            var13, var19, var31, var19.getGroundLevelDelta(), var17, var19.getBoundingBox(var13, var31, var17), var10
         );
         BoundingBox var24 = var23.getBoundingBox();
         int var25 = (var24.maxX() + var24.minX()) / 2;
         int var26 = (var24.maxZ() + var24.minZ()) / 2;
         int var27;
         if (var6.isPresent()) {
            var27 = var4.getY() + var12.getFirstFreeHeight(var25, var26, (Heightmap.Types)var6.get(), var14, var0.randomState());
         } else {
            var27 = var31.getY();
         }

         int var28 = var24.minY() + var23.getGroundLevelDelta();
         var23.move(0, var27 - var28, 0);
         int var29 = var27 + var30.getY();
         return Optional.of(
            new Structure.GenerationStub(
               new BlockPos(var25, var29, var26),
               (Consumer<StructurePiecesBuilder>)(var17x -> {
                  ArrayList var18x = Lists.newArrayList();
                  var18x.add(var23);
                  if (var3 > 0) {
                     AABB var19x = new AABB(
                        (double)(var25 - var7),
                        (double)Math.max(var29 - var7, var14.getMinBuildHeight() + var9.bottom()),
                        (double)(var26 - var7),
                        (double)(var25 + var7 + 1),
                        (double)Math.min(var29 + var7 + 1, var14.getMaxBuildHeight() - var9.top()),
                        (double)(var26 + var7 + 1)
                     );
                     VoxelShape var20x = Shapes.join(Shapes.create(var19x), Shapes.create(AABB.of(var24)), BooleanOp.ONLY_FIRST);
                     addPieces(var0.randomState(), var3, var5, var12, var13, var14, var15, var16, var23, var18x, var20x, var8, var10);
                     var18x.forEach(var17x::addPiece);
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
      PoolAliasLookup var11,
      LiquidSettings var12
   ) {
      JigsawPlacement.Placer var13 = new JigsawPlacement.Placer(var7, var1, var3, var4, var9, var6);
      var13.tryPlacingChildren(var8, new MutableObject(var10), 0, var2, var5, var0, var11, var12);

      while (var13.placing.hasNext()) {
         JigsawPlacement.PieceState var14 = (JigsawPlacement.PieceState)var13.placing.next();
         var13.tryPlacingChildren(var14.piece, var14.free, var14.depth, var2, var5, var0, var11, var12);
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
      Optional var11 = addPieces(
         var10,
         var1,
         Optional.of(var2),
         var3,
         var4,
         false,
         Optional.empty(),
         128,
         PoolAliasLookup.EMPTY,
         JigsawStructure.DEFAULT_DIMENSION_PADDING,
         JigsawStructure.DEFAULT_LIQUID_SETTINGS
      );
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

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

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
         PoolAliasLookup var7,
         LiquidSettings var8
      ) {
         StructurePoolElement var9 = var1.getElement();
         BlockPos var10 = var1.getPosition();
         Rotation var11 = var1.getRotation();
         StructureTemplatePool.Projection var12 = var9.getProjection();
         boolean var13 = var12 == StructureTemplatePool.Projection.RIGID;
         MutableObject var14 = new MutableObject();
         BoundingBox var15 = var1.getBoundingBox();
         int var16 = var15.minY();

         label134:
         for (StructureTemplate.StructureBlockInfo var18 : var9.getShuffledJigsawBlocks(this.structureTemplateManager, var10, var11, this.random)) {
            Direction var19 = JigsawBlock.getFrontFacing(var18.state());
            BlockPos var20 = var18.pos();
            BlockPos var21 = var20.relative(var19);
            int var22 = var20.getY() - var16;
            int var23 = -1;
            ResourceKey var24 = readPoolKey(var18, var7);
            Optional var25 = this.pools.getHolder(var24);
            if (var25.isEmpty()) {
               JigsawPlacement.LOGGER.warn("Empty or non-existent pool: {}", var24.location());
            } else {
               Holder var26 = (Holder)var25.get();
               if (((StructureTemplatePool)var26.value()).size() == 0 && !var26.is(Pools.EMPTY)) {
                  JigsawPlacement.LOGGER.warn("Empty or non-existent pool: {}", var24.location());
               } else {
                  Holder var27 = ((StructureTemplatePool)var26.value()).getFallback();
                  if (((StructureTemplatePool)var27.value()).size() == 0 && !var27.is(Pools.EMPTY)) {
                     JigsawPlacement.LOGGER
                        .warn("Empty or non-existent fallback pool: {}", var27.unwrapKey().map(var0 -> var0.location().toString()).orElse("<unregistered>"));
                  } else {
                     boolean var29 = var15.isInside(var21);
                     MutableObject var28;
                     if (var29) {
                        var28 = var14;
                        if (var14.getValue() == null) {
                           var14.setValue(Shapes.create(AABB.of(var15)));
                        }
                     } else {
                        var28 = var2;
                     }

                     ArrayList var30 = Lists.newArrayList();
                     if (var3 != this.maxDepth) {
                        var30.addAll(((StructureTemplatePool)var26.value()).getShuffledTemplates(this.random));
                     }

                     var30.addAll(((StructureTemplatePool)var27.value()).getShuffledTemplates(this.random));
                     int var31 = var18.nbt() != null ? var18.nbt().getInt("placement_priority") : 0;

                     for (StructurePoolElement var33 : var30) {
                        if (var33 == EmptyPoolElement.INSTANCE) {
                           break;
                        }

                        for (Rotation var35 : Rotation.getShuffled(this.random)) {
                           List var36 = var33.getShuffledJigsawBlocks(this.structureTemplateManager, BlockPos.ZERO, var35, this.random);
                           BoundingBox var37 = var33.getBoundingBox(this.structureTemplateManager, BlockPos.ZERO, var35);
                           int var38;
                           if (var4 && var37.getYSpan() <= 16) {
                              var38 = var36.stream()
                                 .mapToInt(
                                    var3x -> {
                                       if (!var37.isInside(var3x.pos().relative(JigsawBlock.getFrontFacing(var3x.state())))) {
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
                              var38 = 0;
                           }

                           for (StructureTemplate.StructureBlockInfo var40 : var36) {
                              if (JigsawBlock.canAttach(var18, var40)) {
                                 BlockPos var41 = var40.pos();
                                 BlockPos var42 = var21.subtract(var41);
                                 BoundingBox var43 = var33.getBoundingBox(this.structureTemplateManager, var42, var35);
                                 int var44 = var43.minY();
                                 StructureTemplatePool.Projection var45 = var33.getProjection();
                                 boolean var46 = var45 == StructureTemplatePool.Projection.RIGID;
                                 int var47 = var41.getY();
                                 int var48 = var22 - var47 + JigsawBlock.getFrontFacing(var18.state()).getStepY();
                                 int var49;
                                 if (var13 && var46) {
                                    var49 = var16 + var48;
                                 } else {
                                    if (var23 == -1) {
                                       var23 = this.chunkGenerator.getFirstFreeHeight(var20.getX(), var20.getZ(), Heightmap.Types.WORLD_SURFACE_WG, var5, var6);
                                    }

                                    var49 = var23 - var47;
                                 }

                                 int var50 = var49 - var44;
                                 BoundingBox var51 = var43.moved(0, var50, 0);
                                 BlockPos var52 = var42.offset(0, var50, 0);
                                 if (var38 > 0) {
                                    int var53 = Math.max(var38 + 1, var51.maxY() - var51.minY());
                                    var51.encapsulate(new BlockPos(var51.minX(), var51.minY() + var53, var51.minZ()));
                                 }

                                 if (!Shapes.joinIsNotEmpty((VoxelShape)var28.getValue(), Shapes.create(AABB.of(var51).deflate(0.25)), BooleanOp.ONLY_SECOND)) {
                                    var28.setValue(Shapes.joinUnoptimized((VoxelShape)var28.getValue(), Shapes.create(AABB.of(var51)), BooleanOp.ONLY_FIRST));
                                    int var58 = var1.getGroundLevelDelta();
                                    int var54;
                                    if (var46) {
                                       var54 = var58 - var48;
                                    } else {
                                       var54 = var33.getGroundLevelDelta();
                                    }

                                    PoolElementStructurePiece var55 = new PoolElementStructurePiece(
                                       this.structureTemplateManager, var33, var52, var54, var35, var51, var8
                                    );
                                    int var56;
                                    if (var13) {
                                       var56 = var16 + var22;
                                    } else if (var46) {
                                       var56 = var49 + var47;
                                    } else {
                                       if (var23 == -1) {
                                          var23 = this.chunkGenerator
                                             .getFirstFreeHeight(var20.getX(), var20.getZ(), Heightmap.Types.WORLD_SURFACE_WG, var5, var6);
                                       }

                                       var56 = var23 + var48 / 2;
                                    }

                                    var1.addJunction(new JigsawJunction(var21.getX(), var56 - var22 + var58, var21.getZ(), var48, var45));
                                    var55.addJunction(new JigsawJunction(var20.getX(), var56 - var47 + var54, var20.getZ(), -var48, var12));
                                    this.pieces.add(var55);
                                    if (var3 + 1 <= this.maxDepth) {
                                       JigsawPlacement.PieceState var57 = new JigsawPlacement.PieceState(var55, var28, var3 + 1);
                                       this.placing.add(var57, var31);
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
         ResourceKey var3 = Pools.parseKey(var2.getString("pool"));
         return var1.lookup(var3);
      }
   }
}
