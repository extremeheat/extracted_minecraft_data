package net.minecraft.world.level.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.EnumSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction8;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.StemGrownBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UpgradeData {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final UpgradeData EMPTY = new UpgradeData();
   private static final Direction8[] DIRECTIONS = Direction8.values();
   private final EnumSet sides;
   private final int[][] index;
   private static final Map MAP = new IdentityHashMap();
   private static final Set CHUNKY_FIXERS = Sets.newHashSet();

   private UpgradeData() {
      this.sides = EnumSet.noneOf(Direction8.class);
      this.index = new int[16][];
   }

   public UpgradeData(CompoundTag var1) {
      this();
      if (var1.contains("Indices", 10)) {
         CompoundTag var2 = var1.getCompound("Indices");

         for(int var3 = 0; var3 < this.index.length; ++var3) {
            String var4 = String.valueOf(var3);
            if (var2.contains(var4, 11)) {
               this.index[var3] = var2.getIntArray(var4);
            }
         }
      }

      int var7 = var1.getInt("Sides");
      Direction8[] var8 = Direction8.values();
      int var9 = var8.length;

      for(int var5 = 0; var5 < var9; ++var5) {
         Direction8 var6 = var8[var5];
         if ((var7 & 1 << var6.ordinal()) != 0) {
            this.sides.add(var6);
         }
      }

   }

   public void upgrade(LevelChunk var1) {
      this.upgradeInside(var1);
      Direction8[] var2 = DIRECTIONS;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Direction8 var5 = var2[var4];
         upgradeSides(var1, var5);
      }

      Level var6 = var1.getLevel();
      CHUNKY_FIXERS.forEach((var1x) -> {
         var1x.processChunk(var6);
      });
   }

   private static void upgradeSides(LevelChunk var0, Direction8 var1) {
      Level var2 = var0.getLevel();
      if (var0.getUpgradeData().sides.remove(var1)) {
         Set var3 = var1.getDirections();
         boolean var4 = false;
         boolean var5 = true;
         boolean var6 = var3.contains(Direction.EAST);
         boolean var7 = var3.contains(Direction.WEST);
         boolean var8 = var3.contains(Direction.SOUTH);
         boolean var9 = var3.contains(Direction.NORTH);
         boolean var10 = var3.size() == 1;
         ChunkPos var11 = var0.getPos();
         int var12 = var11.getMinBlockX() + (var10 && (var9 || var8) ? 1 : (var7 ? 0 : 15));
         int var13 = var11.getMinBlockX() + (!var10 || !var9 && !var8 ? (var7 ? 0 : 15) : 14);
         int var14 = var11.getMinBlockZ() + (!var10 || !var6 && !var7 ? (var9 ? 0 : 15) : 1);
         int var15 = var11.getMinBlockZ() + (!var10 || !var6 && !var7 ? (var9 ? 0 : 15) : 14);
         Direction[] var16 = Direction.values();
         BlockPos.MutableBlockPos var17 = new BlockPos.MutableBlockPos();
         Iterator var18 = BlockPos.betweenClosed(var12, 0, var14, var13, var2.getMaxBuildHeight() - 1, var15).iterator();

         while(var18.hasNext()) {
            BlockPos var19 = (BlockPos)var18.next();
            BlockState var20 = var2.getBlockState(var19);
            BlockState var21 = var20;
            Direction[] var22 = var16;
            int var23 = var16.length;

            for(int var24 = 0; var24 < var23; ++var24) {
               Direction var25 = var22[var24];
               var17.set((Vec3i)var19).move(var25);
               var21 = updateState(var21, var25, var2, var19, var17);
            }

            Block.updateOrDestroy(var20, var21, var2, var19, 18);
         }

      }
   }

   private static BlockState updateState(BlockState var0, Direction var1, LevelAccessor var2, BlockPos var3, BlockPos var4) {
      return ((UpgradeData.BlockFixer)MAP.getOrDefault(var0.getBlock(), UpgradeData.BlockFixers.DEFAULT)).updateShape(var0, var1, var2.getBlockState(var4), var2, var3, var4);
   }

   private void upgradeInside(LevelChunk var1) {
      BlockPos.PooledMutableBlockPos var2 = BlockPos.PooledMutableBlockPos.acquire();
      Throwable var3 = null;

      try {
         BlockPos.PooledMutableBlockPos var4 = BlockPos.PooledMutableBlockPos.acquire();
         Throwable var5 = null;

         try {
            ChunkPos var6 = var1.getPos();
            Level var7 = var1.getLevel();

            int var8;
            for(var8 = 0; var8 < 16; ++var8) {
               LevelChunkSection var9 = var1.getSections()[var8];
               int[] var10 = this.index[var8];
               this.index[var8] = null;
               if (var9 != null && var10 != null && var10.length > 0) {
                  Direction[] var11 = Direction.values();
                  PalettedContainer var12 = var9.getStates();
                  int[] var13 = var10;
                  int var14 = var10.length;

                  for(int var15 = 0; var15 < var14; ++var15) {
                     int var16 = var13[var15];
                     int var17 = var16 & 15;
                     int var18 = var16 >> 8 & 15;
                     int var19 = var16 >> 4 & 15;
                     var2.set(var6.getMinBlockX() + var17, (var8 << 4) + var18, var6.getMinBlockZ() + var19);
                     BlockState var20 = (BlockState)var12.get(var16);
                     BlockState var21 = var20;
                     Direction[] var22 = var11;
                     int var23 = var11.length;

                     for(int var24 = 0; var24 < var23; ++var24) {
                        Direction var25 = var22[var24];
                        var4.set((Vec3i)var2).move(var25);
                        if (var2.getX() >> 4 == var6.x && var2.getZ() >> 4 == var6.z) {
                           var21 = updateState(var21, var25, var7, var2, var4);
                        }
                     }

                     Block.updateOrDestroy(var20, var21, var7, var2, 18);
                  }
               }
            }

            for(var8 = 0; var8 < this.index.length; ++var8) {
               if (this.index[var8] != null) {
                  LOGGER.warn("Discarding update data for section {} for chunk ({} {})", var8, var6.x, var6.z);
               }

               this.index[var8] = null;
            }

         } catch (Throwable var47) {
            var5 = var47;
            throw var47;
         } finally {
            if (var4 != null) {
               if (var5 != null) {
                  try {
                     var4.close();
                  } catch (Throwable var46) {
                     var5.addSuppressed(var46);
                  }
               } else {
                  var4.close();
               }
            }

         }
      } catch (Throwable var49) {
         var3 = var49;
         throw var49;
      } finally {
         if (var2 != null) {
            if (var3 != null) {
               try {
                  var2.close();
               } catch (Throwable var45) {
                  var3.addSuppressed(var45);
               }
            } else {
               var2.close();
            }
         }

      }
   }

   public boolean isEmpty() {
      int[][] var1 = this.index;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         int[] var4 = var1[var3];
         if (var4 != null) {
            return false;
         }
      }

      return this.sides.isEmpty();
   }

   public CompoundTag write() {
      CompoundTag var1 = new CompoundTag();
      CompoundTag var2 = new CompoundTag();

      int var3;
      for(var3 = 0; var3 < this.index.length; ++var3) {
         String var4 = String.valueOf(var3);
         if (this.index[var3] != null && this.index[var3].length != 0) {
            var2.putIntArray(var4, this.index[var3]);
         }
      }

      if (!var2.isEmpty()) {
         var1.put("Indices", var2);
      }

      var3 = 0;

      Direction8 var5;
      for(Iterator var6 = this.sides.iterator(); var6.hasNext(); var3 |= 1 << var5.ordinal()) {
         var5 = (Direction8)var6.next();
      }

      var1.putByte("Sides", (byte)var3);
      return var1;
   }

   static enum BlockFixers implements UpgradeData.BlockFixer {
      BLACKLIST(new Block[]{Blocks.OBSERVER, Blocks.NETHER_PORTAL, Blocks.WHITE_CONCRETE_POWDER, Blocks.ORANGE_CONCRETE_POWDER, Blocks.MAGENTA_CONCRETE_POWDER, Blocks.LIGHT_BLUE_CONCRETE_POWDER, Blocks.YELLOW_CONCRETE_POWDER, Blocks.LIME_CONCRETE_POWDER, Blocks.PINK_CONCRETE_POWDER, Blocks.GRAY_CONCRETE_POWDER, Blocks.LIGHT_GRAY_CONCRETE_POWDER, Blocks.CYAN_CONCRETE_POWDER, Blocks.PURPLE_CONCRETE_POWDER, Blocks.BLUE_CONCRETE_POWDER, Blocks.BROWN_CONCRETE_POWDER, Blocks.GREEN_CONCRETE_POWDER, Blocks.RED_CONCRETE_POWDER, Blocks.BLACK_CONCRETE_POWDER, Blocks.ANVIL, Blocks.CHIPPED_ANVIL, Blocks.DAMAGED_ANVIL, Blocks.DRAGON_EGG, Blocks.GRAVEL, Blocks.SAND, Blocks.RED_SAND, Blocks.OAK_SIGN, Blocks.SPRUCE_SIGN, Blocks.BIRCH_SIGN, Blocks.ACACIA_SIGN, Blocks.JUNGLE_SIGN, Blocks.DARK_OAK_SIGN, Blocks.OAK_WALL_SIGN, Blocks.SPRUCE_WALL_SIGN, Blocks.BIRCH_WALL_SIGN, Blocks.ACACIA_WALL_SIGN, Blocks.JUNGLE_WALL_SIGN, Blocks.DARK_OAK_WALL_SIGN}) {
         public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
            return var1;
         }
      },
      DEFAULT(new Block[0]) {
         public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
            return var1.updateShape(var2, var4.getBlockState(var6), var4, var5, var6);
         }
      },
      CHEST(new Block[]{Blocks.CHEST, Blocks.TRAPPED_CHEST}) {
         public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
            if (var3.getBlock() == var1.getBlock() && var2.getAxis().isHorizontal() && var1.getValue(ChestBlock.TYPE) == ChestType.SINGLE && var3.getValue(ChestBlock.TYPE) == ChestType.SINGLE) {
               Direction var7 = (Direction)var1.getValue(ChestBlock.FACING);
               if (var2.getAxis() != var7.getAxis() && var7 == var3.getValue(ChestBlock.FACING)) {
                  ChestType var8 = var2 == var7.getClockWise() ? ChestType.LEFT : ChestType.RIGHT;
                  var4.setBlock(var6, (BlockState)var3.setValue(ChestBlock.TYPE, var8.getOpposite()), 18);
                  if (var7 == Direction.NORTH || var7 == Direction.EAST) {
                     BlockEntity var9 = var4.getBlockEntity(var5);
                     BlockEntity var10 = var4.getBlockEntity(var6);
                     if (var9 instanceof ChestBlockEntity && var10 instanceof ChestBlockEntity) {
                        ChestBlockEntity.swapContents((ChestBlockEntity)var9, (ChestBlockEntity)var10);
                     }
                  }

                  return (BlockState)var1.setValue(ChestBlock.TYPE, var8);
               }
            }

            return var1;
         }
      },
      LEAVES(true, new Block[]{Blocks.ACACIA_LEAVES, Blocks.BIRCH_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES}) {
         private final ThreadLocal queue = ThreadLocal.withInitial(() -> {
            return Lists.newArrayListWithCapacity(7);
         });

         public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
            BlockState var7 = var1.updateShape(var2, var4.getBlockState(var6), var4, var5, var6);
            if (var1 != var7) {
               int var8 = (Integer)var7.getValue(BlockStateProperties.DISTANCE);
               List var9 = (List)this.queue.get();
               if (var9.isEmpty()) {
                  for(int var10 = 0; var10 < 7; ++var10) {
                     var9.add(new ObjectOpenHashSet());
                  }
               }

               ((ObjectSet)var9.get(var8)).add(var5.immutable());
            }

            return var1;
         }

         public void processChunk(LevelAccessor var1) {
            BlockPos.MutableBlockPos var2 = new BlockPos.MutableBlockPos();
            List var3 = (List)this.queue.get();

            label44:
            for(int var4 = 2; var4 < var3.size(); ++var4) {
               int var5 = var4 - 1;
               ObjectSet var6 = (ObjectSet)var3.get(var5);
               ObjectSet var7 = (ObjectSet)var3.get(var4);
               ObjectIterator var8 = var6.iterator();

               while(true) {
                  BlockPos var9;
                  BlockState var10;
                  do {
                     do {
                        if (!var8.hasNext()) {
                           continue label44;
                        }

                        var9 = (BlockPos)var8.next();
                        var10 = var1.getBlockState(var9);
                     } while((Integer)var10.getValue(BlockStateProperties.DISTANCE) < var5);

                     var1.setBlock(var9, (BlockState)var10.setValue(BlockStateProperties.DISTANCE, var5), 18);
                  } while(var4 == 7);

                  Direction[] var11 = DIRECTIONS;
                  int var12 = var11.length;

                  for(int var13 = 0; var13 < var12; ++var13) {
                     Direction var14 = var11[var13];
                     var2.set((Vec3i)var9).move(var14);
                     BlockState var15 = var1.getBlockState(var2);
                     if (var15.hasProperty(BlockStateProperties.DISTANCE) && (Integer)var10.getValue(BlockStateProperties.DISTANCE) > var4) {
                        var7.add(var2.immutable());
                     }
                  }
               }
            }

            var3.clear();
         }
      },
      STEM_BLOCK(new Block[]{Blocks.MELON_STEM, Blocks.PUMPKIN_STEM}) {
         public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
            if ((Integer)var1.getValue(StemBlock.AGE) == 7) {
               StemGrownBlock var7 = ((StemBlock)var1.getBlock()).getFruit();
               if (var3.getBlock() == var7) {
                  return (BlockState)var7.getAttachedStem().defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, var2);
               }
            }

            return var1;
         }
      };

      public static final Direction[] DIRECTIONS = Direction.values();

      private BlockFixers(Block... var3) {
         this(false, var3);
      }

      private BlockFixers(boolean var3, Block... var4) {
         Block[] var5 = var4;
         int var6 = var4.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            Block var8 = var5[var7];
            UpgradeData.MAP.put(var8, this);
         }

         if (var3) {
            UpgradeData.CHUNKY_FIXERS.add(this);
         }

      }

      // $FF: synthetic method
      BlockFixers(Block[] var3, Object var4) {
         this(var3);
      }

      // $FF: synthetic method
      BlockFixers(boolean var3, Block[] var4, Object var5) {
         this(var3, var4);
      }
   }

   public interface BlockFixer {
      BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6);

      default void processChunk(LevelAccessor var1) {
      }
   }
}
