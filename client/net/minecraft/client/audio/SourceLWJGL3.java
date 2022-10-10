package net.minecraft.client.audio;

import com.google.common.collect.Lists;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;
import javax.sound.sampled.AudioFormat;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import paulscode.sound.Channel;
import paulscode.sound.FilenameURL;
import paulscode.sound.SoundBuffer;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.Source;

public class SourceLWJGL3 extends Source {
   private ChannelLWJGL3 field_195861_a;
   private IntBuffer field_195862_b;
   private FloatBuffer field_195863_c;
   private FloatBuffer field_195864_d;
   private FloatBuffer field_195865_e;

   public SourceLWJGL3(FloatBuffer var1, IntBuffer var2, boolean var3, boolean var4, boolean var5, String var6, FilenameURL var7, SoundBuffer var8, float var9, float var10, float var11, int var12, float var13, boolean var14) {
      super(var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14);
      this.field_195861_a = (ChannelLWJGL3)this.channel;
      if (this.codec != null) {
         this.codec.reverseByteOrder(true);
      }

      this.field_195863_c = var1;
      this.field_195862_b = var2;
      this.libraryType = LibraryLWJGL3.class;
      this.pitch = 1.0F;
      this.func_195858_b();
   }

   public SourceLWJGL3(FloatBuffer var1, IntBuffer var2, Source var3, SoundBuffer var4) {
      super(var3, var4);
      this.field_195861_a = (ChannelLWJGL3)this.channel;
      if (this.codec != null) {
         this.codec.reverseByteOrder(true);
      }

      this.field_195863_c = var1;
      this.field_195862_b = var2;
      this.libraryType = LibraryLWJGL3.class;
      this.pitch = 1.0F;
      this.func_195858_b();
   }

   public SourceLWJGL3(FloatBuffer var1, AudioFormat var2, boolean var3, String var4, float var5, float var6, float var7, int var8, float var9) {
      super(var2, var3, var4, var5, var6, var7, var8, var9);
      this.field_195861_a = (ChannelLWJGL3)this.channel;
      this.field_195863_c = var1;
      this.libraryType = LibraryLWJGL3.class;
      this.pitch = 1.0F;
      this.func_195858_b();
   }

   public boolean incrementSoundSequence() {
      if (!this.toStream) {
         this.errorMessage("Method 'incrementSoundSequence' may only be used for streaming sources.");
         return false;
      } else {
         synchronized(this.soundSequenceLock) {
            if (this.soundSequenceQueue != null && !this.soundSequenceQueue.isEmpty()) {
               this.filenameURL = (FilenameURL)this.soundSequenceQueue.remove(0);
               if (this.codec != null) {
                  this.codec.cleanup();
               }

               this.codec = SoundSystemConfig.getCodec(this.filenameURL.getFilename());
               if (this.codec == null) {
                  return true;
               } else {
                  this.codec.reverseByteOrder(true);
                  if (this.codec.getAudioFormat() == null) {
                     this.codec.initialize(this.filenameURL.getURL());
                  }

                  AudioFormat var2 = this.codec.getAudioFormat();
                  if (var2 == null) {
                     this.errorMessage("Audio Format null in method 'incrementSoundSequence'");
                     return false;
                  } else {
                     short var3;
                     if (var2.getChannels() == 1) {
                        if (var2.getSampleSizeInBits() == 8) {
                           var3 = 4352;
                        } else {
                           if (var2.getSampleSizeInBits() != 16) {
                              this.errorMessage("Illegal sample size in method 'incrementSoundSequence'");
                              return false;
                           }

                           var3 = 4353;
                        }
                     } else {
                        if (var2.getChannels() != 2) {
                           this.errorMessage("Audio data neither mono nor stereo in method 'incrementSoundSequence'");
                           return false;
                        }

                        if (var2.getSampleSizeInBits() == 8) {
                           var3 = 4354;
                        } else {
                           if (var2.getSampleSizeInBits() != 16) {
                              this.errorMessage("Illegal sample size in method 'incrementSoundSequence'");
                              return false;
                           }

                           var3 = 4355;
                        }
                     }

                     this.field_195861_a.func_195848_a(var3, (int)var2.getSampleRate());
                     this.preLoad = true;
                     return true;
                  }
               }
            } else {
               return false;
            }
         }
      }
   }

   public void listenerMoved() {
      this.positionChanged();
   }

   public void setPosition(float var1, float var2, float var3) {
      super.setPosition(var1, var2, var3);
      if (this.field_195864_d == null) {
         this.func_195858_b();
      } else {
         this.positionChanged();
      }

      this.field_195864_d.put(0, var1);
      this.field_195864_d.put(1, var2);
      this.field_195864_d.put(2, var3);
      if (this.channel != null && this.channel.attachedSource == this && this.field_195861_a != null && this.field_195861_a.field_195851_a != null) {
         AL10.alSourcefv(this.field_195861_a.field_195851_a.get(0), 4100, this.field_195864_d);
         this.func_195857_e();
      }

   }

   public void positionChanged() {
      this.func_195859_c();
      this.func_195860_d();
      if (this.channel != null && this.channel.attachedSource == this && this.field_195861_a != null && this.field_195861_a.field_195851_a != null) {
         AL10.alSourcef(this.field_195861_a.field_195851_a.get(0), 4106, this.gain * this.sourceVolume * Math.abs(this.fadeOutGain) * this.fadeInGain);
         this.func_195857_e();
      }

      this.func_195856_a();
   }

   private void func_195856_a() {
      if (this.channel != null && this.channel.attachedSource == this && LibraryLWJGL3.func_195836_a() && this.field_195861_a != null && this.field_195861_a.field_195851_a != null) {
         AL10.alSourcef(this.field_195861_a.field_195851_a.get(0), 4099, this.pitch);
         this.func_195857_e();
      }

   }

   public void setLooping(boolean var1) {
      super.setLooping(var1);
      if (this.channel != null && this.channel.attachedSource == this && this.field_195861_a != null && this.field_195861_a.field_195851_a != null) {
         AL10.alSourcei(this.field_195861_a.field_195851_a.get(0), 4103, var1 ? 1 : 0);
         this.func_195857_e();
      }

   }

   public void setAttenuation(int var1) {
      super.setAttenuation(var1);
      if (this.channel != null && this.channel.attachedSource == this && this.field_195861_a != null && this.field_195861_a.field_195851_a != null) {
         if (var1 == 1) {
            AL10.alSourcef(this.field_195861_a.field_195851_a.get(0), 4129, this.distOrRoll);
         } else {
            AL10.alSourcef(this.field_195861_a.field_195851_a.get(0), 4129, 0.0F);
         }

         this.func_195857_e();
      }

   }

   public void setDistOrRoll(float var1) {
      super.setDistOrRoll(var1);
      if (this.channel != null && this.channel.attachedSource == this && this.field_195861_a != null && this.field_195861_a.field_195851_a != null) {
         if (this.attModel == 1) {
            AL10.alSourcef(this.field_195861_a.field_195851_a.get(0), 4129, var1);
         } else {
            AL10.alSourcef(this.field_195861_a.field_195851_a.get(0), 4129, 0.0F);
         }

         this.func_195857_e();
      }

   }

   public void setVelocity(float var1, float var2, float var3) {
      super.setVelocity(var1, var2, var3);
      this.field_195865_e = BufferUtils.createFloatBuffer(3).put(new float[]{var1, var2, var3});
      this.field_195865_e.flip();
      if (this.channel != null && this.channel.attachedSource == this && this.field_195861_a != null && this.field_195861_a.field_195851_a != null) {
         AL10.alSourcefv(this.field_195861_a.field_195851_a.get(0), 4102, this.field_195865_e);
         this.func_195857_e();
      }

   }

   public void setPitch(float var1) {
      super.setPitch(var1);
      this.func_195856_a();
   }

   public void play(Channel var1) {
      if (!this.active()) {
         if (this.toLoop) {
            this.toPlay = true;
         }

      } else if (var1 == null) {
         this.errorMessage("Unable to play source, because channel was null");
      } else {
         boolean var2 = this.channel != var1;
         if (this.channel != null && this.channel.attachedSource != this) {
            var2 = true;
         }

         boolean var3 = this.paused();
         super.play(var1);
         this.field_195861_a = (ChannelLWJGL3)this.channel;
         if (var2) {
            this.setPosition(this.position.x, this.position.y, this.position.z);
            this.func_195856_a();
            if (this.field_195861_a != null && this.field_195861_a.field_195851_a != null) {
               if (LibraryLWJGL3.func_195836_a()) {
                  AL10.alSourcef(this.field_195861_a.field_195851_a.get(0), 4099, this.pitch);
                  this.func_195857_e();
               }

               AL10.alSourcefv(this.field_195861_a.field_195851_a.get(0), 4100, this.field_195864_d);
               this.func_195857_e();
               AL10.alSourcefv(this.field_195861_a.field_195851_a.get(0), 4102, this.field_195865_e);
               this.func_195857_e();
               if (this.attModel == 1) {
                  AL10.alSourcef(this.field_195861_a.field_195851_a.get(0), 4129, this.distOrRoll);
               } else {
                  AL10.alSourcef(this.field_195861_a.field_195851_a.get(0), 4129, 0.0F);
               }

               this.func_195857_e();
               if (this.toLoop && !this.toStream) {
                  AL10.alSourcei(this.field_195861_a.field_195851_a.get(0), 4103, 1);
               } else {
                  AL10.alSourcei(this.field_195861_a.field_195851_a.get(0), 4103, 0);
               }

               this.func_195857_e();
            }

            if (!this.toStream) {
               if (this.field_195862_b == null) {
                  this.errorMessage("No sound buffer to play");
                  return;
               }

               this.field_195861_a.func_195847_a(this.field_195862_b);
            }
         }

         if (!this.playing()) {
            if (this.toStream && !var3) {
               if (this.codec == null) {
                  this.errorMessage("Decoder null in method 'play'");
                  return;
               }

               if (this.codec.getAudioFormat() == null) {
                  this.codec.initialize(this.filenameURL.getURL());
               }

               AudioFormat var4 = this.codec.getAudioFormat();
               if (var4 == null) {
                  this.errorMessage("Audio Format null in method 'play'");
                  return;
               }

               short var5;
               if (var4.getChannels() == 1) {
                  if (var4.getSampleSizeInBits() == 8) {
                     var5 = 4352;
                  } else {
                     if (var4.getSampleSizeInBits() != 16) {
                        this.errorMessage("Illegal sample size in method 'play'");
                        return;
                     }

                     var5 = 4353;
                  }
               } else {
                  if (var4.getChannels() != 2) {
                     this.errorMessage("Audio data neither mono nor stereo in method 'play'");
                     return;
                  }

                  if (var4.getSampleSizeInBits() == 8) {
                     var5 = 4354;
                  } else {
                     if (var4.getSampleSizeInBits() != 16) {
                        this.errorMessage("Illegal sample size in method 'play'");
                        return;
                     }

                     var5 = 4355;
                  }
               }

               this.field_195861_a.func_195848_a(var5, (int)var4.getSampleRate());
               this.preLoad = true;
            }

            this.channel.play();
            if (this.pitch != 1.0F) {
               this.func_195856_a();
            }
         }

      }
   }

   public boolean preLoad() {
      if (this.codec == null) {
         return false;
      } else {
         this.codec.initialize(this.filenameURL.getURL());
         LinkedList var1 = Lists.newLinkedList();

         for(int var2 = 0; var2 < SoundSystemConfig.getNumberStreamingBuffers(); ++var2) {
            this.soundBuffer = this.codec.read();
            if (this.soundBuffer == null || this.soundBuffer.audioData == null) {
               break;
            }

            var1.add(this.soundBuffer.audioData);
         }

         this.positionChanged();
         this.channel.preLoadBuffers(var1);
         this.preLoad = false;
         return true;
      }
   }

   private void func_195858_b() {
      this.field_195864_d = BufferUtils.createFloatBuffer(3).put(new float[]{this.position.x, this.position.y, this.position.z});
      this.field_195865_e = BufferUtils.createFloatBuffer(3).put(new float[]{this.velocity.x, this.velocity.y, this.velocity.z});
      this.field_195864_d.flip();
      this.field_195865_e.flip();
      this.positionChanged();
   }

   private void func_195859_c() {
      if (this.field_195863_c != null) {
         double var1 = (double)(this.position.x - this.field_195863_c.get(0));
         double var3 = (double)(this.position.y - this.field_195863_c.get(1));
         double var5 = (double)(this.position.z - this.field_195863_c.get(2));
         this.distanceFromListener = (float)Math.sqrt(var1 * var1 + var3 * var3 + var5 * var5);
      }

   }

   private void func_195860_d() {
      if (this.attModel == 2) {
         if (this.distanceFromListener <= 0.0F) {
            this.gain = 1.0F;
         } else if (this.distanceFromListener >= this.distOrRoll) {
            this.gain = 0.0F;
         } else {
            this.gain = 1.0F - this.distanceFromListener / this.distOrRoll;
         }

         if (this.gain > 1.0F) {
            this.gain = 1.0F;
         }

         if (this.gain < 0.0F) {
            this.gain = 0.0F;
         }
      } else {
         this.gain = 1.0F;
      }

   }

   private boolean func_195857_e() {
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
