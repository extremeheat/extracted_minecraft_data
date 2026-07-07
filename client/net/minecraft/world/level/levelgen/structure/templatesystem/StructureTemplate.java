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
import net.minecraft.SharedConstants;
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
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.RandomSource;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntitySpawnRequest;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.decoration.painting.Painting;
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
import org.jspecify.annotations.Nullable;
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

   public void setAuthor(final String author) {
      this.author = author;
   }

   public String getAuthor() {
      return this.author;
   }

   public void fillFromWorld(final Level level, final BlockPos position, final Vec3i size, final boolean inludeEntities, final List<Block> ignoreBlocks) {
      if (size.getX() >= 1 && size.getY() >= 1 && size.getZ() >= 1) {
         BlockPos corner2 = position.offset(size).offset(-1, -1, -1);
         List<StructureBlockInfo> fullBlockList = Lists.newArrayList();
         List<StructureBlockInfo> blockEntitiesList = Lists.newArrayList();
         List<StructureBlockInfo> otherBlocksList = Lists.newArrayList();
         BlockPos minCorner = new BlockPos(Math.min(position.getX(), corner2.getX()), Math.min(position.getY(), corner2.getY()), Math.min(position.getZ(), corner2.getZ()));
         BlockPos maxCorner = new BlockPos(Math.max(position.getX(), corner2.getX()), Math.max(position.getY(), corner2.getY()), Math.max(position.getZ(), corner2.getZ()));
         this.size = size;

         try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(LOGGER)) {
            level.findBlocksIn(minCorner, maxCorner).filterState((state) -> !ignoreBlocks.contains(state.getBlock())).forEach((pos, state) -> {
               BlockPos relativePos = pos.subtract(minCorner);
               BlockEntity blockEntity = level.getBlockEntity(pos);
               StructureBlockInfo info;
               if (blockEntity != null) {
                  TagValueOutput output = TagValueOutput.createWithContext(reporter, level.registryAccess());
                  blockEntity.saveWithId(output);
                  info = new StructureBlockInfo(relativePos, state, output.buildResult());
               } else {
                  info = new StructureBlockInfo(relativePos, state, (CompoundTag)null);
               }

               addToLists(info, fullBlockList, blockEntitiesList, otherBlocksList);
            });
            List<StructureBlockInfo> blockInfoList = buildInfoList(fullBlockList, blockEntitiesList, otherBlocksList);
            this.palettes.clear();
            this.palettes.add(new Palette(blockInfoList));
            if (inludeEntities) {
               this.fillEntityList(level, minCorner, maxCorner, reporter);
            } else {
               this.entityInfoList.clear();
            }
         }

      }
   }

   private static void addToLists(final StructureBlockInfo info, final List<StructureBlockInfo> fullBlockList, final List<StructureBlockInfo> blockEntitiesList, final List<StructureBlockInfo> otherBlocksList) {
      if (info.nbt != null) {
         blockEntitiesList.add(info);
      } else if (!info.state.getBlock().hasDynamicShape() && info.state.isCollisionShapeFullBlock(EmptyBlockGetter.INSTANCE, BlockPos.ZERO)) {
         fullBlockList.add(info);
      } else {
         otherBlocksList.add(info);
      }

   }

   private static List<StructureBlockInfo> buildInfoList(final List<StructureBlockInfo> fullBlockList, final List<StructureBlockInfo> blockEntitiesList, final List<StructureBlockInfo> otherBlocksList) {
      Comparator<StructureBlockInfo> comparator = Comparator.comparingInt((o) -> o.pos.getY()).thenComparingInt((o) -> o.pos.getX()).thenComparingInt((o) -> o.pos.getZ());
      fullBlockList.sort(comparator);
      otherBlocksList.sort(comparator);
      blockEntitiesList.sort(comparator);
      List<StructureBlockInfo> blockInfoList = Lists.newArrayList();
      blockInfoList.addAll(fullBlockList);
      blockInfoList.addAll(otherBlocksList);
      blockInfoList.addAll(blockEntitiesList);
      return blockInfoList;
   }

   private void fillEntityList(final Level level, final BlockPos minCorner, final BlockPos maxCorner, final ProblemReporter reporter) {
      List<Entity> entities = level.getEntitiesOfClass(Entity.class, AABB.encapsulatingFullBlocks(minCorner, maxCorner), (input) -> !(input instanceof Player));
      this.entityInfoList.clear();

      for(Entity entity : entities) {
         Vec3 pos = new Vec3(entity.getX() - (double)minCorner.getX(), entity.getY() - (double)minCorner.getY(), entity.getZ() - (double)minCorner.getZ());
         TagValueOutput output = TagValueOutput.createWithContext(reporter.forChild(entity.problemPath()), entity.registryAccess());
         entity.save(output);
         BlockPos blockPos;
         if (entity instanceof Painting painting) {
            blockPos = painting.getPos().subtract(minCorner);
         } else {
            blockPos = BlockPos.containing(pos);
         }

         this.entityInfoList.add(new StructureEntityInfo(pos, blockPos, output.buildResult().copy()));
      }

   }

   public List<StructureBlockInfo> filterBlocks(final BlockPos position, final StructurePlaceSettings settings, final Block block) {
      return this.filterBlocks(position, settings, block, true);
   }

   public List<JigsawBlockInfo> getJigsaws(final BlockPos position, final Rotation rotation) {
      if (this.palettes.isEmpty()) {
         return new ArrayList();
      } else {
         StructurePlaceSettings settings = (new StructurePlaceSettings()).setRotation(rotation);
         List<JigsawBlockInfo> jigsaws = settings.getRandomPalette(this.palettes, position).jigsaws();
         List<JigsawBlockInfo> result = new ArrayList(jigsaws.size());

         for(JigsawBlockInfo jigsaw : jigsaws) {
            StructureBlockInfo blockInfo = jigsaw.info;
            result.add(jigsaw.withInfo(new StructureBlockInfo(calculateRelativePosition(settings, blockInfo.pos()).offset(position), blockInfo.state.rotate(settings.getRotation()), blockInfo.nbt)));
         }

         return result;
      }
   }

   public ObjectArrayList<StructureBlockInfo> filterBlocks(final BlockPos position, final StructurePlaceSettings settings, final Block block, final boolean absolute) {
      ObjectArrayList<StructureBlockInfo> result = new ObjectArrayList();
      BoundingBox boundingBox = settings.getBoundingBox();
      if (this.palettes.isEmpty()) {
         return result;
      } else {
         for(StructureBlockInfo blockInfo : settings.getRandomPalette(this.palettes, position).blocks(block)) {
            BlockPos blockPos = absolute ? calculateRelativePosition(settings, blockInfo.pos).offset(position) : blockInfo.pos;
            if (boundingBox == null || boundingBox.isInside(blockPos)) {
               result.add(new StructureBlockInfo(blockPos, blockInfo.state.rotate(settings.getRotation()), blockInfo.nbt));
            }
         }

         return result;
      }
   }

   public BlockPos calculateConnectedPosition(final StructurePlaceSettings settings1, final BlockPos connection1, final StructurePlaceSettings settings2, final BlockPos connection2) {
      BlockPos markerPos1 = calculateRelativePosition(settings1, connection1);
      BlockPos markerPos2 = calculateRelativePosition(settings2, connection2);
      return markerPos1.subtract(markerPos2);
   }

   public static BlockPos calculateRelativePosition(final StructurePlaceSettings settings, final BlockPos pos) {
      return transform(pos, settings.getMirror(), settings.getRotation(), settings.getRotationPivot());
   }

   public boolean placeInWorld(final ServerLevelAccessor level, final BlockPos position, final BlockPos referencePos, final StructurePlaceSettings settings, final RandomSource random, final @Block.UpdateFlags int updateMode) {
      if (this.palettes.isEmpty()) {
         return false;
      } else {
         List<StructureBlockInfo> blockInfoList = settings.getRandomPalette(this.palettes, position).blocks();
         if ((!blockInfoList.isEmpty() || !settings.isIgnoreEntities() && !this.entityInfoList.isEmpty()) && this.size.getX() >= 1 && this.size.getY() >= 1 && this.size.getZ() >= 1) {
            BoundingBox boundingBox = settings.getBoundingBox();
            List<BlockPos> toFill = Lists.newArrayListWithCapacity(settings.shouldApplyWaterlogging() ? blockInfoList.size() : 0);
            List<BlockPos> lockedFluids = Lists.newArrayListWithCapacity(settings.shouldApplyWaterlogging() ? blockInfoList.size() : 0);
            List<Pair<BlockPos, CompoundTag>> placed = Lists.newArrayListWithCapacity(blockInfoList.size());
            int minX = 2147483647;
            int minY = 2147483647;
            int minZ = 2147483647;
            int maxX = -2147483648;
            int maxY = -2147483648;
            int maxZ = -2147483648;
            List<StructureBlockInfo> processedBlockInfoList = processBlockInfos(level, position, referencePos, settings, blockInfoList);

            try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(LOGGER)) {
               for(StructureBlockInfo blockInfo : processedBlockInfoList) {
                  BlockPos blockPos = blockInfo.pos;
                  if (boundingBox == null || boundingBox.isInside(blockPos)) {
                     FluidState previousFluidState = settings.shouldApplyWaterlogging() ? level.getFluidState(blockPos) : null;
                     BlockState state = blockInfo.state.mirror(settings.getMirror()).rotate(settings.getRotation());
                     if (blockInfo.nbt != null) {
                        level.setBlock(blockPos, Blocks.BARRIER.defaultBlockState(), 820);
                     }

                     if (level.setBlock(blockPos, state, updateMode)) {
                        minX = Math.min(minX, blockPos.getX());
                        minY = Math.min(minY, blockPos.getY());
                        minZ = Math.min(minZ, blockPos.getZ());
                        maxX = Math.max(maxX, blockPos.getX());
                        maxY = Math.max(maxY, blockPos.getY());
                        maxZ = Math.max(maxZ, blockPos.getZ());
                        placed.add(Pair.of(blockPos, blockInfo.nbt));
                        if (blockInfo.nbt != null) {
                           BlockEntity blockEntity = level.getBlockEntity(blockPos);
                           if (blockEntity != null) {
                              if (!SharedConstants.DEBUG_STRUCTURE_EDIT_MODE && blockEntity instanceof RandomizableContainer) {
                                 blockInfo.nbt.putLong("LootTableSeed", random.nextLong());
                              }

                              blockEntity.loadWithComponents(TagValueInput.create(reporter.forChild(blockEntity.problemPath()), level.registryAccess(), blockInfo.nbt));
                           }
                        }

                        if (previousFluidState != null) {
                           if (state.getFluidState().isSource()) {
                              lockedFluids.add(blockPos);
                           } else if (state.getBlock() instanceof LiquidBlockContainer) {
                              ((LiquidBlockContainer)state.getBlock()).placeLiquid(level, blockPos, state, previousFluidState);
                              if (!previousFluidState.isSource()) {
                                 toFill.add(blockPos);
                              }
                           }
                        }
                     }
                  }
               }

               boolean filled = true;
               Direction[] directions = new Direction[]{Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

               while(filled && !toFill.isEmpty()) {
                  filled = false;
                  Iterator<BlockPos> iterator = toFill.iterator();

                  while(iterator.hasNext()) {
                     BlockPos pos = (BlockPos)iterator.next();
                     FluidState toPlace = level.getFluidState(pos);

                     for(int i = 0; i < directions.length && !toPlace.isSource(); ++i) {
                        BlockPos neighborPos = pos.relative(directions[i]);
                        FluidState neighbor = level.getFluidState(neighborPos);
                        if (neighbor.isSource() && !lockedFluids.contains(neighborPos)) {
                           toPlace = neighbor;
                        }
                     }

                     if (toPlace.isSource()) {
                        BlockState state = level.getBlockState(pos);
                        Block block = state.getBlock();
                        if (block instanceof LiquidBlockContainer) {
                           LiquidBlockContainer liquidBlockContainer = (LiquidBlockContainer)block;
                           liquidBlockContainer.placeLiquid(level, pos, state, toPlace);
                           filled = true;
                           iterator.remove();
                        }
                     }
                  }
               }

               if (minX <= maxX) {
                  if (!settings.getKnownShape()) {
                     DiscreteVoxelShape shape = new BitSetDiscreteVoxelShape(maxX - minX + 1, maxY - minY + 1, maxZ - minZ + 1);
                     int startX = minX;
                     int startY = minY;
                     int startZ = minZ;

                     for(Pair<BlockPos, CompoundTag> blockInfo : placed) {
                        BlockPos blockPos = (BlockPos)blockInfo.getFirst();
                        shape.fill(blockPos.getX() - startX, blockPos.getY() - startY, blockPos.getZ() - startZ);
                     }

                     updateShapeAtEdge(level, updateMode, shape, startX, startY, startZ);
                  }

                  for(Pair<BlockPos, CompoundTag> blockInfo : placed) {
                     BlockPos blockPos = (BlockPos)blockInfo.getFirst();
                     if (!settings.getKnownShape()) {
                        BlockState state = level.getBlockState(blockPos);
                        BlockState newState = Block.updateFromNeighbourShapes(state, level, blockPos);
                        if (state != newState) {
                           level.setBlock(blockPos, newState, updateMode & -2 | 16);
                        }

                        level.updateNeighborsAt(blockPos, newState.getBlock());
                     }

                     if (blockInfo.getSecond() != null) {
                        BlockEntity blockEntity = level.getBlockEntity(blockPos);
                        if (blockEntity != null) {
                           blockEntity.setChanged();
                        }
                     }
                  }
               }

               if (!settings.isIgnoreEntities()) {
                  this.placeEntities(level, position, settings.getMirror(), settings.getRotation(), settings.getRotationPivot(), boundingBox, settings.shouldFinalizeEntities(), reporter);
               }
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public static void updateShapeAtEdge(final LevelAccessor level, final @Block.UpdateFlags int updateMode, final DiscreteVoxelShape shape, final BlockPos pos) {
      updateShapeAtEdge(level, updateMode, shape, pos.getX(), pos.getY(), pos.getZ());
   }

   public static void updateShapeAtEdge(final LevelAccessor level, final @Block.UpdateFlags int updateMode, final DiscreteVoxelShape shape, final int startX, final int startY, final int startZ) {
      BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
      BlockPos.MutableBlockPos neighborPos = new BlockPos.MutableBlockPos();
      shape.forAllFaces((direction, x, y, z) -> {
         pos.set(startX + x, startY + y, startZ + z);
         neighborPos.setWithOffset(pos, (Direction)direction);
         BlockState state = level.getBlockState(pos);
         BlockState neighborState = level.getBlockState(neighborPos);
         BlockState newState = state.updateShape(level, level, pos, direction, neighborPos, neighborState, level.getRandom());
         if (state != newState) {
            level.setBlock(pos, newState, updateMode & -2);
         }

         BlockState newNeighborState = neighborState.updateShape(level, level, neighborPos, direction.getOpposite(), pos, newState, level.getRandom());
         if (neighborState != newNeighborState) {
            level.setBlock(neighborPos, newNeighborState, updateMode & -2);
         }

      });
   }

   public static List<StructureBlockInfo> processBlockInfos(final ServerLevelAccessor level, final BlockPos position, final BlockPos referencePos, final StructurePlaceSettings settings, final List<StructureBlockInfo> blockInfoList) {
      List<StructureBlockInfo> originalBlockInfoList = new ArrayList();
      List<StructureBlockInfo> processedBlockInfoList = new ArrayList();
      boolean processOnlyInCurrentChunk = true;

      for(StructureProcessor processor : settings.getProcessors()) {
         if (processor.evaluatesEntirePieceState()) {
            processOnlyInCurrentChunk = false;
            break;
         }
      }

      BoundingBox chunkBb = settings.getBoundingBox();

      for(StructureBlockInfo blockInfo : blockInfoList) {
         BlockPos blockPos = calculateRelativePosition(settings, blockInfo.pos).offset(position);
         if (!processOnlyInCurrentChunk || chunkBb == null || chunkBb.isInside(blockPos)) {
            StructureBlockInfo processedBlockInfo = new StructureBlockInfo(blockPos, blockInfo.state, blockInfo.nbt != null ? blockInfo.nbt.copy() : null);

            for(Iterator<StructureProcessor> iterator = settings.getProcessors().iterator(); processedBlockInfo != null && iterator.hasNext(); processedBlockInfo = ((StructureProcessor)iterator.next()).processBlock(level, position, referencePos, blockInfo.pos, processedBlockInfo, settings)) {
            }

            if (processedBlockInfo != null) {
               processedBlockInfoList.add(processedBlockInfo);
               originalBlockInfoList.add(blockInfo);
            }
         }
      }

      for(StructureProcessor processor : settings.getProcessors()) {
         processedBlockInfoList = processor.finalizeProcessing(level, position, referencePos, originalBlockInfoList, processedBlockInfoList, settings);
      }

      return processedBlockInfoList;
   }

   private void placeEntities(final ServerLevelAccessor level, final BlockPos position, final Mirror mirror, final Rotation rotation, final BlockPos pivot, final @Nullable BoundingBox boundingBox, final boolean finalizeEntities, final ProblemReporter problemReporter) {
      for(StructureEntityInfo entityInfo : this.entityInfoList) {
         BlockPos blockPos = transform(entityInfo.blockPos, mirror, rotation, pivot).offset(position);
         if (boundingBox == null || boundingBox.isInside(blockPos)) {
            CompoundTag tag = entityInfo.nbt.copy();
            Vec3 relativePos = transform(entityInfo.pos, mirror, rotation, pivot);
            Vec3 pos = relativePos.add((double)position.getX(), (double)position.getY(), (double)position.getZ());
            ListTag posTag = new ListTag();
            posTag.add(DoubleTag.valueOf(pos.x));
            posTag.add(DoubleTag.valueOf(pos.y));
            posTag.add(DoubleTag.valueOf(pos.z));
            tag.put("Pos", posTag);
            tag.remove("UUID");
            createEntityIgnoreException(problemReporter, level, tag).ifPresent((entity) -> {
               float yRot = entity.rotate(rotation);
               yRot += entity.mirror(mirror) - entity.getYRot();
               entity.snapTo(pos.x, pos.y, pos.z, yRot, entity.getXRot());
               entity.setYBodyRot(yRot);
               entity.setYHeadRot(yRot);
               if (finalizeEntities && entity instanceof Mob mob) {
                  mob.finalizeSpawn(level, level.getCurrentDifficultyAt(BlockPos.containing(pos)), EntitySpawnReason.STRUCTURE, (SpawnGroupData)null);
               }

               level.addFreshEntityWithPassengers(entity);
            });
         }
      }

   }

   private static Optional<Entity> createEntityIgnoreException(final ProblemReporter reporter, final ServerLevelAccessor level, final CompoundTag tag) {
      try {
         return EntityType.create(TagValueInput.create(reporter, level.registryAccess(), tag), level.getLevel(), new EntitySpawnRequest(EntitySpawnReason.STRUCTURE, false));
      } catch (Exception var4) {
         return Optional.empty();
      }
   }

   public Vec3i getSize(final Rotation rotation) {
      Vec3i var10000;
      switch (rotation) {
         case COUNTERCLOCKWISE_90:
         case CLOCKWISE_90:
            var10000 = new Vec3i(this.size.getZ(), this.size.getY(), this.size.getX());
            break;
         default:
            var10000 = this.size;
      }

      return var10000;
   }

   public static BlockPos transform(final BlockPos pos, final Mirror mirror, final Rotation rotation, final BlockPos pivot) {
      int x = pos.getX();
      int y = pos.getY();
      int z = pos.getZ();
      boolean wasMirrored = true;
      switch (mirror) {
         case LEFT_RIGHT -> z = -z;
         case FRONT_BACK -> x = -x;
         default -> wasMirrored = false;
      }

      int pivotX = pivot.getX();
      int pivotZ = pivot.getZ();
      BlockPos var10000;
      switch (rotation) {
         case COUNTERCLOCKWISE_90 -> var10000 = new BlockPos(pivotX - pivotZ + z, y, pivotX + pivotZ - x);
         case CLOCKWISE_90 -> var10000 = new BlockPos(pivotX + pivotZ - z, y, pivotZ - pivotX + x);
         case CLOCKWISE_180 -> var10000 = new BlockPos(pivotX + pivotX - x, y, pivotZ + pivotZ - z);
         default -> var10000 = wasMirrored ? new BlockPos(x, y, z) : pos;
      }

      return var10000;
   }

   public static Vec3 transform(final Vec3 pos, final Mirror mirror, final Rotation rotation, final BlockPos pivot) {
      double x = pos.x;
      double y = pos.y;
      double z = pos.z;
      boolean wasMirrored = true;
      switch (mirror) {
         case LEFT_RIGHT -> z = 1.0 - z;
         case FRONT_BACK -> x = 1.0 - x;
         default -> wasMirrored = false;
      }

      int pivotX = pivot.getX();
      int pivotZ = pivot.getZ();
      Vec3 var10000;
      switch (rotation) {
         case COUNTERCLOCKWISE_90 -> var10000 = new Vec3((double)(pivotX - pivotZ) + z, y, (double)(pivotX + pivotZ + 1) - x);
         case CLOCKWISE_90 -> var10000 = new Vec3((double)(pivotX + pivotZ + 1) - z, y, (double)(pivotZ - pivotX) + x);
         case CLOCKWISE_180 -> var10000 = new Vec3((double)(pivotX + pivotX + 1) - x, y, (double)(pivotZ + pivotZ + 1) - z);
         default -> var10000 = wasMirrored ? new Vec3(x, y, z) : pos;
      }

      return var10000;
   }

   public BlockPos getZeroPositionWithTransform(final BlockPos zeroPos, final Mirror mirror, final Rotation rotation) {
      return getZeroPositionWithTransform(zeroPos, mirror, rotation, this.getSize().getX(), this.getSize().getZ());
   }

   public static BlockPos getZeroPositionWithTransform(final BlockPos zeroPos, final Mirror mirror, final Rotation rotation, int sizeX, int sizeZ) {
      --sizeX;
      --sizeZ;
      int mirrorDeltaX = mirror == Mirror.FRONT_BACK ? sizeX : 0;
      int mirrorDeltaZ = mirror == Mirror.LEFT_RIGHT ? sizeZ : 0;
      BlockPos var10000;
      switch (rotation) {
         case COUNTERCLOCKWISE_90 -> var10000 = zeroPos.offset(mirrorDeltaZ, 0, sizeX - mirrorDeltaX);
         case CLOCKWISE_90 -> var10000 = zeroPos.offset(sizeZ - mirrorDeltaZ, 0, mirrorDeltaX);
         case CLOCKWISE_180 -> var10000 = zeroPos.offset(sizeX - mirrorDeltaX, 0, sizeZ - mirrorDeltaZ);
         case NONE -> var10000 = zeroPos.offset(mirrorDeltaX, 0, mirrorDeltaZ);
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      BlockPos targetPos = var10000;
      return targetPos;
   }

   public BoundingBox getBoundingBox(final StructurePlaceSettings settings, final BlockPos position) {
      return this.getBoundingBox(position, settings.getRotation(), settings.getRotationPivot(), settings.getMirror());
   }

   public BoundingBox getBoundingBox(final BlockPos position, final Rotation rotation, final BlockPos pivot, final Mirror mirror) {
      return getBoundingBox(position, rotation, pivot, mirror, this.size);
   }

   @VisibleForTesting
   protected static BoundingBox getBoundingBox(final BlockPos position, final Rotation rotation, final BlockPos pivot, final Mirror mirror, final Vec3i size) {
      Vec3i delta = size.offset(-1, -1, -1);
      BlockPos corner1 = transform(BlockPos.ZERO, mirror, rotation, pivot);
      BlockPos corner2 = transform(BlockPos.ZERO.offset(delta), mirror, rotation, pivot);
      return BoundingBox.fromCorners(corner1, corner2).move(position);
   }

   public CompoundTag save(final CompoundTag tag) {
      if (this.palettes.isEmpty()) {
         tag.put("blocks", new ListTag());
         tag.put("palette", new ListTag());
      } else {
         List<SimplePalette> palettes = Lists.newArrayList();
         SimplePalette mainPalette = new SimplePalette();
         palettes.add(mainPalette);

         for(int p = 1; p < this.palettes.size(); ++p) {
            palettes.add(new SimplePalette());
         }

         ListTag blockList = new ListTag();
         List<StructureBlockInfo> mainPaletteBlocks = ((Palette)this.palettes.get(0)).blocks();

         for(int i = 0; i < mainPaletteBlocks.size(); ++i) {
            StructureBlockInfo blockInfo = (StructureBlockInfo)mainPaletteBlocks.get(i);
            CompoundTag blockTag = new CompoundTag();
            blockTag.put("pos", this.newIntegerList(blockInfo.pos.getX(), blockInfo.pos.getY(), blockInfo.pos.getZ()));
            int id = mainPalette.idFor(blockInfo.state);
            blockTag.putInt("state", id);
            if (blockInfo.nbt != null) {
               blockTag.put("nbt", blockInfo.nbt);
            }

            blockList.add(blockTag);

            for(int p = 1; p < this.palettes.size(); ++p) {
               SimplePalette palette = (SimplePalette)palettes.get(p);
               palette.addMapping(((StructureBlockInfo)((Palette)this.palettes.get(p)).blocks().get(i)).state, id);
            }
         }

         tag.put("blocks", blockList);
         if (palettes.size() == 1) {
            ListTag paletteList = new ListTag();

            for(BlockState state : mainPalette) {
               paletteList.add(NbtUtils.writeBlockState(state));
            }

            tag.put("palette", paletteList);
         } else {
            ListTag paletteListList = new ListTag();

            for(SimplePalette palette : palettes) {
               ListTag paletteList = new ListTag();

               for(BlockState state : palette) {
                  paletteList.add(NbtUtils.writeBlockState(state));
               }

               paletteListList.add(paletteList);
            }

            tag.put("palettes", paletteListList);
         }
      }

      ListTag entityList = new ListTag();

      for(StructureEntityInfo entityInfo : this.entityInfoList) {
         CompoundTag entityTag = new CompoundTag();
         entityTag.put("pos", this.newDoubleList(entityInfo.pos.x, entityInfo.pos.y, entityInfo.pos.z));
         entityTag.put("blockPos", this.newIntegerList(entityInfo.blockPos.getX(), entityInfo.blockPos.getY(), entityInfo.blockPos.getZ()));
         if (entityInfo.nbt != null) {
            entityTag.put("nbt", entityInfo.nbt);
         }

         entityList.add(entityTag);
      }

      tag.put("entities", entityList);
      tag.put("size", this.newIntegerList(this.size.getX(), this.size.getY(), this.size.getZ()));
      return NbtUtils.addCurrentDataVersion(tag);
   }

   public void load(final HolderGetter<Block> blockLookup, final CompoundTag tag) {
      this.palettes.clear();
      this.entityInfoList.clear();
      ListTag sizeTag = tag.getListOrEmpty("size");
      this.size = new Vec3i(sizeTag.getIntOr(0, 0), sizeTag.getIntOr(1, 0), sizeTag.getIntOr(2, 0));
      ListTag blockList = tag.getListOrEmpty("blocks");
      Optional<ListTag> paletteListList = tag.getList("palettes");
      if (paletteListList.isPresent()) {
         for(int p = 0; p < ((ListTag)paletteListList.get()).size(); ++p) {
            this.loadPalette(blockLookup, ((ListTag)paletteListList.get()).getListOrEmpty(p), blockList);
         }
      } else {
         this.loadPalette(blockLookup, tag.getListOrEmpty("palette"), blockList);
      }

      tag.getListOrEmpty("entities").compoundStream().forEach((entityTag) -> {
         ListTag posTag = entityTag.getListOrEmpty("pos");
         Vec3 pos = new Vec3(posTag.getDoubleOr(0, 0.0), posTag.getDoubleOr(1, 0.0), posTag.getDoubleOr(2, 0.0));
         ListTag blockPosTag = entityTag.getListOrEmpty("blockPos");
         BlockPos blockPos = new BlockPos(blockPosTag.getIntOr(0, 0), blockPosTag.getIntOr(1, 0), blockPosTag.getIntOr(2, 0));
         entityTag.getCompound("nbt").ifPresent((nbt) -> this.entityInfoList.add(new StructureEntityInfo(pos, blockPos, nbt)));
      });
   }

   private void loadPalette(final HolderGetter<Block> blockLookup, final ListTag paletteList, final ListTag blockList) {
      SimplePalette palette = new SimplePalette();

      for(int i = 0; i < paletteList.size(); ++i) {
         palette.addMapping(NbtUtils.readBlockState(blockLookup, paletteList.getCompoundOrEmpty(i)), i);
      }

      List<StructureBlockInfo> fullBlockList = Lists.newArrayList();
      List<StructureBlockInfo> blockEntitiesList = Lists.newArrayList();
      List<StructureBlockInfo> otherBlocksList = Lists.newArrayList();
      blockList.compoundStream().forEach((blockTag) -> {
         ListTag posTag = blockTag.getListOrEmpty("pos");
         BlockPos pos = new BlockPos(posTag.getIntOr(0, 0), posTag.getIntOr(1, 0), posTag.getIntOr(2, 0));
         BlockState state = palette.stateFor(blockTag.getIntOr("state", 0));
         CompoundTag nbt = (CompoundTag)blockTag.getCompound("nbt").orElse((Object)null);
         StructureBlockInfo info = new StructureBlockInfo(pos, state, nbt);
         addToLists(info, fullBlockList, blockEntitiesList, otherBlocksList);
      });
      List<StructureBlockInfo> blockInfoList = buildInfoList(fullBlockList, blockEntitiesList, otherBlocksList);
      this.palettes.add(new Palette(blockInfoList));
   }

   private ListTag newIntegerList(final int... values) {
      ListTag res = new ListTag();

      for(int value : values) {
         res.add(IntTag.valueOf(value));
      }

      return res;
   }

   private ListTag newDoubleList(final double... values) {
      ListTag res = new ListTag();

      for(double value : values) {
         res.add(DoubleTag.valueOf(value));
      }

      return res;
   }

   public static JigsawBlockEntity.JointType getJointType(final CompoundTag nbt, final BlockState state) {
      return (JigsawBlockEntity.JointType)nbt.read("joint", JigsawBlockEntity.JointType.CODEC).orElseGet(() -> getDefaultJointType(state));
   }

   public static JigsawBlockEntity.JointType getDefaultJointType(final BlockState state) {
      return JigsawBlock.getFrontFacing(state).getAxis().isHorizontal() ? JigsawBlockEntity.JointType.ALIGNED : JigsawBlockEntity.JointType.ROLLABLE;
   }

   private static class SimplePalette implements Iterable<BlockState> {
      public static final BlockState DEFAULT_BLOCK_STATE;
      private final IdMapper<BlockState> ids = new IdMapper<BlockState>(16);
      private int lastId;

      private SimplePalette() {
         super();
      }

      public int idFor(final BlockState state) {
         int id = this.ids.getId(state);
         if (id == -1) {
            id = this.lastId++;
            this.ids.addMapping(state, id);
         }

         return id;
      }

      public @Nullable BlockState stateFor(final int index) {
         BlockState blockState = this.ids.byId(index);
         return blockState == null ? DEFAULT_BLOCK_STATE : blockState;
      }

      public Iterator<BlockState> iterator() {
         return this.ids.iterator();
      }

      public void addMapping(final BlockState state, final int id) {
         this.ids.addMapping(state, id);
      }

      static {
         DEFAULT_BLOCK_STATE = Blocks.AIR.defaultBlockState();
      }
   }

   public static record StructureBlockInfo(BlockPos pos, BlockState state, @Nullable CompoundTag nbt) {
      public StructureBlockInfo {
         super();
      }

      public String toString() {
         return String.format(Locale.ROOT, "<StructureBlockInfo | %s | %s | %s>", this.pos, this.state, this.nbt);
      }
   }

   public static record JigsawBlockInfo(StructureBlockInfo info, JigsawBlockEntity.JointType jointType, Identifier name, ResourceKey<StructureTemplatePool> pool, Identifier target, int placementPriority, int selectionPriority) {
      public JigsawBlockInfo {
         super();
      }

      public static JigsawBlockInfo of(final StructureBlockInfo info) {
         CompoundTag nbt = (CompoundTag)Objects.requireNonNull(info.nbt(), () -> String.valueOf(info) + " nbt was null");
         return new JigsawBlockInfo(info, StructureTemplate.getJointType(nbt, info.state()), (Identifier)nbt.read("name", Identifier.CODEC).orElse(JigsawBlockEntity.EMPTY_ID), (ResourceKey)nbt.read("pool", JigsawBlockEntity.POOL_CODEC).orElse(Pools.EMPTY), (Identifier)nbt.read("target", Identifier.CODEC).orElse(JigsawBlockEntity.EMPTY_ID), nbt.getIntOr("placement_priority", 0), nbt.getIntOr("selection_priority", 0));
      }

      public String toString() {
         return String.format(Locale.ROOT, "<JigsawBlockInfo | %s | %s | name: %s | pool: %s | target: %s | placement: %d | selection: %d | %s>", this.info.pos, this.info.state, this.name, this.pool.identifier(), this.target, this.placementPriority, this.selectionPriority, this.info.nbt);
      }

      public JigsawBlockInfo withInfo(final StructureBlockInfo info) {
         return new JigsawBlockInfo(info, this.jointType, this.name, this.pool, this.target, this.placementPriority, this.selectionPriority);
      }
   }

   public static class StructureEntityInfo {
      public final Vec3 pos;
      public final BlockPos blockPos;
      public final CompoundTag nbt;

      public StructureEntityInfo(final Vec3 pos, final BlockPos blockPos, final CompoundTag nbt) {
         super();
         this.pos = pos;
         this.blockPos = blockPos;
         this.nbt = nbt;
      }
   }

   public static final class Palette {
      private final List<StructureBlockInfo> blocks;
      private final Map<Block, List<StructureBlockInfo>> cache = Maps.newHashMap();
      private @Nullable List<JigsawBlockInfo> cachedJigsaws;

      private Palette(final List<StructureBlockInfo> blocks) {
         super();
         this.blocks = blocks;
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

      public List<StructureBlockInfo> blocks(final Block filter) {
         return (List)this.cache.computeIfAbsent(filter, (block) -> (List)this.blocks.stream().filter((b) -> b.state.is(block)).collect(Collectors.toList()));
      }
   }
}
