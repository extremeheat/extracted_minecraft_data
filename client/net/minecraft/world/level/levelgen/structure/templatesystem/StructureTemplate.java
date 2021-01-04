package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.IdMapper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.Clearable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
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
   private final List<List<StructureTemplate.StructureBlockInfo>> palettes = Lists.newArrayList();
   private final List<StructureTemplate.StructureEntityInfo> entityInfoList = Lists.newArrayList();
   private BlockPos size;
   private String author;

   public StructureTemplate() {
      super();
      this.size = BlockPos.ZERO;
      this.author = "?";
   }

   public BlockPos getSize() {
      return this.size;
   }

   public void setAuthor(String var1) {
      this.author = var1;
   }

   public String getAuthor() {
      return this.author;
   }

   public void fillFromWorld(Level var1, BlockPos var2, BlockPos var3, boolean var4, @Nullable Block var5) {
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
            while(true) {
               BlockPos var13;
               BlockPos var14;
               BlockState var15;
               do {
                  if (!var12.hasNext()) {
                     ArrayList var18 = Lists.newArrayList();
                     var18.addAll(var7);
                     var18.addAll(var8);
                     var18.addAll(var9);
                     this.palettes.clear();
                     this.palettes.add(var18);
                     if (var4) {
                        this.fillEntityList(var1, var10, var11.offset(1, 1, 1));
                     } else {
                        this.entityInfoList.clear();
                     }

                     return;
                  }

                  var13 = (BlockPos)var12.next();
                  var14 = var13.subtract(var10);
                  var15 = var1.getBlockState(var13);
               } while(var5 != null && var5 == var15.getBlock());

               BlockEntity var16 = var1.getBlockEntity(var13);
               if (var16 != null) {
                  CompoundTag var17 = var16.save(new CompoundTag());
                  var17.remove("x");
                  var17.remove("y");
                  var17.remove("z");
                  var8.add(new StructureTemplate.StructureBlockInfo(var14, var15, var17));
               } else if (!var15.isSolidRender(var1, var13) && !var15.isCollisionShapeFullBlock(var1, var13)) {
                  var9.add(new StructureTemplate.StructureBlockInfo(var14, var15, (CompoundTag)null));
               } else {
                  var7.add(new StructureTemplate.StructureBlockInfo(var14, var15, (CompoundTag)null));
               }
            }
         }
      }
   }

   private void fillEntityList(Level var1, BlockPos var2, BlockPos var3) {
      List var4 = var1.getEntitiesOfClass(Entity.class, new AABB(var2, var3), (var0) -> {
         return !(var0 instanceof Player);
      });
      this.entityInfoList.clear();

      Vec3 var7;
      CompoundTag var8;
      BlockPos var9;
      for(Iterator var5 = var4.iterator(); var5.hasNext(); this.entityInfoList.add(new StructureTemplate.StructureEntityInfo(var7, var9, var8))) {
         Entity var6 = (Entity)var5.next();
         var7 = new Vec3(var6.x - (double)var2.getX(), var6.y - (double)var2.getY(), var6.z - (double)var2.getZ());
         var8 = new CompoundTag();
         var6.save(var8);
         if (var6 instanceof Painting) {
            var9 = ((Painting)var6).getPos().subtract(var2);
         } else {
            var9 = new BlockPos(var7);
         }
      }

   }

   public List<StructureTemplate.StructureBlockInfo> filterBlocks(BlockPos var1, StructurePlaceSettings var2, Block var3) {
      return this.filterBlocks(var1, var2, var3, true);
   }

   public List<StructureTemplate.StructureBlockInfo> filterBlocks(BlockPos var1, StructurePlaceSettings var2, Block var3, boolean var4) {
      ArrayList var5 = Lists.newArrayList();
      BoundingBox var6 = var2.getBoundingBox();
      Iterator var7 = var2.getPalette(this.palettes, var1).iterator();

      while(true) {
         StructureTemplate.StructureBlockInfo var8;
         BlockPos var9;
         do {
            if (!var7.hasNext()) {
               return var5;
            }

            var8 = (StructureTemplate.StructureBlockInfo)var7.next();
            var9 = var4 ? calculateRelativePosition(var2, var8.pos).offset(var1) : var8.pos;
         } while(var6 != null && !var6.isInside(var9));

         BlockState var10 = var8.state;
         if (var10.getBlock() == var3) {
            var5.add(new StructureTemplate.StructureBlockInfo(var9, var10.rotate(var2.getRotation()), var8.nbt));
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

   public void placeInWorldChunk(LevelAccessor var1, BlockPos var2, StructurePlaceSettings var3) {
      var3.updateBoundingBoxFromChunkPos();
      this.placeInWorld(var1, var2, var3);
   }

   public void placeInWorld(LevelAccessor var1, BlockPos var2, StructurePlaceSettings var3) {
      this.placeInWorld(var1, var2, var3, 2);
   }

   public boolean placeInWorld(LevelAccessor var1, BlockPos var2, StructurePlaceSettings var3, int var4) {
      if (this.palettes.isEmpty()) {
         return false;
      } else {
         List var5 = var3.getPalette(this.palettes, var2);
         if ((!var5.isEmpty() || !var3.isIgnoreEntities() && !this.entityInfoList.isEmpty()) && this.size.getX() >= 1 && this.size.getY() >= 1 && this.size.getZ() >= 1) {
            BoundingBox var6 = var3.getBoundingBox();
            ArrayList var7 = Lists.newArrayListWithCapacity(var3.shouldKeepLiquids() ? var5.size() : 0);
            ArrayList var8 = Lists.newArrayListWithCapacity(var5.size());
            int var9 = 2147483647;
            int var10 = 2147483647;
            int var11 = 2147483647;
            int var12 = -2147483648;
            int var13 = -2147483648;
            int var14 = -2147483648;
            List var15 = processBlockInfos(var1, var2, var3, var5);
            Iterator var16 = var15.iterator();

            while(true) {
               StructureTemplate.StructureBlockInfo var17;
               BlockPos var18;
               BlockEntity var21;
               do {
                  if (!var16.hasNext()) {
                     boolean var25 = true;
                     Direction[] var26 = new Direction[]{Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

                     Iterator var27;
                     BlockPos var32;
                     BlockState var36;
                     while(var25 && !var7.isEmpty()) {
                        var25 = false;
                        var27 = var7.iterator();

                        while(var27.hasNext()) {
                           BlockPos var29 = (BlockPos)var27.next();
                           var32 = var29;
                           FluidState var34 = var1.getFluidState(var29);

                           for(int var22 = 0; var22 < var26.length && !var34.isSource(); ++var22) {
                              BlockPos var23 = var32.relative(var26[var22]);
                              FluidState var24 = var1.getFluidState(var23);
                              if (var24.getHeight(var1, var23) > var34.getHeight(var1, var32) || var24.isSource() && !var34.isSource()) {
                                 var34 = var24;
                                 var32 = var23;
                              }
                           }

                           if (var34.isSource()) {
                              var36 = var1.getBlockState(var29);
                              Block var39 = var36.getBlock();
                              if (var39 instanceof LiquidBlockContainer) {
                                 ((LiquidBlockContainer)var39).placeLiquid(var1, var29, var36, var34);
                                 var25 = true;
                                 var27.remove();
                              }
                           }
                        }
                     }

                     if (var9 <= var12) {
                        if (!var3.getKnownShape()) {
                           BitSetDiscreteVoxelShape var28 = new BitSetDiscreteVoxelShape(var12 - var9 + 1, var13 - var10 + 1, var14 - var11 + 1);
                           int var30 = var9;
                           int var33 = var10;
                           int var35 = var11;
                           Iterator var38 = var8.iterator();

                           while(var38.hasNext()) {
                              Pair var40 = (Pair)var38.next();
                              BlockPos var41 = (BlockPos)var40.getFirst();
                              var28.setFull(var41.getX() - var30, var41.getY() - var33, var41.getZ() - var35, true, true);
                           }

                           updateShapeAtEdge(var1, var4, var28, var30, var33, var35);
                        }

                        var27 = var8.iterator();

                        while(var27.hasNext()) {
                           Pair var31 = (Pair)var27.next();
                           var32 = (BlockPos)var31.getFirst();
                           if (!var3.getKnownShape()) {
                              BlockState var37 = var1.getBlockState(var32);
                              var36 = Block.updateFromNeighbourShapes(var37, var1, var32);
                              if (var37 != var36) {
                                 var1.setBlock(var32, var36, var4 & -2 | 16);
                              }

                              var1.blockUpdated(var32, var36.getBlock());
                           }

                           if (var31.getSecond() != null) {
                              var21 = var1.getBlockEntity(var32);
                              if (var21 != null) {
                                 var21.setChanged();
                              }
                           }
                        }
                     }

                     if (!var3.isIgnoreEntities()) {
                        this.placeEntities(var1, var2, var3.getMirror(), var3.getRotation(), var3.getRotationPivot(), var6);
                     }

                     return true;
                  }

                  var17 = (StructureTemplate.StructureBlockInfo)var16.next();
                  var18 = var17.pos;
               } while(var6 != null && !var6.isInside(var18));

               FluidState var19 = var3.shouldKeepLiquids() ? var1.getFluidState(var18) : null;
               BlockState var20 = var17.state.mirror(var3.getMirror()).rotate(var3.getRotation());
               if (var17.nbt != null) {
                  var21 = var1.getBlockEntity(var18);
                  Clearable.tryClear(var21);
                  var1.setBlock(var18, Blocks.BARRIER.defaultBlockState(), 20);
               }

               if (var1.setBlock(var18, var20, var4)) {
                  var9 = Math.min(var9, var18.getX());
                  var10 = Math.min(var10, var18.getY());
                  var11 = Math.min(var11, var18.getZ());
                  var12 = Math.max(var12, var18.getX());
                  var13 = Math.max(var13, var18.getY());
                  var14 = Math.max(var14, var18.getZ());
                  var8.add(Pair.of(var18, var17.nbt));
                  if (var17.nbt != null) {
                     var21 = var1.getBlockEntity(var18);
                     if (var21 != null) {
                        var17.nbt.putInt("x", var18.getX());
                        var17.nbt.putInt("y", var18.getY());
                        var17.nbt.putInt("z", var18.getZ());
                        var21.load(var17.nbt);
                        var21.mirror(var3.getMirror());
                        var21.rotate(var3.getRotation());
                     }
                  }

                  if (var19 != null && var20.getBlock() instanceof LiquidBlockContainer) {
                     ((LiquidBlockContainer)var20.getBlock()).placeLiquid(var1, var18, var20, var19);
                     if (!var19.isSource()) {
                        var7.add(var18);
                     }
                  }
               }
            }
         } else {
            return false;
         }
      }
   }

   public static void updateShapeAtEdge(LevelAccessor var0, int var1, DiscreteVoxelShape var2, int var3, int var4, int var5) {
      var2.forAllFaces((var5x, var6, var7, var8) -> {
         BlockPos var9 = new BlockPos(var3 + var6, var4 + var7, var5 + var8);
         BlockPos var10 = var9.relative(var5x);
         BlockState var11 = var0.getBlockState(var9);
         BlockState var12 = var0.getBlockState(var10);
         BlockState var13 = var11.updateShape(var5x, var12, var0, var9, var10);
         if (var11 != var13) {
            var0.setBlock(var9, var13, var1 & -2 | 16);
         }

         BlockState var14 = var12.updateShape(var5x.getOpposite(), var13, var0, var10, var9);
         if (var12 != var14) {
            var0.setBlock(var10, var14, var1 & -2 | 16);
         }

      });
   }

   public static List<StructureTemplate.StructureBlockInfo> processBlockInfos(LevelAccessor var0, BlockPos var1, StructurePlaceSettings var2, List<StructureTemplate.StructureBlockInfo> var3) {
      ArrayList var4 = Lists.newArrayList();
      Iterator var5 = var3.iterator();

      while(var5.hasNext()) {
         StructureTemplate.StructureBlockInfo var6 = (StructureTemplate.StructureBlockInfo)var5.next();
         BlockPos var7 = calculateRelativePosition(var2, var6.pos).offset(var1);
         StructureTemplate.StructureBlockInfo var8 = new StructureTemplate.StructureBlockInfo(var7, var6.state, var6.nbt);

         for(Iterator var9 = var2.getProcessors().iterator(); var8 != null && var9.hasNext(); var8 = ((StructureProcessor)var9.next()).processBlock(var0, var1, var6, var8, var2)) {
         }

         if (var8 != null) {
            var4.add(var8);
         }
      }

      return var4;
   }

   private void placeEntities(LevelAccessor var1, BlockPos var2, Mirror var3, Rotation var4, BlockPos var5, @Nullable BoundingBox var6) {
      Iterator var7 = this.entityInfoList.iterator();

      while(true) {
         StructureTemplate.StructureEntityInfo var8;
         BlockPos var9;
         do {
            if (!var7.hasNext()) {
               return;
            }

            var8 = (StructureTemplate.StructureEntityInfo)var7.next();
            var9 = transform(var8.blockPos, var3, var4, var5).offset(var2);
         } while(var6 != null && !var6.isInside(var9));

         CompoundTag var10 = var8.nbt;
         Vec3 var11 = transform(var8.pos, var3, var4, var5);
         Vec3 var12 = var11.add((double)var2.getX(), (double)var2.getY(), (double)var2.getZ());
         ListTag var13 = new ListTag();
         var13.add(new DoubleTag(var12.x));
         var13.add(new DoubleTag(var12.y));
         var13.add(new DoubleTag(var12.z));
         var10.put("Pos", var13);
         var10.remove("UUIDMost");
         var10.remove("UUIDLeast");
         createEntityIgnoreException(var1, var10).ifPresent((var4x) -> {
            float var5 = var4x.mirror(var3);
            var5 += var4x.yRot - var4x.rotate(var4);
            var4x.moveTo(var12.x, var12.y, var12.z, var5, var4x.xRot);
            var1.addFreshEntity(var4x);
         });
      }
   }

   private static Optional<Entity> createEntityIgnoreException(LevelAccessor var0, CompoundTag var1) {
      try {
         return EntityType.create(var1, var0.getLevel());
      } catch (Exception var3) {
         return Optional.empty();
      }
   }

   public BlockPos getSize(Rotation var1) {
      switch(var1) {
      case COUNTERCLOCKWISE_90:
      case CLOCKWISE_90:
         return new BlockPos(this.size.getZ(), this.size.getY(), this.size.getX());
      default:
         return this.size;
      }
   }

   public static BlockPos transform(BlockPos var0, Mirror var1, Rotation var2, BlockPos var3) {
      int var4 = var0.getX();
      int var5 = var0.getY();
      int var6 = var0.getZ();
      boolean var7 = true;
      switch(var1) {
      case LEFT_RIGHT:
         var6 = -var6;
         break;
      case FRONT_BACK:
         var4 = -var4;
         break;
      default:
         var7 = false;
      }

      int var8 = var3.getX();
      int var9 = var3.getZ();
      switch(var2) {
      case COUNTERCLOCKWISE_90:
         return new BlockPos(var8 - var9 + var6, var5, var8 + var9 - var4);
      case CLOCKWISE_90:
         return new BlockPos(var8 + var9 - var6, var5, var9 - var8 + var4);
      case CLOCKWISE_180:
         return new BlockPos(var8 + var8 - var4, var5, var9 + var9 - var6);
      default:
         return var7 ? new BlockPos(var4, var5, var6) : var0;
      }
   }

   private static Vec3 transform(Vec3 var0, Mirror var1, Rotation var2, BlockPos var3) {
      double var4 = var0.x;
      double var6 = var0.y;
      double var8 = var0.z;
      boolean var10 = true;
      switch(var1) {
      case LEFT_RIGHT:
         var8 = 1.0D - var8;
         break;
      case FRONT_BACK:
         var4 = 1.0D - var4;
         break;
      default:
         var10 = false;
      }

      int var11 = var3.getX();
      int var12 = var3.getZ();
      switch(var2) {
      case COUNTERCLOCKWISE_90:
         return new Vec3((double)(var11 - var12) + var8, var6, (double)(var11 + var12 + 1) - var4);
      case CLOCKWISE_90:
         return new Vec3((double)(var11 + var12 + 1) - var8, var6, (double)(var12 - var11) + var4);
      case CLOCKWISE_180:
         return new Vec3((double)(var11 + var11 + 1) - var4, var6, (double)(var12 + var12 + 1) - var8);
      default:
         return var10 ? new Vec3(var4, var6, var8) : var0;
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
      switch(var2) {
      case COUNTERCLOCKWISE_90:
         var7 = var0.offset(var6, 0, var3 - var5);
         break;
      case CLOCKWISE_90:
         var7 = var0.offset(var4 - var6, 0, var5);
         break;
      case CLOCKWISE_180:
         var7 = var0.offset(var3 - var5, 0, var4 - var6);
         break;
      case NONE:
         var7 = var0.offset(var5, 0, var6);
      }

      return var7;
   }

   public BoundingBox getBoundingBox(StructurePlaceSettings var1, BlockPos var2) {
      Rotation var3 = var1.getRotation();
      BlockPos var4 = var1.getRotationPivot();
      BlockPos var5 = this.getSize(var3);
      Mirror var6 = var1.getMirror();
      int var7 = var4.getX();
      int var8 = var4.getZ();
      int var9 = var5.getX() - 1;
      int var10 = var5.getY() - 1;
      int var11 = var5.getZ() - 1;
      BoundingBox var12 = new BoundingBox(0, 0, 0, 0, 0, 0);
      switch(var3) {
      case COUNTERCLOCKWISE_90:
         var12 = new BoundingBox(var7 - var8, 0, var7 + var8 - var11, var7 - var8 + var9, var10, var7 + var8);
         break;
      case CLOCKWISE_90:
         var12 = new BoundingBox(var7 + var8 - var9, 0, var8 - var7, var7 + var8, var10, var8 - var7 + var11);
         break;
      case CLOCKWISE_180:
         var12 = new BoundingBox(var7 + var7 - var9, 0, var8 + var8 - var11, var7 + var7, var10, var8 + var8);
         break;
      case NONE:
         var12 = new BoundingBox(0, 0, 0, var9, var10, var11);
      }

      switch(var6) {
      case LEFT_RIGHT:
         this.mirrorAABB(var3, var11, var9, var12, Direction.NORTH, Direction.SOUTH);
         break;
      case FRONT_BACK:
         this.mirrorAABB(var3, var9, var11, var12, Direction.WEST, Direction.EAST);
      case NONE:
      }

      var12.move(var2.getX(), var2.getY(), var2.getZ());
      return var12;
   }

   private void mirrorAABB(Rotation var1, int var2, int var3, BoundingBox var4, Direction var5, Direction var6) {
      BlockPos var7 = BlockPos.ZERO;
      if (var1 != Rotation.CLOCKWISE_90 && var1 != Rotation.COUNTERCLOCKWISE_90) {
         if (var1 == Rotation.CLOCKWISE_180) {
            var7 = var7.relative(var6, var2);
         } else {
            var7 = var7.relative(var5, var2);
         }
      } else {
         var7 = var7.relative(var1.rotate(var5), var3);
      }

      var4.move(var7.getX(), 0, var7.getZ());
   }

   public CompoundTag save(CompoundTag var1) {
      if (this.palettes.isEmpty()) {
         var1.put("blocks", new ListTag());
         var1.put("palette", new ListTag());
      } else {
         ArrayList var2 = Lists.newArrayList();
         StructureTemplate.SimplePalette var3 = new StructureTemplate.SimplePalette();
         var2.add(var3);

         for(int var4 = 1; var4 < this.palettes.size(); ++var4) {
            var2.add(new StructureTemplate.SimplePalette());
         }

         ListTag var14 = new ListTag();
         List var5 = (List)this.palettes.get(0);

         for(int var6 = 0; var6 < var5.size(); ++var6) {
            StructureTemplate.StructureBlockInfo var7 = (StructureTemplate.StructureBlockInfo)var5.get(var6);
            CompoundTag var8 = new CompoundTag();
            var8.put("pos", this.newIntegerList(var7.pos.getX(), var7.pos.getY(), var7.pos.getZ()));
            int var9 = var3.idFor(var7.state);
            var8.putInt("state", var9);
            if (var7.nbt != null) {
               var8.put("nbt", var7.nbt);
            }

            var14.add(var8);

            for(int var10 = 1; var10 < this.palettes.size(); ++var10) {
               StructureTemplate.SimplePalette var11 = (StructureTemplate.SimplePalette)var2.get(var10);
               var11.addMapping(((StructureTemplate.StructureBlockInfo)((List)this.palettes.get(var10)).get(var6)).state, var9);
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
               StructureTemplate.SimplePalette var20 = (StructureTemplate.SimplePalette)var18.next();
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
         StructureTemplate.StructureEntityInfo var15 = (StructureTemplate.StructureEntityInfo)var13.next();
         var16 = new CompoundTag();
         var16.put("pos", this.newDoubleList(var15.pos.x, var15.pos.y, var15.pos.z));
         var16.put("blockPos", this.newIntegerList(var15.blockPos.getX(), var15.blockPos.getY(), var15.blockPos.getZ()));
         if (var15.nbt != null) {
            var16.put("nbt", var15.nbt);
         }
      }

      var1.put("entities", var12);
      var1.put("size", this.newIntegerList(this.size.getX(), this.size.getY(), this.size.getZ()));
      var1.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());
      return var1;
   }

   public void load(CompoundTag var1) {
      this.palettes.clear();
      this.entityInfoList.clear();
      ListTag var2 = var1.getList("size", 3);
      this.size = new BlockPos(var2.getInt(0), var2.getInt(1), var2.getInt(2));
      ListTag var3 = var1.getList("blocks", 10);
      ListTag var4;
      int var5;
      if (var1.contains("palettes", 9)) {
         var4 = var1.getList("palettes", 9);

         for(var5 = 0; var5 < var4.size(); ++var5) {
            this.loadPalette(var4.getList(var5), var3);
         }
      } else {
         this.loadPalette(var1.getList("palette", 10), var3);
      }

      var4 = var1.getList("entities", 10);

      for(var5 = 0; var5 < var4.size(); ++var5) {
         CompoundTag var6 = var4.getCompound(var5);
         ListTag var7 = var6.getList("pos", 6);
         Vec3 var8 = new Vec3(var7.getDouble(0), var7.getDouble(1), var7.getDouble(2));
         ListTag var9 = var6.getList("blockPos", 3);
         BlockPos var10 = new BlockPos(var9.getInt(0), var9.getInt(1), var9.getInt(2));
         if (var6.contains("nbt")) {
            CompoundTag var11 = var6.getCompound("nbt");
            this.entityInfoList.add(new StructureTemplate.StructureEntityInfo(var8, var10, var11));
         }
      }

   }

   private void loadPalette(ListTag var1, ListTag var2) {
      StructureTemplate.SimplePalette var3 = new StructureTemplate.SimplePalette();
      ArrayList var4 = Lists.newArrayList();

      int var5;
      for(var5 = 0; var5 < var1.size(); ++var5) {
         var3.addMapping(NbtUtils.readBlockState(var1.getCompound(var5)), var5);
      }

      for(var5 = 0; var5 < var2.size(); ++var5) {
         CompoundTag var6 = var2.getCompound(var5);
         ListTag var7 = var6.getList("pos", 3);
         BlockPos var8 = new BlockPos(var7.getInt(0), var7.getInt(1), var7.getInt(2));
         BlockState var9 = var3.stateFor(var6.getInt("state"));
         CompoundTag var10;
         if (var6.contains("nbt")) {
            var10 = var6.getCompound("nbt");
         } else {
            var10 = null;
         }

         var4.add(new StructureTemplate.StructureBlockInfo(var8, var9, var10));
      }

      var4.sort(Comparator.comparingInt((var0) -> {
         return var0.pos.getY();
      }));
      this.palettes.add(var4);
   }

   private ListTag newIntegerList(int... var1) {
      ListTag var2 = new ListTag();
      int[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         int var6 = var3[var5];
         var2.add(new IntTag(var6));
      }

      return var2;
   }

   private ListTag newDoubleList(double... var1) {
      ListTag var2 = new ListTag();
      double[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         double var6 = var3[var5];
         var2.add(new DoubleTag(var6));
      }

      return var2;
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

   public static class StructureBlockInfo {
      public final BlockPos pos;
      public final BlockState state;
      public final CompoundTag nbt;

      public StructureBlockInfo(BlockPos var1, BlockState var2, @Nullable CompoundTag var3) {
         super();
         this.pos = var1;
         this.state = var2;
         this.nbt = var3;
      }

      public String toString() {
         return String.format("<StructureBlockInfo | %s | %s | %s>", this.pos, this.state, this.nbt);
      }
   }

   static class SimplePalette implements Iterable<BlockState> {
      public static final BlockState DEFAULT_BLOCK_STATE;
      private final IdMapper<BlockState> ids;
      private int lastId;

      private SimplePalette() {
         super();
         this.ids = new IdMapper(16);
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

      // $FF: synthetic method
      SimplePalette(Object var1) {
         this();
      }

      static {
         DEFAULT_BLOCK_STATE = Blocks.AIR.defaultBlockState();
      }
   }
}
