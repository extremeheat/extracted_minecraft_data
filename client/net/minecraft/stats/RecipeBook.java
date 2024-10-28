package net.minecraft.stats;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.crafting.RecipeHolder;

public class RecipeBook {
   protected final Set<ResourceLocation> known = Sets.newHashSet();
   protected final Set<ResourceLocation> highlight = Sets.newHashSet();
   private final RecipeBookSettings bookSettings = new RecipeBookSettings();

   public RecipeBook() {
      super();
   }

   public void copyOverData(RecipeBook var1) {
      this.known.clear();
      this.highlight.clear();
      this.bookSettings.replaceFrom(var1.bookSettings);
      this.known.addAll(var1.known);
      this.highlight.addAll(var1.highlight);
   }

   public void add(RecipeHolder<?> var1) {
      if (!var1.value().isSpecial()) {
         this.add(var1.id());
      }

   }

   protected void add(ResourceLocation var1) {
      this.known.add(var1);
   }

   public boolean contains(@Nullable RecipeHolder<?> var1) {
      return var1 == null ? false : this.known.contains(var1.id());
   }

   public boolean contains(ResourceLocation var1) {
      return this.known.contains(var1);
   }

   public void remove(RecipeHolder<?> var1) {
      this.remove(var1.id());
   }

   protected void remove(ResourceLocation var1) {
      this.known.remove(var1);
      this.highlight.remove(var1);
   }

   public boolean willHighlight(RecipeHolder<?> var1) {
      return this.highlight.contains(var1.id());
   }

   public void removeHighlight(RecipeHolder<?> var1) {
      this.highlight.remove(var1.id());
   }

   public void addHighlight(RecipeHolder<?> var1) {
      this.addHighlight(var1.id());
   }

   protected void addHighlight(ResourceLocation var1) {
      this.highlight.add(var1);
   }

   public boolean isOpen(RecipeBookType var1) {
      return this.bookSettings.isOpen(var1);
   }

   public void setOpen(RecipeBookType var1, boolean var2) {
      this.bookSettings.setOpen(var1, var2);
   }

   public boolean isFiltering(RecipeBookMenu<?, ?> var1) {
      return this.isFiltering(var1.getRecipeBookType());
   }

   public boolean isFiltering(RecipeBookType var1) {
      return this.bookSettings.isFiltering(var1);
   }

   public void setFiltering(RecipeBookType var1, boolean var2) {
      this.bookSettings.setFiltering(var1, var2);
   }

   public void setBookSettings(RecipeBookSettings var1) {
      this.bookSettings.replaceFrom(var1);
   }

   public RecipeBookSettings getBookSettings() {
      return this.bookSettings.copy();
   }

   public void setBookSetting(RecipeBookType var1, boolean var2, boolean var3) {
      this.bookSettings.setOpen(var1, var2);
      this.bookSettings.setFiltering(var1, var3);
   }
}
