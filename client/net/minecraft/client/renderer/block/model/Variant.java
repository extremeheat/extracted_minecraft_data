package net.minecraft.client.renderer.block.model;

import com.mojang.math.Quadrant;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

public record Variant(ResourceLocation modelLocation, SimpleModelState modelState, int weight) {
   public static final Codec<Variant> CODEC = RecordCodecBuilder.create((var0) -> var0.group(ResourceLocation.CODEC.fieldOf("model").forGetter(Variant::modelLocation), Variant.SimpleModelState.MAP_CODEC.forGetter(Variant::modelState), ExtraCodecs.POSITIVE_INT.optionalFieldOf("weight", 1).forGetter(Variant::weight)).apply(var0, Variant::new));

   public Variant(ResourceLocation var1) {
      this(var1, Variant.SimpleModelState.DEFAULT, 1);
   }

   public Variant(ResourceLocation var1, SimpleModelState var2, int var3) {
      super();
      this.modelLocation = var1;
      this.modelState = var2;
      this.weight = var3;
   }

   public Variant withXRot(Quadrant var1) {
      return this.withState(this.modelState.withX(var1));
   }

   public Variant withYRot(Quadrant var1) {
      return this.withState(this.modelState.withY(var1));
   }

   public Variant withUvLock(boolean var1) {
      return this.withState(this.modelState.withUvLock(var1));
   }

   public Variant withModel(ResourceLocation var1) {
      return new Variant(var1, this.modelState, this.weight);
   }

   public Variant withState(SimpleModelState var1) {
      return new Variant(this.modelLocation, var1, this.weight);
   }

   public Variant withWeight(int var1) {
      return new Variant(this.modelLocation, this.modelState, var1);
   }

   public Variant with(VariantMutator var1) {
      return (Variant)var1.apply(this);
   }

   public static record SimpleModelState(Quadrant x, Quadrant y, boolean uvLock) {
      public static final MapCodec<SimpleModelState> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Quadrant.CODEC.optionalFieldOf("x", Quadrant.R0).forGetter(SimpleModelState::x), Quadrant.CODEC.optionalFieldOf("y", Quadrant.R0).forGetter(SimpleModelState::y), Codec.BOOL.optionalFieldOf("uvlock", false).forGetter(SimpleModelState::uvLock)).apply(var0, SimpleModelState::new));
      public static final SimpleModelState DEFAULT;

      public SimpleModelState(Quadrant var1, Quadrant var2, boolean var3) {
         super();
         this.x = var1;
         this.y = var2;
         this.uvLock = var3;
      }

      public ModelState asModelState() {
         BlockModelRotation var1 = BlockModelRotation.by(this.x, this.y);
         return (ModelState)(this.uvLock ? var1.withUvLock() : var1);
      }

      public SimpleModelState withX(Quadrant var1) {
         return new SimpleModelState(var1, this.y, this.uvLock);
      }

      public SimpleModelState withY(Quadrant var1) {
         return new SimpleModelState(this.x, var1, this.uvLock);
      }

      public SimpleModelState withUvLock(boolean var1) {
         return new SimpleModelState(this.x, this.y, var1);
      }

      static {
         DEFAULT = new SimpleModelState(Quadrant.R0, Quadrant.R0, false);
      }
   }
}
