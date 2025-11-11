package net.minecraft.client.gui.screens.inventory;

import net.minecraft.resources.Identifier;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.HorseInventoryMenu;
import org.jspecify.annotations.Nullable;

public class HorseInventoryScreen extends AbstractMountInventoryScreen<HorseInventoryMenu> {
   private static final Identifier SLOT_SPRITE = Identifier.withDefaultNamespace("container/slot");
   private static final Identifier CHEST_SLOTS_SPRITE = Identifier.withDefaultNamespace("container/horse/chest_slots");
   private static final Identifier HORSE_INVENTORY_LOCATION = Identifier.withDefaultNamespace("textures/gui/container/horse.png");

   public HorseInventoryScreen(HorseInventoryMenu var1, Inventory var2, AbstractHorse var3, int var4) {
      super(var1, var2, var3.getDisplayName(), var4, var3);
   }

   protected Identifier getBackgroundTextureLocation() {
      return HORSE_INVENTORY_LOCATION;
   }

   protected Identifier getSlotSpriteLocation() {
      return SLOT_SPRITE;
   }

   protected @Nullable Identifier getChestSlotsSpriteLocation() {
      return CHEST_SLOTS_SPRITE;
   }

   protected boolean shouldRenderSaddleSlot() {
      return this.mount.canUseSlot(EquipmentSlot.SADDLE) && this.mount.getType().is(EntityTypeTags.CAN_EQUIP_SADDLE);
   }

   protected boolean shouldRenderArmorSlot() {
      return this.mount.canUseSlot(EquipmentSlot.BODY) && (this.mount.getType().is(EntityTypeTags.CAN_WEAR_HORSE_ARMOR) || this.mount instanceof Llama);
   }
}
