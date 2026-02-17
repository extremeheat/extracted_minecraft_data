package net.minecraft.client.sounds;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.audio.Channel;
import com.mojang.blaze3d.audio.DeviceList;
import com.mojang.blaze3d.audio.Library;
import com.mojang.blaze3d.audio.Listener;
import com.mojang.blaze3d.audio.ListenerTransform;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.minecraft.SharedConstants;
import net.minecraft.client.Camera;
import net.minecraft.client.Options;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
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
   private static final Set<Identifier> ONLY_WARN_ONCE = Sets.newHashSet();
   public static final String MISSING_SOUND = "FOR THE DEBUG!";
   public static final String OPEN_AL_SOFT_PREFIX = "OpenAL Soft on ";
   public static final int OPEN_AL_SOFT_PREFIX_LENGTH = "OpenAL Soft on ".length();
   private final SoundManager soundManager;
   private final Options options;
   private boolean loaded;
   private final Library library = new Library();
   private final Listener listener;
   private final SoundBufferLibrary soundBuffers;
   private final SoundEngineExecutor executor;
   private final ChannelAccess channelAccess;
   private int tickCount;
   private DeviceList lastSeenDevices;
   private final DeviceTracker deviceTracker;
   private final Map<SoundInstance, ChannelAccess.ChannelHandle> instanceToChannel;
   private final Multimap<SoundSource, SoundInstance> instanceBySource;
   private final Object2FloatMap<SoundSource> gainBySource;
   private final List<TickableSoundInstance> tickingSounds;
   private final Map<SoundInstance, Integer> queuedSounds;
   private final Map<SoundInstance, Integer> soundDeleteTime;
   private final List<SoundEventListener> listeners;
   private final List<TickableSoundInstance> queuedTickableSounds;
   private final List<Sound> preloadQueue;

   public SoundEngine(final SoundManager soundManager, final Options options, final ResourceProvider resourceProvider) {
      super();
      this.listener = this.library.getListener();
      this.executor = new SoundEngineExecutor();
      this.channelAccess = new ChannelAccess(this.library, this.executor);
      this.deviceTracker = Library.createDeviceTracker();
      this.instanceToChannel = Maps.newHashMap();
      this.instanceBySource = HashMultimap.create();
      this.gainBySource = (Object2FloatMap)Util.make(new Object2FloatOpenHashMap(), (map) -> map.defaultReturnValue(1.0F));
      this.tickingSounds = Lists.newArrayList();
      this.queuedSounds = Maps.newHashMap();
      this.soundDeleteTime = Maps.newHashMap();
      this.listeners = Lists.newArrayList();
      this.queuedTickableSounds = Lists.newArrayList();
      this.preloadQueue = Lists.newArrayList();
      this.soundManager = soundManager;
      this.options = options;
      this.soundBuffers = new SoundBufferLibrary(resourceProvider);
      this.lastSeenDevices = this.deviceTracker.currentDevices();
   }

   public void reload() {
      ONLY_WARN_ONCE.clear();

      for(SoundEvent sound : BuiltInRegistries.SOUND_EVENT) {
         if (sound != SoundEvents.EMPTY) {
            Identifier location = sound.location();
            if (this.soundManager.getSoundEvent(location) == null) {
               LOGGER.warn("Missing sound for event: {}", BuiltInRegistries.SOUND_EVENT.getKey(sound));
               ONLY_WARN_ONCE.add(location);
            }
         }
      }

      this.destroy();
      this.loadLibrary();
   }

   private synchronized void loadLibrary() {
      if (!this.loaded) {
         try {
            String soundDevice = (String)this.options.soundDevice().get();
            DeviceList currentDevices = this.deviceTracker.currentDevices();
            this.library.init(Options.isSoundDeviceDefault(soundDevice) ? null : soundDevice, currentDevices, (Boolean)this.options.directionalAudio().get());
            this.listener.reset();
            CompletableFuture var10000 = this.soundBuffers.preload(this.preloadQueue);
            List var10001 = this.preloadQueue;
            Objects.requireNonNull(var10001);
            var10000.thenRun(var10001::clear);
            this.loaded = true;
            LOGGER.info(MARKER, "Sound engine started");
         } catch (RuntimeException e) {
            LOGGER.error(MARKER, "Error starting SoundSystem. Turning off sounds & music", e);
         }

      }
   }

   public void refreshCategoryVolume(final SoundSource source) {
      if (this.loaded) {
         this.instanceToChannel.forEach((soundInstance, channelHandle) -> {
            if (source == soundInstance.getSource() || source == SoundSource.MASTER) {
               float newVolume = this.calculateVolume(soundInstance);
               channelHandle.execute((channel) -> channel.setVolume(newVolume));
            }

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

   public void stop(final SoundInstance soundInstance) {
      if (this.loaded) {
         ChannelAccess.ChannelHandle handle = (ChannelAccess.ChannelHandle)this.instanceToChannel.get(soundInstance);
         if (handle != null) {
            handle.execute(Channel::stop);
         }
      }

   }

   public void updateCategoryVolume(final SoundSource source, final float gain) {
      this.gainBySource.put(source, Mth.clamp(gain, 0.0F, 1.0F));
      this.refreshCategoryVolume(source);
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
         this.gainBySource.clear();
         this.executor.startUp();
      }

   }

   public void addEventListener(final SoundEventListener listener) {
      this.listeners.add(listener);
   }

   public void removeEventListener(final SoundEventListener listener) {
      this.listeners.remove(listener);
   }

   private boolean shouldChangeDevice() {
      if (this.library.isCurrentDeviceDisconnected()) {
         LOGGER.info("Audio device was lost!");
         this.deviceTracker.forceRefresh();
         return true;
      } else {
         this.deviceTracker.tick();
         boolean shouldChangeDevice = false;
         DeviceList currentDevices = this.deviceTracker.currentDevices();
         if (!currentDevices.equals(this.lastSeenDevices)) {
            String currentDeviceName = this.library.currentDeviceName();
            if (!currentDevices.allDevices().contains(currentDeviceName)) {
               LOGGER.info("Current audio device has disapeared!");
               shouldChangeDevice = true;
            }

            String userSelectedDevice = (String)this.options.soundDevice().get();
            if (Options.isSoundDeviceDefault(userSelectedDevice)) {
               String newDefault = currentDevices.defaultDevice();
               if (!Objects.equals(currentDeviceName, newDefault)) {
                  LOGGER.info("System default audio device has changed!");
                  shouldChangeDevice = true;
               }
            } else if (!Objects.equals(currentDeviceName, userSelectedDevice) && currentDevices.allDevices().contains(userSelectedDevice)) {
               LOGGER.info("Preferred audio device has become available!");
               shouldChangeDevice = true;
            }

            this.lastSeenDevices = currentDevices;
         }

         return shouldChangeDevice;
      }
   }

   public void tick(final boolean paused) {
      if (this.shouldChangeDevice()) {
         this.reload();
      }

      if (!paused) {
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

      for(TickableSoundInstance instance : this.tickingSounds) {
         if (!instance.canPlaySound()) {
            this.stop(instance);
         }

         instance.tick();
         if (instance.isStopped()) {
            this.stop(instance);
         } else {
            float volume = this.calculateVolume(instance);
            float pitch = this.calculatePitch(instance);
            Vec3 position = new Vec3(instance.getX(), instance.getY(), instance.getZ());
            ChannelAccess.ChannelHandle handle = (ChannelAccess.ChannelHandle)this.instanceToChannel.get(instance);
            if (handle != null) {
               handle.execute((channel) -> {
                  channel.setVolume(volume);
                  channel.setPitch(pitch);
                  channel.setSelfPosition(position);
               });
            }
         }
      }

      Iterator<Map.Entry<SoundInstance, ChannelAccess.ChannelHandle>> iterator = this.instanceToChannel.entrySet().iterator();

      while(iterator.hasNext()) {
         Map.Entry<SoundInstance, ChannelAccess.ChannelHandle> entry = (Map.Entry)iterator.next();
         ChannelAccess.ChannelHandle handle = (ChannelAccess.ChannelHandle)entry.getValue();
         SoundInstance instance = (SoundInstance)entry.getKey();
         if (handle.isStopped()) {
            int minDeleteTime = (Integer)this.soundDeleteTime.get(instance);
            if (minDeleteTime <= this.tickCount) {
               if (shouldLoopManually(instance)) {
                  this.queuedSounds.put(instance, this.tickCount + instance.getDelay());
               }

               iterator.remove();
               LOGGER.debug(MARKER, "Removed channel {} because it's not playing anymore", handle);
               this.soundDeleteTime.remove(instance);

               try {
                  this.instanceBySource.remove(instance.getSource(), instance);
               } catch (RuntimeException var7) {
               }

               if (instance instanceof TickableSoundInstance) {
                  this.tickingSounds.remove(instance);
               }
            }
         }
      }

      Iterator<Map.Entry<SoundInstance, Integer>> queueIterator = this.queuedSounds.entrySet().iterator();

      while(queueIterator.hasNext()) {
         Map.Entry<SoundInstance, Integer> next = (Map.Entry)queueIterator.next();
         if (this.tickCount >= (Integer)next.getValue()) {
            SoundInstance instance = (SoundInstance)next.getKey();
            if (instance instanceof TickableSoundInstance) {
               ((TickableSoundInstance)instance).tick();
            }

            this.play(instance);
            queueIterator.remove();
         }
      }

   }

   private void tickMusicWhenPaused() {
      Iterator<Map.Entry<SoundInstance, ChannelAccess.ChannelHandle>> iterator = this.instanceToChannel.entrySet().iterator();

      while(iterator.hasNext()) {
         Map.Entry<SoundInstance, ChannelAccess.ChannelHandle> entry = (Map.Entry)iterator.next();
         ChannelAccess.ChannelHandle handle = (ChannelAccess.ChannelHandle)entry.getValue();
         SoundInstance instance = (SoundInstance)entry.getKey();
         if (instance.getSource() == SoundSource.MUSIC && handle.isStopped()) {
            iterator.remove();
            LOGGER.debug(MARKER, "Removed channel {} because it's not playing anymore", handle);
            this.soundDeleteTime.remove(instance);
            this.instanceBySource.remove(instance.getSource(), instance);
         }
      }

   }

   private static boolean requiresManualLooping(final SoundInstance instance) {
      return instance.getDelay() > 0;
   }

   private static boolean shouldLoopManually(final SoundInstance instance) {
      return instance.isLooping() && requiresManualLooping(instance);
   }

   private static boolean shouldLoopAutomatically(final SoundInstance instance) {
      return instance.isLooping() && !requiresManualLooping(instance);
   }

   public boolean isActive(final SoundInstance instance) {
      if (!this.loaded) {
         return false;
      } else {
         return this.soundDeleteTime.containsKey(instance) && (Integer)this.soundDeleteTime.get(instance) <= this.tickCount ? true : this.instanceToChannel.containsKey(instance);
      }
   }

   public PlayResult play(final SoundInstance instance) {
      if (!this.loaded) {
         return SoundEngine.PlayResult.NOT_STARTED;
      } else if (!instance.canPlaySound()) {
         return SoundEngine.PlayResult.NOT_STARTED;
      } else {
         WeighedSoundEvents soundEvent = instance.resolve(this.soundManager);
         Identifier eventLocation = instance.getIdentifier();
         if (soundEvent == null) {
            if (ONLY_WARN_ONCE.add(eventLocation)) {
               LOGGER.warn(MARKER, "Unable to play unknown soundEvent: {}", eventLocation);
            }

            if (!SharedConstants.DEBUG_SUBTITLES) {
               return SoundEngine.PlayResult.NOT_STARTED;
            }

            soundEvent = new WeighedSoundEvents(eventLocation, "FOR THE DEBUG!");
         }

         Sound sound = instance.getSound();
         if (sound == SoundManager.INTENTIONALLY_EMPTY_SOUND) {
            return SoundEngine.PlayResult.NOT_STARTED;
         } else if (sound == SoundManager.EMPTY_SOUND) {
            if (ONLY_WARN_ONCE.add(eventLocation)) {
               LOGGER.warn(MARKER, "Unable to play empty soundEvent: {}", eventLocation);
            }

            return SoundEngine.PlayResult.NOT_STARTED;
         } else {
            float instanceVolume = instance.getVolume();
            float attenuationDistance = Math.max(instanceVolume, 1.0F) * (float)sound.getAttenuationDistance();
            SoundSource soundSource = instance.getSource();
            float volume = this.calculateVolume(instanceVolume, soundSource);
            float pitch = this.calculatePitch(instance);
            SoundInstance.Attenuation attenuation = instance.getAttenuation();
            boolean isRelative = instance.isRelative();
            if (!this.listeners.isEmpty()) {
               float range = !isRelative && attenuation != SoundInstance.Attenuation.NONE ? attenuationDistance : 1.0F / 0.0F;

               for(SoundEventListener listener : this.listeners) {
                  listener.onPlaySound(instance, soundEvent, range);
               }
            }

            boolean startedSilently = false;
            if (volume == 0.0F) {
               if (!instance.canStartSilent() && soundSource != SoundSource.MUSIC) {
                  LOGGER.debug(MARKER, "Skipped playing sound {}, volume was zero.", sound.getLocation());
                  return SoundEngine.PlayResult.NOT_STARTED;
               }

               startedSilently = true;
            }

            Vec3 position = new Vec3(instance.getX(), instance.getY(), instance.getZ());
            boolean isLooping = shouldLoopAutomatically(instance);
            boolean isStreaming = sound.shouldStream();
            CompletableFuture<ChannelAccess.ChannelHandle> handleFuture = this.channelAccess.createHandle(sound.shouldStream() ? Library.Pool.STREAMING : Library.Pool.STATIC);
            ChannelAccess.ChannelHandle handle = (ChannelAccess.ChannelHandle)handleFuture.join();
            if (handle == null) {
               if (SharedConstants.IS_RUNNING_IN_IDE) {
                  LOGGER.warn("Failed to create new sound handle");
               }

               return SoundEngine.PlayResult.NOT_STARTED;
            } else {
               LOGGER.debug(MARKER, "Playing sound {} for event {}", sound.getLocation(), eventLocation);
               this.soundDeleteTime.put(instance, this.tickCount + 20);
               this.instanceToChannel.put(instance, handle);
               this.instanceBySource.put(soundSource, instance);
               handle.execute((channel) -> {
                  channel.setPitch(pitch);
                  channel.setVolume(volume);
                  if (attenuation == SoundInstance.Attenuation.LINEAR) {
                     channel.linearAttenuation(attenuationDistance);
                  } else {
                     channel.disableAttenuation();
                  }

                  channel.setLooping(isLooping && !isStreaming);
                  channel.setSelfPosition(position);
                  channel.setRelative(isRelative);
               });
               if (!isStreaming) {
                  this.soundBuffers.getCompleteBuffer(sound.getPath()).thenAccept((soundBuffer) -> handle.execute((channel) -> {
                        channel.attachStaticBuffer(soundBuffer);
                        channel.play();
                     }));
               } else {
                  this.soundBuffers.getStream(sound.getPath(), isLooping).thenAccept((stream) -> handle.execute((channel) -> {
                        channel.attachBufferStream(stream);
                        channel.play();
                     }));
               }

               if (instance instanceof TickableSoundInstance) {
                  this.tickingSounds.add((TickableSoundInstance)instance);
               }

               return startedSilently ? SoundEngine.PlayResult.STARTED_SILENTLY : SoundEngine.PlayResult.STARTED;
            }
         }
      }
   }

   public void queueTickingSound(final TickableSoundInstance tickableSoundInstance) {
      this.queuedTickableSounds.add(tickableSoundInstance);
   }

   public void requestPreload(final Sound sound) {
      this.preloadQueue.add(sound);
   }

   private float calculatePitch(final SoundInstance instance) {
      return Mth.clamp(instance.getPitch(), 0.5F, 2.0F);
   }

   private float calculateVolume(final SoundInstance instance) {
      return this.calculateVolume(instance.getVolume(), instance.getSource());
   }

   private float calculateVolume(final float volume, final SoundSource source) {
      return Mth.clamp(volume, 0.0F, 1.0F) * Mth.clamp(this.options.getFinalSoundSourceVolume(source), 0.0F, 1.0F) * this.gainBySource.getFloat(source);
   }

   public void pauseAllExcept(final SoundSource... ignoredSources) {
      if (this.loaded) {
         for(Map.Entry<SoundInstance, ChannelAccess.ChannelHandle> instance : this.instanceToChannel.entrySet()) {
            if (!List.of(ignoredSources).contains(((SoundInstance)instance.getKey()).getSource())) {
               ((ChannelAccess.ChannelHandle)instance.getValue()).execute(Channel::pause);
            }
         }

      }
   }

   public void resume() {
      if (this.loaded) {
         this.channelAccess.executeOnChannels((channels) -> channels.forEach(Channel::unpause));
      }

   }

   public void playDelayed(final SoundInstance instance, final int delay) {
      this.queuedSounds.put(instance, this.tickCount + delay);
   }

   public void updateSource(final Camera camera) {
      if (this.loaded && camera.isInitialized()) {
         ListenerTransform transform = new ListenerTransform(camera.position(), new Vec3(camera.forwardVector()), new Vec3(camera.upVector()));
         this.executor.execute(() -> this.listener.setTransform(transform));
      }
   }

   public void stop(final @Nullable Identifier sound, final @Nullable SoundSource source) {
      if (source != null) {
         for(SoundInstance instance : this.instanceBySource.get(source)) {
            if (sound == null || instance.getIdentifier().equals(sound)) {
               this.stop(instance);
            }
         }
      } else if (sound == null) {
         this.stopAll();
      } else {
         for(SoundInstance instance : this.instanceToChannel.keySet()) {
            if (instance.getIdentifier().equals(sound)) {
               this.stop(instance);
            }
         }
      }

   }

   public String getDebugString() {
      return this.library.getDebugString();
   }

   public List<String> getAvailableSoundDevices() {
      return this.deviceTracker.currentDevices().allDevices();
   }

   public ListenerTransform getListenerTransform() {
      return this.listener.getTransform();
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
