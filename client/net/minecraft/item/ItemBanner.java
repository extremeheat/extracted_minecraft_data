package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAbstractBanner;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.apache.commons.lang3.Validate;

public class ItemBanner extends ItemWallOrFloor {
   public ItemBanner(Block var1, Block var2, Item.Properties var3) {
      super(var1, var2, var3);
      Validate.isInstanceOf(BlockAbstractBanner.class, var1);
      Validate.isInstanceOf(BlockAbstractBanner.class, var2);
   }

   public static void func_185054_a(ItemStack var0, List<ITextComponent> var1) {
      NBTTagCompound var2 = var0.func_179543_a("BlockEntityTag");
      if (var2 != null && var2.func_74764_b("Patterns")) {
         NBTTagList var3 = var2.func_150295_c("Patterns", 10);

         for(int var4 = 0; var4 < var3.size() && var4 < 6; ++var4) {
            NBTTagCompound var5 = var3.func_150305_b(var4);
            EnumDyeColor var6 = EnumDyeColor.func_196056_a(var5.func_74762_e("Color"));
            BannerPattern var7 = BannerPattern.func_190994_a(var5.func_74779_i("Pattern"));
            if (var7 != null) {
               var1.add((new TextComponentTranslation("block.minecraft.banner." + var7.func_190997_a() + '.' + var6.func_176762_d(), new Object[0])).func_211708_a(TextFormatting.GRAY));
            }
         }

      }
   }

   public EnumDyeColor func_195948_b() {
      return ((BlockAbstractBanner)this.func_179223_d()).func_196285_M_();
   }

   public void func_77624_a(ItemStack var1, @Nullable World var2, List<ITextComponent> var3, ITooltipFlag var4) {
      func_185054_a(var1, var3);
   }
}
