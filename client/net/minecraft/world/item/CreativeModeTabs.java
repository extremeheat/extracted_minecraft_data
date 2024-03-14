package net.minecraft.world.item;

import com.mojang.datafixers.util.Pair;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.InstrumentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.PaintingVariantTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.SuspiciousEffectHolder;

public class CreativeModeTabs {
   private static final ResourceKey<CreativeModeTab> BUILDING_BLOCKS = createKey("building_blocks");
   private static final ResourceKey<CreativeModeTab> COLORED_BLOCKS = createKey("colored_blocks");
   private static final ResourceKey<CreativeModeTab> NATURAL_BLOCKS = createKey("natural_blocks");
   private static final ResourceKey<CreativeModeTab> FUNCTIONAL_BLOCKS = createKey("functional_blocks");
   private static final ResourceKey<CreativeModeTab> REDSTONE_BLOCKS = createKey("redstone_blocks");
   private static final ResourceKey<CreativeModeTab> HOTBAR = createKey("hotbar");
   private static final ResourceKey<CreativeModeTab> SEARCH = createKey("search");
   private static final ResourceKey<CreativeModeTab> TOOLS_AND_UTILITIES = createKey("tools_and_utilities");
   private static final ResourceKey<CreativeModeTab> COMBAT = createKey("combat");
   private static final ResourceKey<CreativeModeTab> FOOD_AND_DRINKS = createKey("food_and_drinks");
   private static final ResourceKey<CreativeModeTab> INGREDIENTS = createKey("ingredients");
   private static final ResourceKey<CreativeModeTab> SPAWN_EGGS = createKey("spawn_eggs");
   private static final ResourceKey<CreativeModeTab> OP_BLOCKS = createKey("op_blocks");
   private static final ResourceKey<CreativeModeTab> INVENTORY = createKey("inventory");
   private static final Comparator<Holder<PaintingVariant>> PAINTING_COMPARATOR = Comparator.comparing(
      Holder::value, Comparator.<PaintingVariant>comparingInt(var0 -> var0.getHeight() * var0.getWidth()).thenComparing(PaintingVariant::getWidth)
   );
   @Nullable
   private static CreativeModeTab.ItemDisplayParameters CACHED_PARAMETERS;

   public CreativeModeTabs() {
      super();
   }

   private static ResourceKey<CreativeModeTab> createKey(String var0) {
      return ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation(var0));
   }

   public static CreativeModeTab bootstrap(Registry<CreativeModeTab> var0) {
      Registry.register(
         var0,
         BUILDING_BLOCKS,
         CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .title(Component.translatable("itemGroup.buildingBlocks"))
            .icon(() -> new ItemStack(Blocks.BRICKS))
            .displayItems((var0x, var1) -> {
               var1.accept(Items.OAK_LOG);
               var1.accept(Items.OAK_WOOD);
               var1.accept(Items.STRIPPED_OAK_LOG);
               var1.accept(Items.STRIPPED_OAK_WOOD);
               var1.accept(Items.OAK_PLANKS);
               var1.accept(Items.OAK_STAIRS);
               var1.accept(Items.OAK_SLAB);
               var1.accept(Items.OAK_FENCE);
               var1.accept(Items.OAK_FENCE_GATE);
               var1.accept(Items.OAK_DOOR);
               var1.accept(Items.OAK_TRAPDOOR);
               var1.accept(Items.OAK_PRESSURE_PLATE);
               var1.accept(Items.OAK_BUTTON);
               var1.accept(Items.SPRUCE_LOG);
               var1.accept(Items.SPRUCE_WOOD);
               var1.accept(Items.STRIPPED_SPRUCE_LOG);
               var1.accept(Items.STRIPPED_SPRUCE_WOOD);
               var1.accept(Items.SPRUCE_PLANKS);
               var1.accept(Items.SPRUCE_STAIRS);
               var1.accept(Items.SPRUCE_SLAB);
               var1.accept(Items.SPRUCE_FENCE);
               var1.accept(Items.SPRUCE_FENCE_GATE);
               var1.accept(Items.SPRUCE_DOOR);
               var1.accept(Items.SPRUCE_TRAPDOOR);
               var1.accept(Items.SPRUCE_PRESSURE_PLATE);
               var1.accept(Items.SPRUCE_BUTTON);
               var1.accept(Items.BIRCH_LOG);
               var1.accept(Items.BIRCH_WOOD);
               var1.accept(Items.STRIPPED_BIRCH_LOG);
               var1.accept(Items.STRIPPED_BIRCH_WOOD);
               var1.accept(Items.BIRCH_PLANKS);
               var1.accept(Items.BIRCH_STAIRS);
               var1.accept(Items.BIRCH_SLAB);
               var1.accept(Items.BIRCH_FENCE);
               var1.accept(Items.BIRCH_FENCE_GATE);
               var1.accept(Items.BIRCH_DOOR);
               var1.accept(Items.BIRCH_TRAPDOOR);
               var1.accept(Items.BIRCH_PRESSURE_PLATE);
               var1.accept(Items.BIRCH_BUTTON);
               var1.accept(Items.JUNGLE_LOG);
               var1.accept(Items.JUNGLE_WOOD);
               var1.accept(Items.STRIPPED_JUNGLE_LOG);
               var1.accept(Items.STRIPPED_JUNGLE_WOOD);
               var1.accept(Items.JUNGLE_PLANKS);
               var1.accept(Items.JUNGLE_STAIRS);
               var1.accept(Items.JUNGLE_SLAB);
               var1.accept(Items.JUNGLE_FENCE);
               var1.accept(Items.JUNGLE_FENCE_GATE);
               var1.accept(Items.JUNGLE_DOOR);
               var1.accept(Items.JUNGLE_TRAPDOOR);
               var1.accept(Items.JUNGLE_PRESSURE_PLATE);
               var1.accept(Items.JUNGLE_BUTTON);
               var1.accept(Items.ACACIA_LOG);
               var1.accept(Items.ACACIA_WOOD);
               var1.accept(Items.STRIPPED_ACACIA_LOG);
               var1.accept(Items.STRIPPED_ACACIA_WOOD);
               var1.accept(Items.ACACIA_PLANKS);
               var1.accept(Items.ACACIA_STAIRS);
               var1.accept(Items.ACACIA_SLAB);
               var1.accept(Items.ACACIA_FENCE);
               var1.accept(Items.ACACIA_FENCE_GATE);
               var1.accept(Items.ACACIA_DOOR);
               var1.accept(Items.ACACIA_TRAPDOOR);
               var1.accept(Items.ACACIA_PRESSURE_PLATE);
               var1.accept(Items.ACACIA_BUTTON);
               var1.accept(Items.DARK_OAK_LOG);
               var1.accept(Items.DARK_OAK_WOOD);
               var1.accept(Items.STRIPPED_DARK_OAK_LOG);
               var1.accept(Items.STRIPPED_DARK_OAK_WOOD);
               var1.accept(Items.DARK_OAK_PLANKS);
               var1.accept(Items.DARK_OAK_STAIRS);
               var1.accept(Items.DARK_OAK_SLAB);
               var1.accept(Items.DARK_OAK_FENCE);
               var1.accept(Items.DARK_OAK_FENCE_GATE);
               var1.accept(Items.DARK_OAK_DOOR);
               var1.accept(Items.DARK_OAK_TRAPDOOR);
               var1.accept(Items.DARK_OAK_PRESSURE_PLATE);
               var1.accept(Items.DARK_OAK_BUTTON);
               var1.accept(Items.MANGROVE_LOG);
               var1.accept(Items.MANGROVE_WOOD);
               var1.accept(Items.STRIPPED_MANGROVE_LOG);
               var1.accept(Items.STRIPPED_MANGROVE_WOOD);
               var1.accept(Items.MANGROVE_PLANKS);
               var1.accept(Items.MANGROVE_STAIRS);
               var1.accept(Items.MANGROVE_SLAB);
               var1.accept(Items.MANGROVE_FENCE);
               var1.accept(Items.MANGROVE_FENCE_GATE);
               var1.accept(Items.MANGROVE_DOOR);
               var1.accept(Items.MANGROVE_TRAPDOOR);
               var1.accept(Items.MANGROVE_PRESSURE_PLATE);
               var1.accept(Items.MANGROVE_BUTTON);
               var1.accept(Items.CHERRY_LOG);
               var1.accept(Items.CHERRY_WOOD);
               var1.accept(Items.STRIPPED_CHERRY_LOG);
               var1.accept(Items.STRIPPED_CHERRY_WOOD);
               var1.accept(Items.CHERRY_PLANKS);
               var1.accept(Items.CHERRY_STAIRS);
               var1.accept(Items.CHERRY_SLAB);
               var1.accept(Items.CHERRY_FENCE);
               var1.accept(Items.CHERRY_FENCE_GATE);
               var1.accept(Items.CHERRY_DOOR);
               var1.accept(Items.CHERRY_TRAPDOOR);
               var1.accept(Items.CHERRY_PRESSURE_PLATE);
               var1.accept(Items.CHERRY_BUTTON);
               var1.accept(Items.BAMBOO_BLOCK);
               var1.accept(Items.STRIPPED_BAMBOO_BLOCK);
               var1.accept(Items.BAMBOO_PLANKS);
               var1.accept(Items.BAMBOO_MOSAIC);
               var1.accept(Items.BAMBOO_STAIRS);
               var1.accept(Items.BAMBOO_MOSAIC_STAIRS);
               var1.accept(Items.BAMBOO_SLAB);
               var1.accept(Items.BAMBOO_MOSAIC_SLAB);
               var1.accept(Items.BAMBOO_FENCE);
               var1.accept(Items.BAMBOO_FENCE_GATE);
               var1.accept(Items.BAMBOO_DOOR);
               var1.accept(Items.BAMBOO_TRAPDOOR);
               var1.accept(Items.BAMBOO_PRESSURE_PLATE);
               var1.accept(Items.BAMBOO_BUTTON);
               var1.accept(Items.CRIMSON_STEM);
               var1.accept(Items.CRIMSON_HYPHAE);
               var1.accept(Items.STRIPPED_CRIMSON_STEM);
               var1.accept(Items.STRIPPED_CRIMSON_HYPHAE);
               var1.accept(Items.CRIMSON_PLANKS);
               var1.accept(Items.CRIMSON_STAIRS);
               var1.accept(Items.CRIMSON_SLAB);
               var1.accept(Items.CRIMSON_FENCE);
               var1.accept(Items.CRIMSON_FENCE_GATE);
               var1.accept(Items.CRIMSON_DOOR);
               var1.accept(Items.CRIMSON_TRAPDOOR);
               var1.accept(Items.CRIMSON_PRESSURE_PLATE);
               var1.accept(Items.CRIMSON_BUTTON);
               var1.accept(Items.WARPED_STEM);
               var1.accept(Items.WARPED_HYPHAE);
               var1.accept(Items.STRIPPED_WARPED_STEM);
               var1.accept(Items.STRIPPED_WARPED_HYPHAE);
               var1.accept(Items.WARPED_PLANKS);
               var1.accept(Items.WARPED_STAIRS);
               var1.accept(Items.WARPED_SLAB);
               var1.accept(Items.WARPED_FENCE);
               var1.accept(Items.WARPED_FENCE_GATE);
               var1.accept(Items.WARPED_DOOR);
               var1.accept(Items.WARPED_TRAPDOOR);
               var1.accept(Items.WARPED_PRESSURE_PLATE);
               var1.accept(Items.WARPED_BUTTON);
               var1.accept(Items.STONE);
               var1.accept(Items.STONE_STAIRS);
               var1.accept(Items.STONE_SLAB);
               var1.accept(Items.STONE_PRESSURE_PLATE);
               var1.accept(Items.STONE_BUTTON);
               var1.accept(Items.COBBLESTONE);
               var1.accept(Items.COBBLESTONE_STAIRS);
               var1.accept(Items.COBBLESTONE_SLAB);
               var1.accept(Items.COBBLESTONE_WALL);
               var1.accept(Items.MOSSY_COBBLESTONE);
               var1.accept(Items.MOSSY_COBBLESTONE_STAIRS);
               var1.accept(Items.MOSSY_COBBLESTONE_SLAB);
               var1.accept(Items.MOSSY_COBBLESTONE_WALL);
               var1.accept(Items.SMOOTH_STONE);
               var1.accept(Items.SMOOTH_STONE_SLAB);
               var1.accept(Items.STONE_BRICKS);
               var1.accept(Items.CRACKED_STONE_BRICKS);
               var1.accept(Items.STONE_BRICK_STAIRS);
               var1.accept(Items.STONE_BRICK_SLAB);
               var1.accept(Items.STONE_BRICK_WALL);
               var1.accept(Items.CHISELED_STONE_BRICKS);
               var1.accept(Items.MOSSY_STONE_BRICKS);
               var1.accept(Items.MOSSY_STONE_BRICK_STAIRS);
               var1.accept(Items.MOSSY_STONE_BRICK_SLAB);
               var1.accept(Items.MOSSY_STONE_BRICK_WALL);
               var1.accept(Items.GRANITE);
               var1.accept(Items.GRANITE_STAIRS);
               var1.accept(Items.GRANITE_SLAB);
               var1.accept(Items.GRANITE_WALL);
               var1.accept(Items.POLISHED_GRANITE);
               var1.accept(Items.POLISHED_GRANITE_STAIRS);
               var1.accept(Items.POLISHED_GRANITE_SLAB);
               var1.accept(Items.DIORITE);
               var1.accept(Items.DIORITE_STAIRS);
               var1.accept(Items.DIORITE_SLAB);
               var1.accept(Items.DIORITE_WALL);
               var1.accept(Items.POLISHED_DIORITE);
               var1.accept(Items.POLISHED_DIORITE_STAIRS);
               var1.accept(Items.POLISHED_DIORITE_SLAB);
               var1.accept(Items.ANDESITE);
               var1.accept(Items.ANDESITE_STAIRS);
               var1.accept(Items.ANDESITE_SLAB);
               var1.accept(Items.ANDESITE_WALL);
               var1.accept(Items.POLISHED_ANDESITE);
               var1.accept(Items.POLISHED_ANDESITE_STAIRS);
               var1.accept(Items.POLISHED_ANDESITE_SLAB);
               var1.accept(Items.DEEPSLATE);
               var1.accept(Items.COBBLED_DEEPSLATE);
               var1.accept(Items.COBBLED_DEEPSLATE_STAIRS);
               var1.accept(Items.COBBLED_DEEPSLATE_SLAB);
               var1.accept(Items.COBBLED_DEEPSLATE_WALL);
               var1.accept(Items.CHISELED_DEEPSLATE);
               var1.accept(Items.POLISHED_DEEPSLATE);
               var1.accept(Items.POLISHED_DEEPSLATE_STAIRS);
               var1.accept(Items.POLISHED_DEEPSLATE_SLAB);
               var1.accept(Items.POLISHED_DEEPSLATE_WALL);
               var1.accept(Items.DEEPSLATE_BRICKS);
               var1.accept(Items.CRACKED_DEEPSLATE_BRICKS);
               var1.accept(Items.DEEPSLATE_BRICK_STAIRS);
               var1.accept(Items.DEEPSLATE_BRICK_SLAB);
               var1.accept(Items.DEEPSLATE_BRICK_WALL);
               var1.accept(Items.DEEPSLATE_TILES);
               var1.accept(Items.CRACKED_DEEPSLATE_TILES);
               var1.accept(Items.DEEPSLATE_TILE_STAIRS);
               var1.accept(Items.DEEPSLATE_TILE_SLAB);
               var1.accept(Items.DEEPSLATE_TILE_WALL);
               var1.accept(Items.REINFORCED_DEEPSLATE);
               var1.accept(Items.TUFF);
               var1.accept(Items.TUFF_STAIRS);
               var1.accept(Items.TUFF_SLAB);
               var1.accept(Items.TUFF_WALL);
               var1.accept(Items.CHISELED_TUFF);
               var1.accept(Items.POLISHED_TUFF);
               var1.accept(Items.POLISHED_TUFF_STAIRS);
               var1.accept(Items.POLISHED_TUFF_SLAB);
               var1.accept(Items.POLISHED_TUFF_WALL);
               var1.accept(Items.TUFF_BRICKS);
               var1.accept(Items.TUFF_BRICK_STAIRS);
               var1.accept(Items.TUFF_BRICK_SLAB);
               var1.accept(Items.TUFF_BRICK_WALL);
               var1.accept(Items.CHISELED_TUFF_BRICKS);
               var1.accept(Items.BRICKS);
               var1.accept(Items.BRICK_STAIRS);
               var1.accept(Items.BRICK_SLAB);
               var1.accept(Items.BRICK_WALL);
               var1.accept(Items.PACKED_MUD);
               var1.accept(Items.MUD_BRICKS);
               var1.accept(Items.MUD_BRICK_STAIRS);
               var1.accept(Items.MUD_BRICK_SLAB);
               var1.accept(Items.MUD_BRICK_WALL);
               var1.accept(Items.SANDSTONE);
               var1.accept(Items.SANDSTONE_STAIRS);
               var1.accept(Items.SANDSTONE_SLAB);
               var1.accept(Items.SANDSTONE_WALL);
               var1.accept(Items.CHISELED_SANDSTONE);
               var1.accept(Items.SMOOTH_SANDSTONE);
               var1.accept(Items.SMOOTH_SANDSTONE_STAIRS);
               var1.accept(Items.SMOOTH_SANDSTONE_SLAB);
               var1.accept(Items.CUT_SANDSTONE);
               var1.accept(Items.CUT_STANDSTONE_SLAB);
               var1.accept(Items.RED_SANDSTONE);
               var1.accept(Items.RED_SANDSTONE_STAIRS);
               var1.accept(Items.RED_SANDSTONE_SLAB);
               var1.accept(Items.RED_SANDSTONE_WALL);
               var1.accept(Items.CHISELED_RED_SANDSTONE);
               var1.accept(Items.SMOOTH_RED_SANDSTONE);
               var1.accept(Items.SMOOTH_RED_SANDSTONE_STAIRS);
               var1.accept(Items.SMOOTH_RED_SANDSTONE_SLAB);
               var1.accept(Items.CUT_RED_SANDSTONE);
               var1.accept(Items.CUT_RED_SANDSTONE_SLAB);
               var1.accept(Items.SEA_LANTERN);
               var1.accept(Items.PRISMARINE);
               var1.accept(Items.PRISMARINE_STAIRS);
               var1.accept(Items.PRISMARINE_SLAB);
               var1.accept(Items.PRISMARINE_WALL);
               var1.accept(Items.PRISMARINE_BRICKS);
               var1.accept(Items.PRISMARINE_BRICK_STAIRS);
               var1.accept(Items.PRISMARINE_BRICK_SLAB);
               var1.accept(Items.DARK_PRISMARINE);
               var1.accept(Items.DARK_PRISMARINE_STAIRS);
               var1.accept(Items.DARK_PRISMARINE_SLAB);
               var1.accept(Items.NETHERRACK);
               var1.accept(Items.NETHER_BRICKS);
               var1.accept(Items.CRACKED_NETHER_BRICKS);
               var1.accept(Items.NETHER_BRICK_STAIRS);
               var1.accept(Items.NETHER_BRICK_SLAB);
               var1.accept(Items.NETHER_BRICK_WALL);
               var1.accept(Items.NETHER_BRICK_FENCE);
               var1.accept(Items.CHISELED_NETHER_BRICKS);
               var1.accept(Items.RED_NETHER_BRICKS);
               var1.accept(Items.RED_NETHER_BRICK_STAIRS);
               var1.accept(Items.RED_NETHER_BRICK_SLAB);
               var1.accept(Items.RED_NETHER_BRICK_WALL);
               var1.accept(Items.BASALT);
               var1.accept(Items.SMOOTH_BASALT);
               var1.accept(Items.POLISHED_BASALT);
               var1.accept(Items.BLACKSTONE);
               var1.accept(Items.GILDED_BLACKSTONE);
               var1.accept(Items.BLACKSTONE_STAIRS);
               var1.accept(Items.BLACKSTONE_SLAB);
               var1.accept(Items.BLACKSTONE_WALL);
               var1.accept(Items.CHISELED_POLISHED_BLACKSTONE);
               var1.accept(Items.POLISHED_BLACKSTONE);
               var1.accept(Items.POLISHED_BLACKSTONE_STAIRS);
               var1.accept(Items.POLISHED_BLACKSTONE_SLAB);
               var1.accept(Items.POLISHED_BLACKSTONE_WALL);
               var1.accept(Items.POLISHED_BLACKSTONE_PRESSURE_PLATE);
               var1.accept(Items.POLISHED_BLACKSTONE_BUTTON);
               var1.accept(Items.POLISHED_BLACKSTONE_BRICKS);
               var1.accept(Items.CRACKED_POLISHED_BLACKSTONE_BRICKS);
               var1.accept(Items.POLISHED_BLACKSTONE_BRICK_STAIRS);
               var1.accept(Items.POLISHED_BLACKSTONE_BRICK_SLAB);
               var1.accept(Items.POLISHED_BLACKSTONE_BRICK_WALL);
               var1.accept(Items.END_STONE);
               var1.accept(Items.END_STONE_BRICKS);
               var1.accept(Items.END_STONE_BRICK_STAIRS);
               var1.accept(Items.END_STONE_BRICK_SLAB);
               var1.accept(Items.END_STONE_BRICK_WALL);
               var1.accept(Items.PURPUR_BLOCK);
               var1.accept(Items.PURPUR_PILLAR);
               var1.accept(Items.PURPUR_STAIRS);
               var1.accept(Items.PURPUR_SLAB);
               var1.accept(Items.COAL_BLOCK);
               var1.accept(Items.IRON_BLOCK);
               var1.accept(Items.IRON_BARS);
               var1.accept(Items.IRON_DOOR);
               var1.accept(Items.IRON_TRAPDOOR);
               var1.accept(Items.HEAVY_WEIGHTED_PRESSURE_PLATE);
               var1.accept(Items.CHAIN);
               var1.accept(Items.GOLD_BLOCK);
               var1.accept(Items.LIGHT_WEIGHTED_PRESSURE_PLATE);
               var1.accept(Items.REDSTONE_BLOCK);
               var1.accept(Items.EMERALD_BLOCK);
               var1.accept(Items.LAPIS_BLOCK);
               var1.accept(Items.DIAMOND_BLOCK);
               var1.accept(Items.NETHERITE_BLOCK);
               var1.accept(Items.QUARTZ_BLOCK);
               var1.accept(Items.QUARTZ_STAIRS);
               var1.accept(Items.QUARTZ_SLAB);
               var1.accept(Items.CHISELED_QUARTZ_BLOCK);
               var1.accept(Items.QUARTZ_BRICKS);
               var1.accept(Items.QUARTZ_PILLAR);
               var1.accept(Items.SMOOTH_QUARTZ);
               var1.accept(Items.SMOOTH_QUARTZ_STAIRS);
               var1.accept(Items.SMOOTH_QUARTZ_SLAB);
               var1.accept(Items.AMETHYST_BLOCK);
               var1.accept(Items.COPPER_BLOCK);
               var1.accept(Items.CHISELED_COPPER);
               var1.accept(Items.COPPER_GRATE);
               var1.accept(Items.CUT_COPPER);
               var1.accept(Items.CUT_COPPER_STAIRS);
               var1.accept(Items.CUT_COPPER_SLAB);
               var1.accept(Items.COPPER_DOOR);
               var1.accept(Items.COPPER_TRAPDOOR);
               var1.accept(Items.COPPER_BULB);
               var1.accept(Items.EXPOSED_COPPER);
               var1.accept(Items.EXPOSED_CHISELED_COPPER);
               var1.accept(Items.EXPOSED_COPPER_GRATE);
               var1.accept(Items.EXPOSED_CUT_COPPER);
               var1.accept(Items.EXPOSED_CUT_COPPER_STAIRS);
               var1.accept(Items.EXPOSED_CUT_COPPER_SLAB);
               var1.accept(Items.EXPOSED_COPPER_DOOR);
               var1.accept(Items.EXPOSED_COPPER_TRAPDOOR);
               var1.accept(Items.EXPOSED_COPPER_BULB);
               var1.accept(Items.WEATHERED_COPPER);
               var1.accept(Items.WEATHERED_CHISELED_COPPER);
               var1.accept(Items.WEATHERED_COPPER_GRATE);
               var1.accept(Items.WEATHERED_CUT_COPPER);
               var1.accept(Items.WEATHERED_CUT_COPPER_STAIRS);
               var1.accept(Items.WEATHERED_CUT_COPPER_SLAB);
               var1.accept(Items.WEATHERED_COPPER_DOOR);
               var1.accept(Items.WEATHERED_COPPER_TRAPDOOR);
               var1.accept(Items.WEATHERED_COPPER_BULB);
               var1.accept(Items.OXIDIZED_COPPER);
               var1.accept(Items.OXIDIZED_CHISELED_COPPER);
               var1.accept(Items.OXIDIZED_COPPER_GRATE);
               var1.accept(Items.OXIDIZED_CUT_COPPER);
               var1.accept(Items.OXIDIZED_CUT_COPPER_STAIRS);
               var1.accept(Items.OXIDIZED_CUT_COPPER_SLAB);
               var1.accept(Items.OXIDIZED_COPPER_DOOR);
               var1.accept(Items.OXIDIZED_COPPER_TRAPDOOR);
               var1.accept(Items.OXIDIZED_COPPER_BULB);
               var1.accept(Items.WAXED_COPPER_BLOCK);
               var1.accept(Items.WAXED_CHISELED_COPPER);
               var1.accept(Items.WAXED_COPPER_GRATE);
               var1.accept(Items.WAXED_CUT_COPPER);
               var1.accept(Items.WAXED_CUT_COPPER_STAIRS);
               var1.accept(Items.WAXED_CUT_COPPER_SLAB);
               var1.accept(Items.WAXED_COPPER_DOOR);
               var1.accept(Items.WAXED_COPPER_TRAPDOOR);
               var1.accept(Items.WAXED_COPPER_BULB);
               var1.accept(Items.WAXED_EXPOSED_COPPER);
               var1.accept(Items.WAXED_EXPOSED_CHISELED_COPPER);
               var1.accept(Items.WAXED_EXPOSED_COPPER_GRATE);
               var1.accept(Items.WAXED_EXPOSED_CUT_COPPER);
               var1.accept(Items.WAXED_EXPOSED_CUT_COPPER_STAIRS);
               var1.accept(Items.WAXED_EXPOSED_CUT_COPPER_SLAB);
               var1.accept(Items.WAXED_EXPOSED_COPPER_DOOR);
               var1.accept(Items.WAXED_EXPOSED_COPPER_TRAPDOOR);
               var1.accept(Items.WAXED_EXPOSED_COPPER_BULB);
               var1.accept(Items.WAXED_WEATHERED_COPPER);
               var1.accept(Items.WAXED_WEATHERED_CHISELED_COPPER);
               var1.accept(Items.WAXED_WEATHERED_COPPER_GRATE);
               var1.accept(Items.WAXED_WEATHERED_CUT_COPPER);
               var1.accept(Items.WAXED_WEATHERED_CUT_COPPER_STAIRS);
               var1.accept(Items.WAXED_WEATHERED_CUT_COPPER_SLAB);
               var1.accept(Items.WAXED_WEATHERED_COPPER_DOOR);
               var1.accept(Items.WAXED_WEATHERED_COPPER_TRAPDOOR);
               var1.accept(Items.WAXED_WEATHERED_COPPER_BULB);
               var1.accept(Items.WAXED_OXIDIZED_COPPER);
               var1.accept(Items.WAXED_OXIDIZED_CHISELED_COPPER);
               var1.accept(Items.WAXED_OXIDIZED_COPPER_GRATE);
               var1.accept(Items.WAXED_OXIDIZED_CUT_COPPER);
               var1.accept(Items.WAXED_OXIDIZED_CUT_COPPER_STAIRS);
               var1.accept(Items.WAXED_OXIDIZED_CUT_COPPER_SLAB);
               var1.accept(Items.WAXED_OXIDIZED_COPPER_DOOR);
               var1.accept(Items.WAXED_OXIDIZED_COPPER_TRAPDOOR);
               var1.accept(Items.WAXED_OXIDIZED_COPPER_BULB);
            })
            .build()
      );
      Registry.register(
         var0,
         COLORED_BLOCKS,
         CreativeModeTab.builder(CreativeModeTab.Row.TOP, 1)
            .title(Component.translatable("itemGroup.coloredBlocks"))
            .icon(() -> new ItemStack(Blocks.CYAN_WOOL))
            .displayItems((var0x, var1) -> {
               var1.accept(Items.WHITE_WOOL);
               var1.accept(Items.LIGHT_GRAY_WOOL);
               var1.accept(Items.GRAY_WOOL);
               var1.accept(Items.BLACK_WOOL);
               var1.accept(Items.BROWN_WOOL);
               var1.accept(Items.RED_WOOL);
               var1.accept(Items.ORANGE_WOOL);
               var1.accept(Items.YELLOW_WOOL);
               var1.accept(Items.LIME_WOOL);
               var1.accept(Items.GREEN_WOOL);
               var1.accept(Items.CYAN_WOOL);
               var1.accept(Items.LIGHT_BLUE_WOOL);
               var1.accept(Items.BLUE_WOOL);
               var1.accept(Items.PURPLE_WOOL);
               var1.accept(Items.MAGENTA_WOOL);
               var1.accept(Items.PINK_WOOL);
               var1.accept(Items.WHITE_CARPET);
               var1.accept(Items.LIGHT_GRAY_CARPET);
               var1.accept(Items.GRAY_CARPET);
               var1.accept(Items.BLACK_CARPET);
               var1.accept(Items.BROWN_CARPET);
               var1.accept(Items.RED_CARPET);
               var1.accept(Items.ORANGE_CARPET);
               var1.accept(Items.YELLOW_CARPET);
               var1.accept(Items.LIME_CARPET);
               var1.accept(Items.GREEN_CARPET);
               var1.accept(Items.CYAN_CARPET);
               var1.accept(Items.LIGHT_BLUE_CARPET);
               var1.accept(Items.BLUE_CARPET);
               var1.accept(Items.PURPLE_CARPET);
               var1.accept(Items.MAGENTA_CARPET);
               var1.accept(Items.PINK_CARPET);
               var1.accept(Items.TERRACOTTA);
               var1.accept(Items.WHITE_TERRACOTTA);
               var1.accept(Items.LIGHT_GRAY_TERRACOTTA);
               var1.accept(Items.GRAY_TERRACOTTA);
               var1.accept(Items.BLACK_TERRACOTTA);
               var1.accept(Items.BROWN_TERRACOTTA);
               var1.accept(Items.RED_TERRACOTTA);
               var1.accept(Items.ORANGE_TERRACOTTA);
               var1.accept(Items.YELLOW_TERRACOTTA);
               var1.accept(Items.LIME_TERRACOTTA);
               var1.accept(Items.GREEN_TERRACOTTA);
               var1.accept(Items.CYAN_TERRACOTTA);
               var1.accept(Items.LIGHT_BLUE_TERRACOTTA);
               var1.accept(Items.BLUE_TERRACOTTA);
               var1.accept(Items.PURPLE_TERRACOTTA);
               var1.accept(Items.MAGENTA_TERRACOTTA);
               var1.accept(Items.PINK_TERRACOTTA);
               var1.accept(Items.WHITE_CONCRETE);
               var1.accept(Items.LIGHT_GRAY_CONCRETE);
               var1.accept(Items.GRAY_CONCRETE);
               var1.accept(Items.BLACK_CONCRETE);
               var1.accept(Items.BROWN_CONCRETE);
               var1.accept(Items.RED_CONCRETE);
               var1.accept(Items.ORANGE_CONCRETE);
               var1.accept(Items.YELLOW_CONCRETE);
               var1.accept(Items.LIME_CONCRETE);
               var1.accept(Items.GREEN_CONCRETE);
               var1.accept(Items.CYAN_CONCRETE);
               var1.accept(Items.LIGHT_BLUE_CONCRETE);
               var1.accept(Items.BLUE_CONCRETE);
               var1.accept(Items.PURPLE_CONCRETE);
               var1.accept(Items.MAGENTA_CONCRETE);
               var1.accept(Items.PINK_CONCRETE);
               var1.accept(Items.WHITE_CONCRETE_POWDER);
               var1.accept(Items.LIGHT_GRAY_CONCRETE_POWDER);
               var1.accept(Items.GRAY_CONCRETE_POWDER);
               var1.accept(Items.BLACK_CONCRETE_POWDER);
               var1.accept(Items.BROWN_CONCRETE_POWDER);
               var1.accept(Items.RED_CONCRETE_POWDER);
               var1.accept(Items.ORANGE_CONCRETE_POWDER);
               var1.accept(Items.YELLOW_CONCRETE_POWDER);
               var1.accept(Items.LIME_CONCRETE_POWDER);
               var1.accept(Items.GREEN_CONCRETE_POWDER);
               var1.accept(Items.CYAN_CONCRETE_POWDER);
               var1.accept(Items.LIGHT_BLUE_CONCRETE_POWDER);
               var1.accept(Items.BLUE_CONCRETE_POWDER);
               var1.accept(Items.PURPLE_CONCRETE_POWDER);
               var1.accept(Items.MAGENTA_CONCRETE_POWDER);
               var1.accept(Items.PINK_CONCRETE_POWDER);
               var1.accept(Items.WHITE_GLAZED_TERRACOTTA);
               var1.accept(Items.LIGHT_GRAY_GLAZED_TERRACOTTA);
               var1.accept(Items.GRAY_GLAZED_TERRACOTTA);
               var1.accept(Items.BLACK_GLAZED_TERRACOTTA);
               var1.accept(Items.BROWN_GLAZED_TERRACOTTA);
               var1.accept(Items.RED_GLAZED_TERRACOTTA);
               var1.accept(Items.ORANGE_GLAZED_TERRACOTTA);
               var1.accept(Items.YELLOW_GLAZED_TERRACOTTA);
               var1.accept(Items.LIME_GLAZED_TERRACOTTA);
               var1.accept(Items.GREEN_GLAZED_TERRACOTTA);
               var1.accept(Items.CYAN_GLAZED_TERRACOTTA);
               var1.accept(Items.LIGHT_BLUE_GLAZED_TERRACOTTA);
               var1.accept(Items.BLUE_GLAZED_TERRACOTTA);
               var1.accept(Items.PURPLE_GLAZED_TERRACOTTA);
               var1.accept(Items.MAGENTA_GLAZED_TERRACOTTA);
               var1.accept(Items.PINK_GLAZED_TERRACOTTA);
               var1.accept(Items.GLASS);
               var1.accept(Items.TINTED_GLASS);
               var1.accept(Items.WHITE_STAINED_GLASS);
               var1.accept(Items.LIGHT_GRAY_STAINED_GLASS);
               var1.accept(Items.GRAY_STAINED_GLASS);
               var1.accept(Items.BLACK_STAINED_GLASS);
               var1.accept(Items.BROWN_STAINED_GLASS);
               var1.accept(Items.RED_STAINED_GLASS);
               var1.accept(Items.ORANGE_STAINED_GLASS);
               var1.accept(Items.YELLOW_STAINED_GLASS);
               var1.accept(Items.LIME_STAINED_GLASS);
               var1.accept(Items.GREEN_STAINED_GLASS);
               var1.accept(Items.CYAN_STAINED_GLASS);
               var1.accept(Items.LIGHT_BLUE_STAINED_GLASS);
               var1.accept(Items.BLUE_STAINED_GLASS);
               var1.accept(Items.PURPLE_STAINED_GLASS);
               var1.accept(Items.MAGENTA_STAINED_GLASS);
               var1.accept(Items.PINK_STAINED_GLASS);
               var1.accept(Items.GLASS_PANE);
               var1.accept(Items.WHITE_STAINED_GLASS_PANE);
               var1.accept(Items.LIGHT_GRAY_STAINED_GLASS_PANE);
               var1.accept(Items.GRAY_STAINED_GLASS_PANE);
               var1.accept(Items.BLACK_STAINED_GLASS_PANE);
               var1.accept(Items.BROWN_STAINED_GLASS_PANE);
               var1.accept(Items.RED_STAINED_GLASS_PANE);
               var1.accept(Items.ORANGE_STAINED_GLASS_PANE);
               var1.accept(Items.YELLOW_STAINED_GLASS_PANE);
               var1.accept(Items.LIME_STAINED_GLASS_PANE);
               var1.accept(Items.GREEN_STAINED_GLASS_PANE);
               var1.accept(Items.CYAN_STAINED_GLASS_PANE);
               var1.accept(Items.LIGHT_BLUE_STAINED_GLASS_PANE);
               var1.accept(Items.BLUE_STAINED_GLASS_PANE);
               var1.accept(Items.PURPLE_STAINED_GLASS_PANE);
               var1.accept(Items.MAGENTA_STAINED_GLASS_PANE);
               var1.accept(Items.PINK_STAINED_GLASS_PANE);
               var1.accept(Items.SHULKER_BOX);
               var1.accept(Items.WHITE_SHULKER_BOX);
               var1.accept(Items.LIGHT_GRAY_SHULKER_BOX);
               var1.accept(Items.GRAY_SHULKER_BOX);
               var1.accept(Items.BLACK_SHULKER_BOX);
               var1.accept(Items.BROWN_SHULKER_BOX);
               var1.accept(Items.RED_SHULKER_BOX);
               var1.accept(Items.ORANGE_SHULKER_BOX);
               var1.accept(Items.YELLOW_SHULKER_BOX);
               var1.accept(Items.LIME_SHULKER_BOX);
               var1.accept(Items.GREEN_SHULKER_BOX);
               var1.accept(Items.CYAN_SHULKER_BOX);
               var1.accept(Items.LIGHT_BLUE_SHULKER_BOX);
               var1.accept(Items.BLUE_SHULKER_BOX);
               var1.accept(Items.PURPLE_SHULKER_BOX);
               var1.accept(Items.MAGENTA_SHULKER_BOX);
               var1.accept(Items.PINK_SHULKER_BOX);
               var1.accept(Items.WHITE_BED);
               var1.accept(Items.LIGHT_GRAY_BED);
               var1.accept(Items.GRAY_BED);
               var1.accept(Items.BLACK_BED);
               var1.accept(Items.BROWN_BED);
               var1.accept(Items.RED_BED);
               var1.accept(Items.ORANGE_BED);
               var1.accept(Items.YELLOW_BED);
               var1.accept(Items.LIME_BED);
               var1.accept(Items.GREEN_BED);
               var1.accept(Items.CYAN_BED);
               var1.accept(Items.LIGHT_BLUE_BED);
               var1.accept(Items.BLUE_BED);
               var1.accept(Items.PURPLE_BED);
               var1.accept(Items.MAGENTA_BED);
               var1.accept(Items.PINK_BED);
               var1.accept(Items.CANDLE);
               var1.accept(Items.WHITE_CANDLE);
               var1.accept(Items.LIGHT_GRAY_CANDLE);
               var1.accept(Items.GRAY_CANDLE);
               var1.accept(Items.BLACK_CANDLE);
               var1.accept(Items.BROWN_CANDLE);
               var1.accept(Items.RED_CANDLE);
               var1.accept(Items.ORANGE_CANDLE);
               var1.accept(Items.YELLOW_CANDLE);
               var1.accept(Items.LIME_CANDLE);
               var1.accept(Items.GREEN_CANDLE);
               var1.accept(Items.CYAN_CANDLE);
               var1.accept(Items.LIGHT_BLUE_CANDLE);
               var1.accept(Items.BLUE_CANDLE);
               var1.accept(Items.PURPLE_CANDLE);
               var1.accept(Items.MAGENTA_CANDLE);
               var1.accept(Items.PINK_CANDLE);
               var1.accept(Items.WHITE_BANNER);
               var1.accept(Items.LIGHT_GRAY_BANNER);
               var1.accept(Items.GRAY_BANNER);
               var1.accept(Items.BLACK_BANNER);
               var1.accept(Items.BROWN_BANNER);
               var1.accept(Items.RED_BANNER);
               var1.accept(Items.ORANGE_BANNER);
               var1.accept(Items.YELLOW_BANNER);
               var1.accept(Items.LIME_BANNER);
               var1.accept(Items.GREEN_BANNER);
               var1.accept(Items.CYAN_BANNER);
               var1.accept(Items.LIGHT_BLUE_BANNER);
               var1.accept(Items.BLUE_BANNER);
               var1.accept(Items.PURPLE_BANNER);
               var1.accept(Items.MAGENTA_BANNER);
               var1.accept(Items.PINK_BANNER);
            })
            .build()
      );
      Registry.register(
         var0,
         NATURAL_BLOCKS,
         CreativeModeTab.builder(CreativeModeTab.Row.TOP, 2)
            .title(Component.translatable("itemGroup.natural"))
            .icon(() -> new ItemStack(Blocks.GRASS_BLOCK))
            .displayItems((var0x, var1) -> {
               var1.accept(Items.GRASS_BLOCK);
               var1.accept(Items.PODZOL);
               var1.accept(Items.MYCELIUM);
               var1.accept(Items.DIRT_PATH);
               var1.accept(Items.DIRT);
               var1.accept(Items.COARSE_DIRT);
               var1.accept(Items.ROOTED_DIRT);
               var1.accept(Items.FARMLAND);
               var1.accept(Items.MUD);
               var1.accept(Items.CLAY);
               var1.accept(Items.GRAVEL);
               var1.accept(Items.SAND);
               var1.accept(Items.SANDSTONE);
               var1.accept(Items.RED_SAND);
               var1.accept(Items.RED_SANDSTONE);
               var1.accept(Items.ICE);
               var1.accept(Items.PACKED_ICE);
               var1.accept(Items.BLUE_ICE);
               var1.accept(Items.SNOW_BLOCK);
               var1.accept(Items.SNOW);
               var1.accept(Items.MOSS_BLOCK);
               var1.accept(Items.MOSS_CARPET);
               var1.accept(Items.STONE);
               var1.accept(Items.DEEPSLATE);
               var1.accept(Items.GRANITE);
               var1.accept(Items.DIORITE);
               var1.accept(Items.ANDESITE);
               var1.accept(Items.CALCITE);
               var1.accept(Items.TUFF);
               var1.accept(Items.DRIPSTONE_BLOCK);
               var1.accept(Items.POINTED_DRIPSTONE);
               var1.accept(Items.PRISMARINE);
               var1.accept(Items.MAGMA_BLOCK);
               var1.accept(Items.OBSIDIAN);
               var1.accept(Items.CRYING_OBSIDIAN);
               var1.accept(Items.NETHERRACK);
               var1.accept(Items.CRIMSON_NYLIUM);
               var1.accept(Items.WARPED_NYLIUM);
               var1.accept(Items.SOUL_SAND);
               var1.accept(Items.SOUL_SOIL);
               var1.accept(Items.BONE_BLOCK);
               var1.accept(Items.BLACKSTONE);
               var1.accept(Items.BASALT);
               var1.accept(Items.SMOOTH_BASALT);
               var1.accept(Items.END_STONE);
               var1.accept(Items.COAL_ORE);
               var1.accept(Items.DEEPSLATE_COAL_ORE);
               var1.accept(Items.IRON_ORE);
               var1.accept(Items.DEEPSLATE_IRON_ORE);
               var1.accept(Items.COPPER_ORE);
               var1.accept(Items.DEEPSLATE_COPPER_ORE);
               var1.accept(Items.GOLD_ORE);
               var1.accept(Items.DEEPSLATE_GOLD_ORE);
               var1.accept(Items.REDSTONE_ORE);
               var1.accept(Items.DEEPSLATE_REDSTONE_ORE);
               var1.accept(Items.EMERALD_ORE);
               var1.accept(Items.DEEPSLATE_EMERALD_ORE);
               var1.accept(Items.LAPIS_ORE);
               var1.accept(Items.DEEPSLATE_LAPIS_ORE);
               var1.accept(Items.DIAMOND_ORE);
               var1.accept(Items.DEEPSLATE_DIAMOND_ORE);
               var1.accept(Items.NETHER_GOLD_ORE);
               var1.accept(Items.NETHER_QUARTZ_ORE);
               var1.accept(Items.ANCIENT_DEBRIS);
               var1.accept(Items.RAW_IRON_BLOCK);
               var1.accept(Items.RAW_COPPER_BLOCK);
               var1.accept(Items.RAW_GOLD_BLOCK);
               var1.accept(Items.GLOWSTONE);
               var1.accept(Items.AMETHYST_BLOCK);
               var1.accept(Items.BUDDING_AMETHYST);
               var1.accept(Items.SMALL_AMETHYST_BUD);
               var1.accept(Items.MEDIUM_AMETHYST_BUD);
               var1.accept(Items.LARGE_AMETHYST_BUD);
               var1.accept(Items.AMETHYST_CLUSTER);
               var1.accept(Items.OAK_LOG);
               var1.accept(Items.SPRUCE_LOG);
               var1.accept(Items.BIRCH_LOG);
               var1.accept(Items.JUNGLE_LOG);
               var1.accept(Items.ACACIA_LOG);
               var1.accept(Items.DARK_OAK_LOG);
               var1.accept(Items.MANGROVE_LOG);
               var1.accept(Items.MANGROVE_ROOTS);
               var1.accept(Items.MUDDY_MANGROVE_ROOTS);
               var1.accept(Items.CHERRY_LOG);
               var1.accept(Items.MUSHROOM_STEM);
               var1.accept(Items.CRIMSON_STEM);
               var1.accept(Items.WARPED_STEM);
               var1.accept(Items.OAK_LEAVES);
               var1.accept(Items.SPRUCE_LEAVES);
               var1.accept(Items.BIRCH_LEAVES);
               var1.accept(Items.JUNGLE_LEAVES);
               var1.accept(Items.ACACIA_LEAVES);
               var1.accept(Items.DARK_OAK_LEAVES);
               var1.accept(Items.MANGROVE_LEAVES);
               var1.accept(Items.CHERRY_LEAVES);
               var1.accept(Items.AZALEA_LEAVES);
               var1.accept(Items.FLOWERING_AZALEA_LEAVES);
               var1.accept(Items.BROWN_MUSHROOM_BLOCK);
               var1.accept(Items.RED_MUSHROOM_BLOCK);
               var1.accept(Items.NETHER_WART_BLOCK);
               var1.accept(Items.WARPED_WART_BLOCK);
               var1.accept(Items.SHROOMLIGHT);
               var1.accept(Items.OAK_SAPLING);
               var1.accept(Items.SPRUCE_SAPLING);
               var1.accept(Items.BIRCH_SAPLING);
               var1.accept(Items.JUNGLE_SAPLING);
               var1.accept(Items.ACACIA_SAPLING);
               var1.accept(Items.DARK_OAK_SAPLING);
               var1.accept(Items.MANGROVE_PROPAGULE);
               var1.accept(Items.CHERRY_SAPLING);
               var1.accept(Items.AZALEA);
               var1.accept(Items.FLOWERING_AZALEA);
               var1.accept(Items.BROWN_MUSHROOM);
               var1.accept(Items.RED_MUSHROOM);
               var1.accept(Items.CRIMSON_FUNGUS);
               var1.accept(Items.WARPED_FUNGUS);
               var1.accept(Items.SHORT_GRASS);
               var1.accept(Items.FERN);
               var1.accept(Items.DEAD_BUSH);
               var1.accept(Items.DANDELION);
               var1.accept(Items.POPPY);
               var1.accept(Items.BLUE_ORCHID);
               var1.accept(Items.ALLIUM);
               var1.accept(Items.AZURE_BLUET);
               var1.accept(Items.RED_TULIP);
               var1.accept(Items.ORANGE_TULIP);
               var1.accept(Items.WHITE_TULIP);
               var1.accept(Items.PINK_TULIP);
               var1.accept(Items.OXEYE_DAISY);
               var1.accept(Items.CORNFLOWER);
               var1.accept(Items.LILY_OF_THE_VALLEY);
               var1.accept(Items.TORCHFLOWER);
               var1.accept(Items.WITHER_ROSE);
               var1.accept(Items.PINK_PETALS);
               var1.accept(Items.SPORE_BLOSSOM);
               var1.accept(Items.BAMBOO);
               var1.accept(Items.SUGAR_CANE);
               var1.accept(Items.CACTUS);
               var1.accept(Items.CRIMSON_ROOTS);
               var1.accept(Items.WARPED_ROOTS);
               var1.accept(Items.NETHER_SPROUTS);
               var1.accept(Items.WEEPING_VINES);
               var1.accept(Items.TWISTING_VINES);
               var1.accept(Items.VINE);
               var1.accept(Items.TALL_GRASS);
               var1.accept(Items.LARGE_FERN);
               var1.accept(Items.SUNFLOWER);
               var1.accept(Items.LILAC);
               var1.accept(Items.ROSE_BUSH);
               var1.accept(Items.PEONY);
               var1.accept(Items.PITCHER_PLANT);
               var1.accept(Items.BIG_DRIPLEAF);
               var1.accept(Items.SMALL_DRIPLEAF);
               var1.accept(Items.CHORUS_PLANT);
               var1.accept(Items.CHORUS_FLOWER);
               var1.accept(Items.GLOW_LICHEN);
               var1.accept(Items.HANGING_ROOTS);
               var1.accept(Items.FROGSPAWN);
               var1.accept(Items.TURTLE_EGG);
               var1.accept(Items.SNIFFER_EGG);
               var1.accept(Items.WHEAT_SEEDS);
               var1.accept(Items.COCOA_BEANS);
               var1.accept(Items.PUMPKIN_SEEDS);
               var1.accept(Items.MELON_SEEDS);
               var1.accept(Items.BEETROOT_SEEDS);
               var1.accept(Items.TORCHFLOWER_SEEDS);
               var1.accept(Items.PITCHER_POD);
               var1.accept(Items.GLOW_BERRIES);
               var1.accept(Items.SWEET_BERRIES);
               var1.accept(Items.NETHER_WART);
               var1.accept(Items.LILY_PAD);
               var1.accept(Items.SEAGRASS);
               var1.accept(Items.SEA_PICKLE);
               var1.accept(Items.KELP);
               var1.accept(Items.DRIED_KELP_BLOCK);
               var1.accept(Items.TUBE_CORAL_BLOCK);
               var1.accept(Items.BRAIN_CORAL_BLOCK);
               var1.accept(Items.BUBBLE_CORAL_BLOCK);
               var1.accept(Items.FIRE_CORAL_BLOCK);
               var1.accept(Items.HORN_CORAL_BLOCK);
               var1.accept(Items.DEAD_TUBE_CORAL_BLOCK);
               var1.accept(Items.DEAD_BRAIN_CORAL_BLOCK);
               var1.accept(Items.DEAD_BUBBLE_CORAL_BLOCK);
               var1.accept(Items.DEAD_FIRE_CORAL_BLOCK);
               var1.accept(Items.DEAD_HORN_CORAL_BLOCK);
               var1.accept(Items.TUBE_CORAL);
               var1.accept(Items.BRAIN_CORAL);
               var1.accept(Items.BUBBLE_CORAL);
               var1.accept(Items.FIRE_CORAL);
               var1.accept(Items.HORN_CORAL);
               var1.accept(Items.DEAD_TUBE_CORAL);
               var1.accept(Items.DEAD_BRAIN_CORAL);
               var1.accept(Items.DEAD_BUBBLE_CORAL);
               var1.accept(Items.DEAD_FIRE_CORAL);
               var1.accept(Items.DEAD_HORN_CORAL);
               var1.accept(Items.TUBE_CORAL_FAN);
               var1.accept(Items.BRAIN_CORAL_FAN);
               var1.accept(Items.BUBBLE_CORAL_FAN);
               var1.accept(Items.FIRE_CORAL_FAN);
               var1.accept(Items.HORN_CORAL_FAN);
               var1.accept(Items.DEAD_TUBE_CORAL_FAN);
               var1.accept(Items.DEAD_BRAIN_CORAL_FAN);
               var1.accept(Items.DEAD_BUBBLE_CORAL_FAN);
               var1.accept(Items.DEAD_FIRE_CORAL_FAN);
               var1.accept(Items.DEAD_HORN_CORAL_FAN);
               var1.accept(Items.SPONGE);
               var1.accept(Items.WET_SPONGE);
               var1.accept(Items.MELON);
               var1.accept(Items.PUMPKIN);
               var1.accept(Items.CARVED_PUMPKIN);
               var1.accept(Items.JACK_O_LANTERN);
               var1.accept(Items.HAY_BLOCK);
               var1.accept(Items.BEE_NEST);
               var1.accept(Items.HONEYCOMB_BLOCK);
               var1.accept(Items.SLIME_BLOCK);
               var1.accept(Items.HONEY_BLOCK);
               var1.accept(Items.OCHRE_FROGLIGHT);
               var1.accept(Items.VERDANT_FROGLIGHT);
               var1.accept(Items.PEARLESCENT_FROGLIGHT);
               var1.accept(Items.SCULK);
               var1.accept(Items.SCULK_VEIN);
               var1.accept(Items.SCULK_CATALYST);
               var1.accept(Items.SCULK_SHRIEKER);
               var1.accept(Items.SCULK_SENSOR);
               var1.accept(Items.COBWEB);
               var1.accept(Items.BEDROCK);
            })
            .build()
      );
      Registry.register(
         var0,
         FUNCTIONAL_BLOCKS,
         CreativeModeTab.builder(CreativeModeTab.Row.TOP, 3)
            .title(Component.translatable("itemGroup.functional"))
            .icon(() -> new ItemStack(Items.OAK_SIGN))
            .displayItems(
               (var0x, var1) -> {
                  var1.accept(Items.TORCH);
                  var1.accept(Items.SOUL_TORCH);
                  var1.accept(Items.REDSTONE_TORCH);
                  var1.accept(Items.LANTERN);
                  var1.accept(Items.SOUL_LANTERN);
                  var1.accept(Items.CHAIN);
                  var1.accept(Items.END_ROD);
                  var1.accept(Items.SEA_LANTERN);
                  var1.accept(Items.REDSTONE_LAMP);
                  var1.accept(Items.WAXED_COPPER_BULB);
                  var1.accept(Items.WAXED_EXPOSED_COPPER_BULB);
                  var1.accept(Items.WAXED_WEATHERED_COPPER_BULB);
                  var1.accept(Items.WAXED_OXIDIZED_COPPER_BULB);
                  var1.accept(Items.GLOWSTONE);
                  var1.accept(Items.SHROOMLIGHT);
                  var1.accept(Items.OCHRE_FROGLIGHT);
                  var1.accept(Items.VERDANT_FROGLIGHT);
                  var1.accept(Items.PEARLESCENT_FROGLIGHT);
                  var1.accept(Items.CRYING_OBSIDIAN);
                  var1.accept(Items.GLOW_LICHEN);
                  var1.accept(Items.MAGMA_BLOCK);
                  var1.accept(Items.CRAFTING_TABLE);
                  var1.accept(Items.STONECUTTER);
                  var1.accept(Items.CARTOGRAPHY_TABLE);
                  var1.accept(Items.FLETCHING_TABLE);
                  var1.accept(Items.SMITHING_TABLE);
                  var1.accept(Items.GRINDSTONE);
                  var1.accept(Items.LOOM);
                  var1.accept(Items.FURNACE);
                  var1.accept(Items.SMOKER);
                  var1.accept(Items.BLAST_FURNACE);
                  var1.accept(Items.CAMPFIRE);
                  var1.accept(Items.SOUL_CAMPFIRE);
                  var1.accept(Items.ANVIL);
                  var1.accept(Items.CHIPPED_ANVIL);
                  var1.accept(Items.DAMAGED_ANVIL);
                  var1.accept(Items.COMPOSTER);
                  var1.accept(Items.NOTE_BLOCK);
                  var1.accept(Items.JUKEBOX);
                  var1.accept(Items.ENCHANTING_TABLE);
                  var1.accept(Items.END_CRYSTAL);
                  var1.accept(Items.BREWING_STAND);
                  var1.accept(Items.CAULDRON);
                  var1.accept(Items.BELL);
                  var1.accept(Items.BEACON);
                  var1.accept(Items.CONDUIT);
                  var1.accept(Items.LODESTONE);
                  var1.accept(Items.LADDER);
                  var1.accept(Items.SCAFFOLDING);
                  var1.accept(Items.BEE_NEST);
                  var1.accept(Items.BEEHIVE);
                  var1.accept(Items.SUSPICIOUS_SAND);
                  var1.accept(Items.SUSPICIOUS_GRAVEL);
                  var1.accept(Items.LIGHTNING_ROD);
                  var1.accept(Items.FLOWER_POT);
                  var1.accept(Items.DECORATED_POT);
                  var1.accept(Items.ARMOR_STAND);
                  var1.accept(Items.ITEM_FRAME);
                  var1.accept(Items.GLOW_ITEM_FRAME);
                  var1.accept(Items.PAINTING);
                  var0x.holders()
                     .lookup(Registries.PAINTING_VARIANT)
                     .ifPresent(
                        var1x -> generatePresetPaintings(
                              var1, var1x, var0xxx -> var0xxx.is(PaintingVariantTags.PLACEABLE), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
                           )
                     );
                  var1.accept(Items.BOOKSHELF);
                  var1.accept(Items.CHISELED_BOOKSHELF);
                  var1.accept(Items.LECTERN);
                  var1.accept(Items.TINTED_GLASS);
                  var1.accept(Items.OAK_SIGN);
                  var1.accept(Items.OAK_HANGING_SIGN);
                  var1.accept(Items.SPRUCE_SIGN);
                  var1.accept(Items.SPRUCE_HANGING_SIGN);
                  var1.accept(Items.BIRCH_SIGN);
                  var1.accept(Items.BIRCH_HANGING_SIGN);
                  var1.accept(Items.JUNGLE_SIGN);
                  var1.accept(Items.JUNGLE_HANGING_SIGN);
                  var1.accept(Items.ACACIA_SIGN);
                  var1.accept(Items.ACACIA_HANGING_SIGN);
                  var1.accept(Items.DARK_OAK_SIGN);
                  var1.accept(Items.DARK_OAK_HANGING_SIGN);
                  var1.accept(Items.MANGROVE_SIGN);
                  var1.accept(Items.MANGROVE_HANGING_SIGN);
                  var1.accept(Items.CHERRY_SIGN);
                  var1.accept(Items.CHERRY_HANGING_SIGN);
                  var1.accept(Items.BAMBOO_SIGN);
                  var1.accept(Items.BAMBOO_HANGING_SIGN);
                  var1.accept(Items.CRIMSON_SIGN);
                  var1.accept(Items.CRIMSON_HANGING_SIGN);
                  var1.accept(Items.WARPED_SIGN);
                  var1.accept(Items.WARPED_HANGING_SIGN);
                  var1.accept(Items.CHEST);
                  var1.accept(Items.BARREL);
                  var1.accept(Items.ENDER_CHEST);
                  var1.accept(Items.SHULKER_BOX);
                  var1.accept(Items.WHITE_SHULKER_BOX);
                  var1.accept(Items.LIGHT_GRAY_SHULKER_BOX);
                  var1.accept(Items.GRAY_SHULKER_BOX);
                  var1.accept(Items.BLACK_SHULKER_BOX);
                  var1.accept(Items.BROWN_SHULKER_BOX);
                  var1.accept(Items.RED_SHULKER_BOX);
                  var1.accept(Items.ORANGE_SHULKER_BOX);
                  var1.accept(Items.YELLOW_SHULKER_BOX);
                  var1.accept(Items.LIME_SHULKER_BOX);
                  var1.accept(Items.GREEN_SHULKER_BOX);
                  var1.accept(Items.CYAN_SHULKER_BOX);
                  var1.accept(Items.LIGHT_BLUE_SHULKER_BOX);
                  var1.accept(Items.BLUE_SHULKER_BOX);
                  var1.accept(Items.PURPLE_SHULKER_BOX);
                  var1.accept(Items.MAGENTA_SHULKER_BOX);
                  var1.accept(Items.PINK_SHULKER_BOX);
                  var1.accept(Items.RESPAWN_ANCHOR);
                  var1.accept(Items.WHITE_BED);
                  var1.accept(Items.LIGHT_GRAY_BED);
                  var1.accept(Items.GRAY_BED);
                  var1.accept(Items.BLACK_BED);
                  var1.accept(Items.BROWN_BED);
                  var1.accept(Items.RED_BED);
                  var1.accept(Items.ORANGE_BED);
                  var1.accept(Items.YELLOW_BED);
                  var1.accept(Items.LIME_BED);
                  var1.accept(Items.GREEN_BED);
                  var1.accept(Items.CYAN_BED);
                  var1.accept(Items.LIGHT_BLUE_BED);
                  var1.accept(Items.BLUE_BED);
                  var1.accept(Items.PURPLE_BED);
                  var1.accept(Items.MAGENTA_BED);
                  var1.accept(Items.PINK_BED);
                  var1.accept(Items.CANDLE);
                  var1.accept(Items.WHITE_CANDLE);
                  var1.accept(Items.LIGHT_GRAY_CANDLE);
                  var1.accept(Items.GRAY_CANDLE);
                  var1.accept(Items.BLACK_CANDLE);
                  var1.accept(Items.BROWN_CANDLE);
                  var1.accept(Items.RED_CANDLE);
                  var1.accept(Items.ORANGE_CANDLE);
                  var1.accept(Items.YELLOW_CANDLE);
                  var1.accept(Items.LIME_CANDLE);
                  var1.accept(Items.GREEN_CANDLE);
                  var1.accept(Items.CYAN_CANDLE);
                  var1.accept(Items.LIGHT_BLUE_CANDLE);
                  var1.accept(Items.BLUE_CANDLE);
                  var1.accept(Items.PURPLE_CANDLE);
                  var1.accept(Items.MAGENTA_CANDLE);
                  var1.accept(Items.PINK_CANDLE);
                  var1.accept(Items.WHITE_BANNER);
                  var1.accept(Items.LIGHT_GRAY_BANNER);
                  var1.accept(Items.GRAY_BANNER);
                  var1.accept(Items.BLACK_BANNER);
                  var1.accept(Items.BROWN_BANNER);
                  var1.accept(Items.RED_BANNER);
                  var1.accept(Items.ORANGE_BANNER);
                  var1.accept(Items.YELLOW_BANNER);
                  var1.accept(Items.LIME_BANNER);
                  var1.accept(Items.GREEN_BANNER);
                  var1.accept(Items.CYAN_BANNER);
                  var1.accept(Items.LIGHT_BLUE_BANNER);
                  var1.accept(Items.BLUE_BANNER);
                  var1.accept(Items.PURPLE_BANNER);
                  var1.accept(Items.MAGENTA_BANNER);
                  var1.accept(Items.PINK_BANNER);
                  var1.accept(Raid.getLeaderBannerInstance(var0x.holders().lookupOrThrow(Registries.BANNER_PATTERN)));
                  var1.accept(Items.SKELETON_SKULL);
                  var1.accept(Items.WITHER_SKELETON_SKULL);
                  var1.accept(Items.PLAYER_HEAD);
                  var1.accept(Items.ZOMBIE_HEAD);
                  var1.accept(Items.CREEPER_HEAD);
                  var1.accept(Items.PIGLIN_HEAD);
                  var1.accept(Items.DRAGON_HEAD);
                  var1.accept(Items.DRAGON_EGG);
                  var1.accept(Items.END_PORTAL_FRAME);
                  var1.accept(Items.ENDER_EYE);
                  var1.accept(Items.VAULT);
                  var1.accept(Items.INFESTED_STONE);
                  var1.accept(Items.INFESTED_COBBLESTONE);
                  var1.accept(Items.INFESTED_STONE_BRICKS);
                  var1.accept(Items.INFESTED_MOSSY_STONE_BRICKS);
                  var1.accept(Items.INFESTED_CRACKED_STONE_BRICKS);
                  var1.accept(Items.INFESTED_CHISELED_STONE_BRICKS);
                  var1.accept(Items.INFESTED_DEEPSLATE);
               }
            )
            .build()
      );
      Registry.register(
         var0,
         REDSTONE_BLOCKS,
         CreativeModeTab.builder(CreativeModeTab.Row.TOP, 4)
            .title(Component.translatable("itemGroup.redstone"))
            .icon(() -> new ItemStack(Items.REDSTONE))
            .displayItems((var0x, var1) -> {
               var1.accept(Items.REDSTONE);
               var1.accept(Items.REDSTONE_TORCH);
               var1.accept(Items.REDSTONE_BLOCK);
               var1.accept(Items.REPEATER);
               var1.accept(Items.COMPARATOR);
               var1.accept(Items.TARGET);
               var1.accept(Items.WAXED_COPPER_BULB);
               var1.accept(Items.WAXED_EXPOSED_COPPER_BULB);
               var1.accept(Items.WAXED_WEATHERED_COPPER_BULB);
               var1.accept(Items.WAXED_OXIDIZED_COPPER_BULB);
               var1.accept(Items.LEVER);
               var1.accept(Items.OAK_BUTTON);
               var1.accept(Items.STONE_BUTTON);
               var1.accept(Items.OAK_PRESSURE_PLATE);
               var1.accept(Items.STONE_PRESSURE_PLATE);
               var1.accept(Items.LIGHT_WEIGHTED_PRESSURE_PLATE);
               var1.accept(Items.HEAVY_WEIGHTED_PRESSURE_PLATE);
               var1.accept(Items.SCULK_SENSOR);
               var1.accept(Items.CALIBRATED_SCULK_SENSOR);
               var1.accept(Items.SCULK_SHRIEKER);
               var1.accept(Items.AMETHYST_BLOCK);
               var1.accept(Items.WHITE_WOOL);
               var1.accept(Items.TRIPWIRE_HOOK);
               var1.accept(Items.STRING);
               var1.accept(Items.LECTERN);
               var1.accept(Items.DAYLIGHT_DETECTOR);
               var1.accept(Items.LIGHTNING_ROD);
               var1.accept(Items.PISTON);
               var1.accept(Items.STICKY_PISTON);
               var1.accept(Items.SLIME_BLOCK);
               var1.accept(Items.HONEY_BLOCK);
               var1.accept(Items.DISPENSER);
               var1.accept(Items.DROPPER);
               var1.accept(Items.CRAFTER);
               var1.accept(Items.HOPPER);
               var1.accept(Items.CHEST);
               var1.accept(Items.BARREL);
               var1.accept(Items.CHISELED_BOOKSHELF);
               var1.accept(Items.FURNACE);
               var1.accept(Items.TRAPPED_CHEST);
               var1.accept(Items.JUKEBOX);
               var1.accept(Items.DECORATED_POT);
               var1.accept(Items.OBSERVER);
               var1.accept(Items.NOTE_BLOCK);
               var1.accept(Items.COMPOSTER);
               var1.accept(Items.CAULDRON);
               var1.accept(Items.RAIL);
               var1.accept(Items.POWERED_RAIL);
               var1.accept(Items.DETECTOR_RAIL);
               var1.accept(Items.ACTIVATOR_RAIL);
               var1.accept(Items.MINECART);
               var1.accept(Items.HOPPER_MINECART);
               var1.accept(Items.CHEST_MINECART);
               var1.accept(Items.FURNACE_MINECART);
               var1.accept(Items.TNT_MINECART);
               var1.accept(Items.OAK_CHEST_BOAT);
               var1.accept(Items.BAMBOO_CHEST_RAFT);
               var1.accept(Items.OAK_DOOR);
               var1.accept(Items.IRON_DOOR);
               var1.accept(Items.OAK_FENCE_GATE);
               var1.accept(Items.OAK_TRAPDOOR);
               var1.accept(Items.IRON_TRAPDOOR);
               var1.accept(Items.TNT);
               var1.accept(Items.REDSTONE_LAMP);
               var1.accept(Items.BELL);
               var1.accept(Items.BIG_DRIPLEAF);
               var1.accept(Items.ARMOR_STAND);
               var1.accept(Items.REDSTONE_ORE);
            })
            .build()
      );
      Registry.register(
         var0,
         HOTBAR,
         CreativeModeTab.builder(CreativeModeTab.Row.TOP, 5)
            .title(Component.translatable("itemGroup.hotbar"))
            .icon(() -> new ItemStack(Blocks.BOOKSHELF))
            .alignedRight()
            .type(CreativeModeTab.Type.HOTBAR)
            .build()
      );
      Registry.register(
         var0,
         SEARCH,
         CreativeModeTab.builder(CreativeModeTab.Row.TOP, 6)
            .title(Component.translatable("itemGroup.search"))
            .icon(() -> new ItemStack(Items.COMPASS))
            .displayItems((var1, var2) -> {
               Set var3 = ItemStackLinkedSet.createTypeAndComponentsSet();
      
               for(CreativeModeTab var5 : var0) {
                  if (var5.getType() != CreativeModeTab.Type.SEARCH) {
                     var3.addAll(var5.getSearchTabDisplayItems());
                  }
               }
      
               var2.acceptAll(var3);
            })
            .backgroundSuffix("item_search.png")
            .alignedRight()
            .type(CreativeModeTab.Type.SEARCH)
            .build()
      );
      Registry.register(
         var0,
         TOOLS_AND_UTILITIES,
         CreativeModeTab.builder(CreativeModeTab.Row.BOTTOM, 0)
            .title(Component.translatable("itemGroup.tools"))
            .icon(() -> new ItemStack(Items.DIAMOND_PICKAXE))
            .displayItems(
               (var0x, var1) -> {
                  var1.accept(Items.WOODEN_SHOVEL);
                  var1.accept(Items.WOODEN_PICKAXE);
                  var1.accept(Items.WOODEN_AXE);
                  var1.accept(Items.WOODEN_HOE);
                  var1.accept(Items.STONE_SHOVEL);
                  var1.accept(Items.STONE_PICKAXE);
                  var1.accept(Items.STONE_AXE);
                  var1.accept(Items.STONE_HOE);
                  var1.accept(Items.IRON_SHOVEL);
                  var1.accept(Items.IRON_PICKAXE);
                  var1.accept(Items.IRON_AXE);
                  var1.accept(Items.IRON_HOE);
                  var1.accept(Items.GOLDEN_SHOVEL);
                  var1.accept(Items.GOLDEN_PICKAXE);
                  var1.accept(Items.GOLDEN_AXE);
                  var1.accept(Items.GOLDEN_HOE);
                  var1.accept(Items.DIAMOND_SHOVEL);
                  var1.accept(Items.DIAMOND_PICKAXE);
                  var1.accept(Items.DIAMOND_AXE);
                  var1.accept(Items.DIAMOND_HOE);
                  var1.accept(Items.NETHERITE_SHOVEL);
                  var1.accept(Items.NETHERITE_PICKAXE);
                  var1.accept(Items.NETHERITE_AXE);
                  var1.accept(Items.NETHERITE_HOE);
                  var1.accept(Items.BUCKET);
                  var1.accept(Items.WATER_BUCKET);
                  var1.accept(Items.COD_BUCKET);
                  var1.accept(Items.SALMON_BUCKET);
                  var1.accept(Items.TROPICAL_FISH_BUCKET);
                  var1.accept(Items.PUFFERFISH_BUCKET);
                  var1.accept(Items.AXOLOTL_BUCKET);
                  var1.accept(Items.TADPOLE_BUCKET);
                  var1.accept(Items.LAVA_BUCKET);
                  var1.accept(Items.POWDER_SNOW_BUCKET);
                  var1.accept(Items.MILK_BUCKET);
                  var1.accept(Items.FISHING_ROD);
                  var1.accept(Items.FLINT_AND_STEEL);
                  var1.accept(Items.FIRE_CHARGE);
                  var1.accept(Items.BONE_MEAL);
                  var1.accept(Items.SHEARS);
                  var1.accept(Items.BRUSH);
                  var1.accept(Items.NAME_TAG);
                  var1.accept(Items.LEAD);
                  if (var0x.enabledFeatures().contains(FeatureFlags.BUNDLE)) {
                     var1.accept(Items.BUNDLE);
                  }
         
                  var1.accept(Items.COMPASS);
                  var1.accept(Items.RECOVERY_COMPASS);
                  var1.accept(Items.CLOCK);
                  var1.accept(Items.SPYGLASS);
                  var1.accept(Items.MAP);
                  var1.accept(Items.WRITABLE_BOOK);
                  var1.accept(Items.WIND_CHARGE);
                  var1.accept(Items.ENDER_PEARL);
                  var1.accept(Items.ENDER_EYE);
                  var1.accept(Items.ELYTRA);
                  generateFireworksAllDurations(var1, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                  var1.accept(Items.SADDLE);
                  var1.accept(Items.CARROT_ON_A_STICK);
                  var1.accept(Items.WARPED_FUNGUS_ON_A_STICK);
                  var1.accept(Items.OAK_BOAT);
                  var1.accept(Items.OAK_CHEST_BOAT);
                  var1.accept(Items.SPRUCE_BOAT);
                  var1.accept(Items.SPRUCE_CHEST_BOAT);
                  var1.accept(Items.BIRCH_BOAT);
                  var1.accept(Items.BIRCH_CHEST_BOAT);
                  var1.accept(Items.JUNGLE_BOAT);
                  var1.accept(Items.JUNGLE_CHEST_BOAT);
                  var1.accept(Items.ACACIA_BOAT);
                  var1.accept(Items.ACACIA_CHEST_BOAT);
                  var1.accept(Items.DARK_OAK_BOAT);
                  var1.accept(Items.DARK_OAK_CHEST_BOAT);
                  var1.accept(Items.MANGROVE_BOAT);
                  var1.accept(Items.MANGROVE_CHEST_BOAT);
                  var1.accept(Items.CHERRY_BOAT);
                  var1.accept(Items.CHERRY_CHEST_BOAT);
                  var1.accept(Items.BAMBOO_RAFT);
                  var1.accept(Items.BAMBOO_CHEST_RAFT);
                  var1.accept(Items.RAIL);
                  var1.accept(Items.POWERED_RAIL);
                  var1.accept(Items.DETECTOR_RAIL);
                  var1.accept(Items.ACTIVATOR_RAIL);
                  var1.accept(Items.MINECART);
                  var1.accept(Items.HOPPER_MINECART);
                  var1.accept(Items.CHEST_MINECART);
                  var1.accept(Items.FURNACE_MINECART);
                  var1.accept(Items.TNT_MINECART);
                  var0x.holders()
                     .lookup(Registries.INSTRUMENT)
                     .ifPresent(
                        var1x -> generateInstrumentTypes(
                              var1, var1x, Items.GOAT_HORN, InstrumentTags.GOAT_HORNS, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
                           )
                     );
                  var1.accept(Items.MUSIC_DISC_13);
                  var1.accept(Items.MUSIC_DISC_CAT);
                  var1.accept(Items.MUSIC_DISC_BLOCKS);
                  var1.accept(Items.MUSIC_DISC_CHIRP);
                  var1.accept(Items.MUSIC_DISC_FAR);
                  var1.accept(Items.MUSIC_DISC_MALL);
                  var1.accept(Items.MUSIC_DISC_MELLOHI);
                  var1.accept(Items.MUSIC_DISC_STAL);
                  var1.accept(Items.MUSIC_DISC_STRAD);
                  var1.accept(Items.MUSIC_DISC_WARD);
                  var1.accept(Items.MUSIC_DISC_11);
                  var1.accept(Items.MUSIC_DISC_WAIT);
                  var1.accept(Items.MUSIC_DISC_OTHERSIDE);
                  var1.accept(Items.MUSIC_DISC_RELIC);
                  var1.accept(Items.MUSIC_DISC_5);
                  var1.accept(Items.MUSIC_DISC_PIGSTEP);
               }
            )
            .build()
      );
      Registry.register(
         var0,
         COMBAT,
         CreativeModeTab.builder(CreativeModeTab.Row.BOTTOM, 1)
            .title(Component.translatable("itemGroup.combat"))
            .icon(() -> new ItemStack(Items.NETHERITE_SWORD))
            .displayItems(
               (var0x, var1) -> {
                  var1.accept(Items.WOODEN_SWORD);
                  var1.accept(Items.STONE_SWORD);
                  var1.accept(Items.IRON_SWORD);
                  var1.accept(Items.GOLDEN_SWORD);
                  var1.accept(Items.DIAMOND_SWORD);
                  var1.accept(Items.NETHERITE_SWORD);
                  var1.accept(Items.WOODEN_AXE);
                  var1.accept(Items.STONE_AXE);
                  var1.accept(Items.IRON_AXE);
                  var1.accept(Items.GOLDEN_AXE);
                  var1.accept(Items.DIAMOND_AXE);
                  var1.accept(Items.NETHERITE_AXE);
                  var1.accept(Items.TRIDENT);
                  var1.accept(Items.MACE);
                  var1.accept(Items.SHIELD);
                  var1.accept(Items.LEATHER_HELMET);
                  var1.accept(Items.LEATHER_CHESTPLATE);
                  var1.accept(Items.LEATHER_LEGGINGS);
                  var1.accept(Items.LEATHER_BOOTS);
                  var1.accept(Items.CHAINMAIL_HELMET);
                  var1.accept(Items.CHAINMAIL_CHESTPLATE);
                  var1.accept(Items.CHAINMAIL_LEGGINGS);
                  var1.accept(Items.CHAINMAIL_BOOTS);
                  var1.accept(Items.IRON_HELMET);
                  var1.accept(Items.IRON_CHESTPLATE);
                  var1.accept(Items.IRON_LEGGINGS);
                  var1.accept(Items.IRON_BOOTS);
                  var1.accept(Items.GOLDEN_HELMET);
                  var1.accept(Items.GOLDEN_CHESTPLATE);
                  var1.accept(Items.GOLDEN_LEGGINGS);
                  var1.accept(Items.GOLDEN_BOOTS);
                  var1.accept(Items.DIAMOND_HELMET);
                  var1.accept(Items.DIAMOND_CHESTPLATE);
                  var1.accept(Items.DIAMOND_LEGGINGS);
                  var1.accept(Items.DIAMOND_BOOTS);
                  var1.accept(Items.NETHERITE_HELMET);
                  var1.accept(Items.NETHERITE_CHESTPLATE);
                  var1.accept(Items.NETHERITE_LEGGINGS);
                  var1.accept(Items.NETHERITE_BOOTS);
                  var1.accept(Items.TURTLE_HELMET);
                  var1.accept(Items.LEATHER_HORSE_ARMOR);
                  var1.accept(Items.IRON_HORSE_ARMOR);
                  var1.accept(Items.GOLDEN_HORSE_ARMOR);
                  var1.accept(Items.DIAMOND_HORSE_ARMOR);
                  var1.accept(Items.WOLF_ARMOR);
                  var1.accept(Items.TOTEM_OF_UNDYING);
                  var1.accept(Items.TNT);
                  var1.accept(Items.END_CRYSTAL);
                  var1.accept(Items.SNOWBALL);
                  var1.accept(Items.EGG);
                  var1.accept(Items.WIND_CHARGE);
                  var1.accept(Items.BOW);
                  var1.accept(Items.CROSSBOW);
                  generateFireworksAllDurations(var1, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                  var1.accept(Items.ARROW);
                  var1.accept(Items.SPECTRAL_ARROW);
                  var0x.holders()
                     .lookup(Registries.POTION)
                     .ifPresent(var1x -> generatePotionEffectTypes(var1, var1x, Items.TIPPED_ARROW, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS));
               }
            )
            .build()
      );
      Registry.register(
         var0,
         FOOD_AND_DRINKS,
         CreativeModeTab.builder(CreativeModeTab.Row.BOTTOM, 2)
            .title(Component.translatable("itemGroup.foodAndDrink"))
            .icon(() -> new ItemStack(Items.GOLDEN_APPLE))
            .displayItems((var0x, var1) -> {
               var1.accept(Items.APPLE);
               var1.accept(Items.GOLDEN_APPLE);
               var1.accept(Items.ENCHANTED_GOLDEN_APPLE);
               var1.accept(Items.MELON_SLICE);
               var1.accept(Items.SWEET_BERRIES);
               var1.accept(Items.GLOW_BERRIES);
               var1.accept(Items.CHORUS_FRUIT);
               var1.accept(Items.CARROT);
               var1.accept(Items.GOLDEN_CARROT);
               var1.accept(Items.POTATO);
               var1.accept(Items.BAKED_POTATO);
               var1.accept(Items.POISONOUS_POTATO);
               var1.accept(Items.BEETROOT);
               var1.accept(Items.DRIED_KELP);
               var1.accept(Items.BEEF);
               var1.accept(Items.COOKED_BEEF);
               var1.accept(Items.PORKCHOP);
               var1.accept(Items.COOKED_PORKCHOP);
               var1.accept(Items.MUTTON);
               var1.accept(Items.COOKED_MUTTON);
               var1.accept(Items.CHICKEN);
               var1.accept(Items.COOKED_CHICKEN);
               var1.accept(Items.RABBIT);
               var1.accept(Items.COOKED_RABBIT);
               var1.accept(Items.COD);
               var1.accept(Items.COOKED_COD);
               var1.accept(Items.SALMON);
               var1.accept(Items.COOKED_SALMON);
               var1.accept(Items.TROPICAL_FISH);
               var1.accept(Items.PUFFERFISH);
               var1.accept(Items.BREAD);
               var1.accept(Items.COOKIE);
               var1.accept(Items.CAKE);
               var1.accept(Items.PUMPKIN_PIE);
               var1.accept(Items.ROTTEN_FLESH);
               var1.accept(Items.SPIDER_EYE);
               var1.accept(Items.MUSHROOM_STEW);
               var1.accept(Items.BEETROOT_SOUP);
               var1.accept(Items.RABBIT_STEW);
               generateSuspiciousStews(var1, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
               var1.accept(Items.MILK_BUCKET);
               var1.accept(Items.HONEY_BOTTLE);
               var0x.holders().lookup(Registries.POTION).ifPresent(var1x -> {
                  generatePotionEffectTypes(var1, var1x, Items.POTION, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                  generatePotionEffectTypes(var1, var1x, Items.SPLASH_POTION, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                  generatePotionEffectTypes(var1, var1x, Items.LINGERING_POTION, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
               });
            })
            .build()
      );
      Registry.register(
         var0,
         INGREDIENTS,
         CreativeModeTab.builder(CreativeModeTab.Row.BOTTOM, 3)
            .title(Component.translatable("itemGroup.ingredients"))
            .icon(() -> new ItemStack(Items.IRON_INGOT))
            .displayItems(
               (var0x, var1) -> {
                  var1.accept(Items.COAL);
                  var1.accept(Items.CHARCOAL);
                  var1.accept(Items.RAW_IRON);
                  var1.accept(Items.RAW_COPPER);
                  var1.accept(Items.RAW_GOLD);
                  var1.accept(Items.EMERALD);
                  var1.accept(Items.LAPIS_LAZULI);
                  var1.accept(Items.DIAMOND);
                  var1.accept(Items.ANCIENT_DEBRIS);
                  var1.accept(Items.QUARTZ);
                  var1.accept(Items.AMETHYST_SHARD);
                  var1.accept(Items.IRON_NUGGET);
                  var1.accept(Items.GOLD_NUGGET);
                  var1.accept(Items.IRON_INGOT);
                  var1.accept(Items.COPPER_INGOT);
                  var1.accept(Items.GOLD_INGOT);
                  var1.accept(Items.NETHERITE_SCRAP);
                  var1.accept(Items.NETHERITE_INGOT);
                  var1.accept(Items.STICK);
                  var1.accept(Items.FLINT);
                  var1.accept(Items.WHEAT);
                  var1.accept(Items.BONE);
                  var1.accept(Items.BONE_MEAL);
                  var1.accept(Items.STRING);
                  var1.accept(Items.FEATHER);
                  var1.accept(Items.SNOWBALL);
                  var1.accept(Items.EGG);
                  var1.accept(Items.LEATHER);
                  var1.accept(Items.RABBIT_HIDE);
                  var1.accept(Items.HONEYCOMB);
                  var1.accept(Items.INK_SAC);
                  var1.accept(Items.GLOW_INK_SAC);
                  var1.accept(Items.TURTLE_SCUTE);
                  var1.accept(Items.ARMADILLO_SCUTE);
                  var1.accept(Items.SLIME_BALL);
                  var1.accept(Items.CLAY_BALL);
                  var1.accept(Items.PRISMARINE_SHARD);
                  var1.accept(Items.PRISMARINE_CRYSTALS);
                  var1.accept(Items.NAUTILUS_SHELL);
                  var1.accept(Items.HEART_OF_THE_SEA);
                  var1.accept(Items.FIRE_CHARGE);
                  var1.accept(Items.BLAZE_ROD);
                  var1.accept(Items.BREEZE_ROD);
                  var1.accept(Items.HEAVY_CORE);
                  var1.accept(Items.NETHER_STAR);
                  var1.accept(Items.ENDER_PEARL);
                  var1.accept(Items.ENDER_EYE);
                  var1.accept(Items.SHULKER_SHELL);
                  var1.accept(Items.POPPED_CHORUS_FRUIT);
                  var1.accept(Items.ECHO_SHARD);
                  var1.accept(Items.DISC_FRAGMENT_5);
                  var1.accept(Items.WHITE_DYE);
                  var1.accept(Items.LIGHT_GRAY_DYE);
                  var1.accept(Items.GRAY_DYE);
                  var1.accept(Items.BLACK_DYE);
                  var1.accept(Items.BROWN_DYE);
                  var1.accept(Items.RED_DYE);
                  var1.accept(Items.ORANGE_DYE);
                  var1.accept(Items.YELLOW_DYE);
                  var1.accept(Items.LIME_DYE);
                  var1.accept(Items.GREEN_DYE);
                  var1.accept(Items.CYAN_DYE);
                  var1.accept(Items.LIGHT_BLUE_DYE);
                  var1.accept(Items.BLUE_DYE);
                  var1.accept(Items.PURPLE_DYE);
                  var1.accept(Items.MAGENTA_DYE);
                  var1.accept(Items.PINK_DYE);
                  var1.accept(Items.BOWL);
                  var1.accept(Items.BRICK);
                  var1.accept(Items.NETHER_BRICK);
                  var1.accept(Items.PAPER);
                  var1.accept(Items.BOOK);
                  var1.accept(Items.FIREWORK_STAR);
                  var1.accept(Items.GLASS_BOTTLE);
                  var1.accept(Items.NETHER_WART);
                  var1.accept(Items.REDSTONE);
                  var1.accept(Items.GLOWSTONE_DUST);
                  var1.accept(Items.GUNPOWDER);
                  var1.accept(Items.DRAGON_BREATH);
                  var1.accept(Items.FERMENTED_SPIDER_EYE);
                  var1.accept(Items.BLAZE_POWDER);
                  var1.accept(Items.SUGAR);
                  var1.accept(Items.RABBIT_FOOT);
                  var1.accept(Items.GLISTERING_MELON_SLICE);
                  var1.accept(Items.SPIDER_EYE);
                  var1.accept(Items.PUFFERFISH);
                  var1.accept(Items.MAGMA_CREAM);
                  var1.accept(Items.GOLDEN_CARROT);
                  var1.accept(Items.GHAST_TEAR);
                  var1.accept(Items.TURTLE_HELMET);
                  var1.accept(Items.PHANTOM_MEMBRANE);
                  var1.accept(Items.FLOWER_BANNER_PATTERN);
                  var1.accept(Items.CREEPER_BANNER_PATTERN);
                  var1.accept(Items.SKULL_BANNER_PATTERN);
                  var1.accept(Items.MOJANG_BANNER_PATTERN);
                  var1.accept(Items.GLOBE_BANNER_PATTERN);
                  var1.accept(Items.PIGLIN_BANNER_PATTERN);
                  var1.accept(Items.FLOW_BANNER_PATTERN);
                  var1.accept(Items.GUSTER_BANNER_PATTERN);
                  var1.accept(Items.ANGLER_POTTERY_SHERD);
                  var1.accept(Items.ARCHER_POTTERY_SHERD);
                  var1.accept(Items.ARMS_UP_POTTERY_SHERD);
                  var1.accept(Items.BLADE_POTTERY_SHERD);
                  var1.accept(Items.BREWER_POTTERY_SHERD);
                  var1.accept(Items.BURN_POTTERY_SHERD);
                  var1.accept(Items.DANGER_POTTERY_SHERD);
                  var1.accept(Items.FLOW_POTTERY_SHERD);
                  var1.accept(Items.EXPLORER_POTTERY_SHERD);
                  var1.accept(Items.FRIEND_POTTERY_SHERD);
                  var1.accept(Items.GUSTER_POTTERY_SHERD);
                  var1.accept(Items.HEART_POTTERY_SHERD);
                  var1.accept(Items.HEARTBREAK_POTTERY_SHERD);
                  var1.accept(Items.HOWL_POTTERY_SHERD);
                  var1.accept(Items.MINER_POTTERY_SHERD);
                  var1.accept(Items.MOURNER_POTTERY_SHERD);
                  var1.accept(Items.PLENTY_POTTERY_SHERD);
                  var1.accept(Items.PRIZE_POTTERY_SHERD);
                  var1.accept(Items.SCRAPE_POTTERY_SHERD);
                  var1.accept(Items.SHEAF_POTTERY_SHERD);
                  var1.accept(Items.SHELTER_POTTERY_SHERD);
                  var1.accept(Items.SKULL_POTTERY_SHERD);
                  var1.accept(Items.SNORT_POTTERY_SHERD);
                  var1.accept(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE);
                  var1.accept(Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE);
                  var1.accept(Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE);
                  var1.accept(Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE);
                  var1.accept(Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE);
                  var1.accept(Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE);
                  var1.accept(Items.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE);
                  var1.accept(Items.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE);
                  var1.accept(Items.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE);
                  var1.accept(Items.HOST_ARMOR_TRIM_SMITHING_TEMPLATE);
                  var1.accept(Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE);
                  var1.accept(Items.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE);
                  var1.accept(Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE);
                  var1.accept(Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE);
                  var1.accept(Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE);
                  var1.accept(Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE);
                  var1.accept(Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE);
                  var1.accept(Items.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE);
                  var1.accept(Items.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE);
                  var1.accept(Items.EXPERIENCE_BOTTLE);
                  var1.accept(Items.TRIAL_KEY);
                  Set var2 = Set.of(
                     ItemTags.FOOT_ARMOR_ENCHANTABLE,
                     ItemTags.LEG_ARMOR_ENCHANTABLE,
                     ItemTags.CHEST_ARMOR_ENCHANTABLE,
                     ItemTags.HEAD_ARMOR_ENCHANTABLE,
                     ItemTags.ARMOR_ENCHANTABLE,
                     ItemTags.SWORD_ENCHANTABLE,
                     ItemTags.WEAPON_ENCHANTABLE,
                     ItemTags.MINING_ENCHANTABLE,
                     ItemTags.FISHING_ENCHANTABLE,
                     ItemTags.TRIDENT_ENCHANTABLE,
                     ItemTags.DURABILITY_ENCHANTABLE,
                     ItemTags.BOW_ENCHANTABLE,
                     ItemTags.EQUIPPABLE_ENCHANTABLE,
                     ItemTags.CROSSBOW_ENCHANTABLE,
                     ItemTags.VANISHING_ENCHANTABLE
                  );
                  var0x.holders().lookup(Registries.ENCHANTMENT).ifPresent(var2x -> {
                     generateEnchantmentBookTypesOnlyMaxLevel(var1, var2x, var2, CreativeModeTab.TabVisibility.PARENT_TAB_ONLY);
                     generateEnchantmentBookTypesAllLevels(var1, var2x, var2, CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY);
                  });
               }
            )
            .build()
      );
      Registry.register(
         var0,
         SPAWN_EGGS,
         CreativeModeTab.builder(CreativeModeTab.Row.BOTTOM, 4)
            .title(Component.translatable("itemGroup.spawnEggs"))
            .icon(() -> new ItemStack(Items.PIG_SPAWN_EGG))
            .displayItems((var0x, var1) -> {
               var1.accept(Items.SPAWNER);
               var1.accept(Items.TRIAL_SPAWNER);
               var1.accept(Items.ALLAY_SPAWN_EGG);
               var1.accept(Items.ARMADILLO_SPAWN_EGG);
               var1.accept(Items.AXOLOTL_SPAWN_EGG);
               var1.accept(Items.BAT_SPAWN_EGG);
               var1.accept(Items.BEE_SPAWN_EGG);
               var1.accept(Items.BLAZE_SPAWN_EGG);
               var1.accept(Items.BOGGED_SPAWN_EGG);
               var1.accept(Items.BREEZE_SPAWN_EGG);
               var1.accept(Items.CAMEL_SPAWN_EGG);
               var1.accept(Items.CAT_SPAWN_EGG);
               var1.accept(Items.CAVE_SPIDER_SPAWN_EGG);
               var1.accept(Items.CHICKEN_SPAWN_EGG);
               var1.accept(Items.COD_SPAWN_EGG);
               var1.accept(Items.COW_SPAWN_EGG);
               var1.accept(Items.CREEPER_SPAWN_EGG);
               var1.accept(Items.DOLPHIN_SPAWN_EGG);
               var1.accept(Items.DONKEY_SPAWN_EGG);
               var1.accept(Items.DROWNED_SPAWN_EGG);
               var1.accept(Items.ELDER_GUARDIAN_SPAWN_EGG);
               var1.accept(Items.ENDERMAN_SPAWN_EGG);
               var1.accept(Items.ENDERMITE_SPAWN_EGG);
               var1.accept(Items.EVOKER_SPAWN_EGG);
               var1.accept(Items.FOX_SPAWN_EGG);
               var1.accept(Items.FROG_SPAWN_EGG);
               var1.accept(Items.GHAST_SPAWN_EGG);
               var1.accept(Items.GLOW_SQUID_SPAWN_EGG);
               var1.accept(Items.GOAT_SPAWN_EGG);
               var1.accept(Items.GUARDIAN_SPAWN_EGG);
               var1.accept(Items.HOGLIN_SPAWN_EGG);
               var1.accept(Items.HORSE_SPAWN_EGG);
               var1.accept(Items.HUSK_SPAWN_EGG);
               var1.accept(Items.IRON_GOLEM_SPAWN_EGG);
               var1.accept(Items.LLAMA_SPAWN_EGG);
               var1.accept(Items.MAGMA_CUBE_SPAWN_EGG);
               var1.accept(Items.MOOSHROOM_SPAWN_EGG);
               var1.accept(Items.MULE_SPAWN_EGG);
               var1.accept(Items.OCELOT_SPAWN_EGG);
               var1.accept(Items.PANDA_SPAWN_EGG);
               var1.accept(Items.PARROT_SPAWN_EGG);
               var1.accept(Items.PHANTOM_SPAWN_EGG);
               var1.accept(Items.PIG_SPAWN_EGG);
               var1.accept(Items.PIGLIN_SPAWN_EGG);
               var1.accept(Items.PIGLIN_BRUTE_SPAWN_EGG);
               var1.accept(Items.PILLAGER_SPAWN_EGG);
               var1.accept(Items.POLAR_BEAR_SPAWN_EGG);
               var1.accept(Items.PUFFERFISH_SPAWN_EGG);
               var1.accept(Items.RABBIT_SPAWN_EGG);
               var1.accept(Items.RAVAGER_SPAWN_EGG);
               var1.accept(Items.SALMON_SPAWN_EGG);
               var1.accept(Items.SHEEP_SPAWN_EGG);
               var1.accept(Items.SHULKER_SPAWN_EGG);
               var1.accept(Items.SILVERFISH_SPAWN_EGG);
               var1.accept(Items.SKELETON_SPAWN_EGG);
               var1.accept(Items.SKELETON_HORSE_SPAWN_EGG);
               var1.accept(Items.SLIME_SPAWN_EGG);
               var1.accept(Items.SNIFFER_SPAWN_EGG);
               var1.accept(Items.SNOW_GOLEM_SPAWN_EGG);
               var1.accept(Items.SPIDER_SPAWN_EGG);
               var1.accept(Items.SQUID_SPAWN_EGG);
               var1.accept(Items.STRAY_SPAWN_EGG);
               var1.accept(Items.STRIDER_SPAWN_EGG);
               var1.accept(Items.TADPOLE_SPAWN_EGG);
               var1.accept(Items.TRADER_LLAMA_SPAWN_EGG);
               var1.accept(Items.TROPICAL_FISH_SPAWN_EGG);
               var1.accept(Items.TURTLE_SPAWN_EGG);
               var1.accept(Items.VEX_SPAWN_EGG);
               var1.accept(Items.VILLAGER_SPAWN_EGG);
               var1.accept(Items.VINDICATOR_SPAWN_EGG);
               var1.accept(Items.WANDERING_TRADER_SPAWN_EGG);
               var1.accept(Items.WARDEN_SPAWN_EGG);
               var1.accept(Items.WITCH_SPAWN_EGG);
               var1.accept(Items.WITHER_SKELETON_SPAWN_EGG);
               var1.accept(Items.WOLF_SPAWN_EGG);
               var1.accept(Items.ZOGLIN_SPAWN_EGG);
               var1.accept(Items.ZOMBIE_SPAWN_EGG);
               var1.accept(Items.ZOMBIE_HORSE_SPAWN_EGG);
               var1.accept(Items.ZOMBIE_VILLAGER_SPAWN_EGG);
               var1.accept(Items.ZOMBIFIED_PIGLIN_SPAWN_EGG);
            })
            .build()
      );
      Registry.register(
         var0,
         OP_BLOCKS,
         CreativeModeTab.builder(CreativeModeTab.Row.BOTTOM, 5)
            .title(Component.translatable("itemGroup.op"))
            .icon(() -> new ItemStack(Items.COMMAND_BLOCK))
            .alignedRight()
            .displayItems(
               (var0x, var1) -> {
                  if (var0x.hasPermissions()) {
                     var1.accept(Items.COMMAND_BLOCK);
                     var1.accept(Items.CHAIN_COMMAND_BLOCK);
                     var1.accept(Items.REPEATING_COMMAND_BLOCK);
                     var1.accept(Items.COMMAND_BLOCK_MINECART);
                     var1.accept(Items.JIGSAW);
                     var1.accept(Items.STRUCTURE_BLOCK);
                     var1.accept(Items.STRUCTURE_VOID);
                     var1.accept(Items.BARRIER);
                     var1.accept(Items.DEBUG_STICK);
         
                     for(int var2 = 15; var2 >= 0; --var2) {
                        var1.accept(LightBlock.setLightOnStack(new ItemStack(Items.LIGHT), var2));
                     }
         
                     var0x.holders()
                        .lookup(Registries.PAINTING_VARIANT)
                        .ifPresent(
                           var1x -> generatePresetPaintings(
                                 var1, var1x, var0xxx -> !var0xxx.is(PaintingVariantTags.PLACEABLE), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
                              )
                        );
                  }
               }
            )
            .build()
      );
      return Registry.register(
         var0,
         INVENTORY,
         CreativeModeTab.builder(CreativeModeTab.Row.BOTTOM, 6)
            .title(Component.translatable("itemGroup.inventory"))
            .icon(() -> new ItemStack(Blocks.CHEST))
            .backgroundSuffix("inventory.png")
            .hideTitle()
            .alignedRight()
            .type(CreativeModeTab.Type.INVENTORY)
            .noScrollBar()
            .build()
      );
   }

   public static void validate() {
      HashMap var0 = new HashMap();

      for(ResourceKey var2 : BuiltInRegistries.CREATIVE_MODE_TAB.registryKeySet()) {
         CreativeModeTab var3 = BuiltInRegistries.CREATIVE_MODE_TAB.getOrThrow(var2);
         String var4 = var3.getDisplayName().getString();
         String var5 = var0.put(Pair.of(var3.row(), var3.column()), var4);
         if (var5 != null) {
            throw new IllegalArgumentException("Duplicate position: " + var4 + " vs. " + var5);
         }
      }
   }

   public static CreativeModeTab getDefaultTab() {
      return BuiltInRegistries.CREATIVE_MODE_TAB.getOrThrow(BUILDING_BLOCKS);
   }

   private static void generatePotionEffectTypes(CreativeModeTab.Output var0, HolderLookup<Potion> var1, Item var2, CreativeModeTab.TabVisibility var3) {
      var1.listElements().map(var1x -> PotionContents.createItemStack(var2, var1x)).forEach(var2x -> var0.accept(var2x, var3));
   }

   private static void generateEnchantmentBookTypesOnlyMaxLevel(
      CreativeModeTab.Output var0, HolderLookup<Enchantment> var1, Set<TagKey<Item>> var2, CreativeModeTab.TabVisibility var3
   ) {
      var1.listElements()
         .map(Holder::value)
         .filter(var1x -> var2.contains(var1x.getMatch()))
         .map(var0x -> EnchantedBookItem.createForEnchantment(new EnchantmentInstance(var0x, var0x.getMaxLevel())))
         .forEach(var2x -> var0.accept(var2x, var3));
   }

   private static void generateEnchantmentBookTypesAllLevels(
      CreativeModeTab.Output var0, HolderLookup<Enchantment> var1, Set<TagKey<Item>> var2, CreativeModeTab.TabVisibility var3
   ) {
      var1.listElements()
         .map(Holder::value)
         .filter(var1x -> var2.contains(var1x.getMatch()))
         .flatMap(
            var0x -> IntStream.rangeClosed(var0x.getMinLevel(), var0x.getMaxLevel())
                  .mapToObj(var1x -> EnchantedBookItem.createForEnchantment(new EnchantmentInstance(var0x, var1x)))
         )
         .forEach(var2x -> var0.accept(var2x, var3));
   }

   private static void generateInstrumentTypes(
      CreativeModeTab.Output var0, HolderLookup<Instrument> var1, Item var2, TagKey<Instrument> var3, CreativeModeTab.TabVisibility var4
   ) {
      var1.get(var3).ifPresent(var3x -> var3x.stream().map(var1xx -> InstrumentItem.create(var2, var1xx)).forEach(var2xx -> var0.accept(var2xx, var4)));
   }

   private static void generateSuspiciousStews(CreativeModeTab.Output var0, CreativeModeTab.TabVisibility var1) {
      List var2 = SuspiciousEffectHolder.getAllEffectHolders();
      Set var3 = ItemStackLinkedSet.createTypeAndComponentsSet();

      for(SuspiciousEffectHolder var5 : var2) {
         ItemStack var6 = new ItemStack(Items.SUSPICIOUS_STEW);
         var6.set(DataComponents.SUSPICIOUS_STEW_EFFECTS, var5.getSuspiciousEffects());
         var3.add(var6);
      }

      var0.acceptAll(var3, var1);
   }

   private static void generateFireworksAllDurations(CreativeModeTab.Output var0, CreativeModeTab.TabVisibility var1) {
      for(byte var5 : FireworkRocketItem.CRAFTABLE_DURATIONS) {
         ItemStack var6 = new ItemStack(Items.FIREWORK_ROCKET);
         var6.set(DataComponents.FIREWORKS, new Fireworks(var5, List.of()));
         var0.accept(var6, var1);
      }
   }

   private static void generatePresetPaintings(
      CreativeModeTab.Output var0,
      HolderLookup.RegistryLookup<PaintingVariant> var1,
      Predicate<Holder<PaintingVariant>> var2,
      CreativeModeTab.TabVisibility var3
   ) {
      var1.listElements()
         .filter(var2)
         .sorted(PAINTING_COMPARATOR)
         .forEach(
            var2x -> {
               CustomData var3xx = Util.<CustomData, IllegalStateException>getOrThrow(
                     CustomData.EMPTY.update(Painting.VARIANT_MAP_CODEC, var2x), IllegalStateException::new
                  )
                  .update(var0xx -> var0xx.putString("id", "minecraft:painting"));
               ItemStack var4 = new ItemStack(Items.PAINTING);
               var4.set(DataComponents.ENTITY_DATA, var3xx);
               var0.accept(var4, var3);
            }
         );
   }

   public static List<CreativeModeTab> tabs() {
      return streamAllTabs().filter(CreativeModeTab::shouldDisplay).toList();
   }

   public static List<CreativeModeTab> allTabs() {
      return streamAllTabs().toList();
   }

   private static Stream<CreativeModeTab> streamAllTabs() {
      return BuiltInRegistries.CREATIVE_MODE_TAB.stream();
   }

   public static CreativeModeTab searchTab() {
      return BuiltInRegistries.CREATIVE_MODE_TAB.getOrThrow(SEARCH);
   }

   private static void buildAllTabContents(CreativeModeTab.ItemDisplayParameters var0) {
      streamAllTabs().filter(var0x -> var0x.getType() == CreativeModeTab.Type.CATEGORY).forEach(var1 -> var1.buildContents(var0));
      streamAllTabs().filter(var0x -> var0x.getType() != CreativeModeTab.Type.CATEGORY).forEach(var1 -> var1.buildContents(var0));
   }

   public static boolean tryRebuildTabContents(FeatureFlagSet var0, boolean var1, HolderLookup.Provider var2) {
      if (CACHED_PARAMETERS != null && !CACHED_PARAMETERS.needsUpdate(var0, var1, var2)) {
         return false;
      } else {
         CACHED_PARAMETERS = new CreativeModeTab.ItemDisplayParameters(var0, var1, var2);
         buildAllTabContents(CACHED_PARAMETERS);
         return true;
      }
   }
}
