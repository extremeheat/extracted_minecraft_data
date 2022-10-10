package net.minecraft.client.shader;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.util.JsonBlendingMode;
import net.minecraft.client.util.JsonException;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ShaderManager implements AutoCloseable {
   private static final Logger field_148003_a = LogManager.getLogger();
   private static final ShaderDefault field_148001_b = new ShaderDefault();
   private static ShaderManager field_148002_c;
   private static int field_147999_d = -1;
   private final Map<String, Object> field_147997_f = Maps.newHashMap();
   private final List<String> field_147998_g = Lists.newArrayList();
   private final List<Integer> field_148010_h = Lists.newArrayList();
   private final List<ShaderUniform> field_148011_i = Lists.newArrayList();
   private final List<Integer> field_148008_j = Lists.newArrayList();
   private final Map<String, ShaderUniform> field_148009_k = Maps.newHashMap();
   private final int field_148006_l;
   private final String field_148007_m;
   private final boolean field_148004_n;
   private boolean field_148005_o;
   private final JsonBlendingMode field_148016_p;
   private final List<Integer> field_148015_q;
   private final List<String> field_148014_r;
   private final ShaderLoader field_148013_s;
   private final ShaderLoader field_148012_t;

   public ShaderManager(IResourceManager var1, String var2) throws IOException {
      super();
      ResourceLocation var3 = new ResourceLocation("shaders/program/" + var2 + ".json");
      this.field_148007_m = var2;
      IResource var4 = null;

      try {
         var4 = var1.func_199002_a(var3);
         JsonObject var5 = JsonUtils.func_212743_a(new InputStreamReader(var4.func_199027_b(), StandardCharsets.UTF_8));
         String var6 = JsonUtils.func_151200_h(var5, "vertex");
         String var27 = JsonUtils.func_151200_h(var5, "fragment");
         JsonArray var8 = JsonUtils.func_151213_a(var5, "samplers", (JsonArray)null);
         if (var8 != null) {
            int var9 = 0;

            for(Iterator var10 = var8.iterator(); var10.hasNext(); ++var9) {
               JsonElement var11 = (JsonElement)var10.next();

               try {
                  this.func_147996_a(var11);
               } catch (Exception var24) {
                  JsonException var13 = JsonException.func_151379_a(var24);
                  var13.func_151380_a("samplers[" + var9 + "]");
                  throw var13;
               }
            }
         }

         JsonArray var28 = JsonUtils.func_151213_a(var5, "attributes", (JsonArray)null);
         Iterator var31;
         if (var28 != null) {
            int var29 = 0;
            this.field_148015_q = Lists.newArrayListWithCapacity(var28.size());
            this.field_148014_r = Lists.newArrayListWithCapacity(var28.size());

            for(var31 = var28.iterator(); var31.hasNext(); ++var29) {
               JsonElement var12 = (JsonElement)var31.next();

               try {
                  this.field_148014_r.add(JsonUtils.func_151206_a(var12, "attribute"));
               } catch (Exception var23) {
                  JsonException var14 = JsonException.func_151379_a(var23);
                  var14.func_151380_a("attributes[" + var29 + "]");
                  throw var14;
               }
            }
         } else {
            this.field_148015_q = null;
            this.field_148014_r = null;
         }

         JsonArray var30 = JsonUtils.func_151213_a(var5, "uniforms", (JsonArray)null);
         if (var30 != null) {
            int var32 = 0;

            for(Iterator var33 = var30.iterator(); var33.hasNext(); ++var32) {
               JsonElement var35 = (JsonElement)var33.next();

               try {
                  this.func_147987_b(var35);
               } catch (Exception var22) {
                  JsonException var15 = JsonException.func_151379_a(var22);
                  var15.func_151380_a("uniforms[" + var32 + "]");
                  throw var15;
               }
            }
         }

         this.field_148016_p = JsonBlendingMode.func_148110_a(JsonUtils.func_151218_a(var5, "blend", (JsonObject)null));
         this.field_148004_n = JsonUtils.func_151209_a(var5, "cull", true);
         this.field_148013_s = ShaderLoader.func_195655_a(var1, ShaderLoader.ShaderType.VERTEX, var6);
         this.field_148012_t = ShaderLoader.func_195655_a(var1, ShaderLoader.ShaderType.FRAGMENT, var27);
         this.field_148006_l = ShaderLinkHelper.func_148074_b().func_148078_c();
         ShaderLinkHelper.func_148074_b().func_148075_b(this);
         this.func_147990_i();
         if (this.field_148014_r != null) {
            var31 = this.field_148014_r.iterator();

            while(var31.hasNext()) {
               String var34 = (String)var31.next();
               int var36 = OpenGlHelper.func_153164_b(this.field_148006_l, var34);
               this.field_148015_q.add(var36);
            }
         }
      } catch (Exception var25) {
         JsonException var7 = JsonException.func_151379_a(var25);
         var7.func_151381_b(var3.func_110623_a());
         throw var7;
      } finally {
         IOUtils.closeQuietly(var4);
      }

      this.func_147985_d();
   }

   public void close() {
      Iterator var1 = this.field_148011_i.iterator();

      while(var1.hasNext()) {
         ShaderUniform var2 = (ShaderUniform)var1.next();
         var2.close();
      }

      ShaderLinkHelper.func_148074_b().func_148077_a(this);
   }

   public void func_147993_b() {
      OpenGlHelper.func_153161_d(0);
      field_147999_d = -1;
      field_148002_c = null;

      for(int var1 = 0; var1 < this.field_148010_h.size(); ++var1) {
         if (this.field_147997_f.get(this.field_147998_g.get(var1)) != null) {
            GlStateManager.func_179138_g(OpenGlHelper.field_77478_a + var1);
            GlStateManager.func_179144_i(0);
         }
      }

   }

   public void func_147995_c() {
      this.field_148005_o = false;
      field_148002_c = this;
      this.field_148016_p.func_148109_a();
      if (this.field_148006_l != field_147999_d) {
         OpenGlHelper.func_153161_d(this.field_148006_l);
         field_147999_d = this.field_148006_l;
      }

      if (this.field_148004_n) {
         GlStateManager.func_179089_o();
      } else {
         GlStateManager.func_179129_p();
      }

      for(int var1 = 0; var1 < this.field_148010_h.size(); ++var1) {
         if (this.field_147997_f.get(this.field_147998_g.get(var1)) != null) {
            GlStateManager.func_179138_g(OpenGlHelper.field_77478_a + var1);
            GlStateManager.func_179098_w();
            Object var2 = this.field_147997_f.get(this.field_147998_g.get(var1));
            int var3 = -1;
            if (var2 instanceof Framebuffer) {
               var3 = ((Framebuffer)var2).field_147617_g;
            } else if (var2 instanceof ITextureObject) {
               var3 = ((ITextureObject)var2).func_110552_b();
            } else if (var2 instanceof Integer) {
               var3 = (Integer)var2;
            }

            if (var3 != -1) {
               GlStateManager.func_179144_i(var3);
               OpenGlHelper.func_153163_f(OpenGlHelper.func_153194_a(this.field_148006_l, (CharSequence)this.field_147998_g.get(var1)), var1);
            }
         }
      }

      Iterator var4 = this.field_148011_i.iterator();

      while(var4.hasNext()) {
         ShaderUniform var5 = (ShaderUniform)var4.next();
         var5.func_148093_b();
      }

   }

   public void func_147985_d() {
      this.field_148005_o = true;
   }

   @Nullable
   public ShaderUniform func_147991_a(String var1) {
      return (ShaderUniform)this.field_148009_k.get(var1);
   }

   public ShaderDefault func_195653_b(String var1) {
      ShaderUniform var2 = this.func_147991_a(var1);
      return (ShaderDefault)(var2 == null ? field_148001_b : var2);
   }

   private void func_147990_i() {
      int var1 = 0;

      String var3;
      int var4;
      for(int var2 = 0; var1 < this.field_147998_g.size(); ++var2) {
         var3 = (String)this.field_147998_g.get(var1);
         var4 = OpenGlHelper.func_153194_a(this.field_148006_l, var3);
         if (var4 == -1) {
            field_148003_a.warn("Shader {}could not find sampler named {} in the specified shader program.", this.field_148007_m, var3);
            this.field_147997_f.remove(var3);
            this.field_147998_g.remove(var2);
            --var2;
         } else {
            this.field_148010_h.add(var4);
         }

         ++var1;
      }

      Iterator var5 = this.field_148011_i.iterator();

      while(var5.hasNext()) {
         ShaderUniform var6 = (ShaderUniform)var5.next();
         var3 = var6.func_148086_a();
         var4 = OpenGlHelper.func_153194_a(this.field_148006_l, var3);
         if (var4 == -1) {
            field_148003_a.warn("Could not find uniform named {} in the specified shader program.", var3);
         } else {
            this.field_148008_j.add(var4);
            var6.func_148084_b(var4);
            this.field_148009_k.put(var3, var6);
         }
      }

   }

   private void func_147996_a(JsonElement var1) {
      JsonObject var2 = JsonUtils.func_151210_l(var1, "sampler");
      String var3 = JsonUtils.func_151200_h(var2, "name");
      if (!JsonUtils.func_151205_a(var2, "file")) {
         this.field_147997_f.put(var3, (Object)null);
         this.field_147998_g.add(var3);
      } else {
         this.field_147998_g.add(var3);
      }
   }

   public void func_147992_a(String var1, Object var2) {
      if (this.field_147997_f.containsKey(var1)) {
         this.field_147997_f.remove(var1);
      }

      this.field_147997_f.put(var1, var2);
      this.func_147985_d();
   }

   private void func_147987_b(JsonElement var1) throws JsonException {
      JsonObject var2 = JsonUtils.func_151210_l(var1, "uniform");
      String var3 = JsonUtils.func_151200_h(var2, "name");
      int var4 = ShaderUniform.func_148085_a(JsonUtils.func_151200_h(var2, "type"));
      int var5 = JsonUtils.func_151203_m(var2, "count");
      float[] var6 = new float[Math.max(var5, 16)];
      JsonArray var7 = JsonUtils.func_151214_t(var2, "values");
      if (var7.size() != var5 && var7.size() > 1) {
         throw new JsonException("Invalid amount of values specified (expected " + var5 + ", found " + var7.size() + ")");
      } else {
         int var8 = 0;

         for(Iterator var9 = var7.iterator(); var9.hasNext(); ++var8) {
            JsonElement var10 = (JsonElement)var9.next();

            try {
               var6[var8] = JsonUtils.func_151220_d(var10, "value");
            } catch (Exception var13) {
               JsonException var12 = JsonException.func_151379_a(var13);
               var12.func_151380_a("values[" + var8 + "]");
               throw var12;
            }
         }

         if (var5 > 1 && var7.size() == 1) {
            while(var8 < var5) {
               var6[var8] = var6[0];
               ++var8;
            }
         }

         int var14 = var5 > 1 && var5 <= 4 && var4 < 8 ? var5 - 1 : 0;
         ShaderUniform var15 = new ShaderUniform(var3, var4 + var14, var5, this);
         if (var4 <= 3) {
            var15.func_148083_a((int)var6[0], (int)var6[1], (int)var6[2], (int)var6[3]);
         } else if (var4 <= 7) {
            var15.func_148092_b(var6[0], var6[1], var6[2], var6[3]);
         } else {
            var15.func_148097_a(var6);
         }

         this.field_148011_i.add(var15);
      }
   }

   public ShaderLoader func_147989_e() {
      return this.field_148013_s;
   }

   public ShaderLoader func_147994_f() {
      return this.field_148012_t;
   }

   public int func_147986_h() {
      return this.field_148006_l;
   }
}
