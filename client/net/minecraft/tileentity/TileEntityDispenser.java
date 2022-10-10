package net.minecraft.tileentity;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerDispenser;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class TileEntityDispenser extends TileEntityLockableLoot {
   private static final Random field_174913_f = new Random();
   private NonNullList<ItemStack> field_146022_i;

   protected TileEntityDispenser(TileEntityType<?> var1) {
      super(var1);
      this.field_146022_i = NonNullList.func_191197_a(9, ItemStack.field_190927_a);
   }

   public TileEntityDispenser() {
      this(TileEntityType.field_200976_g);
   }

   public int func_70302_i_() {
      return 9;
   }

   public boolean func_191420_l() {
      Iterator var1 = this.field_146022_i.iterator();

      ItemStack var2;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         var2 = (ItemStack)var1.next();
      } while(var2.func_190926_b());

      return false;
   }

   public int func_146017_i() {
      this.func_184281_d((EntityPlayer)null);
      int var1 = -1;
      int var2 = 1;

      for(int var3 = 0; var3 < this.field_146022_i.size(); ++var3) {
         if (!((ItemStack)this.field_146022_i.get(var3)).func_190926_b() && field_174913_f.nextInt(var2++) == 0) {
            var1 = var3;
         }
      }

      return var1;
   }

   public int func_146019_a(ItemStack var1) {
      for(int var2 = 0; var2 < this.field_146022_i.size(); ++var2) {
         if (((ItemStack)this.field_146022_i.get(var2)).func_190926_b()) {
            this.func_70299_a(var2, var1);
            return var2;
         }
      }

      return -1;
   }

   public ITextComponent func_200200_C_() {
      ITextComponent var1 = this.func_200201_e();
      return (ITextComponent)(var1 != null ? var1 : new TextComponentTranslation("container.dispenser", new Object[0]));
   }

   public void func_145839_a(NBTTagCompound var1) {
      super.func_145839_a(var1);
      this.field_146022_i = NonNullList.func_191197_a(this.func_70302_i_(), ItemStack.field_190927_a);
      if (!this.func_184283_b(var1)) {
         ItemStackHelper.func_191283_b(var1, this.field_146022_i);
      }

      if (var1.func_150297_b("CustomName", 8)) {
         this.field_190577_o = ITextComponent.Serializer.func_150699_a(var1.func_74779_i("CustomName"));
      }

   }

   public NBTTagCompound func_189515_b(NBTTagCompound var1) {
      super.func_189515_b(var1);
      if (!this.func_184282_c(var1)) {
         ItemStackHelper.func_191282_a(var1, this.field_146022_i);
      }

      ITextComponent var2 = this.func_200201_e();
      if (var2 != null) {
         var1.func_74778_a("CustomName", ITextComponent.Serializer.func_150696_a(var2));
      }

      return var1;
   }

   public int func_70297_j_() {
      return 64;
   }

   public String func_174875_k() {
      return "minecraft:dispenser";
   }

   public Container func_174876_a(InventoryPlayer var1, EntityPlayer var2) {
      this.func_184281_d(var2);
      return new ContainerDispenser(var1, this);
   }

   protected NonNullList<ItemStack> func_190576_q() {
      return this.field_146022_i;
   }

   protected void func_199721_a(NonNullList<ItemStack> var1) {
      this.field_146022_i = var1;
   }
}
