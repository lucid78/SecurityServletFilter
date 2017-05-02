package lucid7.routd.extend;

public abstract class PathElement {
    protected final String name;
    protected final int index;

    public PathElement(String name, int index) {

        this.name = name;
        this.index = index;
    }

    /**
     * Returns the name of the element in the route.
     *
     * @return the name of the element in the route
     */
    public String name() {
        return name;
    }

    /**
     * Returns the absolute position of the element
     * in the route.
     *
     * @return the index of the element in the route
     */
    public int index() {
        return index;
    }

    public int hashCode() {

        int result = 1;
        result = 31 * result + (name == null ? 0 : name.hashCode());
        result = 31 * result + index;
        return result;
    }

    public boolean equals(Object o) {

        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof PathElement)) return false;
        PathElement that = (PathElement)o;
        return
                (this.name == null ? that.name == null :
                        this.name.equals(that.name)) &&
                        this.index == that.index;
    }
}
