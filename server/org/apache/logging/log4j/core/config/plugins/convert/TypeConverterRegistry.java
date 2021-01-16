package org.apache.logging.log4j.core.config.plugins.convert;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.UnknownFormatConversionException;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.plugins.util.PluginManager;
import org.apache.logging.log4j.core.config.plugins.util.PluginType;
import org.apache.logging.log4j.core.util.ReflectionUtil;
import org.apache.logging.log4j.core.util.TypeUtil;
import org.apache.logging.log4j.status.StatusLogger;

public class TypeConverterRegistry {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static volatile TypeConverterRegistry INSTANCE;
   private static final Object INSTANCE_LOCK = new Object();
   private final ConcurrentMap<Type, TypeConverter<?>> registry = new ConcurrentHashMap();

   public static TypeConverterRegistry getInstance() {
      TypeConverterRegistry var0 = INSTANCE;
      if (var0 == null) {
         synchronized(INSTANCE_LOCK) {
            var0 = INSTANCE;
            if (var0 == null) {
               INSTANCE = var0 = new TypeConverterRegistry();
            }
         }
      }

      return var0;
   }

   public TypeConverter<?> findCompatibleConverter(Type var1) {
      Objects.requireNonNull(var1, "No type was provided");
      TypeConverter var2 = (TypeConverter)this.registry.get(var1);
      if (var2 != null) {
         return var2;
      } else {
         if (var1 instanceof Class) {
            Class var3 = (Class)var1;
            if (var3.isEnum()) {
               EnumConverter var8 = new EnumConverter(var3.asSubclass(Enum.class));
               this.registry.putIfAbsent(var1, var8);
               return var8;
            }
         }

         Iterator var7 = this.registry.entrySet().iterator();

         Entry var4;
         Type var5;
         do {
            if (!var7.hasNext()) {
               throw new UnknownFormatConversionException(var1.toString());
            }

            var4 = (Entry)var7.next();
            var5 = (Type)var4.getKey();
         } while(!TypeUtil.isAssignable(var1, var5));

         LOGGER.debug((String)"Found compatible TypeConverter<{}> for type [{}].", (Object)var5, (Object)var1);
         TypeConverter var6 = (TypeConverter)var4.getValue();
         this.registry.putIfAbsent(var1, var6);
         return var6;
      }
   }

   private TypeConverterRegistry() {
      super();
      LOGGER.trace("TypeConverterRegistry initializing.");
      PluginManager var1 = new PluginManager("TypeConverter");
      var1.collectPlugins();
      this.loadKnownTypeConverters(var1.getPlugins().values());
      this.registerPrimitiveTypes();
   }

   private void loadKnownTypeConverters(Collection<PluginType<?>> var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         PluginType var3 = (PluginType)var2.next();
         Class var4 = var3.getPluginClass();
         if (TypeConverter.class.isAssignableFrom(var4)) {
            Class var5 = var4.asSubclass(TypeConverter.class);
            Type var6 = getTypeConverterSupportedType(var5);
            TypeConverter var7 = (TypeConverter)ReflectionUtil.instantiate(var5);
            if (this.registry.putIfAbsent(var6, var7) != null) {
               LOGGER.warn((String)"Found a TypeConverter [{}] for type [{}] that already exists.", (Object)var7, (Object)var6);
            }
         }
      }

   }

   private static Type getTypeConverterSupportedType(Class<? extends TypeConverter> var0) {
      Type[] var1 = var0.getGenericInterfaces();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Type var4 = var1[var3];
         if (var4 instanceof ParameterizedType) {
            ParameterizedType var5 = (ParameterizedType)var4;
            if (TypeConverter.class.equals(var5.getRawType())) {
               return var5.getActualTypeArguments()[0];
            }
         }
      }

      return Void.TYPE;
   }

   private void registerPrimitiveTypes() {
      this.registerTypeAlias(Boolean.class, Boolean.TYPE);
      this.registerTypeAlias(Byte.class, Byte.TYPE);
      this.registerTypeAlias(Character.class, Character.TYPE);
      this.registerTypeAlias(Double.class, Double.TYPE);
      this.registerTypeAlias(Float.class, Float.TYPE);
      this.registerTypeAlias(Integer.class, Integer.TYPE);
      this.registerTypeAlias(Long.class, Long.TYPE);
      this.registerTypeAlias(Short.class, Short.TYPE);
   }

   private void registerTypeAlias(Type var1, Type var2) {
      this.registry.putIfAbsent(var2, this.registry.get(var1));
   }
}
