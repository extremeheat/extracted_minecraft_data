package net.minecraft.world.grid;

import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.util.SimpleBitStorage;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;

public class SubGridBlocks {
   public static final StreamCodec<RegistryFriendlyByteBuf, SubGridBlocks> STREAM_CODEC = new StreamCodec<RegistryFriendlyByteBuf, SubGridBlocks>() {
      public SubGridBlocks decode(RegistryFriendlyByteBuf var1) {
         int var2 = var1.readVarInt();
         int var3 = var1.readVarInt();
         int var4 = var1.readVarInt();
         List var5 = var1.readList(var0 -> Block.stateById(var0.readVarInt()));
         int var6 = Mth.ceillog2(var5.size());
         BlockState[] var7 = new BlockState[var2 * var3 * var4];
         SimpleBitStorage var8 = new SimpleBitStorage(var6, var7.length, var1.readLongArray());

         for(int var9 = 0; var9 < var7.length; ++var9) {
            var7[var9] = (BlockState)var5.get(var8.get(var9));
         }

         ArrayList var10 = var1.readCollection(ArrayList::new, BlockPos.STREAM_CODEC);
         return new SubGridBlocks(var7, var10, var2, var3, var4);
      }

      public void encode(RegistryFriendlyByteBuf var1, SubGridBlocks var2) {
         var1.writeVarInt(var2.sizeX);
         var1.writeVarInt(var2.sizeY);
         var1.writeVarInt(var2.sizeZ);
         Reference2IntOpenHashMap var3 = new Reference2IntOpenHashMap();
         ArrayList var4 = new ArrayList();
         var3.defaultReturnValue(-1);

         for(BlockState var8 : var2.blockStates) {
            int var9 = var4.size();
            int var10 = var3.putIfAbsent(var8, var9);
            if (var10 == -1) {
               var4.add(var8);
            }
         }

         var1.writeCollection(var4, (var0, var1x) -> var0.writeVarInt(Block.getId(var1x)));
         int var12 = Mth.ceillog2(var4.size());
         SimpleBitStorage var13 = new SimpleBitStorage(var12, var2.sizeX * var2.sizeY * var2.sizeZ);
         int var14 = 0;

         for(BlockState var11 : var2.blockStates) {
            var13.set(var14++, var3.getInt(var11));
         }

         var1.writeLongArray(var13.getRaw());
         var1.writeCollection(var2.tickables, BlockPos.STREAM_CODEC);
      }
   };
   private static final int NO_INDEX = -1;
   private static final BlockState EMPTY_BLOCK_STATE = Blocks.AIR.defaultBlockState();
   final BlockState[] blockStates;
   final List<BlockPos> tickables;
   final int sizeX;
   final int sizeY;
   final int sizeZ;

   SubGridBlocks(BlockState[] var1, List<BlockPos> var2, int var3, int var4, int var5) {
      super();
      this.blockStates = var1;
      this.tickables = var2;
      this.sizeX = var3;
      this.sizeY = var4;
      this.sizeZ = var5;
   }

   public SubGridBlocks(int var1, int var2, int var3) {
      super();
      this.blockStates = new BlockState[var1 * var2 * var3];
      Arrays.fill(this.blockStates, EMPTY_BLOCK_STATE);
      this.tickables = new ArrayList<>();
      this.sizeX = var1;
      this.sizeY = var2;
      this.sizeZ = var3;
   }

   public void setBlockState(int var1, int var2, int var3, BlockState var4) {
      int var5 = this.index(var1, var2, var3);
      if (var5 == -1) {
         throw new IllegalStateException("Block was out of bounds");
      } else {
         this.blockStates[var5] = var4;
      }
   }

   public void markTickable(BlockPos var1) {
      this.tickables.add(var1);
   }

   public void tick(Level var1, Vec3 var2, Direction var3) {
      this.tickables.forEach(var4 -> {
         BlockState var5 = this.getBlockState(var4.getX(), var4.getY(), var4.getZ());
         Block var7 = var5.getBlock();
         if (var7 instanceof FlyingTickable var6) {
            var6.flyingTick(var1, this, var5, var4, var2.add((double)var4.getX(), (double)var4.getY(), (double)var4.getZ()), var3);
         }
      });
   }

   public BlockState getBlockState(int var1, int var2, int var3) {
      int var4 = this.index(var1, var2, var3);
      return var4 == -1 ? EMPTY_BLOCK_STATE : this.blockStates[var4];
   }

   public BlockState getBlockState(BlockPos var1) {
      return this.getBlockState(var1.getX(), var1.getY(), var1.getZ());
   }

   private int index(int var1, int var2, int var3) {
      return var1 >= 0 && var2 >= 0 && var3 >= 0 && var1 < this.sizeX && var2 < this.sizeY && var3 < this.sizeZ
         ? (var1 + var3 * this.sizeX) * this.sizeY + var2
         : -1;
   }

   public int sizeX() {
      return this.sizeX;
   }

   public int sizeY() {
      return this.sizeY;
   }

   public int sizeZ() {
      return this.sizeZ;
   }

   public SubGridBlocks copy() {
      return new SubGridBlocks(Arrays.copyOf(this.blockStates, this.blockStates.length), new ArrayList<>(this.tickables), this.sizeX, this.sizeY, this.sizeZ);
   }

   public void place(BlockPos var1, Level var2) {
      BlockPos.MutableBlockPos var3 = new BlockPos.MutableBlockPos();

      for(int var4 = 0; var4 < this.sizeZ; ++var4) {
         for(int var5 = 0; var5 < this.sizeX; ++var5) {
            for(int var6 = 0; var6 < this.sizeY; ++var6) {
               var3.setWithOffset(var1, var5, var6, var4);
               BlockState var7 = this.getBlockState(var5, var6, var4);
               if (!var7.isAir()) {
                  FluidState var8 = var2.getFluidState(var3);
                  if (var8.is(Fluids.WATER)) {
                     var7 = var7.trySetValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(true));
                  }

                  var2.setBlock(var3, var7, 18);
               }
            }
         }
      }

      for(int var9 = 0; var9 < this.sizeZ; ++var9) {
         for(int var10 = 0; var10 < this.sizeX; ++var10) {
            for(int var11 = 0; var11 < this.sizeY; ++var11) {
               var3.setWithOffset(var1, var10, var11, var9);
               var2.blockUpdated(var3, this.getBlockState(var10, var11, var9).getBlock());
            }
         }
      }
   }

   public static SubGridBlocks decode(HolderGetter<Block> var0, CompoundTag var1) {
      int var2 = var1.getInt("size_x");
      int var3 = var1.getInt("size_y");
      int var4 = var1.getInt("size_z");
      BlockState[] var5 = new BlockState[var2 * var3 * var4];
      ListTag var6 = var1.getList("palette", 10);
      ArrayList var7 = new ArrayList();

      for(int var8 = 0; var8 < var6.size(); ++var8) {
         var7.add(NbtUtils.readBlockState(var0, var6.getCompound(var8)));
      }

      int[] var11 = var1.getIntArray("blocks");
      if (var11.length != var5.length) {
         return new SubGridBlocks(var2, var3, var4);
      } else {
         for(int var9 = 0; var9 < var11.length; ++var9) {
            int var10 = var11[var9];
            var5[var9] = var10 < var7.size() ? (BlockState)var7.get(var10) : Blocks.AIR.defaultBlockState();
         }

         ArrayList var12 = new ArrayList();
         if (var1.contains("tickables", 12)) {
            Arrays.stream(var1.getLongArray("tickables")).mapToObj(BlockPos::of).forEach(var12::add);
         }

         return new SubGridBlocks(var5, var12, var2, var3, var4);
      }
   }

   public Tag encode() {
      CompoundTag var1 = new CompoundTag();
      var1.putInt("size_x", this.sizeX);
      var1.putInt("size_y", this.sizeY);
      var1.putInt("size_z", this.sizeZ);
      Reference2IntOpenHashMap var2 = new Reference2IntOpenHashMap();
      var2.defaultReturnValue(-1);
      ListTag var3 = new ListTag();
      int[] var4 = new int[this.blockStates.length];

      for(int var5 = 0; var5 < this.blockStates.length; ++var5) {
         BlockState var6 = this.blockStates[var5];
         int var7 = var3.size();
         int var8 = var2.putIfAbsent(var6, var7);
         if (var8 == -1) {
            var3.add(NbtUtils.writeBlockState(var6));
            var4[var5] = var7;
         } else {
            var4[var5] = var8;
         }
      }

      var1.put("palette", var3);
      var1.put("blocks", new IntArrayTag(var4));
      var1.putLongArray("tickables", this.tickables.stream().mapToLong(BlockPos::asLong).toArray());
      return var1;
   }
}
