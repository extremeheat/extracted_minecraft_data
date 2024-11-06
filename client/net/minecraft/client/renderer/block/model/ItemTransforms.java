package net.minecraft.client.renderer.block.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.world.item.ItemDisplayContext;

public record ItemTransforms(ItemTransform thirdPersonLeftHand, ItemTransform thirdPersonRightHand, ItemTransform firstPersonLeftHand, ItemTransform firstPersonRightHand, ItemTransform head, ItemTransform gui, ItemTransform ground, ItemTransform fixed) {
   public static final ItemTransforms NO_TRANSFORMS;

   public ItemTransforms(ItemTransform var1, ItemTransform var2, ItemTransform var3, ItemTransform var4, ItemTransform var5, ItemTransform var6, ItemTransform var7, ItemTransform var8) {
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
      ItemTransform var10000;
      switch (var1) {
         case THIRD_PERSON_LEFT_HAND -> var10000 = this.thirdPersonLeftHand;
         case THIRD_PERSON_RIGHT_HAND -> var10000 = this.thirdPersonRightHand;
         case FIRST_PERSON_LEFT_HAND -> var10000 = this.firstPersonLeftHand;
         case FIRST_PERSON_RIGHT_HAND -> var10000 = this.firstPersonRightHand;
         case HEAD -> var10000 = this.head;
         case GUI -> var10000 = this.gui;
         case GROUND -> var10000 = this.ground;
         case FIXED -> var10000 = this.fixed;
         default -> var10000 = ItemTransform.NO_TRANSFORM;
      }

      return var10000;
   }

   public ItemTransform thirdPersonLeftHand() {
      return this.thirdPersonLeftHand;
   }

   public ItemTransform thirdPersonRightHand() {
      return this.thirdPersonRightHand;
   }

   public ItemTransform firstPersonLeftHand() {
      return this.firstPersonLeftHand;
   }

   public ItemTransform firstPersonRightHand() {
      return this.firstPersonRightHand;
   }

   public ItemTransform head() {
      return this.head;
   }

   public ItemTransform gui() {
      return this.gui;
   }

   public ItemTransform ground() {
      return this.ground;
   }

   public ItemTransform fixed() {
      return this.fixed;
   }

   static {
      NO_TRANSFORMS = new ItemTransforms(ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM);
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

      // $FF: synthetic method
      public Object deserialize(final JsonElement var1, final Type var2, final JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
