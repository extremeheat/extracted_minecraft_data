package net.minecraft.tileentity;

import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.state.properties.ChestType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockReader;

public class TileEntityChest extends TileEntityLockableLoot implements IChestLid, ITickable {
   private NonNullList<ItemStack> field_145985_p;
   protected float field_145989_m;
   protected float field_145986_n;
   protected int field_145987_o;
   private int field_145983_q;

   protected TileEntityChest(TileEntityType<?> var1) {
      super(var1);
      this.field_145985_p = NonNullList.func_191197_a(27, ItemStack.field_190927_a);
   }

   public TileEntityChest() {
      this(TileEntityType.field_200972_c);
   }

   public int func_70302_i_() {
      return 27;
   }

   public boolean func_191420_l() {
      Iterator var1 = this.field_145985_p.iterator();

      ItemStack var2;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         var2 = (ItemStack)var1.next();
      } while(var2.func_190926_b());

      return false;
   }

   public ITextComponent func_200200_C_() {
      ITextComponent var1 = this.func_200201_e();
      return (ITextComponent)(var1 != null ? var1 : new TextComponentTranslation("container.chest", new Object[0]));
   }

   public void func_145839_a(NBTTagCompound var1) {
      super.func_145839_a(var1);
      this.field_145985_p = NonNullList.func_191197_a(this.func_70302_i_(), ItemStack.field_190927_a);
      if (!this.func_184283_b(var1)) {
         ItemStackHelper.func_191283_b(var1, this.field_145985_p);
      }

      if (var1.func_150297_b("CustomName", 8)) {
         this.field_190577_o = ITextComponent.Serializer.func_150699_a(var1.func_74779_i("CustomName"));
      }

   }

   public NBTTagCompound func_189515_b(NBTTagCompound var1) {
      super.func_189515_b(var1);
      if (!this.func_184282_c(var1)) {
         ItemStackHelper.func_191282_a(var1, this.field_145985_p);
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

   public void func_73660_a() {
      int var1 = this.field_174879_c.func_177958_n();
      int var2 = this.field_174879_c.func_177956_o();
      int var3 = this.field_174879_c.func_177952_p();
      ++this.field_145983_q;
      float var4;
      if (!this.field_145850_b.field_72995_K && this.field_145987_o != 0 && (this.field_145983_q + var1 + var2 + var3) % 200 == 0) {
         this.field_145987_o = 0;
         var4 = 5.0F;
         List var5 = this.field_145850_b.func_72872_a(EntityPlayer.class, new AxisAlignedBB((double)((float)var1 - 5.0F), (double)((float)var2 - 5.0F), (double)((float)var3 - 5.0F), (double)((float)(var1 + 1) + 5.0F), (double)((float)(var2 + 1) + 5.0F), (double)((float)(var3 + 1) + 5.0F)));
         Iterator var6 = var5.iterator();

         label69:
         while(true) {
            IInventory var8;
            do {
               EntityPlayer var7;
               do {
                  if (!var6.hasNext()) {
                     break label69;
                  }

                  var7 = (EntityPlayer)var6.next();
               } while(!(var7.field_71070_bA instanceof ContainerChest));

               var8 = ((ContainerChest)var7.field_71070_bA).func_85151_d();
            } while(var8 != this && (!(var8 instanceof InventoryLargeChest) || !((InventoryLargeChest)var8).func_90010_a(this)));

            ++this.field_145987_o;
         }
      }

      this.field_145986_n = this.field_145989_m;
      var4 = 0.1F;
      if (this.field_145987_o > 0 && this.field_145989_m == 0.0F) {
         this.func_195483_a(SoundEvents.field_187657_V);
      }

      if (this.field_145987_o == 0 && this.field_145989_m > 0.0F || this.field_145987_o > 0 && this.field_145989_m < 1.0F) {
         float var9 = this.field_145989_m;
         if (this.field_145987_o > 0) {
            this.field_145989_m += 0.1F;
         } else {
            this.field_145989_m -= 0.1F;
         }

         if (this.field_145989_m > 1.0F) {
            this.field_145989_m = 1.0F;
         }

         float var10 = 0.5F;
         if (this.field_145989_m < 0.5F && var9 >= 0.5F) {
            this.func_195483_a(SoundEvents.field_187651_T);
         }

         if (this.field_145989_m < 0.0F) {
            this.field_145989_m = 0.0F;
         }
      }

   }

   private void func_195483_a(SoundEvent var1) {
      ChestType var2 = (ChestType)this.func_195044_w().func_177229_b(BlockChest.field_196314_b);
      if (var2 != ChestType.LEFT) {
         double var3 = (double)this.field_174879_c.func_177958_n() + 0.5D;
         double var5 = (double)this.field_174879_c.func_177956_o() + 0.5D;
         double var7 = (double)this.field_174879_c.func_177952_p() + 0.5D;
         if (var2 == ChestType.RIGHT) {
            EnumFacing var9 = BlockChest.func_196311_i(this.func_195044_w());
            var3 += (double)var9.func_82601_c() * 0.5D;
            var7 += (double)var9.func_82599_e() * 0.5D;
         }

         this.field_145850_b.func_184148_a((EntityPlayer)null, var3, var5, var7, var1, SoundCategory.BLOCKS, 0.5F, this.field_145850_b.field_73012_v.nextFloat() * 0.1F + 0.9F);
      }
   }

   public boolean func_145842_c(int var1, int var2) {
      if (var1 == 1) {
         this.field_145987_o = var2;
         return true;
      } else {
         return super.func_145842_c(var1, var2);
      }
   }

   public void func_174889_b(EntityPlayer var1) {
      if (!var1.func_175149_v()) {
         if (this.field_145987_o < 0) {
            this.field_145987_o = 0;
         }

         ++this.field_145987_o;
         this.func_195482_p();
      }

   }

   public void func_174886_c(EntityPlayer var1) {
      if (!var1.func_175149_v()) {
         --this.field_145987_o;
         this.func_195482_p();
      }

   }

   protected void func_195482_p() {
      Block var1 = this.func_195044_w().func_177230_c();
      if (var1 instanceof BlockChest) {
         this.field_145850_b.func_175641_c(this.field_174879_c, var1, 1, this.field_145987_o);
         this.field_145850_b.func_195593_d(this.field_174879_c, var1);
      }

   }

   public String func_174875_k() {
      return "minecraft:chest";
   }

   public Container func_174876_a(InventoryPlayer var1, EntityPlayer var2) {
      this.func_184281_d(var2);
      return new ContainerChest(var1, this, var2);
   }

   protected NonNullList<ItemStack> func_190576_q() {
      return this.field_145985_p;
   }

   protected void func_199721_a(NonNullList<ItemStack> var1) {
      this.field_145985_p = var1;
   }

   public float func_195480_a(float var1) {
      return this.field_145986_n + (this.field_145989_m - this.field_145986_n) * var1;
   }

   public static int func_195481_a(IBlockReader var0, BlockPos var1) {
      IBlockState var2 = var0.func_180495_p(var1);
      if (var2.func_177230_c().func_149716_u()) {
         TileEntity var3 = var0.func_175625_s(var1);
         if (var3 instanceof TileEntityChest) {
            return ((TileEntityChest)var3).field_145987_o;
         }
      }

      return 0;
   }

   public static void func_199722_a(TileEntityChest var0, TileEntityChest var1) {
      NonNullList var2 = var0.func_190576_q();
      var0.func_199721_a(var1.func_190576_q());
      var1.func_199721_a(var2);
   }
}
