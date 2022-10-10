package net.minecraft.item;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import org.apache.commons.lang3.StringUtils;

public class ItemSkull extends ItemWallOrFloor {
   public ItemSkull(Block var1, Block var2, Item.Properties var3) {
      super(var1, var2, var3);
   }

   public ITextComponent func_200295_i(ItemStack var1) {
      if (var1.func_77973_b() == Items.field_196184_dx && var1.func_77942_o()) {
         String var2 = null;
         NBTTagCompound var3 = var1.func_77978_p();
         if (var3.func_150297_b("SkullOwner", 8)) {
            var2 = var3.func_74779_i("SkullOwner");
         } else if (var3.func_150297_b("SkullOwner", 10)) {
            NBTTagCompound var4 = var3.func_74775_l("SkullOwner");
            if (var4.func_150297_b("Name", 8)) {
               var2 = var4.func_74779_i("Name");
            }
         }

         if (var2 != null) {
            return new TextComponentTranslation(this.func_77658_a() + ".named", new Object[]{var2});
         }
      }

      return super.func_200295_i(var1);
   }

   public boolean func_179215_a(NBTTagCompound var1) {
      super.func_179215_a(var1);
      if (var1.func_150297_b("SkullOwner", 8) && !StringUtils.isBlank(var1.func_74779_i("SkullOwner"))) {
         GameProfile var2 = new GameProfile((UUID)null, var1.func_74779_i("SkullOwner"));
         var2 = TileEntitySkull.func_174884_b(var2);
         var1.func_74782_a("SkullOwner", NBTUtil.func_180708_a(new NBTTagCompound(), var2));
         return true;
      } else {
         return false;
      }
   }
}
