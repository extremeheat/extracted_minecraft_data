package net.minecraft.client.renderer.entity;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.model.SquidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
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

   private static <T extends Entity> void register(EntityType<? extends T> var0, EntityRendererProvider<T> var1) {
      PROVIDERS.put(var0, var1);
   }

   public static Map<EntityType<?>, EntityRenderer<?, ?>> createEntityRenderers(EntityRendererProvider.Context var0) {
      ImmutableMap.Builder var1 = ImmutableMap.builder();
      PROVIDERS.forEach((var2, var3) -> {
         try {
            var1.put(var2, var3.create(var0));
         } catch (Exception var5) {
            throw new IllegalArgumentException("Failed to create model for " + String.valueOf(BuiltInRegistries.ENTITY_TYPE.getKey(var2)), var5);
         }
      });
      return var1.build();
   }

   public static <T extends Avatar & ClientAvatarEntity> Map<PlayerModelType, AvatarRenderer<T>> createAvatarRenderers(EntityRendererProvider.Context var0) {
      try {
         return Map.of(PlayerModelType.WIDE, new AvatarRenderer(var0, false), PlayerModelType.SLIM, new AvatarRenderer(var0, true));
      } catch (Exception var2) {
         throw new IllegalArgumentException("Failed to create avatar models", var2);
      }
   }

   public static boolean validateRegistrations() {
      boolean var0 = true;

      for(EntityType var2 : BuiltInRegistries.ENTITY_TYPE) {
         if (var2 != EntityType.PLAYER && var2 != EntityType.MANNEQUIN && !PROVIDERS.containsKey(var2)) {
            LOGGER.warn("No renderer registered for {}", BuiltInRegistries.ENTITY_TYPE.getKey(var2));
            var0 = false;
         }
      }

      return !var0;
   }

   static {
      register(EntityType.ACACIA_BOAT, (var0) -> new BoatRenderer(var0, ModelLayers.ACACIA_BOAT));
      register(EntityType.ACACIA_CHEST_BOAT, (var0) -> new BoatRenderer(var0, ModelLayers.ACACIA_CHEST_BOAT));
      register(EntityType.ALLAY, AllayRenderer::new);
      register(EntityType.AREA_EFFECT_CLOUD, NoopRenderer::new);
      register(EntityType.ARMADILLO, ArmadilloRenderer::new);
      register(EntityType.ARMOR_STAND, ArmorStandRenderer::new);
      register(EntityType.ARROW, TippableArrowRenderer::new);
      register(EntityType.AXOLOTL, AxolotlRenderer::new);
      register(EntityType.BAMBOO_CHEST_RAFT, (var0) -> new RaftRenderer(var0, ModelLayers.BAMBOO_CHEST_RAFT));
      register(EntityType.BAMBOO_RAFT, (var0) -> new RaftRenderer(var0, ModelLayers.BAMBOO_RAFT));
      register(EntityType.BAT, BatRenderer::new);
      register(EntityType.BEE, BeeRenderer::new);
      register(EntityType.BIRCH_BOAT, (var0) -> new BoatRenderer(var0, ModelLayers.BIRCH_BOAT));
      register(EntityType.BIRCH_CHEST_BOAT, (var0) -> new BoatRenderer(var0, ModelLayers.BIRCH_CHEST_BOAT));
      register(EntityType.BLAZE, BlazeRenderer::new);
      register(EntityType.BLOCK_DISPLAY, DisplayRenderer.BlockDisplayRenderer::new);
      register(EntityType.BOGGED, BoggedRenderer::new);
      register(EntityType.BREEZE, BreezeRenderer::new);
      register(EntityType.BREEZE_WIND_CHARGE, WindChargeRenderer::new);
      register(EntityType.CAMEL, CamelRenderer::new);
      register(EntityType.CAT, CatRenderer::new);
      register(EntityType.CAVE_SPIDER, CaveSpiderRenderer::new);
      register(EntityType.CHERRY_BOAT, (var0) -> new BoatRenderer(var0, ModelLayers.CHERRY_BOAT));
      register(EntityType.CHERRY_CHEST_BOAT, (var0) -> new BoatRenderer(var0, ModelLayers.CHERRY_CHEST_BOAT));
      register(EntityType.CHEST_MINECART, (var0) -> new MinecartRenderer(var0, ModelLayers.CHEST_MINECART));
      register(EntityType.CHICKEN, ChickenRenderer::new);
      register(EntityType.COD, CodRenderer::new);
      register(EntityType.COMMAND_BLOCK_MINECART, (var0) -> new MinecartRenderer(var0, ModelLayers.COMMAND_BLOCK_MINECART));
      register(EntityType.COPPER_GOLEM, CopperGolemRenderer::new);
      register(EntityType.COW, CowRenderer::new);
      register(EntityType.CREAKING, CreakingRenderer::new);
      register(EntityType.CREEPER, CreeperRenderer::new);
      register(EntityType.DARK_OAK_BOAT, (var0) -> new BoatRenderer(var0, ModelLayers.DARK_OAK_BOAT));
      register(EntityType.DARK_OAK_CHEST_BOAT, (var0) -> new BoatRenderer(var0, ModelLayers.DARK_OAK_CHEST_BOAT));
      register(EntityType.DOLPHIN, DolphinRenderer::new);
      register(EntityType.DONKEY, (var0) -> new DonkeyRenderer(var0, DonkeyRenderer.Type.DONKEY));
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
      register(EntityType.EYE_OF_ENDER, (var0) -> new ThrownItemRenderer(var0, 1.0F, true));
      register(EntityType.FALLING_BLOCK, FallingBlockRenderer::new);
      register(EntityType.FIREBALL, (var0) -> new ThrownItemRenderer(var0, 3.0F, true));
      register(EntityType.FIREWORK_ROCKET, FireworkEntityRenderer::new);
      register(EntityType.FISHING_BOBBER, FishingHookRenderer::new);
      register(EntityType.FOX, FoxRenderer::new);
      register(EntityType.FROG, FrogRenderer::new);
      register(EntityType.FURNACE_MINECART, (var0) -> new MinecartRenderer(var0, ModelLayers.FURNACE_MINECART));
      register(EntityType.GHAST, GhastRenderer::new);
      register(EntityType.HAPPY_GHAST, HappyGhastRenderer::new);
      register(EntityType.GIANT, (var0) -> new GiantMobRenderer(var0, 6.0F));
      register(EntityType.GLOW_ITEM_FRAME, ItemFrameRenderer::new);
      register(EntityType.GLOW_SQUID, (var0) -> new GlowSquidRenderer(var0, new SquidModel(var0.bakeLayer(ModelLayers.GLOW_SQUID)), new SquidModel(var0.bakeLayer(ModelLayers.GLOW_SQUID_BABY))));
      register(EntityType.GOAT, GoatRenderer::new);
      register(EntityType.GUARDIAN, GuardianRenderer::new);
      register(EntityType.HOGLIN, HoglinRenderer::new);
      register(EntityType.HOPPER_MINECART, (var0) -> new MinecartRenderer(var0, ModelLayers.HOPPER_MINECART));
      register(EntityType.HORSE, HorseRenderer::new);
      register(EntityType.HUSK, HuskRenderer::new);
      register(EntityType.ILLUSIONER, IllusionerRenderer::new);
      register(EntityType.INTERACTION, NoopRenderer::new);
      register(EntityType.IRON_GOLEM, IronGolemRenderer::new);
      register(EntityType.ITEM, ItemEntityRenderer::new);
      register(EntityType.ITEM_DISPLAY, DisplayRenderer.ItemDisplayRenderer::new);
      register(EntityType.ITEM_FRAME, ItemFrameRenderer::new);
      register(EntityType.JUNGLE_BOAT, (var0) -> new BoatRenderer(var0, ModelLayers.JUNGLE_BOAT));
      register(EntityType.JUNGLE_CHEST_BOAT, (var0) -> new BoatRenderer(var0, ModelLayers.JUNGLE_CHEST_BOAT));
      register(EntityType.LEASH_KNOT, LeashKnotRenderer::new);
      register(EntityType.LIGHTNING_BOLT, LightningBoltRenderer::new);
      register(EntityType.LINGERING_POTION, ThrownItemRenderer::new);
      register(EntityType.LLAMA, (var0) -> new LlamaRenderer(var0, ModelLayers.LLAMA, ModelLayers.LLAMA_BABY));
      register(EntityType.LLAMA_SPIT, LlamaSpitRenderer::new);
      register(EntityType.MAGMA_CUBE, MagmaCubeRenderer::new);
      register(EntityType.MANGROVE_BOAT, (var0) -> new BoatRenderer(var0, ModelLayers.MANGROVE_BOAT));
      register(EntityType.MANGROVE_CHEST_BOAT, (var0) -> new BoatRenderer(var0, ModelLayers.MANGROVE_CHEST_BOAT));
      register(EntityType.MARKER, NoopRenderer::new);
      register(EntityType.MINECART, (var0) -> new MinecartRenderer(var0, ModelLayers.MINECART));
      register(EntityType.MOOSHROOM, MushroomCowRenderer::new);
      register(EntityType.MULE, (var0) -> new DonkeyRenderer(var0, DonkeyRenderer.Type.MULE));
      register(EntityType.NAUTILUS, (var0) -> new NautilusRenderer(var0, NautilusRenderer.Type.NAUTILUS));
      register(EntityType.OAK_BOAT, (var0) -> new BoatRenderer(var0, ModelLayers.OAK_BOAT));
      register(EntityType.OAK_CHEST_BOAT, (var0) -> new BoatRenderer(var0, ModelLayers.OAK_CHEST_BOAT));
      register(EntityType.OCELOT, OcelotRenderer::new);
      register(EntityType.OMINOUS_ITEM_SPAWNER, OminousItemSpawnerRenderer::new);
      register(EntityType.PAINTING, PaintingRenderer::new);
      register(EntityType.PALE_OAK_BOAT, (var0) -> new BoatRenderer(var0, ModelLayers.PALE_OAK_BOAT));
      register(EntityType.PALE_OAK_CHEST_BOAT, (var0) -> new BoatRenderer(var0, ModelLayers.PALE_OAK_CHEST_BOAT));
      register(EntityType.PANDA, PandaRenderer::new);
      register(EntityType.PARROT, ParrotRenderer::new);
      register(EntityType.PHANTOM, PhantomRenderer::new);
      register(EntityType.PIG, PigRenderer::new);
      register(EntityType.PIGLIN, (var0) -> new PiglinRenderer(var0, ModelLayers.PIGLIN, ModelLayers.PIGLIN_BABY, ModelLayers.PIGLIN_ARMOR, ModelLayers.PIGLIN_BABY_ARMOR));
      register(EntityType.PIGLIN_BRUTE, (var0) -> new PiglinRenderer(var0, ModelLayers.PIGLIN_BRUTE, ModelLayers.PIGLIN_BRUTE, ModelLayers.PIGLIN_BRUTE_ARMOR, ModelLayers.PIGLIN_BRUTE_ARMOR));
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
      register(EntityType.SKELETON_HORSE, (var0) -> new UndeadHorseRenderer(var0, UndeadHorseRenderer.Type.SKELETON));
      register(EntityType.SLIME, SlimeRenderer::new);
      register(EntityType.SMALL_FIREBALL, (var0) -> new ThrownItemRenderer(var0, 0.75F, true));
      register(EntityType.SNIFFER, SnifferRenderer::new);
      register(EntityType.SNOWBALL, ThrownItemRenderer::new);
      register(EntityType.SNOW_GOLEM, SnowGolemRenderer::new);
      register(EntityType.SPAWNER_MINECART, (var0) -> new MinecartRenderer(var0, ModelLayers.SPAWNER_MINECART));
      register(EntityType.SPECTRAL_ARROW, SpectralArrowRenderer::new);
      register(EntityType.SPIDER, SpiderRenderer::new);
      register(EntityType.SPLASH_POTION, ThrownItemRenderer::new);
      register(EntityType.SPRUCE_BOAT, (var0) -> new BoatRenderer(var0, ModelLayers.SPRUCE_BOAT));
      register(EntityType.SPRUCE_CHEST_BOAT, (var0) -> new BoatRenderer(var0, ModelLayers.SPRUCE_CHEST_BOAT));
      register(EntityType.SQUID, (var0) -> new SquidRenderer(var0, new SquidModel(var0.bakeLayer(ModelLayers.SQUID)), new SquidModel(var0.bakeLayer(ModelLayers.SQUID_BABY))));
      register(EntityType.STRAY, StrayRenderer::new);
      register(EntityType.STRIDER, StriderRenderer::new);
      register(EntityType.TADPOLE, TadpoleRenderer::new);
      register(EntityType.TEXT_DISPLAY, DisplayRenderer.TextDisplayRenderer::new);
      register(EntityType.TNT, TntRenderer::new);
      register(EntityType.TNT_MINECART, TntMinecartRenderer::new);
      register(EntityType.TRADER_LLAMA, (var0) -> new LlamaRenderer(var0, ModelLayers.TRADER_LLAMA, ModelLayers.TRADER_LLAMA_BABY));
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
      register(EntityType.ZOMBIE_HORSE, (var0) -> new UndeadHorseRenderer(var0, UndeadHorseRenderer.Type.ZOMBIE));
      register(EntityType.ZOMBIE_NAUTILUS, (var0) -> new NautilusRenderer(var0, NautilusRenderer.Type.ZOMBIE_NAUTILUS));
      register(EntityType.ZOMBIE_VILLAGER, ZombieVillagerRenderer::new);
      register(EntityType.ZOMBIFIED_PIGLIN, (var0) -> new ZombifiedPiglinRenderer(var0, ModelLayers.ZOMBIFIED_PIGLIN, ModelLayers.ZOMBIFIED_PIGLIN_BABY, ModelLayers.ZOMBIFIED_PIGLIN_ARMOR, ModelLayers.ZOMBIFIED_PIGLIN_BABY_ARMOR));
   }
}
