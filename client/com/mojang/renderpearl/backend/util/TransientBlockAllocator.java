package com.mojang.renderpearl.backend.util;

import com.mojang.renderpearl.util.UncheckedAutoCloseable;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.LongFunction;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;

public class TransientBlockAllocator<T extends TransientBlockAllocator.Allocator.Block> implements UncheckedAutoCloseable {
   private final long blockSize;
   private final long maxAlignment;
   private final Allocator<T> allocator;
   private final Consumer<T> onBlockUse;
   private final ReferenceArrayList<T> specialBlocks;
   private final ReferenceArrayList<T> freeBlocks;
   private final ReferenceArrayList<T> usedBlocks;
   private @Nullable T currentBlock;
   private long currentOffset;

   public TransientBlockAllocator(final long blockSize, final long maxAlignment, final Allocator<T> allocator) {
      this(blockSize, maxAlignment, allocator, (var0) -> {
      });
   }

   public TransientBlockAllocator(final long blockSize, final long maxAlignment, final Allocator<T> allocator, final Consumer<T> onBlockUse) {
      super();
      this.specialBlocks = new ReferenceArrayList();
      this.freeBlocks = new ReferenceArrayList();
      this.usedBlocks = new ReferenceArrayList();
      this.currentOffset = 0L;
      this.blockSize = blockSize;
      this.maxAlignment = maxAlignment;
      this.allocator = allocator;
      this.onBlockUse = onBlockUse;
   }

   public void close() {
      this.rotate().run();
      this.rotate().run();
   }

   public long blockSize() {
      return this.blockSize;
   }

   public Runnable rotate() {
      this.currentBlock = null;
      this.currentOffset = this.blockSize;
      ReferenceArrayList var10000 = this.freeBlocks;
      Allocator var10001 = this.allocator;
      Objects.requireNonNull(var10001);
      var10000.forEach(var10001::free);
      this.freeBlocks.clear();
      if (this.usedBlocks.isEmpty() && this.specialBlocks.isEmpty()) {
         return () -> {
         };
      } else {
         ReferenceArrayList<T> blocksUsedThisRotation = this.usedBlocks.clone();
         this.usedBlocks.clear();
         ReferenceArrayList<T> specialBlocksUsedThisRotation = this.specialBlocks.clone();
         this.specialBlocks.clear();
         return () -> {
            this.freeBlocks.ensureCapacity(this.freeBlocks.size() + blocksUsedThisRotation.size());
            Iterator i$ = blocksUsedThisRotation.iterator();

            while(i$.hasNext()) {
               T usedBlock = (T)(i$.next());
               if (usedBlock.suboptimal()) {
                  this.allocator.free(usedBlock);
               } else {
                  this.freeBlocks.add(usedBlock);
               }
            }

            Allocator var10001 = this.allocator;
            Objects.requireNonNull(var10001);
            specialBlocksUsedThisRotation.forEach(var10001::free);
         };
      }
   }

   @Contract(
      pure = true
   )
   public boolean canAllocateInBlock(final long size, final long alignment) {
      return size <= this.blockSize && alignment <= this.maxAlignment;
   }

   @Contract(
      pure = true
   )
   public boolean canAllocateInCurrentBlock(final long size, final long alignment) {
      if (this.currentBlock == null && this.canAllocateInBlock(size, alignment)) {
         return true;
      } else {
         long alignedOffset = Mth.roundToward(this.currentOffset, alignment);
         return size <= this.blockSize - alignedOffset && alignment <= this.maxAlignment;
      }
   }

   private T allocateBlock() {
      if (this.freeBlocks.isEmpty()) {
         this.freeBlocks.add(this.allocator.alloc(this.blockSize));
      }

      T block = (T)(this.freeBlocks.pop());
      this.onBlockUse.accept(block);
      this.usedBlocks.add(block);
      return block;
   }

   public Allocation<T> allocate(final long size, final long alignment, final long minimumAllocation, final long elementSize) {
      if (alignment > this.maxAlignment) {
         throw new IllegalArgumentException("Alignment requirement over maximum supported alignment");
      } else if (size == this.blockSize) {
         return new Allocation<T>(this.allocateBlock(), 0L, this.blockSize);
      } else if (!this.canAllocateInBlock(size, alignment)) {
         T specialBlock = this.allocator.alloc(size);
         this.onBlockUse.accept(specialBlock);
         this.specialBlocks.add(specialBlock);
         return new Allocation<T>(specialBlock, 0L, size);
      } else {
         if (this.currentBlock == null) {
            this.currentBlock = this.allocateBlock();
            this.currentOffset = 0L;
         }

         if (this.canAllocateInCurrentBlock(size, alignment)) {
            assert this.currentBlock != null;

            long alignedOffset = Mth.roundToward(this.currentOffset, alignment);
            this.currentOffset = alignedOffset + size;
            T block = this.currentBlock;
            return new Allocation<T>(block, alignedOffset, size);
         } else if (this.canAllocateInCurrentBlock(minimumAllocation, alignment)) {
            assert this.currentBlock != null;

            long alignedOffset = Mth.roundToward(this.currentOffset, alignment);
            long allocatedSize = (this.blockSize - alignedOffset) / elementSize * elementSize;
            this.currentOffset = alignedOffset + allocatedSize;
            T block = this.currentBlock;
            return new Allocation<T>(block, alignedOffset, allocatedSize);
         } else {
            T newBlock = this.allocateBlock();
            if (this.currentOffset > size) {
               this.currentBlock = newBlock;
               this.currentOffset = size;
            }

            return new Allocation<T>(newBlock, 0L, size);
         }
      }
   }

   public static record Allocation<T>(T block, long offset, long size) {
      public Allocation {
         super();
      }
   }

   public interface Allocator<T extends Allocator.Block> {
      T alloc(long size);

      void free(T t);

      static <T extends Block> Allocator<T> create(final LongFunction<T> alloc, final Consumer<T> free) {
         return new Allocator<T>() {
            public T alloc(final long size) {
               return (T)((Block)alloc.apply(size));
            }

            public void free(final T t) {
               free.accept(t);
            }
         };
      }

      public static record CpuBlock(long address) implements Block {
         public CpuBlock {
            super();
            if (address == 0L) {
               throw new IllegalArgumentException("Failed to allocate CPU block");
            }
         }

         public boolean suboptimal() {
            return false;
         }

         public static Allocator<CpuBlock> memalloc() {
            return TransientBlockAllocator.Allocator.<CpuBlock>create((size) -> new CpuBlock(MemoryUtil.nmemAlloc(size)), (block) -> MemoryUtil.nmemFree(block.address));
         }
      }

      public interface Block {
         boolean suboptimal();
      }
   }
}
