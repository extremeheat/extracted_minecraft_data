package net.minecraft.advancements.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.JsonUtils;

public class NBTPredicate {
   public static final NBTPredicate field_193479_a = new NBTPredicate((NBTTagCompound)null);
   @Nullable
   private final NBTTagCompound field_193480_b;

   public NBTPredicate(@Nullable NBTTagCompound var1) {
      super();
      this.field_193480_b = var1;
   }

   public boolean func_193478_a(ItemStack var1) {
      return this == field_193479_a ? true : this.func_193477_a(var1.func_77978_p());
   }

   public boolean func_193475_a(Entity var1) {
      return this == field_193479_a ? true : this.func_193477_a(func_196981_b(var1));
   }

   public boolean func_193477_a(@Nullable INBTBase var1) {
      if (var1 == null) {
         return this == field_193479_a;
      } else {
         return this.field_193480_b == null || NBTUtil.func_181123_a(this.field_193480_b, var1, true);
      }
   }

   public JsonElement func_200322_a() {
      return (JsonElement)(this != field_193479_a && this.field_193480_b != null ? new JsonPrimitive(this.field_193480_b.toString()) : JsonNull.INSTANCE);
   }

   public static NBTPredicate func_193476_a(@Nullable JsonElement var0) {
      if (var0 != null && !var0.isJsonNull()) {
         NBTTagCompound var1;
         try {
            var1 = JsonToNBT.func_180713_a(JsonUtils.func_151206_a(var0, "nbt"));
         } catch (CommandSyntaxException var3) {
            throw new JsonSyntaxException("Invalid nbt tag: " + var3.getMessage());
         }

         return new NBTPredicate(var1);
      } else {
         return field_193479_a;
      }
   }

   public static NBTTagCompound func_196981_b(Entity var0) {
      NBTTagCompound var1 = var0.func_189511_e(new NBTTagCompound());
      if (var0 instanceof EntityPlayer) {
         ItemStack var2 = ((EntityPlayer)var0).field_71071_by.func_70448_g();
         if (!var2.func_190926_b()) {
            var1.func_74782_a("SelectedItem", var2.func_77955_b(new NBTTagCompound()));
         }
      }

      return var1;
   }
}
