package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.IdMapper;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;

public class StructureTemplate {
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
         Iterator var12 = BlockPos.betweenClosed(var10, var11).iterator();

         while(true) {
            BlockPos var13;
            BlockPos var14;
            BlockState var15;
            do {
               if (!var12.hasNext()) {
                  List var18 = buildInfoList(var7, var8, var9);
                  this.palettes.clear();
                  this.palettes.add(new Palette(var18));
                  if (var4) {
                     this.fillEntityList(var1, var10, var11);
                  } else {
                     this.entityInfoList.clear();
                  }

                  return;
               }

               var13 = (BlockPos)var12.next();
               var14 = var13.subtract(var10);
               var15 = var1.getBlockState(var13);
            } while(var5 != null && var15.is(var5));

            BlockEntity var16 = var1.getBlockEntity(var13);
            StructureBlockInfo var17;
            if (var16 != null) {
               var17 = new StructureBlockInfo(var14, var15, var16.saveWithId(var1.registryAccess()));
            } else {
               var17 = new StructureBlockInfo(var14, var15, (CompoundTag)null);
            }

            addToLists(var17, var7, var8, var9);
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
      Comparator var3 = Comparator.comparingInt((var0x) -> {
         return var0x.pos.getY();
      }).thenComparingInt((var0x) -> {
         return var0x.pos.getX();
      }).thenComparingInt((var0x) -> {
         return var0x.pos.getZ();
      });
      var0.sort(var3);
      var2.sort(var3);
      var1.sort(var3);
      ArrayList var4 = Lists.newArrayList();
      var4.addAll(var0);
      var4.addAll(var2);
      var4.addAll(var1);
      return var4;
   }

   private void fillEntityList(Level var1, BlockPos var2, BlockPos var3) {
      List var4 = var1.getEntitiesOfClass(Entity.class, AABB.encapsulatingFullBlocks(var2, var3), (var0) -> {
         return !(var0 instanceof Player);
      });
      this.entityInfoList.clear();

      Vec3 var7;
      CompoundTag var8;
      BlockPos var9;
      for(Iterator var5 = var4.iterator(); var5.hasNext(); this.entityInfoList.add(new StructureEntityInfo(var7, var9, var8.copy()))) {
         Entity var6 = (Entity)var5.next();
         var7 = new Vec3(var6.getX() - (double)var2.getX(), var6.getY() - (double)var2.getY(), var6.getZ() - (double)var2.getZ());
         var8 = new CompoundTag();
         var6.save(var8);
         if (var6 instanceof Painting) {
            var9 = ((Painting)var6).getPos().subtract(var2);
         } else {
            var9 = BlockPos.containing(var7);
         }
      }

   }

   public List<StructureBlockInfo> filterBlocks(BlockPos var1, StructurePlaceSettings var2, Block var3) {
      return this.filterBlocks(var1, var2, var3, true);
   }

   public ObjectArrayList<StructureBlockInfo> filterBlocks(BlockPos var1, StructurePlaceSettings var2, Block var3, boolean var4) {
      ObjectArrayList var5 = new ObjectArrayList();
      BoundingBox var6 = var2.getBoundingBox();
      if (this.palettes.isEmpty()) {
         return var5;
      } else {
         Iterator var7 = var2.getRandomPalette(this.palettes, var1).blocks(var3).iterator();

         while(true) {
            StructureBlockInfo var8;
            BlockPos var9;
            do {
               if (!var7.hasNext()) {
                  return var5;
               }

               var8 = (StructureBlockInfo)var7.next();
               var9 = var4 ? calculateRelativePosition(var2, var8.pos).offset(var1) : var8.pos;
            } while(var6 != null && !var6.isInside(var9));

            var5.add(new StructureBlockInfo(var9, var8.state.rotate(var2.getRotation()), var8.nbt));
         }
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
            ArrayList var9 = Lists.newArrayListWithCapacity(var4.shouldKeepLiquids() ? var7.size() : 0);
            ArrayList var10 = Lists.newArrayListWithCapacity(var4.shouldKeepLiquids() ? var7.size() : 0);
            ArrayList var11 = Lists.newArrayListWithCapacity(var7.size());
            int var12 = 2147483647;
            int var13 = 2147483647;
            int var14 = 2147483647;
            int var15 = -2147483648;
            int var16 = -2147483648;
            int var17 = -2147483648;
            List var18 = processBlockInfos(var1, var2, var3, var4, var7);
            Iterator var19 = var18.iterator();

            while(true) {
               StructureBlockInfo var20;
               BlockPos var21;
               BlockEntity var24;
               do {
                  if (!var19.hasNext()) {
                     boolean var28 = true;
                     Direction[] var29 = new Direction[]{Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

                     Iterator var30;
                     int var38;
                     BlockState var39;
                     while(var28 && !var9.isEmpty()) {
                        var28 = false;
                        var30 = var9.iterator();

                        while(var30.hasNext()) {
                           BlockPos var32 = (BlockPos)var30.next();
                           FluidState var35 = var1.getFluidState(var32);

                           for(var38 = 0; var38 < var29.length && !var35.isSource(); ++var38) {
                              BlockPos var25 = var32.relative(var29[var38]);
                              FluidState var26 = var1.getFluidState(var25);
                              if (var26.isSource() && !var10.contains(var25)) {
                                 var35 = var26;
                              }
                           }

                           if (var35.isSource()) {
                              var39 = var1.getBlockState(var32);
                              Block var40 = var39.getBlock();
                              if (var40 instanceof LiquidBlockContainer) {
                                 ((LiquidBlockContainer)var40).placeLiquid(var1, var32, var39, var35);
                                 var28 = true;
                                 var30.remove();
                              }
                           }
                        }
                     }

                     if (var12 <= var15) {
                        if (!var4.getKnownShape()) {
                           BitSetDiscreteVoxelShape var31 = new BitSetDiscreteVoxelShape(var15 - var12 + 1, var16 - var13 + 1, var17 - var14 + 1);
                           int var33 = var12;
                           int var36 = var13;
                           var38 = var14;
                           Iterator var41 = var11.iterator();

                           while(var41.hasNext()) {
                              Pair var43 = (Pair)var41.next();
                              BlockPos var27 = (BlockPos)var43.getFirst();
                              ((DiscreteVoxelShape)var31).fill(var27.getX() - var33, var27.getY() - var36, var27.getZ() - var38);
                           }

                           updateShapeAtEdge(var1, var6, var31, var33, var36, var38);
                        }

                        var30 = var11.iterator();

                        while(var30.hasNext()) {
                           Pair var34 = (Pair)var30.next();
                           BlockPos var37 = (BlockPos)var34.getFirst();
                           if (!var4.getKnownShape()) {
                              var39 = var1.getBlockState(var37);
                              BlockState var42 = Block.updateFromNeighbourShapes(var39, var1, var37);
                              if (var39 != var42) {
                                 var1.setBlock(var37, var42, var6 & -2 | 16);
                              }

                              var1.blockUpdated(var37, var42.getBlock());
                           }

                           if (var34.getSecond() != null) {
                              var24 = var1.getBlockEntity(var37);
                              if (var24 != null) {
                                 var24.setChanged();
                              }
                           }
                        }
                     }

                     if (!var4.isIgnoreEntities()) {
                        this.placeEntities(var1, var2, var4.getMirror(), var4.getRotation(), var4.getRotationPivot(), var8, var4.shouldFinalizeEntities());
                     }

                     return true;
                  }

                  var20 = (StructureBlockInfo)var19.next();
                  var21 = var20.pos;
               } while(var8 != null && !var8.isInside(var21));

               FluidState var22 = var4.shouldKeepLiquids() ? var1.getFluidState(var21) : null;
               BlockState var23 = var20.state.mirror(var4.getMirror()).rotate(var4.getRotation());
               if (var20.nbt != null) {
                  var24 = var1.getBlockEntity(var21);
                  Clearable.tryClear(var24);
                  var1.setBlock(var21, Blocks.BARRIER.defaultBlockState(), 20);
               }

               if (var1.setBlock(var21, var23, var6)) {
                  var12 = Math.min(var12, var21.getX());
                  var13 = Math.min(var13, var21.getY());
                  var14 = Math.min(var14, var21.getZ());
                  var15 = Math.max(var15, var21.getX());
                  var16 = Math.max(var16, var21.getY());
                  var17 = Math.max(var17, var21.getZ());
                  var11.add(Pair.of(var21, var20.nbt));
                  if (var20.nbt != null) {
                     var24 = var1.getBlockEntity(var21);
                     if (var24 != null) {
                        if (var24 instanceof RandomizableContainer) {
                           var20.nbt.putLong("LootTableSeed", var5.nextLong());
                        }

                        var24.loadWithComponents(var20.nbt, var1.registryAccess());
                     }
                  }

                  if (var22 != null) {
                     if (var23.getFluidState().isSource()) {
                        var10.add(var21);
                     } else if (var23.getBlock() instanceof LiquidBlockContainer) {
                        ((LiquidBlockContainer)var23.getBlock()).placeLiquid(var1, var21, var23, var22);
                        if (!var22.isSource()) {
                           var9.add(var21);
                        }
                     }
                  }
               }
            }
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
         BlockState var13 = var11.updateShape(var7x, var12, var0, var6, var7);
         if (var11 != var13) {
            var0.setBlock(var6, var13, var1 & -2);
         }

         BlockState var14 = var12.updateShape(var7x.getOpposite(), var13, var0, var7, var6);
         if (var12 != var14) {
            var0.setBlock(var7, var14, var1 & -2);
         }

      });
   }

   public static List<StructureBlockInfo> processBlockInfos(ServerLevelAccessor var0, BlockPos var1, BlockPos var2, StructurePlaceSettings var3, List<StructureBlockInfo> var4) {
      ArrayList var5 = new ArrayList();
      Object var6 = new ArrayList();
      Iterator var7 = var4.iterator();

      while(var7.hasNext()) {
         StructureBlockInfo var8 = (StructureBlockInfo)var7.next();
         BlockPos var9 = calculateRelativePosition(var3, var8.pos).offset(var1);
         StructureBlockInfo var10 = new StructureBlockInfo(var9, var8.state, var8.nbt != null ? var8.nbt.copy() : null);

         for(Iterator var11 = var3.getProcessors().iterator(); var10 != null && var11.hasNext(); var10 = ((StructureProcessor)var11.next()).processBlock(var0, var1, var2, var8, var10, var3)) {
         }

         if (var10 != null) {
            ((List)var6).add(var10);
            var5.add(var8);
         }
      }

      StructureProcessor var12;
      for(var7 = var3.getProcessors().iterator(); var7.hasNext(); var6 = var12.finalizeProcessing(var0, var1, var2, var5, (List)var6, var3)) {
         var12 = (StructureProcessor)var7.next();
      }

      return (List)var6;
   }

   private void placeEntities(ServerLevelAccessor var1, BlockPos var2, Mirror var3, Rotation var4, BlockPos var5, @Nullable BoundingBox var6, boolean var7) {
      Iterator var8 = this.entityInfoList.iterator();

      while(true) {
         StructureEntityInfo var9;
         BlockPos var10;
         do {
            if (!var8.hasNext()) {
               return;
            }

            var9 = (StructureEntityInfo)var8.next();
            var10 = transform(var9.blockPos, var3, var4, var5).offset(var2);
         } while(var6 != null && !var6.isInside(var10));

         CompoundTag var11 = var9.nbt.copy();
         Vec3 var12 = transform(var9.pos, var3, var4, var5);
         Vec3 var13 = var12.add((double)var2.getX(), (double)var2.getY(), (double)var2.getZ());
         ListTag var14 = new ListTag();
         var14.add(DoubleTag.valueOf(var13.x));
         var14.add(DoubleTag.valueOf(var13.y));
         var14.add(DoubleTag.valueOf(var13.z));
         var11.put("Pos", var14);
         var11.remove("UUID");
         createEntityIgnoreException(var1, var11).ifPresent((var5x) -> {
            float var6 = var5x.rotate(var4);
            var6 += var5x.mirror(var3) - var5x.getYRot();
            var5x.moveTo(var13.x, var13.y, var13.z, var6, var5x.getXRot());
            if (var7 && var5x instanceof Mob) {
               ((Mob)var5x).finalizeSpawn(var1, var1.getCurrentDifficultyAt(BlockPos.containing(var13)), MobSpawnType.STRUCTURE, (SpawnGroupData)null);
            }

            var1.addFreshEntityWithPassengers(var5x);
         });
      }
   }

   private static Optional<Entity> createEntityIgnoreException(ServerLevelAccessor var0, CompoundTag var1) {
      try {
         return EntityType.create(var1, var0.getLevel());
      } catch (Exception var3) {
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
         ListTag var17;
         Iterator var18;
         if (var2.size() == 1) {
            var17 = new ListTag();
            var18 = var3.iterator();

            while(var18.hasNext()) {
               BlockState var19 = (BlockState)var18.next();
               var17.add(NbtUtils.writeBlockState(var19));
            }

            var1.put("palette", var17);
         } else {
            var17 = new ListTag();
            var18 = var2.iterator();

            while(var18.hasNext()) {
               SimplePalette var20 = (SimplePalette)var18.next();
               ListTag var21 = new ListTag();
               Iterator var22 = var20.iterator();

               while(var22.hasNext()) {
                  BlockState var23 = (BlockState)var22.next();
                  var21.add(NbtUtils.writeBlockState(var23));
               }

               var17.add(var21);
            }

            var1.put("palettes", var17);
         }
      }

      ListTag var12 = new ListTag();

      CompoundTag var16;
      for(Iterator var13 = this.entityInfoList.iterator(); var13.hasNext(); var12.add(var16)) {
         StructureEntityInfo var15 = (StructureEntityInfo)var13.next();
         var16 = new CompoundTag();
         var16.put("pos", this.newDoubleList(var15.pos.x, var15.pos.y, var15.pos.z));
         var16.put("blockPos", this.newIntegerList(var15.blockPos.getX(), var15.blockPos.getY(), var15.blockPos.getZ()));
         if (var15.nbt != null) {
            var16.put("nbt", var15.nbt);
         }
      }

      var1.put("entities", var12);
      var1.put("size", this.newIntegerList(this.size.getX(), this.size.getY(), this.size.getZ()));
      return NbtUtils.addCurrentDataVersion(var1);
   }

   public void load(HolderGetter<Block> var1, CompoundTag var2) {
      this.palettes.clear();
      this.entityInfoList.clear();
      ListTag var3 = var2.getList("size", 3);
      this.size = new Vec3i(var3.getInt(0), var3.getInt(1), var3.getInt(2));
      ListTag var4 = var2.getList("blocks", 10);
      ListTag var5;
      int var6;
      if (var2.contains("palettes", 9)) {
         var5 = var2.getList("palettes", 9);

         for(var6 = 0; var6 < var5.size(); ++var6) {
            this.loadPalette(var1, var5.getList(var6), var4);
         }
      } else {
         this.loadPalette(var1, var2.getList("palette", 10), var4);
      }

      var5 = var2.getList("entities", 10);

      for(var6 = 0; var6 < var5.size(); ++var6) {
         CompoundTag var7 = var5.getCompound(var6);
         ListTag var8 = var7.getList("pos", 6);
         Vec3 var9 = new Vec3(var8.getDouble(0), var8.getDouble(1), var8.getDouble(2));
         ListTag var10 = var7.getList("blockPos", 3);
         BlockPos var11 = new BlockPos(var10.getInt(0), var10.getInt(1), var10.getInt(2));
         if (var7.contains("nbt")) {
            CompoundTag var12 = var7.getCompound("nbt");
            this.entityInfoList.add(new StructureEntityInfo(var9, var11, var12));
         }
      }

   }

   private void loadPalette(HolderGetter<Block> var1, ListTag var2, ListTag var3) {
      SimplePalette var4 = new SimplePalette();

      for(int var5 = 0; var5 < var2.size(); ++var5) {
         var4.addMapping(NbtUtils.readBlockState(var1, var2.getCompound(var5)), var5);
      }

      ArrayList var15 = Lists.newArrayList();
      ArrayList var6 = Lists.newArrayList();
      ArrayList var7 = Lists.newArrayList();

      for(int var8 = 0; var8 < var3.size(); ++var8) {
         CompoundTag var9 = var3.getCompound(var8);
         ListTag var10 = var9.getList("pos", 3);
         BlockPos var11 = new BlockPos(var10.getInt(0), var10.getInt(1), var10.getInt(2));
         BlockState var12 = var4.stateFor(var9.getInt("state"));
         CompoundTag var13;
         if (var9.contains("nbt")) {
            var13 = var9.getCompound("nbt");
         } else {
            var13 = null;
         }

         StructureBlockInfo var14 = new StructureBlockInfo(var11, var12, var13);
         addToLists(var14, var15, var6, var7);
      }

      List var16 = buildInfoList(var15, var6, var7);
      this.palettes.add(new Palette(var16));
   }

   private ListTag newIntegerList(int... var1) {
      ListTag var2 = new ListTag();
      int[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         int var6 = var3[var5];
         var2.add(IntTag.valueOf(var6));
      }

      return var2;
   }

   private ListTag newDoubleList(double... var1) {
      ListTag var2 = new ListTag();
      double[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         double var6 = var3[var5];
         var2.add(DoubleTag.valueOf(var6));
      }

      return var2;
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

      public BlockPos pos() {
         return this.pos;
      }

      public BlockState state() {
         return this.state;
      }

      @Nullable
      public CompoundTag nbt() {
         return this.nbt;
      }
   }

   public static final class Palette {
      private final List<StructureBlockInfo> blocks;
      private final Map<Block, List<StructureBlockInfo>> cache = Maps.newHashMap();

      Palette(List<StructureBlockInfo> var1) {
         super();
         this.blocks = var1;
      }

      public List<StructureBlockInfo> blocks() {
         return this.blocks;
      }

      public List<StructureBlockInfo> blocks(Block var1) {
         return (List)this.cache.computeIfAbsent(var1, (var1x) -> {
            return (List)this.blocks.stream().filter((var1) -> {
               return var1.state.is(var1x);
            }).collect(Collectors.toList());
         });
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

   private static class SimplePalette implements Iterable<BlockState> {
      public static final BlockState DEFAULT_BLOCK_STATE;
      private final IdMapper<BlockState> ids = new IdMapper(16);
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
         BlockState var2 = (BlockState)this.ids.byId(var1);
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
}
