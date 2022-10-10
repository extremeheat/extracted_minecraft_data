package net.minecraft.client.renderer;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.state.IProperty;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;

public class BlockModelShapes {
   private final Map<IBlockState, IBakedModel> field_178129_a = Maps.newIdentityHashMap();
   private final ModelManager field_178128_c;

   public BlockModelShapes(ModelManager var1) {
      super();
      this.field_178128_c = var1;
   }

   public TextureAtlasSprite func_178122_a(IBlockState var1) {
      return this.func_178125_b(var1).func_177554_e();
   }

   public IBakedModel func_178125_b(IBlockState var1) {
      IBakedModel var2 = (IBakedModel)this.field_178129_a.get(var1);
      if (var2 == null) {
         var2 = this.field_178128_c.func_174951_a();
      }

      return var2;
   }

   public ModelManager func_178126_b() {
      return this.field_178128_c;
   }

   public void func_178124_c() {
      this.field_178129_a.clear();
      Iterator var1 = IRegistry.field_212618_g.iterator();

      while(var1.hasNext()) {
         Block var2 = (Block)var1.next();
         var2.func_176194_O().func_177619_a().forEach((var1x) -> {
            IBakedModel var10000 = (IBakedModel)this.field_178129_a.put(var1x, this.field_178128_c.func_174953_a(func_209554_c(var1x)));
         });
      }

   }

   public static ModelResourceLocation func_209554_c(IBlockState var0) {
      return func_209553_a(IRegistry.field_212618_g.func_177774_c(var0.func_177230_c()), var0);
   }

   public static ModelResourceLocation func_209553_a(ResourceLocation var0, IBlockState var1) {
      return new ModelResourceLocation(var0, func_209552_a(var1.func_206871_b()));
   }

   public static String func_209552_a(Map<IProperty<?>, Comparable<?>> var0) {
      StringBuilder var1 = new StringBuilder();
      Iterator var2 = var0.entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         if (var1.length() != 0) {
            var1.append(',');
         }

         IProperty var4 = (IProperty)var3.getKey();
         var1.append(var4.func_177701_a());
         var1.append('=');
         var1.append(func_209555_a(var4, (Comparable)var3.getValue()));
      }

      return var1.toString();
   }

   private static <T extends Comparable<T>> String func_209555_a(IProperty<T> var0, Comparable<?> var1) {
      return var0.func_177702_a(var1);
   }
}
