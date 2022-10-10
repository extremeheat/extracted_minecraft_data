package net.minecraft.item;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ItemKnowledgeBook extends Item {
   private static final Logger field_194126_a = LogManager.getLogger();

   public ItemKnowledgeBook(Item.Properties var1) {
      super(var1);
   }

   public ActionResult<ItemStack> func_77659_a(World var1, EntityPlayer var2, EnumHand var3) {
      ItemStack var4 = var2.func_184586_b(var3);
      NBTTagCompound var5 = var4.func_77978_p();
      if (!var2.field_71075_bZ.field_75098_d) {
         var2.func_184611_a(var3, ItemStack.field_190927_a);
      }

      if (var5 != null && var5.func_150297_b("Recipes", 9)) {
         if (!var1.field_72995_K) {
            NBTTagList var6 = var5.func_150295_c("Recipes", 8);
            ArrayList var7 = Lists.newArrayList();

            for(int var8 = 0; var8 < var6.size(); ++var8) {
               String var9 = var6.func_150307_f(var8);
               IRecipe var10 = var1.func_73046_m().func_199529_aN().func_199517_a(new ResourceLocation(var9));
               if (var10 == null) {
                  field_194126_a.error("Invalid recipe: {}", var9);
                  return new ActionResult(EnumActionResult.FAIL, var4);
               }

               var7.add(var10);
            }

            var2.func_195065_a(var7);
            var2.func_71029_a(StatList.field_75929_E.func_199076_b(this));
         }

         return new ActionResult(EnumActionResult.SUCCESS, var4);
      } else {
         field_194126_a.error("Tag not valid: {}", var5);
         return new ActionResult(EnumActionResult.FAIL, var4);
      }
   }
}
