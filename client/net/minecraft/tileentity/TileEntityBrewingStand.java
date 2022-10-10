package net.minecraft.tileentity;

import java.util.Arrays;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.block.BlockBrewingStand;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerBrewingStand;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionBrewing;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class TileEntityBrewingStand extends TileEntityLockable implements ISidedInventory, ITickable {
   private static final int[] field_145941_a = new int[]{3};
   private static final int[] field_184277_f = new int[]{0, 1, 2, 3};
   private static final int[] field_145947_i = new int[]{0, 1, 2, 4};
   private NonNullList<ItemStack> field_145945_j;
   private int field_145946_k;
   private boolean[] field_145943_l;
   private Item field_145944_m;
   private ITextComponent field_145942_n;
   private int field_184278_m;

   public TileEntityBrewingStand() {
      super(TileEntityType.field_200981_l);
      this.field_145945_j = NonNullList.func_191197_a(5, ItemStack.field_190927_a);
   }

   public ITextComponent func_200200_C_() {
      return (ITextComponent)(this.field_145942_n != null ? this.field_145942_n : new TextComponentTranslation("container.brewing", new Object[0]));
   }

   public boolean func_145818_k_() {
      return this.field_145942_n != null;
   }

   @Nullable
   public ITextComponent func_200201_e() {
      return this.field_145942_n;
   }

   public void func_200224_a(@Nullable ITextComponent var1) {
      this.field_145942_n = var1;
   }

   public int func_70302_i_() {
      return this.field_145945_j.size();
   }

   public boolean func_191420_l() {
      Iterator var1 = this.field_145945_j.iterator();

      ItemStack var2;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         var2 = (ItemStack)var1.next();
      } while(var2.func_190926_b());

      return false;
   }

   public void func_73660_a() {
      ItemStack var1 = (ItemStack)this.field_145945_j.get(4);
      if (this.field_184278_m <= 0 && var1.func_77973_b() == Items.field_151065_br) {
         this.field_184278_m = 20;
         var1.func_190918_g(1);
         this.func_70296_d();
      }

      boolean var2 = this.func_145934_k();
      boolean var3 = this.field_145946_k > 0;
      ItemStack var4 = (ItemStack)this.field_145945_j.get(3);
      if (var3) {
         --this.field_145946_k;
         boolean var5 = this.field_145946_k == 0;
         if (var5 && var2) {
            this.func_145940_l();
            this.func_70296_d();
         } else if (!var2) {
            this.field_145946_k = 0;
            this.func_70296_d();
         } else if (this.field_145944_m != var4.func_77973_b()) {
            this.field_145946_k = 0;
            this.func_70296_d();
         }
      } else if (var2 && this.field_184278_m > 0) {
         --this.field_184278_m;
         this.field_145946_k = 400;
         this.field_145944_m = var4.func_77973_b();
         this.func_70296_d();
      }

      if (!this.field_145850_b.field_72995_K) {
         boolean[] var8 = this.func_174902_m();
         if (!Arrays.equals(var8, this.field_145943_l)) {
            this.field_145943_l = var8;
            IBlockState var6 = this.field_145850_b.func_180495_p(this.func_174877_v());
            if (!(var6.func_177230_c() instanceof BlockBrewingStand)) {
               return;
            }

            for(int var7 = 0; var7 < BlockBrewingStand.field_176451_a.length; ++var7) {
               var6 = (IBlockState)var6.func_206870_a(BlockBrewingStand.field_176451_a[var7], var8[var7]);
            }

            this.field_145850_b.func_180501_a(this.field_174879_c, var6, 2);
         }
      }

   }

   public boolean[] func_174902_m() {
      boolean[] var1 = new boolean[3];

      for(int var2 = 0; var2 < 3; ++var2) {
         if (!((ItemStack)this.field_145945_j.get(var2)).func_190926_b()) {
            var1[var2] = true;
         }
      }

      return var1;
   }

   private boolean func_145934_k() {
      ItemStack var1 = (ItemStack)this.field_145945_j.get(3);
      if (var1.func_190926_b()) {
         return false;
      } else if (!PotionBrewing.func_185205_a(var1)) {
         return false;
      } else {
         for(int var2 = 0; var2 < 3; ++var2) {
            ItemStack var3 = (ItemStack)this.field_145945_j.get(var2);
            if (!var3.func_190926_b() && PotionBrewing.func_185208_a(var3, var1)) {
               return true;
            }
         }

         return false;
      }
   }

   private void func_145940_l() {
      ItemStack var1 = (ItemStack)this.field_145945_j.get(3);

      for(int var2 = 0; var2 < 3; ++var2) {
         this.field_145945_j.set(var2, PotionBrewing.func_185212_d(var1, (ItemStack)this.field_145945_j.get(var2)));
      }

      var1.func_190918_g(1);
      BlockPos var4 = this.func_174877_v();
      if (var1.func_77973_b().func_77634_r()) {
         ItemStack var3 = new ItemStack(var1.func_77973_b().func_77668_q());
         if (var1.func_190926_b()) {
            var1 = var3;
         } else {
            InventoryHelper.func_180173_a(this.field_145850_b, (double)var4.func_177958_n(), (double)var4.func_177956_o(), (double)var4.func_177952_p(), var3);
         }
      }

      this.field_145945_j.set(3, var1);
      this.field_145850_b.func_175718_b(1035, var4, 0);
   }

   public void func_145839_a(NBTTagCompound var1) {
      super.func_145839_a(var1);
      this.field_145945_j = NonNullList.func_191197_a(this.func_70302_i_(), ItemStack.field_190927_a);
      ItemStackHelper.func_191283_b(var1, this.field_145945_j);
      this.field_145946_k = var1.func_74765_d("BrewTime");
      if (var1.func_150297_b("CustomName", 8)) {
         this.field_145942_n = ITextComponent.Serializer.func_150699_a(var1.func_74779_i("CustomName"));
      }

      this.field_184278_m = var1.func_74771_c("Fuel");
   }

   public NBTTagCompound func_189515_b(NBTTagCompound var1) {
      super.func_189515_b(var1);
      var1.func_74777_a("BrewTime", (short)this.field_145946_k);
      ItemStackHelper.func_191282_a(var1, this.field_145945_j);
      if (this.field_145942_n != null) {
         var1.func_74778_a("CustomName", ITextComponent.Serializer.func_150696_a(this.field_145942_n));
      }

      var1.func_74774_a("Fuel", (byte)this.field_184278_m);
      return var1;
   }

   public ItemStack func_70301_a(int var1) {
      return var1 >= 0 && var1 < this.field_145945_j.size() ? (ItemStack)this.field_145945_j.get(var1) : ItemStack.field_190927_a;
   }

   public ItemStack func_70298_a(int var1, int var2) {
      return ItemStackHelper.func_188382_a(this.field_145945_j, var1, var2);
   }

   public ItemStack func_70304_b(int var1) {
      return ItemStackHelper.func_188383_a(this.field_145945_j, var1);
   }

   public void func_70299_a(int var1, ItemStack var2) {
      if (var1 >= 0 && var1 < this.field_145945_j.size()) {
         this.field_145945_j.set(var1, var2);
      }

   }

   public int func_70297_j_() {
      return 64;
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
      if (var1 == 3) {
         return PotionBrewing.func_185205_a(var2);
      } else {
         Item var3 = var2.func_77973_b();
         if (var1 == 4) {
            return var3 == Items.field_151065_br;
         } else {
            return (var3 == Items.field_151068_bn || var3 == Items.field_185155_bH || var3 == Items.field_185156_bI || var3 == Items.field_151069_bo) && this.func_70301_a(var1).func_190926_b();
         }
      }
   }

   public int[] func_180463_a(EnumFacing var1) {
      if (var1 == EnumFacing.UP) {
         return field_145941_a;
      } else {
         return var1 == EnumFacing.DOWN ? field_184277_f : field_145947_i;
      }
   }

   public boolean func_180462_a(int var1, ItemStack var2, @Nullable EnumFacing var3) {
      return this.func_94041_b(var1, var2);
   }

   public boolean func_180461_b(int var1, ItemStack var2, EnumFacing var3) {
      if (var1 == 3) {
         return var2.func_77973_b() == Items.field_151069_bo;
      } else {
         return true;
      }
   }

   public String func_174875_k() {
      return "minecraft:brewing_stand";
   }

   public Container func_174876_a(InventoryPlayer var1, EntityPlayer var2) {
      return new ContainerBrewingStand(var1, this);
   }

   public int func_174887_a_(int var1) {
      switch(var1) {
      case 0:
         return this.field_145946_k;
      case 1:
         return this.field_184278_m;
      default:
         return 0;
      }
   }

   public void func_174885_b(int var1, int var2) {
      switch(var1) {
      case 0:
         this.field_145946_k = var2;
         break;
      case 1:
         this.field_184278_m = var2;
      }

   }

   public int func_174890_g() {
      return 2;
   }

   public void func_174888_l() {
      this.field_145945_j.clear();
   }
}
