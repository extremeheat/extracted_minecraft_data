package net.minecraft.stats;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.BlastFurnaceMenu;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.SmokerMenu;
import net.minecraft.world.item.crafting.Recipe;

public class RecipeBook {
   protected final Set known = Sets.newHashSet();
   protected final Set highlight = Sets.newHashSet();
   protected boolean guiOpen;
   protected boolean filteringCraftable;
   protected boolean furnaceGuiOpen;
   protected boolean furnaceFilteringCraftable;
   protected boolean blastingFurnaceGuiOpen;
   protected boolean blastingFurnaceFilteringCraftable;
   protected boolean smokerGuiOpen;
   protected boolean smokerFilteringCraftable;

   public void copyOverData(RecipeBook var1) {
      this.known.clear();
      this.highlight.clear();
      this.known.addAll(var1.known);
      this.highlight.addAll(var1.highlight);
   }

   public void add(Recipe var1) {
      if (!var1.isSpecial()) {
         this.add(var1.getId());
      }

   }

   protected void add(ResourceLocation var1) {
      this.known.add(var1);
   }

   public boolean contains(@Nullable Recipe var1) {
      return var1 == null ? false : this.known.contains(var1.getId());
   }

   public boolean contains(ResourceLocation var1) {
      return this.known.contains(var1);
   }

   public void remove(Recipe var1) {
      this.remove(var1.getId());
   }

   protected void remove(ResourceLocation var1) {
      this.known.remove(var1);
      this.highlight.remove(var1);
   }

   public boolean willHighlight(Recipe var1) {
      return this.highlight.contains(var1.getId());
   }

   public void removeHighlight(Recipe var1) {
      this.highlight.remove(var1.getId());
   }

   public void addHighlight(Recipe var1) {
      this.addHighlight(var1.getId());
   }

   protected void addHighlight(ResourceLocation var1) {
      this.highlight.add(var1);
   }

   public boolean isGuiOpen() {
      return this.guiOpen;
   }

   public void setGuiOpen(boolean var1) {
      this.guiOpen = var1;
   }

   public boolean isFilteringCraftable(RecipeBookMenu var1) {
      if (var1 instanceof FurnaceMenu) {
         return this.furnaceFilteringCraftable;
      } else if (var1 instanceof BlastFurnaceMenu) {
         return this.blastingFurnaceFilteringCraftable;
      } else {
         return var1 instanceof SmokerMenu ? this.smokerFilteringCraftable : this.filteringCraftable;
      }
   }

   public boolean isFilteringCraftable() {
      return this.filteringCraftable;
   }

   public void setFilteringCraftable(boolean var1) {
      this.filteringCraftable = var1;
   }

   public boolean isFurnaceGuiOpen() {
      return this.furnaceGuiOpen;
   }

   public void setFurnaceGuiOpen(boolean var1) {
      this.furnaceGuiOpen = var1;
   }

   public boolean isFurnaceFilteringCraftable() {
      return this.furnaceFilteringCraftable;
   }

   public void setFurnaceFilteringCraftable(boolean var1) {
      this.furnaceFilteringCraftable = var1;
   }

   public boolean isBlastingFurnaceGuiOpen() {
      return this.blastingFurnaceGuiOpen;
   }

   public void setBlastingFurnaceGuiOpen(boolean var1) {
      this.blastingFurnaceGuiOpen = var1;
   }

   public boolean isBlastingFurnaceFilteringCraftable() {
      return this.blastingFurnaceFilteringCraftable;
   }

   public void setBlastingFurnaceFilteringCraftable(boolean var1) {
      this.blastingFurnaceFilteringCraftable = var1;
   }

   public boolean isSmokerGuiOpen() {
      return this.smokerGuiOpen;
   }

   public void setSmokerGuiOpen(boolean var1) {
      this.smokerGuiOpen = var1;
   }

   public boolean isSmokerFilteringCraftable() {
      return this.smokerFilteringCraftable;
   }

   public void setSmokerFilteringCraftable(boolean var1) {
      this.smokerFilteringCraftable = var1;
   }
}
