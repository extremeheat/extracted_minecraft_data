package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;

public class BlockAnvil extends BlockFalling {
   public static final PropertyDirection field_176506_a;
   public static final PropertyInteger field_176505_b;

   protected BlockAnvil() {
      super(Material.field_151574_g);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176506_a, EnumFacing.NORTH).func_177226_a(field_176505_b, 0));
      this.func_149713_g(0);
      this.func_149647_a(CreativeTabs.field_78031_c);
   }

   public boolean func_149686_d() {
      return false;
   }

   public boolean func_149662_c() {
      return false;
   }

   public IBlockState func_180642_a(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      EnumFacing var9 = var8.func_174811_aO().func_176746_e();
      return super.func_180642_a(var1, var2, var3, var4, var5, var6, var7, var8).func_177226_a(field_176506_a, var9).func_177226_a(field_176505_b, var7 >> 2);
   }

   public boolean func_180639_a(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      if (!var1.field_72995_K) {
         var4.func_180468_a(new BlockAnvil.Anvil(var1, var2));
      }

      return true;
   }

   public int func_180651_a(IBlockState var1) {
      return (Integer)var1.func_177229_b(field_176505_b);
   }

   public void func_180654_a(IBlockAccess var1, BlockPos var2) {
      EnumFacing var3 = (EnumFacing)var1.func_180495_p(var2).func_177229_b(field_176506_a);
      if (var3.func_176740_k() == EnumFacing.Axis.X) {
         this.func_149676_a(0.0F, 0.0F, 0.125F, 1.0F, 1.0F, 0.875F);
      } else {
         this.func_149676_a(0.125F, 0.0F, 0.0F, 0.875F, 1.0F, 1.0F);
      }

   }

   public void func_149666_a(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      var3.add(new ItemStack(var1, 1, 0));
      var3.add(new ItemStack(var1, 1, 1));
      var3.add(new ItemStack(var1, 1, 2));
   }

   protected void func_149829_a(EntityFallingBlock var1) {
      var1.func_145806_a(true);
   }

   public void func_176502_a_(World var1, BlockPos var2) {
      var1.func_175718_b(1022, var2, 0);
   }

   public boolean func_176225_a(IBlockAccess var1, BlockPos var2, EnumFacing var3) {
      return true;
   }

   public IBlockState func_176217_b(IBlockState var1) {
      return this.func_176223_P().func_177226_a(field_176506_a, EnumFacing.SOUTH);
   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176506_a, EnumFacing.func_176731_b(var1 & 3)).func_177226_a(field_176505_b, (var1 & 15) >> 2);
   }

   public int func_176201_c(IBlockState var1) {
      byte var2 = 0;
      int var3 = var2 | ((EnumFacing)var1.func_177229_b(field_176506_a)).func_176736_b();
      var3 |= (Integer)var1.func_177229_b(field_176505_b) << 2;
      return var3;
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176506_a, field_176505_b});
   }

   static {
      field_176506_a = PropertyDirection.func_177712_a("facing", EnumFacing.Plane.HORIZONTAL);
      field_176505_b = PropertyInteger.func_177719_a("damage", 0, 2);
   }

   public static class Anvil implements IInteractionObject {
      private final World field_175130_a;
      private final BlockPos field_175129_b;

      public Anvil(World var1, BlockPos var2) {
         super();
         this.field_175130_a = var1;
         this.field_175129_b = var2;
      }

      public String func_70005_c_() {
         return "anvil";
      }

      public boolean func_145818_k_() {
         return false;
      }

      public IChatComponent func_145748_c_() {
         return new ChatComponentTranslation(Blocks.field_150467_bQ.func_149739_a() + ".name", new Object[0]);
      }

      public Container func_174876_a(InventoryPlayer var1, EntityPlayer var2) {
         return new ContainerRepair(var1, this.field_175130_a, this.field_175129_b, var2);
      }

      public String func_174875_k() {
         return "minecraft:anvil";
      }
   }
}
