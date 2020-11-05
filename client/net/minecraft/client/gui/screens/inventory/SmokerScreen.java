package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.screens.recipebook.SmokingRecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.SmokerMenu;

public class SmokerScreen extends AbstractFurnaceScreen<SmokerMenu> {
   private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/smoker.png");

   public SmokerScreen(SmokerMenu var1, Inventory var2, Component var3) {
      super(var1, new SmokingRecipeBookComponent(), var2, var3, TEXTURE);
   }
}
