package net.minecraft.world.inventory;

import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.nautilus.AbstractNautilus;
import net.minecraft.world.entity.player.Inventory;

public class NautilusInventoryMenu extends AbstractMountInventoryMenu {
   private static final Identifier SADDLE_SLOT_SPRITE = Identifier.withDefaultNamespace("container/slot/saddle");
   private static final Identifier ARMOR_SLOT_SPRITE = Identifier.withDefaultNamespace("container/slot/nautilus_armor_inventory");

   public NautilusInventoryMenu(int var1, Inventory var2, Container var3, final AbstractNautilus var4, int var5) {
      super(var1, var2, var3, var4);
      Container var6 = var4.createEquipmentSlotContainer(EquipmentSlot.SADDLE);
      this.addSlot(new ArmorSlot(var6, var4, EquipmentSlot.SADDLE, 0, 8, 18, SADDLE_SLOT_SPRITE) {
         public boolean isActive() {
            return var4.canUseSlot(EquipmentSlot.SADDLE);
         }
      });
      Container var7 = var4.createEquipmentSlotContainer(EquipmentSlot.BODY);
      this.addSlot(new ArmorSlot(var7, var4, EquipmentSlot.BODY, 0, 8, 36, ARMOR_SLOT_SPRITE) {
         public boolean isActive() {
            return var4.canUseSlot(EquipmentSlot.BODY);
         }
      });
      this.addStandardInventorySlots(var2, 8, 84);
   }

   protected boolean hasInventoryChanged(Container var1) {
      return ((AbstractNautilus)this.mount).hasInventoryChanged(var1);
   }
}
