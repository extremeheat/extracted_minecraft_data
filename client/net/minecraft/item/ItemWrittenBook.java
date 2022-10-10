package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemWrittenBook extends Item {
   public ItemWrittenBook(Item.Properties var1) {
      super(var1);
   }

   public static boolean func_77828_a(@Nullable NBTTagCompound var0) {
      if (!ItemWritableBook.func_150930_a(var0)) {
         return false;
      } else if (!var0.func_150297_b("title", 8)) {
         return false;
      } else {
         String var1 = var0.func_74779_i("title");
         return var1.length() > 32 ? false : var0.func_150297_b("author", 8);
      }
   }

   public static int func_179230_h(ItemStack var0) {
      return var0.func_77978_p().func_74762_e("generation");
   }

   public ITextComponent func_200295_i(ItemStack var1) {
      if (var1.func_77942_o()) {
         NBTTagCompound var2 = var1.func_77978_p();
         String var3 = var2.func_74779_i("title");
         if (!StringUtils.func_151246_b(var3)) {
            return new TextComponentString(var3);
         }
      }

      return super.func_200295_i(var1);
   }

   public void func_77624_a(ItemStack var1, @Nullable World var2, List<ITextComponent> var3, ITooltipFlag var4) {
      if (var1.func_77942_o()) {
         NBTTagCompound var5 = var1.func_77978_p();
         String var6 = var5.func_74779_i("author");
         if (!StringUtils.func_151246_b(var6)) {
            var3.add((new TextComponentTranslation("book.byAuthor", new Object[]{var6})).func_211708_a(TextFormatting.GRAY));
         }

         var3.add((new TextComponentTranslation("book.generation." + var5.func_74762_e("generation"), new Object[0])).func_211708_a(TextFormatting.GRAY));
      }

   }

   public ActionResult<ItemStack> func_77659_a(World var1, EntityPlayer var2, EnumHand var3) {
      ItemStack var4 = var2.func_184586_b(var3);
      if (!var1.field_72995_K) {
         this.func_179229_a(var4, var2);
      }

      var2.func_184814_a(var4, var3);
      var2.func_71029_a(StatList.field_75929_E.func_199076_b(this));
      return new ActionResult(EnumActionResult.SUCCESS, var4);
   }

   private void func_179229_a(ItemStack var1, EntityPlayer var2) {
      NBTTagCompound var3 = var1.func_77978_p();
      if (var3 != null && !var3.func_74767_n("resolved")) {
         var3.func_74757_a("resolved", true);
         if (func_77828_a(var3)) {
            NBTTagList var4 = var3.func_150295_c("pages", 8);

            for(int var5 = 0; var5 < var4.size(); ++var5) {
               String var6 = var4.func_150307_f(var5);

               Object var7;
               try {
                  ITextComponent var11 = ITextComponent.Serializer.func_186877_b(var6);
                  var7 = TextComponentUtils.func_197680_a(var2.func_195051_bN(), var11, var2);
               } catch (Exception var9) {
                  var7 = new TextComponentString(var6);
               }

               var4.set(var5, (INBTBase)(new NBTTagString(ITextComponent.Serializer.func_150696_a((ITextComponent)var7))));
            }

            var3.func_74782_a("pages", var4);
            if (var2 instanceof EntityPlayerMP && var2.func_184614_ca() == var1) {
               Slot var10 = var2.field_71070_bA.func_75147_a(var2.field_71071_by, var2.field_71071_by.field_70461_c);
               ((EntityPlayerMP)var2).field_71135_a.func_147359_a(new SPacketSetSlot(0, var10.field_75222_d, var1));
            }

         }
      }
   }

   public boolean func_77636_d(ItemStack var1) {
      return true;
   }
}
