package net.minecraft.tags;

import java.util.Collection;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class ItemTags {
   private static TagCollection<Item> field_199906_c = new TagCollection((var0) -> {
      return false;
   }, (var0) -> {
      return null;
   }, "", false, "");
   private static int field_199907_d;
   public static final Tag<Item> field_199904_a = func_199901_a("wool");
   public static final Tag<Item> field_199905_b = func_199901_a("planks");
   public static final Tag<Item> field_200033_c = func_199901_a("stone_bricks");
   public static final Tag<Item> field_200153_d = func_199901_a("wooden_buttons");
   public static final Tag<Item> field_200034_d = func_199901_a("buttons");
   public static final Tag<Item> field_200035_e = func_199901_a("carpets");
   public static final Tag<Item> field_200154_g = func_199901_a("wooden_doors");
   public static final Tag<Item> field_202898_h = func_199901_a("wooden_stairs");
   public static final Tag<Item> field_202899_i = func_199901_a("wooden_slabs");
   public static final Tag<Item> field_202900_j = func_199901_a("wooden_pressure_plates");
   public static final Tag<Item> field_212188_k = func_199901_a("wooden_trapdoors");
   public static final Tag<Item> field_200036_f = func_199901_a("doors");
   public static final Tag<Item> field_200037_g = func_199901_a("saplings");
   public static final Tag<Item> field_200038_h = func_199901_a("logs");
   public static final Tag<Item> field_203294_n = func_199901_a("dark_oak_logs");
   public static final Tag<Item> field_203295_o = func_199901_a("oak_logs");
   public static final Tag<Item> field_203296_p = func_199901_a("birch_logs");
   public static final Tag<Item> field_203297_q = func_199901_a("acacia_logs");
   public static final Tag<Item> field_203298_r = func_199901_a("jungle_logs");
   public static final Tag<Item> field_203299_s = func_199901_a("spruce_logs");
   public static final Tag<Item> field_202901_n = func_199901_a("banners");
   public static final Tag<Item> field_203440_u = func_199901_a("sand");
   public static final Tag<Item> field_203441_v = func_199901_a("stairs");
   public static final Tag<Item> field_203442_w = func_199901_a("slabs");
   public static final Tag<Item> field_203443_x = func_199901_a("anvil");
   public static final Tag<Item> field_203444_y = func_199901_a("rails");
   public static final Tag<Item> field_206963_E = func_199901_a("leaves");
   public static final Tag<Item> field_212187_B = func_199901_a("trapdoors");
   public static final Tag<Item> field_202902_o = func_199901_a("boats");
   public static final Tag<Item> field_206964_G = func_199901_a("fishes");

   public static void func_199902_a(TagCollection<Item> var0) {
      field_199906_c = var0;
      ++field_199907_d;
   }

   public static TagCollection<Item> func_199903_a() {
      return field_199906_c;
   }

   private static Tag<Item> func_199901_a(String var0) {
      return new ItemTags.Wrapper(new ResourceLocation(var0));
   }

   public static class Wrapper extends Tag<Item> {
      private int field_199890_a = -1;
      private Tag<Item> field_199891_b;

      public Wrapper(ResourceLocation var1) {
         super(var1);
      }

      public boolean func_199685_a_(Item var1) {
         if (this.field_199890_a != ItemTags.field_199907_d) {
            this.field_199891_b = ItemTags.field_199906_c.func_199915_b(this.func_199886_b());
            this.field_199890_a = ItemTags.field_199907_d;
         }

         return this.field_199891_b.func_199685_a_(var1);
      }

      public Collection<Item> func_199885_a() {
         if (this.field_199890_a != ItemTags.field_199907_d) {
            this.field_199891_b = ItemTags.field_199906_c.func_199915_b(this.func_199886_b());
            this.field_199890_a = ItemTags.field_199907_d;
         }

         return this.field_199891_b.func_199885_a();
      }

      public Collection<Tag.ITagEntry<Item>> func_200570_b() {
         if (this.field_199890_a != ItemTags.field_199907_d) {
            this.field_199891_b = ItemTags.field_199906_c.func_199915_b(this.func_199886_b());
            this.field_199890_a = ItemTags.field_199907_d;
         }

         return this.field_199891_b.func_200570_b();
      }
   }
}
