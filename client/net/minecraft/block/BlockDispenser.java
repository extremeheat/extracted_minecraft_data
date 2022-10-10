package net.minecraft.block;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.BlockSourceImpl;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.PositionImpl;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockDispenser extends BlockContainer {
   public static final DirectionProperty field_176441_a;
   public static final BooleanProperty field_176440_b;
   private static final Map<Item, IBehaviorDispenseItem> field_149943_a;

   public static void func_199774_a(IItemProvider var0, IBehaviorDispenseItem var1) {
      field_149943_a.put(var0.func_199767_j(), var1);
   }

   protected BlockDispenser(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_176441_a, EnumFacing.NORTH)).func_206870_a(field_176440_b, false));
   }

   public int func_149738_a(IWorldReaderBase var1) {
      return 4;
   }

   public boolean func_196250_a(IBlockState var1, World var2, BlockPos var3, EntityPlayer var4, EnumHand var5, EnumFacing var6, float var7, float var8, float var9) {
      if (var2.field_72995_K) {
         return true;
      } else {
         TileEntity var10 = var2.func_175625_s(var3);
         if (var10 instanceof TileEntityDispenser) {
            var4.func_71007_a((TileEntityDispenser)var10);
            if (var10 instanceof TileEntityDropper) {
               var4.func_195066_a(StatList.field_188083_Q);
            } else {
               var4.func_195066_a(StatList.field_188085_S);
            }
         }

         return true;
      }
   }

   protected void func_176439_d(World var1, BlockPos var2) {
      BlockSourceImpl var3 = new BlockSourceImpl(var1, var2);
      TileEntityDispenser var4 = (TileEntityDispenser)var3.func_150835_j();
      int var5 = var4.func_146017_i();
      if (var5 < 0) {
         var1.func_175718_b(1001, var2, 0);
      } else {
         ItemStack var6 = var4.func_70301_a(var5);
         IBehaviorDispenseItem var7 = this.func_149940_a(var6);
         if (var7 != IBehaviorDispenseItem.NOOP) {
            var4.func_70299_a(var5, var7.dispense(var3, var6));
         }

      }
   }

   protected IBehaviorDispenseItem func_149940_a(ItemStack var1) {
      return (IBehaviorDispenseItem)field_149943_a.get(var1.func_77973_b());
   }

   public void func_189540_a(IBlockState var1, World var2, BlockPos var3, Block var4, BlockPos var5) {
      boolean var6 = var2.func_175640_z(var3) || var2.func_175640_z(var3.func_177984_a());
      boolean var7 = (Boolean)var1.func_177229_b(field_176440_b);
      if (var6 && !var7) {
         var2.func_205220_G_().func_205360_a(var3, this, this.func_149738_a(var2));
         var2.func_180501_a(var3, (IBlockState)var1.func_206870_a(field_176440_b, true), 4);
      } else if (!var6 && var7) {
         var2.func_180501_a(var3, (IBlockState)var1.func_206870_a(field_176440_b, false), 4);
      }

   }

   public void func_196267_b(IBlockState var1, World var2, BlockPos var3, Random var4) {
      if (!var2.field_72995_K) {
         this.func_176439_d(var2, var3);
      }

   }

   public TileEntity func_196283_a_(IBlockReader var1) {
      return new TileEntityDispenser();
   }

   public IBlockState func_196258_a(BlockItemUseContext var1) {
      return (IBlockState)this.func_176223_P().func_206870_a(field_176441_a, var1.func_196010_d().func_176734_d());
   }

   public void func_180633_a(World var1, BlockPos var2, IBlockState var3, EntityLivingBase var4, ItemStack var5) {
      if (var5.func_82837_s()) {
         TileEntity var6 = var1.func_175625_s(var2);
         if (var6 instanceof TileEntityDispenser) {
            ((TileEntityDispenser)var6).func_200226_a(var5.func_200301_q());
         }
      }

   }

   public void func_196243_a(IBlockState var1, World var2, BlockPos var3, IBlockState var4, boolean var5) {
      if (var1.func_177230_c() != var4.func_177230_c()) {
         TileEntity var6 = var2.func_175625_s(var3);
         if (var6 instanceof TileEntityDispenser) {
            InventoryHelper.func_180175_a(var2, var3, (TileEntityDispenser)var6);
            var2.func_175666_e(var3, this);
         }

         super.func_196243_a(var1, var2, var3, var4, var5);
      }
   }

   public static IPosition func_149939_a(IBlockSource var0) {
      EnumFacing var1 = (EnumFacing)var0.func_189992_e().func_177229_b(field_176441_a);
      double var2 = var0.func_82615_a() + 0.7D * (double)var1.func_82601_c();
      double var4 = var0.func_82617_b() + 0.7D * (double)var1.func_96559_d();
      double var6 = var0.func_82616_c() + 0.7D * (double)var1.func_82599_e();
      return new PositionImpl(var2, var4, var6);
   }

   public boolean func_149740_M(IBlockState var1) {
      return true;
   }

   public int func_180641_l(IBlockState var1, World var2, BlockPos var3) {
      return Container.func_178144_a(var2.func_175625_s(var3));
   }

   public EnumBlockRenderType func_149645_b(IBlockState var1) {
      return EnumBlockRenderType.MODEL;
   }

   public IBlockState func_185499_a(IBlockState var1, Rotation var2) {
      return (IBlockState)var1.func_206870_a(field_176441_a, var2.func_185831_a((EnumFacing)var1.func_177229_b(field_176441_a)));
   }

   public IBlockState func_185471_a(IBlockState var1, Mirror var2) {
      return var1.func_185907_a(var2.func_185800_a((EnumFacing)var1.func_177229_b(field_176441_a)));
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_176441_a, field_176440_b);
   }

   static {
      field_176441_a = BlockDirectional.field_176387_N;
      field_176440_b = BlockStateProperties.field_208197_x;
      field_149943_a = (Map)Util.func_200696_a(new Object2ObjectOpenHashMap(), (var0) -> {
         var0.defaultReturnValue(new BehaviorDefaultDispenseItem());
      });
   }
}
