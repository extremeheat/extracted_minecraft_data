package net.minecraft.client.renderer.item;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class CompositeModel implements ItemModel {
   private final List<ItemModel> models;

   public CompositeModel(List<ItemModel> var1) {
      super();
      this.models = var1;
   }

   public void update(ItemStackRenderState var1, ItemStack var2, ItemModelResolver var3, ItemDisplayContext var4, @Nullable ClientLevel var5, @Nullable LivingEntity var6, int var7) {
      var1.ensureCapacity(this.models.size());
      Iterator var8 = this.models.iterator();

      while(var8.hasNext()) {
         ItemModel var9 = (ItemModel)var8.next();
         var9.update(var1, var2, var3, var4, var5, var6, var7);
      }

   }

   public static record Unbaked(List<ItemModel.Unbaked> models) implements ItemModel.Unbaked {
      public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> {
         return var0.group(ItemModels.CODEC.listOf().fieldOf("models").forGetter(Unbaked::models)).apply(var0, Unbaked::new);
      });

      public Unbaked(List<ItemModel.Unbaked> var1) {
         super();
         this.models = var1;
      }

      public MapCodec<Unbaked> type() {
         return MAP_CODEC;
      }

      public void resolveDependencies(ResolvableModel.Resolver var1) {
         Iterator var2 = this.models.iterator();

         while(var2.hasNext()) {
            ItemModel.Unbaked var3 = (ItemModel.Unbaked)var2.next();
            var3.resolveDependencies(var1);
         }

      }

      public ItemModel bake(ItemModel.BakingContext var1) {
         return new CompositeModel(this.models.stream().map((var1x) -> {
            return var1x.bake(var1);
         }).toList());
      }

      public List<ItemModel.Unbaked> models() {
         return this.models;
      }
   }
}
