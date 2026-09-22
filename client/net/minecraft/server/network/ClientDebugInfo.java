package net.minecraft.server.network;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.util.Map;
import java.util.Objects;
import net.minecraft.network.protocol.common.custom.PropertyMap;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public class ClientDebugInfo {
   private static final int MAX_ALLOWED_PROPERTIES = 262144;
   private @Nullable String brand;
   private @Nullable Table<Identifier, Identifier, String> modProperties;

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
            this.modProperties = HashBasedTable.create();
         }

         entries.forEach((modId, propertyMap) -> propertyMap.properties().forEach((propertyId, propertyValue) -> {
               String previous = (String)this.modProperties.put(modId, propertyId, propertyValue);
               if (previous != null) {
                  String var10002 = String.valueOf(modId);
                  throw new IllegalStateException("Duplicate mod property: " + var10002 + "/" + String.valueOf(propertyId) + ", old: " + previous + ", new: " + propertyValue);
               } else if (this.modProperties.size() > 262144) {
                  throw new IllegalStateException("Max number of mod properties reached");
               }
            }));
      }
   }

   public String print() {
      if (this.brand == null && this.modProperties == null) {
         return "none";
      } else {
         String brand = (String)Objects.requireNonNullElse(this.brand, "<unknown>");
         int modCount = this.modProperties != null ? this.modProperties.rowKeySet().size() : 0;
         return brand + "(" + modCount + " mods)";
      }
   }
}
