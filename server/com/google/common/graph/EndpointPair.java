package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;
import javax.annotation.Nullable;

@Beta
public abstract class EndpointPair<N> implements Iterable<N> {
   private final N nodeU;
   private final N nodeV;

   private EndpointPair(N var1, N var2) {
      super();
      this.nodeU = Preconditions.checkNotNull(var1);
      this.nodeV = Preconditions.checkNotNull(var2);
   }

   public static <N> EndpointPair<N> ordered(N var0, N var1) {
      return new EndpointPair.Ordered(var0, var1);
   }

   public static <N> EndpointPair<N> unordered(N var0, N var1) {
      return new EndpointPair.Unordered(var1, var0);
   }

   static <N> EndpointPair<N> of(Graph<?> var0, N var1, N var2) {
      return var0.isDirected() ? ordered(var1, var2) : unordered(var1, var2);
   }

   static <N> EndpointPair<N> of(Network<?, ?> var0, N var1, N var2) {
      return var0.isDirected() ? ordered(var1, var2) : unordered(var1, var2);
   }

   public abstract N source();

   public abstract N target();

   public final N nodeU() {
      return this.nodeU;
   }

   public final N nodeV() {
      return this.nodeV;
   }

   public final N adjacentNode(Object var1) {
      if (var1.equals(this.nodeU)) {
         return this.nodeV;
      } else if (var1.equals(this.nodeV)) {
         return this.nodeU;
      } else {
         throw new IllegalArgumentException(String.format("EndpointPair %s does not contain node %s", this, var1));
      }
   }

   public abstract boolean isOrdered();

   public final UnmodifiableIterator<N> iterator() {
      return Iterators.forArray(this.nodeU, this.nodeV);
   }

   public abstract boolean equals(@Nullable Object var1);

   public abstract int hashCode();

   // $FF: synthetic method
   EndpointPair(Object var1, Object var2, Object var3) {
      this(var1, var2);
   }

   private static final class Unordered<N> extends EndpointPair<N> {
      private Unordered(N var1, N var2) {
         super(var1, var2, null);
      }

      public N source() {
         throw new UnsupportedOperationException("Cannot call source()/target() on a EndpointPair from an undirected graph. Consider calling adjacentNode(node) if you already have a node, or nodeU()/nodeV() if you don't.");
      }

      public N target() {
         throw new UnsupportedOperationException("Cannot call source()/target() on a EndpointPair from an undirected graph. Consider calling adjacentNode(node) if you already have a node, or nodeU()/nodeV() if you don't.");
      }

      public boolean isOrdered() {
         return false;
      }

      public boolean equals(@Nullable Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof EndpointPair)) {
            return false;
         } else {
            EndpointPair var2 = (EndpointPair)var1;
            if (this.isOrdered() != var2.isOrdered()) {
               return false;
            } else if (this.nodeU().equals(var2.nodeU())) {
               return this.nodeV().equals(var2.nodeV());
            } else {
               return this.nodeU().equals(var2.nodeV()) && this.nodeV().equals(var2.nodeU());
            }
         }
      }

      public int hashCode() {
         return this.nodeU().hashCode() + this.nodeV().hashCode();
      }

      public String toString() {
         return String.format("[%s, %s]", this.nodeU(), this.nodeV());
      }

      // $FF: synthetic method
      Unordered(Object var1, Object var2, Object var3) {
         this(var1, var2);
      }
   }

   private static final class Ordered<N> extends EndpointPair<N> {
      private Ordered(N var1, N var2) {
         super(var1, var2, null);
      }

      public N source() {
         return this.nodeU();
      }

      public N target() {
         return this.nodeV();
      }

      public boolean isOrdered() {
         return true;
      }

      public boolean equals(@Nullable Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof EndpointPair)) {
            return false;
         } else {
            EndpointPair var2 = (EndpointPair)var1;
            if (this.isOrdered() != var2.isOrdered()) {
               return false;
            } else {
               return this.source().equals(var2.source()) && this.target().equals(var2.target());
            }
         }
      }

      public int hashCode() {
         return Objects.hashCode(this.source(), this.target());
      }

      public String toString() {
         return String.format("<%s -> %s>", this.source(), this.target());
      }

      // $FF: synthetic method
      Ordered(Object var1, Object var2, Object var3) {
         this(var1, var2);
      }
   }
}
