package net.minecraft.world.entity;

import com.google.common.collect.ImmutableSet;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
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
import net.minecraft.world.entity.animal.armadillo.Armadillo;
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
import net.minecraft.world.entity.animal.sniffer.Sniffer;
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
import net.minecraft.world.entity.monster.Bogged;
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
import net.minecraft.world.entity.monster.breeze.Breeze;
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
import net.minecraft.world.entity.projectile.windcharge.BreezeWindCharge;
import net.minecraft.world.entity.projectile.windcharge.WindCharge;
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
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import org.slf4j.Logger;

public class EntityType<T extends Entity> implements FeatureElement, EntityTypeTest<Entity, T> {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Holder.Reference<EntityType<?>> builtInRegistryHolder = BuiltInRegistries.ENTITY_TYPE.createIntrusiveHolder(this);
   private static final float MAGIC_HORSE_WIDTH = 1.3964844F;
   private static final int DISPLAY_TRACKING_RANGE = 10;
   public static final EntityType<Allay> ALLAY = register(
      "allay",
      EntityType.Builder.of(Allay::new, MobCategory.CREATURE).sized(0.35F, 0.6F).eyeHeight(0.36F).ridingOffset(0.04F).clientTrackingRange(8).updateInterval(2)
   );
   public static final EntityType<AreaEffectCloud> AREA_EFFECT_CLOUD = register(
      "area_effect_cloud",
      EntityType.Builder.<AreaEffectCloud>of(AreaEffectCloud::new, MobCategory.MISC)
         .fireImmune()
         .sized(6.0F, 0.5F)
         .clientTrackingRange(10)
         .updateInterval(2147483647)
   );
   public static final EntityType<Armadillo> ARMADILLO = register(
      "armadillo", EntityType.Builder.of(Armadillo::new, MobCategory.CREATURE).sized(0.7F, 0.65F).eyeHeight(0.26F).clientTrackingRange(10)
   );
   public static final EntityType<ArmorStand> ARMOR_STAND = register(
      "armor_stand", EntityType.Builder.<ArmorStand>of(ArmorStand::new, MobCategory.MISC).sized(0.5F, 1.975F).eyeHeight(1.7775F).clientTrackingRange(10)
   );
   public static final EntityType<Arrow> ARROW = register(
      "arrow", EntityType.Builder.<Arrow>of(Arrow::new, MobCategory.MISC).sized(0.5F, 0.5F).eyeHeight(0.13F).clientTrackingRange(4).updateInterval(20)
   );
   public static final EntityType<Axolotl> AXOLOTL = register(
      "axolotl", EntityType.Builder.of(Axolotl::new, MobCategory.AXOLOTLS).sized(0.75F, 0.42F).eyeHeight(0.2751F).clientTrackingRange(10)
   );
   public static final EntityType<Bat> BAT = register(
      "bat", EntityType.Builder.of(Bat::new, MobCategory.AMBIENT).sized(0.5F, 0.9F).eyeHeight(0.45F).clientTrackingRange(5)
   );
   public static final EntityType<Bee> BEE = register(
      "bee", EntityType.Builder.of(Bee::new, MobCategory.CREATURE).sized(0.7F, 0.6F).eyeHeight(0.3F).clientTrackingRange(8)
   );
   public static final EntityType<Blaze> BLAZE = register(
      "blaze", EntityType.Builder.of(Blaze::new, MobCategory.MONSTER).fireImmune().sized(0.6F, 1.8F).clientTrackingRange(8)
   );
   public static final EntityType<Display.BlockDisplay> BLOCK_DISPLAY = register(
      "block_display", EntityType.Builder.of(Display.BlockDisplay::new, MobCategory.MISC).sized(0.0F, 0.0F).clientTrackingRange(10).updateInterval(1)
   );
   public static final EntityType<Boat> BOAT = register(
      "boat", EntityType.Builder.<Boat>of(Boat::new, MobCategory.MISC).sized(1.375F, 0.5625F).eyeHeight(0.5625F).clientTrackingRange(10)
   );
   public static final EntityType<Bogged> BOGGED = register(
      "bogged",
      EntityType.Builder.of(Bogged::new, MobCategory.MONSTER)
         .sized(0.6F, 1.99F)
         .eyeHeight(1.74F)
         .ridingOffset(-0.7F)
         .clientTrackingRange(8)
         .requiredFeatures(FeatureFlags.UPDATE_1_21)
   );
   public static final EntityType<Breeze> BREEZE = register(
      "breeze",
      EntityType.Builder.of(Breeze::new, MobCategory.MONSTER)
         .sized(0.6F, 1.77F)
         .eyeHeight(1.3452F)
         .clientTrackingRange(10)
         .requiredFeatures(FeatureFlags.UPDATE_1_21)
   );
   public static final EntityType<BreezeWindCharge> BREEZE_WIND_CHARGE = register(
      "breeze_wind_charge",
      EntityType.Builder.<BreezeWindCharge>of(BreezeWindCharge::new, MobCategory.MISC)
         .sized(0.3125F, 0.3125F)
         .eyeHeight(0.0F)
         .clientTrackingRange(4)
         .updateInterval(10)
         .requiredFeatures(FeatureFlags.UPDATE_1_21)
   );
   public static final EntityType<Camel> CAMEL = register(
      "camel", EntityType.Builder.of(Camel::new, MobCategory.CREATURE).sized(1.7F, 2.375F).eyeHeight(2.275F).clientTrackingRange(10)
   );
   public static final EntityType<Cat> CAT = register(
      "cat", EntityType.Builder.of(Cat::new, MobCategory.CREATURE).sized(0.6F, 0.7F).eyeHeight(0.35F).passengerAttachments(0.5125F).clientTrackingRange(8)
   );
   public static final EntityType<CaveSpider> CAVE_SPIDER = register(
      "cave_spider", EntityType.Builder.of(CaveSpider::new, MobCategory.MONSTER).sized(0.7F, 0.5F).eyeHeight(0.45F).clientTrackingRange(8)
   );
   public static final EntityType<ChestBoat> CHEST_BOAT = register(
      "chest_boat", EntityType.Builder.<ChestBoat>of(ChestBoat::new, MobCategory.MISC).sized(1.375F, 0.5625F).eyeHeight(0.5625F).clientTrackingRange(10)
   );
   public static final EntityType<MinecartChest> CHEST_MINECART = register(
      "chest_minecart",
      EntityType.Builder.<MinecartChest>of(MinecartChest::new, MobCategory.MISC).sized(0.98F, 0.7F).passengerAttachments(0.1875F).clientTrackingRange(8)
   );
   public static final EntityType<Chicken> CHICKEN = register(
      "chicken",
      EntityType.Builder.of(Chicken::new, MobCategory.CREATURE)
         .sized(0.4F, 0.7F)
         .eyeHeight(0.644F)
         .passengerAttachments(new Vec3(0.0, 0.7, -0.1))
         .clientTrackingRange(10)
   );
   public static final EntityType<Cod> COD = register(
      "cod", EntityType.Builder.of(Cod::new, MobCategory.WATER_AMBIENT).sized(0.5F, 0.3F).eyeHeight(0.195F).clientTrackingRange(4)
   );
   public static final EntityType<MinecartCommandBlock> COMMAND_BLOCK_MINECART = register(
      "command_block_minecart",
      EntityType.Builder.<MinecartCommandBlock>of(MinecartCommandBlock::new, MobCategory.MISC)
         .sized(0.98F, 0.7F)
         .passengerAttachments(0.1875F)
         .clientTrackingRange(8)
   );
   public static final EntityType<Cow> COW = register(
      "cow", EntityType.Builder.of(Cow::new, MobCategory.CREATURE).sized(0.9F, 1.4F).eyeHeight(1.3F).passengerAttachments(1.36875F).clientTrackingRange(10)
   );
   public static final EntityType<Creeper> CREEPER = register(
      "creeper", EntityType.Builder.of(Creeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8)
   );
   public static final EntityType<Dolphin> DOLPHIN = register(
      "dolphin", EntityType.Builder.of(Dolphin::new, MobCategory.WATER_CREATURE).sized(0.9F, 0.6F).eyeHeight(0.3F)
   );
   public static final EntityType<Donkey> DONKEY = register(
      "donkey",
      EntityType.Builder.of(Donkey::new, MobCategory.CREATURE).sized(1.3964844F, 1.5F).eyeHeight(1.425F).passengerAttachments(1.1125F).clientTrackingRange(10)
   );
   public static final EntityType<DragonFireball> DRAGON_FIREBALL = register(
      "dragon_fireball",
      EntityType.Builder.<DragonFireball>of(DragonFireball::new, MobCategory.MISC).sized(1.0F, 1.0F).clientTrackingRange(4).updateInterval(10)
   );
   public static final EntityType<Drowned> DROWNED = register(
      "drowned",
      EntityType.Builder.of(Drowned::new, MobCategory.MONSTER)
         .sized(0.6F, 1.95F)
         .eyeHeight(1.74F)
         .passengerAttachments(2.0125F)
         .ridingOffset(-0.7F)
         .clientTrackingRange(8)
   );
   public static final EntityType<ThrownEgg> EGG = register(
      "egg", EntityType.Builder.<ThrownEgg>of(ThrownEgg::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10)
   );
   public static final EntityType<ElderGuardian> ELDER_GUARDIAN = register(
      "elder_guardian",
      EntityType.Builder.of(ElderGuardian::new, MobCategory.MONSTER)
         .sized(1.9975F, 1.9975F)
         .eyeHeight(0.99875F)
         .passengerAttachments(2.350625F)
         .clientTrackingRange(10)
   );
   public static final EntityType<EndCrystal> END_CRYSTAL = register(
      "end_crystal", EntityType.Builder.<EndCrystal>of(EndCrystal::new, MobCategory.MISC).sized(2.0F, 2.0F).clientTrackingRange(16).updateInterval(2147483647)
   );
   public static final EntityType<EnderDragon> ENDER_DRAGON = register(
      "ender_dragon",
      EntityType.Builder.of(EnderDragon::new, MobCategory.MONSTER).fireImmune().sized(16.0F, 8.0F).passengerAttachments(3.0F).clientTrackingRange(10)
   );
   public static final EntityType<ThrownEnderpearl> ENDER_PEARL = register(
      "ender_pearl",
      EntityType.Builder.<ThrownEnderpearl>of(ThrownEnderpearl::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10)
   );
   public static final EntityType<EnderMan> ENDERMAN = register(
      "enderman",
      EntityType.Builder.of(EnderMan::new, MobCategory.MONSTER).sized(0.6F, 2.9F).eyeHeight(2.55F).passengerAttachments(2.80625F).clientTrackingRange(8)
   );
   public static final EntityType<Endermite> ENDERMITE = register(
      "endermite",
      EntityType.Builder.of(Endermite::new, MobCategory.MONSTER).sized(0.4F, 0.3F).eyeHeight(0.13F).passengerAttachments(0.2375F).clientTrackingRange(8)
   );
   public static final EntityType<Evoker> EVOKER = register(
      "evoker",
      EntityType.Builder.of(Evoker::new, MobCategory.MONSTER).sized(0.6F, 1.95F).passengerAttachments(2.0F).ridingOffset(-0.6F).clientTrackingRange(8)
   );
   public static final EntityType<EvokerFangs> EVOKER_FANGS = register(
      "evoker_fangs", EntityType.Builder.<EvokerFangs>of(EvokerFangs::new, MobCategory.MISC).sized(0.5F, 0.8F).clientTrackingRange(6).updateInterval(2)
   );
   public static final EntityType<ThrownExperienceBottle> EXPERIENCE_BOTTLE = register(
      "experience_bottle",
      EntityType.Builder.<ThrownExperienceBottle>of(ThrownExperienceBottle::new, MobCategory.MISC)
         .sized(0.25F, 0.25F)
         .clientTrackingRange(4)
         .updateInterval(10)
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
      "fox",
      EntityType.Builder.of(Fox::new, MobCategory.CREATURE)
         .sized(0.6F, 0.7F)
         .eyeHeight(0.4F)
         .passengerAttachments(new Vec3(0.0, 0.6375, -0.25))
         .clientTrackingRange(8)
         .immuneTo(Blocks.SWEET_BERRY_BUSH)
   );
   public static final EntityType<Frog> FROG = register(
      "frog",
      EntityType.Builder.of(Frog::new, MobCategory.CREATURE).sized(0.5F, 0.5F).passengerAttachments(new Vec3(0.0, 0.375, -0.25)).clientTrackingRange(10)
   );
   public static final EntityType<MinecartFurnace> FURNACE_MINECART = register(
      "furnace_minecart",
      EntityType.Builder.<MinecartFurnace>of(MinecartFurnace::new, MobCategory.MISC).sized(0.98F, 0.7F).passengerAttachments(0.1875F).clientTrackingRange(8)
   );
   public static final EntityType<Ghast> GHAST = register(
      "ghast",
      EntityType.Builder.of(Ghast::new, MobCategory.MONSTER)
         .fireImmune()
         .sized(4.0F, 4.0F)
         .eyeHeight(2.6F)
         .passengerAttachments(4.0625F)
         .ridingOffset(0.5F)
         .clientTrackingRange(10)
   );
   public static final EntityType<Giant> GIANT = register(
      "giant", EntityType.Builder.of(Giant::new, MobCategory.MONSTER).sized(3.6F, 12.0F).eyeHeight(10.44F).ridingOffset(-3.75F).clientTrackingRange(10)
   );
   public static final EntityType<GlowItemFrame> GLOW_ITEM_FRAME = register(
      "glow_item_frame",
      EntityType.Builder.<GlowItemFrame>of(GlowItemFrame::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .eyeHeight(0.0F)
         .clientTrackingRange(10)
         .updateInterval(2147483647)
   );
   public static final EntityType<GlowSquid> GLOW_SQUID = register(
      "glow_squid", EntityType.Builder.of(GlowSquid::new, MobCategory.UNDERGROUND_WATER_CREATURE).sized(0.8F, 0.8F).eyeHeight(0.4F).clientTrackingRange(10)
   );
   public static final EntityType<Goat> GOAT = register(
      "goat", EntityType.Builder.of(Goat::new, MobCategory.CREATURE).sized(0.9F, 1.3F).passengerAttachments(1.1125F).clientTrackingRange(10)
   );
   public static final EntityType<Guardian> GUARDIAN = register(
      "guardian",
      EntityType.Builder.of(Guardian::new, MobCategory.MONSTER).sized(0.85F, 0.85F).eyeHeight(0.425F).passengerAttachments(0.975F).clientTrackingRange(8)
   );
   public static final EntityType<Hoglin> HOGLIN = register(
      "hoglin", EntityType.Builder.of(Hoglin::new, MobCategory.MONSTER).sized(1.3964844F, 1.4F).passengerAttachments(1.49375F).clientTrackingRange(8)
   );
   public static final EntityType<MinecartHopper> HOPPER_MINECART = register(
      "hopper_minecart",
      EntityType.Builder.<MinecartHopper>of(MinecartHopper::new, MobCategory.MISC).sized(0.98F, 0.7F).passengerAttachments(0.1875F).clientTrackingRange(8)
   );
   public static final EntityType<Horse> HORSE = register(
      "horse",
      EntityType.Builder.of(Horse::new, MobCategory.CREATURE).sized(1.3964844F, 1.6F).eyeHeight(1.52F).passengerAttachments(1.44375F).clientTrackingRange(10)
   );
   public static final EntityType<Husk> HUSK = register(
      "husk",
      EntityType.Builder.of(Husk::new, MobCategory.MONSTER)
         .sized(0.6F, 1.95F)
         .eyeHeight(1.74F)
         .passengerAttachments(2.075F)
         .ridingOffset(-0.7F)
         .clientTrackingRange(8)
   );
   public static final EntityType<Illusioner> ILLUSIONER = register(
      "illusioner",
      EntityType.Builder.of(Illusioner::new, MobCategory.MONSTER).sized(0.6F, 1.95F).passengerAttachments(2.0F).ridingOffset(-0.6F).clientTrackingRange(8)
   );
   public static final EntityType<Interaction> INTERACTION = register(
      "interaction", EntityType.Builder.of(Interaction::new, MobCategory.MISC).sized(0.0F, 0.0F).clientTrackingRange(10)
   );
   public static final EntityType<IronGolem> IRON_GOLEM = register(
      "iron_golem", EntityType.Builder.of(IronGolem::new, MobCategory.MISC).sized(1.4F, 2.7F).clientTrackingRange(10)
   );
   public static final EntityType<ItemEntity> ITEM = register(
      "item",
      EntityType.Builder.<ItemEntity>of(ItemEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).eyeHeight(0.2125F).clientTrackingRange(6).updateInterval(20)
   );
   public static final EntityType<Display.ItemDisplay> ITEM_DISPLAY = register(
      "item_display", EntityType.Builder.of(Display.ItemDisplay::new, MobCategory.MISC).sized(0.0F, 0.0F).clientTrackingRange(10).updateInterval(1)
   );
   public static final EntityType<ItemFrame> ITEM_FRAME = register(
      "item_frame",
      EntityType.Builder.<ItemFrame>of(ItemFrame::new, MobCategory.MISC).sized(0.5F, 0.5F).eyeHeight(0.0F).clientTrackingRange(10).updateInterval(2147483647)
   );
   public static final EntityType<OminousItemSpawner> OMINOUS_ITEM_SPAWNER = register(
      "ominous_item_spawner",
      EntityType.Builder.of(OminousItemSpawner::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(8).requiredFeatures(FeatureFlags.UPDATE_1_21)
   );
   public static final EntityType<LargeFireball> FIREBALL = register(
      "fireball", EntityType.Builder.<LargeFireball>of(LargeFireball::new, MobCategory.MISC).sized(1.0F, 1.0F).clientTrackingRange(4).updateInterval(10)
   );
   public static final EntityType<LeashFenceKnotEntity> LEASH_KNOT = register(
      "leash_knot",
      EntityType.Builder.<LeashFenceKnotEntity>of(LeashFenceKnotEntity::new, MobCategory.MISC)
         .noSave()
         .sized(0.375F, 0.5F)
         .eyeHeight(0.0625F)
         .clientTrackingRange(10)
         .updateInterval(2147483647)
   );
   public static final EntityType<LightningBolt> LIGHTNING_BOLT = register(
      "lightning_bolt",
      EntityType.Builder.of(LightningBolt::new, MobCategory.MISC).noSave().sized(0.0F, 0.0F).clientTrackingRange(16).updateInterval(2147483647)
   );
   public static final EntityType<Llama> LLAMA = register(
      "llama",
      EntityType.Builder.of(Llama::new, MobCategory.CREATURE)
         .sized(0.9F, 1.87F)
         .eyeHeight(1.7765F)
         .passengerAttachments(new Vec3(0.0, 1.37, -0.3))
         .clientTrackingRange(10)
   );
   public static final EntityType<LlamaSpit> LLAMA_SPIT = register(
      "llama_spit", EntityType.Builder.<LlamaSpit>of(LlamaSpit::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10)
   );
   public static final EntityType<MagmaCube> MAGMA_CUBE = register(
      "magma_cube",
      EntityType.Builder.of(MagmaCube::new, MobCategory.MONSTER)
         .fireImmune()
         .sized(0.52F, 0.52F)
         .eyeHeight(0.325F)
         .spawnDimensionsScale(4.0F)
         .clientTrackingRange(8)
   );
   public static final EntityType<Marker> MARKER = register(
      "marker", EntityType.Builder.of(Marker::new, MobCategory.MISC).sized(0.0F, 0.0F).clientTrackingRange(0)
   );
   public static final EntityType<Minecart> MINECART = register(
      "minecart", EntityType.Builder.<Minecart>of(Minecart::new, MobCategory.MISC).sized(0.98F, 0.7F).passengerAttachments(0.1875F).clientTrackingRange(8)
   );
   public static final EntityType<MushroomCow> MOOSHROOM = register(
      "mooshroom",
      EntityType.Builder.of(MushroomCow::new, MobCategory.CREATURE).sized(0.9F, 1.4F).eyeHeight(1.3F).passengerAttachments(1.36875F).clientTrackingRange(10)
   );
   public static final EntityType<Mule> MULE = register(
      "mule",
      EntityType.Builder.of(Mule::new, MobCategory.CREATURE).sized(1.3964844F, 1.6F).eyeHeight(1.52F).passengerAttachments(1.2125F).clientTrackingRange(8)
   );
   public static final EntityType<Ocelot> OCELOT = register(
      "ocelot", EntityType.Builder.of(Ocelot::new, MobCategory.CREATURE).sized(0.6F, 0.7F).passengerAttachments(0.6375F).clientTrackingRange(10)
   );
   public static final EntityType<Painting> PAINTING = register(
      "painting", EntityType.Builder.<Painting>of(Painting::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(2147483647)
   );
   public static final EntityType<Panda> PANDA = register(
      "panda", EntityType.Builder.of(Panda::new, MobCategory.CREATURE).sized(1.3F, 1.25F).clientTrackingRange(10)
   );
   public static final EntityType<Parrot> PARROT = register(
      "parrot",
      EntityType.Builder.of(Parrot::new, MobCategory.CREATURE).sized(0.5F, 0.9F).eyeHeight(0.54F).passengerAttachments(0.4625F).clientTrackingRange(8)
   );
   public static final EntityType<Phantom> PHANTOM = register(
      "phantom",
      EntityType.Builder.of(Phantom::new, MobCategory.MONSTER)
         .sized(0.9F, 0.5F)
         .eyeHeight(0.175F)
         .passengerAttachments(0.3375F)
         .ridingOffset(-0.125F)
         .clientTrackingRange(8)
   );
   public static final EntityType<Pig> PIG = register(
      "pig", EntityType.Builder.of(Pig::new, MobCategory.CREATURE).sized(0.9F, 0.9F).passengerAttachments(0.86875F).clientTrackingRange(10)
   );
   public static final EntityType<Piglin> PIGLIN = register(
      "piglin",
      EntityType.Builder.of(Piglin::new, MobCategory.MONSTER)
         .sized(0.6F, 1.95F)
         .eyeHeight(1.79F)
         .passengerAttachments(2.0125F)
         .ridingOffset(-0.7F)
         .clientTrackingRange(8)
   );
   public static final EntityType<PiglinBrute> PIGLIN_BRUTE = register(
      "piglin_brute",
      EntityType.Builder.of(PiglinBrute::new, MobCategory.MONSTER)
         .sized(0.6F, 1.95F)
         .eyeHeight(1.79F)
         .passengerAttachments(2.0125F)
         .ridingOffset(-0.7F)
         .clientTrackingRange(8)
   );
   public static final EntityType<Pillager> PILLAGER = register(
      "pillager",
      EntityType.Builder.of(Pillager::new, MobCategory.MONSTER)
         .canSpawnFarFromPlayer()
         .sized(0.6F, 1.95F)
         .passengerAttachments(2.0F)
         .ridingOffset(-0.6F)
         .clientTrackingRange(8)
   );
   public static final EntityType<PolarBear> POLAR_BEAR = register(
      "polar_bear", EntityType.Builder.of(PolarBear::new, MobCategory.CREATURE).immuneTo(Blocks.POWDER_SNOW).sized(1.4F, 1.4F).clientTrackingRange(10)
   );
   public static final EntityType<ThrownPotion> POTION = register(
      "potion", EntityType.Builder.<ThrownPotion>of(ThrownPotion::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10)
   );
   public static final EntityType<Pufferfish> PUFFERFISH = register(
      "pufferfish", EntityType.Builder.of(Pufferfish::new, MobCategory.WATER_AMBIENT).sized(0.7F, 0.7F).eyeHeight(0.455F).clientTrackingRange(4)
   );
   public static final EntityType<Rabbit> RABBIT = register(
      "rabbit", EntityType.Builder.of(Rabbit::new, MobCategory.CREATURE).sized(0.4F, 0.5F).clientTrackingRange(8)
   );
   public static final EntityType<Ravager> RAVAGER = register(
      "ravager",
      EntityType.Builder.of(Ravager::new, MobCategory.MONSTER).sized(1.95F, 2.2F).passengerAttachments(new Vec3(0.0, 2.2625, -0.0625)).clientTrackingRange(10)
   );
   public static final EntityType<Salmon> SALMON = register(
      "salmon", EntityType.Builder.of(Salmon::new, MobCategory.WATER_AMBIENT).sized(0.7F, 0.4F).eyeHeight(0.26F).clientTrackingRange(4)
   );
   public static final EntityType<Sheep> SHEEP = register(
      "sheep",
      EntityType.Builder.of(Sheep::new, MobCategory.CREATURE).sized(0.9F, 1.3F).eyeHeight(1.235F).passengerAttachments(1.2375F).clientTrackingRange(10)
   );
   public static final EntityType<Shulker> SHULKER = register(
      "shulker",
      EntityType.Builder.of(Shulker::new, MobCategory.MONSTER).fireImmune().canSpawnFarFromPlayer().sized(1.0F, 1.0F).eyeHeight(0.5F).clientTrackingRange(10)
   );
   public static final EntityType<ShulkerBullet> SHULKER_BULLET = register(
      "shulker_bullet", EntityType.Builder.<ShulkerBullet>of(ShulkerBullet::new, MobCategory.MISC).sized(0.3125F, 0.3125F).clientTrackingRange(8)
   );
   public static final EntityType<Silverfish> SILVERFISH = register(
      "silverfish",
      EntityType.Builder.of(Silverfish::new, MobCategory.MONSTER).sized(0.4F, 0.3F).eyeHeight(0.13F).passengerAttachments(0.2375F).clientTrackingRange(8)
   );
   public static final EntityType<Skeleton> SKELETON = register(
      "skeleton", EntityType.Builder.of(Skeleton::new, MobCategory.MONSTER).sized(0.6F, 1.99F).eyeHeight(1.74F).ridingOffset(-0.7F).clientTrackingRange(8)
   );
   public static final EntityType<SkeletonHorse> SKELETON_HORSE = register(
      "skeleton_horse",
      EntityType.Builder.of(SkeletonHorse::new, MobCategory.CREATURE)
         .sized(1.3964844F, 1.6F)
         .eyeHeight(1.52F)
         .passengerAttachments(1.31875F)
         .clientTrackingRange(10)
   );
   public static final EntityType<Slime> SLIME = register(
      "slime", EntityType.Builder.of(Slime::new, MobCategory.MONSTER).sized(0.52F, 0.52F).eyeHeight(0.325F).spawnDimensionsScale(4.0F).clientTrackingRange(10)
   );
   public static final EntityType<SmallFireball> SMALL_FIREBALL = register(
      "small_fireball",
      EntityType.Builder.<SmallFireball>of(SmallFireball::new, MobCategory.MISC).sized(0.3125F, 0.3125F).clientTrackingRange(4).updateInterval(10)
   );
   public static final EntityType<Sniffer> SNIFFER = register(
      "sniffer",
      EntityType.Builder.of(Sniffer::new, MobCategory.CREATURE)
         .sized(1.9F, 1.75F)
         .eyeHeight(1.05F)
         .passengerAttachments(2.09375F)
         .nameTagOffset(2.05F)
         .clientTrackingRange(10)
   );
   public static final EntityType<SnowGolem> SNOW_GOLEM = register(
      "snow_golem",
      EntityType.Builder.of(SnowGolem::new, MobCategory.MISC).immuneTo(Blocks.POWDER_SNOW).sized(0.7F, 1.9F).eyeHeight(1.7F).clientTrackingRange(8)
   );
   public static final EntityType<Snowball> SNOWBALL = register(
      "snowball", EntityType.Builder.<Snowball>of(Snowball::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10)
   );
   public static final EntityType<MinecartSpawner> SPAWNER_MINECART = register(
      "spawner_minecart",
      EntityType.Builder.<MinecartSpawner>of(MinecartSpawner::new, MobCategory.MISC).sized(0.98F, 0.7F).passengerAttachments(0.1875F).clientTrackingRange(8)
   );
   public static final EntityType<SpectralArrow> SPECTRAL_ARROW = register(
      "spectral_arrow",
      EntityType.Builder.<SpectralArrow>of(SpectralArrow::new, MobCategory.MISC).sized(0.5F, 0.5F).eyeHeight(0.13F).clientTrackingRange(4).updateInterval(20)
   );
   public static final EntityType<Spider> SPIDER = register(
      "spider", EntityType.Builder.of(Spider::new, MobCategory.MONSTER).sized(1.4F, 0.9F).eyeHeight(0.65F).passengerAttachments(0.765F).clientTrackingRange(8)
   );
   public static final EntityType<Squid> SQUID = register(
      "squid", EntityType.Builder.of(Squid::new, MobCategory.WATER_CREATURE).sized(0.8F, 0.8F).eyeHeight(0.4F).clientTrackingRange(8)
   );
   public static final EntityType<Stray> STRAY = register(
      "stray",
      EntityType.Builder.of(Stray::new, MobCategory.MONSTER)
         .sized(0.6F, 1.99F)
         .eyeHeight(1.74F)
         .ridingOffset(-0.7F)
         .immuneTo(Blocks.POWDER_SNOW)
         .clientTrackingRange(8)
   );
   public static final EntityType<Strider> STRIDER = register(
      "strider", EntityType.Builder.of(Strider::new, MobCategory.CREATURE).fireImmune().sized(0.9F, 1.7F).clientTrackingRange(10)
   );
   public static final EntityType<Tadpole> TADPOLE = register(
      "tadpole",
      EntityType.Builder.of(Tadpole::new, MobCategory.CREATURE)
         .sized(Tadpole.HITBOX_WIDTH, Tadpole.HITBOX_HEIGHT)
         .eyeHeight(Tadpole.HITBOX_HEIGHT * 0.65F)
         .clientTrackingRange(10)
   );
   public static final EntityType<Display.TextDisplay> TEXT_DISPLAY = register(
      "text_display", EntityType.Builder.of(Display.TextDisplay::new, MobCategory.MISC).sized(0.0F, 0.0F).clientTrackingRange(10).updateInterval(1)
   );
   public static final EntityType<PrimedTnt> TNT = register(
      "tnt",
      EntityType.Builder.<PrimedTnt>of(PrimedTnt::new, MobCategory.MISC)
         .fireImmune()
         .sized(0.98F, 0.98F)
         .eyeHeight(0.15F)
         .clientTrackingRange(10)
         .updateInterval(10)
   );
   public static final EntityType<MinecartTNT> TNT_MINECART = register(
      "tnt_minecart",
      EntityType.Builder.<MinecartTNT>of(MinecartTNT::new, MobCategory.MISC).sized(0.98F, 0.7F).passengerAttachments(0.1875F).clientTrackingRange(8)
   );
   public static final EntityType<TraderLlama> TRADER_LLAMA = register(
      "trader_llama",
      EntityType.Builder.of(TraderLlama::new, MobCategory.CREATURE)
         .sized(0.9F, 1.87F)
         .eyeHeight(1.7765F)
         .passengerAttachments(new Vec3(0.0, 1.37, -0.3))
         .clientTrackingRange(10)
   );
   public static final EntityType<ThrownTrident> TRIDENT = register(
      "trident",
      EntityType.Builder.<ThrownTrident>of(ThrownTrident::new, MobCategory.MISC).sized(0.5F, 0.5F).eyeHeight(0.13F).clientTrackingRange(4).updateInterval(20)
   );
   public static final EntityType<TropicalFish> TROPICAL_FISH = register(
      "tropical_fish", EntityType.Builder.of(TropicalFish::new, MobCategory.WATER_AMBIENT).sized(0.5F, 0.4F).eyeHeight(0.26F).clientTrackingRange(4)
   );
   public static final EntityType<Turtle> TURTLE = register(
      "turtle",
      EntityType.Builder.of(Turtle::new, MobCategory.CREATURE).sized(1.2F, 0.4F).passengerAttachments(new Vec3(0.0, 0.55625, -0.25)).clientTrackingRange(10)
   );
   public static final EntityType<Vex> VEX = register(
      "vex",
      EntityType.Builder.of(Vex::new, MobCategory.MONSTER)
         .fireImmune()
         .sized(0.4F, 0.8F)
         .eyeHeight(0.51875F)
         .passengerAttachments(0.7375F)
         .ridingOffset(0.04F)
         .clientTrackingRange(8)
   );
   public static final EntityType<Villager> VILLAGER = register(
      "villager", EntityType.Builder.<Villager>of(Villager::new, MobCategory.MISC).sized(0.6F, 1.95F).eyeHeight(1.62F).clientTrackingRange(10)
   );
   public static final EntityType<Vindicator> VINDICATOR = register(
      "vindicator",
      EntityType.Builder.of(Vindicator::new, MobCategory.MONSTER).sized(0.6F, 1.95F).passengerAttachments(2.0F).ridingOffset(-0.6F).clientTrackingRange(8)
   );
   public static final EntityType<WanderingTrader> WANDERING_TRADER = register(
      "wandering_trader", EntityType.Builder.of(WanderingTrader::new, MobCategory.CREATURE).sized(0.6F, 1.95F).eyeHeight(1.62F).clientTrackingRange(10)
   );
   public static final EntityType<Warden> WARDEN = register(
      "warden",
      EntityType.Builder.of(Warden::new, MobCategory.MONSTER)
         .sized(0.9F, 2.9F)
         .passengerAttachments(3.15F)
         .attach(EntityAttachment.WARDEN_CHEST, 0.0F, 1.6F, 0.0F)
         .clientTrackingRange(16)
         .fireImmune()
   );
   public static final EntityType<WindCharge> WIND_CHARGE = register(
      "wind_charge",
      EntityType.Builder.<WindCharge>of(WindCharge::new, MobCategory.MISC)
         .sized(0.3125F, 0.3125F)
         .eyeHeight(0.0F)
         .clientTrackingRange(4)
         .updateInterval(10)
         .requiredFeatures(FeatureFlags.UPDATE_1_21)
   );
   public static final EntityType<Witch> WITCH = register(
      "witch", EntityType.Builder.of(Witch::new, MobCategory.MONSTER).sized(0.6F, 1.95F).eyeHeight(1.62F).passengerAttachments(2.2625F).clientTrackingRange(8)
   );
   public static final EntityType<WitherBoss> WITHER = register(
      "wither", EntityType.Builder.of(WitherBoss::new, MobCategory.MONSTER).fireImmune().immuneTo(Blocks.WITHER_ROSE).sized(0.9F, 3.5F).clientTrackingRange(10)
   );
   public static final EntityType<WitherSkeleton> WITHER_SKELETON = register(
      "wither_skeleton",
      EntityType.Builder.of(WitherSkeleton::new, MobCategory.MONSTER)
         .fireImmune()
         .immuneTo(Blocks.WITHER_ROSE)
         .sized(0.7F, 2.4F)
         .eyeHeight(2.1F)
         .ridingOffset(-0.875F)
         .clientTrackingRange(8)
   );
   public static final EntityType<WitherSkull> WITHER_SKULL = register(
      "wither_skull", EntityType.Builder.<WitherSkull>of(WitherSkull::new, MobCategory.MISC).sized(0.3125F, 0.3125F).clientTrackingRange(4).updateInterval(10)
   );
   public static final EntityType<Wolf> WOLF = register(
      "wolf",
      EntityType.Builder.of(Wolf::new, MobCategory.CREATURE)
         .sized(0.6F, 0.85F)
         .eyeHeight(0.68F)
         .passengerAttachments(new Vec3(0.0, 0.81875, -0.0625))
         .clientTrackingRange(10)
   );
   public static final EntityType<Zoglin> ZOGLIN = register(
      "zoglin",
      EntityType.Builder.of(Zoglin::new, MobCategory.MONSTER).fireImmune().sized(1.3964844F, 1.4F).passengerAttachments(1.49375F).clientTrackingRange(8)
   );
   public static final EntityType<Zombie> ZOMBIE = register(
      "zombie",
      EntityType.Builder.<Zombie>of(Zombie::new, MobCategory.MONSTER)
         .sized(0.6F, 1.95F)
         .eyeHeight(1.74F)
         .passengerAttachments(2.0125F)
         .ridingOffset(-0.7F)
         .clientTrackingRange(8)
   );
   public static final EntityType<ZombieHorse> ZOMBIE_HORSE = register(
      "zombie_horse",
      EntityType.Builder.of(ZombieHorse::new, MobCategory.CREATURE)
         .sized(1.3964844F, 1.6F)
         .eyeHeight(1.52F)
         .passengerAttachments(1.31875F)
         .clientTrackingRange(10)
   );
   public static final EntityType<ZombieVillager> ZOMBIE_VILLAGER = register(
      "zombie_villager",
      EntityType.Builder.of(ZombieVillager::new, MobCategory.MONSTER)
         .sized(0.6F, 1.95F)
         .passengerAttachments(2.125F)
         .ridingOffset(-0.7F)
         .eyeHeight(1.74F)
         .clientTrackingRange(8)
   );
   public static final EntityType<ZombifiedPiglin> ZOMBIFIED_PIGLIN = register(
      "zombified_piglin",
      EntityType.Builder.of(ZombifiedPiglin::new, MobCategory.MONSTER)
         .fireImmune()
         .sized(0.6F, 1.95F)
         .eyeHeight(1.79F)
         .passengerAttachments(2.0F)
         .ridingOffset(-0.7F)
         .clientTrackingRange(8)
   );
   public static final EntityType<Player> PLAYER = register(
      "player",
      EntityType.Builder.<Player>createNothing(MobCategory.MISC)
         .noSave()
         .noSummon()
         .sized(0.6F, 1.8F)
         .eyeHeight(1.62F)
         .vehicleAttachment(Player.DEFAULT_VEHICLE_ATTACHMENT)
         .clientTrackingRange(32)
         .updateInterval(2)
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
   private ResourceKey<LootTable> lootTable;
   private final EntityDimensions dimensions;
   private final float spawnDimensionsScale;
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
      float var9,
      int var10,
      int var11,
      FeatureFlagSet var12
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
      this.spawnDimensionsScale = var9;
      this.clientTrackingRange = var10;
      this.updateInterval = var11;
      this.requiredFeatures = var12;
   }

   @Nullable
   public T spawn(ServerLevel var1, @Nullable ItemStack var2, @Nullable Player var3, BlockPos var4, MobSpawnType var5, boolean var6, boolean var7) {
      Consumer var8;
      if (var2 != null) {
         var8 = createDefaultStackConfig(var1, var2, var3);
      } else {
         var8 = var0 -> {
         };
      }

      return this.spawn(var1, var8, var4, var5, var6, var7);
   }

   public static <T extends Entity> Consumer<T> createDefaultStackConfig(ServerLevel var0, ItemStack var1, @Nullable Player var2) {
      return appendDefaultStackConfig(var0x -> {
      }, var0, var1, var2);
   }

   public static <T extends Entity> Consumer<T> appendDefaultStackConfig(Consumer<T> var0, ServerLevel var1, ItemStack var2, @Nullable Player var3) {
      return appendCustomEntityStackConfig(appendCustomNameConfig(var0, var2), var1, var2, var3);
   }

   public static <T extends Entity> Consumer<T> appendCustomNameConfig(Consumer<T> var0, ItemStack var1) {
      Component var2 = var1.get(DataComponents.CUSTOM_NAME);
      return var2 != null ? var0.andThen(var1x -> var1x.setCustomName(var2)) : var0;
   }

   public static <T extends Entity> Consumer<T> appendCustomEntityStackConfig(Consumer<T> var0, ServerLevel var1, ItemStack var2, @Nullable Player var3) {
      CustomData var4 = var2.getOrDefault(DataComponents.ENTITY_DATA, CustomData.EMPTY);
      return !var4.isEmpty() ? var0.andThen(var3x -> updateCustomEntityTag(var1, var3, var3x, var4)) : var0;
   }

   @Nullable
   public T spawn(ServerLevel var1, BlockPos var2, MobSpawnType var3) {
      return this.spawn(var1, null, var2, var3, false, false);
   }

   @Nullable
   public T spawn(ServerLevel var1, @Nullable Consumer<T> var2, BlockPos var3, MobSpawnType var4, boolean var5, boolean var6) {
      Entity var7 = this.create(var1, var2, var3, var4, var5, var6);
      if (var7 != null) {
         var1.addFreshEntityWithPassengers(var7);
      }

      return (T)var7;
   }

   @Nullable
   public T create(ServerLevel var1, @Nullable Consumer<T> var2, BlockPos var3, MobSpawnType var4, boolean var5, boolean var6) {
      Entity var7 = this.create(var1);
      if (var7 == null) {
         return null;
      } else {
         double var8;
         if (var5) {
            var7.setPos((double)var3.getX() + 0.5, (double)(var3.getY() + 1), (double)var3.getZ() + 0.5);
            var8 = getYOffset(var1, var3, var6, var7.getBoundingBox());
         } else {
            var8 = 0.0;
         }

         var7.moveTo((double)var3.getX() + 0.5, (double)var3.getY() + var8, (double)var3.getZ() + 0.5, Mth.wrapDegrees(var1.random.nextFloat() * 360.0F), 0.0F);
         if (var7 instanceof Mob var10) {
            var10.yHeadRot = var10.getYRot();
            var10.yBodyRot = var10.getYRot();
            var10.finalizeSpawn(var1, var1.getCurrentDifficultyAt(var10.blockPosition()), var4, null);
            var10.playAmbientSound();
         }

         if (var2 != null) {
            var2.accept(var7);
         }

         return (T)var7;
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

   public static void updateCustomEntityTag(Level var0, @Nullable Player var1, @Nullable Entity var2, CustomData var3) {
      MinecraftServer var4 = var0.getServer();
      if (var4 != null && var2 != null) {
         if (var0.isClientSide || !var2.onlyOpCanSetNbt() || var1 != null && var4.getPlayerList().isOp(var1.getGameProfile())) {
            var3.loadInto(var2);
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

   public ResourceKey<LootTable> getDefaultLootTable() {
      if (this.lootTable == null) {
         ResourceLocation var1 = BuiltInRegistries.ENTITY_TYPE.getKey(this);
         this.lootTable = ResourceKey.create(Registries.LOOT_TABLE, var1.withPrefix("entities/"));
      }

      return this.lootTable;
   }

   public float getWidth() {
      return this.dimensions.width();
   }

   public float getHeight() {
      return this.dimensions.height();
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

   public AABB getSpawnAABB(double var1, double var3, double var5) {
      float var7 = this.spawnDimensionsScale * this.getWidth() / 2.0F;
      float var8 = this.spawnDimensionsScale * this.getHeight();
      return new AABB(var1 - (double)var7, var3, var5 - (double)var7, var1 + (double)var7, var3 + (double)var8, var5 + (double)var7);
   }

   public boolean isBlockDangerous(BlockState var1) {
      if (this.immuneTo.contains(var1.getBlock())) {
         return false;
      } else {
         return !this.fireImmune && NodeEvaluator.isBurningBlock(var1)
            ? true
            : var1.is(Blocks.WITHER_ROSE) || var1.is(Blocks.SWEET_BERRY_BUSH) || var1.is(Blocks.CACTUS) || var1.is(Blocks.POWDER_SNOW);
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

            for (int var5 = 0; var5 < var4.size(); var5++) {
               Entity var6 = loadEntityRecursive(var4.getCompound(var5), var1, var2);
               if (var6 != null) {
                  var6.startRiding(var3, true);
               }
            }
         }

         return (Entity)var3;
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

   public boolean is(HolderSet<EntityType<?>> var1) {
      return var1.contains(this.builtInRegistryHolder);
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
      private float spawnDimensionsScale = 1.0F;
      private EntityAttachments.Builder attachments = EntityAttachments.builder();
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

      public EntityType.Builder<T> spawnDimensionsScale(float var1) {
         this.spawnDimensionsScale = var1;
         return this;
      }

      public EntityType.Builder<T> eyeHeight(float var1) {
         this.dimensions = this.dimensions.withEyeHeight(var1);
         return this;
      }

      public EntityType.Builder<T> passengerAttachments(float... var1) {
         for (float var5 : var1) {
            this.attachments = this.attachments.attach(EntityAttachment.PASSENGER, 0.0F, var5, 0.0F);
         }

         return this;
      }

      public EntityType.Builder<T> passengerAttachments(Vec3... var1) {
         for (Vec3 var5 : var1) {
            this.attachments = this.attachments.attach(EntityAttachment.PASSENGER, var5);
         }

         return this;
      }

      public EntityType.Builder<T> vehicleAttachment(Vec3 var1) {
         return this.attach(EntityAttachment.VEHICLE, var1);
      }

      public EntityType.Builder<T> ridingOffset(float var1) {
         return this.attach(EntityAttachment.VEHICLE, 0.0F, -var1, 0.0F);
      }

      public EntityType.Builder<T> nameTagOffset(float var1) {
         return this.attach(EntityAttachment.NAME_TAG, 0.0F, var1, 0.0F);
      }

      public EntityType.Builder<T> attach(EntityAttachment var1, float var2, float var3, float var4) {
         this.attachments = this.attachments.attach(var1, var2, var3, var4);
         return this;
      }

      public EntityType.Builder<T> attach(EntityAttachment var1, Vec3 var2) {
         this.attachments = this.attachments.attach(var1, var2);
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
            this.dimensions.withAttachments(this.attachments),
            this.spawnDimensionsScale,
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