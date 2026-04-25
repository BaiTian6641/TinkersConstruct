package slimeknights.tconstruct.library.utils;

import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * Helper for use with our extensions of resource location for some type safety in IDs.
 * In Minecraft 1.21+, ResourceLocation is final, so we use composition instead of inheritance.
 * @see IdParser
 */
public abstract class ResourceId {
  protected final ResourceLocation location;

  protected ResourceId(String namespace, String path) {
    this.location = ResourceLocation.fromNamespaceAndPath(namespace, path);
  }

  protected ResourceId(ResourceLocation location) {
    this.location = Objects.requireNonNull(location);
  }

  protected ResourceId(String location) {
    this.location = ResourceLocation.parse(location);
  }

  /** Gets the namespace (domain) of this location */
  public final String getNamespace() {
    return location.getNamespace();
  }

  /** Gets the path of this location */
  public final String getPath() {
    return location.getPath();
  }

  /** Gets the underlying ResourceLocation */
  public final ResourceLocation getLocation() {
    return location;
  }

  /** Returns a new resource location with the given prefix added to the path. */
  public final ResourceLocation withPrefix(String prefix) {
    return ResourceLocation.fromNamespaceAndPath(getNamespace(), prefix + getPath());
  }

  /** Returns a new resource location with the given suffix added to the path. */
  public final ResourceLocation withSuffix(String suffix) {
    return ResourceLocation.fromNamespaceAndPath(getNamespace(), getPath() + suffix);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ResourceId resourceId = (ResourceId) o;
    return location.equals(resourceId.location);
  }

  @Override
  public int hashCode() {
    return location.hashCode();
  }

  @Override
  public String toString() {
    return location.toString();
  }

  /**
   * Compares two ResourceIds by their underlying ResourceLocation
   */
  public int compareTo(ResourceId other) {
    return this.location.toString().compareTo(other.location.toString());
  }


  /* Helpers for static constructors */

  /**
   * Creates a new ID from the given string
   * @param string  String
   * @return  ID, or null if invalid
   */
  @Nullable
  protected static <T extends ResourceId> T tryParse(String string, BiFunction<String,String,T> constructor) {
    int sep = string.indexOf(':');
    String namespace = sep < 0 ? "minecraft" : string.substring(0, sep);
    String path = sep < 0 ? string : string.substring(sep + 1);
    return tryBuild(namespace, path, constructor);
  }

  /**
   * Creates a new ID from the given namespace and path
   * @param namespace  Namespace
   * @param path       Path
   * @return  ID, or null if invalid
   */
  @Nullable
  protected static <T extends ResourceId> T tryBuild(String namespace, String path, BiFunction<String,String,T> constructor) {
    if (ResourceLocation.isValidNamespace(namespace) && ResourceLocation.isValidPath(path)) {
      return constructor.apply(namespace, path);
    }
    return null;
  }
}
