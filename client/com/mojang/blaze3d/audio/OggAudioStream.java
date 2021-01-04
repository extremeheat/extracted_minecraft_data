package com.mojang.blaze3d.audio;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import javax.annotation.Nullable;
import javax.sound.sampled.AudioFormat;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.util.Mth;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisAlloc;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public class OggAudioStream implements AudioStream {
   private long handle;
   private final AudioFormat audioFormat;
   private final InputStream input;
   private ByteBuffer buffer = MemoryUtil.memAlloc(8192);

   public OggAudioStream(InputStream var1) throws IOException {
      super();
      this.input = var1;
      this.buffer.limit(0);
      MemoryStack var2 = MemoryStack.stackPush();
      Throwable var3 = null;

      try {
         IntBuffer var4 = var2.mallocInt(1);
         IntBuffer var5 = var2.mallocInt(1);

         while(this.handle == 0L) {
            if (!this.refillFromStream()) {
               throw new IOException("Failed to find Ogg header");
            }

            int var6 = this.buffer.position();
            this.buffer.position(0);
            this.handle = STBVorbis.stb_vorbis_open_pushdata(this.buffer, var4, var5, (STBVorbisAlloc)null);
            this.buffer.position(var6);
            int var7 = var5.get(0);
            if (var7 == 1) {
               this.forwardBuffer();
            } else if (var7 != 0) {
               throw new IOException("Failed to read Ogg file " + var7);
            }
         }

         this.buffer.position(this.buffer.position() + var4.get(0));
         STBVorbisInfo var17 = STBVorbisInfo.mallocStack(var2);
         STBVorbis.stb_vorbis_get_info(this.handle, var17);
         this.audioFormat = new AudioFormat((float)var17.sample_rate(), 16, var17.channels(), true, false);
      } catch (Throwable var15) {
         var3 = var15;
         throw var15;
      } finally {
         if (var2 != null) {
            if (var3 != null) {
               try {
                  var2.close();
               } catch (Throwable var14) {
                  var3.addSuppressed(var14);
               }
            } else {
               var2.close();
            }
         }

      }
   }

   private boolean refillFromStream() throws IOException {
      int var1 = this.buffer.limit();
      int var2 = this.buffer.capacity() - var1;
      if (var2 == 0) {
         return true;
      } else {
         byte[] var3 = new byte[var2];
         int var4 = this.input.read(var3);
         if (var4 == -1) {
            return false;
         } else {
            int var5 = this.buffer.position();
            this.buffer.limit(var1 + var4);
            this.buffer.position(var1);
            this.buffer.put(var3, 0, var4);
            this.buffer.position(var5);
            return true;
         }
      }
   }

   private void forwardBuffer() {
      boolean var1 = this.buffer.position() == 0;
      boolean var2 = this.buffer.position() == this.buffer.limit();
      if (var2 && !var1) {
         this.buffer.position(0);
         this.buffer.limit(0);
      } else {
         ByteBuffer var3 = MemoryUtil.memAlloc(var1 ? 2 * this.buffer.capacity() : this.buffer.capacity());
         var3.put(this.buffer);
         MemoryUtil.memFree(this.buffer);
         var3.flip();
         this.buffer = var3;
      }

   }

   private boolean readFrame(OggAudioStream.OutputConcat var1) throws IOException {
      if (this.handle == 0L) {
         return false;
      } else {
         MemoryStack var2 = MemoryStack.stackPush();
         Throwable var3 = null;

         try {
            PointerBuffer var4 = var2.mallocPointer(1);
            IntBuffer var5 = var2.mallocInt(1);
            IntBuffer var6 = var2.mallocInt(1);

            while(true) {
               int var7 = STBVorbis.stb_vorbis_decode_frame_pushdata(this.handle, this.buffer, var5, var4, var6);
               this.buffer.position(this.buffer.position() + var7);
               int var8 = STBVorbis.stb_vorbis_get_error(this.handle);
               if (var8 == 1) {
                  this.forwardBuffer();
                  if (!this.refillFromStream()) {
                     boolean var25 = false;
                     return var25;
                  }
               } else {
                  if (var8 != 0) {
                     throw new IOException("Failed to read Ogg file " + var8);
                  }

                  int var9 = var6.get(0);
                  if (var9 != 0) {
                     int var10 = var5.get(0);
                     PointerBuffer var11 = var4.getPointerBuffer(var10);
                     boolean var12;
                     if (var10 == 1) {
                        this.convertMono(var11.getFloatBuffer(0, var9), var1);
                        var12 = true;
                        return var12;
                     }

                     if (var10 == 2) {
                        this.convertStereo(var11.getFloatBuffer(0, var9), var11.getFloatBuffer(1, var9), var1);
                        var12 = true;
                        return var12;
                     }

                     throw new IllegalStateException("Invalid number of channels: " + var10);
                  }
               }
            }
         } catch (Throwable var23) {
            var3 = var23;
            throw var23;
         } finally {
            if (var2 != null) {
               if (var3 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var22) {
                     var3.addSuppressed(var22);
                  }
               } else {
                  var2.close();
               }
            }

         }
      }
   }

   private void convertMono(FloatBuffer var1, OggAudioStream.OutputConcat var2) {
      while(var1.hasRemaining()) {
         var2.put(var1.get());
      }

   }

   private void convertStereo(FloatBuffer var1, FloatBuffer var2, OggAudioStream.OutputConcat var3) {
      while(var1.hasRemaining() && var2.hasRemaining()) {
         var3.put(var1.get());
         var3.put(var2.get());
      }

   }

   public void close() throws IOException {
      if (this.handle != 0L) {
         STBVorbis.stb_vorbis_close(this.handle);
         this.handle = 0L;
      }

      MemoryUtil.memFree(this.buffer);
      this.input.close();
   }

   public AudioFormat getFormat() {
      return this.audioFormat;
   }

   @Nullable
   public ByteBuffer read(int var1) throws IOException {
      OggAudioStream.OutputConcat var2 = new OggAudioStream.OutputConcat(var1 + 8192);

      while(this.readFrame(var2) && var2.byteCount < var1) {
      }

      return var2.get();
   }

   public ByteBuffer readAll() throws IOException {
      OggAudioStream.OutputConcat var1 = new OggAudioStream.OutputConcat(16384);

      while(this.readFrame(var1)) {
      }

      return var1.get();
   }

   static class OutputConcat {
      private final List<ByteBuffer> buffers = Lists.newArrayList();
      private final int bufferSize;
      private int byteCount;
      private ByteBuffer currentBuffer;

      public OutputConcat(int var1) {
         super();
         this.bufferSize = var1 + 1 & -2;
         this.createNewBuffer();
      }

      private void createNewBuffer() {
         this.currentBuffer = BufferUtils.createByteBuffer(this.bufferSize);
      }

      public void put(float var1) {
         if (this.currentBuffer.remaining() == 0) {
            this.currentBuffer.flip();
            this.buffers.add(this.currentBuffer);
            this.createNewBuffer();
         }

         int var2 = Mth.clamp((int)(var1 * 32767.5F - 0.5F), -32768, 32767);
         this.currentBuffer.putShort((short)var2);
         this.byteCount += 2;
      }

      public ByteBuffer get() {
         this.currentBuffer.flip();
         if (this.buffers.isEmpty()) {
            return this.currentBuffer;
         } else {
            ByteBuffer var1 = BufferUtils.createByteBuffer(this.byteCount);
            this.buffers.forEach(var1::put);
            var1.put(this.currentBuffer);
            var1.flip();
            return var1;
         }
      }
   }
}
