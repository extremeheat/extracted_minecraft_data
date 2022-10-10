package net.minecraft.data;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ShapedRecipeBuilder {
   private static final Logger field_200474_a = LogManager.getLogger();
   private final Item field_200475_b;
   private final int field_200476_c;
   private final List<String> field_200477_d = Lists.newArrayList();
   private final Map<Character, Ingredient> field_200478_e = Maps.newLinkedHashMap();
   private final Advancement.Builder field_200479_f = Advancement.Builder.func_200278_a();
   private String field_200480_g;

   public ShapedRecipeBuilder(IItemProvider var1, int var2) {
      super();
      this.field_200475_b = var1.func_199767_j();
      this.field_200476_c = var2;
   }

   public static ShapedRecipeBuilder func_200470_a(IItemProvider var0) {
      return func_200468_a(var0, 1);
   }

   public static ShapedRecipeBuilder func_200468_a(IItemProvider var0, int var1) {
      return new ShapedRecipeBuilder(var0, var1);
   }

   public ShapedRecipeBuilder func_200469_a(Character var1, Tag<Item> var2) {
      return this.func_200471_a(var1, Ingredient.func_199805_a(var2));
   }

   public ShapedRecipeBuilder func_200462_a(Character var1, IItemProvider var2) {
      return this.func_200471_a(var1, Ingredient.func_199804_a(var2));
   }

   public ShapedRecipeBuilder func_200471_a(Character var1, Ingredient var2) {
      if (this.field_200478_e.containsKey(var1)) {
         throw new IllegalArgumentException("Symbol '" + var1 + "' is already defined!");
      } else if (var1 == ' ') {
         throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
      } else {
         this.field_200478_e.put(var1, var2);
         return this;
      }
   }

   public ShapedRecipeBuilder func_200472_a(String var1) {
      if (!this.field_200477_d.isEmpty() && var1.length() != ((String)this.field_200477_d.get(0)).length()) {
         throw new IllegalArgumentException("Pattern must be the same width on every line!");
      } else {
         this.field_200477_d.add(var1);
         return this;
      }
   }

   public ShapedRecipeBuilder func_200465_a(String var1, ICriterionInstance var2) {
      this.field_200479_f.func_200275_a(var1, var2);
      return this;
   }

   public ShapedRecipeBuilder func_200473_b(String var1) {
      this.field_200480_g = var1;
      return this;
   }

   public void func_200464_a(Consumer<IFinishedRecipe> var1) {
      this.func_200467_a(var1, IRegistry.field_212630_s.func_177774_c(this.field_200475_b));
   }

   public void func_200466_a(Consumer<IFinishedRecipe> var1, String var2) {
      ResourceLocation var3 = IRegistry.field_212630_s.func_177774_c(this.field_200475_b);
      if ((new ResourceLocation(var2)).equals(var3)) {
         throw new IllegalStateException("Shaped Recipe " + var2 + " should remove its 'save' argument");
      } else {
         this.func_200467_a(var1, new ResourceLocation(var2));
      }
   }

   public void func_200467_a(Consumer<IFinishedRecipe> var1, ResourceLocation var2) {
      this.func_200463_a(var2);
      this.field_200479_f.func_200272_a(new ResourceLocation("minecraft:recipes/root")).func_200275_a("has_the_recipe", new RecipeUnlockedTrigger.Instance(var2)).func_200271_a(AdvancementRewards.Builder.func_200280_c(var2)).func_200270_a(RequirementsStrategy.OR);
      var1.accept(new ShapedRecipeBuilder.Result(var2, this.field_200475_b, this.field_200476_c, this.field_200480_g == null ? "" : this.field_200480_g, this.field_200477_d, this.field_200478_e, this.field_200479_f, new ResourceLocation(var2.func_110624_b(), "recipes/" + this.field_200475_b.func_77640_w().func_200300_c() + "/" + var2.func_110623_a())));
   }

   private void func_200463_a(ResourceLocation var1) {
      if (this.field_200477_d.isEmpty()) {
         throw new IllegalStateException("No pattern is defined for shaped recipe " + var1 + "!");
      } else {
         HashSet var2 = Sets.newHashSet(this.field_200478_e.keySet());
         var2.remove(' ');
         Iterator var3 = this.field_200477_d.iterator();

         while(var3.hasNext()) {
            String var4 = (String)var3.next();

            for(int var5 = 0; var5 < var4.length(); ++var5) {
               char var6 = var4.charAt(var5);
               if (!this.field_200478_e.containsKey(var6) && var6 != ' ') {
                  throw new IllegalStateException("Pattern in recipe " + var1 + " uses undefined symbol '" + var6 + "'");
               }

               var2.remove(var6);
            }
         }

         if (!var2.isEmpty()) {
            throw new IllegalStateException("Ingredients are defined but not used in pattern for recipe " + var1);
         } else if (this.field_200477_d.size() == 1 && ((String)this.field_200477_d.get(0)).length() == 1) {
            throw new IllegalStateException("Shaped recipe " + var1 + " only takes in a single item - should it be a shapeless recipe instead?");
         } else if (this.field_200479_f.func_200277_c().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + var1);
         }
      }
   }

   class Result implements IFinishedRecipe {
      private final ResourceLocation field_200445_b;
      private final Item field_200446_c;
      private final int field_200447_d;
      private final String field_200448_e;
      private final List<String> field_200449_f;
      private final Map<Character, Ingredient> field_200450_g;
      private final Advancement.Builder field_200451_h;
      private final ResourceLocation field_200452_i;

      public Result(ResourceLocation var2, Item var3, int var4, String var5, List<String> var6, Map<Character, Ingredient> var7, Advancement.Builder var8, ResourceLocation var9) {
         super();
         this.field_200445_b = var2;
         this.field_200446_c = var3;
         this.field_200447_d = var4;
         this.field_200448_e = var5;
         this.field_200449_f = var6;
         this.field_200450_g = var7;
         this.field_200451_h = var8;
         this.field_200452_i = var9;
      }

      public JsonObject func_200441_a() {
         JsonObject var1 = new JsonObject();
         var1.addProperty("type", "crafting_shaped");
         if (!this.field_200448_e.isEmpty()) {
            var1.addProperty("group", this.field_200448_e);
         }

         JsonArray var2 = new JsonArray();
         Iterator var3 = this.field_200449_f.iterator();

         while(var3.hasNext()) {
            String var4 = (String)var3.next();
            var2.add(var4);
         }

         var1.add("pattern", var2);
         JsonObject var6 = new JsonObject();
         Iterator var7 = this.field_200450_g.entrySet().iterator();

         while(var7.hasNext()) {
            Entry var5 = (Entry)var7.next();
            var6.add(String.valueOf(var5.getKey()), ((Ingredient)var5.getValue()).func_200304_c());
         }

         var1.add("key", var6);
         JsonObject var8 = new JsonObject();
         var8.addProperty("item", IRegistry.field_212630_s.func_177774_c(this.field_200446_c).toString());
         if (this.field_200447_d > 1) {
            var8.addProperty("count", this.field_200447_d);
         }

         var1.add("result", var8);
         return var1;
      }

      public ResourceLocation func_200442_b() {
         return this.field_200445_b;
      }

      @Nullable
      public JsonObject func_200440_c() {
         return this.field_200451_h.func_200273_b();
      }

      @Nullable
      public ResourceLocation func_200443_d() {
         return this.field_200452_i;
      }
   }
}
