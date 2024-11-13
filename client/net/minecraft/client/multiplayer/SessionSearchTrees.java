package net.minecraft.client.multiplayer;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.searchtree.FullTextSearchTree;
import net.minecraft.client.searchtree.IdSearchTree;
import net.minecraft.client.searchtree.SearchTree;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.minecraft.world.level.Level;

public class SessionSearchTrees {
   private static final Key RECIPE_COLLECTIONS = new Key();
   private static final Key CREATIVE_NAMES = new Key();
   private static final Key CREATIVE_TAGS = new Key();
   private CompletableFuture<SearchTree<ItemStack>> creativeByNameSearch = CompletableFuture.completedFuture(SearchTree.empty());
   private CompletableFuture<SearchTree<ItemStack>> creativeByTagSearch = CompletableFuture.completedFuture(SearchTree.empty());
   private CompletableFuture<SearchTree<RecipeCollection>> recipeSearch = CompletableFuture.completedFuture(SearchTree.empty());
   private final Map<Key, Runnable> reloaders = new IdentityHashMap();

   public SessionSearchTrees() {
      super();
   }

   private void register(Key var1, Runnable var2) {
      var2.run();
      this.reloaders.put(var1, var2);
   }

   public void rebuildAfterLanguageChange() {
      for(Runnable var2 : this.reloaders.values()) {
         var2.run();
      }

   }

   private static Stream<String> getTooltipLines(Stream<ItemStack> var0, Item.TooltipContext var1, TooltipFlag var2) {
      return var0.flatMap((var2x) -> var2x.getTooltipLines(var1, (Player)null, var2).stream()).map((var0x) -> ChatFormatting.stripFormatting(var0x.getString()).trim()).filter((var0x) -> !var0x.isEmpty());
   }

   public void updateRecipes(ClientRecipeBook var1, Level var2) {
      this.register(RECIPE_COLLECTIONS, () -> {
         List var3 = var1.getCollections();
         RegistryAccess var4 = var2.registryAccess();
         Registry var5 = var4.lookupOrThrow(Registries.ITEM);
         Item.TooltipContext var6 = Item.TooltipContext.of((HolderLookup.Provider)var4);
         ContextMap var7 = SlotDisplayContext.fromLevel(var2);
         TooltipFlag.Default var8 = TooltipFlag.Default.NORMAL;
         CompletableFuture var9 = this.recipeSearch;
         this.recipeSearch = CompletableFuture.supplyAsync(() -> new FullTextSearchTree((var3x) -> getTooltipLines(var3x.getRecipes().stream().flatMap((var1) -> var1.resultItems(var7).stream()), var6, var8), (var2) -> var2.getRecipes().stream().flatMap((var1) -> var1.resultItems(var7).stream()).map((var1) -> var5.getKey(var1.getItem())), var3), Util.backgroundExecutor());
         var9.cancel(true);
      });
   }

   public SearchTree<RecipeCollection> recipes() {
      return (SearchTree)this.recipeSearch.join();
   }

   public void updateCreativeTags(List<ItemStack> var1) {
      this.register(CREATIVE_TAGS, () -> {
         CompletableFuture var2 = this.creativeByTagSearch;
         this.creativeByTagSearch = CompletableFuture.supplyAsync(() -> new IdSearchTree((var0) -> var0.getTags().map(TagKey::location), var1), Util.backgroundExecutor());
         var2.cancel(true);
      });
   }

   public SearchTree<ItemStack> creativeTagSearch() {
      return (SearchTree)this.creativeByTagSearch.join();
   }

   public void updateCreativeTooltips(HolderLookup.Provider var1, List<ItemStack> var2) {
      this.register(CREATIVE_NAMES, () -> {
         Item.TooltipContext var3 = Item.TooltipContext.of(var1);
         TooltipFlag.Default var4 = TooltipFlag.Default.NORMAL.asCreative();
         CompletableFuture var5 = this.creativeByNameSearch;
         this.creativeByNameSearch = CompletableFuture.supplyAsync(() -> new FullTextSearchTree((var2x) -> getTooltipLines(Stream.of(var2x), var3, var4), (var0) -> var0.getItemHolder().unwrapKey().map(ResourceKey::location).stream(), var2), Util.backgroundExecutor());
         var5.cancel(true);
      });
   }

   public SearchTree<ItemStack> creativeNameSearch() {
      return (SearchTree)this.creativeByNameSearch.join();
   }

   static class Key {
      Key() {
         super();
      }
   }
}
