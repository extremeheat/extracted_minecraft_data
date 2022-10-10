package net.minecraft.client.renderer;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;

public class ItemModelMesher {
   public final Int2ObjectMap<ModelResourceLocation> field_199313_a = new Int2ObjectOpenHashMap(256);
   private final Int2ObjectMap<IBakedModel> field_199314_b = new Int2ObjectOpenHashMap(256);
   private final ModelManager field_178090_d;

   public ItemModelMesher(ModelManager var1) {
      super();
      this.field_178090_d = var1;
   }

   public TextureAtlasSprite func_199934_a(IItemProvider var1) {
      return this.func_199309_a(new ItemStack(var1));
   }

   public TextureAtlasSprite func_199309_a(ItemStack var1) {
      IBakedModel var2 = this.func_178089_a(var1);
      return (var2 == this.field_178090_d.func_174951_a() || var2.func_188618_c()) && var1.func_77973_b() instanceof ItemBlock ? this.field_178090_d.func_174954_c().func_178122_a(((ItemBlock)var1.func_77973_b()).func_179223_d().func_176223_P()) : var2.func_177554_e();
   }

   public IBakedModel func_178089_a(ItemStack var1) {
      IBakedModel var2 = this.func_199312_b(var1.func_77973_b());
      return var2 == null ? this.field_178090_d.func_174951_a() : var2;
   }

   @Nullable
   public IBakedModel func_199312_b(Item var1) {
      return (IBakedModel)this.field_199314_b.get(func_199310_c(var1));
   }

   private static int func_199310_c(Item var0) {
      return Item.func_150891_b(var0);
   }

   public void func_199311_a(Item var1, ModelResourceLocation var2) {
      this.field_199313_a.put(func_199310_c(var1), var2);
      this.field_199314_b.put(func_199310_c(var1), this.field_178090_d.func_174953_a(var2));
   }

   public ModelManager func_178083_a() {
      return this.field_178090_d;
   }

   public void func_178085_b() {
      this.field_199314_b.clear();
      ObjectIterator var1 = this.field_199313_a.entrySet().iterator();

      while(var1.hasNext()) {
         Entry var2 = (Entry)var1.next();
         this.field_199314_b.put((Integer)var2.getKey(), this.field_178090_d.func_174953_a((ModelResourceLocation)var2.getValue()));
      }

   }
}
