package net.minecraft.client.data.models.model;

import java.util.Optional;
import java.util.stream.IntStream;
import net.minecraft.resources.ResourceLocation;

public class ModelTemplates {
   public static final ModelTemplate CUBE;
   public static final ModelTemplate CUBE_DIRECTIONAL;
   public static final ModelTemplate CUBE_ALL;
   public static final ModelTemplate CUBE_ALL_INNER_FACES;
   public static final ModelTemplate CUBE_MIRRORED_ALL;
   public static final ModelTemplate CUBE_NORTH_WEST_MIRRORED_ALL;
   public static final ModelTemplate CUBE_COLUMN_UV_LOCKED_X;
   public static final ModelTemplate CUBE_COLUMN_UV_LOCKED_Y;
   public static final ModelTemplate CUBE_COLUMN_UV_LOCKED_Z;
   public static final ModelTemplate CUBE_COLUMN;
   public static final ModelTemplate CUBE_COLUMN_HORIZONTAL;
   public static final ModelTemplate CUBE_COLUMN_MIRRORED;
   public static final ModelTemplate CUBE_TOP;
   public static final ModelTemplate CUBE_BOTTOM_TOP;
   public static final ModelTemplate CUBE_BOTTOM_TOP_INNER_FACES;
   public static final ModelTemplate CUBE_ORIENTABLE;
   public static final ModelTemplate CUBE_ORIENTABLE_TOP_BOTTOM;
   public static final ModelTemplate CUBE_ORIENTABLE_VERTICAL;
   public static final ModelTemplate BUTTON;
   public static final ModelTemplate BUTTON_PRESSED;
   public static final ModelTemplate BUTTON_INVENTORY;
   public static final ModelTemplate DOOR_BOTTOM_LEFT;
   public static final ModelTemplate DOOR_BOTTOM_LEFT_OPEN;
   public static final ModelTemplate DOOR_BOTTOM_RIGHT;
   public static final ModelTemplate DOOR_BOTTOM_RIGHT_OPEN;
   public static final ModelTemplate DOOR_TOP_LEFT;
   public static final ModelTemplate DOOR_TOP_LEFT_OPEN;
   public static final ModelTemplate DOOR_TOP_RIGHT;
   public static final ModelTemplate DOOR_TOP_RIGHT_OPEN;
   public static final ModelTemplate CUSTOM_FENCE_POST;
   public static final ModelTemplate CUSTOM_FENCE_SIDE_NORTH;
   public static final ModelTemplate CUSTOM_FENCE_SIDE_EAST;
   public static final ModelTemplate CUSTOM_FENCE_SIDE_SOUTH;
   public static final ModelTemplate CUSTOM_FENCE_SIDE_WEST;
   public static final ModelTemplate CUSTOM_FENCE_INVENTORY;
   public static final ModelTemplate FENCE_POST;
   public static final ModelTemplate FENCE_SIDE;
   public static final ModelTemplate FENCE_INVENTORY;
   public static final ModelTemplate WALL_POST;
   public static final ModelTemplate WALL_LOW_SIDE;
   public static final ModelTemplate WALL_TALL_SIDE;
   public static final ModelTemplate WALL_INVENTORY;
   public static final ModelTemplate CUSTOM_FENCE_GATE_CLOSED;
   public static final ModelTemplate CUSTOM_FENCE_GATE_OPEN;
   public static final ModelTemplate CUSTOM_FENCE_GATE_WALL_CLOSED;
   public static final ModelTemplate CUSTOM_FENCE_GATE_WALL_OPEN;
   public static final ModelTemplate FENCE_GATE_CLOSED;
   public static final ModelTemplate FENCE_GATE_OPEN;
   public static final ModelTemplate FENCE_GATE_WALL_CLOSED;
   public static final ModelTemplate FENCE_GATE_WALL_OPEN;
   public static final ModelTemplate PRESSURE_PLATE_UP;
   public static final ModelTemplate PRESSURE_PLATE_DOWN;
   public static final ModelTemplate PARTICLE_ONLY;
   public static final ModelTemplate SLAB_BOTTOM;
   public static final ModelTemplate SLAB_TOP;
   public static final ModelTemplate LEAVES;
   public static final ModelTemplate STAIRS_STRAIGHT;
   public static final ModelTemplate STAIRS_INNER;
   public static final ModelTemplate STAIRS_OUTER;
   public static final ModelTemplate TRAPDOOR_TOP;
   public static final ModelTemplate TRAPDOOR_BOTTOM;
   public static final ModelTemplate TRAPDOOR_OPEN;
   public static final ModelTemplate ORIENTABLE_TRAPDOOR_TOP;
   public static final ModelTemplate ORIENTABLE_TRAPDOOR_BOTTOM;
   public static final ModelTemplate ORIENTABLE_TRAPDOOR_OPEN;
   public static final ModelTemplate POINTED_DRIPSTONE;
   public static final ModelTemplate CROSS;
   public static final ModelTemplate TINTED_CROSS;
   public static final ModelTemplate CROSS_EMISSIVE;
   public static final ModelTemplate FLOWER_POT_CROSS;
   public static final ModelTemplate TINTED_FLOWER_POT_CROSS;
   public static final ModelTemplate FLOWER_POT_CROSS_EMISSIVE;
   public static final ModelTemplate RAIL_FLAT;
   public static final ModelTemplate RAIL_CURVED;
   public static final ModelTemplate RAIL_RAISED_NE;
   public static final ModelTemplate RAIL_RAISED_SW;
   public static final ModelTemplate CARPET;
   public static final ModelTemplate MOSSY_CARPET_SIDE;
   public static final ModelTemplate FLOWERBED_1;
   public static final ModelTemplate FLOWERBED_2;
   public static final ModelTemplate FLOWERBED_3;
   public static final ModelTemplate FLOWERBED_4;
   public static final ModelTemplate CORAL_FAN;
   public static final ModelTemplate CORAL_WALL_FAN;
   public static final ModelTemplate GLAZED_TERRACOTTA;
   public static final ModelTemplate CHORUS_FLOWER;
   public static final ModelTemplate DAYLIGHT_DETECTOR;
   public static final ModelTemplate STAINED_GLASS_PANE_NOSIDE;
   public static final ModelTemplate STAINED_GLASS_PANE_NOSIDE_ALT;
   public static final ModelTemplate STAINED_GLASS_PANE_POST;
   public static final ModelTemplate STAINED_GLASS_PANE_SIDE;
   public static final ModelTemplate STAINED_GLASS_PANE_SIDE_ALT;
   public static final ModelTemplate COMMAND_BLOCK;
   public static final ModelTemplate CHISELED_BOOKSHELF_SLOT_TOP_LEFT;
   public static final ModelTemplate CHISELED_BOOKSHELF_SLOT_TOP_MID;
   public static final ModelTemplate CHISELED_BOOKSHELF_SLOT_TOP_RIGHT;
   public static final ModelTemplate CHISELED_BOOKSHELF_SLOT_BOTTOM_LEFT;
   public static final ModelTemplate CHISELED_BOOKSHELF_SLOT_BOTTOM_MID;
   public static final ModelTemplate CHISELED_BOOKSHELF_SLOT_BOTTOM_RIGHT;
   public static final ModelTemplate ANVIL;
   public static final ModelTemplate[] STEMS;
   public static final ModelTemplate ATTACHED_STEM;
   public static final ModelTemplate CROP;
   public static final ModelTemplate FARMLAND;
   public static final ModelTemplate FIRE_FLOOR;
   public static final ModelTemplate FIRE_SIDE;
   public static final ModelTemplate FIRE_SIDE_ALT;
   public static final ModelTemplate FIRE_UP;
   public static final ModelTemplate FIRE_UP_ALT;
   public static final ModelTemplate CAMPFIRE;
   public static final ModelTemplate LANTERN;
   public static final ModelTemplate HANGING_LANTERN;
   public static final ModelTemplate TORCH;
   public static final ModelTemplate TORCH_UNLIT;
   public static final ModelTemplate WALL_TORCH;
   public static final ModelTemplate WALL_TORCH_UNLIT;
   public static final ModelTemplate REDSTONE_TORCH;
   public static final ModelTemplate REDSTONE_WALL_TORCH;
   public static final ModelTemplate PISTON;
   public static final ModelTemplate PISTON_HEAD;
   public static final ModelTemplate PISTON_HEAD_SHORT;
   public static final ModelTemplate SEAGRASS;
   public static final ModelTemplate TURTLE_EGG;
   public static final ModelTemplate TWO_TURTLE_EGGS;
   public static final ModelTemplate THREE_TURTLE_EGGS;
   public static final ModelTemplate FOUR_TURTLE_EGGS;
   public static final ModelTemplate SINGLE_FACE;
   public static final ModelTemplate CAULDRON_LEVEL1;
   public static final ModelTemplate CAULDRON_LEVEL2;
   public static final ModelTemplate CAULDRON_FULL;
   public static final ModelTemplate AZALEA;
   public static final ModelTemplate POTTED_AZALEA;
   public static final ModelTemplate POTTED_FLOWERING_AZALEA;
   public static final ModelTemplate SNIFFER_EGG;
   public static final ModelTemplate FLAT_ITEM;
   public static final ModelTemplate MUSIC_DISC;
   public static final ModelTemplate FLAT_HANDHELD_ITEM;
   public static final ModelTemplate FLAT_HANDHELD_ROD_ITEM;
   public static final ModelTemplate TWO_LAYERED_ITEM;
   public static final ModelTemplate THREE_LAYERED_ITEM;
   public static final ModelTemplate SHULKER_BOX_INVENTORY;
   public static final ModelTemplate BED_INVENTORY;
   public static final ModelTemplate CHEST_INVENTORY;
   public static final ModelTemplate BUNDLE_OPEN_FRONT_INVENTORY;
   public static final ModelTemplate BUNDLE_OPEN_BACK_INVENTORY;
   public static final ModelTemplate BOW;
   public static final ModelTemplate CROSSBOW;
   public static final ModelTemplate CANDLE;
   public static final ModelTemplate TWO_CANDLES;
   public static final ModelTemplate THREE_CANDLES;
   public static final ModelTemplate FOUR_CANDLES;
   public static final ModelTemplate CANDLE_CAKE;
   public static final ModelTemplate SCULK_SHRIEKER;
   public static final ModelTemplate VAULT;
   public static final ModelTemplate FLAT_HANDHELD_MACE_ITEM;

   public ModelTemplates() {
      super();
   }

   private static ModelTemplate create(TextureSlot... var0) {
      return new ModelTemplate(Optional.empty(), Optional.empty(), var0);
   }

   private static ModelTemplate create(String var0, TextureSlot... var1) {
      return new ModelTemplate(Optional.of(ResourceLocation.withDefaultNamespace("block/" + var0)), Optional.empty(), var1);
   }

   private static ModelTemplate createItem(String var0, TextureSlot... var1) {
      return new ModelTemplate(Optional.of(ResourceLocation.withDefaultNamespace("item/" + var0)), Optional.empty(), var1);
   }

   private static ModelTemplate createItem(String var0, String var1, TextureSlot... var2) {
      return new ModelTemplate(Optional.of(ResourceLocation.withDefaultNamespace("item/" + var0)), Optional.of(var1), var2);
   }

   private static ModelTemplate create(String var0, String var1, TextureSlot... var2) {
      return new ModelTemplate(Optional.of(ResourceLocation.withDefaultNamespace("block/" + var0)), Optional.of(var1), var2);
   }

   static {
      CUBE = create("cube", TextureSlot.PARTICLE, TextureSlot.NORTH, TextureSlot.SOUTH, TextureSlot.EAST, TextureSlot.WEST, TextureSlot.UP, TextureSlot.DOWN);
      CUBE_DIRECTIONAL = create("cube_directional", TextureSlot.PARTICLE, TextureSlot.NORTH, TextureSlot.SOUTH, TextureSlot.EAST, TextureSlot.WEST, TextureSlot.UP, TextureSlot.DOWN);
      CUBE_ALL = create("cube_all", TextureSlot.ALL);
      CUBE_ALL_INNER_FACES = create("cube_all_inner_faces", TextureSlot.ALL);
      CUBE_MIRRORED_ALL = create("cube_mirrored_all", "_mirrored", TextureSlot.ALL);
      CUBE_NORTH_WEST_MIRRORED_ALL = create("cube_north_west_mirrored_all", "_north_west_mirrored", TextureSlot.ALL);
      CUBE_COLUMN_UV_LOCKED_X = create("cube_column_uv_locked_x", "_x", TextureSlot.END, TextureSlot.SIDE);
      CUBE_COLUMN_UV_LOCKED_Y = create("cube_column_uv_locked_y", "_y", TextureSlot.END, TextureSlot.SIDE);
      CUBE_COLUMN_UV_LOCKED_Z = create("cube_column_uv_locked_z", "_z", TextureSlot.END, TextureSlot.SIDE);
      CUBE_COLUMN = create("cube_column", TextureSlot.END, TextureSlot.SIDE);
      CUBE_COLUMN_HORIZONTAL = create("cube_column_horizontal", "_horizontal", TextureSlot.END, TextureSlot.SIDE);
      CUBE_COLUMN_MIRRORED = create("cube_column_mirrored", "_mirrored", TextureSlot.END, TextureSlot.SIDE);
      CUBE_TOP = create("cube_top", TextureSlot.TOP, TextureSlot.SIDE);
      CUBE_BOTTOM_TOP = create("cube_bottom_top", TextureSlot.TOP, TextureSlot.BOTTOM, TextureSlot.SIDE);
      CUBE_BOTTOM_TOP_INNER_FACES = create("cube_bottom_top_inner_faces", TextureSlot.TOP, TextureSlot.BOTTOM, TextureSlot.SIDE);
      CUBE_ORIENTABLE = create("orientable", TextureSlot.TOP, TextureSlot.FRONT, TextureSlot.SIDE);
      CUBE_ORIENTABLE_TOP_BOTTOM = create("orientable_with_bottom", TextureSlot.TOP, TextureSlot.BOTTOM, TextureSlot.SIDE, TextureSlot.FRONT);
      CUBE_ORIENTABLE_VERTICAL = create("orientable_vertical", "_vertical", TextureSlot.FRONT, TextureSlot.SIDE);
      BUTTON = create("button", TextureSlot.TEXTURE);
      BUTTON_PRESSED = create("button_pressed", "_pressed", TextureSlot.TEXTURE);
      BUTTON_INVENTORY = create("button_inventory", "_inventory", TextureSlot.TEXTURE);
      DOOR_BOTTOM_LEFT = create("door_bottom_left", "_bottom_left", TextureSlot.TOP, TextureSlot.BOTTOM);
      DOOR_BOTTOM_LEFT_OPEN = create("door_bottom_left_open", "_bottom_left_open", TextureSlot.TOP, TextureSlot.BOTTOM);
      DOOR_BOTTOM_RIGHT = create("door_bottom_right", "_bottom_right", TextureSlot.TOP, TextureSlot.BOTTOM);
      DOOR_BOTTOM_RIGHT_OPEN = create("door_bottom_right_open", "_bottom_right_open", TextureSlot.TOP, TextureSlot.BOTTOM);
      DOOR_TOP_LEFT = create("door_top_left", "_top_left", TextureSlot.TOP, TextureSlot.BOTTOM);
      DOOR_TOP_LEFT_OPEN = create("door_top_left_open", "_top_left_open", TextureSlot.TOP, TextureSlot.BOTTOM);
      DOOR_TOP_RIGHT = create("door_top_right", "_top_right", TextureSlot.TOP, TextureSlot.BOTTOM);
      DOOR_TOP_RIGHT_OPEN = create("door_top_right_open", "_top_right_open", TextureSlot.TOP, TextureSlot.BOTTOM);
      CUSTOM_FENCE_POST = create("custom_fence_post", "_post", TextureSlot.TEXTURE, TextureSlot.PARTICLE);
      CUSTOM_FENCE_SIDE_NORTH = create("custom_fence_side_north", "_side_north", TextureSlot.TEXTURE);
      CUSTOM_FENCE_SIDE_EAST = create("custom_fence_side_east", "_side_east", TextureSlot.TEXTURE);
      CUSTOM_FENCE_SIDE_SOUTH = create("custom_fence_side_south", "_side_south", TextureSlot.TEXTURE);
      CUSTOM_FENCE_SIDE_WEST = create("custom_fence_side_west", "_side_west", TextureSlot.TEXTURE);
      CUSTOM_FENCE_INVENTORY = create("custom_fence_inventory", "_inventory", TextureSlot.TEXTURE);
      FENCE_POST = create("fence_post", "_post", TextureSlot.TEXTURE);
      FENCE_SIDE = create("fence_side", "_side", TextureSlot.TEXTURE);
      FENCE_INVENTORY = create("fence_inventory", "_inventory", TextureSlot.TEXTURE);
      WALL_POST = create("template_wall_post", "_post", TextureSlot.WALL);
      WALL_LOW_SIDE = create("template_wall_side", "_side", TextureSlot.WALL);
      WALL_TALL_SIDE = create("template_wall_side_tall", "_side_tall", TextureSlot.WALL);
      WALL_INVENTORY = create("wall_inventory", "_inventory", TextureSlot.WALL);
      CUSTOM_FENCE_GATE_CLOSED = create("template_custom_fence_gate", TextureSlot.TEXTURE, TextureSlot.PARTICLE);
      CUSTOM_FENCE_GATE_OPEN = create("template_custom_fence_gate_open", "_open", TextureSlot.TEXTURE, TextureSlot.PARTICLE);
      CUSTOM_FENCE_GATE_WALL_CLOSED = create("template_custom_fence_gate_wall", "_wall", TextureSlot.TEXTURE, TextureSlot.PARTICLE);
      CUSTOM_FENCE_GATE_WALL_OPEN = create("template_custom_fence_gate_wall_open", "_wall_open", TextureSlot.TEXTURE, TextureSlot.PARTICLE);
      FENCE_GATE_CLOSED = create("template_fence_gate", TextureSlot.TEXTURE);
      FENCE_GATE_OPEN = create("template_fence_gate_open", "_open", TextureSlot.TEXTURE);
      FENCE_GATE_WALL_CLOSED = create("template_fence_gate_wall", "_wall", TextureSlot.TEXTURE);
      FENCE_GATE_WALL_OPEN = create("template_fence_gate_wall_open", "_wall_open", TextureSlot.TEXTURE);
      PRESSURE_PLATE_UP = create("pressure_plate_up", TextureSlot.TEXTURE);
      PRESSURE_PLATE_DOWN = create("pressure_plate_down", "_down", TextureSlot.TEXTURE);
      PARTICLE_ONLY = create(TextureSlot.PARTICLE);
      SLAB_BOTTOM = create("slab", TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE);
      SLAB_TOP = create("slab_top", "_top", TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE);
      LEAVES = create("leaves", TextureSlot.ALL);
      STAIRS_STRAIGHT = create("stairs", TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE);
      STAIRS_INNER = create("inner_stairs", "_inner", TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE);
      STAIRS_OUTER = create("outer_stairs", "_outer", TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE);
      TRAPDOOR_TOP = create("template_trapdoor_top", "_top", TextureSlot.TEXTURE);
      TRAPDOOR_BOTTOM = create("template_trapdoor_bottom", "_bottom", TextureSlot.TEXTURE);
      TRAPDOOR_OPEN = create("template_trapdoor_open", "_open", TextureSlot.TEXTURE);
      ORIENTABLE_TRAPDOOR_TOP = create("template_orientable_trapdoor_top", "_top", TextureSlot.TEXTURE);
      ORIENTABLE_TRAPDOOR_BOTTOM = create("template_orientable_trapdoor_bottom", "_bottom", TextureSlot.TEXTURE);
      ORIENTABLE_TRAPDOOR_OPEN = create("template_orientable_trapdoor_open", "_open", TextureSlot.TEXTURE);
      POINTED_DRIPSTONE = create("pointed_dripstone", TextureSlot.CROSS);
      CROSS = create("cross", TextureSlot.CROSS);
      TINTED_CROSS = create("tinted_cross", TextureSlot.CROSS);
      CROSS_EMISSIVE = create("cross_emissive", TextureSlot.CROSS, TextureSlot.CROSS_EMISSIVE);
      FLOWER_POT_CROSS = create("flower_pot_cross", TextureSlot.PLANT);
      TINTED_FLOWER_POT_CROSS = create("tinted_flower_pot_cross", TextureSlot.PLANT);
      FLOWER_POT_CROSS_EMISSIVE = create("flower_pot_cross_emissive", TextureSlot.PLANT, TextureSlot.CROSS_EMISSIVE);
      RAIL_FLAT = create("rail_flat", TextureSlot.RAIL);
      RAIL_CURVED = create("rail_curved", "_corner", TextureSlot.RAIL);
      RAIL_RAISED_NE = create("template_rail_raised_ne", "_raised_ne", TextureSlot.RAIL);
      RAIL_RAISED_SW = create("template_rail_raised_sw", "_raised_sw", TextureSlot.RAIL);
      CARPET = create("carpet", TextureSlot.WOOL);
      MOSSY_CARPET_SIDE = create("mossy_carpet_side", TextureSlot.SIDE);
      FLOWERBED_1 = create("flowerbed_1", "_1", TextureSlot.FLOWERBED, TextureSlot.STEM);
      FLOWERBED_2 = create("flowerbed_2", "_2", TextureSlot.FLOWERBED, TextureSlot.STEM);
      FLOWERBED_3 = create("flowerbed_3", "_3", TextureSlot.FLOWERBED, TextureSlot.STEM);
      FLOWERBED_4 = create("flowerbed_4", "_4", TextureSlot.FLOWERBED, TextureSlot.STEM);
      CORAL_FAN = create("coral_fan", TextureSlot.FAN);
      CORAL_WALL_FAN = create("coral_wall_fan", TextureSlot.FAN);
      GLAZED_TERRACOTTA = create("template_glazed_terracotta", TextureSlot.PATTERN);
      CHORUS_FLOWER = create("template_chorus_flower", TextureSlot.TEXTURE);
      DAYLIGHT_DETECTOR = create("template_daylight_detector", TextureSlot.TOP, TextureSlot.SIDE);
      STAINED_GLASS_PANE_NOSIDE = create("template_glass_pane_noside", "_noside", TextureSlot.PANE);
      STAINED_GLASS_PANE_NOSIDE_ALT = create("template_glass_pane_noside_alt", "_noside_alt", TextureSlot.PANE);
      STAINED_GLASS_PANE_POST = create("template_glass_pane_post", "_post", TextureSlot.PANE, TextureSlot.EDGE);
      STAINED_GLASS_PANE_SIDE = create("template_glass_pane_side", "_side", TextureSlot.PANE, TextureSlot.EDGE);
      STAINED_GLASS_PANE_SIDE_ALT = create("template_glass_pane_side_alt", "_side_alt", TextureSlot.PANE, TextureSlot.EDGE);
      COMMAND_BLOCK = create("template_command_block", TextureSlot.FRONT, TextureSlot.BACK, TextureSlot.SIDE);
      CHISELED_BOOKSHELF_SLOT_TOP_LEFT = create("template_chiseled_bookshelf_slot_top_left", "_slot_top_left", TextureSlot.TEXTURE);
      CHISELED_BOOKSHELF_SLOT_TOP_MID = create("template_chiseled_bookshelf_slot_top_mid", "_slot_top_mid", TextureSlot.TEXTURE);
      CHISELED_BOOKSHELF_SLOT_TOP_RIGHT = create("template_chiseled_bookshelf_slot_top_right", "_slot_top_right", TextureSlot.TEXTURE);
      CHISELED_BOOKSHELF_SLOT_BOTTOM_LEFT = create("template_chiseled_bookshelf_slot_bottom_left", "_slot_bottom_left", TextureSlot.TEXTURE);
      CHISELED_BOOKSHELF_SLOT_BOTTOM_MID = create("template_chiseled_bookshelf_slot_bottom_mid", "_slot_bottom_mid", TextureSlot.TEXTURE);
      CHISELED_BOOKSHELF_SLOT_BOTTOM_RIGHT = create("template_chiseled_bookshelf_slot_bottom_right", "_slot_bottom_right", TextureSlot.TEXTURE);
      ANVIL = create("template_anvil", TextureSlot.TOP);
      STEMS = (ModelTemplate[])IntStream.range(0, 8).mapToObj((var0) -> create("stem_growth" + var0, "_stage" + var0, TextureSlot.STEM)).toArray((var0) -> new ModelTemplate[var0]);
      ATTACHED_STEM = create("stem_fruit", TextureSlot.STEM, TextureSlot.UPPER_STEM);
      CROP = create("crop", TextureSlot.CROP);
      FARMLAND = create("template_farmland", TextureSlot.DIRT, TextureSlot.TOP);
      FIRE_FLOOR = create("template_fire_floor", TextureSlot.FIRE);
      FIRE_SIDE = create("template_fire_side", TextureSlot.FIRE);
      FIRE_SIDE_ALT = create("template_fire_side_alt", TextureSlot.FIRE);
      FIRE_UP = create("template_fire_up", TextureSlot.FIRE);
      FIRE_UP_ALT = create("template_fire_up_alt", TextureSlot.FIRE);
      CAMPFIRE = create("template_campfire", TextureSlot.FIRE, TextureSlot.LIT_LOG);
      LANTERN = create("template_lantern", TextureSlot.LANTERN);
      HANGING_LANTERN = create("template_hanging_lantern", "_hanging", TextureSlot.LANTERN);
      TORCH = create("template_torch", TextureSlot.TORCH);
      TORCH_UNLIT = create("template_torch_unlit", TextureSlot.TORCH);
      WALL_TORCH = create("template_torch_wall", TextureSlot.TORCH);
      WALL_TORCH_UNLIT = create("template_torch_wall_unlit", TextureSlot.TORCH);
      REDSTONE_TORCH = create("template_redstone_torch", TextureSlot.TORCH);
      REDSTONE_WALL_TORCH = create("template_redstone_torch_wall", TextureSlot.TORCH);
      PISTON = create("template_piston", TextureSlot.PLATFORM, TextureSlot.BOTTOM, TextureSlot.SIDE);
      PISTON_HEAD = create("template_piston_head", TextureSlot.PLATFORM, TextureSlot.SIDE, TextureSlot.UNSTICKY);
      PISTON_HEAD_SHORT = create("template_piston_head_short", TextureSlot.PLATFORM, TextureSlot.SIDE, TextureSlot.UNSTICKY);
      SEAGRASS = create("template_seagrass", TextureSlot.TEXTURE);
      TURTLE_EGG = create("template_turtle_egg", TextureSlot.ALL);
      TWO_TURTLE_EGGS = create("template_two_turtle_eggs", TextureSlot.ALL);
      THREE_TURTLE_EGGS = create("template_three_turtle_eggs", TextureSlot.ALL);
      FOUR_TURTLE_EGGS = create("template_four_turtle_eggs", TextureSlot.ALL);
      SINGLE_FACE = create("template_single_face", TextureSlot.TEXTURE);
      CAULDRON_LEVEL1 = create("template_cauldron_level1", TextureSlot.CONTENT, TextureSlot.INSIDE, TextureSlot.PARTICLE, TextureSlot.TOP, TextureSlot.BOTTOM, TextureSlot.SIDE);
      CAULDRON_LEVEL2 = create("template_cauldron_level2", TextureSlot.CONTENT, TextureSlot.INSIDE, TextureSlot.PARTICLE, TextureSlot.TOP, TextureSlot.BOTTOM, TextureSlot.SIDE);
      CAULDRON_FULL = create("template_cauldron_full", TextureSlot.CONTENT, TextureSlot.INSIDE, TextureSlot.PARTICLE, TextureSlot.TOP, TextureSlot.BOTTOM, TextureSlot.SIDE);
      AZALEA = create("template_azalea", TextureSlot.TOP, TextureSlot.SIDE);
      POTTED_AZALEA = create("template_potted_azalea_bush", TextureSlot.PLANT, TextureSlot.TOP, TextureSlot.SIDE);
      POTTED_FLOWERING_AZALEA = create("template_potted_azalea_bush", TextureSlot.PLANT, TextureSlot.TOP, TextureSlot.SIDE);
      SNIFFER_EGG = create("sniffer_egg", TextureSlot.TOP, TextureSlot.BOTTOM, TextureSlot.NORTH, TextureSlot.SOUTH, TextureSlot.EAST, TextureSlot.WEST);
      FLAT_ITEM = createItem("generated", TextureSlot.LAYER0);
      MUSIC_DISC = createItem("template_music_disc", TextureSlot.LAYER0);
      FLAT_HANDHELD_ITEM = createItem("handheld", TextureSlot.LAYER0);
      FLAT_HANDHELD_ROD_ITEM = createItem("handheld_rod", TextureSlot.LAYER0);
      TWO_LAYERED_ITEM = createItem("generated", TextureSlot.LAYER0, TextureSlot.LAYER1);
      THREE_LAYERED_ITEM = createItem("generated", TextureSlot.LAYER0, TextureSlot.LAYER1, TextureSlot.LAYER2);
      SHULKER_BOX_INVENTORY = createItem("template_shulker_box", TextureSlot.PARTICLE);
      BED_INVENTORY = createItem("template_bed", TextureSlot.PARTICLE);
      CHEST_INVENTORY = createItem("template_chest", TextureSlot.PARTICLE);
      BUNDLE_OPEN_FRONT_INVENTORY = createItem("template_bundle_open_front", "_open_front", TextureSlot.LAYER0);
      BUNDLE_OPEN_BACK_INVENTORY = createItem("template_bundle_open_back", "_open_back", TextureSlot.LAYER0);
      BOW = createItem("bow", TextureSlot.LAYER0);
      CROSSBOW = createItem("crossbow", TextureSlot.LAYER0);
      CANDLE = create("template_candle", TextureSlot.ALL, TextureSlot.PARTICLE);
      TWO_CANDLES = create("template_two_candles", TextureSlot.ALL, TextureSlot.PARTICLE);
      THREE_CANDLES = create("template_three_candles", TextureSlot.ALL, TextureSlot.PARTICLE);
      FOUR_CANDLES = create("template_four_candles", TextureSlot.ALL, TextureSlot.PARTICLE);
      CANDLE_CAKE = create("template_cake_with_candle", TextureSlot.CANDLE, TextureSlot.BOTTOM, TextureSlot.SIDE, TextureSlot.TOP, TextureSlot.PARTICLE);
      SCULK_SHRIEKER = create("template_sculk_shrieker", TextureSlot.BOTTOM, TextureSlot.SIDE, TextureSlot.TOP, TextureSlot.PARTICLE, TextureSlot.INNER_TOP);
      VAULT = create("template_vault", TextureSlot.TOP, TextureSlot.BOTTOM, TextureSlot.SIDE, TextureSlot.FRONT);
      FLAT_HANDHELD_MACE_ITEM = createItem("handheld_mace", TextureSlot.LAYER0);
   }
}
