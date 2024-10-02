package net.minecraft.world.item.crafting;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;

public class RecipeCache {
   private final RecipeCache.Entry[] entries;
   private WeakReference<RecipeManager> cachedRecipeManager = new WeakReference<>(null);

   public RecipeCache(int var1) {
      super();
      this.entries = new RecipeCache.Entry[var1];
   }

   public Optional<RecipeHolder<CraftingRecipe>> get(ServerLevel var1, CraftingInput var2) {
      if (var2.isEmpty()) {
         return Optional.empty();
      } else {
         this.validateRecipeManager(var1);

         for (int var3 = 0; var3 < this.entries.length; var3++) {
            RecipeCache.Entry var4 = this.entries[var3];
            if (var4 != null && var4.matches(var2)) {
               this.moveEntryToFront(var3);
               return Optional.ofNullable(var4.value());
            }
         }

         return this.compute(var2, var1);
      }
   }

   private void validateRecipeManager(ServerLevel var1) {
      RecipeManager var2 = var1.recipeAccess();
      if (var2 != this.cachedRecipeManager.get()) {
         this.cachedRecipeManager = new WeakReference<>(var2);
         Arrays.fill(this.entries, null);
      }
   }

   private Optional<RecipeHolder<CraftingRecipe>> compute(CraftingInput var1, ServerLevel var2) {
      Optional var3 = var2.recipeAccess().getRecipeFor(RecipeType.CRAFTING, var1, var2);
      this.insert(var1, (RecipeHolder<CraftingRecipe>)var3.orElse(null));
      return var3;
   }

   private void moveEntryToFront(int var1) {
      if (var1 > 0) {
         RecipeCache.Entry var2 = this.entries[var1];
         System.arraycopy(this.entries, 0, this.entries, 1, var1);
         this.entries[0] = var2;
      }
   }

   private void insert(CraftingInput var1, @Nullable RecipeHolder<CraftingRecipe> var2) {
      NonNullList var3 = NonNullList.withSize(var1.size(), ItemStack.EMPTY);

      for (int var4 = 0; var4 < var1.size(); var4++) {
         var3.set(var4, var1.getItem(var4).copyWithCount(1));
      }

      System.arraycopy(this.entries, 0, this.entries, 1, this.entries.length - 1);
      this.entries[0] = new RecipeCache.Entry(var3, var1.width(), var1.height(), var2);
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
