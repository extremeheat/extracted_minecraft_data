package net.minecraft.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.LockCode;

public abstract class TileEntityLockable extends TileEntity implements IInteractionObject, ILockableContainer {
   private LockCode field_174901_a;

   public TileEntityLockable() {
      super();
      this.field_174901_a = LockCode.field_180162_a;
   }

   public void func_145839_a(NBTTagCompound var1) {
      super.func_145839_a(var1);
      this.field_174901_a = LockCode.func_180158_b(var1);
   }

   public void func_145841_b(NBTTagCompound var1) {
      super.func_145841_b(var1);
      if (this.field_174901_a != null) {
         this.field_174901_a.func_180157_a(var1);
      }

   }

   public boolean func_174893_q_() {
      return this.field_174901_a != null && !this.field_174901_a.func_180160_a();
   }

   public LockCode func_174891_i() {
      return this.field_174901_a;
   }

   public void func_174892_a(LockCode var1) {
      this.field_174901_a = var1;
   }

   public IChatComponent func_145748_c_() {
      return (IChatComponent)(this.func_145818_k_() ? new ChatComponentText(this.func_70005_c_()) : new ChatComponentTranslation(this.func_70005_c_(), new Object[0]));
   }
}
