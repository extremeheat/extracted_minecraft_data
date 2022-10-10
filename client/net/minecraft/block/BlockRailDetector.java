package net.minecraft.block;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartCommandBlock;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.RailShape;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockRailDetector extends BlockRailBase {
   public static final EnumProperty<RailShape> field_176573_b;
   public static final BooleanProperty field_176574_M;

   public BlockRailDetector(Block.Properties var1) {
      super(true, var1);
      this.func_180632_j((IBlockState)((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_176574_M, false)).func_206870_a(field_176573_b, RailShape.NORTH_SOUTH));
   }

   public int func_149738_a(IWorldReaderBase var1) {
      return 20;
   }

   public boolean func_149744_f(IBlockState var1) {
      return true;
   }

   public void func_196262_a(IBlockState var1, World var2, BlockPos var3, Entity var4) {
      if (!var2.field_72995_K) {
         if (!(Boolean)var1.func_177229_b(field_176574_M)) {
            this.func_176570_e(var2, var3, var1);
         }
      }
   }

   public void func_196267_b(IBlockState var1, World var2, BlockPos var3, Random var4) {
      if (!var2.field_72995_K && (Boolean)var1.func_177229_b(field_176574_M)) {
         this.func_176570_e(var2, var3, var1);
      }
   }

   public int func_180656_a(IBlockState var1, IBlockReader var2, BlockPos var3, EnumFacing var4) {
      return (Boolean)var1.func_177229_b(field_176574_M) ? 15 : 0;
   }

   public int func_176211_b(IBlockState var1, IBlockReader var2, BlockPos var3, EnumFacing var4) {
      if (!(Boolean)var1.func_177229_b(field_176574_M)) {
         return 0;
      } else {
         return var4 == EnumFacing.UP ? 15 : 0;
      }
   }

   private void func_176570_e(World var1, BlockPos var2, IBlockState var3) {
      boolean var4 = (Boolean)var3.func_177229_b(field_176574_M);
      boolean var5 = false;
      List var6 = this.func_200878_a(var1, var2, EntityMinecart.class, (Predicate)null);
      if (!var6.isEmpty()) {
         var5 = true;
      }

      if (var5 && !var4) {
         var1.func_180501_a(var2, (IBlockState)var3.func_206870_a(field_176574_M, true), 3);
         this.func_185592_b(var1, var2, var3, true);
         var1.func_195593_d(var2, this);
         var1.func_195593_d(var2.func_177977_b(), this);
         var1.func_175704_b(var2, var2);
      }

      if (!var5 && var4) {
         var1.func_180501_a(var2, (IBlockState)var3.func_206870_a(field_176574_M, false), 3);
         this.func_185592_b(var1, var2, var3, false);
         var1.func_195593_d(var2, this);
         var1.func_195593_d(var2.func_177977_b(), this);
         var1.func_175704_b(var2, var2);
      }

      if (var5) {
         var1.func_205220_G_().func_205360_a(var2, this, this.func_149738_a(var1));
      }

      var1.func_175666_e(var2, this);
   }

   protected void func_185592_b(World var1, BlockPos var2, IBlockState var3, boolean var4) {
      BlockRailState var5 = new BlockRailState(var1, var2, var3);
      List var6 = var5.func_196907_a();
      Iterator var7 = var6.iterator();

      while(var7.hasNext()) {
         BlockPos var8 = (BlockPos)var7.next();
         IBlockState var9 = var1.func_180495_p(var8);
         var9.func_189546_a(var1, var8, var9.func_177230_c(), var2);
      }

   }

   public void func_196259_b(IBlockState var1, World var2, BlockPos var3, IBlockState var4) {
      if (var4.func_177230_c() != var1.func_177230_c()) {
         super.func_196259_b(var1, var2, var3, var4);
         this.func_176570_e(var2, var3, var1);
      }
   }

   public IProperty<RailShape> func_176560_l() {
      return field_176573_b;
   }

   public boolean func_149740_M(IBlockState var1) {
      return true;
   }

   public int func_180641_l(IBlockState var1, World var2, BlockPos var3) {
      if ((Boolean)var1.func_177229_b(field_176574_M)) {
         List var4 = this.func_200878_a(var2, var3, EntityMinecartCommandBlock.class, (Predicate)null);
         if (!var4.isEmpty()) {
            return ((EntityMinecartCommandBlock)var4.get(0)).func_145822_e().func_145760_g();
         }

         List var5 = this.func_200878_a(var2, var3, EntityMinecart.class, EntitySelectors.field_96566_b);
         if (!var5.isEmpty()) {
            return Container.func_94526_b((IInventory)var5.get(0));
         }
      }

      return 0;
   }

   protected <T extends EntityMinecart> List<T> func_200878_a(World var1, BlockPos var2, Class<T> var3, @Nullable Predicate<Entity> var4) {
      return var1.func_175647_a(var3, this.func_176572_a(var2), var4);
   }

   private AxisAlignedBB func_176572_a(BlockPos var1) {
      float var2 = 0.2F;
      return new AxisAlignedBB((double)((float)var1.func_177958_n() + 0.2F), (double)var1.func_177956_o(), (double)((float)var1.func_177952_p() + 0.2F), (double)((float)(var1.func_177958_n() + 1) - 0.2F), (double)((float)(var1.func_177956_o() + 1) - 0.2F), (double)((float)(var1.func_177952_p() + 1) - 0.2F));
   }

   public IBlockState func_185499_a(IBlockState var1, Rotation var2) {
      switch(var2) {
      case CLOCKWISE_180:
         switch((RailShape)var1.func_177229_b(field_176573_b)) {
         case ASCENDING_EAST:
            return (IBlockState)var1.func_206870_a(field_176573_b, RailShape.ASCENDING_WEST);
         case ASCENDING_WEST:
            return (IBlockState)var1.func_206870_a(field_176573_b, RailShape.ASCENDING_EAST);
         case ASCENDING_NORTH:
            return (IBlockState)var1.func_206870_a(field_176573_b, RailShape.ASCENDING_SOUTH);
         case ASCENDING_SOUTH:
            return (IBlockState)var1.func_206870_a(field_176573_b, RailShape.ASCENDING_NORTH);
         case SOUTH_EAST:
            return (IBlockState)var1.func_206870_a(field_176573_b, RailShape.NORTH_WEST);
         case SOUTH_WEST:
            return (IBlockState)var1.func_206870_a(field_176573_b, RailShape.NORTH_EAST);
         case NORTH_WEST:
            return (IBlockState)var1.func_206870_a(field_176573_b, RailShape.SOUTH_EAST);
         case NORTH_EAST:
            return (IBlockState)var1.func_206870_a(field_176573_b, RailShape.SOUTH_WEST);
         }
      case COUNTERCLOCKWISE_90:
         switch((RailShape)var1.func_177229_b(field_176573_b)) {
         case ASCENDING_EAST:
            return (IBlockState)var1.func_206870_a(field_176573_b, RailShape.ASCENDING_NORTH);
         case ASCENDING_WEST:
            return (IBlockState)var1.func_206870_a(field_176573_b, RailShape.ASCENDING_SOUTH);
         case ASCENDING_NORTH:
            return (IBlockState)var1.func_206870_a(field_176573_b, RailShape.ASCENDING_WEST);
         case ASCENDING_SOUTH:
            return (IBlockState)var1.func_206870_a(field_176573_b, RailShape.ASCENDING_EAST);
         case SOUTH_EAST:
            return (IBlockState)var1.func_206870_a(field_176573_b, RailShape.NORTH_EAST);
         case SOUTH_WEST:
            return (IBlockState)var1.func_206870_a(field_176573_b, RailShape.SOUTH_EAST);
         case NORTH_WEST:
            return (IBlockState)var1.func_206870_a(field_176573_b, RailShape.SOUTH_WEST);
         case NORTH_EAST:
            return (IBlockState)var1.func_206870_a(field_176573_b, RailShape.NORTH_WEST);
         case NORTH_SOUTH:
            return (IBlockState)var1.func_206870_a(field_176573_b, RailShape.EAST_WEST);
         case EAST_WEST:
            return (IBlockState)var1.func_206870_a(field_176573_b, RailShape.NORTH_SOUTH);
         }
      case CLOCKWISE_90:
         switch((RailShape)var1.func_177229_b(field_176573_b)) {
         case ASCENDING_EAST:
            return (IBlockState)var1.func_206870_a(field_176573_b, RailShape.ASCENDING_SOUTH);
         case ASCENDING_WEST:
            return (IBlockState)var1.func_206870_a(field_176573_b, RailShape.ASCENDING_NORTH);
         case ASCENDING_NORTH:
            return (IBlockState)var1.func_206870_a(field_176573_b, RailShape.ASCENDING_EAST);
         case ASCENDING_SOUTH:
            return (IBlockState)var1.func_206870_a(field_176573_b, RailShape.ASCENDING_WEST);
         case SOUTH_EAST:
            return (IBlockState)var1.func_206870_a(field_176573_b, RailShape.SOUTH_WEST);
         case SOUTH_WEST:
            return (IBlockState)var1.func_206870_a(field_176573_b, RailShape.NORTH_WEST);
         case NORTH_WEST:
            return (IBlockState)var1.func_206870_a(field_176573_b, RailShape.NORTH_EAST);
         case NORTH_EAST:
            return (IBlockState)var1.func_206870_a(field_176573_b, RailShape.SOUTH_EAST);
         case NORTH_SOUTH:
            return (IBlockState)var1.func_206870_a(field_176573_b, RailShape.EAST_WEST);
         case EAST_WEST:
            return (IBlockState)var1.func_206870_a(field_176573_b, RailShape.NORTH_SOUTH);
         }
      default:
         return var1;
      }
   }

   public IBlockState func_185471_a(IBlockState var1, Mirror var2) {
      RailShape var3 = (RailShape)var1.func_177229_b(field_176573_b);
      switch(var2) {
      case LEFT_RIGHT:
         switch(var3) {
         case ASCENDING_NORTH:
            return (IBlockState)var1.func_206870_a(field_176573_b, RailShape.ASCENDING_SOUTH);
         case ASCENDING_SOUTH:
            return (IBlockState)var1.func_206870_a(field_176573_b, RailShape.ASCENDING_NORTH);
         case SOUTH_EAST:
            return (IBlockState)var1.func_206870_a(field_176573_b, RailShape.NORTH_EAST);
         case SOUTH_WEST:
            return (IBlockState)var1.func_206870_a(field_176573_b, RailShape.NORTH_WEST);
         case NORTH_WEST:
            return (IBlockState)var1.func_206870_a(field_176573_b, RailShape.SOUTH_WEST);
         case NORTH_EAST:
            return (IBlockState)var1.func_206870_a(field_176573_b, RailShape.SOUTH_EAST);
         default:
            return super.func_185471_a(var1, var2);
         }
      case FRONT_BACK:
         switch(var3) {
         case ASCENDING_EAST:
            return (IBlockState)var1.func_206870_a(field_176573_b, RailShape.ASCENDING_WEST);
         case ASCENDING_WEST:
            return (IBlockState)var1.func_206870_a(field_176573_b, RailShape.ASCENDING_EAST);
         case ASCENDING_NORTH:
         case ASCENDING_SOUTH:
         default:
            break;
         case SOUTH_EAST:
            return (IBlockState)var1.func_206870_a(field_176573_b, RailShape.SOUTH_WEST);
         case SOUTH_WEST:
            return (IBlockState)var1.func_206870_a(field_176573_b, RailShape.SOUTH_EAST);
         case NORTH_WEST:
            return (IBlockState)var1.func_206870_a(field_176573_b, RailShape.NORTH_EAST);
         case NORTH_EAST:
            return (IBlockState)var1.func_206870_a(field_176573_b, RailShape.NORTH_WEST);
         }
      }

      return super.func_185471_a(var1, var2);
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_176573_b, field_176574_M);
   }

   static {
      field_176573_b = BlockStateProperties.field_208166_S;
      field_176574_M = BlockStateProperties.field_208194_u;
   }
}
