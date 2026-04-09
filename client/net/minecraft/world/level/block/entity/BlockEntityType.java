package net.minecraft.world.level.block.entity;

import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class BlockEntityType<T extends BlockEntity> {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final BlockEntityType<FurnaceBlockEntity> FURNACE;
   public static final BlockEntityType<ChestBlockEntity> CHEST;
   public static final BlockEntityType<TrappedChestBlockEntity> TRAPPED_CHEST;
   public static final BlockEntityType<EnderChestBlockEntity> ENDER_CHEST;
   public static final BlockEntityType<JukeboxBlockEntity> JUKEBOX;
   public static final BlockEntityType<DispenserBlockEntity> DISPENSER;
   public static final BlockEntityType<DropperBlockEntity> DROPPER;
   public static final BlockEntityType<SignBlockEntity> SIGN;
   public static final BlockEntityType<HangingSignBlockEntity> HANGING_SIGN;
   public static final BlockEntityType<SpawnerBlockEntity> MOB_SPAWNER;
   public static final BlockEntityType<CreakingHeartBlockEntity> CREAKING_HEART;
   public static final BlockEntityType<PistonMovingBlockEntity> PISTON;
   public static final BlockEntityType<BrewingStandBlockEntity> BREWING_STAND;
   public static final BlockEntityType<EnchantingTableBlockEntity> ENCHANTING_TABLE;
   public static final BlockEntityType<TheEndPortalBlockEntity> END_PORTAL;
   public static final BlockEntityType<BeaconBlockEntity> BEACON;
   public static final BlockEntityType<SkullBlockEntity> SKULL;
   public static final BlockEntityType<DaylightDetectorBlockEntity> DAYLIGHT_DETECTOR;
   public static final BlockEntityType<HopperBlockEntity> HOPPER;
   public static final BlockEntityType<ComparatorBlockEntity> COMPARATOR;
   public static final BlockEntityType<BannerBlockEntity> BANNER;
   public static final BlockEntityType<StructureBlockEntity> STRUCTURE_BLOCK;
   public static final BlockEntityType<TheEndGatewayBlockEntity> END_GATEWAY;
   public static final BlockEntityType<CommandBlockEntity> COMMAND_BLOCK;
   public static final BlockEntityType<ShulkerBoxBlockEntity> SHULKER_BOX;
   public static final BlockEntityType<BedBlockEntity> BED;
   public static final BlockEntityType<ConduitBlockEntity> CONDUIT;
   public static final BlockEntityType<BarrelBlockEntity> BARREL;
   public static final BlockEntityType<SmokerBlockEntity> SMOKER;
   public static final BlockEntityType<BlastFurnaceBlockEntity> BLAST_FURNACE;
   public static final BlockEntityType<LecternBlockEntity> LECTERN;
   public static final BlockEntityType<BellBlockEntity> BELL;
   public static final BlockEntityType<JigsawBlockEntity> JIGSAW;
   public static final BlockEntityType<CampfireBlockEntity> CAMPFIRE;
   public static final BlockEntityType<BeehiveBlockEntity> BEEHIVE;
   public static final BlockEntityType<SculkSensorBlockEntity> SCULK_SENSOR;
   public static final BlockEntityType<CalibratedSculkSensorBlockEntity> CALIBRATED_SCULK_SENSOR;
   public static final BlockEntityType<SculkCatalystBlockEntity> SCULK_CATALYST;
   public static final BlockEntityType<SculkShriekerBlockEntity> SCULK_SHRIEKER;
   public static final BlockEntityType<ChiseledBookShelfBlockEntity> CHISELED_BOOKSHELF;
   public static final BlockEntityType<ShelfBlockEntity> SHELF;
   public static final BlockEntityType<BrushableBlockEntity> BRUSHABLE_BLOCK;
   public static final BlockEntityType<DecoratedPotBlockEntity> DECORATED_POT;
   public static final BlockEntityType<CrafterBlockEntity> CRAFTER;
   public static final BlockEntityType<TrialSpawnerBlockEntity> TRIAL_SPAWNER;
   public static final BlockEntityType<VaultBlockEntity> VAULT;
   public static final BlockEntityType<TestBlockEntity> TEST_BLOCK;
   public static final BlockEntityType<TestInstanceBlockEntity> TEST_INSTANCE_BLOCK;
   public static final BlockEntityType<CopperGolemStatueBlockEntity> COPPER_GOLEM_STATUE;
   public static final BlockEntityType<PotentSulfurEntity> POTENT_SULFUR;
   private static final Set<BlockEntityType<?>> OP_ONLY_CUSTOM_DATA;
   private final BlockEntitySupplier<? extends T> factory;
   private final Set<Block> validBlocks;
   private final Holder.Reference<BlockEntityType<?>> builtInRegistryHolder;

   private static <T extends BlockEntity> BlockEntityType<T> register(final String name, final BlockEntitySupplier<? extends T> factory, final Block... validBlocks) {
      if (validBlocks.length == 0) {
         LOGGER.warn("Block entity type {} requires at least one valid block to be defined!", name);
      }

      Util.fetchChoiceType(References.BLOCK_ENTITY, name);
      return (BlockEntityType)Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, (String)name, new BlockEntityType(factory, Set.of(validBlocks)));
   }

   private static <T extends BlockEntity> BlockEntityType<T> register(final String name, final BlockEntitySupplier<? extends T> factory, final List<Block> validBlocks) {
      return register(name, factory, (Block[])validBlocks.toArray(new Block[0]));
   }

   private BlockEntityType(final BlockEntitySupplier<? extends T> factory, final Set<Block> validBlocks) {
      super();
      this.builtInRegistryHolder = BuiltInRegistries.BLOCK_ENTITY_TYPE.createIntrusiveHolder(this);
      this.factory = factory;
      this.validBlocks = validBlocks;
   }

   public T create(final BlockPos worldPosition, final BlockState blockState) {
      return this.factory.create(worldPosition, blockState);
   }

   public boolean isValid(final BlockState state) {
      return this.validBlocks.contains(state.getBlock());
   }

   /** @deprecated */
   @Deprecated
   public Holder.Reference<BlockEntityType<?>> builtInRegistryHolder() {
      return this.builtInRegistryHolder;
   }

   public @Nullable T getBlockEntity(final BlockGetter level, final BlockPos pos) {
      BlockEntity entity = level.getBlockEntity(pos);
      return (T)(entity != null && entity.getType() == this ? entity : null);
   }

   public boolean onlyOpCanSetNbt() {
      return OP_ONLY_CUSTOM_DATA.contains(this);
   }

   static {
      FURNACE = register("furnace", FurnaceBlockEntity::new, Blocks.FURNACE);
      CHEST = register("chest", ChestBlockEntity::new, Util.copyAndAdd(Blocks.COPPER_CHEST.asList(), Blocks.CHEST));
      TRAPPED_CHEST = register("trapped_chest", TrappedChestBlockEntity::new, Blocks.TRAPPED_CHEST);
      ENDER_CHEST = register("ender_chest", EnderChestBlockEntity::new, Blocks.ENDER_CHEST);
      JUKEBOX = register("jukebox", JukeboxBlockEntity::new, Blocks.JUKEBOX);
      DISPENSER = register("dispenser", DispenserBlockEntity::new, Blocks.DISPENSER);
      DROPPER = register("dropper", DropperBlockEntity::new, Blocks.DROPPER);
      SIGN = register("sign", SignBlockEntity::new, Blocks.OAK_SIGN, Blocks.SPRUCE_SIGN, Blocks.BIRCH_SIGN, Blocks.ACACIA_SIGN, Blocks.CHERRY_SIGN, Blocks.JUNGLE_SIGN, Blocks.DARK_OAK_SIGN, Blocks.PALE_OAK_SIGN, Blocks.OAK_WALL_SIGN, Blocks.SPRUCE_WALL_SIGN, Blocks.BIRCH_WALL_SIGN, Blocks.ACACIA_WALL_SIGN, Blocks.CHERRY_WALL_SIGN, Blocks.JUNGLE_WALL_SIGN, Blocks.DARK_OAK_WALL_SIGN, Blocks.PALE_OAK_WALL_SIGN, Blocks.CRIMSON_SIGN, Blocks.CRIMSON_WALL_SIGN, Blocks.WARPED_SIGN, Blocks.WARPED_WALL_SIGN, Blocks.MANGROVE_SIGN, Blocks.MANGROVE_WALL_SIGN, Blocks.BAMBOO_SIGN, Blocks.BAMBOO_WALL_SIGN);
      HANGING_SIGN = register("hanging_sign", HangingSignBlockEntity::new, Blocks.OAK_HANGING_SIGN, Blocks.SPRUCE_HANGING_SIGN, Blocks.BIRCH_HANGING_SIGN, Blocks.ACACIA_HANGING_SIGN, Blocks.CHERRY_HANGING_SIGN, Blocks.JUNGLE_HANGING_SIGN, Blocks.DARK_OAK_HANGING_SIGN, Blocks.PALE_OAK_HANGING_SIGN, Blocks.CRIMSON_HANGING_SIGN, Blocks.WARPED_HANGING_SIGN, Blocks.MANGROVE_HANGING_SIGN, Blocks.BAMBOO_HANGING_SIGN, Blocks.OAK_WALL_HANGING_SIGN, Blocks.SPRUCE_WALL_HANGING_SIGN, Blocks.BIRCH_WALL_HANGING_SIGN, Blocks.ACACIA_WALL_HANGING_SIGN, Blocks.CHERRY_WALL_HANGING_SIGN, Blocks.JUNGLE_WALL_HANGING_SIGN, Blocks.DARK_OAK_WALL_HANGING_SIGN, Blocks.PALE_OAK_WALL_HANGING_SIGN, Blocks.CRIMSON_WALL_HANGING_SIGN, Blocks.WARPED_WALL_HANGING_SIGN, Blocks.MANGROVE_WALL_HANGING_SIGN, Blocks.BAMBOO_WALL_HANGING_SIGN);
      MOB_SPAWNER = register("mob_spawner", SpawnerBlockEntity::new, Blocks.SPAWNER);
      CREAKING_HEART = register("creaking_heart", CreakingHeartBlockEntity::new, Blocks.CREAKING_HEART);
      PISTON = register("piston", PistonMovingBlockEntity::new, Blocks.MOVING_PISTON);
      BREWING_STAND = register("brewing_stand", BrewingStandBlockEntity::new, Blocks.BREWING_STAND);
      ENCHANTING_TABLE = register("enchanting_table", EnchantingTableBlockEntity::new, Blocks.ENCHANTING_TABLE);
      END_PORTAL = register("end_portal", TheEndPortalBlockEntity::new, Blocks.END_PORTAL);
      BEACON = register("beacon", BeaconBlockEntity::new, Blocks.BEACON);
      SKULL = register("skull", SkullBlockEntity::new, Blocks.SKELETON_SKULL, Blocks.SKELETON_WALL_SKULL, Blocks.CREEPER_HEAD, Blocks.CREEPER_WALL_HEAD, Blocks.DRAGON_HEAD, Blocks.DRAGON_WALL_HEAD, Blocks.ZOMBIE_HEAD, Blocks.ZOMBIE_WALL_HEAD, Blocks.WITHER_SKELETON_SKULL, Blocks.WITHER_SKELETON_WALL_SKULL, Blocks.PLAYER_HEAD, Blocks.PLAYER_WALL_HEAD, Blocks.PIGLIN_HEAD, Blocks.PIGLIN_WALL_HEAD);
      DAYLIGHT_DETECTOR = register("daylight_detector", DaylightDetectorBlockEntity::new, Blocks.DAYLIGHT_DETECTOR);
      HOPPER = register("hopper", HopperBlockEntity::new, Blocks.HOPPER);
      COMPARATOR = register("comparator", ComparatorBlockEntity::new, Blocks.COMPARATOR);
      BANNER = register("banner", BannerBlockEntity::new, Util.join(Blocks.BANNER.asList(), Blocks.WALL_BANNER.asList()));
      STRUCTURE_BLOCK = register("structure_block", StructureBlockEntity::new, Blocks.STRUCTURE_BLOCK);
      END_GATEWAY = register("end_gateway", TheEndGatewayBlockEntity::new, Blocks.END_GATEWAY);
      COMMAND_BLOCK = register("command_block", CommandBlockEntity::new, Blocks.COMMAND_BLOCK, Blocks.CHAIN_COMMAND_BLOCK, Blocks.REPEATING_COMMAND_BLOCK);
      SHULKER_BOX = register("shulker_box", ShulkerBoxBlockEntity::new, Util.copyAndAdd(Blocks.DYED_SHULKER_BOX.asList(), Blocks.SHULKER_BOX));
      BED = register("bed", BedBlockEntity::new, (List)Blocks.BED.asList());
      CONDUIT = register("conduit", ConduitBlockEntity::new, Blocks.CONDUIT);
      BARREL = register("barrel", BarrelBlockEntity::new, Blocks.BARREL);
      SMOKER = register("smoker", SmokerBlockEntity::new, Blocks.SMOKER);
      BLAST_FURNACE = register("blast_furnace", BlastFurnaceBlockEntity::new, Blocks.BLAST_FURNACE);
      LECTERN = register("lectern", LecternBlockEntity::new, Blocks.LECTERN);
      BELL = register("bell", BellBlockEntity::new, Blocks.BELL);
      JIGSAW = register("jigsaw", JigsawBlockEntity::new, Blocks.JIGSAW);
      CAMPFIRE = register("campfire", CampfireBlockEntity::new, Blocks.CAMPFIRE, Blocks.SOUL_CAMPFIRE);
      BEEHIVE = register("beehive", BeehiveBlockEntity::new, Blocks.BEE_NEST, Blocks.BEEHIVE);
      SCULK_SENSOR = register("sculk_sensor", SculkSensorBlockEntity::new, Blocks.SCULK_SENSOR);
      CALIBRATED_SCULK_SENSOR = register("calibrated_sculk_sensor", CalibratedSculkSensorBlockEntity::new, Blocks.CALIBRATED_SCULK_SENSOR);
      SCULK_CATALYST = register("sculk_catalyst", SculkCatalystBlockEntity::new, Blocks.SCULK_CATALYST);
      SCULK_SHRIEKER = register("sculk_shrieker", SculkShriekerBlockEntity::new, Blocks.SCULK_SHRIEKER);
      CHISELED_BOOKSHELF = register("chiseled_bookshelf", ChiseledBookShelfBlockEntity::new, Blocks.CHISELED_BOOKSHELF);
      SHELF = register("shelf", ShelfBlockEntity::new, Blocks.ACACIA_SHELF, Blocks.BAMBOO_SHELF, Blocks.BIRCH_SHELF, Blocks.CHERRY_SHELF, Blocks.CRIMSON_SHELF, Blocks.DARK_OAK_SHELF, Blocks.JUNGLE_SHELF, Blocks.MANGROVE_SHELF, Blocks.OAK_SHELF, Blocks.PALE_OAK_SHELF, Blocks.SPRUCE_SHELF, Blocks.WARPED_SHELF);
      BRUSHABLE_BLOCK = register("brushable_block", BrushableBlockEntity::new, Blocks.SUSPICIOUS_SAND, Blocks.SUSPICIOUS_GRAVEL);
      DECORATED_POT = register("decorated_pot", DecoratedPotBlockEntity::new, Blocks.DECORATED_POT);
      CRAFTER = register("crafter", CrafterBlockEntity::new, Blocks.CRAFTER);
      TRIAL_SPAWNER = register("trial_spawner", TrialSpawnerBlockEntity::new, Blocks.TRIAL_SPAWNER);
      VAULT = register("vault", VaultBlockEntity::new, Blocks.VAULT);
      TEST_BLOCK = register("test_block", TestBlockEntity::new, Blocks.TEST_BLOCK);
      TEST_INSTANCE_BLOCK = register("test_instance_block", TestInstanceBlockEntity::new, Blocks.TEST_INSTANCE_BLOCK);
      COPPER_GOLEM_STATUE = register("copper_golem_statue", CopperGolemStatueBlockEntity::new, (List)Blocks.COPPER_GOLEM_STATUE.asList());
      POTENT_SULFUR = register("potent_sulfur", PotentSulfurEntity::new, Blocks.POTENT_SULFUR);
      OP_ONLY_CUSTOM_DATA = Set.of(COMMAND_BLOCK, LECTERN, SIGN, HANGING_SIGN, MOB_SPAWNER, TRIAL_SPAWNER);
   }

   @FunctionalInterface
   private interface BlockEntitySupplier<T extends BlockEntity> {
      T create(BlockPos worldPosition, BlockState blockState);
   }
}
