package net.minecraft.client.renderer;

import java.nio.ByteBuffer;
import java.util.List;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import org.lwjgl.opengl.GL11;

public class WorldVertexBufferUploader {
   public WorldVertexBufferUploader() {
      super();
   }

   public void func_181679_a(WorldRenderer var1) {
      if (var1.func_178989_h() > 0) {
         VertexFormat var2 = var1.func_178973_g();
         int var3 = var2.func_177338_f();
         ByteBuffer var4 = var1.func_178966_f();
         List var5 = var2.func_177343_g();

         int var6;
         int var10;
         for(var6 = 0; var6 < var5.size(); ++var6) {
            VertexFormatElement var7 = (VertexFormatElement)var5.get(var6);
            VertexFormatElement.EnumUsage var8 = var7.func_177375_c();
            int var9 = var7.func_177367_b().func_177397_c();
            var10 = var7.func_177369_e();
            var4.position(var2.func_181720_d(var6));
            switch(var8) {
            case POSITION:
               GL11.glVertexPointer(var7.func_177370_d(), var9, var3, var4);
               GL11.glEnableClientState(32884);
               break;
            case UV:
               OpenGlHelper.func_77472_b(OpenGlHelper.field_77478_a + var10);
               GL11.glTexCoordPointer(var7.func_177370_d(), var9, var3, var4);
               GL11.glEnableClientState(32888);
               OpenGlHelper.func_77472_b(OpenGlHelper.field_77478_a);
               break;
            case COLOR:
               GL11.glColorPointer(var7.func_177370_d(), var9, var3, var4);
               GL11.glEnableClientState(32886);
               break;
            case NORMAL:
               GL11.glNormalPointer(var9, var3, var4);
               GL11.glEnableClientState(32885);
            }
         }

         GL11.glDrawArrays(var1.func_178979_i(), 0, var1.func_178989_h());
         var6 = 0;

         for(int var11 = var5.size(); var6 < var11; ++var6) {
            VertexFormatElement var12 = (VertexFormatElement)var5.get(var6);
            VertexFormatElement.EnumUsage var13 = var12.func_177375_c();
            var10 = var12.func_177369_e();
            switch(var13) {
            case POSITION:
               GL11.glDisableClientState(32884);
               break;
            case UV:
               OpenGlHelper.func_77472_b(OpenGlHelper.field_77478_a + var10);
               GL11.glDisableClientState(32888);
               OpenGlHelper.func_77472_b(OpenGlHelper.field_77478_a);
               break;
            case COLOR:
               GL11.glDisableClientState(32886);
               GlStateManager.func_179117_G();
               break;
            case NORMAL:
               GL11.glDisableClientState(32885);
            }
         }
      }

      var1.func_178965_a();
   }
}
