package com.mojang.blaze3d.audio;

import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.nio.IntBuffer;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.OptionalLong;
import java.util.Set;
import net.minecraft.SharedConstants;
import net.minecraft.util.Mth;
import org.jspecify.annotations.Nullable;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALC11;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.openal.ALUtil;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;

public class Library {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int NO_DEVICE = 0;
   private static final int DEFAULT_CHANNEL_COUNT = 30;
   private long currentDevice;
   private long context;
   private boolean supportsDisconnections;
   private @Nullable String defaultDeviceName;
   private static final ChannelPool EMPTY = new ChannelPool() {
      public @Nullable Channel acquire() {
         return null;
      }

      public boolean release(final Channel channel) {
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

   public void init(final @Nullable String preferredDevice, final boolean useHrtf) {
      this.currentDevice = openDeviceOrFallback(preferredDevice);
      this.supportsDisconnections = false;
      ALCCapabilities alcCapabilities = ALC.createCapabilities(this.currentDevice);
      if (OpenAlUtil.checkALCError(this.currentDevice, "Get capabilities")) {
         throw new IllegalStateException("Failed to get OpenAL capabilities");
      } else if (!alcCapabilities.OpenALC11) {
         throw new IllegalStateException("OpenAL 1.1 not supported");
      } else {
         MemoryStack stack = MemoryStack.stackPush();

         try {
            IntBuffer attr = this.createAttributes(stack, alcCapabilities.ALC_SOFT_HRTF && useHrtf);
            this.context = ALC10.alcCreateContext(this.currentDevice, attr);
         } catch (Throwable var9) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }
            }

            throw var9;
         }

         if (stack != null) {
            stack.close();
         }

         if (OpenAlUtil.checkALCError(this.currentDevice, "Create context")) {
            throw new IllegalStateException("Unable to create OpenAL context");
         } else {
            ALC10.alcMakeContextCurrent(this.context);
            int totalChannelCount = this.getChannelCount();
            int streamingChannelCount = Mth.clamp((int)Mth.sqrt((float)totalChannelCount), 2, 8);
            int staticChannelCount = Mth.clamp(totalChannelCount - streamingChannelCount, 8, 255);
            this.staticChannels = new CountingChannelPool(staticChannelCount);
            this.streamingChannels = new CountingChannelPool(streamingChannelCount);
            ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);
            OpenAlUtil.checkALError("Initialization");
            if (!alCapabilities.AL_EXT_source_distance_model) {
               throw new IllegalStateException("AL_EXT_source_distance_model is not supported");
            } else {
               AL10.alEnable(512);
               if (!alCapabilities.AL_EXT_LINEAR_DISTANCE) {
                  throw new IllegalStateException("AL_EXT_LINEAR_DISTANCE is not supported");
               } else {
                  OpenAlUtil.checkALError("Enable per-source distance models");
                  LOGGER.info("OpenAL initialized on device {}", this.getCurrentDeviceName());
                  this.supportsDisconnections = ALC10.alcIsExtensionPresent(this.currentDevice, "ALC_EXT_disconnect");
               }
            }
         }
      }
   }

   private IntBuffer createAttributes(final MemoryStack stack, final boolean enableHrtf) {
      int maxAttributes = 5;
      IntBuffer attr = stack.callocInt(11);
      int numHrtf = ALC10.alcGetInteger(this.currentDevice, 6548);
      if (numHrtf > 0) {
         attr.put(6546).put(enableHrtf ? 1 : 0);
         attr.put(6550).put(0);
      }

      attr.put(6554).put(1);
      return attr.put(0).flip();
   }

   private int getChannelCount() {
      MemoryStack stack = MemoryStack.stackPush();

      int var7;
      label58: {
         try {
            int size = ALC10.alcGetInteger(this.currentDevice, 4098);
            if (OpenAlUtil.checkALCError(this.currentDevice, "Get attributes size")) {
               throw new IllegalStateException("Failed to get OpenAL attributes");
            }

            IntBuffer attributes = stack.mallocInt(size);
            ALC10.alcGetIntegerv(this.currentDevice, 4099, attributes);
            if (OpenAlUtil.checkALCError(this.currentDevice, "Get attributes")) {
               throw new IllegalStateException("Failed to get OpenAL attributes");
            }

            int pos = 0;

            while(pos < size) {
               int attribute = attributes.get(pos++);
               if (attribute == 0) {
                  break;
               }

               int attributeValue = attributes.get(pos++);
               if (attribute == 4112) {
                  var7 = attributeValue;
                  break label58;
               }
            }
         } catch (Throwable var9) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }
            }

            throw var9;
         }

         if (stack != null) {
            stack.close();
         }

         return 30;
      }

      if (stack != null) {
         stack.close();
      }

      return var7;
   }

   public static @Nullable String getDefaultDeviceName() {
      if (!ALC10.alcIsExtensionPresent(0L, "ALC_ENUMERATE_ALL_EXT")) {
         return null;
      } else {
         ALUtil.getStringList(0L, 4115);
         return ALC10.alcGetString(0L, 4114);
      }
   }

   public String getCurrentDeviceName() {
      String name = ALC10.alcGetString(this.currentDevice, 4115);
      if (name == null) {
         name = ALC10.alcGetString(this.currentDevice, 4101);
      }

      if (name == null) {
         name = "Unknown";
      }

      return name;
   }

   public synchronized boolean hasDefaultDeviceChanged() {
      String name = getDefaultDeviceName();
      if (Objects.equals(this.defaultDeviceName, name)) {
         return false;
      } else {
         this.defaultDeviceName = name;
         return true;
      }
   }

   private static long openDeviceOrFallback(final @Nullable String preferredDevice) {
      OptionalLong device = OptionalLong.empty();
      if (preferredDevice != null) {
         device = tryOpenDevice(preferredDevice);
      }

      if (device.isEmpty()) {
         device = tryOpenDevice(getDefaultDeviceName());
      }

      if (device.isEmpty()) {
         device = tryOpenDevice((String)null);
      }

      if (device.isEmpty()) {
         throw new IllegalStateException("Failed to open OpenAL device");
      } else {
         return device.getAsLong();
      }
   }

   private static OptionalLong tryOpenDevice(final @Nullable String name) {
      long device = ALC10.alcOpenDevice(name);
      return device != 0L && !OpenAlUtil.checkALCError(device, "Open device") ? OptionalLong.of(device) : OptionalLong.empty();
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

   public @Nullable Channel acquireChannel(final Pool pool) {
      return (pool == Library.Pool.STREAMING ? this.streamingChannels : this.staticChannels).acquire();
   }

   public void releaseChannel(final Channel channel) {
      if (!this.staticChannels.release(channel) && !this.streamingChannels.release(channel)) {
         throw new IllegalStateException("Tried to release unknown channel");
      }
   }

   public String getDebugString() {
      return String.format(Locale.ROOT, "Sounds: %d/%d + %d/%d", this.staticChannels.getUsedCount(), this.staticChannels.getMaxCount(), this.streamingChannels.getUsedCount(), this.streamingChannels.getMaxCount());
   }

   public List<String> getAvailableSoundDevices() {
      List<String> result = ALUtil.getStringList(0L, 4115);
      return result == null ? Collections.emptyList() : result;
   }

   public boolean isCurrentDeviceDisconnected() {
      return this.supportsDisconnections && ALC11.alcGetInteger(this.currentDevice, 787) == 0;
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

   private static class CountingChannelPool implements ChannelPool {
      private final int limit;
      private final Set<Channel> activeChannels = Sets.newIdentityHashSet();

      public CountingChannelPool(final int limit) {
         super();
         this.limit = limit;
      }

      public @Nullable Channel acquire() {
         if (this.activeChannels.size() >= this.limit) {
            if (SharedConstants.IS_RUNNING_IN_IDE) {
               Library.LOGGER.warn("Maximum sound pool size {} reached", this.limit);
            }

            return null;
         } else {
            Channel channel = Channel.create();
            if (channel != null) {
               this.activeChannels.add(channel);
            }

            return channel;
         }
      }

      public boolean release(final Channel channel) {
         if (!this.activeChannels.remove(channel)) {
            return false;
         } else {
            channel.destroy();
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

   private interface ChannelPool {
      @Nullable Channel acquire();

      boolean release(final Channel channel);

      void cleanup();

      int getMaxCount();

      int getUsedCount();
   }
}
