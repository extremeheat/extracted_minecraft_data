package net.minecraft.client.renderer.model.multipart;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelBlockDefinition;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.client.renderer.model.MultipartBakedModel;
import net.minecraft.client.renderer.model.VariantList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ResourceLocation;

public class Multipart implements IUnbakedModel {
   private final StateContainer<Block, IBlockState> field_188140_b;
   private final List<Selector> field_188139_a;

   public Multipart(StateContainer<Block, IBlockState> var1, List<Selector> var2) {
      super();
      this.field_188140_b = var1;
      this.field_188139_a = var2;
   }

   public List<Selector> func_188136_a() {
      return this.field_188139_a;
   }

   public Set<VariantList> func_188137_b() {
      HashSet var1 = Sets.newHashSet();
      Iterator var2 = this.field_188139_a.iterator();

      while(var2.hasNext()) {
         Selector var3 = (Selector)var2.next();
         var1.add(var3.func_188165_a());
      }

      return var1;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Multipart)) {
         return false;
      } else {
         Multipart var2 = (Multipart)var1;
         return Objects.equals(this.field_188140_b, var2.field_188140_b) && Objects.equals(this.field_188139_a, var2.field_188139_a);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.field_188140_b, this.field_188139_a});
   }

   public Collection<ResourceLocation> func_187965_e() {
      return (Collection)this.func_188136_a().stream().flatMap((var0) -> {
         return var0.func_188165_a().func_187965_e().stream();
      }).collect(Collectors.toSet());
   }

   public Collection<ResourceLocation> func_209559_a(Function<ResourceLocation, IUnbakedModel> var1, Set<String> var2) {
      return (Collection)this.func_188136_a().stream().flatMap((var2x) -> {
         return var2x.func_188165_a().func_209559_a(var1, var2).stream();
      }).collect(Collectors.toSet());
   }

   @Nullable
   public IBakedModel func_209558_a(Function<ResourceLocation, IUnbakedModel> var1, Function<ResourceLocation, TextureAtlasSprite> var2, ModelRotation var3, boolean var4) {
      MultipartBakedModel.Builder var5 = new MultipartBakedModel.Builder();
      Iterator var6 = this.func_188136_a().iterator();

      while(var6.hasNext()) {
         Selector var7 = (Selector)var6.next();
         IBakedModel var8 = var7.func_188165_a().func_209558_a(var1, var2, var3, var4);
         if (var8 != null) {
            var5.func_188648_a(var7.func_188166_a(this.field_188140_b), var8);
         }
      }

      return var5.func_188647_a();
   }

   public static class Deserializer implements JsonDeserializer<Multipart> {
      private final ModelBlockDefinition.ContainerHolder field_209584_a;

      public Deserializer(ModelBlockDefinition.ContainerHolder var1) {
         super();
         this.field_209584_a = var1;
      }

      public Multipart deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return new Multipart(this.field_209584_a.func_209574_a(), this.func_188133_a(var3, var1.getAsJsonArray()));
      }

      private List<Selector> func_188133_a(JsonDeserializationContext var1, JsonArray var2) {
         ArrayList var3 = Lists.newArrayList();
         Iterator var4 = var2.iterator();

         while(var4.hasNext()) {
            JsonElement var5 = (JsonElement)var4.next();
            var3.add(var1.deserialize(var5, Selector.class));
         }

         return var3;
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
