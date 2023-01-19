package net.minecraft.world.entity;

import com.google.common.collect.ImmutableSet;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Salmon;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.animal.horse.Donkey;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.animal.horse.Mule;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.animal.horse.TraderLlama;
import net.minecraft.world.entity.animal.horse.ZombieHorse;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.decoration.GlowItemFrame;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Giant;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Illusioner;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.entity.projectile.EyeOfEnder;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.entity.projectile.ThrownExperienceBottle;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.minecraft.world.entity.vehicle.MinecartCommandBlock;
import net.minecraft.world.entity.vehicle.MinecartFurnace;
import net.minecraft.world.entity.vehicle.MinecartHopper;
import net.minecraft.world.entity.vehicle.MinecartSpawner;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import org.slf4j.Logger;

public class EntityType<T extends Entity> implements FeatureElement, EntityTypeTest<Entity, T> {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final String ENTITY_TAG = "EntityTag";
   private final Holder.Reference<EntityType<?>> builtInRegistryHolder = BuiltInRegistries.ENTITY_TYPE.createIntrusiveHolder(this);
   private static final float MAGIC_HORSE_WIDTH = 1.3964844F;
   public static final EntityType<Allay> ALLAY = register(
      "allay", EntityType.Builder.<Allay>of(Allay::new, MobCategory.CREATURE).sized(0.35F, 0.6F).clientTrackingRange(8).updateInterval(2)
   );
   public static final EntityType<AreaEffectCloud> AREA_EFFECT_CLOUD = register(
      "area_effect_cloud",
      EntityType.Builder.<AreaEffectCloud>of(AreaEffectCloud::new, MobCategory.MISC)
         .fireImmune()
         .sized(6.0F, 0.5F)
         .clientTrackingRange(10)
         .updateInterval(2147483647)
   );
   public static final EntityType<ArmorStand> ARMOR_STAND = register(
      "armor_stand", EntityType.Builder.<ArmorStand>of(ArmorStand::new, MobCategory.MISC).sized(0.5F, 1.975F).clientTrackingRange(10)
   );
   public static final EntityType<Arrow> ARROW = register(
      "arrow", EntityType.Builder.<Arrow>of(Arrow::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20)
   );
   public static final EntityType<Axolotl> AXOLOTL = register(
      "axolotl", EntityType.Builder.<Axolotl>of(Axolotl::new, MobCategory.AXOLOTLS).sized(0.75F, 0.42F).clientTrackingRange(10)
   );
   public static final EntityType<Bat> BAT = register(
      "bat", EntityType.Builder.<Bat>of(Bat::new, MobCategory.AMBIENT).sized(0.5F, 0.9F).clientTrackingRange(5)
   );
   public static final EntityType<Bee> BEE = register(
      "bee", EntityType.Builder.<Bee>of(Bee::new, MobCategory.CREATURE).sized(0.7F, 0.6F).clientTrackingRange(8)
   );
   public static final EntityType<Blaze> BLAZE = register(
      "blaze", EntityType.Builder.<Blaze>of(Blaze::new, MobCategory.MONSTER).fireImmune().sized(0.6F, 1.8F).clientTrackingRange(8)
   );
   public static final EntityType<Boat> BOAT = register(
      "boat", EntityType.Builder.<Boat>of(Boat::new, MobCategory.MISC).sized(1.375F, 0.5625F).clientTrackingRange(10)
   );
   public static final EntityType<ChestBoat> CHEST_BOAT = register(
      "chest_boat", EntityType.Builder.<ChestBoat>of(ChestBoat::new, MobCategory.MISC).sized(1.375F, 0.5625F).clientTrackingRange(10)
   );
   public static final EntityType<Cat> CAT = register(
      "cat", EntityType.Builder.<Cat>of(Cat::new, MobCategory.CREATURE).sized(0.6F, 0.7F).clientTrackingRange(8)
   );
   public static final EntityType<Camel> CAMEL = register(
      "camel",
      EntityType.Builder.<Camel>of(Camel::new, MobCategory.CREATURE).sized(1.7F, 2.375F).clientTrackingRange(10).requiredFeatures(FeatureFlags.UPDATE_1_20)
   );
   public static final EntityType<CaveSpider> CAVE_SPIDER = register(
      "cave_spider", EntityType.Builder.<CaveSpider>of(CaveSpider::new, MobCategory.MONSTER).sized(0.7F, 0.5F).clientTrackingRange(8)
   );
   public static final EntityType<Chicken> CHICKEN = register(
      "chicken", EntityType.Builder.<Chicken>of(Chicken::new, MobCategory.CREATURE).sized(0.4F, 0.7F).clientTrackingRange(10)
   );
   public static final EntityType<Cod> COD = register(
      "cod", EntityType.Builder.<Cod>of(Cod::new, MobCategory.WATER_AMBIENT).sized(0.5F, 0.3F).clientTrackingRange(4)
   );
   public static final EntityType<Cow> COW = register(
      "cow", EntityType.Builder.<Cow>of(Cow::new, MobCategory.CREATURE).sized(0.9F, 1.4F).clientTrackingRange(10)
   );
   public static final EntityType<Creeper> CREEPER = register(
      "creeper", EntityType.Builder.<Creeper>of(Creeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8)
   );
   public static final EntityType<Dolphin> DOLPHIN = register(
      "dolphin", EntityType.Builder.<Dolphin>of(Dolphin::new, MobCategory.WATER_CREATURE).sized(0.9F, 0.6F)
   );
   public static final EntityType<Donkey> DONKEY = register(
      "donkey", EntityType.Builder.<Donkey>of(Donkey::new, MobCategory.CREATURE).sized(1.3964844F, 1.5F).clientTrackingRange(10)
   );
   public static final EntityType<DragonFireball> DRAGON_FIREBALL = register(
      "dragon_fireball",
      EntityType.Builder.<DragonFireball>of(DragonFireball::new, MobCategory.MISC).sized(1.0F, 1.0F).clientTrackingRange(4).updateInterval(10)
   );
   public static final EntityType<Drowned> DROWNED = register(
      "drowned", EntityType.Builder.<Drowned>of(Drowned::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8)
   );
   public static final EntityType<ElderGuardian> ELDER_GUARDIAN = register(
      "elder_guardian", EntityType.Builder.<ElderGuardian>of(ElderGuardian::new, MobCategory.MONSTER).sized(1.9975F, 1.9975F).clientTrackingRange(10)
   );
   public static final EntityType<EndCrystal> END_CRYSTAL = register(
      "end_crystal", EntityType.Builder.<EndCrystal>of(EndCrystal::new, MobCategory.MISC).sized(2.0F, 2.0F).clientTrackingRange(16).updateInterval(2147483647)
   );
   public static final EntityType<EnderDragon> ENDER_DRAGON = register(
      "ender_dragon", EntityType.Builder.<EnderDragon>of(EnderDragon::new, MobCategory.MONSTER).fireImmune().sized(16.0F, 8.0F).clientTrackingRange(10)
   );
   public static final EntityType<EnderMan> ENDERMAN = register(
      "enderman", EntityType.Builder.<EnderMan>of(EnderMan::new, MobCategory.MONSTER).sized(0.6F, 2.9F).clientTrackingRange(8)
   );
   public static final EntityType<Endermite> ENDERMITE = register(
      "endermite", EntityType.Builder.<Endermite>of(Endermite::new, MobCategory.MONSTER).sized(0.4F, 0.3F).clientTrackingRange(8)
   );
   public static final EntityType<Evoker> EVOKER = register(
      "evoker", EntityType.Builder.<Evoker>of(Evoker::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8)
   );
   public static final EntityType<EvokerFangs> EVOKER_FANGS = register(
      "evoker_fangs", EntityType.Builder.<EvokerFangs>of(EvokerFangs::new, MobCategory.MISC).sized(0.5F, 0.8F).clientTrackingRange(6).updateInterval(2)
   );
   public static final EntityType<ExperienceOrb> EXPERIENCE_ORB = register(
      "experience_orb", EntityType.Builder.<ExperienceOrb>of(ExperienceOrb::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(6).updateInterval(20)
   );
   public static final EntityType<EyeOfEnder> EYE_OF_ENDER = register(
      "eye_of_ender", EntityType.Builder.<EyeOfEnder>of(EyeOfEnder::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(4)
   );
   public static final EntityType<FallingBlockEntity> FALLING_BLOCK = register(
      "falling_block",
      EntityType.Builder.<FallingBlockEntity>of(FallingBlockEntity::new, MobCategory.MISC).sized(0.98F, 0.98F).clientTrackingRange(10).updateInterval(20)
   );
   public static final EntityType<FireworkRocketEntity> FIREWORK_ROCKET = register(
      "firework_rocket",
      EntityType.Builder.<FireworkRocketEntity>of(FireworkRocketEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10)
   );
   public static final EntityType<Fox> FOX = register(
      "fox", EntityType.Builder.<Fox>of(Fox::new, MobCategory.CREATURE).sized(0.6F, 0.7F).clientTrackingRange(8).immuneTo(Blocks.SWEET_BERRY_BUSH)
   );
   public static final EntityType<Frog> FROG = register(
      "frog", EntityType.Builder.<Frog>of(Frog::new, MobCategory.CREATURE).sized(0.5F, 0.5F).clientTrackingRange(10)
   );
   public static final EntityType<Ghast> GHAST = register(
      "ghast", EntityType.Builder.<Ghast>of(Ghast::new, MobCategory.MONSTER).fireImmune().sized(4.0F, 4.0F).clientTrackingRange(10)
   );
   public static final EntityType<Giant> GIANT = register(
      "giant", EntityType.Builder.<Giant>of(Giant::new, MobCategory.MONSTER).sized(3.6F, 12.0F).clientTrackingRange(10)
   );
   public static final EntityType<GlowItemFrame> GLOW_ITEM_FRAME = register(
      "glow_item_frame",
      EntityType.Builder.<GlowItemFrame>of(GlowItemFrame::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(2147483647)
   );
   public static final EntityType<GlowSquid> GLOW_SQUID = register(
      "glow_squid", EntityType.Builder.<GlowSquid>of(GlowSquid::new, MobCategory.UNDERGROUND_WATER_CREATURE).sized(0.8F, 0.8F).clientTrackingRange(10)
   );
   public static final EntityType<Goat> GOAT = register(
      "goat", EntityType.Builder.<Goat>of(Goat::new, MobCategory.CREATURE).sized(0.9F, 1.3F).clientTrackingRange(10)
   );
   public static final EntityType<Guardian> GUARDIAN = register(
      "guardian", EntityType.Builder.<Guardian>of(Guardian::new, MobCategory.MONSTER).sized(0.85F, 0.85F).clientTrackingRange(8)
   );
   public static final EntityType<Hoglin> HOGLIN = register(
      "hoglin", EntityType.Builder.<Hoglin>of(Hoglin::new, MobCategory.MONSTER).sized(1.3964844F, 1.4F).clientTrackingRange(8)
   );
   public static final EntityType<Horse> HORSE = register(
      "horse", EntityType.Builder.<Horse>of(Horse::new, MobCategory.CREATURE).sized(1.3964844F, 1.6F).clientTrackingRange(10)
   );
   public static final EntityType<Husk> HUSK = register(
      "husk", EntityType.Builder.<Husk>of(Husk::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8)
   );
   public static final EntityType<Illusioner> ILLUSIONER = register(
      "illusioner", EntityType.Builder.<Illusioner>of(Illusioner::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8)
   );
   public static final EntityType<IronGolem> IRON_GOLEM = register(
      "iron_golem", EntityType.Builder.<IronGolem>of(IronGolem::new, MobCategory.MISC).sized(1.4F, 2.7F).clientTrackingRange(10)
   );
   public static final EntityType<ItemEntity> ITEM = register(
      "item", EntityType.Builder.<ItemEntity>of(ItemEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(6).updateInterval(20)
   );
   public static final EntityType<ItemFrame> ITEM_FRAME = register(
      "item_frame", EntityType.Builder.<ItemFrame>of(ItemFrame::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(2147483647)
   );
   public static final EntityType<LargeFireball> FIREBALL = register(
      "fireball", EntityType.Builder.<LargeFireball>of(LargeFireball::new, MobCategory.MISC).sized(1.0F, 1.0F).clientTrackingRange(4).updateInterval(10)
   );
   public static final EntityType<LeashFenceKnotEntity> LEASH_KNOT = register(
      "leash_knot",
      EntityType.Builder.<LeashFenceKnotEntity>of(LeashFenceKnotEntity::new, MobCategory.MISC)
         .noSave()
         .sized(0.375F, 0.5F)
         .clientTrackingRange(10)
         .updateInterval(2147483647)
   );
   public static final EntityType<LightningBolt> LIGHTNING_BOLT = register(
      "lightning_bolt",
      EntityType.Builder.<LightningBolt>of(LightningBolt::new, MobCategory.MISC).noSave().sized(0.0F, 0.0F).clientTrackingRange(16).updateInterval(2147483647)
   );
   public static final EntityType<Llama> LLAMA = register(
      "llama", EntityType.Builder.<Llama>of(Llama::new, MobCategory.CREATURE).sized(0.9F, 1.87F).clientTrackingRange(10)
   );
   public static final EntityType<LlamaSpit> LLAMA_SPIT = register(
      "llama_spit", EntityType.Builder.<LlamaSpit>of(LlamaSpit::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10)
   );
   public static final EntityType<MagmaCube> MAGMA_CUBE = register(
      "magma_cube", EntityType.Builder.<MagmaCube>of(MagmaCube::new, MobCategory.MONSTER).fireImmune().sized(2.04F, 2.04F).clientTrackingRange(8)
   );
   public static final EntityType<Marker> MARKER = register(
      "marker", EntityType.Builder.<Marker>of(Marker::new, MobCategory.MISC).sized(0.0F, 0.0F).clientTrackingRange(0)
   );
   public static final EntityType<Minecart> MINECART = register(
      "minecart", EntityType.Builder.<Minecart>of(Minecart::new, MobCategory.MISC).sized(0.98F, 0.7F).clientTrackingRange(8)
   );
   public static final EntityType<MinecartChest> CHEST_MINECART = register(
      "chest_minecart", EntityType.Builder.<MinecartChest>of(MinecartChest::new, MobCategory.MISC).sized(0.98F, 0.7F).clientTrackingRange(8)
   );
   public static final EntityType<MinecartCommandBlock> COMMAND_BLOCK_MINECART = register(
      "command_block_minecart",
      EntityType.Builder.<MinecartCommandBlock>of(MinecartCommandBlock::new, MobCategory.MISC).sized(0.98F, 0.7F).clientTrackingRange(8)
   );
   public static final EntityType<MinecartFurnace> FURNACE_MINECART = register(
      "furnace_minecart", EntityType.Builder.<MinecartFurnace>of(MinecartFurnace::new, MobCategory.MISC).sized(0.98F, 0.7F).clientTrackingRange(8)
   );
   public static final EntityType<MinecartHopper> HOPPER_MINECART = register(
      "hopper_minecart", EntityType.Builder.<MinecartHopper>of(MinecartHopper::new, MobCategory.MISC).sized(0.98F, 0.7F).clientTrackingRange(8)
   );
   public static final EntityType<MinecartSpawner> SPAWNER_MINECART = register(
      "spawner_minecart", EntityType.Builder.<MinecartSpawner>of(MinecartSpawner::new, MobCategory.MISC).sized(0.98F, 0.7F).clientTrackingRange(8)
   );
   public static final EntityType<MinecartTNT> TNT_MINECART = register(
      "tnt_minecart", EntityType.Builder.<MinecartTNT>of(MinecartTNT::new, MobCategory.MISC).sized(0.98F, 0.7F).clientTrackingRange(8)
   );
   public static final EntityType<Mule> MULE = register(
      "mule", EntityType.Builder.<Mule>of(Mule::new, MobCategory.CREATURE).sized(1.3964844F, 1.6F).clientTrackingRange(8)
   );
   public static final EntityType<MushroomCow> MOOSHROOM = register(
      "mooshroom", EntityType.Builder.<MushroomCow>of(MushroomCow::new, MobCategory.CREATURE).sized(0.9F, 1.4F).clientTrackingRange(10)
   );
   public static final EntityType<Ocelot> OCELOT = register(
      "ocelot", EntityType.Builder.<Ocelot>of(Ocelot::new, MobCategory.CREATURE).sized(0.6F, 0.7F).clientTrackingRange(10)
   );
   public static final EntityType<Painting> PAINTING = register(
      "painting", EntityType.Builder.<Painting>of(Painting::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(2147483647)
   );
   public static final EntityType<Panda> PANDA = register(
      "panda", EntityType.Builder.<Panda>of(Panda::new, MobCategory.CREATURE).sized(1.3F, 1.25F).clientTrackingRange(10)
   );
   public static final EntityType<Parrot> PARROT = register(
      "parrot", EntityType.Builder.<Parrot>of(Parrot::new, MobCategory.CREATURE).sized(0.5F, 0.9F).clientTrackingRange(8)
   );
   public static final EntityType<Phantom> PHANTOM = register(
      "phantom", EntityType.Builder.<Phantom>of(Phantom::new, MobCategory.MONSTER).sized(0.9F, 0.5F).clientTrackingRange(8)
   );
   public static final EntityType<Pig> PIG = register(
      "pig", EntityType.Builder.<Pig>of(Pig::new, MobCategory.CREATURE).sized(0.9F, 0.9F).clientTrackingRange(10)
   );
   public static final EntityType<Piglin> PIGLIN = register(
      "piglin", EntityType.Builder.<Piglin>of(Piglin::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8)
   );
   public static final EntityType<PiglinBrute> PIGLIN_BRUTE = register(
      "piglin_brute", EntityType.Builder.<PiglinBrute>of(PiglinBrute::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8)
   );
   public static final EntityType<Pillager> PILLAGER = register(
      "pillager", EntityType.Builder.<Pillager>of(Pillager::new, MobCategory.MONSTER).canSpawnFarFromPlayer().sized(0.6F, 1.95F).clientTrackingRange(8)
   );
   public static final EntityType<PolarBear> POLAR_BEAR = register(
      "polar_bear",
      EntityType.Builder.<PolarBear>of(PolarBear::new, MobCategory.CREATURE).immuneTo(Blocks.POWDER_SNOW).sized(1.4F, 1.4F).clientTrackingRange(10)
   );
   public static final EntityType<PrimedTnt> TNT = register(
      "tnt", EntityType.Builder.<PrimedTnt>of(PrimedTnt::new, MobCategory.MISC).fireImmune().sized(0.98F, 0.98F).clientTrackingRange(10).updateInterval(10)
   );
   public static final EntityType<Pufferfish> PUFFERFISH = register(
      "pufferfish", EntityType.Builder.<Pufferfish>of(Pufferfish::new, MobCategory.WATER_AMBIENT).sized(0.7F, 0.7F).clientTrackingRange(4)
   );
   public static final EntityType<Rabbit> RABBIT = register(
      "rabbit", EntityType.Builder.<Rabbit>of(Rabbit::new, MobCategory.CREATURE).sized(0.4F, 0.5F).clientTrackingRange(8)
   );
   public static final EntityType<Ravager> RAVAGER = register(
      "ravager", EntityType.Builder.<Ravager>of(Ravager::new, MobCategory.MONSTER).sized(1.95F, 2.2F).clientTrackingRange(10)
   );
   public static final EntityType<Salmon> SALMON = register(
      "salmon", EntityType.Builder.<Salmon>of(Salmon::new, MobCategory.WATER_AMBIENT).sized(0.7F, 0.4F).clientTrackingRange(4)
   );
   public static final EntityType<Sheep> SHEEP = register(
      "sheep", EntityType.Builder.<Sheep>of(Sheep::new, MobCategory.CREATURE).sized(0.9F, 1.3F).clientTrackingRange(10)
   );
   public static final EntityType<Shulker> SHULKER = register(
      "shulker",
      EntityType.Builder.<Shulker>of(Shulker::new, MobCategory.MONSTER).fireImmune().canSpawnFarFromPlayer().sized(1.0F, 1.0F).clientTrackingRange(10)
   );
   public static final EntityType<ShulkerBullet> SHULKER_BULLET = register(
      "shulker_bullet", EntityType.Builder.<ShulkerBullet>of(ShulkerBullet::new, MobCategory.MISC).sized(0.3125F, 0.3125F).clientTrackingRange(8)
   );
   public static final EntityType<Silverfish> SILVERFISH = register(
      "silverfish", EntityType.Builder.<Silverfish>of(Silverfish::new, MobCategory.MONSTER).sized(0.4F, 0.3F).clientTrackingRange(8)
   );
   public static final EntityType<Skeleton> SKELETON = register(
      "skeleton", EntityType.Builder.<Skeleton>of(Skeleton::new, MobCategory.MONSTER).sized(0.6F, 1.99F).clientTrackingRange(8)
   );
   public static final EntityType<SkeletonHorse> SKELETON_HORSE = register(
      "skeleton_horse", EntityType.Builder.<SkeletonHorse>of(SkeletonHorse::new, MobCategory.CREATURE).sized(1.3964844F, 1.6F).clientTrackingRange(10)
   );
   public static final EntityType<Slime> SLIME = register(
      "slime", EntityType.Builder.<Slime>of(Slime::new, MobCategory.MONSTER).sized(2.04F, 2.04F).clientTrackingRange(10)
   );
   public static final EntityType<SmallFireball> SMALL_FIREBALL = register(
      "small_fireball",
      EntityType.Builder.<SmallFireball>of(SmallFireball::new, MobCategory.MISC).sized(0.3125F, 0.3125F).clientTrackingRange(4).updateInterval(10)
   );
   public static final EntityType<SnowGolem> SNOW_GOLEM = register(
      "snow_golem", EntityType.Builder.<SnowGolem>of(SnowGolem::new, MobCategory.MISC).immuneTo(Blocks.POWDER_SNOW).sized(0.7F, 1.9F).clientTrackingRange(8)
   );
   public static final EntityType<Snowball> SNOWBALL = register(
      "snowball", EntityType.Builder.<Snowball>of(Snowball::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10)
   );
   public static final EntityType<SpectralArrow> SPECTRAL_ARROW = register(
      "spectral_arrow", EntityType.Builder.<SpectralArrow>of(SpectralArrow::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20)
   );
   public static final EntityType<Spider> SPIDER = register(
      "spider", EntityType.Builder.<Spider>of(Spider::new, MobCategory.MONSTER).sized(1.4F, 0.9F).clientTrackingRange(8)
   );
   public static final EntityType<Squid> SQUID = register(
      "squid", EntityType.Builder.<Squid>of(Squid::new, MobCategory.WATER_CREATURE).sized(0.8F, 0.8F).clientTrackingRange(8)
   );
   public static final EntityType<Stray> STRAY = register(
      "stray", EntityType.Builder.<Stray>of(Stray::new, MobCategory.MONSTER).sized(0.6F, 1.99F).immuneTo(Blocks.POWDER_SNOW).clientTrackingRange(8)
   );
   public static final EntityType<Strider> STRIDER = register(
      "strider", EntityType.Builder.<Strider>of(Strider::new, MobCategory.CREATURE).fireImmune().sized(0.9F, 1.7F).clientTrackingRange(10)
   );
   public static final EntityType<Tadpole> TADPOLE = register(
      "tadpole", EntityType.Builder.<Tadpole>of(Tadpole::new, MobCategory.CREATURE).sized(Tadpole.HITBOX_WIDTH, Tadpole.HITBOX_HEIGHT).clientTrackingRange(10)
   );
   public static final EntityType<ThrownEgg> EGG = register(
      "egg", EntityType.Builder.<ThrownEgg>of(ThrownEgg::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10)
   );
   public static final EntityType<ThrownEnderpearl> ENDER_PEARL = register(
      "ender_pearl",
      EntityType.Builder.<ThrownEnderpearl>of(ThrownEnderpearl::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10)
   );
   public static final EntityType<ThrownExperienceBottle> EXPERIENCE_BOTTLE = register(
      "experience_bottle",
      EntityType.Builder.<ThrownExperienceBottle>of(ThrownExperienceBottle::new, MobCategory.MISC)
         .sized(0.25F, 0.25F)
         .clientTrackingRange(4)
         .updateInterval(10)
   );
   public static final EntityType<ThrownPotion> POTION = register(
      "potion", EntityType.Builder.<ThrownPotion>of(ThrownPotion::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10)
   );
   public static final EntityType<ThrownTrident> TRIDENT = register(
      "trident", EntityType.Builder.<ThrownTrident>of(ThrownTrident::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20)
   );
   public static final EntityType<TraderLlama> TRADER_LLAMA = register(
      "trader_llama", EntityType.Builder.<TraderLlama>of(TraderLlama::new, MobCategory.CREATURE).sized(0.9F, 1.87F).clientTrackingRange(10)
   );
   public static final EntityType<TropicalFish> TROPICAL_FISH = register(
      "tropical_fish", EntityType.Builder.<TropicalFish>of(TropicalFish::new, MobCategory.WATER_AMBIENT).sized(0.5F, 0.4F).clientTrackingRange(4)
   );
   public static final EntityType<Turtle> TURTLE = register(
      "turtle", EntityType.Builder.<Turtle>of(Turtle::new, MobCategory.CREATURE).sized(1.2F, 0.4F).clientTrackingRange(10)
   );
   public static final EntityType<Vex> VEX = register(
      "vex", EntityType.Builder.<Vex>of(Vex::new, MobCategory.MONSTER).fireImmune().sized(0.4F, 0.8F).clientTrackingRange(8)
   );
   public static final EntityType<Villager> VILLAGER = register(
      "villager", EntityType.Builder.<Villager>of(Villager::new, MobCategory.MISC).sized(0.6F, 1.95F).clientTrackingRange(10)
   );
   public static final EntityType<Vindicator> VINDICATOR = register(
      "vindicator", EntityType.Builder.<Vindicator>of(Vindicator::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8)
   );
   public static final EntityType<WanderingTrader> WANDERING_TRADER = register(
      "wandering_trader", EntityType.Builder.<WanderingTrader>of(WanderingTrader::new, MobCategory.CREATURE).sized(0.6F, 1.95F).clientTrackingRange(10)
   );
   public static final EntityType<Warden> WARDEN = register(
      "warden", EntityType.Builder.<Warden>of(Warden::new, MobCategory.MONSTER).sized(0.9F, 2.9F).clientTrackingRange(16).fireImmune()
   );
   public static final EntityType<Witch> WITCH = register(
      "witch", EntityType.Builder.<Witch>of(Witch::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8)
   );
   public static final EntityType<WitherBoss> WITHER = register(
      "wither",
      EntityType.Builder.<WitherBoss>of(WitherBoss::new, MobCategory.MONSTER)
         .fireImmune()
         .immuneTo(Blocks.WITHER_ROSE)
         .sized(0.9F, 3.5F)
         .clientTrackingRange(10)
   );
   public static final EntityType<WitherSkeleton> WITHER_SKELETON = register(
      "wither_skeleton",
      EntityType.Builder.<WitherSkeleton>of(WitherSkeleton::new, MobCategory.MONSTER)
         .fireImmune()
         .immuneTo(Blocks.WITHER_ROSE)
         .sized(0.7F, 2.4F)
         .clientTrackingRange(8)
   );
   public static final EntityType<WitherSkull> WITHER_SKULL = register(
      "wither_skull", EntityType.Builder.<WitherSkull>of(WitherSkull::new, MobCategory.MISC).sized(0.3125F, 0.3125F).clientTrackingRange(4).updateInterval(10)
   );
   public static final EntityType<Wolf> WOLF = register(
      "wolf", EntityType.Builder.<Wolf>of(Wolf::new, MobCategory.CREATURE).sized(0.6F, 0.85F).clientTrackingRange(10)
   );
   public static final EntityType<Zoglin> ZOGLIN = register(
      "zoglin", EntityType.Builder.<Zoglin>of(Zoglin::new, MobCategory.MONSTER).fireImmune().sized(1.3964844F, 1.4F).clientTrackingRange(8)
   );
   public static final EntityType<Zombie> ZOMBIE = register(
      "zombie", EntityType.Builder.<Zombie>of(Zombie::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8)
   );
   public static final EntityType<ZombieHorse> ZOMBIE_HORSE = register(
      "zombie_horse", EntityType.Builder.<ZombieHorse>of(ZombieHorse::new, MobCategory.CREATURE).sized(1.3964844F, 1.6F).clientTrackingRange(10)
   );
   public static final EntityType<ZombieVillager> ZOMBIE_VILLAGER = register(
      "zombie_villager", EntityType.Builder.<ZombieVillager>of(ZombieVillager::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8)
   );
   public static final EntityType<ZombifiedPiglin> ZOMBIFIED_PIGLIN = register(
      "zombified_piglin",
      EntityType.Builder.<ZombifiedPiglin>of(ZombifiedPiglin::new, MobCategory.MONSTER).fireImmune().sized(0.6F, 1.95F).clientTrackingRange(8)
   );
   public static final EntityType<Player> PLAYER = register(
      "player", EntityType.Builder.<Player>createNothing(MobCategory.MISC).noSave().noSummon().sized(0.6F, 1.8F).clientTrackingRange(32).updateInterval(2)
   );
   public static final EntityType<FishingHook> FISHING_BOBBER = register(
      "fishing_bobber",
      EntityType.Builder.<FishingHook>of(FishingHook::new, MobCategory.MISC).noSave().noSummon().sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(5)
   );
   private final EntityType.EntityFactory<T> factory;
   private final MobCategory category;
   private final ImmutableSet<Block> immuneTo;
   private final boolean serialize;
   private final boolean summon;
   private final boolean fireImmune;
   private final boolean canSpawnFarFromPlayer;
   private final int clientTrackingRange;
   private final int updateInterval;
   @Nullable
   private String descriptionId;
   @Nullable
   private Component description;
   @Nullable
   private ResourceLocation lootTable;
   private final EntityDimensions dimensions;
   private final FeatureFlagSet requiredFeatures;

   private static <T extends Entity> EntityType<T> register(String var0, EntityType.Builder<T> var1) {
      return Registry.register(BuiltInRegistries.ENTITY_TYPE, var0, var1.build(var0));
   }

   public static ResourceLocation getKey(EntityType<?> var0) {
      return BuiltInRegistries.ENTITY_TYPE.getKey(var0);
   }

   public static Optional<EntityType<?>> byString(String var0) {
      return BuiltInRegistries.ENTITY_TYPE.getOptional(ResourceLocation.tryParse(var0));
   }

   public EntityType(
      EntityType.EntityFactory<T> var1,
      MobCategory var2,
      boolean var3,
      boolean var4,
      boolean var5,
      boolean var6,
      ImmutableSet<Block> var7,
      EntityDimensions var8,
      int var9,
      int var10,
      FeatureFlagSet var11
   ) {
      super();
      this.factory = var1;
      this.category = var2;
      this.canSpawnFarFromPlayer = var6;
      this.serialize = var3;
      this.summon = var4;
      this.fireImmune = var5;
      this.immuneTo = var7;
      this.dimensions = var8;
      this.clientTrackingRange = var9;
      this.updateInterval = var10;
      this.requiredFeatures = var11;
   }

   @Nullable
   public T spawn(ServerLevel var1, @Nullable ItemStack var2, @Nullable Player var3, BlockPos var4, MobSpawnType var5, boolean var6, boolean var7) {
      Consumer var8;
      CompoundTag var9;
      if (var2 != null) {
         var9 = var2.getTag();
         var8 = createDefaultStackConfig(var1, var2, var3);
      } else {
         var8 = var0 -> {
         };
         var9 = null;
      }

      return this.spawn(var1, var9, var8, var4, var5, var6, var7);
   }

   public static <T extends Entity> Consumer<T> createDefaultStackConfig(ServerLevel var0, ItemStack var1, @Nullable Player var2) {
      return appendDefaultStackConfig(var0x -> {
      }, var0, var1, var2);
   }

   public static <T extends Entity> Consumer<T> appendDefaultStackConfig(Consumer<T> var0, ServerLevel var1, ItemStack var2, @Nullable Player var3) {
      return appendCustomEntityStackConfig(appendCustomNameConfig(var0, var2), var1, var2, var3);
   }

   public static <T extends Entity> Consumer<T> appendCustomNameConfig(Consumer<T> var0, ItemStack var1) {
      return var1.hasCustomHoverName() ? var0.andThen(var1x -> var1x.setCustomName(var1.getHoverName())) : var0;
   }

   public static <T extends Entity> Consumer<T> appendCustomEntityStackConfig(Consumer<T> var0, ServerLevel var1, ItemStack var2, @Nullable Player var3) {
      CompoundTag var4 = var2.getTag();
      return var4 != null ? var0.andThen(var3x -> updateCustomEntityTag(var1, var3, var3x, var4)) : var0;
   }

   @Nullable
   public T spawn(ServerLevel var1, BlockPos var2, MobSpawnType var3) {
      return this.spawn(var1, (CompoundTag)null, null, var2, var3, false, false);
   }

   @Nullable
   public T spawn(ServerLevel var1, @Nullable CompoundTag var2, @Nullable Consumer<T> var3, BlockPos var4, MobSpawnType var5, boolean var6, boolean var7) {
      Entity var8 = this.create(var1, var2, var3, var4, var5, var6, var7);
      if (var8 != null) {
         var1.addFreshEntityWithPassengers(var8);
      }

      return (T)var8;
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Nullable
   public T create(ServerLevel var1, @Nullable CompoundTag var2, @Nullable Consumer<T> var3, BlockPos var4, MobSpawnType var5, boolean var6, boolean var7) {
      Entity var8 = this.create(var1);
      if (var8 == null) {
         return null;
      } else {
         double var9;
         if (var6) {
            var8.setPos((double)var4.getX() + 0.5, (double)(var4.getY() + 1), (double)var4.getZ() + 0.5);
            var9 = getYOffset(var1, var4, var7, var8.getBoundingBox());
         } else {
            var9 = 0.0;
         }

         var8.moveTo((double)var4.getX() + 0.5, (double)var4.getY() + var9, (double)var4.getZ() + 0.5, Mth.wrapDegrees(var1.random.nextFloat() * 360.0F), 0.0F);
         if (var8 instanceof Mob var11) {
            var11.yHeadRot = var11.getYRot();
            var11.yBodyRot = var11.getYRot();
            var11.finalizeSpawn(var1, var1.getCurrentDifficultyAt(var11.blockPosition()), var5, null, var2);
            var11.playAmbientSound();
         }

         if (var3 != null) {
            var3.accept(var8);
         }

         return (T)var8;
      }
   }

   protected static double getYOffset(LevelReader var0, BlockPos var1, boolean var2, AABB var3) {
      AABB var4 = new AABB(var1);
      if (var2) {
         var4 = var4.expandTowards(0.0, -1.0, 0.0);
      }

      Iterable var5 = var0.getCollisions(null, var4);
      return 1.0 + Shapes.collide(Direction.Axis.Y, var3, var5, var2 ? -2.0 : -1.0);
   }

   public static void updateCustomEntityTag(Level var0, @Nullable Player var1, @Nullable Entity var2, @Nullable CompoundTag var3) {
      if (var3 != null && var3.contains("EntityTag", 10)) {
         MinecraftServer var4 = var0.getServer();
         if (var4 != null && var2 != null) {
            if (var0.isClientSide || !var2.onlyOpCanSetNbt() || var1 != null && var4.getPlayerList().isOp(var1.getGameProfile())) {
               CompoundTag var5 = var2.saveWithoutId(new CompoundTag());
               UUID var6 = var2.getUUID();
               var5.merge(var3.getCompound("EntityTag"));
               var2.setUUID(var6);
               var2.load(var5);
            }
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
      if (this.descriptionId == null) {
         this.descriptionId = Util.makeDescriptionId("entity", BuiltInRegistries.ENTITY_TYPE.getKey(this));
      }

      return this.descriptionId;
   }

   public Component getDescription() {
      if (this.description == null) {
         this.description = Component.translatable(this.getDescriptionId());
      }

      return this.description;
   }

   @Override
   public String toString() {
      return this.getDescriptionId();
   }

   public String toShortString() {
      int var1 = this.getDescriptionId().lastIndexOf(46);
      return var1 == -1 ? this.getDescriptionId() : this.getDescriptionId().substring(var1 + 1);
   }

   public ResourceLocation getDefaultLootTable() {
      if (this.lootTable == null) {
         ResourceLocation var1 = BuiltInRegistries.ENTITY_TYPE.getKey(this);
         this.lootTable = var1.withPrefix("entities/");
      }

      return this.lootTable;
   }

   public float getWidth() {
      return this.dimensions.width;
   }

   public float getHeight() {
      return this.dimensions.height;
   }

   @Override
   public FeatureFlagSet requiredFeatures() {
      return this.requiredFeatures;
   }

   @Nullable
   public T create(Level var1) {
      return !this.isEnabled(var1.enabledFeatures()) ? null : this.factory.create(this, var1);
   }

   public static Optional<Entity> create(CompoundTag var0, Level var1) {
      return Util.ifElse(
         by(var0).map(var1x -> var1x.create(var1)), var1x -> var1x.load(var0), () -> LOGGER.warn("Skipping Entity with id {}", var0.getString("id"))
      );
   }

   public AABB getAABB(double var1, double var3, double var5) {
      float var7 = this.getWidth() / 2.0F;
      return new AABB(var1 - (double)var7, var3, var5 - (double)var7, var1 + (double)var7, var3 + (double)this.getHeight(), var5 + (double)var7);
   }

   public boolean isBlockDangerous(BlockState var1) {
      if (this.immuneTo.contains(var1.getBlock())) {
         return false;
      } else if (!this.fireImmune && WalkNodeEvaluator.isBurningBlock(var1)) {
         return true;
      } else {
         return var1.is(Blocks.WITHER_ROSE) || var1.is(Blocks.SWEET_BERRY_BUSH) || var1.is(Blocks.CACTUS) || var1.is(Blocks.POWDER_SNOW);
      }
   }

   public EntityDimensions getDimensions() {
      return this.dimensions;
   }

   public static Optional<EntityType<?>> by(CompoundTag var0) {
      return BuiltInRegistries.ENTITY_TYPE.getOptional(new ResourceLocation(var0.getString("id")));
   }

   @Nullable
   public static Entity loadEntityRecursive(CompoundTag var0, Level var1, Function<Entity, Entity> var2) {
      return loadStaticEntity(var0, var1).<Entity>map(var2).map(var3 -> {
         if (var0.contains("Passengers", 9)) {
            ListTag var4 = var0.getList("Passengers", 10);

            for(int var5 = 0; var5 < var4.size(); ++var5) {
               Entity var6 = loadEntityRecursive(var4.getCompound(var5), var1, var2);
               if (var6 != null) {
                  var6.startRiding(var3, true);
               }
            }
         }

         return var3;
      }).orElse(null);
   }

   public static Stream<Entity> loadEntitiesRecursive(final List<? extends Tag> var0, final Level var1) {
      final Spliterator var2 = var0.spliterator();
      return StreamSupport.stream(new Spliterator<Entity>() {
         @Override
         public boolean tryAdvance(Consumer<? super Entity> var1x) {
            return var2.tryAdvance(var2xx -> EntityType.loadEntityRecursive((CompoundTag)var2xx, var1, var1xxxxx -> {
                  var1x.accept(var1xxxxx);
                  return var1xxxxx;
               }));
         }

         @Override
         public Spliterator<Entity> trySplit() {
            return null;
         }

         @Override
         public long estimateSize() {
            return (long)var0.size();
         }

         @Override
         public int characteristics() {
            return 1297;
         }
      }, false);
   }

   private static Optional<Entity> loadStaticEntity(CompoundTag var0, Level var1) {
      try {
         return create(var0, var1);
      } catch (RuntimeException var3) {
         LOGGER.warn("Exception loading entity: ", var3);
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
      return this != PLAYER
         && this != LLAMA_SPIT
         && this != WITHER
         && this != BAT
         && this != ITEM_FRAME
         && this != GLOW_ITEM_FRAME
         && this != LEASH_KNOT
         && this != PAINTING
         && this != END_CRYSTAL
         && this != EVOKER_FANGS;
   }

   public boolean is(TagKey<EntityType<?>> var1) {
      return this.builtInRegistryHolder.is(var1);
   }

   @Nullable
   public T tryCast(Entity var1) {
      return (T)(var1.getType() == this ? var1 : null);
   }

   @Override
   public Class<? extends Entity> getBaseClass() {
      return Entity.class;
   }

   @Deprecated
   public Holder.Reference<EntityType<?>> builtInRegistryHolder() {
      return this.builtInRegistryHolder;
   }

   public static class Builder<T extends Entity> {
      private final EntityType.EntityFactory<T> factory;
      private final MobCategory category;
      private ImmutableSet<Block> immuneTo = ImmutableSet.of();
      private boolean serialize = true;
      private boolean summon = true;
      private boolean fireImmune;
      private boolean canSpawnFarFromPlayer;
      private int clientTrackingRange = 5;
      private int updateInterval = 3;
      private EntityDimensions dimensions = EntityDimensions.scalable(0.6F, 1.8F);
      private FeatureFlagSet requiredFeatures = FeatureFlags.VANILLA_SET;

      private Builder(EntityType.EntityFactory<T> var1, MobCategory var2) {
         super();
         this.factory = var1;
         this.category = var2;
         this.canSpawnFarFromPlayer = var2 == MobCategory.CREATURE || var2 == MobCategory.MISC;
      }

      public static <T extends Entity> EntityType.Builder<T> of(EntityType.EntityFactory<T> var0, MobCategory var1) {
         return new EntityType.Builder<>(var0, var1);
      }

      public static <T extends Entity> EntityType.Builder<T> createNothing(MobCategory var0) {
         return new EntityType.Builder<>((var0x, var1) -> null, var0);
      }

      public EntityType.Builder<T> sized(float var1, float var2) {
         this.dimensions = EntityDimensions.scalable(var1, var2);
         return this;
      }

      public EntityType.Builder<T> noSummon() {
         this.summon = false;
         return this;
      }

      public EntityType.Builder<T> noSave() {
         this.serialize = false;
         return this;
      }

      public EntityType.Builder<T> fireImmune() {
         this.fireImmune = true;
         return this;
      }

      public EntityType.Builder<T> immuneTo(Block... var1) {
         this.immuneTo = ImmutableSet.copyOf(var1);
         return this;
      }

      public EntityType.Builder<T> canSpawnFarFromPlayer() {
         this.canSpawnFarFromPlayer = true;
         return this;
      }

      public EntityType.Builder<T> clientTrackingRange(int var1) {
         this.clientTrackingRange = var1;
         return this;
      }

      public EntityType.Builder<T> updateInterval(int var1) {
         this.updateInterval = var1;
         return this;
      }

      public EntityType.Builder<T> requiredFeatures(FeatureFlag... var1) {
         this.requiredFeatures = FeatureFlags.REGISTRY.subset(var1);
         return this;
      }

      public EntityType<T> build(String var1) {
         if (this.serialize) {
            Util.fetchChoiceType(References.ENTITY_TREE, var1);
         }

         return new EntityType<>(
            this.factory,
            this.category,
            this.serialize,
            this.summon,
            this.fireImmune,
            this.canSpawnFarFromPlayer,
            this.immuneTo,
            this.dimensions,
            this.clientTrackingRange,
            this.updateInterval,
            this.requiredFeatures
         );
      }
   }

   public interface EntityFactory<T extends Entity> {
      T create(EntityType<T> var1, Level var2);
   }
}
