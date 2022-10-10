package net.minecraft.item;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemFireworkRocket extends Item {
   public ItemFireworkRocket(Item.Properties var1) {
      super(var1);
   }

   public EnumActionResult func_195939_a(ItemUseContext var1) {
      World var2 = var1.func_195991_k();
      if (!var2.field_72995_K) {
         BlockPos var3 = var1.func_195995_a();
         ItemStack var4 = var1.func_195996_i();
         EntityFireworkRocket var5 = new EntityFireworkRocket(var2, (double)((float)var3.func_177958_n() + var1.func_195997_m()), (double)((float)var3.func_177956_o() + var1.func_195993_n()), (double)((float)var3.func_177952_p() + var1.func_195994_o()), var4);
         var2.func_72838_d(var5);
         var4.func_190918_g(1);
      }

      return EnumActionResult.SUCCESS;
   }

   public ActionResult<ItemStack> func_77659_a(World var1, EntityPlayer var2, EnumHand var3) {
      if (var2.func_184613_cA()) {
         ItemStack var4 = var2.func_184586_b(var3);
         if (!var1.field_72995_K) {
            EntityFireworkRocket var5 = new EntityFireworkRocket(var1, var4, var2);
            var1.func_72838_d(var5);
            if (!var2.field_71075_bZ.field_75098_d) {
               var4.func_190918_g(1);
            }
         }

         return new ActionResult(EnumActionResult.SUCCESS, var2.func_184586_b(var3));
      } else {
         return new ActionResult(EnumActionResult.PASS, var2.func_184586_b(var3));
      }
   }

   public void func_77624_a(ItemStack var1, @Nullable World var2, List<ITextComponent> var3, ITooltipFlag var4) {
      NBTTagCompound var5 = var1.func_179543_a("Fireworks");
      if (var5 != null) {
         if (var5.func_150297_b("Flight", 99)) {
            var3.add((new TextComponentTranslation("item.minecraft.firework_rocket.flight", new Object[0])).func_150258_a(" ").func_150258_a(String.valueOf(var5.func_74771_c("Flight"))).func_211708_a(TextFormatting.GRAY));
         }

         NBTTagList var6 = var5.func_150295_c("Explosions", 10);
         if (!var6.isEmpty()) {
            for(int var7 = 0; var7 < var6.size(); ++var7) {
               NBTTagCompound var8 = var6.func_150305_b(var7);
               ArrayList var9 = Lists.newArrayList();
               ItemFireworkStar.func_195967_a(var8, var9);
               if (!var9.isEmpty()) {
                  for(int var10 = 1; var10 < var9.size(); ++var10) {
                     var9.set(var10, (new TextComponentString("  ")).func_150257_a((ITextComponent)var9.get(var10)).func_211708_a(TextFormatting.GRAY));
                  }

                  var3.addAll(var9);
               }
            }
         }

      }
   }

   public static enum Shape {
      SMALL_BALL(0, "small_ball"),
      LARGE_BALL(1, "large_ball"),
      STAR(2, "star"),
      CREEPER(3, "creeper"),
      BURST(4, "burst");

      private static final ItemFireworkRocket.Shape[] field_196077_f = (ItemFireworkRocket.Shape[])Arrays.stream(values()).sorted(Comparator.comparingInt((var0) -> {
         return var0.field_196078_g;
      })).toArray((var0) -> {
         return new ItemFireworkRocket.Shape[var0];
      });
      private final int field_196078_g;
      private final String field_196079_h;

      private Shape(int var3, String var4) {
         this.field_196078_g = var3;
         this.field_196079_h = var4;
      }

      public int func_196071_a() {
         return this.field_196078_g;
      }

      public String func_196068_b() {
         return this.field_196079_h;
      }

      public static ItemFireworkRocket.Shape func_196070_a(int var0) {
         return var0 >= 0 && var0 < field_196077_f.length ? field_196077_f[var0] : SMALL_BALL;
      }
   }
}
