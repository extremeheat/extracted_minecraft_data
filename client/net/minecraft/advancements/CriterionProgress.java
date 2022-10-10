package net.minecraft.advancements;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.minecraft.network.PacketBuffer;

public class CriterionProgress {
   private static final SimpleDateFormat field_192155_a = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
   private Date field_192157_c;

   public CriterionProgress() {
      super();
   }

   public boolean func_192151_a() {
      return this.field_192157_c != null;
   }

   public void func_192153_b() {
      this.field_192157_c = new Date();
   }

   public void func_192154_c() {
      this.field_192157_c = null;
   }

   public Date func_193140_d() {
      return this.field_192157_c;
   }

   public String toString() {
      return "CriterionProgress{obtained=" + (this.field_192157_c == null ? "false" : this.field_192157_c) + '}';
   }

   public void func_192150_a(PacketBuffer var1) {
      var1.writeBoolean(this.field_192157_c != null);
      if (this.field_192157_c != null) {
         var1.func_192574_a(this.field_192157_c);
      }

   }

   public JsonElement func_192148_e() {
      return (JsonElement)(this.field_192157_c != null ? new JsonPrimitive(field_192155_a.format(this.field_192157_c)) : JsonNull.INSTANCE);
   }

   public static CriterionProgress func_192149_a(PacketBuffer var0) {
      CriterionProgress var1 = new CriterionProgress();
      if (var0.readBoolean()) {
         var1.field_192157_c = var0.func_192573_m();
      }

      return var1;
   }

   public static CriterionProgress func_209541_a(String var0) {
      CriterionProgress var1 = new CriterionProgress();

      try {
         var1.field_192157_c = field_192155_a.parse(var0);
         return var1;
      } catch (ParseException var3) {
         throw new JsonSyntaxException("Invalid datetime: " + var0, var3);
      }
   }
}
