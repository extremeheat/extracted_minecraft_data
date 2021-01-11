package net.minecraft.client.resources.model;

import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.IRegistry;

public class ModelManager implements IResourceManagerReloadListener {
   private IRegistry<ModelResourceLocation, IBakedModel> field_174958_a;
   private final TextureMap field_174956_b;
   private final BlockModelShapes field_174957_c;
   private IBakedModel field_174955_d;

   public ModelManager(TextureMap var1) {
      super();
      this.field_174956_b = var1;
      this.field_174957_c = new BlockModelShapes(this);
   }

   public void func_110549_a(IResourceManager var1) {
      ModelBakery var2 = new ModelBakery(var1, this.field_174956_b, this.field_174957_c);
      this.field_174958_a = var2.func_177570_a();
      this.field_174955_d = (IBakedModel)this.field_174958_a.func_82594_a(ModelBakery.field_177604_a);
      this.field_174957_c.func_178124_c();
   }

   public IBakedModel func_174953_a(ModelResourceLocation var1) {
      if (var1 == null) {
         return this.field_174955_d;
      } else {
         IBakedModel var2 = (IBakedModel)this.field_174958_a.func_82594_a(var1);
         return var2 == null ? this.field_174955_d : var2;
      }
   }

   public IBakedModel func_174951_a() {
      return this.field_174955_d;
   }

   public TextureMap func_174952_b() {
      return this.field_174956_b;
   }

   public BlockModelShapes func_174954_c() {
      return this.field_174957_c;
   }
}
