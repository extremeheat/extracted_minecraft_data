package net.minecraft.client.player.inventory;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.LockCode;

public class ContainerLocalMenu extends InventoryBasic implements ILockableContainer {
   private final String field_174896_a;
   private final Map<Integer, Integer> field_174895_b = Maps.newHashMap();

   public ContainerLocalMenu(String var1, ITextComponent var2, int var3) {
      super(var2, var3);
      this.field_174896_a = var1;
   }

   public int func_174887_a_(int var1) {
      return this.field_174895_b.containsKey(var1) ? (Integer)this.field_174895_b.get(var1) : 0;
   }

   public void func_174885_b(int var1, int var2) {
      this.field_174895_b.put(var1, var2);
   }

   public int func_174890_g() {
      return this.field_174895_b.size();
   }

   public boolean func_174893_q_() {
      return false;
   }

   public void func_174892_a(LockCode var1) {
   }

   public LockCode func_174891_i() {
      return LockCode.field_180162_a;
   }

   public String func_174875_k() {
      return this.field_174896_a;
   }

   public Container func_174876_a(InventoryPlayer var1, EntityPlayer var2) {
      throw new UnsupportedOperationException();
   }
}
