package com.mojang.blaze3d.audio;

import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.nio.IntBuffer;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.OptionalLong;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.util.Mth;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALC11;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.openal.ALUtil;
import org.lwjgl.openal.SOFTHRTF;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;

public class Library {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final int NO_DEVICE = 0;
   private static final int DEFAULT_CHANNEL_COUNT = 30;
   private long currentDevice;
   private long context;
   private boolean supportsDisconnections;
   @Nullable
   private String defaultDeviceName;
   private static final ChannelPool EMPTY = new ChannelPool() {
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
   private ChannelPool staticChannels;
   private ChannelPool streamingChannels;
   private final Listener listener;

   public Library() {
      super();
      this.staticChannels = EMPTY;
      this.streamingChannels = EMPTY;
      this.listener = new Listener();
      this.defaultDeviceName = getDefaultDeviceName();
   }

   public void init(@Nullable String var1, boolean var2) {
      this.currentDevice = openDeviceOrFallback(var1);
      this.supportsDisconnections = ALC10.alcIsExtensionPresent(this.currentDevice, "ALC_EXT_disconnect");
      ALCCapabilities var3 = ALC.createCapabilities(this.currentDevice);
      if (OpenAlUtil.checkALCError(this.currentDevice, "Get capabilities")) {
         throw new IllegalStateException("Failed to get OpenAL capabilities");
      } else if (!var3.OpenALC11) {
         throw new IllegalStateException("OpenAL 1.1 not supported");
      } else {
         if (var3.ALC_SOFT_HRTF && var2) {
            this.enableHrtf();
         }

         this.context = ALC10.alcCreateContext(this.currentDevice, (IntBuffer)null);
         ALC10.alcMakeContextCurrent(this.context);
         int var4 = this.getChannelCount();
         int var5 = Mth.clamp((int)((int)Mth.sqrt((float)var4)), (int)2, (int)8);
         int var6 = Mth.clamp((int)(var4 - var5), (int)8, (int)255);
         this.staticChannels = new CountingChannelPool(var6);
         this.streamingChannels = new CountingChannelPool(var5);
         ALCapabilities var7 = AL.createCapabilities(var3);
         OpenAlUtil.checkALError("Initialization");
         if (!var7.AL_EXT_source_distance_model) {
            throw new IllegalStateException("AL_EXT_source_distance_model is not supported");
         } else {
            AL10.alEnable(512);
            if (!var7.AL_EXT_LINEAR_DISTANCE) {
               throw new IllegalStateException("AL_EXT_LINEAR_DISTANCE is not supported");
            } else {
               OpenAlUtil.checkALError("Enable per-source distance models");
               LOGGER.info("OpenAL initialized on device {}", this.getCurrentDeviceName());
            }
         }
      }
   }

   private void enableHrtf() {
      int var1 = ALC10.alcGetInteger(this.currentDevice, 6548);
      if (var1 > 0) {
         MemoryStack var2 = MemoryStack.stackPush();

         try {
            IntBuffer var3 = var2.callocInt(10).put(6546).put(1).put(6550).put(0).put(0).flip();
            if (!SOFTHRTF.alcResetDeviceSOFT(this.currentDevice, var3)) {
               LOGGER.warn("Failed to reset device: {}", ALC10.alcGetString(this.currentDevice, ALC10.alcGetError(this.currentDevice)));
            }
         } catch (Throwable var6) {
            if (var2 != null) {
               try {
                  var2.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (var2 != null) {
            var2.close();
         }
      }

   }

   private int getChannelCount() {
      MemoryStack var1 = MemoryStack.stackPush();

      int var7;
      label58: {
         try {
            int var2 = ALC10.alcGetInteger(this.currentDevice, 4098);
            if (OpenAlUtil.checkALCError(this.currentDevice, "Get attributes size")) {
               throw new IllegalStateException("Failed to get OpenAL attributes");
            }

            IntBuffer var3 = var1.mallocInt(var2);
            ALC10.alcGetIntegerv(this.currentDevice, 4099, var3);
            if (OpenAlUtil.checkALCError(this.currentDevice, "Get attributes")) {
               throw new IllegalStateException("Failed to get OpenAL attributes");
            }

            int var4 = 0;

            while(var4 < var2) {
               int var5 = var3.get(var4++);
               if (var5 == 0) {
                  break;
               }

               int var6 = var3.get(var4++);
               if (var5 == 4112) {
                  var7 = var6;
                  break label58;
               }
            }
         } catch (Throwable var9) {
            if (var1 != null) {
               try {
                  var1.close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }
            }

            throw var9;
         }

         if (var1 != null) {
            var1.close();
         }

         return 30;
      }

      if (var1 != null) {
         var1.close();
      }

      return var7;
   }

   @Nullable
   public static String getDefaultDeviceName() {
      if (!ALC10.alcIsExtensionPresent(0L, "ALC_ENUMERATE_ALL_EXT")) {
         return null;
      } else {
         ALUtil.getStringList(0L, 4115);
         return ALC10.alcGetString(0L, 4114);
      }
   }

   public String getCurrentDeviceName() {
      String var1 = ALC10.alcGetString(this.currentDevice, 4115);
      if (var1 == null) {
         var1 = ALC10.alcGetString(this.currentDevice, 4101);
      }

      if (var1 == null) {
         var1 = "Unknown";
      }

      return var1;
   }

   public synchronized boolean hasDefaultDeviceChanged() {
      String var1 = getDefaultDeviceName();
      if (Objects.equals(this.defaultDeviceName, var1)) {
         return false;
      } else {
         this.defaultDeviceName = var1;
         return true;
      }
   }

   private static long openDeviceOrFallback(@Nullable String var0) {
      OptionalLong var1 = OptionalLong.empty();
      if (var0 != null) {
         var1 = tryOpenDevice(var0);
      }

      if (var1.isEmpty()) {
         var1 = tryOpenDevice(getDefaultDeviceName());
      }

      if (var1.isEmpty()) {
         var1 = tryOpenDevice((String)null);
      }

      if (var1.isEmpty()) {
         throw new IllegalStateException("Failed to open OpenAL device");
      } else {
         return var1.getAsLong();
      }
   }

   private static OptionalLong tryOpenDevice(@Nullable String var0) {
      long var1 = ALC10.alcOpenDevice(var0);
      return var1 != 0L && !OpenAlUtil.checkALCError(var1, "Open device") ? OptionalLong.of(var1) : OptionalLong.empty();
   }

   public void cleanup() {
      this.staticChannels.cleanup();
      this.streamingChannels.cleanup();
      ALC10.alcDestroyContext(this.context);
      if (this.currentDevice != 0L) {
         ALC10.alcCloseDevice(this.currentDevice);
      }

   }

   public Listener getListener() {
      return this.listener;
   }

   @Nullable
   public Channel acquireChannel(Pool var1) {
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

   public List<String> getAvailableSoundDevices() {
      List var1 = ALUtil.getStringList(0L, 4115);
      return var1 == null ? Collections.emptyList() : var1;
   }

   public boolean isCurrentDeviceDisconnected() {
      return this.supportsDisconnections && ALC11.alcGetInteger(this.currentDevice, 787) == 0;
   }

   private interface ChannelPool {
      @Nullable
      Channel acquire();

      boolean release(Channel var1);

      void cleanup();

      int getMaxCount();

      int getUsedCount();
   }

   private static class CountingChannelPool implements ChannelPool {
      private final int limit;
      private final Set<Channel> activeChannels = Sets.newIdentityHashSet();

      public CountingChannelPool(int var1) {
         super();
         this.limit = var1;
      }

      @Nullable
      public Channel acquire() {
         if (this.activeChannels.size() >= this.limit) {
            if (SharedConstants.IS_RUNNING_IN_IDE) {
               Library.LOGGER.warn("Maximum sound pool size {} reached", this.limit);
            }

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

   public static enum Pool {
      STATIC,
      STREAMING;

      private Pool() {
      }

      // $FF: synthetic method
      private static Pool[] $values() {
         return new Pool[]{STATIC, STREAMING};
      }
   }
}
