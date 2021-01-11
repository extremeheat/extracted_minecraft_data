package net.minecraft.client.shader;

import com.google.common.collect.Maps;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.util.JsonException;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.BufferUtils;

public class ShaderLoader {
   private final ShaderLoader.ShaderType field_148061_a;
   private final String field_148059_b;
   private int field_148060_c;
   private int field_148058_d = 0;

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

   public void func_148054_b(ShaderManager var1) {
      --this.field_148058_d;
      if (this.field_148058_d <= 0) {
         OpenGlHelper.func_153180_a(this.field_148060_c);
         this.field_148061_a.func_148064_d().remove(this.field_148059_b);
      }

   }

   public String func_148055_a() {
      return this.field_148059_b;
   }

   public static ShaderLoader func_148057_a(IResourceManager var0, ShaderLoader.ShaderType var1, String var2) throws IOException {
      ShaderLoader var3 = (ShaderLoader)var1.func_148064_d().get(var2);
      if (var3 == null) {
         ResourceLocation var4 = new ResourceLocation("shaders/program/" + var2 + var1.func_148063_b());
         BufferedInputStream var5 = new BufferedInputStream(var0.func_110536_a(var4).func_110527_b());
         byte[] var6 = func_177064_a(var5);
         ByteBuffer var7 = BufferUtils.createByteBuffer(var6.length);
         var7.put(var6);
         var7.position(0);
         int var8 = OpenGlHelper.func_153195_b(var1.func_148065_c());
         OpenGlHelper.func_153169_a(var8, var7);
         OpenGlHelper.func_153170_c(var8);
         if (OpenGlHelper.func_153157_c(var8, OpenGlHelper.field_153208_p) == 0) {
            String var9 = StringUtils.trim(OpenGlHelper.func_153158_d(var8, 32768));
            JsonException var10 = new JsonException("Couldn't compile " + var1.func_148062_a() + " program: " + var9);
            var10.func_151381_b(var4.func_110623_a());
            throw var10;
         }

         var3 = new ShaderLoader(var1, var8, var2);
         var1.func_148064_d().put(var2, var3);
      }

      return var3;
   }

   protected static byte[] func_177064_a(BufferedInputStream var0) throws IOException {
      byte[] var1;
      try {
         var1 = IOUtils.toByteArray(var0);
      } finally {
         var0.close();
      }

      return var1;
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

      protected String func_148063_b() {
         return this.field_148069_d;
      }

      protected int func_148065_c() {
         return this.field_148070_e;
      }

      protected Map<String, ShaderLoader> func_148064_d() {
         return this.field_148067_f;
      }
   }
}
