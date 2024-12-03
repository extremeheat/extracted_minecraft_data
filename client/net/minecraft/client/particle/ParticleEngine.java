package net.minecraft.client.particle;

import com.google.common.collect.EvictingQueue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleGroup;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.slf4j.Logger;

public class ParticleEngine implements PreparableReloadListener {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final FileToIdConverter PARTICLE_LISTER = FileToIdConverter.json("particles");
   private static final ResourceLocation PARTICLES_ATLAS_INFO = ResourceLocation.withDefaultNamespace("particles");
   private static final int MAX_PARTICLES_PER_LAYER = 16384;
   private static final List<ParticleRenderType> RENDER_ORDER;
   protected ClientLevel level;
   private final Map<ParticleRenderType, Queue<Particle>> particles = Maps.newIdentityHashMap();
   private final Queue<TrackingEmitter> trackingEmitters = Queues.newArrayDeque();
   private final TextureManager textureManager;
   private final RandomSource random = RandomSource.create();
   private final Int2ObjectMap<ParticleProvider<?>> providers = new Int2ObjectOpenHashMap();
   private final Queue<Particle> particlesToAdd = Queues.newArrayDeque();
   private final Map<ResourceLocation, MutableSpriteSet> spriteSets = Maps.newHashMap();
   private final TextureAtlas textureAtlas;
   private final Object2IntOpenHashMap<ParticleGroup> trackedParticleCounts = new Object2IntOpenHashMap();

   public ParticleEngine(ClientLevel var1, TextureManager var2) {
      super();
      this.textureAtlas = new TextureAtlas(TextureAtlas.LOCATION_PARTICLES);
      var2.register(this.textureAtlas.location(), this.textureAtlas);
      this.level = var1;
      this.textureManager = var2;
      this.registerProviders();
   }

   private void registerProviders() {
      this.register(ParticleTypes.ANGRY_VILLAGER, HeartParticle.AngryVillagerProvider::new);
      this.register(ParticleTypes.BLOCK_MARKER, new BlockMarker.Provider());
      this.register(ParticleTypes.BLOCK, new TerrainParticle.Provider());
      this.register(ParticleTypes.BUBBLE, BubbleParticle.Provider::new);
      this.register(ParticleTypes.BUBBLE_COLUMN_UP, BubbleColumnUpParticle.Provider::new);
      this.register(ParticleTypes.BUBBLE_POP, BubblePopParticle.Provider::new);
      this.register(ParticleTypes.CAMPFIRE_COSY_SMOKE, CampfireSmokeParticle.CosyProvider::new);
      this.register(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, CampfireSmokeParticle.SignalProvider::new);
      this.register(ParticleTypes.CLOUD, PlayerCloudParticle.Provider::new);
      this.register(ParticleTypes.COMPOSTER, SuspendedTownParticle.ComposterFillProvider::new);
      this.register(ParticleTypes.CRIT, CritParticle.Provider::new);
      this.register(ParticleTypes.CURRENT_DOWN, WaterCurrentDownParticle.Provider::new);
      this.register(ParticleTypes.DAMAGE_INDICATOR, CritParticle.DamageIndicatorProvider::new);
      this.register(ParticleTypes.DRAGON_BREATH, DragonBreathParticle.Provider::new);
      this.register(ParticleTypes.DOLPHIN, SuspendedTownParticle.DolphinSpeedProvider::new);
      this.register(ParticleTypes.DRIPPING_LAVA, DripParticle::createLavaHangParticle);
      this.register(ParticleTypes.FALLING_LAVA, DripParticle::createLavaFallParticle);
      this.register(ParticleTypes.LANDING_LAVA, DripParticle::createLavaLandParticle);
      this.register(ParticleTypes.DRIPPING_WATER, DripParticle::createWaterHangParticle);
      this.register(ParticleTypes.FALLING_WATER, DripParticle::createWaterFallParticle);
      this.register(ParticleTypes.DUST, DustParticle.Provider::new);
      this.register(ParticleTypes.DUST_COLOR_TRANSITION, DustColorTransitionParticle.Provider::new);
      this.register(ParticleTypes.EFFECT, SpellParticle.Provider::new);
      this.register(ParticleTypes.ELDER_GUARDIAN, new MobAppearanceParticle.Provider());
      this.register(ParticleTypes.ENCHANTED_HIT, CritParticle.MagicProvider::new);
      this.register(ParticleTypes.ENCHANT, FlyTowardsPositionParticle.EnchantProvider::new);
      this.register(ParticleTypes.END_ROD, EndRodParticle.Provider::new);
      this.register(ParticleTypes.ENTITY_EFFECT, SpellParticle.MobEffectProvider::new);
      this.register(ParticleTypes.EXPLOSION_EMITTER, new HugeExplosionSeedParticle.Provider());
      this.register(ParticleTypes.EXPLOSION, HugeExplosionParticle.Provider::new);
      this.register(ParticleTypes.SONIC_BOOM, SonicBoomParticle.Provider::new);
      this.register(ParticleTypes.FALLING_DUST, FallingDustParticle.Provider::new);
      this.register(ParticleTypes.GUST, GustParticle.Provider::new);
      this.register(ParticleTypes.SMALL_GUST, GustParticle.SmallProvider::new);
      this.register(ParticleTypes.GUST_EMITTER_LARGE, new GustSeedParticle.Provider(3.0, 7, 0));
      this.register(ParticleTypes.GUST_EMITTER_SMALL, new GustSeedParticle.Provider(1.0, 3, 2));
      this.register(ParticleTypes.FIREWORK, FireworkParticles.SparkProvider::new);
      this.register(ParticleTypes.FISHING, WakeParticle.Provider::new);
      this.register(ParticleTypes.FLAME, FlameParticle.Provider::new);
      this.register(ParticleTypes.INFESTED, SpellParticle.Provider::new);
      this.register(ParticleTypes.SCULK_SOUL, SoulParticle.EmissiveProvider::new);
      this.register(ParticleTypes.SCULK_CHARGE, SculkChargeParticle.Provider::new);
      this.register(ParticleTypes.SCULK_CHARGE_POP, SculkChargePopParticle.Provider::new);
      this.register(ParticleTypes.SOUL, SoulParticle.Provider::new);
      this.register(ParticleTypes.SOUL_FIRE_FLAME, FlameParticle.Provider::new);
      this.register(ParticleTypes.FLASH, FireworkParticles.FlashProvider::new);
      this.register(ParticleTypes.HAPPY_VILLAGER, SuspendedTownParticle.HappyVillagerProvider::new);
      this.register(ParticleTypes.HEART, HeartParticle.Provider::new);
      this.register(ParticleTypes.INSTANT_EFFECT, SpellParticle.InstantProvider::new);
      this.register(ParticleTypes.ITEM, new BreakingItemParticle.Provider());
      this.register(ParticleTypes.ITEM_SLIME, new BreakingItemParticle.SlimeProvider());
      this.register(ParticleTypes.ITEM_COBWEB, new BreakingItemParticle.CobwebProvider());
      this.register(ParticleTypes.ITEM_SNOWBALL, new BreakingItemParticle.SnowballProvider());
      this.register(ParticleTypes.LARGE_SMOKE, LargeSmokeParticle.Provider::new);
      this.register(ParticleTypes.LAVA, LavaParticle.Provider::new);
      this.register(ParticleTypes.MYCELIUM, SuspendedTownParticle.Provider::new);
      this.register(ParticleTypes.NAUTILUS, FlyTowardsPositionParticle.NautilusProvider::new);
      this.register(ParticleTypes.NOTE, NoteParticle.Provider::new);
      this.register(ParticleTypes.POOF, ExplodeParticle.Provider::new);
      this.register(ParticleTypes.PORTAL, PortalParticle.Provider::new);
      this.register(ParticleTypes.RAIN, WaterDropParticle.Provider::new);
      this.register(ParticleTypes.SMOKE, SmokeParticle.Provider::new);
      this.register(ParticleTypes.WHITE_SMOKE, WhiteSmokeParticle.Provider::new);
      this.register(ParticleTypes.SNEEZE, PlayerCloudParticle.SneezeProvider::new);
      this.register(ParticleTypes.SNOWFLAKE, SnowflakeParticle.Provider::new);
      this.register(ParticleTypes.SPIT, SpitParticle.Provider::new);
      this.register(ParticleTypes.SWEEP_ATTACK, AttackSweepParticle.Provider::new);
      this.register(ParticleTypes.TOTEM_OF_UNDYING, TotemParticle.Provider::new);
      this.register(ParticleTypes.SQUID_INK, SquidInkParticle.Provider::new);
      this.register(ParticleTypes.UNDERWATER, SuspendedParticle.UnderwaterProvider::new);
      this.register(ParticleTypes.SPLASH, SplashParticle.Provider::new);
      this.register(ParticleTypes.WITCH, SpellParticle.WitchProvider::new);
      this.register(ParticleTypes.DRIPPING_HONEY, DripParticle::createHoneyHangParticle);
      this.register(ParticleTypes.FALLING_HONEY, DripParticle::createHoneyFallParticle);
      this.register(ParticleTypes.LANDING_HONEY, DripParticle::createHoneyLandParticle);
      this.register(ParticleTypes.FALLING_NECTAR, DripParticle::createNectarFallParticle);
      this.register(ParticleTypes.FALLING_SPORE_BLOSSOM, DripParticle::createSporeBlossomFallParticle);
      this.register(ParticleTypes.SPORE_BLOSSOM_AIR, SuspendedParticle.SporeBlossomAirProvider::new);
      this.register(ParticleTypes.ASH, AshParticle.Provider::new);
      this.register(ParticleTypes.CRIMSON_SPORE, SuspendedParticle.CrimsonSporeProvider::new);
      this.register(ParticleTypes.WARPED_SPORE, SuspendedParticle.WarpedSporeProvider::new);
      this.register(ParticleTypes.DRIPPING_OBSIDIAN_TEAR, DripParticle::createObsidianTearHangParticle);
      this.register(ParticleTypes.FALLING_OBSIDIAN_TEAR, DripParticle::createObsidianTearFallParticle);
      this.register(ParticleTypes.LANDING_OBSIDIAN_TEAR, DripParticle::createObsidianTearLandParticle);
      this.register(ParticleTypes.REVERSE_PORTAL, ReversePortalParticle.ReversePortalProvider::new);
      this.register(ParticleTypes.WHITE_ASH, WhiteAshParticle.Provider::new);
      this.register(ParticleTypes.SMALL_FLAME, FlameParticle.SmallFlameProvider::new);
      this.register(ParticleTypes.DRIPPING_DRIPSTONE_WATER, DripParticle::createDripstoneWaterHangParticle);
      this.register(ParticleTypes.FALLING_DRIPSTONE_WATER, DripParticle::createDripstoneWaterFallParticle);
      this.register(ParticleTypes.CHERRY_LEAVES, FallingLeavesParticle.CherryProvider::new);
      this.register(ParticleTypes.PALE_OAK_LEAVES, FallingLeavesParticle.PaleOakProvider::new);
      this.register(ParticleTypes.DRIPPING_DRIPSTONE_LAVA, DripParticle::createDripstoneLavaHangParticle);
      this.register(ParticleTypes.FALLING_DRIPSTONE_LAVA, DripParticle::createDripstoneLavaFallParticle);
      this.register(ParticleTypes.VIBRATION, VibrationSignalParticle.Provider::new);
      this.register(ParticleTypes.TRAIL, TrailParticle.Provider::new);
      this.register(ParticleTypes.GLOW_SQUID_INK, SquidInkParticle.GlowInkProvider::new);
      this.register(ParticleTypes.GLOW, GlowParticle.GlowSquidProvider::new);
      this.register(ParticleTypes.WAX_ON, GlowParticle.WaxOnProvider::new);
      this.register(ParticleTypes.WAX_OFF, GlowParticle.WaxOffProvider::new);
      this.register(ParticleTypes.ELECTRIC_SPARK, GlowParticle.ElectricSparkProvider::new);
      this.register(ParticleTypes.SCRAPE, GlowParticle.ScrapeProvider::new);
      this.register(ParticleTypes.SHRIEK, ShriekParticle.Provider::new);
      this.register(ParticleTypes.EGG_CRACK, SuspendedTownParticle.EggCrackProvider::new);
      this.register(ParticleTypes.DUST_PLUME, DustPlumeParticle.Provider::new);
      this.register(ParticleTypes.TRIAL_SPAWNER_DETECTED_PLAYER, TrialSpawnerDetectionParticle.Provider::new);
      this.register(ParticleTypes.TRIAL_SPAWNER_DETECTED_PLAYER_OMINOUS, TrialSpawnerDetectionParticle.Provider::new);
      this.register(ParticleTypes.VAULT_CONNECTION, FlyTowardsPositionParticle.VaultConnectionProvider::new);
      this.register(ParticleTypes.DUST_PILLAR, new TerrainParticle.DustPillarProvider());
      this.register(ParticleTypes.RAID_OMEN, SpellParticle.Provider::new);
      this.register(ParticleTypes.TRIAL_OMEN, SpellParticle.Provider::new);
      this.register(ParticleTypes.OMINOUS_SPAWNING, FlyStraightTowardsParticle.OminousSpawnProvider::new);
      this.register(ParticleTypes.BLOCK_CRUMBLE, new TerrainParticle.CrumblingProvider());
   }

   private <T extends ParticleOptions> void register(ParticleType<T> var1, ParticleProvider<T> var2) {
      this.providers.put(BuiltInRegistries.PARTICLE_TYPE.getId(var1), var2);
   }

   private <T extends ParticleOptions> void register(ParticleType<T> var1, ParticleProvider.Sprite<T> var2) {
      this.register(var1, (SpriteParticleRegistration)((var1x) -> (var2x, var3, var4, var6, var8, var10, var12, var14) -> {
            TextureSheetParticle var16 = var2.createParticle(var2x, var3, var4, var6, var8, var10, var12, var14);
            if (var16 != null) {
               var16.pickSprite(var1x);
            }

            return var16;
         }));
   }

   private <T extends ParticleOptions> void register(ParticleType<T> var1, SpriteParticleRegistration<T> var2) {
      MutableSpriteSet var3 = new MutableSpriteSet();
      this.spriteSets.put(BuiltInRegistries.PARTICLE_TYPE.getKey(var1), var3);
      this.providers.put(BuiltInRegistries.PARTICLE_TYPE.getId(var1), var2.create(var3));
   }

   public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier var1, ResourceManager var2, Executor var3, Executor var4) {
      CompletableFuture var5 = CompletableFuture.supplyAsync(() -> PARTICLE_LISTER.listMatchingResources(var2), var3).thenCompose((var2x) -> {
         ArrayList var3x = new ArrayList(var2x.size());
         var2x.forEach((var3xx, var4) -> {
            ResourceLocation var5 = PARTICLE_LISTER.fileToId(var3xx);
            var3x.add(CompletableFuture.supplyAsync(() -> {
               record 1ParticleDefinition(ResourceLocation id, Optional<List<ResourceLocation>> sprites) {
                  _ParticleDefinition/* $FF was: 1ParticleDefinition*/(ResourceLocation var1, Optional<List<ResourceLocation>> var2) {
                     super();
                     this.id = var1;
                     this.sprites = var2;
                  }
               }

               return new 1ParticleDefinition(var5, this.loadParticleDescription(var5, var4));
            }, var3));
         });
         return Util.sequence(var3x);
      });
      CompletableFuture var6 = SpriteLoader.create(this.textureAtlas).loadAndStitch(var2, PARTICLES_ATLAS_INFO, 0, var3).thenCompose(SpriteLoader.Preparations::waitForUpload);
      CompletableFuture var10000 = CompletableFuture.allOf(var6, var5);
      Objects.requireNonNull(var1);
      return var10000.thenCompose(var1::wait).thenAcceptAsync((var3x) -> {
         this.clearParticles();
         ProfilerFiller var4 = Profiler.get();
         var4.push("upload");
         SpriteLoader.Preparations var5x = (SpriteLoader.Preparations)var6.join();
         this.textureAtlas.upload(var5x);
         var4.popPush("bindSpriteSets");
         HashSet var6x = new HashSet();
         TextureAtlasSprite var7 = var5x.missing();
         ((List)var5.join()).forEach((var4x) -> {
            Optional var5 = var4x.sprites();
            if (!var5.isEmpty()) {
               ArrayList var6 = new ArrayList();

               for(ResourceLocation var8 : (List)var5.get()) {
                  TextureAtlasSprite var9 = (TextureAtlasSprite)var5x.regions().get(var8);
                  if (var9 == null) {
                     var6x.add(var8);
                     var6.add(var7);
                  } else {
                     var6.add(var9);
                  }
               }

               if (var6.isEmpty()) {
                  var6.add(var7);
               }

               ((MutableSpriteSet)this.spriteSets.get(var4x.id())).rebind(var6);
            }
         });
         if (!var6x.isEmpty()) {
            LOGGER.warn("Missing particle sprites: {}", var6x.stream().sorted().map(ResourceLocation::toString).collect(Collectors.joining(",")));
         }

         var4.pop();
      }, var4);
   }

   public void close() {
      this.textureAtlas.clearTextureData();
   }

   private Optional<List<ResourceLocation>> loadParticleDescription(ResourceLocation var1, Resource var2) {
      if (!this.spriteSets.containsKey(var1)) {
         LOGGER.debug("Redundant texture list for particle: {}", var1);
         return Optional.empty();
      } else {
         try {
            BufferedReader var3 = var2.openAsReader();

            Optional var5;
            try {
               ParticleDescription var4 = ParticleDescription.fromJson(GsonHelper.parse((Reader)var3));
               var5 = Optional.of(var4.getTextures());
            } catch (Throwable var7) {
               if (var3 != null) {
                  try {
                     ((Reader)var3).close();
                  } catch (Throwable var6) {
                     var7.addSuppressed(var6);
                  }
               }

               throw var7;
            }

            if (var3 != null) {
               ((Reader)var3).close();
            }

            return var5;
         } catch (IOException var8) {
            throw new IllegalStateException("Failed to load description for particle " + String.valueOf(var1), var8);
         }
      }
   }

   public void createTrackingEmitter(Entity var1, ParticleOptions var2) {
      this.trackingEmitters.add(new TrackingEmitter(this.level, var1, var2));
   }

   public void createTrackingEmitter(Entity var1, ParticleOptions var2, int var3) {
      this.trackingEmitters.add(new TrackingEmitter(this.level, var1, var2, var3));
   }

   @Nullable
   public Particle createParticle(ParticleOptions var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      Particle var14 = this.makeParticle(var1, var2, var4, var6, var8, var10, var12);
      if (var14 != null) {
         this.add(var14);
         return var14;
      } else {
         return null;
      }
   }

   @Nullable
   private <T extends ParticleOptions> Particle makeParticle(T var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      ParticleProvider var14 = (ParticleProvider)this.providers.get(BuiltInRegistries.PARTICLE_TYPE.getId(var1.getType()));
      return var14 == null ? null : var14.createParticle(var1, this.level, var2, var4, var6, var8, var10, var12);
   }

   public void add(Particle var1) {
      Optional var2 = var1.getParticleGroup();
      if (var2.isPresent()) {
         if (this.hasSpaceInParticleLimit((ParticleGroup)var2.get())) {
            this.particlesToAdd.add(var1);
            this.updateCount((ParticleGroup)var2.get(), 1);
         }
      } else {
         this.particlesToAdd.add(var1);
      }

   }

   public void tick() {
      this.particles.forEach((var1x, var2) -> {
         Profiler.get().push(var1x.toString());
         this.tickParticleList(var2);
         Profiler.get().pop();
      });
      if (!this.trackingEmitters.isEmpty()) {
         ArrayList var1 = Lists.newArrayList();

         for(TrackingEmitter var3 : this.trackingEmitters) {
            var3.tick();
            if (!var3.isAlive()) {
               var1.add(var3);
            }
         }

         this.trackingEmitters.removeAll(var1);
      }

      Particle var4;
      if (!this.particlesToAdd.isEmpty()) {
         while((var4 = (Particle)this.particlesToAdd.poll()) != null) {
            ((Queue)this.particles.computeIfAbsent(var4.getRenderType(), (var0) -> EvictingQueue.create(16384))).add(var4);
         }
      }

   }

   private void tickParticleList(Collection<Particle> var1) {
      if (!var1.isEmpty()) {
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            Particle var3 = (Particle)var2.next();
            this.tickParticle(var3);
            if (!var3.isAlive()) {
               var3.getParticleGroup().ifPresent((var1x) -> this.updateCount(var1x, -1));
               var2.remove();
            }
         }
      }

   }

   private void updateCount(ParticleGroup var1, int var2) {
      this.trackedParticleCounts.addTo(var1, var2);
   }

   private void tickParticle(Particle var1) {
      try {
         var1.tick();
      } catch (Throwable var5) {
         CrashReport var3 = CrashReport.forThrowable(var5, "Ticking Particle");
         CrashReportCategory var4 = var3.addCategory("Particle being ticked");
         Objects.requireNonNull(var1);
         var4.setDetail("Particle", var1::toString);
         ParticleRenderType var10002 = var1.getRenderType();
         Objects.requireNonNull(var10002);
         var4.setDetail("Particle Type", var10002::toString);
         throw new ReportedException(var3);
      }
   }

   public void render(Camera var1, float var2, MultiBufferSource.BufferSource var3) {
      for(ParticleRenderType var5 : RENDER_ORDER) {
         Queue var6 = (Queue)this.particles.get(var5);
         if (var6 != null && !var6.isEmpty()) {
            renderParticleType(var1, var2, var3, var5, var6);
         }
      }

      Queue var7 = (Queue)this.particles.get(ParticleRenderType.CUSTOM);
      if (var7 != null && !var7.isEmpty()) {
         renderCustomParticles(var1, var2, var3, var7);
      }

      var3.endBatch();
   }

   private static void renderParticleType(Camera var0, float var1, MultiBufferSource.BufferSource var2, ParticleRenderType var3, Queue<Particle> var4) {
      VertexConsumer var5 = var2.getBuffer((RenderType)Objects.requireNonNull(var3.renderType()));

      for(Particle var7 : var4) {
         try {
            var7.render(var5, var0, var1);
         } catch (Throwable var11) {
            CrashReport var9 = CrashReport.forThrowable(var11, "Rendering Particle");
            CrashReportCategory var10 = var9.addCategory("Particle being rendered");
            Objects.requireNonNull(var7);
            var10.setDetail("Particle", var7::toString);
            Objects.requireNonNull(var3);
            var10.setDetail("Particle Type", var3::toString);
            throw new ReportedException(var9);
         }
      }

   }

   private static void renderCustomParticles(Camera var0, float var1, MultiBufferSource.BufferSource var2, Queue<Particle> var3) {
      PoseStack var4 = new PoseStack();

      for(Particle var6 : var3) {
         try {
            var6.renderCustom(var4, var2, var0, var1);
         } catch (Throwable var10) {
            CrashReport var8 = CrashReport.forThrowable(var10, "Rendering Particle");
            CrashReportCategory var9 = var8.addCategory("Particle being rendered");
            Objects.requireNonNull(var6);
            var9.setDetail("Particle", var6::toString);
            var9.setDetail("Particle Type", "Custom");
            throw new ReportedException(var8);
         }
      }

   }

   public void setLevel(@Nullable ClientLevel var1) {
      this.level = var1;
      this.clearParticles();
      this.trackingEmitters.clear();
   }

   public void destroy(BlockPos var1, BlockState var2) {
      if (!var2.isAir() && var2.shouldSpawnTerrainParticles()) {
         VoxelShape var3 = var2.getShape(this.level, var1);
         double var4 = 0.25;
         var3.forAllBoxes((var3x, var5, var7, var9, var11, var13) -> {
            double var15 = Math.min(1.0, var9 - var3x);
            double var17 = Math.min(1.0, var11 - var5);
            double var19 = Math.min(1.0, var13 - var7);
            int var21 = Math.max(2, Mth.ceil(var15 / 0.25));
            int var22 = Math.max(2, Mth.ceil(var17 / 0.25));
            int var23 = Math.max(2, Mth.ceil(var19 / 0.25));

            for(int var24 = 0; var24 < var21; ++var24) {
               for(int var25 = 0; var25 < var22; ++var25) {
                  for(int var26 = 0; var26 < var23; ++var26) {
                     double var27 = ((double)var24 + 0.5) / (double)var21;
                     double var29 = ((double)var25 + 0.5) / (double)var22;
                     double var31 = ((double)var26 + 0.5) / (double)var23;
                     double var33 = var27 * var15 + var3x;
                     double var35 = var29 * var17 + var5;
                     double var37 = var31 * var19 + var7;
                     this.add(new TerrainParticle(this.level, (double)var1.getX() + var33, (double)var1.getY() + var35, (double)var1.getZ() + var37, var27 - 0.5, var29 - 0.5, var31 - 0.5, var2, var1));
                  }
               }
            }

         });
      }
   }

   public void crack(BlockPos var1, Direction var2) {
      BlockState var3 = this.level.getBlockState(var1);
      if (var3.getRenderShape() != RenderShape.INVISIBLE && var3.shouldSpawnTerrainParticles()) {
         int var4 = var1.getX();
         int var5 = var1.getY();
         int var6 = var1.getZ();
         float var7 = 0.1F;
         AABB var8 = var3.getShape(this.level, var1).bounds();
         double var9 = (double)var4 + this.random.nextDouble() * (var8.maxX - var8.minX - 0.20000000298023224) + 0.10000000149011612 + var8.minX;
         double var11 = (double)var5 + this.random.nextDouble() * (var8.maxY - var8.minY - 0.20000000298023224) + 0.10000000149011612 + var8.minY;
         double var13 = (double)var6 + this.random.nextDouble() * (var8.maxZ - var8.minZ - 0.20000000298023224) + 0.10000000149011612 + var8.minZ;
         if (var2 == Direction.DOWN) {
            var11 = (double)var5 + var8.minY - 0.10000000149011612;
         }

         if (var2 == Direction.UP) {
            var11 = (double)var5 + var8.maxY + 0.10000000149011612;
         }

         if (var2 == Direction.NORTH) {
            var13 = (double)var6 + var8.minZ - 0.10000000149011612;
         }

         if (var2 == Direction.SOUTH) {
            var13 = (double)var6 + var8.maxZ + 0.10000000149011612;
         }

         if (var2 == Direction.WEST) {
            var9 = (double)var4 + var8.minX - 0.10000000149011612;
         }

         if (var2 == Direction.EAST) {
            var9 = (double)var4 + var8.maxX + 0.10000000149011612;
         }

         this.add((new TerrainParticle(this.level, var9, var11, var13, 0.0, 0.0, 0.0, var3, var1)).setPower(0.2F).scale(0.6F));
      }
   }

   public String countParticles() {
      return String.valueOf(this.particles.values().stream().mapToInt(Collection::size).sum());
   }

   private boolean hasSpaceInParticleLimit(ParticleGroup var1) {
      return this.trackedParticleCounts.getInt(var1) < var1.getLimit();
   }

   private void clearParticles() {
      this.particles.clear();
      this.particlesToAdd.clear();
      this.trackingEmitters.clear();
      this.trackedParticleCounts.clear();
   }

   static {
      RENDER_ORDER = List.of(ParticleRenderType.TERRAIN_SHEET, ParticleRenderType.PARTICLE_SHEET_OPAQUE, ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT);
   }

   static class MutableSpriteSet implements SpriteSet {
      private List<TextureAtlasSprite> sprites;

      MutableSpriteSet() {
         super();
      }

      public TextureAtlasSprite get(int var1, int var2) {
         return (TextureAtlasSprite)this.sprites.get(var1 * (this.sprites.size() - 1) / var2);
      }

      public TextureAtlasSprite get(RandomSource var1) {
         return (TextureAtlasSprite)this.sprites.get(var1.nextInt(this.sprites.size()));
      }

      public void rebind(List<TextureAtlasSprite> var1) {
         this.sprites = ImmutableList.copyOf(var1);
      }
   }

   @FunctionalInterface
   interface SpriteParticleRegistration<T extends ParticleOptions> {
      ParticleProvider<T> create(SpriteSet var1);
   }
}
