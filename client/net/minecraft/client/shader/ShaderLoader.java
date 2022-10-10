package net.minecraft.client.shader;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.util.JsonException;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.system.MemoryUtil;

public class ShaderLoader {
   private final ShaderLoader.ShaderType field_148061_a;
   private final String field_148059_b;
   private final int field_148060_c;
   private int field_148058_d;

   private ShaderLoader(ShaderLoader.ShaderType var1, int var2, String var3) {
      super();
      this.field_148061_a = var1;
      this.field_148060_c = var2;
      this.field_148059_b = var3;
   }

   public void func_148056_a(ShaderManager var1) {
      ++this.field_148058_d;
      OpenGlHelper.func_153178_b(var1.func_147986_h(), this.field_148060_c);
   }

   public void func_195656_a() {
      --this.field_148058_d;
      if (this.field_148058_d <= 0) {
         OpenGlHelper.func_153180_a(this.field_148060_c);
         this.field_148061_a.func_148064_d().remove(this.field_148059_b);
      }

   }

   public String func_148055_a() {
      return this.field_148059_b;
   }

   public static ShaderLoader func_195655_a(IResourceManager var0, ShaderLoader.ShaderType var1, String var2) throws IOException {
      ShaderLoader var3 = (ShaderLoader)var1.func_148064_d().get(var2);
      if (var3 == null) {
         ResourceLocation var4 = new ResourceLocation("shaders/program/" + var2 + var1.func_148063_b());
         IResource var5 = var0.func_199002_a(var4);
         ByteBuffer var6 = null;

         try {
            var6 = TextureUtil.func_195724_a(var5.func_199027_b());
            int var7 = var6.position();
            var6.rewind();
            int var8 = OpenGlHelper.func_153195_b(var1.func_148065_c());
            String var9 = MemoryUtil.memASCII(var6, var7);
            OpenGlHelper.func_195918_a(var8, var9);
            OpenGlHelper.func_153170_c(var8);
            if (OpenGlHelper.func_153157_c(var8, OpenGlHelper.field_153208_p) == 0) {
               String var10 = StringUtils.trim(OpenGlHelper.func_153158_d(var8, 32768));
               JsonException var11 = new JsonException("Couldn't compile " + var1.func_148062_a() + " program: " + var10);
               var11.func_151381_b(var4.func_110623_a());
               throw var11;
            }

            var3 = new ShaderLoader(var1, var8, var2);
            var1.func_148064_d().put(var2, var3);
         } finally {
            IOUtils.closeQuietly(var5);
            if (var6 != null) {
               MemoryUtil.memFree(var6);
            }

         }
      }

      return var3;
   }

   public static enum ShaderType {
      VERTEX("vertex", ".vsh", OpenGlHelper.field_153209_q),
      FRAGMENT("fragment", ".fsh", OpenGlHelper.field_153210_r);

      private final String field_148072_c;
      private final String field_148069_d;
      private final int field_148070_e;
      private final Map<String, ShaderLoader> field_148067_f = Maps.newHashMap();

      private ShaderType(String var3, String var4, int var5) {
         this.field_148072_c = var3;
         this.field_148069_d = var4;
         this.field_148070_e = var5;
      }

      public String func_148062_a() {
         return this.field_148072_c;
      }

      private String func_148063_b() {
         return this.field_148069_d;
      }

      private int func_148065_c() {
         return this.field_148070_e;
      }

      private Map<String, ShaderLoader> func_148064_d() {
         return this.field_148067_f;
      }
   }
}
