package net.minecraft.client.renderer.block.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.JsonUtils;

public class BlockPartFace {
   public static final EnumFacing field_178246_a = null;
   public final EnumFacing field_178244_b;
   public final int field_178245_c;
   public final String field_178242_d;
   public final BlockFaceUV field_178243_e;

   public BlockPartFace(EnumFacing var1, int var2, String var3, BlockFaceUV var4) {
      super();
      this.field_178244_b = var1;
      this.field_178245_c = var2;
      this.field_178242_d = var3;
      this.field_178243_e = var4;
   }

   static class Deserializer implements JsonDeserializer<BlockPartFace> {
      Deserializer() {
         super();
      }

      public BlockPartFace deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var4 = var1.getAsJsonObject();
         EnumFacing var5 = this.func_178339_c(var4);
         int var6 = this.func_178337_a(var4);
         String var7 = this.func_178340_b(var4);
         BlockFaceUV var8 = (BlockFaceUV)var3.deserialize(var4, BlockFaceUV.class);
         return new BlockPartFace(var5, var6, var7, var8);
      }

      protected int func_178337_a(JsonObject var1) {
         return JsonUtils.func_151208_a(var1, "tintindex", -1);
      }

      private String func_178340_b(JsonObject var1) {
         return JsonUtils.func_151200_h(var1, "texture");
      }

      private EnumFacing func_178339_c(JsonObject var1) {
         String var2 = JsonUtils.func_151219_a(var1, "cullface", "");
         return EnumFacing.func_176739_a(var2);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
