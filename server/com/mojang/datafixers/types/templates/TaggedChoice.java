package com.mojang.datafixers.types.templates;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.FamilyOptic;
import com.mojang.datafixers.FunctionType;
import com.mojang.datafixers.RewriteResult;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.TypedOptic;
import com.mojang.datafixers.View;
import com.mojang.datafixers.functions.Functions;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.kinds.K1;
import com.mojang.datafixers.optics.Affine;
import com.mojang.datafixers.optics.Lens;
import com.mojang.datafixers.optics.Optic;
import com.mojang.datafixers.optics.Optics;
import com.mojang.datafixers.optics.Traversal;
import com.mojang.datafixers.optics.profunctors.AffineP;
import com.mojang.datafixers.optics.profunctors.Cartesian;
import com.mojang.datafixers.optics.profunctors.TraversalP;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.families.RecursiveTypeFamily;
import com.mojang.datafixers.types.families.TypeFamily;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.codecs.KeyDispatchCodec;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.IntFunction;
import javax.annotation.Nullable;

public final class TaggedChoice<K> implements TypeTemplate {
   private final String name;
   private final Type<K> keyType;
   private final Map<K, TypeTemplate> templates;
   private final Map<Pair<TypeFamily, Integer>, Type<?>> types = Maps.newConcurrentMap();
   private final int size;

   public TaggedChoice(String var1, Type<K> var2, Map<K, TypeTemplate> var3) {
      super();
      this.name = var1;
      this.keyType = var2;
      this.templates = var3;
      this.size = var3.values().stream().mapToInt(TypeTemplate::size).max().orElse(0);
   }

   public int size() {
      return this.size;
   }

   public TypeFamily apply(TypeFamily var1) {
      return (var2) -> {
         return (Type)this.types.computeIfAbsent(Pair.of(var1, var2), (var1x) -> {
            return DSL.taggedChoiceType(this.name, this.keyType, (Map)this.templates.entrySet().stream().map((var1) -> {
               return Pair.of(var1.getKey(), ((TypeTemplate)var1.getValue()).apply((TypeFamily)var1x.getFirst()).apply((Integer)var1x.getSecond()));
            }).collect(Pair.toMap()));
         });
      };
   }

   public <A, B> FamilyOptic<A, B> applyO(FamilyOptic<A, B> var1, Type<A> var2, Type<B> var3) {
      throw new UnsupportedOperationException();
   }

   public <A, B> Either<TypeTemplate, Type.FieldNotFoundException> findFieldOrType(int var1, @Nullable String var2, Type<A> var3, Type<B> var4) {
      return Either.right(new Type.FieldNotFoundException("Not implemented"));
   }

   public IntFunction<RewriteResult<?, ?>> hmap(TypeFamily var1, IntFunction<RewriteResult<?, ?>> var2) {
      return (var3) -> {
         RewriteResult var4 = RewriteResult.nop((TaggedChoice.TaggedChoiceType)this.apply(var1).apply(var3));

         Entry var6;
         RewriteResult var7;
         for(Iterator var5 = this.templates.entrySet().iterator(); var5.hasNext(); var4 = TaggedChoice.TaggedChoiceType.elementResult(var6.getKey(), (TaggedChoice.TaggedChoiceType)var4.view().newType(), var7).compose(var4)) {
            var6 = (Entry)var5.next();
            var7 = (RewriteResult)((TypeTemplate)var6.getValue()).hmap(var1, var2).apply(var3);
         }

         return var4;
      };
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof TaggedChoice)) {
         return false;
      } else {
         TaggedChoice var2 = (TaggedChoice)var1;
         return Objects.equals(this.name, var2.name) && Objects.equals(this.keyType, var2.keyType) && Objects.equals(this.templates, var2.templates);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.name, this.keyType, this.templates});
   }

   public String toString() {
      return "TaggedChoice[" + this.name + ", " + Joiner.on(", ").withKeyValueSeparator(" -> ").join(this.templates) + "]";
   }

   public static final class TaggedChoiceType<K> extends Type<Pair<K, ?>> {
      private final String name;
      private final Type<K> keyType;
      protected final Map<K, Type<?>> types;
      private final int hashCode;

      public TaggedChoiceType(String var1, Type<K> var2, Map<K, Type<?>> var3) {
         super();
         this.name = var1;
         this.keyType = var2;
         this.types = var3;
         this.hashCode = Objects.hash(new Object[]{var1, var2, var3});
      }

      public RewriteResult<Pair<K, ?>, ?> all(TypeRewriteRule var1, boolean var2, boolean var3) {
         Map var4 = (Map)this.types.entrySet().stream().map((var1x) -> {
            return var1.rewrite((Type)var1x.getValue()).map((var1xx) -> {
               return Pair.of(var1x.getKey(), var1xx);
            });
         }).filter((var0) -> {
            return var0.isPresent() && !Objects.equals(((RewriteResult)((Pair)var0.get()).getSecond()).view().function(), Functions.id());
         }).map(Optional::get).collect(Pair.toMap());
         if (var4.isEmpty()) {
            return RewriteResult.nop(this);
         } else if (var4.size() == 1) {
            Entry var9 = (Entry)var4.entrySet().iterator().next();
            return elementResult(var9.getKey(), this, (RewriteResult)var9.getValue());
         } else {
            HashMap var5 = Maps.newHashMap(this.types);
            BitSet var6 = new BitSet();
            Iterator var7 = var4.entrySet().iterator();

            while(var7.hasNext()) {
               Entry var8 = (Entry)var7.next();
               var5.put(var8.getKey(), ((RewriteResult)var8.getValue()).view().newType());
               var6.or(((RewriteResult)var8.getValue()).recData());
            }

            return RewriteResult.create(View.create(this, DSL.taggedChoiceType(this.name, this.keyType, var5), Functions.fun("TaggedChoiceTypeRewriteResult " + var4.size(), new TaggedChoice.TaggedChoiceType.RewriteFunc(var4))), var6);
         }
      }

      public static <K, FT, FR> RewriteResult<Pair<K, ?>, Pair<K, ?>> elementResult(K var0, TaggedChoice.TaggedChoiceType<K> var1, RewriteResult<FT, FR> var2) {
         return opticView(var1, var2, TypedOptic.tagged(var1, var0, var2.view().type(), var2.view().newType()));
      }

      public Optional<RewriteResult<Pair<K, ?>, ?>> one(TypeRewriteRule var1) {
         Iterator var2 = this.types.entrySet().iterator();

         Entry var3;
         Optional var4;
         do {
            if (!var2.hasNext()) {
               return Optional.empty();
            }

            var3 = (Entry)var2.next();
            var4 = var1.rewrite((Type)var3.getValue());
         } while(!var4.isPresent());

         return Optional.of(elementResult(var3.getKey(), this, (RewriteResult)var4.get()));
      }

      public Type<?> updateMu(RecursiveTypeFamily var1) {
         return DSL.taggedChoiceType(this.name, this.keyType, (Map)this.types.entrySet().stream().map((var1x) -> {
            return Pair.of(var1x.getKey(), ((Type)var1x.getValue()).updateMu(var1));
         }).collect(Pair.toMap()));
      }

      public TypeTemplate buildTemplate() {
         return DSL.taggedChoice(this.name, this.keyType, (Map)this.types.entrySet().stream().map((var0) -> {
            return Pair.of(var0.getKey(), ((Type)var0.getValue()).template());
         }).collect(Pair.toMap()));
      }

      private <V> DataResult<? extends Encoder<Pair<K, ?>>> encoder(Pair<K, V> var1) {
         return this.getCodec(var1.getFirst()).map((var0) -> {
            return var0.comap((var0x) -> {
               return var0x.getSecond();
            });
         });
      }

      protected Codec<Pair<K, ?>> buildCodec() {
         return KeyDispatchCodec.unsafe(this.name, this.keyType.codec(), (var0) -> {
            return DataResult.success(var0.getFirst());
         }, (var1) -> {
            return this.getCodec(var1).map((var1x) -> {
               return var1x.map((var1xx) -> {
                  return Pair.of(var1, var1xx);
               });
            });
         }, this::encoder).codec();
      }

      private DataResult<? extends Codec<?>> getCodec(K var1) {
         return (DataResult)Optional.ofNullable(this.types.get(var1)).map((var0) -> {
            return DataResult.success(var0.codec());
         }).orElseGet(() -> {
            return DataResult.error("Unsupported key: " + var1);
         });
      }

      public Optional<Type<?>> findFieldTypeOpt(String var1) {
         return this.types.values().stream().map((var1x) -> {
            return var1x.findFieldTypeOpt(var1);
         }).filter(Optional::isPresent).findFirst().flatMap(Function.identity());
      }

      public Optional<Pair<K, ?>> point(DynamicOps<?> var1) {
         return this.types.entrySet().stream().map((var1x) -> {
            return ((Type)var1x.getValue()).point(var1).map((var1xx) -> {
               return Pair.of(var1x.getKey(), var1xx);
            });
         }).filter(Optional::isPresent).findFirst().flatMap(Function.identity()).map((var0) -> {
            return var0;
         });
      }

      public Optional<Typed<Pair<K, ?>>> point(DynamicOps<?> var1, K var2, Object var3) {
         return !this.types.containsKey(var2) ? Optional.empty() : Optional.of(new Typed(this, var1, Pair.of(var2, var3)));
      }

      public <FT, FR> Either<TypedOptic<Pair<K, ?>, ?, FT, FR>, Type.FieldNotFoundException> findTypeInChildren(Type<FT> var1, Type<FR> var2, Type.TypeMatcher<FT, FR> var3, boolean var4) {
         final Map var5 = (Map)this.types.entrySet().stream().map((var4x) -> {
            return Pair.of(var4x.getKey(), ((Type)var4x.getValue()).findType(var1, var2, var3, var4));
         }).filter((var0) -> {
            return ((Either)var0.getSecond()).left().isPresent();
         }).map((var0) -> {
            return var0.mapSecond((var0x) -> {
               return (TypedOptic)var0x.left().get();
            });
         }).collect(Pair.toMap());
         if (var5.isEmpty()) {
            return Either.right(new Type.FieldNotFoundException("Not found in any choices"));
         } else if (var5.size() == 1) {
            Entry var10 = (Entry)var5.entrySet().iterator().next();
            return Either.left(this.cap(this, var10.getKey(), (TypedOptic)var10.getValue()));
         } else {
            HashSet var6 = Sets.newHashSet();
            var5.values().forEach((var1x) -> {
               var6.addAll(var1x.bounds());
            });
            Object var7;
            TypeToken var8;
            if (TypedOptic.instanceOf(var6, Cartesian.Mu.TYPE_TOKEN) && var5.size() == this.types.size()) {
               var8 = Cartesian.Mu.TYPE_TOKEN;
               var7 = new Lens<Pair<K, ?>, Pair<K, ?>, FT, FR>() {
                  public FT view(Pair<K, ?> var1) {
                     TypedOptic var2 = (TypedOptic)var5.get(var1.getFirst());
                     return this.capView(var1, var2);
                  }

                  private <S, T> FT capView(Pair<K, ?> var1, TypedOptic<S, T, FT, FR> var2) {
                     return Optics.toLens((Optic)var2.upCast(Cartesian.Mu.TYPE_TOKEN).orElseThrow(IllegalArgumentException::new)).view(var1.getSecond());
                  }

                  public Pair<K, ?> update(FR var1, Pair<K, ?> var2) {
                     TypedOptic var3 = (TypedOptic)var5.get(var2.getFirst());
                     return this.capUpdate(var1, var2, var3);
                  }

                  private <S, T> Pair<K, ?> capUpdate(FR var1, Pair<K, ?> var2, TypedOptic<S, T, FT, FR> var3) {
                     return Pair.of(var2.getFirst(), Optics.toLens((Optic)var3.upCast(Cartesian.Mu.TYPE_TOKEN).orElseThrow(IllegalArgumentException::new)).update(var1, var2.getSecond()));
                  }
               };
            } else if (TypedOptic.instanceOf(var6, AffineP.Mu.TYPE_TOKEN)) {
               var8 = AffineP.Mu.TYPE_TOKEN;
               var7 = new Affine<Pair<K, ?>, Pair<K, ?>, FT, FR>() {
                  public Either<Pair<K, ?>, FT> preview(Pair<K, ?> var1) {
                     if (!var5.containsKey(var1.getFirst())) {
                        return Either.left(var1);
                     } else {
                        TypedOptic var2 = (TypedOptic)var5.get(var1.getFirst());
                        return this.capPreview(var1, var2);
                     }
                  }

                  private <S, T> Either<Pair<K, ?>, FT> capPreview(Pair<K, ?> var1, TypedOptic<S, T, FT, FR> var2) {
                     return Optics.toAffine((Optic)var2.upCast(AffineP.Mu.TYPE_TOKEN).orElseThrow(IllegalArgumentException::new)).preview(var1.getSecond()).mapLeft((var1x) -> {
                        return Pair.of(var1.getFirst(), var1x);
                     });
                  }

                  public Pair<K, ?> set(FR var1, Pair<K, ?> var2) {
                     if (!var5.containsKey(var2.getFirst())) {
                        return var2;
                     } else {
                        TypedOptic var3 = (TypedOptic)var5.get(var2.getFirst());
                        return this.capSet(var1, var2, var3);
                     }
                  }

                  private <S, T> Pair<K, ?> capSet(FR var1, Pair<K, ?> var2, TypedOptic<S, T, FT, FR> var3) {
                     return Pair.of(var2.getFirst(), Optics.toAffine((Optic)var3.upCast(AffineP.Mu.TYPE_TOKEN).orElseThrow(IllegalArgumentException::new)).set(var1, var2.getSecond()));
                  }
               };
            } else {
               if (!TypedOptic.instanceOf(var6, TraversalP.Mu.TYPE_TOKEN)) {
                  throw new IllegalStateException("Could not merge TaggedChoiceType optics, unknown bound: " + Arrays.toString(var6.toArray()));
               }

               var8 = TraversalP.Mu.TYPE_TOKEN;
               var7 = new Traversal<Pair<K, ?>, Pair<K, ?>, FT, FR>() {
                  public <F extends K1> FunctionType<Pair<K, ?>, App<F, Pair<K, ?>>> wander(Applicative<F, ?> var1, FunctionType<FT, App<F, FR>> var2) {
                     return (var4) -> {
                        if (!var5.containsKey(var4.getFirst())) {
                           return var1.point(var4);
                        } else {
                           TypedOptic var5x = (TypedOptic)var5.get(var4.getFirst());
                           return this.capTraversal(var1, var2, var4, var5x);
                        }
                     };
                  }

                  private <S, T, F extends K1> App<F, Pair<K, ?>> capTraversal(Applicative<F, ?> var1, FunctionType<FT, App<F, FR>> var2, Pair<K, ?> var3, TypedOptic<S, T, FT, FR> var4) {
                     Traversal var5x = Optics.toTraversal((Optic)var4.upCast(TraversalP.Mu.TYPE_TOKEN).orElseThrow(IllegalArgumentException::new));
                     return var1.ap((var1x) -> {
                        return Pair.of(var3.getFirst(), var1x);
                     }, (App)var5x.wander(var1, var2).apply(var3.getSecond()));
                  }
               };
            }

            Map var9 = (Map)this.types.entrySet().stream().map((var1x) -> {
               return Pair.of(var1x.getKey(), var5.containsKey(var1x.getKey()) ? ((TypedOptic)var5.get(var1x.getKey())).tType() : (Type)var1x.getValue());
            }).collect(Pair.toMap());
            return Either.left(new TypedOptic(var8, this, DSL.taggedChoiceType(this.name, this.keyType, var9), var1, var2, (Optic)var7));
         }
      }

      private <S, T, FT, FR> TypedOptic<Pair<K, ?>, Pair<K, ?>, FT, FR> cap(TaggedChoice.TaggedChoiceType<K> var1, K var2, TypedOptic<S, T, FT, FR> var3) {
         return TypedOptic.tagged(var1, var2, var3.sType(), var3.tType()).compose(var3);
      }

      public Optional<TaggedChoice.TaggedChoiceType<?>> findChoiceType(String var1, int var2) {
         return Objects.equals(var1, this.name) ? Optional.of(this) : Optional.empty();
      }

      public Optional<Type<?>> findCheckedType(int var1) {
         return this.types.values().stream().map((var1x) -> {
            return var1x.findCheckedType(var1);
         }).filter(Optional::isPresent).findFirst().flatMap(Function.identity());
      }

      public boolean equals(Object var1, boolean var2, boolean var3) {
         if (this == var1) {
            return true;
         } else if (!(var1 instanceof TaggedChoice.TaggedChoiceType)) {
            return false;
         } else {
            TaggedChoice.TaggedChoiceType var4 = (TaggedChoice.TaggedChoiceType)var1;
            if (!Objects.equals(this.name, var4.name)) {
               return false;
            } else if (!this.keyType.equals(var4.keyType, var2, var3)) {
               return false;
            } else if (this.types.size() != var4.types.size()) {
               return false;
            } else {
               Iterator var5 = this.types.entrySet().iterator();

               Entry var6;
               do {
                  if (!var5.hasNext()) {
                     return true;
                  }

                  var6 = (Entry)var5.next();
               } while(((Type)var6.getValue()).equals(var4.types.get(var6.getKey()), var2, var3));

               return false;
            }
         }
      }

      public int hashCode() {
         return this.hashCode;
      }

      public String toString() {
         return "TaggedChoiceType[" + this.name + ", " + Joiner.on(", \n").withKeyValueSeparator(" -> ").join(this.types) + "]\n";
      }

      public String getName() {
         return this.name;
      }

      public Type<K> getKeyType() {
         return this.keyType;
      }

      public boolean hasType(K var1) {
         return this.types.containsKey(var1);
      }

      public Map<K, Type<?>> types() {
         return this.types;
      }

      private static final class RewriteFunc<K> implements Function<DynamicOps<?>, Function<Pair<K, ?>, Pair<K, ?>>> {
         private final Map<K, ? extends RewriteResult<?, ?>> results;

         public RewriteFunc(Map<K, ? extends RewriteResult<?, ?>> var1) {
            super();
            this.results = var1;
         }

         public FunctionType<Pair<K, ?>, Pair<K, ?>> apply(DynamicOps<?> var1) {
            return (var2) -> {
               RewriteResult var3 = (RewriteResult)this.results.get(var2.getFirst());
               return var3 == null ? var2 : this.capRuleApply(var1, var2, var3);
            };
         }

         private <A, B> Pair<K, B> capRuleApply(DynamicOps<?> var1, Pair<K, ?> var2, RewriteResult<A, B> var3) {
            return var2.mapSecond((var2x) -> {
               return ((Function)var3.view().function().evalCached().apply(var1)).apply(var2x);
            });
         }

         public boolean equals(Object var1) {
            if (this == var1) {
               return true;
            } else if (var1 != null && this.getClass() == var1.getClass()) {
               TaggedChoice.TaggedChoiceType.RewriteFunc var2 = (TaggedChoice.TaggedChoiceType.RewriteFunc)var1;
               return Objects.equals(this.results, var2.results);
            } else {
               return false;
            }
         }

         public int hashCode() {
            return Objects.hash(new Object[]{this.results});
         }
      }
   }
}
