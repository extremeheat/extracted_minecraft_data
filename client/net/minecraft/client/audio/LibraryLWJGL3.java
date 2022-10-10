package net.minecraft.client.audio;

import com.google.common.collect.Maps;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.sound.sampled.AudioFormat;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;
import paulscode.sound.Channel;
import paulscode.sound.FilenameURL;
import paulscode.sound.ICodec;
import paulscode.sound.Library;
import paulscode.sound.ListenerData;
import paulscode.sound.SoundBuffer;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.Source;

public class LibraryLWJGL3 extends Library {
   private FloatBuffer field_195839_a;
   private FloatBuffer field_195840_b;
   private FloatBuffer field_195841_c;
   private Map<String, IntBuffer> field_195842_d = Maps.newHashMap();
   private static boolean field_195843_e = true;
   private String field_195844_f = "PreInit";
   private long field_195845_g;
   private long field_195846_h;

   public LibraryLWJGL3() throws SoundSystemException {
      super();
      this.reverseByteOrder = true;
   }

   public void init() throws SoundSystemException {
      boolean var1 = false;
      long var2 = ALC10.alcOpenDevice((ByteBuffer)null);
      if (var2 == 0L) {
         throw new LibraryLWJGL3.LWJGL3SoundSystemException("Failed to open default device", 101);
      } else {
         ALCCapabilities var4 = ALC.createCapabilities(var2);
         if (!var4.OpenALC10) {
            throw new LibraryLWJGL3.LWJGL3SoundSystemException("OpenAL 1.0 not supported", 101);
         } else {
            this.field_195846_h = ALC10.alcCreateContext(var2, (IntBuffer)null);
            ALC10.alcMakeContextCurrent(this.field_195846_h);
            AL.createCapabilities(var4);
            this.message("OpenAL initialized.");
            this.field_195839_a = BufferUtils.createFloatBuffer(3).put(new float[]{this.listener.position.x, this.listener.position.y, this.listener.position.z});
            this.field_195840_b = BufferUtils.createFloatBuffer(6).put(new float[]{this.listener.lookAt.x, this.listener.lookAt.y, this.listener.lookAt.z, this.listener.up.x, this.listener.up.y, this.listener.up.z});
            this.field_195841_c = BufferUtils.createFloatBuffer(3).put(new float[]{0.0F, 0.0F, 0.0F});
            this.field_195839_a.flip();
            this.field_195840_b.flip();
            this.field_195841_c.flip();
            this.field_195844_f = "Post Init";
            AL10.alListenerfv(4100, this.field_195839_a);
            var1 = this.func_195837_d() || var1;
            AL10.alListenerfv(4111, this.field_195840_b);
            var1 = this.func_195837_d() || var1;
            AL10.alListenerfv(4102, this.field_195841_c);
            var1 = this.func_195837_d() || var1;
            AL10.alDopplerFactor(SoundSystemConfig.getDopplerFactor());
            var1 = this.func_195837_d() || var1;
            AL10.alDopplerVelocity(SoundSystemConfig.getDopplerVelocity());
            var1 = this.func_195837_d() || var1;
            if (var1) {
               this.importantMessage("OpenAL did not initialize properly!");
               throw new LibraryLWJGL3.LWJGL3SoundSystemException("Problem encountered while loading OpenAL or creating the listener. Probable cause: OpenAL not supported", 101);
            } else {
               super.init();
               ChannelLWJGL3 var5 = (ChannelLWJGL3)this.normalChannels.get(1);

               try {
                  AL10.alSourcef(var5.field_195851_a.get(0), 4099, 1.0F);
                  if (this.func_195837_d()) {
                     func_195838_a(true, false);
                     throw new LibraryLWJGL3.LWJGL3SoundSystemException("OpenAL: AL_PITCH not supported.", 108);
                  }

                  func_195838_a(true, true);
               } catch (Exception var7) {
                  func_195838_a(true, false);
                  throw new LibraryLWJGL3.LWJGL3SoundSystemException("OpenAL: AL_PITCH not supported.", 108);
               }

               this.field_195844_f = "Running";
            }
         }
      }
   }

   protected Channel createChannel(int var1) {
      IntBuffer var2 = BufferUtils.createIntBuffer(1);

      try {
         AL10.alGenSources(var2);
      } catch (Exception var4) {
         AL10.alGetError();
         return null;
      }

      return AL10.alGetError() != 0 ? null : new ChannelLWJGL3(var1, var2);
   }

   public void cleanup() {
      super.cleanup();
      Iterator var1 = this.bufferMap.keySet().iterator();

      while(var1.hasNext()) {
         String var2 = (String)var1.next();
         IntBuffer var3 = (IntBuffer)this.field_195842_d.get(var2);
         if (var3 != null) {
            AL10.alDeleteBuffers(var3);
            this.func_195837_d();
            var3.clear();
         }
      }

      this.bufferMap.clear();
      ALC10.alcDestroyContext(this.field_195846_h);
      if (this.field_195845_g != 0L) {
         ALC10.alcCloseDevice(this.field_195845_g);
      }

      this.bufferMap = null;
      this.field_195839_a = null;
      this.field_195840_b = null;
      this.field_195841_c = null;
   }

   public boolean loadSound(FilenameURL var1) {
      if (this.bufferMap == null) {
         this.bufferMap = Maps.newHashMap();
         this.importantMessage("Buffer Map was null in method 'loadSound'");
      }

      if (this.field_195842_d == null) {
         this.field_195842_d = Maps.newHashMap();
         this.importantMessage("Open AL Buffer Map was null in method 'loadSound'");
      }

      if (this.errorCheck(var1 == null, "Filename/URL not specified in method 'loadSound'")) {
         return false;
      } else if (this.bufferMap.get(var1.getFilename()) != null) {
         return true;
      } else {
         ICodec var2 = SoundSystemConfig.getCodec(var1.getFilename());
         if (this.errorCheck(var2 == null, "No codec found for file '" + var1.getFilename() + "' in method 'loadSound'")) {
            return false;
         } else {
            var2.reverseByteOrder(true);
            URL var3 = var1.getURL();
            if (this.errorCheck(var3 == null, "Unable to open file '" + var1.getFilename() + "' in method 'loadSound'")) {
               return false;
            } else {
               var2.initialize(var3);
               SoundBuffer var4 = var2.readAll();
               var2.cleanup();
               var2 = null;
               if (this.errorCheck(var4 == null, "Sound buffer null in method 'loadSound'")) {
                  return false;
               } else {
                  this.bufferMap.put(var1.getFilename(), var4);
                  AudioFormat var5 = var4.audioFormat;
                  short var6;
                  if (var5.getChannels() == 1) {
                     if (var5.getSampleSizeInBits() == 8) {
                        var6 = 4352;
                     } else {
                        if (var5.getSampleSizeInBits() != 16) {
                           this.errorMessage("Illegal sample size in method 'loadSound'");
                           return false;
                        }

                        var6 = 4353;
                     }
                  } else {
                     if (var5.getChannels() != 2) {
                        this.errorMessage("File neither mono nor stereo in method 'loadSound'");
                        return false;
                     }

                     if (var5.getSampleSizeInBits() == 8) {
                        var6 = 4354;
                     } else {
                        if (var5.getSampleSizeInBits() != 16) {
                           this.errorMessage("Illegal sample size in method 'loadSound'");
                           return false;
                        }

                        var6 = 4355;
                     }
                  }

                  IntBuffer var7 = BufferUtils.createIntBuffer(1);
                  AL10.alGenBuffers(var7);
                  if (this.errorCheck(AL10.alGetError() != 0, "alGenBuffers error when loading " + var1.getFilename())) {
                     return false;
                  } else {
                     AL10.alBufferData(var7.get(0), var6, (ByteBuffer)BufferUtils.createByteBuffer(var4.audioData.length).put(var4.audioData).flip(), (int)var5.getSampleRate());
                     if (this.errorCheck(AL10.alGetError() != 0, "alBufferData error when loading " + var1.getFilename()) && this.errorCheck(var7 == null, "Sound buffer was not created for " + var1.getFilename())) {
                        return false;
                     } else {
                        this.field_195842_d.put(var1.getFilename(), var7);
                        return true;
                     }
                  }
               }
            }
         }
      }
   }

   public boolean loadSound(SoundBuffer var1, String var2) {
      if (this.bufferMap == null) {
         this.bufferMap = Maps.newHashMap();
         this.importantMessage("Buffer Map was null in method 'loadSound'");
      }

      if (this.field_195842_d == null) {
         this.field_195842_d = Maps.newHashMap();
         this.importantMessage("Open AL Buffer Map was null in method 'loadSound'");
      }

      if (this.errorCheck(var2 == null, "Identifier not specified in method 'loadSound'")) {
         return false;
      } else if (this.bufferMap.get(var2) != null) {
         return true;
      } else if (this.errorCheck(var1 == null, "Sound buffer null in method 'loadSound'")) {
         return false;
      } else {
         this.bufferMap.put(var2, var1);
         AudioFormat var3 = var1.audioFormat;
         short var4;
         if (var3.getChannels() == 1) {
            if (var3.getSampleSizeInBits() == 8) {
               var4 = 4352;
            } else {
               if (var3.getSampleSizeInBits() != 16) {
                  this.errorMessage("Illegal sample size in method 'loadSound'");
                  return false;
               }

               var4 = 4353;
            }
         } else {
            if (var3.getChannels() != 2) {
               this.errorMessage("File neither mono nor stereo in method 'loadSound'");
               return false;
            }

            if (var3.getSampleSizeInBits() == 8) {
               var4 = 4354;
            } else {
               if (var3.getSampleSizeInBits() != 16) {
                  this.errorMessage("Illegal sample size in method 'loadSound'");
                  return false;
               }

               var4 = 4355;
            }
         }

         IntBuffer var5 = BufferUtils.createIntBuffer(1);
         AL10.alGenBuffers(var5);
         if (this.errorCheck(AL10.alGetError() != 0, "alGenBuffers error when saving " + var2)) {
            return false;
         } else {
            AL10.alBufferData(var5.get(0), var4, (ByteBuffer)BufferUtils.createByteBuffer(var1.audioData.length).put(var1.audioData).flip(), (int)var3.getSampleRate());
            if (this.errorCheck(AL10.alGetError() != 0, "alBufferData error when saving " + var2) && this.errorCheck(var5 == null, "Sound buffer was not created for " + var2)) {
               return false;
            } else {
               this.field_195842_d.put(var2, var5);
               return true;
            }
         }
      }
   }

   public void unloadSound(String var1) {
      this.field_195842_d.remove(var1);
      super.unloadSound(var1);
   }

   public void setMasterVolume(float var1) {
      super.setMasterVolume(var1);
      AL10.alListenerf(4106, var1);
      this.func_195837_d();
   }

   public void newSource(boolean var1, boolean var2, boolean var3, String var4, FilenameURL var5, float var6, float var7, float var8, int var9, float var10) {
      IntBuffer var11 = null;
      if (!var2) {
         var11 = (IntBuffer)this.field_195842_d.get(var5.getFilename());
         if (var11 == null && !this.loadSound(var5)) {
            this.errorMessage(String.format("Source '%s' was not created because an error occurred while loading %s", var4, var5.getFilename()));
            return;
         }

         var11 = (IntBuffer)this.field_195842_d.get(var5.getFilename());
         if (var11 == null) {
            this.errorMessage(String.format("Source '%s' was not created because a sound buffer was not found for %s", var4, var5.getFilename()));
            return;
         }
      }

      SoundBuffer var12 = null;
      if (!var2) {
         var12 = (SoundBuffer)this.bufferMap.get(var5.getFilename());
         if (var12 == null && !this.loadSound(var5)) {
            this.errorMessage(String.format("Source '%s' was not created because an error occurred while loading %s", var4, var5.getFilename()));
            return;
         }

         var12 = (SoundBuffer)this.bufferMap.get(var5.getFilename());
         if (var12 == null) {
            this.errorMessage(String.format("Source '%s' was not created because audio data was not found for %s", var4, var5.getFilename()));
            return;
         }
      }

      this.sourceMap.put(var4, new SourceLWJGL3(this.field_195839_a, var11, var1, var2, var3, var4, var5, var12, var6, var7, var8, var9, var10, false));
   }

   public void rawDataStream(AudioFormat var1, boolean var2, String var3, float var4, float var5, float var6, int var7, float var8) {
      this.sourceMap.put(var3, new SourceLWJGL3(this.field_195839_a, var1, var2, var3, var4, var5, var6, var7, var8));
   }

   public void quickPlay(boolean var1, boolean var2, boolean var3, String var4, FilenameURL var5, float var6, float var7, float var8, int var9, float var10, boolean var11) {
      IntBuffer var12 = null;
      if (!var2) {
         var12 = (IntBuffer)this.field_195842_d.get(var5.getFilename());
         if (var12 == null) {
            this.loadSound(var5);
         }

         var12 = (IntBuffer)this.field_195842_d.get(var5.getFilename());
         if (var12 == null) {
            this.errorMessage("Sound buffer was not created for " + var5.getFilename());
            return;
         }
      }

      SoundBuffer var13 = null;
      if (!var2) {
         var13 = (SoundBuffer)this.bufferMap.get(var5.getFilename());
         if (var13 == null && !this.loadSound(var5)) {
            this.errorMessage(String.format("Source '%s' was not created because an error occurred while loading %s", var4, var5.getFilename()));
            return;
         }

         var13 = (SoundBuffer)this.bufferMap.get(var5.getFilename());
         if (var13 == null) {
            this.errorMessage(String.format("Source '%s' was not created because audio data was not found for %s", var4, var5.getFilename()));
            return;
         }
      }

      SourceLWJGL3 var14 = new SourceLWJGL3(this.field_195839_a, var12, var1, var2, var3, var4, var5, var13, var6, var7, var8, var9, var10, false);
      this.sourceMap.put(var4, var14);
      this.play(var14);
      if (var11) {
         var14.setTemporary(true);
      }

   }

   public void copySources(HashMap<String, Source> var1) {
      if (var1 != null) {
         Set var2 = var1.keySet();
         Iterator var3 = var2.iterator();
         if (this.bufferMap == null) {
            this.bufferMap = Maps.newHashMap();
            this.importantMessage("Buffer Map was null in method 'copySources'");
         }

         if (this.field_195842_d == null) {
            this.field_195842_d = Maps.newHashMap();
            this.importantMessage("Open AL Buffer Map was null in method 'copySources'");
         }

         this.sourceMap.clear();

         while(true) {
            String var4;
            Source var5;
            SoundBuffer var6;
            do {
               do {
                  if (!var3.hasNext()) {
                     return;
                  }

                  var4 = (String)var3.next();
                  var5 = (Source)var1.get(var4);
               } while(var5 == null);

               var6 = null;
               if (!var5.toStream) {
                  this.loadSound(var5.filenameURL);
                  var6 = (SoundBuffer)this.bufferMap.get(var5.filenameURL.getFilename());
               }
            } while(!var5.toStream && var6 == null);

            this.sourceMap.put(var4, new SourceLWJGL3(this.field_195839_a, (IntBuffer)this.field_195842_d.get(var5.filenameURL.getFilename()), var5, var6));
         }
      }
   }

   public void setListenerPosition(float var1, float var2, float var3) {
      super.setListenerPosition(var1, var2, var3);
      this.field_195839_a.put(0, var1);
      this.field_195839_a.put(1, var2);
      this.field_195839_a.put(2, var3);
      AL10.alListenerfv(4100, this.field_195839_a);
      this.func_195837_d();
   }

   public void setListenerAngle(float var1) {
      super.setListenerAngle(var1);
      this.field_195840_b.put(0, this.listener.lookAt.x);
      this.field_195840_b.put(2, this.listener.lookAt.z);
      AL10.alListenerfv(4111, this.field_195840_b);
      this.func_195837_d();
   }

   public void setListenerOrientation(float var1, float var2, float var3, float var4, float var5, float var6) {
      super.setListenerOrientation(var1, var2, var3, var4, var5, var6);
      this.field_195840_b.put(0, var1);
      this.field_195840_b.put(1, var2);
      this.field_195840_b.put(2, var3);
      this.field_195840_b.put(3, var4);
      this.field_195840_b.put(4, var5);
      this.field_195840_b.put(5, var6);
      AL10.alListenerfv(4111, this.field_195840_b);
      this.func_195837_d();
   }

   public void setListenerData(ListenerData var1) {
      super.setListenerData(var1);
      this.field_195839_a.put(0, var1.position.x);
      this.field_195839_a.put(1, var1.position.y);
      this.field_195839_a.put(2, var1.position.z);
      AL10.alListenerfv(4100, this.field_195839_a);
      this.func_195837_d();
      this.field_195840_b.put(0, var1.lookAt.x);
      this.field_195840_b.put(1, var1.lookAt.y);
      this.field_195840_b.put(2, var1.lookAt.z);
      this.field_195840_b.put(3, var1.up.x);
      this.field_195840_b.put(4, var1.up.y);
      this.field_195840_b.put(5, var1.up.z);
      AL10.alListenerfv(4111, this.field_195840_b);
      this.func_195837_d();
      this.field_195841_c.put(0, var1.velocity.x);
      this.field_195841_c.put(1, var1.velocity.y);
      this.field_195841_c.put(2, var1.velocity.z);
      AL10.alListenerfv(4102, this.field_195841_c);
      this.func_195837_d();
   }

   public void setListenerVelocity(float var1, float var2, float var3) {
      super.setListenerVelocity(var1, var2, var3);
      this.field_195841_c.put(0, this.listener.velocity.x);
      this.field_195841_c.put(1, this.listener.velocity.y);
      this.field_195841_c.put(2, this.listener.velocity.z);
      AL10.alListenerfv(4102, this.field_195841_c);
   }

   public void dopplerChanged() {
      super.dopplerChanged();
      AL10.alDopplerFactor(SoundSystemConfig.getDopplerFactor());
      this.func_195837_d();
      AL10.alDopplerVelocity(SoundSystemConfig.getDopplerVelocity());
      this.func_195837_d();
   }

   private boolean func_195837_d() {
      switch(AL10.alGetError()) {
      case 0:
         return false;
      case 40961:
         this.errorMessage("Invalid name parameter: " + this.field_195844_f);
         return true;
      case 40962:
         this.errorMessage("Invalid parameter: " + this.field_195844_f);
         return true;
      case 40963:
         this.errorMessage("Invalid enumerated parameter value: " + this.field_195844_f);
         return true;
      case 40964:
         this.errorMessage("Illegal call: " + this.field_195844_f);
         return true;
      case 40965:
         this.errorMessage("Unable to allocate memory: " + this.field_195844_f);
         return true;
      default:
         this.errorMessage("An unrecognized error occurred: " + this.field_195844_f);
         return true;
      }
   }

   public static boolean func_195836_a() {
      return func_195838_a(false, false);
   }

   private static synchronized boolean func_195838_a(boolean var0, boolean var1) {
      if (var0) {
         field_195843_e = var1;
      }

      return field_195843_e;
   }

   public String getClassName() {
      return "LibraryLWJGL3";
   }

   public static class LWJGL3SoundSystemException extends SoundSystemException {
      public LWJGL3SoundSystemException(String var1) {
         super(var1);
      }

      public LWJGL3SoundSystemException(String var1, int var2) {
         super(var1, var2);
      }
   }
}
