package net.minecraft.client.audio;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;
import javax.sound.sampled.AudioFormat;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import paulscode.sound.Channel;

public class ChannelLWJGL3 extends Channel {
   public IntBuffer field_195851_a;
   public int field_195852_b;
   public int field_195853_c;
   public float field_195854_d;

   public ChannelLWJGL3(int var1, IntBuffer var2) {
      super(var1);
      this.libraryType = LibraryLWJGL3.class;
      this.field_195851_a = var2;
   }

   public void cleanup() {
      if (this.field_195851_a != null) {
         try {
            AL10.alSourceStop(this.field_195851_a.get(0));
            AL10.alGetError();
         } catch (Exception var3) {
         }

         try {
            AL10.alDeleteSources(this.field_195851_a);
            AL10.alGetError();
         } catch (Exception var2) {
         }

         this.field_195851_a.clear();
      }

      this.field_195851_a = null;
      super.cleanup();
   }

   public boolean func_195847_a(IntBuffer var1) {
      if (this.errorCheck(this.channelType != 0, "Sound buffers may only be attached to normal sources.")) {
         return false;
      } else {
         AL10.alSourcei(this.field_195851_a.get(0), 4105, var1.get(0));
         if (this.attachedSource != null && this.attachedSource.soundBuffer != null && this.attachedSource.soundBuffer.audioFormat != null) {
            this.setAudioFormat(this.attachedSource.soundBuffer.audioFormat);
         }

         return this.func_195849_a();
      }
   }

   public void setAudioFormat(AudioFormat var1) {
      short var2;
      if (var1.getChannels() == 1) {
         if (var1.getSampleSizeInBits() == 8) {
            var2 = 4352;
         } else {
            if (var1.getSampleSizeInBits() != 16) {
               this.errorMessage("Illegal sample size in method 'setAudioFormat'");
               return;
            }

            var2 = 4353;
         }
      } else {
         if (var1.getChannels() != 2) {
            this.errorMessage("Audio data neither mono nor stereo in method 'setAudioFormat'");
            return;
         }

         if (var1.getSampleSizeInBits() == 8) {
            var2 = 4354;
         } else {
            if (var1.getSampleSizeInBits() != 16) {
               this.errorMessage("Illegal sample size in method 'setAudioFormat'");
               return;
            }

            var2 = 4355;
         }
      }

      this.field_195852_b = var2;
      this.field_195853_c = (int)var1.getSampleRate();
   }

   public void func_195848_a(int var1, int var2) {
      this.field_195852_b = var1;
      this.field_195853_c = var2;
   }

   public boolean preLoadBuffers(LinkedList<byte[]> var1) {
      if (this.errorCheck(this.channelType != 1, "Buffers may only be queued for streaming sources.")) {
         return false;
      } else if (this.errorCheck(var1 == null, "Buffer List null in method 'preLoadBuffers'")) {
         return false;
      } else {
         boolean var2 = this.playing();
         if (var2) {
            AL10.alSourceStop(this.field_195851_a.get(0));
            this.func_195849_a();
         }

         int var3 = AL10.alGetSourcei(this.field_195851_a.get(0), 4118);
         IntBuffer var4;
         if (var3 > 0) {
            var4 = BufferUtils.createIntBuffer(var3);
            AL10.alGenBuffers(var4);
            if (this.errorCheck(this.func_195849_a(), "Error clearing stream buffers in method 'preLoadBuffers'")) {
               return false;
            }

            AL10.alSourceUnqueueBuffers(this.field_195851_a.get(0), var4);
            if (this.errorCheck(this.func_195849_a(), "Error unqueuing stream buffers in method 'preLoadBuffers'")) {
               return false;
            }
         }

         if (var2) {
            AL10.alSourcePlay(this.field_195851_a.get(0));
            this.func_195849_a();
         }

         var4 = BufferUtils.createIntBuffer(var1.size());
         AL10.alGenBuffers(var4);
         if (this.errorCheck(this.func_195849_a(), "Error generating stream buffers in method 'preLoadBuffers'")) {
            return false;
         } else {
            for(int var6 = 0; var6 < var1.size(); ++var6) {
               ByteBuffer var5 = (ByteBuffer)BufferUtils.createByteBuffer(((byte[])var1.get(var6)).length).put((byte[])var1.get(var6)).flip();

               try {
                  AL10.alBufferData(var4.get(var6), this.field_195852_b, var5, this.field_195853_c);
               } catch (Exception var9) {
                  this.errorMessage("Error creating buffers in method 'preLoadBuffers'");
                  this.printStackTrace(var9);
                  return false;
               }

               if (this.errorCheck(this.func_195849_a(), "Error creating buffers in method 'preLoadBuffers'")) {
                  return false;
               }
            }

            try {
               AL10.alSourceQueueBuffers(this.field_195851_a.get(0), var4);
            } catch (Exception var8) {
               this.errorMessage("Error queuing buffers in method 'preLoadBuffers'");
               this.printStackTrace(var8);
               return false;
            }

            if (this.errorCheck(this.func_195849_a(), "Error queuing buffers in method 'preLoadBuffers'")) {
               return false;
            } else {
               AL10.alSourcePlay(this.field_195851_a.get(0));
               return !this.errorCheck(this.func_195849_a(), "Error playing source in method 'preLoadBuffers'");
            }
         }
      }
   }

   public boolean queueBuffer(byte[] var1) {
      if (this.errorCheck(this.channelType != 1, "Buffers may only be queued for streaming sources.")) {
         return false;
      } else {
         ByteBuffer var2 = (ByteBuffer)BufferUtils.createByteBuffer(var1.length).put(var1).flip();
         IntBuffer var3 = BufferUtils.createIntBuffer(1);
         AL10.alSourceUnqueueBuffers(this.field_195851_a.get(0), var3);
         if (this.func_195849_a()) {
            return false;
         } else {
            if (AL10.alIsBuffer(var3.get(0))) {
               this.field_195854_d += this.func_195850_a(var3.get(0));
            }

            this.func_195849_a();
            AL10.alBufferData(var3.get(0), this.field_195852_b, var2, this.field_195853_c);
            if (this.func_195849_a()) {
               return false;
            } else {
               AL10.alSourceQueueBuffers(this.field_195851_a.get(0), var3);
               return !this.func_195849_a();
            }
         }
      }
   }

   public int feedRawAudioData(byte[] var1) {
      if (this.errorCheck(this.channelType != 1, "Raw audio data can only be fed to streaming sources.")) {
         return -1;
      } else {
         ByteBuffer var2 = (ByteBuffer)BufferUtils.createByteBuffer(var1.length).put(var1).flip();
         int var4 = AL10.alGetSourcei(this.field_195851_a.get(0), 4118);
         IntBuffer var3;
         if (var4 > 0) {
            var3 = BufferUtils.createIntBuffer(var4);
            AL10.alGenBuffers(var3);
            if (this.errorCheck(this.func_195849_a(), "Error clearing stream buffers in method 'feedRawAudioData'")) {
               return -1;
            }

            AL10.alSourceUnqueueBuffers(this.field_195851_a.get(0), var3);
            if (this.errorCheck(this.func_195849_a(), "Error unqueuing stream buffers in method 'feedRawAudioData'")) {
               return -1;
            }

            if (AL10.alIsBuffer(var3.get(0))) {
               this.field_195854_d += this.func_195850_a(var3.get(0));
            }

            this.func_195849_a();
         } else {
            var3 = BufferUtils.createIntBuffer(1);
            AL10.alGenBuffers(var3);
            if (this.errorCheck(this.func_195849_a(), "Error generating stream buffers in method 'preLoadBuffers'")) {
               return -1;
            }
         }

         AL10.alBufferData(var3.get(0), this.field_195852_b, var2, this.field_195853_c);
         if (this.func_195849_a()) {
            return -1;
         } else {
            AL10.alSourceQueueBuffers(this.field_195851_a.get(0), var3);
            if (this.func_195849_a()) {
               return -1;
            } else {
               if (this.attachedSource != null && this.attachedSource.channel == this && this.attachedSource.active() && !this.playing()) {
                  AL10.alSourcePlay(this.field_195851_a.get(0));
                  this.func_195849_a();
               }

               return var4;
            }
         }
      }
   }

   public float func_195850_a(int var1) {
      return (float)(1000 * AL10.alGetBufferi(var1, 8196) / AL10.alGetBufferi(var1, 8195)) / ((float)AL10.alGetBufferi(var1, 8194) / 8.0F) / (float)this.field_195853_c;
   }

   public float millisecondsPlayed() {
      float var1 = (float)AL10.alGetSourcei(this.field_195851_a.get(0), 4134);
      float var2 = 1.0F;
      switch(this.field_195852_b) {
      case 4352:
         var2 = 1.0F;
         break;
      case 4353:
         var2 = 2.0F;
         break;
      case 4354:
         var2 = 2.0F;
         break;
      case 4355:
         var2 = 4.0F;
      }

      var1 = var1 / var2 / (float)this.field_195853_c * 1000.0F;
      if (this.channelType == 1) {
         var1 += this.field_195854_d;
      }

      return var1;
   }

   public int buffersProcessed() {
      if (this.channelType != 1) {
         return 0;
      } else {
         int var1 = AL10.alGetSourcei(this.field_195851_a.get(0), 4118);
         return this.func_195849_a() ? 0 : var1;
      }
   }

   public void flush() {
      if (this.channelType == 1) {
         int var1 = AL10.alGetSourcei(this.field_195851_a.get(0), 4117);
         if (!this.func_195849_a()) {
            for(IntBuffer var2 = BufferUtils.createIntBuffer(1); var1 > 0; --var1) {
               try {
                  AL10.alSourceUnqueueBuffers(this.field_195851_a.get(0), var2);
               } catch (Exception var4) {
                  return;
               }

               if (this.func_195849_a()) {
                  return;
               }
            }

            this.field_195854_d = 0.0F;
         }
      }
   }

   public void close() {
      try {
         AL10.alSourceStop(this.field_195851_a.get(0));
         AL10.alGetError();
      } catch (Exception var2) {
      }

      if (this.channelType == 1) {
         this.flush();
      }

   }

   public void play() {
      AL10.alSourcePlay(this.field_195851_a.get(0));
      this.func_195849_a();
   }

   public void pause() {
      AL10.alSourcePause(this.field_195851_a.get(0));
      this.func_195849_a();
   }

   public void stop() {
      AL10.alSourceStop(this.field_195851_a.get(0));
      if (!this.func_195849_a()) {
         this.field_195854_d = 0.0F;
      }

   }

   public void rewind() {
      if (this.channelType != 1) {
         AL10.alSourceRewind(this.field_195851_a.get(0));
         if (!this.func_195849_a()) {
            this.field_195854_d = 0.0F;
         }

      }
   }

   public boolean playing() {
      int var1 = AL10.alGetSourcei(this.field_195851_a.get(0), 4112);
      if (this.func_195849_a()) {
         return false;
      } else {
         return var1 == 4114;
      }
   }

   private boolean func_195849_a() {
      switch(AL10.alGetError()) {
      case 0:
         return false;
      case 40961:
         this.errorMessage("Invalid name parameter.");
         return true;
      case 40962:
         this.errorMessage("Invalid parameter.");
         return true;
      case 40963:
         this.errorMessage("Invalid enumerated parameter value.");
         return true;
      case 40964:
         this.errorMessage("Illegal call.");
         return true;
      case 40965:
         this.errorMessage("Unable to allocate memory.");
         return true;
      default:
         this.errorMessage("An unrecognized error occurred.");
         return true;
      }
   }
}
