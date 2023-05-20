package net.minecraft.world.level.chunk;

import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class LevelChunkSection {
   public static final int SECTION_WIDTH = 16;
   public static final int SECTION_HEIGHT = 16;
   public static final int SECTION_SIZE = 4096;
   public static final int BIOME_CONTAINER_BITS = 2;
   private final int bottomBlockY;
   private short nonEmptyBlockCount;
   private short tickingBlockCount;
   private short tickingFluidCount;
   private final PalettedContainer<BlockState> states;
   private PalettedContainerRO<Holder<Biome>> biomes;

   public LevelChunkSection(int var1, PalettedContainer<BlockState> var2, PalettedContainerRO<Holder<Biome>> var3) {
      super();
      this.bottomBlockY = getBottomBlockY(var1);
      this.states = var2;
      this.biomes = var3;
      this.recalcBlockCounts();
   }

   public LevelChunkSection(int var1, Registry<Biome> var2) {
      super();
      this.bottomBlockY = getBottomBlockY(var1);
      this.states = new PalettedContainer<>(Block.BLOCK_STATE_REGISTRY, Blocks.AIR.defaultBlockState(), PalettedContainer.Strategy.SECTION_STATES);
      this.biomes = new PalettedContainer<>(var2.asHolderIdMap(), var2.getHolderOrThrow(Biomes.PLAINS), PalettedContainer.Strategy.SECTION_BIOMES);
   }

   public static int getBottomBlockY(int var0) {
      return var0 << 4;
   }

   public BlockState getBlockState(int var1, int var2, int var3) {
      return this.states.get(var1, var2, var3);
   }

   public FluidState getFluidState(int var1, int var2, int var3) {
      return this.states.get(var1, var2, var3).getFluidState();
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
         var6 = this.states.getAndSet(var1, var2, var3, var4);
      } else {
         var6 = this.states.getAndSetUnchecked(var1, var2, var3, var4);
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

   public boolean hasOnlyAir() {
      return this.nonEmptyBlockCount == 0;
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
      class 1BlockCounter implements PalettedContainer.CountConsumer<BlockState> {
         public int nonEmptyBlockCount;
         public int tickingBlockCount;
         public int tickingFluidCount;

         _BlockCounter/* $QF was: 1BlockCounter*/() {
            super();
         }

         public void accept(BlockState var1, int var2) {
            FluidState var3 = var1.getFluidState();
            if (!var1.isAir()) {
               this.nonEmptyBlockCount += var2;
               if (var1.isRandomlyTicking()) {
                  this.tickingBlockCount += var2;
               }
            }

            if (!var3.isEmpty()) {
               this.nonEmptyBlockCount += var2;
               if (var3.isRandomlyTicking()) {
                  this.tickingFluidCount += var2;
               }
            }
         }
      }

      1BlockCounter var1 = new 1BlockCounter();
      this.states.count(var1);
      this.nonEmptyBlockCount = (short)var1.nonEmptyBlockCount;
      this.tickingBlockCount = (short)var1.tickingBlockCount;
      this.tickingFluidCount = (short)var1.tickingFluidCount;
   }

   public PalettedContainer<BlockState> getStates() {
      return this.states;
   }

   public PalettedContainerRO<Holder<Biome>> getBiomes() {
      return this.biomes;
   }

   public void read(FriendlyByteBuf var1) {
      this.nonEmptyBlockCount = var1.readShort();
      this.states.read(var1);
      PalettedContainer var2 = this.biomes.recreate();
      var2.read(var1);
      this.biomes = var2;
   }

   public void readBiomes(FriendlyByteBuf var1) {
      PalettedContainer var2 = this.biomes.recreate();
      var2.read(var1);
      this.biomes = var2;
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeShort(this.nonEmptyBlockCount);
      this.states.write(var1);
      this.biomes.write(var1);
   }

   public int getSerializedSize() {
      return 2 + this.states.getSerializedSize() + this.biomes.getSerializedSize();
   }

   public boolean maybeHas(Predicate<BlockState> var1) {
      return this.states.maybeHas(var1);
   }

   public Holder<Biome> getNoiseBiome(int var1, int var2, int var3) {
      return this.biomes.get(var1, var2, var3);
   }

   public void fillBiomesFromNoise(BiomeResolver var1, Climate.Sampler var2, int var3, int var4) {
      PalettedContainer var5 = this.biomes.recreate();
      int var6 = QuartPos.fromBlock(this.bottomBlockY());
      boolean var7 = true;

      for(int var8 = 0; var8 < 4; ++var8) {
         for(int var9 = 0; var9 < 4; ++var9) {
            for(int var10 = 0; var10 < 4; ++var10) {
               var5.getAndSetUnchecked(var8, var9, var10, var1.getNoiseBiome(var3 + var8, var6 + var9, var4 + var10, var2));
            }
         }
      }

      this.biomes = var5;
   }
}
