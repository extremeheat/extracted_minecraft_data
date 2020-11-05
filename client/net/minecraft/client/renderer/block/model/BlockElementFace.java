package net.minecraft.client.renderer.block.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.util.GsonHelper;

public class BlockElementFace {
   public final Direction cullForDirection;
   public final int tintIndex;
   public final String texture;
   public final BlockFaceUV uv;

   public BlockElementFace(@Nullable Direction var1, int var2, String var3, BlockFaceUV var4) {
      super();
      this.cullForDirection = var1;
      this.tintIndex = var2;
      this.texture = var3;
      this.uv = var4;
   }

   public static class Deserializer implements JsonDeserializer<BlockElementFace> {
      protected Deserializer() {
         super();
      }

      public BlockElementFace deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var4 = var1.getAsJsonObject();
         Direction var5 = this.getCullFacing(var4);
         int var6 = this.getTintIndex(var4);
         String var7 = this.getTexture(var4);
         BlockFaceUV var8 = (BlockFaceUV)var3.deserialize(var4, BlockFaceUV.class);
         return new BlockElementFace(var5, var6, var7, var8);
      }

      protected int getTintIndex(JsonObject var1) {
         return GsonHelper.getAsInt(var1, "tintindex", -1);
      }

      private String getTexture(JsonObject var1) {
         return GsonHelper.getAsString(var1, "texture");
      }

      @Nullable
      private Direction getCullFacing(JsonObject var1) {
         String var2 = GsonHelper.getAsString(var1, "cullface", "");
         return Direction.byName(var2);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
