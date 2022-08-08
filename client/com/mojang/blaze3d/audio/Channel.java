package com.mojang.blaze3d.audio;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nullable;
import javax.sound.sampled.AudioFormat;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.openal.AL10;
import org.slf4j.Logger;

public class Channel {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int QUEUED_BUFFER_COUNT = 4;
   public static final int BUFFER_DURATION_SECONDS = 1;
   private final int source;
   private final AtomicBoolean initialized = new AtomicBoolean(true);
   private int streamingBufferSize = 16384;
   @Nullable
   private AudioStream stream;

   @Nullable
   static Channel create() {
      int[] var0 = new int[1];
      AL10.alGenSources(var0);
      return OpenAlUtil.checkALError("Allocate new source") ? null : new Channel(var0[0]);
   }

   private Channel(int var1) {
      super();
      this.source = var1;
   }

   public void destroy() {
      if (this.initialized.compareAndSet(true, false)) {
         AL10.alSourceStop(this.source);
         OpenAlUtil.checkALError("Stop");
         if (this.stream != null) {
            try {
               this.stream.close();
            } catch (IOException var2) {
               LOGGER.error("Failed to close audio stream", var2);
            }

            this.removeProcessedBuffers();
            this.stream = null;
         }

         AL10.alDeleteSources(new int[]{this.source});
         OpenAlUtil.checkALError("Cleanup");
      }

   }

   public void play() {
      AL10.alSourcePlay(this.source);
   }

   private int getState() {
      return !this.initialized.get() ? 4116 : AL10.alGetSourcei(this.source, 4112);
   }

   public void pause() {
      if (this.getState() == 4114) {
         AL10.alSourcePause(this.source);
      }

   }

   public void unpause() {
      if (this.getState() == 4115) {
         AL10.alSourcePlay(this.source);
      }

   }

   public void stop() {
      if (this.initialized.get()) {
         AL10.alSourceStop(this.source);
         OpenAlUtil.checkALError("Stop");
      }

   }

   public boolean playing() {
      return this.getState() == 4114;
   }

   public boolean stopped() {
      return this.getState() == 4116;
   }

   public void setSelfPosition(Vec3 var1) {
      AL10.alSourcefv(this.source, 4100, new float[]{(float)var1.x, (float)var1.y, (float)var1.z});
   }

   public void setPitch(float var1) {
      AL10.alSourcef(this.source, 4099, var1);
   }

   public void setLooping(boolean var1) {
      AL10.alSourcei(this.source, 4103, var1 ? 1 : 0);
   }

   public void setVolume(float var1) {
      AL10.alSourcef(this.source, 4106, var1);
   }

   public void disableAttenuation() {
      AL10.alSourcei(this.source, 53248, 0);
   }

   public void linearAttenuation(float var1) {
      AL10.alSourcei(this.source, 53248, 53251);
      AL10.alSourcef(this.source, 4131, var1);
      AL10.alSourcef(this.source, 4129, 1.0F);
      AL10.alSourcef(this.source, 4128, 0.0F);
   }

   public void setRelative(boolean var1) {
      AL10.alSourcei(this.source, 514, var1 ? 1 : 0);
   }

   public void attachStaticBuffer(SoundBuffer var1) {
      var1.getAlBuffer().ifPresent((var1x) -> {
         AL10.alSourcei(this.source, 4105, var1x);
      });
   }

   public void attachBufferStream(AudioStream var1) {
      this.stream = var1;
      AudioFormat var2 = var1.getFormat();
      this.streamingBufferSize = calculateBufferSize(var2, 1);
      this.pumpBuffers(4);
   }

   private static int calculateBufferSize(AudioFormat var0, int var1) {
      return (int)((float)(var1 * var0.getSampleSizeInBits()) / 8.0F * (float)var0.getChannels() * var0.getSampleRate());
   }

   private void pumpBuffers(int var1) {
      if (this.stream != null) {
         try {
            for(int var2 = 0; var2 < var1; ++var2) {
               ByteBuffer var3 = this.stream.read(this.streamingBufferSize);
               if (var3 != null) {
                  (new SoundBuffer(var3, this.stream.getFormat())).releaseAlBuffer().ifPresent((var1x) -> {
                     AL10.alSourceQueueBuffers(this.source, new int[]{var1x});
                  });
               }
            }
         } catch (IOException var4) {
            LOGGER.error("Failed to read from audio stream", var4);
         }
      }

   }

   public void updateStream() {
      if (this.stream != null) {
         int var1 = this.removeProcessedBuffers();
         this.pumpBuffers(var1);
      }

   }

   private int removeProcessedBuffers() {
      int var1 = AL10.alGetSourcei(this.source, 4118);
      if (var1 > 0) {
         int[] var2 = new int[var1];
         AL10.alSourceUnqueueBuffers(this.source, var2);
         OpenAlUtil.checkALError("Unqueue buffers");
         AL10.alDeleteBuffers(var2);
         OpenAlUtil.checkALError("Remove processed buffers");
      }

      return var1;
   }
}
