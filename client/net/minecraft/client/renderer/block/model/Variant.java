package net.minecraft.client.renderer.block.model;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.math.Transformation;
import java.lang.reflect.Type;
import java.util.Objects;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class Variant implements ModelState {
   private final ResourceLocation modelLocation;
   private final Transformation rotation;
   private final boolean uvLock;
   private final int weight;

   public Variant(ResourceLocation var1, Transformation var2, boolean var3, int var4) {
      super();
      this.modelLocation = var1;
      this.rotation = var2;
      this.uvLock = var3;
      this.weight = var4;
   }

   public ResourceLocation getModelLocation() {
      return this.modelLocation;
   }

   @Override
   public Transformation getRotation() {
      return this.rotation;
   }

   @Override
   public boolean isUvLocked() {
      return this.uvLock;
   }

   public int getWeight() {
      return this.weight;
   }

   @Override
   public String toString() {
      return "Variant{modelLocation=" + this.modelLocation + ", rotation=" + this.rotation + ", uvLock=" + this.uvLock + ", weight=" + this.weight + "}";
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return !(var1 instanceof Variant var2)
            ? false
            : this.modelLocation.equals(var2.modelLocation)
               && Objects.equals(this.rotation, var2.rotation)
               && this.uvLock == var2.uvLock
               && this.weight == var2.weight;
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.modelLocation.hashCode();
      var1 = 31 * var1 + this.rotation.hashCode();
      var1 = 31 * var1 + Boolean.valueOf(this.uvLock).hashCode();
      return 31 * var1 + this.weight;
   }

   public static class Deserializer implements JsonDeserializer<Variant> {
      @VisibleForTesting
      static final boolean DEFAULT_UVLOCK = false;
      @VisibleForTesting
      static final int DEFAULT_WEIGHT = 1;
      @VisibleForTesting
      static final int DEFAULT_X_ROTATION = 0;
      @VisibleForTesting
      static final int DEFAULT_Y_ROTATION = 0;

      public Deserializer() {
         super();
      }

      public Variant deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var4 = var1.getAsJsonObject();
         ResourceLocation var5 = this.getModel(var4);
         BlockModelRotation var6 = this.getBlockRotation(var4);
         boolean var7 = this.getUvLock(var4);
         int var8 = this.getWeight(var4);
         return new Variant(var5, var6.getRotation(), var7, var8);
      }

      private boolean getUvLock(JsonObject var1) {
         return GsonHelper.getAsBoolean(var1, "uvlock", false);
      }

      protected BlockModelRotation getBlockRotation(JsonObject var1) {
         int var2 = GsonHelper.getAsInt(var1, "x", 0);
         int var3 = GsonHelper.getAsInt(var1, "y", 0);
         BlockModelRotation var4 = BlockModelRotation.by(var2, var3);
         if (var4 == null) {
            throw new JsonParseException("Invalid BlockModelRotation x: " + var2 + ", y: " + var3);
         } else {
            return var4;
         }
      }

      protected ResourceLocation getModel(JsonObject var1) {
         return ResourceLocation.parse(GsonHelper.getAsString(var1, "model"));
      }

      protected int getWeight(JsonObject var1) {
         int var2 = GsonHelper.getAsInt(var1, "weight", 1);
         if (var2 < 1) {
            throw new JsonParseException("Invalid weight " + var2 + " found, expected integer >= 1");
         } else {
            return var2;
         }
      }
   }
}
