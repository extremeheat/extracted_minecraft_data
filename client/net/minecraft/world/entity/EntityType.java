package net.minecraft.world.entity;

import com.google.common.collect.ImmutableSet;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.DependantName;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.animal.camel.CamelHusk;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.entity.animal.cow.MushroomCow;
import net.minecraft.world.entity.animal.dolphin.Dolphin;
import net.minecraft.world.entity.animal.equine.Donkey;
import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.entity.animal.equine.Llama;
import net.minecraft.world.entity.animal.equine.Mule;
import net.minecraft.world.entity.animal.equine.SkeletonHorse;
import net.minecraft.world.entity.animal.equine.TraderLlama;
import net.minecraft.world.entity.animal.equine.ZombieHorse;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.animal.feline.Ocelot;
import net.minecraft.world.entity.animal.fish.Cod;
import net.minecraft.world.entity.animal.fish.Pufferfish;
import net.minecraft.world.entity.animal.fish.Salmon;
import net.minecraft.world.entity.animal.fish.TropicalFish;
import net.minecraft.world.entity.animal.fox.Fox;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.animal.golem.CopperGolem;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.animal.golem.SnowGolem;
import net.minecraft.world.entity.animal.happyghast.HappyGhast;
import net.minecraft.world.entity.animal.nautilus.Nautilus;
import net.minecraft.world.entity.animal.nautilus.ZombieNautilus;
import net.minecraft.world.entity.animal.panda.Panda;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.entity.animal.polarbear.PolarBear;
import net.minecraft.world.entity.animal.rabbit.Rabbit;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.entity.animal.squid.GlowSquid;
import net.minecraft.world.entity.animal.squid.Squid;
import net.minecraft.world.entity.animal.turtle.Turtle;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.decoration.GlowItemFrame;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.decoration.Mannequin;
import net.minecraft.world.entity.decoration.painting.Painting;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Giant;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.entity.monster.creaking.Creaking;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.illager.Evoker;
import net.minecraft.world.entity.monster.illager.Illusioner;
import net.minecraft.world.entity.monster.illager.Pillager;
import net.minecraft.world.entity.monster.illager.Vindicator;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.monster.skeleton.Bogged;
import net.minecraft.world.entity.monster.skeleton.Parched;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import net.minecraft.world.entity.monster.skeleton.Stray;
import net.minecraft.world.entity.monster.skeleton.WitherSkeleton;
import net.minecraft.world.entity.monster.spider.CaveSpider;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.monster.zombie.Drowned;
import net.minecraft.world.entity.monster.zombie.Husk;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.monster.zombie.ZombieVillager;
import net.minecraft.world.entity.monster.zombie.ZombifiedPiglin;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.entity.projectile.EyeOfEnder;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.entity.projectile.arrow.SpectralArrow;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.entity.projectile.hurtingprojectile.DragonFireball;
import net.minecraft.world.entity.projectile.hurtingprojectile.LargeFireball;
import net.minecraft.world.entity.projectile.hurtingprojectile.SmallFireball;
import net.minecraft.world.entity.projectile.hurtingprojectile.WitherSkull;
import net.minecraft.world.entity.projectile.hurtingprojectile.windcharge.BreezeWindCharge;
import net.minecraft.world.entity.projectile.hurtingprojectile.windcharge.WindCharge;
import net.minecraft.world.entity.projectile.throwableitemprojectile.Snowball;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEgg;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownExperienceBottle;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownLingeringPotion;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownSplashPotion;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.entity.vehicle.boat.ChestBoat;
import net.minecraft.world.entity.vehicle.boat.ChestRaft;
import net.minecraft.world.entity.vehicle.boat.Raft;
import net.minecraft.world.entity.vehicle.minecart.Minecart;
import net.minecraft.world.entity.vehicle.minecart.MinecartChest;
import net.minecraft.world.entity.vehicle.minecart.MinecartCommandBlock;
import net.minecraft.world.entity.vehicle.minecart.MinecartFurnace;
import net.minecraft.world.entity.vehicle.minecart.MinecartHopper;
import net.minecraft.world.entity.vehicle.minecart.MinecartSpawner;
import net.minecraft.world.entity.vehicle.minecart.MinecartTNT;
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class EntityType<T extends Entity> implements EntityTypeTest<Entity, T>, FeatureElement {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Holder.Reference<EntityType<?>> builtInRegistryHolder;
   public static final Codec<EntityType<?>> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, EntityType<?>> STREAM_CODEC;
   private static final float MAGIC_HORSE_WIDTH = 1.3964844F;
   private static final int DISPLAY_TRACKING_RANGE = 10;
   public static final EntityType<Boat> ACACIA_BOAT;
   public static final EntityType<ChestBoat> ACACIA_CHEST_BOAT;
   public static final EntityType<Allay> ALLAY;
   public static final EntityType<AreaEffectCloud> AREA_EFFECT_CLOUD;
   public static final EntityType<Armadillo> ARMADILLO;
   public static final EntityType<ArmorStand> ARMOR_STAND;
   public static final EntityType<Arrow> ARROW;
   public static final EntityType<Axolotl> AXOLOTL;
   public static final EntityType<ChestRaft> BAMBOO_CHEST_RAFT;
   public static final EntityType<Raft> BAMBOO_RAFT;
   public static final EntityType<Bat> BAT;
   public static final EntityType<Bee> BEE;
   public static final EntityType<Boat> BIRCH_BOAT;
   public static final EntityType<ChestBoat> BIRCH_CHEST_BOAT;
   public static final EntityType<Blaze> BLAZE;
   public static final EntityType<Display.BlockDisplay> BLOCK_DISPLAY;
   public static final EntityType<Bogged> BOGGED;
   public static final EntityType<Breeze> BREEZE;
   public static final EntityType<BreezeWindCharge> BREEZE_WIND_CHARGE;
   public static final EntityType<Camel> CAMEL;
   public static final EntityType<CamelHusk> CAMEL_HUSK;
   public static final EntityType<Cat> CAT;
   public static final EntityType<CaveSpider> CAVE_SPIDER;
   public static final EntityType<Boat> CHERRY_BOAT;
   public static final EntityType<ChestBoat> CHERRY_CHEST_BOAT;
   public static final EntityType<MinecartChest> CHEST_MINECART;
   public static final EntityType<Chicken> CHICKEN;
   public static final EntityType<Cod> COD;
   public static final EntityType<CopperGolem> COPPER_GOLEM;
   public static final EntityType<MinecartCommandBlock> COMMAND_BLOCK_MINECART;
   public static final EntityType<Cow> COW;
   public static final EntityType<Creaking> CREAKING;
   public static final EntityType<Creeper> CREEPER;
   public static final EntityType<Boat> DARK_OAK_BOAT;
   public static final EntityType<ChestBoat> DARK_OAK_CHEST_BOAT;
   public static final EntityType<Dolphin> DOLPHIN;
   public static final EntityType<Donkey> DONKEY;
   public static final EntityType<DragonFireball> DRAGON_FIREBALL;
   public static final EntityType<Drowned> DROWNED;
   public static final EntityType<ThrownEgg> EGG;
   public static final EntityType<ElderGuardian> ELDER_GUARDIAN;
   public static final EntityType<EnderMan> ENDERMAN;
   public static final EntityType<Endermite> ENDERMITE;
   public static final EntityType<EnderDragon> ENDER_DRAGON;
   public static final EntityType<ThrownEnderpearl> ENDER_PEARL;
   public static final EntityType<EndCrystal> END_CRYSTAL;
   public static final EntityType<Evoker> EVOKER;
   public static final EntityType<EvokerFangs> EVOKER_FANGS;
   public static final EntityType<ThrownExperienceBottle> EXPERIENCE_BOTTLE;
   public static final EntityType<ExperienceOrb> EXPERIENCE_ORB;
   public static final EntityType<EyeOfEnder> EYE_OF_ENDER;
   public static final EntityType<FallingBlockEntity> FALLING_BLOCK;
   public static final EntityType<LargeFireball> FIREBALL;
   public static final EntityType<FireworkRocketEntity> FIREWORK_ROCKET;
   public static final EntityType<Fox> FOX;
   public static final EntityType<Frog> FROG;
   public static final EntityType<MinecartFurnace> FURNACE_MINECART;
   public static final EntityType<Ghast> GHAST;
   public static final EntityType<HappyGhast> HAPPY_GHAST;
   public static final EntityType<Giant> GIANT;
   public static final EntityType<GlowItemFrame> GLOW_ITEM_FRAME;
   public static final EntityType<GlowSquid> GLOW_SQUID;
   public static final EntityType<Goat> GOAT;
   public static final EntityType<Guardian> GUARDIAN;
   public static final EntityType<Hoglin> HOGLIN;
   public static final EntityType<MinecartHopper> HOPPER_MINECART;
   public static final EntityType<Horse> HORSE;
   public static final EntityType<Husk> HUSK;
   public static final EntityType<Illusioner> ILLUSIONER;
   public static final EntityType<Interaction> INTERACTION;
   public static final EntityType<IronGolem> IRON_GOLEM;
   public static final EntityType<ItemEntity> ITEM;
   public static final EntityType<Display.ItemDisplay> ITEM_DISPLAY;
   public static final EntityType<ItemFrame> ITEM_FRAME;
   public static final EntityType<Boat> JUNGLE_BOAT;
   public static final EntityType<ChestBoat> JUNGLE_CHEST_BOAT;
   public static final EntityType<LeashFenceKnotEntity> LEASH_KNOT;
   public static final EntityType<LightningBolt> LIGHTNING_BOLT;
   public static final EntityType<Llama> LLAMA;
   public static final EntityType<LlamaSpit> LLAMA_SPIT;
   public static final EntityType<MagmaCube> MAGMA_CUBE;
   public static final EntityType<Boat> MANGROVE_BOAT;
   public static final EntityType<ChestBoat> MANGROVE_CHEST_BOAT;
   public static final EntityType<Mannequin> MANNEQUIN;
   public static final EntityType<Marker> MARKER;
   public static final EntityType<Minecart> MINECART;
   public static final EntityType<MushroomCow> MOOSHROOM;
   public static final EntityType<Mule> MULE;
   public static final EntityType<Nautilus> NAUTILUS;
   public static final EntityType<Boat> OAK_BOAT;
   public static final EntityType<ChestBoat> OAK_CHEST_BOAT;
   public static final EntityType<Ocelot> OCELOT;
   public static final EntityType<OminousItemSpawner> OMINOUS_ITEM_SPAWNER;
   public static final EntityType<Painting> PAINTING;
   public static final EntityType<Boat> PALE_OAK_BOAT;
   public static final EntityType<ChestBoat> PALE_OAK_CHEST_BOAT;
   public static final EntityType<Panda> PANDA;
   public static final EntityType<Parched> PARCHED;
   public static final EntityType<Parrot> PARROT;
   public static final EntityType<Phantom> PHANTOM;
   public static final EntityType<Pig> PIG;
   public static final EntityType<Piglin> PIGLIN;
   public static final EntityType<PiglinBrute> PIGLIN_BRUTE;
   public static final EntityType<Pillager> PILLAGER;
   public static final EntityType<PolarBear> POLAR_BEAR;
   public static final EntityType<ThrownSplashPotion> SPLASH_POTION;
   public static final EntityType<ThrownLingeringPotion> LINGERING_POTION;
   public static final EntityType<Pufferfish> PUFFERFISH;
   public static final EntityType<Rabbit> RABBIT;
   public static final EntityType<Ravager> RAVAGER;
   public static final EntityType<Salmon> SALMON;
   public static final EntityType<Sheep> SHEEP;
   public static final EntityType<Shulker> SHULKER;
   public static final EntityType<ShulkerBullet> SHULKER_BULLET;
   public static final EntityType<Silverfish> SILVERFISH;
   public static final EntityType<Skeleton> SKELETON;
   public static final EntityType<SkeletonHorse> SKELETON_HORSE;
   public static final EntityType<Slime> SLIME;
   public static final EntityType<SmallFireball> SMALL_FIREBALL;
   public static final EntityType<Sniffer> SNIFFER;
   public static final EntityType<Snowball> SNOWBALL;
   public static final EntityType<SnowGolem> SNOW_GOLEM;
   public static final EntityType<MinecartSpawner> SPAWNER_MINECART;
   public static final EntityType<SpectralArrow> SPECTRAL_ARROW;
   public static final EntityType<Spider> SPIDER;
   public static final EntityType<Boat> SPRUCE_BOAT;
   public static final EntityType<ChestBoat> SPRUCE_CHEST_BOAT;
   public static final EntityType<Squid> SQUID;
   public static final EntityType<Stray> STRAY;
   public static final EntityType<Strider> STRIDER;
   public static final EntityType<Tadpole> TADPOLE;
   public static final EntityType<Display.TextDisplay> TEXT_DISPLAY;
   public static final EntityType<PrimedTnt> TNT;
   public static final EntityType<MinecartTNT> TNT_MINECART;
   public static final EntityType<TraderLlama> TRADER_LLAMA;
   public static final EntityType<ThrownTrident> TRIDENT;
   public static final EntityType<TropicalFish> TROPICAL_FISH;
   public static final EntityType<Turtle> TURTLE;
   public static final EntityType<Vex> VEX;
   public static final EntityType<Villager> VILLAGER;
   public static final EntityType<Vindicator> VINDICATOR;
   public static final EntityType<WanderingTrader> WANDERING_TRADER;
   public static final EntityType<Warden> WARDEN;
   public static final EntityType<WindCharge> WIND_CHARGE;
   public static final EntityType<Witch> WITCH;
   public static final EntityType<WitherBoss> WITHER;
   public static final EntityType<WitherSkeleton> WITHER_SKELETON;
   public static final EntityType<WitherSkull> WITHER_SKULL;
   public static final EntityType<Wolf> WOLF;
   public static final EntityType<Zoglin> ZOGLIN;
   public static final EntityType<Zombie> ZOMBIE;
   public static final EntityType<ZombieHorse> ZOMBIE_HORSE;
   public static final EntityType<ZombieNautilus> ZOMBIE_NAUTILUS;
   public static final EntityType<ZombieVillager> ZOMBIE_VILLAGER;
   public static final EntityType<ZombifiedPiglin> ZOMBIFIED_PIGLIN;
   public static final EntityType<Player> PLAYER;
   public static final EntityType<FishingHook> FISHING_BOBBER;
   private static final Set<EntityType<?>> OP_ONLY_CUSTOM_DATA;
   private final EntityFactory<T> factory;
   private final MobCategory category;
   private final ImmutableSet<Block> immuneTo;
   private final boolean serialize;
   private final boolean summon;
   private final boolean fireImmune;
   private final boolean canSpawnFarFromPlayer;
   private final int clientTrackingRange;
   private final int updateInterval;
   private final String descriptionId;
   private @Nullable Component description;
   private final Optional<ResourceKey<LootTable>> lootTable;
   private final EntityDimensions dimensions;
   private final float spawnDimensionsScale;
   private final FeatureFlagSet requiredFeatures;
   private final boolean allowedInPeaceful;

   private static <T extends Entity> EntityType<T> register(final ResourceKey<EntityType<?>> id, final Builder<T> builder) {
      return (EntityType)Registry.register(BuiltInRegistries.ENTITY_TYPE, (ResourceKey)id, builder.build(id));
   }

   private static ResourceKey<EntityType<?>> vanillaEntityId(final String vanillaId) {
      return ResourceKey.create(Registries.ENTITY_TYPE, Identifier.withDefaultNamespace(vanillaId));
   }

   private static <T extends Entity> EntityType<T> register(final String vanillaId, final Builder<T> builder) {
      return register(vanillaEntityId(vanillaId), builder);
   }

   public static Identifier getKey(final EntityType<?> type) {
      return BuiltInRegistries.ENTITY_TYPE.getKey(type);
   }

   public static Optional<EntityType<?>> byString(final String id) {
      return BuiltInRegistries.ENTITY_TYPE.getOptional(Identifier.tryParse(id));
   }

   public EntityType(final EntityFactory<T> factory, final MobCategory category, final boolean serialize, final boolean summon, final boolean fireImmune, final boolean canSpawnFarFromPlayer, final ImmutableSet<Block> immuneTo, final EntityDimensions dimensions, final float spawnDimensionsScale, final int clientTrackingRange, final int updateInterval, final String descriptionId, final Optional<ResourceKey<LootTable>> lootTable, final FeatureFlagSet requiredFeatures, final boolean allowedInPeaceful) {
      super();
      this.builtInRegistryHolder = BuiltInRegistries.ENTITY_TYPE.createIntrusiveHolder(this);
      this.factory = factory;
      this.category = category;
      this.canSpawnFarFromPlayer = canSpawnFarFromPlayer;
      this.serialize = serialize;
      this.summon = summon;
      this.fireImmune = fireImmune;
      this.immuneTo = immuneTo;
      this.dimensions = dimensions;
      this.spawnDimensionsScale = spawnDimensionsScale;
      this.clientTrackingRange = clientTrackingRange;
      this.updateInterval = updateInterval;
      this.descriptionId = descriptionId;
      this.lootTable = lootTable;
      this.requiredFeatures = requiredFeatures;
      this.allowedInPeaceful = allowedInPeaceful;
   }

   public @Nullable T spawn(final ServerLevel level, final @Nullable ItemStack itemStack, final @Nullable LivingEntity user, final BlockPos spawnPos, final EntitySpawnReason spawnReason, final boolean tryMoveDown, final boolean movedUp) {
      Consumer<T> postSpawnConfig;
      if (itemStack != null) {
         postSpawnConfig = createDefaultStackConfig(level, itemStack, user);
      } else {
         postSpawnConfig = (entity) -> {
         };
      }

      return (T)this.spawn(level, postSpawnConfig, spawnPos, spawnReason, tryMoveDown, movedUp);
   }

   public static <T extends Entity> Consumer<T> createDefaultStackConfig(final Level level, final ItemStack itemStack, final @Nullable LivingEntity user) {
      return appendDefaultStackConfig((entity) -> {
      }, level, itemStack, user);
   }

   public static <T extends Entity> Consumer<T> appendDefaultStackConfig(final Consumer<T> initialConfig, final Level level, final ItemStack itemStack, final @Nullable LivingEntity user) {
      return appendCustomEntityStackConfig(appendComponentsConfig(initialConfig, itemStack), level, itemStack, user);
   }

   public static <T extends Entity> Consumer<T> appendComponentsConfig(final Consumer<T> initialConfig, final ItemStack itemStack) {
      return initialConfig.andThen((entity) -> entity.applyComponentsFromItemStack(itemStack));
   }

   public static <T extends Entity> Consumer<T> appendCustomEntityStackConfig(final Consumer<T> initialConfig, final Level level, final ItemStack itemStack, final @Nullable LivingEntity user) {
      TypedEntityData<EntityType<?>> entityData = (TypedEntityData)itemStack.get(DataComponents.ENTITY_DATA);
      return entityData != null ? initialConfig.andThen((entity) -> updateCustomEntityTag(level, user, entity, entityData)) : initialConfig;
   }

   public @Nullable T spawn(final ServerLevel level, final BlockPos spawnPos, final EntitySpawnReason spawnReason) {
      return (T)this.spawn(level, (Consumer)null, spawnPos, spawnReason, false, false);
   }

   public @Nullable T spawn(final ServerLevel level, final @Nullable Consumer<T> postSpawnConfig, final BlockPos spawnPos, final EntitySpawnReason spawnReason, final boolean tryMoveDown, final boolean movedUp) {
      T entity = this.create(level, postSpawnConfig, spawnPos, spawnReason, tryMoveDown, movedUp);
      if (entity != null) {
         level.addFreshEntityWithPassengers(entity);
         if (entity instanceof Mob) {
            Mob mob = (Mob)entity;
            mob.playAmbientSound();
         }
      }

      return entity;
   }

   public @Nullable T create(final ServerLevel level, final @Nullable Consumer<T> postSpawnConfig, final BlockPos spawnPos, final EntitySpawnReason spawnReason, final boolean tryMoveDown, final boolean movedUp) {
      T entity = this.create(level, spawnReason);
      if (entity == null) {
         return null;
      } else {
         double yOff;
         if (tryMoveDown) {
            entity.setPos((double)spawnPos.getX() + 0.5, (double)(spawnPos.getY() + 1), (double)spawnPos.getZ() + 0.5);
            yOff = getYOffset(level, spawnPos, movedUp, entity.getBoundingBox());
         } else {
            yOff = 0.0;
         }

         entity.snapTo((double)spawnPos.getX() + 0.5, (double)spawnPos.getY() + yOff, (double)spawnPos.getZ() + 0.5, Mth.wrapDegrees(level.getRandom().nextFloat() * 360.0F), 0.0F);
         if (entity instanceof Mob) {
            Mob mob = (Mob)entity;
            mob.yHeadRot = mob.getYRot();
            mob.yBodyRot = mob.getYRot();
            mob.finalizeSpawn(level, level.getCurrentDifficultyAt(mob.blockPosition()), spawnReason, (SpawnGroupData)null);
         }

         if (postSpawnConfig != null) {
            postSpawnConfig.accept(entity);
         }

         return entity;
      }
   }

   protected static double getYOffset(final LevelReader level, final BlockPos spawnPos, final boolean movedUp, final AABB entityBox) {
      AABB aabb = new AABB(spawnPos);
      if (movedUp) {
         aabb = aabb.expandTowards(0.0, -1.0, 0.0);
      }

      Iterable<VoxelShape> shapes = level.getCollisions((Entity)null, aabb);
      return 1.0 + Shapes.collide(Direction.Axis.Y, entityBox, shapes, movedUp ? -2.0 : -1.0);
   }

   public static void updateCustomEntityTag(final Level level, final @Nullable LivingEntity user, final @Nullable Entity entity, final TypedEntityData<EntityType<?>> entityData) {
      MinecraftServer server = level.getServer();
      if (server != null && entity != null) {
         if (entity.getType() == entityData.type()) {
            if (!level.isClientSide() && entity.getType().onlyOpCanSetNbt()) {
               if (!(user instanceof Player)) {
                  return;
               }

               Player player = (Player)user;
               if (!server.getPlayerList().isOp(player.nameAndId())) {
                  return;
               }
            }

            entityData.loadInto(entity);
         }
      }
   }

   public boolean canSerialize() {
      return this.serialize;
   }

   public boolean canSummon() {
      return this.summon;
   }

   public boolean fireImmune() {
      return this.fireImmune;
   }

   public boolean canSpawnFarFromPlayer() {
      return this.canSpawnFarFromPlayer;
   }

   public MobCategory getCategory() {
      return this.category;
   }

   public String getDescriptionId() {
      return this.descriptionId;
   }

   public Component getDescription() {
      if (this.description == null) {
         this.description = Component.translatable(this.getDescriptionId());
      }

      return this.description;
   }

   public String toString() {
      return this.getDescriptionId();
   }

   public String toShortString() {
      int dot = this.getDescriptionId().lastIndexOf(46);
      return dot == -1 ? this.getDescriptionId() : this.getDescriptionId().substring(dot + 1);
   }

   public Optional<ResourceKey<LootTable>> getDefaultLootTable() {
      return this.lootTable;
   }

   public float getWidth() {
      return this.dimensions.width();
   }

   public float getHeight() {
      return this.dimensions.height();
   }

   public FeatureFlagSet requiredFeatures() {
      return this.requiredFeatures;
   }

   public @Nullable T create(final Level level, final EntitySpawnReason reason) {
      return (T)(!this.isEnabled(level.enabledFeatures()) ? null : this.factory.create(this, level));
   }

   public static Optional<Entity> create(final ValueInput input, final Level level, final EntitySpawnReason reason) {
      return Util.<Entity>ifElse(by(input).map((type) -> type.create(level, reason)), (entity) -> entity.load(input), () -> LOGGER.warn("Skipping Entity with id {}", input.getStringOr("id", "[invalid]")));
   }

   public static Optional<Entity> create(final EntityType<?> type, final ValueInput input, final Level level, final EntitySpawnReason reason) {
      Optional<Entity> entity = Optional.ofNullable(type.create(level, reason));
      entity.ifPresent((e) -> e.load(input));
      return entity;
   }

   public AABB getSpawnAABB(final double x, final double y, final double z) {
      float halfWidth = this.spawnDimensionsScale * this.getWidth() / 2.0F;
      float height = this.spawnDimensionsScale * this.getHeight();
      return new AABB(x - (double)halfWidth, y, z - (double)halfWidth, x + (double)halfWidth, y + (double)height, z + (double)halfWidth);
   }

   public boolean isBlockDangerous(final BlockState state) {
      if (this.immuneTo.contains(state.getBlock())) {
         return false;
      } else if (!this.fireImmune && NodeEvaluator.isBurningBlock(state)) {
         return true;
      } else {
         return state.is(Blocks.WITHER_ROSE) || state.is(Blocks.SWEET_BERRY_BUSH) || state.is(Blocks.CACTUS) || state.is(Blocks.POWDER_SNOW);
      }
   }

   public EntityDimensions getDimensions() {
      return this.dimensions;
   }

   public static Optional<EntityType<?>> by(final ValueInput input) {
      return input.<EntityType<?>>read("id", CODEC);
   }

   public static @Nullable Entity loadEntityRecursive(final CompoundTag tag, final Level level, final EntitySpawnReason reason, final EntityProcessor postLoad) {
      try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(LOGGER)) {
         return loadEntityRecursive(TagValueInput.create(reporter, level.registryAccess(), tag), level, reason, postLoad);
      }
   }

   public static @Nullable Entity loadEntityRecursive(final EntityType<?> type, final CompoundTag tag, final Level level, final EntitySpawnReason reason, final EntityProcessor postLoad) {
      try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(LOGGER)) {
         return loadEntityRecursive(type, TagValueInput.create(reporter, level.registryAccess(), tag), level, reason, postLoad);
      }
   }

   public static @Nullable Entity loadEntityRecursive(final ValueInput input, final Level level, final EntitySpawnReason reason, final EntityProcessor postLoad) {
      Optional var10000 = loadStaticEntity(input, level, reason);
      Objects.requireNonNull(postLoad);
      return (Entity)var10000.map(postLoad::process).map((entity) -> loadPassengersRecursive(entity, input, level, reason, postLoad)).orElse((Object)null);
   }

   public static @Nullable Entity loadEntityRecursive(final EntityType<?> type, final ValueInput input, final Level level, final EntitySpawnReason reason, final EntityProcessor postLoad) {
      Optional var10000 = loadStaticEntity(type, input, level, reason);
      Objects.requireNonNull(postLoad);
      return (Entity)var10000.map(postLoad::process).map((entity) -> loadPassengersRecursive(entity, input, level, reason, postLoad)).orElse((Object)null);
   }

   private static Entity loadPassengersRecursive(final Entity entity, final ValueInput input, final Level level, final EntitySpawnReason reason, final EntityProcessor postLoad) {
      for(ValueInput passengerTag : input.childrenListOrEmpty("Passengers")) {
         Entity passenger = loadEntityRecursive(passengerTag, level, reason, postLoad);
         if (passenger != null) {
            passenger.startRiding(entity, true, false);
         }
      }

      return entity;
   }

   public static Stream<Entity> loadEntitiesRecursive(final ValueInput.ValueInputList entities, final Level level, final EntitySpawnReason reason) {
      return entities.stream().mapMulti((tag, output) -> loadEntityRecursive((ValueInput)tag, level, reason, (entity) -> {
            output.accept(entity);
            return entity;
         }));
   }

   private static Optional<Entity> loadStaticEntity(final ValueInput input, final Level level, final EntitySpawnReason reason) {
      try {
         return create(input, level, reason);
      } catch (RuntimeException e) {
         LOGGER.warn("Exception loading entity: ", e);
         return Optional.empty();
      }
   }

   private static Optional<Entity> loadStaticEntity(final EntityType<?> type, final ValueInput input, final Level level, final EntitySpawnReason reason) {
      try {
         return create(type, input, level, reason);
      } catch (RuntimeException e) {
         LOGGER.warn("Exception loading entity: ", e);
         return Optional.empty();
      }
   }

   public int clientTrackingRange() {
      return this.clientTrackingRange;
   }

   public int updateInterval() {
      return this.updateInterval;
   }

   public boolean trackDeltas() {
      return this != PLAYER && this != LLAMA_SPIT && this != WITHER && this != BAT && this != ITEM_FRAME && this != GLOW_ITEM_FRAME && this != LEASH_KNOT && this != PAINTING && this != END_CRYSTAL && this != EVOKER_FANGS;
   }

   public @Nullable T tryCast(final Entity entity) {
      return (T)(entity.getType() == this ? entity : null);
   }

   public Class<? extends Entity> getBaseClass() {
      return Entity.class;
   }

   /** @deprecated */
   @Deprecated
   public Holder.Reference<EntityType<?>> builtInRegistryHolder() {
      return this.builtInRegistryHolder;
   }

   public boolean isAllowedInPeaceful() {
      return this.allowedInPeaceful;
   }

   private static EntityFactory<Boat> boatFactory(final Supplier<Item> boatItem) {
      return (entityType, level) -> new Boat(entityType, level, boatItem);
   }

   private static EntityFactory<ChestBoat> chestBoatFactory(final Supplier<Item> dropItem) {
      return (entityType, level) -> new ChestBoat(entityType, level, dropItem);
   }

   private static EntityFactory<Raft> raftFactory(final Supplier<Item> dropItem) {
      return (entityType, level) -> new Raft(entityType, level, dropItem);
   }

   private static EntityFactory<ChestRaft> chestRaftFactory(final Supplier<Item> dropItem) {
      return (entityType, level) -> new ChestRaft(entityType, level, dropItem);
   }

   public boolean onlyOpCanSetNbt() {
      return OP_ONLY_CUSTOM_DATA.contains(this);
   }

   static {
      CODEC = BuiltInRegistries.ENTITY_TYPE.byNameCodec();
      STREAM_CODEC = ByteBufCodecs.registry(Registries.ENTITY_TYPE);
      ACACIA_BOAT = register("acacia_boat", EntityType.Builder.of(boatFactory(() -> Items.ACACIA_BOAT), MobCategory.MISC).noLootTable().sized(1.375F, 0.5625F).eyeHeight(0.5625F).clientTrackingRange(10));
      ACACIA_CHEST_BOAT = register("acacia_chest_boat", EntityType.Builder.of(chestBoatFactory(() -> Items.ACACIA_CHEST_BOAT), MobCategory.MISC).noLootTable().sized(1.375F, 0.5625F).eyeHeight(0.5625F).clientTrackingRange(10));
      ALLAY = register("allay", EntityType.Builder.of(Allay::new, MobCategory.CREATURE).sized(0.35F, 0.6F).eyeHeight(0.36F).ridingOffset(0.04F).clientTrackingRange(8).updateInterval(2));
      AREA_EFFECT_CLOUD = register("area_effect_cloud", EntityType.Builder.of(AreaEffectCloud::new, MobCategory.MISC).noLootTable().fireImmune().sized(6.0F, 0.5F).clientTrackingRange(10).updateInterval(2147483647));
      ARMADILLO = register("armadillo", EntityType.Builder.of(Armadillo::new, MobCategory.CREATURE).sized(0.7F, 0.65F).eyeHeight(0.26F).clientTrackingRange(10));
      ARMOR_STAND = register("armor_stand", EntityType.Builder.of(ArmorStand::new, MobCategory.MISC).sized(0.5F, 1.975F).eyeHeight(1.7775F).clientTrackingRange(10));
      ARROW = register("arrow", EntityType.Builder.of(Arrow::new, MobCategory.MISC).noLootTable().sized(0.5F, 0.5F).eyeHeight(0.13F).clientTrackingRange(4).updateInterval(20));
      AXOLOTL = register("axolotl", EntityType.Builder.of(Axolotl::new, MobCategory.AXOLOTLS).sized(0.75F, 0.42F).eyeHeight(0.2751F).clientTrackingRange(10));
      BAMBOO_CHEST_RAFT = register("bamboo_chest_raft", EntityType.Builder.of(chestRaftFactory(() -> Items.BAMBOO_CHEST_RAFT), MobCategory.MISC).noLootTable().sized(1.375F, 0.5625F).eyeHeight(0.5625F).clientTrackingRange(10));
      BAMBOO_RAFT = register("bamboo_raft", EntityType.Builder.of(raftFactory(() -> Items.BAMBOO_RAFT), MobCategory.MISC).noLootTable().sized(1.375F, 0.5625F).eyeHeight(0.5625F).clientTrackingRange(10));
      BAT = register("bat", EntityType.Builder.of(Bat::new, MobCategory.AMBIENT).sized(0.5F, 0.9F).eyeHeight(0.45F).clientTrackingRange(5));
      BEE = register("bee", EntityType.Builder.of(Bee::new, MobCategory.CREATURE).sized(0.7F, 0.6F).eyeHeight(0.3F).clientTrackingRange(8));
      BIRCH_BOAT = register("birch_boat", EntityType.Builder.of(boatFactory(() -> Items.BIRCH_BOAT), MobCategory.MISC).noLootTable().sized(1.375F, 0.5625F).eyeHeight(0.5625F).clientTrackingRange(10));
      BIRCH_CHEST_BOAT = register("birch_chest_boat", EntityType.Builder.of(chestBoatFactory(() -> Items.BIRCH_CHEST_BOAT), MobCategory.MISC).noLootTable().sized(1.375F, 0.5625F).eyeHeight(0.5625F).clientTrackingRange(10));
      BLAZE = register("blaze", EntityType.Builder.of(Blaze::new, MobCategory.MONSTER).fireImmune().sized(0.6F, 1.8F).clientTrackingRange(8).notInPeaceful());
      BLOCK_DISPLAY = register("block_display", EntityType.Builder.of(Display.BlockDisplay::new, MobCategory.MISC).noLootTable().sized(0.0F, 0.0F).clientTrackingRange(10).updateInterval(1));
      BOGGED = register("bogged", EntityType.Builder.of(Bogged::new, MobCategory.MONSTER).sized(0.6F, 1.99F).eyeHeight(1.74F).ridingOffset(-0.7F).clientTrackingRange(8).notInPeaceful());
      BREEZE = register("breeze", EntityType.Builder.of(Breeze::new, MobCategory.MONSTER).sized(0.6F, 1.77F).eyeHeight(1.3452F).clientTrackingRange(10).notInPeaceful());
      BREEZE_WIND_CHARGE = register("breeze_wind_charge", EntityType.Builder.of(BreezeWindCharge::new, MobCategory.MISC).noLootTable().sized(0.3125F, 0.3125F).eyeHeight(0.0F).clientTrackingRange(4).updateInterval(10));
      CAMEL = register("camel", EntityType.Builder.of(Camel::new, MobCategory.CREATURE).sized(1.7F, 2.375F).eyeHeight(2.275F).clientTrackingRange(10));
      CAMEL_HUSK = register("camel_husk", EntityType.Builder.of(CamelHusk::new, MobCategory.MONSTER).sized(1.7F, 2.375F).eyeHeight(2.275F).clientTrackingRange(10));
      CAT = register("cat", EntityType.Builder.of(Cat::new, MobCategory.CREATURE).sized(0.6F, 0.7F).eyeHeight(0.35F).passengerAttachments(0.5125F).clientTrackingRange(8));
      CAVE_SPIDER = register("cave_spider", EntityType.Builder.of(CaveSpider::new, MobCategory.MONSTER).sized(0.7F, 0.5F).eyeHeight(0.45F).clientTrackingRange(8).notInPeaceful());
      CHERRY_BOAT = register("cherry_boat", EntityType.Builder.of(boatFactory(() -> Items.CHERRY_BOAT), MobCategory.MISC).noLootTable().sized(1.375F, 0.5625F).eyeHeight(0.5625F).clientTrackingRange(10));
      CHERRY_CHEST_BOAT = register("cherry_chest_boat", EntityType.Builder.of(chestBoatFactory(() -> Items.CHERRY_CHEST_BOAT), MobCategory.MISC).noLootTable().sized(1.375F, 0.5625F).eyeHeight(0.5625F).clientTrackingRange(10));
      CHEST_MINECART = register("chest_minecart", EntityType.Builder.of(MinecartChest::new, MobCategory.MISC).noLootTable().sized(0.98F, 0.7F).passengerAttachments(0.1875F).clientTrackingRange(8));
      CHICKEN = register("chicken", EntityType.Builder.of(Chicken::new, MobCategory.CREATURE).sized(0.4F, 0.7F).eyeHeight(0.644F).passengerAttachments(new Vec3(0.0, 0.7, -0.1)).clientTrackingRange(10));
      COD = register("cod", EntityType.Builder.of(Cod::new, MobCategory.WATER_AMBIENT).sized(0.5F, 0.3F).eyeHeight(0.195F).clientTrackingRange(4));
      COPPER_GOLEM = register("copper_golem", EntityType.Builder.of(CopperGolem::new, MobCategory.MISC).sized(0.49F, 0.98F).eyeHeight(0.8125F).clientTrackingRange(10));
      COMMAND_BLOCK_MINECART = register("command_block_minecart", EntityType.Builder.of(MinecartCommandBlock::new, MobCategory.MISC).noLootTable().sized(0.98F, 0.7F).passengerAttachments(0.1875F).clientTrackingRange(8));
      COW = register("cow", EntityType.Builder.of(Cow::new, MobCategory.CREATURE).sized(0.9F, 1.4F).eyeHeight(1.3F).passengerAttachments(1.36875F).clientTrackingRange(10));
      CREAKING = register("creaking", EntityType.Builder.of(Creaking::new, MobCategory.MONSTER).sized(0.9F, 2.7F).eyeHeight(2.3F).clientTrackingRange(8).notInPeaceful());
      CREEPER = register("creeper", EntityType.Builder.of(Creeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8).notInPeaceful());
      DARK_OAK_BOAT = register("dark_oak_boat", EntityType.Builder.of(boatFactory(() -> Items.DARK_OAK_BOAT), MobCategory.MISC).noLootTable().sized(1.375F, 0.5625F).eyeHeight(0.5625F).clientTrackingRange(10));
      DARK_OAK_CHEST_BOAT = register("dark_oak_chest_boat", EntityType.Builder.of(chestBoatFactory(() -> Items.DARK_OAK_CHEST_BOAT), MobCategory.MISC).noLootTable().sized(1.375F, 0.5625F).eyeHeight(0.5625F).clientTrackingRange(10));
      DOLPHIN = register("dolphin", EntityType.Builder.of(Dolphin::new, MobCategory.WATER_CREATURE).sized(0.9F, 0.6F).eyeHeight(0.3F));
      DONKEY = register("donkey", EntityType.Builder.of(Donkey::new, MobCategory.CREATURE).sized(1.3964844F, 1.5F).eyeHeight(1.425F).passengerAttachments(1.1125F).clientTrackingRange(10));
      DRAGON_FIREBALL = register("dragon_fireball", EntityType.Builder.of(DragonFireball::new, MobCategory.MISC).noLootTable().sized(1.0F, 1.0F).clientTrackingRange(4).updateInterval(10));
      DROWNED = register("drowned", EntityType.Builder.of(Drowned::new, MobCategory.MONSTER).sized(0.6F, 1.95F).eyeHeight(1.74F).passengerAttachments(2.0125F).ridingOffset(-0.7F).clientTrackingRange(8).notInPeaceful());
      EGG = register("egg", EntityType.Builder.of(ThrownEgg::new, MobCategory.MISC).noLootTable().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));
      ELDER_GUARDIAN = register("elder_guardian", EntityType.Builder.of(ElderGuardian::new, MobCategory.MONSTER).sized(1.9975F, 1.9975F).eyeHeight(0.99875F).passengerAttachments(2.350625F).clientTrackingRange(10).notInPeaceful());
      ENDERMAN = register("enderman", EntityType.Builder.of(EnderMan::new, MobCategory.MONSTER).sized(0.6F, 2.9F).eyeHeight(2.55F).passengerAttachments(2.80625F).clientTrackingRange(8).notInPeaceful());
      ENDERMITE = register("endermite", EntityType.Builder.of(Endermite::new, MobCategory.MONSTER).sized(0.4F, 0.3F).eyeHeight(0.13F).passengerAttachments(0.2375F).clientTrackingRange(8).notInPeaceful());
      ENDER_DRAGON = register("ender_dragon", EntityType.Builder.of(EnderDragon::new, MobCategory.MONSTER).fireImmune().sized(16.0F, 8.0F).passengerAttachments(3.0F).clientTrackingRange(10));
      ENDER_PEARL = register("ender_pearl", EntityType.Builder.of(ThrownEnderpearl::new, MobCategory.MISC).noLootTable().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));
      END_CRYSTAL = register("end_crystal", EntityType.Builder.of(EndCrystal::new, MobCategory.MISC).noLootTable().fireImmune().sized(2.0F, 2.0F).clientTrackingRange(16).updateInterval(2147483647));
      EVOKER = register("evoker", EntityType.Builder.of(Evoker::new, MobCategory.MONSTER).sized(0.6F, 1.95F).passengerAttachments(2.0F).ridingOffset(-0.6F).clientTrackingRange(8).notInPeaceful());
      EVOKER_FANGS = register("evoker_fangs", EntityType.Builder.of(EvokerFangs::new, MobCategory.MISC).noLootTable().sized(0.5F, 0.8F).clientTrackingRange(6).updateInterval(2));
      EXPERIENCE_BOTTLE = register("experience_bottle", EntityType.Builder.of(ThrownExperienceBottle::new, MobCategory.MISC).noLootTable().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));
      EXPERIENCE_ORB = register("experience_orb", EntityType.Builder.of(ExperienceOrb::new, MobCategory.MISC).noLootTable().sized(0.5F, 0.5F).clientTrackingRange(6).updateInterval(20));
      EYE_OF_ENDER = register("eye_of_ender", EntityType.Builder.of(EyeOfEnder::new, MobCategory.MISC).noLootTable().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(4));
      FALLING_BLOCK = register("falling_block", EntityType.Builder.of(FallingBlockEntity::new, MobCategory.MISC).noLootTable().sized(0.98F, 0.98F).clientTrackingRange(10).updateInterval(20));
      FIREBALL = register("fireball", EntityType.Builder.of(LargeFireball::new, MobCategory.MISC).noLootTable().sized(1.0F, 1.0F).clientTrackingRange(4).updateInterval(10));
      FIREWORK_ROCKET = register("firework_rocket", EntityType.Builder.of(FireworkRocketEntity::new, MobCategory.MISC).noLootTable().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));
      FOX = register("fox", EntityType.Builder.of(Fox::new, MobCategory.CREATURE).sized(0.6F, 0.7F).eyeHeight(0.4F).passengerAttachments(new Vec3(0.0, 0.6375, -0.25)).clientTrackingRange(8).immuneTo(Blocks.SWEET_BERRY_BUSH));
      FROG = register("frog", EntityType.Builder.of(Frog::new, MobCategory.CREATURE).sized(0.5F, 0.5F).passengerAttachments(new Vec3(0.0, 0.375, -0.25)).clientTrackingRange(10));
      FURNACE_MINECART = register("furnace_minecart", EntityType.Builder.of(MinecartFurnace::new, MobCategory.MISC).noLootTable().sized(0.98F, 0.7F).passengerAttachments(0.1875F).clientTrackingRange(8));
      GHAST = register("ghast", EntityType.Builder.of(Ghast::new, MobCategory.MONSTER).fireImmune().sized(4.0F, 4.0F).eyeHeight(2.6F).passengerAttachments(4.0625F).ridingOffset(0.5F).clientTrackingRange(10).notInPeaceful());
      HAPPY_GHAST = register("happy_ghast", EntityType.Builder.of(HappyGhast::new, MobCategory.CREATURE).sized(4.0F, 4.0F).eyeHeight(2.6F).passengerAttachments(new Vec3(0.0, 4.0, 1.7), new Vec3(-1.7, 4.0, 0.0), new Vec3(0.0, 4.0, -1.7), new Vec3(1.7, 4.0, 0.0)).ridingOffset(0.5F).clientTrackingRange(10));
      GIANT = register("giant", EntityType.Builder.of(Giant::new, MobCategory.MONSTER).sized(3.6F, 12.0F).eyeHeight(10.44F).ridingOffset(-3.75F).clientTrackingRange(10).notInPeaceful());
      GLOW_ITEM_FRAME = register("glow_item_frame", EntityType.Builder.of(GlowItemFrame::new, MobCategory.MISC).noLootTable().sized(0.5F, 0.5F).eyeHeight(0.0F).clientTrackingRange(10).updateInterval(2147483647));
      GLOW_SQUID = register("glow_squid", EntityType.Builder.of(GlowSquid::new, MobCategory.UNDERGROUND_WATER_CREATURE).sized(0.8F, 0.8F).eyeHeight(0.4F).clientTrackingRange(10));
      GOAT = register("goat", EntityType.Builder.of(Goat::new, MobCategory.CREATURE).sized(0.9F, 1.3F).passengerAttachments(1.1125F).clientTrackingRange(10));
      GUARDIAN = register("guardian", EntityType.Builder.of(Guardian::new, MobCategory.MONSTER).sized(0.85F, 0.85F).eyeHeight(0.425F).passengerAttachments(0.975F).clientTrackingRange(8).notInPeaceful());
      HOGLIN = register("hoglin", EntityType.Builder.of(Hoglin::new, MobCategory.MONSTER).sized(1.3964844F, 1.4F).passengerAttachments(1.49375F).clientTrackingRange(8));
      HOPPER_MINECART = register("hopper_minecart", EntityType.Builder.of(MinecartHopper::new, MobCategory.MISC).noLootTable().sized(0.98F, 0.7F).passengerAttachments(0.1875F).clientTrackingRange(8));
      HORSE = register("horse", EntityType.Builder.of(Horse::new, MobCategory.CREATURE).sized(1.3964844F, 1.6F).eyeHeight(1.52F).passengerAttachments(1.44375F).clientTrackingRange(10));
      HUSK = register("husk", EntityType.Builder.of(Husk::new, MobCategory.MONSTER).sized(0.6F, 1.95F).eyeHeight(1.74F).passengerAttachments(2.075F).ridingOffset(-0.7F).clientTrackingRange(8).notInPeaceful());
      ILLUSIONER = register("illusioner", EntityType.Builder.of(Illusioner::new, MobCategory.MONSTER).sized(0.6F, 1.95F).passengerAttachments(2.0F).ridingOffset(-0.6F).clientTrackingRange(8).notInPeaceful());
      INTERACTION = register("interaction", EntityType.Builder.of(Interaction::new, MobCategory.MISC).noLootTable().sized(0.0F, 0.0F).clientTrackingRange(10));
      IRON_GOLEM = register("iron_golem", EntityType.Builder.of(IronGolem::new, MobCategory.MISC).sized(1.4F, 2.7F).clientTrackingRange(10));
      ITEM = register("item", EntityType.Builder.of(ItemEntity::new, MobCategory.MISC).noLootTable().sized(0.25F, 0.25F).eyeHeight(0.2125F).clientTrackingRange(6).updateInterval(20));
      ITEM_DISPLAY = register("item_display", EntityType.Builder.of(Display.ItemDisplay::new, MobCategory.MISC).noLootTable().sized(0.0F, 0.0F).clientTrackingRange(10).updateInterval(1));
      ITEM_FRAME = register("item_frame", EntityType.Builder.of(ItemFrame::new, MobCategory.MISC).noLootTable().sized(0.5F, 0.5F).eyeHeight(0.0F).clientTrackingRange(10).updateInterval(2147483647));
      JUNGLE_BOAT = register("jungle_boat", EntityType.Builder.of(boatFactory(() -> Items.JUNGLE_BOAT), MobCategory.MISC).noLootTable().sized(1.375F, 0.5625F).eyeHeight(0.5625F).clientTrackingRange(10));
      JUNGLE_CHEST_BOAT = register("jungle_chest_boat", EntityType.Builder.of(chestBoatFactory(() -> Items.JUNGLE_CHEST_BOAT), MobCategory.MISC).noLootTable().sized(1.375F, 0.5625F).eyeHeight(0.5625F).clientTrackingRange(10));
      LEASH_KNOT = register("leash_knot", EntityType.Builder.of(LeashFenceKnotEntity::new, MobCategory.MISC).noLootTable().noSave().sized(0.375F, 0.5F).eyeHeight(0.0625F).clientTrackingRange(10).updateInterval(2147483647));
      LIGHTNING_BOLT = register("lightning_bolt", EntityType.Builder.of(LightningBolt::new, MobCategory.MISC).noLootTable().noSave().sized(0.0F, 0.0F).clientTrackingRange(16).updateInterval(2147483647));
      LLAMA = register("llama", EntityType.Builder.of(Llama::new, MobCategory.CREATURE).sized(0.9F, 1.87F).eyeHeight(1.7765F).passengerAttachments(new Vec3(0.0, 1.37, -0.3)).clientTrackingRange(10));
      LLAMA_SPIT = register("llama_spit", EntityType.Builder.of(LlamaSpit::new, MobCategory.MISC).noLootTable().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));
      MAGMA_CUBE = register("magma_cube", EntityType.Builder.of(MagmaCube::new, MobCategory.MONSTER).fireImmune().sized(0.52F, 0.52F).eyeHeight(0.325F).spawnDimensionsScale(4.0F).clientTrackingRange(8).notInPeaceful());
      MANGROVE_BOAT = register("mangrove_boat", EntityType.Builder.of(boatFactory(() -> Items.MANGROVE_BOAT), MobCategory.MISC).noLootTable().sized(1.375F, 0.5625F).eyeHeight(0.5625F).clientTrackingRange(10));
      MANGROVE_CHEST_BOAT = register("mangrove_chest_boat", EntityType.Builder.of(chestBoatFactory(() -> Items.MANGROVE_CHEST_BOAT), MobCategory.MISC).noLootTable().sized(1.375F, 0.5625F).eyeHeight(0.5625F).clientTrackingRange(10));
      MANNEQUIN = register("mannequin", EntityType.Builder.of(Mannequin::create, MobCategory.MISC).sized(0.6F, 1.8F).eyeHeight(1.62F).vehicleAttachment(Avatar.DEFAULT_VEHICLE_ATTACHMENT).clientTrackingRange(32).updateInterval(2));
      MARKER = register("marker", EntityType.Builder.of(Marker::new, MobCategory.MISC).noLootTable().sized(0.0F, 0.0F).clientTrackingRange(0));
      MINECART = register("minecart", EntityType.Builder.of(Minecart::new, MobCategory.MISC).noLootTable().sized(0.98F, 0.7F).passengerAttachments(0.1875F).clientTrackingRange(8));
      MOOSHROOM = register("mooshroom", EntityType.Builder.of(MushroomCow::new, MobCategory.CREATURE).sized(0.9F, 1.4F).eyeHeight(1.3F).passengerAttachments(1.36875F).clientTrackingRange(10));
      MULE = register("mule", EntityType.Builder.of(Mule::new, MobCategory.CREATURE).sized(1.3964844F, 1.6F).eyeHeight(1.52F).passengerAttachments(1.2125F).clientTrackingRange(8));
      NAUTILUS = register("nautilus", EntityType.Builder.of(Nautilus::new, MobCategory.WATER_CREATURE).sized(0.875F, 0.95F).passengerAttachments(1.1375F).eyeHeight(0.2751F).clientTrackingRange(10));
      OAK_BOAT = register("oak_boat", EntityType.Builder.of(boatFactory(() -> Items.OAK_BOAT), MobCategory.MISC).noLootTable().sized(1.375F, 0.5625F).eyeHeight(0.5625F).clientTrackingRange(10));
      OAK_CHEST_BOAT = register("oak_chest_boat", EntityType.Builder.of(chestBoatFactory(() -> Items.OAK_CHEST_BOAT), MobCategory.MISC).noLootTable().sized(1.375F, 0.5625F).eyeHeight(0.5625F).clientTrackingRange(10));
      OCELOT = register("ocelot", EntityType.Builder.of(Ocelot::new, MobCategory.CREATURE).sized(0.6F, 0.7F).passengerAttachments(0.6375F).clientTrackingRange(10));
      OMINOUS_ITEM_SPAWNER = register("ominous_item_spawner", EntityType.Builder.of(OminousItemSpawner::new, MobCategory.MISC).noLootTable().sized(0.25F, 0.25F).clientTrackingRange(8));
      PAINTING = register("painting", EntityType.Builder.of(Painting::new, MobCategory.MISC).noLootTable().sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(2147483647));
      PALE_OAK_BOAT = register("pale_oak_boat", EntityType.Builder.of(boatFactory(() -> Items.PALE_OAK_BOAT), MobCategory.MISC).noLootTable().sized(1.375F, 0.5625F).eyeHeight(0.5625F).clientTrackingRange(10));
      PALE_OAK_CHEST_BOAT = register("pale_oak_chest_boat", EntityType.Builder.of(chestBoatFactory(() -> Items.PALE_OAK_CHEST_BOAT), MobCategory.MISC).noLootTable().sized(1.375F, 0.5625F).eyeHeight(0.5625F).clientTrackingRange(10));
      PANDA = register("panda", EntityType.Builder.of(Panda::new, MobCategory.CREATURE).sized(1.3F, 1.25F).clientTrackingRange(10));
      PARCHED = register("parched", EntityType.Builder.of(Parched::new, MobCategory.MONSTER).sized(0.6F, 1.99F).eyeHeight(1.74F).ridingOffset(-0.7F).clientTrackingRange(8).notInPeaceful());
      PARROT = register("parrot", EntityType.Builder.of(Parrot::new, MobCategory.CREATURE).sized(0.5F, 0.9F).eyeHeight(0.54F).passengerAttachments(0.4625F).clientTrackingRange(8));
      PHANTOM = register("phantom", EntityType.Builder.of(Phantom::new, MobCategory.MONSTER).sized(0.9F, 0.5F).eyeHeight(0.175F).passengerAttachments(0.3375F).ridingOffset(-0.125F).clientTrackingRange(8).notInPeaceful());
      PIG = register("pig", EntityType.Builder.of(Pig::new, MobCategory.CREATURE).sized(0.9F, 0.9F).passengerAttachments(0.86875F).clientTrackingRange(10));
      PIGLIN = register("piglin", EntityType.Builder.of(Piglin::new, MobCategory.MONSTER).sized(0.6F, 1.95F).eyeHeight(1.79F).passengerAttachments(2.0125F).ridingOffset(-0.7F).clientTrackingRange(8));
      PIGLIN_BRUTE = register("piglin_brute", EntityType.Builder.of(PiglinBrute::new, MobCategory.MONSTER).sized(0.6F, 1.95F).eyeHeight(1.79F).passengerAttachments(2.0125F).ridingOffset(-0.7F).clientTrackingRange(8).notInPeaceful());
      PILLAGER = register("pillager", EntityType.Builder.of(Pillager::new, MobCategory.MONSTER).canSpawnFarFromPlayer().sized(0.6F, 1.95F).passengerAttachments(2.0F).ridingOffset(-0.6F).clientTrackingRange(8).notInPeaceful());
      POLAR_BEAR = register("polar_bear", EntityType.Builder.of(PolarBear::new, MobCategory.CREATURE).immuneTo(Blocks.POWDER_SNOW).sized(1.4F, 1.4F).clientTrackingRange(10));
      SPLASH_POTION = register("splash_potion", EntityType.Builder.of(ThrownSplashPotion::new, MobCategory.MISC).noLootTable().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));
      LINGERING_POTION = register("lingering_potion", EntityType.Builder.of(ThrownLingeringPotion::new, MobCategory.MISC).noLootTable().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));
      PUFFERFISH = register("pufferfish", EntityType.Builder.of(Pufferfish::new, MobCategory.WATER_AMBIENT).sized(0.7F, 0.7F).eyeHeight(0.455F).clientTrackingRange(4));
      RABBIT = register("rabbit", EntityType.Builder.of(Rabbit::new, MobCategory.CREATURE).sized(0.49F, 0.6F).eyeHeight(0.59F).clientTrackingRange(8));
      RAVAGER = register("ravager", EntityType.Builder.of(Ravager::new, MobCategory.MONSTER).sized(1.95F, 2.2F).passengerAttachments(new Vec3(0.0, 2.2625, -0.0625)).clientTrackingRange(10).notInPeaceful());
      SALMON = register("salmon", EntityType.Builder.of(Salmon::new, MobCategory.WATER_AMBIENT).sized(0.7F, 0.4F).eyeHeight(0.26F).clientTrackingRange(4));
      SHEEP = register("sheep", EntityType.Builder.of(Sheep::new, MobCategory.CREATURE).sized(0.9F, 1.3F).eyeHeight(1.235F).passengerAttachments(1.2375F).clientTrackingRange(10));
      SHULKER = register("shulker", EntityType.Builder.of(Shulker::new, MobCategory.MONSTER).fireImmune().canSpawnFarFromPlayer().sized(1.0F, 1.0F).eyeHeight(0.5F).clientTrackingRange(10));
      SHULKER_BULLET = register("shulker_bullet", EntityType.Builder.of(ShulkerBullet::new, MobCategory.MISC).noLootTable().sized(0.3125F, 0.3125F).clientTrackingRange(8));
      SILVERFISH = register("silverfish", EntityType.Builder.of(Silverfish::new, MobCategory.MONSTER).sized(0.4F, 0.3F).eyeHeight(0.13F).passengerAttachments(0.2375F).clientTrackingRange(8).notInPeaceful());
      SKELETON = register("skeleton", EntityType.Builder.of(Skeleton::new, MobCategory.MONSTER).sized(0.6F, 1.99F).eyeHeight(1.74F).ridingOffset(-0.7F).clientTrackingRange(8).notInPeaceful());
      SKELETON_HORSE = register("skeleton_horse", EntityType.Builder.of(SkeletonHorse::new, MobCategory.CREATURE).sized(1.3964844F, 1.6F).eyeHeight(1.52F).passengerAttachments(1.31875F).clientTrackingRange(10));
      SLIME = register("slime", EntityType.Builder.of(Slime::new, MobCategory.MONSTER).sized(0.52F, 0.52F).eyeHeight(0.325F).spawnDimensionsScale(4.0F).clientTrackingRange(10).notInPeaceful());
      SMALL_FIREBALL = register("small_fireball", EntityType.Builder.of(SmallFireball::new, MobCategory.MISC).noLootTable().sized(0.3125F, 0.3125F).clientTrackingRange(4).updateInterval(10));
      SNIFFER = register("sniffer", EntityType.Builder.of(Sniffer::new, MobCategory.CREATURE).sized(1.9F, 1.75F).eyeHeight(1.05F).passengerAttachments(2.09375F).nameTagOffset(2.05F).clientTrackingRange(10));
      SNOWBALL = register("snowball", EntityType.Builder.of(Snowball::new, MobCategory.MISC).noLootTable().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));
      SNOW_GOLEM = register("snow_golem", EntityType.Builder.of(SnowGolem::new, MobCategory.MISC).immuneTo(Blocks.POWDER_SNOW).sized(0.7F, 1.9F).eyeHeight(1.7F).clientTrackingRange(8));
      SPAWNER_MINECART = register("spawner_minecart", EntityType.Builder.of(MinecartSpawner::new, MobCategory.MISC).noLootTable().sized(0.98F, 0.7F).passengerAttachments(0.1875F).clientTrackingRange(8));
      SPECTRAL_ARROW = register("spectral_arrow", EntityType.Builder.of(SpectralArrow::new, MobCategory.MISC).noLootTable().sized(0.5F, 0.5F).eyeHeight(0.13F).clientTrackingRange(4).updateInterval(20));
      SPIDER = register("spider", EntityType.Builder.of(Spider::new, MobCategory.MONSTER).sized(1.4F, 0.9F).eyeHeight(0.65F).passengerAttachments(0.765F).clientTrackingRange(8).notInPeaceful());
      SPRUCE_BOAT = register("spruce_boat", EntityType.Builder.of(boatFactory(() -> Items.SPRUCE_BOAT), MobCategory.MISC).noLootTable().sized(1.375F, 0.5625F).eyeHeight(0.5625F).clientTrackingRange(10));
      SPRUCE_CHEST_BOAT = register("spruce_chest_boat", EntityType.Builder.of(chestBoatFactory(() -> Items.SPRUCE_CHEST_BOAT), MobCategory.MISC).noLootTable().sized(1.375F, 0.5625F).eyeHeight(0.5625F).clientTrackingRange(10));
      SQUID = register("squid", EntityType.Builder.of(Squid::new, MobCategory.WATER_CREATURE).sized(0.8F, 0.8F).eyeHeight(0.4F).clientTrackingRange(8));
      STRAY = register("stray", EntityType.Builder.of(Stray::new, MobCategory.MONSTER).sized(0.6F, 1.99F).eyeHeight(1.74F).ridingOffset(-0.7F).immuneTo(Blocks.POWDER_SNOW).clientTrackingRange(8).notInPeaceful());
      STRIDER = register("strider", EntityType.Builder.of(Strider::new, MobCategory.CREATURE).fireImmune().sized(0.9F, 1.7F).clientTrackingRange(10));
      TADPOLE = register("tadpole", EntityType.Builder.of(Tadpole::new, MobCategory.CREATURE).sized(0.4F, 0.3F).eyeHeight(0.19500001F).clientTrackingRange(10));
      TEXT_DISPLAY = register("text_display", EntityType.Builder.of(Display.TextDisplay::new, MobCategory.MISC).noLootTable().sized(0.0F, 0.0F).clientTrackingRange(10).updateInterval(1));
      TNT = register("tnt", EntityType.Builder.of(PrimedTnt::new, MobCategory.MISC).noLootTable().fireImmune().sized(0.98F, 0.98F).eyeHeight(0.15F).clientTrackingRange(10).updateInterval(10));
      TNT_MINECART = register("tnt_minecart", EntityType.Builder.of(MinecartTNT::new, MobCategory.MISC).noLootTable().sized(0.98F, 0.7F).passengerAttachments(0.1875F).clientTrackingRange(8));
      TRADER_LLAMA = register("trader_llama", EntityType.Builder.of(TraderLlama::new, MobCategory.CREATURE).sized(0.9F, 1.87F).eyeHeight(1.7765F).passengerAttachments(new Vec3(0.0, 1.37, -0.3)).clientTrackingRange(10));
      TRIDENT = register("trident", EntityType.Builder.of(ThrownTrident::new, MobCategory.MISC).noLootTable().sized(0.5F, 0.5F).eyeHeight(0.13F).clientTrackingRange(4).updateInterval(20));
      TROPICAL_FISH = register("tropical_fish", EntityType.Builder.of(TropicalFish::new, MobCategory.WATER_AMBIENT).sized(0.5F, 0.4F).eyeHeight(0.26F).clientTrackingRange(4));
      TURTLE = register("turtle", EntityType.Builder.of(Turtle::new, MobCategory.CREATURE).sized(1.2F, 0.4F).passengerAttachments(new Vec3(0.0, 0.55625, -0.25)).clientTrackingRange(10));
      VEX = register("vex", EntityType.Builder.of(Vex::new, MobCategory.MONSTER).fireImmune().sized(0.4F, 0.8F).eyeHeight(0.51875F).passengerAttachments(0.7375F).ridingOffset(0.04F).clientTrackingRange(8).notInPeaceful());
      VILLAGER = register("villager", EntityType.Builder.of(Villager::new, MobCategory.MISC).sized(0.6F, 1.95F).eyeHeight(1.62F).clientTrackingRange(10));
      VINDICATOR = register("vindicator", EntityType.Builder.of(Vindicator::new, MobCategory.MONSTER).sized(0.6F, 1.95F).passengerAttachments(2.0F).ridingOffset(-0.6F).clientTrackingRange(8).notInPeaceful());
      WANDERING_TRADER = register("wandering_trader", EntityType.Builder.of(WanderingTrader::new, MobCategory.CREATURE).sized(0.6F, 1.95F).eyeHeight(1.62F).clientTrackingRange(10));
      WARDEN = register("warden", EntityType.Builder.of(Warden::new, MobCategory.MONSTER).sized(0.9F, 2.9F).passengerAttachments(3.15F).attach(EntityAttachment.WARDEN_CHEST, 0.0F, 1.6F, 0.0F).clientTrackingRange(16).fireImmune().notInPeaceful());
      WIND_CHARGE = register("wind_charge", EntityType.Builder.of(WindCharge::new, MobCategory.MISC).noLootTable().sized(0.3125F, 0.3125F).eyeHeight(0.0F).clientTrackingRange(4).updateInterval(10));
      WITCH = register("witch", EntityType.Builder.of(Witch::new, MobCategory.MONSTER).sized(0.6F, 1.95F).eyeHeight(1.62F).passengerAttachments(2.2625F).clientTrackingRange(8).notInPeaceful());
      WITHER = register("wither", EntityType.Builder.of(WitherBoss::new, MobCategory.MONSTER).fireImmune().immuneTo(Blocks.WITHER_ROSE).sized(0.9F, 3.5F).clientTrackingRange(10).notInPeaceful());
      WITHER_SKELETON = register("wither_skeleton", EntityType.Builder.of(WitherSkeleton::new, MobCategory.MONSTER).fireImmune().immuneTo(Blocks.WITHER_ROSE).sized(0.7F, 2.4F).eyeHeight(2.1F).ridingOffset(-0.875F).clientTrackingRange(8).notInPeaceful());
      WITHER_SKULL = register("wither_skull", EntityType.Builder.of(WitherSkull::new, MobCategory.MISC).noLootTable().sized(0.3125F, 0.3125F).clientTrackingRange(4).updateInterval(10));
      WOLF = register("wolf", EntityType.Builder.of(Wolf::new, MobCategory.CREATURE).sized(0.6F, 0.85F).eyeHeight(0.68F).passengerAttachments(new Vec3(0.0, 0.81875, -0.0625)).clientTrackingRange(10));
      ZOGLIN = register("zoglin", EntityType.Builder.of(Zoglin::new, MobCategory.MONSTER).fireImmune().sized(1.3964844F, 1.4F).passengerAttachments(1.49375F).clientTrackingRange(8).notInPeaceful());
      ZOMBIE = register("zombie", EntityType.Builder.of(Zombie::new, MobCategory.MONSTER).sized(0.6F, 1.95F).eyeHeight(1.74F).passengerAttachments(2.0125F).ridingOffset(-0.7F).clientTrackingRange(8).notInPeaceful());
      ZOMBIE_HORSE = register("zombie_horse", EntityType.Builder.of(ZombieHorse::new, MobCategory.MONSTER).sized(1.3964844F, 1.6F).eyeHeight(1.52F).passengerAttachments(1.31875F).clientTrackingRange(10));
      ZOMBIE_NAUTILUS = register("zombie_nautilus", EntityType.Builder.of(ZombieNautilus::new, MobCategory.MONSTER).sized(0.875F, 0.95F).passengerAttachments(1.1375F).eyeHeight(0.2751F).clientTrackingRange(10));
      ZOMBIE_VILLAGER = register("zombie_villager", EntityType.Builder.of(ZombieVillager::new, MobCategory.MONSTER).sized(0.6F, 1.95F).passengerAttachments(2.125F).ridingOffset(-0.7F).eyeHeight(1.74F).clientTrackingRange(8).notInPeaceful());
      ZOMBIFIED_PIGLIN = register("zombified_piglin", EntityType.Builder.of(ZombifiedPiglin::new, MobCategory.MONSTER).fireImmune().sized(0.6F, 1.95F).eyeHeight(1.79F).passengerAttachments(2.0F).ridingOffset(-0.7F).clientTrackingRange(8).notInPeaceful());
      PLAYER = register("player", EntityType.Builder.createNothing(MobCategory.MISC).noSave().noSummon().sized(0.6F, 1.8F).eyeHeight(1.62F).vehicleAttachment(Avatar.DEFAULT_VEHICLE_ATTACHMENT).clientTrackingRange(32).updateInterval(2));
      FISHING_BOBBER = register("fishing_bobber", EntityType.Builder.of(FishingHook::new, MobCategory.MISC).noLootTable().noSave().noSummon().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(5));
      OP_ONLY_CUSTOM_DATA = Set.of(FALLING_BLOCK, COMMAND_BLOCK_MINECART, SPAWNER_MINECART);
   }

   public static class Builder<T extends Entity> {
      private final EntityFactory<T> factory;
      private final MobCategory category;
      private ImmutableSet<Block> immuneTo = ImmutableSet.of();
      private boolean serialize = true;
      private boolean summon = true;
      private boolean fireImmune;
      private boolean canSpawnFarFromPlayer;
      private int clientTrackingRange = 5;
      private int updateInterval = 3;
      private EntityDimensions dimensions = EntityDimensions.scalable(0.6F, 1.8F);
      private float spawnDimensionsScale = 1.0F;
      private EntityAttachments.Builder attachments = EntityAttachments.builder();
      private FeatureFlagSet requiredFeatures;
      private DependantName<EntityType<?>, Optional<ResourceKey<LootTable>>> lootTable;
      private final DependantName<EntityType<?>, String> descriptionId;
      private boolean allowedInPeaceful;

      private Builder(final EntityFactory<T> factory, final MobCategory category) {
         super();
         this.requiredFeatures = FeatureFlags.VANILLA_SET;
         this.lootTable = (id) -> Optional.of(ResourceKey.create(Registries.LOOT_TABLE, id.identifier().withPrefix("entities/")));
         this.descriptionId = (id) -> Util.makeDescriptionId("entity", id.identifier());
         this.allowedInPeaceful = true;
         this.factory = factory;
         this.category = category;
         this.canSpawnFarFromPlayer = category == MobCategory.CREATURE || category == MobCategory.MISC;
      }

      public static <T extends Entity> Builder<T> of(final EntityFactory<T> factory, final MobCategory category) {
         return new Builder<T>(factory, category);
      }

      public static <T extends Entity> Builder<T> createNothing(final MobCategory category) {
         return new Builder<T>((t, l) -> null, category);
      }

      public Builder<T> sized(final float width, final float height) {
         this.dimensions = EntityDimensions.scalable(width, height);
         return this;
      }

      public Builder<T> spawnDimensionsScale(final float scale) {
         this.spawnDimensionsScale = scale;
         return this;
      }

      public Builder<T> eyeHeight(final float eyeHeight) {
         this.dimensions = this.dimensions.withEyeHeight(eyeHeight);
         return this;
      }

      public Builder<T> passengerAttachments(final float... offsetYs) {
         for(float offsetY : offsetYs) {
            this.attachments = this.attachments.attach(EntityAttachment.PASSENGER, 0.0F, offsetY, 0.0F);
         }

         return this;
      }

      public Builder<T> passengerAttachments(final Vec3... points) {
         for(Vec3 point : points) {
            this.attachments = this.attachments.attach(EntityAttachment.PASSENGER, point);
         }

         return this;
      }

      public Builder<T> vehicleAttachment(final Vec3 point) {
         return this.attach(EntityAttachment.VEHICLE, point);
      }

      public Builder<T> ridingOffset(final float ridingOffset) {
         return this.attach(EntityAttachment.VEHICLE, 0.0F, -ridingOffset, 0.0F);
      }

      public Builder<T> nameTagOffset(final float nameTagOffset) {
         return this.attach(EntityAttachment.NAME_TAG, 0.0F, nameTagOffset, 0.0F);
      }

      public Builder<T> attach(final EntityAttachment attachment, final float x, final float y, final float z) {
         this.attachments = this.attachments.attach(attachment, x, y, z);
         return this;
      }

      public Builder<T> attach(final EntityAttachment attachment, final Vec3 point) {
         this.attachments = this.attachments.attach(attachment, point);
         return this;
      }

      public Builder<T> noSummon() {
         this.summon = false;
         return this;
      }

      public Builder<T> noSave() {
         this.serialize = false;
         return this;
      }

      public Builder<T> fireImmune() {
         this.fireImmune = true;
         return this;
      }

      public Builder<T> immuneTo(final Block... blocks) {
         this.immuneTo = ImmutableSet.copyOf(blocks);
         return this;
      }

      public Builder<T> canSpawnFarFromPlayer() {
         this.canSpawnFarFromPlayer = true;
         return this;
      }

      public Builder<T> clientTrackingRange(final int clientChunkRange) {
         this.clientTrackingRange = clientChunkRange;
         return this;
      }

      public Builder<T> updateInterval(final int updateInterval) {
         this.updateInterval = updateInterval;
         return this;
      }

      public Builder<T> requiredFeatures(final FeatureFlag... flags) {
         this.requiredFeatures = FeatureFlags.REGISTRY.subset(flags);
         return this;
      }

      public Builder<T> noLootTable() {
         this.lootTable = DependantName.<EntityType<?>, Optional<ResourceKey<LootTable>>>fixed(Optional.empty());
         return this;
      }

      public Builder<T> notInPeaceful() {
         this.allowedInPeaceful = false;
         return this;
      }

      public EntityType<T> build(final ResourceKey<EntityType<?>> name) {
         if (this.serialize) {
            Util.fetchChoiceType(References.ENTITY_TREE, name.identifier().toString());
         }

         return new EntityType<T>(this.factory, this.category, this.serialize, this.summon, this.fireImmune, this.canSpawnFarFromPlayer, this.immuneTo, this.dimensions.withAttachments(this.attachments), this.spawnDimensionsScale, this.clientTrackingRange, this.updateInterval, this.descriptionId.get(name), this.lootTable.get(name), this.requiredFeatures, this.allowedInPeaceful);
      }
   }

   @FunctionalInterface
   public interface EntityFactory<T extends Entity> {
      @Nullable T create(final EntityType<T> entityType, final Level level);
   }
}
