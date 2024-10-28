package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.HorseInventoryMenu;

public class HorseInventoryScreen extends AbstractContainerScreen<HorseInventoryMenu> {
   private static final ResourceLocation CHEST_SLOTS_SPRITE = ResourceLocation.withDefaultNamespace("container/horse/chest_slots");
   private static final ResourceLocation SADDLE_SLOT_SPRITE = ResourceLocation.withDefaultNamespace("container/horse/saddle_slot");
   private static final ResourceLocation LLAMA_ARMOR_SLOT_SPRITE = ResourceLocation.withDefaultNamespace("container/horse/llama_armor_slot");
   private static final ResourceLocation ARMOR_SLOT_SPRITE = ResourceLocation.withDefaultNamespace("container/horse/armor_slot");
   private static final ResourceLocation HORSE_INVENTORY_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/container/horse.png");
   private final AbstractHorse horse;
   private final int inventoryColumns;
   private float xMouse;
   private float yMouse;

   public HorseInventoryScreen(HorseInventoryMenu var1, Inventory var2, AbstractHorse var3, int var4) {
      super(var1, var2, var3.getDisplayName());
      this.horse = var3;
      this.inventoryColumns = var4;
   }

   protected void renderBg(GuiGraphics var1, float var2, int var3, int var4) {
      int var5 = (this.width - this.imageWidth) / 2;
      int var6 = (this.height - this.imageHeight) / 2;
      var1.blit(HORSE_INVENTORY_LOCATION, var5, var6, 0, 0, this.imageWidth, this.imageHeight);
      if (this.inventoryColumns > 0) {
         var1.blitSprite(CHEST_SLOTS_SPRITE, 90, 54, 0, 0, var5 + 79, var6 + 17, this.inventoryColumns * 18, 54);
      }

      if (this.horse.isSaddleable()) {
         var1.blitSprite(SADDLE_SLOT_SPRITE, var5 + 7, var6 + 35 - 18, 18, 18);
      }

      if (this.horse.canUseSlot(EquipmentSlot.BODY)) {
         if (this.horse instanceof Llama) {
            var1.blitSprite(LLAMA_ARMOR_SLOT_SPRITE, var5 + 7, var6 + 35, 18, 18);
         } else {
            var1.blitSprite(ARMOR_SLOT_SPRITE, var5 + 7, var6 + 35, 18, 18);
         }
      }

      InventoryScreen.renderEntityInInventoryFollowsMouse(var1, var5 + 26, var6 + 18, var5 + 78, var6 + 70, 17, 0.25F, this.xMouse, this.yMouse, this.horse);
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      this.xMouse = (float)var2;
      this.yMouse = (float)var3;
      super.render(var1, var2, var3, var4);
      this.renderTooltip(var1, var2, var3);
   }
}
