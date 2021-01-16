package org.apache.commons.lang3.concurrent;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractCircuitBreaker<T> implements CircuitBreaker<T> {
   public static final String PROPERTY_NAME = "open";
   protected final AtomicReference<AbstractCircuitBreaker.State> state;
   private final PropertyChangeSupport changeSupport;

   public AbstractCircuitBreaker() {
      super();
      this.state = new AtomicReference(AbstractCircuitBreaker.State.CLOSED);
      this.changeSupport = new PropertyChangeSupport(this);
   }

   public boolean isOpen() {
      return isOpen((AbstractCircuitBreaker.State)this.state.get());
   }

   public boolean isClosed() {
      return !this.isOpen();
   }

   public abstract boolean checkState();

   public abstract boolean incrementAndCheckState(T var1);

   public void close() {
      this.changeState(AbstractCircuitBreaker.State.CLOSED);
   }

   public void open() {
      this.changeState(AbstractCircuitBreaker.State.OPEN);
   }

   protected static boolean isOpen(AbstractCircuitBreaker.State var0) {
      return var0 == AbstractCircuitBreaker.State.OPEN;
   }

   protected void changeState(AbstractCircuitBreaker.State var1) {
      if (this.state.compareAndSet(var1.oppositeState(), var1)) {
         this.changeSupport.firePropertyChange("open", !isOpen(var1), isOpen(var1));
      }

   }

   public void addChangeListener(PropertyChangeListener var1) {
      this.changeSupport.addPropertyChangeListener(var1);
   }

   public void removeChangeListener(PropertyChangeListener var1) {
      this.changeSupport.removePropertyChangeListener(var1);
   }

   protected static enum State {
      CLOSED {
         public AbstractCircuitBreaker.State oppositeState() {
            return OPEN;
         }
      },
      OPEN {
         public AbstractCircuitBreaker.State oppositeState() {
            return CLOSED;
         }
      };

      private State() {
      }

      public abstract AbstractCircuitBreaker.State oppositeState();

      // $FF: synthetic method
      State(Object var3) {
         this();
      }
   }
}
