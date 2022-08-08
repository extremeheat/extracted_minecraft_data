package net.minecraft.world.inventory;

public enum RecipeBookType {
   CRAFTING,
   FURNACE,
   BLAST_FURNACE,
   SMOKER;

   private RecipeBookType() {
   }

   // $FF: synthetic method
   private static RecipeBookType[] $values() {
      return new RecipeBookType[]{CRAFTING, FURNACE, BLAST_FURNACE, SMOKER};
   }
}
