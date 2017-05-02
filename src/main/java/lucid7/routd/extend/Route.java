package lucid7.routd.extend;

import lucid7.secufilter.domain.UrlInfo;

import static lucid7.routd.extend.RouteHelper.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

public class Route {

    private String resourcePath = null;
    private UrlInfo urlInfo = null;

    private final List<PathElement> allPathElements = new ArrayList<>();
    private final List<NamedParameterElement> namedParamElements = new ArrayList<>();
    private final List<SplatParameterElement> splatParamElements = new ArrayList<>();
    private final List<StaticPathElement> staticPathElements = new ArrayList<>();

    // URL Filter
    public Route(final String paramPath,
                 final FilterType filterType,
                 final RuleType ruleType,
                 final String removeString) {
        if (paramPath == null) {
            throw new IllegalArgumentException("path cannot be null");
        }

        this.resourcePath = paramPath;
        urlInfo = new UrlInfo(filterType, ruleType, removeString);
        extractPathElements();
    }

    // PARAM Filter
    public Route(final String paramPath,
                 final FilterType filterType,
                 final String param,
                 final RuleType ruleType,
                 final String removeString) {
        if (paramPath == null) {
            throw new IllegalArgumentException("path cannot be null");
        }

        this.resourcePath = paramPath;
        urlInfo = new UrlInfo(filterType, param, ruleType, removeString);
        extractPathElements();
    }

    private void extractPathElements() {
        Matcher m = CUSTOM_REGEX_PATTERN.matcher(resourcePath);
        Map<String, String> regexMap = getRegexMap(m);
        String path = m.replaceAll("");

        String[] pathElements = RouteHelper.getPathElements(path);
        for (int i = 0; i < pathElements.length; i++) {
            String currentElement = pathElements[i];

            if (currentElement.startsWith(PARAM_PREFIX)) {

                currentElement = currentElement.substring(1);
                NamedParameterElement named =
                        new NamedParameterElement(currentElement, i, regexMap.get(currentElement));
                namedParamElements.add(named);
                allPathElements.add(named);

            } else if (currentElement.equals(WILDCARD)) {
                SplatParameterElement splat = new SplatParameterElement(i);
                splatParamElements.add(splat);
                allPathElements.add(splat);

            } else {
                if (currentElement.trim().length() < 1) continue;
                StaticPathElement staticElem = new StaticPathElement(currentElement, i);
                staticPathElements.add(staticElem);
                allPathElements.add(staticElem);
            }
        }
    }

    /*
     * Returns a map of named param names to their regex, for
     * named params that have a regex.
     * e.g. {"name" -> "[a-z]+"}
     */
    private Map<String, String> getRegexMap(Matcher m) {
        Map<String, String> regexMap = new HashMap<>();
        while (m.find()) {
            String regex = resourcePath.substring(m.start() + 1, m.end() - 1);
            int namedParamStart = m.start() - 1;
            int namedParamEnd = m.start();
            String namedParamName = resourcePath.substring(namedParamStart, namedParamEnd);
            while (!namedParamName.startsWith(PARAM_PREFIX)) {
                namedParamStart--;
                namedParamName = resourcePath.substring(namedParamStart, namedParamEnd);
            }
            namedParamName = resourcePath.substring(namedParamStart + 1, namedParamEnd);
            regexMap.put(namedParamName, regex);
        }
        return regexMap;
    }

    public UrlInfo getUrlInfo() {
        return this.urlInfo;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public List<PathElement> getPathElements() {
        return new ArrayList<>(allPathElements);
    }

    public List<NamedParameterElement> getNamedParameterElements() {
        return new ArrayList<>(namedParamElements);
    }

    public List<SplatParameterElement> getSplatParameterElements() {
        return new ArrayList<>(splatParamElements);
    }

    public List<StaticPathElement> getStaticPathElements() {
        return new ArrayList<>(staticPathElements);
    }

    /**
     * Use of this method assumes the path given matches this Route.
     *
     * @return the value of the named parameter in the path, or null if
     *         no named parameter exists with the given name
     */
    public String getNamedParameter(String paramName, String path) {
        List<NamedParameterElement> pathParams = getNamedParameterElements();
        String[] pathTokens = RouteHelper.getPathElements(path);

        for (NamedParameterElement pathParam : pathParams) {
            if (pathParam.name().equals(paramName)) {
                return urlDecodeForPathParams(pathTokens[pathParam.index()]);
            }
        }

        return null;
    }

    /**
     * Use of this method assumes the path given matches this Route.
     *
     * @return the value of the splat parameter at the given index,
     *         or null if the splat parameter index does not exist
     */
    public String getSplatParameter(int index, String path) {
        String[] splat = splat(path);
        if (index > splat.length - 1) {
            return null;
        }
        return splat[index];
    }

    /**
     * Use of this method assumes the path given matches this Route.
     */
    public String[] splat(String path) {
        List<SplatParameterElement> splatParams = getSplatParameterElements();
        String[] pathTokens = RouteHelper.getPathElements(path, false);
        String[] splat = new String[splatParams.size()];

        for (int i = 0; i < splatParams.size(); i++) {

            SplatParameterElement splatParam = splatParams.get(i);
            splat[i] = urlDecodeForPathParams(pathTokens[splatParam.index()]);

            if (i + 1 == splatParams.size() && endsWithSplat()) {
                /* this is the last splat param and the route ends with splat */
                for (int j = splatParam.index() + 1; j < pathTokens.length; j++) {
                    splat[i] = splat[i] + PATH_ELEMENT_SEPARATOR + urlDecodeForPathParams(pathTokens[j]);
                }
            }
        }

        return splat;
    }

    private boolean endsWithSplat() {
        return resourcePath.endsWith(WILDCARD);
    }

    public boolean endsWithPathSeparator() {
        return resourcePath.endsWith(PATH_ELEMENT_SEPARATOR);
    }

    public boolean hasPathElements() {
        return !allPathElements.isEmpty();
    }

    public String toString() {
        return resourcePath ;
    }

    public int hashCode() {
        int hash = 1;
        hash = hash * 13 + (resourcePath == null ? 0 : resourcePath.hashCode());
        return hash;
    }

    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof Route)) return false;
        Route that = (Route)o;
        return (this.resourcePath == null ? that.resourcePath == null :
                this.resourcePath.equals(that.resourcePath));
    }
}
