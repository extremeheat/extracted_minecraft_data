package net.minecraft.tags;

import java.util.Collection;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class BlockTags {
   private static TagCollection source = new TagCollection((var0) -> {
      return Optional.empty();
   }, "", false, "");
   private static int resetCount;
   public static final Tag WOOL = bind("wool");
   public static final Tag PLANKS = bind("planks");
   public static final Tag STONE_BRICKS = bind("stone_bricks");
   public static final Tag WOODEN_BUTTONS = bind("wooden_buttons");
   public static final Tag BUTTONS = bind("buttons");
   public static final Tag CARPETS = bind("carpets");
   public static final Tag WOODEN_DOORS = bind("wooden_doors");
   public static final Tag WOODEN_STAIRS = bind("wooden_stairs");
   public static final Tag WOODEN_SLABS = bind("wooden_slabs");
   public static final Tag WOODEN_FENCES = bind("wooden_fences");
   public static final Tag WOODEN_PRESSURE_PLATES = bind("wooden_pressure_plates");
   public static final Tag WOODEN_TRAPDOORS = bind("wooden_trapdoors");
   public static final Tag DOORS = bind("doors");
   public static final Tag SAPLINGS = bind("saplings");
   public static final Tag LOGS = bind("logs");
   public static final Tag DARK_OAK_LOGS = bind("dark_oak_logs");
   public static final Tag OAK_LOGS = bind("oak_logs");
   public static final Tag BIRCH_LOGS = bind("birch_logs");
   public static final Tag ACACIA_LOGS = bind("acacia_logs");
   public static final Tag JUNGLE_LOGS = bind("jungle_logs");
   public static final Tag SPRUCE_LOGS = bind("spruce_logs");
   public static final Tag BANNERS = bind("banners");
   public static final Tag SAND = bind("sand");
   public static final Tag STAIRS = bind("stairs");
   public static final Tag SLABS = bind("slabs");
   public static final Tag WALLS = bind("walls");
   public static final Tag ANVIL = bind("anvil");
   public static final Tag RAILS = bind("rails");
   public static final Tag LEAVES = bind("leaves");
   public static final Tag TRAPDOORS = bind("trapdoors");
   public static final Tag SMALL_FLOWERS = bind("small_flowers");
   public static final Tag BEDS = bind("beds");
   public static final Tag FENCES = bind("fences");
   public static final Tag TALL_FLOWERS = bind("tall_flowers");
   public static final Tag FLOWERS = bind("flowers");
   public static final Tag SHULKER_BOXES = bind("shulker_boxes");
   public static final Tag FLOWER_POTS = bind("flower_pots");
   public static final Tag ENDERMAN_HOLDABLE = bind("enderman_holdable");
   public static final Tag ICE = bind("ice");
   public static final Tag VALID_SPAWN = bind("valid_spawn");
   public static final Tag IMPERMEABLE = bind("impermeable");
   public static final Tag UNDERWATER_BONEMEALS = bind("underwater_bonemeals");
   public static final Tag CORAL_BLOCKS = bind("coral_blocks");
   public static final Tag WALL_CORALS = bind("wall_corals");
   public static final Tag CORAL_PLANTS = bind("coral_plants");
   public static final Tag CORALS = bind("corals");
   public static final Tag BAMBOO_PLANTABLE_ON = bind("bamboo_plantable_on");
   public static final Tag STANDING_SIGNS = bind("standing_signs");
   public static final Tag WALL_SIGNS = bind("wall_signs");
   public static final Tag SIGNS = bind("signs");
   public static final Tag DRAGON_IMMUNE = bind("dragon_immune");
   public static final Tag WITHER_IMMUNE = bind("wither_immune");
   public static final Tag BEEHIVES = bind("beehives");
   public static final Tag CROPS = bind("crops");
   public static final Tag BEE_GROWABLES = bind("bee_growables");
   public static final Tag PORTALS = bind("portals");

   public static void reset(TagCollection var0) {
      source = var0;
      ++resetCount;
   }

   public static TagCollection getAllTags() {
      return source;
   }

   private static Tag bind(String var0) {
      return new BlockTags.Wrapper(new ResourceLocation(var0));
   }

   static class Wrapper extends Tag {
      private int check = -1;
      private Tag actual;

      public Wrapper(ResourceLocation var1) {
         super(var1);
      }

      public boolean contains(Block var1) {
         if (this.check != BlockTags.resetCount) {
            this.actual = BlockTags.source.getTagOrEmpty(this.getId());
            this.check = BlockTags.resetCount;
         }

         return this.actual.contains(var1);
      }

      public Collection getValues() {
         if (this.check != BlockTags.resetCount) {
            this.actual = BlockTags.source.getTagOrEmpty(this.getId());
            this.check = BlockTags.resetCount;
         }

         return this.actual.getValues();
      }

      public Collection getSource() {
         if (this.check != BlockTags.resetCount) {
            this.actual = BlockTags.source.getTagOrEmpty(this.getId());
            this.check = BlockTags.resetCount;
         }

         return this.actual.getSource();
      }
   }
}
