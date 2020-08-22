package net.minecraft.world.item.crafting;

import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public interface RecipeSerializer {
   RecipeSerializer SHAPED_RECIPE = register("crafting_shaped", new ShapedRecipe.Serializer());
   RecipeSerializer SHAPELESS_RECIPE = register("crafting_shapeless", new ShapelessRecipe.Serializer());
   SimpleRecipeSerializer ARMOR_DYE = (SimpleRecipeSerializer)register("crafting_special_armordye", new SimpleRecipeSerializer(ArmorDyeRecipe::new));
   SimpleRecipeSerializer BOOK_CLONING = (SimpleRecipeSerializer)register("crafting_special_bookcloning", new SimpleRecipeSerializer(BookCloningRecipe::new));
   SimpleRecipeSerializer MAP_CLONING = (SimpleRecipeSerializer)register("crafting_special_mapcloning", new SimpleRecipeSerializer(MapCloningRecipe::new));
   SimpleRecipeSerializer MAP_EXTENDING = (SimpleRecipeSerializer)register("crafting_special_mapextending", new SimpleRecipeSerializer(MapExtendingRecipe::new));
   SimpleRecipeSerializer FIREWORK_ROCKET = (SimpleRecipeSerializer)register("crafting_special_firework_rocket", new SimpleRecipeSerializer(FireworkRocketRecipe::new));
   SimpleRecipeSerializer FIREWORK_STAR = (SimpleRecipeSerializer)register("crafting_special_firework_star", new SimpleRecipeSerializer(FireworkStarRecipe::new));
   SimpleRecipeSerializer FIREWORK_STAR_FADE = (SimpleRecipeSerializer)register("crafting_special_firework_star_fade", new SimpleRecipeSerializer(FireworkStarFadeRecipe::new));
   SimpleRecipeSerializer TIPPED_ARROW = (SimpleRecipeSerializer)register("crafting_special_tippedarrow", new SimpleRecipeSerializer(TippedArrowRecipe::new));
   SimpleRecipeSerializer BANNER_DUPLICATE = (SimpleRecipeSerializer)register("crafting_special_bannerduplicate", new SimpleRecipeSerializer(BannerDuplicateRecipe::new));
   SimpleRecipeSerializer SHIELD_DECORATION = (SimpleRecipeSerializer)register("crafting_special_shielddecoration", new SimpleRecipeSerializer(ShieldDecorationRecipe::new));
   SimpleRecipeSerializer SHULKER_BOX_COLORING = (SimpleRecipeSerializer)register("crafting_special_shulkerboxcoloring", new SimpleRecipeSerializer(ShulkerBoxColoring::new));
   SimpleRecipeSerializer SUSPICIOUS_STEW = (SimpleRecipeSerializer)register("crafting_special_suspiciousstew", new SimpleRecipeSerializer(SuspiciousStewRecipe::new));
   SimpleRecipeSerializer REPAIR_ITEM = (SimpleRecipeSerializer)register("crafting_special_repairitem", new SimpleRecipeSerializer(RepairItemRecipe::new));
   SimpleCookingSerializer SMELTING_RECIPE = (SimpleCookingSerializer)register("smelting", new SimpleCookingSerializer(SmeltingRecipe::new, 200));
   SimpleCookingSerializer BLASTING_RECIPE = (SimpleCookingSerializer)register("blasting", new SimpleCookingSerializer(BlastingRecipe::new, 100));
   SimpleCookingSerializer SMOKING_RECIPE = (SimpleCookingSerializer)register("smoking", new SimpleCookingSerializer(SmokingRecipe::new, 100));
   SimpleCookingSerializer CAMPFIRE_COOKING_RECIPE = (SimpleCookingSerializer)register("campfire_cooking", new SimpleCookingSerializer(CampfireCookingRecipe::new, 100));
   RecipeSerializer STONECUTTER = register("stonecutting", new SingleItemRecipe.Serializer(StonecutterRecipe::new));

   Recipe fromJson(ResourceLocation var1, JsonObject var2);

   Recipe fromNetwork(ResourceLocation var1, FriendlyByteBuf var2);

   void toNetwork(FriendlyByteBuf var1, Recipe var2);

   static RecipeSerializer register(String var0, RecipeSerializer var1) {
      return (RecipeSerializer)Registry.register(Registry.RECIPE_SERIALIZER, (String)var0, var1);
   }
}
