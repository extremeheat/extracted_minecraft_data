package net.minecraft.tileentity;

import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerShulkerBox;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class TileEntityShulkerBox extends TileEntityLockableLoot implements ISidedInventory, ITickable {
   private static final int[] field_190595_a = IntStream.range(0, 27).toArray();
   private NonNullList<ItemStack> field_190596_f;
   private boolean field_190597_g;
   private int field_190598_h;
   private TileEntityShulkerBox.AnimationStatus field_190599_i;
   private float field_190600_j;
   private float field_190601_k;
   private EnumDyeColor field_190602_l;
   private boolean field_204400_o;
   private boolean field_190594_p;

   public TileEntityShulkerBox(@Nullable EnumDyeColor var1) {
      super(TileEntityType.field_200993_x);
      this.field_190596_f = NonNullList.func_191197_a(27, ItemStack.field_190927_a);
      this.field_190599_i = TileEntityShulkerBox.AnimationStatus.CLOSED;
      this.field_190602_l = var1;
   }

   public TileEntityShulkerBox() {
      this((EnumDyeColor)null);
      this.field_204400_o = true;
   }

   public void func_73660_a() {
      this.func_190583_o();
      if (this.field_190599_i == TileEntityShulkerBox.AnimationStatus.OPENING || this.field_190599_i == TileEntityShulkerBox.AnimationStatus.CLOSING) {
         this.func_190589_G();
      }

   }

   protected void func_190583_o() {
      this.field_190601_k = this.field_190600_j;
      switch(this.field_190599_i) {
      case CLOSED:
         this.field_190600_j = 0.0F;
         break;
      case OPENING:
         this.field_190600_j += 0.1F;
         if (this.field_190600_j >= 1.0F) {
            this.func_190589_G();
            this.field_190599_i = TileEntityShulkerBox.AnimationStatus.OPENED;
            this.field_190600_j = 1.0F;
         }
         break;
      case CLOSING:
         this.field_190600_j -= 0.1F;
         if (this.field_190600_j <= 0.0F) {
            this.field_190599_i = TileEntityShulkerBox.AnimationStatus.CLOSED;
            this.field_190600_j = 0.0F;
         }
         break;
      case OPENED:
         this.field_190600_j = 1.0F;
      }

   }

   public TileEntityShulkerBox.AnimationStatus func_190591_p() {
      return this.field_190599_i;
   }

   public AxisAlignedBB func_190584_a(IBlockState var1) {
      return this.func_190587_b((EnumFacing)var1.func_177229_b(BlockShulkerBox.field_190957_a));
   }

   public AxisAlignedBB func_190587_b(EnumFacing var1) {
      return VoxelShapes.func_197868_b().func_197752_a().func_72321_a((double)(0.5F * this.func_190585_a(1.0F) * (float)var1.func_82601_c()), (double)(0.5F * this.func_190585_a(1.0F) * (float)var1.func_96559_d()), (double)(0.5F * this.func_190585_a(1.0F) * (float)var1.func_82599_e()));
   }

   private AxisAlignedBB func_190588_c(EnumFacing var1) {
      EnumFacing var2 = var1.func_176734_d();
      return this.func_190587_b(var1).func_191195_a((double)var2.func_82601_c(), (double)var2.func_96559_d(), (double)var2.func_82599_e());
   }

   private void func_190589_G() {
      IBlockState var1 = this.field_145850_b.func_180495_p(this.func_174877_v());
      if (var1.func_177230_c() instanceof BlockShulkerBox) {
         EnumFacing var2 = (EnumFacing)var1.func_177229_b(BlockShulkerBox.field_190957_a);
         AxisAlignedBB var3 = this.func_190588_c(var2).func_186670_a(this.field_174879_c);
         List var4 = this.field_145850_b.func_72839_b((Entity)null, var3);
         if (!var4.isEmpty()) {
            for(int var5 = 0; var5 < var4.size(); ++var5) {
               Entity var6 = (Entity)var4.get(var5);
               if (var6.func_184192_z() != EnumPushReaction.IGNORE) {
                  double var7 = 0.0D;
                  double var9 = 0.0D;
                  double var11 = 0.0D;
                  AxisAlignedBB var13 = var6.func_174813_aQ();
                  switch(var2.func_176740_k()) {
                  case X:
                     if (var2.func_176743_c() == EnumFacing.AxisDirection.POSITIVE) {
                        var7 = var3.field_72336_d - var13.field_72340_a;
                     } else {
                        var7 = var13.field_72336_d - var3.field_72340_a;
                     }

                     var7 += 0.01D;
                     break;
                  case Y:
                     if (var2.func_176743_c() == EnumFacing.AxisDirection.POSITIVE) {
                        var9 = var3.field_72337_e - var13.field_72338_b;
                     } else {
                        var9 = var13.field_72337_e - var3.field_72338_b;
                     }

                     var9 += 0.01D;
                     break;
                  case Z:
                     if (var2.func_176743_c() == EnumFacing.AxisDirection.POSITIVE) {
                        var11 = var3.field_72334_f - var13.field_72339_c;
                     } else {
                        var11 = var13.field_72334_f - var3.field_72339_c;
                     }

                     var11 += 0.01D;
                  }

                  var6.func_70091_d(MoverType.SHULKER_BOX, var7 * (double)var2.func_82601_c(), var9 * (double)var2.func_96559_d(), var11 * (double)var2.func_82599_e());
               }
            }

         }
      }
   }

   public int func_70302_i_() {
      return this.field_190596_f.size();
   }

   public int func_70297_j_() {
      return 64;
   }

   public boolean func_145842_c(int var1, int var2) {
      if (var1 == 1) {
         this.field_190598_h = var2;
         if (var2 == 0) {
            this.field_190599_i = TileEntityShulkerBox.AnimationStatus.CLOSING;
         }

         if (var2 == 1) {
            this.field_190599_i = TileEntityShulkerBox.AnimationStatus.OPENING;
         }

         return true;
      } else {
         return super.func_145842_c(var1, var2);
      }
   }

   public void func_174889_b(EntityPlayer var1) {
      if (!var1.func_175149_v()) {
         if (this.field_190598_h < 0) {
            this.field_190598_h = 0;
         }

         ++this.field_190598_h;
         this.field_145850_b.func_175641_c(this.field_174879_c, this.func_195044_w().func_177230_c(), 1, this.field_190598_h);
         if (this.field_190598_h == 1) {
            this.field_145850_b.func_184133_a((EntityPlayer)null, this.field_174879_c, SoundEvents.field_191262_fB, SoundCategory.BLOCKS, 0.5F, this.field_145850_b.field_73012_v.nextFloat() * 0.1F + 0.9F);
         }
      }

   }

   public void func_174886_c(EntityPlayer var1) {
      if (!var1.func_175149_v()) {
         --this.field_190598_h;
         this.field_145850_b.func_175641_c(this.field_174879_c, this.func_195044_w().func_177230_c(), 1, this.field_190598_h);
         if (this.field_190598_h <= 0) {
            this.field_145850_b.func_184133_a((EntityPlayer)null, this.field_174879_c, SoundEvents.field_191261_fA, SoundCategory.BLOCKS, 0.5F, this.field_145850_b.field_73012_v.nextFloat() * 0.1F + 0.9F);
         }
      }

   }

   public Container func_174876_a(InventoryPlayer var1, EntityPlayer var2) {
      return new ContainerShulkerBox(var1, this, var2);
   }

   public String func_174875_k() {
      return "minecraft:shulker_box";
   }

   public ITextComponent func_200200_C_() {
      ITextComponent var1 = this.func_200201_e();
      return (ITextComponent)(var1 != null ? var1 : new TextComponentTranslation("container.shulkerBox", new Object[0]));
   }

   public void func_145839_a(NBTTagCompound var1) {
      super.func_145839_a(var1);
      this.func_190586_e(var1);
   }

   public NBTTagCompound func_189515_b(NBTTagCompound var1) {
      super.func_189515_b(var1);
      return this.func_190580_f(var1);
   }

   public void func_190586_e(NBTTagCompound var1) {
      this.field_190596_f = NonNullList.func_191197_a(this.func_70302_i_(), ItemStack.field_190927_a);
      if (!this.func_184283_b(var1) && var1.func_150297_b("Items", 9)) {
         ItemStackHelper.func_191283_b(var1, this.field_190596_f);
      }

      if (var1.func_150297_b("CustomName", 8)) {
         this.field_190577_o = ITextComponent.Serializer.func_150699_a(var1.func_74779_i("CustomName"));
      }

   }

   public NBTTagCompound func_190580_f(NBTTagCompound var1) {
      if (!this.func_184282_c(var1)) {
         ItemStackHelper.func_191281_a(var1, this.field_190596_f, false);
      }

      ITextComponent var2 = this.func_200201_e();
      if (var2 != null) {
         var1.func_74778_a("CustomName", ITextComponent.Serializer.func_150696_a(var2));
      }

      if (!var1.func_74764_b("Lock") && this.func_174893_q_()) {
         this.func_174891_i().func_180157_a(var1);
      }

      return var1;
   }

   protected NonNullList<ItemStack> func_190576_q() {
      return this.field_190596_f;
   }

   protected void func_199721_a(NonNullList<ItemStack> var1) {
      this.field_190596_f = var1;
   }

   public boolean func_191420_l() {
      Iterator var1 = this.field_190596_f.iterator();

      ItemStack var2;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         var2 = (ItemStack)var1.next();
      } while(var2.func_190926_b());

      return false;
   }

   public int[] func_180463_a(EnumFacing var1) {
      return field_190595_a;
   }

   public boolean func_180462_a(int var1, ItemStack var2, @Nullable EnumFacing var3) {
      return !(Block.func_149634_a(var2.func_77973_b()) instanceof BlockShulkerBox);
   }

   public boolean func_180461_b(int var1, ItemStack var2, EnumFacing var3) {
      return true;
   }

   public void func_174888_l() {
      this.field_190597_g = true;
      super.func_174888_l();
   }

   public boolean func_190590_r() {
      return this.field_190597_g;
   }

   public float func_190585_a(float var1) {
      return this.field_190601_k + (this.field_190600_j - this.field_190601_k) * var1;
   }

   public EnumDyeColor func_190592_s() {
      if (this.field_204400_o) {
         this.field_190602_l = BlockShulkerBox.func_190954_c(this.func_195044_w().func_177230_c());
         this.field_204400_o = false;
      }

      return this.field_190602_l;
   }

   @Nullable
   public SPacketUpdateTileEntity func_189518_D_() {
      return new SPacketUpdateTileEntity(this.field_174879_c, 10, this.func_189517_E_());
   }

   public boolean func_190581_E() {
      return this.field_190594_p;
   }

   public void func_190579_a(boolean var1) {
      this.field_190594_p = var1;
   }

   public boolean func_190582_F() {
      return !this.func_190581_E() || !this.func_191420_l() || this.func_145818_k_() || this.field_184284_m != null;
   }

   public static enum AnimationStatus {
      CLOSED,
      OPENING,
      OPENED,
      CLOSING;

      private AnimationStatus() {
      }
   }
}
