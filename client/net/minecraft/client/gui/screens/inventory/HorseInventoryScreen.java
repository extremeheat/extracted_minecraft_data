package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.HorseInventoryMenu;

public class HorseInventoryScreen extends AbstractContainerScreen<HorseInventoryMenu> {
   private static final ResourceLocation HORSE_INVENTORY_LOCATION = new ResourceLocation("textures/gui/container/horse.png");
   private final AbstractHorse horse;
   private float xMouse;
   private float yMouse;

   public HorseInventoryScreen(HorseInventoryMenu var1, Inventory var2, AbstractHorse var3) {
      super(var1, var2, var3.getDisplayName());
      this.horse = var3;
      this.passEvents = false;
   }

   protected void renderLabels(int var1, int var2) {
      this.font.draw(this.title.getColoredString(), 8.0F, 6.0F, 4210752);
      this.font.draw(this.inventory.getDisplayName().getColoredString(), 8.0F, (float)(this.imageHeight - 96 + 2), 4210752);
   }

   protected void renderBg(float var1, int var2, int var3) {
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bind(HORSE_INVENTORY_LOCATION);
      int var4 = (this.width - this.imageWidth) / 2;
      int var5 = (this.height - this.imageHeight) / 2;
      this.blit(var4, var5, 0, 0, this.imageWidth, this.imageHeight);
      if (this.horse instanceof AbstractChestedHorse) {
         AbstractChestedHorse var6 = (AbstractChestedHorse)this.horse;
         if (var6.hasChest()) {
            this.blit(var4 + 79, var5 + 17, 0, this.imageHeight, var6.getInventoryColumns() * 18, 54);
         }
      }

      if (this.horse.canBeSaddled()) {
         this.blit(var4 + 7, var5 + 35 - 18, 18, this.imageHeight + 54, 18, 18);
      }

      if (this.horse.wearsArmor()) {
         if (this.horse instanceof Llama) {
            this.blit(var4 + 7, var5 + 35, 36, this.imageHeight + 54, 18, 18);
         } else {
            this.blit(var4 + 7, var5 + 35, 0, this.imageHeight + 54, 18, 18);
         }
      }

      InventoryScreen.renderPlayerModel(var4 + 51, var5 + 60, 17, (float)(var4 + 51) - this.xMouse, (float)(var5 + 75 - 50) - this.yMouse, this.horse);
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      this.xMouse = (float)var1;
      this.yMouse = (float)var2;
      super.render(var1, var2, var3);
      this.renderTooltip(var1, var2);
   }
}
