package net.minecraft.client.resources;

import com.google.common.collect.ImmutableSet;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;

public class DefaultResourcePack implements IResourcePack {
   public static final Set<String> field_110608_a = ImmutableSet.of("minecraft", "realms");
   private final Map<String, File> field_152781_b;

   public DefaultResourcePack(Map<String, File> var1) {
      super();
      this.field_152781_b = var1;
   }

   public InputStream func_110590_a(ResourceLocation var1) throws IOException {
      InputStream var2 = this.func_110605_c(var1);
      if (var2 != null) {
         return var2;
      } else {
         InputStream var3 = this.func_152780_c(var1);
         if (var3 != null) {
            return var3;
         } else {
            throw new FileNotFoundException(var1.func_110623_a());
         }
      }
   }

   public InputStream func_152780_c(ResourceLocation var1) throws FileNotFoundException {
      File var2 = (File)this.field_152781_b.get(var1.toString());
      return var2 != null && var2.isFile() ? new FileInputStream(var2) : null;
   }

   private InputStream func_110605_c(ResourceLocation var1) {
      return DefaultResourcePack.class.getResourceAsStream("/assets/" + var1.func_110624_b() + "/" + var1.func_110623_a());
   }

   public boolean func_110589_b(ResourceLocation var1) {
      return this.func_110605_c(var1) != null || this.field_152781_b.containsKey(var1.toString());
   }

   public Set<String> func_110587_b() {
      return field_110608_a;
   }

   public <T extends IMetadataSection> T func_135058_a(IMetadataSerializer var1, String var2) throws IOException {
      try {
         FileInputStream var3 = new FileInputStream((File)this.field_152781_b.get("pack.mcmeta"));
         return AbstractResourcePack.func_110596_a(var1, var3, var2);
      } catch (RuntimeException var4) {
         return null;
      } catch (FileNotFoundException var5) {
         return null;
      }
   }

   public BufferedImage func_110586_a() throws IOException {
      return TextureUtil.func_177053_a(DefaultResourcePack.class.getResourceAsStream("/" + (new ResourceLocation("pack.png")).func_110623_a()));
   }

   public String func_130077_b() {
      return "Default";
   }
}
