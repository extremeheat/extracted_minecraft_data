package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.GameRenderer;
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

   protected void renderBg(PoseStack var1, float var2, int var3, int var4) {
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, HORSE_INVENTORY_LOCATION);
      int var5 = (this.width - this.imageWidth) / 2;
      int var6 = (this.height - this.imageHeight) / 2;
      this.blit(var1, var5, var6, 0, 0, this.imageWidth, this.imageHeight);
      if (this.horse instanceof AbstractChestedHorse) {
         AbstractChestedHorse var7 = (AbstractChestedHorse)this.horse;
         if (var7.hasChest()) {
            this.blit(var1, var5 + 79, var6 + 17, 0, this.imageHeight, var7.getInventoryColumns() * 18, 54);
         }
      }

      if (this.horse.isSaddleable()) {
         this.blit(var1, var5 + 7, var6 + 35 - 18, 18, this.imageHeight + 54, 18, 18);
      }

      if (this.horse.canWearArmor()) {
         if (this.horse instanceof Llama) {
            this.blit(var1, var5 + 7, var6 + 35, 36, this.imageHeight + 54, 18, 18);
         } else {
            this.blit(var1, var5 + 7, var6 + 35, 0, this.imageHeight + 54, 18, 18);
         }
      }

      InventoryScreen.renderEntityInInventory(var5 + 51, var6 + 60, 17, (float)(var5 + 51) - this.xMouse, (float)(var6 + 75 - 50) - this.yMouse, this.horse);
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      this.xMouse = (float)var2;
      this.yMouse = (float)var3;
      super.render(var1, var2, var3, var4);
      this.renderTooltip(var1, var2, var3);
   }
}
