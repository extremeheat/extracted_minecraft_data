package net.minecraft.client.gui.screens.inventory;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.SmokerMenu;

public class SmokerScreen extends AbstractFurnaceScreen<SmokerMenu> {
   private static final ResourceLocation LIT_PROGRESS_SPRITE = ResourceLocation.withDefaultNamespace("container/smoker/lit_progress");
   private static final ResourceLocation BURN_PROGRESS_SPRITE = ResourceLocation.withDefaultNamespace("container/smoker/burn_progress");
   private static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/gui/container/smoker.png");
   private static final Component FILTER_NAME = Component.translatable("gui.recipebook.toggleRecipes.smokable");

   public SmokerScreen(SmokerMenu var1, Inventory var2, Component var3) {
      super(var1, var2, var3, FILTER_NAME, TEXTURE, LIT_PROGRESS_SPRITE, BURN_PROGRESS_SPRITE);
   }
}
