package net.minecraft.util.text;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;

public class TextComponentUtils {
   public static ITextComponent func_211401_a(ITextComponent var0, Style var1) {
      if (var1.func_150229_g()) {
         return var0;
      } else {
         return var0.func_150256_b().func_150229_g() ? var0.func_150255_a(var1.func_150232_l()) : (new TextComponentString("")).func_150257_a(var0).func_150255_a(var1.func_150232_l());
      }
   }

   public static ITextComponent func_197680_a(@Nullable CommandSource var0, ITextComponent var1, @Nullable Entity var2) throws CommandSyntaxException {
      Object var3;
      if (var1 instanceof TextComponentScore && var0 != null) {
         TextComponentScore var7 = (TextComponentScore)var1;
         String var9;
         if (var7.func_197666_h() != null) {
            List var10 = var7.func_197666_h().func_197341_b(var0);
            if (var10.isEmpty()) {
               var9 = var7.func_179995_g();
            } else {
               if (var10.size() != 1) {
                  throw EntityArgument.field_197098_a.create();
               }

               var9 = ((Entity)var10.get(0)).func_195047_I_();
            }
         } else {
            var9 = var7.func_179995_g();
         }

         String var11 = var2 != null && var9.equals("*") ? var2.func_195047_I_() : var9;
         var3 = new TextComponentScore(var11, var7.func_179994_h());
         ((TextComponentScore)var3).func_179997_b(var7.func_150261_e());
         ((TextComponentScore)var3).func_197665_b(var0);
      } else if (var1 instanceof TextComponentSelector && var0 != null) {
         var3 = ((TextComponentSelector)var1).func_197668_a(var0);
      } else if (var1 instanceof TextComponentString) {
         var3 = new TextComponentString(((TextComponentString)var1).func_150265_g());
      } else if (var1 instanceof TextComponentKeybind) {
         var3 = new TextComponentKeybind(((TextComponentKeybind)var1).func_193633_h());
      } else {
         if (!(var1 instanceof TextComponentTranslation)) {
            return var1;
         }

         Object[] var4 = ((TextComponentTranslation)var1).func_150271_j();

         for(int var5 = 0; var5 < var4.length; ++var5) {
            Object var6 = var4[var5];
            if (var6 instanceof ITextComponent) {
               var4[var5] = func_197680_a(var0, (ITextComponent)var6, var2);
            }
         }

         var3 = new TextComponentTranslation(((TextComponentTranslation)var1).func_150268_i(), var4);
      }

      Iterator var8 = var1.func_150253_a().iterator();

      while(var8.hasNext()) {
         ITextComponent var12 = (ITextComponent)var8.next();
         ((ITextComponent)var3).func_150257_a(func_197680_a(var0, var12, var2));
      }

      return func_211401_a((ITextComponent)var3, var1.func_150256_b());
   }

   public static ITextComponent func_197679_a(GameProfile var0) {
      if (var0.getName() != null) {
         return new TextComponentString(var0.getName());
      } else {
         return var0.getId() != null ? new TextComponentString(var0.getId().toString()) : new TextComponentString("(unknown)");
      }
   }

   public static ITextComponent func_197678_a(Collection<String> var0) {
      return func_197675_a(var0, (var0x) -> {
         return (new TextComponentString(var0x)).func_211708_a(TextFormatting.GREEN);
      });
   }

   public static <T extends Comparable<T>> ITextComponent func_197675_a(Collection<T> var0, Function<T, ITextComponent> var1) {
      if (var0.isEmpty()) {
         return new TextComponentString("");
      } else if (var0.size() == 1) {
         return (ITextComponent)var1.apply(var0.iterator().next());
      } else {
         ArrayList var2 = Lists.newArrayList(var0);
         var2.sort(Comparable::compareTo);
         return func_197677_b(var0, var1);
      }
   }

   public static <T> ITextComponent func_197677_b(Collection<T> var0, Function<T, ITextComponent> var1) {
      if (var0.isEmpty()) {
         return new TextComponentString("");
      } else if (var0.size() == 1) {
         return (ITextComponent)var1.apply(var0.iterator().next());
      } else {
         TextComponentString var2 = new TextComponentString("");
         boolean var3 = true;

         for(Iterator var4 = var0.iterator(); var4.hasNext(); var3 = false) {
            Object var5 = var4.next();
            if (!var3) {
               var2.func_150257_a((new TextComponentString(", ")).func_211708_a(TextFormatting.GRAY));
            }

            var2.func_150257_a((ITextComponent)var1.apply(var5));
         }

         return var2;
      }
   }

   public static ITextComponent func_197676_a(ITextComponent var0) {
      return (new TextComponentString("[")).func_150257_a(var0).func_150258_a("]");
   }

   public static ITextComponent func_202465_a(Message var0) {
      return (ITextComponent)(var0 instanceof ITextComponent ? (ITextComponent)var0 : new TextComponentString(var0.getString()));
   }
}
