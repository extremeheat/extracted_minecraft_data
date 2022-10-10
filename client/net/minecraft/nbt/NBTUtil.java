package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.DSL.TypeReference;
import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.state.IProperty;
import net.minecraft.state.IStateHolder;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.IRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class NBTUtil {
   private static final Logger field_193591_a = LogManager.getLogger();

   @Nullable
   public static GameProfile func_152459_a(NBTTagCompound var0) {
      String var1 = null;
      String var2 = null;
      if (var0.func_150297_b("Name", 8)) {
         var1 = var0.func_74779_i("Name");
      }

      if (var0.func_150297_b("Id", 8)) {
         var2 = var0.func_74779_i("Id");
      }

      try {
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

               for(int var9 = 0; var9 < var8.size(); ++var9) {
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
      } catch (Throwable var13) {
         return null;
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
            for(Iterator var6 = var1.getProperties().get(var4).iterator(); var6.hasNext(); var5.add((INBTBase)var8)) {
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

   @VisibleForTesting
   public static boolean func_181123_a(@Nullable INBTBase var0, @Nullable INBTBase var1, boolean var2) {
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
         INBTBase var13;
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
         if (var3.isEmpty()) {
            return var4.isEmpty();
         } else {
            for(int var5 = 0; var5 < var3.size(); ++var5) {
               INBTBase var6 = var3.get(var5);
               boolean var7 = false;

               for(int var8 = 0; var8 < var4.size(); ++var8) {
                  if (func_181123_a(var6, var4.get(var8), var2)) {
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

   public static NBTTagCompound func_186862_a(UUID var0) {
      NBTTagCompound var1 = new NBTTagCompound();
      var1.func_74772_a("M", var0.getMostSignificantBits());
      var1.func_74772_a("L", var0.getLeastSignificantBits());
      return var1;
   }

   public static UUID func_186860_b(NBTTagCompound var0) {
      return new UUID(var0.func_74763_f("M"), var0.func_74763_f("L"));
   }

   public static BlockPos func_186861_c(NBTTagCompound var0) {
      return new BlockPos(var0.func_74762_e("X"), var0.func_74762_e("Y"), var0.func_74762_e("Z"));
   }

   public static NBTTagCompound func_186859_a(BlockPos var0) {
      NBTTagCompound var1 = new NBTTagCompound();
      var1.func_74768_a("X", var0.func_177958_n());
      var1.func_74768_a("Y", var0.func_177956_o());
      var1.func_74768_a("Z", var0.func_177952_p());
      return var1;
   }

   public static IBlockState func_190008_d(NBTTagCompound var0) {
      if (!var0.func_150297_b("Name", 8)) {
         return Blocks.field_150350_a.func_176223_P();
      } else {
         Block var1 = (Block)IRegistry.field_212618_g.func_82594_a(new ResourceLocation(var0.func_74779_i("Name")));
         IBlockState var2 = var1.func_176223_P();
         if (var0.func_150297_b("Properties", 10)) {
            NBTTagCompound var3 = var0.func_74775_l("Properties");
            StateContainer var4 = var1.func_176194_O();
            Iterator var5 = var3.func_150296_c().iterator();

            while(var5.hasNext()) {
               String var6 = (String)var5.next();
               IProperty var7 = var4.func_185920_a(var6);
               if (var7 != null) {
                  var2 = (IBlockState)func_193590_a(var2, var7, var6, var3, var0);
               }
            }
         }

         return var2;
      }
   }

   private static <S extends IStateHolder<S>, T extends Comparable<T>> S func_193590_a(S var0, IProperty<T> var1, String var2, NBTTagCompound var3, NBTTagCompound var4) {
      Optional var5 = var1.func_185929_b(var3.func_74779_i(var2));
      if (var5.isPresent()) {
         return (IStateHolder)var0.func_206870_a(var1, (Comparable)var5.get());
      } else {
         field_193591_a.warn("Unable to read property: {} with value: {} for blockstate: {}", var2, var3.func_74779_i(var2), var4.toString());
         return var0;
      }
   }

   public static NBTTagCompound func_190009_a(IBlockState var0) {
      NBTTagCompound var1 = new NBTTagCompound();
      var1.func_74778_a("Name", IRegistry.field_212618_g.func_177774_c(var0.func_177230_c()).toString());
      ImmutableMap var2 = var0.func_206871_b();
      if (!var2.isEmpty()) {
         NBTTagCompound var3 = new NBTTagCompound();
         UnmodifiableIterator var4 = var2.entrySet().iterator();

         while(var4.hasNext()) {
            Entry var5 = (Entry)var4.next();
            IProperty var6 = (IProperty)var5.getKey();
            var3.func_74778_a(var6.func_177701_a(), func_190010_a(var6, (Comparable)var5.getValue()));
         }

         var1.func_74782_a("Properties", var3);
      }

      return var1;
   }

   private static <T extends Comparable<T>> String func_190010_a(IProperty<T> var0, Comparable<?> var1) {
      return var0.func_177702_a(var1);
   }

   public static NBTTagCompound func_210822_a(DataFixer var0, TypeReference var1, NBTTagCompound var2, int var3) {
      return func_210821_a(var0, var1, var2, var3, 1631);
   }

   public static NBTTagCompound func_210821_a(DataFixer var0, TypeReference var1, NBTTagCompound var2, int var3, int var4) {
      return (NBTTagCompound)var0.update(var1, new Dynamic(NBTDynamicOps.field_210820_a, var2), var3, var4).getValue();
   }
}
