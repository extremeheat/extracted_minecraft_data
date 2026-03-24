package net.minecraft.client.resources.model.cuboid;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.world.item.ItemDisplayContext;

public record ItemTransforms(ItemTransform thirdPersonLeftHand, ItemTransform thirdPersonRightHand, ItemTransform firstPersonLeftHand, ItemTransform firstPersonRightHand, ItemTransform head, ItemTransform gui, ItemTransform ground, ItemTransform fixed, ItemTransform fixedFromBottom) {
   public static final ItemTransforms NO_TRANSFORMS;

   public ItemTransforms {
      super();
   }

   public ItemTransform getTransform(final ItemDisplayContext type) {
      ItemTransform var10000;
      switch (type) {
         case THIRD_PERSON_LEFT_HAND -> var10000 = this.thirdPersonLeftHand;
         case THIRD_PERSON_RIGHT_HAND -> var10000 = this.thirdPersonRightHand;
         case FIRST_PERSON_LEFT_HAND -> var10000 = this.firstPersonLeftHand;
         case FIRST_PERSON_RIGHT_HAND -> var10000 = this.firstPersonRightHand;
         case HEAD -> var10000 = this.head;
         case GUI -> var10000 = this.gui;
         case GROUND -> var10000 = this.ground;
         case FIXED -> var10000 = this.fixed;
         case ON_SHELF -> var10000 = this.fixedFromBottom;
         default -> var10000 = ItemTransform.NO_TRANSFORM;
      }

      return var10000;
   }

   static {
      NO_TRANSFORMS = new ItemTransforms(ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM);
   }

   protected static class Deserializer implements JsonDeserializer<ItemTransforms> {
      protected Deserializer() {
         super();
      }

      public ItemTransforms deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
         JsonObject object = json.getAsJsonObject();
         ItemTransform thirdPersonRightHand = this.getTransform(context, object, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND);
         ItemTransform thirdPersonLeftHand = this.getTransform(context, object, ItemDisplayContext.THIRD_PERSON_LEFT_HAND);
         if (thirdPersonLeftHand == ItemTransform.NO_TRANSFORM) {
            thirdPersonLeftHand = thirdPersonRightHand;
         }

         ItemTransform firstPersonRightHand = this.getTransform(context, object, ItemDisplayContext.FIRST_PERSON_RIGHT_HAND);
         ItemTransform firstPersonLeftHand = this.getTransform(context, object, ItemDisplayContext.FIRST_PERSON_LEFT_HAND);
         if (firstPersonLeftHand == ItemTransform.NO_TRANSFORM) {
            firstPersonLeftHand = firstPersonRightHand;
         }

         ItemTransform head = this.getTransform(context, object, ItemDisplayContext.HEAD);
         ItemTransform gui = this.getTransform(context, object, ItemDisplayContext.GUI);
         ItemTransform ground = this.getTransform(context, object, ItemDisplayContext.GROUND);
         ItemTransform fixed = this.getTransform(context, object, ItemDisplayContext.FIXED);
         ItemTransform fixedFromBottom = this.getTransform(context, object, ItemDisplayContext.ON_SHELF);
         return new ItemTransforms(thirdPersonLeftHand, thirdPersonRightHand, firstPersonLeftHand, firstPersonRightHand, head, gui, ground, fixed, fixedFromBottom);
      }

      private ItemTransform getTransform(final JsonDeserializationContext context, final JsonObject object, final ItemDisplayContext transform) {
         String name = transform.getSerializedName();
         return object.has(name) ? (ItemTransform)context.deserialize(object.get(name), ItemTransform.class) : ItemTransform.NO_TRANSFORM;
      }
   }
}
