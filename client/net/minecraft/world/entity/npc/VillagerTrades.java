package net.minecraft.world.entity.npc;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.StructureTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.providers.EnchantmentProvider;
import net.minecraft.world.item.enchantment.providers.TradeRebalanceEnchantmentProviders;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.apache.commons.lang3.tuple.Pair;

public class VillagerTrades {
   private static final int DEFAULT_SUPPLY = 12;
   private static final int COMMON_ITEMS_SUPPLY = 16;
   private static final int UNCOMMON_ITEMS_SUPPLY = 3;
   private static final int XP_LEVEL_1_SELL = 1;
   private static final int XP_LEVEL_1_BUY = 2;
   private static final int XP_LEVEL_2_SELL = 5;
   private static final int XP_LEVEL_2_BUY = 10;
   private static final int XP_LEVEL_3_SELL = 10;
   private static final int XP_LEVEL_3_BUY = 20;
   private static final int XP_LEVEL_4_SELL = 15;
   private static final int XP_LEVEL_4_BUY = 30;
   private static final int XP_LEVEL_5_TRADE = 30;
   private static final float LOW_TIER_PRICE_MULTIPLIER = 0.05F;
   private static final float HIGH_TIER_PRICE_MULTIPLIER = 0.2F;
   public static final Map<VillagerProfession, Int2ObjectMap<VillagerTrades.ItemListing[]>> TRADES = Util.make(
      Maps.newHashMap(),
      var0 -> {
         var0.put(
            VillagerProfession.FARMER,
            toIntMap(
               ImmutableMap.of(
                  1,
                  new VillagerTrades.ItemListing[]{
                     new VillagerTrades.EmeraldForItems(Items.WHEAT, 20, 16, 2),
                     new VillagerTrades.EmeraldForItems(Items.POTATO, 26, 16, 2),
                     new VillagerTrades.EmeraldForItems(Items.CARROT, 22, 16, 2),
                     new VillagerTrades.EmeraldForItems(Items.BEETROOT, 15, 16, 2),
                     new VillagerTrades.ItemsForEmeralds(Items.BREAD, 1, 6, 16, 1)
                  },
                  2,
                  new VillagerTrades.ItemListing[]{
                     new VillagerTrades.EmeraldForItems(Blocks.PUMPKIN, 6, 12, 10),
                     new VillagerTrades.ItemsForEmeralds(Items.PUMPKIN_PIE, 1, 4, 5),
                     new VillagerTrades.ItemsForEmeralds(Items.APPLE, 1, 4, 16, 5)
                  },
                  3,
                  new VillagerTrades.ItemListing[]{
                     new VillagerTrades.ItemsForEmeralds(Items.COOKIE, 3, 18, 10), new VillagerTrades.EmeraldForItems(Blocks.MELON, 4, 12, 20)
                  },
                  4,
                  new VillagerTrades.ItemListing[]{
                     new VillagerTrades.ItemsForEmeralds(Blocks.CAKE, 1, 1, 12, 15),
                     new VillagerTrades.SuspiciousStewForEmerald(MobEffects.NIGHT_VISION, 100, 15),
                     new VillagerTrades.SuspiciousStewForEmerald(MobEffects.JUMP, 160, 15),
                     new VillagerTrades.SuspiciousStewForEmerald(MobEffects.WEAKNESS, 140, 15),
                     new VillagerTrades.SuspiciousStewForEmerald(MobEffects.BLINDNESS, 120, 15),
                     new VillagerTrades.SuspiciousStewForEmerald(MobEffects.POISON, 280, 15),
                     new VillagerTrades.SuspiciousStewForEmerald(MobEffects.SATURATION, 7, 15)
                  },
                  5,
                  new VillagerTrades.ItemListing[]{
                     new VillagerTrades.ItemsForEmeralds(Items.GOLDEN_CARROT, 3, 3, 30),
                     new VillagerTrades.ItemsForEmeralds(Items.GLISTERING_MELON_SLICE, 4, 3, 30)
                  }
               )
            )
         );
         var0.put(
            VillagerProfession.FISHERMAN,
            toIntMap(
               ImmutableMap.of(
                  1,
                  new VillagerTrades.ItemListing[]{
                     new VillagerTrades.EmeraldForItems(Items.STRING, 20, 16, 2),
                     new VillagerTrades.EmeraldForItems(Items.COAL, 10, 16, 2),
                     new VillagerTrades.ItemsAndEmeraldsToItems(Items.COD, 6, 1, Items.COOKED_COD, 6, 16, 1, 0.05F),
                     new VillagerTrades.ItemsForEmeralds(Items.COD_BUCKET, 3, 1, 16, 1)
                  },
                  2,
                  new VillagerTrades.ItemListing[]{
                     new VillagerTrades.EmeraldForItems(Items.COD, 15, 16, 10),
                     new VillagerTrades.ItemsAndEmeraldsToItems(Items.SALMON, 6, 1, Items.COOKED_SALMON, 6, 16, 5, 0.05F),
                     new VillagerTrades.ItemsForEmeralds(Items.CAMPFIRE, 2, 1, 5)
                  },
                  3,
                  new VillagerTrades.ItemListing[]{
                     new VillagerTrades.EmeraldForItems(Items.SALMON, 13, 16, 20),
                     new VillagerTrades.EnchantedItemForEmeralds(Items.FISHING_ROD, 3, 3, 10, 0.2F)
                  },
                  4,
                  new VillagerTrades.ItemListing[]{new VillagerTrades.EmeraldForItems(Items.TROPICAL_FISH, 6, 12, 30)},
                  5,
                  new VillagerTrades.ItemListing[]{
                     new VillagerTrades.EmeraldForItems(Items.PUFFERFISH, 4, 12, 30),
                     new VillagerTrades.EmeraldsForVillagerTypeItem(
                        1,
                        12,
                        30,
                        ImmutableMap.builder()
                           .put(VillagerType.PLAINS, Items.OAK_BOAT)
                           .put(VillagerType.TAIGA, Items.SPRUCE_BOAT)
                           .put(VillagerType.SNOW, Items.SPRUCE_BOAT)
                           .put(VillagerType.DESERT, Items.JUNGLE_BOAT)
                           .put(VillagerType.JUNGLE, Items.JUNGLE_BOAT)
                           .put(VillagerType.SAVANNA, Items.ACACIA_BOAT)
                           .put(VillagerType.SWAMP, Items.DARK_OAK_BOAT)
                           .build()
                     )
                  }
               )
            )
         );
         var0.put(
            VillagerProfession.SHEPHERD,
            toIntMap(
               ImmutableMap.of(
                  1,
                  new VillagerTrades.ItemListing[]{
                     new VillagerTrades.EmeraldForItems(Blocks.WHITE_WOOL, 18, 16, 2),
                     new VillagerTrades.EmeraldForItems(Blocks.BROWN_WOOL, 18, 16, 2),
                     new VillagerTrades.EmeraldForItems(Blocks.BLACK_WOOL, 18, 16, 2),
                     new VillagerTrades.EmeraldForItems(Blocks.GRAY_WOOL, 18, 16, 2),
                     new VillagerTrades.ItemsForEmeralds(Items.SHEARS, 2, 1, 1)
                  },
                  2,
                  new VillagerTrades.ItemListing[]{
                     new VillagerTrades.EmeraldForItems(Items.WHITE_DYE, 12, 16, 10),
                     new VillagerTrades.EmeraldForItems(Items.GRAY_DYE, 12, 16, 10),
                     new VillagerTrades.EmeraldForItems(Items.BLACK_DYE, 12, 16, 10),
                     new VillagerTrades.EmeraldForItems(Items.LIGHT_BLUE_DYE, 12, 16, 10),
                     new VillagerTrades.EmeraldForItems(Items.LIME_DYE, 12, 16, 10),
                     new VillagerTrades.ItemsForEmeralds(Blocks.WHITE_WOOL, 1, 1, 16, 5),
                     new VillagerTrades.ItemsForEmeralds(Blocks.ORANGE_WOOL, 1, 1, 16, 5),
                     new VillagerTrades.ItemsForEmeralds(Blocks.MAGENTA_WOOL, 1, 1, 16, 5),
                     new VillagerTrades.ItemsForEmeralds(Blocks.LIGHT_BLUE_WOOL, 1, 1, 16, 5),
                     new VillagerTrades.ItemsForEmeralds(Blocks.YELLOW_WOOL, 1, 1, 16, 5),
                     new VillagerTrades.ItemsForEmeralds(Blocks.LIME_WOOL, 1, 1, 16, 5),
                     new VillagerTrades.ItemsForEmeralds(Blocks.PINK_WOOL, 1, 1, 16, 5),
                     new VillagerTrades.ItemsForEmeralds(Blocks.GRAY_WOOL, 1, 1, 16, 5),
                     new VillagerTrades.ItemsForEmeralds(Blocks.LIGHT_GRAY_WOOL, 1, 1, 16, 5),
                     new VillagerTrades.ItemsForEmeralds(Blocks.CYAN_WOOL, 1, 1, 16, 5),
                     new VillagerTrades.ItemsForEmeralds(Blocks.PURPLE_WOOL, 1, 1, 16, 5),
                     new VillagerTrades.ItemsForEmeralds(Blocks.BLUE_WOOL, 1, 1, 16, 5),
                     new VillagerTrades.ItemsForEmeralds(Blocks.BROWN_WOOL, 1, 1, 16, 5),
                     new VillagerTrades.ItemsForEmeralds(Blocks.GREEN_WOOL, 1, 1, 16, 5),
                     new VillagerTrades.ItemsForEmeralds(Blocks.RED_WOOL, 1, 1, 16, 5),
                     new VillagerTrades.ItemsForEmeralds(Blocks.BLACK_WOOL, 1, 1, 16, 5),
                     new VillagerTrades.ItemsForEmeralds(Blocks.WHITE_CARPET, 1, 4, 16, 5),
                     new VillagerTrades.ItemsForEmeralds(Blocks.ORANGE_CARPET, 1, 4, 16, 5),
                     new VillagerTrades.ItemsForEmeralds(Blocks.MAGENTA_CARPET, 1, 4, 16, 5),
                     new VillagerTrades.ItemsForEmeralds(Blocks.LIGHT_BLUE_CARPET, 1, 4, 16, 5),
                     new VillagerTrades.ItemsForEmeralds(Blocks.YELLOW_CARPET, 1, 4, 16, 5),
                     new VillagerTrades.ItemsForEmeralds(Blocks.LIME_CARPET, 1, 4, 16, 5),
                     new VillagerTrades.ItemsForEmeralds(Blocks.PINK_CARPET, 1, 4, 16, 5),
                     new VillagerTrades.ItemsForEmeralds(Blocks.GRAY_CARPET, 1, 4, 16, 5),
                     new VillagerTrades.ItemsForEmeralds(Blocks.LIGHT_GRAY_CARPET, 1, 4, 16, 5),
                     new VillagerTrades.ItemsForEmeralds(Blocks.CYAN_CARPET, 1, 4, 16, 5),
                     new VillagerTrades.ItemsForEmeralds(Blocks.PURPLE_CARPET, 1, 4, 16, 5),
                     new VillagerTrades.ItemsForEmeralds(Blocks.BLUE_CARPET, 1, 4, 16, 5),
                     new VillagerTrades.ItemsForEmeralds(Blocks.BROWN_CARPET, 1, 4, 16, 5),
                     new VillagerTrades.ItemsForEmeralds(Blocks.GREEN_CARPET, 1, 4, 16, 5),
                     new VillagerTrades.ItemsForEmeralds(Blocks.RED_CARPET, 1, 4, 16, 5),
                     new VillagerTrades.ItemsForEmeralds(Blocks.BLACK_CARPET, 1, 4, 16, 5)
                  },
                  3,
                  new VillagerTrades.ItemListing[]{
                     new VillagerTrades.EmeraldForItems(Items.YELLOW_DYE, 12, 16, 20),
                     new VillagerTrades.EmeraldForItems(Items.LIGHT_GRAY_DYE, 12, 16, 20),
                     new VillagerTrades.EmeraldForItems(Items.ORANGE_DYE, 12, 16, 20),
                     new VillagerTrades.EmeraldForItems(Items.RED_DYE, 12, 16, 20),
                     new VillagerTrades.EmeraldForItems(Items.PINK_DYE, 12, 16, 20),
                     new VillagerTrades.ItemsForEmeralds(Blocks.WHITE_BED, 3, 1, 12, 10),
                     new VillagerTrades.ItemsForEmeralds(Blocks.YELLOW_BED, 3, 1, 12, 10),
                     new VillagerTrades.ItemsForEmeralds(Blocks.RED_BED, 3, 1, 12, 10),
                     new VillagerTrades.ItemsForEmeralds(Blocks.BLACK_BED, 3, 1, 12, 10),
                     new VillagerTrades.ItemsForEmeralds(Blocks.BLUE_BED, 3, 1, 12, 10),
                     new VillagerTrades.ItemsForEmeralds(Blocks.BROWN_BED, 3, 1, 12, 10),
                     new VillagerTrades.ItemsForEmeralds(Blocks.CYAN_BED, 3, 1, 12, 10),
                     new VillagerTrades.ItemsForEmeralds(Blocks.GRAY_BED, 3, 1, 12, 10),
                     new VillagerTrades.ItemsForEmeralds(Blocks.GREEN_BED, 3, 1, 12, 10),
                     new VillagerTrades.ItemsForEmeralds(Blocks.LIGHT_BLUE_BED, 3, 1, 12, 10),
                     new VillagerTrades.ItemsForEmeralds(Blocks.LIGHT_GRAY_BED, 3, 1, 12, 10),
                     new VillagerTrades.ItemsForEmeralds(Blocks.LIME_BED, 3, 1, 12, 10),
                     new VillagerTrades.ItemsForEmeralds(Blocks.MAGENTA_BED, 3, 1, 12, 10),
                     new VillagerTrades.ItemsForEmeralds(Blocks.ORANGE_BED, 3, 1, 12, 10),
                     new VillagerTrades.ItemsForEmeralds(Blocks.PINK_BED, 3, 1, 12, 10),
                     new VillagerTrades.ItemsForEmeralds(Blocks.PURPLE_BED, 3, 1, 12, 10)
                  },
                  4,
                  new VillagerTrades.ItemListing[]{
                     new VillagerTrades.EmeraldForItems(Items.BROWN_DYE, 12, 16, 30),
                     new VillagerTrades.EmeraldForItems(Items.PURPLE_DYE, 12, 16, 30),
                     new VillagerTrades.EmeraldForItems(Items.BLUE_DYE, 12, 16, 30),
                     new VillagerTrades.EmeraldForItems(Items.GREEN_DYE, 12, 16, 30),
                     new VillagerTrades.EmeraldForItems(Items.MAGENTA_DYE, 12, 16, 30),
                     new VillagerTrades.EmeraldForItems(Items.CYAN_DYE, 12, 16, 30),
                     new VillagerTrades.ItemsForEmeralds(Items.WHITE_BANNER, 3, 1, 12, 15),
                     new VillagerTrades.ItemsForEmeralds(Items.BLUE_BANNER, 3, 1, 12, 15),
                     new VillagerTrades.ItemsForEmeralds(Items.LIGHT_BLUE_BANNER, 3, 1, 12, 15),
                     new VillagerTrades.ItemsForEmeralds(Items.RED_BANNER, 3, 1, 12, 15),
                     new VillagerTrades.ItemsForEmeralds(Items.PINK_BANNER, 3, 1, 12, 15),
                     new VillagerTrades.ItemsForEmeralds(Items.GREEN_BANNER, 3, 1, 12, 15),
                     new VillagerTrades.ItemsForEmeralds(Items.LIME_BANNER, 3, 1, 12, 15),
                     new VillagerTrades.ItemsForEmeralds(Items.GRAY_BANNER, 3, 1, 12, 15),
                     new VillagerTrades.ItemsForEmeralds(Items.BLACK_BANNER, 3, 1, 12, 15),
                     new VillagerTrades.ItemsForEmeralds(Items.PURPLE_BANNER, 3, 1, 12, 15),
                     new VillagerTrades.ItemsForEmeralds(Items.MAGENTA_BANNER, 3, 1, 12, 15),
                     new VillagerTrades.ItemsForEmeralds(Items.CYAN_BANNER, 3, 1, 12, 15),
                     new VillagerTrades.ItemsForEmeralds(Items.BROWN_BANNER, 3, 1, 12, 15),
                     new VillagerTrades.ItemsForEmeralds(Items.YELLOW_BANNER, 3, 1, 12, 15),
                     new VillagerTrades.ItemsForEmeralds(Items.ORANGE_BANNER, 3, 1, 12, 15),
                     new VillagerTrades.ItemsForEmeralds(Items.LIGHT_GRAY_BANNER, 3, 1, 12, 15)
                  },
                  5,
                  new VillagerTrades.ItemListing[]{new VillagerTrades.ItemsForEmeralds(Items.PAINTING, 2, 3, 30)}
               )
            )
         );
         var0.put(
            VillagerProfession.FLETCHER,
            toIntMap(
               ImmutableMap.of(
                  1,
                  new VillagerTrades.ItemListing[]{
                     new VillagerTrades.EmeraldForItems(Items.STICK, 32, 16, 2),
                     new VillagerTrades.ItemsForEmeralds(Items.ARROW, 1, 16, 1),
                     new VillagerTrades.ItemsAndEmeraldsToItems(Blocks.GRAVEL, 10, 1, Items.FLINT, 10, 12, 1, 0.05F)
                  },
                  2,
                  new VillagerTrades.ItemListing[]{
                     new VillagerTrades.EmeraldForItems(Items.FLINT, 26, 12, 10), new VillagerTrades.ItemsForEmeralds(Items.BOW, 2, 1, 5)
                  },
                  3,
                  new VillagerTrades.ItemListing[]{
                     new VillagerTrades.EmeraldForItems(Items.STRING, 14, 16, 20), new VillagerTrades.ItemsForEmeralds(Items.CROSSBOW, 3, 1, 10)
                  },
                  4,
                  new VillagerTrades.ItemListing[]{
                     new VillagerTrades.EmeraldForItems(Items.FEATHER, 24, 16, 30), new VillagerTrades.EnchantedItemForEmeralds(Items.BOW, 2, 3, 15)
                  },
                  5,
                  new VillagerTrades.ItemListing[]{
                     new VillagerTrades.EmeraldForItems(Items.TRIPWIRE_HOOK, 8, 12, 30),
                     new VillagerTrades.EnchantedItemForEmeralds(Items.CROSSBOW, 3, 3, 15),
                     new VillagerTrades.TippedArrowForItemsAndEmeralds(Items.ARROW, 5, Items.TIPPED_ARROW, 5, 2, 12, 30)
                  }
               )
            )
         );
         var0.put(
            VillagerProfession.LIBRARIAN,
            toIntMap(
               ImmutableMap.builder()
                  .put(
                     1,
                     new VillagerTrades.ItemListing[]{
                        new VillagerTrades.EmeraldForItems(Items.PAPER, 24, 16, 2),
                        new VillagerTrades.EnchantBookForEmeralds(1, EnchantmentTags.TRADEABLE),
                        new VillagerTrades.ItemsForEmeralds(Blocks.BOOKSHELF, 9, 1, 12, 1)
                     }
                  )
                  .put(
                     2,
                     new VillagerTrades.ItemListing[]{
                        new VillagerTrades.EmeraldForItems(Items.BOOK, 4, 12, 10),
                        new VillagerTrades.EnchantBookForEmeralds(5, EnchantmentTags.TRADEABLE),
                        new VillagerTrades.ItemsForEmeralds(Items.LANTERN, 1, 1, 5)
                     }
                  )
                  .put(
                     3,
                     new VillagerTrades.ItemListing[]{
                        new VillagerTrades.EmeraldForItems(Items.INK_SAC, 5, 12, 20),
                        new VillagerTrades.EnchantBookForEmeralds(10, EnchantmentTags.TRADEABLE),
                        new VillagerTrades.ItemsForEmeralds(Items.GLASS, 1, 4, 10)
                     }
                  )
                  .put(
                     4,
                     new VillagerTrades.ItemListing[]{
                        new VillagerTrades.EmeraldForItems(Items.WRITABLE_BOOK, 2, 12, 30),
                        new VillagerTrades.EnchantBookForEmeralds(15, EnchantmentTags.TRADEABLE),
                        new VillagerTrades.ItemsForEmeralds(Items.CLOCK, 5, 1, 15),
                        new VillagerTrades.ItemsForEmeralds(Items.COMPASS, 4, 1, 15)
                     }
                  )
                  .put(5, new VillagerTrades.ItemListing[]{new VillagerTrades.ItemsForEmeralds(Items.NAME_TAG, 20, 1, 30)})
                  .build()
            )
         );
         var0.put(
            VillagerProfession.CARTOGRAPHER,
            toIntMap(
               ImmutableMap.of(
                  1,
                  new VillagerTrades.ItemListing[]{
                     new VillagerTrades.EmeraldForItems(Items.PAPER, 24, 16, 2), new VillagerTrades.ItemsForEmeralds(Items.MAP, 7, 1, 1)
                  },
                  2,
                  new VillagerTrades.ItemListing[]{
                     new VillagerTrades.EmeraldForItems(Items.GLASS_PANE, 11, 16, 10),
                     new VillagerTrades.TreasureMapForEmeralds(
                        13, StructureTags.ON_OCEAN_EXPLORER_MAPS, "filled_map.monument", MapDecorationTypes.OCEAN_MONUMENT, 12, 5
                     )
                  },
                  3,
                  new VillagerTrades.ItemListing[]{
                     new VillagerTrades.EmeraldForItems(Items.COMPASS, 1, 12, 20),
                     new VillagerTrades.TreasureMapForEmeralds(
                        14, StructureTags.ON_WOODLAND_EXPLORER_MAPS, "filled_map.mansion", MapDecorationTypes.WOODLAND_MANSION, 12, 10
                     ),
                     new VillagerTrades.TreasureMapForEmeralds(
                        12, StructureTags.ON_TRIAL_CHAMBERS_MAPS, "filled_map.trial_chambers", MapDecorationTypes.TRIAL_CHAMBERS, 12, 10
                     )
                  },
                  4,
                  new VillagerTrades.ItemListing[]{
                     new VillagerTrades.ItemsForEmeralds(Items.ITEM_FRAME, 7, 1, 15),
                     new VillagerTrades.ItemsForEmeralds(Items.WHITE_BANNER, 3, 1, 15),
                     new VillagerTrades.ItemsForEmeralds(Items.BLUE_BANNER, 3, 1, 15),
                     new VillagerTrades.ItemsForEmeralds(Items.LIGHT_BLUE_BANNER, 3, 1, 15),
                     new VillagerTrades.ItemsForEmeralds(Items.RED_BANNER, 3, 1, 15),
                     new VillagerTrades.ItemsForEmeralds(Items.PINK_BANNER, 3, 1, 15),
                     new VillagerTrades.ItemsForEmeralds(Items.GREEN_BANNER, 3, 1, 15),
                     new VillagerTrades.ItemsForEmeralds(Items.LIME_BANNER, 3, 1, 15),
                     new VillagerTrades.ItemsForEmeralds(Items.GRAY_BANNER, 3, 1, 15),
                     new VillagerTrades.ItemsForEmeralds(Items.BLACK_BANNER, 3, 1, 15),
                     new VillagerTrades.ItemsForEmeralds(Items.PURPLE_BANNER, 3, 1, 15),
                     new VillagerTrades.ItemsForEmeralds(Items.MAGENTA_BANNER, 3, 1, 15),
                     new VillagerTrades.ItemsForEmeralds(Items.CYAN_BANNER, 3, 1, 15),
                     new VillagerTrades.ItemsForEmeralds(Items.BROWN_BANNER, 3, 1, 15),
                     new VillagerTrades.ItemsForEmeralds(Items.YELLOW_BANNER, 3, 1, 15),
                     new VillagerTrades.ItemsForEmeralds(Items.ORANGE_BANNER, 3, 1, 15),
                     new VillagerTrades.ItemsForEmeralds(Items.LIGHT_GRAY_BANNER, 3, 1, 15)
                  },
                  5,
                  new VillagerTrades.ItemListing[]{new VillagerTrades.ItemsForEmeralds(Items.GLOBE_BANNER_PATTERN, 8, 1, 30)}
               )
            )
         );
         var0.put(
            VillagerProfession.CLERIC,
            toIntMap(
               ImmutableMap.of(
                  1,
                  new VillagerTrades.ItemListing[]{
                     new VillagerTrades.EmeraldForItems(Items.ROTTEN_FLESH, 32, 16, 2), new VillagerTrades.ItemsForEmeralds(Items.REDSTONE, 1, 2, 1)
                  },
                  2,
                  new VillagerTrades.ItemListing[]{
                     new VillagerTrades.EmeraldForItems(Items.GOLD_INGOT, 3, 12, 10), new VillagerTrades.ItemsForEmeralds(Items.LAPIS_LAZULI, 1, 1, 5)
                  },
                  3,
                  new VillagerTrades.ItemListing[]{
                     new VillagerTrades.EmeraldForItems(Items.RABBIT_FOOT, 2, 12, 20), new VillagerTrades.ItemsForEmeralds(Blocks.GLOWSTONE, 4, 1, 12, 10)
                  },
                  4,
                  new VillagerTrades.ItemListing[]{
                     new VillagerTrades.EmeraldForItems(Items.TURTLE_SCUTE, 4, 12, 30),
                     new VillagerTrades.EmeraldForItems(Items.GLASS_BOTTLE, 9, 12, 30),
                     new VillagerTrades.ItemsForEmeralds(Items.ENDER_PEARL, 5, 1, 15)
                  },
                  5,
                  new VillagerTrades.ItemListing[]{
                     new VillagerTrades.EmeraldForItems(Items.NETHER_WART, 22, 12, 30), new VillagerTrades.ItemsForEmeralds(Items.EXPERIENCE_BOTTLE, 3, 1, 30)
                  }
               )
            )
         );
         var0.put(
            VillagerProfession.ARMORER,
            toIntMap(
               ImmutableMap.of(
                  1,
                  new VillagerTrades.ItemListing[]{
                     new VillagerTrades.EmeraldForItems(Items.COAL, 15, 16, 2),
                     new VillagerTrades.ItemsForEmeralds(new ItemStack(Items.IRON_LEGGINGS), 7, 1, 12, 1, 0.2F),
                     new VillagerTrades.ItemsForEmeralds(new ItemStack(Items.IRON_BOOTS), 4, 1, 12, 1, 0.2F),
                     new VillagerTrades.ItemsForEmeralds(new ItemStack(Items.IRON_HELMET), 5, 1, 12, 1, 0.2F),
                     new VillagerTrades.ItemsForEmeralds(new ItemStack(Items.IRON_CHESTPLATE), 9, 1, 12, 1, 0.2F)
                  },
                  2,
                  new VillagerTrades.ItemListing[]{
                     new VillagerTrades.EmeraldForItems(Items.IRON_INGOT, 4, 12, 10),
                     new VillagerTrades.ItemsForEmeralds(new ItemStack(Items.BELL), 36, 1, 12, 5, 0.2F),
                     new VillagerTrades.ItemsForEmeralds(new ItemStack(Items.CHAINMAIL_BOOTS), 1, 1, 12, 5, 0.2F),
                     new VillagerTrades.ItemsForEmeralds(new ItemStack(Items.CHAINMAIL_LEGGINGS), 3, 1, 12, 5, 0.2F)
                  },
                  3,
                  new VillagerTrades.ItemListing[]{
                     new VillagerTrades.EmeraldForItems(Items.LAVA_BUCKET, 1, 12, 20),
                     new VillagerTrades.EmeraldForItems(Items.DIAMOND, 1, 12, 20),
                     new VillagerTrades.ItemsForEmeralds(new ItemStack(Items.CHAINMAIL_HELMET), 1, 1, 12, 10, 0.2F),
                     new VillagerTrades.ItemsForEmeralds(new ItemStack(Items.CHAINMAIL_CHESTPLATE), 4, 1, 12, 10, 0.2F),
                     new VillagerTrades.ItemsForEmeralds(new ItemStack(Items.SHIELD), 5, 1, 12, 10, 0.2F)
                  },
                  4,
                  new VillagerTrades.ItemListing[]{
                     new VillagerTrades.EnchantedItemForEmeralds(Items.DIAMOND_LEGGINGS, 14, 3, 15, 0.2F),
                     new VillagerTrades.EnchantedItemForEmeralds(Items.DIAMOND_BOOTS, 8, 3, 15, 0.2F)
                  },
                  5,
                  new VillagerTrades.ItemListing[]{
                     new VillagerTrades.EnchantedItemForEmeralds(Items.DIAMOND_HELMET, 8, 3, 30, 0.2F),
                     new VillagerTrades.EnchantedItemForEmeralds(Items.DIAMOND_CHESTPLATE, 16, 3, 30, 0.2F)
                  }
               )
            )
         );
         var0.put(
            VillagerProfession.WEAPONSMITH,
            toIntMap(
               ImmutableMap.of(
                  1,
                  new VillagerTrades.ItemListing[]{
                     new VillagerTrades.EmeraldForItems(Items.COAL, 15, 16, 2),
                     new VillagerTrades.ItemsForEmeralds(new ItemStack(Items.IRON_AXE), 3, 1, 12, 1, 0.2F),
                     new VillagerTrades.EnchantedItemForEmeralds(Items.IRON_SWORD, 2, 3, 1)
                  },
                  2,
                  new VillagerTrades.ItemListing[]{
                     new VillagerTrades.EmeraldForItems(Items.IRON_INGOT, 4, 12, 10),
                     new VillagerTrades.ItemsForEmeralds(new ItemStack(Items.BELL), 36, 1, 12, 5, 0.2F)
                  },
                  3,
                  new VillagerTrades.ItemListing[]{new VillagerTrades.EmeraldForItems(Items.FLINT, 24, 12, 20)},
                  4,
                  new VillagerTrades.ItemListing[]{
                     new VillagerTrades.EmeraldForItems(Items.DIAMOND, 1, 12, 30),
                     new VillagerTrades.EnchantedItemForEmeralds(Items.DIAMOND_AXE, 12, 3, 15, 0.2F)
                  },
                  5,
                  new VillagerTrades.ItemListing[]{new VillagerTrades.EnchantedItemForEmeralds(Items.DIAMOND_SWORD, 8, 3, 30, 0.2F)}
               )
            )
         );
         var0.put(
            VillagerProfession.TOOLSMITH,
            toIntMap(
               ImmutableMap.of(
                  1,
                  new VillagerTrades.ItemListing[]{
                     new VillagerTrades.EmeraldForItems(Items.COAL, 15, 16, 2),
                     new VillagerTrades.ItemsForEmeralds(new ItemStack(Items.STONE_AXE), 1, 1, 12, 1, 0.2F),
                     new VillagerTrades.ItemsForEmeralds(new ItemStack(Items.STONE_SHOVEL), 1, 1, 12, 1, 0.2F),
                     new VillagerTrades.ItemsForEmeralds(new ItemStack(Items.STONE_PICKAXE), 1, 1, 12, 1, 0.2F),
                     new VillagerTrades.ItemsForEmeralds(new ItemStack(Items.STONE_HOE), 1, 1, 12, 1, 0.2F)
                  },
                  2,
                  new VillagerTrades.ItemListing[]{
                     new VillagerTrades.EmeraldForItems(Items.IRON_INGOT, 4, 12, 10),
                     new VillagerTrades.ItemsForEmeralds(new ItemStack(Items.BELL), 36, 1, 12, 5, 0.2F)
                  },
                  3,
                  new VillagerTrades.ItemListing[]{
                     new VillagerTrades.EmeraldForItems(Items.FLINT, 30, 12, 20),
                     new VillagerTrades.EnchantedItemForEmeralds(Items.IRON_AXE, 1, 3, 10, 0.2F),
                     new VillagerTrades.EnchantedItemForEmeralds(Items.IRON_SHOVEL, 2, 3, 10, 0.2F),
                     new VillagerTrades.EnchantedItemForEmeralds(Items.IRON_PICKAXE, 3, 3, 10, 0.2F),
                     new VillagerTrades.ItemsForEmeralds(new ItemStack(Items.DIAMOND_HOE), 4, 1, 3, 10, 0.2F)
                  },
                  4,
                  new VillagerTrades.ItemListing[]{
                     new VillagerTrades.EmeraldForItems(Items.DIAMOND, 1, 12, 30),
                     new VillagerTrades.EnchantedItemForEmeralds(Items.DIAMOND_AXE, 12, 3, 15, 0.2F),
                     new VillagerTrades.EnchantedItemForEmeralds(Items.DIAMOND_SHOVEL, 5, 3, 15, 0.2F)
                  },
                  5,
                  new VillagerTrades.ItemListing[]{new VillagerTrades.EnchantedItemForEmeralds(Items.DIAMOND_PICKAXE, 13, 3, 30, 0.2F)}
               )
            )
         );
         var0.put(
            VillagerProfession.BUTCHER,
            toIntMap(
               ImmutableMap.of(
                  1,
                  new VillagerTrades.ItemListing[]{
                     new VillagerTrades.EmeraldForItems(Items.CHICKEN, 14, 16, 2),
                     new VillagerTrades.EmeraldForItems(Items.PORKCHOP, 7, 16, 2),
                     new VillagerTrades.EmeraldForItems(Items.RABBIT, 4, 16, 2),
                     new VillagerTrades.ItemsForEmeralds(Items.RABBIT_STEW, 1, 1, 1)
                  },
                  2,
                  new VillagerTrades.ItemListing[]{
                     new VillagerTrades.EmeraldForItems(Items.COAL, 15, 16, 2),
                     new VillagerTrades.ItemsForEmeralds(Items.COOKED_PORKCHOP, 1, 5, 16, 5),
                     new VillagerTrades.ItemsForEmeralds(Items.COOKED_CHICKEN, 1, 8, 16, 5)
                  },
                  3,
                  new VillagerTrades.ItemListing[]{
                     new VillagerTrades.EmeraldForItems(Items.MUTTON, 7, 16, 20), new VillagerTrades.EmeraldForItems(Items.BEEF, 10, 16, 20)
                  },
                  4,
                  new VillagerTrades.ItemListing[]{new VillagerTrades.EmeraldForItems(Items.DRIED_KELP_BLOCK, 10, 12, 30)},
                  5,
                  new VillagerTrades.ItemListing[]{new VillagerTrades.EmeraldForItems(Items.SWEET_BERRIES, 10, 12, 30)}
               )
            )
         );
         var0.put(
            VillagerProfession.LEATHERWORKER,
            toIntMap(
               ImmutableMap.of(
                  1,
                  new VillagerTrades.ItemListing[]{
                     new VillagerTrades.EmeraldForItems(Items.LEATHER, 6, 16, 2),
                     new VillagerTrades.DyedArmorForEmeralds(Items.LEATHER_LEGGINGS, 3),
                     new VillagerTrades.DyedArmorForEmeralds(Items.LEATHER_CHESTPLATE, 7)
                  },
                  2,
                  new VillagerTrades.ItemListing[]{
                     new VillagerTrades.EmeraldForItems(Items.FLINT, 26, 12, 10),
                     new VillagerTrades.DyedArmorForEmeralds(Items.LEATHER_HELMET, 5, 12, 5),
                     new VillagerTrades.DyedArmorForEmeralds(Items.LEATHER_BOOTS, 4, 12, 5)
                  },
                  3,
                  new VillagerTrades.ItemListing[]{
                     new VillagerTrades.EmeraldForItems(Items.RABBIT_HIDE, 9, 12, 20), new VillagerTrades.DyedArmorForEmeralds(Items.LEATHER_CHESTPLATE, 7)
                  },
                  4,
                  new VillagerTrades.ItemListing[]{
                     new VillagerTrades.EmeraldForItems(Items.TURTLE_SCUTE, 4, 12, 30),
                     new VillagerTrades.DyedArmorForEmeralds(Items.LEATHER_HORSE_ARMOR, 6, 12, 15)
                  },
                  5,
                  new VillagerTrades.ItemListing[]{
                     new VillagerTrades.ItemsForEmeralds(new ItemStack(Items.SADDLE), 6, 1, 12, 30, 0.2F),
                     new VillagerTrades.DyedArmorForEmeralds(Items.LEATHER_HELMET, 5, 12, 30)
                  }
               )
            )
         );
         var0.put(
            VillagerProfession.MASON,
            toIntMap(
               ImmutableMap.of(
                  1,
                  new VillagerTrades.ItemListing[]{
                     new VillagerTrades.EmeraldForItems(Items.CLAY_BALL, 10, 16, 2), new VillagerTrades.ItemsForEmeralds(Items.BRICK, 1, 10, 16, 1)
                  },
                  2,
                  new VillagerTrades.ItemListing[]{
                     new VillagerTrades.EmeraldForItems(Blocks.STONE, 20, 16, 10),
                     new VillagerTrades.ItemsForEmeralds(Blocks.CHISELED_STONE_BRICKS, 1, 4, 16, 5)
                  },
                  3,
                  new VillagerTrades.ItemListing[]{
                     new VillagerTrades.EmeraldForItems(Blocks.GRANITE, 16, 16, 20),
                     new VillagerTrades.EmeraldForItems(Blocks.ANDESITE, 16, 16, 20),
                     new VillagerTrades.EmeraldForItems(Blocks.DIORITE, 16, 16, 20),
                     new VillagerTrades.ItemsForEmeralds(Blocks.DRIPSTONE_BLOCK, 1, 4, 16, 10),
                     new VillagerTrades.ItemsForEmeralds(Blocks.POLISHED_ANDESITE, 1, 4, 16, 10),
                     new VillagerTrades.ItemsForEmeralds(Blocks.POLISHED_DIORITE, 1, 4, 16, 10),
                     new VillagerTrades.ItemsForEmeralds(Blocks.POLISHED_GRANITE, 1, 4, 16, 10)
                  },
                  4,
                  new VillagerTrades.ItemListing[]{
                     new VillagerTrades.EmeraldForItems(Items.QUARTZ, 12, 12, 30),
                     new VillagerTrades.ItemsForEmeralds(Blocks.ORANGE_TERRACOTTA, 1, 1, 12, 15),
                     new VillagerTrades.ItemsForEmeralds(Blocks.WHITE_TERRACOTTA, 1, 1, 12, 15),
                     new VillagerTrades.ItemsForEmeralds(Blocks.BLUE_TERRACOTTA, 1, 1, 12, 15),
                     new VillagerTrades.ItemsForEmeralds(Blocks.LIGHT_BLUE_TERRACOTTA, 1, 1, 12, 15),
                     new VillagerTrades.ItemsForEmeralds(Blocks.GRAY_TERRACOTTA, 1, 1, 12, 15),
                     new VillagerTrades.ItemsForEmeralds(Blocks.LIGHT_GRAY_TERRACOTTA, 1, 1, 12, 15),
                     new VillagerTrades.ItemsForEmeralds(Blocks.BLACK_TERRACOTTA, 1, 1, 12, 15),
                     new VillagerTrades.ItemsForEmeralds(Blocks.RED_TERRACOTTA, 1, 1, 12, 15),
                     new VillagerTrades.ItemsForEmeralds(Blocks.PINK_TERRACOTTA, 1, 1, 12, 15),
                     new VillagerTrades.ItemsForEmeralds(Blocks.MAGENTA_TERRACOTTA, 1, 1, 12, 15),
                     new VillagerTrades.ItemsForEmeralds(Blocks.LIME_TERRACOTTA, 1, 1, 12, 15),
                     new VillagerTrades.ItemsForEmeralds(Blocks.GREEN_TERRACOTTA, 1, 1, 12, 15),
                     new VillagerTrades.ItemsForEmeralds(Blocks.CYAN_TERRACOTTA, 1, 1, 12, 15),
                     new VillagerTrades.ItemsForEmeralds(Blocks.PURPLE_TERRACOTTA, 1, 1, 12, 15),
                     new VillagerTrades.ItemsForEmeralds(Blocks.YELLOW_TERRACOTTA, 1, 1, 12, 15),
                     new VillagerTrades.ItemsForEmeralds(Blocks.BROWN_TERRACOTTA, 1, 1, 12, 15),
                     new VillagerTrades.ItemsForEmeralds(Blocks.ORANGE_GLAZED_TERRACOTTA, 1, 1, 12, 15),
                     new VillagerTrades.ItemsForEmeralds(Blocks.WHITE_GLAZED_TERRACOTTA, 1, 1, 12, 15),
                     new VillagerTrades.ItemsForEmeralds(Blocks.BLUE_GLAZED_TERRACOTTA, 1, 1, 12, 15),
                     new VillagerTrades.ItemsForEmeralds(Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA, 1, 1, 12, 15),
                     new VillagerTrades.ItemsForEmeralds(Blocks.GRAY_GLAZED_TERRACOTTA, 1, 1, 12, 15),
                     new VillagerTrades.ItemsForEmeralds(Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA, 1, 1, 12, 15),
                     new VillagerTrades.ItemsForEmeralds(Blocks.BLACK_GLAZED_TERRACOTTA, 1, 1, 12, 15),
                     new VillagerTrades.ItemsForEmeralds(Blocks.RED_GLAZED_TERRACOTTA, 1, 1, 12, 15),
                     new VillagerTrades.ItemsForEmeralds(Blocks.PINK_GLAZED_TERRACOTTA, 1, 1, 12, 15),
                     new VillagerTrades.ItemsForEmeralds(Blocks.MAGENTA_GLAZED_TERRACOTTA, 1, 1, 12, 15),
                     new VillagerTrades.ItemsForEmeralds(Blocks.LIME_GLAZED_TERRACOTTA, 1, 1, 12, 15),
                     new VillagerTrades.ItemsForEmeralds(Blocks.GREEN_GLAZED_TERRACOTTA, 1, 1, 12, 15),
                     new VillagerTrades.ItemsForEmeralds(Blocks.CYAN_GLAZED_TERRACOTTA, 1, 1, 12, 15),
                     new VillagerTrades.ItemsForEmeralds(Blocks.PURPLE_GLAZED_TERRACOTTA, 1, 1, 12, 15),
                     new VillagerTrades.ItemsForEmeralds(Blocks.YELLOW_GLAZED_TERRACOTTA, 1, 1, 12, 15),
                     new VillagerTrades.ItemsForEmeralds(Blocks.BROWN_GLAZED_TERRACOTTA, 1, 1, 12, 15)
                  },
                  5,
                  new VillagerTrades.ItemListing[]{
                     new VillagerTrades.ItemsForEmeralds(Blocks.QUARTZ_PILLAR, 1, 1, 12, 30),
                     new VillagerTrades.ItemsForEmeralds(Blocks.QUARTZ_BLOCK, 1, 1, 12, 30)
                  }
               )
            )
         );
      }
   );
   public static final Int2ObjectMap<VillagerTrades.ItemListing[]> WANDERING_TRADER_TRADES = toIntMap(
      ImmutableMap.of(
         1,
         new VillagerTrades.ItemListing[]{
            new VillagerTrades.ItemsForEmeralds(Items.SEA_PICKLE, 2, 1, 5, 1),
            new VillagerTrades.ItemsForEmeralds(Items.SLIME_BALL, 4, 1, 5, 1),
            new VillagerTrades.ItemsForEmeralds(Items.GLOWSTONE, 2, 1, 5, 1),
            new VillagerTrades.ItemsForEmeralds(Items.NAUTILUS_SHELL, 5, 1, 5, 1),
            new VillagerTrades.ItemsForEmeralds(Items.FERN, 1, 1, 12, 1),
            new VillagerTrades.ItemsForEmeralds(Items.SUGAR_CANE, 1, 1, 8, 1),
            new VillagerTrades.ItemsForEmeralds(Items.PUMPKIN, 1, 1, 4, 1),
            new VillagerTrades.ItemsForEmeralds(Items.KELP, 3, 1, 12, 1),
            new VillagerTrades.ItemsForEmeralds(Items.CACTUS, 3, 1, 8, 1),
            new VillagerTrades.ItemsForEmeralds(Items.DANDELION, 1, 1, 12, 1),
            new VillagerTrades.ItemsForEmeralds(Items.POPPY, 1, 1, 12, 1),
            new VillagerTrades.ItemsForEmeralds(Items.BLUE_ORCHID, 1, 1, 8, 1),
            new VillagerTrades.ItemsForEmeralds(Items.ALLIUM, 1, 1, 12, 1),
            new VillagerTrades.ItemsForEmeralds(Items.AZURE_BLUET, 1, 1, 12, 1),
            new VillagerTrades.ItemsForEmeralds(Items.RED_TULIP, 1, 1, 12, 1),
            new VillagerTrades.ItemsForEmeralds(Items.ORANGE_TULIP, 1, 1, 12, 1),
            new VillagerTrades.ItemsForEmeralds(Items.WHITE_TULIP, 1, 1, 12, 1),
            new VillagerTrades.ItemsForEmeralds(Items.PINK_TULIP, 1, 1, 12, 1),
            new VillagerTrades.ItemsForEmeralds(Items.OXEYE_DAISY, 1, 1, 12, 1),
            new VillagerTrades.ItemsForEmeralds(Items.CORNFLOWER, 1, 1, 12, 1),
            new VillagerTrades.ItemsForEmeralds(Items.LILY_OF_THE_VALLEY, 1, 1, 7, 1),
            new VillagerTrades.ItemsForEmeralds(Items.WHEAT_SEEDS, 1, 1, 12, 1),
            new VillagerTrades.ItemsForEmeralds(Items.BEETROOT_SEEDS, 1, 1, 12, 1),
            new VillagerTrades.ItemsForEmeralds(Items.PUMPKIN_SEEDS, 1, 1, 12, 1),
            new VillagerTrades.ItemsForEmeralds(Items.MELON_SEEDS, 1, 1, 12, 1),
            new VillagerTrades.ItemsForEmeralds(Items.ACACIA_SAPLING, 5, 1, 8, 1),
            new VillagerTrades.ItemsForEmeralds(Items.BIRCH_SAPLING, 5, 1, 8, 1),
            new VillagerTrades.ItemsForEmeralds(Items.DARK_OAK_SAPLING, 5, 1, 8, 1),
            new VillagerTrades.ItemsForEmeralds(Items.JUNGLE_SAPLING, 5, 1, 8, 1),
            new VillagerTrades.ItemsForEmeralds(Items.OAK_SAPLING, 5, 1, 8, 1),
            new VillagerTrades.ItemsForEmeralds(Items.SPRUCE_SAPLING, 5, 1, 8, 1),
            new VillagerTrades.ItemsForEmeralds(Items.CHERRY_SAPLING, 5, 1, 8, 1),
            new VillagerTrades.ItemsForEmeralds(Items.MANGROVE_PROPAGULE, 5, 1, 8, 1),
            new VillagerTrades.ItemsForEmeralds(Items.RED_DYE, 1, 3, 12, 1),
            new VillagerTrades.ItemsForEmeralds(Items.WHITE_DYE, 1, 3, 12, 1),
            new VillagerTrades.ItemsForEmeralds(Items.BLUE_DYE, 1, 3, 12, 1),
            new VillagerTrades.ItemsForEmeralds(Items.PINK_DYE, 1, 3, 12, 1),
            new VillagerTrades.ItemsForEmeralds(Items.BLACK_DYE, 1, 3, 12, 1),
            new VillagerTrades.ItemsForEmeralds(Items.GREEN_DYE, 1, 3, 12, 1),
            new VillagerTrades.ItemsForEmeralds(Items.LIGHT_GRAY_DYE, 1, 3, 12, 1),
            new VillagerTrades.ItemsForEmeralds(Items.MAGENTA_DYE, 1, 3, 12, 1),
            new VillagerTrades.ItemsForEmeralds(Items.YELLOW_DYE, 1, 3, 12, 1),
            new VillagerTrades.ItemsForEmeralds(Items.GRAY_DYE, 1, 3, 12, 1),
            new VillagerTrades.ItemsForEmeralds(Items.PURPLE_DYE, 1, 3, 12, 1),
            new VillagerTrades.ItemsForEmeralds(Items.LIGHT_BLUE_DYE, 1, 3, 12, 1),
            new VillagerTrades.ItemsForEmeralds(Items.LIME_DYE, 1, 3, 12, 1),
            new VillagerTrades.ItemsForEmeralds(Items.ORANGE_DYE, 1, 3, 12, 1),
            new VillagerTrades.ItemsForEmeralds(Items.BROWN_DYE, 1, 3, 12, 1),
            new VillagerTrades.ItemsForEmeralds(Items.CYAN_DYE, 1, 3, 12, 1),
            new VillagerTrades.ItemsForEmeralds(Items.BRAIN_CORAL_BLOCK, 3, 1, 8, 1),
            new VillagerTrades.ItemsForEmeralds(Items.BUBBLE_CORAL_BLOCK, 3, 1, 8, 1),
            new VillagerTrades.ItemsForEmeralds(Items.FIRE_CORAL_BLOCK, 3, 1, 8, 1),
            new VillagerTrades.ItemsForEmeralds(Items.HORN_CORAL_BLOCK, 3, 1, 8, 1),
            new VillagerTrades.ItemsForEmeralds(Items.TUBE_CORAL_BLOCK, 3, 1, 8, 1),
            new VillagerTrades.ItemsForEmeralds(Items.VINE, 1, 1, 12, 1),
            new VillagerTrades.ItemsForEmeralds(Items.BROWN_MUSHROOM, 1, 1, 12, 1),
            new VillagerTrades.ItemsForEmeralds(Items.RED_MUSHROOM, 1, 1, 12, 1),
            new VillagerTrades.ItemsForEmeralds(Items.LILY_PAD, 1, 2, 5, 1),
            new VillagerTrades.ItemsForEmeralds(Items.SMALL_DRIPLEAF, 1, 2, 5, 1),
            new VillagerTrades.ItemsForEmeralds(Items.SAND, 1, 8, 8, 1),
            new VillagerTrades.ItemsForEmeralds(Items.RED_SAND, 1, 4, 6, 1),
            new VillagerTrades.ItemsForEmeralds(Items.POINTED_DRIPSTONE, 1, 2, 5, 1),
            new VillagerTrades.ItemsForEmeralds(Items.ROOTED_DIRT, 1, 2, 5, 1),
            new VillagerTrades.ItemsForEmeralds(Items.MOSS_BLOCK, 1, 2, 5, 1)
         },
         2,
         new VillagerTrades.ItemListing[]{
            new VillagerTrades.ItemsForEmeralds(Items.TROPICAL_FISH_BUCKET, 5, 1, 4, 1),
            new VillagerTrades.ItemsForEmeralds(Items.PUFFERFISH_BUCKET, 5, 1, 4, 1),
            new VillagerTrades.ItemsForEmeralds(Items.PACKED_ICE, 3, 1, 6, 1),
            new VillagerTrades.ItemsForEmeralds(Items.BLUE_ICE, 6, 1, 6, 1),
            new VillagerTrades.ItemsForEmeralds(Items.GUNPOWDER, 1, 1, 8, 1),
            new VillagerTrades.ItemsForEmeralds(Items.PODZOL, 3, 3, 6, 1)
         }
      )
   );
   private static final VillagerTrades.TreasureMapForEmeralds DESERT_MAP = new VillagerTrades.TreasureMapForEmeralds(
      8, StructureTags.ON_DESERT_VILLAGE_MAPS, "filled_map.village_desert", MapDecorationTypes.DESERT_VILLAGE, 12, 5
   );
   private static final VillagerTrades.TreasureMapForEmeralds SAVANNA_MAP = new VillagerTrades.TreasureMapForEmeralds(
      8, StructureTags.ON_SAVANNA_VILLAGE_MAPS, "filled_map.village_savanna", MapDecorationTypes.SAVANNA_VILLAGE, 12, 5
   );
   private static final VillagerTrades.TreasureMapForEmeralds PLAINS_MAP = new VillagerTrades.TreasureMapForEmeralds(
      8, StructureTags.ON_PLAINS_VILLAGE_MAPS, "filled_map.village_plains", MapDecorationTypes.PLAINS_VILLAGE, 12, 5
   );
   private static final VillagerTrades.TreasureMapForEmeralds TAIGA_MAP = new VillagerTrades.TreasureMapForEmeralds(
      8, StructureTags.ON_TAIGA_VILLAGE_MAPS, "filled_map.village_taiga", MapDecorationTypes.TAIGA_VILLAGE, 12, 5
   );
   private static final VillagerTrades.TreasureMapForEmeralds SNOWY_MAP = new VillagerTrades.TreasureMapForEmeralds(
      8, StructureTags.ON_SNOWY_VILLAGE_MAPS, "filled_map.village_snowy", MapDecorationTypes.SNOWY_VILLAGE, 12, 5
   );
   private static final VillagerTrades.TreasureMapForEmeralds JUNGLE_MAP = new VillagerTrades.TreasureMapForEmeralds(
      8, StructureTags.ON_JUNGLE_EXPLORER_MAPS, "filled_map.explorer_jungle", MapDecorationTypes.JUNGLE_TEMPLE, 12, 5
   );
   private static final VillagerTrades.TreasureMapForEmeralds SWAMP_MAP = new VillagerTrades.TreasureMapForEmeralds(
      8, StructureTags.ON_SWAMP_EXPLORER_MAPS, "filled_map.explorer_swamp", MapDecorationTypes.SWAMP_HUT, 12, 5
   );
   public static final Map<VillagerProfession, Int2ObjectMap<VillagerTrades.ItemListing[]>> EXPERIMENTAL_TRADES = Map.of(
      VillagerProfession.LIBRARIAN,
      toIntMap(
         ImmutableMap.builder()
            .put(
               1,
               new VillagerTrades.ItemListing[]{
                  new VillagerTrades.EmeraldForItems(Items.PAPER, 24, 16, 2),
                  commonBooks(1),
                  new VillagerTrades.ItemsForEmeralds(Blocks.BOOKSHELF, 9, 1, 12, 1)
               }
            )
            .put(
               2,
               new VillagerTrades.ItemListing[]{
                  new VillagerTrades.EmeraldForItems(Items.BOOK, 4, 12, 10), commonBooks(5), new VillagerTrades.ItemsForEmeralds(Items.LANTERN, 1, 1, 5)
               }
            )
            .put(
               3,
               new VillagerTrades.ItemListing[]{
                  new VillagerTrades.EmeraldForItems(Items.INK_SAC, 5, 12, 20), commonBooks(10), new VillagerTrades.ItemsForEmeralds(Items.GLASS, 1, 4, 10)
               }
            )
            .put(
               4,
               new VillagerTrades.ItemListing[]{
                  new VillagerTrades.EmeraldForItems(Items.WRITABLE_BOOK, 2, 12, 30),
                  new VillagerTrades.ItemsForEmeralds(Items.CLOCK, 5, 1, 15),
                  new VillagerTrades.ItemsForEmeralds(Items.COMPASS, 4, 1, 15)
               }
            )
            .put(5, new VillagerTrades.ItemListing[]{specialBooks(), new VillagerTrades.ItemsForEmeralds(Items.NAME_TAG, 20, 1, 30)})
            .build()
      ),
      VillagerProfession.ARMORER,
      toIntMap(
         ImmutableMap.builder()
            .put(
               1,
               new VillagerTrades.ItemListing[]{
                  new VillagerTrades.EmeraldForItems(Items.COAL, 15, 12, 2), new VillagerTrades.EmeraldForItems(Items.IRON_INGOT, 5, 12, 2)
               }
            )
            .put(
               2,
               new VillagerTrades.ItemListing[]{
                  VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(
                     new VillagerTrades.ItemsForEmeralds(Items.IRON_BOOTS, 4, 1, 12, 5, 0.05F),
                     VillagerType.DESERT,
                     VillagerType.PLAINS,
                     VillagerType.SAVANNA,
                     VillagerType.SNOW,
                     VillagerType.TAIGA
                  ),
                  VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(
                     new VillagerTrades.ItemsForEmeralds(Items.CHAINMAIL_BOOTS, 4, 1, 12, 5, 0.05F), VillagerType.JUNGLE, VillagerType.SWAMP
                  ),
                  VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(
                     new VillagerTrades.ItemsForEmeralds(Items.IRON_HELMET, 5, 1, 12, 5, 0.05F),
                     VillagerType.DESERT,
                     VillagerType.PLAINS,
                     VillagerType.SAVANNA,
                     VillagerType.SNOW,
                     VillagerType.TAIGA
                  ),
                  VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(
                     new VillagerTrades.ItemsForEmeralds(Items.CHAINMAIL_HELMET, 5, 1, 12, 5, 0.05F), VillagerType.JUNGLE, VillagerType.SWAMP
                  ),
                  VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(
                     new VillagerTrades.ItemsForEmeralds(Items.IRON_LEGGINGS, 7, 1, 12, 5, 0.05F),
                     VillagerType.DESERT,
                     VillagerType.PLAINS,
                     VillagerType.SAVANNA,
                     VillagerType.SNOW,
                     VillagerType.TAIGA
                  ),
                  VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(
                     new VillagerTrades.ItemsForEmeralds(Items.CHAINMAIL_LEGGINGS, 7, 1, 12, 5, 0.05F), VillagerType.JUNGLE, VillagerType.SWAMP
                  ),
                  VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(
                     new VillagerTrades.ItemsForEmeralds(Items.IRON_CHESTPLATE, 9, 1, 12, 5, 0.05F),
                     VillagerType.DESERT,
                     VillagerType.PLAINS,
                     VillagerType.SAVANNA,
                     VillagerType.SNOW,
                     VillagerType.TAIGA
                  ),
                  VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(
                     new VillagerTrades.ItemsForEmeralds(Items.CHAINMAIL_CHESTPLATE, 9, 1, 12, 5, 0.05F), VillagerType.JUNGLE, VillagerType.SWAMP
                  )
               }
            )
            .put(
               3,
               new VillagerTrades.ItemListing[]{
                  new VillagerTrades.EmeraldForItems(Items.LAVA_BUCKET, 1, 12, 20),
                  new VillagerTrades.ItemsForEmeralds(Items.SHIELD, 5, 1, 12, 10, 0.05F),
                  new VillagerTrades.ItemsForEmeralds(Items.BELL, 36, 1, 12, 10, 0.2F)
               }
            )
            .put(
               4,
               new VillagerTrades.ItemListing[]{
                  VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(
                     new VillagerTrades.ItemsForEmeralds(Items.IRON_BOOTS, 8, 1, 3, 15, 0.05F, TradeRebalanceEnchantmentProviders.TRADES_DESERT_ARMORER_BOOTS_4),
                     VillagerType.DESERT
                  ),
                  VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(
                     new VillagerTrades.ItemsForEmeralds(
                        Items.IRON_HELMET, 9, 1, 3, 15, 0.05F, TradeRebalanceEnchantmentProviders.TRADES_DESERT_ARMORER_HELMET_4
                     ),
                     VillagerType.DESERT
                  ),
                  VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(
                     new VillagerTrades.ItemsForEmeralds(
                        Items.IRON_LEGGINGS, 11, 1, 3, 15, 0.05F, TradeRebalanceEnchantmentProviders.TRADES_DESERT_ARMORER_LEGGINGS_4
                     ),
                     VillagerType.DESERT
                  ),
                  VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(
                     new VillagerTrades.ItemsForEmeralds(
                        Items.IRON_CHESTPLATE, 13, 1, 3, 15, 0.05F, TradeRebalanceEnchantmentProviders.TRADES_DESERT_ARMORER_CHESTPLATE_4
                     ),
                     VillagerType.DESERT
                  ),
                  VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(
                     new VillagerTrades.ItemsForEmeralds(Items.IRON_BOOTS, 8, 1, 3, 15, 0.05F, TradeRebalanceEnchantmentProviders.TRADES_PLAINS_ARMORER_BOOTS_4),
                     VillagerType.PLAINS
                  ),
                  VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(
                     new VillagerTrades.ItemsForEmeralds(
                        Items.IRON_HELMET, 9, 1, 3, 15, 0.05F, TradeRebalanceEnchantmentProviders.TRADES_PLAINS_ARMORER_HELMET_4
                     ),
                     VillagerType.PLAINS
                  ),
                  VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(
                     new VillagerTrades.ItemsForEmeralds(
                        Items.IRON_LEGGINGS, 11, 1, 3, 15, 0.05F, TradeRebalanceEnchantmentProviders.TRADES_PLAINS_ARMORER_LEGGINGS_4
                     ),
                     VillagerType.PLAINS
                  ),
                  VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(
                     new VillagerTrades.ItemsForEmeralds(
                        Items.IRON_CHESTPLATE, 13, 1, 3, 15, 0.05F, TradeRebalanceEnchantmentProviders.TRADES_PLAINS_ARMORER_CHESTPLATE_4
                     ),
                     VillagerType.PLAINS
                  ),
                  VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(
                     new VillagerTrades.ItemsForEmeralds(
                        Items.IRON_BOOTS, 2, 1, 3, 15, 0.05F, TradeRebalanceEnchantmentProviders.TRADES_SAVANNA_ARMORER_BOOTS_4
                     ),
                     VillagerType.SAVANNA
                  ),
                  VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(
                     new VillagerTrades.ItemsForEmeralds(
                        Items.IRON_HELMET, 3, 1, 3, 15, 0.05F, TradeRebalanceEnchantmentProviders.TRADES_SAVANNA_ARMORER_HELMET_4
                     ),
                     VillagerType.SAVANNA
                  ),
                  VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(
                     new VillagerTrades.ItemsForEmeralds(
                        Items.IRON_LEGGINGS, 5, 1, 3, 15, 0.05F, TradeRebalanceEnchantmentProviders.TRADES_SAVANNA_ARMORER_LEGGINGS_4
                     ),
                     VillagerType.SAVANNA
                  ),
                  VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(
                     new VillagerTrades.ItemsForEmeralds(
                        Items.IRON_CHESTPLATE, 7, 1, 3, 15, 0.05F, TradeRebalanceEnchantmentProviders.TRADES_SAVANNA_ARMORER_CHESTPLATE_4
                     ),
                     VillagerType.SAVANNA
                  ),
                  VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(
                     new VillagerTrades.ItemsForEmeralds(Items.IRON_BOOTS, 8, 1, 3, 15, 0.05F, TradeRebalanceEnchantmentProviders.TRADES_SNOW_ARMORER_BOOTS_4),
                     VillagerType.SNOW
                  ),
                  VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(
                     new VillagerTrades.ItemsForEmeralds(Items.IRON_HELMET, 9, 1, 3, 15, 0.05F, TradeRebalanceEnchantmentProviders.TRADES_SNOW_ARMORER_HELMET_4),
                     VillagerType.SNOW
                  ),
                  VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(
                     new VillagerTrades.ItemsForEmeralds(
                        Items.CHAINMAIL_BOOTS, 8, 1, 3, 15, 0.05F, TradeRebalanceEnchantmentProviders.TRADES_JUNGLE_ARMORER_BOOTS_4
                     ),
                     VillagerType.JUNGLE
                  ),
                  VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(
                     new VillagerTrades.ItemsForEmeralds(
                        Items.CHAINMAIL_HELMET, 9, 1, 3, 15, 0.05F, TradeRebalanceEnchantmentProviders.TRADES_JUNGLE_ARMORER_HELMET_4
                     ),
                     VillagerType.JUNGLE
                  ),
                  VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(
                     new VillagerTrades.ItemsForEmeralds(
                        Items.CHAINMAIL_LEGGINGS, 11, 1, 3, 15, 0.05F, TradeRebalanceEnchantmentProviders.TRADES_JUNGLE_ARMORER_LEGGINGS_4
                     ),
                     VillagerType.JUNGLE
                  ),
                  VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(
                     new VillagerTrades.ItemsForEmeralds(
                        Items.CHAINMAIL_CHESTPLATE, 13, 1, 3, 15, 0.05F, TradeRebalanceEnchantmentProviders.TRADES_JUNGLE_ARMORER_CHESTPLATE_4
                     ),
                     VillagerType.JUNGLE
                  ),
                  VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(
                     new VillagerTrades.ItemsForEmeralds(
                        Items.CHAINMAIL_BOOTS, 8, 1, 3, 15, 0.05F, TradeRebalanceEnchantmentProviders.TRADES_SWAMP_ARMORER_BOOTS_4
                     ),
                     VillagerType.SWAMP
                  ),
                  VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(
                     new VillagerTrades.ItemsForEmeralds(
                        Items.CHAINMAIL_HELMET, 9, 1, 3, 15, 0.05F, TradeRebalanceEnchantmentProviders.TRADES_SWAMP_ARMORER_HELMET_4
                     ),
                     VillagerType.SWAMP
                  ),
                  VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(
                     new VillagerTrades.ItemsForEmeralds(
                        Items.CHAINMAIL_LEGGINGS, 11, 1, 3, 15, 0.05F, TradeRebalanceEnchantmentProviders.TRADES_SWAMP_ARMORER_LEGGINGS_4
                     ),
                     VillagerType.SWAMP
                  ),
                  VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(
                     new VillagerTrades.ItemsForEmeralds(
                        Items.CHAINMAIL_CHESTPLATE, 13, 1, 3, 15, 0.05F, TradeRebalanceEnchantmentProviders.TRADES_SWAMP_ARMORER_CHESTPLATE_4
                     ),
                     VillagerType.SWAMP
                  ),
                  VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(
                     new VillagerTrades.ItemsAndEmeraldsToItems(Items.DIAMOND_BOOTS, 1, 4, Items.DIAMOND_LEGGINGS, 1, 3, 15, 0.05F), VillagerType.TAIGA
                  ),
                  VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(
                     new VillagerTrades.ItemsAndEmeraldsToItems(Items.DIAMOND_LEGGINGS, 1, 4, Items.DIAMOND_CHESTPLATE, 1, 3, 15, 0.05F), VillagerType.TAIGA
                  ),
                  VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(
                     new VillagerTrades.ItemsAndEmeraldsToItems(Items.DIAMOND_HELMET, 1, 4, Items.DIAMOND_BOOTS, 1, 3, 15, 0.05F), VillagerType.TAIGA
                  ),
                  VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(
                     new VillagerTrades.ItemsAndEmeraldsToItems(Items.DIAMOND_CHESTPLATE, 1, 2, Items.DIAMOND_HELMET, 1, 3, 15, 0.05F), VillagerType.TAIGA
                  )
               }
            )
            .put(
               5,
               new VillagerTrades.ItemListing[]{
                  VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(
                     new VillagerTrades.ItemsAndEmeraldsToItems(
                        Items.DIAMOND, 4, 16, Items.DIAMOND_CHESTPLATE, 1, 3, 30, 0.05F, TradeRebalanceEnchantmentProviders.TRADES_DESERT_ARMORER_CHESTPLATE_5
                     ),
                     VillagerType.DESERT
                  ),
                  VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(
                     new VillagerTrades.ItemsAndEmeraldsToItems(
                        Items.DIAMOND, 3, 16, Items.DIAMOND_LEGGINGS, 1, 3, 30, 0.05F, TradeRebalanceEnchantmentProviders.TRADES_DESERT_ARMORER_LEGGINGS_5
                     ),
                     VillagerType.DESERT
                  ),
                  VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(
                     new VillagerTrades.ItemsAndEmeraldsToItems(
                        Items.DIAMOND, 3, 16, Items.DIAMOND_LEGGINGS, 1, 3, 30, 0.05F, TradeRebalanceEnchantmentProviders.TRADES_PLAINS_ARMORER_LEGGINGS_5
                     ),
                     VillagerType.PLAINS
                  ),
                  VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(
                     new VillagerTrades.ItemsAndEmeraldsToItems(
                        Items.DIAMOND, 2, 12, Items.DIAMOND_BOOTS, 1, 3, 30, 0.05F, TradeRebalanceEnchantmentProviders.TRADES_PLAINS_ARMORER_BOOTS_5
                     ),
                     VillagerType.PLAINS
                  ),
                  VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(
                     new VillagerTrades.ItemsAndEmeraldsToItems(
                        Items.DIAMOND, 2, 6, Items.DIAMOND_HELMET, 1, 3, 30, 0.05F, TradeRebalanceEnchantmentProviders.TRADES_SAVANNA_ARMORER_HELMET_5
                     ),
                     VillagerType.SAVANNA
                  ),
                  VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(
                     new VillagerTrades.ItemsAndEmeraldsToItems(
                        Items.DIAMOND, 3, 8, Items.DIAMOND_CHESTPLATE, 1, 3, 30, 0.05F, TradeRebalanceEnchantmentProviders.TRADES_SAVANNA_ARMORER_CHESTPLATE_5
                     ),
                     VillagerType.SAVANNA
                  ),
                  VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(
                     new VillagerTrades.ItemsAndEmeraldsToItems(
                        Items.DIAMOND, 2, 12, Items.DIAMOND_BOOTS, 1, 3, 30, 0.05F, TradeRebalanceEnchantmentProviders.TRADES_SNOW_ARMORER_BOOTS_5
                     ),
                     VillagerType.SNOW
                  ),
                  VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(
                     new VillagerTrades.ItemsAndEmeraldsToItems(
                        Items.DIAMOND, 3, 12, Items.DIAMOND_HELMET, 1, 3, 30, 0.05F, TradeRebalanceEnchantmentProviders.TRADES_SNOW_ARMORER_HELMET_5
                     ),
                     VillagerType.SNOW
                  ),
                  VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(
                     new VillagerTrades.ItemsForEmeralds(
                        Items.CHAINMAIL_HELMET, 9, 1, 3, 30, 0.05F, TradeRebalanceEnchantmentProviders.TRADES_JUNGLE_ARMORER_HELMET_5
                     ),
                     VillagerType.JUNGLE
                  ),
                  VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(
                     new VillagerTrades.ItemsForEmeralds(
                        Items.CHAINMAIL_BOOTS, 8, 1, 3, 30, 0.05F, TradeRebalanceEnchantmentProviders.TRADES_JUNGLE_ARMORER_BOOTS_5
                     ),
                     VillagerType.JUNGLE
                  ),
                  VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(
                     new VillagerTrades.ItemsForEmeralds(
                        Items.CHAINMAIL_HELMET, 9, 1, 3, 30, 0.05F, TradeRebalanceEnchantmentProviders.TRADES_SWAMP_ARMORER_HELMET_5
                     ),
                     VillagerType.SWAMP
                  ),
                  VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(
                     new VillagerTrades.ItemsForEmeralds(
                        Items.CHAINMAIL_BOOTS, 8, 1, 3, 30, 0.05F, TradeRebalanceEnchantmentProviders.TRADES_SWAMP_ARMORER_BOOTS_5
                     ),
                     VillagerType.SWAMP
                  ),
                  VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(
                     new VillagerTrades.ItemsAndEmeraldsToItems(
                        Items.DIAMOND, 4, 18, Items.DIAMOND_CHESTPLATE, 1, 3, 30, 0.05F, TradeRebalanceEnchantmentProviders.TRADES_TAIGA_ARMORER_CHESTPLATE_5
                     ),
                     VillagerType.TAIGA
                  ),
                  VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(
                     new VillagerTrades.ItemsAndEmeraldsToItems(
                        Items.DIAMOND, 3, 18, Items.DIAMOND_LEGGINGS, 1, 3, 30, 0.05F, TradeRebalanceEnchantmentProviders.TRADES_TAIGA_ARMORER_LEGGINGS_5
                     ),
                     VillagerType.TAIGA
                  ),
                  VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(new VillagerTrades.EmeraldForItems(Items.DIAMOND_BLOCK, 1, 12, 30, 42), VillagerType.TAIGA),
                  VillagerTrades.TypeSpecificTrade.oneTradeInBiomes(
                     new VillagerTrades.EmeraldForItems(Items.IRON_BLOCK, 1, 12, 30, 4),
                     VillagerType.DESERT,
                     VillagerType.JUNGLE,
                     VillagerType.PLAINS,
                     VillagerType.SAVANNA,
                     VillagerType.SNOW,
                     VillagerType.SWAMP
                  )
               }
            )
            .build()
      ),
      VillagerProfession.CARTOGRAPHER,
      toIntMap(
         ImmutableMap.of(
            1,
            new VillagerTrades.ItemListing[]{
               new VillagerTrades.EmeraldForItems(Items.PAPER, 24, 16, 2), new VillagerTrades.ItemsForEmeralds(Items.MAP, 7, 1, 1)
            },
            2,
            new VillagerTrades.ItemListing[]{
               new VillagerTrades.EmeraldForItems(Items.GLASS_PANE, 11, 16, 10),
               new VillagerTrades.TypeSpecificTrade(
                  ImmutableMap.builder()
                     .put(VillagerType.DESERT, SAVANNA_MAP)
                     .put(VillagerType.SAVANNA, PLAINS_MAP)
                     .put(VillagerType.PLAINS, TAIGA_MAP)
                     .put(VillagerType.TAIGA, SNOWY_MAP)
                     .put(VillagerType.SNOW, PLAINS_MAP)
                     .put(VillagerType.JUNGLE, SAVANNA_MAP)
                     .put(VillagerType.SWAMP, SNOWY_MAP)
                     .build()
               ),
               new VillagerTrades.TypeSpecificTrade(
                  ImmutableMap.builder()
                     .put(VillagerType.DESERT, PLAINS_MAP)
                     .put(VillagerType.SAVANNA, DESERT_MAP)
                     .put(VillagerType.PLAINS, SAVANNA_MAP)
                     .put(VillagerType.TAIGA, PLAINS_MAP)
                     .put(VillagerType.SNOW, TAIGA_MAP)
                     .put(VillagerType.JUNGLE, DESERT_MAP)
                     .put(VillagerType.SWAMP, TAIGA_MAP)
                     .build()
               ),
               new VillagerTrades.TypeSpecificTrade(
                  ImmutableMap.builder()
                     .put(VillagerType.DESERT, JUNGLE_MAP)
                     .put(VillagerType.SAVANNA, JUNGLE_MAP)
                     .put(VillagerType.PLAINS, new VillagerTrades.FailureItemListing())
                     .put(VillagerType.TAIGA, SWAMP_MAP)
                     .put(VillagerType.SNOW, SWAMP_MAP)
                     .put(VillagerType.JUNGLE, SWAMP_MAP)
                     .put(VillagerType.SWAMP, JUNGLE_MAP)
                     .build()
               )
            },
            3,
            new VillagerTrades.ItemListing[]{
               new VillagerTrades.EmeraldForItems(Items.COMPASS, 1, 12, 20),
               new VillagerTrades.TreasureMapForEmeralds(
                  13, StructureTags.ON_OCEAN_EXPLORER_MAPS, "filled_map.monument", MapDecorationTypes.OCEAN_MONUMENT, 12, 10
               ),
               new VillagerTrades.TreasureMapForEmeralds(
                  12, StructureTags.ON_TRIAL_CHAMBERS_MAPS, "filled_map.trial_chambers", MapDecorationTypes.TRIAL_CHAMBERS, 12, 10
               )
            },
            4,
            new VillagerTrades.ItemListing[]{
               new VillagerTrades.ItemsForEmeralds(Items.ITEM_FRAME, 7, 1, 15),
               new VillagerTrades.ItemsForEmeralds(Items.WHITE_BANNER, 3, 1, 15),
               new VillagerTrades.ItemsForEmeralds(Items.BLUE_BANNER, 3, 1, 15),
               new VillagerTrades.ItemsForEmeralds(Items.LIGHT_BLUE_BANNER, 3, 1, 15),
               new VillagerTrades.ItemsForEmeralds(Items.RED_BANNER, 3, 1, 15),
               new VillagerTrades.ItemsForEmeralds(Items.PINK_BANNER, 3, 1, 15),
               new VillagerTrades.ItemsForEmeralds(Items.GREEN_BANNER, 3, 1, 15),
               new VillagerTrades.ItemsForEmeralds(Items.LIME_BANNER, 3, 1, 15),
               new VillagerTrades.ItemsForEmeralds(Items.GRAY_BANNER, 3, 1, 15),
               new VillagerTrades.ItemsForEmeralds(Items.BLACK_BANNER, 3, 1, 15),
               new VillagerTrades.ItemsForEmeralds(Items.PURPLE_BANNER, 3, 1, 15),
               new VillagerTrades.ItemsForEmeralds(Items.MAGENTA_BANNER, 3, 1, 15),
               new VillagerTrades.ItemsForEmeralds(Items.CYAN_BANNER, 3, 1, 15),
               new VillagerTrades.ItemsForEmeralds(Items.BROWN_BANNER, 3, 1, 15),
               new VillagerTrades.ItemsForEmeralds(Items.YELLOW_BANNER, 3, 1, 15),
               new VillagerTrades.ItemsForEmeralds(Items.ORANGE_BANNER, 3, 1, 15),
               new VillagerTrades.ItemsForEmeralds(Items.LIGHT_GRAY_BANNER, 3, 1, 15)
            },
            5,
            new VillagerTrades.ItemListing[]{
               new VillagerTrades.ItemsForEmeralds(Items.GLOBE_BANNER_PATTERN, 8, 1, 30),
               new VillagerTrades.TreasureMapForEmeralds(
                  14, StructureTags.ON_WOODLAND_EXPLORER_MAPS, "filled_map.mansion", MapDecorationTypes.WOODLAND_MANSION, 1, 30
               )
            }
         )
      )
   );
   public static final List<Pair<VillagerTrades.ItemListing[], Integer>> EXPERIMENTAL_WANDERING_TRADER_TRADES = ImmutableList.builder()
      .add(
         Pair.of(
            new VillagerTrades.ItemListing[]{
               new VillagerTrades.EmeraldForItems(potionCost(Potions.WATER), 1, 1, 1),
               new VillagerTrades.EmeraldForItems(Items.WATER_BUCKET, 1, 1, 1, 2),
               new VillagerTrades.EmeraldForItems(Items.MILK_BUCKET, 1, 1, 1, 2),
               new VillagerTrades.EmeraldForItems(Items.FERMENTED_SPIDER_EYE, 1, 1, 1, 3),
               new VillagerTrades.EmeraldForItems(Items.BAKED_POTATO, 4, 1, 1),
               new VillagerTrades.EmeraldForItems(Items.HAY_BLOCK, 1, 1, 1)
            },
            2
         )
      )
      .add(
         Pair.of(
            new VillagerTrades.ItemListing[]{
               new VillagerTrades.ItemsForEmeralds(Items.PACKED_ICE, 1, 1, 6, 1),
               new VillagerTrades.ItemsForEmeralds(Items.BLUE_ICE, 6, 1, 6, 1),
               new VillagerTrades.ItemsForEmeralds(Items.GUNPOWDER, 1, 4, 2, 1),
               new VillagerTrades.ItemsForEmeralds(Items.PODZOL, 3, 3, 6, 1),
               new VillagerTrades.ItemsForEmeralds(Blocks.ACACIA_LOG, 1, 8, 4, 1),
               new VillagerTrades.ItemsForEmeralds(Blocks.BIRCH_LOG, 1, 8, 4, 1),
               new VillagerTrades.ItemsForEmeralds(Blocks.DARK_OAK_LOG, 1, 8, 4, 1),
               new VillagerTrades.ItemsForEmeralds(Blocks.JUNGLE_LOG, 1, 8, 4, 1),
               new VillagerTrades.ItemsForEmeralds(Blocks.OAK_LOG, 1, 8, 4, 1),
               new VillagerTrades.ItemsForEmeralds(Blocks.SPRUCE_LOG, 1, 8, 4, 1),
               new VillagerTrades.ItemsForEmeralds(Blocks.CHERRY_LOG, 1, 8, 4, 1),
               new VillagerTrades.EnchantedItemForEmeralds(Items.IRON_PICKAXE, 1, 1, 1, 0.2F),
               new VillagerTrades.ItemsForEmeralds(potion(Potions.LONG_INVISIBILITY), 5, 1, 1, 1)
            },
            2
         )
      )
      .add(
         Pair.of(
            new VillagerTrades.ItemListing[]{
               new VillagerTrades.ItemsForEmeralds(Items.TROPICAL_FISH_BUCKET, 3, 1, 4, 1),
               new VillagerTrades.ItemsForEmeralds(Items.PUFFERFISH_BUCKET, 3, 1, 4, 1),
               new VillagerTrades.ItemsForEmeralds(Items.SEA_PICKLE, 2, 1, 5, 1),
               new VillagerTrades.ItemsForEmeralds(Items.SLIME_BALL, 4, 1, 5, 1),
               new VillagerTrades.ItemsForEmeralds(Items.GLOWSTONE, 2, 1, 5, 1),
               new VillagerTrades.ItemsForEmeralds(Items.NAUTILUS_SHELL, 5, 1, 5, 1),
               new VillagerTrades.ItemsForEmeralds(Items.FERN, 1, 1, 12, 1),
               new VillagerTrades.ItemsForEmeralds(Items.SUGAR_CANE, 1, 1, 8, 1),
               new VillagerTrades.ItemsForEmeralds(Items.PUMPKIN, 1, 1, 4, 1),
               new VillagerTrades.ItemsForEmeralds(Items.KELP, 3, 1, 12, 1),
               new VillagerTrades.ItemsForEmeralds(Items.CACTUS, 3, 1, 8, 1),
               new VillagerTrades.ItemsForEmeralds(Items.DANDELION, 1, 1, 12, 1),
               new VillagerTrades.ItemsForEmeralds(Items.POPPY, 1, 1, 12, 1),
               new VillagerTrades.ItemsForEmeralds(Items.BLUE_ORCHID, 1, 1, 8, 1),
               new VillagerTrades.ItemsForEmeralds(Items.ALLIUM, 1, 1, 12, 1),
               new VillagerTrades.ItemsForEmeralds(Items.AZURE_BLUET, 1, 1, 12, 1),
               new VillagerTrades.ItemsForEmeralds(Items.RED_TULIP, 1, 1, 12, 1),
               new VillagerTrades.ItemsForEmeralds(Items.ORANGE_TULIP, 1, 1, 12, 1),
               new VillagerTrades.ItemsForEmeralds(Items.WHITE_TULIP, 1, 1, 12, 1),
               new VillagerTrades.ItemsForEmeralds(Items.PINK_TULIP, 1, 1, 12, 1),
               new VillagerTrades.ItemsForEmeralds(Items.OXEYE_DAISY, 1, 1, 12, 1),
               new VillagerTrades.ItemsForEmeralds(Items.CORNFLOWER, 1, 1, 12, 1),
               new VillagerTrades.ItemsForEmeralds(Items.LILY_OF_THE_VALLEY, 1, 1, 7, 1),
               new VillagerTrades.ItemsForEmeralds(Items.WHEAT_SEEDS, 1, 1, 12, 1),
               new VillagerTrades.ItemsForEmeralds(Items.BEETROOT_SEEDS, 1, 1, 12, 1),
               new VillagerTrades.ItemsForEmeralds(Items.PUMPKIN_SEEDS, 1, 1, 12, 1),
               new VillagerTrades.ItemsForEmeralds(Items.MELON_SEEDS, 1, 1, 12, 1),
               new VillagerTrades.ItemsForEmeralds(Items.ACACIA_SAPLING, 5, 1, 8, 1),
               new VillagerTrades.ItemsForEmeralds(Items.BIRCH_SAPLING, 5, 1, 8, 1),
               new VillagerTrades.ItemsForEmeralds(Items.DARK_OAK_SAPLING, 5, 1, 8, 1),
               new VillagerTrades.ItemsForEmeralds(Items.JUNGLE_SAPLING, 5, 1, 8, 1),
               new VillagerTrades.ItemsForEmeralds(Items.OAK_SAPLING, 5, 1, 8, 1),
               new VillagerTrades.ItemsForEmeralds(Items.SPRUCE_SAPLING, 5, 1, 8, 1),
               new VillagerTrades.ItemsForEmeralds(Items.CHERRY_SAPLING, 5, 1, 8, 1),
               new VillagerTrades.ItemsForEmeralds(Items.MANGROVE_PROPAGULE, 5, 1, 8, 1),
               new VillagerTrades.ItemsForEmeralds(Items.RED_DYE, 1, 3, 12, 1),
               new VillagerTrades.ItemsForEmeralds(Items.WHITE_DYE, 1, 3, 12, 1),
               new VillagerTrades.ItemsForEmeralds(Items.BLUE_DYE, 1, 3, 12, 1),
               new VillagerTrades.ItemsForEmeralds(Items.PINK_DYE, 1, 3, 12, 1),
               new VillagerTrades.ItemsForEmeralds(Items.BLACK_DYE, 1, 3, 12, 1),
               new VillagerTrades.ItemsForEmeralds(Items.GREEN_DYE, 1, 3, 12, 1),
               new VillagerTrades.ItemsForEmeralds(Items.LIGHT_GRAY_DYE, 1, 3, 12, 1),
               new VillagerTrades.ItemsForEmeralds(Items.MAGENTA_DYE, 1, 3, 12, 1),
               new VillagerTrades.ItemsForEmeralds(Items.YELLOW_DYE, 1, 3, 12, 1),
               new VillagerTrades.ItemsForEmeralds(Items.GRAY_DYE, 1, 3, 12, 1),
               new VillagerTrades.ItemsForEmeralds(Items.PURPLE_DYE, 1, 3, 12, 1),
               new VillagerTrades.ItemsForEmeralds(Items.LIGHT_BLUE_DYE, 1, 3, 12, 1),
               new VillagerTrades.ItemsForEmeralds(Items.LIME_DYE, 1, 3, 12, 1),
               new VillagerTrades.ItemsForEmeralds(Items.ORANGE_DYE, 1, 3, 12, 1),
               new VillagerTrades.ItemsForEmeralds(Items.BROWN_DYE, 1, 3, 12, 1),
               new VillagerTrades.ItemsForEmeralds(Items.CYAN_DYE, 1, 3, 12, 1),
               new VillagerTrades.ItemsForEmeralds(Items.BRAIN_CORAL_BLOCK, 3, 1, 8, 1),
               new VillagerTrades.ItemsForEmeralds(Items.BUBBLE_CORAL_BLOCK, 3, 1, 8, 1),
               new VillagerTrades.ItemsForEmeralds(Items.FIRE_CORAL_BLOCK, 3, 1, 8, 1),
               new VillagerTrades.ItemsForEmeralds(Items.HORN_CORAL_BLOCK, 3, 1, 8, 1),
               new VillagerTrades.ItemsForEmeralds(Items.TUBE_CORAL_BLOCK, 3, 1, 8, 1),
               new VillagerTrades.ItemsForEmeralds(Items.VINE, 1, 3, 4, 1),
               new VillagerTrades.ItemsForEmeralds(Items.BROWN_MUSHROOM, 1, 3, 4, 1),
               new VillagerTrades.ItemsForEmeralds(Items.RED_MUSHROOM, 1, 3, 4, 1),
               new VillagerTrades.ItemsForEmeralds(Items.LILY_PAD, 1, 5, 2, 1),
               new VillagerTrades.ItemsForEmeralds(Items.SMALL_DRIPLEAF, 1, 2, 5, 1),
               new VillagerTrades.ItemsForEmeralds(Items.SAND, 1, 8, 8, 1),
               new VillagerTrades.ItemsForEmeralds(Items.RED_SAND, 1, 4, 6, 1),
               new VillagerTrades.ItemsForEmeralds(Items.POINTED_DRIPSTONE, 1, 2, 5, 1),
               new VillagerTrades.ItemsForEmeralds(Items.ROOTED_DIRT, 1, 2, 5, 1),
               new VillagerTrades.ItemsForEmeralds(Items.MOSS_BLOCK, 1, 2, 5, 1)
            },
            5
         )
      )
      .build();

   public VillagerTrades() {
      super();
   }

   private static VillagerTrades.ItemListing commonBooks(int var0) {
      return new VillagerTrades.TypeSpecificTrade(
         ImmutableMap.builder()
            .put(VillagerType.DESERT, new VillagerTrades.EnchantBookForEmeralds(var0, EnchantmentTags.TRADES_DESERT_COMMON))
            .put(VillagerType.JUNGLE, new VillagerTrades.EnchantBookForEmeralds(var0, EnchantmentTags.TRADES_JUNGLE_COMMON))
            .put(VillagerType.PLAINS, new VillagerTrades.EnchantBookForEmeralds(var0, EnchantmentTags.TRADES_PLAINS_COMMON))
            .put(VillagerType.SAVANNA, new VillagerTrades.EnchantBookForEmeralds(var0, EnchantmentTags.TRADES_SAVANNA_COMMON))
            .put(VillagerType.SNOW, new VillagerTrades.EnchantBookForEmeralds(var0, EnchantmentTags.TRADES_SNOW_COMMON))
            .put(VillagerType.SWAMP, new VillagerTrades.EnchantBookForEmeralds(var0, EnchantmentTags.TRADES_SWAMP_COMMON))
            .put(VillagerType.TAIGA, new VillagerTrades.EnchantBookForEmeralds(var0, EnchantmentTags.TRADES_TAIGA_COMMON))
            .build()
      );
   }

   private static VillagerTrades.ItemListing specialBooks() {
      return new VillagerTrades.TypeSpecificTrade(
         ImmutableMap.builder()
            .put(VillagerType.DESERT, new VillagerTrades.EnchantBookForEmeralds(30, 3, 3, EnchantmentTags.TRADES_DESERT_SPECIAL))
            .put(VillagerType.JUNGLE, new VillagerTrades.EnchantBookForEmeralds(30, 2, 2, EnchantmentTags.TRADES_JUNGLE_SPECIAL))
            .put(VillagerType.PLAINS, new VillagerTrades.EnchantBookForEmeralds(30, 3, 3, EnchantmentTags.TRADES_PLAINS_SPECIAL))
            .put(VillagerType.SAVANNA, new VillagerTrades.EnchantBookForEmeralds(30, 3, 3, EnchantmentTags.TRADES_SAVANNA_SPECIAL))
            .put(VillagerType.SNOW, new VillagerTrades.EnchantBookForEmeralds(30, EnchantmentTags.TRADES_SNOW_SPECIAL))
            .put(VillagerType.SWAMP, new VillagerTrades.EnchantBookForEmeralds(30, EnchantmentTags.TRADES_SWAMP_SPECIAL))
            .put(VillagerType.TAIGA, new VillagerTrades.EnchantBookForEmeralds(30, 2, 2, EnchantmentTags.TRADES_TAIGA_SPECIAL))
            .build()
      );
   }

   private static Int2ObjectMap<VillagerTrades.ItemListing[]> toIntMap(ImmutableMap<Integer, VillagerTrades.ItemListing[]> var0) {
      return new Int2ObjectOpenHashMap(var0);
   }

   private static ItemCost potionCost(Holder<Potion> var0) {
      return new ItemCost(Items.POTION).withComponents(var1 -> var1.expect(DataComponents.POTION_CONTENTS, new PotionContents(var0)));
   }

   private static ItemStack potion(Holder<Potion> var0) {
      return PotionContents.createItemStack(Items.POTION, var0);
   }

   static class DyedArmorForEmeralds implements VillagerTrades.ItemListing {
      private final Item item;
      private final int value;
      private final int maxUses;
      private final int villagerXp;

      public DyedArmorForEmeralds(Item var1, int var2) {
         this(var1, var2, 12, 1);
      }

      public DyedArmorForEmeralds(Item var1, int var2, int var3, int var4) {
         super();
         this.item = var1;
         this.value = var2;
         this.maxUses = var3;
         this.villagerXp = var4;
      }

      @Override
      public MerchantOffer getOffer(Entity var1, RandomSource var2) {
         ItemCost var3 = new ItemCost(Items.EMERALD, this.value);
         ItemStack var4 = new ItemStack(this.item);
         if (var4.is(ItemTags.DYEABLE)) {
            ArrayList var5 = Lists.newArrayList();
            var5.add(getRandomDye(var2));
            if (var2.nextFloat() > 0.7F) {
               var5.add(getRandomDye(var2));
            }

            if (var2.nextFloat() > 0.8F) {
               var5.add(getRandomDye(var2));
            }

            var4 = DyedItemColor.applyDyes(var4, var5);
         }

         return new MerchantOffer(var3, var4, this.maxUses, this.villagerXp, 0.2F);
      }

      private static DyeItem getRandomDye(RandomSource var0) {
         return DyeItem.byColor(DyeColor.byId(var0.nextInt(16)));
      }
   }

   static class EmeraldForItems implements VillagerTrades.ItemListing {
      private final ItemCost itemStack;
      private final int maxUses;
      private final int villagerXp;
      private final int emeraldAmount;
      private final float priceMultiplier;

      public EmeraldForItems(ItemLike var1, int var2, int var3, int var4) {
         this(var1, var2, var3, var4, 1);
      }

      public EmeraldForItems(ItemLike var1, int var2, int var3, int var4, int var5) {
         this(new ItemCost(var1.asItem(), var2), var3, var4, var5);
      }

      public EmeraldForItems(ItemCost var1, int var2, int var3, int var4) {
         super();
         this.itemStack = var1;
         this.maxUses = var2;
         this.villagerXp = var3;
         this.emeraldAmount = var4;
         this.priceMultiplier = 0.05F;
      }

      @Override
      public MerchantOffer getOffer(Entity var1, RandomSource var2) {
         return new MerchantOffer(this.itemStack, new ItemStack(Items.EMERALD, this.emeraldAmount), this.maxUses, this.villagerXp, this.priceMultiplier);
      }
   }

   static class EmeraldsForVillagerTypeItem implements VillagerTrades.ItemListing {
      private final Map<VillagerType, Item> trades;
      private final int cost;
      private final int maxUses;
      private final int villagerXp;

      public EmeraldsForVillagerTypeItem(int var1, int var2, int var3, Map<VillagerType, Item> var4) {
         super();
         BuiltInRegistries.VILLAGER_TYPE.stream().filter(var1x -> !var4.containsKey(var1x)).findAny().ifPresent(var0 -> {
            throw new IllegalStateException("Missing trade for villager type: " + BuiltInRegistries.VILLAGER_TYPE.getKey(var0));
         });
         this.trades = var4;
         this.cost = var1;
         this.maxUses = var2;
         this.villagerXp = var3;
      }

      @Nullable
      @Override
      public MerchantOffer getOffer(Entity var1, RandomSource var2) {
         if (var1 instanceof VillagerDataHolder var3) {
            ItemCost var4 = new ItemCost(this.trades.get(var3.getVillagerData().getType()), this.cost);
            return new MerchantOffer(var4, new ItemStack(Items.EMERALD), this.maxUses, this.villagerXp, 0.05F);
         } else {
            return null;
         }
      }
   }

   static class EnchantBookForEmeralds implements VillagerTrades.ItemListing {
      private final int villagerXp;
      private final TagKey<Enchantment> tradeableEnchantments;
      private final int minLevel;
      private final int maxLevel;

      public EnchantBookForEmeralds(int var1, TagKey<Enchantment> var2) {
         this(var1, 0, 2147483647, var2);
      }

      public EnchantBookForEmeralds(int var1, int var2, int var3, TagKey<Enchantment> var4) {
         super();
         this.minLevel = var2;
         this.maxLevel = var3;
         this.villagerXp = var1;
         this.tradeableEnchantments = var4;
      }

      @Override
      public MerchantOffer getOffer(Entity var1, RandomSource var2) {
         Optional var3 = var1.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getRandomElementOf(this.tradeableEnchantments, var2);
         int var4;
         ItemStack var5;
         if (!var3.isEmpty()) {
            Holder var6 = (Holder)var3.get();
            Enchantment var7 = (Enchantment)var6.value();
            int var8 = Math.max(var7.getMinLevel(), this.minLevel);
            int var9 = Math.min(var7.getMaxLevel(), this.maxLevel);
            int var10 = Mth.nextInt(var2, var8, var9);
            var5 = EnchantmentHelper.createBook(new EnchantmentInstance(var6, var10));
            var4 = 2 + var2.nextInt(5 + var10 * 10) + 3 * var10;
            if (var6.is(EnchantmentTags.DOUBLE_TRADE_PRICE)) {
               var4 *= 2;
            }

            if (var4 > 64) {
               var4 = 64;
            }
         } else {
            var4 = 1;
            var5 = new ItemStack(Items.BOOK);
         }

         return new MerchantOffer(new ItemCost(Items.EMERALD, var4), Optional.of(new ItemCost(Items.BOOK)), var5, 12, this.villagerXp, 0.2F);
      }
   }

   static class EnchantedItemForEmeralds implements VillagerTrades.ItemListing {
      private final ItemStack itemStack;
      private final int baseEmeraldCost;
      private final int maxUses;
      private final int villagerXp;
      private final float priceMultiplier;

      public EnchantedItemForEmeralds(Item var1, int var2, int var3, int var4) {
         this(var1, var2, var3, var4, 0.05F);
      }

      public EnchantedItemForEmeralds(Item var1, int var2, int var3, int var4, float var5) {
         super();
         this.itemStack = new ItemStack(var1);
         this.baseEmeraldCost = var2;
         this.maxUses = var3;
         this.villagerXp = var4;
         this.priceMultiplier = var5;
      }

      @Override
      public MerchantOffer getOffer(Entity var1, RandomSource var2) {
         int var3 = 5 + var2.nextInt(15);
         RegistryAccess var4 = var1.level().registryAccess();
         Optional var5 = var4.lookupOrThrow(Registries.ENCHANTMENT).get(EnchantmentTags.ON_TRADED_EQUIPMENT);
         ItemStack var6 = EnchantmentHelper.enchantItem(var2, new ItemStack(this.itemStack.getItem()), var3, var4, var5);
         int var7 = Math.min(this.baseEmeraldCost + var3, 64);
         ItemCost var8 = new ItemCost(Items.EMERALD, var7);
         return new MerchantOffer(var8, var6, this.maxUses, this.villagerXp, this.priceMultiplier);
      }
   }

   static class FailureItemListing implements VillagerTrades.ItemListing {
      FailureItemListing() {
         super();
      }

      @Override
      public MerchantOffer getOffer(Entity var1, RandomSource var2) {
         return null;
      }
   }

   public interface ItemListing {
      @Nullable
      MerchantOffer getOffer(Entity var1, RandomSource var2);
   }

   static class ItemsAndEmeraldsToItems implements VillagerTrades.ItemListing {
      private final ItemCost fromItem;
      private final int emeraldCost;
      private final ItemStack toItem;
      private final int maxUses;
      private final int villagerXp;
      private final float priceMultiplier;
      private final Optional<ResourceKey<EnchantmentProvider>> enchantmentProvider;

      public ItemsAndEmeraldsToItems(ItemLike var1, int var2, int var3, Item var4, int var5, int var6, int var7, float var8) {
         this(var1, var2, var3, new ItemStack(var4), var5, var6, var7, var8);
      }

      private ItemsAndEmeraldsToItems(ItemLike var1, int var2, int var3, ItemStack var4, int var5, int var6, int var7, float var8) {
         this(new ItemCost(var1, var2), var3, var4.copyWithCount(var5), var6, var7, var8, Optional.empty());
      }

      ItemsAndEmeraldsToItems(ItemLike var1, int var2, int var3, ItemLike var4, int var5, int var6, int var7, float var8, ResourceKey<EnchantmentProvider> var9) {
         this(new ItemCost(var1, var2), var3, new ItemStack(var4, var5), var6, var7, var8, Optional.of(var9));
      }

      public ItemsAndEmeraldsToItems(ItemCost var1, int var2, ItemStack var3, int var4, int var5, float var6, Optional<ResourceKey<EnchantmentProvider>> var7) {
         super();
         this.fromItem = var1;
         this.emeraldCost = var2;
         this.toItem = var3;
         this.maxUses = var4;
         this.villagerXp = var5;
         this.priceMultiplier = var6;
         this.enchantmentProvider = var7;
      }

      @Nullable
      @Override
      public MerchantOffer getOffer(Entity var1, RandomSource var2) {
         ItemStack var3 = this.toItem.copy();
         Level var4 = var1.level();
         this.enchantmentProvider
            .ifPresent(
               var4x -> EnchantmentHelper.enchantItemFromProvider(
                     var3, var4.registryAccess(), (ResourceKey<EnchantmentProvider>)var4x, var4.getCurrentDifficultyAt(var1.blockPosition()), var2
                  )
            );
         return new MerchantOffer(
            new ItemCost(Items.EMERALD, this.emeraldCost), Optional.of(this.fromItem), var3, 0, this.maxUses, this.villagerXp, this.priceMultiplier
         );
      }
   }

   static class ItemsForEmeralds implements VillagerTrades.ItemListing {
      private final ItemStack itemStack;
      private final int emeraldCost;
      private final int maxUses;
      private final int villagerXp;
      private final float priceMultiplier;
      private final Optional<ResourceKey<EnchantmentProvider>> enchantmentProvider;

      public ItemsForEmeralds(Block var1, int var2, int var3, int var4, int var5) {
         this(new ItemStack(var1), var2, var3, var4, var5);
      }

      public ItemsForEmeralds(Item var1, int var2, int var3, int var4) {
         this(new ItemStack(var1), var2, var3, 12, var4);
      }

      public ItemsForEmeralds(Item var1, int var2, int var3, int var4, int var5) {
         this(new ItemStack(var1), var2, var3, var4, var5);
      }

      public ItemsForEmeralds(ItemStack var1, int var2, int var3, int var4, int var5) {
         this(var1, var2, var3, var4, var5, 0.05F);
      }

      public ItemsForEmeralds(Item var1, int var2, int var3, int var4, int var5, float var6) {
         this(new ItemStack(var1), var2, var3, var4, var5, var6);
      }

      public ItemsForEmeralds(Item var1, int var2, int var3, int var4, int var5, float var6, ResourceKey<EnchantmentProvider> var7) {
         this(new ItemStack(var1), var2, var3, var4, var5, var6, Optional.of(var7));
      }

      public ItemsForEmeralds(ItemStack var1, int var2, int var3, int var4, int var5, float var6) {
         this(var1, var2, var3, var4, var5, var6, Optional.empty());
      }

      public ItemsForEmeralds(ItemStack var1, int var2, int var3, int var4, int var5, float var6, Optional<ResourceKey<EnchantmentProvider>> var7) {
         super();
         this.itemStack = var1;
         this.emeraldCost = var2;
         this.itemStack.setCount(var3);
         this.maxUses = var4;
         this.villagerXp = var5;
         this.priceMultiplier = var6;
         this.enchantmentProvider = var7;
      }

      @Override
      public MerchantOffer getOffer(Entity var1, RandomSource var2) {
         ItemStack var3 = this.itemStack.copy();
         Level var4 = var1.level();
         this.enchantmentProvider
            .ifPresent(
               var4x -> EnchantmentHelper.enchantItemFromProvider(
                     var3, var4.registryAccess(), (ResourceKey<EnchantmentProvider>)var4x, var4.getCurrentDifficultyAt(var1.blockPosition()), var2
                  )
            );
         return new MerchantOffer(new ItemCost(Items.EMERALD, this.emeraldCost), var3, this.maxUses, this.villagerXp, this.priceMultiplier);
      }
   }

   static class SuspiciousStewForEmerald implements VillagerTrades.ItemListing {
      private final SuspiciousStewEffects effects;
      private final int xp;
      private final float priceMultiplier;

      public SuspiciousStewForEmerald(Holder<MobEffect> var1, int var2, int var3) {
         this(new SuspiciousStewEffects(List.of(new SuspiciousStewEffects.Entry(var1, var2))), var3, 0.05F);
      }

      public SuspiciousStewForEmerald(SuspiciousStewEffects var1, int var2, float var3) {
         super();
         this.effects = var1;
         this.xp = var2;
         this.priceMultiplier = var3;
      }

      @Nullable
      @Override
      public MerchantOffer getOffer(Entity var1, RandomSource var2) {
         ItemStack var3 = new ItemStack(Items.SUSPICIOUS_STEW, 1);
         var3.set(DataComponents.SUSPICIOUS_STEW_EFFECTS, this.effects);
         return new MerchantOffer(new ItemCost(Items.EMERALD), var3, 12, this.xp, this.priceMultiplier);
      }
   }

   static class TippedArrowForItemsAndEmeralds implements VillagerTrades.ItemListing {
      private final ItemStack toItem;
      private final int toCount;
      private final int emeraldCost;
      private final int maxUses;
      private final int villagerXp;
      private final Item fromItem;
      private final int fromCount;
      private final float priceMultiplier;

      public TippedArrowForItemsAndEmeralds(Item var1, int var2, Item var3, int var4, int var5, int var6, int var7) {
         super();
         this.toItem = new ItemStack(var3);
         this.emeraldCost = var5;
         this.maxUses = var6;
         this.villagerXp = var7;
         this.fromItem = var1;
         this.fromCount = var2;
         this.toCount = var4;
         this.priceMultiplier = 0.05F;
      }

      @Override
      public MerchantOffer getOffer(Entity var1, RandomSource var2) {
         ItemCost var3 = new ItemCost(Items.EMERALD, this.emeraldCost);
         List var4 = BuiltInRegistries.POTION
            .listElements()
            .filter(var1x -> !var1x.value().getEffects().isEmpty() && var1.level().potionBrewing().isBrewablePotion(var1x))
            .collect(Collectors.toList());
         Holder var5 = Util.getRandom(var4, var2);
         ItemStack var6 = new ItemStack(this.toItem.getItem(), this.toCount);
         var6.set(DataComponents.POTION_CONTENTS, new PotionContents(var5));
         return new MerchantOffer(var3, Optional.of(new ItemCost(this.fromItem, this.fromCount)), var6, this.maxUses, this.villagerXp, this.priceMultiplier);
      }
   }

   static class TreasureMapForEmeralds implements VillagerTrades.ItemListing {
      private final int emeraldCost;
      private final TagKey<Structure> destination;
      private final String displayName;
      private final Holder<MapDecorationType> destinationType;
      private final int maxUses;
      private final int villagerXp;

      public TreasureMapForEmeralds(int var1, TagKey<Structure> var2, String var3, Holder<MapDecorationType> var4, int var5, int var6) {
         super();
         this.emeraldCost = var1;
         this.destination = var2;
         this.displayName = var3;
         this.destinationType = var4;
         this.maxUses = var5;
         this.villagerXp = var6;
      }

      @Nullable
      @Override
      public MerchantOffer getOffer(Entity var1, RandomSource var2) {
         if (!(var1.level() instanceof ServerLevel)) {
            return null;
         } else {
            ServerLevel var3 = (ServerLevel)var1.level();
            BlockPos var4 = var3.findNearestMapStructure(this.destination, var1.blockPosition(), 100, true);
            if (var4 != null) {
               ItemStack var5 = MapItem.create(var3, var4.getX(), var4.getZ(), (byte)2, true, true);
               MapItem.renderBiomePreviewMap(var3, var5);
               MapItemSavedData.addTargetDecoration(var5, var4, "+", this.destinationType);
               var5.set(DataComponents.ITEM_NAME, Component.translatable(this.displayName));
               return new MerchantOffer(
                  new ItemCost(Items.EMERALD, this.emeraldCost), Optional.of(new ItemCost(Items.COMPASS)), var5, this.maxUses, this.villagerXp, 0.2F
               );
            } else {
               return null;
            }
         }
      }
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "varName" is null
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
