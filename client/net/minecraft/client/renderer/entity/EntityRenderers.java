package net.minecraft.client.renderer.entity;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.model.animal.squid.SquidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.PlayerModelType;
import org.slf4j.Logger;

public class EntityRenderers {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Map<EntityType<?>, EntityRendererProvider<?>> PROVIDERS = new Object2ObjectOpenHashMap();

   public EntityRenderers() {
      super();
   }

   private static <T extends Entity> void register(final EntityType<? extends T> type, final EntityRendererProvider<T> renderer) {
      PROVIDERS.put(type, renderer);
   }

   public static Map<EntityType<?>, EntityRenderer<?, ?>> createEntityRenderers(final EntityRendererProvider.Context context) {
      ImmutableMap.Builder<EntityType<?>, EntityRenderer<?, ?>> result = ImmutableMap.builder();
      PROVIDERS.forEach((type, provider) -> {
         try {
            result.put(type, provider.create(context));
         } catch (Exception e) {
            throw new IllegalArgumentException("Failed to create model for " + String.valueOf(BuiltInRegistries.ENTITY_TYPE.getKey(type)), e);
         }
      });
      return result.build();
   }

   public static <T extends Avatar & ClientAvatarEntity> Map<PlayerModelType, AvatarRenderer<T>> createAvatarRenderers(final EntityRendererProvider.Context context) {
      try {
         return Map.of(PlayerModelType.WIDE, new AvatarRenderer(context, false), PlayerModelType.SLIM, new AvatarRenderer(context, true));
      } catch (Exception e) {
         throw new IllegalArgumentException("Failed to create avatar models", e);
      }
   }

   public static boolean validateRegistrations() {
      boolean hasAllModels = true;

      for(EntityType<?> type : BuiltInRegistries.ENTITY_TYPE) {
         if (type != EntityType.PLAYER && type != EntityType.MANNEQUIN && !PROVIDERS.containsKey(type)) {
            LOGGER.warn("No renderer registered for {}", BuiltInRegistries.ENTITY_TYPE.getKey(type));
            hasAllModels = false;
         }
      }

      return !hasAllModels;
   }

   static {
      register(EntityType.ACACIA_BOAT, (context) -> new BoatRenderer(context, ModelLayers.ACACIA_BOAT));
      register(EntityType.ACACIA_CHEST_BOAT, (context) -> new BoatRenderer(context, ModelLayers.ACACIA_CHEST_BOAT));
      register(EntityType.ALLAY, AllayRenderer::new);
      register(EntityType.AREA_EFFECT_CLOUD, NoopRenderer::new);
      register(EntityType.ARMADILLO, ArmadilloRenderer::new);
      register(EntityType.ARMOR_STAND, ArmorStandRenderer::new);
      register(EntityType.ARROW, TippableArrowRenderer::new);
      register(EntityType.AXOLOTL, AxolotlRenderer::new);
      register(EntityType.BAMBOO_CHEST_RAFT, (context) -> new RaftRenderer(context, ModelLayers.BAMBOO_CHEST_RAFT));
      register(EntityType.BAMBOO_RAFT, (context) -> new RaftRenderer(context, ModelLayers.BAMBOO_RAFT));
      register(EntityType.BAT, BatRenderer::new);
      register(EntityType.BEE, BeeRenderer::new);
      register(EntityType.BIRCH_BOAT, (context) -> new BoatRenderer(context, ModelLayers.BIRCH_BOAT));
      register(EntityType.BIRCH_CHEST_BOAT, (context) -> new BoatRenderer(context, ModelLayers.BIRCH_CHEST_BOAT));
      register(EntityType.BLAZE, BlazeRenderer::new);
      register(EntityType.BLOCK_DISPLAY, DisplayRenderer.BlockDisplayRenderer::new);
      register(EntityType.BOGGED, BoggedRenderer::new);
      register(EntityType.BREEZE, BreezeRenderer::new);
      register(EntityType.BREEZE_WIND_CHARGE, WindChargeRenderer::new);
      register(EntityType.CAMEL, CamelRenderer::new);
      register(EntityType.CAMEL_HUSK, CamelHuskRenderer::new);
      register(EntityType.CAT, CatRenderer::new);
      register(EntityType.CAVE_SPIDER, CaveSpiderRenderer::new);
      register(EntityType.CHERRY_BOAT, (context) -> new BoatRenderer(context, ModelLayers.CHERRY_BOAT));
      register(EntityType.CHERRY_CHEST_BOAT, (context) -> new BoatRenderer(context, ModelLayers.CHERRY_CHEST_BOAT));
      register(EntityType.CHEST_MINECART, (context) -> new MinecartRenderer(context, ModelLayers.CHEST_MINECART));
      register(EntityType.CHICKEN, ChickenRenderer::new);
      register(EntityType.COD, CodRenderer::new);
      register(EntityType.COMMAND_BLOCK_MINECART, (context) -> new MinecartRenderer(context, ModelLayers.COMMAND_BLOCK_MINECART));
      register(EntityType.COPPER_GOLEM, CopperGolemRenderer::new);
      register(EntityType.COW, CowRenderer::new);
      register(EntityType.CREAKING, CreakingRenderer::new);
      register(EntityType.CREEPER, CreeperRenderer::new);
      register(EntityType.DARK_OAK_BOAT, (context) -> new BoatRenderer(context, ModelLayers.DARK_OAK_BOAT));
      register(EntityType.DARK_OAK_CHEST_BOAT, (context) -> new BoatRenderer(context, ModelLayers.DARK_OAK_CHEST_BOAT));
      register(EntityType.DOLPHIN, DolphinRenderer::new);
      register(EntityType.DONKEY, (context) -> new DonkeyRenderer(context, EquipmentClientInfo.LayerType.DONKEY_SADDLE, ModelLayers.DONKEY_SADDLE, DonkeyRenderer.Type.DONKEY, DonkeyRenderer.Type.DONKEY_BABY));
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
      register(EntityType.EYE_OF_ENDER, (context) -> new ThrownItemRenderer(context, 1.0F, true));
      register(EntityType.FALLING_BLOCK, FallingBlockRenderer::new);
      register(EntityType.FIREBALL, (context) -> new ThrownItemRenderer(context, 3.0F, true));
      register(EntityType.FIREWORK_ROCKET, FireworkEntityRenderer::new);
      register(EntityType.FISHING_BOBBER, FishingHookRenderer::new);
      register(EntityType.FOX, FoxRenderer::new);
      register(EntityType.FROG, FrogRenderer::new);
      register(EntityType.FURNACE_MINECART, (context) -> new MinecartRenderer(context, ModelLayers.FURNACE_MINECART));
      register(EntityType.GHAST, GhastRenderer::new);
      register(EntityType.HAPPY_GHAST, HappyGhastRenderer::new);
      register(EntityType.GIANT, (context) -> new GiantMobRenderer(context, 6.0F));
      register(EntityType.GLOW_ITEM_FRAME, ItemFrameRenderer::new);
      register(EntityType.GLOW_SQUID, (context) -> new GlowSquidRenderer(context, new SquidModel(context.bakeLayer(ModelLayers.GLOW_SQUID)), new SquidModel(context.bakeLayer(ModelLayers.GLOW_SQUID_BABY))));
      register(EntityType.GOAT, GoatRenderer::new);
      register(EntityType.GUARDIAN, GuardianRenderer::new);
      register(EntityType.HOGLIN, HoglinRenderer::new);
      register(EntityType.HOPPER_MINECART, (context) -> new MinecartRenderer(context, ModelLayers.HOPPER_MINECART));
      register(EntityType.HORSE, HorseRenderer::new);
      register(EntityType.HUSK, HuskRenderer::new);
      register(EntityType.ILLUSIONER, IllusionerRenderer::new);
      register(EntityType.INTERACTION, NoopRenderer::new);
      register(EntityType.IRON_GOLEM, IronGolemRenderer::new);
      register(EntityType.ITEM, ItemEntityRenderer::new);
      register(EntityType.ITEM_DISPLAY, DisplayRenderer.ItemDisplayRenderer::new);
      register(EntityType.ITEM_FRAME, ItemFrameRenderer::new);
      register(EntityType.JUNGLE_BOAT, (context) -> new BoatRenderer(context, ModelLayers.JUNGLE_BOAT));
      register(EntityType.JUNGLE_CHEST_BOAT, (context) -> new BoatRenderer(context, ModelLayers.JUNGLE_CHEST_BOAT));
      register(EntityType.LEASH_KNOT, LeashKnotRenderer::new);
      register(EntityType.LIGHTNING_BOLT, LightningBoltRenderer::new);
      register(EntityType.LINGERING_POTION, ThrownItemRenderer::new);
      register(EntityType.LLAMA, (context) -> new LlamaRenderer(context, ModelLayers.LLAMA, ModelLayers.LLAMA_BABY));
      register(EntityType.LLAMA_SPIT, LlamaSpitRenderer::new);
      register(EntityType.MAGMA_CUBE, MagmaCubeRenderer::new);
      register(EntityType.MANGROVE_BOAT, (context) -> new BoatRenderer(context, ModelLayers.MANGROVE_BOAT));
      register(EntityType.MANGROVE_CHEST_BOAT, (context) -> new BoatRenderer(context, ModelLayers.MANGROVE_CHEST_BOAT));
      register(EntityType.MARKER, NoopRenderer::new);
      register(EntityType.MINECART, (context) -> new MinecartRenderer(context, ModelLayers.MINECART));
      register(EntityType.MOOSHROOM, MushroomCowRenderer::new);
      register(EntityType.MULE, (context) -> new DonkeyRenderer(context, EquipmentClientInfo.LayerType.MULE_SADDLE, ModelLayers.MULE_SADDLE, DonkeyRenderer.Type.MULE, DonkeyRenderer.Type.MULE_BABY));
      register(EntityType.NAUTILUS, NautilusRenderer::new);
      register(EntityType.OAK_BOAT, (context) -> new BoatRenderer(context, ModelLayers.OAK_BOAT));
      register(EntityType.OAK_CHEST_BOAT, (context) -> new BoatRenderer(context, ModelLayers.OAK_CHEST_BOAT));
      register(EntityType.OCELOT, OcelotRenderer::new);
      register(EntityType.OMINOUS_ITEM_SPAWNER, OminousItemSpawnerRenderer::new);
      register(EntityType.PAINTING, PaintingRenderer::new);
      register(EntityType.PALE_OAK_BOAT, (context) -> new BoatRenderer(context, ModelLayers.PALE_OAK_BOAT));
      register(EntityType.PALE_OAK_CHEST_BOAT, (context) -> new BoatRenderer(context, ModelLayers.PALE_OAK_CHEST_BOAT));
      register(EntityType.PANDA, PandaRenderer::new);
      register(EntityType.PARCHED, ParchedRenderer::new);
      register(EntityType.PARROT, ParrotRenderer::new);
      register(EntityType.PHANTOM, PhantomRenderer::new);
      register(EntityType.PIG, PigRenderer::new);
      register(EntityType.PIGLIN, (context) -> new PiglinRenderer(context, ModelLayers.PIGLIN, ModelLayers.PIGLIN_BABY, ModelLayers.PIGLIN_ARMOR, ModelLayers.PIGLIN_BABY_ARMOR));
      register(EntityType.PIGLIN_BRUTE, (context) -> new PiglinRenderer(context, ModelLayers.PIGLIN_BRUTE, ModelLayers.PIGLIN_BRUTE, ModelLayers.PIGLIN_BRUTE_ARMOR, ModelLayers.PIGLIN_BRUTE_ARMOR));
      register(EntityType.PILLAGER, PillagerRenderer::new);
      register(EntityType.POLAR_BEAR, PolarBearRenderer::new);
      register(EntityType.PUFFERFISH, PufferfishRenderer::new);
      register(EntityType.RABBIT, RabbitRenderer::new);
      register(EntityType.RAVAGER, RavagerRenderer::new);
      register(EntityType.SALMON, SalmonRenderer::new);
      register(EntityType.SHEEP, SheepRenderer::new);
      register(EntityType.SHULKER, ShulkerRenderer::new);
      register(EntityType.SHULKER_BULLET, ShulkerBulletRenderer::new);
      register(EntityType.SILVERFISH, SilverfishRenderer::new);
      register(EntityType.SKELETON, SkeletonRenderer::new);
      register(EntityType.SKELETON_HORSE, (context) -> new UndeadHorseRenderer(context, EquipmentClientInfo.LayerType.SKELETON_HORSE_SADDLE, ModelLayers.SKELETON_HORSE_SADDLE, UndeadHorseRenderer.Type.SKELETON, UndeadHorseRenderer.Type.SKELETON_BABY));
      register(EntityType.SLIME, SlimeRenderer::new);
      register(EntityType.SMALL_FIREBALL, (context) -> new ThrownItemRenderer(context, 0.75F, true));
      register(EntityType.SNIFFER, SnifferRenderer::new);
      register(EntityType.SNOWBALL, ThrownItemRenderer::new);
      register(EntityType.SNOW_GOLEM, SnowGolemRenderer::new);
      register(EntityType.SPAWNER_MINECART, (context) -> new MinecartRenderer(context, ModelLayers.SPAWNER_MINECART));
      register(EntityType.SPECTRAL_ARROW, SpectralArrowRenderer::new);
      register(EntityType.SPIDER, SpiderRenderer::new);
      register(EntityType.SPLASH_POTION, ThrownItemRenderer::new);
      register(EntityType.SPRUCE_BOAT, (context) -> new BoatRenderer(context, ModelLayers.SPRUCE_BOAT));
      register(EntityType.SPRUCE_CHEST_BOAT, (context) -> new BoatRenderer(context, ModelLayers.SPRUCE_CHEST_BOAT));
      register(EntityType.SQUID, (context) -> new SquidRenderer(context, new SquidModel(context.bakeLayer(ModelLayers.SQUID)), new SquidModel(context.bakeLayer(ModelLayers.SQUID_BABY))));
      register(EntityType.STRAY, StrayRenderer::new);
      register(EntityType.STRIDER, StriderRenderer::new);
      register(EntityType.TADPOLE, TadpoleRenderer::new);
      register(EntityType.TEXT_DISPLAY, DisplayRenderer.TextDisplayRenderer::new);
      register(EntityType.TNT, TntRenderer::new);
      register(EntityType.TNT_MINECART, TntMinecartRenderer::new);
      register(EntityType.TRADER_LLAMA, (context) -> new LlamaRenderer(context, ModelLayers.TRADER_LLAMA, ModelLayers.TRADER_LLAMA_BABY));
      register(EntityType.TRIDENT, ThrownTridentRenderer::new);
      register(EntityType.TROPICAL_FISH, TropicalFishRenderer::new);
      register(EntityType.TURTLE, TurtleRenderer::new);
      register(EntityType.VEX, VexRenderer::new);
      register(EntityType.VILLAGER, VillagerRenderer::new);
      register(EntityType.VINDICATOR, VindicatorRenderer::new);
      register(EntityType.WANDERING_TRADER, WanderingTraderRenderer::new);
      register(EntityType.WARDEN, WardenRenderer::new);
      register(EntityType.WIND_CHARGE, WindChargeRenderer::new);
      register(EntityType.WITCH, WitchRenderer::new);
      register(EntityType.WITHER, WitherBossRenderer::new);
      register(EntityType.WITHER_SKELETON, WitherSkeletonRenderer::new);
      register(EntityType.WITHER_SKULL, WitherSkullRenderer::new);
      register(EntityType.WOLF, WolfRenderer::new);
      register(EntityType.ZOGLIN, ZoglinRenderer::new);
      register(EntityType.ZOMBIE, ZombieRenderer::new);
      register(EntityType.ZOMBIE_HORSE, (context) -> new UndeadHorseRenderer(context, EquipmentClientInfo.LayerType.ZOMBIE_HORSE_SADDLE, ModelLayers.ZOMBIE_HORSE_SADDLE, UndeadHorseRenderer.Type.ZOMBIE, UndeadHorseRenderer.Type.ZOMBIE_BABY));
      register(EntityType.ZOMBIE_NAUTILUS, ZombieNautilusRenderer::new);
      register(EntityType.ZOMBIE_VILLAGER, ZombieVillagerRenderer::new);
      register(EntityType.ZOMBIFIED_PIGLIN, (context) -> new ZombifiedPiglinRenderer(context, ModelLayers.ZOMBIFIED_PIGLIN, ModelLayers.ZOMBIFIED_PIGLIN_BABY, ModelLayers.ZOMBIFIED_PIGLIN_ARMOR, ModelLayers.ZOMBIFIED_PIGLIN_BABY_ARMOR));
   }
}
