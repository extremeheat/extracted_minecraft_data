package net.minecraft.block;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockShulkerBox extends BlockContainer {
   public static final EnumProperty<EnumFacing> field_190957_a;
   @Nullable
   private final EnumDyeColor field_190958_b;

   public BlockShulkerBox(@Nullable EnumDyeColor var1, Block.Properties var2) {
      super(var2);
      this.field_190958_b = var1;
      this.func_180632_j((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_190957_a, EnumFacing.UP));
   }

   public TileEntity func_196283_a_(IBlockReader var1) {
      return new TileEntityShulkerBox(this.field_190958_b);
   }

   public boolean func_176214_u(IBlockState var1) {
      return true;
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public boolean func_190946_v(IBlockState var1) {
      return true;
   }

   public EnumBlockRenderType func_149645_b(IBlockState var1) {
      return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
   }

   public boolean func_196250_a(IBlockState var1, World var2, BlockPos var3, EntityPlayer var4, EnumHand var5, EnumFacing var6, float var7, float var8, float var9) {
      if (var2.field_72995_K) {
         return true;
      } else if (var4.func_175149_v()) {
         return true;
      } else {
         TileEntity var10 = var2.func_175625_s(var3);
         if (var10 instanceof TileEntityShulkerBox) {
            EnumFacing var11 = (EnumFacing)var1.func_177229_b(field_190957_a);
            boolean var12;
            if (((TileEntityShulkerBox)var10).func_190591_p() == TileEntityShulkerBox.AnimationStatus.CLOSED) {
               AxisAlignedBB var13 = VoxelShapes.func_197868_b().func_197752_a().func_72321_a((double)(0.5F * (float)var11.func_82601_c()), (double)(0.5F * (float)var11.func_96559_d()), (double)(0.5F * (float)var11.func_82599_e())).func_191195_a((double)var11.func_82601_c(), (double)var11.func_96559_d(), (double)var11.func_82599_e());
               var12 = var2.func_195586_b((Entity)null, var13.func_186670_a(var3.func_177972_a(var11)));
            } else {
               var12 = true;
            }

            if (var12) {
               var4.func_195066_a(StatList.field_191272_ae);
               var4.func_71007_a((IInventory)var10);
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public IBlockState func_196258_a(BlockItemUseContext var1) {
      return (IBlockState)this.func_176223_P().func_206870_a(field_190957_a, var1.func_196000_l());
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_190957_a);
   }

   public void func_176208_a(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4) {
      if (var1.func_175625_s(var2) instanceof TileEntityShulkerBox) {
         TileEntityShulkerBox var5 = (TileEntityShulkerBox)var1.func_175625_s(var2);
         var5.func_190579_a(var4.field_71075_bZ.field_75098_d);
         var5.func_184281_d(var4);
      }

      super.func_176208_a(var1, var2, var3, var4);
   }

   public void func_196255_a(IBlockState var1, World var2, BlockPos var3, float var4, int var5) {
   }

   public void func_180633_a(World var1, BlockPos var2, IBlockState var3, EntityLivingBase var4, ItemStack var5) {
      if (var5.func_82837_s()) {
         TileEntity var6 = var1.func_175625_s(var2);
         if (var6 instanceof TileEntityShulkerBox) {
            ((TileEntityShulkerBox)var6).func_200226_a(var5.func_200301_q());
         }
      }

   }

   public void func_196243_a(IBlockState var1, World var2, BlockPos var3, IBlockState var4, boolean var5) {
      if (var1.func_177230_c() != var4.func_177230_c()) {
         TileEntity var6 = var2.func_175625_s(var3);
         if (var6 instanceof TileEntityShulkerBox) {
            TileEntityShulkerBox var7 = (TileEntityShulkerBox)var6;
            if (!var7.func_190590_r() && var7.func_190582_F()) {
               ItemStack var8 = new ItemStack(this);
               var8.func_196082_o().func_74782_a("BlockEntityTag", ((TileEntityShulkerBox)var6).func_190580_f(new NBTTagCompound()));
               if (var7.func_145818_k_()) {
                  var8.func_200302_a(var7.func_200201_e());
                  var7.func_200226_a((ITextComponent)null);
               }

               func_180635_a(var2, var3, var8);
            }

            var2.func_175666_e(var3, var1.func_177230_c());
         }

         super.func_196243_a(var1, var2, var3, var4, var5);
      }
   }

   public void func_190948_a(ItemStack var1, @Nullable IBlockReader var2, List<ITextComponent> var3, ITooltipFlag var4) {
      super.func_190948_a(var1, var2, var3, var4);
      NBTTagCompound var5 = var1.func_179543_a("BlockEntityTag");
      if (var5 != null) {
         if (var5.func_150297_b("LootTable", 8)) {
            var3.add(new TextComponentString("???????"));
         }

         if (var5.func_150297_b("Items", 9)) {
            NonNullList var6 = NonNullList.func_191197_a(27, ItemStack.field_190927_a);
            ItemStackHelper.func_191283_b(var5, var6);
            int var7 = 0;
            int var8 = 0;
            Iterator var9 = var6.iterator();

            while(var9.hasNext()) {
               ItemStack var10 = (ItemStack)var9.next();
               if (!var10.func_190926_b()) {
                  ++var8;
                  if (var7 <= 4) {
                     ++var7;
                     ITextComponent var11 = var10.func_200301_q().func_212638_h();
                     var11.func_150258_a(" x").func_150258_a(String.valueOf(var10.func_190916_E()));
                     var3.add(var11);
                  }
               }
            }

            if (var8 - var7 > 0) {
               var3.add((new TextComponentTranslation("container.shulkerBox.more", new Object[]{var8 - var7})).func_211708_a(TextFormatting.ITALIC));
            }
         }
      }

   }

   public EnumPushReaction func_149656_h(IBlockState var1) {
      return EnumPushReaction.DESTROY;
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      TileEntity var4 = var2.func_175625_s(var3);
      return var4 instanceof TileEntityShulkerBox ? VoxelShapes.func_197881_a(((TileEntityShulkerBox)var4).func_190584_a(var1)) : VoxelShapes.func_197868_b();
   }

   public boolean func_200124_e(IBlockState var1) {
      return false;
   }

   public boolean func_149740_M(IBlockState var1) {
      return true;
   }

   public int func_180641_l(IBlockState var1, World var2, BlockPos var3) {
      return Container.func_94526_b((IInventory)var2.func_175625_s(var3));
   }

   public ItemStack func_185473_a(IBlockReader var1, BlockPos var2, IBlockState var3) {
      ItemStack var4 = super.func_185473_a(var1, var2, var3);
      TileEntityShulkerBox var5 = (TileEntityShulkerBox)var1.func_175625_s(var2);
      NBTTagCompound var6 = var5.func_190580_f(new NBTTagCompound());
      if (!var6.isEmpty()) {
         var4.func_77983_a("BlockEntityTag", var6);
      }

      return var4;
   }

   public static EnumDyeColor func_190955_b(Item var0) {
      return func_190954_c(Block.func_149634_a(var0));
   }

   public static EnumDyeColor func_190954_c(Block var0) {
      return var0 instanceof BlockShulkerBox ? ((BlockShulkerBox)var0).func_190956_e() : null;
   }

   public static Block func_190952_a(EnumDyeColor var0) {
      if (var0 == null) {
         return Blocks.field_204409_il;
      } else {
         switch(var0) {
         case WHITE:
            return Blocks.field_190977_dl;
         case ORANGE:
            return Blocks.field_190978_dm;
         case MAGENTA:
            return Blocks.field_190979_dn;
         case LIGHT_BLUE:
            return Blocks.field_190980_do;
         case YELLOW:
            return Blocks.field_190981_dp;
         case LIME:
            return Blocks.field_190982_dq;
         case PINK:
            return Blocks.field_190983_dr;
         case GRAY:
            return Blocks.field_190984_ds;
         case LIGHT_GRAY:
            return Blocks.field_196875_ie;
         case CYAN:
            return Blocks.field_190986_du;
         case PURPLE:
         default:
            return Blocks.field_190987_dv;
         case BLUE:
            return Blocks.field_190988_dw;
         case BROWN:
            return Blocks.field_190989_dx;
         case GREEN:
            return Blocks.field_190990_dy;
         case RED:
            return Blocks.field_190991_dz;
         case BLACK:
            return Blocks.field_190975_dA;
         }
      }
   }

   public EnumDyeColor func_190956_e() {
      return this.field_190958_b;
   }

   public static ItemStack func_190953_b(EnumDyeColor var0) {
      return new ItemStack(func_190952_a(var0));
   }

   public IBlockState func_185499_a(IBlockState var1, Rotation var2) {
      return (IBlockState)var1.func_206870_a(field_190957_a, var2.func_185831_a((EnumFacing)var1.func_177229_b(field_190957_a)));
   }

   public IBlockState func_185471_a(IBlockState var1, Mirror var2) {
      return var1.func_185907_a(var2.func_185800_a((EnumFacing)var1.func_177229_b(field_190957_a)));
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      EnumFacing var5 = (EnumFacing)var2.func_177229_b(field_190957_a);
      TileEntityShulkerBox.AnimationStatus var6 = ((TileEntityShulkerBox)var1.func_175625_s(var3)).func_190591_p();
      return var6 != TileEntityShulkerBox.AnimationStatus.CLOSED && (var6 != TileEntityShulkerBox.AnimationStatus.OPENED || var5 != var4.func_176734_d() && var5 != var4) ? BlockFaceShape.UNDEFINED : BlockFaceShape.SOLID;
   }

   static {
      field_190957_a = BlockDirectional.field_176387_N;
   }
}
