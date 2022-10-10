package net.minecraft.potion;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class PotionUtils {
   public static List<PotionEffect> func_185189_a(ItemStack var0) {
      return func_185185_a(var0.func_77978_p());
   }

   public static List<PotionEffect> func_185186_a(PotionType var0, Collection<PotionEffect> var1) {
      ArrayList var2 = Lists.newArrayList();
      var2.addAll(var0.func_185170_a());
      var2.addAll(var1);
      return var2;
   }

   public static List<PotionEffect> func_185185_a(@Nullable NBTTagCompound var0) {
      ArrayList var1 = Lists.newArrayList();
      var1.addAll(func_185187_c(var0).func_185170_a());
      func_185193_a(var0, var1);
      return var1;
   }

   public static List<PotionEffect> func_185190_b(ItemStack var0) {
      return func_185192_b(var0.func_77978_p());
   }

   public static List<PotionEffect> func_185192_b(@Nullable NBTTagCompound var0) {
      ArrayList var1 = Lists.newArrayList();
      func_185193_a(var0, var1);
      return var1;
   }

   public static void func_185193_a(@Nullable NBTTagCompound var0, List<PotionEffect> var1) {
      if (var0 != null && var0.func_150297_b("CustomPotionEffects", 9)) {
         NBTTagList var2 = var0.func_150295_c("CustomPotionEffects", 10);

         for(int var3 = 0; var3 < var2.size(); ++var3) {
            NBTTagCompound var4 = var2.func_150305_b(var3);
            PotionEffect var5 = PotionEffect.func_82722_b(var4);
            if (var5 != null) {
               var1.add(var5);
            }
         }
      }

   }

   public static int func_190932_c(ItemStack var0) {
      NBTTagCompound var1 = var0.func_77978_p();
      if (var1 != null && var1.func_150297_b("CustomPotionColor", 99)) {
         return var1.func_74762_e("CustomPotionColor");
      } else {
         return func_185191_c(var0) == PotionTypes.field_185229_a ? 16253176 : func_185181_a(func_185189_a(var0));
      }
   }

   public static int func_185183_a(PotionType var0) {
      return var0 == PotionTypes.field_185229_a ? 16253176 : func_185181_a(var0.func_185170_a());
   }

   public static int func_185181_a(Collection<PotionEffect> var0) {
      int var1 = 3694022;
      if (var0.isEmpty()) {
         return 3694022;
      } else {
         float var2 = 0.0F;
         float var3 = 0.0F;
         float var4 = 0.0F;
         int var5 = 0;
         Iterator var6 = var0.iterator();

         while(var6.hasNext()) {
            PotionEffect var7 = (PotionEffect)var6.next();
            if (var7.func_188418_e()) {
               int var8 = var7.func_188419_a().func_76401_j();
               int var9 = var7.func_76458_c() + 1;
               var2 += (float)(var9 * (var8 >> 16 & 255)) / 255.0F;
               var3 += (float)(var9 * (var8 >> 8 & 255)) / 255.0F;
               var4 += (float)(var9 * (var8 >> 0 & 255)) / 255.0F;
               var5 += var9;
            }
         }

         if (var5 == 0) {
            return 0;
         } else {
            var2 = var2 / (float)var5 * 255.0F;
            var3 = var3 / (float)var5 * 255.0F;
            var4 = var4 / (float)var5 * 255.0F;
            return (int)var2 << 16 | (int)var3 << 8 | (int)var4;
         }
      }
   }

   public static PotionType func_185191_c(ItemStack var0) {
      return func_185187_c(var0.func_77978_p());
   }

   public static PotionType func_185187_c(@Nullable NBTTagCompound var0) {
      return var0 == null ? PotionTypes.field_185229_a : PotionType.func_185168_a(var0.func_74779_i("Potion"));
   }

   public static ItemStack func_185188_a(ItemStack var0, PotionType var1) {
      ResourceLocation var2 = IRegistry.field_212621_j.func_177774_c(var1);
      if (var1 == PotionTypes.field_185229_a) {
         var0.func_196083_e("Potion");
      } else {
         var0.func_196082_o().func_74778_a("Potion", var2.toString());
      }

      return var0;
   }

   public static ItemStack func_185184_a(ItemStack var0, Collection<PotionEffect> var1) {
      if (var1.isEmpty()) {
         return var0;
      } else {
         NBTTagCompound var2 = var0.func_196082_o();
         NBTTagList var3 = var2.func_150295_c("CustomPotionEffects", 9);
         Iterator var4 = var1.iterator();

         while(var4.hasNext()) {
            PotionEffect var5 = (PotionEffect)var4.next();
            var3.add((INBTBase)var5.func_82719_a(new NBTTagCompound()));
         }

         var2.func_74782_a("CustomPotionEffects", var3);
         return var0;
      }
   }

   public static void func_185182_a(ItemStack var0, List<ITextComponent> var1, float var2) {
      List var3 = func_185189_a(var0);
      ArrayList var4 = Lists.newArrayList();
      Iterator var5;
      TextComponentTranslation var7;
      Potion var8;
      if (var3.isEmpty()) {
         var1.add((new TextComponentTranslation("effect.none", new Object[0])).func_211708_a(TextFormatting.GRAY));
      } else {
         for(var5 = var3.iterator(); var5.hasNext(); var1.add(var7.func_211708_a(var8.func_76398_f() ? TextFormatting.RED : TextFormatting.BLUE))) {
            PotionEffect var6 = (PotionEffect)var5.next();
            var7 = new TextComponentTranslation(var6.func_76453_d(), new Object[0]);
            var8 = var6.func_188419_a();
            Map var9 = var8.func_111186_k();
            if (!var9.isEmpty()) {
               Iterator var10 = var9.entrySet().iterator();

               while(var10.hasNext()) {
                  Entry var11 = (Entry)var10.next();
                  AttributeModifier var12 = (AttributeModifier)var11.getValue();
                  AttributeModifier var13 = new AttributeModifier(var12.func_111166_b(), var8.func_111183_a(var6.func_76458_c(), var12), var12.func_111169_c());
                  var4.add(new Tuple(((IAttribute)var11.getKey()).func_111108_a(), var13));
               }
            }

            if (var6.func_76458_c() > 0) {
               var7.func_150258_a(" ").func_150257_a(new TextComponentTranslation("potion.potency." + var6.func_76458_c(), new Object[0]));
            }

            if (var6.func_76459_b() > 20) {
               var7.func_150258_a(" (").func_150258_a(PotionUtil.func_188410_a(var6, var2)).func_150258_a(")");
            }
         }
      }

      if (!var4.isEmpty()) {
         var1.add(new TextComponentString(""));
         var1.add((new TextComponentTranslation("potion.whenDrank", new Object[0])).func_211708_a(TextFormatting.DARK_PURPLE));
         var5 = var4.iterator();

         while(var5.hasNext()) {
            Tuple var14 = (Tuple)var5.next();
            AttributeModifier var15 = (AttributeModifier)var14.func_76340_b();
            double var16 = var15.func_111164_d();
            double var17;
            if (var15.func_111169_c() != 1 && var15.func_111169_c() != 2) {
               var17 = var15.func_111164_d();
            } else {
               var17 = var15.func_111164_d() * 100.0D;
            }

            if (var16 > 0.0D) {
               var1.add((new TextComponentTranslation("attribute.modifier.plus." + var15.func_111169_c(), new Object[]{ItemStack.field_111284_a.format(var17), new TextComponentTranslation("attribute.name." + (String)var14.func_76341_a(), new Object[0])})).func_211708_a(TextFormatting.BLUE));
            } else if (var16 < 0.0D) {
               var17 *= -1.0D;
               var1.add((new TextComponentTranslation("attribute.modifier.take." + var15.func_111169_c(), new Object[]{ItemStack.field_111284_a.format(var17), new TextComponentTranslation("attribute.name." + (String)var14.func_76341_a(), new Object[0])})).func_211708_a(TextFormatting.RED));
            }
         }
      }

   }
}
