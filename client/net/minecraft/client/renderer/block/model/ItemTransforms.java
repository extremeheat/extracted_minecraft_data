package net.minecraft.client.renderer.block.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.world.item.ItemDisplayContext;

public class ItemTransforms {
   public static final ItemTransforms NO_TRANSFORMS = new ItemTransforms();
   public final ItemTransform thirdPersonLeftHand;
   public final ItemTransform thirdPersonRightHand;
   public final ItemTransform firstPersonLeftHand;
   public final ItemTransform firstPersonRightHand;
   public final ItemTransform head;
   public final ItemTransform gui;
   public final ItemTransform ground;
   public final ItemTransform fixed;

   private ItemTransforms() {
      this(
         ItemTransform.NO_TRANSFORM,
         ItemTransform.NO_TRANSFORM,
         ItemTransform.NO_TRANSFORM,
         ItemTransform.NO_TRANSFORM,
         ItemTransform.NO_TRANSFORM,
         ItemTransform.NO_TRANSFORM,
         ItemTransform.NO_TRANSFORM,
         ItemTransform.NO_TRANSFORM
      );
   }

   public ItemTransforms(ItemTransforms var1) {
      super();
      this.thirdPersonLeftHand = var1.thirdPersonLeftHand;
      this.thirdPersonRightHand = var1.thirdPersonRightHand;
      this.firstPersonLeftHand = var1.firstPersonLeftHand;
      this.firstPersonRightHand = var1.firstPersonRightHand;
      this.head = var1.head;
      this.gui = var1.gui;
      this.ground = var1.ground;
      this.fixed = var1.fixed;
   }

   public ItemTransforms(
      ItemTransform var1,
      ItemTransform var2,
      ItemTransform var3,
      ItemTransform var4,
      ItemTransform var5,
      ItemTransform var6,
      ItemTransform var7,
      ItemTransform var8
   ) {
      super();
      this.thirdPersonLeftHand = var1;
      this.thirdPersonRightHand = var2;
      this.firstPersonLeftHand = var3;
      this.firstPersonRightHand = var4;
      this.head = var5;
      this.gui = var6;
      this.ground = var7;
      this.fixed = var8;
   }

   public ItemTransform getTransform(ItemDisplayContext var1) {
      return switch(var1) {
         case THIRD_PERSON_LEFT_HAND -> this.thirdPersonLeftHand;
         case THIRD_PERSON_RIGHT_HAND -> this.thirdPersonRightHand;
         case FIRST_PERSON_LEFT_HAND -> this.firstPersonLeftHand;
         case FIRST_PERSON_RIGHT_HAND -> this.firstPersonRightHand;
         case HEAD -> this.head;
         case GUI -> this.gui;
         case GROUND -> this.ground;
         case FIXED -> this.fixed;
         default -> ItemTransform.NO_TRANSFORM;
      };
   }

   public boolean hasTransform(ItemDisplayContext var1) {
      return this.getTransform(var1) != ItemTransform.NO_TRANSFORM;
   }

   protected static class Deserializer implements JsonDeserializer<ItemTransforms> {
      protected Deserializer() {
         super();
      }

      public ItemTransforms deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var4 = var1.getAsJsonObject();
         ItemTransform var5 = this.getTransform(var3, var4, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND);
         ItemTransform var6 = this.getTransform(var3, var4, ItemDisplayContext.THIRD_PERSON_LEFT_HAND);
         if (var6 == ItemTransform.NO_TRANSFORM) {
            var6 = var5;
         }

         ItemTransform var7 = this.getTransform(var3, var4, ItemDisplayContext.FIRST_PERSON_RIGHT_HAND);
         ItemTransform var8 = this.getTransform(var3, var4, ItemDisplayContext.FIRST_PERSON_LEFT_HAND);
         if (var8 == ItemTransform.NO_TRANSFORM) {
            var8 = var7;
         }

         ItemTransform var9 = this.getTransform(var3, var4, ItemDisplayContext.HEAD);
         ItemTransform var10 = this.getTransform(var3, var4, ItemDisplayContext.GUI);
         ItemTransform var11 = this.getTransform(var3, var4, ItemDisplayContext.GROUND);
         ItemTransform var12 = this.getTransform(var3, var4, ItemDisplayContext.FIXED);
         return new ItemTransforms(var6, var5, var8, var7, var9, var10, var11, var12);
      }

      private ItemTransform getTransform(JsonDeserializationContext var1, JsonObject var2, ItemDisplayContext var3) {
         String var4 = var3.getSerializedName();
         return var2.has(var4) ? (ItemTransform)var1.deserialize(var2.get(var4), ItemTransform.class) : ItemTransform.NO_TRANSFORM;
      }
   }
}