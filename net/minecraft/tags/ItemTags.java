package net.minecraft.tags;

import java.util.Collection;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class ItemTags {
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
   public static final Tag BOATS = bind("boats");
   public static final Tag FISHES = bind("fishes");
   public static final Tag SIGNS = bind("signs");
   public static final Tag MUSIC_DISCS = bind("music_discs");
   public static final Tag COALS = bind("coals");
   public static final Tag ARROWS = bind("arrows");
   public static final Tag LECTERN_BOOKS = bind("lectern_books");

   public static void reset(TagCollection var0) {
      source = var0;
      ++resetCount;
   }

   public static TagCollection getAllTags() {
      return source;
   }

   private static Tag bind(String var0) {
      return new ItemTags.Wrapper(new ResourceLocation(var0));
   }

   public static class Wrapper extends Tag {
      private int check = -1;
      private Tag actual;

      public Wrapper(ResourceLocation var1) {
         super(var1);
      }

      public boolean contains(Item var1) {
         if (this.check != ItemTags.resetCount) {
            this.actual = ItemTags.source.getTagOrEmpty(this.getId());
            this.check = ItemTags.resetCount;
         }

         return this.actual.contains(var1);
      }

      public Collection getValues() {
         if (this.check != ItemTags.resetCount) {
            this.actual = ItemTags.source.getTagOrEmpty(this.getId());
            this.check = ItemTags.resetCount;
         }

         return this.actual.getValues();
      }

      public Collection getSource() {
         if (this.check != ItemTags.resetCount) {
            this.actual = ItemTags.source.getTagOrEmpty(this.getId());
            this.check = ItemTags.resetCount;
         }

         return this.actual.getSource();
      }
   }
}
