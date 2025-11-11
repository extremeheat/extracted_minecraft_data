package net.minecraft.client.gui.screens.inventory;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.nautilus.AbstractNautilus;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.NautilusInventoryMenu;
import org.jspecify.annotations.Nullable;

public class NautilusInventoryScreen extends AbstractMountInventoryScreen<NautilusInventoryMenu> {
   private static final Identifier SLOT_SPRITE = Identifier.withDefaultNamespace("container/slot");
   private static final Identifier NAUTILUS_INVENTORY_LOCATION = Identifier.withDefaultNamespace("textures/gui/container/nautilus.png");

   public NautilusInventoryScreen(NautilusInventoryMenu var1, Inventory var2, AbstractNautilus var3, int var4) {
      super(var1, var2, var3.getDisplayName(), var4, var3);
   }

   protected Identifier getBackgroundTextureLocation() {
      return NAUTILUS_INVENTORY_LOCATION;
   }

   protected Identifier getSlotSpriteLocation() {
      return SLOT_SPRITE;
   }

   protected @Nullable Identifier getChestSlotsSpriteLocation() {
      return null;
   }

   protected boolean shouldRenderSaddleSlot() {
      return this.mount.canUseSlot(EquipmentSlot.SADDLE);
   }

   protected boolean shouldRenderArmorSlot() {
      return this.mount.canUseSlot(EquipmentSlot.BODY);
   }
}
