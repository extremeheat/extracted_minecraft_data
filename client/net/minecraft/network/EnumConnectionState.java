package net.minecraft.network;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;

public enum EnumConnectionState {
   HANDSHAKING(-1),
   PLAY(0),
   STATUS(1),
   LOGIN(2);

   private static final TIntObjectMap field_150764_e = new TIntObjectHashMap();
   private static final Map field_150761_f = Maps.newHashMap();
   private final int field_150762_g;
   private final BiMap field_150769_h = HashBiMap.create();
   private final BiMap field_150770_i = HashBiMap.create();

   private EnumConnectionState(int var3) {
      this.field_150762_g = var3;
   }

   protected EnumConnectionState func_150751_a(int var1, Class var2) {
      if (this.field_150769_h.containsKey(var1)) {
         String var4 = "Serverbound packet ID " + var1 + " is already assigned to " + this.field_150769_h.get(var1) + "; cannot re-assign to " + var2;
         LogManager.getLogger().fatal(var4);
         throw new IllegalArgumentException(var4);
      } else if (this.field_150769_h.containsValue(var2)) {
         String var3 = "Serverbound packet "
            + var2
            + " is already assigned to ID "
            + this.field_150769_h.inverse().get(var2)
            + "; cannot re-assign to "
            + var1;
         LogManager.getLogger().fatal(var3);
         throw new IllegalArgumentException(var3);
      } else {
         this.field_150769_h.put(var1, var2);
         return this;
      }
   }

   protected EnumConnectionState func_150756_b(int var1, Class var2) {
      if (this.field_150770_i.containsKey(var1)) {
         String var4 = "Clientbound packet ID " + var1 + " is already assigned to " + this.field_150770_i.get(var1) + "; cannot re-assign to " + var2;
         LogManager.getLogger().fatal(var4);
         throw new IllegalArgumentException(var4);
      } else if (this.field_150770_i.containsValue(var2)) {
         String var3 = "Clientbound packet "
            + var2
            + " is already assigned to ID "
            + this.field_150770_i.inverse().get(var2)
            + "; cannot re-assign to "
            + var1;
         LogManager.getLogger().fatal(var3);
         throw new IllegalArgumentException(var3);
      } else {
         this.field_150770_i.put(var1, var2);
         return this;
      }
   }

   public BiMap func_150753_a() {
      return this.field_150769_h;
   }

   public BiMap func_150755_b() {
      return this.field_150770_i;
   }

   public BiMap func_150757_a(boolean var1) {
      return var1 ? this.func_150755_b() : this.func_150753_a();
   }

   public BiMap func_150754_b(boolean var1) {
      return var1 ? this.func_150753_a() : this.func_150755_b();
   }

   public int func_150759_c() {
      return this.field_150762_g;
   }

   public static EnumConnectionState func_150760_a(int var0) {
      return (EnumConnectionState)field_150764_e.get(var0);
   }

   public static EnumConnectionState func_150752_a(Packet var0) {
      return (EnumConnectionState)field_150761_f.get(var0.getClass());
   }

   static {
      for(EnumConnectionState var3 : values()) {
         field_150764_e.put(var3.func_150759_c(), var3);

         for(Class var5 : Iterables.concat(var3.func_150755_b().values(), var3.func_150753_a().values())) {
            if (field_150761_f.containsKey(var5) && field_150761_f.get(var5) != var3) {
               throw new Error("Packet " + var5 + " is already assigned to protocol " + field_150761_f.get(var5) + " - can't reassign to " + var3);
            }

            field_150761_f.put(var5, var3);
         }
      }
   }
}
