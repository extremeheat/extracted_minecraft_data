package net.minecraft.world.item.crafting;

import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public interface RecipeSerializer<T extends Recipe<?>> {
   RecipeSerializer<ShapedRecipe> SHAPED_RECIPE = register("crafting_shaped", new ShapedRecipe.Serializer());
   RecipeSerializer<ShapelessRecipe> SHAPELESS_RECIPE = register("crafting_shapeless", new ShapelessRecipe.Serializer());
   SimpleRecipeSerializer<ArmorDyeRecipe> ARMOR_DYE = (SimpleRecipeSerializer)register("crafting_special_armordye", new SimpleRecipeSerializer(ArmorDyeRecipe::new));
   SimpleRecipeSerializer<BookCloningRecipe> BOOK_CLONING = (SimpleRecipeSerializer)register("crafting_special_bookcloning", new SimpleRecipeSerializer(BookCloningRecipe::new));
   SimpleRecipeSerializer<MapCloningRecipe> MAP_CLONING = (SimpleRecipeSerializer)register("crafting_special_mapcloning", new SimpleRecipeSerializer(MapCloningRecipe::new));
   SimpleRecipeSerializer<MapExtendingRecipe> MAP_EXTENDING = (SimpleRecipeSerializer)register("crafting_special_mapextending", new SimpleRecipeSerializer(MapExtendingRecipe::new));
   SimpleRecipeSerializer<FireworkRocketRecipe> FIREWORK_ROCKET = (SimpleRecipeSerializer)register("crafting_special_firework_rocket", new SimpleRecipeSerializer(FireworkRocketRecipe::new));
   SimpleRecipeSerializer<FireworkStarRecipe> FIREWORK_STAR = (SimpleRecipeSerializer)register("crafting_special_firework_star", new SimpleRecipeSerializer(FireworkStarRecipe::new));
   SimpleRecipeSerializer<FireworkStarFadeRecipe> FIREWORK_STAR_FADE = (SimpleRecipeSerializer)register("crafting_special_firework_star_fade", new SimpleRecipeSerializer(FireworkStarFadeRecipe::new));
   SimpleRecipeSerializer<TippedArrowRecipe> TIPPED_ARROW = (SimpleRecipeSerializer)register("crafting_special_tippedarrow", new SimpleRecipeSerializer(TippedArrowRecipe::new));
   SimpleRecipeSerializer<BannerDuplicateRecipe> BANNER_DUPLICATE = (SimpleRecipeSerializer)register("crafting_special_bannerduplicate", new SimpleRecipeSerializer(BannerDuplicateRecipe::new));
   SimpleRecipeSerializer<ShieldDecorationRecipe> SHIELD_DECORATION = (SimpleRecipeSerializer)register("crafting_special_shielddecoration", new SimpleRecipeSerializer(ShieldDecorationRecipe::new));
   SimpleRecipeSerializer<ShulkerBoxColoring> SHULKER_BOX_COLORING = (SimpleRecipeSerializer)register("crafting_special_shulkerboxcoloring", new SimpleRecipeSerializer(ShulkerBoxColoring::new));
   SimpleRecipeSerializer<SuspiciousStewRecipe> SUSPICIOUS_STEW = (SimpleRecipeSerializer)register("crafting_special_suspiciousstew", new SimpleRecipeSerializer(SuspiciousStewRecipe::new));
   SimpleRecipeSerializer<RepairItemRecipe> REPAIR_ITEM = (SimpleRecipeSerializer)register("crafting_special_repairitem", new SimpleRecipeSerializer(RepairItemRecipe::new));
   SimpleCookingSerializer<SmeltingRecipe> SMELTING_RECIPE = (SimpleCookingSerializer)register("smelting", new SimpleCookingSerializer(SmeltingRecipe::new, 200));
   SimpleCookingSerializer<BlastingRecipe> BLASTING_RECIPE = (SimpleCookingSerializer)register("blasting", new SimpleCookingSerializer(BlastingRecipe::new, 100));
   SimpleCookingSerializer<SmokingRecipe> SMOKING_RECIPE = (SimpleCookingSerializer)register("smoking", new SimpleCookingSerializer(SmokingRecipe::new, 100));
   SimpleCookingSerializer<CampfireCookingRecipe> CAMPFIRE_COOKING_RECIPE = (SimpleCookingSerializer)register("campfire_cooking", new SimpleCookingSerializer(CampfireCookingRecipe::new, 100));
   RecipeSerializer<StonecutterRecipe> STONECUTTER = register("stonecutting", new SingleItemRecipe.Serializer(StonecutterRecipe::new));
   RecipeSerializer<UpgradeRecipe> SMITHING = register("smithing", new UpgradeRecipe.Serializer());

   T fromJson(ResourceLocation var1, JsonObject var2);

   T fromNetwork(ResourceLocation var1, FriendlyByteBuf var2);

   void toNetwork(FriendlyByteBuf var1, T var2);

   static <S extends RecipeSerializer<T>, T extends Recipe<?>> S register(String var0, S var1) {
      return (RecipeSerializer)Registry.register(Registry.RECIPE_SERIALIZER, (String)var0, var1);
   }
}
