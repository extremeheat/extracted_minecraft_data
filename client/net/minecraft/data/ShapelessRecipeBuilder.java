package net.minecraft.data;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.Iterator;
import java.util.List;
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

public class ShapelessRecipeBuilder {
   private static final Logger field_200493_a = LogManager.getLogger();
   private final Item field_200494_b;
   private final int field_200495_c;
   private final List<Ingredient> field_200496_d = Lists.newArrayList();
   private final Advancement.Builder field_200497_e = Advancement.Builder.func_200278_a();
   private String field_200498_f;

   public ShapelessRecipeBuilder(IItemProvider var1, int var2) {
      super();
      this.field_200494_b = var1.func_199767_j();
      this.field_200495_c = var2;
   }

   public static ShapelessRecipeBuilder func_200486_a(IItemProvider var0) {
      return new ShapelessRecipeBuilder(var0, 1);
   }

   public static ShapelessRecipeBuilder func_200488_a(IItemProvider var0, int var1) {
      return new ShapelessRecipeBuilder(var0, var1);
   }

   public ShapelessRecipeBuilder func_203221_a(Tag<Item> var1) {
      return this.func_200489_a(Ingredient.func_199805_a(var1));
   }

   public ShapelessRecipeBuilder func_200487_b(IItemProvider var1) {
      return this.func_200491_b(var1, 1);
   }

   public ShapelessRecipeBuilder func_200491_b(IItemProvider var1, int var2) {
      for(int var3 = 0; var3 < var2; ++var3) {
         this.func_200489_a(Ingredient.func_199804_a(var1));
      }

      return this;
   }

   public ShapelessRecipeBuilder func_200489_a(Ingredient var1) {
      return this.func_200492_a(var1, 1);
   }

   public ShapelessRecipeBuilder func_200492_a(Ingredient var1, int var2) {
      for(int var3 = 0; var3 < var2; ++var3) {
         this.field_200496_d.add(var1);
      }

      return this;
   }

   public ShapelessRecipeBuilder func_200483_a(String var1, ICriterionInstance var2) {
      this.field_200497_e.func_200275_a(var1, var2);
      return this;
   }

   public ShapelessRecipeBuilder func_200490_a(String var1) {
      this.field_200498_f = var1;
      return this;
   }

   public void func_200482_a(Consumer<IFinishedRecipe> var1) {
      this.func_200485_a(var1, IRegistry.field_212630_s.func_177774_c(this.field_200494_b));
   }

   public void func_200484_a(Consumer<IFinishedRecipe> var1, String var2) {
      ResourceLocation var3 = IRegistry.field_212630_s.func_177774_c(this.field_200494_b);
      if ((new ResourceLocation(var2)).equals(var3)) {
         throw new IllegalStateException("Shapeless Recipe " + var2 + " should remove its 'save' argument");
      } else {
         this.func_200485_a(var1, new ResourceLocation(var2));
      }
   }

   public void func_200485_a(Consumer<IFinishedRecipe> var1, ResourceLocation var2) {
      this.func_200481_a(var2);
      this.field_200497_e.func_200272_a(new ResourceLocation("minecraft:recipes/root")).func_200275_a("has_the_recipe", new RecipeUnlockedTrigger.Instance(var2)).func_200271_a(AdvancementRewards.Builder.func_200280_c(var2)).func_200270_a(RequirementsStrategy.OR);
      var1.accept(new ShapelessRecipeBuilder.Result(var2, this.field_200494_b, this.field_200495_c, this.field_200498_f == null ? "" : this.field_200498_f, this.field_200496_d, this.field_200497_e, new ResourceLocation(var2.func_110624_b(), "recipes/" + this.field_200494_b.func_77640_w().func_200300_c() + "/" + var2.func_110623_a())));
   }

   private void func_200481_a(ResourceLocation var1) {
      if (this.field_200497_e.func_200277_c().isEmpty()) {
         throw new IllegalStateException("No way of obtaining recipe " + var1);
      }
   }

   public static class Result implements IFinishedRecipe {
      private final ResourceLocation field_200453_a;
      private final Item field_200454_b;
      private final int field_200455_c;
      private final String field_200456_d;
      private final List<Ingredient> field_200457_e;
      private final Advancement.Builder field_200458_f;
      private final ResourceLocation field_200459_g;

      public Result(ResourceLocation var1, Item var2, int var3, String var4, List<Ingredient> var5, Advancement.Builder var6, ResourceLocation var7) {
         super();
         this.field_200453_a = var1;
         this.field_200454_b = var2;
         this.field_200455_c = var3;
         this.field_200456_d = var4;
         this.field_200457_e = var5;
         this.field_200458_f = var6;
         this.field_200459_g = var7;
      }

      public JsonObject func_200441_a() {
         JsonObject var1 = new JsonObject();
         var1.addProperty("type", "crafting_shapeless");
         if (!this.field_200456_d.isEmpty()) {
            var1.addProperty("group", this.field_200456_d);
         }

         JsonArray var2 = new JsonArray();
         Iterator var3 = this.field_200457_e.iterator();

         while(var3.hasNext()) {
            Ingredient var4 = (Ingredient)var3.next();
            var2.add(var4.func_200304_c());
         }

         var1.add("ingredients", var2);
         JsonObject var5 = new JsonObject();
         var5.addProperty("item", IRegistry.field_212630_s.func_177774_c(this.field_200454_b).toString());
         if (this.field_200455_c > 1) {
            var5.addProperty("count", this.field_200455_c);
         }

         var1.add("result", var5);
         return var1;
      }

      public ResourceLocation func_200442_b() {
         return this.field_200453_a;
      }

      @Nullable
      public JsonObject func_200440_c() {
         return this.field_200458_f.func_200273_b();
      }

      @Nullable
      public ResourceLocation func_200443_d() {
         return this.field_200459_g;
      }
   }
}
