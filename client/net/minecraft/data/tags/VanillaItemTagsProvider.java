package net.minecraft.data.tags;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

public class VanillaItemTagsProvider extends IntrinsicHolderTagsProvider<Item> {
   public VanillaItemTagsProvider(final PackOutput output, final CompletableFuture<HolderLookup.Provider> lookupProvider) {
      super(output, Registries.ITEM, lookupProvider, (e) -> e.builtInRegistryHolder().key());
   }

   protected void addTags(final HolderLookup.Provider registries) {
      (new BlockItemTagsProvider() {
         {
            Objects.requireNonNull(VanillaItemTagsProvider.this);
         }

         protected TagAppender<Block, Block> tag(final TagKey<Block> blockTag, final TagKey<Item> itemTag) {
            return new BlockToItemConverter(VanillaItemTagsProvider.this.tag(itemTag));
         }
      }).run();
      this.tag(ItemTags.BANNERS).addAll(Items.BANNER.asList());
      this.tag(ItemTags.BOATS).add(Items.OAK_BOAT, Items.SPRUCE_BOAT, Items.BIRCH_BOAT, Items.JUNGLE_BOAT, Items.ACACIA_BOAT, Items.DARK_OAK_BOAT, Items.PALE_OAK_BOAT, Items.MANGROVE_BOAT, Items.BAMBOO_RAFT, Items.CHERRY_BOAT).addTag(ItemTags.CHEST_BOATS);
      this.tag(ItemTags.BUNDLES).add(Items.BUNDLE).addAll(Items.DYED_BUNDLE.asList());
      this.tag(ItemTags.CHEST_BOATS).add(Items.OAK_CHEST_BOAT, Items.SPRUCE_CHEST_BOAT, Items.BIRCH_CHEST_BOAT, Items.JUNGLE_CHEST_BOAT, Items.ACACIA_CHEST_BOAT, Items.DARK_OAK_CHEST_BOAT, Items.PALE_OAK_CHEST_BOAT, Items.MANGROVE_CHEST_BOAT, Items.BAMBOO_CHEST_RAFT, Items.CHERRY_CHEST_BOAT);
      this.tag(ItemTags.EGGS).add(Items.EGG, Items.BLUE_EGG, Items.BROWN_EGG);
      this.tag(ItemTags.FISHES).add(Items.COD, Items.COOKED_COD, Items.SALMON, Items.COOKED_SALMON, Items.PUFFERFISH, Items.TROPICAL_FISH);
      this.tag(ItemTags.CREEPER_DROP_MUSIC_DISCS).add(Items.MUSIC_DISC_13, Items.MUSIC_DISC_CAT, Items.MUSIC_DISC_BLOCKS, Items.MUSIC_DISC_CHIRP, Items.MUSIC_DISC_FAR, Items.MUSIC_DISC_MALL, Items.MUSIC_DISC_MELLOHI, Items.MUSIC_DISC_STAL, Items.MUSIC_DISC_STRAD, Items.MUSIC_DISC_WARD, Items.MUSIC_DISC_11, Items.MUSIC_DISC_WAIT);
      this.tag(ItemTags.COALS).add(Items.COAL, Items.CHARCOAL);
      this.tag(ItemTags.ARROWS).add(Items.ARROW, Items.TIPPED_ARROW, Items.SPECTRAL_ARROW);
      this.tag(ItemTags.LECTERN_BOOKS).add(Items.WRITTEN_BOOK, Items.WRITABLE_BOOK);
      this.tag(ItemTags.BEACON_PAYMENT_ITEMS).add(Items.NETHERITE_INGOT, Items.EMERALD, Items.DIAMOND, Items.GOLD_INGOT, Items.IRON_INGOT);
      this.tag(ItemTags.PIGLIN_REPELLENTS).add(Items.SOUL_TORCH).add(Items.SOUL_LANTERN).add(Items.SOUL_CAMPFIRE);
      this.tag(ItemTags.PIGLIN_LOVED).addTag(ItemTags.GOLD_ORES).add(Items.GOLD_BLOCK, Items.GILDED_BLACKSTONE, Items.LIGHT_WEIGHTED_PRESSURE_PLATE, Items.GOLD_INGOT, Items.BELL, Items.CLOCK, Items.GOLDEN_CARROT, Items.GLISTERING_MELON_SLICE, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE, Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS, Items.GOLDEN_HORSE_ARMOR, Items.GOLDEN_NAUTILUS_ARMOR, Items.GOLDEN_SWORD, Items.GOLDEN_SPEAR, Items.GOLDEN_PICKAXE, Items.GOLDEN_SHOVEL, Items.GOLDEN_AXE, Items.GOLDEN_HOE, Items.RAW_GOLD, Items.RAW_GOLD_BLOCK, Items.GOLDEN_DANDELION);
      this.tag(ItemTags.IGNORED_BY_PIGLIN_BABIES).add(Items.LEATHER);
      this.tag(ItemTags.PIGLIN_FOOD).add(Items.PORKCHOP, Items.COOKED_PORKCHOP);
      this.tag(ItemTags.PIGLIN_SAFE_ARMOR).add(Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS);
      this.tag(ItemTags.FOX_FOOD).add(Items.SWEET_BERRIES, Items.GLOW_BERRIES);
      this.tag(ItemTags.DUPLICATES_ALLAYS).add(Items.AMETHYST_SHARD);
      this.tag(ItemTags.BREWING_FUEL).add(Items.BLAZE_POWDER);
      this.tag(ItemTags.NON_FLAMMABLE_WOOD).add(Items.WARPED_STEM, Items.STRIPPED_WARPED_STEM, Items.WARPED_HYPHAE, Items.STRIPPED_WARPED_HYPHAE, Items.CRIMSON_STEM, Items.STRIPPED_CRIMSON_STEM, Items.CRIMSON_HYPHAE, Items.STRIPPED_CRIMSON_HYPHAE, Items.CRIMSON_PLANKS, Items.WARPED_PLANKS, Items.CRIMSON_SLAB, Items.WARPED_SLAB, Items.CRIMSON_PRESSURE_PLATE, Items.WARPED_PRESSURE_PLATE, Items.CRIMSON_FENCE, Items.WARPED_FENCE, Items.CRIMSON_TRAPDOOR, Items.WARPED_TRAPDOOR, Items.CRIMSON_FENCE_GATE, Items.WARPED_FENCE_GATE, Items.CRIMSON_STAIRS, Items.WARPED_STAIRS, Items.CRIMSON_BUTTON, Items.WARPED_BUTTON, Items.CRIMSON_DOOR, Items.WARPED_DOOR, Items.CRIMSON_SIGN, Items.WARPED_SIGN, Items.WARPED_HANGING_SIGN, Items.CRIMSON_HANGING_SIGN, Items.WARPED_SHELF, Items.CRIMSON_SHELF);
      this.tag(ItemTags.WOODEN_TOOL_MATERIALS).addTag(ItemTags.PLANKS);
      this.tag(ItemTags.STONE_TOOL_MATERIALS).add(Items.COBBLESTONE, Items.BLACKSTONE, Items.COBBLED_DEEPSLATE);
      this.tag(ItemTags.COPPER_TOOL_MATERIALS).add(Items.COPPER_INGOT);
      this.tag(ItemTags.IRON_TOOL_MATERIALS).add(Items.IRON_INGOT);
      this.tag(ItemTags.GOLD_TOOL_MATERIALS).add(Items.GOLD_INGOT);
      this.tag(ItemTags.DIAMOND_TOOL_MATERIALS).add(Items.DIAMOND);
      this.tag(ItemTags.NETHERITE_TOOL_MATERIALS).add(Items.NETHERITE_INGOT);
      this.tag(ItemTags.REPAIRS_LEATHER_ARMOR).add(Items.LEATHER);
      this.tag(ItemTags.REPAIRS_COPPER_ARMOR).add(Items.COPPER_INGOT);
      this.tag(ItemTags.REPAIRS_CHAIN_ARMOR).add(Items.IRON_INGOT);
      this.tag(ItemTags.REPAIRS_IRON_ARMOR).add(Items.IRON_INGOT);
      this.tag(ItemTags.REPAIRS_GOLD_ARMOR).add(Items.GOLD_INGOT);
      this.tag(ItemTags.REPAIRS_DIAMOND_ARMOR).add(Items.DIAMOND);
      this.tag(ItemTags.REPAIRS_NETHERITE_ARMOR).add(Items.NETHERITE_INGOT);
      this.tag(ItemTags.REPAIRS_TURTLE_HELMET).add(Items.TURTLE_SCUTE);
      this.tag(ItemTags.REPAIRS_WOLF_ARMOR).add(Items.ARMADILLO_SCUTE);
      this.tag(ItemTags.STONE_CRAFTING_MATERIALS).add(Items.COBBLESTONE, Items.BLACKSTONE, Items.COBBLED_DEEPSLATE);
      this.tag(ItemTags.FREEZE_IMMUNE_WEARABLES).add(Items.LEATHER_BOOTS, Items.LEATHER_LEGGINGS, Items.LEATHER_CHESTPLATE, Items.LEATHER_HELMET, Items.LEATHER_HORSE_ARMOR);
      this.tag(ItemTags.AXOLOTL_FOOD).add(Items.TROPICAL_FISH_BUCKET);
      this.tag(ItemTags.CLUSTER_MAX_HARVESTABLES).add(Items.DIAMOND_PICKAXE, Items.GOLDEN_PICKAXE, Items.IRON_PICKAXE, Items.NETHERITE_PICKAXE, Items.STONE_PICKAXE, Items.WOODEN_PICKAXE, Items.COPPER_PICKAXE);
      this.tag(ItemTags.COMPASSES).add(Items.COMPASS).add(Items.RECOVERY_COMPASS);
      this.tag(ItemTags.CREEPER_IGNITERS).add(Items.FLINT_AND_STEEL).add(Items.FIRE_CHARGE);
      this.tag(ItemTags.SWORDS).add(Items.DIAMOND_SWORD).add(Items.STONE_SWORD).add(Items.GOLDEN_SWORD).add(Items.NETHERITE_SWORD).add(Items.WOODEN_SWORD).add(Items.IRON_SWORD).add(Items.COPPER_SWORD);
      this.tag(ItemTags.AXES).add(Items.DIAMOND_AXE).add(Items.STONE_AXE).add(Items.GOLDEN_AXE).add(Items.NETHERITE_AXE).add(Items.WOODEN_AXE).add(Items.IRON_AXE).add(Items.COPPER_AXE);
      this.tag(ItemTags.PICKAXES).add(Items.DIAMOND_PICKAXE).add(Items.STONE_PICKAXE).add(Items.GOLDEN_PICKAXE).add(Items.NETHERITE_PICKAXE).add(Items.WOODEN_PICKAXE).add(Items.IRON_PICKAXE).add(Items.COPPER_PICKAXE);
      this.tag(ItemTags.SHOVELS).add(Items.DIAMOND_SHOVEL).add(Items.STONE_SHOVEL).add(Items.GOLDEN_SHOVEL).add(Items.NETHERITE_SHOVEL).add(Items.WOODEN_SHOVEL).add(Items.IRON_SHOVEL).add(Items.COPPER_SHOVEL);
      this.tag(ItemTags.HOES).add(Items.DIAMOND_HOE).add(Items.STONE_HOE).add(Items.GOLDEN_HOE).add(Items.NETHERITE_HOE).add(Items.WOODEN_HOE).add(Items.IRON_HOE).add(Items.COPPER_HOE);
      this.tag(ItemTags.SPEARS).add(Items.DIAMOND_SPEAR, Items.STONE_SPEAR, Items.GOLDEN_SPEAR, Items.NETHERITE_SPEAR, Items.WOODEN_SPEAR, Items.IRON_SPEAR, Items.COPPER_SPEAR);
      this.tag(ItemTags.BREAKS_DECORATED_POTS).addTag(ItemTags.SWORDS).addTag(ItemTags.AXES).addTag(ItemTags.PICKAXES).addTag(ItemTags.SHOVELS).addTag(ItemTags.HOES).add(Items.TRIDENT).add(Items.MACE);
      this.tag(ItemTags.SKELETON_PREFERRED_WEAPONS).add(Items.BOW);
      this.tag(ItemTags.DROWNED_PREFERRED_WEAPONS).add(Items.TRIDENT);
      this.tag(ItemTags.PIGLIN_PREFERRED_WEAPONS).add(Items.CROSSBOW, Items.GOLDEN_SPEAR);
      this.tag(ItemTags.PILLAGER_PREFERRED_WEAPONS).add(Items.CROSSBOW);
      this.tag(ItemTags.WITHER_SKELETON_DISLIKED_WEAPONS).add(Items.BOW).add(Items.CROSSBOW);
      this.tag(ItemTags.DECORATED_POT_SHERDS).add(Items.ANGLER_POTTERY_SHERD, Items.ARCHER_POTTERY_SHERD, Items.ARMS_UP_POTTERY_SHERD, Items.BLADE_POTTERY_SHERD, Items.BREWER_POTTERY_SHERD, Items.BURN_POTTERY_SHERD, Items.DANGER_POTTERY_SHERD, Items.EXPLORER_POTTERY_SHERD, Items.FRIEND_POTTERY_SHERD, Items.HEART_POTTERY_SHERD, Items.HEARTBREAK_POTTERY_SHERD, Items.HOWL_POTTERY_SHERD, Items.MINER_POTTERY_SHERD, Items.MOURNER_POTTERY_SHERD, Items.PLENTY_POTTERY_SHERD, Items.PRIZE_POTTERY_SHERD, Items.SHEAF_POTTERY_SHERD, Items.SHELTER_POTTERY_SHERD, Items.SKULL_POTTERY_SHERD, Items.SNORT_POTTERY_SHERD, Items.FLOW_POTTERY_SHERD, Items.GUSTER_POTTERY_SHERD, Items.SCRAPE_POTTERY_SHERD);
      this.tag(ItemTags.DECORATED_POT_INGREDIENTS).add(Items.BRICK).addTag(ItemTags.DECORATED_POT_SHERDS);
      this.tag(ItemTags.FOOT_ARMOR).add(Items.LEATHER_BOOTS, Items.COPPER_BOOTS, Items.CHAINMAIL_BOOTS, Items.GOLDEN_BOOTS, Items.IRON_BOOTS, Items.DIAMOND_BOOTS, Items.NETHERITE_BOOTS);
      this.tag(ItemTags.LEG_ARMOR).add(Items.LEATHER_LEGGINGS, Items.COPPER_LEGGINGS, Items.CHAINMAIL_LEGGINGS, Items.GOLDEN_LEGGINGS, Items.IRON_LEGGINGS, Items.DIAMOND_LEGGINGS, Items.NETHERITE_LEGGINGS);
      this.tag(ItemTags.CHEST_ARMOR).add(Items.LEATHER_CHESTPLATE, Items.COPPER_CHESTPLATE, Items.CHAINMAIL_CHESTPLATE, Items.GOLDEN_CHESTPLATE, Items.IRON_CHESTPLATE, Items.DIAMOND_CHESTPLATE, Items.NETHERITE_CHESTPLATE);
      this.tag(ItemTags.HEAD_ARMOR).add(Items.LEATHER_HELMET, Items.COPPER_HELMET, Items.CHAINMAIL_HELMET, Items.GOLDEN_HELMET, Items.IRON_HELMET, Items.DIAMOND_HELMET, Items.NETHERITE_HELMET, Items.TURTLE_HELMET);
      this.tag(ItemTags.SKULLS).add(Items.PLAYER_HEAD, Items.CREEPER_HEAD, Items.ZOMBIE_HEAD, Items.SKELETON_SKULL, Items.WITHER_SKELETON_SKULL, Items.DRAGON_HEAD, Items.PIGLIN_HEAD);
      this.tag(ItemTags.TRIMMABLE_ARMOR).addTag(ItemTags.FOOT_ARMOR).addTag(ItemTags.LEG_ARMOR).addTag(ItemTags.CHEST_ARMOR).addTag(ItemTags.HEAD_ARMOR);
      this.tag(ItemTags.TRIM_MATERIALS).add(Items.AMETHYST_SHARD, Items.COPPER_INGOT, Items.DIAMOND, Items.EMERALD, Items.GOLD_INGOT, Items.IRON_INGOT, Items.LAPIS_LAZULI, Items.NETHERITE_INGOT, Items.QUARTZ, Items.REDSTONE, Items.RESIN_BRICK);
      this.tag(ItemTags.BOOKSHELF_BOOKS).add(Items.BOOK, Items.WRITTEN_BOOK, Items.ENCHANTED_BOOK, Items.WRITABLE_BOOK, Items.KNOWLEDGE_BOOK);
      this.tag(ItemTags.NOTE_BLOCK_TOP_INSTRUMENTS).add(Items.ZOMBIE_HEAD, Items.SKELETON_SKULL, Items.CREEPER_HEAD, Items.DRAGON_HEAD, Items.WITHER_SKELETON_SKULL, Items.PIGLIN_HEAD, Items.PLAYER_HEAD);
      this.tag(ItemTags.SNIFFER_FOOD).add(Items.TORCHFLOWER_SEEDS);
      this.tag(ItemTags.VILLAGER_PLANTABLE_SEEDS).add(Items.WHEAT_SEEDS, Items.POTATO, Items.CARROT, Items.BEETROOT_SEEDS, Items.TORCHFLOWER_SEEDS, Items.PITCHER_POD);
      this.tag(ItemTags.VILLAGER_PICKS_UP).addTag(ItemTags.VILLAGER_PLANTABLE_SEEDS).add(Items.BREAD, Items.WHEAT, Items.BEETROOT);
      this.tag(ItemTags.BOOK_CLONING_TARGET).add(Items.WRITABLE_BOOK);
      this.tag(ItemTags.FOOT_ARMOR_ENCHANTABLE).addTag(ItemTags.FOOT_ARMOR);
      this.tag(ItemTags.LEG_ARMOR_ENCHANTABLE).addTag(ItemTags.LEG_ARMOR);
      this.tag(ItemTags.CHEST_ARMOR_ENCHANTABLE).addTag(ItemTags.CHEST_ARMOR);
      this.tag(ItemTags.HEAD_ARMOR_ENCHANTABLE).addTag(ItemTags.HEAD_ARMOR);
      this.tag(ItemTags.ARMOR_ENCHANTABLE).addTag(ItemTags.FOOT_ARMOR_ENCHANTABLE).addTag(ItemTags.LEG_ARMOR_ENCHANTABLE).addTag(ItemTags.CHEST_ARMOR_ENCHANTABLE).addTag(ItemTags.HEAD_ARMOR_ENCHANTABLE);
      this.tag(ItemTags.SWEEPING_ENCHANTABLE).addTag(ItemTags.SWORDS);
      this.tag(ItemTags.MELEE_WEAPON_ENCHANTABLE).addTag(ItemTags.SWORDS).addTag(ItemTags.SPEARS);
      this.tag(ItemTags.FIRE_ASPECT_ENCHANTABLE).addTag(ItemTags.MELEE_WEAPON_ENCHANTABLE).add(Items.MACE);
      this.tag(ItemTags.SHARP_WEAPON_ENCHANTABLE).addTag(ItemTags.MELEE_WEAPON_ENCHANTABLE).addTag(ItemTags.AXES);
      this.tag(ItemTags.WEAPON_ENCHANTABLE).addTag(ItemTags.SHARP_WEAPON_ENCHANTABLE).add(Items.MACE);
      this.tag(ItemTags.MACE_ENCHANTABLE).add(Items.MACE);
      this.tag(ItemTags.MINING_ENCHANTABLE).addTag(ItemTags.AXES).addTag(ItemTags.PICKAXES).addTag(ItemTags.SHOVELS).addTag(ItemTags.HOES).add(Items.SHEARS);
      this.tag(ItemTags.MINING_LOOT_ENCHANTABLE).addTag(ItemTags.AXES).addTag(ItemTags.PICKAXES).addTag(ItemTags.SHOVELS).addTag(ItemTags.HOES);
      this.tag(ItemTags.FISHING_ENCHANTABLE).add(Items.FISHING_ROD);
      this.tag(ItemTags.TRIDENT_ENCHANTABLE).add(Items.TRIDENT);
      this.tag(ItemTags.LUNGE_ENCHANTABLE).addTag(ItemTags.SPEARS);
      this.tag(ItemTags.DURABILITY_ENCHANTABLE).addTag(ItemTags.FOOT_ARMOR).addTag(ItemTags.LEG_ARMOR).addTag(ItemTags.CHEST_ARMOR).addTag(ItemTags.HEAD_ARMOR).add(Items.ELYTRA).add(Items.SHIELD).addTag(ItemTags.SWORDS).addTag(ItemTags.AXES).addTag(ItemTags.PICKAXES).addTag(ItemTags.SHOVELS).addTag(ItemTags.HOES).add(Items.BOW).add(Items.CROSSBOW).add(Items.TRIDENT).add(Items.FLINT_AND_STEEL).add(Items.SHEARS).add(Items.BRUSH).add(Items.FISHING_ROD).add(Items.CARROT_ON_A_STICK, Items.WARPED_FUNGUS_ON_A_STICK).add(Items.MACE).addTag(ItemTags.SPEARS);
      this.tag(ItemTags.BOW_ENCHANTABLE).add(Items.BOW);
      this.tag(ItemTags.EQUIPPABLE_ENCHANTABLE).addTag(ItemTags.FOOT_ARMOR).addTag(ItemTags.LEG_ARMOR).addTag(ItemTags.CHEST_ARMOR).addTag(ItemTags.HEAD_ARMOR).add(Items.ELYTRA).addTag(ItemTags.SKULLS).add(Items.CARVED_PUMPKIN);
      this.tag(ItemTags.CROSSBOW_ENCHANTABLE).add(Items.CROSSBOW);
      this.tag(ItemTags.VANISHING_ENCHANTABLE).addTag(ItemTags.DURABILITY_ENCHANTABLE).add(Items.COMPASS).add(Items.CARVED_PUMPKIN).addTag(ItemTags.SKULLS);
      this.tag(ItemTags.DYES).addAll(Items.DYE.asList());
      this.tag(ItemTags.CAULDRON_CAN_REMOVE_DYE).add(Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS, Items.LEATHER_HORSE_ARMOR, Items.WOLF_ARMOR);
      this.tag(ItemTags.FURNACE_MINECART_FUEL).add(Items.COAL, Items.CHARCOAL);
      this.tag(ItemTags.MEAT).add(Items.BEEF, Items.CHICKEN, Items.COOKED_BEEF, Items.COOKED_CHICKEN, Items.COOKED_MUTTON, Items.COOKED_PORKCHOP, Items.COOKED_RABBIT, Items.MUTTON, Items.PORKCHOP, Items.RABBIT, Items.ROTTEN_FLESH);
      this.tag(ItemTags.WOLF_FOOD).addTag(ItemTags.MEAT).add(Items.COD, Items.COOKED_COD, Items.SALMON, Items.COOKED_SALMON, Items.TROPICAL_FISH, Items.PUFFERFISH, Items.RABBIT_STEW);
      this.tag(ItemTags.OCELOT_FOOD).add(Items.COD, Items.SALMON);
      this.tag(ItemTags.CAT_FOOD).add(Items.COD, Items.SALMON);
      this.tag(ItemTags.HORSE_FOOD).add(Items.WHEAT, Items.SUGAR, Items.HAY_BLOCK, Items.APPLE, Items.CARROT, Items.GOLDEN_CARROT, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE);
      this.tag(ItemTags.ZOMBIE_HORSE_FOOD).add(Items.RED_MUSHROOM);
      this.tag(ItemTags.HORSE_TEMPT_ITEMS).add(Items.GOLDEN_CARROT, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE);
      this.tag(ItemTags.HARNESSES).addAll(Items.HARNESS.asList());
      this.tag(ItemTags.HAPPY_GHAST_FOOD).add(Items.SNOWBALL);
      this.tag(ItemTags.HAPPY_GHAST_TEMPT_ITEMS).addTag(ItemTags.HAPPY_GHAST_FOOD).addTag(ItemTags.HARNESSES);
      this.tag(ItemTags.CAMEL_FOOD).add(Items.CACTUS);
      this.tag(ItemTags.CAMEL_HUSK_FOOD).add(Items.RABBIT_FOOT);
      this.tag(ItemTags.ARMADILLO_FOOD).add(Items.SPIDER_EYE);
      this.tag(ItemTags.CHICKEN_FOOD).add(Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEEDS, Items.TORCHFLOWER_SEEDS, Items.PITCHER_POD);
      this.tag(ItemTags.FROG_FOOD).add(Items.SLIME_BALL);
      this.tag(ItemTags.HOGLIN_FOOD).add(Items.CRIMSON_FUNGUS);
      this.tag(ItemTags.LLAMA_FOOD).add(Items.WHEAT, Items.HAY_BLOCK);
      this.tag(ItemTags.LLAMA_TEMPT_ITEMS).add(Items.HAY_BLOCK);
      this.tag(ItemTags.NAUTILUS_TAMING_ITEMS).add(Items.PUFFERFISH_BUCKET, Items.PUFFERFISH);
      this.tag(ItemTags.NAUTILUS_BUCKET_FOOD).add(Items.PUFFERFISH_BUCKET, Items.COD_BUCKET, Items.SALMON_BUCKET, Items.TROPICAL_FISH_BUCKET);
      this.tag(ItemTags.NAUTILUS_FOOD).addTag(ItemTags.FISHES).addTag(ItemTags.NAUTILUS_BUCKET_FOOD);
      this.tag(ItemTags.PANDA_FOOD).add(Items.BAMBOO);
      this.tag(ItemTags.PANDA_EATS_FROM_GROUND).addTag(ItemTags.PANDA_FOOD).add(Items.CAKE);
      this.tag(ItemTags.PIG_FOOD).add(Items.CARROT, Items.POTATO, Items.BEETROOT);
      this.tag(ItemTags.RABBIT_FOOD).add(Items.CARROT, Items.GOLDEN_CARROT, Items.DANDELION);
      this.tag(ItemTags.STRIDER_FOOD).add(Items.WARPED_FUNGUS);
      this.tag(ItemTags.STRIDER_TEMPT_ITEMS).addTag(ItemTags.STRIDER_FOOD).add(Items.WARPED_FUNGUS_ON_A_STICK);
      this.tag(ItemTags.TURTLE_FOOD).add(Items.SEAGRASS);
      this.tag(ItemTags.PARROT_FOOD).add(Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEEDS, Items.TORCHFLOWER_SEEDS, Items.PITCHER_POD);
      this.tag(ItemTags.PARROT_POISONOUS_FOOD).add(Items.COOKIE);
      this.tag(ItemTags.COW_FOOD).add(Items.WHEAT);
      this.tag(ItemTags.SHEEP_FOOD).add(Items.WHEAT);
      this.tag(ItemTags.SULFUR_CUBE_FOOD).add(Items.SLIME_BALL);
      this.tag(ItemTags.GOAT_FOOD).add(Items.WHEAT);
      this.tag(ItemTags.MAP_INVISIBILITY_EQUIPMENT).add(Items.CARVED_PUMPKIN);
      this.tag(ItemTags.GAZE_DISGUISE_EQUIPMENT).add(Items.CARVED_PUMPKIN);
      this.tag(ItemTags.SHEARABLE_FROM_COPPER_GOLEM).add(Items.POPPY);
      this.tag(ItemTags.METAL_NUGGETS).add(Items.COPPER_NUGGET, Items.IRON_NUGGET, Items.GOLD_NUGGET);
      this.tag(ItemTags.SULFUR_CUBE_ARCHETYPE_BOUNCY).addTag(ItemTags.PLANKS).add(Items.BAMBOO_MOSAIC).addTag(ItemTags.LOGS).addTag(ItemTags.BAMBOO_BLOCKS);
      this.tag(ItemTags.SULFUR_CUBE_ARCHETYPE_REGULAR).add(Items.DEEPSLATE, Items.CHISELED_DEEPSLATE, Items.POLISHED_DEEPSLATE, Items.COBBLED_DEEPSLATE, Items.CRACKED_DEEPSLATE_BRICKS, Items.CRACKED_DEEPSLATE_TILES, Items.DEEPSLATE_BRICKS, Items.DEEPSLATE_TILES).add(Items.NETHER_BRICKS, Items.RED_NETHER_BRICKS, Items.CHISELED_NETHER_BRICKS, Items.CRACKED_NETHER_BRICKS).add(Items.BLACKSTONE, Items.GILDED_BLACKSTONE, Items.POLISHED_BLACKSTONE, Items.CHISELED_POLISHED_BLACKSTONE, Items.CRACKED_POLISHED_BLACKSTONE_BRICKS, Items.POLISHED_BLACKSTONE_BRICKS).add(Items.QUARTZ_BLOCK, Items.SMOOTH_QUARTZ, Items.CHISELED_QUARTZ_BLOCK, Items.QUARTZ_BRICKS, Items.QUARTZ_PILLAR).add(Items.SANDSTONE, Items.RED_SANDSTONE, Items.CHISELED_RED_SANDSTONE, Items.CHISELED_SANDSTONE, Items.CUT_RED_SANDSTONE, Items.CUT_SANDSTONE, Items.SMOOTH_RED_SANDSTONE, Items.SMOOTH_SANDSTONE).add(Items.STONE, Items.COBBLESTONE, Items.SMOOTH_STONE, Items.MOSSY_COBBLESTONE, Items.MOSSY_STONE_BRICKS, Items.CRACKED_STONE_BRICKS, Items.STONE_BRICKS, Items.CHISELED_STONE_BRICKS).add(Items.TUFF, Items.TUFF_BRICKS, Items.CHISELED_TUFF, Items.CHISELED_TUFF_BRICKS, Items.POLISHED_TUFF).add(Items.CINNABAR, Items.CHISELED_CINNABAR, Items.POLISHED_CINNABAR, Items.CINNABAR_BRICKS).add(Items.SULFUR, Items.POLISHED_SULFUR, Items.CHISELED_SULFUR, Items.SULFUR_BRICKS).add(Items.BASALT, Items.POLISHED_BASALT, Items.SMOOTH_BASALT).add(Items.DIORITE, Items.POLISHED_DIORITE, Items.GRANITE, Items.POLISHED_GRANITE, Items.ANDESITE, Items.POLISHED_ANDESITE, Items.CALCITE, Items.DRIPSTONE_BLOCK).add(Items.MUD, Items.MUDDY_MANGROVE_ROOTS, Items.MUD_BRICKS, Items.PACKED_MUD).addTag(ItemTags.TERRACOTTA).addTag(ItemTags.GLAZED_TERRACOTTA).addTag(ItemTags.CONCRETE).addTag(ItemTags.CONCRETE_POWDERS).addTag(ItemTags.COAL_ORES).addTag(ItemTags.LAPIS_ORES).addTag(ItemTags.REDSTONE_ORES).addTag(ItemTags.DIAMOND_ORES).addTag(ItemTags.EMERALD_ORES).add(Items.COAL_BLOCK, Items.LAPIS_BLOCK, Items.REDSTONE_BLOCK, Items.DIAMOND_BLOCK, Items.EMERALD_BLOCK).add(Items.NETHERRACK, Items.NETHER_QUARTZ_ORE, Items.CRIMSON_NYLIUM, Items.WARPED_NYLIUM, Items.GLOWSTONE).add(Items.PRISMARINE, Items.PRISMARINE_BRICKS, Items.DARK_PRISMARINE, Items.SEA_LANTERN).add(Items.END_STONE, Items.END_STONE_BRICKS, Items.PURPUR_BLOCK, Items.PURPUR_PILLAR).add(Items.DIRT, Items.COARSE_DIRT, Items.ROOTED_DIRT, Items.PODZOL, Items.GRASS_BLOCK, Items.CLAY).add(Items.GRAVEL, Items.SAND, Items.RED_SAND).add(Items.OBSIDIAN, Items.CRYING_OBSIDIAN, Items.BONE_BLOCK, Items.AMETHYST_BLOCK, Items.BRICKS, Items.MAGMA_BLOCK);
      this.tag(ItemTags.SULFUR_CUBE_ARCHETYPE_SLOW_FLAT).add(Items.IRON_BLOCK, Items.GOLD_BLOCK, Items.RAW_COPPER_BLOCK, Items.RAW_GOLD_BLOCK, Items.RAW_IRON_BLOCK).addTag(ItemTags.GOLD_ORES).addTag(ItemTags.IRON_ORES).addTag(ItemTags.COPPER_ORES).add(Items.NETHERITE_BLOCK, Items.ANCIENT_DEBRIS).addAll(Items.COPPER_BLOCK.asList()).addAll(Items.COPPER_BULB.asList()).addAll(Items.CUT_COPPER.asList()).addAll(Items.CHISELED_COPPER.asList());
      this.tag(ItemTags.SULFUR_CUBE_ARCHETYPE_FAST_FLAT).add(Items.TUBE_CORAL_BLOCK, Items.BRAIN_CORAL_BLOCK, Items.BUBBLE_CORAL_BLOCK, Items.FIRE_CORAL_BLOCK, Items.HORN_CORAL_BLOCK).add(Items.DEAD_TUBE_CORAL_BLOCK, Items.DEAD_BRAIN_CORAL_BLOCK, Items.DEAD_BUBBLE_CORAL_BLOCK, Items.DEAD_FIRE_CORAL_BLOCK, Items.DEAD_HORN_CORAL_BLOCK).add(Items.SPONGE, Items.WET_SPONGE, Items.DRIED_KELP_BLOCK).addTag(ItemTags.MOSS_BLOCKS).add(Items.RESIN_BLOCK, Items.RESIN_BRICKS, Items.CHISELED_RESIN_BRICKS).add(Items.PUMPKIN, Items.MELON, Items.HAY_BLOCK, Items.PUMPKIN, Items.CARVED_PUMPKIN, Items.JACK_O_LANTERN).add(Items.OCHRE_FROGLIGHT, Items.PEARLESCENT_FROGLIGHT, Items.VERDANT_FROGLIGHT);
      this.tag(ItemTags.SULFUR_CUBE_ARCHETYPE_LIGHT).addTag(ItemTags.WOOL);
      this.tag(ItemTags.SULFUR_CUBE_ARCHETYPE_FAST_SLIDING).add(Items.BLUE_ICE, Items.PACKED_ICE, Items.SNOW_BLOCK);
      this.tag(ItemTags.SULFUR_CUBE_ARCHETYPE_SLOW_SLIDING).add(Items.BROWN_MUSHROOM_BLOCK, Items.RED_MUSHROOM_BLOCK, Items.MUSHROOM_STEM, Items.MYCELIUM).addTag(ItemTags.WART_BLOCKS).add(Items.SHROOMLIGHT);
      this.tag(ItemTags.SULFUR_CUBE_ARCHETYPE_STICKY).add(Items.HONEYCOMB_BLOCK);
      this.tag(ItemTags.SULFUR_CUBE_ARCHETYPE_HIGH_RESISTANCE).add(Items.SOUL_SAND, Items.SOUL_SOIL);
      this.tag(ItemTags.SULFUR_CUBE_SWALLOWABLE).addTag(ItemTags.SULFUR_CUBE_ARCHETYPE_BOUNCY).addTag(ItemTags.SULFUR_CUBE_ARCHETYPE_REGULAR).addTag(ItemTags.SULFUR_CUBE_ARCHETYPE_SLOW_FLAT).addTag(ItemTags.SULFUR_CUBE_ARCHETYPE_FAST_FLAT).addTag(ItemTags.SULFUR_CUBE_ARCHETYPE_LIGHT).addTag(ItemTags.SULFUR_CUBE_ARCHETYPE_FAST_SLIDING).addTag(ItemTags.SULFUR_CUBE_ARCHETYPE_SLOW_SLIDING).addTag(ItemTags.SULFUR_CUBE_ARCHETYPE_STICKY).addTag(ItemTags.SULFUR_CUBE_ARCHETYPE_HIGH_RESISTANCE);
      this.tag(ItemTags.LOOM_DYES).addTag(ItemTags.DYES);
      this.tag(ItemTags.LOOM_PATTERNS).add(Items.FLOWER_BANNER_PATTERN, Items.CREEPER_BANNER_PATTERN, Items.SKULL_BANNER_PATTERN, Items.MOJANG_BANNER_PATTERN, Items.GLOBE_BANNER_PATTERN, Items.PIGLIN_BANNER_PATTERN, Items.FLOW_BANNER_PATTERN, Items.GUSTER_BANNER_PATTERN, Items.FIELD_MASONED_BANNER_PATTERN, Items.BORDURE_INDENTED_BANNER_PATTERN);
      this.tag(ItemTags.CAT_COLLAR_DYES).addTag(ItemTags.DYES);
      this.tag(ItemTags.WOLF_COLLAR_DYES).addTag(ItemTags.DYES);
   }

   private static class BlockToItemConverter implements TagAppender<Block, Block> {
      private final TagAppender<Item, Item> itemAppender;

      public BlockToItemConverter(final TagAppender<Item, Item> itemAppender) {
         super();
         this.itemAppender = itemAppender;
      }

      public TagAppender<Block, Block> add(final Block element) {
         this.itemAppender.add((Item)Objects.requireNonNull(element.asItem()));
         return this;
      }

      public TagAppender<Block, Block> addOptional(final Block element) {
         this.itemAppender.addOptional((Item)Objects.requireNonNull(element.asItem()));
         return this;
      }

      private static TagKey<Item> blockTagToItemTag(final TagKey<Block> blockTag) {
         return TagKey.<Item>create(Registries.ITEM, blockTag.location());
      }

      public TagAppender<Block, Block> addTag(final TagKey<Block> tag) {
         this.itemAppender.addTag(blockTagToItemTag(tag));
         return this;
      }

      public TagAppender<Block, Block> addOptionalTag(final TagKey<Block> tag) {
         this.itemAppender.addOptionalTag(blockTagToItemTag(tag));
         return this;
      }
   }
}
