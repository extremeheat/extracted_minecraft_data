package net.minecraft.server.management;

import com.google.gson.JsonObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class BanEntry<T> extends UserListEntry<T> {
   public static final SimpleDateFormat field_73698_a = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
   protected final Date field_73694_d;
   protected final String field_73695_e;
   protected final Date field_73692_f;
   protected final String field_73693_g;

   public BanEntry(T var1, Date var2, String var3, Date var4, String var5) {
      super(var1);
      this.field_73694_d = var2 == null ? new Date() : var2;
      this.field_73695_e = var3 == null ? "(Unknown)" : var3;
      this.field_73692_f = var4;
      this.field_73693_g = var5 == null ? "Banned by an operator." : var5;
   }

   protected BanEntry(T var1, JsonObject var2) {
      super(var1, var2);

      Date var3;
      try {
         var3 = var2.has("created") ? field_73698_a.parse(var2.get("created").getAsString()) : new Date();
      } catch (ParseException var7) {
         var3 = new Date();
      }

      this.field_73694_d = var3;
      this.field_73695_e = var2.has("source") ? var2.get("source").getAsString() : "(Unknown)";

      Date var4;
      try {
         var4 = var2.has("expires") ? field_73698_a.parse(var2.get("expires").getAsString()) : null;
      } catch (ParseException var6) {
         var4 = null;
      }

      this.field_73692_f = var4;
      this.field_73693_g = var2.has("reason") ? var2.get("reason").getAsString() : "Banned by an operator.";
   }

   public Date func_73680_d() {
      return this.field_73692_f;
   }

   public String func_73686_f() {
      return this.field_73693_g;
   }

   boolean func_73682_e() {
      return this.field_73692_f == null ? false : this.field_73692_f.before(new Date());
   }

   protected void func_152641_a(JsonObject var1) {
      var1.addProperty("created", field_73698_a.format(this.field_73694_d));
      var1.addProperty("source", this.field_73695_e);
      var1.addProperty("expires", this.field_73692_f == null ? "forever" : field_73698_a.format(this.field_73692_f));
      var1.addProperty("reason", this.field_73693_g);
   }
}
