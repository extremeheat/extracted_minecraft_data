package net.minecraft.client.particle;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import net.minecraft.client.core.registries.BuiltInClientRegistries;
import net.minecraft.client.core.registries.ClientRegistries;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.AtlasManager;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class ParticleResources implements PreparableReloadListener {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final FileToIdConverter PARTICLE_LISTER = FileToIdConverter.json("particles");
   private static final Map<ResourceKey<ParticleProvider<?>>, MutableSpriteSet> SPRITE_SETS = new HashMap();
   private final Map<ParticleType<?>, ParticleProvider<?>> providers = buildProviderLookup();
   private @Nullable Runnable onReload;

   public ParticleResources() {
      super();
   }

   @VisibleForTesting
   static Map<ParticleType<?>, ParticleProvider<?>> buildProviderLookup() {
      Map<ParticleType<?>, ParticleProvider<?>> result = new Reference2ObjectOpenHashMap();

      for(Holder.Reference<ParticleType<?>> type : BuiltInRegistries.PARTICLE_TYPE.holders()) {
         ResourceKey<ParticleProvider<?>> key = particleTypeKey(type.key());
         ParticleProvider<?> provider = (ParticleProvider)Objects.requireNonNull((ParticleProvider)BuiltInClientRegistries.PARTICLE_PROVIDER.getValue(key), () -> "Particle type " + String.valueOf(key) + " missing corresponding provider registration");
         result.put(type.value(), provider);
      }

      return result;
   }

   public void onReload(final Runnable onReload) {
      this.onReload = onReload;
   }

   public static ParticleProvider<?> bootstrap(final Registry<ParticleProvider<?>> registry) {
      register(registry, ParticleTypes.ANGRY_VILLAGER, HeartParticle.AngryVillagerProvider::new);
      register(registry, ParticleTypes.BLOCK_MARKER, new BlockMarker.Provider());
      register(registry, ParticleTypes.BLOCK, new TerrainParticle.Provider());
      register(registry, ParticleTypes.BUBBLE, BubbleParticle.Provider::new);
      register(registry, ParticleTypes.BUBBLE_COLUMN_UP, BubbleColumnUpParticle.Provider::new);
      register(registry, ParticleTypes.BUBBLE_POP, BubblePopParticle.Provider::new);
      register(registry, ParticleTypes.SULFUR_BUBBLES, SulfurBubbleParticle.Provider::new);
      register(registry, ParticleTypes.NOXIOUS_GAS, NoxiousGasParticle.Provider::new);
      register(registry, ParticleTypes.NOXIOUS_GAS_CLOUD, new NoxiousGasCloudParticle.Provider());
      register(registry, ParticleTypes.GEYSER, new GeyserEruptionParticle.Provider());
      register(registry, ParticleTypes.GEYSER_BASE, GeyserBaseParticle.Provider::new);
      register(registry, ParticleTypes.GEYSER_POOF, GeyserBaseParticle.Provider::new);
      register(registry, ParticleTypes.GEYSER_PLUME, GeyserPlumeParticle.Provider::new);
      register(registry, ParticleTypes.CAMPFIRE_COSY_SMOKE, CampfireSmokeParticle.CosyProvider::new);
      register(registry, ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, CampfireSmokeParticle.SignalProvider::new);
      register(registry, ParticleTypes.CLOUD, PlayerCloudParticle.Provider::new);
      register(registry, ParticleTypes.COMPOSTER, SuspendedTownParticle.ComposterFillProvider::new);
      register(registry, ParticleTypes.COPPER_FIRE_FLAME, FlameParticle.Provider::new);
      register(registry, ParticleTypes.CRIT, CritParticle.Provider::new);
      register(registry, ParticleTypes.CURRENT_DOWN, WaterCurrentDownParticle.Provider::new);
      register(registry, ParticleTypes.DAMAGE_INDICATOR, CritParticle.DamageIndicatorProvider::new);
      register(registry, ParticleTypes.DRAGON_BREATH, DragonBreathParticle.Provider::new);
      register(registry, ParticleTypes.DOLPHIN, SuspendedTownParticle.DolphinSpeedProvider::new);
      register(registry, ParticleTypes.DRIPPING_LAVA, DripParticle.LavaHangProvider::new);
      register(registry, ParticleTypes.FALLING_LAVA, DripParticle.LavaFallProvider::new);
      register(registry, ParticleTypes.LANDING_LAVA, DripParticle.LavaLandProvider::new);
      register(registry, ParticleTypes.DRIPPING_WATER, DripParticle.WaterHangProvider::new);
      register(registry, ParticleTypes.FALLING_WATER, DripParticle.WaterFallProvider::new);
      register(registry, ParticleTypes.DUST, DustParticle.Provider::new);
      register(registry, ParticleTypes.DUST_COLOR_TRANSITION, DustColorTransitionParticle.Provider::new);
      register(registry, ParticleTypes.EFFECT, SpellParticle.InstantProvider::new);
      register(registry, ParticleTypes.ELDER_GUARDIAN, new ElderGuardianParticle.Provider());
      register(registry, ParticleTypes.ENCHANTED_HIT, CritParticle.MagicProvider::new);
      register(registry, ParticleTypes.ENCHANT, FlyTowardsPositionParticle.EnchantProvider::new);
      register(registry, ParticleTypes.END_ROD, EndRodParticle.Provider::new);
      register(registry, ParticleTypes.ENTITY_EFFECT, SpellParticle.MobEffectProvider::new);
      register(registry, ParticleTypes.EXPLOSION_EMITTER, new HugeExplosionSeedParticle.Provider());
      register(registry, ParticleTypes.EXPLOSION, HugeExplosionParticle.Provider::new);
      register(registry, ParticleTypes.SONIC_BOOM, SonicBoomParticle.Provider::new);
      register(registry, ParticleTypes.FALLING_DUST, FallingDustParticle.Provider::new);
      register(registry, ParticleTypes.FREEZING, FreezingParticle.Provider::new);
      register(registry, ParticleTypes.GUST, GustParticle.Provider::new);
      register(registry, ParticleTypes.SMALL_GUST, GustParticle.SmallProvider::new);
      register(registry, ParticleTypes.GUST_EMITTER_LARGE, new GustSeedParticle.Provider(3.0, 7, 0));
      register(registry, ParticleTypes.GUST_EMITTER_SMALL, new GustSeedParticle.Provider(1.0, 3, 2));
      register(registry, ParticleTypes.FIREWORK, FireworkParticles.SparkProvider::new);
      register(registry, ParticleTypes.FISHING, WakeParticle.Provider::new);
      register(registry, ParticleTypes.FLAME, FlameParticle.Provider::new);
      register(registry, ParticleTypes.INFESTED, SpellParticle.Provider::new);
      register(registry, ParticleTypes.SCULK_SOUL, EmissiveRisingParticle.EmissiveProvider::new);
      register(registry, ParticleTypes.SCULK_CHARGE, SculkChargeParticle.Provider::new);
      register(registry, ParticleTypes.SCULK_CHARGE_POP, SculkChargePopParticle.Provider::new);
      register(registry, ParticleTypes.SOUL, EmissiveRisingParticle.Provider::new);
      register(registry, ParticleTypes.SOUL_FIRE_FLAME, FlameParticle.Provider::new);
      register(registry, ParticleTypes.FLASH, FireworkParticles.FlashProvider::new);
      register(registry, ParticleTypes.HAPPY_VILLAGER, SuspendedTownParticle.HappyVillagerProvider::new);
      register(registry, ParticleTypes.HEART, HeartParticle.Provider::new);
      register(registry, ParticleTypes.INSTANT_EFFECT, SpellParticle.InstantProvider::new);
      register(registry, ParticleTypes.ITEM, new BreakingItemParticle.Provider());
      register(registry, ParticleTypes.ITEM_SLIME, new BreakingItemParticle.SlimeProvider());
      register(registry, ParticleTypes.ITEM_COBWEB, new BreakingItemParticle.CobwebProvider());
      register(registry, ParticleTypes.LARGE_SMOKE, LargeSmokeParticle.Provider::new);
      register(registry, ParticleTypes.LAVA, LavaParticle.Provider::new);
      register(registry, ParticleTypes.MYCELIUM, SuspendedTownParticle.Provider::new);
      register(registry, ParticleTypes.NAUTILUS, FlyTowardsPositionParticle.NautilusProvider::new);
      register(registry, ParticleTypes.NOTE, NoteParticle.Provider::new);
      register(registry, ParticleTypes.POOF, ExplodeParticle.Provider::new);
      register(registry, ParticleTypes.PORTAL, PortalParticle.Provider::new);
      register(registry, ParticleTypes.RAIN, WaterDropParticle.Provider::new);
      register(registry, ParticleTypes.SMOKE, SmokeParticle.Provider::new);
      register(registry, ParticleTypes.WHITE_SMOKE, WhiteSmokeParticle.Provider::new);
      register(registry, ParticleTypes.SNEEZE, PlayerCloudParticle.SneezeProvider::new);
      register(registry, ParticleTypes.SNOWFLAKE, SnowflakeParticle.Provider::new);
      register(registry, ParticleTypes.SPIT, SpitParticle.Provider::new);
      register(registry, ParticleTypes.SWEEP_ATTACK, AttackSweepParticle.Provider::new);
      register(registry, ParticleTypes.TOTEM_OF_UNDYING, TotemParticle.Provider::new);
      register(registry, ParticleTypes.SQUID_INK, SquidInkParticle.Provider::new);
      register(registry, ParticleTypes.UNDERWATER, SuspendedParticle.UnderwaterProvider::new);
      register(registry, ParticleTypes.SPLASH, SplashParticle.Provider::new);
      register(registry, ParticleTypes.WITCH, SpellParticle.WitchProvider::new);
      register(registry, ParticleTypes.DRIPPING_HONEY, DripParticle.HoneyHangProvider::new);
      register(registry, ParticleTypes.FALLING_HONEY, DripParticle.HoneyFallProvider::new);
      register(registry, ParticleTypes.LANDING_HONEY, DripParticle.HoneyLandProvider::new);
      register(registry, ParticleTypes.FALLING_NECTAR, DripParticle.NectarFallProvider::new);
      register(registry, ParticleTypes.FALLING_SPORE_BLOSSOM, DripParticle.SporeBlossomFallProvider::new);
      register(registry, ParticleTypes.SPORE_BLOSSOM_AIR, SuspendedParticle.SporeBlossomAirProvider::new);
      register(registry, ParticleTypes.ASH, AshParticle.Provider::new);
      register(registry, ParticleTypes.CRIMSON_SPORE, SuspendedParticle.CrimsonSporeProvider::new);
      register(registry, ParticleTypes.WARPED_SPORE, SuspendedParticle.WarpedSporeProvider::new);
      register(registry, ParticleTypes.DRIPPING_OBSIDIAN_TEAR, DripParticle.ObsidianTearHangProvider::new);
      register(registry, ParticleTypes.FALLING_OBSIDIAN_TEAR, DripParticle.ObsidianTearFallProvider::new);
      register(registry, ParticleTypes.LANDING_OBSIDIAN_TEAR, DripParticle.ObsidianTearLandProvider::new);
      register(registry, ParticleTypes.REVERSE_PORTAL, ReversePortalParticle.ReversePortalProvider::new);
      register(registry, ParticleTypes.WHITE_ASH, WhiteAshParticle.Provider::new);
      register(registry, ParticleTypes.SMALL_FLAME, FlameParticle.SmallFlameProvider::new);
      register(registry, ParticleTypes.DRIPPING_DRIPSTONE_WATER, DripParticle.DripstoneWaterHangProvider::new);
      register(registry, ParticleTypes.FALLING_DRIPSTONE_WATER, DripParticle.DripstoneWaterFallProvider::new);
      register(registry, ParticleTypes.CHERRY_LEAVES, FallingLeavesParticle.CherryProvider::new);
      register(registry, ParticleTypes.PALE_OAK_LEAVES, FallingLeavesParticle.PaleOakProvider::new);
      register(registry, ParticleTypes.RED_POPLAR_LEAVES, FallingLeavesParticle.PoplarProvider::new);
      register(registry, ParticleTypes.ORANGE_POPLAR_LEAVES, FallingLeavesParticle.PoplarProvider::new);
      register(registry, ParticleTypes.YELLOW_POPLAR_LEAVES, FallingLeavesParticle.PoplarProvider::new);
      register(registry, ParticleTypes.TINTED_LEAVES, FallingLeavesParticle.TintedLeavesProvider::new);
      register(registry, ParticleTypes.DRIPPING_DRIPSTONE_LAVA, DripParticle.DripstoneLavaHangProvider::new);
      register(registry, ParticleTypes.FALLING_DRIPSTONE_LAVA, DripParticle.DripstoneLavaFallProvider::new);
      register(registry, ParticleTypes.VIBRATION, VibrationSignalParticle.Provider::new);
      register(registry, ParticleTypes.TRAIL, TrailParticle.Provider::new);
      register(registry, ParticleTypes.PAUSE_MOB_GROWTH, SimpleVerticalParticle.PauseMobGrowthProvider::new);
      register(registry, ParticleTypes.RESET_MOB_GROWTH, SimpleVerticalParticle.ResetMobGrowthProvider::new);
      register(registry, ParticleTypes.GLOW_SQUID_INK, SquidInkParticle.GlowInkProvider::new);
      register(registry, ParticleTypes.GLOW, GlowParticle.GlowSquidProvider::new);
      register(registry, ParticleTypes.WAX_ON, GlowParticle.WaxOnProvider::new);
      register(registry, ParticleTypes.WAX_OFF, GlowParticle.WaxOffProvider::new);
      register(registry, ParticleTypes.ELECTRIC_SPARK, GlowParticle.ElectricSparkProvider::new);
      register(registry, ParticleTypes.SCRAPE, GlowParticle.ScrapeProvider::new);
      register(registry, ParticleTypes.SHRIEK, ShriekParticle.Provider::new);
      register(registry, ParticleTypes.EGG_CRACK, SuspendedTownParticle.EggCrackProvider::new);
      register(registry, ParticleTypes.DUST_PLUME, DustPlumeParticle.Provider::new);
      register(registry, ParticleTypes.TRIAL_SPAWNER_DETECTED_PLAYER, TrialSpawnerDetectionParticle.Provider::new);
      register(registry, ParticleTypes.TRIAL_SPAWNER_DETECTED_PLAYER_OMINOUS, TrialSpawnerDetectionParticle.Provider::new);
      register(registry, ParticleTypes.VAULT_CONNECTION, FlyTowardsPositionParticle.VaultConnectionProvider::new);
      register(registry, ParticleTypes.DUST_PILLAR, new TerrainParticle.DustPillarProvider());
      register(registry, ParticleTypes.RAID_OMEN, SpellParticle.Provider::new);
      register(registry, ParticleTypes.TRIAL_OMEN, SpellParticle.Provider::new);
      register(registry, ParticleTypes.OMINOUS_SPAWNING, FlyStraightTowardsParticle.OminousSpawnProvider::new);
      register(registry, ParticleTypes.BLOCK_CRUMBLE, new TerrainParticle.CrumblingProvider());
      register(registry, ParticleTypes.FIREFLY, FireflyParticle.FireflyProvider::new);
      return register(registry, ParticleTypes.SULFUR_CUBE_GOO, BreakingItemParticle.SulfurCubeProvider::new);
   }

   private static <T extends ParticleOptions> ParticleProvider<?> register(final Registry<ParticleProvider<?>> registry, final ParticleType<T> type, final ParticleProvider<T> provider) {
      return (ParticleProvider)Registry.register(registry, (ResourceKey)particleTypeKey(type), provider);
   }

   private static <T extends ParticleOptions> ParticleProvider<?> register(final Registry<ParticleProvider<?>> registry, final ParticleType<T> type, final SpriteParticleRegistration<T> provider) {
      MutableSpriteSet spriteSet = new MutableSpriteSet();
      ResourceKey<ParticleProvider<?>> key = particleTypeKey(type);
      SPRITE_SETS.put(key, spriteSet);
      return (ParticleProvider)Registry.register(registry, (ResourceKey)key, provider.create(spriteSet));
   }

   private static ResourceKey<ParticleProvider<?>> particleTypeKey(final ResourceKey<ParticleType<?>> key) {
      return ResourceKey.create(ClientRegistries.PARTICLE_PROVIDER, key.identifier());
   }

   private static ResourceKey<ParticleProvider<?>> particleTypeKey(final ParticleType<?> type) {
      return ResourceKey.create(ClientRegistries.PARTICLE_PROVIDER, (Identifier)Objects.requireNonNull(BuiltInRegistries.PARTICLE_TYPE.getKey(type), () -> "Unregistered particle type: " + String.valueOf(type)));
   }

   public CompletableFuture<Void> reload(final PreparableReloadListener.SharedState currentReload, final Executor taskExecutor, final PreparableReloadListener.PreparationBarrier preparationBarrier, final Executor reloadExecutor) {
      ResourceManager manager = currentReload.resourceManager();
      CompletableFuture<List<ParticleDefinition>> spriteSetsToLoad = CompletableFuture.supplyAsync(() -> PARTICLE_LISTER.listMatchingResources(manager), taskExecutor).thenCompose((definitionsToScan) -> {
         List<CompletableFuture<ParticleDefinition>> loadTasks = new ArrayList(definitionsToScan.size());
         definitionsToScan.forEach((resourceId, resource) -> {
            ResourceKey<ParticleProvider<?>> particleKey = ResourceKey.create(ClientRegistries.PARTICLE_PROVIDER, PARTICLE_LISTER.fileToId(resourceId));
            loadTasks.add(CompletableFuture.supplyAsync(() -> {
               record ParticleDefinition(ResourceKey<ParticleProvider<?>> id, Optional<List<Identifier>> sprites) {
                  ParticleDefinition {
                     super();
                  }
               }

               return new ParticleDefinition(particleKey, this.loadParticleDescription(particleKey, resource));
            }, taskExecutor));
         });
         return Util.sequence(loadTasks);
      });
      CompletableFuture<SpriteLoader.Preparations> pendingSprites = ((AtlasManager.PendingStitchResults)currentReload.get(AtlasManager.PENDING_STITCH)).get(AtlasIds.PARTICLES);
      CompletableFuture var10000 = CompletableFuture.allOf(spriteSetsToLoad, pendingSprites);
      Objects.requireNonNull(preparationBarrier);
      return var10000.thenCompose(preparationBarrier::wait).thenAcceptAsync((unused) -> {
         if (this.onReload != null) {
            this.onReload.run();
         }

         ProfilerFiller reloadProfiler = Profiler.get();
         reloadProfiler.push("upload");
         SpriteLoader.Preparations sprites = (SpriteLoader.Preparations)pendingSprites.join();
         reloadProfiler.popPush("bindSpriteSets");
         Set<Identifier> missingSprites = new HashSet();
         TextureAtlasSprite missingSprite = sprites.missing();
         ((List)spriteSetsToLoad.join()).forEach((p) -> {
            Optional<List<Identifier>> spriteIds = p.sprites();
            if (!spriteIds.isEmpty()) {
               List<TextureAtlasSprite> contents = new ArrayList();

               for(Identifier spriteId : (List)spriteIds.get()) {
                  TextureAtlasSprite sprite = sprites.getSprite(spriteId);
                  if (sprite == null) {
                     missingSprites.add(spriteId);
                     contents.add(missingSprite);
                  } else {
                     contents.add(sprite);
                  }
               }

               if (contents.isEmpty()) {
                  contents.add(missingSprite);
               }

               ((MutableSpriteSet)SPRITE_SETS.get(p.id())).rebind(contents);
            }
         });
         if (!missingSprites.isEmpty()) {
            LOGGER.warn("Missing particle sprites: {}", missingSprites.stream().sorted().map(Identifier::toString).collect(Collectors.joining(",")));
         }

         reloadProfiler.pop();
      }, reloadExecutor);
   }

   private Optional<List<Identifier>> loadParticleDescription(final ResourceKey<ParticleProvider<?>> key, final Resource resource) {
      if (!SPRITE_SETS.containsKey(key)) {
         LOGGER.debug("Redundant texture list for particle: {}", key.identifier());
         return Optional.empty();
      } else {
         try {
            Reader reader = resource.openAsReader();

            Optional var5;
            try {
               ParticleDescription description = ParticleDescription.fromJson(GsonHelper.parse(reader));
               var5 = Optional.of(description.getTextures());
            } catch (Throwable var7) {
               if (reader != null) {
                  try {
                     reader.close();
                  } catch (Throwable var6) {
                     var7.addSuppressed(var6);
                  }
               }

               throw var7;
            }

            if (reader != null) {
               reader.close();
            }

            return var5;
         } catch (IOException e) {
            throw new IllegalStateException("Failed to load description for particle " + String.valueOf(key.identifier()), e);
         }
      }
   }

   public <T extends ParticleOptions> @Nullable ParticleProvider<T> getProvider(final T options) {
      return (ParticleProvider)this.providers.get(options.getType());
   }

   private static class MutableSpriteSet implements SpriteSet {
      private List<TextureAtlasSprite> sprites;

      private MutableSpriteSet() {
         super();
      }

      public TextureAtlasSprite get(final int index, final int max) {
         return (TextureAtlasSprite)this.sprites.get(index * (this.sprites.size() - 1) / max);
      }

      public TextureAtlasSprite get(final RandomSource random) {
         return (TextureAtlasSprite)this.sprites.get(random.nextInt(this.sprites.size()));
      }

      public TextureAtlasSprite first() {
         return (TextureAtlasSprite)this.sprites.getFirst();
      }

      public void rebind(final List<TextureAtlasSprite> ids) {
         this.sprites = ImmutableList.copyOf(ids);
      }
   }

   @FunctionalInterface
   private interface SpriteParticleRegistration<T extends ParticleOptions> {
      ParticleProvider<T> create(SpriteSet spriteSet);
   }
}
