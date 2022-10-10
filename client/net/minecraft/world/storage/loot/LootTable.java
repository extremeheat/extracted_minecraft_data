package net.minecraft.world.storage.loot;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootTable {
   private static final Logger field_186465_b = LogManager.getLogger();
   public static final LootTable field_186464_a = new LootTable(new LootPool[0]);
   private final LootPool[] field_186466_c;

   public LootTable(LootPool[] var1) {
      super();
      this.field_186466_c = var1;
   }

   public List<ItemStack> func_186462_a(Random var1, LootContext var2) {
      ArrayList var3 = Lists.newArrayList();
      if (var2.func_186496_a(this)) {
         LootPool[] var4 = this.field_186466_c;
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            LootPool var7 = var4[var6];
            var7.func_186449_b(var3, var1, var2);
         }

         var2.func_186490_b(this);
      } else {
         field_186465_b.warn("Detected infinite loop in loot tables");
      }

      return var3;
   }

   public void func_186460_a(IInventory var1, Random var2, LootContext var3) {
      List var4 = this.func_186462_a(var2, var3);
      List var5 = this.func_186459_a(var1, var2);
      this.func_186463_a(var4, var5.size(), var2);
      Iterator var6 = var4.iterator();

      while(var6.hasNext()) {
         ItemStack var7 = (ItemStack)var6.next();
         if (var5.isEmpty()) {
            field_186465_b.warn("Tried to over-fill a container");
            return;
         }

         if (var7.func_190926_b()) {
            var1.func_70299_a((Integer)var5.remove(var5.size() - 1), ItemStack.field_190927_a);
         } else {
            var1.func_70299_a((Integer)var5.remove(var5.size() - 1), var7);
         }
      }

   }

   private void func_186463_a(List<ItemStack> var1, int var2, Random var3) {
      ArrayList var4 = Lists.newArrayList();
      Iterator var5 = var1.iterator();

      while(var5.hasNext()) {
         ItemStack var6 = (ItemStack)var5.next();
         if (var6.func_190926_b()) {
            var5.remove();
         } else if (var6.func_190916_E() > 1) {
            var4.add(var6);
            var5.remove();
         }
      }

      while(var2 - var1.size() - var4.size() > 0 && !var4.isEmpty()) {
         ItemStack var8 = (ItemStack)var4.remove(MathHelper.func_76136_a(var3, 0, var4.size() - 1));
         int var9 = MathHelper.func_76136_a(var3, 1, var8.func_190916_E() / 2);
         ItemStack var7 = var8.func_77979_a(var9);
         if (var8.func_190916_E() > 1 && var3.nextBoolean()) {
            var4.add(var8);
         } else {
            var1.add(var8);
         }

         if (var7.func_190916_E() > 1 && var3.nextBoolean()) {
            var4.add(var7);
         } else {
            var1.add(var7);
         }
      }

      var1.addAll(var4);
      Collections.shuffle(var1, var3);
   }

   private List<Integer> func_186459_a(IInventory var1, Random var2) {
      ArrayList var3 = Lists.newArrayList();

      for(int var4 = 0; var4 < var1.func_70302_i_(); ++var4) {
         if (var1.func_70301_a(var4).func_190926_b()) {
            var3.add(var4);
         }
      }

      Collections.shuffle(var3, var2);
      return var3;
   }

   public static class Serializer implements JsonDeserializer<LootTable>, JsonSerializer<LootTable> {
      public Serializer() {
         super();
      }

      public LootTable deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var4 = JsonUtils.func_151210_l(var1, "loot table");
         LootPool[] var5 = (LootPool[])JsonUtils.func_188177_a(var4, "pools", new LootPool[0], var3, LootPool[].class);
         return new LootTable(var5);
      }

      public JsonElement serialize(LootTable var1, Type var2, JsonSerializationContext var3) {
         JsonObject var4 = new JsonObject();
         var4.add("pools", var3.serialize(var1.field_186466_c));
         return var4;
      }

      // $FF: synthetic method
      public JsonElement serialize(Object var1, Type var2, JsonSerializationContext var3) {
         return this.serialize((LootTable)var1, var2, var3);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
