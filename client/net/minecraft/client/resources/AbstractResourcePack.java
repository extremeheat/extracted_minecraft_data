package net.minecraft.client.resources;

import com.google.common.base.Charsets;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractResourcePack implements IResourcePack {
   private static final Logger field_110598_a = LogManager.getLogger();
   protected final File field_110597_b;

   public AbstractResourcePack(File var1) {
      super();
      this.field_110597_b = var1;
   }

   private static String func_110592_c(ResourceLocation var0) {
      return String.format("%s/%s/%s", "assets", var0.func_110624_b(), var0.func_110623_a());
   }

   protected static String func_110595_a(File var0, File var1) {
      return var0.toURI().relativize(var1.toURI()).getPath();
   }

   public InputStream func_110590_a(ResourceLocation var1) throws IOException {
      return this.func_110591_a(func_110592_c(var1));
   }

   public boolean func_110589_b(ResourceLocation var1) {
      return this.func_110593_b(func_110592_c(var1));
   }

   protected abstract InputStream func_110591_a(String var1) throws IOException;

   protected abstract boolean func_110593_b(String var1);

   protected void func_110594_c(String var1) {
      field_110598_a.warn("ResourcePack: ignored non-lowercase namespace: %s in %s", new Object[]{var1, this.field_110597_b});
   }

   public <T extends IMetadataSection> T func_135058_a(IMetadataSerializer var1, String var2) throws IOException {
      return func_110596_a(var1, this.func_110591_a("pack.mcmeta"), var2);
   }

   static <T extends IMetadataSection> T func_110596_a(IMetadataSerializer var0, InputStream var1, String var2) {
      JsonObject var3 = null;
      BufferedReader var4 = null;

      try {
         var4 = new BufferedReader(new InputStreamReader(var1, Charsets.UTF_8));
         var3 = (new JsonParser()).parse(var4).getAsJsonObject();
      } catch (RuntimeException var9) {
         throw new JsonParseException(var9);
      } finally {
         IOUtils.closeQuietly(var4);
      }

      return var0.func_110503_a(var2, var3);
   }

   public BufferedImage func_110586_a() throws IOException {
      return TextureUtil.func_177053_a(this.func_110591_a("pack.png"));
   }

   public String func_130077_b() {
      return this.field_110597_b.getName();
   }
}
