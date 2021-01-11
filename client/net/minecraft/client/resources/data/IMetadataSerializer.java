package net.minecraft.client.resources.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumTypeAdapterFactory;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IRegistry;
import net.minecraft.util.RegistrySimple;

public class IMetadataSerializer {
   private final IRegistry<String, IMetadataSerializer.Registration<? extends IMetadataSection>> field_110508_a = new RegistrySimple();
   private final GsonBuilder field_110506_b = new GsonBuilder();
   private Gson field_110507_c;

   public IMetadataSerializer() {
      super();
      this.field_110506_b.registerTypeHierarchyAdapter(IChatComponent.class, new IChatComponent.Serializer());
      this.field_110506_b.registerTypeHierarchyAdapter(ChatStyle.class, new ChatStyle.Serializer());
      this.field_110506_b.registerTypeAdapterFactory(new EnumTypeAdapterFactory());
   }

   public <T extends IMetadataSection> void func_110504_a(IMetadataSectionSerializer<T> var1, Class<T> var2) {
      this.field_110508_a.func_82595_a(var1.func_110483_a(), new IMetadataSerializer.Registration(var1, var2));
      this.field_110506_b.registerTypeAdapter(var2, var1);
      this.field_110507_c = null;
   }

   public <T extends IMetadataSection> T func_110503_a(String var1, JsonObject var2) {
      if (var1 == null) {
         throw new IllegalArgumentException("Metadata section name cannot be null");
      } else if (!var2.has(var1)) {
         return null;
      } else if (!var2.get(var1).isJsonObject()) {
         throw new IllegalArgumentException("Invalid metadata for '" + var1 + "' - expected object, found " + var2.get(var1));
      } else {
         IMetadataSerializer.Registration var3 = (IMetadataSerializer.Registration)this.field_110508_a.func_82594_a(var1);
         if (var3 == null) {
            throw new IllegalArgumentException("Don't know how to handle metadata section '" + var1 + "'");
         } else {
            return (IMetadataSection)this.func_110505_a().fromJson(var2.getAsJsonObject(var1), var3.field_110500_b);
         }
      }
   }

   private Gson func_110505_a() {
      if (this.field_110507_c == null) {
         this.field_110507_c = this.field_110506_b.create();
      }

      return this.field_110507_c;
   }

   class Registration<T extends IMetadataSection> {
      final IMetadataSectionSerializer<T> field_110502_a;
      final Class<T> field_110500_b;

      private Registration(IMetadataSectionSerializer<T> var2, Class<T> var3) {
         super();
         this.field_110502_a = var2;
         this.field_110500_b = var3;
      }

      // $FF: synthetic method
      Registration(IMetadataSectionSerializer var2, Class var3, Object var4) {
         this(var2, var3);
      }
   }
}
