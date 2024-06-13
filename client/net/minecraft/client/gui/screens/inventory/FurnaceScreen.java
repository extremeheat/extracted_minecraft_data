package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.screens.recipebook.SmeltingRecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.FurnaceMenu;

public class FurnaceScreen extends AbstractFurnaceScreen<FurnaceMenu> {
   private static final ResourceLocation LIT_PROGRESS_SPRITE = ResourceLocation.withDefaultNamespace("container/furnace/lit_progress");
   private static final ResourceLocation BURN_PROGRESS_SPRITE = ResourceLocation.withDefaultNamespace("container/furnace/burn_progress");
   private static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/gui/container/furnace.png");

   public FurnaceScreen(FurnaceMenu var1, Inventory var2, Component var3) {
      super(var1, new SmeltingRecipeBookComponent(), var2, var3, TEXTURE, LIT_PROGRESS_SPRITE, BURN_PROGRESS_SPRITE);
   }
}
