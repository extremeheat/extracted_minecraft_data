package net.minecraft.client.particle;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.AtlasManager;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

public class ParticleResources implements PreparableReloadListener {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final FileToIdConverter PARTICLE_LISTER = FileToIdConverter.json("particles");
   private final Map<ResourceLocation, MutableSpriteSet> spriteSets = Maps.newHashMap();
   private final Int2ObjectMap<ParticleProvider<?>> providers = new Int2ObjectOpenHashMap();
   @Nullable
   private Runnable onReload;

   public ParticleResources() {
      super();
      this.registerProviders();
   }

   public void onReload(Runnable var1) {
      this.onReload = var1;
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
      this.register(ParticleTypes.COPPER_FIRE_FLAME, FlameParticle.Provider::new);
      this.register(ParticleTypes.CRIT, CritParticle.Provider::new);
      this.register(ParticleTypes.CURRENT_DOWN, WaterCurrentDownParticle.Provider::new);
      this.register(ParticleTypes.DAMAGE_INDICATOR, CritParticle.DamageIndicatorProvider::new);
      this.register(ParticleTypes.DRAGON_BREATH, DragonBreathParticle.Provider::new);
      this.register(ParticleTypes.DOLPHIN, SuspendedTownParticle.DolphinSpeedProvider::new);
      this.register(ParticleTypes.DRIPPING_LAVA, DripParticle.LavaHangProvider::new);
      this.register(ParticleTypes.FALLING_LAVA, DripParticle.LavaFallProvider::new);
      this.register(ParticleTypes.LANDING_LAVA, DripParticle.LavaLandProvider::new);
      this.register(ParticleTypes.DRIPPING_WATER, DripParticle.WaterHangProvider::new);
      this.register(ParticleTypes.FALLING_WATER, DripParticle.WaterFallProvider::new);
      this.register(ParticleTypes.DUST, DustParticle.Provider::new);
      this.register(ParticleTypes.DUST_COLOR_TRANSITION, DustColorTransitionParticle.Provider::new);
      this.register(ParticleTypes.EFFECT, SpellParticle.InstantProvider::new);
      this.register(ParticleTypes.ELDER_GUARDIAN, new ElderGuardianParticle.Provider());
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
      this.register(ParticleTypes.DRIPPING_HONEY, DripParticle.HoneyHangProvider::new);
      this.register(ParticleTypes.FALLING_HONEY, DripParticle.HoneyFallProvider::new);
      this.register(ParticleTypes.LANDING_HONEY, DripParticle.HoneyLandProvider::new);
      this.register(ParticleTypes.FALLING_NECTAR, DripParticle.NectarFallProvider::new);
      this.register(ParticleTypes.FALLING_SPORE_BLOSSOM, DripParticle.SporeBlossomFallProvider::new);
      this.register(ParticleTypes.SPORE_BLOSSOM_AIR, SuspendedParticle.SporeBlossomAirProvider::new);
      this.register(ParticleTypes.ASH, AshParticle.Provider::new);
      this.register(ParticleTypes.CRIMSON_SPORE, SuspendedParticle.CrimsonSporeProvider::new);
      this.register(ParticleTypes.WARPED_SPORE, SuspendedParticle.WarpedSporeProvider::new);
      this.register(ParticleTypes.DRIPPING_OBSIDIAN_TEAR, DripParticle.ObsidianTearHangProvider::new);
      this.register(ParticleTypes.FALLING_OBSIDIAN_TEAR, DripParticle.ObsidianTearFallProvider::new);
      this.register(ParticleTypes.LANDING_OBSIDIAN_TEAR, DripParticle.ObsidianTearLandProvider::new);
      this.register(ParticleTypes.REVERSE_PORTAL, ReversePortalParticle.ReversePortalProvider::new);
      this.register(ParticleTypes.WHITE_ASH, WhiteAshParticle.Provider::new);
      this.register(ParticleTypes.SMALL_FLAME, FlameParticle.SmallFlameProvider::new);
      this.register(ParticleTypes.DRIPPING_DRIPSTONE_WATER, DripParticle.DripstoneWaterHangProvider::new);
      this.register(ParticleTypes.FALLING_DRIPSTONE_WATER, DripParticle.DripstoneWaterFallProvider::new);
      this.register(ParticleTypes.CHERRY_LEAVES, FallingLeavesParticle.CherryProvider::new);
      this.register(ParticleTypes.PALE_OAK_LEAVES, FallingLeavesParticle.PaleOakProvider::new);
      this.register(ParticleTypes.TINTED_LEAVES, FallingLeavesParticle.TintedLeavesProvider::new);
      this.register(ParticleTypes.DRIPPING_DRIPSTONE_LAVA, DripParticle.DripstoneLavaHangProvider::new);
      this.register(ParticleTypes.FALLING_DRIPSTONE_LAVA, DripParticle.DripstoneLavaFallProvider::new);
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
      this.register(ParticleTypes.FIREFLY, FireflyParticle.FireflyProvider::new);
   }

   private <T extends ParticleOptions> void register(ParticleType<T> var1, ParticleProvider<T> var2) {
      this.providers.put(BuiltInRegistries.PARTICLE_TYPE.getId(var1), var2);
   }

   private <T extends ParticleOptions> void register(ParticleType<T> var1, SpriteParticleRegistration<T> var2) {
      MutableSpriteSet var3 = new MutableSpriteSet();
      this.spriteSets.put(BuiltInRegistries.PARTICLE_TYPE.getKey(var1), var3);
      this.providers.put(BuiltInRegistries.PARTICLE_TYPE.getId(var1), var2.create(var3));
   }

   public CompletableFuture<Void> reload(PreparableReloadListener.SharedState var1, Executor var2, PreparableReloadListener.PreparationBarrier var3, Executor var4) {
      ResourceManager var5 = var1.resourceManager();
      CompletableFuture var6 = CompletableFuture.supplyAsync(() -> PARTICLE_LISTER.listMatchingResources(var5), var2).thenCompose((var2x) -> {
         ArrayList var3 = new ArrayList(var2x.size());
         var2x.forEach((var3x, var4) -> {
            ResourceLocation var5 = PARTICLE_LISTER.fileToId(var3x);
            var3.add(CompletableFuture.supplyAsync(() -> {
               record 1ParticleDefinition(ResourceLocation id, Optional<List<ResourceLocation>> sprites) {
                  _ParticleDefinition/* $FF was: 1ParticleDefinition*/(ResourceLocation var1, Optional<List<ResourceLocation>> var2) {
                     super();
                     this.id = var1;
                     this.sprites = var2;
                  }
               }

               return new 1ParticleDefinition(var5, this.loadParticleDescription(var5, var4));
            }, var2));
         });
         return Util.sequence(var3);
      });
      CompletableFuture var7 = ((AtlasManager.PendingStitchResults)var1.get(AtlasManager.PENDING_STITCH)).get(AtlasIds.PARTICLES);
      CompletableFuture var10000 = CompletableFuture.allOf(var6, var7);
      Objects.requireNonNull(var3);
      return var10000.thenCompose(var3::wait).thenAcceptAsync((var3x) -> {
         if (this.onReload != null) {
            this.onReload.run();
         }

         ProfilerFiller var4 = Profiler.get();
         var4.push("upload");
         SpriteLoader.Preparations var5 = (SpriteLoader.Preparations)var7.join();
         var4.popPush("bindSpriteSets");
         HashSet var6x = new HashSet();
         TextureAtlasSprite var7x = var5.missing();
         ((List)var6.join()).forEach((var4x) -> {
            Optional var5x = var4x.sprites();
            if (!var5x.isEmpty()) {
               ArrayList var6 = new ArrayList();

               for(ResourceLocation var8 : (List)var5x.get()) {
                  TextureAtlasSprite var9 = var5.getSprite(var8);
                  if (var9 == null) {
                     var6x.add(var8);
                     var6.add(var7x);
                  } else {
                     var6.add(var9);
                  }
               }

               if (var6.isEmpty()) {
                  var6.add(var7x);
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

   public Int2ObjectMap<ParticleProvider<?>> getProviders() {
      return this.providers;
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

      public TextureAtlasSprite first() {
         return (TextureAtlasSprite)this.sprites.getFirst();
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
