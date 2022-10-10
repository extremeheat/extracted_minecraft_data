package net.minecraft.client.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.VanillaPack;
import net.minecraft.util.ResourceLocation;

public class VirtualAssetsPack extends VanillaPack {
   private final ResourceIndex field_195785_b;

   public VirtualAssetsPack(ResourceIndex var1) {
      super("minecraft", "realms");
      this.field_195785_b = var1;
   }

   @Nullable
   protected InputStream func_195782_c(ResourcePackType var1, ResourceLocation var2) {
      if (var1 == ResourcePackType.CLIENT_RESOURCES) {
         File var3 = this.field_195785_b.func_188547_a(var2);
         if (var3 != null && var3.exists()) {
            try {
               return new FileInputStream(var3);
            } catch (FileNotFoundException var5) {
            }
         }
      }

      return super.func_195782_c(var1, var2);
   }

   @Nullable
   protected InputStream func_200010_a(String var1) {
      File var2 = this.field_195785_b.func_200009_a(var1);
      if (var2 != null && var2.exists()) {
         try {
            return new FileInputStream(var2);
         } catch (FileNotFoundException var4) {
         }
      }

      return super.func_200010_a(var1);
   }

   public Collection<ResourceLocation> func_195758_a(ResourcePackType var1, String var2, int var3, Predicate<String> var4) {
      Collection var5 = super.func_195758_a(var1, var2, var3, var4);
      var5.addAll((Collection)this.field_195785_b.func_211685_a(var2, var3, var4).stream().map(ResourceLocation::new).collect(Collectors.toList()));
      return var5;
   }
}
