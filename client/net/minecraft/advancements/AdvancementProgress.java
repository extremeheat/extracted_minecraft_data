package net.minecraft.advancements;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JsonUtils;

public class AdvancementProgress implements Comparable<AdvancementProgress> {
   private final Map<String, CriterionProgress> field_192110_a = Maps.newHashMap();
   private String[][] field_192111_b = new String[0][];

   public AdvancementProgress() {
      super();
   }

   public void func_192099_a(Map<String, Criterion> var1, String[][] var2) {
      Set var3 = var1.keySet();
      this.field_192110_a.entrySet().removeIf((var1x) -> {
         return !var3.contains(var1x.getKey());
      });
      Iterator var4 = var3.iterator();

      while(var4.hasNext()) {
         String var5 = (String)var4.next();
         if (!this.field_192110_a.containsKey(var5)) {
            this.field_192110_a.put(var5, new CriterionProgress());
         }
      }

      this.field_192111_b = var2;
   }

   public boolean func_192105_a() {
      if (this.field_192111_b.length == 0) {
         return false;
      } else {
         String[][] var1 = this.field_192111_b;
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            String[] var4 = var1[var3];
            boolean var5 = false;
            String[] var6 = var4;
            int var7 = var4.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               String var9 = var6[var8];
               CriterionProgress var10 = this.func_192106_c(var9);
               if (var10 != null && var10.func_192151_a()) {
                  var5 = true;
                  break;
               }
            }

            if (!var5) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean func_192108_b() {
      Iterator var1 = this.field_192110_a.values().iterator();

      CriterionProgress var2;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         var2 = (CriterionProgress)var1.next();
      } while(!var2.func_192151_a());

      return true;
   }

   public boolean func_192109_a(String var1) {
      CriterionProgress var2 = (CriterionProgress)this.field_192110_a.get(var1);
      if (var2 != null && !var2.func_192151_a()) {
         var2.func_192153_b();
         return true;
      } else {
         return false;
      }
   }

   public boolean func_192101_b(String var1) {
      CriterionProgress var2 = (CriterionProgress)this.field_192110_a.get(var1);
      if (var2 != null && var2.func_192151_a()) {
         var2.func_192154_c();
         return true;
      } else {
         return false;
      }
   }

   public String toString() {
      return "AdvancementProgress{criteria=" + this.field_192110_a + ", requirements=" + Arrays.deepToString(this.field_192111_b) + '}';
   }

   public void func_192104_a(PacketBuffer var1) {
      var1.func_150787_b(this.field_192110_a.size());
      Iterator var2 = this.field_192110_a.entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         var1.func_180714_a((String)var3.getKey());
         ((CriterionProgress)var3.getValue()).func_192150_a(var1);
      }

   }

   public static AdvancementProgress func_192100_b(PacketBuffer var0) {
      AdvancementProgress var1 = new AdvancementProgress();
      int var2 = var0.func_150792_a();

      for(int var3 = 0; var3 < var2; ++var3) {
         var1.field_192110_a.put(var0.func_150789_c(32767), CriterionProgress.func_192149_a(var0));
      }

      return var1;
   }

   @Nullable
   public CriterionProgress func_192106_c(String var1) {
      return (CriterionProgress)this.field_192110_a.get(var1);
   }

   public float func_192103_c() {
      if (this.field_192110_a.isEmpty()) {
         return 0.0F;
      } else {
         float var1 = (float)this.field_192111_b.length;
         float var2 = (float)this.func_194032_h();
         return var2 / var1;
      }
   }

   @Nullable
   public String func_193126_d() {
      if (this.field_192110_a.isEmpty()) {
         return null;
      } else {
         int var1 = this.field_192111_b.length;
         if (var1 <= 1) {
            return null;
         } else {
            int var2 = this.func_194032_h();
            return var2 + "/" + var1;
         }
      }
   }

   private int func_194032_h() {
      int var1 = 0;
      String[][] var2 = this.field_192111_b;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String[] var5 = var2[var4];
         boolean var6 = false;
         String[] var7 = var5;
         int var8 = var5.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            String var10 = var7[var9];
            CriterionProgress var11 = this.func_192106_c(var10);
            if (var11 != null && var11.func_192151_a()) {
               var6 = true;
               break;
            }
         }

         if (var6) {
            ++var1;
         }
      }

      return var1;
   }

   public Iterable<String> func_192107_d() {
      ArrayList var1 = Lists.newArrayList();
      Iterator var2 = this.field_192110_a.entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         if (!((CriterionProgress)var3.getValue()).func_192151_a()) {
            var1.add(var3.getKey());
         }
      }

      return var1;
   }

   public Iterable<String> func_192102_e() {
      ArrayList var1 = Lists.newArrayList();
      Iterator var2 = this.field_192110_a.entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         if (((CriterionProgress)var3.getValue()).func_192151_a()) {
            var1.add(var3.getKey());
         }
      }

      return var1;
   }

   @Nullable
   public Date func_193128_g() {
      Date var1 = null;
      Iterator var2 = this.field_192110_a.values().iterator();

      while(true) {
         CriterionProgress var3;
         do {
            do {
               if (!var2.hasNext()) {
                  return var1;
               }

               var3 = (CriterionProgress)var2.next();
            } while(!var3.func_192151_a());
         } while(var1 != null && !var3.func_193140_d().before(var1));

         var1 = var3.func_193140_d();
      }
   }

   public int compareTo(AdvancementProgress var1) {
      Date var2 = this.func_193128_g();
      Date var3 = var1.func_193128_g();
      if (var2 == null && var3 != null) {
         return 1;
      } else if (var2 != null && var3 == null) {
         return -1;
      } else {
         return var2 == null && var3 == null ? 0 : var2.compareTo(var3);
      }
   }

   // $FF: synthetic method
   public int compareTo(Object var1) {
      return this.compareTo((AdvancementProgress)var1);
   }

   public static class Serializer implements JsonDeserializer<AdvancementProgress>, JsonSerializer<AdvancementProgress> {
      public Serializer() {
         super();
      }

      public JsonElement serialize(AdvancementProgress var1, Type var2, JsonSerializationContext var3) {
         JsonObject var4 = new JsonObject();
         JsonObject var5 = new JsonObject();
         Iterator var6 = var1.field_192110_a.entrySet().iterator();

         while(var6.hasNext()) {
            Entry var7 = (Entry)var6.next();
            CriterionProgress var8 = (CriterionProgress)var7.getValue();
            if (var8.func_192151_a()) {
               var5.add((String)var7.getKey(), var8.func_192148_e());
            }
         }

         if (!var5.entrySet().isEmpty()) {
            var4.add("criteria", var5);
         }

         var4.addProperty("done", var1.func_192105_a());
         return var4;
      }

      public AdvancementProgress deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var4 = JsonUtils.func_151210_l(var1, "advancement");
         JsonObject var5 = JsonUtils.func_151218_a(var4, "criteria", new JsonObject());
         AdvancementProgress var6 = new AdvancementProgress();
         Iterator var7 = var5.entrySet().iterator();

         while(var7.hasNext()) {
            Entry var8 = (Entry)var7.next();
            String var9 = (String)var8.getKey();
            var6.field_192110_a.put(var9, CriterionProgress.func_209541_a(JsonUtils.func_151206_a((JsonElement)var8.getValue(), var9)));
         }

         return var6;
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }

      // $FF: synthetic method
      public JsonElement serialize(Object var1, Type var2, JsonSerializationContext var3) {
         return this.serialize((AdvancementProgress)var1, var2, var3);
      }
   }
}
