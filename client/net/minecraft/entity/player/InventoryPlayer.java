package net.minecraft.entity.player;

import com.google.common.collect.ImmutableList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.tags.Tag;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class InventoryPlayer implements IInventory {
   public final NonNullList<ItemStack> field_70462_a;
   public final NonNullList<ItemStack> field_70460_b;
   public final NonNullList<ItemStack> field_184439_c;
   private final List<NonNullList<ItemStack>> field_184440_g;
   public int field_70461_c;
   public EntityPlayer field_70458_d;
   private ItemStack field_70457_g;
   private int field_194017_h;

   public InventoryPlayer(EntityPlayer var1) {
      super();
      this.field_70462_a = NonNullList.func_191197_a(36, ItemStack.field_190927_a);
      this.field_70460_b = NonNullList.func_191197_a(4, ItemStack.field_190927_a);
      this.field_184439_c = NonNullList.func_191197_a(1, ItemStack.field_190927_a);
      this.field_184440_g = ImmutableList.of(this.field_70462_a, this.field_70460_b, this.field_184439_c);
      this.field_70457_g = ItemStack.field_190927_a;
      this.field_70458_d = var1;
   }

   public ItemStack func_70448_g() {
      return func_184435_e(this.field_70461_c) ? (ItemStack)this.field_70462_a.get(this.field_70461_c) : ItemStack.field_190927_a;
   }

   public static int func_70451_h() {
      return 9;
   }

   private boolean func_184436_a(ItemStack var1, ItemStack var2) {
      return !var1.func_190926_b() && this.func_184431_b(var1, var2) && var1.func_77985_e() && var1.func_190916_E() < var1.func_77976_d() && var1.func_190916_E() < this.func_70297_j_();
   }

   private boolean func_184431_b(ItemStack var1, ItemStack var2) {
      return var1.func_77973_b() == var2.func_77973_b() && ItemStack.func_77970_a(var1, var2);
   }

   public int func_70447_i() {
      for(int var1 = 0; var1 < this.field_70462_a.size(); ++var1) {
         if (((ItemStack)this.field_70462_a.get(var1)).func_190926_b()) {
            return var1;
         }
      }

      return -1;
   }

   public void func_184434_a(ItemStack var1) {
      int var2 = this.func_184429_b(var1);
      if (func_184435_e(var2)) {
         this.field_70461_c = var2;
      } else {
         if (var2 == -1) {
            this.field_70461_c = this.func_184433_k();
            if (!((ItemStack)this.field_70462_a.get(this.field_70461_c)).func_190926_b()) {
               int var3 = this.func_70447_i();
               if (var3 != -1) {
                  this.field_70462_a.set(var3, this.field_70462_a.get(this.field_70461_c));
               }
            }

            this.field_70462_a.set(this.field_70461_c, var1);
         } else {
            this.func_184430_d(var2);
         }

      }
   }

   public void func_184430_d(int var1) {
      this.field_70461_c = this.func_184433_k();
      ItemStack var2 = (ItemStack)this.field_70462_a.get(this.field_70461_c);
      this.field_70462_a.set(this.field_70461_c, this.field_70462_a.get(var1));
      this.field_70462_a.set(var1, var2);
   }

   public static boolean func_184435_e(int var0) {
      return var0 >= 0 && var0 < 9;
   }

   public int func_184429_b(ItemStack var1) {
      for(int var2 = 0; var2 < this.field_70462_a.size(); ++var2) {
         if (!((ItemStack)this.field_70462_a.get(var2)).func_190926_b() && this.func_184431_b(var1, (ItemStack)this.field_70462_a.get(var2))) {
            return var2;
         }
      }

      return -1;
   }

   public int func_194014_c(ItemStack var1) {
      for(int var2 = 0; var2 < this.field_70462_a.size(); ++var2) {
         ItemStack var3 = (ItemStack)this.field_70462_a.get(var2);
         if (!((ItemStack)this.field_70462_a.get(var2)).func_190926_b() && this.func_184431_b(var1, (ItemStack)this.field_70462_a.get(var2)) && !((ItemStack)this.field_70462_a.get(var2)).func_77951_h() && !var3.func_77948_v() && !var3.func_82837_s()) {
            return var2;
         }
      }

      return -1;
   }

   public int func_184433_k() {
      int var1;
      int var2;
      for(var1 = 0; var1 < 9; ++var1) {
         var2 = (this.field_70461_c + var1) % 9;
         if (((ItemStack)this.field_70462_a.get(var2)).func_190926_b()) {
            return var2;
         }
      }

      for(var1 = 0; var1 < 9; ++var1) {
         var2 = (this.field_70461_c + var1) % 9;
         if (!((ItemStack)this.field_70462_a.get(var2)).func_77948_v()) {
            return var2;
         }
      }

      return this.field_70461_c;
   }

   public void func_195409_a(double var1) {
      if (var1 > 0.0D) {
         var1 = 1.0D;
      }

      if (var1 < 0.0D) {
         var1 = -1.0D;
      }

      for(this.field_70461_c = (int)((double)this.field_70461_c - var1); this.field_70461_c < 0; this.field_70461_c += 9) {
      }

      while(this.field_70461_c >= 9) {
         this.field_70461_c -= 9;
      }

   }

   public int func_195408_a(Predicate<ItemStack> var1, int var2) {
      int var3 = 0;

      int var4;
      for(var4 = 0; var4 < this.func_70302_i_(); ++var4) {
         ItemStack var5 = this.func_70301_a(var4);
         if (!var5.func_190926_b() && var1.test(var5)) {
            int var6 = var2 <= 0 ? var5.func_190916_E() : Math.min(var2 - var3, var5.func_190916_E());
            var3 += var6;
            if (var2 != 0) {
               var5.func_190918_g(var6);
               if (var5.func_190926_b()) {
                  this.func_70299_a(var4, ItemStack.field_190927_a);
               }

               if (var2 > 0 && var3 >= var2) {
                  return var3;
               }
            }
         }
      }

      if (!this.field_70457_g.func_190926_b() && var1.test(this.field_70457_g)) {
         var4 = var2 <= 0 ? this.field_70457_g.func_190916_E() : Math.min(var2 - var3, this.field_70457_g.func_190916_E());
         var3 += var4;
         if (var2 != 0) {
            this.field_70457_g.func_190918_g(var4);
            if (this.field_70457_g.func_190926_b()) {
               this.field_70457_g = ItemStack.field_190927_a;
            }

            if (var2 > 0 && var3 >= var2) {
               return var3;
            }
         }
      }

      return var3;
   }

   private int func_70452_e(ItemStack var1) {
      int var2 = this.func_70432_d(var1);
      if (var2 == -1) {
         var2 = this.func_70447_i();
      }

      return var2 == -1 ? var1.func_190916_E() : this.func_191973_d(var2, var1);
   }

   private int func_191973_d(int var1, ItemStack var2) {
      Item var3 = var2.func_77973_b();
      int var4 = var2.func_190916_E();
      ItemStack var5 = this.func_70301_a(var1);
      if (var5.func_190926_b()) {
         var5 = new ItemStack(var3, 0);
         if (var2.func_77942_o()) {
            var5.func_77982_d(var2.func_77978_p().func_74737_b());
         }

         this.func_70299_a(var1, var5);
      }

      int var6 = var4;
      if (var4 > var5.func_77976_d() - var5.func_190916_E()) {
         var6 = var5.func_77976_d() - var5.func_190916_E();
      }

      if (var6 > this.func_70297_j_() - var5.func_190916_E()) {
         var6 = this.func_70297_j_() - var5.func_190916_E();
      }

      if (var6 == 0) {
         return var4;
      } else {
         var4 -= var6;
         var5.func_190917_f(var6);
         var5.func_190915_d(5);
         return var4;
      }
   }

   public int func_70432_d(ItemStack var1) {
      if (this.func_184436_a(this.func_70301_a(this.field_70461_c), var1)) {
         return this.field_70461_c;
      } else if (this.func_184436_a(this.func_70301_a(40), var1)) {
         return 40;
      } else {
         for(int var2 = 0; var2 < this.field_70462_a.size(); ++var2) {
            if (this.func_184436_a((ItemStack)this.field_70462_a.get(var2), var1)) {
               return var2;
            }
         }

         return -1;
      }
   }

   public void func_70429_k() {
      Iterator var1 = this.field_184440_g.iterator();

      while(var1.hasNext()) {
         NonNullList var2 = (NonNullList)var1.next();

         for(int var3 = 0; var3 < var2.size(); ++var3) {
            if (!((ItemStack)var2.get(var3)).func_190926_b()) {
               ((ItemStack)var2.get(var3)).func_77945_a(this.field_70458_d.field_70170_p, this.field_70458_d, var3, this.field_70461_c == var3);
            }
         }
      }

   }

   public boolean func_70441_a(ItemStack var1) {
      return this.func_191971_c(-1, var1);
   }

   public boolean func_191971_c(int var1, ItemStack var2) {
      if (var2.func_190926_b()) {
         return false;
      } else {
         try {
            if (var2.func_77951_h()) {
               if (var1 == -1) {
                  var1 = this.func_70447_i();
               }

               if (var1 >= 0) {
                  this.field_70462_a.set(var1, var2.func_77946_l());
                  ((ItemStack)this.field_70462_a.get(var1)).func_190915_d(5);
                  var2.func_190920_e(0);
                  return true;
               } else if (this.field_70458_d.field_71075_bZ.field_75098_d) {
                  var2.func_190920_e(0);
                  return true;
               } else {
                  return false;
               }
            } else {
               int var3;
               do {
                  var3 = var2.func_190916_E();
                  if (var1 == -1) {
                     var2.func_190920_e(this.func_70452_e(var2));
                  } else {
                     var2.func_190920_e(this.func_191973_d(var1, var2));
                  }
               } while(!var2.func_190926_b() && var2.func_190916_E() < var3);

               if (var2.func_190916_E() == var3 && this.field_70458_d.field_71075_bZ.field_75098_d) {
                  var2.func_190920_e(0);
                  return true;
               } else {
                  return var2.func_190916_E() < var3;
               }
            }
         } catch (Throwable var6) {
            CrashReport var4 = CrashReport.func_85055_a(var6, "Adding item to inventory");
            CrashReportCategory var5 = var4.func_85058_a("Item being added");
            var5.func_71507_a("Item ID", Item.func_150891_b(var2.func_77973_b()));
            var5.func_71507_a("Item data", var2.func_77952_i());
            var5.func_189529_a("Item name", () -> {
               return var2.func_200301_q().getString();
            });
            throw new ReportedException(var4);
         }
      }
   }

   public void func_191975_a(World var1, ItemStack var2) {
      if (!var1.field_72995_K) {
         while(!var2.func_190926_b()) {
            int var3 = this.func_70432_d(var2);
            if (var3 == -1) {
               var3 = this.func_70447_i();
            }

            if (var3 == -1) {
               this.field_70458_d.func_71019_a(var2, false);
               break;
            }

            int var4 = var2.func_77976_d() - this.func_70301_a(var3).func_190916_E();
            if (this.func_191971_c(var3, var2.func_77979_a(var4))) {
               ((EntityPlayerMP)this.field_70458_d).field_71135_a.func_147359_a(new SPacketSetSlot(-2, var3, this.func_70301_a(var3)));
            }
         }

      }
   }

   public ItemStack func_70298_a(int var1, int var2) {
      NonNullList var3 = null;

      NonNullList var5;
      for(Iterator var4 = this.field_184440_g.iterator(); var4.hasNext(); var1 -= var5.size()) {
         var5 = (NonNullList)var4.next();
         if (var1 < var5.size()) {
            var3 = var5;
            break;
         }
      }

      return var3 != null && !((ItemStack)var3.get(var1)).func_190926_b() ? ItemStackHelper.func_188382_a(var3, var1, var2) : ItemStack.field_190927_a;
   }

   public void func_184437_d(ItemStack var1) {
      Iterator var2 = this.field_184440_g.iterator();

      while(true) {
         while(var2.hasNext()) {
            NonNullList var3 = (NonNullList)var2.next();

            for(int var4 = 0; var4 < var3.size(); ++var4) {
               if (var3.get(var4) == var1) {
                  var3.set(var4, ItemStack.field_190927_a);
                  break;
               }
            }
         }

         return;
      }
   }

   public ItemStack func_70304_b(int var1) {
      NonNullList var2 = null;

      NonNullList var4;
      for(Iterator var3 = this.field_184440_g.iterator(); var3.hasNext(); var1 -= var4.size()) {
         var4 = (NonNullList)var3.next();
         if (var1 < var4.size()) {
            var2 = var4;
            break;
         }
      }

      if (var2 != null && !((ItemStack)var2.get(var1)).func_190926_b()) {
         ItemStack var5 = (ItemStack)var2.get(var1);
         var2.set(var1, ItemStack.field_190927_a);
         return var5;
      } else {
         return ItemStack.field_190927_a;
      }
   }

   public void func_70299_a(int var1, ItemStack var2) {
      NonNullList var3 = null;

      NonNullList var5;
      for(Iterator var4 = this.field_184440_g.iterator(); var4.hasNext(); var1 -= var5.size()) {
         var5 = (NonNullList)var4.next();
         if (var1 < var5.size()) {
            var3 = var5;
            break;
         }
      }

      if (var3 != null) {
         var3.set(var1, var2);
      }

   }

   public float func_184438_a(IBlockState var1) {
      return ((ItemStack)this.field_70462_a.get(this.field_70461_c)).func_150997_a(var1);
   }

   public NBTTagList func_70442_a(NBTTagList var1) {
      int var2;
      NBTTagCompound var3;
      for(var2 = 0; var2 < this.field_70462_a.size(); ++var2) {
         if (!((ItemStack)this.field_70462_a.get(var2)).func_190926_b()) {
            var3 = new NBTTagCompound();
            var3.func_74774_a("Slot", (byte)var2);
            ((ItemStack)this.field_70462_a.get(var2)).func_77955_b(var3);
            var1.add((INBTBase)var3);
         }
      }

      for(var2 = 0; var2 < this.field_70460_b.size(); ++var2) {
         if (!((ItemStack)this.field_70460_b.get(var2)).func_190926_b()) {
            var3 = new NBTTagCompound();
            var3.func_74774_a("Slot", (byte)(var2 + 100));
            ((ItemStack)this.field_70460_b.get(var2)).func_77955_b(var3);
            var1.add((INBTBase)var3);
         }
      }

      for(var2 = 0; var2 < this.field_184439_c.size(); ++var2) {
         if (!((ItemStack)this.field_184439_c.get(var2)).func_190926_b()) {
            var3 = new NBTTagCompound();
            var3.func_74774_a("Slot", (byte)(var2 + 150));
            ((ItemStack)this.field_184439_c.get(var2)).func_77955_b(var3);
            var1.add((INBTBase)var3);
         }
      }

      return var1;
   }

   public void func_70443_b(NBTTagList var1) {
      this.field_70462_a.clear();
      this.field_70460_b.clear();
      this.field_184439_c.clear();

      for(int var2 = 0; var2 < var1.size(); ++var2) {
         NBTTagCompound var3 = var1.func_150305_b(var2);
         int var4 = var3.func_74771_c("Slot") & 255;
         ItemStack var5 = ItemStack.func_199557_a(var3);
         if (!var5.func_190926_b()) {
            if (var4 >= 0 && var4 < this.field_70462_a.size()) {
               this.field_70462_a.set(var4, var5);
            } else if (var4 >= 100 && var4 < this.field_70460_b.size() + 100) {
               this.field_70460_b.set(var4 - 100, var5);
            } else if (var4 >= 150 && var4 < this.field_184439_c.size() + 150) {
               this.field_184439_c.set(var4 - 150, var5);
            }
         }
      }

   }

   public int func_70302_i_() {
      return this.field_70462_a.size() + this.field_70460_b.size() + this.field_184439_c.size();
   }

   public boolean func_191420_l() {
      Iterator var1 = this.field_70462_a.iterator();

      ItemStack var2;
      do {
         if (!var1.hasNext()) {
            var1 = this.field_70460_b.iterator();

            do {
               if (!var1.hasNext()) {
                  var1 = this.field_184439_c.iterator();

                  do {
                     if (!var1.hasNext()) {
                        return true;
                     }

                     var2 = (ItemStack)var1.next();
                  } while(var2.func_190926_b());

                  return false;
               }

               var2 = (ItemStack)var1.next();
            } while(var2.func_190926_b());

            return false;
         }

         var2 = (ItemStack)var1.next();
      } while(var2.func_190926_b());

      return false;
   }

   public ItemStack func_70301_a(int var1) {
      NonNullList var2 = null;

      NonNullList var4;
      for(Iterator var3 = this.field_184440_g.iterator(); var3.hasNext(); var1 -= var4.size()) {
         var4 = (NonNullList)var3.next();
         if (var1 < var4.size()) {
            var2 = var4;
            break;
         }
      }

      return var2 == null ? ItemStack.field_190927_a : (ItemStack)var2.get(var1);
   }

   public ITextComponent func_200200_C_() {
      return new TextComponentTranslation("container.inventory", new Object[0]);
   }

   @Nullable
   public ITextComponent func_200201_e() {
      return null;
   }

   public boolean func_145818_k_() {
      return false;
   }

   public int func_70297_j_() {
      return 64;
   }

   public boolean func_184432_b(IBlockState var1) {
      return this.func_70301_a(this.field_70461_c).func_150998_b(var1);
   }

   public ItemStack func_70440_f(int var1) {
      return (ItemStack)this.field_70460_b.get(var1);
   }

   public void func_70449_g(float var1) {
      if (var1 > 0.0F) {
         var1 /= 4.0F;
         if (var1 < 1.0F) {
            var1 = 1.0F;
         }

         for(int var2 = 0; var2 < this.field_70460_b.size(); ++var2) {
            ItemStack var3 = (ItemStack)this.field_70460_b.get(var2);
            if (var3.func_77973_b() instanceof ItemArmor) {
               var3.func_77972_a((int)var1, this.field_70458_d);
            }
         }

      }
   }

   public void func_70436_m() {
      Iterator var1 = this.field_184440_g.iterator();

      while(var1.hasNext()) {
         List var2 = (List)var1.next();

         for(int var3 = 0; var3 < var2.size(); ++var3) {
            ItemStack var4 = (ItemStack)var2.get(var3);
            if (!var4.func_190926_b()) {
               this.field_70458_d.func_146097_a(var4, true, false);
               var2.set(var3, ItemStack.field_190927_a);
            }
         }
      }

   }

   public void func_70296_d() {
      ++this.field_194017_h;
   }

   public int func_194015_p() {
      return this.field_194017_h;
   }

   public void func_70437_b(ItemStack var1) {
      this.field_70457_g = var1;
   }

   public ItemStack func_70445_o() {
      return this.field_70457_g;
   }

   public boolean func_70300_a(EntityPlayer var1) {
      if (this.field_70458_d.field_70128_L) {
         return false;
      } else {
         return var1.func_70068_e(this.field_70458_d) <= 64.0D;
      }
   }

   public boolean func_70431_c(ItemStack var1) {
      Iterator var2 = this.field_184440_g.iterator();

      while(var2.hasNext()) {
         List var3 = (List)var2.next();
         Iterator var4 = var3.iterator();

         while(var4.hasNext()) {
            ItemStack var5 = (ItemStack)var4.next();
            if (!var5.func_190926_b() && var5.func_77969_a(var1)) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean func_199712_a(Tag<Item> var1) {
      Iterator var2 = this.field_184440_g.iterator();

      while(var2.hasNext()) {
         List var3 = (List)var2.next();
         Iterator var4 = var3.iterator();

         while(var4.hasNext()) {
            ItemStack var5 = (ItemStack)var4.next();
            if (!var5.func_190926_b() && var1.func_199685_a_(var5.func_77973_b())) {
               return true;
            }
         }
      }

      return false;
   }

   public void func_174889_b(EntityPlayer var1) {
   }

   public void func_174886_c(EntityPlayer var1) {
   }

   public boolean func_94041_b(int var1, ItemStack var2) {
      return true;
   }

   public void func_70455_b(InventoryPlayer var1) {
      for(int var2 = 0; var2 < this.func_70302_i_(); ++var2) {
         this.func_70299_a(var2, var1.func_70301_a(var2));
      }

      this.field_70461_c = var1.field_70461_c;
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
      Iterator var1 = this.field_184440_g.iterator();

      while(var1.hasNext()) {
         List var2 = (List)var1.next();
         var2.clear();
      }

   }

   public void func_201571_a(RecipeItemHelper var1) {
      Iterator var2 = this.field_70462_a.iterator();

      while(var2.hasNext()) {
         ItemStack var3 = (ItemStack)var2.next();
         var1.func_195932_a(var3);
      }

   }
}
