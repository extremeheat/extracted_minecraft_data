package net.minecraft.advancements.criterion;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;

public class ItemPredicate {
   public static final ItemPredicate field_192495_a = new ItemPredicate();
   @Nullable
   private final Tag<Item> field_200018_b;
   @Nullable
   private final Item field_192496_b;
   private final MinMaxBounds.IntBound field_192498_d;
   private final MinMaxBounds.IntBound field_193444_e;
   private final EnchantmentPredicate[] field_192499_e;
   @Nullable
   private final PotionType field_192500_f;
   private final NBTPredicate field_193445_h;

   public ItemPredicate() {
      super();
      this.field_200018_b = null;
      this.field_192496_b = null;
      this.field_192500_f = null;
      this.field_192498_d = MinMaxBounds.IntBound.field_211347_e;
      this.field_193444_e = MinMaxBounds.IntBound.field_211347_e;
      this.field_192499_e = new EnchantmentPredicate[0];
      this.field_193445_h = NBTPredicate.field_193479_a;
   }

   public ItemPredicate(@Nullable Tag<Item> var1, @Nullable Item var2, MinMaxBounds.IntBound var3, MinMaxBounds.IntBound var4, EnchantmentPredicate[] var5, @Nullable PotionType var6, NBTPredicate var7) {
      super();
      this.field_200018_b = var1;
      this.field_192496_b = var2;
      this.field_192498_d = var3;
      this.field_193444_e = var4;
      this.field_192499_e = var5;
      this.field_192500_f = var6;
      this.field_193445_h = var7;
   }

   public boolean func_192493_a(ItemStack var1) {
      if (this.field_200018_b != null && !this.field_200018_b.func_199685_a_(var1.func_77973_b())) {
         return false;
      } else if (this.field_192496_b != null && var1.func_77973_b() != this.field_192496_b) {
         return false;
      } else if (!this.field_192498_d.func_211339_d(var1.func_190916_E())) {
         return false;
      } else if (!this.field_193444_e.func_211335_c() && !var1.func_77984_f()) {
         return false;
      } else if (!this.field_193444_e.func_211339_d(var1.func_77958_k() - var1.func_77952_i())) {
         return false;
      } else if (!this.field_193445_h.func_193478_a(var1)) {
         return false;
      } else {
         Map var2 = EnchantmentHelper.func_82781_a(var1);

         for(int var3 = 0; var3 < this.field_192499_e.length; ++var3) {
            if (!this.field_192499_e[var3].func_192463_a(var2)) {
               return false;
            }
         }

         PotionType var4 = PotionUtils.func_185191_c(var1);
         if (this.field_192500_f != null && this.field_192500_f != var4) {
            return false;
         } else {
            return true;
         }
      }
   }

   public static ItemPredicate func_192492_a(@Nullable JsonElement var0) {
      if (var0 != null && !var0.isJsonNull()) {
         JsonObject var1 = JsonUtils.func_151210_l(var0, "item");
         MinMaxBounds.IntBound var2 = MinMaxBounds.IntBound.func_211344_a(var1.get("count"));
         MinMaxBounds.IntBound var3 = MinMaxBounds.IntBound.func_211344_a(var1.get("durability"));
         if (var1.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
         } else {
            NBTPredicate var4 = NBTPredicate.func_193476_a(var1.get("nbt"));
            Item var5 = null;
            if (var1.has("item")) {
               ResourceLocation var6 = new ResourceLocation(JsonUtils.func_151200_h(var1, "item"));
               var5 = (Item)IRegistry.field_212630_s.func_212608_b(var6);
               if (var5 == null) {
                  throw new JsonSyntaxException("Unknown item id '" + var6 + "'");
               }
            }

            Tag var10 = null;
            if (var1.has("tag")) {
               ResourceLocation var7 = new ResourceLocation(JsonUtils.func_151200_h(var1, "tag"));
               var10 = ItemTags.func_199903_a().func_199910_a(var7);
               if (var10 == null) {
                  throw new JsonSyntaxException("Unknown item tag '" + var7 + "'");
               }
            }

            EnchantmentPredicate[] var11 = EnchantmentPredicate.func_192465_b(var1.get("enchantments"));
            PotionType var8 = null;
            if (var1.has("potion")) {
               ResourceLocation var9 = new ResourceLocation(JsonUtils.func_151200_h(var1, "potion"));
               if (!IRegistry.field_212621_j.func_212607_c(var9)) {
                  throw new JsonSyntaxException("Unknown potion '" + var9 + "'");
               }

               var8 = (PotionType)IRegistry.field_212621_j.func_82594_a(var9);
            }

            return new ItemPredicate(var10, var5, var2, var3, var11, var8, var4);
         }
      } else {
         return field_192495_a;
      }
   }

   public JsonElement func_200319_a() {
      if (this == field_192495_a) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject var1 = new JsonObject();
         if (this.field_192496_b != null) {
            var1.addProperty("item", IRegistry.field_212630_s.func_177774_c(this.field_192496_b).toString());
         }

         if (this.field_200018_b != null) {
            var1.addProperty("tag", this.field_200018_b.func_199886_b().toString());
         }

         var1.add("count", this.field_192498_d.func_200321_c());
         var1.add("durability", this.field_193444_e.func_200321_c());
         var1.add("nbt", this.field_193445_h.func_200322_a());
         if (this.field_192499_e.length > 0) {
            JsonArray var2 = new JsonArray();
            EnchantmentPredicate[] var3 = this.field_192499_e;
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               EnchantmentPredicate var6 = var3[var5];
               var2.add(var6.func_200306_a());
            }

            var1.add("enchantments", var2);
         }

         if (this.field_192500_f != null) {
            var1.addProperty("potion", IRegistry.field_212621_j.func_177774_c(this.field_192500_f).toString());
         }

         return var1;
      }
   }

   public static ItemPredicate[] func_192494_b(@Nullable JsonElement var0) {
      if (var0 != null && !var0.isJsonNull()) {
         JsonArray var1 = JsonUtils.func_151207_m(var0, "items");
         ItemPredicate[] var2 = new ItemPredicate[var1.size()];

         for(int var3 = 0; var3 < var2.length; ++var3) {
            var2[var3] = func_192492_a(var1.get(var3));
         }

         return var2;
      } else {
         return new ItemPredicate[0];
      }
   }

   public static class Builder {
      private final List<EnchantmentPredicate> field_200312_a = Lists.newArrayList();
      @Nullable
      private Item field_200313_b;
      @Nullable
      private Tag<Item> field_200314_c;
      private MinMaxBounds.IntBound field_200315_d;
      private MinMaxBounds.IntBound field_200316_e;
      @Nullable
      private PotionType field_200317_f;
      private NBTPredicate field_200318_g;

      private Builder() {
         super();
         this.field_200315_d = MinMaxBounds.IntBound.field_211347_e;
         this.field_200316_e = MinMaxBounds.IntBound.field_211347_e;
         this.field_200318_g = NBTPredicate.field_193479_a;
      }

      public static ItemPredicate.Builder func_200309_a() {
         return new ItemPredicate.Builder();
      }

      public ItemPredicate.Builder func_200308_a(IItemProvider var1) {
         this.field_200313_b = var1.func_199767_j();
         return this;
      }

      public ItemPredicate.Builder func_200307_a(Tag<Item> var1) {
         this.field_200314_c = var1;
         return this;
      }

      public ItemPredicate.Builder func_200311_a(MinMaxBounds.IntBound var1) {
         this.field_200315_d = var1;
         return this;
      }

      public ItemPredicate func_200310_b() {
         return new ItemPredicate(this.field_200314_c, this.field_200313_b, this.field_200315_d, this.field_200316_e, (EnchantmentPredicate[])this.field_200312_a.toArray(new EnchantmentPredicate[0]), this.field_200317_f, this.field_200318_g);
      }
   }
}
