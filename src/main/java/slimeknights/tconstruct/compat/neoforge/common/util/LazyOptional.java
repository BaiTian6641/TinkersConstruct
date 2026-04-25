package slimeknights.tconstruct.compat.neoforge.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Minimal compatibility implementation for legacy LazyOptional usages.
 */
public class LazyOptional<T> {
  @SuppressWarnings("rawtypes")
  private static final LazyOptional EMPTY = new LazyOptional<>(null, false);

  private final Supplier<? extends T> supplier;
  private final List<Consumer<LazyOptional<T>>> listeners = new ArrayList<>();
  private boolean valid;
  private boolean resolved;
  private T value;

  private LazyOptional(Supplier<? extends T> supplier, boolean valid) {
    this.supplier = supplier;
    this.valid = valid;
  }

  public static <T> LazyOptional<T> of(Supplier<? extends T> supplier) {
    return new LazyOptional<>(supplier, true);
  }

  @SuppressWarnings("unchecked")
  public static <T> LazyOptional<T> empty() {
    return (LazyOptional<T>) EMPTY;
  }

  private Optional<T> resolveInternal() {
    if (!valid || supplier == null) {
      return Optional.empty();
    }
    if (!resolved) {
      value = supplier.get();
      resolved = true;
    }
    return Optional.ofNullable(value);
  }

  public Optional<T> resolve() {
    return resolveInternal();
  }

  public boolean isPresent() {
    return resolveInternal().isPresent();
  }

  public void ifPresent(Consumer<? super T> consumer) {
    resolveInternal().ifPresent(consumer);
  }

  public T orElse(T other) {
    return resolveInternal().orElse(other);
  }

  public T orElseGet(Supplier<? extends T> other) {
    return resolveInternal().orElseGet(other);
  }

  public <R> Optional<R> map(Function<? super T, ? extends R> mapper) {
    return resolveInternal().map(mapper);
  }

  public LazyOptional<T> filter(Predicate<? super T> predicate) {
    Optional<T> optional = resolveInternal().filter(predicate);
    return optional.<LazyOptional<T>>map(value -> this).orElseGet(LazyOptional::empty);
  }

  @SuppressWarnings("unchecked")
  public <U> LazyOptional<U> cast() {
    return (LazyOptional<U>) this;
  }

  public void addListener(Consumer<LazyOptional<T>> listener) {
    if (!valid) {
      listener.accept(this);
      return;
    }
    listeners.add(listener);
  }

  public void invalidate() {
    if (!valid) {
      return;
    }
    valid = false;
    for (Consumer<LazyOptional<T>> listener : listeners) {
      listener.accept(this);
    }
    listeners.clear();
  }
}
