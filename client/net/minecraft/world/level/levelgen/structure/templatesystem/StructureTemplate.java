package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.IdMapper;
import net.minecraft.core.Vec3i;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.RandomSource;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import org.slf4j.Logger;

public class StructureTemplate {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final String PALETTE_TAG = "palette";
   public static final String PALETTE_LIST_TAG = "palettes";
   public static final String ENTITIES_TAG = "entities";
   public static final String BLOCKS_TAG = "blocks";
   public static final String BLOCK_TAG_POS = "pos";
   public static final String BLOCK_TAG_STATE = "state";
   public static final String BLOCK_TAG_NBT = "nbt";
   public static final String ENTITY_TAG_POS = "pos";
   public static final String ENTITY_TAG_BLOCKPOS = "blockPos";
   public static final String ENTITY_TAG_NBT = "nbt";
   public static final String SIZE_TAG = "size";
   private final List<Palette> palettes = Lists.newArrayList();
   private final List<StructureEntityInfo> entityInfoList = Lists.newArrayList();
   private Vec3i size;
   private String author;

   public StructureTemplate() {
      super();
      this.size = Vec3i.ZERO;
      this.author = "?";
   }

   public Vec3i getSize() {
      return this.size;
   }

   public void setAuthor(String var1) {
      this.author = var1;
   }

   public String getAuthor() {
      return this.author;
   }

   public void fillFromWorld(Level var1, BlockPos var2, Vec3i var3, boolean var4, @Nullable Block var5) {
      if (var3.getX() >= 1 && var3.getY() >= 1 && var3.getZ() >= 1) {
         BlockPos var6 = var2.offset(var3).offset(-1, -1, -1);
         ArrayList var7 = Lists.newArrayList();
         ArrayList var8 = Lists.newArrayList();
         ArrayList var9 = Lists.newArrayList();
         BlockPos var10 = new BlockPos(Math.min(var2.getX(), var6.getX()), Math.min(var2.getY(), var6.getY()), Math.min(var2.getZ(), var6.getZ()));
         BlockPos var11 = new BlockPos(Math.max(var2.getX(), var6.getX()), Math.max(var2.getY(), var6.getY()), Math.max(var2.getZ(), var6.getZ()));
         this.size = var3;

         try (ProblemReporter.ScopedCollector var12 = new ProblemReporter.ScopedCollector(LOGGER)) {
            for(BlockPos var14 : BlockPos.betweenClosed(var10, var11)) {
               BlockPos var15 = var14.subtract(var10);
               BlockState var16 = var1.getBlockState(var14);
               if (var5 == null || !var16.is(var5)) {
                  BlockEntity var17 = var1.getBlockEntity(var14);
                  StructureBlockInfo var18;
                  if (var17 != null) {
                     TagValueOutput var19 = TagValueOutput.createWithContext(var12, var1.registryAccess());
                     var17.saveWithId(var19);
                     var18 = new StructureBlockInfo(var15, var16, var19.buildResult());
                  } else {
                     var18 = new StructureBlockInfo(var15, var16, (CompoundTag)null);
                  }

                  addToLists(var18, var7, var8, var9);
               }
            }

            List var22 = buildInfoList(var7, var8, var9);
            this.palettes.clear();
            this.palettes.add(new Palette(var22));
            if (var4) {
               this.fillEntityList(var1, var10, var11, var12);
            } else {
               this.entityInfoList.clear();
            }
         }

      }
   }

   private static void addToLists(StructureBlockInfo var0, List<StructureBlockInfo> var1, List<StructureBlockInfo> var2, List<StructureBlockInfo> var3) {
      if (var0.nbt != null) {
         var2.add(var0);
      } else if (!var0.state.getBlock().hasDynamicShape() && var0.state.isCollisionShapeFullBlock(EmptyBlockGetter.INSTANCE, BlockPos.ZERO)) {
         var1.add(var0);
      } else {
         var3.add(var0);
      }

   }

   private static List<StructureBlockInfo> buildInfoList(List<StructureBlockInfo> var0, List<StructureBlockInfo> var1, List<StructureBlockInfo> var2) {
      Comparator var3 = Comparator.comparingInt((var0x) -> var0x.pos.getY()).thenComparingInt((var0x) -> var0x.pos.getX()).thenComparingInt((var0x) -> var0x.pos.getZ());
      var0.sort(var3);
      var2.sort(var3);
      var1.sort(var3);
      ArrayList var4 = Lists.newArrayList();
      var4.addAll(var0);
      var4.addAll(var2);
      var4.addAll(var1);
      return var4;
   }

   private void fillEntityList(Level var1, BlockPos var2, BlockPos var3, ProblemReporter var4) {
      List var5 = var1.getEntitiesOfClass(Entity.class, AABB.encapsulatingFullBlocks(var2, var3), (var0) -> !(var0 instanceof Player));
      this.entityInfoList.clear();

      for(Entity var7 : var5) {
         Vec3 var8 = new Vec3(var7.getX() - (double)var2.getX(), var7.getY() - (double)var2.getY(), var7.getZ() - (double)var2.getZ());
         TagValueOutput var9 = TagValueOutput.createWithContext(var4.forChild(var7.problemPath()), var7.registryAccess());
         var7.save(var9);
         BlockPos var10;
         if (var7 instanceof Painting var11) {
            var10 = var11.getPos().subtract(var2);
         } else {
            var10 = BlockPos.containing(var8);
         }

         this.entityInfoList.add(new StructureEntityInfo(var8, var10, var9.buildResult().copy()));
      }

   }

   public List<StructureBlockInfo> filterBlocks(BlockPos var1, StructurePlaceSettings var2, Block var3) {
      return this.filterBlocks(var1, var2, var3, true);
   }

   public List<JigsawBlockInfo> getJigsaws(BlockPos var1, Rotation var2) {
      if (this.palettes.isEmpty()) {
         return new ArrayList();
      } else {
         StructurePlaceSettings var3 = (new StructurePlaceSettings()).setRotation(var2);
         List var4 = var3.getRandomPalette(this.palettes, var1).jigsaws();
         ArrayList var5 = new ArrayList(var4.size());

         for(JigsawBlockInfo var7 : var4) {
            StructureBlockInfo var8 = var7.info;
            var5.add(var7.withInfo(new StructureBlockInfo(calculateRelativePosition(var3, var8.pos()).offset(var1), var8.state.rotate(var3.getRotation()), var8.nbt)));
         }

         return var5;
      }
   }

   public ObjectArrayList<StructureBlockInfo> filterBlocks(BlockPos var1, StructurePlaceSettings var2, Block var3, boolean var4) {
      ObjectArrayList var5 = new ObjectArrayList();
      BoundingBox var6 = var2.getBoundingBox();
      if (this.palettes.isEmpty()) {
         return var5;
      } else {
         for(StructureBlockInfo var8 : var2.getRandomPalette(this.palettes, var1).blocks(var3)) {
            BlockPos var9 = var4 ? calculateRelativePosition(var2, var8.pos).offset(var1) : var8.pos;
            if (var6 == null || var6.isInside(var9)) {
               var5.add(new StructureBlockInfo(var9, var8.state.rotate(var2.getRotation()), var8.nbt));
            }
         }

         return var5;
      }
   }

   public BlockPos calculateConnectedPosition(StructurePlaceSettings var1, BlockPos var2, StructurePlaceSettings var3, BlockPos var4) {
      BlockPos var5 = calculateRelativePosition(var1, var2);
      BlockPos var6 = calculateRelativePosition(var3, var4);
      return var5.subtract(var6);
   }

   public static BlockPos calculateRelativePosition(StructurePlaceSettings var0, BlockPos var1) {
      return transform(var1, var0.getMirror(), var0.getRotation(), var0.getRotationPivot());
   }

   public boolean placeInWorld(ServerLevelAccessor var1, BlockPos var2, BlockPos var3, StructurePlaceSettings var4, RandomSource var5, int var6) {
      if (this.palettes.isEmpty()) {
         return false;
      } else {
         List var7 = var4.getRandomPalette(this.palettes, var2).blocks();
         if ((!var7.isEmpty() || !var4.isIgnoreEntities() && !this.entityInfoList.isEmpty()) && this.size.getX() >= 1 && this.size.getY() >= 1 && this.size.getZ() >= 1) {
            BoundingBox var8 = var4.getBoundingBox();
            ArrayList var9 = Lists.newArrayListWithCapacity(var4.shouldApplyWaterlogging() ? var7.size() : 0);
            ArrayList var10 = Lists.newArrayListWithCapacity(var4.shouldApplyWaterlogging() ? var7.size() : 0);
            ArrayList var11 = Lists.newArrayListWithCapacity(var7.size());
            int var12 = 2147483647;
            int var13 = 2147483647;
            int var14 = 2147483647;
            int var15 = -2147483648;
            int var16 = -2147483648;
            int var17 = -2147483648;
            List var18 = processBlockInfos(var1, var2, var3, var4, var7);

            try (ProblemReporter.ScopedCollector var19 = new ProblemReporter.ScopedCollector(LOGGER)) {
               for(StructureBlockInfo var21 : var18) {
                  BlockPos var22 = var21.pos;
                  if (var8 == null || var8.isInside(var22)) {
                     FluidState var23 = var4.shouldApplyWaterlogging() ? var1.getFluidState(var22) : null;
                     BlockState var24 = var21.state.mirror(var4.getMirror()).rotate(var4.getRotation());
                     if (var21.nbt != null) {
                        var1.setBlock(var22, Blocks.BARRIER.defaultBlockState(), 820);
                     }

                     if (var1.setBlock(var22, var24, var6)) {
                        var12 = Math.min(var12, var22.getX());
                        var13 = Math.min(var13, var22.getY());
                        var14 = Math.min(var14, var22.getZ());
                        var15 = Math.max(var15, var22.getX());
                        var16 = Math.max(var16, var22.getY());
                        var17 = Math.max(var17, var22.getZ());
                        var11.add(Pair.of(var22, var21.nbt));
                        if (var21.nbt != null) {
                           BlockEntity var25 = var1.getBlockEntity(var22);
                           if (var25 != null) {
                              if (var25 instanceof RandomizableContainer) {
                                 var21.nbt.putLong("LootTableSeed", var5.nextLong());
                              }

                              var25.loadWithComponents(TagValueInput.create(var19.forChild(var25.problemPath()), var1.registryAccess(), var21.nbt));
                           }
                        }

                        if (var23 != null) {
                           if (var24.getFluidState().isSource()) {
                              var10.add(var22);
                           } else if (var24.getBlock() instanceof LiquidBlockContainer) {
                              ((LiquidBlockContainer)var24.getBlock()).placeLiquid(var1, var22, var24, var23);
                              if (!var23.isSource()) {
                                 var9.add(var22);
                              }
                           }
                        }
                     }
                  }
               }

               boolean var31 = true;
               Direction[] var32 = new Direction[]{Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

               while(var31 && !var9.isEmpty()) {
                  var31 = false;
                  Iterator var33 = var9.iterator();

                  while(var33.hasNext()) {
                     BlockPos var36 = (BlockPos)var33.next();
                     FluidState var39 = var1.getFluidState(var36);

                     for(int var42 = 0; var42 < var32.length && !var39.isSource(); ++var42) {
                        BlockPos var26 = var36.relative(var32[var42]);
                        FluidState var27 = var1.getFluidState(var26);
                        if (var27.isSource() && !var10.contains(var26)) {
                           var39 = var27;
                        }
                     }

                     if (var39.isSource()) {
                        BlockState var43 = var1.getBlockState(var36);
                        Block var47 = var43.getBlock();
                        if (var47 instanceof LiquidBlockContainer) {
                           ((LiquidBlockContainer)var47).placeLiquid(var1, var36, var43, var39);
                           var31 = true;
                           var33.remove();
                        }
                     }
                  }
               }

               if (var12 <= var15) {
                  if (!var4.getKnownShape()) {
                     BitSetDiscreteVoxelShape var34 = new BitSetDiscreteVoxelShape(var15 - var12 + 1, var16 - var13 + 1, var17 - var14 + 1);
                     int var37 = var12;
                     int var40 = var13;
                     int var44 = var14;

                     for(Pair var50 : var11) {
                        BlockPos var28 = (BlockPos)var50.getFirst();
                        ((DiscreteVoxelShape)var34).fill(var28.getX() - var37, var28.getY() - var40, var28.getZ() - var44);
                     }

                     updateShapeAtEdge(var1, var6, var34, var37, var40, var44);
                  }

                  for(Pair var38 : var11) {
                     BlockPos var41 = (BlockPos)var38.getFirst();
                     if (!var4.getKnownShape()) {
                        BlockState var45 = var1.getBlockState(var41);
                        BlockState var49 = Block.updateFromNeighbourShapes(var45, var1, var41);
                        if (var45 != var49) {
                           var1.setBlock(var41, var49, var6 & -2 | 16);
                        }

                        var1.updateNeighborsAt(var41, var49.getBlock());
                     }

                     if (var38.getSecond() != null) {
                        BlockEntity var46 = var1.getBlockEntity(var41);
                        if (var46 != null) {
                           var46.setChanged();
                        }
                     }
                  }
               }

               if (!var4.isIgnoreEntities()) {
                  this.placeEntities(var1, var2, var4.getMirror(), var4.getRotation(), var4.getRotationPivot(), var8, var4.shouldFinalizeEntities(), var19);
               }
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public static void updateShapeAtEdge(LevelAccessor var0, int var1, DiscreteVoxelShape var2, BlockPos var3) {
      updateShapeAtEdge(var0, var1, var2, var3.getX(), var3.getY(), var3.getZ());
   }

   public static void updateShapeAtEdge(LevelAccessor var0, int var1, DiscreteVoxelShape var2, int var3, int var4, int var5) {
      BlockPos.MutableBlockPos var6 = new BlockPos.MutableBlockPos();
      BlockPos.MutableBlockPos var7 = new BlockPos.MutableBlockPos();
      var2.forAllFaces((var7x, var8, var9, var10) -> {
         var6.set(var3 + var8, var4 + var9, var5 + var10);
         var7.setWithOffset(var6, (Direction)var7x);
         BlockState var11 = var0.getBlockState(var6);
         BlockState var12 = var0.getBlockState(var7);
         BlockState var13 = var11.updateShape(var0, var0, var6, var7x, var7, var12, var0.getRandom());
         if (var11 != var13) {
            var0.setBlock(var6, var13, var1 & -2);
         }

         BlockState var14 = var12.updateShape(var0, var0, var7, var7x.getOpposite(), var6, var13, var0.getRandom());
         if (var12 != var14) {
            var0.setBlock(var7, var14, var1 & -2);
         }

      });
   }

   public static List<StructureBlockInfo> processBlockInfos(ServerLevelAccessor var0, BlockPos var1, BlockPos var2, StructurePlaceSettings var3, List<StructureBlockInfo> var4) {
      ArrayList var5 = new ArrayList();
      Object var6 = new ArrayList();

      for(StructureBlockInfo var8 : var4) {
         BlockPos var9 = calculateRelativePosition(var3, var8.pos).offset(var1);
         StructureBlockInfo var10 = new StructureBlockInfo(var9, var8.state, var8.nbt != null ? var8.nbt.copy() : null);

         for(Iterator var11 = var3.getProcessors().iterator(); var10 != null && var11.hasNext(); var10 = ((StructureProcessor)var11.next()).processBlock(var0, var1, var2, var8, var10, var3)) {
         }

         if (var10 != null) {
            ((List)var6).add(var10);
            var5.add(var8);
         }
      }

      for(StructureProcessor var13 : var3.getProcessors()) {
         var6 = var13.finalizeProcessing(var0, var1, var2, var5, (List)var6, var3);
      }

      return (List<StructureBlockInfo>)var6;
   }

   private void placeEntities(ServerLevelAccessor var1, BlockPos var2, Mirror var3, Rotation var4, BlockPos var5, @Nullable BoundingBox var6, boolean var7, ProblemReporter var8) {
      for(StructureEntityInfo var10 : this.entityInfoList) {
         BlockPos var11 = transform(var10.blockPos, var3, var4, var5).offset(var2);
         if (var6 == null || var6.isInside(var11)) {
            CompoundTag var12 = var10.nbt.copy();
            Vec3 var13 = transform(var10.pos, var3, var4, var5);
            Vec3 var14 = var13.add((double)var2.getX(), (double)var2.getY(), (double)var2.getZ());
            ListTag var15 = new ListTag();
            var15.add(DoubleTag.valueOf(var14.x));
            var15.add(DoubleTag.valueOf(var14.y));
            var15.add(DoubleTag.valueOf(var14.z));
            var12.put("Pos", var15);
            var12.remove("UUID");
            createEntityIgnoreException(var8, var1, var12).ifPresent((var5x) -> {
               float var6 = var5x.rotate(var4);
               var6 += var5x.mirror(var3) - var5x.getYRot();
               var5x.snapTo(var14.x, var14.y, var14.z, var6, var5x.getXRot());
               if (var7 && var5x instanceof Mob) {
                  ((Mob)var5x).finalizeSpawn(var1, var1.getCurrentDifficultyAt(BlockPos.containing(var14)), EntitySpawnReason.STRUCTURE, (SpawnGroupData)null);
               }

               var1.addFreshEntityWithPassengers(var5x);
            });
         }
      }

   }

   private static Optional<Entity> createEntityIgnoreException(ProblemReporter var0, ServerLevelAccessor var1, CompoundTag var2) {
      try {
         return EntityType.create(TagValueInput.create(var0, var1.registryAccess(), var2), var1.getLevel(), EntitySpawnReason.STRUCTURE);
      } catch (Exception var4) {
         return Optional.empty();
      }
   }

   public Vec3i getSize(Rotation var1) {
      switch (var1) {
         case COUNTERCLOCKWISE_90:
         case CLOCKWISE_90:
            return new Vec3i(this.size.getZ(), this.size.getY(), this.size.getX());
         default:
            return this.size;
      }
   }

   public static BlockPos transform(BlockPos var0, Mirror var1, Rotation var2, BlockPos var3) {
      int var4 = var0.getX();
      int var5 = var0.getY();
      int var6 = var0.getZ();
      boolean var7 = true;
      switch (var1) {
         case LEFT_RIGHT -> var6 = -var6;
         case FRONT_BACK -> var4 = -var4;
         default -> var7 = false;
      }

      int var8 = var3.getX();
      int var9 = var3.getZ();
      switch (var2) {
         case COUNTERCLOCKWISE_90 -> {
            return new BlockPos(var8 - var9 + var6, var5, var8 + var9 - var4);
         }
         case CLOCKWISE_90 -> {
            return new BlockPos(var8 + var9 - var6, var5, var9 - var8 + var4);
         }
         case CLOCKWISE_180 -> {
            return new BlockPos(var8 + var8 - var4, var5, var9 + var9 - var6);
         }
         default -> {
            return var7 ? new BlockPos(var4, var5, var6) : var0;
         }
      }
   }

   public static Vec3 transform(Vec3 var0, Mirror var1, Rotation var2, BlockPos var3) {
      double var4 = var0.x;
      double var6 = var0.y;
      double var8 = var0.z;
      boolean var10 = true;
      switch (var1) {
         case LEFT_RIGHT -> var8 = 1.0 - var8;
         case FRONT_BACK -> var4 = 1.0 - var4;
         default -> var10 = false;
      }

      int var11 = var3.getX();
      int var12 = var3.getZ();
      switch (var2) {
         case COUNTERCLOCKWISE_90 -> {
            return new Vec3((double)(var11 - var12) + var8, var6, (double)(var11 + var12 + 1) - var4);
         }
         case CLOCKWISE_90 -> {
            return new Vec3((double)(var11 + var12 + 1) - var8, var6, (double)(var12 - var11) + var4);
         }
         case CLOCKWISE_180 -> {
            return new Vec3((double)(var11 + var11 + 1) - var4, var6, (double)(var12 + var12 + 1) - var8);
         }
         default -> {
            return var10 ? new Vec3(var4, var6, var8) : var0;
         }
      }
   }

   public BlockPos getZeroPositionWithTransform(BlockPos var1, Mirror var2, Rotation var3) {
      return getZeroPositionWithTransform(var1, var2, var3, this.getSize().getX(), this.getSize().getZ());
   }

   public static BlockPos getZeroPositionWithTransform(BlockPos var0, Mirror var1, Rotation var2, int var3, int var4) {
      --var3;
      --var4;
      int var5 = var1 == Mirror.FRONT_BACK ? var3 : 0;
      int var6 = var1 == Mirror.LEFT_RIGHT ? var4 : 0;
      BlockPos var7 = var0;
      switch (var2) {
         case COUNTERCLOCKWISE_90 -> var7 = var0.offset(var6, 0, var3 - var5);
         case CLOCKWISE_90 -> var7 = var0.offset(var4 - var6, 0, var5);
         case CLOCKWISE_180 -> var7 = var0.offset(var3 - var5, 0, var4 - var6);
         case NONE -> var7 = var0.offset(var5, 0, var6);
      }

      return var7;
   }

   public BoundingBox getBoundingBox(StructurePlaceSettings var1, BlockPos var2) {
      return this.getBoundingBox(var2, var1.getRotation(), var1.getRotationPivot(), var1.getMirror());
   }

   public BoundingBox getBoundingBox(BlockPos var1, Rotation var2, BlockPos var3, Mirror var4) {
      return getBoundingBox(var1, var2, var3, var4, this.size);
   }

   @VisibleForTesting
   protected static BoundingBox getBoundingBox(BlockPos var0, Rotation var1, BlockPos var2, Mirror var3, Vec3i var4) {
      Vec3i var5 = var4.offset(-1, -1, -1);
      BlockPos var6 = transform(BlockPos.ZERO, var3, var1, var2);
      BlockPos var7 = transform(BlockPos.ZERO.offset(var5), var3, var1, var2);
      return BoundingBox.fromCorners(var6, var7).move(var0);
   }

   public CompoundTag save(CompoundTag var1) {
      if (this.palettes.isEmpty()) {
         var1.put("blocks", new ListTag());
         var1.put("palette", new ListTag());
      } else {
         ArrayList var2 = Lists.newArrayList();
         SimplePalette var3 = new SimplePalette();
         var2.add(var3);

         for(int var4 = 1; var4 < this.palettes.size(); ++var4) {
            var2.add(new SimplePalette());
         }

         ListTag var14 = new ListTag();
         List var5 = ((Palette)this.palettes.get(0)).blocks();

         for(int var6 = 0; var6 < var5.size(); ++var6) {
            StructureBlockInfo var7 = (StructureBlockInfo)var5.get(var6);
            CompoundTag var8 = new CompoundTag();
            var8.put("pos", this.newIntegerList(var7.pos.getX(), var7.pos.getY(), var7.pos.getZ()));
            int var9 = var3.idFor(var7.state);
            var8.putInt("state", var9);
            if (var7.nbt != null) {
               var8.put("nbt", var7.nbt);
            }

            var14.add(var8);

            for(int var10 = 1; var10 < this.palettes.size(); ++var10) {
               SimplePalette var11 = (SimplePalette)var2.get(var10);
               var11.addMapping(((StructureBlockInfo)((Palette)this.palettes.get(var10)).blocks().get(var6)).state, var9);
            }
         }

         var1.put("blocks", var14);
         if (var2.size() == 1) {
            ListTag var17 = new ListTag();

            for(BlockState var21 : var3) {
               var17.add(NbtUtils.writeBlockState(var21));
            }

            var1.put("palette", var17);
         } else {
            ListTag var18 = new ListTag();

            for(SimplePalette var22 : var2) {
               ListTag var23 = new ListTag();

               for(BlockState var25 : var22) {
                  var23.add(NbtUtils.writeBlockState(var25));
               }

               var18.add(var23);
            }

            var1.put("palettes", var18);
         }
      }

      ListTag var12 = new ListTag();

      for(StructureEntityInfo var15 : this.entityInfoList) {
         CompoundTag var16 = new CompoundTag();
         var16.put("pos", this.newDoubleList(var15.pos.x, var15.pos.y, var15.pos.z));
         var16.put("blockPos", this.newIntegerList(var15.blockPos.getX(), var15.blockPos.getY(), var15.blockPos.getZ()));
         if (var15.nbt != null) {
            var16.put("nbt", var15.nbt);
         }

         var12.add(var16);
      }

      var1.put("entities", var12);
      var1.put("size", this.newIntegerList(this.size.getX(), this.size.getY(), this.size.getZ()));
      return NbtUtils.addCurrentDataVersion(var1);
   }

   public void load(HolderGetter<Block> var1, CompoundTag var2) {
      this.palettes.clear();
      this.entityInfoList.clear();
      ListTag var3 = var2.getListOrEmpty("size");
      this.size = new Vec3i(var3.getIntOr(0, 0), var3.getIntOr(1, 0), var3.getIntOr(2, 0));
      ListTag var4 = var2.getListOrEmpty("blocks");
      Optional var5 = var2.getList("palettes");
      if (var5.isPresent()) {
         for(int var6 = 0; var6 < ((ListTag)var5.get()).size(); ++var6) {
            this.loadPalette(var1, ((ListTag)var5.get()).getListOrEmpty(var6), var4);
         }
      } else {
         this.loadPalette(var1, var2.getListOrEmpty("palette"), var4);
      }

      var2.getListOrEmpty("entities").compoundStream().forEach((var1x) -> {
         ListTag var2 = var1x.getListOrEmpty("pos");
         Vec3 var3 = new Vec3(var2.getDoubleOr(0, 0.0), var2.getDoubleOr(1, 0.0), var2.getDoubleOr(2, 0.0));
         ListTag var4 = var1x.getListOrEmpty("blockPos");
         BlockPos var5 = new BlockPos(var4.getIntOr(0, 0), var4.getIntOr(1, 0), var4.getIntOr(2, 0));
         var1x.getCompound("nbt").ifPresent((var3x) -> this.entityInfoList.add(new StructureEntityInfo(var3, var5, var3x)));
      });
   }

   private void loadPalette(HolderGetter<Block> var1, ListTag var2, ListTag var3) {
      SimplePalette var4 = new SimplePalette();

      for(int var5 = 0; var5 < var2.size(); ++var5) {
         var4.addMapping(NbtUtils.readBlockState(var1, var2.getCompoundOrEmpty(var5)), var5);
      }

      ArrayList var9 = Lists.newArrayList();
      ArrayList var6 = Lists.newArrayList();
      ArrayList var7 = Lists.newArrayList();
      var3.compoundStream().forEach((var4x) -> {
         ListTag var5 = var4x.getListOrEmpty("pos");
         BlockPos var6x = new BlockPos(var5.getIntOr(0, 0), var5.getIntOr(1, 0), var5.getIntOr(2, 0));
         BlockState var7x = var4.stateFor(var4x.getIntOr("state", 0));
         CompoundTag var8 = (CompoundTag)var4x.getCompound("nbt").orElse((Object)null);
         StructureBlockInfo var9x = new StructureBlockInfo(var6x, var7x, var8);
         addToLists(var9x, var9, var6, var7);
      });
      List var8 = buildInfoList(var9, var6, var7);
      this.palettes.add(new Palette(var8));
   }

   private ListTag newIntegerList(int... var1) {
      ListTag var2 = new ListTag();

      for(int var6 : var1) {
         var2.add(IntTag.valueOf(var6));
      }

      return var2;
   }

   private ListTag newDoubleList(double... var1) {
      ListTag var2 = new ListTag();

      for(double var6 : var1) {
         var2.add(DoubleTag.valueOf(var6));
      }

      return var2;
   }

   public static JigsawBlockEntity.JointType getJointType(CompoundTag var0, BlockState var1) {
      return (JigsawBlockEntity.JointType)var0.read("joint", JigsawBlockEntity.JointType.CODEC).orElseGet(() -> getDefaultJointType(var1));
   }

   public static JigsawBlockEntity.JointType getDefaultJointType(BlockState var0) {
      return JigsawBlock.getFrontFacing(var0).getAxis().isHorizontal() ? JigsawBlockEntity.JointType.ALIGNED : JigsawBlockEntity.JointType.ROLLABLE;
   }

   static class SimplePalette implements Iterable<BlockState> {
      public static final BlockState DEFAULT_BLOCK_STATE;
      private final IdMapper<BlockState> ids = new IdMapper<BlockState>(16);
      private int lastId;

      SimplePalette() {
         super();
      }

      public int idFor(BlockState var1) {
         int var2 = this.ids.getId(var1);
         if (var2 == -1) {
            var2 = this.lastId++;
            this.ids.addMapping(var1, var2);
         }

         return var2;
      }

      @Nullable
      public BlockState stateFor(int var1) {
         BlockState var2 = this.ids.byId(var1);
         return var2 == null ? DEFAULT_BLOCK_STATE : var2;
      }

      public Iterator<BlockState> iterator() {
         return this.ids.iterator();
      }

      public void addMapping(BlockState var1, int var2) {
         this.ids.addMapping(var1, var2);
      }

      static {
         DEFAULT_BLOCK_STATE = Blocks.AIR.defaultBlockState();
      }
   }

   public static record StructureBlockInfo(BlockPos pos, BlockState state, @Nullable CompoundTag nbt) {
      final BlockPos pos;
      final BlockState state;
      @Nullable
      final CompoundTag nbt;

      public StructureBlockInfo(BlockPos var1, BlockState var2, @Nullable CompoundTag var3) {
         super();
         this.pos = var1;
         this.state = var2;
         this.nbt = var3;
      }

      public String toString() {
         return String.format(Locale.ROOT, "<StructureBlockInfo | %s | %s | %s>", this.pos, this.state, this.nbt);
      }
   }

   public static record JigsawBlockInfo(StructureBlockInfo info, JigsawBlockEntity.JointType jointType, ResourceLocation name, ResourceKey<StructureTemplatePool> pool, ResourceLocation target, int placementPriority, int selectionPriority) {
      final StructureBlockInfo info;

      public JigsawBlockInfo(StructureBlockInfo var1, JigsawBlockEntity.JointType var2, ResourceLocation var3, ResourceKey<StructureTemplatePool> var4, ResourceLocation var5, int var6, int var7) {
         super();
         this.info = var1;
         this.jointType = var2;
         this.name = var3;
         this.pool = var4;
         this.target = var5;
         this.placementPriority = var6;
         this.selectionPriority = var7;
      }

      public static JigsawBlockInfo of(StructureBlockInfo var0) {
         CompoundTag var1 = (CompoundTag)Objects.requireNonNull(var0.nbt(), () -> String.valueOf(var0) + " nbt was null");
         return new JigsawBlockInfo(var0, StructureTemplate.getJointType(var1, var0.state()), (ResourceLocation)var1.read("name", ResourceLocation.CODEC).orElse(JigsawBlockEntity.EMPTY_ID), (ResourceKey)var1.read("pool", JigsawBlockEntity.POOL_CODEC).orElse(Pools.EMPTY), (ResourceLocation)var1.read("target", ResourceLocation.CODEC).orElse(JigsawBlockEntity.EMPTY_ID), var1.getIntOr("placement_priority", 0), var1.getIntOr("selection_priority", 0));
      }

      public String toString() {
         return String.format(Locale.ROOT, "<JigsawBlockInfo | %s | %s | name: %s | pool: %s | target: %s | placement: %d | selection: %d | %s>", this.info.pos, this.info.state, this.name, this.pool.location(), this.target, this.placementPriority, this.selectionPriority, this.info.nbt);
      }

      public JigsawBlockInfo withInfo(StructureBlockInfo var1) {
         return new JigsawBlockInfo(var1, this.jointType, this.name, this.pool, this.target, this.placementPriority, this.selectionPriority);
      }
   }

   public static class StructureEntityInfo {
      public final Vec3 pos;
      public final BlockPos blockPos;
      public final CompoundTag nbt;

      public StructureEntityInfo(Vec3 var1, BlockPos var2, CompoundTag var3) {
         super();
         this.pos = var1;
         this.blockPos = var2;
         this.nbt = var3;
      }
   }

   public static final class Palette {
      private final List<StructureBlockInfo> blocks;
      private final Map<Block, List<StructureBlockInfo>> cache = Maps.newHashMap();
      @Nullable
      private List<JigsawBlockInfo> cachedJigsaws;

      Palette(List<StructureBlockInfo> var1) {
         super();
         this.blocks = var1;
      }

      public List<JigsawBlockInfo> jigsaws() {
         if (this.cachedJigsaws == null) {
            this.cachedJigsaws = this.blocks(Blocks.JIGSAW).stream().map(JigsawBlockInfo::of).toList();
         }

         return this.cachedJigsaws;
      }

      public List<StructureBlockInfo> blocks() {
         return this.blocks;
      }

      public List<StructureBlockInfo> blocks(Block var1) {
         return (List)this.cache.computeIfAbsent(var1, (var1x) -> (List)this.blocks.stream().filter((var1) -> var1.state.is(var1x)).collect(Collectors.toList()));
      }
   }
}
