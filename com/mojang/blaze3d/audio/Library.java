package com.mojang.blaze3d.audio;

import com.google.common.collect.Sets;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.Mth;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.system.MemoryStack;

public class Library {
   private static final Logger LOGGER = LogManager.getLogger();
   private long device;
   private long context;
   private static final Library.ChannelPool EMPTY = new Library.ChannelPool() {
      @Nullable
      public Channel acquire() {
         return null;
      }

      public boolean release(Channel var1) {
         return false;
      }

      public void cleanup() {
      }

      public int getMaxCount() {
         return 0;
      }

      public int getUsedCount() {
         return 0;
      }
   };
   private Library.ChannelPool staticChannels;
   private Library.ChannelPool streamingChannels;
   private final Listener listener;

   public Library() {
      this.staticChannels = EMPTY;
      this.streamingChannels = EMPTY;
      this.listener = new Listener();
   }

   public void init() {
      this.device = tryOpenDevice();
      ALCCapabilities var1 = ALC.createCapabilities(this.device);
      if (OpenAlUtil.checkALCError(this.device, "Get capabilities")) {
         throw new IllegalStateException("Failed to get OpenAL capabilities");
      } else if (!var1.OpenALC11) {
         throw new IllegalStateException("OpenAL 1.1 not supported");
      } else {
         this.context = ALC10.alcCreateContext(this.device, (IntBuffer)null);
         ALC10.alcMakeContextCurrent(this.context);
         int var2 = this.getChannelCount();
         int var3 = Mth.clamp((int)Mth.sqrt((float)var2), 2, 8);
         int var4 = Mth.clamp(var2 - var3, 8, 255);
         this.staticChannels = new Library.CountingChannelPool(var4);
         this.streamingChannels = new Library.CountingChannelPool(var3);
         ALCapabilities var5 = AL.createCapabilities(var1);
         OpenAlUtil.checkALError("Initialization");
         if (!var5.AL_EXT_source_distance_model) {
            throw new IllegalStateException("AL_EXT_source_distance_model is not supported");
         } else {
            AL10.alEnable(512);
            if (!var5.AL_EXT_LINEAR_DISTANCE) {
               throw new IllegalStateException("AL_EXT_LINEAR_DISTANCE is not supported");
            } else {
               OpenAlUtil.checkALError("Enable per-source distance models");
               LOGGER.info("OpenAL initialized.");
            }
         }
      }
   }

   private int getChannelCount() {
      MemoryStack var1 = MemoryStack.stackPush();
      Throwable var2 = null;

      try {
         int var3 = ALC10.alcGetInteger(this.device, 4098);
         if (OpenAlUtil.checkALCError(this.device, "Get attributes size")) {
            throw new IllegalStateException("Failed to get OpenAL attributes");
         }

         IntBuffer var4 = var1.mallocInt(var3);
         ALC10.alcGetIntegerv(this.device, 4099, var4);
         if (OpenAlUtil.checkALCError(this.device, "Get attributes")) {
            throw new IllegalStateException("Failed to get OpenAL attributes");
         }

         int var5 = 0;

         while(var5 < var3) {
            int var6 = var4.get(var5++);
            if (var6 == 0) {
               break;
            }

            int var7 = var4.get(var5++);
            if (var6 == 4112) {
               int var8 = var7;
               return var8;
            }
         }
      } catch (Throwable var18) {
         var2 = var18;
         throw var18;
      } finally {
         if (var1 != null) {
            if (var2 != null) {
               try {
                  var1.close();
               } catch (Throwable var17) {
                  var2.addSuppressed(var17);
               }
            } else {
               var1.close();
            }
         }

      }

      return 30;
   }

   private static long tryOpenDevice() {
      for(int var0 = 0; var0 < 3; ++var0) {
         long var1 = ALC10.alcOpenDevice((ByteBuffer)null);
         if (var1 != 0L && !OpenAlUtil.checkALCError(var1, "Open device")) {
            return var1;
         }
      }

      throw new IllegalStateException("Failed to open OpenAL device");
   }

   public void cleanup() {
      this.staticChannels.cleanup();
      this.streamingChannels.cleanup();
      ALC10.alcDestroyContext(this.context);
      if (this.device != 0L) {
         ALC10.alcCloseDevice(this.device);
      }

   }

   public Listener getListener() {
      return this.listener;
   }

   @Nullable
   public Channel acquireChannel(Library.Pool var1) {
      return (var1 == Library.Pool.STREAMING ? this.streamingChannels : this.staticChannels).acquire();
   }

   public void releaseChannel(Channel var1) {
      if (!this.staticChannels.release(var1) && !this.streamingChannels.release(var1)) {
         throw new IllegalStateException("Tried to release unknown channel");
      }
   }

   public String getDebugString() {
      return String.format("Sounds: %d/%d + %d/%d", this.staticChannels.getUsedCount(), this.staticChannels.getMaxCount(), this.streamingChannels.getUsedCount(), this.streamingChannels.getMaxCount());
   }

   static class CountingChannelPool implements Library.ChannelPool {
      private final int limit;
      private final Set activeChannels = Sets.newIdentityHashSet();

      public CountingChannelPool(int var1) {
         this.limit = var1;
      }

      @Nullable
      public Channel acquire() {
         if (this.activeChannels.size() >= this.limit) {
            return null;
         } else {
            Channel var1 = Channel.create();
            if (var1 != null) {
               this.activeChannels.add(var1);
            }

            return var1;
         }
      }

      public boolean release(Channel var1) {
         if (!this.activeChannels.remove(var1)) {
            return false;
         } else {
            var1.destroy();
            return true;
         }
      }

      public void cleanup() {
         this.activeChannels.forEach(Channel::destroy);
         this.activeChannels.clear();
      }

      public int getMaxCount() {
         return this.limit;
      }

      public int getUsedCount() {
         return this.activeChannels.size();
      }
   }

   interface ChannelPool {
      @Nullable
      Channel acquire();

      boolean release(Channel var1);

      void cleanup();

      int getMaxCount();

      int getUsedCount();
   }

   public static enum Pool {
      STATIC,
      STREAMING;
   }
}
