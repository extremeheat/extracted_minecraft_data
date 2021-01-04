package net.minecraft.world.level.chunk;

import javax.annotation.Nullable;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class LevelChunkSection {
   private static final Palette<BlockState> GLOBAL_BLOCKSTATE_PALETTE;
   private final int bottomBlockY;
   private short nonEmptyBlockCount;
   private short tickingBlockCount;
   private short tickingFluidCount;
   private final PalettedContainer<BlockState> states;

   public LevelChunkSection(int var1) {
      this(var1, (short)0, (short)0, (short)0);
   }

   public LevelChunkSection(int var1, short var2, short var3, short var4) {
      super();
      this.bottomBlockY = var1;
      this.nonEmptyBlockCount = var2;
      this.tickingBlockCount = var3;
      this.tickingFluidCount = var4;
      this.states = new PalettedContainer(GLOBAL_BLOCKSTATE_PALETTE, Block.BLOCK_STATE_REGISTRY, NbtUtils::readBlockState, NbtUtils::writeBlockState, Blocks.AIR.defaultBlockState());
   }

   public BlockState getBlockState(int var1, int var2, int var3) {
      return (BlockState)this.states.get(var1, var2, var3);
   }

   public FluidState getFluidState(int var1, int var2, int var3) {
      return ((BlockState)this.states.get(var1, var2, var3)).getFluidState();
   }

   public void acquire() {
      this.states.acquire();
   }

   public void release() {
      this.states.release();
   }

   public BlockState setBlockState(int var1, int var2, int var3, BlockState var4) {
      return this.setBlockState(var1, var2, var3, var4, true);
   }

   public BlockState setBlockState(int var1, int var2, int var3, BlockState var4, boolean var5) {
      BlockState var6;
      if (var5) {
         var6 = (BlockState)this.states.getAndSet(var1, var2, var3, var4);
      } else {
         var6 = (BlockState)this.states.getAndSetUnchecked(var1, var2, var3, var4);
      }

      FluidState var7 = var6.getFluidState();
      FluidState var8 = var4.getFluidState();
      if (!var6.isAir()) {
         --this.nonEmptyBlockCount;
         if (var6.isRandomlyTicking()) {
            --this.tickingBlockCount;
         }
      }

      if (!var7.isEmpty()) {
         --this.tickingFluidCount;
      }

      if (!var4.isAir()) {
         ++this.nonEmptyBlockCount;
         if (var4.isRandomlyTicking()) {
            ++this.tickingBlockCount;
         }
      }

      if (!var8.isEmpty()) {
         ++this.tickingFluidCount;
      }

      return var6;
   }

   public boolean isEmpty() {
      return this.nonEmptyBlockCount == 0;
   }

   public static boolean isEmpty(@Nullable LevelChunkSection var0) {
      return var0 == LevelChunk.EMPTY_SECTION || var0.isEmpty();
   }

   public boolean isRandomlyTicking() {
      return this.isRandomlyTickingBlocks() || this.isRandomlyTickingFluids();
   }

   public boolean isRandomlyTickingBlocks() {
      return this.tickingBlockCount > 0;
   }

   public boolean isRandomlyTickingFluids() {
      return this.tickingFluidCount > 0;
   }

   public int bottomBlockY() {
      return this.bottomBlockY;
   }

   public void recalcBlockCounts() {
      this.nonEmptyBlockCount = 0;
      this.tickingBlockCount = 0;
      this.tickingFluidCount = 0;
      this.states.count((var1, var2) -> {
         FluidState var3 = var1.getFluidState();
         if (!var1.isAir()) {
            this.nonEmptyBlockCount = (short)(this.nonEmptyBlockCount + var2);
            if (var1.isRandomlyTicking()) {
               this.tickingBlockCount = (short)(this.tickingBlockCount + var2);
            }
         }

         if (!var3.isEmpty()) {
            this.nonEmptyBlockCount = (short)(this.nonEmptyBlockCount + var2);
            if (var3.isRandomlyTicking()) {
               this.tickingFluidCount = (short)(this.tickingFluidCount + var2);
            }
         }

      });
   }

   public PalettedContainer<BlockState> getStates() {
      return this.states;
   }

   public void read(FriendlyByteBuf var1) {
      this.nonEmptyBlockCount = var1.readShort();
      this.states.read(var1);
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeShort(this.nonEmptyBlockCount);
      this.states.write(var1);
   }

   public int getSerializedSize() {
      return 2 + this.states.getSerializedSize();
   }

   public boolean maybeHas(BlockState var1) {
      return this.states.maybeHas(var1);
   }

   static {
      GLOBAL_BLOCKSTATE_PALETTE = new GlobalPalette(Block.BLOCK_STATE_REGISTRY, Blocks.AIR.defaultBlockState());
   }
}
