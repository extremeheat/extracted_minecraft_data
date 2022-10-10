package net.minecraft.data;

import com.google.gson.JsonObject;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FurnaceRecipeBuilder {
   private static final Logger field_202144_a = LogManager.getLogger();
   private final Item field_202145_b;
   private final Ingredient field_202146_c;
   private final float field_202147_d;
   private final int field_202148_e;
   private final Advancement.Builder field_202149_f = Advancement.Builder.func_200278_a();
   private String field_202150_g;

   public FurnaceRecipeBuilder(Ingredient var1, IItemProvider var2, float var3, int var4) {
      super();
      this.field_202145_b = var2.func_199767_j();
      this.field_202146_c = var1;
      this.field_202147_d = var3;
      this.field_202148_e = var4;
   }

   public static FurnaceRecipeBuilder func_202138_a(Ingredient var0, IItemProvider var1, float var2, int var3) {
      return new FurnaceRecipeBuilder(var0, var1, var2, var3);
   }

   public FurnaceRecipeBuilder func_202139_a(String var1, ICriterionInstance var2) {
      this.field_202149_f.func_200275_a(var1, var2);
      return this;
   }

   public void func_202140_a(Consumer<IFinishedRecipe> var1) {
      this.func_202143_a(var1, IRegistry.field_212630_s.func_177774_c(this.field_202145_b));
   }

   public void func_202141_a(Consumer<IFinishedRecipe> var1, String var2) {
      ResourceLocation var3 = IRegistry.field_212630_s.func_177774_c(this.field_202145_b);
      if ((new ResourceLocation(var2)).equals(var3)) {
         throw new IllegalStateException("Smelting Recipe " + var2 + " should remove its 'save' argument");
      } else {
         this.func_202143_a(var1, new ResourceLocation(var2));
      }
   }

   public void func_202143_a(Consumer<IFinishedRecipe> var1, ResourceLocation var2) {
      this.func_202142_a(var2);
      this.field_202149_f.func_200272_a(new ResourceLocation("minecraft:recipes/root")).func_200275_a("has_the_recipe", new RecipeUnlockedTrigger.Instance(var2)).func_200271_a(AdvancementRewards.Builder.func_200280_c(var2)).func_200270_a(RequirementsStrategy.OR);
      var1.accept(new FurnaceRecipeBuilder.Result(var2, this.field_202150_g == null ? "" : this.field_202150_g, this.field_202146_c, this.field_202145_b, this.field_202147_d, this.field_202148_e, this.field_202149_f, new ResourceLocation(var2.func_110624_b(), "recipes/" + this.field_202145_b.func_77640_w().func_200300_c() + "/" + var2.func_110623_a())));
   }

   private void func_202142_a(ResourceLocation var1) {
      if (this.field_202149_f.func_200277_c().isEmpty()) {
         throw new IllegalStateException("No way of obtaining recipe " + var1);
      }
   }

   public static class Result implements IFinishedRecipe {
      private final ResourceLocation field_202117_a;
      private final String field_202118_b;
      private final Ingredient field_202119_c;
      private final Item field_202120_d;
      private final float field_202121_e;
      private final int field_202122_f;
      private final Advancement.Builder field_202123_g;
      private final ResourceLocation field_202124_h;

      public Result(ResourceLocation var1, String var2, Ingredient var3, Item var4, float var5, int var6, Advancement.Builder var7, ResourceLocation var8) {
         super();
         this.field_202117_a = var1;
         this.field_202118_b = var2;
         this.field_202119_c = var3;
         this.field_202120_d = var4;
         this.field_202121_e = var5;
         this.field_202122_f = var6;
         this.field_202123_g = var7;
         this.field_202124_h = var8;
      }

      public JsonObject func_200441_a() {
         JsonObject var1 = new JsonObject();
         var1.addProperty("type", "smelting");
         if (!this.field_202118_b.isEmpty()) {
            var1.addProperty("group", this.field_202118_b);
         }

         var1.add("ingredient", this.field_202119_c.func_200304_c());
         var1.addProperty("result", IRegistry.field_212630_s.func_177774_c(this.field_202120_d).toString());
         var1.addProperty("experience", this.field_202121_e);
         var1.addProperty("cookingtime", this.field_202122_f);
         return var1;
      }

      public ResourceLocation func_200442_b() {
         return this.field_202117_a;
      }

      @Nullable
      public JsonObject func_200440_c() {
         return this.field_202123_g.func_200273_b();
      }

      @Nullable
      public ResourceLocation func_200443_d() {
         return this.field_202124_h;
      }
   }
}
