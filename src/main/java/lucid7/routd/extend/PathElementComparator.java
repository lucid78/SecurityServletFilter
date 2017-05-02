package lucid7.routd.extend;

import java.util.Comparator;

public class PathElementComparator implements Comparator<String> {
    public int compare(String r1Elem, String r2Elem) {

        if (r1Elem.equals("")) return -1;
        if (r2Elem.equals("")) return 1;

        if (r1Elem.equals(RouteHelper.WILDCARD) && !r2Elem.equals("")) return -1;
        if (r2Elem.equals(RouteHelper.WILDCARD) && !r1Elem.equals("")) return 1;

        if (r1Elem.equals(RouteHelper.WILDCARD) && r2Elem.equals("")) return 1;
        if (r2Elem.equals(RouteHelper.WILDCARD) && r1Elem.equals("")) return -1;

        if (r1Elem.startsWith(RouteHelper.PARAM_PREFIX) && !r2Elem.equals("") && !r2Elem.equals(RouteHelper.WILDCARD)) return 1;
        if (r2Elem.startsWith(RouteHelper.PARAM_PREFIX) && !r1Elem.equals("") && !r1Elem.equals(RouteHelper.WILDCARD)) return -1;

        if (r1Elem.startsWith(RouteHelper.PARAM_PREFIX) && (r2Elem.equals(RouteHelper.WILDCARD) || r2Elem.equals(""))) return -1;
        if (r2Elem.startsWith(RouteHelper.PARAM_PREFIX) && (r1Elem.equals(RouteHelper.WILDCARD) || r1Elem.equals(""))) return 1;

        return r1Elem.compareTo(r2Elem);
    }
}
