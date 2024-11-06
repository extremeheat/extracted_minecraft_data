package net.minecraft.client.renderer.item;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperties;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class ConditionalItemModel implements ItemModel {
   private final ConditionalItemModelProperty property;
   private final ItemModel onTrue;
   private final ItemModel onFalse;

   public ConditionalItemModel(ConditionalItemModelProperty var1, ItemModel var2, ItemModel var3) {
      super();
      this.property = var1;
      this.onTrue = var2;
      this.onFalse = var3;
   }

   public void update(ItemStackRenderState var1, ItemStack var2, ItemModelResolver var3, ItemDisplayContext var4, @Nullable ClientLevel var5, @Nullable LivingEntity var6, int var7) {
      (this.property.get(var2, var5, var6, var7) ? this.onTrue : this.onFalse).update(var1, var2, var3, var4, var5, var6, var7);
   }

   public static record Unbaked(ConditionalItemModelProperty property, ItemModel.Unbaked onTrue, ItemModel.Unbaked onFalse) implements ItemModel.Unbaked {
      public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> {
         return var0.group(ConditionalItemModelProperties.MAP_CODEC.forGetter(Unbaked::property), ItemModels.CODEC.fieldOf("on_true").forGetter(Unbaked::onTrue), ItemModels.CODEC.fieldOf("on_false").forGetter(Unbaked::onFalse)).apply(var0, Unbaked::new);
      });

      public Unbaked(ConditionalItemModelProperty var1, ItemModel.Unbaked var2, ItemModel.Unbaked var3) {
         super();
         this.property = var1;
         this.onTrue = var2;
         this.onFalse = var3;
      }

      public MapCodec<Unbaked> type() {
         return MAP_CODEC;
      }

      public ItemModel bake(ItemModel.BakingContext var1) {
         return new ConditionalItemModel(this.property, this.onTrue.bake(var1), this.onFalse.bake(var1));
      }

      public void resolveDependencies(ResolvableModel.Resolver var1) {
         this.onTrue.resolveDependencies(var1);
         this.onFalse.resolveDependencies(var1);
      }

      public ConditionalItemModelProperty property() {
         return this.property;
      }

      public ItemModel.Unbaked onTrue() {
         return this.onTrue;
      }

      public ItemModel.Unbaked onFalse() {
         return this.onFalse;
      }
   }
}
