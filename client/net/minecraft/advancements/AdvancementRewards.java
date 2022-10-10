package net.minecraft.advancements;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.command.FunctionObject;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.storage.loot.LootContext;

public class AdvancementRewards {
   public static final AdvancementRewards field_192114_a;
   private final int field_192115_b;
   private final ResourceLocation[] field_192116_c;
   private final ResourceLocation[] field_192117_d;
   private final FunctionObject.CacheableFunction field_193129_e;

   public AdvancementRewards(int var1, ResourceLocation[] var2, ResourceLocation[] var3, FunctionObject.CacheableFunction var4) {
      super();
      this.field_192115_b = var1;
      this.field_192116_c = var2;
      this.field_192117_d = var3;
      this.field_193129_e = var4;
   }

   public void func_192113_a(EntityPlayerMP var1) {
      var1.func_195068_e(this.field_192115_b);
      LootContext var2 = (new LootContext.Builder(var1.func_71121_q())).func_186472_a(var1).func_204313_a(new BlockPos(var1)).func_186471_a();
      boolean var3 = false;
      ResourceLocation[] var4 = this.field_192116_c;
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         ResourceLocation var7 = var4[var6];
         Iterator var8 = var1.field_71133_b.func_200249_aQ().func_186521_a(var7).func_186462_a(var1.func_70681_au(), var2).iterator();

         while(var8.hasNext()) {
            ItemStack var9 = (ItemStack)var8.next();
            if (var1.func_191521_c(var9)) {
               var1.field_70170_p.func_184148_a((EntityPlayer)null, var1.field_70165_t, var1.field_70163_u, var1.field_70161_v, SoundEvents.field_187638_cR, SoundCategory.PLAYERS, 0.2F, ((var1.func_70681_au().nextFloat() - var1.func_70681_au().nextFloat()) * 0.7F + 1.0F) * 2.0F);
               var3 = true;
            } else {
               EntityItem var10 = var1.func_71019_a(var9, false);
               if (var10 != null) {
                  var10.func_174868_q();
                  var10.func_200217_b(var1.func_110124_au());
               }
            }
         }
      }

      if (var3) {
         var1.field_71069_bz.func_75142_b();
      }

      if (this.field_192117_d.length > 0) {
         var1.func_193102_a(this.field_192117_d);
      }

      MinecraftServer var11 = var1.field_71133_b;
      FunctionObject var12 = this.field_193129_e.func_193518_a(var11.func_193030_aL());
      if (var12 != null) {
         var11.func_193030_aL().func_195447_a(var12, var1.func_195051_bN().func_197031_a().func_197033_a(2));
      }

   }

   public String toString() {
      return "AdvancementRewards{experience=" + this.field_192115_b + ", loot=" + Arrays.toString(this.field_192116_c) + ", recipes=" + Arrays.toString(this.field_192117_d) + ", function=" + this.field_193129_e + '}';
   }

   public JsonElement func_200286_b() {
      if (this == field_192114_a) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject var1 = new JsonObject();
         if (this.field_192115_b != 0) {
            var1.addProperty("experience", this.field_192115_b);
         }

         JsonArray var2;
         ResourceLocation[] var3;
         int var4;
         int var5;
         ResourceLocation var6;
         if (this.field_192116_c.length > 0) {
            var2 = new JsonArray();
            var3 = this.field_192116_c;
            var4 = var3.length;

            for(var5 = 0; var5 < var4; ++var5) {
               var6 = var3[var5];
               var2.add(var6.toString());
            }

            var1.add("loot", var2);
         }

         if (this.field_192117_d.length > 0) {
            var2 = new JsonArray();
            var3 = this.field_192117_d;
            var4 = var3.length;

            for(var5 = 0; var5 < var4; ++var5) {
               var6 = var3[var5];
               var2.add(var6.toString());
            }

            var1.add("recipes", var2);
         }

         if (this.field_193129_e.func_200376_a() != null) {
            var1.addProperty("function", this.field_193129_e.func_200376_a().toString());
         }

         return var1;
      }
   }

   static {
      field_192114_a = new AdvancementRewards(0, new ResourceLocation[0], new ResourceLocation[0], FunctionObject.CacheableFunction.field_193519_a);
   }

   public static class Builder {
      private int field_200282_a;
      private final List<ResourceLocation> field_200283_b = Lists.newArrayList();
      private final List<ResourceLocation> field_200284_c = Lists.newArrayList();
      @Nullable
      private ResourceLocation field_200285_d;

      public Builder() {
         super();
      }

      public static AdvancementRewards.Builder func_203907_a(int var0) {
         return (new AdvancementRewards.Builder()).func_203906_b(var0);
      }

      public AdvancementRewards.Builder func_203906_b(int var1) {
         this.field_200282_a += var1;
         return this;
      }

      public static AdvancementRewards.Builder func_200280_c(ResourceLocation var0) {
         return (new AdvancementRewards.Builder()).func_200279_d(var0);
      }

      public AdvancementRewards.Builder func_200279_d(ResourceLocation var1) {
         this.field_200284_c.add(var1);
         return this;
      }

      public AdvancementRewards func_200281_a() {
         return new AdvancementRewards(this.field_200282_a, (ResourceLocation[])this.field_200283_b.toArray(new ResourceLocation[0]), (ResourceLocation[])this.field_200284_c.toArray(new ResourceLocation[0]), this.field_200285_d == null ? FunctionObject.CacheableFunction.field_193519_a : new FunctionObject.CacheableFunction(this.field_200285_d));
      }
   }

   public static class Deserializer implements JsonDeserializer<AdvancementRewards> {
      public Deserializer() {
         super();
      }

      public AdvancementRewards deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var4 = JsonUtils.func_151210_l(var1, "rewards");
         int var5 = JsonUtils.func_151208_a(var4, "experience", 0);
         JsonArray var6 = JsonUtils.func_151213_a(var4, "loot", new JsonArray());
         ResourceLocation[] var7 = new ResourceLocation[var6.size()];

         for(int var8 = 0; var8 < var7.length; ++var8) {
            var7[var8] = new ResourceLocation(JsonUtils.func_151206_a(var6.get(var8), "loot[" + var8 + "]"));
         }

         JsonArray var12 = JsonUtils.func_151213_a(var4, "recipes", new JsonArray());
         ResourceLocation[] var9 = new ResourceLocation[var12.size()];

         for(int var10 = 0; var10 < var9.length; ++var10) {
            var9[var10] = new ResourceLocation(JsonUtils.func_151206_a(var12.get(var10), "recipes[" + var10 + "]"));
         }

         FunctionObject.CacheableFunction var11;
         if (var4.has("function")) {
            var11 = new FunctionObject.CacheableFunction(new ResourceLocation(JsonUtils.func_151200_h(var4, "function")));
         } else {
            var11 = FunctionObject.CacheableFunction.field_193519_a;
         }

         return new AdvancementRewards(var5, var7, var9, var11);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
