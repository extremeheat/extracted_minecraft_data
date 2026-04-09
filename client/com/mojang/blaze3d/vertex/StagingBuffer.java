package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import java.nio.ByteBuffer;
import java.util.Objects;
import net.minecraft.client.renderer.MappableRingBuffer;
import org.jspecify.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;

public abstract class StagingBuffer implements AutoCloseable {
   private int nextWriteOffset;
   private int usedBufferCount;

   public StagingBuffer() {
      super();
   }

   public static StagingBuffer create(final String name, final GpuDevice gpuDevice, final int bufferSize) {
      return (StagingBuffer)(!gpuDevice.getDeviceInfo().hintsAndWorkarounds().writeToBufferIsSlow() ? new Cpu(bufferSize) : new PersistentlyMapped(name, gpuDevice, bufferSize));
   }

   public @Nullable BufferHandle tryAppend(final ByteBuffer buffer) {
      int writeOffset = this.nextWriteOffset;
      int bufferSize = buffer.remaining();
      ByteBuffer writeBuffer = this.getWriteBuffer();
      if (bufferSize > writeBuffer.capacity()) {
         throw new IllegalArgumentException("Cannot fit allocation of size " + bufferSize + " into staging buffer of size " + writeBuffer.capacity());
      } else if (bufferSize > writeBuffer.capacity() - writeOffset) {
         return null;
      } else {
         MemoryUtil.memCopy(buffer, writeBuffer.position(writeOffset));
         this.nextWriteOffset += bufferSize;
         ++this.usedBufferCount;
         return new BufferHandle(writeOffset, bufferSize);
      }
   }

   protected abstract ByteBuffer getWriteBuffer();

   protected abstract void copyTo(final CommandEncoder encoder, final GpuBuffer dstBuffer, long dstOffset, long stagingBufferOffset, long copySize);

   protected void rotateBuffer(final CommandEncoder encoder) {
   }

   public Uploader startUploading(final CommandEncoder encoder) {
      return new Uploader(encoder);
   }

   private void tryClearAndRotate(final CommandEncoder encoder) {
      if (this.nextWriteOffset > 0 && this.usedBufferCount == 0) {
         this.rotateBuffer(encoder);
         this.nextWriteOffset = 0;
      }

   }

   public abstract void close();

   private static class Cpu extends StagingBuffer {
      private final ByteBuffer stagingBuffer;

      private Cpu(final int bufferSize) {
         super();
         this.stagingBuffer = MemoryUtil.memAlloc(bufferSize);
      }

      protected ByteBuffer getWriteBuffer() {
         return this.stagingBuffer;
      }

      protected void copyTo(final CommandEncoder encoder, final GpuBuffer dstBuffer, final long dstOffset, final long stagingBufferOffset, final long copySize) {
         encoder.writeToBuffer(dstBuffer.slice(dstOffset, copySize), this.stagingBuffer.slice((int)stagingBufferOffset, (int)copySize));
      }

      public void close() {
         MemoryUtil.memFree(this.stagingBuffer);
      }
   }

   private static class PersistentlyMapped extends StagingBuffer {
      private final MappableRingBuffer mappableRingBuffer;
      private GpuBuffer.MappedView currentMappedView;
      private GpuBuffer currentGPUBuffer;
      private ByteBuffer currentBuffer;

      private PersistentlyMapped(final String name, final GpuDevice gpuDevice, final int bufferSize) {
         super();
         this.mappableRingBuffer = new MappableRingBuffer(() -> name + " staging buffer", 18, bufferSize / 2);
         CommandEncoder encoder = gpuDevice.createCommandEncoder();
         this.currentGPUBuffer = this.mappableRingBuffer.currentBuffer();
         this.currentMappedView = encoder.mapBuffer(this.currentGPUBuffer, false, true);
         this.currentBuffer = this.currentMappedView.data();
      }

      protected ByteBuffer getWriteBuffer() {
         return this.currentBuffer;
      }

      protected void copyTo(final CommandEncoder encoder, final GpuBuffer dstBuffer, final long dstOffset, final long stagingBufferOffset, final long copySize) {
         encoder.copyToBuffer(this.currentGPUBuffer.slice(stagingBufferOffset, copySize), dstBuffer.slice(dstOffset, copySize));
      }

      protected void rotateBuffer(final CommandEncoder encoder) {
         this.currentMappedView.close();
         this.mappableRingBuffer.rotate();
         this.currentGPUBuffer = this.mappableRingBuffer.currentBuffer();
         this.currentMappedView = encoder.mapBuffer(this.currentGPUBuffer, false, true);
         this.currentBuffer = this.currentMappedView.data();
      }

      public void close() {
         this.currentMappedView.close();
         this.mappableRingBuffer.close();
      }
   }

   public class Uploader implements AutoCloseable {
      private final CommandEncoder encoder;

      public Uploader(final CommandEncoder encoder) {
         Objects.requireNonNull(StagingBuffer.this);
         super();
         this.encoder = encoder;
      }

      public void copyTo(final BufferHandle srcBuffer, final GpuBuffer dstBuffer, final long dstOffset) {
         srcBuffer.checkValidFor(StagingBuffer.this);
         StagingBuffer.this.copyTo(this.encoder, dstBuffer, dstOffset, (long)srcBuffer.offset, (long)srcBuffer.size);
      }

      public void close() {
         StagingBuffer.this.tryClearAndRotate(this.encoder);
      }

      public void checkValidFor(final StagingBuffer stagingBuffer) {
         if (stagingBuffer != StagingBuffer.this) {
            throw new IllegalArgumentException("Uploader is not valid for " + String.valueOf(stagingBuffer));
         }
      }
   }

   public class BufferHandle implements AutoCloseable {
      private final int offset;
      private final int size;
      private boolean closed;

      public BufferHandle(final int offset, final int size) {
         Objects.requireNonNull(StagingBuffer.this);
         super();
         this.offset = offset;
         this.size = size;
      }

      private void checkValidFor(final StagingBuffer stagingBuffer) {
         if (this.closed) {
            throw new IllegalStateException("Buffer has already been closed");
         } else if (stagingBuffer != StagingBuffer.this) {
            throw new IllegalArgumentException("Buffer is not valid for " + String.valueOf(stagingBuffer));
         }
      }

      public long size() {
         return (long)this.size;
      }

      public void close() {
         if (!this.closed) {
            this.closed = true;
            --StagingBuffer.this.usedBufferCount;
         }
      }
   }
}
