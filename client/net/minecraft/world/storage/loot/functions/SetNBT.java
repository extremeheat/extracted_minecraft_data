package net.minecraft.world.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;

public class SetNBT extends LootFunction {
   private final NBTTagCompound field_186570_a;

   public SetNBT(LootCondition[] var1, NBTTagCompound var2) {
      super(var1);
      this.field_186570_a = var2;
   }

   public ItemStack func_186553_a(ItemStack var1, Random var2, LootContext var3) {
      var1.func_196082_o().func_197643_a(this.field_186570_a);
      return var1;
   }

   public static class Serializer extends LootFunction.Serializer<SetNBT> {
      public Serializer() {
         super(new ResourceLocation("set_nbt"), SetNBT.class);
      }

      public void func_186532_a(JsonObject var1, SetNBT var2, JsonSerializationContext var3) {
         var1.addProperty("tag", var2.field_186570_a.toString());
      }

      public SetNBT func_186530_b(JsonObject var1, JsonDeserializationContext var2, LootCondition[] var3) {
         try {
            NBTTagCompound var4 = JsonToNBT.func_180713_a(JsonUtils.func_151200_h(var1, "tag"));
            return new SetNBT(var3, var4);
         } catch (CommandSyntaxException var5) {
            throw new JsonSyntaxException(var5.getMessage());
         }
      }

      // $FF: synthetic method
      public LootFunction func_186530_b(JsonObject var1, JsonDeserializationContext var2, LootCondition[] var3) {
         return this.func_186530_b(var1, var2, var3);
      }
   }
}
