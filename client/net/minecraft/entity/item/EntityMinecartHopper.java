package net.minecraft.entity.item;

import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerHopper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityMinecartHopper extends EntityMinecartContainer implements IHopper {
   private boolean field_96113_a = true;
   private int field_98044_b = -1;
   private final BlockPos field_174900_c;

   public EntityMinecartHopper(World var1) {
      super(EntityType.field_200776_P, var1);
      this.field_174900_c = BlockPos.field_177992_a;
   }

   public EntityMinecartHopper(World var1, double var2, double var4, double var6) {
      super(EntityType.field_200776_P, var2, var4, var6, var1);
      this.field_174900_c = BlockPos.field_177992_a;
   }

   public EntityMinecart.Type func_184264_v() {
      return EntityMinecart.Type.HOPPER;
   }

   public IBlockState func_180457_u() {
      return Blocks.field_150438_bZ.func_176223_P();
   }

   public int func_94085_r() {
      return 1;
   }

   public int func_70302_i_() {
      return 5;
   }

   public boolean func_184230_a(EntityPlayer var1, EnumHand var2) {
      if (!this.field_70170_p.field_72995_K) {
         var1.func_71007_a(this);
      }

      return true;
   }

   public void func_96095_a(int var1, int var2, int var3, boolean var4) {
      boolean var5 = !var4;
      if (var5 != this.func_96111_ay()) {
         this.func_96110_f(var5);
      }

   }

   public boolean func_96111_ay() {
      return this.field_96113_a;
   }

   public void func_96110_f(boolean var1) {
      this.field_96113_a = var1;
   }

   public World func_145831_w() {
      return this.field_70170_p;
   }

   public double func_96107_aA() {
      return this.field_70165_t;
   }

   public double func_96109_aB() {
      return this.field_70163_u + 0.5D;
   }

   public double func_96108_aC() {
      return this.field_70161_v;
   }

   public void func_70071_h_() {
      super.func_70071_h_();
      if (!this.field_70170_p.field_72995_K && this.func_70089_S() && this.func_96111_ay()) {
         BlockPos var1 = new BlockPos(this);
         if (var1.equals(this.field_174900_c)) {
            --this.field_98044_b;
         } else {
            this.func_98042_n(0);
         }

         if (!this.func_98043_aE()) {
            this.func_98042_n(0);
            if (this.func_96112_aD()) {
               this.func_98042_n(4);
               this.func_70296_d();
            }
         }
      }

   }

   public boolean func_96112_aD() {
      if (TileEntityHopper.func_145891_a(this)) {
         return true;
      } else {
         List var1 = this.field_70170_p.func_175647_a(EntityItem.class, this.func_174813_aQ().func_72314_b(0.25D, 0.0D, 0.25D), EntitySelectors.field_94557_a);
         if (!var1.isEmpty()) {
            TileEntityHopper.func_200114_a(this, (EntityItem)var1.get(0));
         }

         return false;
      }
   }

   public void func_94095_a(DamageSource var1) {
      super.func_94095_a(var1);
      if (this.field_70170_p.func_82736_K().func_82766_b("doEntityDrops")) {
         this.func_199703_a(Blocks.field_150438_bZ);
      }

   }

   protected void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74768_a("TransferCooldown", this.field_98044_b);
      var1.func_74757_a("Enabled", this.field_96113_a);
   }

   protected void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      this.field_98044_b = var1.func_74762_e("TransferCooldown");
      this.field_96113_a = var1.func_74764_b("Enabled") ? var1.func_74767_n("Enabled") : true;
   }

   public void func_98042_n(int var1) {
      this.field_98044_b = var1;
   }

   public boolean func_98043_aE() {
      return this.field_98044_b > 0;
   }

   public String func_174875_k() {
      return "minecraft:hopper";
   }

   public Container func_174876_a(InventoryPlayer var1, EntityPlayer var2) {
      return new ContainerHopper(var1, this, var2);
   }
}
