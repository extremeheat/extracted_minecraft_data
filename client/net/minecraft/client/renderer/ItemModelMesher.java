package net.minecraft.client.renderer;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemModelMesher {
   private final Map<Integer, ModelResourceLocation> field_178093_a = Maps.newHashMap();
   private final Map<Integer, IBakedModel> field_178091_b = Maps.newHashMap();
   private final Map<Item, ItemMeshDefinition> field_178092_c = Maps.newHashMap();
   private final ModelManager field_178090_d;

   public ItemModelMesher(ModelManager var1) {
      super();
      this.field_178090_d = var1;
   }

   public TextureAtlasSprite func_178082_a(Item var1) {
      return this.func_178087_a(var1, 0);
   }

   public TextureAtlasSprite func_178087_a(Item var1, int var2) {
      return this.func_178089_a(new ItemStack(var1, 1, var2)).func_177554_e();
   }

   public IBakedModel func_178089_a(ItemStack var1) {
      Item var2 = var1.func_77973_b();
      IBakedModel var3 = this.func_178088_b(var2, this.func_178084_b(var1));
      if (var3 == null) {
         ItemMeshDefinition var4 = (ItemMeshDefinition)this.field_178092_c.get(var2);
         if (var4 != null) {
            var3 = this.field_178090_d.func_174953_a(var4.func_178113_a(var1));
         }
      }

      if (var3 == null) {
         var3 = this.field_178090_d.func_174951_a();
      }

      return var3;
   }

   protected int func_178084_b(ItemStack var1) {
      return var1.func_77984_f() ? 0 : var1.func_77960_j();
   }

   protected IBakedModel func_178088_b(Item var1, int var2) {
      return (IBakedModel)this.field_178091_b.get(this.func_178081_c(var1, var2));
   }

   private int func_178081_c(Item var1, int var2) {
      return Item.func_150891_b(var1) << 16 | var2;
   }

   public void func_178086_a(Item var1, int var2, ModelResourceLocation var3) {
      this.field_178093_a.put(this.func_178081_c(var1, var2), var3);
      this.field_178091_b.put(this.func_178081_c(var1, var2), this.field_178090_d.func_174953_a(var3));
   }

   public void func_178080_a(Item var1, ItemMeshDefinition var2) {
      this.field_178092_c.put(var1, var2);
   }

   public ModelManager func_178083_a() {
      return this.field_178090_d;
   }

   public void func_178085_b() {
      this.field_178091_b.clear();
      Iterator var1 = this.field_178093_a.entrySet().iterator();

      while(var1.hasNext()) {
         Entry var2 = (Entry)var1.next();
         this.field_178091_b.put(var2.getKey(), this.field_178090_d.func_174953_a((ModelResourceLocation)var2.getValue()));
      }

   }
}
