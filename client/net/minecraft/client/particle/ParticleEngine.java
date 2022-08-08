package net.minecraft.client.particle;

import com.google.common.collect.EvictingQueue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleGroup;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ParticleEngine implements PreparableReloadListener {
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
      var2.register((ResourceLocation)this.textureAtlas.location(), (AbstractTexture)this.textureAtlas);
      this.level = var1;
      this.textureManager = var2;
      this.registerProviders();
   }

   private void registerProviders() {
      this.register(ParticleTypes.AMBIENT_ENTITY_EFFECT, (SpriteParticleRegistration)(SpellParticle.AmbientMobProvider::new));
      this.register(ParticleTypes.ANGRY_VILLAGER, (SpriteParticleRegistration)(HeartParticle.AngryVillagerProvider::new));
      this.register(ParticleTypes.BLOCK_MARKER, (ParticleProvider)(new BlockMarker.Provider()));
      this.register(ParticleTypes.BLOCK, (ParticleProvider)(new TerrainParticle.Provider()));
      this.register(ParticleTypes.BUBBLE, (SpriteParticleRegistration)(BubbleParticle.Provider::new));
      this.register(ParticleTypes.BUBBLE_COLUMN_UP, (SpriteParticleRegistration)(BubbleColumnUpParticle.Provider::new));
      this.register(ParticleTypes.BUBBLE_POP, (SpriteParticleRegistration)(BubblePopParticle.Provider::new));
      this.register(ParticleTypes.CAMPFIRE_COSY_SMOKE, (SpriteParticleRegistration)(CampfireSmokeParticle.CosyProvider::new));
      this.register(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, (SpriteParticleRegistration)(CampfireSmokeParticle.SignalProvider::new));
      this.register(ParticleTypes.CLOUD, (SpriteParticleRegistration)(PlayerCloudParticle.Provider::new));
      this.register(ParticleTypes.COMPOSTER, (SpriteParticleRegistration)(SuspendedTownParticle.ComposterFillProvider::new));
      this.register(ParticleTypes.CRIT, (SpriteParticleRegistration)(CritParticle.Provider::new));
      this.register(ParticleTypes.CURRENT_DOWN, (SpriteParticleRegistration)(WaterCurrentDownParticle.Provider::new));
      this.register(ParticleTypes.DAMAGE_INDICATOR, (SpriteParticleRegistration)(CritParticle.DamageIndicatorProvider::new));
      this.register(ParticleTypes.DRAGON_BREATH, (SpriteParticleRegistration)(DragonBreathParticle.Provider::new));
      this.register(ParticleTypes.DOLPHIN, (SpriteParticleRegistration)(SuspendedTownParticle.DolphinSpeedProvider::new));
      this.register(ParticleTypes.DRIPPING_LAVA, (SpriteParticleRegistration)(DripParticle.LavaHangProvider::new));
      this.register(ParticleTypes.FALLING_LAVA, (SpriteParticleRegistration)(DripParticle.LavaFallProvider::new));
      this.register(ParticleTypes.LANDING_LAVA, (SpriteParticleRegistration)(DripParticle.LavaLandProvider::new));
      this.register(ParticleTypes.DRIPPING_WATER, (SpriteParticleRegistration)(DripParticle.WaterHangProvider::new));
      this.register(ParticleTypes.FALLING_WATER, (SpriteParticleRegistration)(DripParticle.WaterFallProvider::new));
      this.register(ParticleTypes.DUST, DustParticle.Provider::new);
      this.register(ParticleTypes.DUST_COLOR_TRANSITION, DustColorTransitionParticle.Provider::new);
      this.register(ParticleTypes.EFFECT, (SpriteParticleRegistration)(SpellParticle.Provider::new));
      this.register(ParticleTypes.ELDER_GUARDIAN, (ParticleProvider)(new MobAppearanceParticle.Provider()));
      this.register(ParticleTypes.ENCHANTED_HIT, (SpriteParticleRegistration)(CritParticle.MagicProvider::new));
      this.register(ParticleTypes.ENCHANT, (SpriteParticleRegistration)(EnchantmentTableParticle.Provider::new));
      this.register(ParticleTypes.END_ROD, (SpriteParticleRegistration)(EndRodParticle.Provider::new));
      this.register(ParticleTypes.ENTITY_EFFECT, (SpriteParticleRegistration)(SpellParticle.MobProvider::new));
      this.register(ParticleTypes.EXPLOSION_EMITTER, (ParticleProvider)(new HugeExplosionSeedParticle.Provider()));
      this.register(ParticleTypes.EXPLOSION, (SpriteParticleRegistration)(HugeExplosionParticle.Provider::new));
      this.register(ParticleTypes.SONIC_BOOM, (SpriteParticleRegistration)(SonicBoomParticle.Provider::new));
      this.register(ParticleTypes.FALLING_DUST, FallingDustParticle.Provider::new);
      this.register(ParticleTypes.FIREWORK, (SpriteParticleRegistration)(FireworkParticles.SparkProvider::new));
      this.register(ParticleTypes.FISHING, (SpriteParticleRegistration)(WakeParticle.Provider::new));
      this.register(ParticleTypes.FLAME, (SpriteParticleRegistration)(FlameParticle.Provider::new));
      this.register(ParticleTypes.SCULK_SOUL, (SpriteParticleRegistration)(SoulParticle.EmissiveProvider::new));
      this.register(ParticleTypes.SCULK_CHARGE, SculkChargeParticle.Provider::new);
      this.register(ParticleTypes.SCULK_CHARGE_POP, (SpriteParticleRegistration)(SculkChargePopParticle.Provider::new));
      this.register(ParticleTypes.SOUL, (SpriteParticleRegistration)(SoulParticle.Provider::new));
      this.register(ParticleTypes.SOUL_FIRE_FLAME, (SpriteParticleRegistration)(FlameParticle.Provider::new));
      this.register(ParticleTypes.FLASH, (SpriteParticleRegistration)(FireworkParticles.FlashProvider::new));
      this.register(ParticleTypes.HAPPY_VILLAGER, (SpriteParticleRegistration)(SuspendedTownParticle.HappyVillagerProvider::new));
      this.register(ParticleTypes.HEART, (SpriteParticleRegistration)(HeartParticle.Provider::new));
      this.register(ParticleTypes.INSTANT_EFFECT, (SpriteParticleRegistration)(SpellParticle.InstantProvider::new));
      this.register(ParticleTypes.ITEM, (ParticleProvider)(new BreakingItemParticle.Provider()));
      this.register(ParticleTypes.ITEM_SLIME, (ParticleProvider)(new BreakingItemParticle.SlimeProvider()));
      this.register(ParticleTypes.ITEM_SNOWBALL, (ParticleProvider)(new BreakingItemParticle.SnowballProvider()));
      this.register(ParticleTypes.LARGE_SMOKE, (SpriteParticleRegistration)(LargeSmokeParticle.Provider::new));
      this.register(ParticleTypes.LAVA, (SpriteParticleRegistration)(LavaParticle.Provider::new));
      this.register(ParticleTypes.MYCELIUM, (SpriteParticleRegistration)(SuspendedTownParticle.Provider::new));
      this.register(ParticleTypes.NAUTILUS, (SpriteParticleRegistration)(EnchantmentTableParticle.NautilusProvider::new));
      this.register(ParticleTypes.NOTE, (SpriteParticleRegistration)(NoteParticle.Provider::new));
      this.register(ParticleTypes.POOF, (SpriteParticleRegistration)(ExplodeParticle.Provider::new));
      this.register(ParticleTypes.PORTAL, (SpriteParticleRegistration)(PortalParticle.Provider::new));
      this.register(ParticleTypes.RAIN, (SpriteParticleRegistration)(WaterDropParticle.Provider::new));
      this.register(ParticleTypes.SMOKE, (SpriteParticleRegistration)(SmokeParticle.Provider::new));
      this.register(ParticleTypes.SNEEZE, (SpriteParticleRegistration)(PlayerCloudParticle.SneezeProvider::new));
      this.register(ParticleTypes.SNOWFLAKE, (SpriteParticleRegistration)(SnowflakeParticle.Provider::new));
      this.register(ParticleTypes.SPIT, (SpriteParticleRegistration)(SpitParticle.Provider::new));
      this.register(ParticleTypes.SWEEP_ATTACK, (SpriteParticleRegistration)(AttackSweepParticle.Provider::new));
      this.register(ParticleTypes.TOTEM_OF_UNDYING, (SpriteParticleRegistration)(TotemParticle.Provider::new));
      this.register(ParticleTypes.SQUID_INK, (SpriteParticleRegistration)(SquidInkParticle.Provider::new));
      this.register(ParticleTypes.UNDERWATER, (SpriteParticleRegistration)(SuspendedParticle.UnderwaterProvider::new));
      this.register(ParticleTypes.SPLASH, (SpriteParticleRegistration)(SplashParticle.Provider::new));
      this.register(ParticleTypes.WITCH, (SpriteParticleRegistration)(SpellParticle.WitchProvider::new));
      this.register(ParticleTypes.DRIPPING_HONEY, (SpriteParticleRegistration)(DripParticle.HoneyHangProvider::new));
      this.register(ParticleTypes.FALLING_HONEY, (SpriteParticleRegistration)(DripParticle.HoneyFallProvider::new));
      this.register(ParticleTypes.LANDING_HONEY, (SpriteParticleRegistration)(DripParticle.HoneyLandProvider::new));
      this.register(ParticleTypes.FALLING_NECTAR, (SpriteParticleRegistration)(DripParticle.NectarFallProvider::new));
      this.register(ParticleTypes.FALLING_SPORE_BLOSSOM, (SpriteParticleRegistration)(DripParticle.SporeBlossomFallProvider::new));
      this.register(ParticleTypes.SPORE_BLOSSOM_AIR, (SpriteParticleRegistration)(SuspendedParticle.SporeBlossomAirProvider::new));
      this.register(ParticleTypes.ASH, (SpriteParticleRegistration)(AshParticle.Provider::new));
      this.register(ParticleTypes.CRIMSON_SPORE, (SpriteParticleRegistration)(SuspendedParticle.CrimsonSporeProvider::new));
      this.register(ParticleTypes.WARPED_SPORE, (SpriteParticleRegistration)(SuspendedParticle.WarpedSporeProvider::new));
      this.register(ParticleTypes.DRIPPING_OBSIDIAN_TEAR, (SpriteParticleRegistration)(DripParticle.ObsidianTearHangProvider::new));
      this.register(ParticleTypes.FALLING_OBSIDIAN_TEAR, (SpriteParticleRegistration)(DripParticle.ObsidianTearFallProvider::new));
      this.register(ParticleTypes.LANDING_OBSIDIAN_TEAR, (SpriteParticleRegistration)(DripParticle.ObsidianTearLandProvider::new));
      this.register(ParticleTypes.REVERSE_PORTAL, (SpriteParticleRegistration)(ReversePortalParticle.ReversePortalProvider::new));
      this.register(ParticleTypes.WHITE_ASH, (SpriteParticleRegistration)(WhiteAshParticle.Provider::new));
      this.register(ParticleTypes.SMALL_FLAME, (SpriteParticleRegistration)(FlameParticle.SmallFlameProvider::new));
      this.register(ParticleTypes.DRIPPING_DRIPSTONE_WATER, (SpriteParticleRegistration)(DripParticle.DripstoneWaterHangProvider::new));
      this.register(ParticleTypes.FALLING_DRIPSTONE_WATER, (SpriteParticleRegistration)(DripParticle.DripstoneWaterFallProvider::new));
      this.register(ParticleTypes.DRIPPING_DRIPSTONE_LAVA, (SpriteParticleRegistration)(DripParticle.DripstoneLavaHangProvider::new));
      this.register(ParticleTypes.FALLING_DRIPSTONE_LAVA, (SpriteParticleRegistration)(DripParticle.DripstoneLavaFallProvider::new));
      this.register(ParticleTypes.VIBRATION, VibrationSignalParticle.Provider::new);
      this.register(ParticleTypes.GLOW_SQUID_INK, (SpriteParticleRegistration)(SquidInkParticle.GlowInkProvider::new));
      this.register(ParticleTypes.GLOW, (SpriteParticleRegistration)(GlowParticle.GlowSquidProvider::new));
      this.register(ParticleTypes.WAX_ON, (SpriteParticleRegistration)(GlowParticle.WaxOnProvider::new));
      this.register(ParticleTypes.WAX_OFF, (SpriteParticleRegistration)(GlowParticle.WaxOffProvider::new));
      this.register(ParticleTypes.ELECTRIC_SPARK, (SpriteParticleRegistration)(GlowParticle.ElectricSparkProvider::new));
      this.register(ParticleTypes.SCRAPE, (SpriteParticleRegistration)(GlowParticle.ScrapeProvider::new));
      this.register(ParticleTypes.SHRIEK, ShriekParticle.Provider::new);
   }

   private <T extends ParticleOptions> void register(ParticleType<T> var1, ParticleProvider<T> var2) {
      this.providers.put(Registry.PARTICLE_TYPE.getId(var1), var2);
   }

   private <T extends ParticleOptions> void register(ParticleType<T> var1, SpriteParticleRegistration<T> var2) {
      MutableSpriteSet var3 = new MutableSpriteSet();
      this.spriteSets.put(Registry.PARTICLE_TYPE.getKey(var1), var3);
      this.providers.put(Registry.PARTICLE_TYPE.getId(var1), var2.create(var3));
   }

   public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier var1, ResourceManager var2, ProfilerFiller var3, ProfilerFiller var4, Executor var5, Executor var6) {
      ConcurrentMap var7 = Maps.newConcurrentMap();
      CompletableFuture[] var8 = (CompletableFuture[])Registry.PARTICLE_TYPE.keySet().stream().map((var4x) -> {
         return CompletableFuture.runAsync(() -> {
            this.loadParticleDescription(var2, var4x, var7);
         }, var5);
      }).toArray((var0) -> {
         return new CompletableFuture[var0];
      });
      CompletableFuture var10000 = CompletableFuture.allOf(var8).thenApplyAsync((var4x) -> {
         var3.startTick();
         var3.push("stitching");
         TextureAtlas.Preparations var5 = this.textureAtlas.prepareToStitch(var2, var7.values().stream().flatMap(Collection::stream), var3, 0);
         var3.pop();
         var3.endTick();
         return var5;
      }, var5);
      Objects.requireNonNull(var1);
      return var10000.thenCompose(var1::wait).thenAcceptAsync((var3x) -> {
         this.particles.clear();
         var4.startTick();
         var4.push("upload");
         this.textureAtlas.reload(var3x);
         var4.popPush("bindSpriteSets");
         TextureAtlasSprite var4x = this.textureAtlas.getSprite(MissingTextureAtlasSprite.getLocation());
         var7.forEach((var2, var3) -> {
            ImmutableList var10000;
            if (var3.isEmpty()) {
               var10000 = ImmutableList.of(var4x);
            } else {
               Stream var5 = var3.stream();
               TextureAtlas var10001 = this.textureAtlas;
               Objects.requireNonNull(var10001);
               var10000 = (ImmutableList)var5.map(var10001::getSprite).collect(ImmutableList.toImmutableList());
            }

            ImmutableList var4 = var10000;
            ((MutableSpriteSet)this.spriteSets.get(var2)).rebind(var4);
         });
         var4.pop();
         var4.endTick();
      }, var6);
   }

   public void close() {
      this.textureAtlas.clearTextureData();
   }

   private void loadParticleDescription(ResourceManager var1, ResourceLocation var2, Map<ResourceLocation, List<ResourceLocation>> var3) {
      ResourceLocation var4 = new ResourceLocation(var2.getNamespace(), "particles/" + var2.getPath() + ".json");

      try {
         BufferedReader var5 = var1.openAsReader(var4);

         try {
            ParticleDescription var6 = ParticleDescription.fromJson(GsonHelper.parse((Reader)var5));
            List var7 = var6.getTextures();
            boolean var8 = this.spriteSets.containsKey(var2);
            if (var7 == null) {
               if (var8) {
                  throw new IllegalStateException("Missing texture list for particle " + var2);
               }
            } else {
               if (!var8) {
                  throw new IllegalStateException("Redundant texture list for particle " + var2);
               }

               var3.put(var2, (List)var7.stream().map((var0) -> {
                  return new ResourceLocation(var0.getNamespace(), "particle/" + var0.getPath());
               }).collect(Collectors.toList()));
            }
         } catch (Throwable var10) {
            if (var5 != null) {
               try {
                  var5.close();
               } catch (Throwable var9) {
                  var10.addSuppressed(var9);
               }
            }

            throw var10;
         }

         if (var5 != null) {
            var5.close();
         }

      } catch (IOException var11) {
         throw new IllegalStateException("Failed to load description for particle " + var2, var11);
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
      ParticleProvider var14 = (ParticleProvider)this.providers.get(Registry.PARTICLE_TYPE.getId(var1.getType()));
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
      this.particles.forEach((var1x, var2x) -> {
         this.level.getProfiler().push(var1x.toString());
         this.tickParticleList(var2x);
         this.level.getProfiler().pop();
      });
      if (!this.trackingEmitters.isEmpty()) {
         ArrayList var1 = Lists.newArrayList();
         Iterator var2 = this.trackingEmitters.iterator();

         while(var2.hasNext()) {
            TrackingEmitter var3 = (TrackingEmitter)var2.next();
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
            ((Queue)this.particles.computeIfAbsent(var4.getRenderType(), (var0) -> {
               return EvictingQueue.create(16384);
            })).add(var4);
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
               var3.getParticleGroup().ifPresent((var1x) -> {
                  this.updateCount(var1x, -1);
               });
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

   public void render(PoseStack var1, MultiBufferSource.BufferSource var2, LightTexture var3, Camera var4, float var5) {
      var3.turnOnLightLayer();
      RenderSystem.enableDepthTest();
      PoseStack var6 = RenderSystem.getModelViewStack();
      var6.pushPose();
      var6.mulPoseMatrix(var1.last().pose());
      RenderSystem.applyModelViewMatrix();
      Iterator var7 = RENDER_ORDER.iterator();

      while(true) {
         ParticleRenderType var8;
         Iterable var9;
         do {
            if (!var7.hasNext()) {
               var6.popPose();
               RenderSystem.applyModelViewMatrix();
               RenderSystem.depthMask(true);
               RenderSystem.disableBlend();
               var3.turnOffLightLayer();
               return;
            }

            var8 = (ParticleRenderType)var7.next();
            var9 = (Iterable)this.particles.get(var8);
         } while(var9 == null);

         RenderSystem.setShader(GameRenderer::getParticleShader);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         Tesselator var10 = Tesselator.getInstance();
         BufferBuilder var11 = var10.getBuilder();
         var8.begin(var11, this.textureManager);
         Iterator var12 = var9.iterator();

         while(var12.hasNext()) {
            Particle var13 = (Particle)var12.next();

            try {
               var13.render(var11, var4, var5);
            } catch (Throwable var17) {
               CrashReport var15 = CrashReport.forThrowable(var17, "Rendering Particle");
               CrashReportCategory var16 = var15.addCategory("Particle being rendered");
               Objects.requireNonNull(var13);
               var16.setDetail("Particle", var13::toString);
               Objects.requireNonNull(var8);
               var16.setDetail("Particle Type", var8::toString);
               throw new ReportedException(var15);
            }
         }

         var8.end(var10);
      }
   }

   public void setLevel(@Nullable ClientLevel var1) {
      this.level = var1;
      this.particles.clear();
      this.trackingEmitters.clear();
      this.trackedParticleCounts.clear();
   }

   public void destroy(BlockPos var1, BlockState var2) {
      if (!var2.isAir()) {
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
      if (var3.getRenderShape() != RenderShape.INVISIBLE) {
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

   static {
      RENDER_ORDER = ImmutableList.of(ParticleRenderType.TERRAIN_SHEET, ParticleRenderType.PARTICLE_SHEET_OPAQUE, ParticleRenderType.PARTICLE_SHEET_LIT, ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT, ParticleRenderType.CUSTOM);
   }

   @FunctionalInterface
   private interface SpriteParticleRegistration<T extends ParticleOptions> {
      ParticleProvider<T> create(SpriteSet var1);
   }

   private static class MutableSpriteSet implements SpriteSet {
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
}
