package net.minecraft.item;

import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class ItemDebugStick extends Item {
   public ItemDebugStick(Item.Properties var1) {
      super(var1);
   }

   public boolean func_77636_d(ItemStack var1) {
      return true;
   }

   public boolean func_195938_a(IBlockState var1, World var2, BlockPos var3, EntityPlayer var4) {
      if (!var2.field_72995_K) {
         this.func_195958_a(var4, var1, var2, var3, false, var4.func_184586_b(EnumHand.MAIN_HAND));
      }

      return false;
   }

   public EnumActionResult func_195939_a(ItemUseContext var1) {
      EntityPlayer var2 = var1.func_195999_j();
      World var3 = var1.func_195991_k();
      if (!var3.field_72995_K && var2 != null) {
         BlockPos var4 = var1.func_195995_a();
         this.func_195958_a(var2, var3.func_180495_p(var4), var3, var4, true, var1.func_195996_i());
      }

      return EnumActionResult.SUCCESS;
   }

   private void func_195958_a(EntityPlayer var1, IBlockState var2, IWorld var3, BlockPos var4, boolean var5, ItemStack var6) {
      if (var1.func_195070_dx()) {
         Block var7 = var2.func_177230_c();
         StateContainer var8 = var7.func_176194_O();
         Collection var9 = var8.func_177623_d();
         String var10 = IRegistry.field_212618_g.func_177774_c(var7).toString();
         if (var9.isEmpty()) {
            func_195956_a(var1, new TextComponentTranslation(this.func_77658_a() + ".empty", new Object[]{var10}));
         } else {
            NBTTagCompound var11 = var6.func_190925_c("DebugProperty");
            String var12 = var11.func_74779_i(var10);
            IProperty var13 = var8.func_185920_a(var12);
            if (var5) {
               if (var13 == null) {
                  var13 = (IProperty)var9.iterator().next();
               }

               IBlockState var14 = func_195960_a(var2, var13, var1.func_70093_af());
               var3.func_180501_a(var4, var14, 18);
               func_195956_a(var1, new TextComponentTranslation(this.func_77658_a() + ".update", new Object[]{var13.func_177701_a(), func_195957_a(var14, var13)}));
            } else {
               var13 = (IProperty)func_195959_a(var9, var13, var1.func_70093_af());
               String var15 = var13.func_177701_a();
               var11.func_74778_a(var10, var15);
               func_195956_a(var1, new TextComponentTranslation(this.func_77658_a() + ".select", new Object[]{var15, func_195957_a(var2, var13)}));
            }

         }
      }
   }

   private static <T extends Comparable<T>> IBlockState func_195960_a(IBlockState var0, IProperty<T> var1, boolean var2) {
      return (IBlockState)var0.func_206870_a(var1, (Comparable)func_195959_a(var1.func_177700_c(), var0.func_177229_b(var1), var2));
   }

   private static <T> T func_195959_a(Iterable<T> var0, @Nullable T var1, boolean var2) {
      return var2 ? Util.func_195648_b(var0, var1) : Util.func_195647_a(var0, var1);
   }

   private static void func_195956_a(EntityPlayer var0, ITextComponent var1) {
      ((EntityPlayerMP)var0).func_195395_a(var1, ChatType.GAME_INFO);
   }

   private static <T extends Comparable<T>> String func_195957_a(IBlockState var0, IProperty<T> var1) {
      return var1.func_177702_a(var0.func_177229_b(var1));
   }
}
