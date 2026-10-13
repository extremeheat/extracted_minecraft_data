package net.minecraft.server.network;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import net.minecraft.network.protocol.common.custom.PropertyMap;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public class ClientDebugInfo {
   private static final int MAX_ALLOWED_PROPERTIES = 262144;
   private @Nullable String brand;
   private @Nullable Map<PropertyKey, String> modProperties;

   public ClientDebugInfo() {
      super();
   }

   public void setBrand(final String brand) {
      if (this.brand != null) {
         throw new IllegalStateException("Brand already set to " + this.brand);
      } else {
         this.brand = brand;
      }
   }

   public void appendModInfo(final Map<Identifier, PropertyMap> entries) {
      if (!entries.isEmpty()) {
         if (this.modProperties == null) {
            this.modProperties = new HashMap();
         }

         entries.forEach((modId, propertyMap) -> propertyMap.properties().forEach((propertyId, propertyValue) -> {
               if (this.modProperties.size() >= 262144) {
                  throw new IllegalStateException("Max number of mod properties reached");
               } else {
                  String previous = (String)this.modProperties.put(new PropertyKey(modId, propertyId), propertyValue);
                  if (previous != null) {
                     String var10002 = String.valueOf(modId);
                     throw new IllegalStateException("Duplicate mod property: " + var10002 + "/" + String.valueOf(propertyId) + ", old: " + previous + ", new: " + propertyValue);
                  }
               }
            }));
      }
   }

   public String print() {
      if (this.brand == null && this.modProperties == null) {
         return "none";
      } else {
         String brand = (String)Objects.requireNonNullElse(this.brand, "<unknown>");
         long modCount = this.modProperties != null ? this.modProperties.keySet().stream().map(PropertyKey::mod).distinct().count() : 0L;
         return brand + "(" + modCount + " mods)";
      }
   }

   private static record PropertyKey(Identifier mod, Identifier property) {
      private PropertyKey {
         super();
      }
   }
}
