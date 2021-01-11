package net.minecraft.client.player.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.IInteractionObject;

public class LocalBlockIntercommunication implements IInteractionObject {
   private String field_175126_a;
   private IChatComponent field_175125_b;

   public LocalBlockIntercommunication(String var1, IChatComponent var2) {
      super();
      this.field_175126_a = var1;
      this.field_175125_b = var2;
   }

   public Container func_174876_a(InventoryPlayer var1, EntityPlayer var2) {
      throw new UnsupportedOperationException();
   }

   public String func_70005_c_() {
      return this.field_175125_b.func_150260_c();
   }

   public boolean func_145818_k_() {
      return true;
   }

   public String func_174875_k() {
      return this.field_175126_a;
   }

   public IChatComponent func_145748_c_() {
      return this.field_175125_b;
   }
}
