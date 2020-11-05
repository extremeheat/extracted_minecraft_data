package net.minecraft.client.sounds;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.audio.Channel;
import com.mojang.blaze3d.audio.Library;
import com.mojang.blaze3d.audio.Listener;
import com.mojang.math.Vector3f;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class SoundEngine {
   private static final Marker MARKER = MarkerManager.getMarker("SOUNDS");
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Set<ResourceLocation> ONLY_WARN_ONCE = Sets.newHashSet();
   private final SoundManager soundManager;
   private final Options options;
   private boolean loaded;
   private final Library library = new Library();
   private final Listener listener;
   private final SoundBufferLibrary soundBuffers;
   private final SoundEngineExecutor executor;
   private final ChannelAccess channelAccess;
   private int tickCount;
   private final Map<SoundInstance, ChannelAccess.ChannelHandle> instanceToChannel;
   private final Multimap<SoundSource, SoundInstance> instanceBySource;
   private final List<TickableSoundInstance> tickingSounds;
   private final Map<SoundInstance, Integer> queuedSounds;
   private final Map<SoundInstance, Integer> soundDeleteTime;
   private final List<SoundEventListener> listeners;
   private final List<TickableSoundInstance> queuedTickableSounds;
   private final List<Sound> preloadQueue;

   public SoundEngine(SoundManager var1, Options var2, ResourceManager var3) {
      super();
      this.listener = this.library.getListener();
      this.executor = new SoundEngineExecutor();
      this.channelAccess = new ChannelAccess(this.library, this.executor);
      this.instanceToChannel = Maps.newHashMap();
      this.instanceBySource = HashMultimap.create();
      this.tickingSounds = Lists.newArrayList();
      this.queuedSounds = Maps.newHashMap();
      this.soundDeleteTime = Maps.newHashMap();
      this.listeners = Lists.newArrayList();
      this.queuedTickableSounds = Lists.newArrayList();
      this.preloadQueue = Lists.newArrayList();
      this.soundManager = var1;
      this.options = var2;
      this.soundBuffers = new SoundBufferLibrary(var3);
   }

   public void reload() {
      ONLY_WARN_ONCE.clear();
      Iterator var1 = Registry.SOUND_EVENT.iterator();

      while(var1.hasNext()) {
         SoundEvent var2 = (SoundEvent)var1.next();
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
            this.library.init();
            this.listener.reset();
            this.listener.setGain(this.options.getSoundSourceVolume(SoundSource.MASTER));
            CompletableFuture var10000 = this.soundBuffers.preload(this.preloadQueue);
            List var10001 = this.preloadQueue;
            var10000.thenRun(var10001::clear);
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
               var2x.execute((var1) -> {
                  if (var3 <= 0.0F) {
                     var1.stop();
                  } else {
                     var1.setVolume(var3);
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
         ChannelAccess.ChannelHandle var2 = (ChannelAccess.ChannelHandle)this.instanceToChannel.get(var1);
         if (var2 != null) {
            var2.execute(Channel::stop);
         }
      }

   }

   public void stopAll() {
      if (this.loaded) {
         this.executor.flush();
         this.instanceToChannel.values().forEach((var0) -> {
            var0.execute(Channel::stop);
         });
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

   public void tick(boolean var1) {
      if (!var1) {
         this.tickNonPaused();
      }

      this.channelAccess.scheduleTick();
   }

   private void tickNonPaused() {
      ++this.tickCount;
      this.queuedTickableSounds.stream().filter(SoundInstance::canPlaySound).forEach(this::play);
      this.queuedTickableSounds.clear();
      Iterator var1 = this.tickingSounds.iterator();

      while(var1.hasNext()) {
         TickableSoundInstance var2 = (TickableSoundInstance)var1.next();
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

      var1 = this.instanceToChannel.entrySet().iterator();

      SoundInstance var13;
      while(var1.hasNext()) {
         Entry var9 = (Entry)var1.next();
         ChannelAccess.ChannelHandle var11 = (ChannelAccess.ChannelHandle)var9.getValue();
         var13 = (SoundInstance)var9.getKey();
         float var14 = this.options.getSoundSourceVolume(var13.getSource());
         if (var14 <= 0.0F) {
            var11.execute(Channel::stop);
            var1.remove();
         } else if (var11.isStopped()) {
            int var15 = (Integer)this.soundDeleteTime.get(var13);
            if (var15 <= this.tickCount) {
               if (shouldLoopManually(var13)) {
                  this.queuedSounds.put(var13, this.tickCount + var13.getDelay());
               }

               var1.remove();
               LOGGER.debug(MARKER, "Removed channel {} because it's not playing anymore", var11);
               this.soundDeleteTime.remove(var13);

               try {
                  this.instanceBySource.remove(var13.getSource(), var13);
               } catch (RuntimeException var8) {
               }

               if (var13 instanceof TickableSoundInstance) {
                  this.tickingSounds.remove(var13);
               }
            }
         }
      }

      Iterator var10 = this.queuedSounds.entrySet().iterator();

      while(var10.hasNext()) {
         Entry var12 = (Entry)var10.next();
         if (this.tickCount >= (Integer)var12.getValue()) {
            var13 = (SoundInstance)var12.getKey();
            if (var13 instanceof TickableSoundInstance) {
               ((TickableSoundInstance)var13).tick();
            }

            this.play(var13);
            var10.remove();
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
                  float var8 = this.calculateVolume(var1);
                  float var9 = this.calculatePitch(var1);
                  SoundInstance.Attenuation var10 = var1.getAttenuation();
                  boolean var11 = var1.isRelative();
                  if (var8 == 0.0F && !var1.canStartSilent()) {
                     LOGGER.debug(MARKER, "Skipped playing sound {}, volume was zero.", var4.getLocation());
                  } else {
                     Vec3 var12 = new Vec3(var1.getX(), var1.getY(), var1.getZ());
                     boolean var13;
                     if (!this.listeners.isEmpty()) {
                        var13 = var11 || var10 == SoundInstance.Attenuation.NONE || this.listener.getListenerPosition().distanceToSqr(var12) < (double)(var6 * var6);
                        if (var13) {
                           Iterator var14 = this.listeners.iterator();

                           while(var14.hasNext()) {
                              SoundEventListener var15 = (SoundEventListener)var14.next();
                              var15.onPlaySound(var1, var2);
                           }
                        } else {
                           LOGGER.debug(MARKER, "Did not notify listeners of soundEvent: {}, it is too far away to hear", var3);
                        }
                     }

                     if (this.listener.getGain() <= 0.0F) {
                        LOGGER.debug(MARKER, "Skipped playing soundEvent: {}, master volume was zero", var3);
                     } else {
                        var13 = shouldLoopAutomatically(var1);
                        boolean var17 = var4.shouldStream();
                        CompletableFuture var18 = this.channelAccess.createHandle(var4.shouldStream() ? Library.Pool.STREAMING : Library.Pool.STATIC);
                        ChannelAccess.ChannelHandle var16 = (ChannelAccess.ChannelHandle)var18.join();
                        if (var16 == null) {
                           LOGGER.warn("Failed to create new sound handle");
                        } else {
                           LOGGER.debug(MARKER, "Playing sound {} for event {}", var4.getLocation(), var3);
                           this.soundDeleteTime.put(var1, this.tickCount + 20);
                           this.instanceToChannel.put(var1, var16);
                           this.instanceBySource.put(var7, var1);
                           var16.execute((var8x) -> {
                              var8x.setPitch(var9);
                              var8x.setVolume(var8);
                              if (var10 == SoundInstance.Attenuation.LINEAR) {
                                 var8x.linearAttenuation(var6);
                              } else {
                                 var8x.disableAttenuation();
                              }

                              var8x.setLooping(var13 && !var17);
                              var8x.setSelfPosition(var12);
                              var8x.setRelative(var11);
                           });
                           if (!var17) {
                              this.soundBuffers.getCompleteBuffer(var4.getPath()).thenAccept((var1x) -> {
                                 var16.execute((var1) -> {
                                    var1.attachStaticBuffer(var1x);
                                    var1.play();
                                 });
                              });
                           } else {
                              this.soundBuffers.getStream(var4.getPath(), var13).thenAccept((var1x) -> {
                                 var16.execute((var1) -> {
                                    var1.attachBufferStream(var1x);
                                    var1.play();
                                 });
                              });
                           }

                           if (var1 instanceof TickableSoundInstance) {
                              this.tickingSounds.add((TickableSoundInstance)var1);
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
      return Mth.clamp(var1.getVolume() * this.getVolume(var1.getSource()), 0.0F, 1.0F);
   }

   public void pause() {
      if (this.loaded) {
         this.channelAccess.executeOnChannels((var0) -> {
            var0.forEach(Channel::pause);
         });
      }

   }

   public void resume() {
      if (this.loaded) {
         this.channelAccess.executeOnChannels((var0) -> {
            var0.forEach(Channel::unpause);
         });
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
      Iterator var3;
      SoundInstance var4;
      if (var2 != null) {
         var3 = this.instanceBySource.get(var2).iterator();

         while(true) {
            do {
               if (!var3.hasNext()) {
                  return;
               }

               var4 = (SoundInstance)var3.next();
            } while(var1 != null && !var4.getLocation().equals(var1));

            this.stop(var4);
         }
      } else if (var1 == null) {
         this.stopAll();
      } else {
         var3 = this.instanceToChannel.keySet().iterator();

         while(var3.hasNext()) {
            var4 = (SoundInstance)var3.next();
            if (var4.getLocation().equals(var1)) {
               this.stop(var4);
            }
         }
      }

   }

   public String getDebugString() {
      return this.library.getDebugString();
   }
}
