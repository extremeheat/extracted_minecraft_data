package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
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

   public BlockElement(final Vector3fc from, final Vector3fc to, final Map<Direction, BlockElementFace> faces) {
      this(from, to, faces, (BlockElementRotation)null, true, 0);
   }

   public BlockElement {
      super();
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

      public BlockElement deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
         JsonObject object = json.getAsJsonObject();
         Vector3f from = getPosition(object, "from");
         Vector3f to = getPosition(object, "to");
         BlockElementRotation rotation = this.getRotation(object);
         Map<Direction, BlockElementFace> faces = this.getFaces(context, object);
         if (object.has("shade") && !GsonHelper.isBooleanValue(object, "shade")) {
            throw new JsonParseException("Expected 'shade' to be a Boolean");
         } else {
            boolean shade = GsonHelper.getAsBoolean(object, "shade", true);
            int lightEmission = 0;
            if (object.has("light_emission")) {
               boolean isNumber = GsonHelper.isNumberValue(object, "light_emission");
               if (isNumber) {
                  lightEmission = GsonHelper.getAsInt(object, "light_emission");
               }

               if (!isNumber || lightEmission < 0 || lightEmission > 15) {
                  throw new JsonParseException("Expected 'light_emission' to be an Integer between (inclusive) 0 and 15");
               }
            }

            return new BlockElement(from, to, faces, rotation, shade, lightEmission);
         }
      }

      private @Nullable BlockElementRotation getRotation(final JsonObject object) {
         if (!object.has("rotation")) {
            return null;
         } else {
            JsonObject rotationObject = GsonHelper.getAsJsonObject(object, "rotation");
            Vector3f origin = getVector3f(rotationObject, "origin");
            origin.mul(0.0625F);
            BlockElementRotation.RotationValue rotationValue;
            if (!rotationObject.has("axis") && !rotationObject.has("angle")) {
               if (!rotationObject.has("x") && !rotationObject.has("y") && !rotationObject.has("z")) {
                  throw new JsonParseException("Missing rotation value, expected either 'axis' and 'angle' or 'x', 'y' and 'z'");
               }

               float x = GsonHelper.getAsFloat(rotationObject, "x", 0.0F);
               float y = GsonHelper.getAsFloat(rotationObject, "y", 0.0F);
               float z = GsonHelper.getAsFloat(rotationObject, "z", 0.0F);
               rotationValue = new BlockElementRotation.EulerXYZRotation(x, y, z);
            } else {
               Direction.Axis axis = this.getAxis(rotationObject);
               float angle = GsonHelper.getAsFloat(rotationObject, "angle");
               rotationValue = new BlockElementRotation.SingleAxisRotation(axis, angle);
            }

            boolean rescale = GsonHelper.getAsBoolean(rotationObject, "rescale", false);
            return new BlockElementRotation(origin, rotationValue, rescale);
         }
      }

      private Direction.Axis getAxis(final JsonObject object) {
         String axisName = GsonHelper.getAsString(object, "axis");
         Direction.Axis axis = Direction.Axis.byName(axisName.toLowerCase(Locale.ROOT));
         if (axis == null) {
            throw new JsonParseException("Invalid rotation axis: " + axisName);
         } else {
            return axis;
         }
      }

      private Map<Direction, BlockElementFace> getFaces(final JsonDeserializationContext context, final JsonObject object) {
         Map<Direction, BlockElementFace> faces = this.filterNullFromFaces(context, object);
         if (faces.isEmpty()) {
            throw new JsonParseException("Expected between 1 and 6 unique faces, got 0");
         } else {
            return faces;
         }
      }

      private Map<Direction, BlockElementFace> filterNullFromFaces(final JsonDeserializationContext context, final JsonObject object) {
         Map<Direction, BlockElementFace> result = Maps.newEnumMap(Direction.class);
         JsonObject faceObjects = GsonHelper.getAsJsonObject(object, "faces");

         for(Map.Entry<String, JsonElement> entry : faceObjects.entrySet()) {
            Direction direction = this.getFacing((String)entry.getKey());
            result.put(direction, (BlockElementFace)context.deserialize((JsonElement)entry.getValue(), BlockElementFace.class));
         }

         return result;
      }

      private Direction getFacing(final String name) {
         Direction direction = Direction.byName(name);
         if (direction == null) {
            throw new JsonParseException("Unknown facing: " + name);
         } else {
            return direction;
         }
      }

      private static Vector3f getPosition(final JsonObject object, final String key) {
         Vector3f from = getVector3f(object, key);
         if (!(from.x() < -16.0F) && !(from.y() < -16.0F) && !(from.z() < -16.0F) && !(from.x() > 32.0F) && !(from.y() > 32.0F) && !(from.z() > 32.0F)) {
            return from;
         } else {
            throw new JsonParseException("'" + key + "' specifier exceeds the allowed boundaries: " + String.valueOf(from));
         }
      }

      private static Vector3f getVector3f(final JsonObject object, final String key) {
         JsonArray vecArray = GsonHelper.getAsJsonArray(object, key);
         if (vecArray.size() != 3) {
            throw new JsonParseException("Expected 3 " + key + " values, found: " + vecArray.size());
         } else {
            float[] elements = new float[3];

            for(int i = 0; i < elements.length; ++i) {
               elements[i] = GsonHelper.convertToFloat(vecArray.get(i), key + "[" + i + "]");
            }

            return new Vector3f(elements[0], elements[1], elements[2]);
         }
      }
   }
}
