package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.screens.recipebook.BlastingRecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.BlastFurnaceMenu;

public class BlastFurnaceScreen extends AbstractFurnaceScreen<BlastFurnaceMenu> {
   private static final ResourceLocation LIT_PROGRESS_SPRITE = ResourceLocation.withDefaultNamespace("container/blast_furnace/lit_progress");
   private static final ResourceLocation BURN_PROGRESS_SPRITE = ResourceLocation.withDefaultNamespace("container/blast_furnace/burn_progress");
   private static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/gui/container/blast_furnace.png");

   public BlastFurnaceScreen(BlastFurnaceMenu var1, Inventory var2, Component var3) {
      super(var1, new BlastingRecipeBookComponent(), var2, var3, TEXTURE, LIT_PROGRESS_SPRITE, BURN_PROGRESS_SPRITE);
   }
}
