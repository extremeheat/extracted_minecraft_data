package net.minecraft.tileentity;

import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerHopper;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class TileEntityHopper extends TileEntityLockableLoot implements IHopper, ITickable {
   private NonNullList<ItemStack> field_145900_a;
   private int field_145901_j;
   private long field_190578_g;

   public TileEntityHopper() {
      super(TileEntityType.field_200987_r);
      this.field_145900_a = NonNullList.func_191197_a(5, ItemStack.field_190927_a);
      this.field_145901_j = -1;
   }

   public void func_145839_a(NBTTagCompound var1) {
      super.func_145839_a(var1);
      this.field_145900_a = NonNullList.func_191197_a(this.func_70302_i_(), ItemStack.field_190927_a);
      if (!this.func_184283_b(var1)) {
         ItemStackHelper.func_191283_b(var1, this.field_145900_a);
      }

      if (var1.func_150297_b("CustomName", 8)) {
         this.func_200226_a(ITextComponent.Serializer.func_150699_a(var1.func_74779_i("CustomName")));
      }

      this.field_145901_j = var1.func_74762_e("TransferCooldown");
   }

   public NBTTagCompound func_189515_b(NBTTagCompound var1) {
      super.func_189515_b(var1);
      if (!this.func_184282_c(var1)) {
         ItemStackHelper.func_191282_a(var1, this.field_145900_a);
      }

      var1.func_74768_a("TransferCooldown", this.field_145901_j);
      ITextComponent var2 = this.func_200201_e();
      if (var2 != null) {
         var1.func_74778_a("CustomName", ITextComponent.Serializer.func_150696_a(var2));
      }

      return var1;
   }

   public int func_70302_i_() {
      return this.field_145900_a.size();
   }

   public ItemStack func_70298_a(int var1, int var2) {
      this.func_184281_d((EntityPlayer)null);
      return ItemStackHelper.func_188382_a(this.func_190576_q(), var1, var2);
   }

   public void func_70299_a(int var1, ItemStack var2) {
      this.func_184281_d((EntityPlayer)null);
      this.func_190576_q().set(var1, var2);
      if (var2.func_190916_E() > this.func_70297_j_()) {
         var2.func_190920_e(this.func_70297_j_());
      }

   }

   public ITextComponent func_200200_C_() {
      return (ITextComponent)(this.field_190577_o != null ? this.field_190577_o : new TextComponentTranslation("container.hopper", new Object[0]));
   }

   public int func_70297_j_() {
      return 64;
   }

   public void func_73660_a() {
      if (this.field_145850_b != null && !this.field_145850_b.field_72995_K) {
         --this.field_145901_j;
         this.field_190578_g = this.field_145850_b.func_82737_E();
         if (!this.func_145888_j()) {
            this.func_145896_c(0);
            this.func_200109_a(() -> {
               return func_145891_a(this);
            });
         }

      }
   }

   private boolean func_200109_a(Supplier<Boolean> var1) {
      if (this.field_145850_b != null && !this.field_145850_b.field_72995_K) {
         if (!this.func_145888_j() && (Boolean)this.func_195044_w().func_177229_b(BlockHopper.field_176429_b)) {
            boolean var2 = false;
            if (!this.func_152104_k()) {
               var2 = this.func_145883_k();
            }

            if (!this.func_152105_l()) {
               var2 |= (Boolean)var1.get();
            }

            if (var2) {
               this.func_145896_c(8);
               this.func_70296_d();
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private boolean func_152104_k() {
      Iterator var1 = this.field_145900_a.iterator();

      ItemStack var2;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         var2 = (ItemStack)var1.next();
      } while(var2.func_190926_b());

      return false;
   }

   public boolean func_191420_l() {
      return this.func_152104_k();
   }

   private boolean func_152105_l() {
      Iterator var1 = this.field_145900_a.iterator();

      ItemStack var2;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         var2 = (ItemStack)var1.next();
      } while(!var2.func_190926_b() && var2.func_190916_E() == var2.func_77976_d());

      return false;
   }

   private boolean func_145883_k() {
      IInventory var1 = this.func_145895_l();
      if (var1 == null) {
         return false;
      } else {
         EnumFacing var2 = ((EnumFacing)this.func_195044_w().func_177229_b(BlockHopper.field_176430_a)).func_176734_d();
         if (this.func_174919_a(var1, var2)) {
            return false;
         } else {
            for(int var3 = 0; var3 < this.func_70302_i_(); ++var3) {
               if (!this.func_70301_a(var3).func_190926_b()) {
                  ItemStack var4 = this.func_70301_a(var3).func_77946_l();
                  ItemStack var5 = func_174918_a(this, var1, this.func_70298_a(var3, 1), var2);
                  if (var5.func_190926_b()) {
                     var1.func_70296_d();
                     return true;
                  }

                  this.func_70299_a(var3, var4);
               }
            }

            return false;
         }
      }
   }

   private boolean func_174919_a(IInventory var1, EnumFacing var2) {
      if (var1 instanceof ISidedInventory) {
         ISidedInventory var10 = (ISidedInventory)var1;
         int[] var11 = var10.func_180463_a(var2);
         int[] var12 = var11;
         int var6 = var11.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            int var8 = var12[var7];
            ItemStack var9 = var10.func_70301_a(var8);
            if (var9.func_190926_b() || var9.func_190916_E() != var9.func_77976_d()) {
               return false;
            }
         }
      } else {
         int var3 = var1.func_70302_i_();

         for(int var4 = 0; var4 < var3; ++var4) {
            ItemStack var5 = var1.func_70301_a(var4);
            if (var5.func_190926_b() || var5.func_190916_E() != var5.func_77976_d()) {
               return false;
            }
         }
      }

      return true;
   }

   private static boolean func_174917_b(IInventory var0, EnumFacing var1) {
      if (var0 instanceof ISidedInventory) {
         ISidedInventory var2 = (ISidedInventory)var0;
         int[] var3 = var2.func_180463_a(var1);
         int[] var4 = var3;
         int var5 = var3.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            int var7 = var4[var6];
            if (!var2.func_70301_a(var7).func_190926_b()) {
               return false;
            }
         }
      } else {
         int var8 = var0.func_70302_i_();

         for(int var9 = 0; var9 < var8; ++var9) {
            if (!var0.func_70301_a(var9).func_190926_b()) {
               return false;
            }
         }
      }

      return true;
   }

   public static boolean func_145891_a(IHopper var0) {
      IInventory var1 = func_145884_b(var0);
      if (var1 != null) {
         EnumFacing var2 = EnumFacing.DOWN;
         if (func_174917_b(var1, var2)) {
            return false;
         }

         if (var1 instanceof ISidedInventory) {
            ISidedInventory var3 = (ISidedInventory)var1;
            int[] var4 = var3.func_180463_a(var2);
            int[] var5 = var4;
            int var6 = var4.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               int var8 = var5[var7];
               if (func_174915_a(var0, var1, var8, var2)) {
                  return true;
               }
            }
         } else {
            int var10 = var1.func_70302_i_();

            for(int var12 = 0; var12 < var10; ++var12) {
               if (func_174915_a(var0, var1, var12, var2)) {
                  return true;
               }
            }
         }
      } else {
         Iterator var9 = func_200115_c(var0).iterator();

         while(var9.hasNext()) {
            EntityItem var11 = (EntityItem)var9.next();
            if (func_200114_a(var0, var11)) {
               return true;
            }
         }
      }

      return false;
   }

   private static boolean func_174915_a(IHopper var0, IInventory var1, int var2, EnumFacing var3) {
      ItemStack var4 = var1.func_70301_a(var2);
      if (!var4.func_190926_b() && func_174921_b(var1, var4, var2, var3)) {
         ItemStack var5 = var4.func_77946_l();
         ItemStack var6 = func_174918_a(var1, var0, var1.func_70298_a(var2, 1), (EnumFacing)null);
         if (var6.func_190926_b()) {
            var1.func_70296_d();
            return true;
         }

         var1.func_70299_a(var2, var5);
      }

      return false;
   }

   public static boolean func_200114_a(IInventory var0, EntityItem var1) {
      boolean var2 = false;
      ItemStack var3 = var1.func_92059_d().func_77946_l();
      ItemStack var4 = func_174918_a((IInventory)null, var0, var3, (EnumFacing)null);
      if (var4.func_190926_b()) {
         var2 = true;
         var1.func_70106_y();
      } else {
         var1.func_92058_a(var4);
      }

      return var2;
   }

   public static ItemStack func_174918_a(@Nullable IInventory var0, IInventory var1, ItemStack var2, @Nullable EnumFacing var3) {
      if (var1 instanceof ISidedInventory && var3 != null) {
         ISidedInventory var7 = (ISidedInventory)var1;
         int[] var8 = var7.func_180463_a(var3);

         for(int var6 = 0; var6 < var8.length && !var2.func_190926_b(); ++var6) {
            var2 = func_174916_c(var0, var1, var2, var8[var6], var3);
         }
      } else {
         int var4 = var1.func_70302_i_();

         for(int var5 = 0; var5 < var4 && !var2.func_190926_b(); ++var5) {
            var2 = func_174916_c(var0, var1, var2, var5, var3);
         }
      }

      return var2;
   }

   private static boolean func_174920_a(IInventory var0, ItemStack var1, int var2, @Nullable EnumFacing var3) {
      if (!var0.func_94041_b(var2, var1)) {
         return false;
      } else {
         return !(var0 instanceof ISidedInventory) || ((ISidedInventory)var0).func_180462_a(var2, var1, var3);
      }
   }

   private static boolean func_174921_b(IInventory var0, ItemStack var1, int var2, EnumFacing var3) {
      return !(var0 instanceof ISidedInventory) || ((ISidedInventory)var0).func_180461_b(var2, var1, var3);
   }

   private static ItemStack func_174916_c(@Nullable IInventory var0, IInventory var1, ItemStack var2, int var3, @Nullable EnumFacing var4) {
      ItemStack var5 = var1.func_70301_a(var3);
      if (func_174920_a(var1, var2, var3, var4)) {
         boolean var6 = false;
         boolean var7 = var1.func_191420_l();
         if (var5.func_190926_b()) {
            var1.func_70299_a(var3, var2);
            var2 = ItemStack.field_190927_a;
            var6 = true;
         } else if (func_145894_a(var5, var2)) {
            int var8 = var2.func_77976_d() - var5.func_190916_E();
            int var9 = Math.min(var2.func_190916_E(), var8);
            var2.func_190918_g(var9);
            var5.func_190917_f(var9);
            var6 = var9 > 0;
         }

         if (var6) {
            if (var7 && var1 instanceof TileEntityHopper) {
               TileEntityHopper var11 = (TileEntityHopper)var1;
               if (!var11.func_174914_o()) {
                  byte var12 = 0;
                  if (var0 instanceof TileEntityHopper) {
                     TileEntityHopper var10 = (TileEntityHopper)var0;
                     if (var11.field_190578_g >= var10.field_190578_g) {
                        var12 = 1;
                     }
                  }

                  var11.func_145896_c(8 - var12);
               }
            }

            var1.func_70296_d();
         }
      }

      return var2;
   }

   @Nullable
   private IInventory func_145895_l() {
      EnumFacing var1 = (EnumFacing)this.func_195044_w().func_177229_b(BlockHopper.field_176430_a);
      return func_195484_a(this.func_145831_w(), this.field_174879_c.func_177972_a(var1));
   }

   @Nullable
   public static IInventory func_145884_b(IHopper var0) {
      return func_145893_b(var0.func_145831_w(), var0.func_96107_aA(), var0.func_96109_aB() + 1.0D, var0.func_96108_aC());
   }

   public static List<EntityItem> func_200115_c(IHopper var0) {
      return (List)var0.func_200100_i().func_197756_d().stream().flatMap((var1) -> {
         return var0.func_145831_w().func_175647_a(EntityItem.class, var1.func_72317_d(var0.func_96107_aA() - 0.5D, var0.func_96109_aB() - 0.5D, var0.func_96108_aC() - 0.5D), EntitySelectors.field_94557_a).stream();
      }).collect(Collectors.toList());
   }

   @Nullable
   public static IInventory func_195484_a(World var0, BlockPos var1) {
      return func_145893_b(var0, (double)var1.func_177958_n() + 0.5D, (double)var1.func_177956_o() + 0.5D, (double)var1.func_177952_p() + 0.5D);
   }

   @Nullable
   public static IInventory func_145893_b(World var0, double var1, double var3, double var5) {
      Object var7 = null;
      BlockPos var8 = new BlockPos(var1, var3, var5);
      IBlockState var9 = var0.func_180495_p(var8);
      Block var10 = var9.func_177230_c();
      if (var10.func_149716_u()) {
         TileEntity var11 = var0.func_175625_s(var8);
         if (var11 instanceof IInventory) {
            var7 = (IInventory)var11;
            if (var7 instanceof TileEntityChest && var10 instanceof BlockChest) {
               var7 = ((BlockChest)var10).func_196309_a(var9, var0, var8, true);
            }
         }
      }

      if (var7 == null) {
         List var12 = var0.func_175674_a((Entity)null, new AxisAlignedBB(var1 - 0.5D, var3 - 0.5D, var5 - 0.5D, var1 + 0.5D, var3 + 0.5D, var5 + 0.5D), EntitySelectors.field_96566_b);
         if (!var12.isEmpty()) {
            var7 = (IInventory)var12.get(var0.field_73012_v.nextInt(var12.size()));
         }
      }

      return (IInventory)var7;
   }

   private static boolean func_145894_a(ItemStack var0, ItemStack var1) {
      if (var0.func_77973_b() != var1.func_77973_b()) {
         return false;
      } else if (var0.func_77952_i() != var1.func_77952_i()) {
         return false;
      } else if (var0.func_190916_E() > var0.func_77976_d()) {
         return false;
      } else {
         return ItemStack.func_77970_a(var0, var1);
      }
   }

   public double func_96107_aA() {
      return (double)this.field_174879_c.func_177958_n() + 0.5D;
   }

   public double func_96109_aB() {
      return (double)this.field_174879_c.func_177956_o() + 0.5D;
   }

   public double func_96108_aC() {
      return (double)this.field_174879_c.func_177952_p() + 0.5D;
   }

   private void func_145896_c(int var1) {
      this.field_145901_j = var1;
   }

   private boolean func_145888_j() {
      return this.field_145901_j > 0;
   }

   private boolean func_174914_o() {
      return this.field_145901_j > 8;
   }

   public String func_174875_k() {
      return "minecraft:hopper";
   }

   public Container func_174876_a(InventoryPlayer var1, EntityPlayer var2) {
      this.func_184281_d(var2);
      return new ContainerHopper(var1, this, var2);
   }

   protected NonNullList<ItemStack> func_190576_q() {
      return this.field_145900_a;
   }

   protected void func_199721_a(NonNullList<ItemStack> var1) {
      this.field_145900_a = var1;
   }

   public void func_200113_a(Entity var1) {
      if (var1 instanceof EntityItem) {
         BlockPos var2 = this.func_174877_v();
         if (VoxelShapes.func_197879_c(VoxelShapes.func_197881_a(var1.func_174813_aQ().func_72317_d((double)(-var2.func_177958_n()), (double)(-var2.func_177956_o()), (double)(-var2.func_177952_p()))), this.func_200100_i(), IBooleanFunction.AND)) {
            this.func_200109_a(() -> {
               return func_200114_a(this, (EntityItem)var1);
            });
         }
      }

   }
}
