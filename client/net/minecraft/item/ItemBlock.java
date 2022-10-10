package net.minecraft.item;

import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class ItemBlock extends Item {
   @Deprecated
   private final Block field_150939_a;

   public ItemBlock(Block var1, Item.Properties var2) {
      super(var2);
      this.field_150939_a = var1;
   }

   public EnumActionResult func_195939_a(ItemUseContext var1) {
      return this.func_195942_a(new BlockItemUseContext(var1));
   }

   public EnumActionResult func_195942_a(BlockItemUseContext var1) {
      if (!var1.func_196011_b()) {
         return EnumActionResult.FAIL;
      } else {
         IBlockState var2 = this.func_195945_b(var1);
         if (var2 == null) {
            return EnumActionResult.FAIL;
         } else if (!this.func_195941_b(var1, var2)) {
            return EnumActionResult.FAIL;
         } else {
            BlockPos var3 = var1.func_195995_a();
            World var4 = var1.func_195991_k();
            EntityPlayer var5 = var1.func_195999_j();
            ItemStack var6 = var1.func_195996_i();
            IBlockState var7 = var4.func_180495_p(var3);
            Block var8 = var7.func_177230_c();
            if (var8 == var2.func_177230_c()) {
               this.func_195943_a(var3, var4, var5, var6, var7);
               var8.func_180633_a(var4, var3, var7, var5, var6);
               if (var5 instanceof EntityPlayerMP) {
                  CriteriaTriggers.field_193137_x.func_193173_a((EntityPlayerMP)var5, var3, var6);
               }
            }

            SoundType var9 = var8.func_185467_w();
            var4.func_184133_a(var5, var3, var9.func_185841_e(), SoundCategory.BLOCKS, (var9.func_185843_a() + 1.0F) / 2.0F, var9.func_185847_b() * 0.8F);
            var6.func_190918_g(1);
            return EnumActionResult.SUCCESS;
         }
      }
   }

   protected boolean func_195943_a(BlockPos var1, World var2, @Nullable EntityPlayer var3, ItemStack var4, IBlockState var5) {
      return func_179224_a(var2, var3, var1, var4);
   }

   @Nullable
   protected IBlockState func_195945_b(BlockItemUseContext var1) {
      IBlockState var2 = this.func_179223_d().func_196258_a(var1);
      return var2 != null && this.func_195944_a(var1, var2) ? var2 : null;
   }

   protected boolean func_195944_a(BlockItemUseContext var1, IBlockState var2) {
      return var2.func_196955_c(var1.func_195991_k(), var1.func_195995_a()) && var1.func_195991_k().func_195584_a(var2, var1.func_195995_a());
   }

   protected boolean func_195941_b(BlockItemUseContext var1, IBlockState var2) {
      return var1.func_195991_k().func_180501_a(var1.func_195995_a(), var2, 11);
   }

   public static boolean func_179224_a(World var0, @Nullable EntityPlayer var1, BlockPos var2, ItemStack var3) {
      MinecraftServer var4 = var0.func_73046_m();
      if (var4 == null) {
         return false;
      } else {
         NBTTagCompound var5 = var3.func_179543_a("BlockEntityTag");
         if (var5 != null) {
            TileEntity var6 = var0.func_175625_s(var2);
            if (var6 != null) {
               if (!var0.field_72995_K && var6.func_183000_F() && (var1 == null || !var1.func_195070_dx())) {
                  return false;
               }

               NBTTagCompound var7 = var6.func_189515_b(new NBTTagCompound());
               NBTTagCompound var8 = var7.func_74737_b();
               var7.func_197643_a(var5);
               var7.func_74768_a("x", var2.func_177958_n());
               var7.func_74768_a("y", var2.func_177956_o());
               var7.func_74768_a("z", var2.func_177952_p());
               if (!var7.equals(var8)) {
                  var6.func_145839_a(var7);
                  var6.func_70296_d();
                  return true;
               }
            }
         }

         return false;
      }
   }

   public String func_77658_a() {
      return this.func_179223_d().func_149739_a();
   }

   public void func_150895_a(ItemGroup var1, NonNullList<ItemStack> var2) {
      if (this.func_194125_a(var1)) {
         this.func_179223_d().func_149666_a(var1, var2);
      }

   }

   public void func_77624_a(ItemStack var1, @Nullable World var2, List<ITextComponent> var3, ITooltipFlag var4) {
      super.func_77624_a(var1, var2, var3, var4);
      this.func_179223_d().func_190948_a(var1, var2, var3, var4);
   }

   public Block func_179223_d() {
      return this.field_150939_a;
   }

   public void func_195946_a(Map<Block, Item> var1, Item var2) {
      var1.put(this.func_179223_d(), var2);
   }
}
