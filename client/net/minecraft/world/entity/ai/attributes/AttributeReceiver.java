package net.minecraft.world.entity.ai.attributes;

public interface AttributeReceiver {
   AttributeReceiver EMPTY = (var0) -> {
   };

   void onAttributeModified(AttributeInstance var1);
}
