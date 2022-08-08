package net.minecraft.advancements;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;

public class CriterionProgress {
   private static final SimpleDateFormat DATE_FORMAT;
   @Nullable
   private Date obtained;

   public CriterionProgress() {
      super();
   }

   public boolean isDone() {
      return this.obtained != null;
   }

   public void grant() {
      this.obtained = new Date();
   }

   public void revoke() {
      this.obtained = null;
   }

   @Nullable
   public Date getObtained() {
      return this.obtained;
   }

   public String toString() {
      Object var10000 = this.obtained == null ? "false" : this.obtained;
      return "CriterionProgress{obtained=" + var10000 + "}";
   }

   public void serializeToNetwork(FriendlyByteBuf var1) {
      var1.writeNullable(this.obtained, FriendlyByteBuf::writeDate);
   }

   public JsonElement serializeToJson() {
      return (JsonElement)(this.obtained != null ? new JsonPrimitive(DATE_FORMAT.format(this.obtained)) : JsonNull.INSTANCE);
   }

   public static CriterionProgress fromNetwork(FriendlyByteBuf var0) {
      CriterionProgress var1 = new CriterionProgress();
      var1.obtained = (Date)var0.readNullable(FriendlyByteBuf::readDate);
      return var1;
   }

   public static CriterionProgress fromJson(String var0) {
      CriterionProgress var1 = new CriterionProgress();

      try {
         var1.obtained = DATE_FORMAT.parse(var0);
         return var1;
      } catch (ParseException var3) {
         throw new JsonSyntaxException("Invalid datetime: " + var0, var3);
      }
   }

   static {
      DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.ROOT);
   }
}
