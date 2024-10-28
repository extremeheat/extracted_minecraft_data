package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

public class BlockElement {
   private static final boolean DEFAULT_RESCALE = false;
   private static final float MIN_EXTENT = -16.0F;
   private static final float MAX_EXTENT = 32.0F;
   public final Vector3f from;
   public final Vector3f to;
   public final Map<Direction, BlockElementFace> faces;
   public final BlockElementRotation rotation;
   public final boolean shade;

   public BlockElement(Vector3f var1, Vector3f var2, Map<Direction, BlockElementFace> var3, @Nullable BlockElementRotation var4, boolean var5) {
      super();
      this.from = var1;
      this.to = var2;
      this.faces = var3;
      this.rotation = var4;
      this.shade = var5;
      this.fillUvs();
   }

   private void fillUvs() {
      Iterator var1 = this.faces.entrySet().iterator();

      while(var1.hasNext()) {
         Map.Entry var2 = (Map.Entry)var1.next();
         float[] var3 = this.uvsByFace((Direction)var2.getKey());
         ((BlockElementFace)var2.getValue()).uv().setMissingUv(var3);
      }

   }

   private float[] uvsByFace(Direction var1) {
      switch (var1) {
         case DOWN:
            return new float[]{this.from.x(), 16.0F - this.to.z(), this.to.x(), 16.0F - this.from.z()};
         case UP:
            return new float[]{this.from.x(), this.from.z(), this.to.x(), this.to.z()};
         case NORTH:
         default:
            return new float[]{16.0F - this.to.x(), 16.0F - this.to.y(), 16.0F - this.from.x(), 16.0F - this.from.y()};
         case SOUTH:
            return new float[]{this.from.x(), 16.0F - this.to.y(), this.to.x(), 16.0F - this.from.y()};
         case WEST:
            return new float[]{this.from.z(), 16.0F - this.to.y(), this.to.z(), 16.0F - this.from.y()};
         case EAST:
            return new float[]{16.0F - this.to.z(), 16.0F - this.to.y(), 16.0F - this.from.z(), 16.0F - this.from.y()};
      }
   }

   protected static class Deserializer implements JsonDeserializer<BlockElement> {
      private static final boolean DEFAULT_SHADE = true;

      protected Deserializer() {
         super();
      }

      public BlockElement deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var4 = var1.getAsJsonObject();
         Vector3f var5 = this.getFrom(var4);
         Vector3f var6 = this.getTo(var4);
         BlockElementRotation var7 = this.getRotation(var4);
         Map var8 = this.getFaces(var3, var4);
         if (var4.has("shade") && !GsonHelper.isBooleanValue(var4, "shade")) {
            throw new JsonParseException("Expected shade to be a Boolean");
         } else {
            boolean var9 = GsonHelper.getAsBoolean(var4, "shade", true);
            return new BlockElement(var5, var6, var8, var7, var9);
         }
      }

      @Nullable
      private BlockElementRotation getRotation(JsonObject var1) {
         BlockElementRotation var2 = null;
         if (var1.has("rotation")) {
            JsonObject var3 = GsonHelper.getAsJsonObject(var1, "rotation");
            Vector3f var4 = this.getVector3f(var3, "origin");
            var4.mul(0.0625F);
            Direction.Axis var5 = this.getAxis(var3);
            float var6 = this.getAngle(var3);
            boolean var7 = GsonHelper.getAsBoolean(var3, "rescale", false);
            var2 = new BlockElementRotation(var4, var5, var6, var7);
         }

         return var2;
      }

      private float getAngle(JsonObject var1) {
         float var2 = GsonHelper.getAsFloat(var1, "angle");
         if (var2 != 0.0F && Mth.abs(var2) != 22.5F && Mth.abs(var2) != 45.0F) {
            throw new JsonParseException("Invalid rotation " + var2 + " found, only -45/-22.5/0/22.5/45 allowed");
         } else {
            return var2;
         }
      }

      private Direction.Axis getAxis(JsonObject var1) {
         String var2 = GsonHelper.getAsString(var1, "axis");
         Direction.Axis var3 = Direction.Axis.byName(var2.toLowerCase(Locale.ROOT));
         if (var3 == null) {
            throw new JsonParseException("Invalid rotation axis: " + var2);
         } else {
            return var3;
         }
      }

      private Map<Direction, BlockElementFace> getFaces(JsonDeserializationContext var1, JsonObject var2) {
         Map var3 = this.filterNullFromFaces(var1, var2);
         if (var3.isEmpty()) {
            throw new JsonParseException("Expected between 1 and 6 unique faces, got 0");
         } else {
            return var3;
         }
      }

      private Map<Direction, BlockElementFace> filterNullFromFaces(JsonDeserializationContext var1, JsonObject var2) {
         EnumMap var3 = Maps.newEnumMap(Direction.class);
         JsonObject var4 = GsonHelper.getAsJsonObject(var2, "faces");
         Iterator var5 = var4.entrySet().iterator();

         while(var5.hasNext()) {
            Map.Entry var6 = (Map.Entry)var5.next();
            Direction var7 = this.getFacing((String)var6.getKey());
            var3.put(var7, (BlockElementFace)var1.deserialize((JsonElement)var6.getValue(), BlockElementFace.class));
         }

         return var3;
      }

      private Direction getFacing(String var1) {
         Direction var2 = Direction.byName(var1);
         if (var2 == null) {
            throw new JsonParseException("Unknown facing: " + var1);
         } else {
            return var2;
         }
      }

      private Vector3f getTo(JsonObject var1) {
         Vector3f var2 = this.getVector3f(var1, "to");
         if (!(var2.x() < -16.0F) && !(var2.y() < -16.0F) && !(var2.z() < -16.0F) && !(var2.x() > 32.0F) && !(var2.y() > 32.0F) && !(var2.z() > 32.0F)) {
            return var2;
         } else {
            throw new JsonParseException("'to' specifier exceeds the allowed boundaries: " + String.valueOf(var2));
         }
      }

      private Vector3f getFrom(JsonObject var1) {
         Vector3f var2 = this.getVector3f(var1, "from");
         if (!(var2.x() < -16.0F) && !(var2.y() < -16.0F) && !(var2.z() < -16.0F) && !(var2.x() > 32.0F) && !(var2.y() > 32.0F) && !(var2.z() > 32.0F)) {
            return var2;
         } else {
            throw new JsonParseException("'from' specifier exceeds the allowed boundaries: " + String.valueOf(var2));
         }
      }

      private Vector3f getVector3f(JsonObject var1, String var2) {
         JsonArray var3 = GsonHelper.getAsJsonArray(var1, var2);
         if (var3.size() != 3) {
            throw new JsonParseException("Expected 3 " + var2 + " values, found: " + var3.size());
         } else {
            float[] var4 = new float[3];

            for(int var5 = 0; var5 < var4.length; ++var5) {
               var4[var5] = GsonHelper.convertToFloat(var3.get(var5), var2 + "[" + var5 + "]");
            }

            return new Vector3f(var4[0], var4[1], var4[2]);
         }
      }

      // $FF: synthetic method
      public Object deserialize(final JsonElement var1, final Type var2, final JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
