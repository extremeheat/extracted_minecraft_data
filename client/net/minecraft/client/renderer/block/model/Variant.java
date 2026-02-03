package net.minecraft.client.renderer.block.model;

import com.mojang.math.Quadrant;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.resources.Identifier;

public record Variant(Identifier modelLocation, SimpleModelState modelState) implements BlockModelPart.Unbaked {
   public static final MapCodec<Variant> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Identifier.CODEC.fieldOf("model").forGetter(Variant::modelLocation), Variant.SimpleModelState.MAP_CODEC.forGetter(Variant::modelState)).apply(i, Variant::new));
   public static final Codec<Variant> CODEC;

   public Variant(final Identifier modelLocation) {
      this(modelLocation, Variant.SimpleModelState.DEFAULT);
   }

   public Variant {
      super();
   }

   public Variant withXRot(final Quadrant x) {
      return this.withState(this.modelState.withX(x));
   }

   public Variant withYRot(final Quadrant y) {
      return this.withState(this.modelState.withY(y));
   }

   public Variant withZRot(final Quadrant z) {
      return this.withState(this.modelState.withZ(z));
   }

   public Variant withUvLock(final boolean uvLock) {
      return this.withState(this.modelState.withUvLock(uvLock));
   }

   public Variant withModel(final Identifier modelLocation) {
      return new Variant(modelLocation, this.modelState);
   }

   public Variant withState(final SimpleModelState modelState) {
      return new Variant(this.modelLocation, modelState);
   }

   public Variant with(final VariantMutator mutator) {
      return (Variant)mutator.apply(this);
   }

   public BlockModelPart bake(final ModelBaker modelBakery) {
      return SimpleModelWrapper.bake(modelBakery, this.modelLocation, this.modelState.asModelState());
   }

   public void resolveDependencies(final ResolvableModel.Resolver resolver) {
      resolver.markDependency(this.modelLocation);
   }

   static {
      CODEC = MAP_CODEC.codec();
   }

   public static record SimpleModelState(Quadrant x, Quadrant y, Quadrant z, boolean uvLock) {
      public static final MapCodec<SimpleModelState> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Quadrant.CODEC.optionalFieldOf("x", Quadrant.R0).forGetter(SimpleModelState::x), Quadrant.CODEC.optionalFieldOf("y", Quadrant.R0).forGetter(SimpleModelState::y), Quadrant.CODEC.optionalFieldOf("z", Quadrant.R0).forGetter(SimpleModelState::z), Codec.BOOL.optionalFieldOf("uvlock", false).forGetter(SimpleModelState::uvLock)).apply(i, SimpleModelState::new));
      public static final SimpleModelState DEFAULT;

      public SimpleModelState {
         super();
      }

      public ModelState asModelState() {
         BlockModelRotation rotation = BlockModelRotation.get(Quadrant.fromXYZAngles(this.x, this.y, this.z));
         return (ModelState)(this.uvLock ? rotation.withUvLock() : rotation);
      }

      public SimpleModelState withX(final Quadrant x) {
         return new SimpleModelState(x, this.y, this.z, this.uvLock);
      }

      public SimpleModelState withY(final Quadrant y) {
         return new SimpleModelState(this.x, y, this.z, this.uvLock);
      }

      public SimpleModelState withZ(final Quadrant z) {
         return new SimpleModelState(this.x, this.y, z, this.uvLock);
      }

      public SimpleModelState withUvLock(final boolean uvLock) {
         return new SimpleModelState(this.x, this.y, this.z, uvLock);
      }

      static {
         DEFAULT = new SimpleModelState(Quadrant.R0, Quadrant.R0, Quadrant.R0, false);
      }
   }
}
