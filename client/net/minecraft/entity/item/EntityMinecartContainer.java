package net.minecraft.entity.item;

import java.util.Iterator;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.LockCode;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.loot.ILootContainer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;

public abstract class EntityMinecartContainer extends EntityMinecart implements ILockableContainer, ILootContainer {
   private NonNullList<ItemStack> field_94113_a;
   private boolean field_94112_b;
   private ResourceLocation field_184290_c;
   private long field_184291_d;

   protected EntityMinecartContainer(EntityType<?> var1, World var2) {
      super(var1, var2);
      this.field_94113_a = NonNullList.func_191197_a(36, ItemStack.field_190927_a);
      this.field_94112_b = true;
   }

   protected EntityMinecartContainer(EntityType<?> var1, double var2, double var4, double var6, World var8) {
      super(var1, var8, var2, var4, var6);
      this.field_94113_a = NonNullList.func_191197_a(36, ItemStack.field_190927_a);
      this.field_94112_b = true;
   }

   public void func_94095_a(DamageSource var1) {
      super.func_94095_a(var1);
      if (this.field_70170_p.func_82736_K().func_82766_b("doEntityDrops")) {
         InventoryHelper.func_180176_a(this.field_70170_p, this, this);
      }

   }

   public boolean func_191420_l() {
      Iterator var1 = this.field_94113_a.iterator();

      ItemStack var2;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         var2 = (ItemStack)var1.next();
      } while(var2.func_190926_b());

      return false;
   }

   public ItemStack func_70301_a(int var1) {
      this.func_184288_f((EntityPlayer)null);
      return (ItemStack)this.field_94113_a.get(var1);
   }

   public ItemStack func_70298_a(int var1, int var2) {
      this.func_184288_f((EntityPlayer)null);
      return ItemStackHelper.func_188382_a(this.field_94113_a, var1, var2);
   }

   public ItemStack func_70304_b(int var1) {
      this.func_184288_f((EntityPlayer)null);
      ItemStack var2 = (ItemStack)this.field_94113_a.get(var1);
      if (var2.func_190926_b()) {
         return ItemStack.field_190927_a;
      } else {
         this.field_94113_a.set(var1, ItemStack.field_190927_a);
         return var2;
      }
   }

   public void func_70299_a(int var1, ItemStack var2) {
      this.func_184288_f((EntityPlayer)null);
      this.field_94113_a.set(var1, var2);
      if (!var2.func_190926_b() && var2.func_190916_E() > this.func_70297_j_()) {
         var2.func_190920_e(this.func_70297_j_());
      }

   }

   public boolean func_174820_d(int var1, ItemStack var2) {
      if (var1 >= 0 && var1 < this.func_70302_i_()) {
         this.func_70299_a(var1, var2);
         return true;
      } else {
         return false;
      }
   }

   public void func_70296_d() {
   }

   public boolean func_70300_a(EntityPlayer var1) {
      if (this.field_70128_L) {
         return false;
      } else {
         return var1.func_70068_e(this) <= 64.0D;
      }
   }

   public void func_174889_b(EntityPlayer var1) {
   }

   public void func_174886_c(EntityPlayer var1) {
   }

   public boolean func_94041_b(int var1, ItemStack var2) {
      return true;
   }

   public int func_70297_j_() {
      return 64;
   }

   @Nullable
   public Entity func_212321_a(DimensionType var1) {
      this.field_94112_b = false;
      return super.func_212321_a(var1);
   }

   public void func_70106_y() {
      if (this.field_94112_b) {
         InventoryHelper.func_180176_a(this.field_70170_p, this, this);
      }

      super.func_70106_y();
   }

   public void func_184174_b(boolean var1) {
      this.field_94112_b = var1;
   }

   protected void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      if (this.field_184290_c != null) {
         var1.func_74778_a("LootTable", this.field_184290_c.toString());
         if (this.field_184291_d != 0L) {
            var1.func_74772_a("LootTableSeed", this.field_184291_d);
         }
      } else {
         ItemStackHelper.func_191282_a(var1, this.field_94113_a);
      }

   }

   protected void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      this.field_94113_a = NonNullList.func_191197_a(this.func_70302_i_(), ItemStack.field_190927_a);
      if (var1.func_150297_b("LootTable", 8)) {
         this.field_184290_c = new ResourceLocation(var1.func_74779_i("LootTable"));
         this.field_184291_d = var1.func_74763_f("LootTableSeed");
      } else {
         ItemStackHelper.func_191283_b(var1, this.field_94113_a);
      }

   }

   public boolean func_184230_a(EntityPlayer var1, EnumHand var2) {
      if (!this.field_70170_p.field_72995_K) {
         var1.func_71007_a(this);
      }

      return true;
   }

   protected void func_94101_h() {
      float var1 = 0.98F;
      if (this.field_184290_c == null) {
         int var2 = 15 - Container.func_94526_b(this);
         var1 += (float)var2 * 0.001F;
      }

      this.field_70159_w *= (double)var1;
      this.field_70181_x *= 0.0D;
      this.field_70179_y *= (double)var1;
   }

   public int func_174887_a_(int var1) {
      return 0;
   }

   public void func_174885_b(int var1, int var2) {
   }

   public int func_174890_g() {
      return 0;
   }

   public boolean func_174893_q_() {
      return false;
   }

   public void func_174892_a(LockCode var1) {
   }

   public LockCode func_174891_i() {
      return LockCode.field_180162_a;
   }

   public void func_184288_f(@Nullable EntityPlayer var1) {
      if (this.field_184290_c != null && this.field_70170_p.func_73046_m() != null) {
         LootTable var2 = this.field_70170_p.func_73046_m().func_200249_aQ().func_186521_a(this.field_184290_c);
         this.field_184290_c = null;
         Random var3;
         if (this.field_184291_d == 0L) {
            var3 = new Random();
         } else {
            var3 = new Random(this.field_184291_d);
         }

         LootContext.Builder var4 = (new LootContext.Builder((WorldServer)this.field_70170_p)).func_204313_a(new BlockPos(this));
         if (var1 != null) {
            var4.func_186469_a(var1.func_184817_da());
         }

         var2.func_186460_a(this, var3, var4.func_186471_a());
      }

   }

   public void func_174888_l() {
      this.func_184288_f((EntityPlayer)null);
      this.field_94113_a.clear();
   }

   public void func_184289_a(ResourceLocation var1, long var2) {
      this.field_184290_c = var1;
      this.field_184291_d = var2;
   }

   public ResourceLocation func_184276_b() {
      return this.field_184290_c;
   }
}
