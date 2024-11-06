package net.minecraft.client.renderer.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperties;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class SelectItemModel<T> implements ItemModel {
   private final SelectItemModelProperty<T> property;
   private final Object2ObjectMap<T, ItemModel> models;

   public SelectItemModel(SelectItemModelProperty<T> var1, Object2ObjectMap<T, ItemModel> var2) {
      super();
      this.property = var1;
      this.models = var2;
   }

   public void update(ItemStackRenderState var1, ItemStack var2, ItemModelResolver var3, ItemDisplayContext var4, @Nullable ClientLevel var5, @Nullable LivingEntity var6, int var7) {
      Object var8 = this.property.get(var2, var5, var6, var7, var4);
      ItemModel var9 = (ItemModel)this.models.get(var8);
      if (var9 != null) {
         var9.update(var1, var2, var3, var4, var5, var6, var7);
      }

   }

   public static record SwitchCase<T>(List<T> values, ItemModel.Unbaked model) {
      final List<T> values;
      final ItemModel.Unbaked model;

      public SwitchCase(List<T> var1, ItemModel.Unbaked var2) {
         super();
         this.values = var1;
         this.model = var2;
      }

      public static <T> Codec<SwitchCase<T>> codec(Codec<T> var0) {
         return RecordCodecBuilder.create((var1) -> {
            return var1.group(ExtraCodecs.nonEmptyList(ExtraCodecs.compactListCodec(var0)).fieldOf("when").forGetter(SwitchCase::values), ItemModels.CODEC.fieldOf("model").forGetter(SwitchCase::model)).apply(var1, SwitchCase::new);
         });
      }

      public List<T> values() {
         return this.values;
      }

      public ItemModel.Unbaked model() {
         return this.model;
      }
   }

   public static record UnbakedSwitch<P extends SelectItemModelProperty<T>, T>(P property, List<SwitchCase<T>> cases) {
      public static final MapCodec<UnbakedSwitch<?, ?>> MAP_CODEC;

      public UnbakedSwitch(P var1, List<SwitchCase<T>> var2) {
         super();
         this.property = var1;
         this.cases = var2;
      }

      public ItemModel bake(ItemModel.BakingContext var1, ItemModel var2) {
         Object2ObjectOpenHashMap var3 = new Object2ObjectOpenHashMap();
         Iterator var4 = this.cases.iterator();

         while(var4.hasNext()) {
            SwitchCase var5 = (SwitchCase)var4.next();
            ItemModel.Unbaked var6 = var5.model;
            ItemModel var7 = var6.bake(var1);
            Iterator var8 = var5.values.iterator();

            while(var8.hasNext()) {
               Object var9 = var8.next();
               var3.put(var9, var7);
            }
         }

         var3.defaultReturnValue(var2);
         return new SelectItemModel(this.property, var3);
      }

      public void resolveDependencies(ResolvableModel.Resolver var1) {
         Iterator var2 = this.cases.iterator();

         while(var2.hasNext()) {
            SwitchCase var3 = (SwitchCase)var2.next();
            var3.model.resolveDependencies(var1);
         }

      }

      public P property() {
         return this.property;
      }

      public List<SwitchCase<T>> cases() {
         return this.cases;
      }

      static {
         MAP_CODEC = SelectItemModelProperties.CODEC.dispatchMap("property", (var0) -> {
            return var0.property().type();
         }, SelectItemModelProperty.Type::switchCodec);
      }
   }

   public static record Unbaked(UnbakedSwitch<?, ?> unbakedSwitch, Optional<ItemModel.Unbaked> fallback) implements ItemModel.Unbaked {
      public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> {
         return var0.group(SelectItemModel.UnbakedSwitch.MAP_CODEC.forGetter(Unbaked::unbakedSwitch), ItemModels.CODEC.optionalFieldOf("fallback").forGetter(Unbaked::fallback)).apply(var0, Unbaked::new);
      });

      public Unbaked(UnbakedSwitch<?, ?> var1, Optional<ItemModel.Unbaked> var2) {
         super();
         this.unbakedSwitch = var1;
         this.fallback = var2;
      }

      public MapCodec<Unbaked> type() {
         return MAP_CODEC;
      }

      public ItemModel bake(ItemModel.BakingContext var1) {
         ItemModel var2 = (ItemModel)this.fallback.map((var1x) -> {
            return var1x.bake(var1);
         }).orElse(var1.missingItemModel());
         return this.unbakedSwitch.bake(var1, var2);
      }

      public void resolveDependencies(ResolvableModel.Resolver var1) {
         this.unbakedSwitch.resolveDependencies(var1);
         this.fallback.ifPresent((var1x) -> {
            var1x.resolveDependencies(var1);
         });
      }

      public UnbakedSwitch<?, ?> unbakedSwitch() {
         return this.unbakedSwitch;
      }

      public Optional<ItemModel.Unbaked> fallback() {
         return this.fallback;
      }
   }
}
