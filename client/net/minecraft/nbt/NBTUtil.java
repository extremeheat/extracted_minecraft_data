package net.minecraft.nbt;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
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

            for(String var7 : var5.func_150296_c()) {
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

   public static void func_152460_a(NBTTagCompound var0, GameProfile var1) {
      if (!StringUtils.func_151246_b(var1.getName())) {
         var0.func_74778_a("Name", var1.getName());
      }

      if (var1.getId() != null) {
         var0.func_74778_a("Id", var1.getId().toString());
      }

      if (!var1.getProperties().isEmpty()) {
         NBTTagCompound var2 = new NBTTagCompound();

         for(String var4 : var1.getProperties().keySet()) {
            NBTTagList var5 = new NBTTagList();

            for(Property var7 : var1.getProperties().get(var4)) {
               NBTTagCompound var8 = new NBTTagCompound();
               var8.func_74778_a("Value", var7.getValue());
               if (var7.hasSignature()) {
                  var8.func_74778_a("Signature", var7.getSignature());
               }

               var5.func_74742_a(var8);
            }

            var2.func_74782_a(var4, var5);
         }

         var0.func_74782_a("Properties", var2);
      }
   }
}
