package net.minecraft.client.renderer.entity;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.logging.LogUtils;
import java.util.Map;
import net.minecraft.client.model.SquidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;

public class EntityRenderers {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final String DEFAULT_PLAYER_MODEL = "default";
   private static final Map<EntityType<?>, EntityRendererProvider<?>> PROVIDERS = Maps.newHashMap();
   private static final Map<String, EntityRendererProvider<AbstractClientPlayer>> PLAYER_PROVIDERS = ImmutableMap.of(
      "default", (EntityRendererProvider<>)var0 -> new PlayerRenderer(var0, false), "slim", (EntityRendererProvider<>)var0 -> new PlayerRenderer(var0, true)
   );

   public EntityRenderers() {
      super();
   }

   private static <T extends Entity> void register(EntityType<? extends T> var0, EntityRendererProvider<T> var1) {
      PROVIDERS.put(var0, var1);
   }

   public static Map<EntityType<?>, EntityRenderer<?>> createEntityRenderers(EntityRendererProvider.Context var0) {
      Builder var1 = ImmutableMap.builder();
      PROVIDERS.forEach((var2, var3) -> {
         try {
            var1.put(var2, var3.create(var0));
         } catch (Exception var5) {
            throw new IllegalArgumentException("Failed to create model for " + BuiltInRegistries.ENTITY_TYPE.getKey(var2), var5);
         }
      });
      return var1.build();
   }

   public static Map<String, EntityRenderer<? extends Player>> createPlayerRenderers(EntityRendererProvider.Context var0) {
      Builder var1 = ImmutableMap.builder();
      PLAYER_PROVIDERS.forEach((var2, var3) -> {
         try {
            var1.put(var2, var3.create(var0));
         } catch (Exception var5) {
            throw new IllegalArgumentException("Failed to create player model for " + var2, var5);
         }
      });
      return var1.build();
   }

   public static boolean validateRegistrations() {
      boolean var0 = true;

      for(EntityType var2 : BuiltInRegistries.ENTITY_TYPE) {
         if (var2 != EntityType.PLAYER && !PROVIDERS.containsKey(var2)) {
            LOGGER.warn("No renderer registered for {}", BuiltInRegistries.ENTITY_TYPE.getKey(var2));
            var0 = false;
         }
      }

      return !var0;
   }

   static {
      register(EntityType.ALLAY, AllayRenderer::new);
      register(EntityType.AREA_EFFECT_CLOUD, NoopRenderer::new);
      register(EntityType.ARMOR_STAND, ArmorStandRenderer::new);
      register(EntityType.ARROW, TippableArrowRenderer::new);
      register(EntityType.AXOLOTL, AxolotlRenderer::new);
      register(EntityType.BAT, BatRenderer::new);
      register(EntityType.BEE, BeeRenderer::new);
      register(EntityType.BLAZE, BlazeRenderer::new);
      register(EntityType.BOAT, var0 -> new BoatRenderer(var0, false));
      register(EntityType.CAT, CatRenderer::new);
      register(EntityType.CAMEL, var0 -> new CamelRenderer(var0, ModelLayers.CAMEL));
      register(EntityType.CAVE_SPIDER, CaveSpiderRenderer::new);
      register(EntityType.CHEST_BOAT, var0 -> new BoatRenderer(var0, true));
      register(EntityType.CHEST_MINECART, var0 -> new MinecartRenderer<>(var0, ModelLayers.CHEST_MINECART));
      register(EntityType.CHICKEN, ChickenRenderer::new);
      register(EntityType.COD, CodRenderer::new);
      register(EntityType.COMMAND_BLOCK_MINECART, var0 -> new MinecartRenderer<>(var0, ModelLayers.COMMAND_BLOCK_MINECART));
      register(EntityType.COW, CowRenderer::new);
      register(EntityType.CREEPER, CreeperRenderer::new);
      register(EntityType.DOLPHIN, DolphinRenderer::new);
      register(EntityType.DONKEY, var0 -> new ChestedHorseRenderer<>(var0, 0.87F, ModelLayers.DONKEY));
      register(EntityType.DRAGON_FIREBALL, DragonFireballRenderer::new);
      register(EntityType.DROWNED, DrownedRenderer::new);
      register(EntityType.EGG, ThrownItemRenderer::new);
      register(EntityType.ELDER_GUARDIAN, ElderGuardianRenderer::new);
      register(EntityType.ENDERMAN, EndermanRenderer::new);
      register(EntityType.ENDERMITE, EndermiteRenderer::new);
      register(EntityType.ENDER_DRAGON, EnderDragonRenderer::new);
      register(EntityType.ENDER_PEARL, ThrownItemRenderer::new);
      register(EntityType.END_CRYSTAL, EndCrystalRenderer::new);
      register(EntityType.EVOKER, EvokerRenderer::new);
      register(EntityType.EVOKER_FANGS, EvokerFangsRenderer::new);
      register(EntityType.EXPERIENCE_BOTTLE, ThrownItemRenderer::new);
      register(EntityType.EXPERIENCE_ORB, ExperienceOrbRenderer::new);
      register(EntityType.EYE_OF_ENDER, var0 -> new ThrownItemRenderer<>(var0, 1.0F, true));
      register(EntityType.FALLING_BLOCK, FallingBlockRenderer::new);
      register(EntityType.FIREBALL, var0 -> new ThrownItemRenderer<>(var0, 3.0F, true));
      register(EntityType.FIREWORK_ROCKET, FireworkEntityRenderer::new);
      register(EntityType.FISHING_BOBBER, FishingHookRenderer::new);
      register(EntityType.FOX, FoxRenderer::new);
      register(EntityType.FROG, FrogRenderer::new);
      register(EntityType.FURNACE_MINECART, var0 -> new MinecartRenderer<>(var0, ModelLayers.FURNACE_MINECART));
      register(EntityType.GHAST, GhastRenderer::new);
      register(EntityType.GIANT, var0 -> new GiantMobRenderer(var0, 6.0F));
      register(EntityType.GLOW_ITEM_FRAME, ItemFrameRenderer::new);
      register(EntityType.GLOW_SQUID, var0 -> new GlowSquidRenderer(var0, new SquidModel<>(var0.bakeLayer(ModelLayers.GLOW_SQUID))));
      register(EntityType.GOAT, GoatRenderer::new);
      register(EntityType.GUARDIAN, GuardianRenderer::new);
      register(EntityType.HOGLIN, HoglinRenderer::new);
      register(EntityType.HOPPER_MINECART, var0 -> new MinecartRenderer<>(var0, ModelLayers.HOPPER_MINECART));
      register(EntityType.HORSE, HorseRenderer::new);
      register(EntityType.HUSK, HuskRenderer::new);
      register(EntityType.ILLUSIONER, IllusionerRenderer::new);
      register(EntityType.IRON_GOLEM, IronGolemRenderer::new);
      register(EntityType.ITEM, ItemEntityRenderer::new);
      register(EntityType.ITEM_FRAME, ItemFrameRenderer::new);
      register(EntityType.LEASH_KNOT, LeashKnotRenderer::new);
      register(EntityType.LIGHTNING_BOLT, LightningBoltRenderer::new);
      register(EntityType.LLAMA, var0 -> new LlamaRenderer(var0, ModelLayers.LLAMA));
      register(EntityType.LLAMA_SPIT, LlamaSpitRenderer::new);
      register(EntityType.MAGMA_CUBE, MagmaCubeRenderer::new);
      register(EntityType.MARKER, NoopRenderer::new);
      register(EntityType.MINECART, var0 -> new MinecartRenderer<>(var0, ModelLayers.MINECART));
      register(EntityType.MOOSHROOM, MushroomCowRenderer::new);
      register(EntityType.MULE, var0 -> new ChestedHorseRenderer<>(var0, 0.92F, ModelLayers.MULE));
      register(EntityType.OCELOT, OcelotRenderer::new);
      register(EntityType.PAINTING, PaintingRenderer::new);
      register(EntityType.PANDA, PandaRenderer::new);
      register(EntityType.PARROT, ParrotRenderer::new);
      register(EntityType.PHANTOM, PhantomRenderer::new);
      register(EntityType.PIG, PigRenderer::new);
      register(EntityType.PIGLIN, var0 -> new PiglinRenderer(var0, ModelLayers.PIGLIN, ModelLayers.PIGLIN_INNER_ARMOR, ModelLayers.PIGLIN_OUTER_ARMOR, false));
      register(
         EntityType.PIGLIN_BRUTE,
         var0 -> new PiglinRenderer(var0, ModelLayers.PIGLIN_BRUTE, ModelLayers.PIGLIN_BRUTE_INNER_ARMOR, ModelLayers.PIGLIN_BRUTE_OUTER_ARMOR, false)
      );
      register(EntityType.PILLAGER, PillagerRenderer::new);
      register(EntityType.POLAR_BEAR, PolarBearRenderer::new);
      register(EntityType.POTION, ThrownItemRenderer::new);
      register(EntityType.PUFFERFISH, PufferfishRenderer::new);
      register(EntityType.RABBIT, RabbitRenderer::new);
      register(EntityType.RAVAGER, RavagerRenderer::new);
      register(EntityType.SALMON, SalmonRenderer::new);
      register(EntityType.SHEEP, SheepRenderer::new);
      register(EntityType.SHULKER, ShulkerRenderer::new);
      register(EntityType.SHULKER_BULLET, ShulkerBulletRenderer::new);
      register(EntityType.SILVERFISH, SilverfishRenderer::new);
      register(EntityType.SKELETON, SkeletonRenderer::new);
      register(EntityType.SKELETON_HORSE, var0 -> new UndeadHorseRenderer(var0, ModelLayers.SKELETON_HORSE));
      register(EntityType.SLIME, SlimeRenderer::new);
      register(EntityType.SMALL_FIREBALL, var0 -> new ThrownItemRenderer<>(var0, 0.75F, true));
      register(EntityType.SNOWBALL, ThrownItemRenderer::new);
      register(EntityType.SNOW_GOLEM, SnowGolemRenderer::new);
      register(EntityType.SPAWNER_MINECART, var0 -> new MinecartRenderer<>(var0, ModelLayers.SPAWNER_MINECART));
      register(EntityType.SPECTRAL_ARROW, SpectralArrowRenderer::new);
      register(EntityType.SPIDER, SpiderRenderer::new);
      register(EntityType.SQUID, var0 -> new SquidRenderer<>(var0, new SquidModel<>(var0.bakeLayer(ModelLayers.SQUID))));
      register(EntityType.STRAY, StrayRenderer::new);
      register(EntityType.STRIDER, StriderRenderer::new);
      register(EntityType.TADPOLE, TadpoleRenderer::new);
      register(EntityType.TNT, TntRenderer::new);
      register(EntityType.TNT_MINECART, TntMinecartRenderer::new);
      register(EntityType.TRADER_LLAMA, var0 -> new LlamaRenderer(var0, ModelLayers.TRADER_LLAMA));
      register(EntityType.TRIDENT, ThrownTridentRenderer::new);
      register(EntityType.TROPICAL_FISH, TropicalFishRenderer::new);
      register(EntityType.TURTLE, TurtleRenderer::new);
      register(EntityType.VEX, VexRenderer::new);
      register(EntityType.VILLAGER, VillagerRenderer::new);
      register(EntityType.VINDICATOR, VindicatorRenderer::new);
      register(EntityType.WARDEN, WardenRenderer::new);
      register(EntityType.WANDERING_TRADER, WanderingTraderRenderer::new);
      register(EntityType.WITCH, WitchRenderer::new);
      register(EntityType.WITHER, WitherBossRenderer::new);
      register(EntityType.WITHER_SKELETON, WitherSkeletonRenderer::new);
      register(EntityType.WITHER_SKULL, WitherSkullRenderer::new);
      register(EntityType.WOLF, WolfRenderer::new);
      register(EntityType.ZOGLIN, ZoglinRenderer::new);
      register(EntityType.ZOMBIE, ZombieRenderer::new);
      register(EntityType.ZOMBIE_HORSE, var0 -> new UndeadHorseRenderer(var0, ModelLayers.ZOMBIE_HORSE));
      register(EntityType.ZOMBIE_VILLAGER, ZombieVillagerRenderer::new);
      register(
         EntityType.ZOMBIFIED_PIGLIN,
         var0 -> new PiglinRenderer(
               var0, ModelLayers.ZOMBIFIED_PIGLIN, ModelLayers.ZOMBIFIED_PIGLIN_INNER_ARMOR, ModelLayers.ZOMBIFIED_PIGLIN_OUTER_ARMOR, true
            )
      );
   }
}
