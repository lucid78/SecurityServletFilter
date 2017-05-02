package lucid7.secufilter;

import lucid7.routd.extend.RouteHelper.*;

import com.google.common.base.CharMatcher;
import lucid7.routd.extend.Route;
import lucid7.routd.extend.Router;
import lucid7.routd.extend.TreeRouter;
import org.springframework.web.filter.OncePerRequestFilter;
import lucid7.secufilter.domain.Rule;
import lucid7.secufilter.domain.UrlInfo;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

public class FilterRule extends OncePerRequestFilter {

    private static Router router = new TreeRouter();

    //
    // URL Filter
    //
    public void addRule(final String url,
                        final FilterType filterType,
                        final RuleType ruleType) {
        addRule(url, filterType, ruleType, null);
    }

    public void addRule(final String url,
                        final FilterType filterType,
                        final RuleType ruleType,
                        final String removeString) {
        router.add(new Route(url, filterType, ruleType, removeString));
    }

    //
    // PARAM Filter
    //

    public void addRule(final String url,
                        final FilterType filterType,
                        final String param,
                        final RuleType ruleType) {
        addRule(url, filterType, param, ruleType, null);
    }

    public void addRule(final String url,
                        final FilterType filterType,
                        final String param,
                        final RuleType ruleType,
                        final String removeString) {

        final Route route = router.route(url);
        if (route != null) {
            final UrlInfo urlInfo = route.getUrlInfo();
            urlInfo.addRule(param, ruleType, removeString);
        } else {
            router.add(new Route(url, filterType, param, ruleType, removeString));
        }
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {
        final Route route = router.route(request.getRequestURI());
        if (route != null) {
            final UrlInfo urlInfo = route.getUrlInfo();
            switch (urlInfo.getFilterType()) {
                case URLFilter:
                    for (final String params : Collections.list(request.getParameterNames())) {
                        FilterRequestWrapper wrapper = (FilterRequestWrapper) request;
                        wrapper.setParameter(params, filter(request.getParameter(params), urlInfo.getRuleType(), urlInfo.getRemoveString()));
                    }
                    break;
                case PARAMFilter:
                    for (final String params : Collections.list(request.getParameterNames())) {
                        final Collection<Rule> rules = urlInfo.getRule(params);
                        for (final Rule rule : rules) {
                            FilterRequestWrapper wrapper = (FilterRequestWrapper) request;
                            wrapper.setParameter(params, filter(request.getParameter(params), rule.getRuleType(), rule.getRemoveString()));
                        }
                    }
                    break;
            }
        }
        filterChain.doFilter(request, response);
    }

    public String filter(final String param, final RuleType ruleType, final String removeString) {
        CharMatcher matcher;
        String result = null;
        switch (ruleType) {
            case CHARACTER:
                matcher = CharMatcher.javaLetter();
                result = matcher.retainFrom(param);
                break;
            case NUMBER:
                matcher = CharMatcher.javaDigit();
                result = matcher.retainFrom(param);
                break;
            case CHARNUM:
                matcher = CharMatcher.javaLetterOrDigit();
                result = matcher.retainFrom(param);
                break;
            case BLACKLIST:
                if (removeString != null) {
                    matcher = CharMatcher.noneOf(removeString);
                    result = matcher.retainFrom(param);
                }
                break;
        }
        return result;
    }



}