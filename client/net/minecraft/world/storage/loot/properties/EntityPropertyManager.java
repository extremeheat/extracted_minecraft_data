package net.minecraft.world.storage.loot.properties;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.util.ResourceLocation;

public class EntityPropertyManager {
   private static final Map<ResourceLocation, EntityProperty.Serializer<?>> field_186647_a = Maps.newHashMap();
   private static final Map<Class<? extends EntityProperty>, EntityProperty.Serializer<?>> field_186648_b = Maps.newHashMap();

   public static <T extends EntityProperty> void func_186644_a(EntityProperty.Serializer<? extends T> var0) {
      ResourceLocation var1 = var0.func_186649_a();
      Class var2 = var0.func_186651_b();
      if (field_186647_a.containsKey(var1)) {
         throw new IllegalArgumentException("Can't re-register entity property name " + var1);
      } else if (field_186648_b.containsKey(var2)) {
         throw new IllegalArgumentException("Can't re-register entity property class " + var2.getName());
      } else {
         field_186647_a.put(var1, var0);
         field_186648_b.put(var2, var0);
      }
   }

   public static EntityProperty.Serializer<?> func_186646_a(ResourceLocation var0) {
      EntityProperty.Serializer var1 = (EntityProperty.Serializer)field_186647_a.get(var0);
      if (var1 == null) {
         throw new IllegalArgumentException("Unknown loot entity property '" + var0 + "'");
      } else {
         return var1;
      }
   }

   public static <T extends EntityProperty> EntityProperty.Serializer<T> func_186645_a(T var0) {
      EntityProperty.Serializer var1 = (EntityProperty.Serializer)field_186648_b.get(var0.getClass());
      if (var1 == null) {
         throw new IllegalArgumentException("Unknown loot entity property " + var0);
      } else {
         return var1;
      }
   }

   static {
      func_186644_a(new EntityOnFire.Serializer());
   }
}
