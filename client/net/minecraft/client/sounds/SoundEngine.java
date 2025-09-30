package net.minecraft.client.sounds;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.audio.Channel;
import com.mojang.blaze3d.audio.Library;
import com.mojang.blaze3d.audio.Listener;
import com.mojang.blaze3d.audio.ListenerTransform;
import com.mojang.logging.LogUtils;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Options;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class SoundEngine {
   private static final Marker MARKER = MarkerFactory.getMarker("SOUNDS");
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final float PITCH_MIN = 0.5F;
   private static final float PITCH_MAX = 2.0F;
   private static final float VOLUME_MIN = 0.0F;
   private static final float VOLUME_MAX = 1.0F;
   private static final int MIN_SOURCE_LIFETIME = 20;
   private static final Set<ResourceLocation> ONLY_WARN_ONCE = Sets.newHashSet();
   private static final long DEFAULT_DEVICE_CHECK_INTERVAL_MS = 1000L;
   public static final String MISSING_SOUND = "FOR THE DEBUG!";
   public static final String OPEN_AL_SOFT_PREFIX = "OpenAL Soft on ";
   public static final int OPEN_AL_SOFT_PREFIX_LENGTH = "OpenAL Soft on ".length();
   private final MusicManager musicManager;
   private final SoundManager soundManager;
   private final Options options;
   private boolean loaded;
   private final Library library = new Library();
   private final Listener listener;
   private final SoundBufferLibrary soundBuffers;
   private final SoundEngineExecutor executor;
   private final ChannelAccess channelAccess;
   private int tickCount;
   private long lastDeviceCheckTime;
   private final AtomicReference<DeviceCheckState> devicePoolState;
   private final Map<SoundInstance, ChannelAccess.ChannelHandle> instanceToChannel;
   private final Multimap<SoundSource, SoundInstance> instanceBySource;
   private final List<TickableSoundInstance> tickingSounds;
   private final Map<SoundInstance, Integer> queuedSounds;
   private final Map<SoundInstance, Integer> soundDeleteTime;
   private final List<SoundEventListener> listeners;
   private final List<TickableSoundInstance> queuedTickableSounds;
   private final List<Sound> preloadQueue;

   public SoundEngine(MusicManager var1, SoundManager var2, Options var3, ResourceProvider var4) {
      super();
      this.listener = this.library.getListener();
      this.executor = new SoundEngineExecutor();
      this.channelAccess = new ChannelAccess(this.library, this.executor);
      this.devicePoolState = new AtomicReference(SoundEngine.DeviceCheckState.NO_CHANGE);
      this.instanceToChannel = Maps.newHashMap();
      this.instanceBySource = HashMultimap.create();
      this.tickingSounds = Lists.newArrayList();
      this.queuedSounds = Maps.newHashMap();
      this.soundDeleteTime = Maps.newHashMap();
      this.listeners = Lists.newArrayList();
      this.queuedTickableSounds = Lists.newArrayList();
      this.preloadQueue = Lists.newArrayList();
      this.musicManager = var1;
      this.soundManager = var2;
      this.options = var3;
      this.soundBuffers = new SoundBufferLibrary(var4);
   }

   public void reload() {
      ONLY_WARN_ONCE.clear();

      for(SoundEvent var2 : BuiltInRegistries.SOUND_EVENT) {
         if (var2 != SoundEvents.EMPTY) {
            ResourceLocation var3 = var2.location();
            if (this.soundManager.getSoundEvent(var3) == null) {
               LOGGER.warn("Missing sound for event: {}", BuiltInRegistries.SOUND_EVENT.getKey(var2));
               ONLY_WARN_ONCE.add(var3);
            }
         }
      }

      this.destroy();
      this.loadLibrary();
   }

   private synchronized void loadLibrary() {
      if (!this.loaded) {
         try {
            String var1 = (String)this.options.soundDevice().get();
            this.library.init("".equals(var1) ? null : var1, (Boolean)this.options.directionalAudio().get());
            this.listener.reset();
            CompletableFuture var10000 = this.soundBuffers.preload(this.preloadQueue);
            List var10001 = this.preloadQueue;
            Objects.requireNonNull(var10001);
            var10000.thenRun(var10001::clear);
            this.loaded = true;
            LOGGER.info(MARKER, "Sound engine started");
         } catch (RuntimeException var2) {
            LOGGER.error(MARKER, "Error starting SoundSystem. Turning off sounds & music", var2);
         }

      }
   }

   public void updateCategoryVolume(SoundSource var1) {
      if (this.loaded) {
         if ((var1 == SoundSource.MASTER || var1 == SoundSource.MUSIC) && this.options.getFinalSoundSourceVolume(SoundSource.MUSIC) > 0.0F) {
            this.musicManager.showNowPlayingToastIfNeeded();
         }

         this.instanceToChannel.forEach((var1x, var2) -> {
            float var3 = this.calculateVolume(var1x);
            var2.execute((var1) -> var1.setVolume(var3));
         });
      }
   }

   public void destroy() {
      if (this.loaded) {
         this.stopAll();
         this.soundBuffers.clear();
         this.library.cleanup();
         this.loaded = false;
      }

   }

   public void emergencyShutdown() {
      if (this.loaded) {
         this.library.cleanup();
      }

   }

   public void stop(SoundInstance var1) {
      if (this.loaded) {
         ChannelAccess.ChannelHandle var2 = (ChannelAccess.ChannelHandle)this.instanceToChannel.get(var1);
         if (var2 != null) {
            var2.execute(Channel::stop);
         }
      }

   }

   public void setVolume(SoundInstance var1, float var2) {
      if (this.loaded) {
         ChannelAccess.ChannelHandle var3 = (ChannelAccess.ChannelHandle)this.instanceToChannel.get(var1);
         if (var3 != null) {
            var3.execute((var3x) -> var3x.setVolume(var2 * this.calculateVolume(var1)));
         }
      }

   }

   public void stopAll() {
      if (this.loaded) {
         this.executor.shutDown();
         this.instanceToChannel.clear();
         this.channelAccess.clear();
         this.queuedSounds.clear();
         this.tickingSounds.clear();
         this.instanceBySource.clear();
         this.soundDeleteTime.clear();
         this.queuedTickableSounds.clear();
         this.executor.startUp();
      }

   }

   public void addEventListener(SoundEventListener var1) {
      this.listeners.add(var1);
   }

   public void removeEventListener(SoundEventListener var1) {
      this.listeners.remove(var1);
   }

   private boolean shouldChangeDevice() {
      if (this.library.isCurrentDeviceDisconnected()) {
         LOGGER.info("Audio device was lost!");
         return true;
      } else {
         long var1 = Util.getMillis();
         boolean var3 = var1 - this.lastDeviceCheckTime >= 1000L;
         if (var3) {
            this.lastDeviceCheckTime = var1;
            if (this.devicePoolState.compareAndSet(SoundEngine.DeviceCheckState.NO_CHANGE, SoundEngine.DeviceCheckState.ONGOING)) {
               String var4 = (String)this.options.soundDevice().get();
               Util.ioPool().execute(() -> {
                  if ("".equals(var4)) {
                     if (this.library.hasDefaultDeviceChanged()) {
                        LOGGER.info("System default audio device has changed!");
                        this.devicePoolState.compareAndSet(SoundEngine.DeviceCheckState.ONGOING, SoundEngine.DeviceCheckState.CHANGE_DETECTED);
                     }
                  } else if (!this.library.getCurrentDeviceName().equals(var4) && this.library.getAvailableSoundDevices().contains(var4)) {
                     LOGGER.info("Preferred audio device has become available!");
                     this.devicePoolState.compareAndSet(SoundEngine.DeviceCheckState.ONGOING, SoundEngine.DeviceCheckState.CHANGE_DETECTED);
                  }

                  this.devicePoolState.compareAndSet(SoundEngine.DeviceCheckState.ONGOING, SoundEngine.DeviceCheckState.NO_CHANGE);
               });
            }
         }

         return this.devicePoolState.compareAndSet(SoundEngine.DeviceCheckState.CHANGE_DETECTED, SoundEngine.DeviceCheckState.NO_CHANGE);
      }
   }

   public void tick(boolean var1) {
      if (this.shouldChangeDevice()) {
         this.reload();
      }

      if (!var1) {
         this.tickInGameSound();
      } else {
         this.tickMusicWhenPaused();
      }

      this.channelAccess.scheduleTick();
   }

   private void tickInGameSound() {
      ++this.tickCount;
      this.queuedTickableSounds.stream().filter(SoundInstance::canPlaySound).forEach(this::play);
      this.queuedTickableSounds.clear();

      for(TickableSoundInstance var2 : this.tickingSounds) {
         if (!var2.canPlaySound()) {
            this.stop(var2);
         }

         var2.tick();
         if (var2.isStopped()) {
            this.stop(var2);
         } else {
            float var3 = this.calculateVolume(var2);
            float var4 = this.calculatePitch(var2);
            Vec3 var5 = new Vec3(var2.getX(), var2.getY(), var2.getZ());
            ChannelAccess.ChannelHandle var6 = (ChannelAccess.ChannelHandle)this.instanceToChannel.get(var2);
            if (var6 != null) {
               var6.execute((var3x) -> {
                  var3x.setVolume(var3);
                  var3x.setPitch(var4);
                  var3x.setSelfPosition(var5);
               });
            }
         }
      }

      Iterator var8 = this.instanceToChannel.entrySet().iterator();

      while(var8.hasNext()) {
         Map.Entry var9 = (Map.Entry)var8.next();
         ChannelAccess.ChannelHandle var11 = (ChannelAccess.ChannelHandle)var9.getValue();
         SoundInstance var13 = (SoundInstance)var9.getKey();
         if (var11.isStopped()) {
            int var15 = (Integer)this.soundDeleteTime.get(var13);
            if (var15 <= this.tickCount) {
               if (shouldLoopManually(var13)) {
                  this.queuedSounds.put(var13, this.tickCount + var13.getDelay());
               }

               var8.remove();
               LOGGER.debug(MARKER, "Removed channel {} because it's not playing anymore", var11);
               this.soundDeleteTime.remove(var13);

               try {
                  this.instanceBySource.remove(var13.getSource(), var13);
               } catch (RuntimeException var7) {
               }

               if (var13 instanceof TickableSoundInstance) {
                  this.tickingSounds.remove(var13);
               }
            }
         }
      }

      Iterator var10 = this.queuedSounds.entrySet().iterator();

      while(var10.hasNext()) {
         Map.Entry var12 = (Map.Entry)var10.next();
         if (this.tickCount >= (Integer)var12.getValue()) {
            SoundInstance var14 = (SoundInstance)var12.getKey();
            if (var14 instanceof TickableSoundInstance) {
               ((TickableSoundInstance)var14).tick();
            }

            this.play(var14);
            var10.remove();
         }
      }

   }

   private void tickMusicWhenPaused() {
      Iterator var1 = this.instanceToChannel.entrySet().iterator();

      while(var1.hasNext()) {
         Map.Entry var2 = (Map.Entry)var1.next();
         ChannelAccess.ChannelHandle var3 = (ChannelAccess.ChannelHandle)var2.getValue();
         SoundInstance var4 = (SoundInstance)var2.getKey();
         if (var4.getSource() == SoundSource.MUSIC && var3.isStopped()) {
            var1.remove();
            LOGGER.debug(MARKER, "Removed channel {} because it's not playing anymore", var3);
            this.soundDeleteTime.remove(var4);
            this.instanceBySource.remove(var4.getSource(), var4);
         }
      }

   }

   private static boolean requiresManualLooping(SoundInstance var0) {
      return var0.getDelay() > 0;
   }

   private static boolean shouldLoopManually(SoundInstance var0) {
      return var0.isLooping() && requiresManualLooping(var0);
   }

   private static boolean shouldLoopAutomatically(SoundInstance var0) {
      return var0.isLooping() && !requiresManualLooping(var0);
   }

   public boolean isActive(SoundInstance var1) {
      if (!this.loaded) {
         return false;
      } else {
         return this.soundDeleteTime.containsKey(var1) && (Integer)this.soundDeleteTime.get(var1) <= this.tickCount ? true : this.instanceToChannel.containsKey(var1);
      }
   }

   public PlayResult play(SoundInstance var1) {
      if (!this.loaded) {
         return SoundEngine.PlayResult.NOT_STARTED;
      } else if (!var1.canPlaySound()) {
         return SoundEngine.PlayResult.NOT_STARTED;
      } else {
         WeighedSoundEvents var2 = var1.resolve(this.soundManager);
         ResourceLocation var3 = var1.getLocation();
         if (var2 == null) {
            if (ONLY_WARN_ONCE.add(var3)) {
               LOGGER.warn(MARKER, "Unable to play unknown soundEvent: {}", var3);
            }

            if (!SharedConstants.DEBUG_SUBTITLES) {
               return SoundEngine.PlayResult.NOT_STARTED;
            }

            var2 = new WeighedSoundEvents(var3, "FOR THE DEBUG!");
         }

         Sound var4 = var1.getSound();
         if (var4 == SoundManager.INTENTIONALLY_EMPTY_SOUND) {
            return SoundEngine.PlayResult.NOT_STARTED;
         } else if (var4 == SoundManager.EMPTY_SOUND) {
            if (ONLY_WARN_ONCE.add(var3)) {
               LOGGER.warn(MARKER, "Unable to play empty soundEvent: {}", var3);
            }

            return SoundEngine.PlayResult.NOT_STARTED;
         } else {
            float var5 = var1.getVolume();
            float var6 = Math.max(var5, 1.0F) * (float)var4.getAttenuationDistance();
            SoundSource var7 = var1.getSource();
            float var8 = this.calculateVolume(var5, var7);
            float var9 = this.calculatePitch(var1);
            SoundInstance.Attenuation var10 = var1.getAttenuation();
            boolean var11 = var1.isRelative();
            if (!this.listeners.isEmpty()) {
               float var12 = !var11 && var10 != SoundInstance.Attenuation.NONE ? var6 : 1.0F / 0.0F;

               for(SoundEventListener var14 : this.listeners) {
                  var14.onPlaySound(var1, var2, var12);
               }
            }

            boolean var18 = false;
            if (var8 == 0.0F) {
               if (!var1.canStartSilent() && var7 != SoundSource.MUSIC) {
                  LOGGER.debug(MARKER, "Skipped playing sound {}, volume was zero.", var4.getLocation());
                  return SoundEngine.PlayResult.NOT_STARTED;
               }

               var18 = true;
            }

            Vec3 var19 = new Vec3(var1.getX(), var1.getY(), var1.getZ());
            boolean var20 = shouldLoopAutomatically(var1);
            boolean var15 = var4.shouldStream();
            CompletableFuture var16 = this.channelAccess.createHandle(var4.shouldStream() ? Library.Pool.STREAMING : Library.Pool.STATIC);
            ChannelAccess.ChannelHandle var17 = (ChannelAccess.ChannelHandle)var16.join();
            if (var17 == null) {
               if (SharedConstants.IS_RUNNING_IN_IDE) {
                  LOGGER.warn("Failed to create new sound handle");
               }

               return SoundEngine.PlayResult.NOT_STARTED;
            } else {
               LOGGER.debug(MARKER, "Playing sound {} for event {}", var4.getLocation(), var3);
               this.soundDeleteTime.put(var1, this.tickCount + 20);
               this.instanceToChannel.put(var1, var17);
               this.instanceBySource.put(var7, var1);
               var17.execute((var8x) -> {
                  var8x.setPitch(var9);
                  var8x.setVolume(var8);
                  if (var10 == SoundInstance.Attenuation.LINEAR) {
                     var8x.linearAttenuation(var6);
                  } else {
                     var8x.disableAttenuation();
                  }

                  var8x.setLooping(var20 && !var15);
                  var8x.setSelfPosition(var19);
                  var8x.setRelative(var11);
               });
               if (!var15) {
                  this.soundBuffers.getCompleteBuffer(var4.getPath()).thenAccept((var1x) -> var17.execute((var1) -> {
                        var1.attachStaticBuffer(var1x);
                        var1.play();
                     }));
               } else {
                  this.soundBuffers.getStream(var4.getPath(), var20).thenAccept((var1x) -> var17.execute((var1) -> {
                        var1.attachBufferStream(var1x);
                        var1.play();
                     }));
               }

               if (var1 instanceof TickableSoundInstance) {
                  this.tickingSounds.add((TickableSoundInstance)var1);
               }

               return var18 ? SoundEngine.PlayResult.STARTED_SILENTLY : SoundEngine.PlayResult.STARTED;
            }
         }
      }
   }

   public void queueTickingSound(TickableSoundInstance var1) {
      this.queuedTickableSounds.add(var1);
   }

   public void requestPreload(Sound var1) {
      this.preloadQueue.add(var1);
   }

   private float calculatePitch(SoundInstance var1) {
      return Mth.clamp(var1.getPitch(), 0.5F, 2.0F);
   }

   private float calculateVolume(SoundInstance var1) {
      return this.calculateVolume(var1.getVolume(), var1.getSource());
   }

   private float calculateVolume(float var1, SoundSource var2) {
      return Mth.clamp(var1, 0.0F, 1.0F) * Mth.clamp(this.options.getFinalSoundSourceVolume(var2), 0.0F, 1.0F);
   }

   public void pauseAllExcept(SoundSource... var1) {
      if (this.loaded) {
         for(Map.Entry var3 : this.instanceToChannel.entrySet()) {
            if (!List.of(var1).contains(((SoundInstance)var3.getKey()).getSource())) {
               ((ChannelAccess.ChannelHandle)var3.getValue()).execute(Channel::pause);
            }
         }

      }
   }

   public void resume() {
      if (this.loaded) {
         this.channelAccess.executeOnChannels((var0) -> var0.forEach(Channel::unpause));
      }

   }

   public void playDelayed(SoundInstance var1, int var2) {
      this.queuedSounds.put(var1, this.tickCount + var2);
   }

   public void updateSource(Camera var1) {
      if (this.loaded && var1.isInitialized()) {
         ListenerTransform var2 = new ListenerTransform(var1.getPosition(), new Vec3(var1.getLookVector()), new Vec3(var1.getUpVector()));
         this.executor.execute(() -> this.listener.setTransform(var2));
      }
   }

   public void stop(@Nullable ResourceLocation var1, @Nullable SoundSource var2) {
      if (var2 != null) {
         for(SoundInstance var4 : this.instanceBySource.get(var2)) {
            if (var1 == null || var4.getLocation().equals(var1)) {
               this.stop(var4);
            }
         }
      } else if (var1 == null) {
         this.stopAll();
      } else {
         for(SoundInstance var6 : this.instanceToChannel.keySet()) {
            if (var6.getLocation().equals(var1)) {
               this.stop(var6);
            }
         }
      }

   }

   public String getDebugString() {
      return this.library.getDebugString();
   }

   public List<String> getAvailableSoundDevices() {
      return this.library.getAvailableSoundDevices();
   }

   public ListenerTransform getListenerTransform() {
      return this.listener.getTransform();
   }

   static enum DeviceCheckState {
      ONGOING,
      CHANGE_DETECTED,
      NO_CHANGE;

      private DeviceCheckState() {
      }

      // $FF: synthetic method
      private static DeviceCheckState[] $values() {
         return new DeviceCheckState[]{ONGOING, CHANGE_DETECTED, NO_CHANGE};
      }
   }

   public static enum PlayResult {
      STARTED,
      STARTED_SILENTLY,
      NOT_STARTED;

      private PlayResult() {
      }

      // $FF: synthetic method
      private static PlayResult[] $values() {
         return new PlayResult[]{STARTED, STARTED_SILENTLY, NOT_STARTED};
      }
   }
}
