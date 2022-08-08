package com.mojang.blaze3d.audio;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Objects;
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
   private static final int EXPECTED_MAX_FRAME_SIZE = 8192;
   private long handle;
   private final AudioFormat audioFormat;
   private final InputStream input;
   private ByteBuffer buffer = MemoryUtil.memAlloc(8192);

   public OggAudioStream(InputStream var1) throws IOException {
      super();
      this.input = var1;
      this.buffer.limit(0);
      MemoryStack var2 = MemoryStack.stackPush();

      try {
         IntBuffer var3 = var2.mallocInt(1);
         IntBuffer var4 = var2.mallocInt(1);

         while(true) {
            if (this.handle != 0L) {
               this.buffer.position(this.buffer.position() + var3.get(0));
               STBVorbisInfo var9 = STBVorbisInfo.mallocStack(var2);
               STBVorbis.stb_vorbis_get_info(this.handle, var9);
               this.audioFormat = new AudioFormat((float)var9.sample_rate(), 16, var9.channels(), true, false);
               break;
            }

            if (!this.refillFromStream()) {
               throw new IOException("Failed to find Ogg header");
            }

            int var5 = this.buffer.position();
            this.buffer.position(0);
            this.handle = STBVorbis.stb_vorbis_open_pushdata(this.buffer, var3, var4, (STBVorbisAlloc)null);
            this.buffer.position(var5);
            int var6 = var4.get(0);
            if (var6 == 1) {
               this.forwardBuffer();
            } else if (var6 != 0) {
               throw new IOException("Failed to read Ogg file " + var6);
            }
         }
      } catch (Throwable var8) {
         if (var2 != null) {
            try {
               var2.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (var2 != null) {
         var2.close();
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

   private boolean readFrame(OutputConcat var1) throws IOException {
      if (this.handle == 0L) {
         return false;
      } else {
         MemoryStack var2 = MemoryStack.stackPush();

         boolean var14;
         label79: {
            boolean var11;
            label80: {
               try {
                  PointerBuffer var3 = var2.mallocPointer(1);
                  IntBuffer var4 = var2.mallocInt(1);
                  IntBuffer var5 = var2.mallocInt(1);

                  while(true) {
                     int var6 = STBVorbis.stb_vorbis_decode_frame_pushdata(this.handle, this.buffer, var4, var3, var5);
                     this.buffer.position(this.buffer.position() + var6);
                     int var7 = STBVorbis.stb_vorbis_get_error(this.handle);
                     if (var7 == 1) {
                        this.forwardBuffer();
                        if (!this.refillFromStream()) {
                           var14 = false;
                           break label79;
                        }
                     } else {
                        if (var7 != 0) {
                           throw new IOException("Failed to read Ogg file " + var7);
                        }

                        int var8 = var5.get(0);
                        if (var8 != 0) {
                           int var9 = var4.get(0);
                           PointerBuffer var10 = var3.getPointerBuffer(var9);
                           if (var9 == 1) {
                              this.convertMono(var10.getFloatBuffer(0, var8), var1);
                              var11 = true;
                              break label80;
                           }

                           if (var9 != 2) {
                              throw new IllegalStateException("Invalid number of channels: " + var9);
                           }

                           this.convertStereo(var10.getFloatBuffer(0, var8), var10.getFloatBuffer(1, var8), var1);
                           var11 = true;
                           break;
                        }
                     }
                  }
               } catch (Throwable var13) {
                  if (var2 != null) {
                     try {
                        var2.close();
                     } catch (Throwable var12) {
                        var13.addSuppressed(var12);
                     }
                  }

                  throw var13;
               }

               if (var2 != null) {
                  var2.close();
               }

               return var11;
            }

            if (var2 != null) {
               var2.close();
            }

            return var11;
         }

         if (var2 != null) {
            var2.close();
         }

         return var14;
      }
   }

   private void convertMono(FloatBuffer var1, OutputConcat var2) {
      while(var1.hasRemaining()) {
         var2.put(var1.get());
      }

   }

   private void convertStereo(FloatBuffer var1, FloatBuffer var2, OutputConcat var3) {
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

   public ByteBuffer read(int var1) throws IOException {
      OutputConcat var2 = new OutputConcat(var1 + 8192);

      while(this.readFrame(var2) && var2.byteCount < var1) {
      }

      return var2.get();
   }

   public ByteBuffer readAll() throws IOException {
      OutputConcat var1 = new OutputConcat(16384);

      while(this.readFrame(var1)) {
      }

      return var1.get();
   }

   private static class OutputConcat {
      private final List<ByteBuffer> buffers = Lists.newArrayList();
      private final int bufferSize;
      int byteCount;
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

         int var2 = Mth.clamp((int)((int)(var1 * 32767.5F - 0.5F)), (int)-32768, (int)32767);
         this.currentBuffer.putShort((short)var2);
         this.byteCount += 2;
      }

      public ByteBuffer get() {
         this.currentBuffer.flip();
         if (this.buffers.isEmpty()) {
            return this.currentBuffer;
         } else {
            ByteBuffer var1 = BufferUtils.createByteBuffer(this.byteCount);
            List var10000 = this.buffers;
            Objects.requireNonNull(var1);
            var10000.forEach(var1::put);
            var1.put(this.currentBuffer);
            var1.flip();
            return var1;
         }
      }
   }
}
