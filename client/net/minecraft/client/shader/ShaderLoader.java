package net.minecraft.client.shader;

import java.io.BufferedInputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.util.JsonException;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.BufferUtils;

public class ShaderLoader {
   private final ShaderLoader$ShaderType field_148061_a;
   private final String field_148059_b;
   private int field_148060_c;
   private int field_148058_d = 0;

   private ShaderLoader(ShaderLoader$ShaderType var1, int var2, String var3) {
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

   public static ShaderLoader func_148057_a(IResourceManager var0, ShaderLoader$ShaderType var1, String var2) {
      ShaderLoader var3 = (ShaderLoader)var1.func_148064_d().get(var2);
      if (var3 == null) {
         ResourceLocation var4 = new ResourceLocation("shaders/program/" + var2 + var1.func_148063_b());
         BufferedInputStream var5 = new BufferedInputStream(var0.func_110536_a(var4).func_110527_b());
         byte[] var6 = IOUtils.toByteArray(var5);
         ByteBuffer var7 = BufferUtils.createByteBuffer(var6.length);
         var7.put(var6);
         ((Buffer)var7).position(0);
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
}
