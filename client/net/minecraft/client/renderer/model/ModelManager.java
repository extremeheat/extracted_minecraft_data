package net.minecraft.client.renderer.model;

import java.util.Map;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;

public class ModelManager implements IResourceManagerReloadListener {
   private Map<ModelResourceLocation, IBakedModel> field_174958_a;
   private final TextureMap field_174956_b;
   private final BlockModelShapes field_174957_c;
   private IBakedModel field_174955_d;

   public ModelManager(TextureMap var1) {
      super();
      this.field_174956_b = var1;
      this.field_174957_c = new BlockModelShapes(this);
   }

   public void func_195410_a(IResourceManager var1) {
      ModelBakery var2 = new ModelBakery(var1, this.field_174956_b);
      this.field_174958_a = var2.func_177570_a();
      this.field_174955_d = (IBakedModel)this.field_174958_a.get(ModelBakery.field_177604_a);
      this.field_174957_c.func_178124_c();
   }

   public IBakedModel func_174953_a(ModelResourceLocation var1) {
      return (IBakedModel)this.field_174958_a.getOrDefault(var1, this.field_174955_d);
   }

   public IBakedModel func_174951_a() {
      return this.field_174955_d;
   }

   public BlockModelShapes func_174954_c() {
      return this.field_174957_c;
   }
}
