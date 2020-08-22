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
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;

public class AdvancementProgress implements Comparable {
   private final Map criteria = Maps.newHashMap();
   private String[][] requirements = new String[0][];

   public void update(Map var1, String[][] var2) {
      Set var3 = var1.keySet();
      this.criteria.entrySet().removeIf((var1x) -> {
         return !var3.contains(var1x.getKey());
      });
      Iterator var4 = var3.iterator();

      while(var4.hasNext()) {
         String var5 = (String)var4.next();
         if (!this.criteria.containsKey(var5)) {
            this.criteria.put(var5, new CriterionProgress());
         }
      }

      this.requirements = var2;
   }

   public boolean isDone() {
      if (this.requirements.length == 0) {
         return false;
      } else {
         String[][] var1 = this.requirements;
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            String[] var4 = var1[var3];
            boolean var5 = false;
            String[] var6 = var4;
            int var7 = var4.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               String var9 = var6[var8];
               CriterionProgress var10 = this.getCriterion(var9);
               if (var10 != null && var10.isDone()) {
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

   public boolean hasProgress() {
      Iterator var1 = this.criteria.values().iterator();

      CriterionProgress var2;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         var2 = (CriterionProgress)var1.next();
      } while(!var2.isDone());

      return true;
   }

   public boolean grantProgress(String var1) {
      CriterionProgress var2 = (CriterionProgress)this.criteria.get(var1);
      if (var2 != null && !var2.isDone()) {
         var2.grant();
         return true;
      } else {
         return false;
      }
   }

   public boolean revokeProgress(String var1) {
      CriterionProgress var2 = (CriterionProgress)this.criteria.get(var1);
      if (var2 != null && var2.isDone()) {
         var2.revoke();
         return true;
      } else {
         return false;
      }
   }

   public String toString() {
      return "AdvancementProgress{criteria=" + this.criteria + ", requirements=" + Arrays.deepToString(this.requirements) + '}';
   }

   public void serializeToNetwork(FriendlyByteBuf var1) {
      var1.writeVarInt(this.criteria.size());
      Iterator var2 = this.criteria.entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         var1.writeUtf((String)var3.getKey());
         ((CriterionProgress)var3.getValue()).serializeToNetwork(var1);
      }

   }

   public static AdvancementProgress fromNetwork(FriendlyByteBuf var0) {
      AdvancementProgress var1 = new AdvancementProgress();
      int var2 = var0.readVarInt();

      for(int var3 = 0; var3 < var2; ++var3) {
         var1.criteria.put(var0.readUtf(32767), CriterionProgress.fromNetwork(var0));
      }

      return var1;
   }

   @Nullable
   public CriterionProgress getCriterion(String var1) {
      return (CriterionProgress)this.criteria.get(var1);
   }

   public float getPercent() {
      if (this.criteria.isEmpty()) {
         return 0.0F;
      } else {
         float var1 = (float)this.requirements.length;
         float var2 = (float)this.countCompletedRequirements();
         return var2 / var1;
      }
   }

   @Nullable
   public String getProgressText() {
      if (this.criteria.isEmpty()) {
         return null;
      } else {
         int var1 = this.requirements.length;
         if (var1 <= 1) {
            return null;
         } else {
            int var2 = this.countCompletedRequirements();
            return var2 + "/" + var1;
         }
      }
   }

   private int countCompletedRequirements() {
      int var1 = 0;
      String[][] var2 = this.requirements;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String[] var5 = var2[var4];
         boolean var6 = false;
         String[] var7 = var5;
         int var8 = var5.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            String var10 = var7[var9];
            CriterionProgress var11 = this.getCriterion(var10);
            if (var11 != null && var11.isDone()) {
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

   public Iterable getRemainingCriteria() {
      ArrayList var1 = Lists.newArrayList();
      Iterator var2 = this.criteria.entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         if (!((CriterionProgress)var3.getValue()).isDone()) {
            var1.add(var3.getKey());
         }
      }

      return var1;
   }

   public Iterable getCompletedCriteria() {
      ArrayList var1 = Lists.newArrayList();
      Iterator var2 = this.criteria.entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         if (((CriterionProgress)var3.getValue()).isDone()) {
            var1.add(var3.getKey());
         }
      }

      return var1;
   }

   @Nullable
   public Date getFirstProgressDate() {
      Date var1 = null;
      Iterator var2 = this.criteria.values().iterator();

      while(true) {
         CriterionProgress var3;
         do {
            do {
               if (!var2.hasNext()) {
                  return var1;
               }

               var3 = (CriterionProgress)var2.next();
            } while(!var3.isDone());
         } while(var1 != null && !var3.getObtained().before(var1));

         var1 = var3.getObtained();
      }
   }

   public int compareTo(AdvancementProgress var1) {
      Date var2 = this.getFirstProgressDate();
      Date var3 = var1.getFirstProgressDate();
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

   public static class Serializer implements JsonDeserializer, JsonSerializer {
      public JsonElement serialize(AdvancementProgress var1, Type var2, JsonSerializationContext var3) {
         JsonObject var4 = new JsonObject();
         JsonObject var5 = new JsonObject();
         Iterator var6 = var1.criteria.entrySet().iterator();

         while(var6.hasNext()) {
            Entry var7 = (Entry)var6.next();
            CriterionProgress var8 = (CriterionProgress)var7.getValue();
            if (var8.isDone()) {
               var5.add((String)var7.getKey(), var8.serializeToJson());
            }
         }

         if (!var5.entrySet().isEmpty()) {
            var4.add("criteria", var5);
         }

         var4.addProperty("done", var1.isDone());
         return var4;
      }

      public AdvancementProgress deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var4 = GsonHelper.convertToJsonObject(var1, "advancement");
         JsonObject var5 = GsonHelper.getAsJsonObject(var4, "criteria", new JsonObject());
         AdvancementProgress var6 = new AdvancementProgress();
         Iterator var7 = var5.entrySet().iterator();

         while(var7.hasNext()) {
            Entry var8 = (Entry)var7.next();
            String var9 = (String)var8.getKey();
            var6.criteria.put(var9, CriterionProgress.fromJson(GsonHelper.convertToString((JsonElement)var8.getValue(), var9)));
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
