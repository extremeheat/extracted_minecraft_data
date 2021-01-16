package com.mojang.datafixers;

import com.mojang.datafixers.optics.Optics;
import com.mojang.datafixers.optics.Proj1;
import com.mojang.datafixers.optics.profunctors.Cartesian;
import com.mojang.datafixers.optics.profunctors.Profunctor;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.Tag;
import com.mojang.datafixers.types.templates.TaggedChoice;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import javax.annotation.Nullable;

public final class FieldFinder<FT> implements OpticFinder<FT> {
   @Nullable
   private final String name;
   private final Type<FT> type;

   public FieldFinder(@Nullable String var1, Type<FT> var2) {
      super();
      this.name = var1;
      this.type = var2;
   }

   public Type<FT> type() {
      return this.type;
   }

   public <A, FR> Either<TypedOptic<A, ?, FT, FR>, Type.FieldNotFoundException> findType(Type<A> var1, Type<FR> var2, boolean var3) {
      return var1.findTypeCached(this.type, var2, new FieldFinder.Matcher(this.name, this.type, var2), var3);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof FieldFinder)) {
         return false;
      } else {
         FieldFinder var2 = (FieldFinder)var1;
         return Objects.equals(this.name, var2.name) && Objects.equals(this.type, var2.type);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.name, this.type});
   }

   private static final class Matcher<FT, FR> implements Type.TypeMatcher<FT, FR> {
      private final Type<FR> resultType;
      @Nullable
      private final String name;
      private final Type<FT> type;

      public Matcher(@Nullable String var1, Type<FT> var2, Type<FR> var3) {
         super();
         this.resultType = var3;
         this.name = var1;
         this.type = var2;
      }

      public <S> Either<TypedOptic<S, ?, FT, FR>, Type.FieldNotFoundException> match(Type<S> var1) {
         if (this.name == null && this.type.equals(var1, true, false)) {
            return Either.left(new TypedOptic(Profunctor.Mu.TYPE_TOKEN, var1, this.resultType, var1, this.resultType, Optics.id()));
         } else if (var1 instanceof Tag.TagType) {
            Tag.TagType var3 = (Tag.TagType)var1;
            if (!Objects.equals(var3.name(), this.name)) {
               return Either.right(new Type.FieldNotFoundException(String.format("Not found: \"%s\" (in type: %s)", this.name, var1)));
            } else {
               return !Objects.equals(this.type, var3.element()) ? Either.right(new Type.FieldNotFoundException(String.format("Type error for field \"%s\": expected type: %s, actual type: %s)", this.name, this.type, var3.element()))) : Either.left(new TypedOptic(Profunctor.Mu.TYPE_TOKEN, var3, DSL.field(var3.name(), this.resultType), this.type, this.resultType, Optics.id()));
            }
         } else {
            if (var1 instanceof TaggedChoice.TaggedChoiceType) {
               TaggedChoice.TaggedChoiceType var2 = (TaggedChoice.TaggedChoiceType)var1;
               if (Objects.equals(this.name, var2.getName())) {
                  if (!Objects.equals(this.type, var2.getKeyType())) {
                     return Either.right(new Type.FieldNotFoundException(String.format("Type error for field \"%s\": expected type: %s, actual type: %s)", this.name, this.type, var2.getKeyType())));
                  }

                  if (!Objects.equals(this.type, this.resultType)) {
                     return Either.right(new Type.FieldNotFoundException("TaggedChoiceType key type change is unsupported."));
                  }

                  return Either.left(this.capChoice(var2));
               }
            }

            return Either.right(new Type.Continue());
         }
      }

      private <V> TypedOptic<Pair<FT, V>, ?, FT, FT> capChoice(Type<?> var1) {
         return new TypedOptic(Cartesian.Mu.TYPE_TOKEN, var1, var1, this.type, this.type, new Proj1());
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            FieldFinder.Matcher var2 = (FieldFinder.Matcher)var1;
            return Objects.equals(this.resultType, var2.resultType) && Objects.equals(this.name, var2.name) && Objects.equals(this.type, var2.type);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.resultType, this.name, this.type});
      }
   }
}
