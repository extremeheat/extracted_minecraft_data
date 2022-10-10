package net.minecraft.resources;

import java.util.Map;

public class ServerPackFinder implements IPackFinder {
   private final VanillaPack field_195738_a = new VanillaPack(new String[]{"minecraft"});

   public ServerPackFinder() {
      super();
   }

   public <T extends ResourcePackInfo> void func_195730_a(Map<String, T> var1, ResourcePackInfo.IFactory<T> var2) {
      ResourcePackInfo var3 = ResourcePackInfo.func_195793_a("vanilla", false, () -> {
         return this.field_195738_a;
      }, var2, ResourcePackInfo.Priority.BOTTOM);
      if (var3 != null) {
         var1.put("vanilla", var3);
      }

   }
}
