package net.minecraft.client.shader;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.renderer.OpenGlHelper;

public enum ShaderLoader$ShaderType {
   VERTEX("vertex", ".vsh", OpenGlHelper.field_153209_q),
   FRAGMENT("fragment", ".fsh", OpenGlHelper.field_153210_r);

   private final String field_148072_c;
   private final String field_148069_d;
   private final int field_148070_e;
   private final Map field_148067_f = Maps.newHashMap();

   private ShaderLoader$ShaderType(String var3, String var4, int var5) {
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

   protected Map func_148064_d() {
      return this.field_148067_f;
   }
}
