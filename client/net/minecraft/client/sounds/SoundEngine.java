package net.minecraft.client.sounds;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.audio.Channel;
import com.mojang.blaze3d.audio.Library;
import com.mojang.blaze3d.audio.Listener;
import com.mojang.logging.LogUtils;
import com.mojang.math.Vector3f;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
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
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.sounds.SoundEvent;
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
   private final SoundManager soundManager;
   private final Options options;
   private boolean loaded;
   private final Library library = new Library();
   private final Listener listener = this.library.getListener();
   private final SoundBufferLibrary soundBuffers;
   private final SoundEngineExecutor executor = new SoundEngineExecutor();
   private final ChannelAccess channelAccess = new ChannelAccess(this.library, this.executor);
   private int tickCount;
   private long lastDeviceCheckTime;
   private final AtomicReference<SoundEngine.DeviceCheckState> devicePoolState = new AtomicReference<>(SoundEngine.DeviceCheckState.NO_CHANGE);
   private final Map<SoundInstance, ChannelAccess.ChannelHandle> instanceToChannel = Maps.newHashMap();
   private final Multimap<SoundSource, SoundInstance> instanceBySource = HashMultimap.create();
   private final List<TickableSoundInstance> tickingSounds = Lists.newArrayList();
   private final Map<SoundInstance, Integer> queuedSounds = Maps.newHashMap();
   private final Map<SoundInstance, Integer> soundDeleteTime = Maps.newHashMap();
   private final List<SoundEventListener> listeners = Lists.newArrayList();
   private final List<TickableSoundInstance> queuedTickableSounds = Lists.newArrayList();
   private final List<Sound> preloadQueue = Lists.newArrayList();

   public SoundEngine(SoundManager var1, Options var2, ResourceManager var3) {
      super();
      this.soundManager = var1;
      this.options = var2;
      this.soundBuffers = new SoundBufferLibrary(var3);
   }

   public void reload() {
      ONLY_WARN_ONCE.clear();

      for(SoundEvent var2 : Registry.SOUND_EVENT) {
         ResourceLocation var3 = var2.getLocation();
         if (this.soundManager.getSoundEvent(var3) == null) {
            LOGGER.warn("Missing sound for event: {}", Registry.SOUND_EVENT.getKey(var2));
            ONLY_WARN_ONCE.add(var3);
         }
      }

      this.destroy();
      this.loadLibrary();
   }

   private synchronized void loadLibrary() {
      if (!this.loaded) {
         try {
            String var1 = this.options.soundDevice().get();
            this.library.init("".equals(var1) ? null : var1, this.options.directionalAudio().get());
            this.listener.reset();
            this.listener.setGain(this.options.getSoundSourceVolume(SoundSource.MASTER));
            this.soundBuffers.preload(this.preloadQueue).thenRun(this.preloadQueue::clear);
            this.loaded = true;
            LOGGER.info(MARKER, "Sound engine started");
         } catch (RuntimeException var2) {
            LOGGER.error(MARKER, "Error starting SoundSystem. Turning off sounds & music", var2);
         }
      }
   }

   private float getVolume(@Nullable SoundSource var1) {
      return var1 != null && var1 != SoundSource.MASTER ? this.options.getSoundSourceVolume(var1) : 1.0F;
   }

   public void updateCategoryVolume(SoundSource var1, float var2) {
      if (this.loaded) {
         if (var1 == SoundSource.MASTER) {
            this.listener.setGain(var2);
         } else {
            this.instanceToChannel.forEach((var1x, var2x) -> {
               float var3 = this.calculateVolume(var1x);
               var2x.execute(var1xx -> {
                  if (var3 <= 0.0F) {
                     var1xx.stop();
                  } else {
                     var1xx.setVolume(var3);
                  }
               });
            });
         }
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

   public void stop(SoundInstance var1) {
      if (this.loaded) {
         ChannelAccess.ChannelHandle var2 = this.instanceToChannel.get(var1);
         if (var2 != null) {
            var2.execute(Channel::stop);
         }
      }
   }

   public void stopAll() {
      if (this.loaded) {
         this.executor.flush();
         this.instanceToChannel.values().forEach(var0 -> var0.execute(Channel::stop));
         this.instanceToChannel.clear();
         this.channelAccess.clear();
         this.queuedSounds.clear();
         this.tickingSounds.clear();
         this.instanceBySource.clear();
         this.soundDeleteTime.clear();
         this.queuedTickableSounds.clear();
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
               String var4 = this.options.soundDevice().get();
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
         this.tickNonPaused();
      }

      this.channelAccess.scheduleTick();
   }

   private void tickNonPaused() {
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
            ChannelAccess.ChannelHandle var6 = this.instanceToChannel.get(var2);
            if (var6 != null) {
               var6.execute(var3x -> {
                  var3x.setVolume(var3);
                  var3x.setPitch(var4);
                  var3x.setSelfPosition(var5);
               });
            }
         }
      }

      Iterator var9 = this.instanceToChannel.entrySet().iterator();

      while(var9.hasNext()) {
         Entry var10 = (Entry)var9.next();
         ChannelAccess.ChannelHandle var12 = (ChannelAccess.ChannelHandle)var10.getValue();
         SoundInstance var14 = (SoundInstance)var10.getKey();
         float var16 = this.options.getSoundSourceVolume(var14.getSource());
         if (var16 <= 0.0F) {
            var12.execute(Channel::stop);
            var9.remove();
         } else if (var12.isStopped()) {
            int var17 = this.soundDeleteTime.get(var14);
            if (var17 <= this.tickCount) {
               if (shouldLoopManually(var14)) {
                  this.queuedSounds.put(var14, this.tickCount + var14.getDelay());
               }

               var9.remove();
               LOGGER.debug(MARKER, "Removed channel {} because it's not playing anymore", var12);
               this.soundDeleteTime.remove(var14);

               try {
                  this.instanceBySource.remove(var14.getSource(), var14);
               } catch (RuntimeException var8) {
               }

               if (var14 instanceof TickableSoundInstance) {
                  this.tickingSounds.remove(var14);
               }
            }
         }
      }

      Iterator var11 = this.queuedSounds.entrySet().iterator();

      while(var11.hasNext()) {
         Entry var13 = (Entry)var11.next();
         if (this.tickCount >= var13.getValue()) {
            SoundInstance var15 = (SoundInstance)var13.getKey();
            if (var15 instanceof TickableSoundInstance) {
               ((TickableSoundInstance)var15).tick();
            }

            this.play(var15);
            var11.remove();
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
         return this.soundDeleteTime.containsKey(var1) && this.soundDeleteTime.get(var1) <= this.tickCount ? true : this.instanceToChannel.containsKey(var1);
      }
   }

   public void play(SoundInstance var1) {
      if (this.loaded) {
         if (var1.canPlaySound()) {
            WeighedSoundEvents var2 = var1.resolve(this.soundManager);
            ResourceLocation var3 = var1.getLocation();
            if (var2 == null) {
               if (ONLY_WARN_ONCE.add(var3)) {
                  LOGGER.warn(MARKER, "Unable to play unknown soundEvent: {}", var3);
               }
            } else {
               Sound var4 = var1.getSound();
               if (var4 == SoundManager.EMPTY_SOUND) {
                  if (ONLY_WARN_ONCE.add(var3)) {
                     LOGGER.warn(MARKER, "Unable to play empty soundEvent: {}", var3);
                  }
               } else {
                  float var5 = var1.getVolume();
                  float var6 = Math.max(var5, 1.0F) * (float)var4.getAttenuationDistance();
                  SoundSource var7 = var1.getSource();
                  float var8 = this.calculateVolume(var5, var7);
                  float var9 = this.calculatePitch(var1);
                  SoundInstance.Attenuation var10 = var1.getAttenuation();
                  boolean var11 = var1.isRelative();
                  if (var8 == 0.0F && !var1.canStartSilent()) {
                     LOGGER.debug(MARKER, "Skipped playing sound {}, volume was zero.", var4.getLocation());
                  } else {
                     Vec3 var12 = new Vec3(var1.getX(), var1.getY(), var1.getZ());
                     if (!this.listeners.isEmpty()) {
                        boolean var13 = var11
                           || var10 == SoundInstance.Attenuation.NONE
                           || this.listener.getListenerPosition().distanceToSqr(var12) < (double)(var6 * var6);
                        if (var13) {
                           for(SoundEventListener var15 : this.listeners) {
                              var15.onPlaySound(var1, var2);
                           }
                        } else {
                           LOGGER.debug(MARKER, "Did not notify listeners of soundEvent: {}, it is too far away to hear", var3);
                        }
                     }

                     if (this.listener.getGain() <= 0.0F) {
                        LOGGER.debug(MARKER, "Skipped playing soundEvent: {}, master volume was zero", var3);
                     } else {
                        boolean var17 = shouldLoopAutomatically(var1);
                        boolean var18 = var4.shouldStream();
                        CompletableFuture var19 = this.channelAccess.createHandle(var4.shouldStream() ? Library.Pool.STREAMING : Library.Pool.STATIC);
                        ChannelAccess.ChannelHandle var16 = (ChannelAccess.ChannelHandle)var19.join();
                        if (var16 == null) {
                           if (SharedConstants.IS_RUNNING_IN_IDE) {
                              LOGGER.warn("Failed to create new sound handle");
                           }
                        } else {
                           LOGGER.debug(MARKER, "Playing sound {} for event {}", var4.getLocation(), var3);
                           this.soundDeleteTime.put(var1, this.tickCount + 20);
                           this.instanceToChannel.put(var1, var16);
                           this.instanceBySource.put(var7, var1);
                           var16.execute(var8x -> {
                              var8x.setPitch(var9);
                              var8x.setVolume(var8);
                              if (var10 == SoundInstance.Attenuation.LINEAR) {
                                 var8x.linearAttenuation(var6);
                              } else {
                                 var8x.disableAttenuation();
                              }

                              var8x.setLooping(var17 && !var18);
                              var8x.setSelfPosition(var12);
                              var8x.setRelative(var11);
                           });
                           if (!var18) {
                              this.soundBuffers.getCompleteBuffer(var4.getPath()).thenAccept(var1x -> var16.execute(var1xx -> {
                                    var1xx.attachStaticBuffer(var1x);
                                    var1xx.play();
                                 }));
                           } else {
                              this.soundBuffers.getStream(var4.getPath(), var17).thenAccept(var1x -> var16.execute(var1xx -> {
                                    var1xx.attachBufferStream(var1x);
                                    var1xx.play();
                                 }));
                           }

                           if (var1 instanceof TickableSoundInstance) {
                              this.tickingSounds.add(var1);
                           }
                        }
                     }
                  }
               }
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
      return Mth.clamp(var1 * this.getVolume(var2), 0.0F, 1.0F);
   }

   public void pause() {
      if (this.loaded) {
         this.channelAccess.executeOnChannels(var0 -> var0.forEach(Channel::pause));
      }
   }

   public void resume() {
      if (this.loaded) {
         this.channelAccess.executeOnChannels(var0 -> var0.forEach(Channel::unpause));
      }
   }

   public void playDelayed(SoundInstance var1, int var2) {
      this.queuedSounds.put(var1, this.tickCount + var2);
   }

   public void updateSource(Camera var1) {
      if (this.loaded && var1.isInitialized()) {
         Vec3 var2 = var1.getPosition();
         Vector3f var3 = var1.getLookVector();
         Vector3f var4 = var1.getUpVector();
         this.executor.execute(() -> {
            this.listener.setListenerPosition(var2);
            this.listener.setListenerOrientation(var3, var4);
         });
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

   static enum DeviceCheckState {
      ONGOING,
      CHANGE_DETECTED,
      NO_CHANGE;

      private DeviceCheckState() {
      }
   }
}