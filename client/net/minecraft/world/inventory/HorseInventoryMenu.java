package net.minecraft.world.inventory;

import net.minecraft.resources.Identifier;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.player.Inventory;

public class HorseInventoryMenu extends AbstractMountInventoryMenu {
   private static final Identifier SADDLE_SLOT_SPRITE = Identifier.withDefaultNamespace("container/slot/saddle");
   private static final Identifier LLAMA_ARMOR_SLOT_SPRITE = Identifier.withDefaultNamespace("container/slot/llama_armor");
   private static final Identifier ARMOR_SLOT_SPRITE = Identifier.withDefaultNamespace("container/slot/horse_armor");

   public HorseInventoryMenu(int var1, Inventory var2, Container var3, final AbstractHorse var4, int var5) {
      super(var1, var2, var3, var4);
      Container var6 = var4.createEquipmentSlotContainer(EquipmentSlot.SADDLE);
      this.addSlot(new ArmorSlot(var6, var4, EquipmentSlot.SADDLE, 0, 8, 18, SADDLE_SLOT_SPRITE) {
         public boolean isActive() {
            return var4.canUseSlot(EquipmentSlot.SADDLE) && var4.getType().is(EntityTypeTags.CAN_EQUIP_SADDLE);
         }
      });
      final boolean var7 = var4 instanceof Llama;
      Identifier var8 = var7 ? LLAMA_ARMOR_SLOT_SPRITE : ARMOR_SLOT_SPRITE;
      Container var9 = var4.createEquipmentSlotContainer(EquipmentSlot.BODY);
      this.addSlot(new ArmorSlot(var9, var4, EquipmentSlot.BODY, 0, 8, 36, var8) {
         public boolean isActive() {
            return var4.canUseSlot(EquipmentSlot.BODY) && (var4.getType().is(EntityTypeTags.CAN_WEAR_HORSE_ARMOR) || var7);
         }
      });
      if (var5 > 0) {
         for(int var10 = 0; var10 < 3; ++var10) {
            for(int var11 = 0; var11 < var5; ++var11) {
               this.addSlot(new Slot(var3, var11 + var10 * var5, 80 + var11 * 18, 18 + var10 * 18));
            }
         }
      }

      this.addStandardInventorySlots(var2, 8, 84);
   }

   protected boolean hasInventoryChanged(Container var1) {
      return ((AbstractHorse)this.mount).hasInventoryChanged(var1);
   }
}
