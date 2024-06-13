package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public abstract class ValueObject {
   public ValueObject() {
      super();
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder("{");

      for (Field var5 : this.getClass().getFields()) {
         if (!isStatic(var5)) {
            try {
               var1.append(getName(var5)).append("=").append(var5.get(this)).append(" ");
            } catch (IllegalAccessException var7) {
            }
         }
      }

      var1.deleteCharAt(var1.length() - 1);
      var1.append('}');
      return var1.toString();
   }

   private static String getName(Field var0) {
      SerializedName var1 = var0.getAnnotation(SerializedName.class);
      return var1 != null ? var1.value() : var0.getName();
   }

   private static boolean isStatic(Field var0) {
      return Modifier.isStatic(var0.getModifiers());
   }
}
