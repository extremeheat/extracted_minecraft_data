package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.LegacySmithingMenu;

@Deprecated(
   forRemoval = true
)
public class LegacySmithingScreen extends ItemCombinerScreen<LegacySmithingMenu> {
   private static final ResourceLocation SMITHING_LOCATION = new ResourceLocation("textures/gui/container/legacy_smithing.png");

   public LegacySmithingScreen(LegacySmithingMenu var1, Inventory var2, Component var3) {
      super(var1, var2, var3, SMITHING_LOCATION);
      this.titleLabelX = 60;
      this.titleLabelY = 18;
   }

   @Override
   protected void renderErrorIcon(PoseStack var1, int var2, int var3) {
      if ((this.menu.getSlot(0).hasItem() || this.menu.getSlot(1).hasItem()) && !this.menu.getSlot(this.menu.getResultSlot()).hasItem()) {
         blit(var1, var2 + 99, var3 + 45, this.imageWidth, 0, 28, 21);
      }
   }
}
