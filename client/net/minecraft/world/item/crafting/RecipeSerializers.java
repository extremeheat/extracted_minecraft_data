package net.minecraft.world.item.crafting;

import net.minecraft.core.Registry;

public class RecipeSerializers {
   public RecipeSerializers() {
      super();
   }

   public static Object bootstrap(final Registry<RecipeSerializer<?>> registry) {
      Registry.register(registry, (String)"crafting_shaped", ShapedRecipe.SERIALIZER);
      Registry.register(registry, (String)"crafting_shapeless", ShapelessRecipe.SERIALIZER);
      Registry.register(registry, (String)"crafting_dye", DyeRecipe.SERIALIZER);
      Registry.register(registry, (String)"crafting_imbue", ImbueRecipe.SERIALIZER);
      Registry.register(registry, (String)"crafting_transmute", TransmuteRecipe.SERIALIZER);
      Registry.register(registry, (String)"crafting_decorated_pot", DecoratedPotRecipe.SERIALIZER);
      Registry.register(registry, (String)"crafting_special_bookcloning", BookCloningRecipe.SERIALIZER);
      Registry.register(registry, (String)"crafting_special_mapextending", MapExtendingRecipe.SERIALIZER);
      Registry.register(registry, (String)"crafting_special_firework_rocket", FireworkRocketRecipe.SERIALIZER);
      Registry.register(registry, (String)"crafting_special_firework_star", FireworkStarRecipe.SERIALIZER);
      Registry.register(registry, (String)"crafting_special_firework_star_fade", FireworkStarFadeRecipe.SERIALIZER);
      Registry.register(registry, (String)"crafting_special_bannerduplicate", BannerDuplicateRecipe.SERIALIZER);
      Registry.register(registry, (String)"crafting_special_shielddecoration", ShieldDecorationRecipe.SERIALIZER);
      Registry.register(registry, (String)"crafting_special_repairitem", RepairItemRecipe.SERIALIZER);
      Registry.register(registry, (String)"smelting", SmeltingRecipe.SERIALIZER);
      Registry.register(registry, (String)"blasting", BlastingRecipe.SERIALIZER);
      Registry.register(registry, (String)"smoking", SmokingRecipe.SERIALIZER);
      Registry.register(registry, (String)"campfire_cooking", CampfireCookingRecipe.SERIALIZER);
      Registry.register(registry, (String)"stonecutting", StonecutterRecipe.SERIALIZER);
      Registry.register(registry, (String)"smithing_transform", SmithingTransformRecipe.SERIALIZER);
      return Registry.register(registry, (String)"smithing_trim", SmithingTrimRecipe.SERIALIZER);
   }
}
