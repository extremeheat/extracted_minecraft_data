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
import java.util.Locale;
import java.util.Map;
import net.minecraft.core.Direction;
import net.minecraft.util.GsonHelper;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

public record BlockElement(Vector3fc from, Vector3fc to, Map<Direction, BlockElementFace> faces, @Nullable BlockElementRotation rotation, boolean shade, int lightEmission) {
   private static final boolean DEFAULT_RESCALE = false;
   private static final float MIN_EXTENT = -16.0F;
   private static final float MAX_EXTENT = 32.0F;

   public BlockElement(Vector3fc var1, Vector3fc var2, Map<Direction, BlockElementFace> var3) {
      this(var1, var2, var3, (BlockElementRotation)null, true, 0);
   }

   public BlockElement(Vector3fc var1, Vector3fc var2, Map<Direction, BlockElementFace> var3, @Nullable BlockElementRotation var4, boolean var5, int var6) {
      super();
      this.from = var1;
      this.to = var2;
      this.faces = var3;
      this.rotation = var4;
      this.shade = var5;
      this.lightEmission = var6;
   }

   protected static class Deserializer implements JsonDeserializer<BlockElement> {
      private static final boolean DEFAULT_SHADE = true;
      private static final int DEFAULT_LIGHT_EMISSION = 0;
      private static final String FIELD_SHADE = "shade";
      private static final String FIELD_LIGHT_EMISSION = "light_emission";
      private static final String FIELD_ROTATION = "rotation";
      private static final String FIELD_ORIGIN = "origin";
      private static final String FIELD_ANGLE = "angle";
      private static final String FIELD_X = "x";
      private static final String FIELD_Y = "y";
      private static final String FIELD_Z = "z";
      private static final String FIELD_AXIS = "axis";
      private static final String FIELD_RESCALE = "rescale";
      private static final String FIELD_FACES = "faces";
      private static final String FIELD_TO = "to";
      private static final String FIELD_FROM = "from";

      protected Deserializer() {
         super();
      }

      public BlockElement deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var4 = var1.getAsJsonObject();
         Vector3f var5 = getPosition(var4, "from");
         Vector3f var6 = getPosition(var4, "to");
         BlockElementRotation var7 = this.getRotation(var4);
         Map var8 = this.getFaces(var3, var4);
         if (var4.has("shade") && !GsonHelper.isBooleanValue(var4, "shade")) {
            throw new JsonParseException("Expected 'shade' to be a Boolean");
         } else {
            boolean var9 = GsonHelper.getAsBoolean(var4, "shade", true);
            int var10 = 0;
            if (var4.has("light_emission")) {
               boolean var11 = GsonHelper.isNumberValue(var4, "light_emission");
               if (var11) {
                  var10 = GsonHelper.getAsInt(var4, "light_emission");
               }

               if (!var11 || var10 < 0 || var10 > 15) {
                  throw new JsonParseException("Expected 'light_emission' to be an Integer between (inclusive) 0 and 15");
               }
            }

            return new BlockElement(var5, var6, var8, var7, var9, var10);
         }
      }

      private @Nullable BlockElementRotation getRotation(JsonObject var1) {
         if (!var1.has("rotation")) {
            return null;
         } else {
            JsonObject var2 = GsonHelper.getAsJsonObject(var1, "rotation");
            Vector3f var3 = getVector3f(var2, "origin");
            var3.mul(0.0625F);
            Object var4;
            if (!var2.has("axis") && !var2.has("angle")) {
               if (!var2.has("x") && !var2.has("y") && !var2.has("z")) {
                  throw new JsonParseException("Missing rotation value, expected either 'axis' and 'angle' or 'x', 'y' and 'z'");
               }

               float var8 = GsonHelper.getAsFloat(var2, "x", 0.0F);
               float var10 = GsonHelper.getAsFloat(var2, "y", 0.0F);
               float var7 = GsonHelper.getAsFloat(var2, "z", 0.0F);
               var4 = new BlockElementRotation.EulerXYZRotation(var8, var10, var7);
            } else {
               Direction.Axis var5 = this.getAxis(var2);
               float var6 = GsonHelper.getAsFloat(var2, "angle");
               var4 = new BlockElementRotation.SingleAxisRotation(var5, var6);
            }

            boolean var9 = GsonHelper.getAsBoolean(var2, "rescale", false);
            return new BlockElementRotation(var3, (BlockElementRotation.RotationValue)var4, var9);
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

         for(Map.Entry var6 : var4.entrySet()) {
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

      private static Vector3f getPosition(JsonObject var0, String var1) {
         Vector3f var2 = getVector3f(var0, var1);
         if (!(var2.x() < -16.0F) && !(var2.y() < -16.0F) && !(var2.z() < -16.0F) && !(var2.x() > 32.0F) && !(var2.y() > 32.0F) && !(var2.z() > 32.0F)) {
            return var2;
         } else {
            throw new JsonParseException("'" + var1 + "' specifier exceeds the allowed boundaries: " + String.valueOf(var2));
         }
      }

      private static Vector3f getVector3f(JsonObject var0, String var1) {
         JsonArray var2 = GsonHelper.getAsJsonArray(var0, var1);
         if (var2.size() != 3) {
            throw new JsonParseException("Expected 3 " + var1 + " values, found: " + var2.size());
         } else {
            float[] var3 = new float[3];

            for(int var4 = 0; var4 < var3.length; ++var4) {
               var3[var4] = GsonHelper.convertToFloat(var2.get(var4), var1 + "[" + var4 + "]");
            }

            return new Vector3f(var3[0], var3[1], var3[2]);
         }
      }

      // $FF: synthetic method
      public Object deserialize(final JsonElement var1, final Type var2, final JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
