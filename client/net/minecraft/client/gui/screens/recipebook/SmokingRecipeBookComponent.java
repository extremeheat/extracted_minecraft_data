package net.minecraft.client.gui.screens.recipebook;

import java.util.Set;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

public class SmokingRecipeBookComponent extends AbstractFurnaceRecipeBookComponent {
   private static final Component FILTER_NAME = Component.translatable("gui.recipebook.toggleRecipes.smokable");

   public SmokingRecipeBookComponent() {
      super();
   }

   protected Component getRecipeFilterName() {
      return FILTER_NAME;
   }

   protected Set<Item> getFuelItems() {
      return AbstractFurnaceBlockEntity.getFuel().keySet();
   }
}
