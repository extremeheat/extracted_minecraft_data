package net.minecraft.client.renderer.entity;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.client.core.registries.BuiltInClientRegistries;
import net.minecraft.client.core.registries.ClientRegistries;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.model.animal.squid.SquidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypeIds;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.PlayerModelType;
import org.slf4j.Logger;

public class EntityRenderers {
   private static final Logger LOGGER = LogUtils.getLogger();

   public EntityRenderers() {
      super();
   }

   public static EntityRendererProvider<?> bootstrap(final Registry<EntityRendererProvider<?>> registry) {
      register(registry, EntityTypes.ACACIA_BOAT, (context) -> new BoatRenderer(context, ModelLayers.ACACIA_BOAT));
      register(registry, EntityTypes.ACACIA_CHEST_BOAT, (context) -> new BoatRenderer(context, ModelLayers.ACACIA_CHEST_BOAT));
      register(registry, EntityTypes.ALLAY, AllayRenderer::new);
      register(registry, EntityTypes.AREA_EFFECT_CLOUD, NoopRenderer::new);
      register(registry, EntityTypes.ARMADILLO, ArmadilloRenderer::new);
      register(registry, EntityTypes.ARMOR_STAND, ArmorStandRenderer::new);
      register(registry, EntityTypes.ARROW, TippableArrowRenderer::new);
      register(registry, EntityTypes.AXOLOTL, AxolotlRenderer::new);
      register(registry, EntityTypes.BAMBOO_CHEST_RAFT, (context) -> new RaftRenderer(context, ModelLayers.BAMBOO_CHEST_RAFT));
      register(registry, EntityTypes.BAMBOO_RAFT, (context) -> new RaftRenderer(context, ModelLayers.BAMBOO_RAFT));
      register(registry, EntityTypes.BAT, BatRenderer::new);
      register(registry, EntityTypes.BEE, BeeRenderer::new);
      register(registry, EntityTypes.BIRCH_BOAT, (context) -> new BoatRenderer(context, ModelLayers.BIRCH_BOAT));
      register(registry, EntityTypes.BIRCH_CHEST_BOAT, (context) -> new BoatRenderer(context, ModelLayers.BIRCH_CHEST_BOAT));
      register(registry, EntityTypes.BLAZE, BlazeRenderer::new);
      register(registry, EntityTypes.BLOCK_DISPLAY, DisplayRenderer.BlockDisplayRenderer::new);
      register(registry, EntityTypes.BOGGED, BoggedRenderer::new);
      register(registry, EntityTypes.BREEZE, BreezeRenderer::new);
      register(registry, EntityTypes.BREEZE_WIND_CHARGE, WindChargeRenderer::new);
      register(registry, EntityTypes.CAMEL, CamelRenderer::new);
      register(registry, EntityTypes.CAMEL_HUSK, CamelHuskRenderer::new);
      register(registry, EntityTypes.CAT, CatRenderer::new);
      register(registry, EntityTypes.CAVE_SPIDER, CaveSpiderRenderer::new);
      register(registry, EntityTypes.CHERRY_BOAT, (context) -> new BoatRenderer(context, ModelLayers.CHERRY_BOAT));
      register(registry, EntityTypes.CHERRY_CHEST_BOAT, (context) -> new BoatRenderer(context, ModelLayers.CHERRY_CHEST_BOAT));
      register(registry, EntityTypes.CHEST_MINECART, (context) -> new MinecartRenderer(context, ModelLayers.CHEST_MINECART));
      register(registry, EntityTypes.CHICKEN, ChickenRenderer::new);
      register(registry, EntityTypes.COD, CodRenderer::new);
      register(registry, EntityTypes.COMMAND_BLOCK_MINECART, (context) -> new MinecartRenderer(context, ModelLayers.COMMAND_BLOCK_MINECART));
      register(registry, EntityTypes.COPPER_GOLEM, CopperGolemRenderer::new);
      register(registry, EntityTypes.COW, CowRenderer::new);
      register(registry, EntityTypes.CREAKING, CreakingRenderer::new);
      register(registry, EntityTypes.CREEPER, CreeperRenderer::new);
      register(registry, EntityTypes.CUSHION, CushionRenderer::new);
      register(registry, EntityTypes.DARK_OAK_BOAT, (context) -> new BoatRenderer(context, ModelLayers.DARK_OAK_BOAT));
      register(registry, EntityTypes.DARK_OAK_CHEST_BOAT, (context) -> new BoatRenderer(context, ModelLayers.DARK_OAK_CHEST_BOAT));
      register(registry, EntityTypes.DOLPHIN, DolphinRenderer::new);
      register(registry, EntityTypes.DONKEY, (context) -> new DonkeyRenderer(context, EquipmentClientInfo.LayerType.DONKEY_SADDLE, ModelLayers.DONKEY_SADDLE, DonkeyRenderer.Type.DONKEY, DonkeyRenderer.Type.DONKEY_BABY));
      register(registry, EntityTypes.DRAGON_FIREBALL, DragonFireballRenderer::new);
      register(registry, EntityTypes.DROWNED, DrownedRenderer::new);
      register(registry, EntityTypes.EGG, ThrownItemRenderer::new);
      register(registry, EntityTypes.ELDER_GUARDIAN, ElderGuardianRenderer::new);
      register(registry, EntityTypes.ENDERMAN, EndermanRenderer::new);
      register(registry, EntityTypes.ENDERMITE, EndermiteRenderer::new);
      register(registry, EntityTypes.ENDER_DRAGON, EnderDragonRenderer::new);
      register(registry, EntityTypes.ENDER_PEARL, ThrownItemRenderer::new);
      register(registry, EntityTypes.END_CRYSTAL, EndCrystalRenderer::new);
      register(registry, EntityTypes.EVOKER, EvokerRenderer::new);
      register(registry, EntityTypes.EVOKER_FANGS, EvokerFangsRenderer::new);
      register(registry, EntityTypes.EXPERIENCE_BOTTLE, ThrownItemRenderer::new);
      register(registry, EntityTypes.EXPERIENCE_ORB, ExperienceOrbRenderer::new);
      register(registry, EntityTypes.EYE_OF_ENDER, (context) -> new ThrownItemRenderer(context, 1.0F, true));
      register(registry, EntityTypes.FALLING_BLOCK, FallingBlockRenderer::new);
      register(registry, EntityTypes.FIREBALL, (context) -> new ThrownItemRenderer(context, 3.0F, true));
      register(registry, EntityTypes.FIREWORK_ROCKET, FireworkEntityRenderer::new);
      register(registry, EntityTypes.FISHING_BOBBER, FishingHookRenderer::new);
      register(registry, EntityTypes.FOX, FoxRenderer::new);
      register(registry, EntityTypes.FROG, FrogRenderer::new);
      register(registry, EntityTypes.FROSTBITE, FrostbiteRenderer::new);
      register(registry, EntityTypes.FURNACE_MINECART, (context) -> new MinecartRenderer(context, ModelLayers.FURNACE_MINECART));
      register(registry, EntityTypes.GHAST, GhastRenderer::new);
      register(registry, EntityTypes.HAPPY_GHAST, HappyGhastRenderer::new);
      register(registry, EntityTypes.GIANT, (context) -> new GiantMobRenderer(context, 6.0F));
      register(registry, EntityTypes.GLOW_ITEM_FRAME, ItemFrameRenderer::new);
      register(registry, EntityTypes.GLOW_SQUID, (context) -> new GlowSquidRenderer(context, new SquidModel(context.bakeLayer(ModelLayers.GLOW_SQUID)), new SquidModel(context.bakeLayer(ModelLayers.GLOW_SQUID_BABY))));
      register(registry, EntityTypes.GOAT, GoatRenderer::new);
      register(registry, EntityTypes.GUARDIAN, GuardianRenderer::new);
      register(registry, EntityTypes.HOGLIN, HoglinRenderer::new);
      register(registry, EntityTypes.HOPPER_MINECART, (context) -> new MinecartRenderer(context, ModelLayers.HOPPER_MINECART));
      register(registry, EntityTypes.HORSE, HorseRenderer::new);
      register(registry, EntityTypes.HUSK, HuskRenderer::new);
      register(registry, EntityTypes.ICE_BALL, ThrownItemRenderer::new);
      register(registry, EntityTypes.ILLUSIONER, IllusionerRenderer::new);
      register(registry, EntityTypes.INTERACTION, NoopRenderer::new);
      register(registry, EntityTypes.IRON_GOLEM, IronGolemRenderer::new);
      register(registry, EntityTypes.ITEM, ItemEntityRenderer::new);
      register(registry, EntityTypes.ITEM_DISPLAY, DisplayRenderer.ItemDisplayRenderer::new);
      register(registry, EntityTypes.ITEM_FRAME, ItemFrameRenderer::new);
      register(registry, EntityTypes.JUNGLE_BOAT, (context) -> new BoatRenderer(context, ModelLayers.JUNGLE_BOAT));
      register(registry, EntityTypes.JUNGLE_CHEST_BOAT, (context) -> new BoatRenderer(context, ModelLayers.JUNGLE_CHEST_BOAT));
      register(registry, EntityTypes.LEASH_KNOT, LeashKnotRenderer::new);
      register(registry, EntityTypes.LIGHTNING_BOLT, LightningBoltRenderer::new);
      register(registry, EntityTypes.LINGERING_POTION, ThrownItemRenderer::new);
      register(registry, EntityTypes.LLAMA, (context) -> new LlamaRenderer(context, ModelLayers.LLAMA, ModelLayers.LLAMA_BABY));
      register(registry, EntityTypes.LLAMA_SPIT, LlamaSpitRenderer::new);
      register(registry, EntityTypes.MAGMA_CUBE, MagmaCubeRenderer::new);
      register(registry, EntityTypes.MANGROVE_BOAT, (context) -> new BoatRenderer(context, ModelLayers.MANGROVE_BOAT));
      register(registry, EntityTypes.MANGROVE_CHEST_BOAT, (context) -> new BoatRenderer(context, ModelLayers.MANGROVE_CHEST_BOAT));
      register(registry, EntityTypes.MARKER, NoopRenderer::new);
      register(registry, EntityTypes.MINECART, (context) -> new MinecartRenderer(context, ModelLayers.MINECART));
      register(registry, EntityTypes.MOOSHROOM, MushroomCowRenderer::new);
      register(registry, EntityTypes.MULE, (context) -> new DonkeyRenderer(context, EquipmentClientInfo.LayerType.MULE_SADDLE, ModelLayers.MULE_SADDLE, DonkeyRenderer.Type.MULE, DonkeyRenderer.Type.MULE_BABY));
      register(registry, EntityTypes.NAUTILUS, NautilusRenderer::new);
      register(registry, EntityTypes.OAK_BOAT, (context) -> new BoatRenderer(context, ModelLayers.OAK_BOAT));
      register(registry, EntityTypes.OAK_CHEST_BOAT, (context) -> new BoatRenderer(context, ModelLayers.OAK_CHEST_BOAT));
      register(registry, EntityTypes.OCELOT, OcelotRenderer::new);
      register(registry, EntityTypes.OMINOUS_ITEM_SPAWNER, OminousItemSpawnerRenderer::new);
      register(registry, EntityTypes.PAINTING, PaintingRenderer::new);
      register(registry, EntityTypes.PALE_OAK_BOAT, (context) -> new BoatRenderer(context, ModelLayers.PALE_OAK_BOAT));
      register(registry, EntityTypes.PALE_OAK_CHEST_BOAT, (context) -> new BoatRenderer(context, ModelLayers.PALE_OAK_CHEST_BOAT));
      register(registry, EntityTypes.PANDA, PandaRenderer::new);
      register(registry, EntityTypes.PARCHED, ParchedRenderer::new);
      register(registry, EntityTypes.PARROT, ParrotRenderer::new);
      register(registry, EntityTypes.PHANTOM, PhantomRenderer::new);
      register(registry, EntityTypes.PIG, PigRenderer::new);
      register(registry, EntityTypes.PIGLIN, (context) -> new PiglinRenderer(context, ModelLayers.PIGLIN, ModelLayers.PIGLIN_BABY, ModelLayers.PIGLIN_ARMOR, ModelLayers.PIGLIN_BABY_ARMOR));
      register(registry, EntityTypes.PIGLIN_BRUTE, (context) -> new PiglinRenderer(context, ModelLayers.PIGLIN_BRUTE, ModelLayers.PIGLIN_BRUTE, ModelLayers.PIGLIN_BRUTE_ARMOR, ModelLayers.PIGLIN_BRUTE_ARMOR));
      register(registry, EntityTypes.PILLAGER, PillagerRenderer::new);
      register(registry, EntityTypes.POLAR_BEAR, PolarBearRenderer::new);
      register(registry, EntityTypes.POPLAR_BOAT, (context) -> new BoatRenderer(context, ModelLayers.POPLAR_BOAT));
      register(registry, EntityTypes.POPLAR_CHEST_BOAT, (context) -> new BoatRenderer(context, ModelLayers.POPLAR_CHEST_BOAT));
      register(registry, EntityTypes.PUFFERFISH, PufferfishRenderer::new);
      register(registry, EntityTypes.RABBIT, RabbitRenderer::new);
      register(registry, EntityTypes.RAVAGER, RavagerRenderer::new);
      register(registry, EntityTypes.SALMON, SalmonRenderer::new);
      register(registry, EntityTypes.SHEEP, SheepRenderer::new);
      register(registry, EntityTypes.SHULKER, ShulkerRenderer::new);
      register(registry, EntityTypes.SHULKER_BULLET, ShulkerBulletRenderer::new);
      register(registry, EntityTypes.SILVERFISH, SilverfishRenderer::new);
      register(registry, EntityTypes.SKELETON, SkeletonRenderer::new);
      register(registry, EntityTypes.SKELETON_HORSE, (context) -> new UndeadHorseRenderer(context, EquipmentClientInfo.LayerType.SKELETON_HORSE_SADDLE, ModelLayers.SKELETON_HORSE_SADDLE, UndeadHorseRenderer.Type.SKELETON, UndeadHorseRenderer.Type.SKELETON_BABY));
      register(registry, EntityTypes.SLIME, SlimeRenderer::new);
      register(registry, EntityTypes.SMALL_FIREBALL, (context) -> new ThrownItemRenderer(context, 0.75F, true));
      register(registry, EntityTypes.SNIFFER, SnifferRenderer::new);
      register(registry, EntityTypes.SNOWBALL, ThrownItemRenderer::new);
      register(registry, EntityTypes.SNOW_GOLEM, SnowGolemRenderer::new);
      register(registry, EntityTypes.SPAWNER_MINECART, (context) -> new MinecartRenderer(context, ModelLayers.SPAWNER_MINECART));
      register(registry, EntityTypes.SPECTRAL_ARROW, SpectralArrowRenderer::new);
      register(registry, EntityTypes.SPIDER, SpiderRenderer::new);
      register(registry, EntityTypes.SPLASH_POTION, ThrownItemRenderer::new);
      register(registry, EntityTypes.SPRUCE_BOAT, (context) -> new BoatRenderer(context, ModelLayers.SPRUCE_BOAT));
      register(registry, EntityTypes.SPRUCE_CHEST_BOAT, (context) -> new BoatRenderer(context, ModelLayers.SPRUCE_CHEST_BOAT));
      register(registry, EntityTypes.SQUID, (context) -> new SquidRenderer(context, new SquidModel(context.bakeLayer(ModelLayers.SQUID)), new SquidModel(context.bakeLayer(ModelLayers.SQUID_BABY))));
      register(registry, EntityTypes.STRAY, StrayRenderer::new);
      register(registry, EntityTypes.STRIDER, StriderRenderer::new);
      register(registry, EntityTypes.SULFUR_CUBE, SulfurCubeRenderer::new);
      register(registry, EntityTypes.TADPOLE, TadpoleRenderer::new);
      register(registry, EntityTypes.TEXT_DISPLAY, DisplayRenderer.TextDisplayRenderer::new);
      register(registry, EntityTypes.TNT, TntRenderer::new);
      register(registry, EntityTypes.TNT_MINECART, TntMinecartRenderer::new);
      register(registry, EntityTypes.TRADER_LLAMA, (context) -> new LlamaRenderer(context, ModelLayers.TRADER_LLAMA, ModelLayers.TRADER_LLAMA_BABY));
      register(registry, EntityTypes.TRIDENT, ThrownTridentRenderer::new);
      register(registry, EntityTypes.TROPICAL_FISH, TropicalFishRenderer::new);
      register(registry, EntityTypes.TURTLE, TurtleRenderer::new);
      register(registry, EntityTypes.VEX, VexRenderer::new);
      register(registry, EntityTypes.VILLAGER, VillagerRenderer::new);
      register(registry, EntityTypes.VINDICATOR, VindicatorRenderer::new);
      register(registry, EntityTypes.WANDERING_TRADER, WanderingTraderRenderer::new);
      register(registry, EntityTypes.WARDEN, WardenRenderer::new);
      register(registry, EntityTypes.WIND_CHARGE, WindChargeRenderer::new);
      register(registry, EntityTypes.WITCH, WitchRenderer::new);
      register(registry, EntityTypes.WITHER, WitherBossRenderer::new);
      register(registry, EntityTypes.WITHER_SKELETON, WitherSkeletonRenderer::new);
      register(registry, EntityTypes.WITHER_SKULL, WitherSkullRenderer::new);
      register(registry, EntityTypes.WOLF, WolfRenderer::new);
      register(registry, EntityTypes.ZOGLIN, ZoglinRenderer::new);
      register(registry, EntityTypes.ZOMBIE, ZombieRenderer::new);
      register(registry, EntityTypes.ZOMBIE_HORSE, (context) -> new UndeadHorseRenderer(context, EquipmentClientInfo.LayerType.ZOMBIE_HORSE_SADDLE, ModelLayers.ZOMBIE_HORSE_SADDLE, UndeadHorseRenderer.Type.ZOMBIE, UndeadHorseRenderer.Type.ZOMBIE_BABY));
      register(registry, EntityTypes.ZOMBIE_NAUTILUS, ZombieNautilusRenderer::new);
      register(registry, EntityTypes.ZOMBIE_VILLAGER, ZombieVillagerRenderer::new);
      return register(registry, EntityTypes.ZOMBIFIED_PIGLIN, (context) -> new ZombifiedPiglinRenderer(context, ModelLayers.ZOMBIFIED_PIGLIN, ModelLayers.ZOMBIFIED_PIGLIN_BABY, ModelLayers.ZOMBIFIED_PIGLIN_ARMOR, ModelLayers.ZOMBIFIED_PIGLIN_BABY_ARMOR));
   }

   private static <T extends Entity> EntityRendererProvider<T> register(final Registry<EntityRendererProvider<?>> registry, final EntityType<? extends T> type, final EntityRendererProvider<T> renderer) {
      return (EntityRendererProvider)Registry.register(registry, (Identifier)BuiltInRegistries.ENTITY_TYPE.getKey(type), renderer);
   }

   private static ResourceKey<EntityRendererProvider<?>> entityKey(final Holder.Reference<EntityType<?>> type) {
      return ResourceKey.create(ClientRegistries.ENTITY_RENDERER, type.key().identifier());
   }

   public static Map<EntityType<?>, EntityRenderer<?, ?>> createEntityRenderers(final EntityRendererProvider.Context context) {
      ImmutableMap.Builder<EntityType<?>, EntityRenderer<?, ?>> result = ImmutableMap.builder();

      for(Holder.Reference<EntityType<?>> type : BuiltInRegistries.ENTITY_TYPE.holders()) {
         ResourceKey<EntityRendererProvider<?>> key = entityKey(type);
         EntityRendererProvider<?> provider = (EntityRendererProvider)BuiltInClientRegistries.ENTITY_RENDERER.getValue(key);
         if (provider != null) {
            try {
               result.put(type.value(), provider.create(context));
            } catch (Exception e) {
               throw new IllegalArgumentException("Failed to create model for " + String.valueOf(key.identifier()), e);
            }
         }
      }

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
      return !collectMissingEntityRenderers().isEmpty();
   }

   public static List<ResourceKey<EntityRendererProvider<?>>> collectMissingEntityRenderers() {
      List<ResourceKey<EntityRendererProvider<?>>> missingEntityKeys = new ArrayList();

      for(Holder.Reference<EntityType<?>> type : BuiltInRegistries.ENTITY_TYPE.holders()) {
         if (!type.is(EntityTypeIds.PLAYER) && !type.is(EntityTypeIds.MANNEQUIN)) {
            ResourceKey<EntityRendererProvider<?>> key = entityKey(type);
            if (BuiltInClientRegistries.ENTITY_RENDERER.getValue(key) == null) {
               LOGGER.warn("No renderer registered for {}", key);
               missingEntityKeys.add(key);
            }
         }
      }

      return missingEntityKeys;
   }
}
