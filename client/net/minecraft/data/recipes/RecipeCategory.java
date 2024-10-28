package net.minecraft.data.recipes;

public enum RecipeCategory {
   BUILDING_BLOCKS("building_blocks"),
   DECORATIONS("decorations"),
   REDSTONE("redstone"),
   TRANSPORTATION("transportation"),
   TOOLS("tools"),
   COMBAT("combat"),
   FOOD("food"),
   BREWING("brewing"),
   MISC("misc");

   private final String recipeFolderName;

   private RecipeCategory(String var3) {
      this.recipeFolderName = var3;
   }

   public String getFolderName() {
      return this.recipeFolderName;
   }

   // $FF: synthetic method
   private static RecipeCategory[] $values() {
      return new RecipeCategory[]{BUILDING_BLOCKS, DECORATIONS, REDSTONE, TRANSPORTATION, TOOLS, COMBAT, FOOD, BREWING, MISC};
   }
}
