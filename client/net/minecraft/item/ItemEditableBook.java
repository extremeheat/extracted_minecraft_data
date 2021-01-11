package net.minecraft.item;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.stats.StatList;
import net.minecraft.util.ChatComponentProcessor;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;

public class ItemEditableBook extends Item {
   public ItemEditableBook() {
      super();
      this.func_77625_d(1);
   }

   public static boolean func_77828_a(NBTTagCompound var0) {
      if (!ItemWritableBook.func_150930_a(var0)) {
         return false;
      } else if (!var0.func_150297_b("title", 8)) {
         return false;
      } else {
         String var1 = var0.func_74779_i("title");
         if (var1 != null && var1.length() <= 32) {
            return var0.func_150297_b("author", 8);
         } else {
            return false;
         }
      }
   }

   public static int func_179230_h(ItemStack var0) {
      return var0.func_77978_p().func_74762_e("generation");
   }

   public String func_77653_i(ItemStack var1) {
      if (var1.func_77942_o()) {
         NBTTagCompound var2 = var1.func_77978_p();
         String var3 = var2.func_74779_i("title");
         if (!StringUtils.func_151246_b(var3)) {
            return var3;
         }
      }

      return super.func_77653_i(var1);
   }

   public void func_77624_a(ItemStack var1, EntityPlayer var2, List<String> var3, boolean var4) {
      if (var1.func_77942_o()) {
         NBTTagCompound var5 = var1.func_77978_p();
         String var6 = var5.func_74779_i("author");
         if (!StringUtils.func_151246_b(var6)) {
            var3.add(EnumChatFormatting.GRAY + StatCollector.func_74837_a("book.byAuthor", var6));
         }

         var3.add(EnumChatFormatting.GRAY + StatCollector.func_74838_a("book.generation." + var5.func_74762_e("generation")));
      }

   }

   public ItemStack func_77659_a(ItemStack var1, World var2, EntityPlayer var3) {
      if (!var2.field_72995_K) {
         this.func_179229_a(var1, var3);
      }

      var3.func_71048_c(var1);
      var3.func_71029_a(StatList.field_75929_E[Item.func_150891_b(this)]);
      return var1;
   }

   private void func_179229_a(ItemStack var1, EntityPlayer var2) {
      if (var1 != null && var1.func_77978_p() != null) {
         NBTTagCompound var3 = var1.func_77978_p();
         if (!var3.func_74767_n("resolved")) {
            var3.func_74757_a("resolved", true);
            if (func_77828_a(var3)) {
               NBTTagList var4 = var3.func_150295_c("pages", 8);

               for(int var5 = 0; var5 < var4.func_74745_c(); ++var5) {
                  String var6 = var4.func_150307_f(var5);

                  Object var7;
                  try {
                     IChatComponent var11 = IChatComponent.Serializer.func_150699_a(var6);
                     var7 = ChatComponentProcessor.func_179985_a(var2, var11, var2);
                  } catch (Exception var9) {
                     var7 = new ChatComponentText(var6);
                  }

                  var4.func_150304_a(var5, new NBTTagString(IChatComponent.Serializer.func_150696_a((IChatComponent)var7)));
               }

               var3.func_74782_a("pages", var4);
               if (var2 instanceof EntityPlayerMP && var2.func_71045_bC() == var1) {
                  Slot var10 = var2.field_71070_bA.func_75147_a(var2.field_71071_by, var2.field_71071_by.field_70461_c);
                  ((EntityPlayerMP)var2).field_71135_a.func_147359_a(new S2FPacketSetSlot(0, var10.field_75222_d, var1));
               }

            }
         }
      }
   }

   public boolean func_77636_d(ItemStack var1) {
      return true;
   }
}
