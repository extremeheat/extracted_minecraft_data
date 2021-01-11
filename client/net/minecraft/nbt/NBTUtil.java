package net.minecraft.nbt;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.util.Iterator;
import java.util.UUID;
import net.minecraft.util.StringUtils;

public final class NBTUtil {
   public static GameProfile func_152459_a(NBTTagCompound var0) {
      String var1 = null;
      String var2 = null;
      if (var0.func_150297_b("Name", 8)) {
         var1 = var0.func_74779_i("Name");
      }

      if (var0.func_150297_b("Id", 8)) {
         var2 = var0.func_74779_i("Id");
      }

      if (StringUtils.func_151246_b(var1) && StringUtils.func_151246_b(var2)) {
         return null;
      } else {
         UUID var3;
         try {
            var3 = UUID.fromString(var2);
         } catch (Throwable var12) {
            var3 = null;
         }

         GameProfile var4 = new GameProfile(var3, var1);
         if (var0.func_150297_b("Properties", 10)) {
            NBTTagCompound var5 = var0.func_74775_l("Properties");
            Iterator var6 = var5.func_150296_c().iterator();

            while(var6.hasNext()) {
               String var7 = (String)var6.next();
               NBTTagList var8 = var5.func_150295_c(var7, 10);

               for(int var9 = 0; var9 < var8.func_74745_c(); ++var9) {
                  NBTTagCompound var10 = var8.func_150305_b(var9);
                  String var11 = var10.func_74779_i("Value");
                  if (var10.func_150297_b("Signature", 8)) {
                     var4.getProperties().put(var7, new Property(var7, var11, var10.func_74779_i("Signature")));
                  } else {
                     var4.getProperties().put(var7, new Property(var7, var11));
                  }
               }
            }
         }

         return var4;
      }
   }

   public static NBTTagCompound func_180708_a(NBTTagCompound var0, GameProfile var1) {
      if (!StringUtils.func_151246_b(var1.getName())) {
         var0.func_74778_a("Name", var1.getName());
      }

      if (var1.getId() != null) {
         var0.func_74778_a("Id", var1.getId().toString());
      }

      if (!var1.getProperties().isEmpty()) {
         NBTTagCompound var2 = new NBTTagCompound();
         Iterator var3 = var1.getProperties().keySet().iterator();

         while(var3.hasNext()) {
            String var4 = (String)var3.next();
            NBTTagList var5 = new NBTTagList();

            NBTTagCompound var8;
            for(Iterator var6 = var1.getProperties().get(var4).iterator(); var6.hasNext(); var5.func_74742_a(var8)) {
               Property var7 = (Property)var6.next();
               var8 = new NBTTagCompound();
               var8.func_74778_a("Value", var7.getValue());
               if (var7.hasSignature()) {
                  var8.func_74778_a("Signature", var7.getSignature());
               }
            }

            var2.func_74782_a(var4, var5);
         }

         var0.func_74782_a("Properties", var2);
      }

      return var0;
   }

   public static boolean func_181123_a(NBTBase var0, NBTBase var1, boolean var2) {
      if (var0 == var1) {
         return true;
      } else if (var0 == null) {
         return true;
      } else if (var1 == null) {
         return false;
      } else if (!var0.getClass().equals(var1.getClass())) {
         return false;
      } else if (var0 instanceof NBTTagCompound) {
         NBTTagCompound var9 = (NBTTagCompound)var0;
         NBTTagCompound var10 = (NBTTagCompound)var1;
         Iterator var11 = var9.func_150296_c().iterator();

         String var12;
         NBTBase var13;
         do {
            if (!var11.hasNext()) {
               return true;
            }

            var12 = (String)var11.next();
            var13 = var9.func_74781_a(var12);
         } while(func_181123_a(var13, var10.func_74781_a(var12), var2));

         return false;
      } else if (var0 instanceof NBTTagList && var2) {
         NBTTagList var3 = (NBTTagList)var0;
         NBTTagList var4 = (NBTTagList)var1;
         if (var3.func_74745_c() == 0) {
            return var4.func_74745_c() == 0;
         } else {
            for(int var5 = 0; var5 < var3.func_74745_c(); ++var5) {
               NBTBase var6 = var3.func_179238_g(var5);
               boolean var7 = false;

               for(int var8 = 0; var8 < var4.func_74745_c(); ++var8) {
                  if (func_181123_a(var6, var4.func_179238_g(var8), var2)) {
                     var7 = true;
                     break;
                  }
               }

               if (!var7) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return var0.equals(var1);
      }
   }
}
