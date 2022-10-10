package net.minecraft.resources.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.text.ITextComponent;

public class PackMetadataSectionSerializer implements IMetadataSectionSerializer<PackMetadataSection> {
   public PackMetadataSectionSerializer() {
      super();
   }

   public PackMetadataSection func_195812_a(JsonObject var1) {
      ITextComponent var2 = ITextComponent.Serializer.func_197672_a(var1.get("description"));
      if (var2 == null) {
         throw new JsonParseException("Invalid/missing description!");
      } else {
         int var3 = JsonUtils.func_151203_m(var1, "pack_format");
         return new PackMetadataSection(var2, var3);
      }
   }

   public String func_110483_a() {
      return "pack";
   }

   // $FF: synthetic method
   public Object func_195812_a(JsonObject var1) {
      return this.func_195812_a(var1);
   }
}
