package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;

public class BlockTypes {
   public static final MapCodec<Block> CODEC = BuiltInRegistries.BLOCK_TYPE.byNameCodec().dispatchMap(Block::codec, MapCodec::codec);

   public BlockTypes() {
      super();
   }

   public static MapCodec<? extends Block> bootstrap(Registry<MapCodec<? extends Block>> var0) {
      MapCodec var1 = Registry.register(var0, "block", Block.CODEC);
      Registry.register(var0, "air", AirBlock.CODEC);
      Registry.register(var0, "amethyst", AmethystBlock.CODEC);
      Registry.register(var0, "amethyst_cluster", AmethystClusterBlock.CODEC);
      Registry.register(var0, "anvil", AnvilBlock.CODEC);
      Registry.register(var0, "attached_stem", AttachedStemBlock.CODEC);
      Registry.register(var0, "azalea", AzaleaBlock.CODEC);
      Registry.register(var0, "bamboo_sapling", BambooSaplingBlock.CODEC);
      Registry.register(var0, "bamboo_stalk", BambooStalkBlock.CODEC);
      Registry.register(var0, "banner", BannerBlock.CODEC);
      Registry.register(var0, "barrel", BarrelBlock.CODEC);
      Registry.register(var0, "barrier", BarrierBlock.CODEC);
      Registry.register(var0, "base_coral_fan", BaseCoralFanBlock.CODEC);
      Registry.register(var0, "base_coral_plant", BaseCoralPlantBlock.CODEC);
      Registry.register(var0, "base_coral_wall_fan", BaseCoralWallFanBlock.CODEC);
      Registry.register(var0, "beacon", BeaconBlock.CODEC);
      Registry.register(var0, "bed", BedBlock.CODEC);
      Registry.register(var0, "beehive", BeehiveBlock.CODEC);
      Registry.register(var0, "beetroot", BeetrootBlock.CODEC);
      Registry.register(var0, "bell", BellBlock.CODEC);
      Registry.register(var0, "big_dripleaf", BigDripleafBlock.CODEC);
      Registry.register(var0, "big_dripleaf_stem", BigDripleafStemBlock.CODEC);
      Registry.register(var0, "blast_furnace", BlastFurnaceBlock.CODEC);
      Registry.register(var0, "brewing_stand", BrewingStandBlock.CODEC);
      Registry.register(var0, "brushable", BrushableBlock.CODEC);
      Registry.register(var0, "bubble_column", BubbleColumnBlock.CODEC);
      Registry.register(var0, "budding_amethyst", BuddingAmethystBlock.CODEC);
      Registry.register(var0, "button", ButtonBlock.CODEC);
      Registry.register(var0, "cactus", CactusBlock.CODEC);
      Registry.register(var0, "cake", CakeBlock.CODEC);
      Registry.register(var0, "calibrated_sculk_sensor", CalibratedSculkSensorBlock.CODEC);
      Registry.register(var0, "campfire", CampfireBlock.CODEC);
      Registry.register(var0, "candle_cake", CandleCakeBlock.CODEC);
      Registry.register(var0, "candle", CandleBlock.CODEC);
      Registry.register(var0, "carpet", CarpetBlock.CODEC);
      Registry.register(var0, "carrot", CarrotBlock.CODEC);
      Registry.register(var0, "cartography_table", CartographyTableBlock.CODEC);
      Registry.register(var0, "carved_pumpkin", EquipableCarvedPumpkinBlock.CODEC);
      Registry.register(var0, "cauldron", CauldronBlock.CODEC);
      Registry.register(var0, "cave_vines", CaveVinesBlock.CODEC);
      Registry.register(var0, "cave_vines_plant", CaveVinesPlantBlock.CODEC);
      Registry.register(var0, "ceiling_hanging_sign", CeilingHangingSignBlock.CODEC);
      Registry.register(var0, "chain", ChainBlock.CODEC);
      Registry.register(var0, "cherry_leaves", CherryLeavesBlock.CODEC);
      Registry.register(var0, "chest", ChestBlock.CODEC);
      Registry.register(var0, "chiseled_book_shelf", ChiseledBookShelfBlock.CODEC);
      Registry.register(var0, "chorus_flower", ChorusFlowerBlock.CODEC);
      Registry.register(var0, "chorus_plant", ChorusPlantBlock.CODEC);
      Registry.register(var0, "cocoa", CocoaBlock.CODEC);
      Registry.register(var0, "colored_falling", ColoredFallingBlock.CODEC);
      Registry.register(var0, "command", CommandBlock.CODEC);
      Registry.register(var0, "comparator", ComparatorBlock.CODEC);
      Registry.register(var0, "composter", ComposterBlock.CODEC);
      Registry.register(var0, "concrete_powder", ConcretePowderBlock.CODEC);
      Registry.register(var0, "conduit", ConduitBlock.CODEC);
      Registry.register(var0, "copper_bulb_block", CopperBulbBlock.CODEC);
      Registry.register(var0, "coral", CoralBlock.CODEC);
      Registry.register(var0, "coral_fan", CoralFanBlock.CODEC);
      Registry.register(var0, "coral_plant", CoralPlantBlock.CODEC);
      Registry.register(var0, "coral_wall_fan", CoralWallFanBlock.CODEC);
      Registry.register(var0, "crafter", CrafterBlock.CODEC);
      Registry.register(var0, "crafting_table", CraftingTableBlock.CODEC);
      Registry.register(var0, "crop", CropBlock.CODEC);
      Registry.register(var0, "crying_obsidian", CryingObsidianBlock.CODEC);
      Registry.register(var0, "daylight_detector", DaylightDetectorBlock.CODEC);
      Registry.register(var0, "dead_bush", DeadBushBlock.CODEC);
      Registry.register(var0, "decorated_pot", DecoratedPotBlock.CODEC);
      Registry.register(var0, "detector_rail", DetectorRailBlock.CODEC);
      Registry.register(var0, "dirt_path", DirtPathBlock.CODEC);
      Registry.register(var0, "dispenser", DispenserBlock.CODEC);
      Registry.register(var0, "door", DoorBlock.CODEC);
      Registry.register(var0, "double_plant", DoublePlantBlock.CODEC);
      Registry.register(var0, "dragon_egg", DragonEggBlock.CODEC);
      Registry.register(var0, "drop_experience", DropExperienceBlock.CODEC);
      Registry.register(var0, "dropper", DropperBlock.CODEC);
      Registry.register(var0, "enchantment_table", EnchantmentTableBlock.CODEC);
      Registry.register(var0, "ender_chest", EnderChestBlock.CODEC);
      Registry.register(var0, "end_gateway", EndGatewayBlock.CODEC);
      Registry.register(var0, "end_portal", EndPortalBlock.CODEC);
      Registry.register(var0, "end_portal_frame", EndPortalFrameBlock.CODEC);
      Registry.register(var0, "end_rod", EndRodBlock.CODEC);
      Registry.register(var0, "farm", FarmBlock.CODEC);
      Registry.register(var0, "fence", FenceBlock.CODEC);
      Registry.register(var0, "fence_gate", FenceGateBlock.CODEC);
      Registry.register(var0, "fire", FireBlock.CODEC);
      Registry.register(var0, "fletching_table", FletchingTableBlock.CODEC);
      Registry.register(var0, "flower", FlowerBlock.CODEC);
      Registry.register(var0, "flower_pot", FlowerPotBlock.CODEC);
      Registry.register(var0, "frogspawn", FrogspawnBlock.CODEC);
      Registry.register(var0, "frosted_ice", FrostedIceBlock.CODEC);
      Registry.register(var0, "fungus", FungusBlock.CODEC);
      Registry.register(var0, "furnace", FurnaceBlock.CODEC);
      Registry.register(var0, "glazed_terracotta", GlazedTerracottaBlock.CODEC);
      Registry.register(var0, "glow_lichen", GlowLichenBlock.CODEC);
      Registry.register(var0, "grass", GrassBlock.CODEC);
      Registry.register(var0, "grindstone", GrindstoneBlock.CODEC);
      Registry.register(var0, "half_transparent", HalfTransparentBlock.CODEC);
      Registry.register(var0, "hanging_roots", HangingRootsBlock.CODEC);
      Registry.register(var0, "hay", HayBlock.CODEC);
      Registry.register(var0, "honey", HoneyBlock.CODEC);
      Registry.register(var0, "hopper", HopperBlock.CODEC);
      Registry.register(var0, "huge_mushroom", HugeMushroomBlock.CODEC);
      Registry.register(var0, "ice", IceBlock.CODEC);
      Registry.register(var0, "infested", InfestedBlock.CODEC);
      Registry.register(var0, "infested_rotated_pillar", InfestedRotatedPillarBlock.CODEC);
      Registry.register(var0, "iron_bars", IronBarsBlock.CODEC);
      Registry.register(var0, "jack_o_lantern", CarvedPumpkinBlock.CODEC);
      Registry.register(var0, "jigsaw", JigsawBlock.CODEC);
      Registry.register(var0, "jukebox", JukeboxBlock.CODEC);
      Registry.register(var0, "kelp", KelpBlock.CODEC);
      Registry.register(var0, "kelp_plant", KelpPlantBlock.CODEC);
      Registry.register(var0, "ladder", LadderBlock.CODEC);
      Registry.register(var0, "lantern", LanternBlock.CODEC);
      Registry.register(var0, "lava_cauldron", LavaCauldronBlock.CODEC);
      Registry.register(var0, "layered_cauldron", LayeredCauldronBlock.CODEC);
      Registry.register(var0, "leaves", LeavesBlock.CODEC);
      Registry.register(var0, "lectern", LecternBlock.CODEC);
      Registry.register(var0, "lever", LeverBlock.CODEC);
      Registry.register(var0, "light", LightBlock.CODEC);
      Registry.register(var0, "lightning_rod", LightningRodBlock.CODEC);
      Registry.register(var0, "liquid", LiquidBlock.CODEC);
      Registry.register(var0, "loom", LoomBlock.CODEC);
      Registry.register(var0, "magma", MagmaBlock.CODEC);
      Registry.register(var0, "mangrove_leaves", MangroveLeavesBlock.CODEC);
      Registry.register(var0, "mangrove_propagule", MangrovePropaguleBlock.CODEC);
      Registry.register(var0, "mangrove_roots", MangroveRootsBlock.CODEC);
      Registry.register(var0, "moss", MossBlock.CODEC);
      Registry.register(var0, "moving_piston", MovingPistonBlock.CODEC);
      Registry.register(var0, "mud", MudBlock.CODEC);
      Registry.register(var0, "mushroom", MushroomBlock.CODEC);
      Registry.register(var0, "mycelium", MyceliumBlock.CODEC);
      Registry.register(var0, "nether_portal", NetherPortalBlock.CODEC);
      Registry.register(var0, "netherrack", NetherrackBlock.CODEC);
      Registry.register(var0, "nether_sprouts", NetherSproutsBlock.CODEC);
      Registry.register(var0, "nether_wart", NetherWartBlock.CODEC);
      Registry.register(var0, "note", NoteBlock.CODEC);
      Registry.register(var0, "nylium", NyliumBlock.CODEC);
      Registry.register(var0, "observer", ObserverBlock.CODEC);
      Registry.register(var0, "piglinwallskull", PiglinWallSkullBlock.CODEC);
      Registry.register(var0, "pink_petals", PinkPetalsBlock.CODEC);
      Registry.register(var0, "piston_base", PistonBaseBlock.CODEC);
      Registry.register(var0, "piston_head", PistonHeadBlock.CODEC);
      Registry.register(var0, "pitcher_crop", PitcherCropBlock.CODEC);
      Registry.register(var0, "player_head", PlayerHeadBlock.CODEC);
      Registry.register(var0, "player_wall_head", PlayerWallHeadBlock.CODEC);
      Registry.register(var0, "pointed_dripstone", PointedDripstoneBlock.CODEC);
      Registry.register(var0, "potato", PotatoBlock.CODEC);
      Registry.register(var0, "powder_snow", PowderSnowBlock.CODEC);
      Registry.register(var0, "powered", PoweredBlock.CODEC);
      Registry.register(var0, "powered_rail", PoweredRailBlock.CODEC);
      Registry.register(var0, "pressure_plate", PressurePlateBlock.CODEC);
      Registry.register(var0, "pumpkin", PumpkinBlock.CODEC);
      Registry.register(var0, "rail", RailBlock.CODEC);
      Registry.register(var0, "redstone_lamp", RedstoneLampBlock.CODEC);
      Registry.register(var0, "redstone_ore", RedStoneOreBlock.CODEC);
      Registry.register(var0, "redstone_torch", RedstoneTorchBlock.CODEC);
      Registry.register(var0, "redstone_wall_torch", RedstoneWallTorchBlock.CODEC);
      Registry.register(var0, "redstone_wire", RedStoneWireBlock.CODEC);
      Registry.register(var0, "repeater", RepeaterBlock.CODEC);
      Registry.register(var0, "respawn_anchor", RespawnAnchorBlock.CODEC);
      Registry.register(var0, "rooted_dirt", RootedDirtBlock.CODEC);
      Registry.register(var0, "roots", RootsBlock.CODEC);
      Registry.register(var0, "rotated_pillar", RotatedPillarBlock.CODEC);
      Registry.register(var0, "sapling", SaplingBlock.CODEC);
      Registry.register(var0, "scaffolding", ScaffoldingBlock.CODEC);
      Registry.register(var0, "sculk_catalyst", SculkCatalystBlock.CODEC);
      Registry.register(var0, "sculk", SculkBlock.CODEC);
      Registry.register(var0, "sculk_sensor", SculkSensorBlock.CODEC);
      Registry.register(var0, "sculk_shrieker", SculkShriekerBlock.CODEC);
      Registry.register(var0, "sculk_vein", SculkVeinBlock.CODEC);
      Registry.register(var0, "seagrass", SeagrassBlock.CODEC);
      Registry.register(var0, "sea_pickle", SeaPickleBlock.CODEC);
      Registry.register(var0, "shulker_box", ShulkerBoxBlock.CODEC);
      Registry.register(var0, "skull", SkullBlock.CODEC);
      Registry.register(var0, "slab", SlabBlock.CODEC);
      Registry.register(var0, "slime", SlimeBlock.CODEC);
      Registry.register(var0, "small_dripleaf", SmallDripleafBlock.CODEC);
      Registry.register(var0, "smithing_table", SmithingTableBlock.CODEC);
      Registry.register(var0, "smoker", SmokerBlock.CODEC);
      Registry.register(var0, "sniffer_egg", SnifferEggBlock.CODEC);
      Registry.register(var0, "snow_layer", SnowLayerBlock.CODEC);
      Registry.register(var0, "snowy_dirt", SnowyDirtBlock.CODEC);
      Registry.register(var0, "soul_fire", SoulFireBlock.CODEC);
      Registry.register(var0, "soul_sand", SoulSandBlock.CODEC);
      Registry.register(var0, "spawner", SpawnerBlock.CODEC);
      Registry.register(var0, "sponge", SpongeBlock.CODEC);
      Registry.register(var0, "spore_blossom", SporeBlossomBlock.CODEC);
      Registry.register(var0, "stained_glass_pane", StainedGlassPaneBlock.CODEC);
      Registry.register(var0, "stained_glass", StainedGlassBlock.CODEC);
      Registry.register(var0, "stair", StairBlock.CODEC);
      Registry.register(var0, "standing_sign", StandingSignBlock.CODEC);
      Registry.register(var0, "stem", StemBlock.CODEC);
      Registry.register(var0, "stonecutter", StonecutterBlock.CODEC);
      Registry.register(var0, "structure", StructureBlock.CODEC);
      Registry.register(var0, "structure_void", StructureVoidBlock.CODEC);
      Registry.register(var0, "sugar_cane", SugarCaneBlock.CODEC);
      Registry.register(var0, "sweet_berry_bush", SweetBerryBushBlock.CODEC);
      Registry.register(var0, "tall_flower", TallFlowerBlock.CODEC);
      Registry.register(var0, "tall_grass", TallGrassBlock.CODEC);
      Registry.register(var0, "tall_seagrass", TallSeagrassBlock.CODEC);
      Registry.register(var0, "target", TargetBlock.CODEC);
      Registry.register(var0, "tinted_glass", TintedGlassBlock.CODEC);
      Registry.register(var0, "tnt", TntBlock.CODEC);
      Registry.register(var0, "torchflower_crop", TorchflowerCropBlock.CODEC);
      Registry.register(var0, "torch", TorchBlock.CODEC);
      Registry.register(var0, "transparent", TransparentBlock.CODEC);
      Registry.register(var0, "trapdoor", TrapDoorBlock.CODEC);
      Registry.register(var0, "trapped_chest", TrappedChestBlock.CODEC);
      Registry.register(var0, "trial_spawner", TrialSpawnerBlock.CODEC);
      Registry.register(var0, "trip_wire_hook", TripWireHookBlock.CODEC);
      Registry.register(var0, "tripwire", TripWireBlock.CODEC);
      Registry.register(var0, "turtle_egg", TurtleEggBlock.CODEC);
      Registry.register(var0, "twisting_vines_plant", TwistingVinesPlantBlock.CODEC);
      Registry.register(var0, "twisting_vines", TwistingVinesBlock.CODEC);
      Registry.register(var0, "vine", VineBlock.CODEC);
      Registry.register(var0, "wall_banner", WallBannerBlock.CODEC);
      Registry.register(var0, "wall_hanging_sign", WallHangingSignBlock.CODEC);
      Registry.register(var0, "wall_sign", WallSignBlock.CODEC);
      Registry.register(var0, "wall_skull", WallSkullBlock.CODEC);
      Registry.register(var0, "wall_torch", WallTorchBlock.CODEC);
      Registry.register(var0, "wall", WallBlock.CODEC);
      Registry.register(var0, "waterlily", WaterlilyBlock.CODEC);
      Registry.register(var0, "waterlogged_transparent", WaterloggedTransparentBlock.CODEC);
      Registry.register(var0, "weathering_copper_bulb", WeatheringCopperBulbBlock.CODEC);
      Registry.register(var0, "weathering_copper_door", WeatheringCopperDoorBlock.CODEC);
      Registry.register(var0, "weathering_copper_full", WeatheringCopperFullBlock.CODEC);
      Registry.register(var0, "weathering_copper_grate", WeatheringCopperGrateBlock.CODEC);
      Registry.register(var0, "weathering_copper_slab", WeatheringCopperSlabBlock.CODEC);
      Registry.register(var0, "weathering_copper_stair", WeatheringCopperStairBlock.CODEC);
      Registry.register(var0, "weathering_copper_trap_door", WeatheringCopperTrapDoorBlock.CODEC);
      Registry.register(var0, "web", WebBlock.CODEC);
      Registry.register(var0, "weeping_vines_plant", WeepingVinesPlantBlock.CODEC);
      Registry.register(var0, "weeping_vines", WeepingVinesBlock.CODEC);
      Registry.register(var0, "weighted_pressure_plate", WeightedPressurePlateBlock.CODEC);
      Registry.register(var0, "wet_sponge", WetSpongeBlock.CODEC);
      Registry.register(var0, "wither_rose", WitherRoseBlock.CODEC);
      Registry.register(var0, "wither_skull", WitherSkullBlock.CODEC);
      Registry.register(var0, "wither_wall_skull", WitherWallSkullBlock.CODEC);
      Registry.register(var0, "wool_carpet", WoolCarpetBlock.CODEC);
      return var1;
   }
}
