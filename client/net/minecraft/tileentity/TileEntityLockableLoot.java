package net.minecraft.tileentity;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.ILootContainer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;

public abstract class TileEntityLockableLoot extends TileEntityLockable implements ILootContainer {
   protected ResourceLocation field_184284_m;
   protected long field_184285_n;
   protected ITextComponent field_190577_o;

   protected TileEntityLockableLoot(TileEntityType<?> var1) {
      super(var1);
   }

   public static void func_195479_a(IBlockReader var0, Random var1, BlockPos var2, ResourceLocation var3) {
      TileEntity var4 = var0.func_175625_s(var2);
      if (var4 instanceof TileEntityLockableLoot) {
         ((TileEntityLockableLoot)var4).func_189404_a(var3, var1.nextLong());
      }

   }

   protected boolean func_184283_b(NBTTagCompound var1) {
      if (var1.func_150297_b("LootTable", 8)) {
         this.field_184284_m = new ResourceLocation(var1.func_74779_i("LootTable"));
         this.field_184285_n = var1.func_74763_f("LootTableSeed");
         return true;
      } else {
         return false;
      }
   }

   protected boolean func_184282_c(NBTTagCompound var1) {
      if (this.field_184284_m == null) {
         return false;
      } else {
         var1.func_74778_a("LootTable", this.field_184284_m.toString());
         if (this.field_184285_n != 0L) {
            var1.func_74772_a("LootTableSeed", this.field_184285_n);
         }

         return true;
      }
   }

   public void func_184281_d(@Nullable EntityPlayer var1) {
      if (this.field_184284_m != null && this.field_145850_b.func_73046_m() != null) {
         LootTable var2 = this.field_145850_b.func_73046_m().func_200249_aQ().func_186521_a(this.field_184284_m);
         this.field_184284_m = null;
         Random var3;
         if (this.field_184285_n == 0L) {
            var3 = new Random();
         } else {
            var3 = new Random(this.field_184285_n);
         }

         LootContext.Builder var4 = new LootContext.Builder((WorldServer)this.field_145850_b);
         var4.func_204313_a(this.field_174879_c);
         if (var1 != null) {
            var4.func_186469_a(var1.func_184817_da());
         }

         var2.func_186460_a(this, var3, var4.func_186471_a());
      }

   }

   public ResourceLocation func_184276_b() {
      return this.field_184284_m;
   }

   public void func_189404_a(ResourceLocation var1, long var2) {
      this.field_184284_m = var1;
      this.field_184285_n = var2;
   }

   public boolean func_145818_k_() {
      return this.field_190577_o != null;
   }

   public void func_200226_a(@Nullable ITextComponent var1) {
      this.field_190577_o = var1;
   }

   @Nullable
   public ITextComponent func_200201_e() {
      return this.field_190577_o;
   }

   public ItemStack func_70301_a(int var1) {
      this.func_184281_d((EntityPlayer)null);
      return (ItemStack)this.func_190576_q().get(var1);
   }

   public ItemStack func_70298_a(int var1, int var2) {
      this.func_184281_d((EntityPlayer)null);
      ItemStack var3 = ItemStackHelper.func_188382_a(this.func_190576_q(), var1, var2);
      if (!var3.func_190926_b()) {
         this.func_70296_d();
      }

      return var3;
   }

   public ItemStack func_70304_b(int var1) {
      this.func_184281_d((EntityPlayer)null);
      return ItemStackHelper.func_188383_a(this.func_190576_q(), var1);
   }

   public void func_70299_a(int var1, @Nullable ItemStack var2) {
      this.func_184281_d((EntityPlayer)null);
      this.func_190576_q().set(var1, var2);
      if (var2.func_190916_E() > this.func_70297_j_()) {
         var2.func_190920_e(this.func_70297_j_());
      }

      this.func_70296_d();
   }

   public boolean func_70300_a(EntityPlayer var1) {
      if (this.field_145850_b.func_175625_s(this.field_174879_c) != this) {
         return false;
      } else {
         return var1.func_70092_e((double)this.field_174879_c.func_177958_n() + 0.5D, (double)this.field_174879_c.func_177956_o() + 0.5D, (double)this.field_174879_c.func_177952_p() + 0.5D) <= 64.0D;
      }
   }

   public void func_174889_b(EntityPlayer var1) {
   }

   public void func_174886_c(EntityPlayer var1) {
   }

   public boolean func_94041_b(int var1, ItemStack var2) {
      return true;
   }

   public int func_174887_a_(int var1) {
      return 0;
   }

   public void func_174885_b(int var1, int var2) {
   }

   public int func_174890_g() {
      return 0;
   }

   public void func_174888_l() {
      this.func_190576_q().clear();
   }

   protected abstract NonNullList<ItemStack> func_190576_q();

   protected abstract void func_199721_a(NonNullList<ItemStack> var1);
}
